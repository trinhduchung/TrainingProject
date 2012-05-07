package gnt.sd.view;

import gnt.sd.R;
import gnt.sd.util.Util;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SDListSongShortView extends RelativeLayout{
	ListView _listView;
	TextView _textAlbum;
	TextView _textArtist;
	RelativeLayout _containerView;
	SDAlbumCoverArt _swipView;
	public SDListSongShortView(Context context, SDAlbumCoverArt swipView) {
		super(context);
		_swipView = swipView;
		LayoutInflater inflate = LayoutInflater.from(context);
		inflate.inflate(R.layout.listsong_shortview, this);
		_textAlbum = (TextView) findViewById(R.id.listsong_short_album);
		_textArtist = (TextView) findViewById(R.id.listsong_short_artist);
		ContentResolver contentResolver = context.getContentResolver();
		String selection = BaseColumns._ID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(swipView._id) };
		Cursor cursorAlbum = contentResolver.query(Albums.EXTERNAL_CONTENT_URI, null,
				selection, selectionArgs, Albums.DEFAULT_SORT_ORDER);
		if (cursorAlbum.moveToFirst()) {
			_textAlbum.setText(cursorAlbum.getString(cursorAlbum.getColumnIndex(AlbumColumns.ALBUM)));
			_textArtist.setText(cursorAlbum.getString(cursorAlbum.getColumnIndex(AlbumColumns.ARTIST)));
		} 
		cursorAlbum.close();
		selection = AudioColumns.ALBUM_ID + "=?";
		selectionArgs = new String[] { String.valueOf(swipView._id) };
		Cursor cursorTrack = contentResolver.query(Media.EXTERNAL_CONTENT_URI, null,
				selection, selectionArgs, Media.DEFAULT_SORT_ORDER);
		SDListSongShortAdapter _adapter = new SDListSongShortAdapter(context, cursorTrack);
		_listView = (ListView) findViewById(android.R.id.list);
		_listView.setAdapter(_adapter);
		_containerView = (RelativeLayout) findViewById(R.id.listsong_container);
		_containerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.applyRotation(SDListSongShortView.this, _swipView, 0, -90);
				((ViewGroup)SDListSongShortView.this.getParent()).removeView(SDListSongShortView.this);
			}
		});
	}

}
