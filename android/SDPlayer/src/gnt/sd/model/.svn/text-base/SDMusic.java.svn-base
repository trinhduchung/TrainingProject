package gnt.sd.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class SDMusic implements Parcelable {
	
	private String _lyric;
	private String _albumArtPath;
	
	public SDMusic() {
		
	}
	
	public SDMusic(Parcel in) {
		Bundle bundle = in.readBundle();
		setLyric(bundle.getCharSequence("lyric") == null ? "" : bundle.getCharSequence("lyric").toString());
		setAlbumArtPath(bundle.getCharSequence("album_art_path") == null ? "" : bundle.getCharSequence("album_art_path").toString());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Bundle bundle = new Bundle();
		bundle.putString("lyric", getLyric());
		bundle.putString("album_art_path", getAlbumArtPath());
		dest.writeBundle(bundle);
	}
	
	public static final Parcelable.Creator<SDMusic> CREATOR = new Creator<SDMusic>() {
		
		@Override
		public SDMusic[] newArray(int size) {
			return new SDMusic[size];
		}
		
		@Override
		public SDMusic createFromParcel(Parcel in) {
			return new SDMusic(in);
		}
	};
	
	public void setAlbumArtPath(String _albumArtPath) {
		this._albumArtPath = _albumArtPath;
	}

	public String getAlbumArtPath() {
		return _albumArtPath;
	}

	public void setLyric(String _lyric) {
		this._lyric = _lyric;
	}

	public String getLyric() {
		return _lyric;
	}
}
