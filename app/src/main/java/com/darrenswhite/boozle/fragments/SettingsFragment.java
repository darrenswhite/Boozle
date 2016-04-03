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
import android.widget.NumberPicker;
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

		CheckBox showAnims = (CheckBox) v.findViewById(R.id.show_animations);
		CheckBox showDescs = (CheckBox) v.findViewById(R.id.show_descriptions);
		CheckBox showPlayers = (CheckBox) v.findViewById(R.id.show_players);
		NumberPicker nextAction = (NumberPicker) v.findViewById(R.id.next_action_period);
		int period = Integer.parseInt(Settings.getProperty(Settings.NEXT_ACTION_PERIOD));
		final int increment = 10;
		int min = 0, max = 60;
		String[] values = new String[(max - min) / increment];

		for (int i = 0; i < values.length; i++) {
			values[i] = String.valueOf(min + i * increment);
		}

		showAnims.setChecked(Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_ANIMATIONS)));
		showDescs.setChecked(Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_DESCRIPTIONS)));
		showPlayers.setChecked(Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_PLAYERS)));
		nextAction.setDisplayedValues(values);
		nextAction.setMinValue(0);
		nextAction.setMaxValue(values.length - 1);
		nextAction.setValue(period / increment);
		nextAction.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				Settings.setAndStore(getActivity(), Settings.NEXT_ACTION_PERIOD,
						newVal * increment);
			}
		});

		mTracker = ((MainActivity) getActivity()).getTracker();

		String name = "Settings";
		Log.i(TAG, "Setting screen name: " + name);
		mTracker.setScreenName("Image~" + name);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());

		return v;
	}
}