package com.csst.smarthome.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义文本控件
 * @author liuyang
 */
public final class CsstSHTextView extends TextView{
	
	public CsstSHTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CsstSHTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CsstSHTextView(Context context) {
		super(context);
	}
	
	@Override
	public boolean isFocused() {
		return true;
	}
	
	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		//super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}
	
}
