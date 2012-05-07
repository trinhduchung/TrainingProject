package gnt.sd;

import java.util.List;

import gnt.sd.controller.Service;
import gnt.sd.controller.ServiceAction;
import gnt.sd.controller.ServiceListener;
import gnt.sd.controller.ServiceRespone;
import gnt.sd.model.SDAudio;
import gnt.sd.view.SDArtistAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.provider.MediaStore.Audio.Artists;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SDArtistActivity extends SDBaseLibaryActivity implements
		OnItemClickListener, ServiceListener {
	private Cursor _cursor;
	private SDArtistAdapter _adapter;
	public static final int MENU_SEARCH = 0;
	public static final int MENU_SEARCH_YOUTUBE = MENU_SEARCH + 1;
	public static final int MENU_MORE_INFO = MENU_SEARCH_YOUTUBE + 1;
	public static final int MENU_SETTING = MENU_MORE_INFO + 1;
	Service _serviceSimilar;
	Service _serviceSearchZing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		_headerView.setVisibility(View.GONE);
		
		_listView.setFastScrollEnabled(true);
		
		_listView.setOnItemClickListener(this);
		registerForContextMenu(_listView);
	}

	@Override
	protected void onStart() {
		super.onStart();
		_cursor = getContentResolver().query(
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null, null,
				null, ArtistColumns.ARTIST + " ASC");
		_adapter = new SDArtistAdapter(this, _cursor);
		_listView.setAdapter(_adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SEARCH, 0, "Search")
				.setIcon(R.drawable.ic_menu_search);
		menu.add(0, MENU_SEARCH_YOUTUBE, 0, "Youtube").setIcon(
				R.drawable.ic_menu_youtube);
		menu.add(0, MENU_MORE_INFO, 0, "More Info").setIcon(
				R.drawable.ic_menu_details);
		menu.add(0, MENU_SETTING, 0, "Setting").setIcon(
				R.drawable.ic_menu_settings);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SEARCH:
			// Search implement
			return true;
		case MENU_SEARCH_YOUTUBE:
			// Search youtube implement
			return true;
		case MENU_MORE_INFO:
			// Artist Info included : similar artist,top album, top track.
			return true;
		case MENU_SETTING:
			// Go to Setting
			return true;
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		_cursor.moveToPosition(position);
		Intent intent = new Intent(this, SDArtistDetailActivity.class);
		intent.putExtra("artist_id",
				_cursor.getLong(_cursor.getColumnIndex(BaseColumns._ID)));
		startActivity(intent);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Options");
		menu.add(0, 0, 0, "Search from Zing");
		menu.add(0, 1, 0, "Similar Artist");
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	private ProgressDialog _waittingForSearch;
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == 0) {
			AdapterView.AdapterContextMenuInfo info;
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			long position = _adapter.getItemId(info.position);
			_cursor.moveToPosition((int) position);
			if (_serviceSearchZing == null)
				_serviceSearchZing = new Service(this);
			_serviceSearchZing.searchFromZing(
					_cursor.getString(_cursor.getColumnIndex(Artists.ARTIST)),
					1);
			_waittingForSearch = ProgressDialog.show(SDArtistActivity.this, "", "Waitting For Search...",true,true);
			_waittingForSearch.show();
			return true;
		} else if (item.getItemId() == 1) {
			// edit
			AdapterView.AdapterContextMenuInfo info;
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			long position = _adapter.getItemId(info.position);
			_cursor.moveToPosition((int) position);
			if (_serviceSimilar == null) {
				_serviceSimilar = new Service(this);
			}
			_serviceSimilar.getSimilar(_cursor.getString(_cursor
					.getColumnIndex(Artists.ARTIST)));
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onComplete(Service service, ServiceRespone result) {
		if(result.getAction() ==ServiceAction.ActionSearchFromZing) {
			_waittingForSearch.dismiss();
			if(result.isSuccess()){
				List<SDAudio> listSong = (List<SDAudio>) result.getData();
				//Switch to list activity
				SDApplication.Instance().putData("list", listSong);
				SDApplication.Instance().putData("artist", _cursor.getColumnIndex(Artists.ARTIST));
				startActivity(new Intent(this, SDListPlayStreamingActivity.class));
			}
		}

	}
}
