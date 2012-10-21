package com.example.insight;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.insight.datamodel.Friend;
import com.example.insight.eventListBaseAdapter.ViewHolder;

public class FriendListBaseAdapter extends BaseAdapter {
	private static ArrayList<Friend> friendlist;
	private final Context context;

	private final LayoutInflater mInflater;

	public FriendListBaseAdapter(Context context, ArrayList<Friend> friendList) {
		this.friendlist = friendList;
		this.context = context;
		//font = Typeface.createFromAsset(context.getAssets(), "EraserDust.ttf");
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return friendlist.size();
	}

	public Object getItem(int position) {
		return friendlist.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void removeItem(int position) {
		friendlist.remove(position);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.friendlistitem, null);
			holder = new ViewHolder();
			holder.txtTitle = (TextView) convertView.findViewById(R.id.friendNameitem);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtTitle.setText(friendlist.get(position).getName());

		
		return convertView;
	}

	static class ViewHolder {
		TextView txtTitle;
	}

}
