package com.example.habit1;

import java.util.Date;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

public class DailyItem extends ActivityItem {
	public String name;
	public double xpRew, hpPen;
	public long created, lastCompleted, lastUpdated;
	private View convertView;
	private CheckBox cDailyDone;
	private TextView tvItemName, tvXP, tvHP;

	public DailyItem(String name, double xpRew, double hpPen, MainActivity mainActivity) {
		super(name, xpRew, hpPen, mainActivity);
		this.name = name;
		this.xpRew = xpRew;
		this.hpPen = hpPen;
		this.created = new Date().getTime();
		this.lastCompleted = new Date().getTime();
	}

	@Override
	public View makeView(View convertView) {
		this.convertView = convertView;
		setupViews();
		return convertView;
	}

	private void setupViews() {
		getViewReferences();
		tvXP.setText("XP: " + Double.toString(this.getXpRew()));
		tvHP.setText("HP: " + Double.toString(this.getHpPen()));
		cDailyDone.setOnClickListener((OnClickListener) mainActivity);
		tvItemName.setText(this.getName());
	}

	private void getViewReferences() {
		tvItemName = (TextView) convertView.findViewById(R.id.tvItem);
		tvXP = (TextView) convertView.findViewById(R.id.tvItemXP);
		tvHP = (TextView) convertView.findViewById(R.id.tvItemHP);
		cDailyDone = (CheckBox) convertView.findViewById(R.id.cDailyDone);
	}

	public long timeSinceLastComp() {
		return (new Date().getTime() - lastCompleted);
	}

	@Override
	public double achieve() {
		lastCompleted = new Date().getTime();
		return xpRew;
	}

	@Override
	public boolean isDueToday() {
		return !finished;
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