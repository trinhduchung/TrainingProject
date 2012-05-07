package gnt.sd;

import gnt.sd.view.SDSongAdapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class SDAddSongActivity extends Activity {

	RelativeLayout _btnCheckAll;
	CheckBox _cbAll;
	ListView _listView;
	Button _btnAdd;
	Button _btnCancel;
	SDSongAdapter _adapter;
	Cursor _cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addsong_view);
		setupUI();

	}

	public void setupUI() {
		_btnCheckAll = (RelativeLayout) findViewById(R.id.addsong_buttonSelectAll);
		_cbAll = (CheckBox) findViewById(R.id.addsong_checkboxAll);
		_listView = (ListView) findViewById(R.id.addsong_listview);
		_btnAdd = (Button) findViewById(R.id.addsong_buttonAdd);
		_btnCancel = (Button) findViewById(R.id.addsong_buttonCancel);
		_cursor = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaColumns.TITLE + " ASC");
		_adapter = new SDSongAdapter(this, _cursor, true);
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
		_btnAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addToPlaylist();
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

	public void addToPlaylist() {
		ArrayList<String> listSongs = new ArrayList<String>();
		for (int i = 0; i < _adapter._listcheck.length; i++) {
			if (_adapter._listcheck[i] == true) {
				_cursor.moveToPosition(i);
				listSongs.add(String.valueOf(_cursor.getLong(_cursor
						.getColumnIndex(BaseColumns._ID))));
			}
		}
		Intent intent = new Intent(this, SDAddToPlaylistActivity.class);
		intent.putStringArrayListExtra("listsong", listSongs);
		startActivity(intent);
	}
}
