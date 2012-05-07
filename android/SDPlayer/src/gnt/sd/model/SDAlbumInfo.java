package gnt.sd.model;

public class SDAlbumInfo {
	private String _title;
	private String _artist;
	private String _releaseDate;
	private String _summary;
	public void setTitle(String title) {
		this._title = title;
	}
	public String getTitle() {
		return _title;
	}
	public void setArtist(String artist) {
		this._artist = artist;
	}
	public String getArtist() {
		return _artist;
	}
	public void setReleaseDate(String releaseDate) {
		this._releaseDate = releaseDate;
	}
	public String getReleaseDate() {
		return _releaseDate;
	}
	public void setSummary(String summary) {
		this._summary = summary;
	}
	public String getSummary() {
		return _summary;
	}
	
}
