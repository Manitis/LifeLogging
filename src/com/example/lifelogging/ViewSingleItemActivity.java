package com.example.lifelogging;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewSingleItemActivity extends Activity implements OnClickListener {
	private GoogleMap map;
	String name, description;
	double hpPen, xpRew;
	LatLng location;
	double lon, lat;
	boolean locationEnabled;
	EditText itemName, itemDescription;
	CheckBox cb_locationAlert;
	CheckedTextView ctv_location, ctv_setLocation;
	NumberPicker npXP, npHP;
	private final int SET_LOCATION_REQUEST = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_single_item_activity);
		getValues();
		getViewReferences();
		itemName.setText(name);
		itemDescription.setText(description);
		int position = itemName.length();
		Editable etext = itemName.getText();
		Selection.setSelection(etext, position);
		npXP.setValue((int) xpRew);
		npHP.setValue((int) hpPen);
		ctv_location.setOnClickListener(this);
		ctv_setLocation.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK, getResultIntent());
		super.onBackPressed();
	}

	private Intent getResultIntent() {
		Bundle bundle = new Bundle();
		bundle.putString("name", name);
		bundle.putString("description", description);
		bundle.putDouble("hpPen", hpPen);
		bundle.putDouble("xpRew", xpRew);
		if (locationEnabled) {
			bundle.putBoolean("locationEnabled", locationEnabled);
			bundle.putDouble("lat", location.latitude);
			bundle.putDouble("lon", location.longitude);
		}
		Intent intent = new Intent(getBaseContext(), MainActivity.class);
		intent.putExtras(bundle);
		return intent;
	}

	private void getViewReferences() {
		itemName = (EditText) findViewById(R.id.etItemName);
		itemDescription = (EditText) findViewById(R.id.etItemDescription);
		npXP = (NumberPicker) findViewById(R.id.npXP);
		npHP = (NumberPicker) findViewById(R.id.npHP);
		cb_locationAlert = (CheckBox) findViewById(R.id.cbEnableLocationAlert);
		ctv_location = (CheckedTextView) findViewById(R.id.ctv_setLocation);
		ctv_setLocation = (CheckedTextView) findViewById(R.id.ctv_setLocation);
	}

	private void getValues() {
		Bundle extras = getIntent().getExtras();
		name = extras.getString("name");
		description = extras.getString("description");
		xpRew = extras.getDouble("xpRew");
		hpPen = extras.getDouble("hpPen");
		locationEnabled = extras.getBoolean("locationEnabled");
		if (locationEnabled) {
			lat = extras.getDouble("lat");
			lon = extras.getDouble("lon");
			location = new LatLng(lat, lon);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ctv_setLocation:
			Bundle bundle = new Bundle();
			bundle.putBoolean("locationEnabled", locationEnabled);
			if (locationEnabled) {
				bundle.putDouble("lat", lat);
				bundle.putDouble("lon", lon);
			}
			Intent intent = new Intent();
			intent.setClass(this, SetItemLocation.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, SET_LOCATION_REQUEST);
			break;
		case R.id.ctv_locationAlert:
			System.out.println("CheckedTextView");
			cb_locationAlert.toggle();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_CANCELED) {
		} else {
			switch (requestCode) {
			case SET_LOCATION_REQUEST:
				Bundle extras = intent.getExtras();
				lat = extras.getDouble("lat");
				lon = extras.getDouble("lon");
				location = new LatLng(lat, lon);
				break;
			}
		}
	}
}
