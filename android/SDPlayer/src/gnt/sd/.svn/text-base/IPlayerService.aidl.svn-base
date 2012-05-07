package gnt.sd;

import gnt.sd.model.SDMusic;

interface IPlayerService {
	int getState();
	boolean isPlaying();
	void setLooping(boolean isLooping);
	boolean getLooping();
	void play();
	void replay();
	void pause();
	void prev();
	void next();
	void setVolume(float vol);
	void seekTo(int position);
	int getDuration();
	int getPosition();
	void stop();
	long getCurrentAudioId();
	boolean isShuffle();
	void setShuffle(boolean isEnable);
	int getLoopMode();
	void setLoopMode(int mode);
	int getSequenceIndex();
  long[] getIdsSequence();
  void setIdsSequence(in long[] ids, int startIdx);
  void setSequenceIndex(int startIdx);
  long[] getIdsList();
  SDMusic getCurrentInfo();
  boolean canNext();
  boolean canPrev();
}