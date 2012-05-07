package gnt.sd.view;

import java.io.InputStream;

import gnt.sd.R;
import gnt.sd.model.SDAudio;
import gnt.sd.util.Util;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class SDLyricView extends RelativeLayout {

	private WebView _lyricView;
	private ProgressBar _loadingBar;
	public SDAlbumCoverArt _coverArt;
	public RelativeLayout _containerView;
	public SDLyricView(Context context) {
		super(context);
		inflateView(context);
	}

	public SDLyricView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inflateView(context);
	}

	public SDLyricView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflateView(context);
	}

	private void inflateView(Context context) {
		inflate(context, R.layout.player_lyric_view, this);
		_containerView = (RelativeLayout) findViewById(R.id.lyricView_container);
		_lyricView = (WebView) findViewById(R.id.lyric);
		_lyricView.setBackgroundColor(0);
		_loadingBar = (ProgressBar) findViewById(R.id.progress_bar_waiting_lyric);
		_loadingBar.setVisibility(View.INVISIBLE);
		_coverArt = (SDAlbumCoverArt) findViewById(R.id.cover_art);
		renderNoLyric();
	}
	
	public void renderLyric(SDAudio audio, String lyric) {
		InputStream is = getContext().getResources().openRawResource(
				R.raw.lyric);
		String content;
		content = Util.convertStreamToString(is);
		lyric = lyric.replace("\r\n", "<br/>");
		lyric = lyric.replace("\n", "<br/>");
		lyric = lyric.replace("\r", "<br/>");
		String html = content.replace("__SONG_TITLE__", audio.getTitle());
		html = html.replace("__ARTIST_NAME__",
				(audio.getArtist() != null) ? audio.getArtist()
						: "not found");
		html = html.replace("__LYRIC_CONTENT__", lyric);
		_lyricView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
	}
	
	public void renderNoLyric() {
		_lyricView
				.loadUrl("file:///android_asset/html/lyric_tips_not_found.html");
	}

	public void visibleViews() {
		this.setVisibility(View.VISIBLE);
		_lyricView.setVisibility(View.VISIBLE);
	}
	
	public void invisibleViews() {
		this.setVisibility(View.GONE);
		_lyricView.setVisibility(View.GONE);
	}
	
	private float _fromX = 0;
	private float _fromY = 0;
	private float _toX = 0;
	private float _toY = 0;
	private long _fromT = 0;
	private long _fromTapT = 0;

	public interface OnDoubleTapListener {
		public void onDoubleTap(View v);
	}

	private OnDoubleTapListener _onDoubleTapListener;

	public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
		_onDoubleTapListener = onDoubleTapListener;
	}

	public interface OnSingleTapListener {
		public void onSingleTap(View v);
	}

	private OnSingleTapListener _onSingleTapListener;

	public void setOnSingleTapListener(OnSingleTapListener onSingleTapListener) {
		_onSingleTapListener = onSingleTapListener;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		float velocityX = 0;
		long now = System.currentTimeMillis();

		switch (action) {
		case MotionEvent.ACTION_UP:
			_toX = ev.getX();
			_toY = ev.getY();
			velocityX = Math.abs((_toX - _fromX) / (now - _fromT));
			if (Math.sqrt((_toX - _fromX) * (_toX - _fromX) + (_toY - _fromY)
					* (_toY - _fromY)) < ViewConfiguration.getTouchSlop()) {
				// not too long
				if (now - _fromT < ViewConfiguration.getTapTimeout()) {
					if (now - _fromTapT < ViewConfiguration
							.getDoubleTapTimeout()) {
						if (_onDoubleTapListener != null) {
							_onDoubleTapListener.onDoubleTap(this);
						}
						_fromTapT = 0;
						return true;
					} else {
						_fromTapT = now;
					}
				} else {
					if (_onSingleTapListener != null) {
						_onSingleTapListener.onSingleTap(this);
					}
				}
			}
			break;
		case MotionEvent.ACTION_DOWN:
			_fromX = _toX = ev.getX();
			_fromY = _toY = ev.getY();
			_fromT = now;
			break;
		case MotionEvent.ACTION_MOVE:
			if ((_toX - _fromX) * (ev.getX() - _toX) <= 0) {
				_fromX = _toX;
				_fromY = _toY;
				_fromT = now;
			}
			_toX = ev.getX();
			_toY = ev.getY();
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

}
