package com.example.lifelogging;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class TodoItem extends ActivityItem {
	private View convertView;
	public CheckBox cDone;
	public TextView tvItemName, tvXP, tvHP, tvDue;
	private final int MILLISEC_IN_DAY = 86400 * 1000;
	private final int MILLISEC_IN_HOUR = 3600 * 1000;
	private final int MILLISEC_IN_MINUTE = 60 * 1000;

	public TodoItem(String name, double xpRew, double hpPen, double dueIn,
			MainActivity mainActivity) {
		super(name, xpRew, hpPen, mainActivity);
		this.itemType = MainActivity.TODO;
		setDue(getDueDateInMillis(dueIn));
	}

	public TodoItem(String name, double xpRew, double hpPen, double dueIn,
			MainActivity mainActivity, LatLng location) {
		super(name, xpRew, hpPen, mainActivity, location);
		this.itemType = MainActivity.HABIT;
		setDue(getDueDateInMillis(dueIn));
	}

	public TodoItem(String name, double xpRew, double hpPen, double dueIn,
			MainActivity mainActivity, LatLng location, int locationArea) {
		super(name, xpRew, hpPen, mainActivity, location, locationArea);
		this.itemType = MainActivity.HABIT;
		setDue(getDueDateInMillis(dueIn));
	}

	public TodoItem(Bundle bundle) {
		super(bundle);
	}

	public long getDueDateInMillis(double dueIn) {
		Calendar date = new GregorianCalendar();
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		date.add(Calendar.DAY_OF_MONTH, (int) dueIn + 1);
		return date.getTimeInMillis();
	}

	@Override
	public View makeView(View convertView) {
		this.convertView = convertView;
		setupViews();
		return convertView;
	}

	private void setupViews() {
		getViewReferences();
		tvXP.setText("XP: " + Double.toString(xpRew()));
		tvHP.setText("HP: " + Double.toString(hpPen()));
		tvDue.setText(getDueText());
		tvItemName.setText(name());
		cDone.setChecked(isFinished());
		cDone.setOnClickListener((OnClickListener) mainActivity);
	}

	private void getViewReferences() {
		tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
		tvXP = (TextView) convertView.findViewById(R.id.tvItemXP);
		tvHP = (TextView) convertView.findViewById(R.id.tvItemHP);
		cDone = (CheckBox) convertView.findViewById(R.id.cDone);
		tvDue = (TextView) convertView.findViewById(R.id.tvItemDue);
	}

	public String getDueText() {
		long numberOfDays = daysUntilDue();
		long numberOfHours = hoursUntilDueWithoutDays();
		long numberOfMinutes = minutesUntilDueWithoutHours();
		if (numberOfDays > 0) {
			return String.format("%d day%s", daysUntilDue(),
					pluralOrNot(numberOfDays));
		} else if (numberOfHours > 0) {
			return String.format("%d hour%s", numberOfHours,
					pluralOrNot(numberOfHours));
		} else if (numberOfMinutes > 0) {
			return String.format("%d minute%s", numberOfMinutes,
					pluralOrNot(numberOfMinutes));
		}
		return "";
	}

	private String pluralOrNot(long value) {
		if (value == 1) {
			return "";
		} else {
			return "s";
		}
	}

	public long daysUntilDue() {
		return getMillisTillDue() / MILLISEC_IN_DAY;
	}

	public long hoursUntilDueWithoutDays() {
		return (getMillisTillDue() % MILLISEC_IN_DAY) / MILLISEC_IN_HOUR;
	}

	public long minutesUntilDueWithoutHours() {
		return (getMillisTillDue() % MILLISEC_IN_HOUR) / MILLISEC_IN_MINUTE;
	}

	public long getMillisTillDue() {
		long now = new Date().getTime();
		return (getDue() - now);
	}

	@Override
	public double update() {
		long now = new Date().getTime();
		if (getDue() - now < 0) {
			return hpPen;
		} else {
			hpPen = incrementFunction(hpPen);
			xpRew = incrementFunction(xpRew);
			return 0;
		}
	}

	@Override
	public boolean isDueToday() {
		return (daysUntilDue() < 1 && !isFinished());
	}

	public double incrementFunction(double value) {
		long now = new Date().getTime();
		return value * (now - created) / (getDue() - created);
	}

	@Override
	public void done() {
		finished(true);
	}
}