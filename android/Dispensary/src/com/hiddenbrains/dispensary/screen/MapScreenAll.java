package com.hiddenbrains.dispensary.screen;

import java.util.List;

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

public class MapScreenAll extends MapActivity {
	
    MapView mapview;
    Drawable image;
    List<Overlay> mapOverlay;
    MyItemizedOverlay overlayLayout;
    
    @Override
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        try
		{
	        setContentView(R.layout.map_screen);
	        
	        Bundle bundle = this.getIntent().getExtras();
	        int index = bundle.getInt("index");
	        ResourceLatLong resource = new ResourceLatLong(index);
	        
	        mapview = (MapView)findViewById(R.id.mapview);
	        mapview.setBuiltInZoomControls(true);
//	        image = getResources().getDrawable(R.drawable.green_leaf);
	        mapOverlay = mapview.getOverlays();
	        
	        String [][]temp = resource.getLat_Lng();
	        String []title =  resource.getTitle();
	        String []address = resource.getAddress();
	        int len = title.length;
	        GeoPoint []points = new GeoPoint[len];
	        OverlayItem []overlayItem = new OverlayItem[len];
	        for(int i=0; i<len;i++)
	        {
	        	points[i] = new GeoPoint((int)((Double.parseDouble(temp[i][0]))*1E6),(int)((Double.parseDouble(temp[i][1]))*1E6));
	        	overlayItem[i] = new OverlayItem(points[i],title[i],address[i]);

	        	if(DispensaryListScreen.icon_image.get(i).toString().equals("green-leaf.png"))
				{
	        		image = getResources().getDrawable(R.drawable.green_leaf);
				}
				else if(DispensaryListScreen.icon_image.get(i).toString().equals("blue-leaf.png"))
				{
					image = getResources().getDrawable(R.drawable.blue_leaf);	
				}
				else if(DispensaryListScreen.icon_image.get(i).toString().equals("orange_leaf.png") || DispensaryListScreen.icon_image.get(i).toString().equals("orange-leaf.png"))
				{
					image = getResources().getDrawable(R.drawable.orange_leaf);	
				}
				else if(DispensaryListScreen.icon_image.get(i).toString().equals("delivery.png"))
				{
					image = getResources().getDrawable(R.drawable.delivery);	
				}
				else if(DispensaryListScreen.icon_image.get(i).toString().equals("delivery_blue.png"))
				{
					image = getResources().getDrawable(R.drawable.delivery_blue);	
				}
				else if(DispensaryListScreen.icon_image.get(i).toString().equals("delivery_orange.png"))
				{
					image = getResources().getDrawable(R.drawable.delivery_orange);	
				}else if(DispensaryListScreen.icon_image.get(i).toString().equals("app_diamond.png"))
				{
					image = getResources().getDrawable(R.drawable.app_diamond);	
				}
				else
				{
					image = getResources().getDrawable(R.drawable.green_leaf);	
				}

	        	overlayLayout = new MyItemizedOverlay(image, mapview);
	        	overlayLayout.setContext(this);
		        overlayLayout.addOverlay(overlayItem[i]);
		        mapOverlay.add(overlayLayout);
	        }
	        
	        final MapController mc = mapview.getController();
	        mc.animateTo(points[0]);
	        mc.setZoom(8);
		}
		catch(Exception e)
		{
			e.getMessage();
		}
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}