package com.hiddenbrains.dispensary.MapRoute;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Parser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.StaticLayout;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.hiddenbrains.dispensary.common.DispensaryConstant;
import com.hiddenbrains.dispensary.screen.LoadingScreen;
import com.hiddenbrains.dispensary.screen.MapRoute;
import com.hiddenbrains.dispensary.screen.R;

public class MapRouteActivity extends MapActivity {

	LinearLayout linearLayout;
	MapView mapView;
	Button click;
	private Road mRoad;
	public String url;
	KMLHandler handler;
	static double fromLat = 18.092828, fromLon = 83.552141, toLat = 22.608306, toLon = 88.258667; //VIZAG-KOLKATA
	public int citys;
	boolean function_flag=true; 
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_screen);
		mapView=(MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		try
		{
			fromLat = Double.parseDouble(DispensaryConstant.latitude);
			fromLon = Double.parseDouble(DispensaryConstant.longitude);
			toLat = Double.parseDouble(MapRoute.dlat);
			toLon = Double.parseDouble(MapRoute.dlong);
			if(toLat==0.0||toLon==0.0)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(MapRouteActivity.this);
			    builder.setTitle("Provider Error ");
				builder.setMessage("Geo-coordinate not found.");
				builder.setPositiveButton("ok", new DialogInterface.OnClickListener() 
				{
						public void onClick(DialogInterface dialog, int which) 
						{
//						finish();
					}});
		    	  AlertDialog alert = builder.create();
			      alert.show();
			}
			url = "http://maps.google.com/maps?f=d&hl=en&saddr="+fromLat+","+fromLon+"&daddr="+toLat+","+toLon+"&ie=UTF8&0&om=0&output=kml";
			handler = new KMLHandler();
		
			function();
			
		}catch (Exception e) 
		{
			e.getMessage();
		}
	}


	
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) 
		{
//			TextView textView = (TextView) findViewById(R.id.description);
//			textView.setText(mRoad.mName + " " + mRoad.mDescription);
			MapOverlay mapOverlay = new MapOverlay(mRoad, mapView);
			List<Overlay> listOfOverlays = mapView.getOverlays();
			listOfOverlays.add(mapOverlay);
			mapView.invalidate();
			
		};
	};
	
	
	
	
	void function(){
		InputStream is = getConnection(url);
		SAXParser parser;
		try {
			parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(is, handler);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		mRoad = handler.mRoad;
		mHandler.sendEmptyMessage(0);
	}
	
	

	
	private InputStream getConnection(String url) {
		InputStream is = null;
		try {
			URLConnection conn = new URL(url).openConnection();
			is = conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return is;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}


}

class MapOverlay extends com.google.android.maps.Overlay 
{
	
	Road mRoad;
	ArrayList<GeoPoint> mPoints;

	public MapOverlay(Road road, MapView mv) 
	{
		try
		{
			mRoad = road;
			if (road.mRoute.length > 0) {
				mPoints = new ArrayList<GeoPoint>();
				for (int i = 0; i < road.mRoute.length; i++) {
					mPoints.add(new GeoPoint((int) (road.mRoute[i][1] * 1000000),
							(int) (road.mRoute[i][0] * 1000000)));
				}
				int moveToLat = (mPoints.get(0).getLatitudeE6() + (mPoints.get(
						mPoints.size() - 1).getLatitudeE6() - mPoints.get(0)
						.getLatitudeE6()) / 2);
				int moveToLong = (mPoints.get(0).getLongitudeE6() + (mPoints.get(
						mPoints.size() - 1).getLongitudeE6() - mPoints.get(0)
						.getLongitudeE6()) / 2);
				GeoPoint moveTo = new GeoPoint(moveToLat, moveToLong);

				MapController mapController = mv.getController();
				mapController.animateTo(moveTo);
				mapController.setZoom(14);
			}
		}catch (Exception e) 
		{
			e.getMessage();
		}
	}

	@Override
	public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
		super.draw(canvas, mv, shadow);
		drawPath(mv, canvas);
		return true;
	}

	public void drawPath(MapView mv, Canvas canvas) 
	{
		try
		{
			int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(3);
			for (int i = 0; i < mPoints.size(); i++) {
				Point point = new Point();
				mv.getProjection().toPixels(mPoints.get(i), point);
				x2 = point.x;
				y2 = point.y;
				if (i > 0) {
					canvas.drawLine(x1, y1, x2, y2, paint);
				}
				x1 = x2;
				y1 = y2;
			}
		}catch (Exception e) 
		{
			e.getMessage();
			
		}
		
	}
}