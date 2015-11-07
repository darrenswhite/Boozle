package com.darrenswhite.boozle.game;

import android.support.annotation.NonNull;

import com.darrenswhite.boozle.util.Function;

import java.io.Serializable;

/**
 * @author Darren White
 */
public class Action implements Comparable<Action>, Serializable {

	protected Player player;
	protected String text;
	protected String description;
	protected Function<String, String> function;
	protected String currentText;
	protected int weight;
	protected boolean enabled;
	protected int position;

	public Action(String text, String description, Function<String, String> function, int weight, boolean enabled) {
		this.text = currentText = text;
		this.description = description;
		this.function = function;
		this.weight = weight;
		this.enabled = enabled;
	}

	public String applyFunction() {
		return (currentText = function.apply(text));
	}

	@Override
	public int compareTo(@NonNull Action a) {
		return Integer.compare(position, a.position);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Action action = (Action) o;

		return action.weight == weight && enabled == action.enabled && action.position == position &&
				!(text != null ? !text.equals(action.text) : action.text != null);
	}

	public String getCurrentText() {
		return currentText;
	}

	public String getDescription() {
		return description;
	}

	public Function<String, String> getFunction() {
		return function;
	}

	public Player getPlayer() {
		return player;
	}

	public String getText() {
		return text;
	}

	public int getWeight() {
		return weight;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public String toString() {
		return String.format("Action{text='%s', description='%s', function=%s, currentText='%s', " +
						"weight=%d, enabled=%s, position=%d}", text, description, function,
				currentText, weight, enabled, position);
	}
}