package com.darrenswhite.boozle.util;

import android.util.Log;
import android.widget.TextView;
import com.darrenswhite.boozle.game.Action;

import java.util.Random;

/**
 * @author Darren White
 */
public class ActionFunctionThread implements Runnable {

	private static final String TAG = "ActionFunctionThread";
	private static final Random rnd = new Random();
	private static final int interval = 80;
	private static final int min = 750;
	private static final int max = 1500;

	private final TextView text;
	private final Action action;

	public ActionFunctionThread(TextView text, Action action) {
		this.text = text;
		this.action = action;
	}

	@Override
	public void run() {
		if (action.getFunction() == null) {
			return;
		}

		int total = (min + rnd.nextInt(max - min)) / interval;

		for (int i = 0; i < total; i++) {
			text.post(new Runnable() {

				@Override
				public void run() {
					text.setText(action.applyFunction());
				}
			});

			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				Log.e(TAG, "Unable to sleep");
			}
		}
	}
}