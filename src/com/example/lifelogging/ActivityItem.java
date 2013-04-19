package com.example.lifelogging;

import android.view.View;

public abstract class ActivityItem {
	public String name;
	public double xpRew, hpPen;
	private boolean finished, locationEnabled;
	protected MainActivity mainActivity;

	public ActivityItem(String name, double xpRew, double hpPen, MainActivity mainActivity) {
		this.name = name;
		this.xpRew = xpRew;
		this.hpPen = hpPen;
		this.mainActivity = mainActivity;
		this.finished = false;
	}

	public abstract double update();
	
	public boolean isLocationEnabled(){
		return locationEnabled;
	}
	
	public void locationEnabled(boolean value){
		this.locationEnabled = value;
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

	public View makeView(View convertView) {
		return null;
	}

	public boolean isDueToday() {
		return false;
	}

}