package com.hiddenbrains.dispensary.screen;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hiddenbrains.dispensary.common.DispensaryConstant;
import com.hiddenbrains.dispensary.service.GPSInfo;
import com.hiddenbrains.dispensary.service.GPSService;
import com.hiddenbrains.dispensary.service.GPSService.GPSServiceListener;

public class LoadingScreen extends Activity implements GPSServiceListener{
	
	public static final int GPS_CODE = 0x01;
	public static final int MAIN_CODE = 0x02;
	
	private GPSService gpsService;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadingscreen);
        gpsService = new GPSService(this, this);
        gpsService.getCurrentLocation();
    } 
    
    
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == GPS_CODE) {
			gpsService.getCurrentLocation();
		} else if (requestCode == MAIN_CODE) {
			finish();
		}
	}



	@Override
	public void onGetGPSSuccess(GPSInfo gpsInfo) {
		gpsInfo.toString();
		
		DispensaryConstant.latitude = gpsInfo.getLat()+"";
		DispensaryConstant.longitude = gpsInfo.getLng()+"";

		Intent i = new Intent(LoadingScreen.this,DispensaryListScreen.class);
		startActivityForResult(i, MAIN_CODE);
	}
	@Override
	public void onGetGPSFail() {
		DispensaryConstant.latitude = "36.70366";
		DispensaryConstant.longitude = "-119.443359";
		
		Intent i = new Intent(LoadingScreen.this,DispensaryListScreen.class);
		startActivityForResult(i, MAIN_CODE);
	}
}