package gnt.sd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;

public class SDProgressMonitor extends Handler {

	private SDIMusicPlayerImpl _player;
	private SeekBar _seekBar;
	private boolean _isMonitoring;
	private Context _context;
	
	private int _buffer;
	
	private static final int MONITOR_START = 1;
	private static final int MONITOR_STOP = 2;
	private static final int MONITOR_UPDATE = 3;
	
	public SDProgressMonitor(Context context, SDIMusicPlayerImpl player, SeekBar seekBar) {
		super();
		_player = player;
		_seekBar = seekBar;
		_context = context;
		IntentFilter f = new IntentFilter();
		f.addAction(SDPlayerService.BUFFER_UPDATE);
		_context.registerReceiver(_bufferReceiver, f);
	}
	
	BroadcastReceiver _bufferReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			int percent = intent.getIntExtra(SDPlayerService.BUFFER, 0);
			setBuffer(percent);
		}
	};
	
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case MONITOR_START:
		case MONITOR_UPDATE:
			processUpdate();
			break;
		case MONITOR_STOP:
			stopInternalMonitor();
			break;
		}
		super.handleMessage(msg);
	}
	
	private void processUpdate() {
		update();
		sendEmptyMessageDelayed(MONITOR_UPDATE, 500);
	}

	private void update() {
		synchronized (_seekBar) {
			int max = _player.getDuration();
			_seekBar.setMax(max);
			int pos = _player.getSeekPosition();
			_seekBar.setProgress(pos);
//			_seekBar.setSecondaryProgress(getBuffer());
		}
	}
	
	private void stopInternalMonitor() {
		while (hasMessages(MONITOR_UPDATE)) {
			removeMessages(MONITOR_UPDATE);
		}
		_isMonitoring = false;
	}
	
	private void setBuffer(int percent) {
		int posBuffer = percent;//(int)((percent / 100.0) * _player.getDuration());
		_buffer = posBuffer;
	}
	
	private int getBuffer() {
		return _buffer;
	}
	
	public void startMonitor() {
		sendEmptyMessage(MONITOR_START);
		_isMonitoring = true;
	}
	
	public void stopMonitor() {
		sendEmptyMessage(MONITOR_STOP);
	}
	
	public boolean isMonitoring() {
		return _isMonitoring;
	}
	
	public void unregisterReceiver() {
		_context.unregisterReceiver(_bufferReceiver);
	}
}


