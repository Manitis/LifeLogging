package com.example.lifelogging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

public class ItemAdapter extends ArrayAdapter<ActivityItem> {

	public static final byte ALPHA = 0;
	public static final int XP_REW = 1;
	public static final int HP_PEN = 2;
	public static final int DUE_DATE = 3;
	public static final int CREATED = 4;
	public static final int LAST_COMPLETED = 5;
	public List<ActivityItem> items = new ArrayList<ActivityItem>();
	private Context context;
	private int layoutID;
	public ViewGroup parent;
	private LayoutInflater inflater;
	private int lastSort = ALPHA;
	private boolean direction = true;

	public ItemAdapter(Context context, int layoutID) {
		super(context, layoutID);
		this.layoutID = layoutID;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void sortLastSort(){
		Collections.sort(items, new MyComparator(lastSort, direction));
	}

	public void add(ActivityItem item) {
		items.add(item);
		sortLastSort();
		notifyDataSetChanged();
	}

	@Override
	public void insert(ActivityItem item, int position) {
		items.add(position, item);
		sortLastSort();
		notifyDataSetChanged();
	}

	@Override
	public void remove(ActivityItem object) {
		items.remove(object);
		notifyDataSetChanged();
	}

	public void edit(int position, ActivityItem item) {
		items.set(position, item);
		sortLastSort();
		notifyDataSetChanged();
	}

	@Override
	public ActivityItem getItem(int position) {
		return items.get(position);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		this.parent = parent;
		ActivityItem item = items.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(layoutID, parent, false);
		}
		return item.makeView(convertView);
	}

	public void sort(int sortType, boolean ascending) {
		lastSort = sortType;
		direction = ascending;
		Collections.sort(items, new MyComparator(sortType, ascending));
		notifyDataSetChanged();
		fixViewsAfterSort();
	}

	private void fixViewsAfterSort() {
		for (int i = 0; i < items.size(); i++) {
			ActivityItem item = items.get(i);
			View row = parent.getChildAt(i);
			if(item.isFinished()){
				row.setBackgroundColor(0x22222222);
			} else{
				row.setBackgroundColor(0x00000000);
			}
			CheckBox cDone = (CheckBox) row.findViewById(R.id.cDone);
			if(cDone != null){
				cDone.setChecked(item.isFinished());
			}
		}
	}

	@Override
	public int getCount() {
		return items.size();
	}

	public class MyComparator implements Comparator<ActivityItem> {

		private int orderType, multiplier = 1;

		public MyComparator(int type, boolean direction) {
			this.orderType = type;
			if (!direction) {
				this.multiplier = -1;
			}
		}

		public int compare(ActivityItem item1, ActivityItem item2) {
			int result = 0;
			if ((item1.isFinished() && !item2.isFinished())) {
				result = 1;
			} else if (!item1.isFinished() && item2.isFinished()) {
				result = -1;
			} else {

				if (orderType == ALPHA) {
					result = (item1.name).compareTo(item2.name);
				} else if (orderType == XP_REW) {
					double diff = item2.xpRew - item1.xpRew;
					result = (int) (diff / Math.abs(diff));
				} else if (orderType == HP_PEN) {
					double diff = item2.hpPen - item1.hpPen;
					result = (int) (diff / Math.abs(diff));
				} else if (orderType == DUE_DATE) {
					double diff = item1.due - item2.due;
					result = (int) (diff / Math.abs(diff));
				} else if (orderType == CREATED) {
					double diff = item1.created - item2.created;
					result = (int) (diff / Math.abs(diff));
				} else if (orderType == LAST_COMPLETED) {
					double diff = item1.lastCompleted - item2.lastCompleted;
					result = (int) (diff / Math.abs(diff));
				}
			}
			return result * multiplier;
		}
	}

	public void moveLineToBottom(int position) {
	}
}