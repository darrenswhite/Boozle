package com.darrenswhite.boozle.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.darrenswhite.boozle.R;
import com.darrenswhite.boozle.game.Game;
import com.darrenswhite.boozle.game.Player;

import java.io.IOException;

/**
 * @author Darren White
 */
public class PlayerDialog extends DialogFragment implements DialogInterface.OnClickListener {

	private static final String TAG = "PlayerDialog";

	private DialogInterface.OnDismissListener dismissListener;

	private Game getGame() {
		return (Game) getArguments().getSerializable("game");
	}

	private Player getPlayer() {
		return (Player) getArguments().getSerializable("player");
	}

	public static PlayerDialog newInstance(Game game, Player p) {
		PlayerDialog dialog = new PlayerDialog();
		Bundle args = new Bundle();

		args.putSerializable("game", game);
		args.putSerializable("player", p);
		dialog.setArguments(args);

		return dialog;
	}

	@Override
	public void onClick(DialogInterface dialogInterface, int i) {
		CharSequence name = ((TextView) getDialog().findViewById(R.id.player_name)).getText();

		if (name == null || name.toString().trim().length() == 0) {
			Toast.makeText(getActivity(), "Please enter a name!", Toast.LENGTH_SHORT).show();
			return;
		}

		Game game = getGame();
		Player p = getPlayer();

		if (p == null) {
			game.add(new Player(name.toString().trim(), true));
		} else {
			p.setName(name.toString().trim());
		}

		try {
			getGame().saveCustom(getActivity());
		} catch (IOException e) {
			Log.e(TAG, "Unable to save custom");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.dialog_add_player, null);

		builder.setView(v).setPositiveButton("OK", this).setNegativeButton("Cancel", null);

		return builder.create();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);

		if (dismissListener != null) {
			dismissListener.onDismiss(dialog);
		}
	}

	public void setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
		this.dismissListener = dismissListener;
	}
}