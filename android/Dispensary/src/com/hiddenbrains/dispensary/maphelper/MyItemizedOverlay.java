package com.hiddenbrains.dispensary.maphelper;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.hiddenbrains.dispensary.common.DispensaryConstant;
import com.hiddenbrains.dispensary.screen.DispansaryApplication;
import com.hiddenbrains.dispensary.screen.DispensaryListScreen;
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
		if (DispansaryApplication.map_flag == 1) {
			
			System.out.println("ballon tap" + m_overlays.get(index).routableAddress());
			if (mContext != null) {
//				Intent intent = new Intent(mContext, DispensaryListScreen.class);
//				intent.putExtra("from_overlay", true);
//				intent.putExtra("position", m_overlays.get(index).routableAddress());
				((Activity) mContext ).finish();//startActivity(intent);
			} 
			
		}
		return true;
	}
	
}
