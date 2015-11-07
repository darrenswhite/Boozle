package com.darrenswhite.boozle.util;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * @author Darren White
 */
public class FixedQueue<E> extends AbstractQueue<E> implements Queue<E>, Serializable {

	private final int capacity;
	private transient int modCount;
	private int elementCount;
	private Object[] elementData;

	public FixedQueue(int capacity) {
		this.capacity = capacity;
		elementData = newElementArray(capacity);
	}

	public int count(E e) {
		Iterator<E> it = iterator();
		int count = 0;

		if (e == null) {
			throw new NullPointerException();
		}

		while (it.hasNext()) {
			if (e.equals(it.next())) {
				count++;
			}
		}

		return count;
	}

	@SuppressWarnings("unchecked")
	public synchronized E elementAt(int location) {
		if (location < capacity) {
			return (E) elementData[location];
		}

		throw new ArrayIndexOutOfBoundsException(location);
	}

	public int getCapacity() {
		return capacity;
	}

	@NonNull
	@Override
	public Iterator<E> iterator() {
		return new SimpleQueueIterator();
	}

	@SuppressWarnings("unchecked")
	private E[] newElementArray(int size) {
		return (E[]) new Object[size];
	}

	@Override
	public boolean offer(E e) {
		shiftByOne();

		elementData[0] = e;
		modCount++;

		if (elementCount < capacity) {
			elementCount++;
		}

		return true;
	}

	@Override
	public E peek() {
		return elementAt(capacity - 1);
	}

	@Override
	public E poll() {
		return elementAt(0);
	}

	private void shiftByOne() {
		System.arraycopy(elementData, 0, elementData, 1, capacity - 1);
		elementData[0] = null;
	}

	@Override
	public synchronized int size() {
		return elementCount;
	}

	private class SimpleQueueIterator implements Iterator<E> {

		int pos = -1;
		int expectedModCount;
		int lastPosition = -1;

		SimpleQueueIterator() {
			expectedModCount = modCount;
		}

		@Override
		public boolean hasNext() {
			return pos + 1 < size();
		}

		@Override
		public E next() {
			if (expectedModCount == modCount) {
				try {
					E result = elementAt(pos + 1);

					lastPosition = ++pos;

					return result;
				} catch (IndexOutOfBoundsException e) {
					throw new NoSuchElementException();
				}
			}

			throw new ConcurrentModificationException();
		}

		public void remove() {
			if (this.lastPosition == -1) {
				throw new IllegalStateException();
			}

			if (expectedModCount != modCount) {
				throw new ConcurrentModificationException();
			}

			try {
				FixedQueue.this.remove(lastPosition);
			} catch (IndexOutOfBoundsException e) {
				throw new ConcurrentModificationException();
			}

			expectedModCount = modCount;

			if (pos == lastPosition) {
				pos--;
			}

			lastPosition = -1;
		}
	}
}