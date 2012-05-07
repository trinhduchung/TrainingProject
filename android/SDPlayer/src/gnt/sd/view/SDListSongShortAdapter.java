package gnt.sd.view;

import gnt.sd.R;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SDListSongShortAdapter extends BaseAdapter{
	private Cursor _cursor;
	private Context _context;
	SDSongShortTableCell[] _listCache;

	public SDListSongShortAdapter(Context context, Cursor c) {
		this._cursor = c;
		_context = context;
		_listCache = new SDSongShortTableCell[c.getCount()];
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
		SDSongShortTableCell row;
		// if (_listCache[position] == null) {
		row = new SDSongShortTableCell(_context);
		_cursor.moveToPosition(position);
		row._textSong.setText(_cursor.getString(_cursor
				.getColumnIndex(MediaColumns.TITLE)));
		int duration = _cursor.getInt(_cursor
				.getColumnIndex(AudioColumns.DURATION));
		row._textDuration.setText(String.format("%02d:%02d", duration / 60000,
				duration % 60));
		_listCache[position] = row;
		// }
		return _listCache[position];
	}

	public class SDSongShortTableCell extends RelativeLayout {
		TextView _textSong;
		TextView _textDuration;
		public SDSongShortTableCell(Context context) {
			super(context);
			LayoutInflater.from(context).inflate(R.layout.listsong_short_tablecell,
					this);
			_textSong = (TextView) findViewById(R.id.listsong_short_songCell_title);
			_textDuration = (TextView) findViewById(R.id.listsong_short_songCell_duration);
		}
	}



}
