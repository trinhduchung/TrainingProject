package gnt.sd;

import java.util.ArrayList;
import java.util.List;


import gnt.sd.controller.Service;
import gnt.sd.controller.ServiceListener;
import gnt.sd.controller.ServiceRespone;
import gnt.sd.model.SDArtistInfo;
import gnt.sd.view.SDAlbumCoverArt;
import gnt.sd.view.SDSongAdapter;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.provider.MediaStore.Audio.Artists;
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

public class SDArtistDetailActivity extends SDBaseLibaryActivity implements
		ServiceListener, OnItemClickListener{
	TextView _textArtist;
	TextView _textBio;
	SDAlbumCoverArt _coverart;
	Cursor _cursorArtist;
	Cursor _cursorTrack;
	SDSongAdapter _adapter;
	Service _serviceBio;
	ProgressBar _loadingBio;
	RelativeLayout _layoutBio;
	String _bio;
	List<SDAudio> _listSong;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ContentResolver contentResolver = getApplication().getContentResolver();
		long id = getIntent().getLongExtra("artist_id", 0);
		_listSong = getAudioByArtistId(id);
		String selection = BaseColumns._ID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(id) };
		_cursorArtist = contentResolver.query(Artists.EXTERNAL_CONTENT_URI,
				null, selection, selectionArgs, Artists.DEFAULT_SORT_ORDER);
		if (!_cursorArtist.moveToFirst()) {
			finish();
		}
		setupUI();
		_serviceBio = new Service(this);
		_serviceBio.getArtistBio(_cursorArtist.getString(_cursorArtist
				.getColumnIndex(ArtistColumns.ARTIST)));
	}

	public void setupUI() {
		RelativeLayout header = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.artistdetail_headerview, null);
		_headerView.addView(header);
		_textArtist = (TextView) header.findViewById(R.id.artistdetail_title);
		_layoutBio = (RelativeLayout) findViewById(R.id.artistdetail_layoutbio);
		_layoutBio.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SDArtistDetailActivity.this, SDBioActivity.class);
				intent.putExtra("artist", _cursorArtist.getString(_cursorArtist
				.getColumnIndex(ArtistColumns.ARTIST)));
				intent.putExtra("bio", _bio);
				startActivity(intent);
			}
		});
		_textBio = (TextView) header.findViewById(R.id.artistdetail_bio);
		_loadingBio = (ProgressBar) header
				.findViewById(R.id.artistdetail_loadbio);
		_coverart = (SDAlbumCoverArt) header
				.findViewById(R.id.artistdetail_coverart);
		_textArtist.setText(_cursorArtist.getString(_cursorArtist
				.getColumnIndex(ArtistColumns.ARTIST)));
		_coverart.getArtistCoverArt(_cursorArtist.getLong(_cursorArtist
				.getColumnIndex(BaseColumns._ID)));

		ContentResolver contentResolver = getApplication().getContentResolver();
		String selection = AudioColumns.ARTIST_ID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(_cursorArtist
				.getLong(_cursorArtist
						.getColumnIndex(BaseColumns._ID))) };
		_cursorTrack = contentResolver.query(Media.EXTERNAL_CONTENT_URI, null,
				selection, selectionArgs, Media.DEFAULT_SORT_ORDER);
		_adapter = new SDSongAdapter(this, _cursorTrack, false);
		_listView.setAdapter(_adapter);
		_listView.setOnItemClickListener(this);
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
	public List<SDAudio> getAudioByArtistId(long artistId) {
		ArrayList<SDAudio> songs = new ArrayList<SDAudio>();
		ContentResolver contentResolver = getApplication().getContentResolver();
		String selection = Media.ARTIST_ID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(artistId) };
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
	
	@Override
	public void onComplete(Service service, ServiceRespone result) {
		if (result.isSuccess()) {
			SDArtistInfo artistInfo = (SDArtistInfo) result.getData();
			if (artistInfo.getBio() != null
					&& artistInfo.getBio().length() > 0){
				_bio = artistInfo.getBio();
				_textBio.setText("Bio: " + _bio);
				_layoutBio.setClickable(true);
			}
			
			else {
				_textBio.setText("Bio: Not found");
				_layoutBio.setClickable(false);
			}
		} else {
			_textBio.setText("Bio: Not found");
			_layoutBio.setClickable(false);
		}
		_loadingBio.setVisibility(View.GONE);
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
