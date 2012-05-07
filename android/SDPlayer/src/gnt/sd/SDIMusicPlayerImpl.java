package gnt.sd;

import gnt.sd.model.SDAudio;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class SDIMusicPlayerImpl implements SDIMusicPlayer{
	
	private final String DEBUG_TAG = "MusicPlayerImpl";
	private SDPlayerService _playerService;
	private boolean _isBound;
	private Context _context;
	private ServiceConnection _connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			_isBound = false;
			_playerService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			_isBound = true;
			_playerService = ((SDPlayerService.LocalBinder) service).getService();
			Log.i(DEBUG_TAG, "Start binding to player service");
			if (getCurrentAudio() == null) {
				SDLibrary library = ((SDApplication) _context.getApplicationContext()).getLibrary();
				SDNowPlaylist playlist = library.getPlaylistForAllAudio();
				playlist.setCurrentIndex(0);
				setPlaylist(playlist);
			}
		}
	};
	
	public SDIMusicPlayerImpl(Context context) {
		_context = context;
	}
	
	public void initialize() {
		Log.i(DEBUG_TAG, "Initialize music player.");
		Intent iConnect = new Intent(_context, SDPlayerService.class);
		IntentFilter f = new IntentFilter();
		f.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		_context.registerReceiver(_mediaScanned, f);
    	_context.startService(iConnect);
    	_context.bindService(iConnect, _connection,
    				Context.BIND_AUTO_CREATE);
	}
	
	BroadcastReceiver _mediaScanned = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			SDLibrary library = ((SDApplication) _context.getApplicationContext()).getLibrary();
			SDNowPlaylist playlist = library.getPlaylistForAllAudio();
			playlist.setCurrentIndex(0);
			setPlaylist(playlist);
		}
	};
	
	public boolean isReady() {
		return _isBound;
	}
	
	public int getState() {
		return _playerService.getState();
	}
	
	@Override
	public void play() {
		_playerService.play();
	}

	@Override
	public void stop() {
		_playerService.stop();
	}

	@Override
	public void pause() {
		_playerService.pause();
	}

	@Override
	public void replay() {
		_playerService.replay();
	}

	@Override
	public void next() {
		_playerService.next();
	}

	@Override
	public void prev() {
		_playerService.prev();
	}

	@Override
	public boolean isPlaying() {
		return _playerService.isPlaying();
	}

	@Override
	public void setLoopMode(int mode) {
		_playerService.setLoopMode(mode);
	}

	@Override
	public int getSeekPosition() {
		return _playerService.getPosition();
	}

	@Override
	public void setSeekPosition(int position) {
		_playerService.seekTo(position);
	}

	@Override
	public int getDuration() {
		return _playerService.getDuration();
	}

	@Override
	public boolean isShuffle() {
		return _playerService.isShuffle();
	}

	@Override
	public void setShuffle(boolean isShuffle) {
		_playerService.setShuffle(isShuffle);
	}

	@Override
	public boolean canPrev() {
		return _playerService.canGoBackward();
	}

	@Override
	public boolean canNext() {
		return _playerService.canGoForward();
	}

	@Override
	public SDAudio getCurrentAudio() {
		return _playerService.getCurrentSong();
	}

	@Override
	public long[] getIdsQueue() {
		return _playerService.getIdsSequence();
	}

	@Override
	public boolean isPlaylistEmpty() {
		long[] Ids = _playerService.getIdsSequence();
		return (Ids == null || Ids.length == 0);
	}

	@Override
	public void setSequenceIndex(int idx) {
		_playerService.setSequenceIndex(idx);
	}

	@Override
	public void setPlaylist(SDNowPlaylist playlist) {
		if (playlist == null) {
			_playerService.setIdsSequence(new long[] {}, -1);
		}
		long[] ids = playlist.getIdsSequence();
		int startIdx = playlist.getCurrentIndex();
		if(_playerService != null) {
			_playerService.stop();
			_playerService.setIdsSequence(ids, startIdx);
		}
	}

	@Override
	public SDNowPlaylist getPlaylist() {
		long[] ids = _playerService.getIdsSequence();
		SDNowPlaylist playlist = new SDNowPlaylist();
		playlist.setSongIds(ids);
		long id = getCurrentAudio().getId();
		playlist.setCurrentIndex(0);
		for (int i = 0; i < ids.length; i++) {
			if (id == ids[i]) {
				playlist.setCurrentIndex(i);
			}
		}
		return playlist;
	}

	@Override
	public int getLoopMode() {
		return _playerService.getLoopMode();
	}

	@Override
	public int getSequenceIndex() {
		return _playerService.getSequenceIndex();
	}

	@Override
	public MediaPlayer getMediaPlayer() {
		if(_playerService != null) {
			return _playerService.getMediaPlayer();
		} else {
			return null;
		}
	}

	@Override
	public void setAudio(SDAudio audio) {
		_playerService.setAudio(audio);
	}

}
