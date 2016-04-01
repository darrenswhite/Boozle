package com.darrenswhite.boozle.fragments;

import android.graphics.Color;
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
import com.darrenswhite.boozle.util.ProgressBar;
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
		ProgressBar pb = (ProgressBar) v.findViewById(R.id.progress);
		Action current = getGame().getCurrent();

		pb.setProgress(95f);
		pb.setBackgroundColor(Color.argb(0, 0, 0, 0));
		pb.setForegroundColor(Color.argb(200, 255, 215, 0));

		if (current != null) {
			action.setText(current.getCurrentText());

			if (Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_PLAYERS))) {
				player.setText(current.getPlayer() != null ? current.getPlayer().getName() : null);
			}

			if (Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_DESCRIPTIONS))) {
				actionDesc.setText(current.getDescription());
			}
		}

		MainActivity activity = (MainActivity) getActivity();

		mTracker = activity.getTracker();

		String name = "Game";
		Log.i(TAG, "Setting screen name: " + name);
		mTracker.setScreenName("Image~" + name);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());

		return v;
	}

	@Override
	public void onPause() {
		super.onPause();
		((MainActivity) getActivity()).stopActionTimer();
	}

	@Override
	public void onResume() {
		super.onResume();
		((MainActivity) getActivity()).startActionTimer(true);
	}

	@Override
	public void onStop() {
		super.onStop();
		((MainActivity) getActivity()).stopActionTimer();
	}
}