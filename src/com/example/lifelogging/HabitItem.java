package com.example.lifelogging;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class HabitItem extends ActivityItem {
	public TextView tvItemName, tvXP, tvHP;
	private View convertView;
	private Button bUp, bDown;

	public HabitItem(String name, double xpRew, double hpPen,
			MainActivity mainActivity) {
		super(name, xpRew, hpPen, mainActivity);
		this.itemType = MainActivity.HABIT;
	}

	public HabitItem(String name, double xpRew, double hpPen,
			MainActivity mainActivity, LatLng location) {
		super(name, xpRew, hpPen, mainActivity, location);
		this.itemType = MainActivity.HABIT;
	}

	public HabitItem(String name, double xpRew, double hpPen,
			MainActivity mainActivity, LatLng location, int locationArea) {
		super(name, xpRew, hpPen, mainActivity, location, locationArea);
		this.itemType = MainActivity.HABIT;
	}

	public HabitItem(Bundle bundle) {
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
		bUp.setOnClickListener((OnClickListener) mainActivity);
		bDown.setOnClickListener((OnClickListener) mainActivity);
		tvItemName.setText(name());
	}

	private void getViewReferences() {
		tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
		tvXP = (TextView) convertView.findViewById(R.id.tvItemXP);
		tvHP = (TextView) convertView.findViewById(R.id.tvItemHP);
		bUp = (Button) convertView.findViewById(R.id.bUp);
		bDown = (Button) convertView.findViewById(R.id.bDown);

	}

	public long timeSinceLastComp() {
		return (new Date().getTime() - lastCompleted);
	}

	@Override
	public void done() {
		lastCompleted = new Date().getTime();
	}

	@Override
	public double update() {
		return 0;
	}

}