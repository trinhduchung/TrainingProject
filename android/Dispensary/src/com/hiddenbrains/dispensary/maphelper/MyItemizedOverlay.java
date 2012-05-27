package com.hiddenbrains.dispensary.maphelper;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
public class MyItemizedOverlay extends ShowBalloonItem<OverlayItem>{
	private ArrayList<OverlayItem> m_overlays = new ArrayList<OverlayItem>();
	private Context mContext;
	public MyItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
//		c = mapView.getContext();
	}
	
	public void setContext(Context context) {
		mContext = context;
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
		System.out.println("ballon tap");
		if (mContext != null) {
			((Activity) mContext ).finish();
		} 
		return true;
	}
	
}
