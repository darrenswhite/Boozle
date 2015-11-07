package com.darrenswhite.boozle.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.darrenswhite.boozle.MainActivity;
import com.darrenswhite.boozle.R;
import com.darrenswhite.boozle.adapter.CustomAdapter;
import com.darrenswhite.boozle.game.Action;
import com.darrenswhite.boozle.game.Game;
import com.darrenswhite.boozle.util.DividerItemDecoration;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.IOException;

/**
 * @author Darren White
 */
public class CustomFragment extends Fragment {

	private static final String TAG = "CustomFragment";

	private Tracker mTracker;
	private RecyclerView actionsView;

	private Game getGame() {
		return (Game) getArguments().getSerializable("game");
	}

	public static CustomFragment newInstance(Game game) {
		CustomFragment fragment = new CustomFragment();
		Bundle args = new Bundle();

		args.putSerializable("game", game);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Game game = getGame();
		final int position = ((CustomAdapter) actionsView.getAdapter()).getPosition();
		Action a = game.getActions().get(position);

		switch (item.getItemId()) {
			case R.id.delete:
				game.remove(a);
				actionsView.getAdapter().notifyItemRemoved(position);
				break;
		}

		try {
			game.saveCustom(getActivity());
		} catch (IOException e) {
			Log.e(TAG, "Unable to save custom");
		}

		return super.onContextItemSelected(item);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_custom, container, false);
		Activity a = getActivity();

		actionsView = (RecyclerView) v.findViewById(R.id.actions_view);
		actionsView.addItemDecoration(new DividerItemDecoration(a, DividerItemDecoration.VERTICAL_LIST));
		actionsView.setAdapter(new CustomAdapter(a, getGame()));
		actionsView.setHasFixedSize(true);
		actionsView.setLayoutManager(new LinearLayoutManager(a));

		registerForContextMenu(actionsView);

		mTracker = ((MainActivity) getActivity()).getTracker();

		String name = "Custom";
		Log.i(TAG, "Setting screen name: " + name);
		mTracker.setScreenName("Image~" + name);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());

		return v;
	}
}