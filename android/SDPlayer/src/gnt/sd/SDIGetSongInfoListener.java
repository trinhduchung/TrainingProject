package gnt.sd;

import gnt.sd.model.SDAudio;
import gnt.sd.model.SDMusic;

public interface SDIGetSongInfoListener {
	
	public interface onGetSongInfoCompletedListener {
		public void onGetSongInfoCompleted(SDMusic info);
	}
	
	public void getSongInfo(SDAudio audio, onGetSongInfoCompletedListener callback);
}
