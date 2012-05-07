package gnt.sd;

import gnt.sd.view.SDPlaylistAdapter;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SDPlaylistActivity extends SDBaseLibaryActivity implements
		OnItemClickListener {
	private Cursor _cursor;
	private SDPlaylistAdapter _adapter;

	public static final int MENU_CREATE = 0;
	public static final int MENU_DELETE = MENU_CREATE + 1;
	public static final int MENU_SEARCH = MENU_DELETE + 1;
	public static final int MENU_SETTING = MENU_SEARCH + 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		_headerView.setVisibility(View.GONE);
		_cursor = getContentResolver().query(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, null,
				null, PlaylistsColumns.NAME + " ASC");
		_adapter = new SDPlaylistAdapter(this, _cursor, false);
		_listView.setAdapter(_adapter);
		_listView.setOnItemClickListener(this);
		registerForContextMenu(_listView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_CREATE, 0, "Create")
				.setIcon(R.drawable.ic_menu_create);
		menu.add(0, MENU_DELETE, 0, "Delete")
				.setIcon(R.drawable.ic_menu_delete);
		menu.add(0, MENU_SEARCH, 0, "Search")
				.setIcon(R.drawable.ic_menu_search);
		menu.add(0, MENU_SETTING, 0, "Setting").setIcon(
				R.drawable.ic_menu_settings);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_CREATE:
			Intent newintent = new Intent(this, SDNewPlayListActivity.class);
			startActivityForResult(newintent, 0);
			return true;
		case MENU_SEARCH:
			onSearchRequested();
			return true;
		case MENU_DELETE:
			Intent deleteIntent = new Intent(this,
					SDDeletePlaylistActivity.class);
			startActivityForResult(deleteIntent, 0);
			return true;
		}
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Options");
		menu.add(0, 0, 0, "Delete");
		menu.add(0, 1, 0, "Edit");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == 0) {
			AdapterView.AdapterContextMenuInfo info;
			try {
				info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
				long position = _adapter.getItemId(info.position);
				_cursor.moveToPosition((int) position);
				ContentResolver contentResolver = getApplication()
						.getContentResolver();
				contentResolver
						.delete(Playlists.EXTERNAL_CONTENT_URI,
								BaseColumns._ID
										+ "="
										+ _cursor.getLong(_cursor
												.getColumnIndex(BaseColumns._ID)),
								null);
				_cursor.requery();
				_adapter = new SDPlaylistAdapter(this, _cursor, false);
				_listView.setAdapter(_adapter);
			} catch (ClassCastException e) {
				return false;
			}

		} else {
			// edit
			AdapterView.AdapterContextMenuInfo info;
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			long position = _adapter.getItemId(info.position);
			_cursor.moveToPosition((int) position);
			Intent newintent = new Intent(this, SDNewPlayListActivity.class);
			newintent.putExtra("mode", 1);
			newintent.putExtra("playlist_id", _cursor.getLong(_cursor
					.getColumnIndex(BaseColumns._ID)));
			startActivityForResult(newintent, 0);
			return true;
		}
		return super.onContextItemSelected(item);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		_cursor.moveToPosition(position);
		Intent intent = new Intent(this, SDPlaylistDetailActivity.class);
		intent.putExtra("playlist_id", _cursor.getLong(_cursor
				.getColumnIndex(MediaStore.Audio.Playlists._ID)));
		startActivity(intent);
	}

}
