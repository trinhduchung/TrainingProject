package gnt.sd;

import java.util.HashMap;

import android.app.Application;
import android.media.MediaPlayer;

public class SDApplication extends Application {
	
	SDLibrary _library;
	SDIMusicPlayerImpl _musicPlayer;
	private MediaPlayer _mediaPlayer;
	public static SDApplication _instance;
	HashMap<Object, Object> _data = new HashMap<Object, Object>();
	
	public SDApplication() {
		super();
		_instance = this;
	}
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		_library = new SDLibrary(this);
		_musicPlayer = new SDIMusicPlayerImpl(this);
		_musicPlayer.initialize();
	}

	public MediaPlayer getMediaplayer() {
		if(_musicPlayer != null) {
			_mediaPlayer = _musicPlayer.getMediaPlayer();
		}
		return _mediaPlayer;
	}
	
	public SDIMusicPlayerImpl getMusicPlayer() {
		return _musicPlayer;
	}

	public void putData(Object key, Object value) {
        _data.put(key, value);
    }

    public void removeData(Object key) {
        _data.remove(key);
    }

    public Object getData(Object key) {
        return _data.get(key);
    }
	
	public SDLibrary getLibrary() {
		return _library;
	}
	
	public static SDApplication Instance() {
		return _instance;
	}
}
