package com.csst.smarthome.activity.fragment;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.device.CsstSHUseDeviceActivity;
import com.csst.smarthome.bean.CsstSHDRCBean;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.bean.CsstSHSwitchBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHSwitchTable;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.csst.smarthome.util.TitleLayoutUtils;

public class Socket_Action extends Activity implements ICsstSHInitialize, ICsstSHConstant {
	private String TAG = "TriplePanelSwitchActivity";
	private boolean debug = true;
	
	
	
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	/** 当前设备 */
	private CsstSHDeviceBean mDevice = null;
	byte ssuidLow = 0x00;
	
	byte ssuidHight = 0x00;
	byte sendcmd = 0x00;
	byte ssstatus = 0x00;
	
	/**
	 * 是否是动作调用是从whichaction 跳到本界面，就将在点遥控器的按键就调回 addaction界面
	 */
	String whichAction =null;
	/**
	 *出来的位置 从whichaction 界面传过来的关于位置的信息
	 */
	String location =null;

	/**
	 * 上层传过来的modelname
	 */
	private String modelName = null;
	
	
	/** 设备按键列表 */
	private List<CsstSHDRCBean> mDeviceKeys = null;
	
	//用来存储当前面板想对应的开关
	private CsstSHSwitchBean curswitchbean =null;
	String[] msgBuffer = null;
	String strTemp = null;
	
	
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	//从上个层面发送过来的器件名称
	
	private Button btnAllOn,btnAllOff;
	private LinearLayout btnback;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socket_action);
		initDataSource();
		TitleLayoutUtils.setupTitleLayout(this, getResources().getString(R.string.csst_socket_title));
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
		
		
    }

	public void addWidgetListener() {
		// TODO Auto-generated method stub
		
	}
	


	public void initDataSource() {
		// TODO Auto-generated method stub
		// 初始数据源
		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对�?
		mDb = csstSHDataBase.getWritDatabase();
		Intent intent = getIntent();
		if (null != intent){
			modelName =(String) intent.getSerializableExtra("modelName");
			mDevice = (CsstSHDeviceBean) intent.getSerializableExtra("device");
			strTemp =mDevice.getDeviceSSID();
			curswitchbean = CsstSHSwitchTable.getInstance().queryByDeviceId(mDb, mDevice.getDeviceId());
			msgBuffer = strTemp.split(",");
		    ssuidLow = (byte)Integer.parseInt(msgBuffer[0]);
		    ssuidHight = (byte)Integer.parseInt(msgBuffer[1]);
			ssstatus = (byte)Integer.parseInt(msgBuffer[2]);
			whichAction =(String) intent.getSerializableExtra("whichaction");
			location =(String) intent.getSerializableExtra("location");
			System.out.println("the value whichaction is "+whichAction+" the locatin is "+location+"the get ssid is "+strTemp);
		}
		
		
	}


	public void initWidget() {
		// TODO Auto-generated method stub
		btnAllOff = (Button)findViewById(R.id.allOffBtn);
		btnAllOn = (Button) findViewById(R.id.allOnBtn);
		btnback =(LinearLayout) findViewById(R.id.back_btn);
		

	}
	

	public void initWidgetState() {
		// TODO Auto-generated method stub
	
		
	}
  
			
		
	public void initWidgetListener() {
		// TODO Auto-generated method stub
		btnback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {				
			Intent intent = new Intent();
			intent.setClass(Socket_Action.this, CsstSHAddModelWhichAction.class);
			intent.putExtra("modelName", modelName);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			Socket_Action.this.finish();
			if (mDb != null)
			mDb.close();
			}
		});
		
		
		
		btnAllOn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				System.out.println("go back add whichaction  ");
				Intent intent = new Intent(Socket_Action.this, CsstSHAddModelAddAction.class);
				intent.putExtra("location",location+mDevice.getDeviceName().toString());
				byte cmdtype = (byte)0x03;
				if(mDevice.getDeviceIconPath().equals(getResources().getString(R.string.csst_adddevice_switch))){
					byte cmd = (byte)0xf1;
					cmdtype = (byte)0x02;
					intent.putExtra("keyCode",getResources().getString(R.string.csst_adddevice_switch)+","+cmd+","+ssuidLow+","+ssuidHight+cmdtype);
				}else{
					cmdtype = (byte)0x03;
					intent.putExtra("keyCode",getResources().getString(R.string.csst_adddevice_charge)+","+"1"+","+ssuidLow+","+ssuidHight+cmdtype);
				}
				
				intent.putExtra("modelName", modelName);
				startActivity(intent);
				Socket_Action.this.finish();
				if (mDb != null)
				mDb.close();
			}
		});
		
		
		btnAllOff.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				System.out.println("go back add whichaction  ");
				Intent intent = new Intent(Socket_Action.this, CsstSHAddModelAddAction.class);
				intent.putExtra("location",location+mDevice.getDeviceName().toString());
				if(mDevice.getDeviceIconPath().equals(getResources().getString(R.string.csst_adddevice_switch))){
					byte cmd = (byte)0xf0;
					intent.putExtra("keyCode",getResources().getString(R.string.csst_adddevice_switch)+","+cmd+","+ssuidLow+","+ssuidHight);
				}else{
					intent.putExtra("keyCode",getResources().getString(R.string.csst_adddevice_charge)+","+"0"+","+ssuidLow+","+ssuidHight);
				}
				
				intent.putExtra("modelName", modelName);
				startActivity(intent);
				Socket_Action.this.finish();
				if (mDb != null)
				mDb.close();
			}
		});
	}
		
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	if(keyCode==KeyEvent.KEYCODE_BACK){
		System.out.println(TAG+"backEvent() is here 12");
		backEvent();
	}
		 return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 销毁时关闭数据
		if (mDb != null)
			mDb.close();
	}
	/**
	 * 返回事件
	 */
	private final void backEvent(){
		System.out.println("go back add whichaction  ");
		Intent intent = new Intent(Socket_Action.this, CsstSHAddModelWhichAction.class);
		intent.putExtra("location",location+mDevice.getDeviceName().toString());
		intent.putExtra("keyCode",getResources().getString(R.string.csst_adddevice_charge)+","+"0"+","+ssuidLow+","+ssuidHight);
		intent.putExtra("modelName", modelName);
		startActivity(intent);
		Socket_Action.this.finish();
		if (mDb != null)
		mDb.close();
		
		
		
	}
	
	
    
    
    
}
