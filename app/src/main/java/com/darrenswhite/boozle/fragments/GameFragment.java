package com.darrenswhite.boozle.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darrenswhite.boozle.MainActivity;
import com.darrenswhite.boozle.R;
import com.darrenswhite.boozle.Settings;
import com.darrenswhite.boozle.game.Action;
import com.darrenswhite.boozle.game.Game;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * @author Darren White
 */
public class GameFragment extends Fragment {

	private static final String TAG = "GameFragment";

	private Tracker mTracker;

	private Game getGame() {
		return (Game) getArguments().getSerializable("game");
	}

	public static GameFragment newInstance(Game game) {
		GameFragment fragment = new GameFragment();
		Bundle args = new Bundle();

		args.putSerializable("game", game);
		fragment.setArguments(args);

		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_game, container, false);
		TextView action = (TextView) v.findViewById(R.id.action_title);
		TextView actionDesc = (TextView) v.findViewById(R.id.action_desc);
		TextView player = (TextView) v.findViewById(R.id.player);
		Action current = getGame().getCurrent();

		if (current != null) {
			action.setText(current.getCurrentText());

			if (Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_PLAYERS))) {
				player.setText(current.getPlayer() != null ? current.getPlayer().getName() : null);
			}

			if (Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_DESCRIPTIONS))) {
				actionDesc.setText(current.getDescription());
			}
		}

		mTracker = ((MainActivity) getActivity()).getTracker();

		String name = "Game";
		Log.i(TAG, "Setting screen name: " + name);
		mTracker.setScreenName("Image~" + name);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());

		return v;
	}
}