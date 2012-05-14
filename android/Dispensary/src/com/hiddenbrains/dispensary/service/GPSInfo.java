package com.hiddenbrains.dispensary.service;

public class GPSInfo {
	private double _lat;
	private double _lng;
	private String _info;
	public void setLat(double lat) {
		this._lat = lat;
	}
	public double getLat() {
		return _lat;
	}
	public void setLng(double lng) {
		this._lng = lng;
	}
	public double getLng() {
		return _lng;
	}
	public void setInfo(String info) {
		this._info = info;
	}
	public String getInfo() {
		return _info;
	}
	
	@Override
	public String toString() {
		return getLat() + "~" + getLng() + "--" + getInfo();
	}
}
