package gnt.sd;

import gnt.sd.model.SDAudio;
import gnt.sd.model.SDMusic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.RemoteViews;

public class SDPlayerService extends Service {
	
	public class LocalBinder extends Binder {
		SDPlayerService getService() {
			return SDPlayerService.this;
		}
	}
	
	private final Binder _binder = new LocalBinder();
	
	private static final String DEBUG_TAG = "[PlayerService]";
	private SDPlayer _player;
	private boolean _isBound = false;
	private ServiceTub _serviceTub = new ServiceTub(this);
	private SDMusic _currentInfo;
	private SDIGetSongInfoListener _getSongInfoListener;
	
	private SDIGetSongInfoListener.onGetSongInfoCompletedListener _onGetSongInfoCompletedListener = new SDIGetSongInfoListener.onGetSongInfoCompletedListener() {
		
		@Override
		public void onGetSongInfoCompleted(SDMusic info) {
			_currentInfo = info;
			Intent i = new Intent(AUDIO_INFO_READY);
			i.putExtra(INFO, info);
			sendBroadcast(i);
		}
	};
	
	private SDIPlayerListener _listener = new SDIPlayerListener() {
		
		@Override
		public void onPlaylistError(String msg) {
			Intent i = new Intent(PLAYLIST_ERROR);
			i.putExtra(ERROR, msg);
			sendBroadcast(i);
		}
		
		@Override
		public void onPlaylistComplete() {
			if(getLoopMode() == LOOP_MODE_NONE) {
				gotoIdleState();
			} 
			Intent i = new Intent(PLAYLIST_COMPLETE);
			sendBroadcast(i);
		}
		
		@Override
		public void onPlaylistChanged() {
			saveQueue(true);
			Intent i = new Intent(PLAYLIST_CHANGED);
			sendBroadcast(i);
		}
		
		@Override
		public void onPlayerStateChanged(int oState, int nState) {
			
			if(nState == SDPlayer.STATE_PLAYING) {
				showNotification();
			}
			
			Intent i = new Intent(PLAYER_STATE_CHANGED);
			i.putExtra(PLAYER_STATE, nState);
			sendBroadcast(i);
		}
		
		@Override
		public void onPlayerError(String msg) {
			Intent i = new Intent(PLAYER_ERROR);
			i.putExtra(ERROR, msg);
			sendBroadcast(i);
		}
		
		@Override
		public void onPlayerCompleted() {
			
		}
		
		@Override
		public void onAudioError(SDAudio audio) {
			gotoIdleState();
			Intent i = new Intent(AUDIO_ERROR);
			i.putExtra(ERROR, audio);
			sendBroadcast(i);
		}
		
		@Override
		public void onAudioComplete(SDAudio audio) {
			Intent i = new Intent(AUDIO_COMPLETE);
			i.putExtra(AUDIO, audio);
			sendBroadcast(i);
			if(getLoopMode() == LOOP_MODE_ONE) {
				replay();
			} else {
				SDAudio next = getNextSong();
				if(next == null) {
					_listener.onPlaylistComplete();
				}
				boolean isContinue = canGoForward();
				if(isContinue) {
					if(prepareNext(LOOP_MODE_ALL)) {
						_player.play();
					}
				}
			}
			
		}
		
		@Override
		public void onAudioChanged(SDAudio audio) {
			Intent i = new Intent(AUDIO_CHANGED);
			i.putExtra(AUDIO, audio);
			sendBroadcast(i);
		}

		@Override
		public void onBufferingUpdate(int progress) {
			Intent i = new Intent(BUFFER_UPDATE);
			i.putExtra(BUFFER, progress);
			sendBroadcast(i);
		}
	};
	
	private int _serviceId;
	private boolean _isShuffle;
	private int _loopMode;
	
	public static final int LOOP_MODE_NONE = 0;
	public static final int LOOP_MODE_ALL = 1;
	public static final int LOOP_MODE_ONE = 2;
	//set up playlist
	private List<Long> _songList = new ArrayList<Long>();
	private List<Long> _sequence = new ArrayList<Long>();
	private int _sequenceIndex;
	
