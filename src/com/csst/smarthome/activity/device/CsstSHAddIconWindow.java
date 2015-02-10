package com.csst.smarthome.activity.device;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.csst.smarthome.R;
import com.csst.smarthome.common.ICsstSHInitialize;

/**
 * 添加封面窗体
 * @author liuyang
 */
public final class CsstSHAddIconWindow extends PopupWindow implements ICsstSHInitialize{

	private final Context mContext;
	private View mContentView;
	private LayoutInflater mInflater;
	private View pop_layout = null;
	/** 预设 */
	private Button mPickPreset;
	/** 拍照 */
	private Button mPickTake;
	/** 相册 */
	private Button mPickAlbum;
	/** 取消 */
	private Button mCancel;
	/** 用户预设按钮监听器回调 */
	private View.OnClickListener mPickPersetListener = null;
	/** 用户拍照按钮监听器回调 */
	private View.OnClickListener mPickTackListener = null;
	/** 用户专辑按钮监听器回调 */
	private View.OnClickListener mPickAlbumListener = null;
	/** 用户取消按钮监听器回调 */
	private View.OnClickListener mPickCanceltListener = null;
	/** 默认预设按钮回调 */
	private PerSertListener mPerSertListener = null;
	/** 默认拍照按钮回调 */
	private TackListener mTackListener = null;
	/** 默认专辑按钮回调 */
	private AlbumListener mAlbumListener = null;
	/** 默认取消按钮回调 */
	private CancelListener mCancelListener = null;
	/** 点击关闭窗口监听器 */
	private DismissWindowListener mDismissWindowListener = null;
	
	public CsstSHAddIconWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public CsstSHAddIconWindow(Context context, View root) {
		super(context);
		mContext = context;
		initDataSource();
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
		showAtLocation(root, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	public CsstSHAddIconWindow(Context context, View root, View menu) {
		super(context);
		mContext = context;
		mContentView = menu;
		//设置CsstSHAddIconWindow的View
		setContentView(mContentView);
		// 设置CsstSHAddIconWindow弹出窗体的宽
		setWidth(LayoutParams.MATCH_PARENT);
		// 设置CsstSHAddIconWindow弹出窗体的高
		setHeight(LayoutParams.WRAP_CONTENT);
		// 设置CsstSHAddIconWindow弹出窗体可点击
		setFocusable(true);
		// 设置CsstSHAddIconWindow弹出窗体动画效果
		setAnimationStyle(android.R.style.Animation);
		// 设置CsstSHAddIconWindow弹出窗体的背景
		setBackgroundDrawable(new ColorDrawable(0xb0000000));
		showAtLocation(root, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		mContentView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int height = mContentView.findViewById(R.id.presetview).getTop();
				int y=(int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<height){
						dismiss();
					}
				}				
				return true;
			}
		});
	}

	@Override
	public void initDataSource() {
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContentView = mInflater.inflate(R.layout.csst_add_icon_window_layout, null);
		//设置CsstSHAddIconWindow的View
		setContentView(mContentView);
		//设置CsstSHAddIconWindow弹出窗体的宽
		setWidth(LayoutParams.MATCH_PARENT);
		//设置CsstSHAddIconWindow弹出窗体的高
		setHeight(LayoutParams.WRAP_CONTENT);
		//设置CsstSHAddIconWindow弹出窗体可点击
		setFocusable(true);
		//设置CsstSHAddIconWindow弹出窗体动画效果
		setAnimationStyle(android.R.style.Animation);
		//设置CsstSHAddIconWindow弹出窗体的背景
		setBackgroundDrawable(new ColorDrawable(0xb0000000));
	}

	@Override
	public void initWidget() {
		pop_layout = mContentView.findViewById(R.id.pop_layout);
		mPickPreset = (Button) mContentView.findViewById(R.id.mPickPreset);
		mPickTake = (Button) mContentView.findViewById(R.id.mPickTake);
		mPickAlbum = (Button) mContentView.findViewById(R.id.mPickAlbum);
		mCancel = (Button) mContentView.findViewById(R.id.mCancel);
	}

	@Override
	public void initWidgetState() {
		
	}

	@Override
	public void initWidgetListener() {
		mDismissWindowListener = new DismissWindowListener();
		mPerSertListener = new PerSertListener();
		mTackListener = new TackListener();
		mAlbumListener = new AlbumListener();
		mCancelListener = new CancelListener();
	}

	@Override
	public void addWidgetListener() {
		pop_layout.setOnTouchListener(mDismissWindowListener);
		mPickPreset.setOnClickListener(mPerSertListener);
		mPickTake.setOnClickListener(mTackListener);
		mPickAlbum.setOnClickListener(mAlbumListener);
		mCancel.setOnClickListener(mCancelListener);
	}
	
	public void setPickPersetListener(View.OnClickListener pickPersetListener) {
		this.mPickPersetListener = pickPersetListener;
	}

	public void setPickTackListener(View.OnClickListener pickTackListener) {
		this.mPickTackListener = pickTackListener;
	}

	public void setPickAlbumListener(View.OnClickListener pickAlbumListener) {
		this.mPickAlbumListener = pickAlbumListener;
	}

	public void setPickCanceltListener(View.OnClickListener pickCanceltListener) {
		this.mPickCanceltListener = pickCanceltListener;
	}

	/**
	 * 内置按钮监听器
	 * @author liuyang
	 */
	private final class PerSertListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if (null != mPickPersetListener){
				mPickPersetListener.onClick(v);
			}
			dismiss();
		}
	}
	
	/**
	 * 拍照按钮监听器
	 * @author liuyang
	 */
	private final class TackListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if (null != mPickTackListener){
				mPickTackListener.onClick(v);
			}
			dismiss();
		}
	}
	
	/**
	 * 专辑按钮监听器
	 * @author liuyang
	 */
	private final class AlbumListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if (null != mPickAlbumListener){
				mPickAlbumListener.onClick(v);
			}
			dismiss();
		}
	}
	
	/**
	 * 取消按钮监听器
	 * @author liuyang
	 */
	private final class CancelListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if (null != mPickCanceltListener){
				mPickCanceltListener.onClick(v);
			}
			dismiss();
		}
	}
	
	/**
	 * 关闭窗体事件
	 * @author liuyang
	 */
	private final class DismissWindowListener implements View.OnTouchListener{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int height = mContentView.findViewById(R.id.pop_layout).getTop();
			int y=(int) event.getY();
			if(event.getAction()==MotionEvent.ACTION_UP){
				if(y<height){
					dismiss();
				}
			}				
			return true;
		}
	}
	
}
