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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class SDDeletePlaylistActivity extends Activity {

	RelativeLayout _btnCheckAll;
	CheckBox _cbAll;
	ListView _listView;
	Button _btnDelete;
	Button _btnCancel;
	SDPlaylistAdapter _adapter;
	Cursor _cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deleteplaylist_view);
		setupUI();

	}

	public void setupUI() {
		_btnCheckAll = (RelativeLayout) findViewById(R.id.deleteplaylist_buttonSelectAll);
		_cbAll = (CheckBox) findViewById(R.id.deleteplaylist_checkboxAll);
		_listView = (ListView) findViewById(R.id.deleteplaylist_listview);
		_btnDelete = (Button) findViewById(R.id.deleteplayist_buttonDelete);
		_btnCancel = (Button) findViewById(R.id.deleteplayist_buttonCancel);
		_cursor = getContentResolver().query(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, null,
				null, PlaylistsColumns.NAME + " ASC");
		_adapter = new SDPlaylistAdapter(this, _cursor, true);
		_listView.setAdapter(_adapter);
		_btnCheckAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				_cbAll.setChecked(!_cbAll.isChecked());
				checkAll();
			}
		});

		_cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				checkAll();
			}
		});
		_btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		_btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deletePlaylist();
				Intent intent = getIntent();
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	public void checkAll() {
		for (int i = 0; i < _adapter.getCount(); i++) {
			_adapter._listcheck[i] = _cbAll.isChecked();
		}
		_adapter.notifyDataSetChanged();
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
}
