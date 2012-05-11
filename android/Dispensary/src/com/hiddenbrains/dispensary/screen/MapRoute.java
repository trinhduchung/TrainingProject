package com.hiddenbrains.dispensary.screen;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.maps.MapActivity;

public class MapRoute extends MapActivity 
{

	public static String dlat = "", dlong = "",current_latitude,current_longitude;
	Uri uri;
	private static Location myLocation = null;
    private static LocationManager myLocationManager = null;
    
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		try
		{
			
			myLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		        
			if(myLocation == null) 
			{
				myLocation = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			if(myLocation!=null)
			{
				current_latitude = Location.convert(myLocation.getLatitude(), Location.FORMAT_DEGREES);
				current_longitude = Location.convert(myLocation.getLongitude(), Location.FORMAT_DEGREES);
			}
			
			
			uri = Uri.parse("http://maps.google.com/maps?&saddr="+ current_latitude + ","+ current_longitude + "&daddr=" + dlat + ","+ dlong);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
			finish();
			
		}catch (Exception e) 
		{
			e.getMessage();
		}
	}

	@Override
	protected boolean isRouteDisplayed() 
	{
		return false;
	}

}
