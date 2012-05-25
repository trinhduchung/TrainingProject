package com.hiddenbrains.dispensary.service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import com.hiddenbrains.dispensary.screen.LoadingScreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

public class GPSService {
	LocationManager _locManager;
	LocationListener _locListener;
	LocationListener _networkLocListener;
	public GPSServiceListener _listener;
	GPSInfo _gpsInfo;
	Context _context;
	boolean _gpsTimeout = false;
	Timer _gpsTimer = new Timer();

	public GPSService(Context context , GPSServiceListener listener) {
		this._context = context;
		this._listener = listener;
		_locManager = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public void getCurrentLocation() {
		
		Message msg = _handler.obtainMessage(2);
		_handler.sendMessageDelayed(msg, 8000);
		
		String provider = Settings.Secure.getString(_context.getContentResolver(),Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (provider == null || provider.equalsIgnoreCase("")) {
			showError();
		} else {
			getByGSPProvider();
		}
	}
	
	private void getByGSPProvider() {
		boolean gps_enabled = _locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (gps_enabled) {
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
			_locManager.getBestProvider(criteria, true);
			_locListener = new MyLocationListener();
			_locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, _locListener);
			
			Message message = _handler.obtainMessage(0);
			_handler.sendMessageDelayed(message, 3000);
		} else {
			boolean network_enabled = _locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (network_enabled) {
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
				_locManager.getBestProvider(criteria, true);
				_networkLocListener = new MyLocationListener();
				_locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, _networkLocListener);
				
				Message message = _handler.obtainMessage(1);
				_handler.sendMessageDelayed(message, 3000);
			} else {
				_listener.onGetGPSFail();
			}
		}
	}
	
	private void getByNetworkProvider() {
		boolean network_enabled = _locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (network_enabled) {
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
			_locManager.getBestProvider(criteria, true);
			_networkLocListener = new MyLocationListener();
			_locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, _networkLocListener);
			
			Message message = _handler.obtainMessage(1);
			_handler.sendMessageDelayed(message, 5000);
		}
	}
	
	private Handler _handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (_gpsTimeout == false) {
				if (msg.what == 0) {
					_locManager.removeUpdates(_locListener);
					getByNetworkProvider();
				} else if (msg.what == 1) {
					_locManager.removeUpdates(_networkLocListener);
					_listener.onGetGPSFail();
				} else if (msg.what == 2) {
					System.out.println("here");
					if (_locListener != null) {
						_locManager.removeUpdates(_locListener);
					}
					if (_networkLocListener != null) {
						_locManager.removeUpdates(_networkLocListener);
					}
					_listener.onGetGPSFail();
				}
			}
		}
		
	};
	
	public interface GPSServiceListener {
		public void onGetGPSSuccess(GPSInfo gpsInfo);
		public void onGetGPSFail();
	}

	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			
			if (loc != null) {
				_gpsInfo = new GPSInfo();
				_gpsInfo.setLat(loc.getLatitude());
				_gpsInfo.setLng(loc.getLongitude());
				_locManager.removeUpdates(this);
				Geocoder geocoder = new Geocoder(_context, Locale.getDefault());
				try {
					List<Address> addresses = geocoder.getFromLocation(
							_gpsInfo.getLat(), _gpsInfo.getLng(), 1);
					if (addresses.size() > 0) {
						Address address = addresses.get(0);
						String info = "";
						for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
							info += address.getAddressLine(i) + ",";
						_gpsInfo.setInfo(info);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				_listener.onGetGPSSuccess(_gpsInfo);
				System.out.println("on location changed : " + _gpsInfo.getInfo());
			} else {
				_locManager.removeUpdates(this);
				_listener.onGetGPSFail();
			}
			
			_gpsTimeout = true;
		}

		@Override
		public void onProviderDisabled(String provider) {
			showError();
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	Handler timerHandler = new Handler();
	
	public void showError() {
		if (!(_context instanceof Activity)) {
			return;
		}
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				_context);
		alertDialogBuilder.setMessage("Application alows your GSP service.Could you alow this?")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent callGPSSettingIntent = new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						((Activity)_context).startActivityForResult(callGPSSettingIntent, LoadingScreen.GPS_CODE);
					}
				});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						_listener.onGetGPSFail();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}
}
