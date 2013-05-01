package com.example.lifelogging;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SetItemLocation extends Activity implements OnMapClickListener,
		OnMapLongClickListener, OnMarkerDragListener, OnSeekBarChangeListener {
	private GoogleMap map;
	LatLng location;
	Marker locationMarker = null;
<<<<<<< HEAD
	Circle locationArea = null;
	SeekBar sbLocationArea;
	TextView tvLocationArea;
=======
	Circle ciLocationArea = null;
	private int locationArea;
>>>>>>> Implemented sorting, greying out finished items and cleaned up item
	final int RQS_GooglePlayServices = 1;
	int i = 0;
	SeekBar sbLocationArea;
	TextView tvLocationArea;
	Location myLocation;
	LatLng endingPos, startingPos;
	UiSettings mapSettings;
	private boolean locationSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_item_location);
		getValues();
		getViewReferences();
		initialize();

		map.setOnMapClickListener(this);
		map.setOnMapLongClickListener(this);
		map.setOnMarkerDragListener(this);
		sbLocationArea.setOnSeekBarChangeListener(this);
	}

	private void initialize() {
		if (locationSet) {
			makeMarker(location);
			makeAreaCircle(location);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
		}
<<<<<<< HEAD
		map.setOnMapClickListener(this);
		map.setOnMapLongClickListener(this);
		map.setOnMarkerDragListener(this);
		sbLocationArea.setOnSeekBarChangeListener(this);
=======
		map.setMyLocationEnabled(true);
		mapSettings = map.getUiSettings();
		mapSettings.setMyLocationButtonEnabled(true);
		sbLocationArea.setProgress(locationArea / 50);
		updateRadius(locationArea);
>>>>>>> Implemented sorting, greying out finished items and cleaned up item
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_location, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_legalnotices:
<<<<<<< HEAD
			String LicenseInfo = GooglePlayServicesUtil
					.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
			AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(this);
			LicenseDialog.setTitle("Legal Notices").setMessage(LicenseInfo);
			LicenseDialog.show();
=======
			showLicenseDialog();
>>>>>>> Implemented sorting, greying out finished items and cleaned up item
			return true;
		case R.id.menu_set_location_save:
			finishActivity();
		}
		return super.onOptionsItemSelected(item);
	}

	private void finishActivity() {
		location = locationMarker.getPosition();
		setResult(RESULT_OK, getResultIntent());
		finish();
	}

	private void showLicenseDialog() {
		String LicenseInfo = GooglePlayServicesUtil
				.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
		AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(this);
		LicenseDialog.setTitle("Legal Notices");
		LicenseDialog.setMessage(LicenseInfo);
		LicenseDialog.show();
	}

	private void getViewReferences() {
		map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.mEditItemLocation)).getMap();
		sbLocationArea = (SeekBar) findViewById(R.id.sbLocationArea);
<<<<<<< HEAD
		tvLocationArea = (TextView) findViewById(R.id.tvLocationRange);
=======
		tvLocationArea = (TextView) findViewById(R.id.tvLocationArea);
>>>>>>> Implemented sorting, greying out finished items and cleaned up item
	}

	private void makeMarker(LatLng point) {
		locationMarker = map.addMarker(new MarkerOptions().position(point)
				.draggable(true));
		location = point;
	}

	private void makeAreaCircle(LatLng point) {
		ciLocationArea = map.addCircle(new CircleOptions().center(point)
				.radius(locationArea).strokeColor(0x00000000)
				.fillColor(0x4433b5e5));
	}

	private void getValues() {
		Bundle extras = getIntent().getExtras();
<<<<<<< HEAD
		locationEnabled = extras.getBoolean("locationEnabled");
		if (locationEnabled) {
			location = new LatLng(extras.getDouble("lat"),
					extras.getDouble("lon"));
=======
		locationSet = extras.getBoolean("locationSet");
		if (locationSet) {
			location = new LatLng(extras.getDouble("lat"),
					extras.getDouble("lon"));
			locationArea = extras.getInt("locationArea");
>>>>>>> Implemented sorting, greying out finished items and cleaned up item
		}
	}

	private Intent getResultIntent() {
		Intent intent = new Intent(getBaseContext(), MainActivity.class);
		intent.putExtras(getBundleWithValues());
		return intent;
	}

	private Bundle getBundleWithValues() {
		Bundle bundle = new Bundle();
		bundle.putDouble("lat", location.latitude);
		bundle.putDouble("lon", location.longitude);
		bundle.putInt("locationArea", locationArea);
		return bundle;
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK, getResultIntent());
		super.onBackPressed();
	}

	@Override
	public void onMapClick(LatLng point) {
		map.animateCamera(CameraUpdateFactory.newLatLng(point));
	}

	@Override
	public void onMapLongClick(LatLng point) {
		removeMarkerAndCircle();
		makeMarker(point);
		makeAreaCircle(point);
	}

	private void removeMarkerAndCircle() {
		if (locationMarker != null) {
			locationMarker.remove();
		}
		if (ciLocationArea != null) {
			ciLocationArea.remove();
		}
	}

	private void updateRadius(int radius) {
		ciLocationArea.setRadius(radius);
		tvLocationArea.setText("Radius: " + Integer.toString(radius) + "m");
		locationArea = radius;
	}

	@Override
	public void onMarkerDrag(Marker marker) {
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		endingPos = marker.getPosition();
		if (Math.abs(endingPos.latitude - startingPos.latitude) < 0.0005
				&& Math.abs(endingPos.longitude - startingPos.longitude) < 0.0005) {
<<<<<<< HEAD
			locationMarker.remove();
			locationArea.remove();
		} else {
			locationArea.setCenter(marker.getPosition());
=======
			removeMarkerAndCircle();
>>>>>>> Implemented sorting, greying out finished items and cleaned up item
		}
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		startingPos = marker.getPosition();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
<<<<<<< HEAD
		locationArea.setRadius(progress * 100);
		tvLocationArea.setText(Integer.toString(progress * 100));

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
=======
		updateRadius(progress * 50);
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
>>>>>>> Implemented sorting, greying out finished items and cleaned up item
	}
}
