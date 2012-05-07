package gnt.sd.controller;

import gnt.sd.configs.Config;
import gnt.sd.util.Util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Service implements Runnable {

	private Thread _thread;
	private ServiceAction _action;
	private Service _service;
	private ServiceListener _listener;
	private HttpURLConnection _connection;
	private Map<String, String> _params;
	private boolean _isGet;
	private boolean _connecting;
	private String _actionUri;
	private boolean _isBitMap;

	public static final String LAST_FM_URL = "http://ws.audioscrobbler.com/2.0/";
	private final Handler _handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			_listener.onComplete(_service, (ServiceRespone) msg.obj);
		}
	};

	public Service() {
		this(null);
	}

	public Service(ServiceListener listener) {
		_action = ServiceAction.ActionNone;
		_listener = listener;
		_service = this;
		_connecting = false;
		_isBitMap = false;
	}

	public void getAlbumInfo(String album, String artist) {
		_action = ServiceAction.ActionGetAlbumInfo;
		Map<String, String> params = new HashMap<String, String>();
		params.put("method", "album.getinfo");
		params.put("api_key", gnt.sd.configs.Config.LAST_FM_API_KEY);
		params.put("artist", artist);
		params.put("album", album);
		request(LAST_FM_URL, params, true);
	}

	public void getArtistBio(String artist) {
		_action = ServiceAction.ActionGetArtistInfo;
		Map<String, String> params = new HashMap<String, String>();
		params.put("method", "artist.getinfo");
		params.put("api_key", gnt.sd.configs.Config.LAST_FM_API_KEY);
		params.put("artist", artist);
		request(LAST_FM_URL, params, true);
	}

	public void searchYoutube(String key) {
		_action = ServiceAction.ActionSearchYouTube;
		Map<String, String> params = new HashMap<String, String>();
		params.put("q", key);
		params.put("start-index", "1");
		params.put("max-results", "20");
		params.put("v", "2");
		params.put("format", "5");
		request("http://gdata.youtube.com/feeds/api/videos", params, true,
				false);
	}

	public void getArtistImage(String artist) {
		_action = ServiceAction.ActionGetArtistImage;
		Map<String, String> params = new HashMap<String, String>();
		params.put("method", "artist.getimages");
		params.put("api_key", gnt.sd.configs.Config.LAST_FM_API_KEY);
		params.put("artist", artist);
		params.put("limit", "7");
		request(LAST_FM_URL, params, true);
	}

	public void downloadImage(String path) {
		_action = ServiceAction.ActionDownLoadImage;
		Map<String, String> params = new HashMap<String, String>();
		_isBitMap = true;
		request(path, params, false, true);
	}

	public void getCorrecttrackInfo(String track, String artist) {
		_action = ServiceAction.ActionGetCorrectTracInfo;
		Map<String, String> params = new HashMap<String, String>();
		params.put("method", "track.getcorrection");
		params.put("api_key", gnt.sd.configs.Config.LAST_FM_API_KEY);
		params.put("artist", artist);
		params.put("track", track);
		request(LAST_FM_URL, params, true);
	}

	public void getLyric(String track, String artist) {
		_action = ServiceAction.ActionGetLyric;
		String trackEn = track.replace(" ", "_");
		String artistEn = artist.replace(" ", "_");
		Map<String, String> params = new HashMap<String, String>();
		params.put("artist", artistEn);
		params.put("song", trackEn);
		request(Config.URL + "/test/getlyric.php", params, true);
	}

	public void searchFromZing(String keyword, int type) {
		_action = ServiceAction.ActionSearchFromZing;
		String keywordEn = keyword.replace(" ", "_");
		Map<String, String> params = new HashMap<String, String>();
		params.put("keyword", keywordEn);
		if(type == 0)
			params.put("type", "title");
		else if(type == 1)
			params.put("type", "artist");
		request(Config.URL + "/test/search_zing.php", params, true);
	}
	
	public void getHotVPop(String keyword) {
		_action = ServiceAction.ActionGetHotVPop;
		String keywordEn = keyword.replace(" ", "_");
		Map<String, String> params = new HashMap<String, String>();
		params.put("keyword", keywordEn);
		params.put("type", "artist");
		request(Config.URL + "/test/search_zing.php", params, true);
	}
	
	public void getHotKPop(String keyword) {
		_action = ServiceAction.ActionGetHotKPop;
		String keywordEn = keyword.replace(" ", "_");
		Map<String, String> params = new HashMap<String, String>();
		params.put("keyword", keywordEn);
		params.put("type", "artist");
		request(Config.URL + "/test/search_zing.php", params, true);
	}
	
	public void getHotAuMy(String keyword) {
		_action = ServiceAction.ActionGetHotAuMy;
		String keywordEn = keyword.replace(" ", "_");
		Map<String, String> params = new HashMap<String, String>();
		params.put("keyword", keywordEn);
		params.put("type", "artist");
		request(Config.URL + "/test/search_zing.php", params, true);
	}
	
	public void getSimilar(String artist){
		
	}
	public boolean request(String url, Map<String, String> params, boolean isGet) {
		return request(url, params, isGet, false);
	}

	public boolean request(String url, Map<String, String> params,
			boolean isGet, boolean isBitmap) {
		if (_connecting) {
			return false;
		}
		_connecting = true;
		_actionUri = url;
		_params = params;
		_isGet = isGet;
		_isBitMap = isBitmap;
		_thread = new Thread(this);
		_thread.start();
		return true;
	}

	public boolean isConnecting() {
		return _connecting;
	}

	@Override
	public void run() {
		String urlString = _actionUri;
		String data = getParamsString(_params);
		if (_isGet) {
			if (data != null || data != "") {
				urlString = urlString + "?" + data;
			}
		}
		Log.d("Service", "Url request : " + urlString);
		try {
			URL url = new URL(urlString);
			_connection = (HttpURLConnection) url.openConnection();
			_connection.setRequestMethod(_isGet ? "GET" : "POST");
			_connection.setDoInput(true);

			if (!_isGet) {
				Log.d("Service", "Params:" + data);
				_connection.setDoOutput(true);
				try {
					OutputStream out = new BufferedOutputStream(
							_connection.getOutputStream());
					out.write(data.getBytes());
					out.flush();
					out.close();
				} catch (IOException e) {
					throw e;
				}
			}
			int httpCode = _connection.getResponseCode();
			Log.d("Service", "code=" + httpCode);
			if (httpCode == HttpURLConnection.HTTP_OK) {
				InputStream in;
				if (_connection.getHeaderField("Content-encoding") != null
						&& _connection.getHeaderField("Content-encoding")
								.trim().toLowerCase().equals("gzip")) {
					in = new GZIPInputStream(_connection.getInputStream());
				} else {
					in = new BufferedInputStream(_connection.getInputStream());
				}
				if (_isBitMap) {
					Bitmap bm = BitmapFactory.decodeStream(in);
					dispatchResult(bm);
				} else {
					String result = Util.convertStreamToString(in);
					dispatchResult(result.toString());
				}
			} else if (httpCode == HttpURLConnection.HTTP_NOT_FOUND) {
				processError(ResultCode.Failed);
			} else if (httpCode == HttpURLConnection.HTTP_SERVER_ERROR) {
				processError(ResultCode.ServerError);
			} else {
				processError(ResultCode.NetworkError);
			}
		} catch (Exception e) {
			e.printStackTrace();
			processError(ResultCode.NetworkError);
		} finally {
			clearUp();
		}
	}

	private void clearUp() {
		_action = ServiceAction.ActionNone;
		if (_connection != null) {
			try {
				_connection.disconnect();
			} catch (Exception ex) {
			}
			_connection = null;
		}
		_connecting = false;
		Log.d("Service", "StopService");
	}

	private void processError(ResultCode failed) {
		Message msg = _handler.obtainMessage(0, new ServiceRespone(_action,
				null, failed));
		_handler.sendMessage(msg);
	}

	private void dispatchResult(String result) {
		if (_listener == null || _action == ServiceAction.ActionNone
				|| !_connecting)
			return;
		Log.d("Service", result);

		ServiceAction act = _action;
		Object resObj = null;
		ServiceRespone response = null;
		if (act == ServiceAction.ActionGetLyric) {
			resObj = result;
		} else {
			DataParser parser = new DataParser();
			boolean isOK = parser.parse(result, DataType.XML);
			if (isOK) {
				switch (act) {
				case ActionSearchYouTube:
					resObj = parser.getYoutube();
					break;
				case ActionGetAlbumInfo:
					resObj = parser.getAlbumInfo();
					break;
				case ActionGetArtistInfo:
					resObj = parser.getArtistBio();
					break;
				case ActionGetArtistImage:
					resObj = parser.getArtistImage();
					break;
				case ActionGetCorrectTracInfo:
					resObj = parser.getCorrectTrackInfo();
					break;
				case ActionSearchFromZing:
					resObj = parser.searchFromZing();
					break;
				case ActionGetHotVPop:
					resObj = parser.searchFromZing();
					break;
				case ActionGetHotKPop:
					resObj = parser.searchFromZing();
					break;
				case ActionGetHotAuMy:
					resObj = parser.searchFromZing();
					break;
				default:
					break;
				}
			}
		}
		if (resObj == null) {
			response = new ServiceRespone(_action, resObj, ResultCode.Failed);
		} else {
			response = new ServiceRespone(_action, resObj);
		}
		stop();

		Message msg = _handler.obtainMessage(0, response);
		_handler.sendMessage(msg);
	}

	private void dispatchResult(Bitmap result) {
		if (_listener == null || _action == ServiceAction.ActionNone
				|| !_connecting)
			return;
		ServiceAction act = _action;
		ServiceRespone response = null;

		if (result == null) {
			response = new ServiceRespone(act, null, ResultCode.Failed);
		} else {
			response = new ServiceRespone(act, result);
		}
		stop();
		Message msg = _handler.obtainMessage(0, response);
		_handler.sendMessage(msg);
	}

	public void stop() {
		clearUp();
	}

	private String getParamsString(Map<String, String> params) {
		if (params == null)
			return null;
		String ret = "";
		for (String key : params.keySet()) {
			String value = params.get(key);
			ret += key + "=" + URLEncoder.encode(value) + "&";
		}
		return ret;
	}
}
