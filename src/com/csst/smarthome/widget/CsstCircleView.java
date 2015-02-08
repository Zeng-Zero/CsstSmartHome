package com.csst.smarthome.widget;

import com.csst.smarthome.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

/**
 * 遥控器圆形按钮
 * @author liuyang
 */
public class CsstCircleView extends FrameLayout {

	protected Context mContext = null;
	private LayoutInflater mInflater = null;
	private View container = null;
	private ImageButton mBtnMiddle = null;
	private ImageButton mBtnLeft = null;
	private ImageButton mBtnRight = null;
	private ImageButton mBtnTop = null;
	private ImageButton mBtnBottom = null;
	private CircleBtnListener mCircleBtnListener = null;
	private View.OnClickListener mLeftBtnListener = null;
	private View.OnClickListener mRightBtnListener = null;
	private View.OnClickListener mTopBtnListener = null;
	private View.OnClickListener mBottomBtnListener = null;
	private View.OnClickListener mMiddleBtnListener = null;
	private CircleBtnLongListener mCircleBtnLongListener = null;
	private View.OnLongClickListener mLeftLongListener = null;
	private View.OnLongClickListener mRightLongListener = null;
	private View.OnLongClickListener mTopLongListener = null;
	private View.OnLongClickListener mBottomLongListener = null;
	private View.OnLongClickListener mMiddleLongListener = null;
	
	public CsstCircleView(Context context) {
		super(context);
		init(context);
	}

	public CsstCircleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CsstCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private final void init(Context context){
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		container = mInflater.inflate(R.layout.csst_rc_vol_ch_layout, null);
		mBtnMiddle = (ImageButton) container.findViewById(R.id.mBtnMiddle);
		mBtnLeft = (ImageButton) container.findViewById(R.id.mBtnLeft);
		mBtnRight = (ImageButton) container.findViewById(R.id.mBtnRight);
		mBtnTop = (ImageButton) container.findViewById(R.id.mBtnTop);
		mBtnBottom = (ImageButton) container.findViewById(R.id.mBtnBottom);
		//按键监听器
		mCircleBtnListener = new CircleBtnListener();
		mBtnMiddle.setOnClickListener(mCircleBtnListener);
		mBtnLeft.setOnClickListener(mCircleBtnListener);
		mBtnRight.setOnClickListener(mCircleBtnListener);
		mBtnTop.setOnClickListener(mCircleBtnListener);
		mBtnBottom.setOnClickListener(mCircleBtnListener);
		mCircleBtnLongListener = new CircleBtnLongListener();
		mBtnMiddle.setOnLongClickListener(mCircleBtnLongListener);
		mBtnLeft.setOnLongClickListener(mCircleBtnLongListener);
		mBtnRight.setOnLongClickListener(mCircleBtnLongListener);
		mBtnTop.setOnLongClickListener(mCircleBtnLongListener);
		mBtnBottom.setOnLongClickListener(mCircleBtnLongListener);
		this.addView(container);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	private final class CircleBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
//				case R.id.mBtnMiddle:
//					if (mMiddleBtnListener != null){
//						mMiddleBtnListener.onClick(v);
//					}
//					break;
//	
//				case R.id.mBtnLeft:
//					if (mLeftBtnListener != null){
//						mLeftBtnListener.onClick(v);
//					}
//					break;
//					
//				case R.id.mBtnRight:
//					if (mRightBtnListener != null){
//						mRightBtnListener.onClick(v);
//					}
//					break;
//					
//				case R.id.mBtnTop:
//					if (mTopBtnListener != null){
//						mTopBtnListener.onClick(v);
//					}
//					break;
//					
//				case R.id.mBtnBottom:
//					if (null != mBottomBtnListener){
//						mBottomBtnListener.onClick(v);
//					}
//					break;
			}
		}
	}

	private final class CircleBtnLongListener implements View.OnLongClickListener{
		@Override
		public boolean onLongClick(View v) {
			switch (v.getId()) {
//				case R.id.mBtnMiddle:
//					if (mMiddleLongListener != null){
//						return mMiddleLongListener.onLongClick(v);
//					}
//					break;
//	
//				case R.id.mBtnLeft:
//					if (mLeftLongListener != null){
//						return mLeftLongListener.onLongClick(v);
//					}
//					break;
//					
//				case R.id.mBtnRight:
//					if (mRightLongListener != null){
//						return mRightLongListener.onLongClick(v);
//					}
//					break;
//					
//				case R.id.mBtnTop:
//					if (mTopLongListener != null){
//						return mTopLongListener.onLongClick(v);
//					}
//					break;
//					
//				case R.id.mBtnBottom:
//					if (null != mBottomLongListener){
//						return mBottomLongListener.onLongClick(v);
//					}
//					break;
			}
			return false;
		}
	}
	
	public final void setMiddleBtnTag(Object tag){
		mBtnMiddle.setTag(tag);
	}
	
	public final void setLeftBtnTag(Object tag){
		mBtnLeft.setTag(tag);
	}
	
	public final void setTopBtnTag(Object tag){
		mBtnTop.setTag(tag);
	}
	
	public final void setRightBtnTag(Object tag){
		mBtnRight.setTag(tag);
	}
	
	public final void setBottomBtnTag(Object tag){
		mBtnBottom.setTag(tag);
	}
	
	public void setLeftBtnListener(View.OnClickListener leftBtnListener) {
		this.mLeftBtnListener = leftBtnListener;
	}

	public void setRightBtnListener(View.OnClickListener rightBtnListener) {
		this.mRightBtnListener = rightBtnListener;
	}

	public void setTopBtnListener(View.OnClickListener topBtnListener) {
		this.mTopBtnListener = topBtnListener;
	}

	public void setBottomBtnListener(View.OnClickListener bottomBtnListener) {
		this.mBottomBtnListener = bottomBtnListener;
	}

	public void setMiddleBtnListener(View.OnClickListener middleBtnListener) {
		this.mMiddleBtnListener = middleBtnListener;
	}
	
	public void setLeftLongListener(View.OnLongClickListener leftLongListener) {
		this.mLeftLongListener = leftLongListener;
	}

	public void setRightLongListener(View.OnLongClickListener rightLongListener) {
		this.mRightLongListener = rightLongListener;
	}
	
	public void setTopLongListener(View.OnLongClickListener topLongListener) {
		this.mTopLongListener = topLongListener;
	}
	
	public void setBottomLongListener(View.OnLongClickListener bottomLongListener) {
		this.mBottomLongListener = bottomLongListener;
	}
	
	public void setMiddleLongListener(View.OnLongClickListener middleLongListener) {
		this.mMiddleLongListener = middleLongListener;
	}

}
