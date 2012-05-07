package gnt.sd;

import gnt.sd.model.SDAudio;
import gnt.sd.model.SDPlaylist;

import java.util.ArrayList;
import java.util.List;

public class SDNowPlaylist extends SDPlaylist {
	private List<SDAudio> _songs;  // The songs of this playlist
	private int _currentIndex;	// The index of current song was playing

	public SDNowPlaylist() {
        this._songs = new ArrayList<SDAudio>();
	}
	public SDNowPlaylist(String name)
	{
		this._name = name;
	}
	public void setSongs(List<SDAudio> songs) {
		_songs = songs;
	}

	public void setSongIds(long[] ids) {
		SDApplication mobiApp = SDApplication.Instance();
		SDLibrary library = mobiApp.getLibrary();
		_songs = library.getAudioByIds(ids);
	}

    public long[] getIdsSequence() {
    	if (_songs == null) {
    		return null;
    	}
    	long[] ids = new long[_songs.size()];
    	for (int i = 0; i < _songs.size(); i++) {
    		ids[i] = _songs.get(i).getId();
    	}
    	return ids;
    }

	public List<SDAudio> getSongs() {
		return _songs;
	}

    public int getCurrentIndex() {
        return _currentIndex;
    }

    public SDAudio getCurrentSong() {
    	return _songs.get(_currentIndex);
    }

    public void setCurrentIndex(int currentIndex) {
        this._currentIndex = currentIndex;
    }

    public boolean contains(SDAudio audio) {
    	long[] ids = getIdsSequence();
    	for (long id : ids) {
    		if (id == audio.getId()) {
    			return true;
    		}
    	}
    	return false;
    }

    public void addSong(SDAudio audio) {
    	_songs.add(audio);
    }

    public void insertSong(int index, SDAudio audio) {
    	_songs.add(index, audio);
    }

    public void removeSong(SDAudio audio) {
    	_songs.remove(audio);
    }

    public void removeSong(int index) {
    	_songs.remove(index);
    }
}
