package com.csst.smarthome.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class CsstVerticalPager extends ViewGroup {

	private static final String TAG = "CsstVerticalPager";
	
	private VelocityTracker mVelocityTracker = null; // 用于判断甩动手势
	private static final int SNAP_VELOCITY = 600;
	private Scroller mScroller = null; // 滑动控制器
	private int mCurScreen;
	private int mDefaultScreen = 0;
	private float mLastMotionY;
	private OnViewChangeListener mOnViewChangeListener;

	public CsstVerticalPager(Context context) {
		super(context);
		init(context);
	}

	public CsstVerticalPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CsstVerticalPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mCurScreen = mDefaultScreen;
		mScroller = new Scroller(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed){
			int childTop = 0;
			final int childCount = getChildCount();
			for (int i = 0; i < childCount; i++){
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childHeight = childView.getMeasuredHeight();
					childView.layout(0, childTop, childView.getMeasuredWidth(), childTop + childHeight);
					childTop += childHeight;
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int height = MeasureSpec.getSize(heightMeasureSpec);
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(mCurScreen * height, 0);
	}

	public void snapToDestination() {
		final int screenHeight = getHeight();
		final int destScreen = (getScrollY() + screenHeight / 2) / screenHeight;
		snapToScreen(destScreen);
	}

	public void snapToScreen(int heightScreen) {
		heightScreen = Math.max(0, Math.min(heightScreen, getChildCount() - 1));
		if (getScrollY() != (heightScreen * getHeight())) {
			final int delta = heightScreen * getHeight() - getScrollY();
			mScroller.startScroll(0, getScrollY(), 0, delta, Math.abs(delta) * 2);
			mCurScreen = heightScreen;
			invalidate(); // Redraw the layout
			if (mOnViewChangeListener != null) {
				mOnViewChangeListener.OnViewChange(mCurScreen);
			}
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		//final float x = event.getX();
		final float y = event.getY();

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				Log.d(TAG, "onTouchEvent  ACTION_DOWN");
				if (mVelocityTracker == null) {
					mVelocityTracker = VelocityTracker.obtain();
					mVelocityTracker.addMovement(event);
				}
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}
				mLastMotionY = y;
				break;
	
			case MotionEvent.ACTION_MOVE:
				int deltaY = (int) (mLastMotionY - y);
				if (IsCanMove(deltaY)) {
					if (mVelocityTracker != null) {
						mVelocityTracker.addMovement(event);
					}
					mLastMotionY = y;
					scrollBy(0, deltaY);
				}
				break;
	
			case MotionEvent.ACTION_UP:
				int velocityY = 0;
				if (mVelocityTracker != null) {
					mVelocityTracker.addMovement(event);
					mVelocityTracker.computeCurrentVelocity(1000);
					velocityY = (int) mVelocityTracker.getYVelocity();
				}
				if (velocityY > SNAP_VELOCITY && mCurScreen > 0) {
					Log.d(TAG, "snap left");
					snapToScreen(mCurScreen - 1);
				} else if (velocityY < -SNAP_VELOCITY
						&& mCurScreen < getChildCount() - 1) {
					Log.d(TAG, "snap right");
					snapToScreen(mCurScreen + 1);
				} else {
					snapToDestination();
				}
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				break;
		}
		return true;
	}

	private boolean IsCanMove(int deltaY) {
		if (getScrollY() <= 0 && deltaY < 0) {
			return false;
		}
		if (getScrollY() >= (getChildCount() - 1) * getHeight() && deltaY > 0) {
			return false;
		}
		return true;
	}

	public void SetOnViewChangeListener(OnViewChangeListener listener) {
		mOnViewChangeListener = listener;
	}
	
	public interface OnViewChangeListener {
		public void OnViewChange(int view);
	}

}