	private static final String PLAYER_SERVICE_PREFS = "player_prefs";
	private SharedPreferences _prefs;
	
	private static final int PLAYER_SERVICE_STATUS = 1;
	//Filter
	public static final String AUDIO = "audio";
	public static final String PLAYLIST = "playlist";
	public static final String PLAYER_STATE = "playerstate";
	public static final String ERROR = "error";
	public static final String INFO = "info";
	public static final String BUFFER = "buffer";
	
	public static final String AUDIO_ERROR = "gnt.sd.player.audio_error";
	public static final String AUDIO_CHANGED = "gnt.sd.player.audio_changed";
	public static final String AUDIO_COMPLETE = "gnt.sd.player.audio_complete";
	public static final String PLAYER_COMPLETE = "gnt.sd.player.player_complete";
	public static final String PLAYER_ERROR = "gnt.sd.player.player_error";
	public static final String PLAYLIST_ERROR = "gnt.sd.player.playlist_error";
	public static final String PLAYLIST_COMPLETE = "gnt.sd.player.playlist_complete";
	public static final String PLAYLIST_CHANGED = "gnt.sd.player.playlist_changed";
	public static final String PLAYER_STATE_CHANGED = "gnt.sd.player.player_state_change";
	public static final String AUDIO_INFO_READY = "gnt.sd.player.audio_info_ready";
	public static final String BUFFER_UPDATE = "gnt.sd.player.buffer_update";
	
