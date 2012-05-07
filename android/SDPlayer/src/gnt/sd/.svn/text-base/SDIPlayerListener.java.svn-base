package gnt.sd;

import gnt.sd.model.SDAudio;

public interface SDIPlayerListener {
	
	public void onPlayerStateChanged(int oState, int nState);
	
	public void onAudioError(SDAudio audio);
	public void onAudioChanged(SDAudio audio);
	public void onAudioComplete(SDAudio audio);
	
	public void onPlayerCompleted();
	public void onPlayerError(String msg);
	
	public void onPlaylistError(String msg);
	public void onPlaylistComplete();
	public void onPlaylistChanged();
	
	public void onBufferingUpdate(int progress);
}
