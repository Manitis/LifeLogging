package com.example.habit1;

import android.view.View;

public class ActivityItem {
	public String name;
	public double xpRew, hpPen;
	public boolean finished;
	protected MainActivity mainActivity;

	public ActivityItem(String name, double xpRew, double hpPen, MainActivity mainActivity) {
		this.name = name;
		this.xpRew = xpRew;
		this.hpPen = hpPen;
		this.mainActivity = mainActivity;
		this.finished = false;
	}

	public double update() {
		return 0;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public double achieve() {
		return xpRew;
	}

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