	private final char hexdigits[] = new char[] { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	
	private BroadcastReceiver _unmountedReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			_player.stop();
		}
	};
	
	public SDPlayerService() {
		_player = new SDPlayer(this);
		_player.setPlayerListener(_listener);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		_prefs = getSharedPreferences(PLAYER_SERVICE_PREFS, MODE_PRIVATE);
		reloadQueue();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		registerReceiver(_unmountedReceiver, filter);
	}


	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		_serviceId = startId;
	}

	@Override
	public void onDestroy() {
		saveQueue(true);
		if(_unmountedReceiver != null) {
			unregisterReceiver(_unmountedReceiver);
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		_isBound = true;
		return _binder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		_isBound = false;
		saveQueue(true);
		if(_player.isPlaying()) {
			return true;
		}
		if(canGoForward()) {
			return true;
		}
		stopSelf(_serviceId);
		return super.onUnbind(intent);
	}
	//show when play service in status service of device
	private void showNotification() {
		Notification status = new Notification();
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification);
		views.setImageViewResource(R.id.icon, R.drawable.stat_notify_musicplayer);
		SDAudio audio = getCurrentSong();
		if(audio != null) {
			String artist = audio.getArtist();
			views.setTextViewText(R.id.trackname, audio.getTitle());
			if (artist == null || artist.equals("<unknown>")) {
				artist = "unknown";
			}
			String album = audio.getAlbum();
			if (album == null || album.equals("<unknown>")) {
				album = "unknown";
			}
			views.setTextViewText(
					R.id.artistalbum,
					getString(R.string.notification_artist_album, artist, album));

			status.flags |= Notification.FLAG_ONGOING_EVENT;
			status.icon = R.drawable.stat_notify_musicplayer;
		}
		status.contentView = views;
		status.contentIntent = PendingIntent.getActivity(this, 0, new Intent(
				this, SDPlayerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.notify(PLAYER_SERVICE_STATUS, status);
	}
	
	public SDAudio getCurrentSong() {
		long Id = getCurrentSongId();
		if(Id == -1) {
			return null;
		} else {
			SDAudio audio = getLibrary().getAudioById(Id);
			return audio;
		}
	}

	public long getCurrentSongId() {
		if(_sequence == null || _sequence.isEmpty()) {
			return -1;
		} else {
			return _sequence.get(_sequenceIndex);
		}
	}

	public void saveQueue(boolean full) {
		Log.i(DEBUG_TAG, "Save queue");
		Editor ed = _prefs.edit();
		// long start = System.currentTimeMillis();
		if (full) {
			StringBuilder q = new StringBuilder();

			// The current playlist is saved as a list of "reverse hexadecimal"
			// numbers, which we can generate faster than normal decimal or
			// hexadecimal numbers, which in turn allows us to save the playlist
			// more often without worrying too much about performance.
			// (saving the full state takes about 40 ms under no-load conditions
			// on the phone)
			List<Long> songs = _sequence;
			int len = songs.size();
			for (int i = 0; i < len; i++) {
				long n = songs.get(i);
				if (n < 0) {
					continue;
				} else if (n == 0) {
					q.append("0;");
				} else {
					while (n != 0) {
						int digit = (int) (n & 0xf);
						n >>>= 4;
						q.append(hexdigits[digit]);
					}
					;
					q.append(";");
				}
			}
			// Log.i("@@@@ service", "created queue string in " +
			// (System.currentTimeMillis() - start) + " ms");
			ed.putString("queue", q.toString());
			if (_isShuffle) {
				// In shuffle mode we need to save the history too
				songs = _songList;
				len = songs.size();
				q.setLength(0);
				for (int i = 0; i < len; i++) {
					long n = songs.get(i);
					if (n == 0) {
						q.append("0;");
					} else {
						while (n != 0) {
							int digit = (int) (n & 0xf);
							n >>>= 4;
							q.append(hexdigits[digit]);
						}
						q.append(";");
					}
				}
				ed.putString("history", q.toString());
			}
		}

		ed.putInt("curpos", _sequenceIndex);
		ed.putInt("seekpos", _player.getPosition());

		ed.putInt("repeatmode", _loopMode);
		ed.putBoolean("shufflemode", _isShuffle);
		ed.commit();
	}

	public void reloadQueue() {
		Log.i(DEBUG_TAG, "Reload queue");
		String q = null;
		q = _prefs.getString("queue", "");
		int qlen = q != null ? q.length() : 0;
		if (qlen > 1) {
			_sequence = new ArrayList<Long>();
			long n = 0;
			int shift = 0;
			for (int i = 0; i < qlen; i++) {
				char c = q.charAt(i);
				if (c == ';') {
					_sequence.add(n);
					n = 0;
					shift = 0;
				} else {
					if (c >= '0' && c <= '9') {
						n += ((c - '0') << shift);
					} else if (c >= 'a' && c <= 'f') {
						n += ((10 + c - 'a') << shift);
					} else {
						// bogus playlist data
						break;
					}
					shift += 4;
				}
			}

			int pos = _prefs.getInt("curpos", 0);
			if (pos < 0 || pos >= _sequence.size()) {
				// The saved playlist is bogus, discard it
				return;
			}

			_sequenceIndex = pos;

			int repmode = _prefs.getInt("repeatmode", LOOP_MODE_NONE);
			if (repmode != LOOP_MODE_ALL && repmode != LOOP_MODE_ONE) {
				repmode = LOOP_MODE_NONE;
			}
			_loopMode = repmode;

			boolean shufmode = _prefs.getBoolean("shufflemode", false);
			if (shufmode) {
				// in shuffle mode we need to restore the history too
				q = _prefs.getString("history", "");
				qlen = q != null ? q.length() : 0;
				if (qlen > 1) {
					n = 0;
					shift = 0;
					_songList = new ArrayList<Long>();
					for (int i = 0; i < qlen; i++) {
						char c = q.charAt(i);
						if (c == ';') {
							_songList.add(n);
							n = 0;
							shift = 0;
						} else {
							if (c >= '0' && c <= '9') {
								n += ((c - '0') << shift);
							} else if (c >= 'a' && c <= 'f') {
								n += ((10 + c - 'a') << shift);
							} else {
								// bogus history data
								_songList.clear();
								break;
							}
							shift += 4;
						}
					}
				}
			} else {
				_songList = _sequence;
			}

			int seekpos = _prefs.getInt("seekpos", 0);
			_player.setAudio(getLibrary().getAudioById(
					_sequence.get(_sequenceIndex)));
			seekTo(seekpos >= 0 && seekpos < getDuration() ? seekpos : 0);
			Log.d("PlayerService", "restored queue, currently at position "
					+ getPosition() + "/" + getDuration() + " (requested "
					+ seekpos + ")" + "state" + getState());
		}
	}
	
	
	public SDLibrary getLibrary() {
		return ((SDApplication) getApplication()).getLibrary();
	}

	public boolean isBound() {
		return _isBound;
	}
	
	public int getState() {
		return _player.getState();
	}
	
	public boolean isPlaying() {
		return _player.isPlaying();
	}
	
	public void setLooping(boolean isLooping) {
		_player.setLooping(isLooping);
	}
	
	public boolean getLooping() {
		return _player.getLooping();
	}
	
	public void setAudio(SDAudio audio) {
		_player.setAudio(audio);
	}
	
	public void play() {
		_player.play();
		setForeground(true);
	}
	
	public void replay() {
		_player.replay();
	}
	
	public void pause() {
		_player.pause();
		gotoIdleState();
	}
//	
//	public void setVolume(float vol) {
//		_player.setVolume(vol);
//	}
	
	public void seekTo(int position) {
		_player.seekTo(position);
	}
	
	public int getDuration() {
		return _player.getDuration();
	}
	
	public int getPosition() {
		return _player.getPosition();
	}
	
	public void stop() {
		_player.stop();
		gotoIdleState();
	}
	
	
	public void setShuffle(boolean _isShuffle) {
		if(this._isShuffle != _isShuffle) {
			this._isShuffle = _isShuffle;
			calSequeue(_isShuffle, _sequenceIndex);
		}
		saveQueue(true);
	}

	public void calSequeue(boolean isShuffle, int start) {
		if(_songList == null) {
			return;
		}
		if (isShuffle) {
			_sequence = shuffle(_songList, start);
			_sequenceIndex = 0;
		} else {
			_sequence = _songList;
			_sequenceIndex = start;
		}
	}

	public List<Long> shuffle(List<Long> list, int start) {
		int quantity = list.size();
		List<Long> newList = new ArrayList<Long>(quantity);
		for (int i = 0; i < quantity; i++) {
			newList.add(list.get(i));
		}
		if (!list.isEmpty()) {
			Random rand = new Random(System.currentTimeMillis());
			// set starting of this sequence
			swap(newList, 0, start);
			for (int i = 1; i < quantity; i++) {
				int j = rand.nextInt(i) + 1;
				swap(newList, i, j);
			}
		}
		return newList;
	}

	public void swap(List<Long> list, int a, int b) {
		long va = list.get(a);
		long vb = list.get(b);
		list.set(a, vb);
		list.set(b, va);
	}
	
	public long getPreviousSongId() {
		int newSeqIdx = _sequenceIndex;
		if (_sequence.isEmpty()) {
			return - 1;
		}
		if(_sequenceIndex > 0) {
			newSeqIdx --;
		} else {
			newSeqIdx -= 1;
		}
		
		if(newSeqIdx == -1) {
			return -1;
		} else {
			long Id = _sequence.get(newSeqIdx);
			return Id;
		}
		
	}
	
	public boolean canGoForward() {
		if(_songList.isEmpty()) {
			return false;
		}
		
		if(_loopMode != LOOP_MODE_NONE) {
			return true;
		}
		
		return _sequenceIndex < _sequence.size() - 1;
	}
	
	public boolean canGoBackward() {
		if(_songList.isEmpty()) {
			return false;
		}
		
		if(_loopMode != LOOP_MODE_NONE) {
			return true;
		}
		
		return _sequenceIndex > 0;
	}
	
	public void gotoIdleState() {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(PLAYER_SERVICE_STATUS);
		setForeground(false);
		saveQueue(false);
		if (!_isBound) {
			if (!canGoForward()) {
				stopSelf();
			}
		}
	}
	
	public SDAudio getPreviousSong() {
		long Id = getPreviousSongId();
		if(Id == -1) {
			return null;
		} else {
			SDAudio audio = getLibrary().getAudioById(Id);
			return audio;
		}
	}
	
	public long getLastSongId() {
		if(_sequence.isEmpty()) {
			return -1;
		} else {
			return _sequence.get(_sequence.size() - 1);
		}
		
	}
	
	public SDAudio getLastSong() {
		long Id = getLastSongId();
		if(Id == -1) {
			return null;
			
		} else {
			return getLibrary().getAudioById(Id);
		}
	}
	
	public long getFirstSongId() {
		if(_sequence.isEmpty()) {
			return -1;
		} else {
			return _sequence.get(0);
		}
	}
	
	public SDAudio getFirstSong() {
		long Id = getFirstSongId();
		if(Id == -1) {
			return null;
		} else {
			return getLibrary().getAudioById(Id);
		}
	}
	
	public SDAudio getNextSong() {
		
		long Id = getNextSongId();
		
		if(Id == -1) {
			return null;
		} else {
			SDAudio audio = getLibrary().getAudioById(Id);
			return audio;
		}
	}
	
	public long getNextSongId() {
		int newSeqIdx = _sequenceIndex;
		if (_sequence.isEmpty()) {
			return -1;
		}

		if (_sequenceIndex < _sequence.size() - 1) {
			newSeqIdx++;
		} else {
			newSeqIdx = -1;
		}

		if (newSeqIdx == -1) {
			return -1;
		} else {
			long songId = _sequence.get(newSeqIdx);
			return songId;
		}
	}
	
	public boolean setIdsSequence(long[] Ids, int startIdx) {
		if(Ids == null) {
			return false;
		}
		_player.stop();
		_songList = new ArrayList<Long>();
		for(int i = 0; i < Ids.length;i++) {
			_songList.add(Ids[i]);
		}
		setSequenceIndex(startIdx);
		_listener.onPlaylistChanged();
		return true;
	}
	
	public boolean isShuffle() {
		return _isShuffle;
	}


	public void setLoopMode(int _loopMode) {
		this._loopMode = _loopMode;
	}

	public int getLoopMode() {
		return _loopMode;
	}

	public void setSequenceIndex(int _sequenceIndex) {
		calSequeue(isShuffle(), _sequenceIndex);
		SDAudio audio = getCurrentSong();
		_player.setAudio(audio);
		_listener.onAudioChanged(audio);
	}

	public int getSequenceIndex() {
		return _sequenceIndex;
	}

	public SDMusic getCurrentInfo() {
		return _currentInfo;
	}
	
	public long[] getIdsSequence() {
		if(_sequence == null) {
			return null;
		}
		long Ids[] = new long[_sequence.size()];
		int i;
		for(i = 0;i < Ids.length;i++) {
			Ids[i] = _sequence.get(i);
		}
		return Ids;
	}
	
	public boolean prepareBack(int loopMode) {
		if(_sequence == null) {
			return false;
		}
		SDAudio audio = getPreviousSong();
		if(audio == null) {
			if(loopMode == LOOP_MODE_ALL) {
				audio = getLastSong();
				if(audio != null) {
					_player.setAudio(audio);
					_sequenceIndex = _sequence.size() - 1;
					return true;
				}
			}
		} else {
			_player.setAudio(audio);
			_sequenceIndex--;
			return true;
		}
		return false;
	}
	
	public void prev() {
		boolean isPlay = false;
		if(isPlaying()) {
			isPlay = true;
		}
		prepareBack(LOOP_MODE_ALL);
		if(isPlay) {
			play();
		}
	}
	
	public boolean prepareNext(int loopMode) {
		if(_sequence == null) {
			return false;
		}
		if(loopMode == LOOP_MODE_ONE) {
			replay();
			return true;
		}
		SDAudio audio = getNextSong();
		if(audio == null) {
			if(loopMode == LOOP_MODE_ALL) {
				audio = getFirstSong();
				if(audio != null) {
					_player.setAudio(audio);
					_sequenceIndex = 0;
					return true;
				}
			}
		} else {
			_player.setAudio(audio);
			_sequenceIndex++;
			return true;
		}
		return false;
	}
	
	public void next() {
		boolean isPlay = false;
		if(isPlaying()) {
			isPlay = true;
		}
		prepareNext(LOOP_MODE_ALL);
		if(isPlay) {
			play();
		}
	}
	
	public MediaPlayer getMediaPlayer() {
		return this._player.getMediaPlayer();
	}
	
	public class ServiceTub extends IPlayerService.Stub {

		SDPlayerService _playerService;
		
		public ServiceTub(SDPlayerService playerService) {
			_playerService = playerService;
		}
		
		@Override
		public int getState() throws RemoteException {
			return _playerService.getState();
		}

		@Override
		public boolean isPlaying() throws RemoteException {
			return _playerService.isPlaying();
		}

		@Override
		public void setLooping(boolean isLooping) throws RemoteException {
			_playerService.setLooping(isLooping);
		}

		@Override
		public boolean getLooping() throws RemoteException {
			return _playerService.getLooping();
		}

		@Override
		public void play() throws RemoteException {
			_playerService.play();
		}

		@Override
		public void replay() throws RemoteException {
			_playerService.replay();
		}

		@Override
		public void pause() throws RemoteException {
			_playerService.pause();
		}

		@Override
		public void setVolume(float vol) throws RemoteException {
			//_playerService.setVolume(vol);
		}

		@Override
		public void seekTo(int position) throws RemoteException {
			_playerService.seekTo(position);
		}

		@Override
		public int getDuration() throws RemoteException {
			return _playerService.getDuration();
		}

		@Override
		public void stop() throws RemoteException {
			_playerService.stop();
		}

		@Override
		public int getPosition() throws RemoteException {
			return _playerService.getPosition();
		}

		@Override
		public long getCurrentAudioId() throws RemoteException {
			Log.i(DEBUG_TAG, "GetCurrentAudio");
			return _playerService.getCurrentSongId();
		}

		@Override
		public boolean isShuffle() throws RemoteException {
			return _playerService.isShuffle();
		}

		@Override
		public void setShuffle(boolean isEnable) throws RemoteException {
			_playerService.setShuffle(isEnable);
		}

		@Override
		public int getLoopMode() throws RemoteException {
			return _playerService.getLoopMode();
		}

		@Override
		public void setLoopMode(int mode) throws RemoteException {
			_playerService.setLoopMode(mode);
		}

		@Override
		public int getSequenceIndex() throws RemoteException {
			return _playerService.getSequenceIndex();
		}

		@Override
		public long[] getIdsSequence() throws RemoteException {
			return _playerService.getIdsSequence();
		}

		@Override
		public void setIdsSequence(long[] ids, int startIdx)
				throws RemoteException {
			_playerService.setIdsSequence(ids, startIdx);
		}

		@Override
		public void setSequenceIndex(int startIdx) throws RemoteException {
			_playerService.setSequenceIndex(startIdx);
		}

		@Override
		public long[] getIdsList() throws RemoteException {
			return _playerService.getIdsSequence();
		}

		@Override
		public SDMusic getCurrentInfo() throws RemoteException {
			return _playerService.getCurrentInfo();
		}

		@Override
		public void prev() throws RemoteException {
			_playerService.prev();
		}

		@Override
		public void next() throws RemoteException {
			_playerService.next();
		}

		@Override
		public boolean canNext() throws RemoteException {
			return _playerService.canGoForward();
		}

		@Override
		public boolean canPrev() throws RemoteException {
			return _playerService.canGoBackward();
		}
		
	}
}
