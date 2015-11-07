package com.darrenswhite.boozle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.darrenswhite.boozle.R;
import com.darrenswhite.boozle.game.Action;
import com.darrenswhite.boozle.game.Game;

import java.io.IOException;

/**
 * @author Darren White
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

	private static final String TAG = "CustomAdapter";

	private final Game game;
	private final Context mContext;
	private int position;

	public CustomAdapter(Context mContext, Game game) {
		this.mContext = mContext;
		this.game = game;
	}

	@Override
	public int getItemCount() {
		return game.size();
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		final Action a = game.getActions().get(position);
		View.OnClickListener clickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckBox cb = holder.enabledBox;
				if (!(v instanceof CheckBox)) {
					cb.setChecked(!cb.isChecked());
				}
				setActionEnabled(a, cb.isChecked());
			}
		};
		View.OnCreateContextMenuListener contextMenuListener = new View.OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
				contextMenu.clear();
				contextMenu.setHeaderTitle(R.string.menu_title);
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


		holder.descText.setText(a.getDescription());
		holder.enabledBox.setChecked(a.isEnabled());
		holder.enabledBox.setText(a.getText().trim());
		holder.weightText.setMinValue(0);
		holder.weightText.setMaxValue(10);
		holder.weightText.setValue(a.getWeight());
		holder.weightText.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				game.setWeight(a, newVal);

				try {
					game.saveCustom(mContext);
				} catch (IOException e) {
					Log.e(TAG, "Unable to save custom");
				}
			}
		});
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_row, parent, false), viewType);
	}

	@Override
	public void onViewRecycled(ViewHolder holder) {
		super.onViewRecycled(holder);
		holder.content.setOnLongClickListener(null);
		holder.enabledBox.setOnLongClickListener(null);
	}

	private void setActionEnabled(Action a, boolean enabled) {
		game.setEnabled(a, enabled);

		try {
			game.saveCustom(mContext);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		private final int viewType;
		private final RelativeLayout content;
		private final TextView descText;
		private final CheckBox enabledBox;
		private final NumberPicker weightText;

		public ViewHolder(View v, int viewType) {
			super(v);
			this.viewType = viewType;

			content = (RelativeLayout) itemView.findViewById(R.id.row_content);
			descText = (TextView) itemView.findViewById(R.id.action_desc);
			enabledBox = (CheckBox) itemView.findViewById(R.id.action);
			weightText = (NumberPicker) itemView.findViewById(R.id.weight_picker);
		}
	}
}