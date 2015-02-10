package com.csst.smarthome.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.adapter.CsstSHDeviceAdapter;
import com.csst.smarthome.activity.camera.CsstSHCameraActivity;
import com.csst.smarthome.activity.device.CsstSHAddDeviceActivityZQL;
import com.csst.smarthome.activity.device.CsstSHDeviceManagerActivity;
import com.csst.smarthome.activity.device.CsstSHUseDeviceActivity;
import com.csst.smarthome.activity.device.TriplePanelSwitchActivity;
import com.csst.smarthome.activity.floor.CsstSHFloorFragment;
import com.csst.smarthome.activity.fragment.CsstSHRightFragment;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.bean.CsstSHRoomBean;
import com.csst.smarthome.bean.CsstSHSwitchBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHDeviceTable;
import com.csst.smarthome.dao.CsstSHFloorTable;
import com.csst.smarthome.dao.CsstSHRoomTable;
import com.csst.smarthome.dao.CsstSHSwitchTable;
import com.csst.smarthome.rc.custom.CustomRemoteActivity;
import com.csst.smarthome.rc.custom.CustomRemoteEditActivity;
import com.csst.smarthome.safe.CsstSHSaftListView;
import com.csst.smarthome.slidingmenu.SlidingMenu;
import com.csst.smarthome.slidingmenu.SlidingMenu.CanvasTransformer;
import com.csst.smarthome.slidingmenu.app.SlidingActivity;
import com.csst.smarthome.util.AutoScrollTextView;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.encryption.BaseCodeMessage;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.net.UdpJavaEncodingProcess;

/**
 * 主界面进入类
 * @author liuyang
 */
