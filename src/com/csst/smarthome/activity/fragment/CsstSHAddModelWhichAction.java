package com.csst.smarthome.activity.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.adapter.CsstSHDeviceAdapter;
import com.csst.smarthome.activity.device.CsstSHDeviceManagerActivity;
import com.csst.smarthome.activity.device.CsstSHUseDeviceActivity;
import com.csst.smarthome.activity.device.TriplePanelSwitchActivity;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.bean.CsstSHRoomBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHFloorTable;
import com.csst.smarthome.dao.CsstSHRoomTable;
import com.csst.smarthome.rc.custom.CustomRemoteEditActivity;
import com.csst.smarthome.slidingmenu.SlidingMenu;
import com.csst.smarthome.slidingmenu.SlidingMenu.CanvasTransformer;
import com.csst.smarthome.slidingmenu.app.SlidingActivity;
import com.csst.smarthome.util.AutoScrollTextView;
import com.csst.smarthome.util.CsstSHConfigPreference;

/**
 * 主界面进入类
 * @author liuyang
 */
public class CsstSHAddModelWhichAction extends SlidingActivity implements
		ICsstSHInitialize, ICsstSHConstant {

	public static final String TAG = "CsstSHAddModelWhichAction";
	
	/** 更新view */
	public static final int REFRESH_CONTENT_CMD = 0x00;
	
	/** 修改设备请求命令 */
	public static final int MODIFY_DEVICE_REQUEST_CODE = 0x00;

	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对�?*/
	private SQLiteDatabase mDb = null;
	/** 楼层id */
	private int mFloorId = -1;
	/** 房间id */
	private int mRoomId = -1;
	/** Handler对象 */
	private Handler mHandler = null;
	
	/** 动画效果 */
	private CanvasTransformer mTransformer = null;
	/** 是否打开wifi */
	private ImageView mTvOpenWifi = null;
	/** 设备导航列表 */
	private LinearLayout mDeviceGuideList = null;
	/** 设备列表 */
	private ListView myDeviceListView = null;
	/** 房间列表 */
	private List<CsstSHRoomBean> roomBeans = null;
	/** 设备列表 */
	private List<CsstSHDeviceBean> deviceBeans = null;
	/** 设备导航按键监听�?*/
	private DeviceBuideListener mDeviceBuideListener = null;
	/** 房间切换监听�?*/
	private RoomListener mRoomListener = null;
	/** 设备管理按钮监听�?*/
	private DeviceManagerListener mDeviceManagerListener = null;
	/** 设备监听�?*/
	private DeviceListener mDeviceListener = null;
	/** 房间按钮 */
	private RadioGroup mRoomRadioGroup = null;
	/** 设备列表设配�?*/
	private CsstSHDeviceAdapter deviceAdapter = null;
	/** Inflater */
	private LayoutInflater mInflater = null;
	
	/**
	 * 上层传过来的modelname
	 */
	private String modelName = null;
	private AutoScrollTextView autoText = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csst_whichaction_layout);
		Intent intent = getIntent();
		modelName =(String) intent.getSerializableExtra("modelName");
		initDataSource();
		initWidget();
		initWidgetListener();
		addWidgetListener();
        autoText.setText(getResources().getString(R.string.csst_autotextview_title));  
        autoText.setSpeed(AutoScrollTextView.SPEED_SLOW);
        autoText.startScroll();
	}
	
	@Override
	protected void onResume() {
		initWidgetState();
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case MODIFY_DEVICE_REQUEST_CODE:
				if (RESULT_OK == resultCode){
					//初始化房间对应的设备
					initDeviceState();
				}
				break;
	
			default:
				break;
		}
	}
	
	@Override
	public void initDataSource() {
		mHandler = new StartActivityHandler();
		// 初始数据�?
		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
		// 当前楼层id
		mFloorId = configPreference.getFloorId();
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对�?
		mDb = csstSHDataBase.getWritDatabase();
		// Inflater
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public void initWidget() {
		initSlidingMenu();
		initView();
	}

	@Override
	public void initWidgetState() {
		//初始化房间列�?
		initRoomState();
		//初始化房间对应的设备
		initDeviceState();
	}
	
	private final void initDeviceState(){
		//查询房间对应的设备列�?
		deviceBeans = CsstSHRoomTable.getInstance().getRoomDevices(mDb, mRoomId);
		//初始化适配�?
		deviceAdapter=new CsstSHDeviceAdapter(this, deviceBeans);
		//设置适配�?
		myDeviceListView.setAdapter(deviceAdapter);
		//导航容器
//		mDeviceGuideList.removeAllViews();
//		Button device = null;
//		if (null != deviceBeans && !deviceBeans.isEmpty()){
//			mDeviceBuideListener = new DeviceBuideListener();
//			for (int i = 0; i < deviceBeans.size(); i++){
//				device = (Button) mInflater.inflate(R.layout.csst_device_guide_button, null);
//				device.setId(deviceBeans.get(i).getDeviceId());
//				device.setTag(deviceBeans.get(i));
//				device.setText(deviceBeans.get(i).getDeviceName());
//				device.setOnClickListener(mDeviceBuideListener);
//				mDeviceGuideList.addView(device);
//			}
//		}
	}
	
	/**
	 * 初始化房间状�?
	 */
	private final void initRoomState(){
		// 当前房间id
		mRoomId = configPreference.getRoomId();
		
		//查询当前楼层下的房间列表
		roomBeans=CsstSHFloorTable.getInstance().getFloorRooms(mDb, mFloorId);
		//清空所有房�?
		if(roomBeans!=null){
			System.out.println(TAG+" the device room is initRoomState11 "+mRoomId+" the roomBeans size is"+roomBeans.size());
		}
		
		mRoomRadioGroup.removeAllViews();
		//当前场景下不存在房间数据
		if(null == roomBeans || roomBeans.isEmpty()){
			return;
		}
		for (int i = 0; i < roomBeans.size(); i++){
			if (mRoomId < 0){
				System.out.println(TAG+" the device room is initRoomState 22 "+mRoomId);
				mRoomId = roomBeans.get(i).getRoomId();
				configPreference.setRoomId(mRoomId);
			}
			//创建房间按钮
			RadioButton rb = createRoomBtnView(roomBeans.get(i));
			//设置房间选择状�?
			rb.setChecked(mRoomId == roomBeans.get(i).getRoomId());
			//将房间添加到房间列表�?
			mRoomRadioGroup.addView(rb);
		}
	}

	@Override
	public void initWidgetListener() {
		mDeviceListener = new DeviceListener();
		//切换房间监听�?
		mRoomListener = new RoomListener();
		mDeviceManagerListener = new DeviceManagerListener();
	}

	@Override
	public void addWidgetListener() {
		//设备监听�?
		myDeviceListView.setOnItemClickListener(mDeviceListener);
		//切换房间
		mRoomRadioGroup.setOnCheckedChangeListener(mRoomListener);
	}
	
	/**
	 * 初始化菜�?
	 */
	private final void initSlidingMenu(){
		// 设置左边的布局
		setBehindContentView(R.layout.csst_menu_left_frame);
		// 替换左边布局
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.menu_left_frame, new CsstSHWhichActionFloorFragment())
		.commit();
		/** 设置动画效果 */
		mTransformer = new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scaleX = (float) (percentOpen * 0.25 + 0.75);
				float scaleY = (float) (percentOpen * 0.25 + 0.75);
				canvas.scale(scaleX, scaleY, -canvas.getWidth() / 2,
						canvas.getHeight() / 2);
			}
		};
		/** 获取左滑的菜�?*/
		SlidingMenu sm = getSlidingMenu();
		/** 左右滑动 */
		sm.setMode(SlidingMenu.LEFT);
		/** 设置右边菜单布局 */
