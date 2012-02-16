package com.map.model;

import com.map.R;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

public class ListNotesExample extends ListActivity {
	
	public static final int INSERT_ID = Menu.FIRST;
	public static final int INSERT_REQUEST = 1;
	
	private int mNoteNumber = 1;
	private DbAdapter mDbHelper;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
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
        Log.d("AAAAAAAMMMMMMMMM=", ""+c+"==="+from+"===="+to);
        setListAdapter(notes);
	}
	
	private void createNote() {
		
		Intent iIntent = new Intent(ListNotesExample.this, AddNote.class);
		AddNote.setDbAdapter(mDbHelper);
		startActivityForResult(iIntent, INSERT_REQUEST);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createNote();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0,"Insert note");
        return result;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case INSERT_REQUEST:
			if(resultCode == RESULT_OK){
				fillData();
			}
			break;

		default:
			break;
		}
	}
}