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
import android.widget.CheckBox;
import android.widget.TextView;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHDRCBean;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.bean.CsstSHSwitchBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHSwitchTable;
import com.csst.smarthome.util.CsstSHConfigPreference;

public class TriplePanelSwitchAction extends Activity implements ICsstSHInitialize, ICsstSHConstant {
	private String TAG = "TriplePanelSwitchActivity";
	private boolean debug = true;
	
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	/** 当前设备 */
	private CsstSHDeviceBean mDevice = null;
	/** 设备按键列表 */
	private List<CsstSHDRCBean> mDeviceKeys = null;
	
	//用来存储当前面板想对应的开关
	private CsstSHSwitchBean curswitchbean =null;
	
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	//从上个层面发送过来的器件名称
	private String switchname =null;
	
	byte ssuidLow = 0x00;
	
	private final int SWITCH1 = 1;
	private final int SWITCH2 =2;
	private final int SWITCH3 =3;
	private final int SWITCH4 =4;
	
	byte ssuidHight = 0x00;
	byte sendcmd = 0x00;
	byte ssstatus = 0x00;
	private Button 				mbtnback;
	private Button 	 mBtnDone;
    private TextView          titleView;
	private Button btnAllOn,btnAllOff;
	private CheckBox cb1,cb2,cb3; 
	private TextView et1,et2,et3;
	
	//每个按钮的状态标识
	private boolean allonflag=true,alloffflag =true,cb1flag = true,cb2flag = true,cb3flag = true;
	
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
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.triple_panel_switch_action);
		initDataSource();
//		TitleLayoutUtils.setupTitleLayout(this, switchname);
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
		// 数据库对
		mDb = csstSHDataBase.getWritDatabase();
		Intent intent = getIntent();
		if (null != intent){
			mDevice = (CsstSHDeviceBean) intent.getSerializableExtra("device");
			String strTemp =mDevice.getDeviceSSID();
			curswitchbean = CsstSHSwitchTable.getInstance().queryByDeviceId(mDb, mDevice.getDeviceId());
			String[] msgBuffer = strTemp.split(",");
		    ssuidLow = (byte)Integer.parseInt(msgBuffer[0]);
		    ssuidHight = (byte)Integer.parseInt(msgBuffer[1]);
			ssstatus = (byte)Integer.parseInt(msgBuffer[2]);
			
			modelName =(String) intent.getSerializableExtra("modelName");
			whichAction =(String) intent.getSerializableExtra("whichaction");
			location =(String) intent.getSerializableExtra("location");
			System.out.println("the value whichaction is "+whichAction+" the locatin is "+location);
			System.out.println("the value whichaction is "+" the locatin is "+mDevice.getDeviceName()+"the ssuidLow "+ssuidLow+" ssuidhight "+ssuidHight+" the status is "+ssstatus);
			switchname =mDevice.getDeviceName();
		}
		if(switchname ==null){
			switchname = getResources().getString(R.string.csst_adddevice_switch_three);
		}
	}


	public void initWidget() {
		// TODO Auto-generated method stub
		btnAllOff = (Button)findViewById(R.id.allOffBtn);
		btnAllOn = (Button) findViewById(R.id.allOnBtn);
		titleView = (TextView)findViewById(R.id.title_textview);
        mbtnback = (Button) findViewById(R.id.back_btn);
        mBtnDone = (Button) findViewById(R.id.mBtnDone);
		cb1 = (CheckBox) findViewById(R.id.drop_light_checkbox);
		cb2 = (CheckBox) findViewById(R.id.track_light_checkbox);
		cb3 = (CheckBox) findViewById(R.id.wall_light_checkbox);
		et1 = (TextView) findViewById(R.id.drop_light_name_input);
		et2 = (TextView) findViewById(R.id.track_light_name_input);
		et3 = (TextView) findViewById(R.id.wall_light_name_input);
	}

	public void initWidgetState() {
		// TODO Auto-generated method stub
		//如果是2位面板开关或者1位面板开关 显示界面做出调整
		if(ssuidLow == 2){
			cb3.setVisibility(View.GONE);
			et3.setVisibility(View.GONE);
		}else if(ssuidLow == 1) {
			cb2.setVisibility(View.GONE);
			et2.setVisibility(View.GONE);
			cb3.setVisibility(View.GONE);
			et3.setVisibility(View.GONE);
		}
//		et1.setVisibility(View.GONE);
//		et2.setVisibility(View.GONE);
//		et3.setVisibility(View.GONE);
		et1.setText(curswitchbean.getmSwitchName1());
		et2.setText(curswitchbean.getmSwitchName2());
		et3.setText(curswitchbean.getmSwitchName3());
	    titleView.setText(switchname);
		System.out.println("timerdelaygetstatu timerOut ");
		
	}
		
	public void initWidgetListener() {
		// TODO Auto-generated method stu
		
		mbtnback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {				
			Intent intent = new Intent();
			intent.setClass(TriplePanelSwitchAction.this, CsstSHAddModelWhichAction.class);
			intent.putExtra("modelName", modelName);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			TriplePanelSwitchAction.this.finish();
			if (mDb != null)
			mDb.close();
			}
		});
		
		
		mBtnDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				byte cmd = 0x00;
				//获取各个状态