//		sm.setSecondaryMenu(R.layout.csst_menu_right_frame);
		sm.setFadeDegree(0.65f);
		/** 设置SlidingMenu菜单的宽�?*/
		sm.setBehindWidth(300);
		/** 设置滑动的区�?*/
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		/** 设置菜单的宽 */
		//sm.setBehindWidth(200);
		sm.setBackgroundResource(R.drawable.csst_background);// 设置背景
		setSlidingActionBarEnabled(true);
		sm.setBehindScrollScale(0.0f);
		sm.setBehindCanvasTransformer(mTransformer);
		// getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * 初始化控�?
	 */
	private final void initView() {
		mTvOpenWifi = (ImageView) findViewById(R.id.iv_netstat);
//		mDeviceGuideList = (LinearLayout) findViewById(R.id.mDeviceGuideList);
		autoText =(AutoScrollTextView)findViewById(R.id.autoTxt);  
		myDeviceListView = (ListView) findViewById(R.id.deviceitemList);
		mRoomRadioGroup = (RadioGroup)findViewById(R.id.rg_roomlist);
	}

	/**
	 * 创建房间按钮
	 * @param room
	 * @return
	 */
	private final RadioButton createRoomBtnView(CsstSHRoomBean room){
		RadioButton mRBRoom = (RadioButton) mInflater.inflate(R.layout.csst_room_item, null);

		 DisplayMetrics displayMetrics = new DisplayMetrics();       
		 getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);         
		 int displayHeight = displayMetrics.heightPixels; 
		 
		 LinearLayout.LayoutParams paramsradiobutton= new LinearLayout.LayoutParams(       
				 (int) (displayHeight * 0.1f+0.5f),
				 (int) (displayHeight * 0.08f+0.5f)); 
