package com.map.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.map.R;

public class MapLocationOverlay  extends Overlay {

	//  Store these as global instances so we don't keep reloading every time
    private Bitmap bubbleIcon, shadowIcon;
    private Drawable mImage;
    private MapLocationViewer mapLocationViewer;
	private float mPosX = 100;
	private float mPosY = 100;
	private Paint	innerPaint, borderPaint, textPaint;
	private List<MapLocation> _listDrawMap = new ArrayList<MapLocation>(4);
	private List<RectF> _rects = new ArrayList<RectF>(4);
	private RectF _currentRect;
	private HashMap<MapLocation, RectF> _hm = new HashMap<MapLocation, RectF>(4);
    
    //  The currently selected Map Location...if any is selected.  This tracks whether an information  
    //  window should be displayed & where...i.e. whether a user 'clicked' on a known map location
    private MapLocation selectedMapLocation;  
    private MapView _mapView;
	public MapLocationOverlay(MapLocationViewer		mapLocationViewer) {
		
		this.mapLocationViewer = mapLocationViewer;
		_mapView = this.mapLocationViewer.getMapView();
		bubbleIcon = BitmapFactory.decodeResource(mapLocationViewer.getResources(),R.drawable.bubble);
		shadowIcon = BitmapFactory.decodeResource(mapLocationViewer.getResources(),R.drawable.shadow);
		 mImage = mapLocationViewer.getResources().getDrawable(R.drawable.default_avatar);

		 mImage.setBounds(0, 0, mImage.getIntrinsicWidth(), mImage.getIntrinsicHeight());
		 
		resetHashMap();
	}
	
	public void resetHashMap() {
		_hm.clear();
		_rects.clear();
		RectF hitTestRecr = new RectF();
		Point screenCoords = new Point();
		Iterator<MapLocation> iterator = mapLocationViewer.getMapLocations()
				.iterator();
		while (iterator.hasNext()) {
			MapLocation testLocation = iterator.next();

			// Translate the MapLocation's lat/long coordinates to screen
			// coordinates
			_mapView.getProjection().toPixels(testLocation.getPoint(),
					screenCoords);

			// Create a 'hit' testing Rectangle w/size and coordinates of our
			// icon
			// Set the 'hit' testing Rectangle with the size and coordinates of
			// our on screen icon
			hitTestRecr.set(-bubbleIcon.getWidth() / 2, -bubbleIcon.getHeight(), bubbleIcon.getWidth() / 2, 0);
//			hitTestRecr = new RectF(0, 0, 20, 20);
			hitTestRecr.offset(screenCoords.x, screenCoords.y);
			
			Log.d("MapLocationOverlay", "center x = " + hitTestRecr.centerX()
					+ " ; center y = " + hitTestRecr.centerY());
			
			_hm.put(testLocation, hitTestRecr);
		}
		Iterator it = _hm.keySet().iterator();
		while (it.hasNext()) {
			MapLocation key = (MapLocation) it.next();
			RectF value = (RectF) _hm.get(key);
			_rects.add(value);
			
		}
		Log.d("Overlay", "size : " + _rects.size());
	}
	
	private MapLocation getMapLocationByRect(RectF rect) {
		Iterator it = _hm.keySet().iterator();
		while (it.hasNext()) {
			MapLocation key = (MapLocation) it.next();
			RectF value = (RectF) _hm.get(key);
			if (value.equals(rect)) {
				return key;
			}
		}
		return null;
	}
	
	@Override
	public boolean onTap(GeoPoint p, MapView	mapView)  {
		
		//  Store whether prior popup was displayed so we can call invalidate() & remove it if necessary.
		boolean isRemovePriorPopup = selectedMapLocation != null;  

		//  Next test whether a new popup should be displayed
		selectedMapLocation = getHitMapLocation(mapView,p);
		if ( isRemovePriorPopup || selectedMapLocation != null) {
			mapView.invalidate();
		}		
		
		//  Lastly return true if we handled this onTap()
		return selectedMapLocation != null;
	}
	
