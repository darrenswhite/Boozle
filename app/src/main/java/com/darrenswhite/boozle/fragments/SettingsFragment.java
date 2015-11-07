package com.darrenswhite.boozle.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.darrenswhite.boozle.MainActivity;
import com.darrenswhite.boozle.R;
import com.darrenswhite.boozle.Settings;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * @author Darren White
 */
public class SettingsFragment extends Fragment {

	private static final String TAG = "SettingsFragment";

	private Tracker mTracker;

	public static Fragment newInstance() {
		return new SettingsFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_settings, container, false);
		Activity a = getActivity();

		CheckBox showAnims = (CheckBox) v.findViewById(R.id.show_anims);
		CheckBox showDescs = (CheckBox) v.findViewById(R.id.show_descs);
		CheckBox showPlayers = (CheckBox) v.findViewById(R.id.show_players);

		showAnims.setChecked(Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_ANIMATIONS)));
		showDescs.setChecked(Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_DESCRIPTIONS)));
		showPlayers.setChecked(Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_PLAYERS)));

		mTracker = ((MainActivity) getActivity()).getTracker();

		String name = "Settings";
		Log.i(TAG, "Setting screen name: " + name);
		mTracker.setScreenName("Image~" + name);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());

		return v;
	}
}