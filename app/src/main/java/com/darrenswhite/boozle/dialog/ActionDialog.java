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
import com.darrenswhite.boozle.game.Action;
import com.darrenswhite.boozle.game.Game;

import java.io.IOException;

/**
 * @author Darren White
 */
public class ActionDialog extends DialogFragment implements DialogInterface.OnClickListener {

	private static final String TAG = "ActionDialog";

	private Game getGame() {
		return (Game) getArguments().getSerializable("game");
	}

	public static ActionDialog newInstance(Game game) {
		ActionDialog dialog = new ActionDialog();
		Bundle args = new Bundle();
		args.putSerializable("game", game);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public void onClick(DialogInterface dialogInterface, int i) {
		CharSequence desc = ((TextView) getDialog().findViewById(R.id.action_desc)).getText();
		CharSequence title = ((TextView) getDialog().findViewById(R.id.action_title)).getText();

		if (title == null || title.toString().trim().length() == 0) {
			Toast.makeText(getActivity(), "Please enter a title!", Toast.LENGTH_SHORT).show();
			return;
		}

		getGame().add(new Action(title.toString().trim(), desc != null ? desc.toString().trim() : "", null, 10, true));

		Toast.makeText(getActivity(), "Action added: " + title, Toast.LENGTH_SHORT).show();

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
		View v = inflater.inflate(R.layout.dialog_add_action, null);

		builder.setView(v).setPositiveButton("OK", this).setNegativeButton("Cancel", null);

		return builder.create();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}