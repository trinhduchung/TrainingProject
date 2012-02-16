package com.map;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import com.google.android.maps.MapActivity;
import com.map.model.DbAdapter;
import com.map.model.QueryImageDatabase;
import com.map.services.ImageService;

public class HomeMapActivity extends MapActivity {

	private DbAdapter mDbHelper;
	public QueryImageDatabase queryImageDatabase;
    @Override
	public void onCreate(Bundle icicle) {
    	
        super.onCreate(icicle);

        setContentView(R.layout.main);
        accessSDCard();
    }

    /**
     * Must let Google know that a route will not be displayed
     */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	} 
	protected void accessSDCard(){
        String image_URL = "http://www.fortux.com/images/icon_objectives.gif";
        ImageService imageService = new ImageService();
        imageService.SaveImageFromUrlToSDCard(image_URL,"img1");
        imageService.getImageFromSDCard("img1");
        sqliteAccess("1", image_URL);
	}
	protected void sqliteAccess(String id, String body){
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
		queryImageDatabase = new QueryImageDatabase();
        queryImageDatabase.setDbAdapter(mDbHelper);
        queryImageDatabase.createNote(id,body);
        queryImageDatabase.getImageUrl("1");
        fillData();
	}
	private void fillData() {
		// TODO Auto-generated method stub
		 // Get all of the notes from the database and create the item list
        Cursor c = mDbHelper.getAllNotes();
        startManagingCursor(c);

        String[] from = new String[] { DbAdapter.KEY_TITLE };
        int[] to = new int[] { R.id.text1 };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
            new SimpleCursorAdapter(this, R.layout.notes_row, c, from, to);
        
      //  setListAdapter(notes);

	}
}
