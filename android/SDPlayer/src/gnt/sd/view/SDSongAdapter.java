package gnt.sd.view;

import gnt.sd.R;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SDSongAdapter extends BaseAdapter implements SectionIndexer {
	private Cursor _cursor;
	private Context _context;
	SDSongTableCell[] _listCache;
	boolean _isShowCheckBox;
	public boolean[] _listcheck;
	AlphabetIndexer _alphabetIndexer;

	public SDSongAdapter(Context context, Cursor c, boolean isShowCheckBox) {
		this._cursor = c;
		_context = context;
		_listCache = new SDSongTableCell[c.getCount()];
		_isShowCheckBox = isShowCheckBox;
		if (_isShowCheckBox)
			_listcheck = new boolean[c.getCount()];
		_alphabetIndexer = new AlphabetIndexer(_cursor,
				_cursor.getColumnIndex(MediaColumns.TITLE),
				" ABCDEFGHIJKLMNOPQRSTUVWXYZ");
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
		SDSongTableCell row;
		// if (_listCache[position] == null) {
		row = new SDSongTableCell(_context, position);
		_cursor.moveToPosition(position);
		if (!_isShowCheckBox) {
			row._checkBox.setVisibility(View.GONE);
		} else {
			row._checkBox.setChecked(_listcheck[position]);
		}
		row._textSong.setText(_cursor.getString(_cursor
				.getColumnIndex(MediaColumns.TITLE)));
		row._textAlbum.setText(_cursor.getString(_cursor
				.getColumnIndex(AudioColumns.ALBUM)));
		row._textArtist.setText(_cursor.getString(_cursor
				.getColumnIndex(AudioColumns.ARTIST)));
		row._coverArt.getCoverArt(_cursor.getLong(_cursor
				.getColumnIndex(AudioColumns.ALBUM_ID)));
		int duration = _cursor.getInt(_cursor
				.getColumnIndex(AudioColumns.DURATION));
		row._textDuration.setText(String.format("%02d:%02d", duration / 60000,
				duration % 60));
		row._textAlbum.setText(_cursor.getString(_cursor
				.getColumnIndex(AudioColumns.ALBUM)));
		_listCache[position] = row;
		// }
		return _listCache[position];
	}

	public class SDSongTableCell extends RelativeLayout {
		TextView _textArtist;
		TextView _textSong;
		TextView _textAlbum;
		SDAlbumCoverArt _coverArt;
		TextView _textDuration;
		public CheckBox _checkBox;
		int _possition;

		public SDSongTableCell(Context context, int possition) {
			super(context);
			LayoutInflater.from(context).inflate(R.layout.listsong_tablecell,
					this);
			_possition = possition;
			_coverArt = (SDAlbumCoverArt) findViewById(R.id.songCell_coverart);
			_textArtist = (TextView) findViewById(R.id.songCell_artist);
			_textSong = (TextView) findViewById(R.id.songCell_title);
			_textAlbum = (TextView) findViewById(R.id.songCell_album);
			_textDuration = (TextView) findViewById(R.id.songName_duration);
			_checkBox = (CheckBox) findViewById(R.id.songCell_checkbox);
			_checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					_listcheck[_possition] = isChecked;
				}
			});
		}
	}

	@Override
	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		return _alphabetIndexer.getPositionForSection(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return _alphabetIndexer.getSectionForPosition(position);
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return _alphabetIndexer.getSections();
	}

	// package gnt.sd.view;
	//
	// import gnt.sd.R;
	// import android.content.Context;
	// import android.database.Cursor;
	// import android.provider.MediaStore;
	// import android.provider.MediaStore.Audio.Media;
	// import android.view.LayoutInflater;
	// import android.view.View;
	// import android.view.ViewGroup;
	// import android.widget.AlphabetIndexer;
	// import android.widget.CheckBox;
	// import android.widget.CompoundButton;
	// import android.widget.CompoundButton.OnCheckedChangeListener;
	// import android.widget.RelativeLayout;
	// import android.widget.SectionIndexer;
	// import android.widget.SimpleCursorAdapter;
	// import android.widget.TextView;
	//
	// public class SDSongAdapter extends SimpleCursorAdapter implements
	// SectionIndexer {
	// private Cursor _cursor;
	// private Context _context;
	// SDSongTableCell[] _listCache;
	// boolean _isShowCheckBox;
	// public boolean[] _listcheck;
	// AlphabetIndexer _alphabetIndexer;
	//
	// public SDSongAdapter(Context context, int layout, Cursor c, String[]
	// from,
	// int[] to) {
	// super(context, layout, c, from, to);
	// // TODO Auto-generated constructor stub
	// }
	//
	// public SDSongAdapter(Context context, Cursor c, boolean isShowCheckBox) {
	// this(context, R.layout.listsong_tablecell, c, new String[] {},
	// new int[] {});
	// this._cursor = c;
	// _context = context;
	// _listCache = new SDSongTableCell[c.getCount()];
	// _isShowCheckBox = isShowCheckBox;
	// if (_isShowCheckBox)
	// _listcheck = new boolean[c.getCount()];
	// _alphabetIndexer = new AlphabetIndexer(c,
	// c.getColumnIndex(Media.TITLE), " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	// }
	//
	// @Override
	// public int getCount() {
	// // TODO Auto-generated method stub
	// return _cursor.getCount();
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// // TODO Auto-generated method stub
	// return position;
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// // TODO Auto-generated method stub
	// return position;
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// // TODO Auto-generated method stub
	// SDSongTableCell row;
	// // if (_listCache[position] == null) {
	// row = new SDSongTableCell(_context, position);
	// _cursor.moveToPosition(position);
	// if (!_isShowCheckBox) {
	// row._checkBox.setVisibility(View.GONE);
	// } else {
	// row._checkBox.setChecked(_listcheck[position]);
	// }
	// row._textSong.setText(_cursor.getString(_cursor
	// .getColumnIndex(MediaStore.Audio.Media.TITLE)));
	// row._textAlbum.setText(_cursor.getString(_cursor
	// .getColumnIndex(MediaStore.Audio.Media.ALBUM)));
	// row._textArtist.setText(_cursor.getString(_cursor
	// .getColumnIndex(MediaStore.Audio.Media.ARTIST)));
	// row._coverArt.getCoverArt(_cursor.getLong(_cursor
	// .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
	// int duration = _cursor.getInt(_cursor
	// .getColumnIndex(MediaStore.Audio.Media.DURATION));
	// row._textDuration.setText(String.format("%02d:%02d", duration / 60000,
	// duration % 60));
	// row._textAlbum.setText(_cursor.getString(_cursor
	// .getColumnIndex(MediaStore.Audio.Media.ALBUM)));
	// _listCache[position] = row;
	// // }
	// return _listCache[position];
	// }
	//
	// public void bindView(View view, Context context, Cursor cursor) {
	//
	// }
	//
	// public class SDSongTableCell extends RelativeLayout {
	// TextView _textArtist;
	// TextView _textSong;
	// TextView _textAlbum;
	// SDAlbumCoverArt _coverArt;
	// TextView _textDuration;
	// public CheckBox _checkBox;
	// int _possition;
	//
	// public SDSongTableCell(Context context, int possition) {
	// super(context);
	// LayoutInflater.from(context).inflate(R.layout.listsong_tablecell,
	// this);
	// _possition = possition;
	// _coverArt = (SDAlbumCoverArt) findViewById(R.id.songCell_coverart);
	// _textArtist = (TextView) findViewById(R.id.songCell_artist);
	// _textSong = (TextView) findViewById(R.id.songCell_title);
	// _textAlbum = (TextView) findViewById(R.id.songCell_album);
	// _textDuration = (TextView) findViewById(R.id.songName_duration);
	// _checkBox = (CheckBox) findViewById(R.id.songCell_checkbox);
	// _checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	// @Override
	// public void onCheckedChanged(CompoundButton buttonView,
	// boolean isChecked) {
	// _listcheck[_possition] = isChecked;
	// }
	// });
	// }
	// }
	//
	// public Object[] getSections() {
	// return _alphabetIndexer.getSections();
	// }
	//
	// public int getPositionForSection(int section) {
	// return _alphabetIndexer.getPositionForSection(section);
	// }
	//
	// public int getSectionForPosition(int position) {
	// return _alphabetIndexer.getSectionForPosition(position);
	// }
	//
	// }

}
