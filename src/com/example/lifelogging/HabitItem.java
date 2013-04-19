package com.example.lifelogging;

import java.util.Date;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HabitItem extends ActivityItem {
	public String name;
	public double xpRew, hpPen;
	public long created, lastCompleted;
	public boolean finished;
	private View convertView;
	private TextView tvItemName, tvXP, tvHP;
	private Button bUp, bDown;

	public HabitItem(String name, double xpRew, double hpPen, MainActivity mainActivity) {
		super(name, xpRew, hpPen, mainActivity);
		this.name = name;
		this.xpRew = xpRew;
		this.hpPen = hpPen;
		this.lastCompleted = this.created = new Date().getTime();
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
		bUp.setOnClickListener((OnClickListener) mainActivity);
		bDown.setOnClickListener((OnClickListener) mainActivity);
		tvItemName.setText(this.getName());
	}

	private void getViewReferences() {
		tvItemName = (TextView) convertView.findViewById(R.id.tvItem);
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
		// TODO Auto-generated method stub
		return 0;
	}

}