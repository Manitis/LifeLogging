package com.example.lifelogging;

import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class DailyItem extends ActivityItem {
	public CheckBox cDone;
	public TextView tvItemName, tvXP, tvHP, tvDue;

	public DailyItem(String name, double xpRew, double hpPen,
			MainActivity mainActivity) {
		super(name, xpRew, hpPen, mainActivity);
		this.itemType = MainActivity.DAILY;
	}

	public DailyItem(String name, double xpRew, double hpPen,
			MainActivity mainActivity, LatLng location) {
		super(name, xpRew, hpPen, mainActivity, location);
		this.itemType = MainActivity.DAILY;
	}

	public DailyItem(String name, double xpRew, double hpPen,
			MainActivity mainActivity, LatLng location, int locationArea) {
		super(name, xpRew, hpPen, mainActivity, location, locationArea);
		this.itemType = MainActivity.DAILY;
	}

	public DailyItem(Bundle bundle) {
		super(bundle);
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
		cDone.setChecked(isFinished());
		cDone.setOnClickListener(mainActivity);
		tvItemName.setText(this.name());
	}

	private void getViewReferences() {
		tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
		tvXP = (TextView) convertView.findViewById(R.id.tvItemXP);
		tvHP = (TextView) convertView.findViewById(R.id.tvItemHP);
		cDone = (CheckBox) convertView.findViewById(R.id.cDone);
	}

	public long timeSinceLastComp() {
		return (new Date().getTime() - lastCompleted);
	}

	public void done() {
		lastCompleted = new Date().getTime();
		finished(true);
	}

	@Override
	public boolean isDueToday() {
		return !isFinished();
	}

	@Override
	public double update() {
		this.lastUpdated = new Date().getTime();
		if (timeSinceLastComp() > 86400 * 1000) {
			return hpPen;
		} else {
			return 0;
		}
	}

}