public class SmartStartActivity extends SlidingActivity implements
		ICsstSHInitialize, ICsstSHConstant {

	public static final String TAG = "SmartStartActivity";
	
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
	/** 主控MAC地址 */
	public static String mMacAdress = null;
	
	/**
	 * 利用查询如果三次参数温度不存在就提示设备离线
	 */
	private int moffLineflag = 0;
	/** 房间id */
	private int mRoomId = -1;
	/** Handler对象 */
	private Handler mHandler = null;
	
	/** 动画效果 */
	private CanvasTransformer mTransformer = null;
	/** 是否打开wifi */
	private ImageView mTvOpenWifi = null;
	/**
	 * 滚动字幕
	 */
	private AutoScrollTextView autoText = null;
	/** 添加设备 */
	private RelativeLayout mRlAddDevice = null;
	/** 添加房间 */
	private RelativeLayout mRlAddRoom = null;
	/** 设备导航列表 */
//	private LinearLayout mDeviceGuideList = null;
	/** 设备列表 */
	private ListView myDeviceListView = null;
	/** 设备管理 */
	private Button mBtnDeviceManage = null;
	/** 视频监控 */
	private Button mBtnCamera = null;
	/** 运行管理 */
	private Button mBtnShiPin = null;
	/** 定时设置 */
	private Button mBtnSafeManager = null;
	//air 空气的been
	private Button air_been = null;
	//air flag 
	private int air_flag = 0 ;
	/** 房间列表 */
	private List<CsstSHRoomBean> roomBeans = null;
	/** 设备列表 */
	private List<CsstSHDeviceBean> deviceBeans = null;
	/** 按键监听�?*/
	private BtnListener mBtnListener = null;
	/** 视频监控�?*/
	private CameraBtnListener mCameraBtnListener = null;
	/** 设备监听�?*/
	private DeviceListener mDeviceListener = null;
	/** 设备导航按键监听�?*/
	private DeviceBuideListener mDeviceBuideListener = null;
	/** 添加设备监听�?*/
	private AddDeviceListener mAddDeviceListener = null;
	/** 添加房间按钮监听�?*/
	private AddRoomBtnListener mAddRoomBtnListener = null;
	/** 房间切换监听�?*/
	private RoomListener mRoomListener = null;
	/** 房间修改监听�?*/
	private RoomItemListener mRoomItemListener = null;
	/** 修改设备监听�?*/
	private DeviceModifyListener mDeviceModifyListener = null;
	/** 修改设备监听�?*/
	private DeviceBuideModifyListener mDeviceBuideModifyListener = null;
	/** 设备管理按钮监听�?*/
	private DeviceManagerListener mDeviceManagerListener = null;
	/** 房间按钮 */
	private RadioGroup mRoomRadioGroup = null;
	/** 设备列表设配�?*/
	private CsstSHDeviceAdapter deviceAdapter = null;
	/** Inflater */
	private LayoutInflater mInflater = null;
	
	

	/** Inflater */
	private TextView mTVtempValue,mTVshiduValue,mTVjiaquanValue,mTVjiaquanIMG,mTVjiaquanTEXT;
	private TextView mImagejiaqua ;
	/**
	 * clock
	 */
	
	private SharedPreferences mPrefs;
	ProgressDialog myprogress;
	
	private static int MSG_LEGHTE_SEND = 7;
	private static int MSG_UID_LOW_RECIVE = 2;
	private static int MSG_UID_HIGHT_RECIVE = 3;
	private static int MSG_TYPE_RECIVE = 4;
	private static int MSG_STATUE_RECIVE = 5;
	
	private final int QUERYTEMP=1;
	private final int SENDCMD=2;
	private final int SENDCMD_AIR=3;
	
	//用来存储当前控制插座或者面板开关的变量
	private CsstSHDeviceBean curDeviceBean = null;
	//用来存储当前面板想对应的开�?
	private CsstSHSwitchBean curswitchbean =null;
	
	//用来计算当前插座的数�?
	private List<CsstSHDeviceBean> curListSocketBean =null;
	
	//用来存储当前房间所有面板开�?
	private List<CsstSHDeviceBean> curListSwitchBean =null;
	//计算出所有需要查询的次数 包括�?次温�?listsocket.size()+listswitch.size()�?
	//initDeviceState 进行计算 需要重新计�?
	private int queryTotalTimes = 0;
	//当前查询位置 在重新进入的话就会清�?
	private int queryTimes = 0;
	//查询现在是查询谁�?
	private final int ISTEMP =1;
	private final int ISSOCKET =2;
	private final int ISSWITCH = 3;
	
	//air level
	private final int AIR_GOOD =0;
	private final int AIR_MID =1;
	private final int AIR_BAD = 2;
	private final int AIR_MIDBAD = 3;
	private final int AIR_VERYBAD = 4;
	

			
	byte contentBuffer[] = new byte[8];
	//因为接收的数据为不定长度�?需要定义一个比较大的buffer 进行全局变量的设�?
	byte cmdcontentbuffer[] = new byte[256];
	
	byte ssuidLow = 0x00;
	
	byte ssuidHight = 0x00;
	byte ssstatus = 0x00;
	byte sendcmd = 0x00;
	
	private boolean startthread_flag = true;
// timer
	private final Timer timer=new Timer();
	private TimerTask tast;
	
//	 seek send cmd timer is here;
	private Timer timerconfigwifi;
	private TimerTask tastconfigwifi;
	private Handler handlerconfigwifi;
	
	private boolean debug = true;
	//d定时使用的数�?
	public static final String PREFERENCES = "AlarmClock";
	
	//用来启动和停止查询温湿度线程  退出的时候为假回来的时候为�?
	boolean sendflag = true;
	//使用线程进行获取温度状�?
	public boolean getTempflag_Thread =true;
	
	private boolean getTempflag_Thread_main = true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(debug){
			System.out.println(TAG+" onCreate");
		}
		setContentView(R.layout.csst_start_layout);
		initDataSource();
		initWidget();
		initWidgetListener();
		addWidgetListener();
		if(debug){
			System.out.println(TAG+" init finish ");
		}
		//显示滚动广告
        autoText.setText(getResources().getString(R.string.csst_autotextview_title));  
        autoText.setSpeed(AutoScrollTextView.SPEED_SLOW);
        autoText.startScroll();
        LoadLayout();
        
        
//		sendflag =true;
//		new UpdateTempe().execute();
	}
	
	@Override
	protected void onResume() {
		//暂停的时候后退出的时候是它是false ，回来之后需要开启接收数据的线程
		if(!getTempflag_Thread){
			  getTempflag_Thread = true;
			}
		initWidgetState();
		//开启温度查�?和开关状态查�?
//		sendflag = true;
//		new UpdateTempe().execute();
		
		System.out.println(TAG+"onResume");
		super.onResume();
		
		new getStatues((byte)0x00).execute();  
	}
	@Override
	protected void onPause(){
		//开启温度查�?和开关状态查�?
//		sendflag = false;
//		new UpdateTempe().cancel(true);
		System.out.println(TAG+"onPause");
		super.onPause();
	}
	protected void onRestart(){
//		sendflag = true;
//		new UpdateTempe().execute();
		System.out.println(TAG+"onRestart");
		super.onRestart();	
	}
	
	protected void onStop(){
		getTempflag_Thread = false;
//		sendflag = false;
//		new UpdateTempe().cancel(true);
		System.out.println(TAG+"onStop");
		super.onStop();	
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
		//读取当前主控MAC地址
		mMacAdress = configPreference.getMacAdress();
		if(debug){
			System.out.println("the mMacAdress is "+mMacAdress);
		}
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对�?
		mDb = csstSHDataBase.getWritDatabase();
		// Inflater
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		/**
		 * Clock 
		 */
        //取getSharedPreferences中key==“AlarmClock”的�?
        mPrefs = getSharedPreferences(PREFERENCES, 0);
        //初始化当前房间的插座和面�?
        curListSocketBean = new ArrayList<CsstSHDeviceBean>();
        curListSwitchBean = new ArrayList<CsstSHDeviceBean>();
        configwifitimer();
//        getUpdataTem_thread = new getUpdatatemp();
//        getTempflag_Thread = true;
//        getUpdataTem_thread.start();
        
	}

	@Override
	public void initWidget() {
		mRoomItemListener = new RoomItemListener();
		initSlidingMenu();
		initView();
	}

	@Override
	public void initWidgetState() {
		//读取当前主控MAC地址
		mMacAdress = configPreference.getMacAdress();
		if(debug){
			System.out.println("the mMacAdress is "+mMacAdress);
		}
		
//		//如果是默认的mac 地址就不开启线�?
//		if(!mMacAdress.equals(ICsstSHConstant.CONTROL_MAC_ADDR_DEFAUL)){
//			if(startthread_flag){
//				getUpdataTem_thread.start();
//				startthread_flag = false;
//			}
//		}
		
		//初始化房间列
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
		//清零总次数和当前位置 //在获取到 数据的时候也要进行界面的更新 所以在查询的界面更新需要二外的设定
		queryTimes =0;
		queryTotalTimes = 0;
		curListSocketBean.clear();
		curListSwitchBean.clear();
		//导航容器
		if(deviceBeans!=null){
			if(debug){
				System.out.println("the deviceBeans size is  "+deviceBeans.size());
			}	
			
			//获取查询次数queryTimes
			
			for(int i=0;i<deviceBeans.size();i++){
				//在不为空的情况下进行查询
				if(deviceBeans.get(i).getDeviceIconPath()!=null){
					if(deviceBeans.get(i).getDeviceIconPath().equals(getResources().getString(R.string.csst_adddevice_charge))){
						System.out.println("get socket is here "+i);
						curListSocketBean.add(deviceBeans.get(i));
					}
					else if(deviceBeans.get(i).getDeviceIconPath().equals(getResources().getString(R.string.csst_adddevice_switch))){
						System.out.println("get switch is here "+i);
						curListSwitchBean.add(deviceBeans.get(i));
					}
				}
				
			}
			//计算总的查询次数
			queryTotalTimes = curListSocketBean.size()+curListSwitchBean.size();
		}
		
		//如果是默认的mac 地址就不开启线�?
		if(!mMacAdress.equals(ICsstSHConstant.CONTROL_MAC_ADDR_DEFAUL)){
			if(startthread_flag){
//				getUpdataTem_thread.start();
				startthread_flag = false;
			}
		}

		
		
		
		
		
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
//				device.setOnLongClickListener(mDeviceBuideModifyListener);
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
				
		mRoomRadioGroup.removeAllViews();
		//当前场景下不存在房间数据
		if(null == roomBeans || roomBeans.isEmpty()){
			return;
		}
		for (int i = 0; i < roomBeans.size(); i++){
			if (mRoomId < 0){
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
		mBtnListener = new BtnListener();
		mDeviceListener = new DeviceListener();
		mCameraBtnListener = new CameraBtnListener();
		//添加设备监听�?
		mAddDeviceListener = new AddDeviceListener();
		//添加房间监听�?
		mAddRoomBtnListener = new AddRoomBtnListener();
		//切换房间监听�?
		mRoomListener = new RoomListener();
		
		mDeviceModifyListener = new DeviceModifyListener();
		mDeviceBuideModifyListener = new DeviceBuideModifyListener();
		
		mDeviceManagerListener = new DeviceManagerListener();
	}

	@Override
	public void addWidgetListener() {
		mTvOpenWifi.setOnClickListener(mBtnListener);
		mBtnDeviceManage.setOnClickListener(mDeviceManagerListener);
		mBtnShiPin.setOnClickListener(mBtnListener);
		mBtnCamera.setOnClickListener(mCameraBtnListener);
		mBtnSafeManager.setOnClickListener(mBtnListener);
		//设备监听�?
		myDeviceListView.setOnItemClickListener(mDeviceListener);
		myDeviceListView.setOnItemLongClickListener(mDeviceModifyListener);
		//添加设备
		mRlAddDevice.setOnClickListener(mAddDeviceListener);
		//添加房间
		mRlAddRoom.setOnClickListener(mAddRoomBtnListener);
		//切换房间
		mRoomRadioGroup.setOnCheckedChangeListener(mRoomListener);
		air_been.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(air_flag==0){
					air_been.setBackgroundDrawable((SmartStartActivity.this.getResources().getDrawable(R.drawable.air_been_open)));
					new sendAirBeen((byte)0x00).cancel(true);
					new sendAirBeen((byte)0x00).execute();
					air_flag = 1;
				}else{
					air_been.setBackgroundDrawable((SmartStartActivity.this.getResources().getDrawable(R.drawable.air_been_off)));
					new sendAirBeen((byte)0x01).cancel(true);
					new sendAirBeen((byte)0x01).execute();
					air_flag = 0;
				}
				
			}
		});
		
	}
	
	/**
	 * 初始化菜�?
	 */
	private final void initSlidingMenu(){
		// 设置左边的布局
		setBehindContentView(R.layout.csst_menu_left_frame);
		// 替换左边布局
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_left_frame, new CsstSHFloorFragment())
				.commit();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_right_frame, new CsstSHRightFragment())
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
		sm.setMode(SlidingMenu.LEFT_RIGHT);
		/** 设置右边菜单布局 */
		sm.setSecondaryMenu(R.layout.csst_menu_right_frame);
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
		mRlAddDevice = (RelativeLayout) findViewById(R.id.rl_adddevice);
//		mDeviceGuideList = (LinearLayout) findViewById(R.id.mDeviceGuideList);
		autoText =(AutoScrollTextView)findViewById(R.id.autoTxt);  
		mRlAddRoom = (RelativeLayout) findViewById(R.id.rl_addroom);
		myDeviceListView = (ListView) findViewById(R.id.deviceitemList);
		mBtnDeviceManage = (Button) findViewById(R.id.btn_devicemanager);
		mBtnCamera = (Button) findViewById(R.id.btn_cameramanager);
		mBtnShiPin = (Button) findViewById(R.id.btn_videomonitoring);
		mBtnSafeManager = (Button) findViewById(R.id.btn_safemanager);
		mRoomRadioGroup = (RadioGroup)findViewById(R.id.rg_roomlist);
		mTVtempValue = (TextView)findViewById(R.id.tvtempvalue);
		mTVshiduValue = (TextView)findViewById(R.id.tvshiduvalue);
		mTVjiaquanValue = (TextView)findViewById(R.id.tvjiaquanvalue);
		
		mTVjiaquanIMG = (TextView)findViewById(R.id.jiaquan);
		mTVjiaquanTEXT = (TextView)findViewById(R.id.jiaquan_level);
		
		mImagejiaqua = (TextView)findViewById(R.id.jiaquan);
		
		air_been  = (Button)findViewById(R.id.air_been);
		
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
		 
