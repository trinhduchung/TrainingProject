package com.map.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbAdapter {

	public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private static final String DATABASE_NAME = "NotesDb";
    private static final String DATABASE_TABLE = "NoteTbl";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;
    private static final String DATABASE_CREATE =
        "create table NoteTbl (_id integer primary key autoincrement, "
        + "title text not null, body text not null);";
    
    private static class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
    }
    
    public DbAdapter open() throws SQLException{
    	mDbHelper = new DatabaseHelper(mCtx);
    	mDb = mDbHelper.getWritableDatabase();
    	return this;
    }
    
    public void close(){
    	mDbHelper.close();
    }
    
    
    public DbAdapter(Context context){
    	mCtx = context;
    }
    
  
    
    /**
     * Return a Cursor over the list of all notes in the database
     * e.g NoteTable has three column: id=KEY_ROWID, title=KEY_TITLE and body=KEY_BODY
     * Below query is to get all note from this table
     * @return Cursor over all notes
     */
    public Cursor getAllNotes(){
    	
    	return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE, KEY_BODY}, 
    			null, null, null, null, null);
    	
    }
    
    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure
     */
    public long insertNote(String title, String body){
    	ContentValues insertedValue = new ContentValues();
    	insertedValue.put(KEY_TITLE, title);
    	insertedValue.put(KEY_BODY, body);
    	
    	return mDb.insert(DATABASE_TABLE, null, insertedValue);
    }
    
    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
}
