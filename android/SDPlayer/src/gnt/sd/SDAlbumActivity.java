package gnt.sd;

import gnt.sd.view.SDAlbumAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SDAlbumActivity extends SDBaseLibaryActivity implements OnItemClickListener {

	private Cursor _cursor;
	private SDAlbumAdapter _adapter;

	public static final int MENU_SEARCH = 0;
	public static final int MENU_BUY_LINK = MENU_SEARCH + 1;
	public static final int MENU_SETTING = MENU_BUY_LINK + 1;
	public static final int MENU_LAND = MENU_SETTING + 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_headerView.setVisibility(View.GONE);
		_listView.setFastScrollEnabled(true);
		_listView.setOnItemClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		_cursor = getContentResolver().query(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null,
				AlbumColumns.ALBUM + " ASC");
		_adapter = new SDAlbumAdapter(this, _cursor);
		_listView.setAdapter(_adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SEARCH, 0, "Search")
				.setIcon(R.drawable.ic_menu_search);
		menu.add(0, MENU_BUY_LINK, 0, "Buy Link").setIcon(
				R.drawable.ic_menu_details);
		menu.add(0, MENU_SETTING, 0, "Setting").setIcon(
				R.drawable.ic_menu_settings);
		menu.add(0, MENU_LAND, 0, "Lanscape").setIcon(
				R.drawable.ic_menu_settings);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SEARCH:
			// Search implement
			return true;
		case MENU_BUY_LINK:
			// Album Info included : b
			return true;
		case MENU_SETTING:
			// Go to Setting
			return true;
		case MENU_LAND:
			// Go to Setting
			Intent intent = new Intent(this, SDAlbumLandActivity.class);
			startActivity(intent);
			return true;

		}

		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		_cursor.moveToPosition(position);
		Intent intent = new Intent(this, SDAlbumDetailActivity.class);
		intent.putExtra("album_id", _cursor.getLong(_cursor
				.getColumnIndex(BaseColumns._ID)));
		startActivity(intent);
	}
}
