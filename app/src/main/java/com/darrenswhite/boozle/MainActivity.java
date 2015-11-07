package com.darrenswhite.boozle;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.darrenswhite.boozle.adapter.DrawerAdapter;
import com.darrenswhite.boozle.animation.Animations;
import com.darrenswhite.boozle.dialog.ActionDialog;
import com.darrenswhite.boozle.dialog.PlayerDialog;
import com.darrenswhite.boozle.fragments.AboutFragment;
import com.darrenswhite.boozle.fragments.CustomFragment;
import com.darrenswhite.boozle.fragments.GameFragment;
import com.darrenswhite.boozle.fragments.ModesFragment;
import com.darrenswhite.boozle.fragments.PlayersFragment;
import com.darrenswhite.boozle.fragments.SettingsFragment;
import com.darrenswhite.boozle.game.Action;
import com.darrenswhite.boozle.game.Game;
import com.darrenswhite.boozle.util.ActionFunctionThread;
import com.darrenswhite.boozle.util.DividerItemDecoration;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Darren White
 */
public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";
	private static final int TRANSITION_DURATION = 80;

	private RecyclerView mDrawerView;
	private LinearLayoutManager mLayoutManager;
	private DrawerLayout mDrawer;
	private ActionBarDrawerToggle mDrawerToggle;
	private Toolbar mToolbar;
	private RecyclerView.Adapter mAdapter;
	private Fragment mContent;
	private Tracker mTracker;

	private CharSequence mTitle;
	private String[] mTitles;
	private String[] mFragTitles;
	private int[] mIcons;

	private Game game;

	public synchronized Tracker getTracker() {
		if (mTracker == null) {
			mTracker = GoogleAnalytics.getInstance(this).newTracker(R.xml.app_tracker);
			mTracker.enableAdvertisingIdCollection(true);

			String name = "Game";
			Log.i(TAG, "Setting screen name: " + name);
			mTracker.setScreenName("Image~" + name);
		}

		return mTracker;
	}

	private boolean isGameFragment() {
		FragmentManager fragmentManager = getSupportFragmentManager();

		for (int i = 1; i < mTitles.length; i++) {
			Fragment f = fragmentManager.findFragmentByTag(mTitles[i]);
			if (f != null && f.isVisible()) {
				return false;
			}
		}

		return true;
	}

	public void newAction(View v) {
		DialogFragment dialog = ActionDialog.newInstance(game);
		dialog.show(getFragmentManager(), "new_action");
	}

	public void newPlayer(View view) {
		DialogFragment dialog = PlayerDialog.newInstance(game, null);
		dialog.show(getFragmentManager(), "new_player");
	}

	public void nextAction(View v) {
		final TextView action = (TextView) findViewById(R.id.action_title);
		final TextView actionDesc = (TextView) findViewById(R.id.action_desc);
		final TextView player = (TextView) findViewById(R.id.player);
		final Action next = game.next();

		Log.i(TAG, "Setting next action: " + next);
		mTracker.setScreenName("Game");
		mTracker.send(new HitBuilders.EventBuilder().setCategory("Action").setAction("Next").build());

		if (next == null) {
			action.setText(R.string.no_actions);
			actionDesc.setText(null);
			player.setText(null);
			return;
		}

		if (!Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_ANIMATIONS))) {
			action.setText(next.getFunction() != null ? next.applyFunction() : next.getText());

			if (Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_DESCRIPTIONS))) {
				actionDesc.setText(next.getDescription());
			}

			if (Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_PLAYERS))) {
				player.setText(next.getPlayer() != null ? next.getPlayer().getName() : null);
			}

			return;
		}

		final float actionX = action.getTranslationX();
		Animations.slideOffScreenRight(action, TRANSITION_DURATION).withEndAction(new Runnable() {

			@Override
			public void run() {
				action.setText(next.getText());
				Animations.slideOnScreenLeft(action, TRANSITION_DURATION, actionX).withEndAction(new Runnable() {

					@Override
					public void run() {
						new Thread(new ActionFunctionThread(action, next)).start();
					}
				});
			}
		});

		if (Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_DESCRIPTIONS))) {
			final float actionDescY = actionDesc.getTranslationY();
			Animations.slideOffScreenBottom(actionDesc, TRANSITION_DURATION).withEndAction(new Runnable() {

				@Override
				public void run() {
					actionDesc.setText(next.getDescription());
					Animations.slideOnScreenBottom(actionDesc, TRANSITION_DURATION, actionDescY);
				}
			});
		}

		if (Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_PLAYERS))) {
			final float playerY = player.getTranslationY();
			Animations.slideOffScreenTop(player, TRANSITION_DURATION).withEndAction(new Runnable() {

				@Override
				public void run() {
					player.setText(next.getPlayer() != null ? next.getPlayer().getName() : null);
					Animations.slideOnScreenTop(player, TRANSITION_DURATION, playerY);
				}
			});
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mContent = getSupportFragmentManager().getFragments().get(0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState != null) {
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
			game = (Game) savedInstanceState.getSerializable("game");
		} else {
			game = new Game();
		}

		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		mDrawer = (DrawerLayout) findViewById(R.id.drawer);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.app_name, R.string.app_name);
		mDrawerView = (RecyclerView) findViewById(R.id.drawer_view);
		mIcons = getResources().getIntArray(R.array.menu_icons);
		mLayoutManager = new LinearLayoutManager(this);
		mTitle = getTitle();
		mTitles = getResources().getStringArray(R.array.menu_titles);
		mFragTitles = getResources().getStringArray(R.array.fragment_titles);
		mAdapter = new DrawerAdapter(this, mTitles, mIcons);

		mDrawer.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();
		mDrawerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
		mDrawerView.setAdapter(mAdapter);
		mDrawerView.setHasFixedSize(true);
		mDrawerView.setLayoutManager(mLayoutManager);

		Animations.init(this);

		try {
			Settings.load(this);
		} catch (IOException e) {
			Log.e(TAG, "Unable to load settings");
		}

		try {
			File dir = getDir("partytime", Context.MODE_PRIVATE);
			File custom = new File(dir, "custom.dat");
			InputStream in;

			if (custom.exists()) {
				in = new FileInputStream(custom);
			} else {
				in = getAssets().open("normal.dat");
			}

			game.load(in);
		} catch (Exception e) {
			Log.e(TAG, "Unable to load game");
		}

		if (savedInstanceState == null) {
			selectFragment(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			selectFragment(5);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
		outState.putSerializable("game", game);
	}

	public void selectFragment(int position) {
		Fragment f = null;

		switch (position) {
			case 0:
				f = GameFragment.newInstance(game);
				break;
			case 1:
				f = CustomFragment.newInstance(game);
				break;
			case 2:
				f = PlayersFragment.newInstance(game);
				break;
			case 3:
				f = ModesFragment.newInstance(game);
				break;
			case 4:
				f = AboutFragment.newInstance();
				break;
			case 5:
				f = SettingsFragment.newInstance();
				break;
			default:
				break;
		}

		if (f != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			String title = mFragTitles[position];

			mContent = f;

			if (f.getClass().equals(GameFragment.class)) {
				fragmentManager.popBackStackImmediate();
				fragmentManager.beginTransaction().replace(R.id.content_frame, f, title).commit();
			} else if (isGameFragment()) {
				fragmentManager.beginTransaction().replace(R.id.content_frame, f, title).addToBackStack(title).commit();
			} else {
				fragmentManager.popBackStackImmediate();
				fragmentManager.beginTransaction().replace(R.id.content_frame, f, title).addToBackStack(title).commit();
			}

			setTitle(title);
			mDrawer.closeDrawer(mDrawerView);
		} else {
			Log.e(TAG, "Unable to create fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
	}

	public void showAnims(View v) {
		Settings.setProperty(Settings.SHOW_ANIMATIONS, !Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_ANIMATIONS)));

		try {
			Settings.store(this);
		} catch (IOException e) {
			Log.e(TAG, "Unable to store settings");
		}
	}

	public void showDescs(View v) {
		Settings.setProperty(Settings.SHOW_DESCRIPTIONS, !Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_DESCRIPTIONS)));

		try {
			Settings.store(this);
		} catch (IOException e) {
			Log.e(TAG, "Unable to change setting: showDescs");
		}
	}

	public void showPlayers(View view) {
		Settings.setProperty(Settings.SHOW_PLAYERS, !Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_PLAYERS)));

		try {
			Settings.store(this);
		} catch (IOException e) {
			Log.e(TAG, "Unable to change setting: showPlayers");
		}
	}
}