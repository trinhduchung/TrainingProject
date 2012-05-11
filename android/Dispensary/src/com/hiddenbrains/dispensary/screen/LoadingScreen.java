package com.hiddenbrains.dispensary.screen;


import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.MapController;
import com.hiddenbrains.dispensary.common.DispensaryConstant;

public class LoadingScreen extends Activity {
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	setContentView(R.layout.loadingscreen);
        	LocationManager lm=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,500L,500.0f,locationLisenter);
        	Location ll=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        	if(ll==null)
        	{
//				DispensaryConstant.latitude = "41.903299";
//				DispensaryConstant.longitude ="-87.634048";
        		DispensaryConstant.latitude = "36.70366";
				DispensaryConstant.longitude ="-119.443359";
//				36.70366,-119.443359
//        		DispensaryConstant.latitude = "0";
//				DispensaryConstant.longitude ="0";
				
				AlertDialog.Builder builder = new AlertDialog.Builder(LoadingScreen.this);
			    builder.setTitle("Provider Error ");
				builder.setMessage("Geo-coordinate not found.");
				builder.setPositiveButton("ok", new DialogInterface.OnClickListener() 
				{
						public void onClick(DialogInterface dialog, int which) 
						{
						finish();
					}});
		    	  AlertDialog alert = builder.create();
			      //alert.show();
        		
        	}
        	else
        	{
        		DispensaryConstant.latitude = ll.getLatitude()+"";
    			DispensaryConstant.longitude =ll.getLongitude()+"";
        	}
        }
        catch(Exception e){
        	e.getMessage();
        }
        final int count_min=2000;
        Thread thread = new Thread(){
        	int counter=0;
        	@Override
        	public void run() {
        		super.run();
        		try{
        			while(counter<count_min){
        				sleep(100);
        				counter+=100;
        			}
        		}catch (Exception e) {
				e.getMessage();
        		}
finally{
					
					boolean flag = false;
			    	Calendar cal = Calendar.getInstance();
			    	int day = cal.get(Calendar.DAY_OF_MONTH);
			    	int month = cal.get(Calendar.MONTH)+1;
			    	int year = cal.get(Calendar.YEAR);

			    	if(year == 2020){//hack year
			    		if(month == 10 && day <30){
			    			flag = true;//true
			    		}else if(month==9){
			    			flag=true;
			    		}
			    	}else{
			    		flag = false;
			    	}

			    	if(flag)
			    	{
			    		Intent i = new Intent(LoadingScreen.this,DispensaryListScreen.class);
						startActivity(i);
						finish();
			    	}
			    	
		        	// TODO for test
		    		Intent i = new Intent(LoadingScreen.this,DispensaryListScreen.class);
					startActivity(i);
					finish();

				}
        	}

    		//Intent i = new Intent(LoadingScreen.this,DispensaryListScreen.class);
			//startActivity(i);
			//finish();
        	
        };
        thread.start();
    }
    private LocationListener locationLisenter=new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			DispensaryConstant.latitude = location.getLatitude()+"";
			DispensaryConstant.longitude =location.getLongitude()+"";
		}
	};
}