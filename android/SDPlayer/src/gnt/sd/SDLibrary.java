package gnt.sd;

import gnt.sd.model.SDAudio;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.MediaColumns;

public class SDLibrary {
	
	private static final String[] ProjectionAudio = { Media._ID, Media.ALBUM_ID,
			Media.ALBUM, Media.ARTIST_ID, Media.ARTIST, Media.COMPOSER,
			Media.DATA, Media.DURATION, Media.DATE_ADDED, Media.DATE_MODIFIED,
			Media.DISPLAY_NAME, Media.SIZE, Media.MIME_TYPE, Media.TITLE,
			Media.TRACK, Media.YEAR};
	
	private Context _context;
	private SDNowPlaylist _nowPlaylist;
	public SDLibrary(Context context) {
		_context = context;
		_nowPlaylist = getPlaylistForAllAudio();
	}
	
	public SDAudio getAudioByPath(String path) {
		ContentResolver contentResolver = _context.getContentResolver();
		String selection = MediaColumns.DATA + "=?";
		String[] selectionArgs = new String[] { path };
		try {
			Cursor c = contentResolver.query(Media.EXTERNAL_CONTENT_URI, ProjectionAudio, selection, selectionArgs, Media.DEFAULT_SORT_ORDER);
			if (c != null && c.moveToFirst()) {
				SDAudio audio = cursor2Audio(c);
				c.close();
				return audio;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized SDNowPlaylist makePlaylistFromUri(final Uri uri) {
		//check this file has scanned or not
		SDAudio audio = getAudioByPath(uri.getPath());
		//this file has not scanned yet
		if (audio == null) {
			try {
				final MediaScannerConnection msc = new MediaScannerConnection(_context, new MediaScannerConnection.MediaScannerConnectionClient() {
					@Override
					public void onScanCompleted(String path, Uri uri) {
						SDLibrary.this.notify();
					}
					@Override
					public void onMediaScannerConnected() {
						SDLibrary.this.notify();
					}
				});
				msc.connect();
				wait();
				msc.scanFile(uri.getPath(), "audio/mpeg");
				wait();
				audio = getAudioByPath(uri.getPath());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (audio != null) {
			SDNowPlaylist nowPlaylist = new SDNowPlaylist();
			nowPlaylist.addSong(audio);
			return nowPlaylist;
		}
		return null;
	}
	
	public SDAudio getAudioById(long Id) {
		ContentResolver contentResolver = _context.getContentResolver();
		String selection = BaseColumns._ID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(Id) };
		try {
			Cursor c = contentResolver.query(Media.EXTERNAL_CONTENT_URI, null, selection, selectionArgs, Media.DEFAULT_SORT_ORDER);
			if (c != null && c.moveToFirst()) {
				SDAudio audio = cursor2Audio(c);
				c.close();
				return audio;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Cursor getAudioCursor() {
		ContentResolver contentResolver = _context.getContentResolver();
		Cursor c = contentResolver.query(Media.EXTERNAL_CONTENT_URI,
				ProjectionAudio, null, null, MediaColumns.TITLE);
		return c;
	}
	
	public List<SDAudio> getAudioByIds(long[] Ids) {
		int i;
		List<SDAudio> audioList = new ArrayList<SDAudio>();
		for(i = 0;i < Ids.length;i++) {
			SDAudio audio = getAudioById(Ids[i]);
			audioList.add(audio);
		}
		return audioList;
	}
	
	public SDNowPlaylist getPlaylistForAllAudio() {
		SDNowPlaylist playlist = new SDNowPlaylist();
		List<SDAudio> songs = new ArrayList<SDAudio>();
		SDAudio audio = null;
		Cursor c = getAudioCursor();
		if (c != null) {
			try {
				while (c.moveToNext()) {
					audio = cursor2Audio(c);
					songs.add(audio);
				}
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		playlist.setSongs(songs);
		return playlist;
	}
	
	public SDAudio getTheFirstAudio() {
		ContentResolver contentResolver = _context.getContentResolver();

		try {
			Cursor c = contentResolver.query(Media.EXTERNAL_CONTENT_URI, null, null, null, Media.DEFAULT_SORT_ORDER);
			if (c != null && c.moveToFirst()) {
				SDAudio audio = cursor2Audio(c);
				c.close();
				return audio;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public SDNowPlaylist getNowPlaylist() {
		return _nowPlaylist;
	}
	
	public void setNowPlaylist(SDNowPlaylist playlist) {
		_nowPlaylist = playlist;
	}
	
	private SDAudio cursor2Audio(Cursor c) {
		SDAudio audio = new SDAudio();
		audio.setId(c.getLong(c.getColumnIndex(BaseColumns._ID)));
		audio.setAlbumId(c.getLong(c.getColumnIndex(AudioColumns.ALBUM_ID)));
		audio.setAlbum(c.getString(c.getColumnIndex(AudioColumns.ALBUM)));
		audio.setArtist(c.getString(c.getColumnIndex(AudioColumns.ARTIST)));
		audio.setArtistId(c.getLong(c.getColumnIndex(AudioColumns.ARTIST_ID)));
		audio.setComposer(c.getString(c.getColumnIndex(AudioColumns.COMPOSER)));
		audio.setDuration(c.getLong(c.getColumnIndex(AudioColumns.DURATION)));
		audio.setPath(c.getString(c.getColumnIndex(MediaColumns.DATA)));
		audio.setDateAdded(c.getLong(c.getColumnIndex(MediaColumns.DATE_ADDED)));
		audio.setDateModified(c.getLong(c.getColumnIndex(MediaColumns.DATE_MODIFIED)));
		audio.setDisplayName(c.getString(c.getColumnIndex(MediaColumns.DISPLAY_NAME)));
		audio.setSize(c.getLong(c.getColumnIndex(MediaColumns.SIZE)));
		audio.setMimeType(c.getString(c.getColumnIndex(MediaColumns.MIME_TYPE)));
		audio.setTitle(c.getString(c.getColumnIndex(MediaColumns.TITLE)));
		audio.setTrack(c.getInt(c.getColumnIndex(AudioColumns.TRACK)));
		audio.setYear(c.getInt(c.getColumnIndex(AudioColumns.YEAR)));
		return audio;
	}
}
