package com.darrenswhite.boozle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.darrenswhite.boozle.R;
import com.darrenswhite.boozle.game.Game;
import com.darrenswhite.boozle.game.Player;

import java.io.IOException;

/**
 * @author Darren White
 */
public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.ViewHolder> {

	private static final String TAG = "PlayersAdapter";

	private final Context mContext;
	private final Game game;
	private int position;

	public PlayersAdapter(Context mContext, Game game) {
		this.mContext = mContext;
		this.game = game;
	}

	@Override
	public int getItemCount() {
		return game.getPlayers().size();
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		final Player p = game.getPlayers().get(position);
		View.OnClickListener clickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckBox cb = holder.enabledBox;
				if (!(v instanceof CheckBox)) {
					cb.setChecked(!cb.isChecked());
				}
				setPlayerEnabled(p, cb.isChecked());
			}
		};
		View.OnCreateContextMenuListener contextMenuListener = new View.OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
				contextMenu.clear();
				contextMenu.setHeaderTitle(R.string.menu_title);
				contextMenu.add(0, R.id.rename, 0, R.string.menu_rename);
				contextMenu.add(0, R.id.delete, 0, R.string.menu_delete);
			}
		};
		View.OnLongClickListener longClickListener = new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				setPosition(holder.getAdapterPosition());
				return false;
			}
		};

		holder.content.setOnClickListener(clickListener);
		holder.content.setOnCreateContextMenuListener(contextMenuListener);
		holder.content.setOnLongClickListener(longClickListener);

		holder.enabledBox.setOnClickListener(clickListener);
		holder.enabledBox.setOnCreateContextMenuListener(contextMenuListener);
		holder.enabledBox.setOnLongClickListener(longClickListener);

		holder.enabledBox.setChecked(p.isEnabled());
		holder.enabledBox.setText(p.getName().trim());
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.players_item_row, parent, false), viewType);
	}

	@Override
	public void onViewRecycled(ViewHolder holder) {
		super.onViewRecycled(holder);

		holder.content.setOnLongClickListener(null);
		holder.enabledBox.setOnLongClickListener(null);
	}

	private void setPlayerEnabled(Player p, boolean enabled) {
		game.setEnabled(p, enabled);

		try {
			game.saveCustom(mContext);
		} catch (IOException e) {
			Log.e(TAG, "Unable to save custom");
		}
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		private final int viewType;
		private final RelativeLayout content;
		private final CheckBox enabledBox;

		public ViewHolder(View v, int viewType) {
			super(v);
			this.viewType = viewType;

			content = (RelativeLayout) itemView.findViewById(R.id.row_content);
			enabledBox = (CheckBox) itemView.findViewById(R.id.player);
		}
	}
}