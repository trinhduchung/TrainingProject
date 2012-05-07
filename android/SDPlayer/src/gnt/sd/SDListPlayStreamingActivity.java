package gnt.sd;

import gnt.sd.SDListPlayStreamingActivity.ListItemAdapter.RowViewHolder;
import gnt.sd.model.SDAudio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SDListPlayStreamingActivity extends Activity implements DialogListener{
	
	List<SDAudio> _list = new ArrayList<SDAudio>();
	ListItemAdapter _adapter;
	ListView _lv;
	Context _context = this;
	SDStreamingMediaPlayer _streamPlayer;
	RowViewHolder[] _listCached;
	TextView _song;
	TextView _artist;
	String _strArtist;
	
	boolean _isConnecting;
	HttpURLConnection _connection;
	private final String _folderName = "Audio";
	
	private DialogListener _dialogListener;
	private ProgressDialog _progressDialog;
	public boolean _bCancelDownload = false;
	public boolean _isShowDialog = false;
	
	private String _name;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_play_streaming);
		_list = (List<SDAudio>) SDApplication.Instance().getData("list");
		_strArtist = SDApplication.Instance().getData("artist").toString();
		_adapter = new ListItemAdapter(_list);
		_lv = (ListView) findViewById(R.id.list);
		_lv.setAdapter(_adapter);
		_listCached = new RowViewHolder[(_list == null) ? 0 : _list.size()];
		_song = (TextView) findViewById(R.id.song_name);
		_artist = (TextView) findViewById(R.id.artist);
		_song.setText("Danh sách");
		_artist.setText((_strArtist == null ? "" : _strArtist));
		
		_dialogListener = this;
		_progressDialog = new ProgressDialog(this);
        _progressDialog.setCancelable(true);
        _progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        _progressDialog.setOnCancelListener(new OnCancelListener() {
        	public void onCancel(DialogInterface dialog) {
				_bCancelDownload = true;
			}
        }); 
        
        IntentFilter f = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
		f.addAction(Intent.ACTION_MEDIA_MOUNTED);
		f.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		f.addDataScheme("file");
		registerReceiver(_receiver, f);  
	}

	class ListItemAdapter extends BaseAdapter {

		List<SDAudio> _listAudio = new ArrayList<SDAudio>();

		public ListItemAdapter(List<SDAudio> list) {
			_listAudio = list;
		}

		@Override
		public int getCount() {
			if (_listAudio != null) {
				return _listAudio.size();
			}
			return 0;
		}

		@Override
		public SDAudio getItem(int position) {
			return _listAudio.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (_listCached[position] == null) {
				final RowViewHolder row = new RowViewHolder(
						SDListPlayStreamingActivity.this);
				final SDAudio audio = _listAudio.get(position);
				row._title.setText(audio.getTitle());
				row._play.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						SDApplication.Instance().putData("audio", audio);
						SDApplication.Instance().putData(SDPlayerActivity.CODE_STREAMING, true);
						//startActivity(new Intent(SDListPlayStreamingActivity.this, SDPlayerActivity.class));
						if (_streamPlayer != null) {
							_streamPlayer.interrupt();
						}
						_streamPlayer = new SDStreamingMediaPlayer(_context,
								row._play, row._progress);
						try {
							_streamPlayer
									.startStreaming(
											audio, 216);
							Log.d("SDListPlayStreaming","Path="+ audio.getPath());
							Log.d("SDListPlayStreaming","TotalLenght="+ audio.getSize());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				row._download.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						download(audio.getPath());
						_name = audio.getTitle();
						if(!_isShowDialog) {
							onDialogShow();
						}
					}
				});
				_listCached[position] = row;
			}
			return _listCached[position];
		}

		class RowViewHolder extends LinearLayout {

			public RowViewHolder(Context context) {
				super(context);
				inflatViews();
			}

			private void inflatViews() {
				LayoutInflater.from(SDListPlayStreamingActivity.this).inflate(
						R.layout.player_streaming_item, this);
				_logo = (ImageView) findViewById(R.id.logo);
				_title = (TextView) findViewById(R.id.song_name);
				_play = (ImageButton) findViewById(R.id.play);
				_download = (ImageView) findViewById(R.id.download);
				_progress = (ProgressBar) findViewById(R.id.seek_bar_progress);

			}

			public ImageView _logo;
			public TextView _title;
			public ProgressBar _progress;
			public ImageButton _play;
			public ImageView _download;

		}
	}
	
	public class DownloadTask extends AsyncTask<String, Integer, Object> {

		@Override
		protected Bitmap doInBackground(String... urls) {
			try {
				URL url = new URL(urls[0]);
				_connection = (HttpURLConnection) url.openConnection();
				_connection.setRequestMethod("GET");
				_connection.setDoInput(true);
				_connection.setDoOutput(true);
				int resultCode = _connection.getResponseCode();
				if(resultCode == HttpURLConnection.HTTP_OK) {
					InputStream in = new BufferedInputStream(_connection.getInputStream());
					String fileName = URLUtil.guessFileName(urls[0], null, null);
					System.out.println("File Name : " + fileName);
					File parentFolder = new File(Environment.getExternalStorageDirectory(),"/" +_folderName);
					File outputFile = new File(parentFolder.getAbsolutePath() + "/" + fileName);
					if(outputFile.exists()) {
						String error = fileName + " is existed";
						_dialogListener.onDialogSendErrorMessage("error", error);
						return null;
					}
					long fileSize = _connection.getContentLength();
					if(!isEnoughSpaceOnStorge(fileSize)) {
						_dialogListener.onDialogSendErrorMessage("error", "Does not space on Storge");
						return null;
					}
					try {
						FileOutputStream out = new FileOutputStream(outputFile);
						byte[] buffer = new byte[1024];
						int len = 0;
		                int totallen = 0;
		                
		                _dialogListener.onDialogSetFileSize(_name, fileSize);
		                
		                while ((len = in.read(buffer)) != -1) {
		                	out.write(buffer, 0, len);
		                	totallen += len;
		                	_dialogListener.onDialogProgressUpdate(totallen);
		                }
		                out.flush();
		                out.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
	                in.close();
	               
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				clearUp();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			 _dialogListener.onDialogDismiss();
			if(result != null) {

			}
			
		}
    	
    }
	
	private void clearUp() {
		if(_connection != null) {
			try {
			_connection.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
			_connection = null;
		}
		_isConnecting = false;
	}
	
	public void download(String url) {
		if(_isConnecting) {
    		return;
    	} else {
    		_isConnecting = true;
    		new DownloadTask().execute(new String[]{url, ""});
    	}
	}
	
	public boolean createFolderInSdcard(String folderName) {
		File sdcard = new File(Environment.getExternalStorageDirectory(), "");
		String folderDir = sdcard.getAbsolutePath() + "/" + folderName;
		System.out.println(folderDir);
		File file = new File(folderDir);
		if(!file.exists()) {
			if(file.mkdir()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isEnoughSpaceOnStorge(long updateSize) {
		String state = Environment.getExternalStorageState();
		if(state == Environment.MEDIA_MOUNTED) {
			return false;
		}
		File path = Environment.getExternalStorageDirectory();
		StatFs fs = new StatFs(path.getPath());
		long blockSize = fs.getBlockSize();
		long availableBlock = fs.getAvailableBlocks();
		return (updateSize < (blockSize * availableBlock));
	}
	
	//Rescan the sdcard after copy the file
    private void rescanSDCard() throws Exception {       
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, 
			Uri.parse("file://" + Environment.getExternalStorageDirectory()))); 
		
		_dialog = ProgressDialog.show(this, "Media Scanner", "Scanning...", true, true);
		_dialog.show();
    }
    ProgressDialog _dialog;
    BroadcastReceiver _receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equalsIgnoreCase(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
				_dialog.dismiss();
			}
		}
    };

	
	public void onReloadClicked(View v) {
		try {
			rescanSDCard();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(_streamPlayer != null) {
				_streamPlayer.interrupt();
			}
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if(_receiver != null) {
			unregisterReceiver(_receiver);
		}
		super.onDestroy();
	}

	public void onDownloadClicked(View v) {

	}

	public void onPlayClicked(View v) {

	}

	@Override
	public void onDialogShow() {
		this.runOnUiThread(new Runnable() {
            public void run() {
        		_isShowDialog = true;
       		_bCancelDownload = false;
               _progressDialog.setTitle("Download");
               _progressDialog.setMessage("Downloading...");
       		_progressDialog.setMax(0);
       		_progressDialog.setProgress(0);
       		_progressDialog.show();
            }
		});
	}

	@Override
	public void onDialogSendErrorMessage(final String title, final String message) {
		this.runOnUiThread(new Runnable() {
            public void run() {
				_progressDialog.setTitle(title);
		        _progressDialog.setMessage(message);
				_bCancelDownload = false;
				_isShowDialog = false;
            }
		});
	}

	@Override
	public void onDialogSetFileSize(final String filename, final long Size) {
		this.runOnUiThread(new Runnable() {
            public void run() {
            	_progressDialog.setMessage(filename);
            	_progressDialog.setMax((int) Size);
            }
		});
	}

	@Override
	public void onDialogProgressUpdate(final int value) {
		this.runOnUiThread(new Runnable() {
            public void run() {
            	_progressDialog.setProgress(value);
            }
		});
	}

	@Override
	public void onDialogDismiss() {
		this.runOnUiThread(new Runnable() {
            public void run() {
                _progressDialog.dismiss();
        		_bCancelDownload = false;
        		_isShowDialog = false;
            }
        });
	}
}
