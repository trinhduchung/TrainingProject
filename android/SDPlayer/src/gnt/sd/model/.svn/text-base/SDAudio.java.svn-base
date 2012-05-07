package gnt.sd.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;


public class SDAudio extends BaseObject implements Parcelable {

	public SDAudio() {
		
	}
	
	public SDAudio(Parcel in) {
		Bundle bundle = in.readBundle();
        setId(bundle.getLong(ID, 0));
        setAlbum((bundle.getCharSequence(ALBUM) == null) ? "" : bundle.getCharSequence(ALBUM).toString());
        setAlbumId(bundle.getLong(ALBUM_ID, 0));
        setArtist((bundle.getCharSequence(ARTIST) == null) ? "" : bundle.getCharSequence(ARTIST).toString());
        setArtistId(bundle.getLong(ARTIST_ID, 0));
        setBookmark(bundle.getLong(BOOKMARK, 0));
        setComposer((bundle.getCharSequence(COMPOSER) == null) ? "" : bundle.getCharSequence(COMPOSER).toString());
        setDuration(bundle.getLong(DURATION, 0));
        setYear(bundle.getInt(YEAR, 0));
        setPath(bundle.getString(DATA).toString());
        setDateAdded(bundle.getLong(DATE_ADDED, 0));
        setDateModified(bundle.getLong(DATE_MODIFIED, 0));
        setDisplayName((bundle.getCharSequence(DISPLAY_NAME) == null) ? "" : bundle.getCharSequence(DISPLAY_NAME).toString());
        setSize(bundle.getLong(SIZE, 0));
        setMimeType((bundle.getCharSequence(MIME_TYPE) == null) ? "" : bundle.getCharSequence(MIME_TYPE).toString());
        setTitle((bundle.getCharSequence(TITLE) == null) ? "" : bundle.getCharSequence(TITLE).toString());
        setTrack(bundle.getInt(TRACK, 0));
	}
	
	public static final Parcelable.Creator<SDAudio> CREATOR = new Parcelable.Creator<SDAudio>() {
        @Override
		public SDAudio createFromParcel(Parcel in) {
            return new SDAudio(in);
        }

        @Override
		public SDAudio[] newArray(int size) {
            return new SDAudio[size];
        }
    };
	
	@Override
	public boolean equals(Object o) {
		SDAudio audio = (SDAudio) o;
		return this.getTitle().equals(audio.getTitle());
	}

	private String _album; // the album the audio file is from, if any
	private long _albumId; // the album id the audio file is from, if any
	private String _artist; // the artist who created the audio file, if any
	private long _artistId; // the artist id
	private long _bookmark; // the position, in ms, playback was at when
							// playback
	// for this file was last stopped.
	private String _composer; // The composer of the audio file, if any
	private long _duration; // The duration of the audio file, in ms
	private int _year; // The year the audio file was recorded, if any
	private String _data;
	private long _dateAdded;
	private long _dateModified;
	private String _displayName;
	private long _size;
	private String _mimeType;
	private String _title;
	private int _track;
	private String _albumArt;

	public String getAlbum() {
		return _album;
	}

	public void setAlbum(String album) {
		this._album = album;
	}

	public long getAlbumId() {
		return _albumId;
	}

	public void setAlbumId(long albumId) {
		this._albumId = albumId;
	}

	public String getArtist() {
		return _artist;
	}

	public void setArtist(String artist) {
		this._artist = artist;
	}

	public long getArtistId() {
		return _artistId;
	}

	public void setArtistId(long artistId) {
		this._artistId = artistId;
	}

	public long getBookmark() {
		return _bookmark;
	}

	public void setBookmark(long bookmark) {
		this._bookmark = bookmark;
	}

	public String getComposer() {
		return _composer;
	}

	public void setComposer(String composer) {
		this._composer = composer;
	}

	public long getDuration() {
		return _duration;
	}

	public void setDuration(long duration) {
		this._duration = duration;
	}

	public int getYear() {
		return _year;
	}

	public void setYear(int year) {
		this._year = year;
	}

	public void setPath(String path) {
		this._data = path;
	}

	public String getPath() {
		return _data;
	}

	public long getDateAdded() {
		return _dateAdded;
	}

	public void setDateAdded(long dateAdded) {
		this._dateAdded = dateAdded;
	}

	public long getDateModified() {
		return _dateModified;
	}

	public void setDateModified(long dateModified) {
		this._dateModified = dateModified;
	}

	public String getDisplayName() {
		return _displayName;
	}

	public void setDisplayName(String displayName) {
		this._displayName = displayName;
	}

	public long getSize() {
		return _size;
	}

	public void setSize(long size) {
		this._size = size;
	}

	public String getMimeType() {
		return _mimeType;
	}

	public void setMimeType(String mimeType) {
		this._mimeType = mimeType;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		this._title = title;
	}

	public int getTrack() {
		return _track;
	}

	public void setTrack(int track) {
		this._track = track;
	}

	public void setAlbumArt(String albumArt) {
		this._albumArt = albumArt;
	}

	public String getAlbumArt() {
		return _albumArt;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Bundle bundle = new Bundle();
        bundle.putLong(ID, getId());
        bundle.putString(ALBUM, getAlbum());
        bundle.putLong(ALBUM_ID, getAlbumId());
        bundle.putString(ARTIST, getArtist());
        bundle.putLong(ARTIST_ID, getArtistId());
        bundle.putLong(BOOKMARK, getBookmark());
        bundle.putString(COMPOSER, getComposer());
        bundle.putLong(DURATION, getDuration());
        bundle.putInt(YEAR, getYear());
        bundle.putString(DATA, getPath());
        bundle.putLong(DATE_ADDED, getDateAdded());
        bundle.putLong(DATE_MODIFIED, getDateModified());
        bundle.putString(DISPLAY_NAME, getDisplayName());
        bundle.putLong(SIZE, getSize());
        bundle.putString(MIME_TYPE, getMimeType());
        bundle.putString(TITLE, getTitle());
        bundle.putInt(TRACK, getTrack());
        dest.writeBundle(bundle);
	}
	
	public static final String ID = "id";
    public static final String ALBUM = "album";
    public static final String ALBUM_ID = "album_id";
    public static final String ARTIST = "artist";
    public static final String ARTIST_ID = "artist_id";
    public static final String BOOKMARK = "bookmark";
    public static final String COMPOSER = "composer";
    public static final String DURATION = "duration";
    public static final String YEAR = "year";
    public static final String DATA = "data";
    public static final String DATE_ADDED = "date_added";
    public static final String DATE_MODIFIED = "date_modified";
    public static final String DISPLAY_NAME = "display_name";
    public static final String SIZE = "size";
    public static final String MIME_TYPE = "mime_type";
    public static final String TITLE = "title";
    public static final String TRACK = "track";
}
