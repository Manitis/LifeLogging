package com.example.lifelogging;

import android.view.View;

import com.google.android.gms.maps.model.LatLng;

public abstract class ActivityItem {
	public String name, description;
	public double xpRew, hpPen;
	private boolean finished, locationEnabled;
	protected MainActivity mainActivity;
	private LatLng location;

	public ActivityItem(String name, double xpRew, double hpPen,
			MainActivity mainActivity) {
		this.name = name;
		this.xpRew = xpRew;
		this.hpPen = hpPen;
		this.mainActivity = mainActivity;
		this.finished = false;
	}

	public abstract double update();

	public boolean isLocationEnabled() {
		return locationEnabled;
	}

	public void setLocationEnabled(boolean value) {
		locationEnabled = value;
	}

	public void setLocation(LatLng value) {
		locationEnabled = true;
		location = value;
	}

	public LatLng getLocation() {
		return location;
	}

	public boolean isFinished() {
		return finished;
	}

	public void finished(boolean value) {
		finished = value;
	}

	public abstract void done();

	public String getName() {
		return name;
	}

	public double getXpRew() {
		return xpRew;
	}

	public double getHpPen() {
		return hpPen;
	}

	public abstract View makeView(View convertView);

	public boolean isDueToday() {
		return false;
	}

}