//		 paramsradiobutton.setMargins((int) (displayHeight * 0.01f+0.5f), (int) (displayHeight * 0.01f+0.5f), (int) (displayHeight * 0.01f+0.5f), (int) (displayHeight * 0.01f+0.5f));
		 mRBRoom.setLayoutParams(paramsradiobutton);
		
		mRBRoom.setTag(room);
		mRBRoom.setId(room.getRoomId());
		mRBRoom.setText(room.getRoomName());
		return mRBRoom;
	}

	/**
	 * 更新界面
	 */
	public final void refreshView() {
		initWidgetState();
		mHandler.sendEmptyMessageDelayed(REFRESH_CONTENT_CMD, 50);
	}

	/**
	 * 获取数据库对�?
	 * @return
	 */
	public final SQLiteDatabase getDataBase() {
		return mDb;
	}
	
	/**
	 * 当前使用的楼层id
	 * @return
	 */
	public final int getCurFloorId(){
		mFloorId = configPreference.getFloorId();
		return mFloorId;
	}
	
	/**
	 * 设置楼层id
	 * @param floorid
	 */
	public final void setCurFloorId(int floorid){
		mFloorId = floorid;
		configPreference.setFloorId(floorid);
	}
	
	
	/**
	 * 使用设备
	 */
	private final void useDevice(CsstSHDeviceBean device){
		
		
		
		
//		RadioButton rb;
//		rb=(RadioButton)findViewById(mRoomRadioGroup.getCheckedRadioButtonId());
//		
//		
//		
////		System.out.println(TAG+" the device floor is useDevice(CsstSHDeviceBean device) "+getCurFloorId());
////		System.out.println(TAG+" the device room is useDevice(CsstSHDeviceBean device) "+rb.getText());
////		(RadioButton)mRoomRadioGroup.getChildAt(mRoomId);
//		Intent intent = new Intent(this, CsstSHUseDeviceActivity.class);
//		intent.putExtra("device", device);
//		intent.putExtra("whichaction", "whichaction");
//		System.out.println(TAG+" the device floor is 22");
//		String location = CsstSHFloorTable.getInstance().query(mDb, mFloorId).getFloorName()+rb.getText().toString();;
////		String location = getCurFloorId()+ rb.getText();
//		System.out.println(TAG+" the device room location "+location);
//		
//		intent.putExtra("location",location );
//		startActivity(intent);
		if(device.getDeviceIconPath()!=null){//没有图标的就是自定义或者是模板遥控
		if(device.getDeviceIconPath().equals(getResources().getString(R.string.csst_adddevice_switch))){//面板开关
			Intent intent;
			if(device.getRCTypeId()==0x01){//一位面板开关 就进入插座一样的界面
				 intent = new Intent(CsstSHAddModelWhichAction.this, Socket_Action.class);	
			}else{
				intent = new Intent(CsstSHAddModelWhichAction.this, TriplePanelSwitchAction.class);	
			}
			
			intent.putExtra("modelName", modelName);
			intent.putExtra("device", device);
			intent.putExtra("whichaction", "whichAction");
			System.out.println(TAG+" the device floor is 22");
			String location = CsstSHFloorTable.getInstance().query(mDb, mFloorId).getFloorName()+"/"+CsstSHRoomTable.getInstance().query(mDb, mRoomId).getRoomName()+"/";
			System.out.println(TAG+" the device room location "+location);
			intent.putExtra("location",location );
			CsstSHAddModelWhichAction.this.finish();
			startActivity(intent);
		} else if(device.getDeviceIconPath().equals(getResources().getString(R.string.csst_adddevice_charge))){//面板开关插座
			//判断是不是绑定插座
			//是 就进入系统进入遥控面板
			if(device.isSearched()==1){//普通遥控器
				Intent intent = new Intent(CsstSHAddModelWhichAction.this, CsstSHUseDeviceActivity.class);
				intent.putExtra("modelName", modelName);
				intent.putExtra("device", device);
				intent.putExtra("whichaction", "whichAction");
				System.out.println(TAG+" the device floor is 22");
				String location = CsstSHFloorTable.getInstance().query(mDb, mFloorId).getFloorName()+"/"+CsstSHRoomTable.getInstance().query(mDb, mRoomId).getRoomName()+"/";
				System.out.println(TAG+" the device room location "+location);
				intent.putExtra("location",location );
				CsstSHAddModelWhichAction.this.finish();
				startActivity(intent);
			}else{
				Intent intent = new Intent(CsstSHAddModelWhichAction.this, Socket_Action.class);
				intent.putExtra("modelName", modelName);
				intent.putExtra("device", device);
				intent.putExtra("whichaction", "whichAction");
				System.out.println(TAG+" the device floor is 22");
				String location = CsstSHFloorTable.getInstance().query(mDb, mFloorId).getFloorName()+"/"+CsstSHRoomTable.getInstance().query(mDb, mRoomId).getRoomName()+"/";
				System.out.println(TAG+" the device room location "+location);
				intent.putExtra("location",location );
				CsstSHAddModelWhichAction.this.finish();
				startActivity(intent);
			}
			
		}else if(device.getRCTypeId()==8){//说明是自定义遥控
			Intent intent = new Intent(this, CustomRemoteEditActivity.class);
			intent.putExtra("modelName", modelName);
			intent.putExtra("device", device);
			intent.putExtra("whichaction", "whichAction");
			String location = CsstSHFloorTable.getInstance().query(mDb, mFloorId).getFloorName()+"/"+CsstSHRoomTable.getInstance().query(mDb, mRoomId).getRoomName()+"/";
			System.out.println(TAG+" the device room location "+location);
			intent.putExtra("location",location );
			CsstSHAddModelWhichAction.this.finish();
			startActivity(intent);
		}else{//普通遥控器
			Intent intent = new Intent(CsstSHAddModelWhichAction.this, CsstSHUseDeviceActivity.class);
			intent.putExtra("modelName", modelName);
			intent.putExtra("device", device);
			intent.putExtra("whichaction", "whichAction");
			System.out.println(TAG+" the device floor is 22");
			String location = CsstSHFloorTable.getInstance().query(mDb, mFloorId).getFloorName()+"/"+CsstSHRoomTable.getInstance().query(mDb, mRoomId).getRoomName()+"/";
			System.out.println(TAG+" the device room location "+location);
			intent.putExtra("location",location );
			CsstSHAddModelWhichAction.this.finish();
			startActivity(intent);
		}
		
		}else{
			if(device.getRCTypeId()==8){//说明是自定义遥控
				Intent intent = new Intent(this, CustomRemoteEditActivity.class);
				intent.putExtra("modelName", modelName);
				intent.putExtra("device", device);
				intent.putExtra("whichaction", "whichAction");
				String location = CsstSHFloorTable.getInstance().query(mDb, mFloorId).getFloorName()+"/"+CsstSHRoomTable.getInstance().query(mDb, mRoomId).getRoomName()+"/";
				System.out.println(TAG+" the device room location "+location);
				intent.putExtra("location",location );
				CsstSHAddModelWhichAction.this.finish();
				startActivity(intent);
			}else{//普通遥控器
				Intent intent = new Intent(CsstSHAddModelWhichAction.this, CsstSHUseDeviceActivity.class);
				intent.putExtra("modelName", modelName);
				intent.putExtra("device", device);
				intent.putExtra("whichaction", "whichAction");
				System.out.println(TAG+" the device floor is 22");
				String location = CsstSHFloorTable.getInstance().query(mDb, mFloorId).getFloorName()+"/"+CsstSHRoomTable.getInstance().query(mDb, mRoomId).getRoomName()+"/";
				System.out.println(TAG+" the device room location "+location);
				intent.putExtra("location",location );
				CsstSHAddModelWhichAction.this.finish();
				startActivity(intent);
			}
			
		}
		
		
		
		
		
		
	
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 销毁时关闭数据�?
		if (mDb != null)
			mDb.close();
	}
	
	/**
	 * Handler实体
	 * @author liuyang
	 */
	@SuppressLint("HandlerLeak")
	private final class StartActivityHandler extends android.os.Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case REFRESH_CONTENT_CMD:
					getSlidingMenu().showContent();
					break;
	
				default:
					break;
			}
		}
	}
	
	/**
	 * 设备管理按钮监听�?
	 * @author liuyang
	 */
	private final class DeviceManagerListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			Intent inent = new Intent(CsstSHAddModelWhichAction.this, CsstSHDeviceManagerActivity.class);
			startActivity(inent);
		}
	}
	/**
	 * 设备监听�?
	 * @author liuyang
	 */
	private final class DeviceListener implements AdapterView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			useDevice((CsstSHDeviceBean) view.getTag());
		}
	}
	
	/**
	 * 设备导航按键监听�?
	 * @author liuyang
	 */
	private final class DeviceBuideListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			System.out.println(TAG+" the device floor is "+getCurFloorId());
			useDevice((CsstSHDeviceBean) v.getTag());
		}
	}
	
	/**
	 * 房间切换监听�?
	 * @author liuyang
	 */
	private final class RoomListener implements RadioGroup.OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			//点击当前正在使用的房间，直接返回
			if (mRoomId == checkedId){
				return;
			}
			System.out.println("select room is checkedId is "+checkedId+"  current mRoomId is"+mRoomId);
			//修改当前选择的房间id
			mRoomId = checkedId;
			//存储当前操作的房�?
			configPreference.setRoomId(mRoomId);
			//更新设备列表
			initDeviceState();
		}
	}
	
	
}
