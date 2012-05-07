package gnt.sd;

import gnt.sd.controller.Service;
import gnt.sd.controller.ServiceAction;
import gnt.sd.controller.ServiceListener;
import gnt.sd.controller.ServiceRespone;
import gnt.sd.model.SDAudio;
import gnt.sd.util.Util;
import gnt.sd.view.CoverAdapterView;
import gnt.sd.view.CoverAdapterView.OnItemClickListener;
import gnt.sd.view.SDLyricView;
import gnt.sd.view.SDLyricView.OnDoubleTapListener;
import gnt.sd.view.SDLyricView.OnSingleTapListener;
import gnt.sd.view.SDPlayerCoverFlow;
import gnt.sd.view.SDPlayerCoverFlowAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class SDPlayerActivity extends Activity implements OnItemClickListener, ServiceListener{
	private SDIMusicPlayerImpl _musicPlayer;
	private SDLibrary _library;
	private SDApplication _application;
	public static final String CODE_RELOAD = "playlist";
	public static final String CODE_STREAMING = "streaming";
	private SDNowPlaylist _nowPlaylist;
	private SDProgressMonitor _progressMonitor;
	private Service _getLyric;
	private BroadcastReceiver _sdcardUnMountReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
		}
	}; 
	
	SDPlayerCoverFlow _coverFlow ;
	RelativeLayout _layoutLyric;
	SDLyricView _lyricView;
	LinearLayout _dynamic_control;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		_application = (SDApplication) getApplication();
		_library = _application.getLibrary();
		_getLyric = new Service(this);
		_layoutLyric = (RelativeLayout) findViewById(R.id.player_layout_lyric);
		_dynamic_control = (LinearLayout) findViewById(R.id.player_dynamic_controll);
		_lyricView = (SDLyricView) findViewById(R.id.player_lyric);
		_lyricView.visibleViews();
		_lyricView.setClickable(true);
		_lyricView._containerView.setClickable(true);
		/*
		_lyricView.setOnDoubleTapListener(new OnDoubleTapListener() {	
			@Override
			public void onDoubleTap(View v) {
				if(_dynamic_control.getVisibility() == View.VISIBLE)
					_dynamic_control.setVisibility(View.GONE);
				else
					_dynamic_control.setVisibility(View.VISIBLE);
			}
		});
		
		_lyricView.setOnSingleTapListener(new OnSingleTapListener() {	
			@Override
			public void onSingleTap(View v) {
				_lyricView.invisibleViews();
				_coverFlow.setVisibility(View.VISIBLE);
				_dynamic_control.setVisibility(View.GONE);
			}
		});
		*/
		_musicPlayer = _application.getMusicPlayer();
		
		_progressMonitor = new SDProgressMonitor(this, _musicPlayer, getPlaySeekbar());
		getPlaySeekbar().setOnSeekBarChangeListener(_onProgressChangeListener);
		getVolumeSeekbar().setOnSeekBarChangeListener(_onVolumeChangeListener);
		getVolumeSeekbar().setMax(getMaxVolume());
		
		_coverFlow = (SDPlayerCoverFlow) findViewById(R.id.player_coverflow);
		_coverFlow.setSpacing(0);
		_coverFlow.setVisibility(View.GONE);
		_coverFlow.setOnItemClickListener(this);
		
		IntentFilter f = new IntentFilter();
		f.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		f.addAction(Intent.ACTION_MEDIA_EJECT);
		f.addAction(Intent.ACTION_MEDIA_REMOVED);
		f.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		registerReceiver(_sdcardUnMountReceiver, f);
	}
	
	@Override
	public void onItemClick(CoverAdapterView<?> parent, View view,
			int position, long id) {
//		_lyricView.visibleViews();
//		_lyricView._coverArt.getCoverArt(_musicPlayer.getPlaylist().getSongs().get(position).getAlbumId());
//		_coverFlow.setVisibility(View.GONE);
//		SDNowPlaylist playlist = _musicPlayer.getPlaylist();
//		playlist.setCurrentIndex(position);
//		_nowPlaylist = playlist;
//		_musicPlayer.setPlaylist(playlist);
//		_musicPlayer.play();
	}
	private OnSeekBarChangeListener _onProgressChangeListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if(_progressMonitor != null) {
				if(_musicPlayer != null) {
					_musicPlayer.setSeekPosition(seekBar.getProgress());
					if(_musicPlayer.isPlaying()) {
						_progressMonitor.startMonitor();
					}
				}
			}
			updatePlayerProgress();
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			if(_progressMonitor != null) {
				_progressMonitor.stopMonitor();
			}
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			updatePlayerProgress();
		}
	};
	
	private void updatePlayerProgress() {
		TextView elapse = (TextView) findViewById(R.id.elapse);
		TextView remain = (TextView) findViewById(R.id.remain);
		
		long pos = _musicPlayer.getSeekPosition();
		elapse.setText(Util.formatDurationFromMs(pos, false));
		remain.setText("-" + Util.formatDurationFromMs(_musicPlayer.getDuration() - pos, false));
	}
	
	private OnSeekBarChangeListener _onVolumeChangeListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if(fromUser) {
				setVolume(progress);
			}
		}
	};
	
	private int getVolume() {
		AudioManager audioManager = (AudioManager) getApplication()
				.getSystemService(Context.AUDIO_SERVICE);
		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	private int getMaxVolume() {
		AudioManager audioManager = (AudioManager) getApplication()
				.getSystemService(Context.AUDIO_SERVICE);
		return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	private void setVolume(int volume) {
		AudioManager audioManager = (AudioManager) getApplication()
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
				AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
		getVolumeSeekbar().setProgress(volume);
	}
	
	private BroadcastReceiver _statusReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action == SDPlayerService.AUDIO_COMPLETE) {
				onAudioCompletion((SDAudio) intent.getParcelableExtra(SDPlayerService.AUDIO));
			} else if (SDPlayerService.AUDIO_CHANGED.equalsIgnoreCase(action)) {
				onAudioChanged((SDAudio) intent
						.getParcelableExtra(SDPlayerService.AUDIO));
			} else if (SDPlayerService.PLAYER_STATE_CHANGED
					.equalsIgnoreCase(action)) {
				onPlayerStateChanged(intent.getIntExtra(
						SDPlayerService.PLAYER_STATE, SDPlayer.STATE_STOP));
			} else if (SDPlayerService.AUDIO_ERROR.equalsIgnoreCase(action)) {
				
			}
		}
	};
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent != null) {
			String action = intent.getAction();
			if(Intent.ACTION_VIEW.equals(action)) {
				Uri data = intent.getData();
				if(data != null) {
					int flags = intent.getFlags();
					if(0 == (flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY)) {
						SDNowPlaylist playlist = _library.makePlaylistFromUri(data);
						if(playlist != null) {
							openNowPlaylist(playlist);
						}
					}
				}
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		SDNowPlaylist playlist = null;
		playlist = _library.getNowPlaylist();
		boolean shouldReload = false;
		if (_application.getData(CODE_RELOAD) != null) {
			shouldReload = (Boolean) _application.getData(CODE_RELOAD);
		}
		if(shouldReload) {
			openNowPlaylist(playlist);
		} else {
			if(_musicPlayer.getCurrentAudio() == null) {
				finish();
			}
		}
		
		boolean isPlayStreaming = false;
		if(_application.getData(CODE_STREAMING) != null) {
			isPlayStreaming = (Boolean) _application.getData(CODE_STREAMING);
		}
		if(isPlayStreaming) {
			SDAudio audio = (SDAudio)_application.getData("audio");
			openStreamingAudio(audio);
		}
		//update UI
		updatePlayerProgress();
		updatePlayInfo();
		updatePlayButton(_musicPlayer.isPlaying());
		getVolumeSeekbar().setProgress(getVolume());
		getPlaySeekbar().setMax(_musicPlayer.getDuration());
		getPlaySeekbar().setProgress(_musicPlayer.getSeekPosition());
		if(_musicPlayer.getCurrentAudio() != null) {
			updateAudioInfo(_musicPlayer.getCurrentAudio());
			SDPlayerCoverFlowAdapter coverImageAdapter = new SDPlayerCoverFlowAdapter(
					this, _musicPlayer.getPlaylist().getSongs());
			_coverFlow.setAdapter(coverImageAdapter);
			_coverFlow.setSelection(_musicPlayer.getPlaylist().getCurrentIndex(), true);
			updateCoverArt(_musicPlayer.getCurrentAudio());
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter f = new IntentFilter();
		f.addAction(SDPlayerService.PLAYER_STATE_CHANGED);
		f.addAction(SDPlayerService.AUDIO_COMPLETE);
		f.addAction(SDPlayerService.AUDIO_CHANGED);
		f.addAction(SDPlayerService.AUDIO_ERROR);
		f.addAction(SDPlayerService.AUDIO_INFO_READY);
		registerReceiver(_statusReceiver, f);
		
		if(_musicPlayer.isPlaying()) {
			_progressMonitor.startMonitor();
			//update UI
			updateCoverArt(_musicPlayer.getCurrentAudio());
			updateAudioInfo(_musicPlayer.getCurrentAudio());
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(_statusReceiver);
		
		_progressMonitor.stopMonitor();
	}

	public void openNowPlaylist(SDNowPlaylist playlist) {
		if(_musicPlayer.getCurrentAudio() == null) {
			finish();
		} 
		if(playlist != null && playlist.getSongs() != null && playlist.getSongs().size() > 0) {
			_nowPlaylist = playlist;
			_musicPlayer.setPlaylist(_nowPlaylist);
			_musicPlayer.play();
			SDAudio currAudio = _musicPlayer.getCurrentAudio();
			onAudioChanged(currAudio);
		} else {
			if (_musicPlayer.getCurrentAudio() != null) {
				_nowPlaylist = _musicPlayer.getPlaylist();
				_musicPlayer.play();
				onAudioChanged(_musicPlayer.getCurrentAudio());
			}
		}
	}
	
	public void openStreamingAudio(SDAudio audio) {
		_musicPlayer.setAudio(audio);
	}
	
	/* button action */
	
	public void onPlayClicked(View v) {
		if(_musicPlayer.isPlaying()) {
			_musicPlayer.pause();
		} else {
			_musicPlayer.play();
		}
	}
	
	public void onFwdClicked(View v) {
		_musicPlayer.next();
	}
	
	public void onPrevClicked(View v) {
		_musicPlayer.prev();
	}
	
	public void onShuffleClicked(View v) {
		boolean isShuffle = _musicPlayer.isShuffle();
		setShuffle(!isShuffle);
	}
	
	public void onLoopClicked(View v) {
		int mode = _musicPlayer.getLoopMode();
		switch (mode) {
		case SDPlayerService.LOOP_MODE_NONE:
			mode = SDPlayerService.LOOP_MODE_ONE;
			break;
		case SDPlayerService.LOOP_MODE_ONE:
			mode = SDPlayerService.LOOP_MODE_ALL;
			break;
		case SDPlayerService.LOOP_MODE_ALL:
			mode = SDPlayerService.LOOP_MODE_NONE;
			break;
		}
		_musicPlayer.setLoopMode(mode);
		setLoopMode(mode);
	}
	
	/*end button action*/
	
	private SeekBar getPlaySeekbar() {
		return (SeekBar) findViewById(R.id.seek_bar_progress);
	}
	
	private SeekBar getVolumeSeekbar() {
		return (SeekBar) findViewById(R.id.seek_bar_volume);
	}
	
	private void onAudioCompletion(SDAudio audio) {
		
	}
	
	private void onAudioChanged(SDAudio newAudio) {
		//update UI
		updatePlayerProgress();
		updatePlayButton(_musicPlayer.isPlaying());
		getPlaySeekbar().setMax(_musicPlayer.getDuration());
		getPlaySeekbar().setProgress(_musicPlayer.getSeekPosition());
		updateAudioInfo(newAudio);
		updateLyric(newAudio);
		updateCoverArt(newAudio);
	}
	
	private void onPlayerStateChanged(int newState) {
		if(newState == SDPlayer.STATE_PLAYING) {
			updatePlayButton(true);
			if(_progressMonitor != null) {
				_progressMonitor.startMonitor();
			}
		}
		else if(newState == SDPlayer.STATE_PAUSE) {
			updatePlayButton(false);
			if(_progressMonitor != null) {
				_progressMonitor.startMonitor();
			}
		}
		else {
			updatePlayButton(false);
			getPlaySeekbar().setProgress(0);
			if(_progressMonitor != null) {
				_progressMonitor.startMonitor();
			}
		}
	}
	/*update UI*/
	//update lyric
	private void updateLyric(SDAudio audio) {
		_getLyric.getLyric(audio.getTitle(), audio.getArtist());
	}
	//update cover_art
	private void updateCoverArt(SDAudio audio) {
		_lyricView._coverArt.getCoverArt(audio.getAlbumId());
	}
	//update play_button
	private void updatePlayButton(boolean isPlaying) {
		Button playBtn = (Button) findViewById(R.id.button_play);
		Drawable playDrw = getResources().getDrawable(
				R.drawable.music_play_control_play_btn_selector);
		Drawable pauseDrw = getResources().getDrawable(
				R.drawable.music_play_control_pause_btn_selector);

		if (isPlaying) {
			playBtn.setBackgroundDrawable(pauseDrw);
			//playBtn.setCompoundDrawablesWithIntrinsicBounds(null, pauseDrw,
					//null, null);
		} else {
			playBtn.setBackgroundDrawable(playDrw);
			//playBtn.setCompoundDrawablesWithIntrinsicBounds(null, playDrw,
					//null, null);
		}
	}
	//update header
	private void updateHeader(SDAudio audio) {
		TextView songName = (TextView) findViewById(R.id.song_name);
		TextView artist = (TextView) findViewById(R.id.artist);
		
		songName.setText(audio.getTitle());
		artist.setText(audio.getArtist());
	}
	//update player info : shuffle , repeat
	private void updatePlayInfo() {
		setLoopMode(_musicPlayer.getLoopMode());
		setShuffle(_musicPlayer.isShuffle());
	}
	//set loop mode
	private void setLoopMode(int mode) {
		ImageView loop = (ImageView) findViewById(R.id.loop);
		switch (mode) {
		case SDPlayerService.LOOP_MODE_NONE:
			loop.setImageResource(R.drawable.music_play_menu_rep_1_off);
			break;
		case SDPlayerService.LOOP_MODE_ONE:
			loop.setImageResource(R.drawable.music_play_menu_rep_1_on);
			break;
		case SDPlayerService.LOOP_MODE_ALL:
			loop.setImageResource(R.drawable.music_play_menu_rep_all_on);
			break;
		}
	}
	//set shuffle
	private void setShuffle(boolean isShuffle) {
		ImageView shuffle = (ImageView) findViewById(R.id.shuffle);
		if(isShuffle) {
			shuffle.setImageResource(R.drawable.music_play_menu_shuffle_on);
		} else {
			shuffle.setImageResource(R.drawable.music_play_menu_shuffle_off);
		}
		_musicPlayer.setShuffle(isShuffle);
	}
	//update rating, current position in playlist
	private void updateAudioInfo(SDAudio audio) {
		updateHeader(audio);
		//update sequence index
		TextView songPosition = (TextView) findViewById(R.id.song_position);
		songPosition.setText(_musicPlayer.getSequenceIndex() + 1 + "/" + _musicPlayer.getIdsQueue().length);
	}
	/*key event handle*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			setVolume(Math.min(getMaxVolume(), getVolume() + 1));
			return true;
		}
		if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			setVolume(Math.max(0, getVolume() - 1));
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if(_sdcardUnMountReceiver != null) {
			unregisterReceiver(_sdcardUnMountReceiver);
		}
		if(_getLyric != null) {
			_getLyric.stop();
		}
		if(_progressMonitor != null) {
			_progressMonitor.unregisterReceiver();
		}
		super.onDestroy();
	}

	@Override
	public void onComplete(Service service, ServiceRespone result) {
		if(result.isSuccess()) {
			if(result.getAction() == ServiceAction.ActionGetLyric) {
				String lyric = (String) result.getData().toString();
				if(lyric.equals("")) {
					_lyricView.renderNoLyric();
				} else {
					_lyricView.renderLyric(_musicPlayer.getCurrentAudio(), lyric);
				}
			}
		} else {
			_lyricView.renderNoLyric();
		}
	}
}
