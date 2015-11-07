package com.darrenswhite.boozle.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darrenswhite.boozle.MainActivity;
import com.darrenswhite.boozle.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * @author Darren White
 */
public class AboutFragment extends Fragment {

	private static final String TAG = "AboutFragment";

	private Tracker mTracker;

	public static AboutFragment newInstance() {
		return new AboutFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mTracker = ((MainActivity) getActivity()).getTracker();

		String name = "About";
		Log.i(TAG, "Setting screen name: " + name);
		mTracker.setScreenName("Image~" + name);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());

		return inflater.inflate(R.layout.fragment_about, container, false);
	}
}