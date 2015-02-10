package com.csst.smarthome.util;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.TextView;

public class AutoScrollTextView extends TextView {

	public final static String TAG = AutoScrollTextView.class.getSimpleName();

	private float textLength = 0f;// �ı�����
	private float viewWidth = 0f;
	private float step = 0f;// ���ֵĺ����
	private float y = 0f;// ���ֵ������
	private float x = 0f;//������ʱ�ĺ����
	private float temp_view_plus_text_length = 0.0f;// ���ڼ������ʱ����
	private float temp_view_plus_two_text_length = 0.0f;// ���ڼ������ʱ����
	public boolean isStarting = false;// �Ƿ�ʼ����
	private Paint paint = null;// ��ͼ��ʽ
	private String text = "";// �ı�����
	private boolean first = true;

	public AutoScrollTextView(Context context) {
		super(context);
		this.setFocusable(true);
		init();
	}

	public AutoScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setFocusable(true);
		init();
	}

	public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setFocusable(true);
		init();
	}

	public static final float SPEED_SLOW=1.0f;
	public static final float SPEED_NORMAL=2.5f;
	public static final float SPEED_FAST=5.0f;
	
	private float speed=SPEED_NORMAL;
	public void setSpeed(float model){
		if(model!=SPEED_FAST&&model!=SPEED_NORMAL&&model!=SPEED_SLOW)
			model=SPEED_NORMAL;
		speed=model;
	}
	/**
	 * �ı���ʼ����ÿ�θ���ı����ݻ����ı�Ч���֮����Ҫ���³�ʼ��һ��
	 */
	private void init() {
		paint = getPaint();
		paint.setARGB(255, 255, 165, 0);
		
	}

	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);

		ss.step = step;
		ss.isStarting = isStarting;

		return ss;

	}

	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		step = ss.step;
		isStarting = ss.isStarting;

	}

	public static class SavedState extends BaseSavedState {
		public boolean isStarting = false;
		public float step = 0.0f;

		SavedState(Parcelable superState) {
			super(superState);
		}

		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeBooleanArray(new boolean[] { isStarting });
			out.writeFloat(step);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}

			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}
		};

		private SavedState(Parcel in) {
			super(in);
			boolean[] b = null;
			try{
				in.readBooleanArray(b);
				if (b != null && b.length > 0)
					isStarting = b[0];
					step = in.readFloat();
			}catch(Exception ex){
				System.out.println(ex.toString());
			}
			
			
		}
	}

	/**
	 * ��ʼ����
	 */
	public void startScroll() {
		isStarting = true;
		invalidate();
	}

	/**
	 * ֹͣ����
	 */
	public void stopScroll() {
		isStarting = false;
		invalidate();
	}
	@Override
	public void onDraw(Canvas canvas) {
		if (first) {
			viewWidth = getWidth();
			textLength = paint.measureText(text);
			if (textLength > viewWidth) {
				isStarting = true;
			} else {
				isStarting = false;
			}
			step = textLength;
			temp_view_plus_text_length = viewWidth + textLength;
			temp_view_plus_two_text_length = viewWidth + textLength * 2;
			y = getTextSize() + getPaddingTop();
			x = getPaddingLeft();
			first = false;
		}
		if (!isStarting) {
			canvas.drawText(text, x, y, paint);
			return;
		}
		canvas.drawText(text, temp_view_plus_text_length - step, y, paint);
		step += speed;
		if (step > temp_view_plus_two_text_length)
			step = textLength;
		invalidate();
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
