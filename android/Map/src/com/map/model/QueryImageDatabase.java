package com.map.model;

import android.database.Cursor;
import android.util.Log;
import android.widget.Button;

public class QueryImageDatabase{

	static DbAdapter dbAdapter;

    public QueryImageDatabase() {

    }
	public void createNote(String id, String url) {
		
		dbAdapter.insertNote(id, "Thien is me"+url);
	}
	
	public  void setDbAdapter(DbAdapter db){
		dbAdapter = db;
	}
	public String getImageUrl(String id){
		String url = null;
		Cursor cur;
		cur = dbAdapter.getAllNotes();
        cur.moveToFirst();
        while (cur.isAfterLast() == false) {
            Log.d("CCCCCCCCC", cur.toString());
            url = cur.getString(1);
       	    cur.moveToNext();
        }
        cur.close();
        return url;
	}
}