    @Override
	public void draw(Canvas canvas, MapView	mapView, boolean shadow) {
    	
   		drawMapLocations(canvas, mapView, shadow);
   		if (selectedMapLocation != null) {
   			drawInfoWindow(canvas, mapView, shadow);
   			mapView.getController().setCenter(selectedMapLocation.getPoint());
   		}
    }

    /**
     * Test whether an information balloon should be displayed or a prior balloon hidden.
     */
    private MapLocation getHitMapLocation(MapView	mapView, GeoPoint	tapPoint) {
    	
    	//  Track which MapLocation was hit...if any
    	MapLocation hitMapLocation = null;
		
    	RectF hitTestRecr = new RectF();
		Point screenCoords = new Point();
    	Iterator<MapLocation> iterator = mapLocationViewer.getMapLocations().iterator();
    	while(iterator.hasNext()) {
    		MapLocation testLocation = iterator.next();
    		
    		//  Translate the MapLocation's lat/long coordinates to screen coordinates
    		mapView.getProjection().toPixels(testLocation.getPoint(), screenCoords);

	    	// Create a 'hit' testing Rectangle w/size and coordinates of our icon
	    	// Set the 'hit' testing Rectangle with the size and coordinates of our on screen icon
    		hitTestRecr.set(-bubbleIcon.getWidth()/2,-bubbleIcon.getHeight(),bubbleIcon.getWidth()/2,0);
    		hitTestRecr.offset(screenCoords.x,screenCoords.y);
    		
	    	//  Finally test for a match between our 'hit' Rectangle and the location clicked by the user
    		mapView.getProjection().toPixels(tapPoint, screenCoords);
    		if (hitTestRecr.contains(screenCoords.x,screenCoords.y)) {
    			hitMapLocation = testLocation;
    			mImage = mapLocationViewer.getResources().getDrawable(testLocation.getImage());
    			break;
    		}
    	}
    	
    	//  Lastly clear the newMouseSelection as it has now been processed
    	tapPoint = null;
    	
    	return hitMapLocation; 
    }
    
    private void drawMapLocations(Canvas canvas, MapView	mapView, boolean shadow) {
    	
		Iterator<MapLocation> iterator = mapLocationViewer.getMapLocations().iterator();
		Point screenCoords = new Point();
    	while(iterator.hasNext()) {	   
    		MapLocation location = iterator.next();
    		mapView.getProjection().toPixels(location.getPoint(), screenCoords);
			
	    	if (shadow) {
	    		//  Only offset the shadow in the y-axis as the shadow is angled so the base is at x=0; 
	    		canvas.drawBitmap(shadowIcon, screenCoords.x, screenCoords.y - shadowIcon.getHeight(),null);
	    	} else {
    			canvas.drawBitmap(bubbleIcon, screenCoords.x - bubbleIcon.getWidth()/2, screenCoords.y - bubbleIcon.getHeight(),null);
	    	}
    	}
    }
    
    private RectF getRectByMapLocation(MapLocation mloc) {
    	Iterator it = _hm.keySet().iterator();
		while (it.hasNext()) {
			MapLocation key = (MapLocation) it.next();
			if (key.getId() == mloc.getId()) {
				RectF value = (RectF) _hm.get(key);
				return value;
			}
			
		}
		return null;
    }
    
