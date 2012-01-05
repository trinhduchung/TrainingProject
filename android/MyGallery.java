package com.shoutz.android.views;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.shoutz.android.MyApplication;
import com.shoutz.android.R;
import com.shoutz.android.activity.BrandsActivity;
import com.shoutz.android.activity.LoginActivity;
import com.shoutz.android.activity.MultiviewActivity;
import com.shoutz.android.activity.ShoutActivity;
import com.shoutz.android.activity.ShoutsActivity;
import com.shoutz.android.adapters.items.BrandItem;
import com.shoutz.android.adapters.items.BrandLabel;
import com.shoutz.android.adapters.items.MoreBrands;
import com.shoutz.android.adapters.items.MoreShouts;
import com.shoutz.android.adapters.items.ShoutHint;
import com.shoutz.android.adapters.items.ShoutItem;
import com.shoutz.android.adapters.items.ShoutLabel;
import com.shoutz.android.adapters.items.StreamLabel;
import com.shoutz.android.context.MultiviewContext;
import com.shoutz.android.transfer.BrandRef;
import com.shoutz.android.transfer.Shout;
import com.shoutz.android.user.User;
import com.shoutz.android.user.UserContext;

public class MyGallery extends Gallery {
	
	private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private int mScrollX = 0;
    private int mCurrentScreen = 0;

    private float mLastMotionX;

    private static final String LOG_TAG = "DragableSpace";

    private static final int SNAP_VELOCITY = 1000;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;

    private int mTouchState = TOUCH_STATE_REST;

    private int mTouchSlop = 0;
	
	private int mCurrentPosition = -1;
	private FrameLayout mCurrentFrame;
	private Rect mTouchFrame;
	private int mFirstPosition = 0;
	private Context mContext;
	private long mTouchDownTime;
	private long mTouchUpTime;
	private long mClickTime = 108;
	GestureDetector gestureDetector = new GestureDetector(
			new innerGestureDetector());

	public MyGallery(Context context) {
		super(context);
		inflateView(context);
	}

