package com.map.view;

import com.google.android.maps.GeoPoint;

/** Class to hold our location information */
public class MapLocation {
	
	private String id;
	private GeoPoint	point;
	private String		name;
	private int    image;
	
	public MapLocation() {
		
	}
	
	public MapLocation(String name,double latitude, double longitude, int img) {
		this.name = name;
		point = new GeoPoint((int)(latitude*1e6),(int)(longitude*1e6));
		this.setImage(img);
	}

	public GeoPoint getPoint() {
		return point;
	}
	
	public void setPoint(int lat,int lon) {
		point = new GeoPoint(lat, lon);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
