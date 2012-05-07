package gnt.sd;

import gnt.sd.model.SDAudio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;

public class SDPlayer implements OnErrorListener, OnCompletionListener, OnBufferingUpdateListener, OnPreparedListener {
	
	private MediaPlayer _mediaPlayer;
	
	private int _state;
	public static final int STATE_IDLE = 4;
	public static final int STATE_PREPARE = 3;
	public static final int STATE_PAUSE = 2;
	public static final int STATE_PLAYING = 1;
	public static final int STATE_STOP = 0;
	
	private boolean _mediaPrepare = false;
	private SDAudio _currentAudio;
	private SDAudio _inputAudio;
	
	private SDIPlayerListener _playerListener;
	private Context _context;
	public SDPlayer(Context context) {
		_context = context;
		init();
	}
	
	private void init() {
		_mediaPlayer = new MediaPlayer();
		_mediaPlayer.setOnErrorListener(this);
		_mediaPlayer.setOnCompletionListener(this);
		_mediaPlayer.setOnBufferingUpdateListener(this);
		_mediaPlayer.setOnPreparedListener(this);
		_currentAudio = null;
		if(_state != STATE_STOP) {
			triggerPlayerStateChange(_state, STATE_STOP);
		}
	}
	
	public void setPlayerListener(SDIPlayerListener listener) {
		_playerListener = listener;
	}
	
	public int getState() {
		return this._state;
	}
	
	public boolean isPlaying() {
		return (_state == STATE_PLAYING);
	}
	
	public void setLooping(boolean isLooping) {
		_mediaPlayer.setLooping(isLooping);
	}
	
	public boolean getLooping() {
		return _mediaPlayer.isLooping();
	}
	
	public void setAudio(SDAudio audio) {
		if(audio == null) {
			return;
		}
		
		if(_currentAudio != null) {
			_mediaPlayer.reset();
		}
		
		_inputAudio = audio;
		
		try {
			_mediaPlayer.setDataSource(audio.getPath());
			//_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			if(_state != STATE_IDLE) {
				triggerPlayerStateChange(_state, STATE_IDLE);
			}
			triggerAudioChange(_inputAudio);
		} catch (IOException e) {
			_playerListener.onAudioError(_inputAudio);
		} catch (IllegalStateException ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean play() {
		try {
			switch (_state) {
			case STATE_IDLE:
				_mediaPlayer.prepare();
			case STATE_PREPARE:
			case STATE_PAUSE:
				_mediaPlayer.start();
				triggerPlayerStateChange(_state, STATE_PLAYING);
			case STATE_PLAYING:
				return true;
			case STATE_STOP:
				if(_inputAudio == null) {
					_playerListener.onAudioError(_inputAudio);
				}
				return false;
			default:
				return false;
			}
		} catch (IOException e) {
			_playerListener.onAudioError(_inputAudio);
			init();
			return false;
		}
	}
	
	public boolean replay() {
		switch (_state) {
        case STATE_PLAYING:
        case STATE_PAUSE:
            seekTo(0);
        case STATE_IDLE:
        case STATE_PREPARE:
            play();
            return true;
        case STATE_STOP:
            return false;
        default:
            return false;
        }
	}
	
	public boolean pause() {
		try {
			switch (_state) {
			case STATE_IDLE:
				_mediaPlayer.prepare();
			case STATE_PREPARE:
			case STATE_PLAYING:
				_mediaPlayer.pause();
				triggerPlayerStateChange(_state, STATE_PAUSE);
				return true;
			case STATE_PAUSE:
				return true;
			case STATE_STOP:
				return false;
			default:
				return false;
			}
		} catch (IOException e) {
			_playerListener.onAudioError(_currentAudio);
			init();
			return false;
		}
	}
	
//	public boolean setVolume(float vol) {
//		try {
//			switch (_state) {
//			case STATE_IDLE:
//				_mediaPlayer.prepare();
//				_state = STATE_PREPARE;
//			case STATE_PAUSE:
//			case STATE_PLAYING:
//			case STATE_PREPARE:
//				_mediaPlayer.setVolume(vol, vol);
//				return true;
//			case STATE_STOP:
//				return false;
//			default:
//				return false;
//			}
//		} catch (IOException e) {
//			_playerListener.onAudioError(_currentAudio);
//			init();
//			return false;
//		}
//	}
	
	public boolean seekTo(int position) {
		boolean isPlaying = false;
		try {
			switch (_state) {
			case STATE_IDLE:
				_mediaPlayer.prepare();
				_state = STATE_PREPARE;
			case STATE_PLAYING:
				isPlaying = true;
			case STATE_PAUSE:
				_mediaPlayer.pause();
			case STATE_PREPARE:
				_mediaPlayer.seekTo(position);
				if(isPlaying) {
					_mediaPlayer.start();
				}
				return true;
			case STATE_STOP:
				return false;
			default:
				return false;
		}
		} catch (IOException e) {
			_playerListener.onAudioError(_currentAudio);
			init();
			return false;
		}
	}
	
	public int getDuration() {
		try {
			switch (_state) {
			case STATE_IDLE:
				_mediaPlayer.prepare();
				_state = STATE_PREPARE;
			case STATE_PREPARE:
			case STATE_PLAYING:
			case STATE_PAUSE:
				return _mediaPlayer.getDuration();
			case STATE_STOP:
				return 0;
			default:
				return 0;
			}
		} catch (IOException e) {
			triggerAudioChange(_currentAudio);
			init();
			return 0;
		}
	}
	
	public int getPosition() {
			switch (_state) {
			case STATE_PREPARE:
			case STATE_PLAYING:
			case STATE_PAUSE:
				return _mediaPlayer.getCurrentPosition();
			case STATE_IDLE:
			case STATE_STOP:
				return 0;
			default:
				return 0;
			}
	}
	
	public boolean stop() {
		switch (_state) {
		case STATE_PREPARE:
		case STATE_PLAYING:
		case STATE_PAUSE:
			_mediaPlayer.stop();
			triggerPlayerStateChange(_state, STATE_STOP);
			return true;
		case STATE_STOP:
			return true;
		default:
			return false;
		}
	}
	
	public MediaPlayer getMediaPlayer() {
		if(_mediaPlayer != null) {
			return this._mediaPlayer;
		} else {
			return null;
		}
	}
	
	private void triggerPlayerStateChange(int oState,int nState) {
		_state = nState;
		if(_playerListener != null) {
			_playerListener.onPlayerStateChanged(oState, nState);
		}
	}
	
	private void triggerCompletion(SDAudio audio) {
		if(_playerListener != null) {
			_playerListener.onAudioComplete(audio);
		}
	}
	
	private void triggerAudioChange(SDAudio audio) {
		_currentAudio = audio;
		if(_playerListener != null) {
			_playerListener.onAudioChanged(audio);
		}
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		_mediaPlayer.stop();
		triggerPlayerStateChange(_state, STATE_IDLE);
		triggerCompletion(_currentAudio);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		mp.release();
		_playerListener.onPlayerError("");
		init();
		return false;
	}

	@Override
	protected void finalize() throws Throwable {
		_mediaPlayer.release();
		super.finalize();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		if(!_mediaPrepare) {
			mp.prepareAsync();
		}
		_playerListener.onBufferingUpdate(percent);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		_mediaPrepare = true;
	}
	
}
