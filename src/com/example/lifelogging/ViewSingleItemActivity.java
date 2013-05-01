package com.example.lifelogging;

import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.doomonafireball.betterpickers.BetterPickerUtils;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class ViewSingleItemActivity extends FragmentActivity implements
		OnClickListener, DatePickerDialogFragment.DatePickerDialogHandler {
	private static final int NOTIFY_NEARBY = 0, SET_LOCATION = 1;
	private GoogleMap map;
	long due;
	ActivityItem item;
	EditText itemName;
	CheckBox cbLocationAlert;
	CheckedTextView ctv_location, ctv_setLocation;
	NumberPicker npXP, npHP;
	ItemAdapter settingsAdapter;
	private final int SET_LOCATION_REQUEST = 10;
	LinearLayout llSetLocation, llLocationAlert, llSetDueDate;
	private int itemType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getValuesFromIntent();
		if (itemType == MainActivity.TODO) {
			setContentView(R.layout.view_single_todo_item_activity);

		} else {
			setContentView(R.layout.view_single_item_activity);
		}
		getViewReferences();
		initialize();
	}

	private void initialize() {
		initializeCursosInEditText();
		llLocationAlert.setOnClickListener(this);
		llSetLocation.setOnClickListener(this);
		if (itemType == MainActivity.TODO) {
			llSetDueDate.setOnClickListener(this);
		}
		npXP.setValue((int) item.xpRew);
		npHP.setValue((int) item.hpPen);
		cbLocationAlert.setChecked(item.isLocationEnabled());
	}

	private void initializeCursosInEditText() {
		itemName.setText(item.name);
		int position = itemName.length();
		Editable etext = itemName.getText();
		Selection.setSelection(etext, position);
	}

	private void getViewReferences() {
		itemName = (EditText) findViewById(R.id.etItemName);
		npXP = (NumberPicker) findViewById(R.id.npXP);
		npHP = (NumberPicker) findViewById(R.id.npHP);
		cbLocationAlert = (CheckBox) findViewById(R.id.cb_locationAlert);
		llLocationAlert = (LinearLayout) findViewById(R.id.llLocationAlert);
		llSetLocation = (LinearLayout) findViewById(R.id.llSetLocation);
		llSetDueDate = (LinearLayout) findViewById(R.id.llSetDueDate);
	}

	private void getValuesFromIntent() {
		Bundle extras = getIntent().getExtras();
		itemType = extras.getInt("itemType");
		due = extras.getLong("dueAt");
		switch (itemType) {
		case MainActivity.DAILY:
			item = new DailyItem(extras);
			break;
		case MainActivity.HABIT:
			item = new HabitItem(extras);
			break;
		case MainActivity.TODO:
			item = new TodoItem(extras);
			break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.llLocationAlert:
			long test = item.due;
			cbLocationAlert.toggle();
			break;
		case R.id.llSetLocation:
			Intent intent = new Intent();
			intent.setClass(this, SetItemLocation.class);
			intent.putExtras(item.getBundle());
			startActivityForResult(intent, SET_LOCATION_REQUEST);
			break;
		case R.id.llSetDueDate:
			BetterPickerUtils.showDateEditDialog(getSupportFragmentManager(),
					R.style.BetterPickersDialogFragment_Light);
			break;
		}
	}

	@Override
	public void onDialogDateSet(int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 00);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		calendar.set(Calendar.MONTH, monthOfYear);
		long now = new Date().getTime();
		if (calendar.getTimeInMillis() < now) {
			calendar.add(Calendar.YEAR, 1);
		}
		item.setDue(calendar.getTimeInMillis());
		Log.d("test", calendar.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_single_item, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_set_location_save:
			finishActivity();
		}
		return super.onOptionsItemSelected(item);
	}

	private void finishActivity() {
		setResult(RESULT_OK, getResultIntent());
		finish();
	}

	private Intent getResultIntent() {
		getValuesFromFields();
		Intent intent = new Intent(getBaseContext(), MainActivity.class);
		intent.putExtras(item.getBundle());
		return intent;
	}

	private void getValuesFromFields() {
		item.name = itemName.getText().toString();
		item.xpRew = npXP.getValue();
		item.hpPen = npHP.getValue();
		item.locationEnabled(cbLocationAlert.isChecked());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_CANCELED) {
		} else {
			switch (requestCode) {
			case SET_LOCATION_REQUEST:
				Bundle extras = intent.getExtras();
				switch (itemType) {
				case MainActivity.DAILY:
					item = new DailyItem(extras);
					break;
				case MainActivity.HABIT:
					item = new HabitItem(extras);
					break;
				case MainActivity.TODO:
					item = new TodoItem(extras);
					break;
				}
				break;
			}
		}
	}

}
