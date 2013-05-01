package com.example.lifelogging;

import java.util.Date;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

public abstract class ActivityItem {
	public long created, lastCompleted, lastUpdated, due;
	protected View convertView;
	public String name, description;
	public double xpRew, hpPen;
	public int itemType;
	private boolean finished, locationEnabled, locationSet;
	protected MainActivity mainActivity;
	private LatLng location;
	private int locationArea;

	public ActivityItem(String name, double xpRew, double hpPen,
			MainActivity mainActivity) {
		this.name = name;
		this.xpRew = xpRew;
		this.hpPen = hpPen;
		this.mainActivity = mainActivity;
		this.finished = false;
		this.lastUpdated = this.created = new Date().getTime();
		this.lastCompleted = new Date().getTime();
		this.due = 0;
		this.itemType = -1;
		this.locationEnabled = false;
		this.location = null;
		this.locationSet = false;
		this.locationArea = 500;
	}

	public ActivityItem(String name, double xpRew, double hpPen,
			MainActivity mainActivity, LatLng location) {
		this(name, xpRew, hpPen, mainActivity);
		if (location == null) {
			this.locationSet = false;
		} else {
			this.locationSet = true;
		}
		this.location = location;
		this.locationArea = 500;
	}

	public ActivityItem(String name, double xpRew, double hpPen,
			MainActivity mainActivity, LatLng location, int locationArea) {
		this(name, xpRew, hpPen, mainActivity);
		if (location == null) {
			this.locationSet = false;
			this.locationArea = 500;
		} else {
			this.locationSet = true;
			this.locationArea = locationArea;
		}
		this.location = location;
		this.locationArea = locationArea;
	}

	public ActivityItem(Bundle bundle) {
		readFromBundle(bundle);
	}

	public Bundle getBundle() {
		Bundle result = new Bundle();
		result.putInt("itemType", itemType);
		result.putString("name", name);
		result.putDouble("hpPen", hpPen);
		result.putDouble("xpRew", xpRew);
		result.putBoolean("locationEnabled", isLocationEnabled());
		result.putBoolean("locationSet", isLocationSet());
		result.putLong("dueAt", getDue());
		if (isLocationSet()) {
			result.putDouble("lat", location().latitude);
			result.putDouble("lon", location().longitude);
			result.putInt("locationArea", locationArea());
		}
		return result;
	}

	public void readFromBundle(Bundle bundle) {
		itemType = bundle.getInt("itemType");
		name = bundle.getString("name");
		xpRew = bundle.getDouble("xpRew");
		hpPen = bundle.getDouble("hpPen");
		setDue(bundle.getLong("dueAt"));
		locationEnabled = bundle.getBoolean("locationEnabled");
		locationSet = bundle.getBoolean("locationSet");
		if (locationSet) {
			location = new LatLng(bundle.getDouble("lat"),
					bundle.getDouble("lon"));
			locationArea = bundle.getInt("locationArea");
		}
	}

	public long getDue() {
		return due;
	}

	public void setDue(long value) {
		this.due = value;
	}

	public abstract double update();

	public boolean isLocationEnabled() {
		return locationEnabled;
	}

	public boolean isLocationSet() {
		return locationSet;
	}

	public void locationEnabled(boolean value) {
		locationEnabled = value;
	}

	public void setLocation(LatLng value) {
		locationSet = true;
		location = value;
	}

	public LatLng location() {
		return location;
	}

	public int locationArea() {
		return locationArea;
	}

	public boolean isFinished() {
		return finished;
	}

	public void finished(boolean value) {
		finished = value;
	}

	public abstract void done();

	public String name() {
		return name;
	}

	public double xpRew() {
		return xpRew;
	}

	public double hpPen() {
		return hpPen;
	}

	public abstract View makeView(View convertView);

	public boolean isDueToday() {
		return false;
	}

}