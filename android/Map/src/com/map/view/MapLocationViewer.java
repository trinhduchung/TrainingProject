package com.map.view;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.google.android.maps.MapView;
import com.map.MapApplication;
import com.map.R;
import com.map.services.DatabaseHelper;

public class MapLocationViewer extends LinearLayout {

	private MapLocationOverlay overlay;
	
    //  Known latitude/longitude coordinates that we'll be using.
    private List<MapLocation> mapLocations;
    
    private MapView mapView;
    private Context context;
    DatabaseHelper db;
	public MapLocationViewer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MapLocationViewer(Context context) {
		super(context);
		init(context);
	}

	public void init(Context ctx) {		
		context = ctx;
		setOrientation(VERTICAL);
		setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT));

		mapView = new MapView(getContext(),"0_CaK9g9NQHD2sDVeZn8qIFM1q1lcMdG2fm_Slw");
		mapView.setEnabled(true);
		mapView.setClickable(true);
		addView(mapView);

		overlay = new MapLocationOverlay(this);
		mapView.getOverlays().add(overlay);

    	mapView.getController().setZoom(14);
    	mapView.getController().setCenter(getMapLocations().get(0).getPoint());
    	mapView.displayZoomControls(true);
    	
    	
	}
	
	public List<MapLocation> getMapLocations() {
		mapLocations = MapApplication.Instance().mapLocations;
		if (mapLocations == null | mapLocations.size() <= 0) {
			mapLocations = new ArrayList<MapLocation>();
			mapLocations.add(new MapLocation("North Beach",37.799800872802734,-122.40699768066406, R.drawable.a1));
			mapLocations.add(new MapLocation("China Town",37.792598724365234,-122.40599822998047, R.drawable.a2));
			mapLocations.add(new MapLocation("Fisherman's Wharf",37.80910110473633,-122.41600036621094, R.drawable.a3));
			mapLocations.add(new MapLocation("Financial District",37.79410171508789,-122.4010009765625, R.drawable.a4));
			for (MapLocation loc : mapLocations) {
				db.insertLocation(loc);
			}
		}
		return mapLocations;
	}

	public MapView getMapView() {
		return mapView;
	}
}
