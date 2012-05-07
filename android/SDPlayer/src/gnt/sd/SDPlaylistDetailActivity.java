package gnt.sd;

import java.util.ArrayList;
import java.util.List;


import gnt.sd.model.SDAudio;
import gnt.sd.view.SDPlaylistCoverArt;
import gnt.sd.view.SDSongAdapter;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SDPlaylistDetailActivity extends SDBaseLibaryActivity implements OnItemClickListener{
	TextView _textTitle;
	SDPlaylistCoverArt _coverart;
	Cursor _cursorPlaylist;
	Cursor _cursorTrack;
	SDSongAdapter _adapter;
	long _id = 0;
	List<SDAudio> _list;
	public static final String[] ProjectionAudio = { Media._ID, Media.ALBUM_ID,
		Media.ALBUM, Media.ARTIST_ID, Media.ARTIST, Media.COMPOSER,
		Media.DATA, Media.DURATION, Media.DATE_ADDED, Media.DATE_MODIFIED,
		Media.DISPLAY_NAME, Media.SIZE, Media.MIME_TYPE, Media.TITLE,
		Media.TRACK, Media.YEAR};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ContentResolver contentResolver = getApplication().getContentResolver();
		long id = getIntent().getLongExtra("playlist_id", 0);
		_id = id;
		_list = getAudioByPlaylistId(_id);
		String selection = BaseColumns._ID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(id) };
		_cursorPlaylist = contentResolver.query(Playlists.EXTERNAL_CONTENT_URI,
				null, selection, selectionArgs, Playlists.DEFAULT_SORT_ORDER);
		if (!_cursorPlaylist.moveToFirst()) {
			finish();
		}
		setupUI();
	}

	public void setupUI() {
		_headerView.getLayoutParams().height = (int) (60 * getResources().getDisplayMetrics().density);
		RelativeLayout header = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.playlistdetail_headerview, null);
		_headerView.addView(header);
		_textTitle = (TextView) header.findViewById(R.id.playlistdetail_title);
		_coverart = (SDPlaylistCoverArt) header
				.findViewById(R.id.playlistdetail_coverart);
		_textTitle.setText(_cursorPlaylist.getString(_cursorPlaylist
				.getColumnIndex(PlaylistsColumns.NAME)));
		_coverart.getCoverArt(_cursorPlaylist.getLong(_cursorPlaylist
				.getColumnIndex(BaseColumns._ID)));

		ContentResolver contentResolver = getApplication().getContentResolver();
		_cursorTrack = contentResolver.query(Playlists.Members.getContentUri(
				"external", _cursorPlaylist.getLong(_cursorPlaylist
						.getColumnIndex(BaseColumns._ID))),
				null, null, null, Playlists.Members.DEFAULT_SORT_ORDER);
		_adapter = new SDSongAdapter(this, _cursorTrack, false);
		_listView.setAdapter(_adapter);
		_listView.setOnItemClickListener(this);
	}
	
	public List<SDAudio> getAudioByPlaylistId(long playlistId) {
		// get list audio id of play_list
		List<Long> ids = getAudioIdsByPlaylistId(playlistId);

		// get list songs
		List<SDAudio> songs = getAudioByIds(ids);
		return songs;
	}
	
	public List<Long> getAudioIdsByPlaylistId(long playlistId) {
		ContentResolver resolver = getApplication().getContentResolver();
		// get list audio id of play_list
		ArrayList<Long> ids = new ArrayList<Long>();
		try {
			String[] projection = { Playlists.Members.AUDIO_ID };

			Cursor c = resolver.query(Playlists.Members.getContentUri(
					"external", playlistId), projection, null, null,
					Playlists.Members.DEFAULT_SORT_ORDER);
			if (c != null) {
				while (c.moveToNext()) {
					long id = c.getLong(c
							.getColumnIndex(Playlists.Members.AUDIO_ID));
					ids.add(id);
				}
				c.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return ids;
	}
	
	public List<SDAudio> getAudioByIds(List<Long> ids) {
		ArrayList<SDAudio> songs = new ArrayList<SDAudio>();
		if (ids == null || ids.isEmpty()) {
			return songs;
		}

		ContentResolver contentResolver = getApplication().getContentResolver();

		StringBuilder selection = new StringBuilder(Media._ID).append("=?");
		String[] selectionArgs = new String[ids.size()];
		selectionArgs[0] = String.valueOf(ids.get(0));
		for (int i = 1; i < ids.size(); i++) {
			selection.append(" OR ").append(Media._ID).append("=?");
			selectionArgs[i] = String.valueOf(ids.get(i));
		}
		try {
			Cursor c = contentResolver.query(Media.EXTERNAL_CONTENT_URI, null,
					selection.toString(), selectionArgs,
					Media.DEFAULT_SORT_ORDER);
			SDAudio audio = null;
			if (c != null) {
				while (c.moveToNext()) {
					audio = cursor2Audio(c);
					songs.add(audio);
				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return songs;
	}
	
	public List<SDAudio> getAudioByIds(long[] ids) {
		ArrayList<SDAudio> songs = new ArrayList<SDAudio>();
		if (ids == null || ids.length == 0) {
			return songs;
		}

		ContentResolver contentResolver = getApplication().getContentResolver();

		StringBuilder selection = new StringBuilder(Media._ID).append("=?");
		String[] selectionArgs = new String[ids.length];
		selectionArgs[0] = String.valueOf(ids[0]);
		for (int i = 1; i < ids.length; i++) {
			selection.append(" OR ").append(Media._ID).append("=?");
			selectionArgs[i] = String.valueOf(ids[i]);
		}
		try {
			Cursor c = contentResolver.query(Media.EXTERNAL_CONTENT_URI, null,
					selection.toString(), selectionArgs,
					Media.DEFAULT_SORT_ORDER);
			SDAudio audio = null;
			if (c != null) {
				if (c.moveToNext()) {
					audio = cursor2Audio(c);
					songs.add(audio);
				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return songs;

	}
	
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		SDNowPlaylist nowPlaylist = new SDNowPlaylist();
		nowPlaylist.setSongs(_list);
		nowPlaylist.setCurrentIndex(position);
		SDLibrary library = SDApplication._instance.getLibrary();
		library.setNowPlaylist(nowPlaylist);
		SDApplication._instance.putData(SDPlayerActivity.CODE_RELOAD, true);
		Intent intent = new Intent(this, SDPlayerActivity.class);
		startActivity(intent);
	}
}
