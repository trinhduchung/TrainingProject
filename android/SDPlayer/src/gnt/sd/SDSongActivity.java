package gnt.sd;

import gnt.sd.controller.Service;
import gnt.sd.controller.ServiceListener;
import gnt.sd.controller.ServiceRespone;
import gnt.sd.model.SDAudio;
import gnt.sd.view.SDSongAdapter;

import java.util.ArrayList;
import java.util.List;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SDSongActivity extends SDBaseLibaryActivity implements ServiceListener, OnItemClickListener {
	private Cursor _cursor;
	private SDSongAdapter _adapter;
	AlertDialog _alertDialog;
	ProgressDialog _progressDialog;
	Service _serviceGetCorrect;
	List<SDAudio> _listSong;
	public static final String[] ProjectionAudio = { Media._ID, Media.ALBUM_ID,
		Media.ALBUM, Media.ARTIST_ID, Media.ARTIST, Media.COMPOSER,
		Media.DATA, Media.DURATION, Media.DATE_ADDED, Media.DATE_MODIFIED,
		Media.DISPLAY_NAME, Media.SIZE, Media.MIME_TYPE, Media.TITLE,
		Media.TRACK, Media.YEAR};
	public static final int MENU_ADD_TO_PLAYLIST = 0;
	public static final int MENU_SEARCH = MENU_ADD_TO_PLAYLIST + 1;
	public static final int MENU_SEARCH_YOUTUBE = MENU_SEARCH + 1;
	public static final int MENU_SETTING = MENU_SEARCH_YOUTUBE + 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		_headerView.setVisibility(View.GONE);
		_listView.setFastScrollEnabled(true);
		_listView.setOnItemClickListener(this);
		registerForContextMenu(_listView);
		_listSong = new ArrayList<SDAudio>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ADD_TO_PLAYLIST, 0, "Add to playlist").setIcon(
				R.drawable.ic_menu_add_to_playlist);
		menu.add(0, MENU_SEARCH, 0, "Search")
				.setIcon(R.drawable.ic_menu_search);
		menu.add(0, MENU_SEARCH_YOUTUBE, 0, "Youtube").setIcon(
				R.drawable.ic_menu_youtube);
		menu.add(0, MENU_SETTING, 0, "Setting").setIcon(
				R.drawable.ic_menu_settings);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		_cursor = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				Media.TITLE + " ASC");
		_listSong = getAllAudio();
		_adapter = new SDSongAdapter(this, _cursor, false);
		_listView.setAdapter(_adapter);
	}
	
	public Cursor getAudioCursor() {
		ContentResolver contentResolver = getApplication().getContentResolver();
		Cursor c = contentResolver.query(Media.EXTERNAL_CONTENT_URI,
				ProjectionAudio, null, null, Media.TITLE + " ASC");
		return c;
	}
	
	public List<SDAudio> getAllAudio() {
		ArrayList<SDAudio> songs = new ArrayList<SDAudio>();
		SDAudio audio = null;
		Cursor c = getAudioCursor();
		if (c != null) {
			try {
				while (c.moveToNext()) {
					audio = cursor2Audio(c);
					songs.add(audio);
				}
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return songs;
	}
	
	private SDAudio cursor2Audio(Cursor c) {
		SDAudio audio = new SDAudio();
		audio.setId(c.getLong(c.getColumnIndex(Media._ID)));
		audio.setAlbumId(c.getLong(c.getColumnIndex(Media.ALBUM_ID)));
		audio.setAlbum(c.getString(c.getColumnIndex(Media.ALBUM)));
		audio.setArtist(c.getString(c.getColumnIndex(Media.ARTIST)));
		audio.setArtistId(c.getLong(c.getColumnIndex(Media.ARTIST_ID)));
		audio.setComposer(c.getString(c.getColumnIndex(Media.COMPOSER)));
		audio.setDuration(c.getLong(c.getColumnIndex(Media.DURATION)));
		audio.setPath(c.getString(c.getColumnIndex(Media.DATA)));
		audio.setDateAdded(c.getLong(c.getColumnIndex(Media.DATE_ADDED)));
		audio.setDateModified(c.getLong(c.getColumnIndex(Media.DATE_MODIFIED)));
		audio.setDisplayName(c.getString(c.getColumnIndex(Media.DISPLAY_NAME)));
		audio.setSize(c.getLong(c.getColumnIndex(Media.SIZE)));
		audio.setMimeType(c.getString(c.getColumnIndex(Media.MIME_TYPE)));
		audio.setTitle(c.getString(c.getColumnIndex(Media.TITLE)));
		audio.setTrack(c.getInt(c.getColumnIndex(Media.TRACK)));
		audio.setYear(c.getInt(c.getColumnIndex(Media.YEAR)));
		return audio;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ADD_TO_PLAYLIST:
			Intent intent = new Intent(this, SDAddSongActivity.class);
			startActivity(intent);
			return true;
		case MENU_SEARCH:
			// Album Info included : b
			return true;
		case MENU_SEARCH_YOUTUBE:
			// Search on youtube
			return true;
		case MENU_SETTING:
			// Go to Setting
			return true;
		}
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Options");
		menu.add(0, 0, 0, "Add to playlist");
		menu.add(0, 1, 0, "Delete");
		menu.add(0, 2, 0, "Get Correct Infomation");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == 0) {
			ArrayList<String> listSongs = new ArrayList<String>();
			AdapterView.AdapterContextMenuInfo info;
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			long position = _adapter.getItemId(info.position);
			_cursor.moveToPosition((int) position);
			listSongs.add(String.valueOf(_cursor.getLong(_cursor
					.getColumnIndex(BaseColumns._ID))));
			Intent intent = new Intent(this, SDAddToPlaylistActivity.class);
			intent.putStringArrayListExtra("listsong", listSongs);
			startActivity(intent);
		} else if (item.getItemId() == 2) {
			// edit
			AdapterView.AdapterContextMenuInfo info;
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			long position = _adapter.getItemId(info.position);
			_cursor.moveToPosition((int) position);

			if (_serviceGetCorrect == null) {
				_serviceGetCorrect = new Service(this);
			}
			_progressDialog = ProgressDialog.show(this, "In progress",
					"Searching...");
			_serviceGetCorrect.getCorrecttrackInfo(
					_cursor.getString(_cursor.getColumnIndex(MediaColumns.TITLE)),
					_cursor.getString(_cursor.getColumnIndex(AudioColumns.ARTIST)));
			return true;
		} else if (item.getItemId() == 1) {
			// edit
			AdapterView.AdapterContextMenuInfo info;
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			long position = _adapter.getItemId(info.position);
			_cursor.moveToPosition((int) position);
			ContentResolver contentResolver = getContentResolver();
			contentResolver
					.delete(Media.EXTERNAL_CONTENT_URI,
							BaseColumns._ID
									+ "="
									+ _cursor.getLong(_cursor
											.getColumnIndex(BaseColumns._ID)),
							null);
			_cursor.requery();
			_adapter = new SDSongAdapter(this, _cursor, false);
			_listView.setAdapter(_adapter);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onComplete(Service service, ServiceRespone result) {
		_progressDialog.dismiss();
		if (result.isSuccess()) {
			final SDAudio song = (SDAudio) result.getData();
			if (song != null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setCancelable(true);
				_alertDialog = builder.create();
				_alertDialog.setTitle("Successful");
				_alertDialog.setMessage("Correct Infomation \nArtist: "
						+ song.getArtist() + "\nTrack: " + song.getTitle()
						+ "\n Do you want to edit your song info?");
				_alertDialog.setButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								_alertDialog.dismiss();
							}
						});
				_alertDialog.setButton2("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ContentResolver contentResolver = getApplication()
										.getContentResolver();
								ContentValues cv = new ContentValues();
								cv.put(MediaColumns.TITLE, song.getTitle());
								cv.put(AudioColumns.ARTIST, song.getArtist());
								String selection = BaseColumns._ID + "=?";
								String[] selectionArgs = new String[] { String
										.valueOf(_cursor.getLong(_cursor
												.getColumnIndex(BaseColumns._ID))) };
								contentResolver.update(
										Media.EXTERNAL_CONTENT_URI, cv,
										selection, selectionArgs);
								_cursor.requery();
								_adapter = new SDSongAdapter(
										SDSongActivity.this, _cursor, false);
								_listView.setAdapter(_adapter);
							}
						});
				_alertDialog.show();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setCancelable(true);
				_alertDialog = builder.create();
				_alertDialog.setTitle("Fail");
				_alertDialog.setMessage("Not Found");
				_alertDialog.setButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								_alertDialog.dismiss();
							}
						});
				_alertDialog.show();
			}
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(true);
			_alertDialog = builder.create();
			_alertDialog.setTitle("Fail");
			_alertDialog.setMessage("Not Found");
			_alertDialog.setButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							_alertDialog.dismiss();
						}
					});
			_alertDialog.show();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//		SDLibrary library = new SDLibrary(this);
//		SDNowPlaylist nowPlaylist = library.getPlaylistForAllAudio();
//		nowPlaylist.setCurrentIndex(position);
//		library.setNowPlaylist(nowPlaylist);
		Log.d("SongActivity", "position click = " + position);
		SDNowPlaylist nowPlaylist = new SDNowPlaylist();
		nowPlaylist.setSongs(_listSong);
		nowPlaylist.setCurrentIndex(position);
		SDLibrary library = SDApplication._instance.getLibrary();
		library.setNowPlaylist(nowPlaylist);
		SDApplication._instance.putData(SDPlayerActivity.CODE_RELOAD, true);
		Intent intent = new Intent(this, SDPlayerActivity.class);
		startActivity(intent);
	}
}
