package gnt.sd;

import gnt.sd.view.SDPlaylistAdapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SDAddToPlaylistActivity extends Activity {

	RelativeLayout _btnAdd;
	ListView _listView;
	SDPlaylistAdapter _adapter;
	Cursor _cursor;
	ArrayList<String> _listSongs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addtoplaylist_view);
		_listSongs = getIntent().getStringArrayListExtra("listsong");
		setupUI();

	}

	public void setupUI() {
		_btnAdd = (RelativeLayout) findViewById(R.id.addtoplaylist_buttonSelectAll);
		_listView = (ListView) findViewById(R.id.addtoplaylist_listview);
		_cursor = getContentResolver().query(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, null,
				null, PlaylistsColumns.NAME + " ASC");
		_adapter = new SDPlaylistAdapter(this, _cursor, false);
		_listView.setAdapter(_adapter);
		_btnAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newintent = new Intent(SDAddToPlaylistActivity.this,
						SDNewPlayListActivity.class);
				startActivityForResult(newintent, 0);
			}
		});
		_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int possition, long arg3) {
				_cursor.moveToPosition(possition);
				for (int i = 0; i < _listSongs.size(); i++) {
					ContentResolver contentResolver = getApplication()
							.getContentResolver();
					String[] cols = new String[] { "count(*)" };
					Uri uri = MediaStore.Audio.Playlists.Members.getContentUri(
							"external",
							_cursor.getLong(_cursor
									.getColumnIndex(BaseColumns._ID)));
					Cursor cur = contentResolver.query(uri, cols, null, null,
							null);
					cur.moveToFirst();
					final int base = cur.getInt(0);
					cur.close();
					ContentValues values = new ContentValues();
					values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER,
							Integer.valueOf(base + _listSongs.get(i)));
					values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID,
							_listSongs.get(i));
					contentResolver.insert(uri, values);
					Toast.makeText(SDAddToPlaylistActivity.this, _listSongs.size() + " songs added.",Toast.LENGTH_SHORT);
					finish();
					
				}
			}
		});
	}

	public void deletePlaylist() {
		for (int i = 0; i < _adapter._listcheck.length; i++) {
			if (_adapter._listcheck[i] == true) {
				_cursor.moveToPosition(i);
				ContentResolver contentResolver = getContentResolver();
				contentResolver
						.delete(Playlists.EXTERNAL_CONTENT_URI,
								BaseColumns._ID
										+ "="
										+ _cursor.getLong(_cursor
												.getColumnIndex(BaseColumns._ID)),
								null);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && requestCode == 0) {
			_cursor = getContentResolver().query(
					MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null,
					null, null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
			_adapter = new SDPlaylistAdapter(this, _cursor, false);
			_listView.setAdapter(_adapter);
		}
	}
}
