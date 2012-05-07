package gnt.sd.view;

import gnt.sd.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SDPlayerMiniView extends LinearLayout implements OnTouchListener {
	public final static int MOVE_LEFT = 0;
	public final static int MOVE_RIGHT = 1;
	public final static int MOVE_UP = 2;
	public final static int MOVE_DOWN = 3;
	ImageView _buttonArrow;
	ImageView _buttonCoverArt;
	ImageView _buttonPre;
	ImageView _buttonNext;
	ImageView _buttonPlay;
	ImageView _buttonPause;
	Context _context;
	LinearLayout _controllLayout;
	int _offY;
	int _offX;
	public static int MIN_X = 0;
	public static int MAX_X = 320;
	public static int MIN_Y = -105;
	public static int MAX_Y = 0;
	int _type = -1;

	public SDPlayerMiniView(Context context) {
		super(context);
		_context = context;
		setupUI();
	}

	public SDPlayerMiniView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_context = context;
		setupUI();
	}

	public void setupUI() {
		LayoutInflater inflate = LayoutInflater.from(_context);
		inflate.inflate(R.layout.musicplayer_mini_view, this);
		_buttonArrow = (ImageView) findViewById(R.id.player_mini_arrow);
		this.setOnTouchListener(this);
		_buttonCoverArt = (ImageView) findViewById(R.id.player_mini_coverart);
		_buttonNext = (ImageView) findViewById(R.id.player_mini_imageButtonFF);
		_buttonPlay = (ImageView) findViewById(R.id.player_mini_imageButtonPlay);
		_buttonPause = (ImageView) findViewById(R.id.player_mini_imageButtonPause);
		_buttonPre = (ImageView) findViewById(R.id.player_mini_imageButtonREW);
		_controllLayout = (LinearLayout) findViewById(R.id.player_mini_control);
		// _controllLayout.setVisibility(View.GONE);
	}

	public int pxToDip(int px) {
		float scale = getResources().getDisplayMetrics().density;
		return (int) (scale * px);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) this
				.getLayoutParams();
		int y = (int) event.getRawY();
		int x = (int) event.getRawX();
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if ( y < _offY) {
				_type = MOVE_UP;
			} else if ( y >= _offY) {
				_type = MOVE_DOWN;
			} else if (layoutParam.bottomMargin == MIN_Y && x < _offX) {
				_type = MOVE_LEFT;
			} else if (layoutParam.bottomMargin == MIN_Y && x >= _offX) {
				_type = MOVE_RIGHT;
			}
			switch (_type) {
			case MOVE_DOWN:
				if (y != _offY) {
					layoutParam.bottomMargin -= (y - _offY);
					if (layoutParam.bottomMargin >= MAX_Y)
						layoutParam.bottomMargin = MAX_Y;
					if (layoutParam.bottomMargin <= MIN_Y)
						layoutParam.bottomMargin = MIN_Y;
					this.setLayoutParams(layoutParam);
					_offY = y;
				}
				break;
			case MOVE_UP:
				if (y != _offY) {
					layoutParam.bottomMargin -= (y - _offY);
					if (layoutParam.bottomMargin >= MAX_Y)
						layoutParam.bottomMargin = MAX_Y;
					if (layoutParam.bottomMargin <= MIN_Y)
						layoutParam.bottomMargin = MIN_Y;
					this.setLayoutParams(layoutParam);
					_offY = y;
				}
				break;
			case MOVE_RIGHT:
				if (x != _offX) {
					layoutParam.leftMargin -= (x - _offX);
					if (layoutParam.leftMargin >= MAX_X)
						layoutParam.leftMargin = MAX_X;
					if (layoutParam.leftMargin <= MIN_X)
						layoutParam.leftMargin = MIN_X;
					this.setLayoutParams(layoutParam);
					_offX = x;
				}
				break;
			default:
				break;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			_offY = y;
			_offX = x;
			break;

		case MotionEvent.ACTION_UP:
			if (layoutParam.bottomMargin >= (MAX_Y + MIN_Y) / 2)
				layoutParam.bottomMargin = MAX_Y;
			if (layoutParam.bottomMargin <= (MAX_Y + MIN_Y) / 2)
				layoutParam.bottomMargin = MIN_Y;
			this.setLayoutParams(layoutParam);
			break;
		}
		return true;

	}

	public void translate(int y1, int y2) {
		TranslateAnimation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, -y2);
		animation.setDuration(300);
		animation.setInterpolator(new LinearInterpolator());

		startAnimation(animation);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				_controllLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

		});

	}
}