//		 LinearLayout.LayoutParams paramsradiobutton= new LinearLayout.LayoutParams(       
//				 (int) (displayHeight * 0.1f+0.5f),
//				 (int) (displayHeight * 0.08f+0.5f)); 
		 
		 LinearLayout.LayoutParams paramsradiobutton= new LinearLayout.LayoutParams(       
				 LayoutParams.WRAP_CONTENT,
				 (int) (displayHeight * 0.08f+0.5f)); 
		 mRBRoom.setLayoutParams(paramsradiobutton);
		
		
		
		mRBRoom.setTag(room);
		mRBRoom.setId(room.getRoomId());
		mRBRoom.setText(room.getRoomName());
		mRBRoom.setOnLongClickListener(mRoomItemListener);
		return mRBRoom;
	}

	/**
	 * 更新界面
	 */
	public final void refreshView() {
		if(!getTempflag_Thread){
			  getTempflag_Thread = true;
			}
		initWidgetState();
		mHandler.sendEmptyMessageDelayed(REFRESH_CONTENT_CMD, 50);
	}

	/**
	 * 获取数据库对
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
	 * 删除房间
	 * @param room
	 */
	private final void deleteRoom(CsstSHRoomBean room){
		//从房间列表中移除房间
		mRoomRadioGroup.removeView(mRoomRadioGroup.findViewById(room.getRoomId()));
		//删除房间下的设备
		CsstSHDeviceTable.getInstance().deleteByRoom(mDb, room.getRoomId());
		//从数据库中删除房�?
		CsstSHRoomTable.getInstance().delete(mDb, room);
		//更新当前房间id
		roomBeans.remove(room);
		System.out.println("deleteRoom  11 the mRoomId is "+mRoomId);
		if (mRoomId == room.getRoomId()){
			System.out.println("deleteRoom  22 the mRoomId is "+mRoomId);
			if (roomBeans.isEmpty()){
				mRoomId = -1;
			}else{
				System.out.println("deleteRoom  33 the mRoomId is "+mRoomId);
				mRoomId = roomBeans.get(0).getRoomId();
			}
			System.out.println("deleteRoom  44 the mRoomId is "+mRoomId);
			configPreference.setRoomId(mRoomId);
		}
		refreshView();
	}
	
	

	/**
	 * 修改房间
	 * @param room
	 */
	private final void modifychargeAndSwitch(final CsstSHDeviceBean device){
		//显示天剑房间对话�?
		final EditText inputServer = new EditText(SmartStartActivity.this);
		inputServer.setText(device.getDeviceName());
		inputServer.setSelection(device.getDeviceName().length());
		AlertDialog.Builder builder = new AlertDialog.Builder(SmartStartActivity.this);
		builder.setTitle(R.string.csst_devicemodify_devicename_inputtip);
		builder.setView(inputServer);
		builder.setNegativeButton(R.string.csst_cancel, null);
		builder.setPositiveButton(R.string.csst_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String devicename = inputServer.getText().toString();
				//房间名为�?
				if (TextUtils.isEmpty(devicename)){
					Toast.makeText(SmartStartActivity.this, R.string.csst_devicemodify_devicename_tipnull, Toast.LENGTH_LONG).show();
					return;
				}
				//新建设备同一房间存在相同的设备返�?
				if (CsstSHDeviceTable.getInstance().roomExisSameDevice(mDb, devicename, mRoomId)){
					if(!devicename.endsWith(device.getDeviceName())){
						Toast.makeText(SmartStartActivity.this, R.string.csst_home_exists_device, Toast.LENGTH_LONG).show();
					}
					return;
				}
				device.setDeviceName(devicename);
				CsstSHDeviceTable.getInstance().update(mDb, device);
				refreshView();
			}
		});
		builder.show();
	}
	
	
	
	
	
	
	/**
	 * 修改房间
	 * @param room
	 */
	private final void modifyRoom(final CsstSHRoomBean room){
		//显示天剑房间对话�?
		final EditText inputServer = new EditText(SmartStartActivity.this);
		inputServer.setText(room.getRoomName());
		inputServer.setSelection(room.getRoomName().length());
		AlertDialog.Builder builder = new AlertDialog.Builder(SmartStartActivity.this);
		builder.setTitle(R.string.csst_please_input_roomname);
		builder.setIcon(R.drawable.csst_floor);
		builder.setView(inputServer);
		builder.setNegativeButton(R.string.csst_cancel, null);
		builder.setPositiveButton(R.string.csst_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String roomName = inputServer.getText().toString();
				//房间名为�?
				if (TextUtils.isEmpty(roomName)){
					Toast.makeText(SmartStartActivity.this, R.string.csst_floorname_not_null, Toast.LENGTH_LONG).show();
					return;
				}
				//同一场景下是否存在同一房间名称
				if (CsstSHRoomTable.getInstance().floorExistsSameRoom(mDb, roomName, mFloorId)){
					Toast.makeText(SmartStartActivity.this, R.string.csst_same_room_message, Toast.LENGTH_LONG).show();
					return;
				}
				//修改房间名称
				room.setRoomName(roomName);
				CsstSHRoomTable.getInstance().update(mDb, room);
				//修改显示名称
				RadioButton rb = (RadioButton) mRoomRadioGroup.findViewById(room.getRoomId());
				rb.setText(roomName);
			}
		});
		builder.show();
	}
	
	/**
	 * 使用设备
	 */
	private final void useDevice(CsstSHDeviceBean device){
		//按下图标之后要跳转界面先�?
		//在插座的时候需�?在接收到发送数�?或者没收到接收数据都需要恢复这个标志位 getTempflag_Thread onPostExecute函数�?
		if(!isOffLine()){
		getTempflag_Thread = false;
		System.out.println("the getDeviceIconPath is   "+device.getDeviceIconPath());
		if(device.getDeviceIconPath()!=null){//没有图标的就�?
			if(device.getDeviceIconPath().equals(getResources().getString(R.string.csst_adddevice_switch))){
				if(device.getRCTypeId()==0x01){//一位面板开光可以直接在主界面控�?
					curDeviceBean = device;
					ssuidLow=(byte)Integer.parseInt(curDeviceBean.getDeviceSSID().split(",")[0]);
					ssuidHight=(byte)Integer.parseInt(curDeviceBean.getDeviceSSID().split(",")[1]);
					new sendChargCmd((byte)0x00).cancel(true);
					if(device.isDeviceIconPersert()){
						new sendChargCmd((byte)0x10).execute();
					}else{
						new sendChargCmd((byte)0x11).execute();
					}
				}else{
					Intent intent = new Intent(this, TriplePanelSwitchActivity.class);
					intent.putExtra("switchname", device.getDeviceName());
					intent.putExtra("device", device);
					startActivity(intent);
				}
				
				
				
				
			} else if(device.getDeviceIconPath().equals(getResources().getString(R.string.csst_adddevice_charge))){
				if(device.isSearched()==1){//判断是绑定的遥控器模�?
					//定义的指定模板的遥控�?
					Intent intent = new Intent(this, CsstSHUseDeviceActivity.class);
					intent.putExtra("modelName", "null");
					intent.putExtra("device", device);
					intent.putExtra("whichaction", "nowhichaction");
					intent.putExtra("location", "nolocation");
					startActivity(intent);
				}else{
					curDeviceBean = device;
					ssuidLow=(byte)Integer.parseInt(curDeviceBean.getDeviceSSID().split(",")[0]);
					ssuidHight=(byte)Integer.parseInt(curDeviceBean.getDeviceSSID().split(",")[1]);
					new sendChargCmd((byte)0x00).cancel(true);
					if(device.isDeviceIconPersert()){
						new sendChargCmd((byte)0x00).execute();
					}else{
						new sendChargCmd((byte)0x01).execute();
					}
				}
				
				
				
			}else{
				if(device.getRCTypeId()==8){//说明是自定义遥控�?
					Intent intent = new Intent(this, CustomRemoteEditActivity.class);
					intent.putExtra("modelName", "null");
					intent.putExtra("device", device);
					intent.putExtra("whichaction", "nowhichaction");
					intent.putExtra("location", "nolocation");
					startActivity(intent);
				}else{//定义的指定模板的遥控�?
					Intent intent = new Intent(this, CsstSHUseDeviceActivity.class);
					intent.putExtra("modelName", "null");
					intent.putExtra("device", device);
					intent.putExtra("whichaction", "nowhichaction");
					intent.putExtra("location", "nolocation");
					startActivity(intent);
				}
			}
		}else{//其他有自己写好界面的 遥控�?
			if(device.getRCTypeId()==8){//说明是自定义遥控�?
				Intent intent = new Intent(this, CustomRemoteEditActivity.class);
				intent.putExtra("modelName", "null");
				intent.putExtra("device", device);
				intent.putExtra("whichaction", "nowhichaction");
				intent.putExtra("location", "nolocation");
				startActivity(intent);
			}else{//定义的指定模板的遥控�?
				Intent intent = new Intent(this, CsstSHUseDeviceActivity.class);
				intent.putExtra("modelName", "null");
				intent.putExtra("device", device);
				intent.putExtra("whichaction", "nowhichaction");
				intent.putExtra("location", "nolocation");
				startActivity(intent);
			}
		
		}
		
	}
	
	}
	
	/**
	 * 修改设备
	 * @param device
	 */
	private final void modifyDevice(CsstSHDeviceBean device){
//		Intent intent = new Intent();
//		intent.putExtra("device", device);
		Intent intent =null;
		getTempflag_Thread = false;
		System.out.println("the getDeviceIconPath is   "+device.getDeviceIconPath());
			if(device.getRCTypeId()==8){//说明是自定义遥控�?
				 intent = new Intent(this, CustomRemoteActivity.class);
				intent.putExtra("modelName", "null");
				intent.putExtra("device", device);
				intent.putExtra("whichaction", "nowhichaction");
				intent.putExtra("location", "nolocation");
				startActivityForResult(intent, MODIFY_DEVICE_REQUEST_CODE);
			}else{
				modifychargeAndSwitch(device);
			}
		
	}
	
	/**
	 * 删除设备
	 * @param device
	 */
	private final void deleteDevice(CsstSHDeviceBean device){
		CsstSHDeviceTable.getInstance().deleteByDevice(mDb, device.getDeviceId());
		//更新设备状�?
		initDeviceState();
		refreshView();
	}
	
	private final void modifyDeviceOption(final CsstSHDeviceBean device){
		AlertDialog.Builder builder = new AlertDialog.Builder(SmartStartActivity.this);
		builder.setTitle(device.getDeviceName());
		builder.setItems(R.array.csst_device_modify, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					// 修改
					case 0:
						modifyDevice(device);
						break;
					// 删除
					case 1:
						deleteDevice(device);
						break;
				}
			}
		});
		builder.setNegativeButton(R.string.csst_cancel, null);
		Dialog d = builder.show();
		d.setCanceledOnTouchOutside(true);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 销毁时关闭数据�?
		getTempflag_Thread_main =false;
		getTempflag_Thread = false;
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
			Intent inent = new Intent(SmartStartActivity.this, CsstSHDeviceManagerActivity.class);
			startActivity(inent);
		}
	}
	
	/**
	 * 按键监听�?
	 * @author liuyang
	 */
	private final class BtnListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_safemanager:
				if(!isOffLine()){
					Intent intent = new Intent(SmartStartActivity.this, CsstSHSaftListView.class);
					startActivity(intent);
				}
				break;
			case R.id.iv_netstat:
				new getStatues((byte)0x01).execute() ;  
				break;
			}
		}

	}
	
	/**
	 * 视频监控
	 * @author liuyang
	 */
	private final class CameraBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if(!isOffLine()){
				Intent intent = new Intent(SmartStartActivity.this, CsstSHCameraActivity.class);
				startActivity(intent);
			}
		}
	}
	
	/**
	 * 设备item修改设备
	 * @author liuyang
	 */
	private final class DeviceModifyListener implements AdapterView.OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			if(!isOffLine()){
			modifyDeviceOption((CsstSHDeviceBean) view.getTag());
			}
			return false;
		}
		
	}
	
	/**
	 * 设备导航条修改设�?
	 * @author liuyang
	 */
	private final class DeviceBuideModifyListener implements View.OnLongClickListener{
		@Override
		public boolean onLongClick(View v) {
			if(!isOffLine()){
			modifyDeviceOption((CsstSHDeviceBean) v.getTag());
			}
			return false;
		}
	}
	
	/**
	 * 设备导航按键监听�?
	 * @author liuyang
	 */
	private final class DeviceBuideListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if(!isOffLine()){
			useDevice((CsstSHDeviceBean) v.getTag());
			}
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
			if(!isOffLine()){
			useDevice((CsstSHDeviceBean) view.getTag());
			}
		}
	}
	
	/**
	 * 添加设备监听�?
	 * @author liuyang
	 */
	private final class AddDeviceListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if(!isOffLine()){
			
			if (mFloorId < 0 || mRoomId < 0){
				//请添加房间，没有依赖的房间！
				Toast.makeText(SmartStartActivity.this, R.string.csst_adddevice_empty_room, Toast.LENGTH_LONG).show();
				return;
			}
			Intent inent = new Intent(SmartStartActivity.this, CsstSHAddDeviceActivityZQL.class);
			inent.putExtra("roomId", configPreference.readInteger("roomId"));
			startActivity(inent);
		}
		}
	}
	
	/**
	 * 添加房间按钮监听�?
	 * @author liuyang
	 */
	private final class AddRoomBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if(!isOffLine()){
			//没有场景返回
			mFloorId = getCurFloorId();
			if (mFloorId < 0){
				Toast.makeText(SmartStartActivity.this, R.string.csst_add_room_empty_floor, Toast.LENGTH_LONG).show();
				return;
			}
			//显示天剑房间对话�?
			final EditText inputServer = new EditText(SmartStartActivity.this);
			AlertDialog.Builder builder = new AlertDialog.Builder(SmartStartActivity.this);
			builder.setTitle(R.string.csst_please_input_roomname);
			builder.setIcon(R.drawable.csst_floor);
			builder.setView(inputServer);
			builder.setNegativeButton(R.string.csst_cancel, null);
			builder.setPositiveButton(R.string.csst_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String roomName = inputServer.getText().toString();
					//房间名为�?
					if (TextUtils.isEmpty(roomName)){
						Toast.makeText(SmartStartActivity.this, R.string.csst_floorname_not_null, Toast.LENGTH_LONG).show();
						return;
					}
					//同一场景下是否存在同一房间名称
					if (CsstSHRoomTable.getInstance().floorExistsSameRoom(mDb, roomName, mFloorId)){
						Toast.makeText(SmartStartActivity.this, R.string.csst_same_room_message, Toast.LENGTH_LONG).show();
						return;
					}
					//插入房间
					CsstSHRoomBean roomBean = new CsstSHRoomBean(roomName, mFloorId);
					int rid = (int) CsstSHRoomTable.getInstance().insert(mDb, roomBean);
					if (rid < 0){
						//数据存储错误，请与厂商联系！
						Toast.makeText(SmartStartActivity.this, R.string.csst_save_error, Toast.LENGTH_SHORT).show();
						return;
					}
					if (mRoomId < 0){
						mRoomId = rid;
						configPreference.setRoomId(mRoomId);
					}
					roomBean.setRoomId(rid);
					RadioButton rb = createRoomBtnView(roomBean);
					rb.setChecked(rid == mRoomId);
					mRoomRadioGroup.addView(rb);
					//没添加一个房间就刷新一下�?
					refreshView();
				}
			});
			builder.show();
			}
		}
	}
	
	/**
	 * 房间切换监听�?
	 * @author liuyang
	 */
	private final class RoomListener implements RadioGroup.OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			
			if(!isOffLine()){
			//点击当前正在使用的房间，直接返回
			if (mRoomId == checkedId){
				return;
			}
			
			System.out.println("RoomListener  11 the mRoomId is "+mRoomId);
			//修改当前选择的房间id
			mRoomId = checkedId;
			//存储当前操作的房�?
			configPreference.setRoomId(mRoomId);
			//更新设备列表
			initDeviceState();
			}
		}
	}
	
	/**
	 * 房间修改事件
	 * @author liuyang
	 */
	private final class RoomItemListener implements View.OnLongClickListener{
		@Override
		public boolean onLongClick(View v) {
			final CsstSHRoomBean roomBean = (CsstSHRoomBean) v.getTag();
			AlertDialog.Builder builder = new AlertDialog.Builder(SmartStartActivity.this);
			builder.setTitle(roomBean.getRoomName());
			builder.setItems(R.array.csst_floor_modify, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						// 重命�?
						case 0:
							modifyRoom(roomBean);
							break;
						// 删除
						case 1:
							deleteRoom(roomBean);
							break;
					}
				}
			});
			builder.setNegativeButton(R.string.csst_cancel, null);
			Dialog d = builder.show();
			d.setCanceledOnTouchOutside(true);
			return false;
		}
	}
	
	public void exitTip()
	 {
			AlertDialog.Builder builder=new Builder(SmartStartActivity.this);
			builder.setTitle(getResources().getString(R.string.dialognotice));
			builder.setMessage(getResources().getString(R.string.exittip));
			builder.setPositiveButton(getResources().getString(R.string.csst_ok), new DialogInterface.OnClickListener()
			{  
				public void onClick(DialogInterface dialog, int which)
				{ 
//					MyApplication.getInstance().exit();
					SmartStartActivity.this.finish();
					System.exit(0);
				}
		 });  
				 builder.setNegativeButton(getResources().getString(R.string.csst_cancel), new DialogInterface.OnClickListener()
				 {  
					 public void onClick(DialogInterface dialog, int which) 
					 {   }	});  
			builder.create().show();
		}	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	if(keyCode==KeyEvent.KEYCODE_BACK){
		getTempflag_Thread = false;
		//需要处�?
		exitTip();
		if(CsstSHRightFragment.pop!=null&&getTempflag_Thread){//同时满足才消除情景模式下的pop 上拉，这样就是避免在执行情景模式执行的过程中按下消除
			CsstSHRightFragment.pop.dismiss();
		}
		
	}
		 return true;
	}
	
    
    //seek 定时发�?
    private void startTimer(){  
        if (timerconfigwifi == null) {  
            timerconfigwifi = new Timer();  
        }  
  
        if (tastconfigwifi == null) {  
            tastconfigwifi = new TimerTask() {  
                @Override  
                public void run() {  
                    Message message=new Message();
			         message.what=handlerconfigwifi.hashCode();
			         handlerconfigwifi.sendMessage(message);
                      
                     
                }  
            };  
        }  
  
        if(timerconfigwifi != null && tastconfigwifi != null )  
            timerconfigwifi.schedule(tastconfigwifi,  300);  
  
    }  
  
    private void stopTimer(){  
          
        if (timerconfigwifi != null) {  
            timerconfigwifi.cancel();  
            timerconfigwifi = null;  
        }  
  
        if (tastconfigwifi != null) {  
            tastconfigwifi.cancel();  
            tastconfigwifi = null;  
        }     
  
    }  

    public void configwifitimer(){
		handlerconfigwifi=new Handler(){
			  @Override
				  public void handleMessage(Message msg) {
				  System.out.println(TAG+" configwifitimer Message is here   ");
				  if(CsstSHRightFragment.pop!=null&&getTempflag_Thread){//同时满足才消除情景模式下的pop 上拉，这样就是避免在执行情景模式执行的过程中按下消除
						CsstSHRightFragment.pop.dismiss();
					}
				   super.handleMessage(msg);
				   //主要处理seekBar 参数
				   switch(msg.what){
				   case QUERYTEMP:
					   switch(queryWhich(queryTimes)){
					   case ISTEMP:
						   if(debug){
								System.out.println(TAG+" ISTEMP receive the contentBuffer[] lenght  "+contentBuffer.length);
							}
							mTVtempValue.setText(contentBuffer[2]+getResources().getString(R.string.csst_wendu_param));
							if(debug){
								System.out.println(TAG+" ISTEMP receive the tempeture  here 1111 ");
							}
							
							mTVshiduValue.setText(contentBuffer[3]+getResources().getString(R.string.csst_shidu_rh)+getResources().getString(R.string.csst_shidu_param));
							if(debug){
								System.out.println(TAG+" ISTEMP  receive the tempeture  here 22222");
							}
							if(contentBuffer[5]<10){
								mTVjiaquanValue.setText(contentBuffer[4]+"."+"0"+contentBuffer[5]+getResources().getString(R.string.csst_jiaquan_param));
							}else{
								mTVjiaquanValue.setText(contentBuffer[4]+"."+contentBuffer[5]+getResources().getString(R.string.csst_jiaquan_param));
							}
							switch((byte)contentBuffer[6]){
								case AIR_GOOD: 
									mTVjiaquanIMG.setBackgroundDrawable(getResources().getDrawable(R.drawable.air_good));
									mTVjiaquanTEXT.setText(getResources().getString(R.string.csst_air_good));
									break;
								case AIR_MID: 
									mTVjiaquanIMG.setBackgroundDrawable(getResources().getDrawable(R.drawable.air_mid));
									mTVjiaquanTEXT.setText(getResources().getString(R.string.csst_air_mid));
									break;
								case AIR_BAD: 
									mTVjiaquanIMG.setBackgroundDrawable(getResources().getDrawable(R.drawable.air_bad));
									mTVjiaquanTEXT.setText(getResources().getString(R.string.csst_air_bad));
									break;
								case AIR_MIDBAD: 
									mTVjiaquanIMG.setBackgroundDrawable(getResources().getDrawable(R.drawable.air_midbad));
									mTVjiaquanTEXT.setText(getResources().getString(R.string.csst_air_midbad));
									break;
								case AIR_VERYBAD: 
									mTVjiaquanIMG.setBackgroundDrawable(getResources().getDrawable(R.drawable.air_verybad));
									mTVjiaquanTEXT.setText(getResources().getString(R.string.csst_air_verybad));
									break;
							}
							break;
					   case ISSOCKET:
						   //一次发送数
//						   for(int n=3;n<curListSocketBean.size();n=n+4){
//							   curDeviceBean= curListSocketBean.get(n/3);
//							   String[] msgBuffer = curDeviceBean.getDeviceSSID().split(",");
//							   ssuidLow = (byte)Integer.parseInt(msgBuffer[0]);
//							   ssuidHight = (byte)Integer.parseInt(msgBuffer[1]);
//							   ssstatus = cmdcontentbuffer[n];
//							   curDeviceBean.setDeviceIconPath(ssuidLow+","+ssuidHight+","+ssstatus);
//							   if(debug){
//								   System.out.println("the n is "+n +" the n/3 is "+n/3+"the device name is "+curDeviceBean.getDeviceName());
//							   }
//							   if((cmdcontentbuffer[n]&0x01)==(byte)0x01){
//								   curDeviceBean.setDeviceIconPersert(true);
//								   CsstSHDeviceTable.getInstance().update(mDb, curDeviceBean);
//							   }else{
//								   curDeviceBean.setDeviceIconPersert(false);
//								   CsstSHDeviceTable.getInstance().update(mDb, curDeviceBean);
//							   }
//						   }
//						   
//						   //单个查询
						   
						   String[] msgBuffer = curDeviceBean.getDeviceSSID().split(",");
						   ssuidLow = (byte)Integer.parseInt(msgBuffer[0]);
						   ssuidHight = (byte)Integer.parseInt(msgBuffer[1]);
						   ssstatus = cmdcontentbuffer[MSG_STATUE_RECIVE];
						   curDeviceBean.setDeviceSSID(ssuidLow+","+ssuidHight+","+ssstatus);
						   if(cmdcontentbuffer[MSG_STATUE_RECIVE]==(byte)0x01){
								curDeviceBean.setDeviceIconPersert(true);
								CsstSHDeviceTable.getInstance().update(mDb, curDeviceBean);
							   
						   }else{
							   curDeviceBean.setDeviceIconPersert(false);
							   CsstSHDeviceTable.getInstance().update(mDb, curDeviceBean);
							     
						   }
							//初始化房间列
							initRoomState();
							//初始化房间对应的设备
							//查询房间对应的设备列�?
							deviceBeans = CsstSHRoomTable.getInstance().getRoomDevices(mDb, mRoomId);
							//初始化适配�?
							deviceAdapter=new CsstSHDeviceAdapter(SmartStartActivity.this, deviceBeans);
							//设置适配�?
							myDeviceListView.setAdapter(deviceAdapter);
							mHandler.sendEmptyMessageDelayed(REFRESH_CONTENT_CMD, 50);
						   break;
					   case ISSWITCH:
						   msgBuffer = curDeviceBean.getDeviceSSID().split(",");
						   ssuidLow = (byte)Integer.parseInt(msgBuffer[0]);
						   ssuidHight = (byte)Integer.parseInt(msgBuffer[1]);
						   ssstatus = cmdcontentbuffer[MSG_STATUE_RECIVE];
						   curDeviceBean.setDeviceSSID(ssuidLow+","+ssuidHight+","+ssstatus);
//						   //使用RCTypeId来保存状�?
//						   curDeviceBean.setRCTypeId(cmdcontentbuffer[MSG_STATUE_RECIVE]);
						   //更新相应灯的数据
						   curswitchbean = CsstSHSwitchTable.getInstance().queryByDeviceId(mDb, curDeviceBean.getDeviceId());
						   if(curswitchbean!=null){
							   curswitchbean.setSwitchonoff(ssstatus);  
							   CsstSHSwitchTable.getInstance().update(mDb,curswitchbean);
						   }
						   
						   
						   if(debug){
							   System.out.println("the on or off is "+cmdcontentbuffer[MSG_STATUE_RECIVE]);  
						   }
						   if((cmdcontentbuffer[MSG_STATUE_RECIVE]&0x0F)!=0){
								curDeviceBean.setDeviceIconPersert(true);
								CsstSHDeviceTable.getInstance().update(mDb, curDeviceBean);
							   
						   }else{
							   curDeviceBean.setDeviceIconPersert(false);
							   CsstSHDeviceTable.getInstance().update(mDb, curDeviceBean);
							     
						   }
							//初始化房间列
							initRoomState();
							//初始化房间对应的设备
							//查询房间对应的设备列�?
							deviceBeans = CsstSHRoomTable.getInstance().getRoomDevices(mDb, mRoomId);
							//初始化适配�?
							deviceAdapter=new CsstSHDeviceAdapter(SmartStartActivity.this, deviceBeans);
							//设置适配�?
							myDeviceListView.setAdapter(deviceAdapter);
							mHandler.sendEmptyMessageDelayed(REFRESH_CONTENT_CMD, 50);
						   break;
					   }
					   
					   System.out.println(TAG+" configwifitimer Message is here  queryTimes is " +queryTimes);
					   queryTimes++;
						//先自增这样为了保持一致需要加1总数
						if(queryTimes==(queryTotalTimes+1)){
							System.out.println(TAG+" configwifitimer Message is here  queryTimes==queryTotalTimes+1 ");
							if(myprogress!=null){
								myprogress.cancel();
							}
							queryTimes=0;
						}else{
							new getStatues((byte)0x00).execute() ;  
						}
						System.out.println(TAG+" configwifitimer Message is here  queryTimes 22 is " +queryTimes);
				   	break;
				   case SENDCMD:
					   if(curDeviceBean.getDeviceIconPath().equals(getResources().getString(R.string.csst_adddevice_charge))){
						   if(cmdcontentbuffer[MSG_STATUE_RECIVE]==(byte)0x01){
								curDeviceBean.setDeviceIconPersert(true);
								CsstSHDeviceTable.getInstance().update(mDb, curDeviceBean);
							   
						   }else{
							   curDeviceBean.setDeviceIconPersert(false);
							   CsstSHDeviceTable.getInstance().update(mDb, curDeviceBean);
							     
						   }   
					   }else{//只要是面板开关只要低位为1就点�?
						   if((cmdcontentbuffer[MSG_STATUE_RECIVE]&(byte)0x01)==1){
								curDeviceBean.setDeviceIconPersert(true);
								CsstSHDeviceTable.getInstance().update(mDb, curDeviceBean);
							   
						   }else{
							   curDeviceBean.setDeviceIconPersert(false);
							   CsstSHDeviceTable.getInstance().update(mDb, curDeviceBean);
							     
						   }  
					   }
					  
					   refreshView();
					   break;
				   case SENDCMD_AIR:
					   
					   break;
				   case 4:
					   break;
				   default:
					   break;
				   }
				  
//					stopTimer();
				  }
			    };
	}
	
    public final class getStatues extends AsyncTask<Void, Void, Boolean>{
    	byte cmd ;
		public getStatues(byte cmd) {
			this.cmd = cmd;
		}
		@Override
  		protected void onPreExecute() {
  			super.onPreExecute();
  			if(cmd==0x01){
  				LoadProcess_getstatus();
  			}
  			
  		}
		@Override
		protected Boolean doInBackground(Void... params) {
    	//设备的MAC地址
		long toid = 109860815673L;
		ServerItemModel sim = new ServerItemModel();
		String msgBuffer ="1111";
		if(CsstSHUseDeviceActivity.msgBuffer!=null){
			msgBuffer=  CsstSHUseDeviceActivity.msgBuffer[0];
		}
		msgBuffer = mMacAdress;
		sim.setIpaddress("218.244.129.177");
		sim.setPort(GobalDef.SERVER_PORT);
		
		
		BaseCodeMessage bcm = new BaseCodeMessage();
		bcm.Direct = MessageDef.BASE_MSG_FT_REQ;
		bcm.setFromId(GobalDef.MOBILEID);
		bcm.Seq = MessageSeqFactory.GetNextSeq();
		bcm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
		bcm.ToType = MessageDef.BASE_MSG_FT_HUB;
		bcm.setToId(toid);
		bcm.setToId(Long.valueOf(msgBuffer));
		bcm.setMCMD((byte) 4);
		bcm.setSCMD((byte) 1);
		Message msg; 

				if(debug){
					System.out.println(TAG+"zengqinglin  getUpdatate + querytime is "+queryTimes+"  the querytotal is "+queryTotalTimes);
				}
			
			//判断当前是查询谁 就发指定的命令
			switch(queryWhich(queryTimes)){
			case ISTEMP:
				byte[] contentBuf = {(byte) 4,(byte)1};
				bcm.setContentBuf(contentBuf);
				BaseCodeMessage bcmrcv = UdpJavaEncodingProcess.GetMsgReturn(bcm, sim);
				if(debug){
					System.out.println(TAG+" receive ISTEMP the star 1  here ");
				}
				
				for(int n=0;n<contentBuf.length;n++){
					System.out.println(TAG+ "ISTEMP send " + n + " value: " + contentBuf[n]);
				}
				
				for(int m=0;m<contentBuffer.length;m++){
					contentBuffer[m]=0;
				}
				if(bcmrcv != null){
					//return true;
					if(bcmrcv.getContentBuf() != null){
						for(int i = 0; i<bcmrcv.getContentBuf().length; i++){
							contentBuffer[i]=bcmrcv.getContentBuf()[i];
							System.out.println(" ISTEMP revice content " + i + " value: " + contentBuffer[i]);
						}
					}
					
					if(debug){
						System.out.println(TAG+" receive the ISTEMP  finish");
					}
					//清除离线标志
					moffLineflag =0;
				}
				else{
					if(debug){
						System.out.println(TAG+" receive the gettemp offline "+moffLineflag);
					}
					//三次拿到的数据为空就设定设备为离线状�?
					moffLineflag++;
				}
				
				msg = handlerconfigwifi.obtainMessage(QUERYTEMP);
	  			handlerconfigwifi.sendMessage(msg);
				
//				try {
////						Thread.sleep(30000);
//					Thread.sleep(20000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				break;
			case ISSOCKET:
				
//					int curquerysize = curListSocketBean.size();
//					byte[] contentBufcmd = new byte[(curquerysize*2+2)];
//					if(debug){
//						System.out.println("the contentBufcmd is length "+contentBufcmd.length+ " the curListSocketBean.size() is "+ curquerysize);
//					}
//					System.out.println("the o is 1");
//					contentBufcmd[0]=(byte)0x04;//24
//					System.out.println("the o is 2");
//					contentBufcmd[1]=(byte)0x05;//25
//					System.out.println("the o is 3");
//					for(int o=0;o<(curquerysize*2);o++){
//						System.out.println("the o is 1 "+o);
//						contentBufcmd[o+2] = (byte)Integer.parseInt(curListSocketBean.get(o/2).getDeviceSSID().substring(0,curListSocketBean.get(o/2).getDeviceSSID().indexOf(",")));
//						o++;
//						System.out.println("the o is 2 "+o);
//						contentBufcmd[o+2] =(byte)Integer.parseInt(curListSocketBean.get(o/2).getDeviceSSID().substring(curListSocketBean.get(o/2).getDeviceSSID().indexOf(",")+1,curListSocketBean.get(o/2).getDeviceSSID().length()));
//						System.out.println("the o is 3 "+o);
//					}
//					
//					if(debug){
//						System.out.println("the contentBufcmd is length 22 "+contentBufcmd.length+ " the curListSocketBean.size() is "+ curquerysize);
//					}
				
				
				
				byte[] contentBufcmd = new byte[5];
				contentBufcmd[0]=(byte)0x04;//24
				contentBufcmd[1]=(byte)0x05;//25
				//根据查询的次数来拿去相应的DeviceBean;
				curDeviceBean =curListSocketBean.get(queryTimes-1);
				contentBufcmd[2]=(byte)Integer.parseInt(curDeviceBean.getDeviceSSID().split(",")[0]);
				contentBufcmd[3]=(byte)Integer.parseInt(curDeviceBean.getDeviceSSID().split(",")[1]);
				contentBufcmd[4]=(byte)0x03;//28表示类型是插座
				for(int n=0;n<contentBufcmd.length;n++){
					System.out.println(TAG+ "ISSOCKET send " + n + " value: " + contentBufcmd[n]);
				}
				
				bcm.setContentBuf(contentBufcmd);
				//在接收之前先清除，以免没接收到还用原来的数据
				for(int m=0;m<cmdcontentbuffer.length;m++){
					cmdcontentbuffer[m]=0;
				}
				bcmrcv = UdpJavaEncodingProcess.GetMsgReturn(bcm, sim);
				if(debug){
					System.out.println(TAG+" receive ISSOCKET the star 1  here ");
				}
				if(bcmrcv != null){
					//return true;
					if(bcmrcv.getContentBuf() != null){
						for(int i = 0; i<bcmrcv.getContentBuf().length; i++){
							cmdcontentbuffer[i]=bcmrcv.getContentBuf()[i];
							System.out.println(" ISSOCKET revice content " + i + " value: " + cmdcontentbuffer[i]);
						}
					}
					
					if(debug){
						System.out.println(TAG+" receive the ISSOCKET  finish");
					}
					//清除离线标志
					moffLineflag =0;
				}
				else{
					//没查到数据就说明该device is offline 
					if(debug){
						System.out.println(TAG+" receive the ISSOCKET offline ");
					}
					//三次拿到的数据为空就设定设备为离线状�?
				}
				msg = handlerconfigwifi.obtainMessage(QUERYTEMP);
	  			handlerconfigwifi.sendMessage(msg);
				try {
//						Thread.sleep(30000);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case ISSWITCH:
				byte[] contentBufcmdswitch = new byte[5];
				contentBufcmdswitch[0]=(byte)0x04;//24
				contentBufcmdswitch[1]=(byte)0x05;//25
				//根据查询的次数来拿去相应的DeviceBean;
				curDeviceBean =curListSwitchBean.get(queryTimes-curListSocketBean.size()-1);
				contentBufcmdswitch[2]=(byte)Integer.parseInt(curDeviceBean.getDeviceSSID().split(",")[0]);
				contentBufcmdswitch[3]=(byte)Integer.parseInt(curDeviceBean.getDeviceSSID().split(",")[1]);
				contentBufcmdswitch[4]=(byte)0x02;//28表示类型是插座
				
				for(int n=0;n<contentBufcmdswitch.length;n++){
					System.out.println(TAG+ "ISSWITCH send " + n + " value: " + contentBufcmdswitch[n]);
				}
				
				bcm.setContentBuf(contentBufcmdswitch);
				bcmrcv = UdpJavaEncodingProcess.GetMsgReturn(bcm, sim);
				if(debug){
					System.out.println(TAG+" receive ISSWITCH the star 1  here ");
				}
				//在接收之前先清除，以免没接收到还用原来的数据
				
				for(int m=0;m<cmdcontentbuffer.length;m++){
					cmdcontentbuffer[m]=0;
				}
					
				if(bcmrcv != null){
					//return true;
					if(bcmrcv.getContentBuf() != null){
						for(int i = 0; i<bcmrcv.getContentBuf().length; i++){
							cmdcontentbuffer[i]=bcmrcv.getContentBuf()[i];
							System.out.println(" ISSWITCH revice content " + i + " value: " + cmdcontentbuffer[i]);
						}
					}
					if(debug){
						System.out.println(TAG+" receive the ISSWITCH  finish");
					}
					//清除离线标志
					moffLineflag =0;
				}
				else{
					//没查到数据就说明该device is offline 
					if(debug){
						System.out.println(TAG+" receive the ISSOCKET offline ");
					}
					//三次拿到的数据为空就设定设备为离线状�?
				}
				
				
				msg = handlerconfigwifi.obtainMessage(QUERYTEMP);
	  			handlerconfigwifi.sendMessage(msg);
				try {
//						Thread.sleep(30000);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				break;
			default:
				break;
				
			
			}
			return true;
		}
    }

	public int queryWhich(int mquerytime){
		int who =1;
		if(mquerytime<1){
			if(debug){
				System.out.println("the queryWhich is ISTEMP");
			}
			who = ISTEMP;
		}else if(mquerytime<1+curListSocketBean.size()&&0<mquerytime){
			if(debug){
				System.out.println("the queryWhich is ISSOCKET");
			}
			who = ISSOCKET;
		}else if(mquerytime<1+curListSocketBean.size()+curListSwitchBean.size()&&curListSocketBean.size()<mquerytime){
			if(debug){
				System.out.println("the queryWhich is ISSWITCH");
			}
			who = ISSWITCH;
		}
		return who;
		
	}
	
	public void  LoadProcess_fisrt() {
		if(myprogress!=null){
			myprogress.cancel();
		}
		myprogress = new ProgressDialog(this);
		myprogress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		myprogress.setMessage(getResources().getString(R.string.csst_send_cmd));
		myprogress.setIndeterminate(false);
		myprogress.setCancelable(true);
		myprogress.show();
	}
	

	public void  LoadProcess_getstatus() {
		if(myprogress!=null){
			myprogress.cancel();
		}
		myprogress = new ProgressDialog(this);
		myprogress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		myprogress.setMessage(getResources().getString(R.string.csst_getstatus));
		myprogress.setIndeterminate(false);
		myprogress.setCancelable(true);
		myprogress.show();
	}
	
  	private final class sendChargCmd extends AsyncTask<Void, Void, Boolean>{
  		byte cmd = 0x00;
  		public sendChargCmd(byte cmd){
  			
  			this.cmd = cmd;
  		}
  		@Override
  		protected void onPreExecute() {
  			super.onPreExecute();
  			LoadProcess_fisrt();
  		}
  		@Override
  		protected Boolean doInBackground(Void... params) {
  			int flag = 0;
  			while(sendflag){
  				System.out.println("zengqinglin  UpdateTempe Updatesenddebugcmdmianban \n");
  				//设备的MAC地址
  			long toid = 109860815673L;
  			ServerItemModel sim = new ServerItemModel();
			//sim.setIpaddress("192.168.2.105");
			//局域网控制
//			sim.setIpaddress("255.255.255.255");
//			sim.setPort(80);
			
			
			
//			sim.setIpaddress("192.168.1.105");
//			sim.setPort(80);
			
			
			//测试用的MAC地址
//			String msgBuffer = "109861926250";
			String msgBuffer ="1111";
			if(CsstSHUseDeviceActivity.msgBuffer!=null){
				msgBuffer=  CsstSHUseDeviceActivity.msgBuffer[0];
			}
			msgBuffer = mMacAdress;
			sim.setIpaddress("218.244.129.177");
			sim.setPort(GobalDef.SERVER_PORT);
			
			
			BaseCodeMessage bcm = new BaseCodeMessage();
			bcm.Direct = MessageDef.BASE_MSG_FT_REQ;
			bcm.setFromId(GobalDef.MOBILEID);
			bcm.Seq = MessageSeqFactory.GetNextSeq();
			bcm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
			bcm.ToType = MessageDef.BASE_MSG_FT_HUB;
			bcm.setToId(toid);
			
			
			bcm.setToId(Long.valueOf(msgBuffer));
			
  			bcm.setMCMD((byte) 4);
  			bcm.setSCMD((byte) 1);
  			System.out.println(" the cmd  is "+cmd);
  			//
  			byte[] contentBuf = new byte[6];
  			contentBuf[0]=(byte)0x07;
  			contentBuf[1]=(byte)0x01;
  			contentBuf[2]=(byte)ssuidLow;
  			contentBuf[3]=(byte)ssuidHight;
  			//如果是一位面板开�?
  			if(curDeviceBean.getDeviceIconPath().equals(getResources().getString(R.string.csst_adddevice_switch))){
  				contentBuf[4]=(byte)0x02;
  			}else{//插座
  				contentBuf[4]=(byte)0x03;
  			}
  			
  			contentBuf[5]=(byte)cmd;
  			for(int n=0;n<6;n++){
  				System.out.println(TAG+ "contentbuf setting send" + n + " value: " + contentBuf[n]);
  			}
  			
  			bcm.setContentBuf(contentBuf);
  			System.out.println("sendChargCmd start to send MsgReturn ");
  			BaseCodeMessage bcmrcv = UdpJavaEncodingProcess.GetMsgReturn(bcm, sim);
  			System.out.println("sendChargCmd ready send get MsgReturn ");
  			if(bcmrcv != null){
  				//return true;
  				if(bcmrcv.getContentBuf() != null){
  					for(int i = 0; i<bcmrcv.getContentBuf().length; i++){
  						cmdcontentbuffer[i]=bcmrcv.getContentBuf()[i];
  						System.out.println(TAG+ "content " + i + " value: " + bcmrcv.getContentBuf()[i]);
  					}
  				}
  				myprogress.dismiss();
  				System.out.println("sendChargCmd get messega finish ");
  				//先停止原来来定时�?因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
  				Message msg = handlerconfigwifi.obtainMessage(SENDCMD);
  	  			handlerconfigwifi.sendMessage(msg);
  				return true;
  			}
  			else{
  				//return false;
  				
  			}
  			
  			System.out.println("Updatesenddebugcmdmianban start to sleep for wait");
  			try {
  				Thread.sleep(500);
  			} catch (InterruptedException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			flag++;
  			if(flag >=1){
  				break;
  			}
  			}
  			return false;
  		}

  		@Override
  		protected void onPostExecute(Boolean result) {
  			super.onPostExecute(result);
  			int response = result ? R.string.csst_send_cmd_success : R.string.csst_send_cmd_fail;
  			Toast.makeText(SmartStartActivity.this, response, Toast.LENGTH_SHORT).show();
  			//debug 不消�?
//  			mDialog.dismiss();
  			//重新开启查询数据线�?
  			getTempflag_Thread = true;
  			myprogress.dismiss();
  			
  			Message msg = handlerconfigwifi.obtainMessage(SENDCMD);
  			handlerconfigwifi.sendMessage(msg);
  			//先停止原来来定时�?因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
  		}
  		
  	}
  	
  	
  	

  	private final class sendAirBeen extends AsyncTask<Void, Void, Boolean>{
  		byte cmd = 0x00;
  		public sendAirBeen(byte cmd){
  			
  			this.cmd = cmd;
  		}
  		@Override
  		protected void onPreExecute() {
  			super.onPreExecute();
  			LoadProcess_fisrt();
  		}
  		@Override
  		protected Boolean doInBackground(Void... params) {
  			int flag = 0;
  			while(sendflag){
  				System.out.println("zengqinglin  UpdateTempe Updatesenddebugcmdmianban \n");
  				//设备的MAC地址
  			long toid = 109860815673L;
  			ServerItemModel sim = new ServerItemModel();
			String msgBuffer ="1111";
			if(CsstSHUseDeviceActivity.msgBuffer!=null){
				msgBuffer=  CsstSHUseDeviceActivity.msgBuffer[0];
			}
			msgBuffer = mMacAdress;
			sim.setIpaddress("218.244.129.177");
			sim.setPort(GobalDef.SERVER_PORT);
			
			
			BaseCodeMessage bcm = new BaseCodeMessage();
			bcm.Direct = MessageDef.BASE_MSG_FT_REQ;
			bcm.setFromId(GobalDef.MOBILEID);
			bcm.Seq = MessageSeqFactory.GetNextSeq();
			bcm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
			bcm.ToType = MessageDef.BASE_MSG_FT_HUB;
			bcm.setToId(toid);
			
			
			bcm.setToId(Long.valueOf(msgBuffer));
			
  			bcm.setMCMD((byte) 4);
  			bcm.setSCMD((byte) 1);
  			System.out.println(" the cmd  is "+cmd);
  			//
  			byte[] contentBuf = new byte[3];
  			contentBuf[0]=(byte)0x07;
  			contentBuf[1]=(byte)0x02;
  			contentBuf[2]=(byte)cmd;
  			
  			for(int n=0;n<3;n++){
  				System.out.println(TAG+ "contentbuf setting send" + n + " value: " + contentBuf[n]);
  			}
  			
  			bcm.setContentBuf(contentBuf);
  			System.out.println("sendAirBeen start to send MsgReturn ");
  			BaseCodeMessage bcmrcv = UdpJavaEncodingProcess.GetMsgReturn(bcm, sim);
  			System.out.println("sendAirBeen ready send get MsgReturn ");
  			if(bcmrcv != null){
  				//return true;
  				if(bcmrcv.getContentBuf() != null){
  					for(int i = 0; i<bcmrcv.getContentBuf().length; i++){
  						cmdcontentbuffer[i]=bcmrcv.getContentBuf()[i];
  						System.out.println(TAG+ "content " + i + " value: " + bcmrcv.getContentBuf()[i]);
  					}
  				}
  				myprogress.dismiss();
  				System.out.println("sendAirBeen get messega finish ");
  				//先停止原来来定时�?因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
//  				Message msg = handlerconfigwifi.obtainMessage(SENDCMD);
//  	  			handlerconfigwifi.sendMessage(msg);
  				return true;
  			}
  			else{
  				//return false;
  				
  			}
  			
  			System.out.println("sendAirBeen start to sleep for wait");
  			try {
  				Thread.sleep(500);
  			} catch (InterruptedException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			flag++;
  			if(flag >=1){
  				break;
  			}
  			}
  			return false;
  		}

  		@Override
  		protected void onPostExecute(Boolean result) {
  			super.onPostExecute(result);
  			int response = result ? R.string.csst_send_cmd_success : R.string.csst_send_cmd_fail;
  			Toast.makeText(SmartStartActivity.this, response, Toast.LENGTH_SHORT).show();
  			//debug 不消�?
//  			mDialog.dismiss();
  			//重新开启查询数据线�?
  			getTempflag_Thread = true;
  			myprogress.dismiss();
  			
  			Message msg = handlerconfigwifi.obtainMessage(SENDCMD_AIR);
  			handlerconfigwifi.sendMessage(msg);
  			//先停止原来来定时�?因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
  		}
  		
  	}
    
  	
  	 public void LoadLayout()
	 {
		 DisplayMetrics displayMetrics = new DisplayMetrics();       
		 getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);         
		 int displayWidth = displayMetrics.widthPixels;          
		 int displayHeight = displayMetrics.heightPixels; 
		 
		 
		 LinearLayout.LayoutParams paramsdone = new LinearLayout.LayoutParams(         
				 (int) (displayWidth * 0.405f + 0.5f), 
				 (int) (displayHeight * 0.0755f + 0.5f)); 
		 paramsdone.leftMargin=15;
		 paramsdone.rightMargin=15;
//		 paramsdone.bottomMargin=15;
		mBtnCamera.setLayoutParams(paramsdone);
		mBtnShiPin.setLayoutParams(paramsdone);
		mBtnSafeManager.setLayoutParams(paramsdone);
		mBtnDeviceManage.setLayoutParams(paramsdone);
		 
		 
	 } 
	
	public boolean isOffLine(){
		boolean isoffline = true;
//			if(mMacAdress.equals(ICsstSHConstant.CONTROL_MAC_ADDR_DEFAUL)){
//				Toast.makeText(SmartStartActivity.this, R.string.csst_configdevice_tip, Toast.LENGTH_LONG).show();	
//				isoffline = true;
//			}else if(moffLineflag>4){
//				Toast.makeText(SmartStartActivity.this, R.string.csst_offline_tip, Toast.LENGTH_LONG).show();	
//				isoffline = true;
//			}else{
//				isoffline = false;
//			}
//			if(debug){
//				System.out.println("the moffLineflag is "+moffLineflag);
//			}
			isoffline = false;
			return isoffline ;
		}
	
	
	
	
}
