package com.doctor.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.doctor.app.R;
import com.doctor.app.model.DrawerPojo;

import java.util.List;

public class DrawerAdapter extends BaseAdapter {

	private List<DrawerPojo> mDrawerItems;
	private LayoutInflater mInflater;

	public DrawerAdapter(Context context, List<DrawerPojo> items) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDrawerItems = items;
	}

	@Override
	public int getCount() {
		return mDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mDrawerItems.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.list_view_item_navigation_drawer, parent,
					false);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView
					.findViewById(R.id.ivicon);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		DrawerPojo item = mDrawerItems.get(position);

		holder.icon.setImageResource(item.getIconRes());
		holder.title.setText(item.getText());

		return convertView;
	}

	private static class ViewHolder {
		public ImageView icon;
		public TextView title;
	}
}
