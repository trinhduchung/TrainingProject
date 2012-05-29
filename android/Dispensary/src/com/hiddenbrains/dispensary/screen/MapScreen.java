package com.hiddenbrains.dispensary.screen;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.hiddenbrains.dispensary.maphelper.MyItemizedOverlay;
import com.hiddenbrains.dispensary.resource.ResourceLatLong;

public class MapScreen extends MapActivity{
	
	MapView mapView;
	//GeoPoint points;
	List<Overlay> mapOverlay;
	Drawable image;
	MyItemizedOverlay overlayLayout;
	ResourceLatLong resource;
	
	@Override
	protected void onCreate(Bundle icicle) {
			super.onCreate(icicle);
			try
			{
				setContentView(R.layout.map_screen);
				mapView=(MapView) findViewById(R.id.mapview);
				mapView.setBuiltInZoomControls(true);
//				image=getResources().getDrawable(R.drawable.green_leaf);
				mapOverlay=mapView.getOverlays();
				Bundle bundle=getIntent().getExtras();
				String lat=bundle.getString("lat");
				String lont=bundle.getString("long");
				String marker=bundle.getString("marker");
				if(marker.equals("green-leaf.png"))
				{
	        		image = getResources().getDrawable(R.drawable.green_leaf);
				}
				else if(marker.equals("blue-leaf.png"))
				{
					image = getResources().getDrawable(R.drawable.blue_leaf);	
				}
				else if(marker.equals("orange_leaf.png"))
				{
					image = getResources().getDrawable(R.drawable.orange_leaf);	
				}
				else if(marker.equals("delivery.png"))
				{
					image = getResources().getDrawable(R.drawable.delivery);	
				}
				else if(marker.equals("delivery_blue.png"))
				{
					image = getResources().getDrawable(R.drawable.delivery_blue);	
				}
				else if(marker.equals("delivery_orange.png"))
				{
					image = getResources().getDrawable(R.drawable.delivery_orange);	
				}	
				else
				{
					image = getResources().getDrawable(R.drawable.green_leaf);	
				}
				overlayLayout = new MyItemizedOverlay(image, mapView);
				overlayLayout.setContext(this);
				if(lat.equals("0")||lont.equals("0"))
				{
					Builder builder = new AlertDialog.Builder(MapScreen.this);
					builder.setTitle("Provider Error ");
					builder.setMessage("Geo-coordinate not found.");
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() 
					{
						
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.dismiss();
//							finish();
							 final MapController mc = mapView.getController();
						        mc.setZoom(8);
							
						}
					});
					AlertDialog alert = builder.create();
				    alert.show();	
				}
				else
				{
					String []temp={lat,lont};  // resource.getLat_Lng();
			        String []title ={bundle.getString("title")};
			        
			        String []address ={bundle.getString("address")};  //resource.getAddress();
			        int len = title.length;
			        GeoPoint []points = new GeoPoint[len];
			        OverlayItem []overlayItem = new OverlayItem[len];
			        for(int i=0; i<len;i++)
			        {
			        	points[0] = new GeoPoint((int)((Double.parseDouble(temp[0]))*1E6),(int)((Double.parseDouble(temp[1]))*1E6));
			        	overlayItem[0] = new OverlayItem(points[0],title[0],address[0]);
			        	overlayLayout.addOverlay(overlayItem[0]);
			        }
			        mapOverlay.add(overlayLayout);
			        final MapController mc = mapView.getController();
			        mc.animateTo(points[0]);
			        mc.setZoom(14);

			        mapView.setStreetView(true);
			        mapView.setSatellite(false);
				}
			}
			catch(Exception e){
				e.getMessage();
			}
		
			
   }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	

}