//				if(cb1.isSelected()){
//					   System.out.println("cb1 11");
//					cmd = (byte) (cmd|0x11);
//				}else{
//					 System.out.println("cb1 00");
//					cmd = (byte) (cmd&0xfe);
//				}
				
				switch((int)ssuidLow){
				case SWITCH1:
					if(cb1.isChecked()){
						cmd = (byte)0xf1;
					}else{
						cmd = (byte)0xf0;
					}
					break;
				case SWITCH2:
					if(cb1.isChecked()){
						   System.out.println("cb1 1");
						cmd = (byte) (cmd|0x31);
					}else{
						 System.out.println("cb1 0");
						cmd = (byte) (cmd&0xfe);
					}
					if(cb2.isChecked()){
						System.out.println("cb2 1");
						cmd = (byte) (cmd|0x32);
					}else{
						System.out.println("cb2 0");
						cmd = (byte) (cmd&0xfc);
					}
					if(cb1.isChecked()&&cb2.isChecked()){
						cmd = (byte)0xf1;
					}
					if(!cb1.isChecked()&&!cb2.isChecked()){
						cmd = (byte)0xf0;
					}
					break;
				case SWITCH3:
					if(cb1.isChecked()){
						   System.out.println("cb1 1");
						cmd = (byte) (cmd|0x71);
					}else{
						 System.out.println("cb1 0");
						cmd = (byte) (cmd&0xfe);
					}
					if(cb2.isChecked()){
						System.out.println("cb2 1");
						cmd = (byte) (cmd|0x72);
					}else{
						System.out.println("cb2 0");
						cmd = (byte) (cmd&0xfc);
					}
					if(cb3.isChecked()){
						cmd = (byte) (cmd|0x74);
					}else{
						cmd = (byte) (cmd&0xfb);
					}
					if(!cb1.isChecked()&&!cb2.isChecked()&&!cb3.isChecked()){
						cmd = (byte)0xf0;
					}
					if(cb1.isChecked()&&cb2.isChecked()&&cb3.isChecked()){
						cmd = (byte)0xf1;
					}
					break;
					
				}
				System.out.println("go back add whichaction the cmd is  "+cmd);
				Intent intent = new Intent(TriplePanelSwitchAction.this, CsstSHAddModelAddAction.class);
				intent.putExtra("location",location+mDevice.getDeviceName().toString());
				byte cmdtype = (byte)0x02;
				intent.putExtra("keyCode",getResources().getString(R.string.csst_adddevice_switch)+","+cmd+","+ssuidLow+","+ssuidHight+cmdtype);
				intent.putExtra("modelName", modelName);
				startActivity(intent);
				TriplePanelSwitchAction.this.finish();
				if (mDb != null)
				mDb.close();
			}
		});
		btnAllOn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				cb1.setSelected(true);
				cb2.setSelected(true);
				cb3.setSelected(true);
				
			}
		});
		btnAllOff.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				cb1.setSelected(false);
				cb2.setSelected(false);
				cb3.setSelected(false);}
		});
		
		cb1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
		
				// TODO Auto-generated method stub
				if(cb1flag){}else{}
			}
		});
			
		cb2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				// TODO Auto-generated method stub
				if(cb2flag){}else{}
			}
			});
		cb3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(cb3flag){
				}else{
				}
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
		Intent intent = new Intent(TriplePanelSwitchAction.this, CsstSHAddModelWhichAction.class);
		intent.putExtra("location",location+mDevice.getDeviceName().toString());
		intent.putExtra("keyCode",getResources().getString(R.string.csst_adddevice_charge)+","+"0"+","+ssuidLow+","+ssuidHight);
		intent.putExtra("modelName", modelName);
		startActivity(intent);
		TriplePanelSwitchAction.this.finish();
		if (mDb != null)
		mDb.close();
		
	}
	
	
    
    
    
}
