package com.example.habit1;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

public class TodoItem extends ActivityItem {
	public String name;
	public double xpRew, hpPen;
	public long created, due;
	public boolean finished;
	private TextView tvItemName, tvXP, tvHP, tvDue;
	private CheckBox cDone;
	private View convertView;
	private final int MILLISEC_IN_DAY = 86400 * 1000;
	private final int MILLISEC_IN_HOUR = 3600 * 1000;
	private final int MILLISEC_IN_MINUTE = 60 * 1000;

	public TodoItem(String name, double xpRew, double hpPen, double dueIn,
			MainActivity mainActivity) {
		super(name, xpRew, hpPen, mainActivity);
		this.name = name;
		this.xpRew = xpRew;
		this.hpPen = hpPen;
		this.created = new Date().getTime();
		this.due = getDueDateInMillis(dueIn);
	}

	private long getDueDateInMillis(double dueIn) {
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
		tvXP.setText("XP: " + Double.toString(this.getXpRew()));
		tvHP.setText("HP: " + Double.toString(this.getHpPen()));
		tvDue.setText(this.getDueText());
		tvItemName.setText(this.getName());
		cDone.setOnClickListener((OnClickListener) mainActivity);
	}

	private void getViewReferences() {
		tvItemName = (TextView) convertView.findViewById(R.id.tvItem);
		tvXP = (TextView) convertView.findViewById(R.id.tvItemXP);
		tvHP = (TextView) convertView.findViewById(R.id.tvItemHP);
		tvDue = (TextView) convertView.findViewById(R.id.tvItemDue);
		cDone = (CheckBox) convertView.findViewById(R.id.cTodoDone);
	}

	public String getDueText() {
		//TODO Take care of when there is 1 day, 1 hour and 1 minute
		int numberOfDays = daysUntilDue();
		int numberOfHours = hoursUntilDueWithoutDays();
		int numberOfMinutes = minutesUntilDueWithoutHours();
		
		if (numberOfDays > 0) {
			return String.format("%d day%s", daysUntilDue(), pluralOrNot(numberOfDays));
		} else if (numberOfHours > 0) {
			return String.format("%d hour%s", numberOfHours, pluralOrNot(numberOfHours));
		} else if (numberOfMinutes > 0) {
			return String.format("%d minute%s", numberOfMinutes, pluralOrNot(numberOfMinutes));
		}
		return "";
	}

	private String pluralOrNot(int value) {
		if(value == 1){
			return "s";
		}
		else{
			return "";
		}
	}

	public int daysUntilDue() {
		return (int) getMillisTillDue() / MILLISEC_IN_DAY;
	}

	public int hoursUntilDueWithoutDays() {
		return (int) (getMillisTillDue() % MILLISEC_IN_DAY) / MILLISEC_IN_HOUR;
	}

	public int minutesUntilDueWithoutHours() {
		return (int) (getMillisTillDue() % MILLISEC_IN_HOUR) / MILLISEC_IN_MINUTE;
	}

	public long getMillisTillDue() {
		long now = new Date().getTime();
		return (long) (due - now);
	}

	@Override
	public double update() {
		long now = new Date().getTime();
		if (due - now < 0) {
			return hpPen;
		} else {
			hpPen = incrementFunction(hpPen);
			xpRew = incrementFunction(xpRew);
			return 0;
		}
	}
	
	@Override
	public boolean isDueToday() {
		return (daysUntilDue() < 1 && !finished);
	}

	public double incrementFunction(double value) {
		long now = new Date().getTime();
		return value * (now - created) / (due - created);
	}
}