	public MyGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inflateView(context);
	}

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflateView(context);
	}
	
	private void inflateView(Context context) {
		this.dispatchSetSelected(true);
		mContext = context;
		mScroller = new Scroller(context);

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		super.onFling(e1, e2, velocityX, velocityY);
		System.out.println("on fling , is touch mode : " + this.isInTouchMode());
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		/*
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			return false;
		case MotionEvent.ACTION_UP:
			return false;
		default:
			break;
		}
		if (gestureDetector.onTouchEvent(ev)) {
			super.onTouchEvent(ev);
			return true;
		}

		return super.onInterceptTouchEvent(ev);
		*/
		final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE)
                && (mTouchState != TOUCH_STATE_REST)) {
            return true;
                }

        final float x = ev.getX();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                 * Locally do absolute value. mLastMotionX is set to the y value
                 * of the down event.
                 */
                final int xDiff = (int) Math.abs(x - mLastMotionX);

                boolean xMoved = xDiff > mTouchSlop;

                if (xMoved) {
                    // Scroll if the user moved far enough along the X axis
                    mTouchState = TOUCH_STATE_SCROLLING;
                }
                break;

            case MotionEvent.ACTION_DOWN:
                // Remember location of down touch
                mLastMotionX = x;

                /*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't.  mScroller.isFinished should be false when
                 * being flinged.
                 */
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // Release the drag
                mTouchState = TOUCH_STATE_REST;
                break;
        }

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        return mTouchState != TOUCH_STATE_REST;
	}
	
	private FrameLayout getCurrentFrameLayout() {
		mCurrentFrame = null;
		View v = this.getChildAt(mCurrentPosition - this.getFirstVisiblePosition());
		RelativeLayout curr = null;
		if (v instanceof RelativeLayout) {
			curr = (RelativeLayout) v;
		}
		if (null != curr) {
			for (int i = 0;i < curr.getChildCount();i++) {
				View vi = curr.getChildAt(i);
				if (vi instanceof FrameLayout) {
					mCurrentFrame = (FrameLayout) vi;
					break;
				}
			}
		}
		if (null == mCurrentFrame) {
			return null;
		}
		return mCurrentFrame;
	}
	
	private void gotoShout() {
		System.out.println("cur pos = " + mCurrentPosition);
		Object item = getAdapter().getItem(mCurrentPosition);

		if (item instanceof ShoutItem) {
			Intent i = new Intent(mContext, ShoutActivity.class);
			Shout shout = ((ShoutItem) item).getShout();
			i.putExtra(ShoutActivity.SHOUT, shout);
			mContext.startActivity(i);
		} else if (item instanceof ShoutHint) {
			User user = new UserContext(mContext).getUser();
			if (user != null){// logged in, open search page
				((Activity) mContext).onSearchRequested();
			} else {// not logged in, open login page 
				Intent i = new Intent(mContext, LoginActivity.class);
				((Activity) mContext).startActivityForResult(i,
						LoginActivity.LOGIN_REQUEST_CODE);
			}
		} else if ((item instanceof ShoutLabel) || (item instanceof MoreShouts)) {
			Intent i = new Intent(mContext, ShoutsActivity.class);
			i.putExtra(ShoutsActivity.SHOUTS_URL, "shouts/featured?");
			mContext.startActivity(i);
		} else if (item instanceof BrandItem) {
			Intent i = new Intent(mContext, MultiviewActivity.class);
			i.putExtra(MultiviewContext.BRAND_ID,
					getAdapter().getItemId(mCurrentPosition));
			
			//nvhau
			BrandRef brand = ((BrandItem) item).getBrand();				
			i.putExtra(MultiviewContext.BRAND_NAME, brand.getName());
			
			mContext.startActivity(i);
		} else if ((item instanceof BrandLabel) || (item instanceof MoreBrands)) {
			Intent i = new Intent(mContext, BrandsActivity.class);
			mContext.startActivity(i);
		} else if (item instanceof StreamLabel){
			User user = new UserContext(mContext).getUser();
			if (user != null) {
				Intent i = new Intent(mContext, MultiviewActivity.class);
				mContext.startActivity(i);
			}
		}
	}
	
	private void restoreCurrentState() {
		if (null != mCurrentFrame) {
			MyApplication myapp = (MyApplication) MyApplication.getConText();	
			Drawable imgDrw = myapp.getResources().getDrawable(R.color.opaque_black);
			long id = getAdapter().getItemId(mCurrentPosition);
			if( myapp.isViewed(id) == false) {								
				mCurrentFrame.setForeground(null);			
			}
			else {
				mCurrentFrame.setForeground(imgDrw);			
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
		Drawable activeImg = getResources().getDrawable(R.color.gallery_active);
		final float x = ev.getX();
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
				mLastMotionX = x;
				mTouchDownTime = Calendar.getInstance().getTimeInMillis();
				mCurrentPosition = this.pointToPos((int)ev.getX(), (int)ev.getY());
				getCurrentFrameLayout();
				if (mCurrentFrame != null) {
					System.out.println("active");
					mCurrentFrame.setForeground(activeImg);
				}
				
				break;
			case MotionEvent.ACTION_UP:
				
				mTouchUpTime = Calendar.getInstance().getTimeInMillis();
				if (mCurrentFrame != null) {
					mCurrentFrame.setForeground(null);
				}
				restoreCurrentState();
				System.out.println("click time = " + (mTouchUpTime - mTouchDownTime));
				if ((mTouchUpTime - mTouchDownTime) <= mClickTime) {
					gotoShout();
					mTouchDownTime = mTouchUpTime = 0;
				}
				if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                // }
                mTouchState = TOUCH_STATE_REST;
				break;
			case MotionEvent.ACTION_CANCEL:
				if (mCurrentFrame != null) {
					mCurrentFrame.setForeground(null);
				}
				restoreCurrentState();
				mTouchState = TOUCH_STATE_REST;
				break;
			case MotionEvent.ACTION_MOVE:
				final int deltaX = (int) (mLastMotionX - x);
                mLastMotionX = x;

                //Log.i(LOG_TAG, "event : move, deltaX " + deltaX + ", mScrollX " + mScrollX);

                if (deltaX < 0) {
                    if (mScrollX > 0) {
                        scrollBy(Math.max(-mScrollX, deltaX), 0);
                    }
                } else if (deltaX > 0) {
                    final int availableToScroll = getChildAt(getChildCount() - 1)
                        .getRight()
                        - mScrollX - getWidth();
                    if (availableToScroll > 0) {
                        scrollBy(Math.min(availableToScroll, deltaX), 0);
                    }
                }
				break;
			default:
				break;
		}
		mScrollX = this.getScrollX();
		return true;
	}

	@Override
	public int pointToPosition(int x, int y) {
		return INVALID_POSITION;
	}
	
	public int pointToPos(int x, int y) {
		mFirstPosition = this.getFirstVisiblePosition();
		Rect frame = mTouchFrame;
		if (frame == null) {
			mTouchFrame = new Rect();
			frame = mTouchFrame;
		}

		final int count = getChildCount();
		for (int i = count - 1; i >= 0; i--) {
			View child = getChildAt(i);
			if (child.getVisibility() == View.VISIBLE) {
				child.getHitRect(frame);
				if (frame.contains(x, y)) {
					return mFirstPosition + i;
				}
			}
		}
		return INVALID_POSITION;
	}
	
	class innerGestureDetector extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return MyGallery.this.onFling(e1, e2, velocityX * 2, velocityY);
		}

	}

}
