package com.darrenswhite.boozle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.darrenswhite.boozle.MainActivity;
import com.darrenswhite.boozle.R;

/**
 * @author Darren White
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

	private static final int TYPE_HEADER = 0;
	private static final int TYPE_ITEM = 1;

	private final Context mContext;
	private final String[] mTitles;
	private final int[] mIcons;

	public DrawerAdapter(Context mContext, String[] mTitles, int[] mIcons) {
		this.mContext = mContext;
		this.mTitles = mTitles;
		this.mIcons = mIcons;
	}

	@Override
	public int getItemCount() {
		return mTitles.length;
	}

	@Override
	public int getItemViewType(int position) {
		return isPositionHeader(position) ? TYPE_HEADER : TYPE_ITEM;
	}

	private boolean isPositionHeader(int position) {
		return position == 0;
	}

	@Override
	public void onBindViewHolder(DrawerAdapter.ViewHolder holder, final int position) {
		holder.textView.setText(mTitles[position]);
		holder.textView.setCompoundDrawables(null, null, null, null);
		holder.content.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				selectFragment(position);
			}
		});
	}

	@Override
	public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == TYPE_HEADER) {
			return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false), viewType);
		} else {
			return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_row, parent, false), viewType);
		}
	}

	private void selectFragment(int position) {
		if (mContext instanceof MainActivity) {
			((MainActivity) mContext).selectFragment(position);
		}
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		private final TextView textView;
		private final int viewType;
		private RelativeLayout content;

		public ViewHolder(View v, int viewType) {
			super(v);
			this.viewType = viewType;

			if (this.viewType != TYPE_ITEM && this.viewType != TYPE_HEADER) {
				throw new IllegalArgumentException("Invalid holder id: " + this.viewType);
			}

			content = (RelativeLayout) itemView.findViewById(R.id.row_content);
			textView = (TextView) itemView.findViewById(R.id.row_text);
		}
	}
}