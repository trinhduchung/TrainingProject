package com.map;

import java.util.List;

import android.app.Application;

import com.map.services.DatabaseHelper;
import com.map.view.MapLocation;

public class MapApplication extends Application {
	public List<MapLocation> mapLocations;

	DatabaseHelper db;
	static MapApplication instance;
	
	
	
	public MapApplication() {
		super();
		instance = this;
	}



	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		db = new DatabaseHelper(this);
		mapLocations = db.getListMapLocation();
	}
	
	public static MapApplication Instance() {
		return instance;
	}
}
