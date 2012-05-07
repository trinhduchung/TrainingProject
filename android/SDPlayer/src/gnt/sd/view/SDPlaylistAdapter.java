package gnt.sd.view;

import gnt.sd.R;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SDPlaylistAdapter extends BaseAdapter {
	private Cursor _cursor;
	private Context _context;
	SDPlayListTableCell[] _listCache;
	boolean _isShowCheckBox;
	public boolean[] _listcheck;

	public SDPlaylistAdapter(Context context, Cursor c, boolean isShowCheckBox) {
		this._cursor = c;
		_context = context;
		_listCache = new SDPlayListTableCell[c.getCount()];
		_isShowCheckBox = isShowCheckBox;
		if (_isShowCheckBox)
			_listcheck = new boolean[c.getCount()];
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return _cursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		SDPlayListTableCell row;
		// if (_listCache[position] == null) {
		row = new SDPlayListTableCell(_context, position);
		_cursor.moveToPosition(position);
		if (!_isShowCheckBox) {
			row._checkBox.setVisibility(View.GONE);
		} else {
			row._checkBox.setChecked(_listcheck[position]);
		}
		row._textPlaylist.setText(_cursor.getString(_cursor
				.getColumnIndex(PlaylistsColumns.NAME)));
		// row._textNumOfSongs.setText(_cursor.getString(_cursor
		// .getColumnIndex(MediaStore.Audio.Playlists._COUNT)));
		row._coverArt.getCoverArt(_cursor.getLong(_cursor
				.getColumnIndex(BaseColumns._ID)));
		_listCache[position] = row;
		// }
		return _listCache[position];
	}

	public class SDPlayListTableCell extends RelativeLayout {
		public TextView _textPlaylist;
		public SDPlaylistCoverArt _coverArt;
		public CheckBox _checkBox;
		int _possition;

		// TextView _textNumOfSongs;
		public SDPlayListTableCell(Context context, int possition) {
			super(context);
			_possition = possition;
			LayoutInflater.from(context).inflate(
					R.layout.listplaylist_tablecell, this);
			_coverArt = (SDPlaylistCoverArt) findViewById(R.id.playlistCell_coverart);
			_textPlaylist = (TextView) findViewById(R.id.playlistCell_playlist);
			_checkBox = (CheckBox) findViewById(R.id.playlistCell_checkbox);
			_checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					_listcheck[_possition] = isChecked;
				}
			});
			// _textNumOfSongs = (TextView)
			// findViewById(R.id.playlistCell_numOfSong);
		}
	}
}
