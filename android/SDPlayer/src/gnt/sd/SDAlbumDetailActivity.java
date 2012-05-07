package gnt.sd;

import java.util.ArrayList;
import java.util.List;


import gnt.sd.controller.Service;
import gnt.sd.controller.ServiceListener;
import gnt.sd.controller.ServiceRespone;
import gnt.sd.model.SDAlbumInfo;
import gnt.sd.view.SDAlbumCoverArt;
import gnt.sd.view.SDSongAdapter;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import gnt.sd.model.SDAudio;;
public class SDAlbumDetailActivity extends SDBaseLibaryActivity implements
		ServiceListener,OnItemClickListener {
	TextView _textArtist;
	TextView _textAlbum;
	TextView _textReview;
	SDAlbumCoverArt _coverart;
	Cursor _cursorAlbum;
	Cursor _cursorTrack;
	SDSongAdapter _adapter;
	Service _serviceReview;
	ProgressBar _loadingReview;
	List<SDAudio> _listSong;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ContentResolver contentResolver = getApplication().getContentResolver();
		long id = getIntent().getLongExtra("album_id", 0);
		_listSong = getAudioByAlbumId(id);
		String selection = BaseColumns._ID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(id) };
		_cursorAlbum = contentResolver.query(Albums.EXTERNAL_CONTENT_URI, null,
				selection, selectionArgs, Albums.DEFAULT_SORT_ORDER);
		if (!_cursorAlbum.moveToFirst()) {
			finish();
		}
		setupUI();
		_serviceReview = new Service(this);
		_serviceReview.getAlbumInfo(_cursorAlbum.getString(_cursorAlbum
				.getColumnIndex(AlbumColumns.ALBUM)), _cursorAlbum
				.getString(_cursorAlbum
						.getColumnIndex(AlbumColumns.ARTIST)));
	}
	public static final String[] ProjectionAudio = { Media._ID, Media.ALBUM_ID,
		Media.ALBUM, Media.ARTIST_ID, Media.ARTIST, Media.COMPOSER,
		Media.DATA, Media.DURATION, Media.DATE_ADDED, Media.DATE_MODIFIED,
		Media.DISPLAY_NAME, Media.SIZE, Media.MIME_TYPE, Media.TITLE,
		Media.TRACK, Media.YEAR};
	private SDAudio cursor2Audio(Cursor c) {
		SDAudio audio = new SDAudio();
		audio.setId(c.getLong(c.getColumnIndex(Media._ID)));
		audio.setAlbumId(c.getLong(c.getColumnIndex(Media.ALBUM_ID)));
		audio.setAlbum(c.getString(c.getColumnIndex(Media.ALBUM)));
		audio.setArtist(c.getString(c.getColumnIndex(Media.ARTIST)));
		audio.setArtistId(c.getLong(c.getColumnIndex(Media.ARTIST_ID)));
		audio.setComposer(c.getString(c.getColumnIndex(Media.COMPOSER)));
		audio.setDuration(c.getLong(c.getColumnIndex(Media.DURATION)));
		audio.setPath(c.getString(c.getColumnIndex(Media.DATA)));
		audio.setDateAdded(c.getLong(c.getColumnIndex(Media.DATE_ADDED)));
		audio.setDateModified(c.getLong(c.getColumnIndex(Media.DATE_MODIFIED)));
		audio.setDisplayName(c.getString(c.getColumnIndex(Media.DISPLAY_NAME)));
		audio.setSize(c.getLong(c.getColumnIndex(Media.SIZE)));
		audio.setMimeType(c.getString(c.getColumnIndex(Media.MIME_TYPE)));
		audio.setTitle(c.getString(c.getColumnIndex(Media.TITLE)));
		audio.setTrack(c.getInt(c.getColumnIndex(Media.TRACK)));
		audio.setYear(c.getInt(c.getColumnIndex(Media.YEAR)));
		return audio;
	}
	public List<SDAudio> getAudioByAlbumId(long albumId) {
		ArrayList<SDAudio> songs = new ArrayList<SDAudio>();
		ContentResolver contentResolver = getApplication().getContentResolver();
		String selection = Media.ALBUM_ID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(albumId) };
		Cursor c = contentResolver.query(Media.EXTERNAL_CONTENT_URI,
				ProjectionAudio, selection, selectionArgs,
				Media.DEFAULT_SORT_ORDER);
		SDAudio audio = null;
		if (c != null) {
			try {
				while (c.moveToNext()) {
					audio = cursor2Audio(c);
					songs.add(audio);
				}
				c.close();
			} catch (Exception e) {
				c.close();
				e.printStackTrace();
			}
		}
		return songs;
	}
	public void setupUI() {
		RelativeLayout header = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.albumdetail_headerview, null);
		_headerView.addView(header);
		_textArtist = (TextView) header.findViewById(R.id.albumdetail_artist);
		_textAlbum = (TextView) header.findViewById(R.id.albumdetail_title);
		_textReview = (TextView) header.findViewById(R.id.albumdetail_review);
		_loadingReview = (ProgressBar) header
				.findViewById(R.id.albumdetail_loadreview);
		_coverart = (SDAlbumCoverArt) header
				.findViewById(R.id.albumdetail_coverart);
		_textArtist.setText(_cursorAlbum.getString(_cursorAlbum
				.getColumnIndex(AlbumColumns.ARTIST)));
		_textAlbum.setText(_cursorAlbum.getString(_cursorAlbum
				.getColumnIndex(AlbumColumns.ALBUM)));
		_coverart.getCoverArt(_cursorAlbum.getLong(_cursorAlbum
				.getColumnIndex(BaseColumns._ID)));

		ContentResolver contentResolver = getApplication().getContentResolver();
		String selection = AudioColumns.ALBUM_ID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(_cursorAlbum
				.getLong(_cursorAlbum
						.getColumnIndex(BaseColumns._ID))) };
		_cursorTrack = contentResolver.query(Media.EXTERNAL_CONTENT_URI, null,
				selection, selectionArgs, Media.DEFAULT_SORT_ORDER);
		_adapter = new SDSongAdapter(this, _cursorTrack, false);
		_listView.setAdapter(_adapter);
		_listView.setOnItemClickListener(this);
	}

	@Override
	public void onComplete(Service service, ServiceRespone result) {
		if (result.isSuccess()) {
			SDAlbumInfo albumInfo = (SDAlbumInfo) result.getData();
			if (albumInfo.getSummary() != null
					&& albumInfo.getSummary().length() > 0)
				_textReview.setText("Review: " + albumInfo.getSummary());
			else {
				_textReview.setText("Review: Not found");
			}
		} else {
			_textReview.setText("Review: Not found");
		}
		_loadingReview.setVisibility(View.GONE);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		SDNowPlaylist nowPlaylist = new SDNowPlaylist();
		nowPlaylist.setSongs(_listSong);
		nowPlaylist.setCurrentIndex(position);
		SDLibrary library = SDApplication._instance.getLibrary();
		library.setNowPlaylist(nowPlaylist);
		SDApplication._instance.putData(SDPlayerActivity.CODE_RELOAD, true);
		Intent intent = new Intent(this, SDPlayerActivity.class);
		startActivity(intent);
	}

}
