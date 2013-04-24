package com.example.lifelogging;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SetItemLocation extends Activity implements OnMapClickListener,
		OnMapLongClickListener, OnMarkerDragListener {
	private GoogleMap map;
	LatLng location;
	boolean locationEnabled;
	Marker locationMarker = null;
	Circle locationArea = null;
	final int RQS_GooglePlayServices = 1;

	Location myLocation;
	LatLng endingPos, startingPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_item_location);
		getValues();
		getViewReferences();
		if (locationEnabled) {
			makeMarker(location);
			makeAreaCircle(location);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
		}
		map.setOnMapClickListener(this);
		map.setOnMapLongClickListener(this);
		map.setOnMarkerDragListener(this);
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
			String LicenseInfo = GooglePlayServicesUtil
					.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
			AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(this);
			LicenseDialog.setTitle("Legal Notices");
			LicenseDialog.setMessage(LicenseInfo);
			LicenseDialog.show();
			return true;
		case R.id.menu_set_location_save:
			location = locationMarker.getPosition();
			setResult(RESULT_OK, getResultIntent());
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void getViewReferences() {
		map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.mEditItemLocation)).getMap();
	}

	private void makeMarker(LatLng point) {
		locationMarker = map.addMarker(new MarkerOptions().position(location)
				.draggable(true));
	}

	private void makeAreaCircle(LatLng point) {
		locationArea = map.addCircle(new CircleOptions().center(point)
				.radius(1000).strokeColor(0x00000000).fillColor(0x4433b5e5));
	}

	private void getValues() {
		Bundle extras = getIntent().getExtras();
		locationEnabled = extras.getBoolean("locationEnabled");
		if (locationEnabled) {
			location = new LatLng(extras.getDouble("lat"), extras.getDouble("lon"));
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
		if (locationMarker != null) {
			locationMarker.remove();
		}
		if (locationArea != null) {
			locationArea.remove();
		}
		locationMarker = map.addMarker(new MarkerOptions().position(point)
				.draggable(true));
		locationArea = map.addCircle(new CircleOptions().center(point)
				.radius(1000).strokeColor(0x00000000).fillColor(0x4433b5e5));
	}

	@Override
	public void onMarkerDrag(Marker marker) {
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		endingPos = marker.getPosition();
		if (Math.abs(endingPos.latitude - startingPos.latitude) < 0.0005
				&& Math.abs(endingPos.longitude - startingPos.longitude) < 0.0005) {
			locationMarker.remove();
			locationArea.remove();
		}
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		startingPos = marker.getPosition();
	}
}