    private void drawInfoWindow(Canvas canvas, MapView	mapView, boolean shadow) {
    	Log.d("Overlay", "draw info size : " + _rects.size());
    	_listDrawMap.clear();
    	if ( selectedMapLocation != null) {
    		if ( shadow) {
    			//  Skip painting a shadow in this tutorial
    		} else {
    			
    			_currentRect = getRectByMapLocation(selectedMapLocation);
    			if (_currentRect != null) {
	    			for (RectF r : _rects) {
						if (r.intersect(_currentRect)) {
							Log.d("Overlay","intersects");
							MapLocation mloc = getMapLocationByRect(r);
							if (mloc != null) {
								_listDrawMap.add(mloc);
							}
						}
					}
    			}
    			if (_listDrawMap.size() == 0) {
    				_listDrawMap.add(selectedMapLocation);
    			}
    			if (_listDrawMap.size() == 1) {
    				Log.d("Overlay","DrawInfo " + _listDrawMap.size());
					//  First determine the screen coordinates of the selected MapLocation
					Point selDestinationOffset = new Point();
					mapView.getProjection().toPixels(selectedMapLocation.getPoint(), selDestinationOffset);
			    	
			    	//  Setup the info window with the right size & location
					int INFO_WINDOW_WIDTH = 280;
					int INFO_WINDOW_HEIGHT = 120;
					RectF infoWindowRect = new RectF(0,0,INFO_WINDOW_WIDTH,INFO_WINDOW_HEIGHT);				
					int infoWindowOffsetX = selDestinationOffset.x-INFO_WINDOW_WIDTH/2;
					int infoWindowOffsetY = selDestinationOffset.y-INFO_WINDOW_HEIGHT-bubbleIcon.getHeight();
					infoWindowRect.offset(0,0);
					canvas.translate(infoWindowOffsetX, infoWindowOffsetY);
					//  Draw inner info window
					canvas.drawRoundRect(infoWindowRect, 5, 5, getInnerPaint());
					
					//  Draw border for info window
					canvas.drawRoundRect(infoWindowRect, 5, 5, getBorderPaint());
						
					//  Draw the MapLocation's name
					int TEXT_OFFSET_X = 70;
					int TEXT_OFFSET_Y = 40;
					canvas.drawText(selectedMapLocation.getName(),TEXT_OFFSET_X,TEXT_OFFSET_Y,getTextPaint());
					Bitmap bitmap = ((BitmapDrawable) mImage).getBitmap();
					Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
					Rect des = new Rect(0, 10, 60, 60);
					canvas.drawBitmap(bitmap, src, des, getTextPaint());
    			} else if (_listDrawMap.size() > 1) {
    				Log.d("Overlay","DrawInfo xxxx : " + _listDrawMap.size());
    				Point selDestinationOffset = new Point();
					mapView.getProjection().toPixels(selectedMapLocation.getPoint(), selDestinationOffset);
			    	
			    	//  Setup the info window with the right size & location
					int INFO_WINDOW_WIDTH = 230;
					int INFO_WINDOW_HEIGHT = 90;
					RectF infoWindowRect = new RectF(0,0,INFO_WINDOW_WIDTH,INFO_WINDOW_HEIGHT);				
					int infoWindowOffsetX = selDestinationOffset.x-INFO_WINDOW_WIDTH/2;
					int infoWindowOffsetY = selDestinationOffset.y-INFO_WINDOW_HEIGHT-bubbleIcon.getHeight();
					infoWindowRect.offset(0,0);
					canvas.translate(infoWindowOffsetX, infoWindowOffsetY);
					//  Draw inner info window
					canvas.drawRoundRect(infoWindowRect, 5, 5, getInnerPaint());
					
					//  Draw border for info window
					canvas.drawRoundRect(infoWindowRect, 5, 5, getBorderPaint());
					
					int left = 0;
					int top = 10;
					int w = 70;
					for (MapLocation loc : _listDrawMap) {
						mImage = mapLocationViewer.getResources().getDrawable(loc.getImage());
						Bitmap bitmap = ((BitmapDrawable) mImage).getBitmap();
						Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
						Log.d("drawwww", "left : " + left);
						Rect des = new Rect(left, 10, 60, 60);
						canvas.drawBitmap(bitmap, left, top, getTextPaint());
						left += w;
					}
    			}
    		}
    	}
    }

	public Paint getInnerPaint() {
		if ( innerPaint == null) {
			innerPaint = new Paint();
			innerPaint.setARGB(150, 75, 75, 75); //gray
			innerPaint.setAntiAlias(true);
		}
		return innerPaint;
	}

	public Paint getBorderPaint() {
		if ( borderPaint == null) {
			borderPaint = new Paint();
			borderPaint.setARGB(255, 255, 255, 255);
			borderPaint.setAntiAlias(true);
			borderPaint.setStyle(Style.STROKE);
			borderPaint.setStrokeWidth(2);
		}
		return borderPaint;
	}

	public Paint getTextPaint() {
		if ( textPaint == null) {
			textPaint = new Paint();
			textPaint.setARGB(255, 255, 255, 255);
			textPaint.setAntiAlias(true);
		}
		return textPaint;
	}
}