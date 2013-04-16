package com.example.habit1;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ActivityItemArrayAdapter extends ArrayAdapter<ActivityItem> {

	private List<ActivityItem> items;
	private int layoutID;
	private Context context;


	public ActivityItemArrayAdapter(List<ActivityItem> items, int layoutID, Context context) {
		super(context, layoutID, items);
		this.layoutID = layoutID;
		this.items = items;
		this.context = context;
	}

	public View getConvertView(ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(layoutID, parent, false);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = getConvertView(parent);
		}
		ActivityItem item = items.get(position);
		return item.makeView(convertView);
	}
}