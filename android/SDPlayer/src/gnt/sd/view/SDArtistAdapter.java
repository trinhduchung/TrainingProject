package gnt.sd.view;

import gnt.sd.R;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SDArtistAdapter extends BaseAdapter implements SectionIndexer{

	private Cursor _cursor;
	SDArtistTableCell[] _listCache;
	Context _context;
	AlphabetIndexer _alphabetIndexer;
	public SDArtistAdapter(Context context, Cursor c) {
		this._cursor = c;
		_context = context;
		_listCache = new SDArtistTableCell[c.getCount()];
		_alphabetIndexer = new AlphabetIndexer(_cursor,
				_cursor.getColumnIndex(ArtistColumns.ARTIST),
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
		//if (_listCache[position] == null) {
			_listCache[position] = new SDArtistTableCell(_context);
			_cursor.moveToPosition(position);
			_listCache[position]._textArtist.setText(_cursor.getString(_cursor
					.getColumnIndex(ArtistColumns.ARTIST)));
			_listCache[position]._textNumOfAlbum
					.setText(_cursor.getString(_cursor
							.getColumnIndex(ArtistColumns.NUMBER_OF_ALBUMS)) + " albums");
			_listCache[position]._textNumOfTrack
			.setText(_cursor.getString(_cursor
					.getColumnIndex(ArtistColumns.NUMBER_OF_TRACKS)) + " tracks");
			_listCache[position]._coverArt.getArtistCoverArt(_cursor.getLong(_cursor
					.getColumnIndex(BaseColumns._ID)));
		//}
		return _listCache[position];
	}

	public class SDArtistTableCell extends RelativeLayout {
		TextView _textArtist;
		TextView _textNumOfAlbum;
		TextView _textNumOfTrack;
		SDAlbumCoverArt _coverArt;

		public SDArtistTableCell(Context context) {
			super(context);
			LayoutInflater.from(context).inflate(R.layout.listartist_tablecell,
					this);
			_coverArt = (SDAlbumCoverArt) findViewById(R.id.artistCell_coverart);
			_textArtist = (TextView) findViewById(R.id.artistCell_artist);
			_textNumOfAlbum = (TextView) findViewById(R.id.artistCell_numOfAlbum);
			_textNumOfTrack = (TextView) findViewById(R.id.artistCell_numOfTrack);
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

}
