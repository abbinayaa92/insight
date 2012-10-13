package com.example.insight;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.insight.datamodel.Event;

public class eventListBaseAdapter extends BaseAdapter {
	private static ArrayList<Event> eventList;
	private final Context context;

	private final LayoutInflater mInflater;

	public eventListBaseAdapter(Context context, ArrayList<Event> eventList) {
		this.eventList = eventList;
		this.context = context;
		//font = Typeface.createFromAsset(context.getAssets(), "EraserDust.ttf");
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return eventList.size();
	}

	public Object getItem(int position) {
		return eventList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void removeItem(int position) {
		eventList.remove(position);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.eventlistitem, null);
			holder = new ViewHolder();
			holder.txtTitle = (TextView) convertView.findViewById(R.id.eventTitle);
			holder.txtTime = (TextView) convertView.findViewById(R.id.eventTime);
			holder.txtDate = (TextView) convertView.findViewById(R.id.eventDate);
			holder.txtVenue = (TextView) convertView.findViewById(R.id.eventVenue);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtTitle.setText(eventList.get(position).getTitle());
		holder.txtDate.setText(eventList.get(position).getDate());
		holder.txtVenue.setText(eventList.get(position).getVenue());
		holder.txtTime.setText(eventList.get(position).getTime());

		
		return convertView;
	}

	static class ViewHolder {
		TextView txtTitle;
		TextView txtDate;
		TextView txtVenue;
		TextView txtTime;
	}

}
