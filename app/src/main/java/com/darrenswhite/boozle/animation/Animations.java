package com.darrenswhite.boozle.animation;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;
import android.view.ViewPropertyAnimator;

/**
 * @author Darren White
 */
public class Animations {

	private static final Point screenSize = new Point();

	public static void init(Activity a) {
		a.getWindowManager().getDefaultDisplay().getSize(screenSize);
	}

	public static ViewPropertyAnimator slideOffScreenBottom(View v, long duration) {
		return v.animate().setDuration(duration).translationY(screenSize.y);
	}

	public static ViewPropertyAnimator slideOffScreenLeft(View v, long duration) {
		return v.animate().setDuration(duration).translationX(-v.getWidth());
	}

	public static ViewPropertyAnimator slideOffScreenRight(View v, long duration) {
		return v.animate().setDuration(duration).translationX(screenSize.x);
	}

	public static ViewPropertyAnimator slideOffScreenTop(View v, long duration) {
		return v.animate().setDuration(duration).translationY(-v.getHeight());
	}

	public static ViewPropertyAnimator slideOnScreenBottom(View v, long duration, float y) {
		v.setTranslationY(screenSize.y);
		return v.animate().setDuration(duration).translationY(y);
	}

	public static ViewPropertyAnimator slideOnScreenLeft(View v, long duration, float x) {
		v.setTranslationX(-v.getWidth());
		return v.animate().setDuration(duration).translationX(x);
	}

	public static ViewPropertyAnimator slideOnScreenRight(View v, long duration, float x) {
		v.setTranslationX(screenSize.x);
		return v.animate().setDuration(duration).translationX(x);
	}

	public static ViewPropertyAnimator slideOnScreenTop(View v, long duration, float y) {
		v.setTranslationY(-v.getHeight());
		return v.animate().setDuration(duration).translationY(y);
	}
}