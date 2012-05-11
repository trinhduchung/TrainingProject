package com.hiddenbrains.dispensary.maphelper;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
public class MyItemizedOverlay extends ShowBalloonItem<OverlayItem>{
	private ArrayList<OverlayItem> m_overlays = new ArrayList<OverlayItem>();
	
	public MyItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
//		c = mapView.getContext();
	}

	public void addOverlay(OverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index) {
//		Toast.makeText(c, "onBalloonTap for overlay index " + index,
//				Toast.LENGTH_LONG).show();
		return true;
	}
	
}
