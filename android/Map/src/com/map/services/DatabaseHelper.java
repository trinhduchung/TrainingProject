package com.map.services;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.map.view.MapLocation;

public class DatabaseHelper extends SQLiteOpenHelper {

	private Context _context;
	public static final String DBName = "mobion_music.db";
	public static final int DBVersion = 4;
	
	/* MapLocation */
	public static final String LOCATION_TABLE = "location";
	
	public final static String KEY_ID = "id";
	public static final String NAME = "name";
	public static final String LAT = "lat";
	public static final String LON = "lon";
	public static final String IMAGE = "img";
	
	public DatabaseHelper(Context context) {
		super(context, DBName, null, DBVersion);
		_context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		/* Create location table */
		String query = "CREATE TABLE " + LOCATION_TABLE + " (" + KEY_ID
				+ " integer PRIMARY KEY autoincrement ," + "" + NAME
				+ " text NOT NULL ," + LAT + " integer NOT NULL ,"
				+ LON + " integer NOT NULL ," + "" + IMAGE + " integer NOT NULL);";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (newVersion > oldVersion) {
			db.execSQL("drop table if exists " + LOCATION_TABLE);
			this.onCreate(db);
		}
	}
	
	public void insertLocation(MapLocation loc) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(NAME, loc.getName());
		contentValues.put(LAT, loc.getPoint().getLatitudeE6());
		contentValues.put(LON, loc.getPoint().getLongitudeE6());
		contentValues.put(IMAGE, loc.getImage());
		db.insert(LOCATION_TABLE, null, contentValues);
		db.close();
	}
	
	public Cursor getMapLocationCursor() {
		return this.getReadableDatabase().query(
				LOCATION_TABLE,
				new String[] { KEY_ID, NAME, LAT, LON,
						IMAGE }, null, null, null,
				null, NAME + " DESC");
	}
	
	public List<MapLocation> getListMapLocation() {
		List<MapLocation> list = new ArrayList<MapLocation>();
		Cursor cursor = getMapLocationCursor();
		if (cursor !=null) {
			while (cursor.moveToNext()) {
				MapLocation location = new MapLocation();
				location.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
				location.setName(cursor.getString(cursor.getColumnIndex(NAME)));
				location.setPoint(cursor.getInt(cursor.getColumnIndex(LAT)), cursor.getInt(cursor.getColumnIndex(LON)));
				location.setImage(cursor.getInt(cursor.getColumnIndex(IMAGE)));
				list.add(location);
			}
		}
		cursor.close();
		return list;
	}
}
