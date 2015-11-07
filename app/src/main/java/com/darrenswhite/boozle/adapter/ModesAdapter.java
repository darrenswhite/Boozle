package com.darrenswhite.boozle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darrenswhite.boozle.R;
import com.darrenswhite.boozle.game.Game;

/**
 * @author Darren White
 */
public class ModesAdapter extends RecyclerView.Adapter<ModesAdapter.ViewHolder> {

	private static final String TAG = "ModesAdapter";

	private final Context mContext;
	private final Game game;
	private final String[] modes;

	public ModesAdapter(Context mContext, Game game, String[] modes) {
		this.mContext = mContext;
		this.game = game;
		this.modes = modes;
	}

	@Override
	public int getItemCount() {
		return modes.length;
	}

	@Override
	public void onBindViewHolder(ModesAdapter.ViewHolder holder, final int position) {
		holder.textView.setText(modes[position]);
		holder.content.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String fileName = modes[position].replace(" ", "_").toLowerCase() + ".dat";

				try {
					game.load(mContext.getAssets().open(fileName));
					Toast.makeText(mContext, "Loaded mode: " + modes[position], Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Log.e(TAG, "Unable to load mode: " + fileName);
				}
			}
		});
	}

	@Override
	public ModesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.modes_item_row, parent, false), viewType);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		private final TextView textView;
		private final int viewType;
		private final RelativeLayout content;

		public ViewHolder(View v, int viewType) {
			super(v);
			this.viewType = viewType;

			content = (RelativeLayout) itemView.findViewById(R.id.row_content);
			textView = (TextView) itemView.findViewById(R.id.row_text);
		}
	}
}