package com.darrenswhite.boozle.fragments;

import android.app.Activity;
import android.content.DialogInterface;
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
import com.darrenswhite.boozle.adapter.PlayersAdapter;
import com.darrenswhite.boozle.dialog.PlayerDialog;
import com.darrenswhite.boozle.game.Game;
import com.darrenswhite.boozle.game.Player;
import com.darrenswhite.boozle.util.DividerItemDecoration;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.IOException;

/**
 * @author Darren White
 */
public class PlayersFragment extends Fragment {

	private static final String TAG = "PlayersFragment";

	private Tracker mTracker;
	private RecyclerView playersView;

	private Game getGame() {
		return (Game) getArguments().getSerializable("game");
	}

	public static PlayersFragment newInstance(Game game) {
		PlayersFragment fragment = new PlayersFragment();
		Bundle args = new Bundle();

		args.putSerializable("game", game);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final int position = ((PlayersAdapter) playersView.getAdapter()).getPosition();
		final Game game = getGame();
		final Player p = game.getPlayers().get(position);

		switch (item.getItemId()) {
			case R.id.rename:
				PlayerDialog dialog = PlayerDialog.newInstance(game, p);
				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						playersView.getAdapter().notifyItemChanged(position);
					}
				});
				dialog.show(getActivity().getFragmentManager(), "rename_player");
				break;
			case R.id.delete:
				game.remove(p);
				playersView.getAdapter().notifyItemRemoved(position);
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
		View v = inflater.inflate(R.layout.fragment_players, container, false);
		Activity a = getActivity();

		playersView = (RecyclerView) v.findViewById(R.id.players_view);
		playersView.addItemDecoration(new DividerItemDecoration(a, DividerItemDecoration.VERTICAL_LIST));
		playersView.setAdapter(new PlayersAdapter(a, getGame()));
		playersView.setHasFixedSize(true);
		playersView.setLayoutManager(new LinearLayoutManager(a));

		registerForContextMenu(playersView);

		mTracker = ((MainActivity) getActivity()).getTracker();

		String name = "Players";
		Log.i(TAG, "Setting screen name: " + name);
		mTracker.setScreenName("Image~" + name);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());

		return v;
	}
}