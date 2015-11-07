package com.darrenswhite.boozle.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darrenswhite.boozle.MainActivity;
import com.darrenswhite.boozle.R;
import com.darrenswhite.boozle.adapter.ModesAdapter;
import com.darrenswhite.boozle.game.Game;
import com.darrenswhite.boozle.util.DividerItemDecoration;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * @author Darren White
 */
public class ModesFragment extends Fragment {

	private static final String TAG = "ModesFragment";
	private static final String[] MODES = {"Boozle King", "Lightweight", "Normal", "Heavyweight"};

	private Tracker mTracker;
	private RecyclerView modesView;

	private Game getGame() {
		return (Game) getArguments().getSerializable("game");
	}

	public static ModesFragment newInstance(Game game) {
		ModesFragment fragment = new ModesFragment();
		Bundle args = new Bundle();

		args.putSerializable("game", game);
		fragment.setArguments(args);

		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_modes, container, false);
		Activity a = getActivity();

		modesView = (RecyclerView) v.findViewById(R.id.modes_view);
		modesView.addItemDecoration(new DividerItemDecoration(a, DividerItemDecoration.VERTICAL_LIST));
		modesView.setAdapter(new ModesAdapter(a, getGame(), MODES));
		modesView.setHasFixedSize(true);
		modesView.setLayoutManager(new LinearLayoutManager(a));

		mTracker = ((MainActivity) getActivity()).getTracker();

		String name = "Modes";
		Log.i(TAG, "Setting screen name: " + name);
		mTracker.setScreenName("Image~" + name);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());

		return v;
	}
}