package gnt.sd.view;

import gnt.sd.R;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SDAlbumAdapter extends BaseAdapter implements SectionIndexer {

	private Cursor _cursor;
	private LayoutInflater _inflate;
	View[] _listCache;
	AlphabetIndexer _alphabetIndexer;

	public SDAlbumAdapter(Context context, Cursor c) {
		this._cursor = c;
		_inflate = LayoutInflater.from(context);
		_listCache = new View[c.getCount()];
		_alphabetIndexer = new AlphabetIndexer(_cursor,
				_cursor.getColumnIndex(AlbumColumns.ALBUM),
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
		SDAlbumTableCell row;
		// if (_listCache[position] == null) {
		convertView = _inflate.inflate(R.layout.listalbum_tablecell, null);
		row = new SDAlbumTableCell();
		row._coverArt = (SDAlbumCoverArt) convertView
				.findViewById(R.id.albumCell_coverart);
		row._textAlbum = (TextView) convertView
				.findViewById(R.id.albumCell_album);
		row._textArtist = (TextView) convertView
				.findViewById(R.id.albumCell_artist);
		_cursor.moveToPosition(position);
		row._textAlbum.setText(_cursor.getString(_cursor
				.getColumnIndex(AlbumColumns.ALBUM)));
		row._textArtist.setText(_cursor.getString(_cursor
				.getColumnIndex(AlbumColumns.ARTIST)));
		row._coverArt.getCoverArt(_cursor.getLong(_cursor
				.getColumnIndex(BaseColumns._ID)));
		_listCache[position] = convertView;
		// }
		return _listCache[position];
	}

	public class SDAlbumTableCell {
		TextView _textArtist;
		TextView _textAlbum;
		SDAlbumCoverArt _coverArt;
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
