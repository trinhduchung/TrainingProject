package gnt.sd;

import android.media.MediaPlayer;
import gnt.sd.model.SDAudio;

public interface SDIMusicPlayer {
	public void play();
	public void stop();
	public void pause();
	public void replay();
	public void next();
	public void prev();
	public boolean isPlaying();
	public void setLoopMode(int mode);
	public int getLoopMode();
	public int getSeekPosition();
	public void setSeekPosition(int position);
	public int getDuration();
	public boolean isShuffle();
	public void setShuffle(boolean isShuffle);
	public boolean canPrev();
	public boolean canNext();
	public SDAudio getCurrentAudio();
	public long[] getIdsQueue();
	public boolean isPlaylistEmpty();
	public void setSequenceIndex(int idx);
	public int getSequenceIndex();
	public void setPlaylist(SDNowPlaylist playlist);
	public SDNowPlaylist getPlaylist();
	public void setAudio(SDAudio audio);
	public MediaPlayer getMediaPlayer();
}
