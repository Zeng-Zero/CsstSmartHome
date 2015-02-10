package com.csst.smarthome.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.csst.smarthome.R;

public final class CsstContextUtil {
	
	
	/**
	 * 搜索对话框
	 * @param context
	 * @param title
	 * @param message
	 * @param callback
	 * @return
	 */
	public static final Dialog searchDialog(Context context, String title, String message, DialogInterface.OnClickListener callback){
		String negative = context.getString(R.string.csst_cancel);
		return searchDialog(context, title, message, null, null, negative, callback);
	}
	
	/**
	 * 链接对话框
	 * @param context
	 * @param title
	 * @param message
	 * @param callback
	 * @return
	 */
	public static final Dialog searchDialog(Context context, String title, String message, String positive, DialogInterface.OnClickListener positiveCallback, String negative, DialogInterface.OnClickListener negativeCallback){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.csst_signal_view, null);
		ImageView mIVSignal = (ImageView) view.findViewById(R.id.mIVSignal);
		if(null != message &&message.equals(context.getString(R.string.csst_safe_search_sensor))){
			mIVSignal.setBackgroundResource(R.anim.csst_safesearch);
		}else if(null != message &&message.equals(context.getString(R.string.csst_study_code_message))){
			mIVSignal.setBackgroundResource(R.anim.csst_studycode);
		}else{
			mIVSignal.setBackgroundResource(R.anim.csst_researchdevice);
		}
		
		
//		if(message.equals(context.getString(R.string.csst_adddevice_research_device_message))){
//			mIVSignal.setBackgroundResource(R.anim.csst_researchdevice);
//		}else{
//			mIVSignal.setBackgroundResource(R.anim.csst_signal);
//		}
		AnimationDrawable signalAnimation = (AnimationDrawable) mIVSignal.getBackground();
		signalAnimation.setOneShot(false);  
		signalAnimation.start();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (!TextUtils.isEmpty(title)){
			builder.setTitle(title);
		}
		if (!TextUtils.isEmpty(message)){
			builder.setMessage(message);
		}
		builder.setView(view);
		builder.setCancelable(false);
		if (!TextUtils.isEmpty(positive)){
			builder.setPositiveButton(positive, positiveCallback);
		}
		if (!TextUtils.isEmpty(negative)){
			builder.setNegativeButton(negative, negativeCallback);
		}
		return builder.create();
	}
	
	/**
	 * 隐藏软键盘
	 * @param context
	 */
	public static final void hideInputKeyBoard(final Context context) {
		((Activity) context).getWindow().getDecorView()
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						((InputMethodManager) context
								.getSystemService(Context.INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(v.getWindowToken(),
										InputMethodManager.HIDE_NOT_ALWAYS);
						return false;
					}
				});
	}
}
