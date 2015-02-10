package com.csst.smarthome.activity.device;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.fragment.CsstSHAddModelAddAction;
import com.csst.smarthome.bean.CsstSHDRCBean;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHDeviceRCKeyTable;
import com.csst.smarthome.dao.CsstSHDeviceTable;
import com.csst.smarthome.rc.CsstSHAirConditionRCFragment_ZQL;
import com.csst.smarthome.rc.CsstSHCurtainRCFragment_ZQL;
import com.csst.smarthome.rc.CsstSHDVDCFragment;
import com.csst.smarthome.rc.CsstSHElectricfanRCFragment_ZQL;
import com.csst.smarthome.rc.CsstSHSOUNDRCFragment;
import com.csst.smarthome.rc.CsstSHSTBRCFragment_ZQL;
import com.csst.smarthome.rc.CsstSHTVRCFragment_ZQL;
import com.csst.smarthome.util.CsstContextUtil;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.csst.smarthome.util.CsstSHDBColumnUtil;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.encryption.BaseCodeMessage;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.message.baseMessage;
import com.lishate.message.rfcode.FinishRFCodeReqMessage;
import com.lishate.message.rfcode.SendRFCodeReqMessage;
import com.lishate.message.rfcode.StudyRFCodeReqMessage;
import com.lishate.message.rfcode.StudyRFCodeRspMessage;
import com.lishate.net.UdpJavaEncodingProcess;
import com.lishate.net.UdpProcess;

/**
 * 操作遥控器
 * @author liuyang
 */
public class CsstSHUseDeviceActivity extends FragmentActivity implements
		ICsstSHInitialize, ICsstSHConstant {
	
	public static final String TAG = "CsstSHUseDeviceActivity";

	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	/** 当前设备 */
	private CsstSHDeviceBean mDevice = null;
	
	/** 当前设备 */
	private CsstSHDeviceBean curDeviceBean = null;
	private CsstSHDRCBean curkey=null;
	
	byte ssuidLow = 0x00;
	
	byte ssuidHight = 0x00;
	//因为接收的数据为不定长度的.需要定义一个比较大的buffer 进行全局变量的设定
	byte cmdcontentbuffer[] = new byte[256];
	
	/** 设备按键列表 */
	private List<CsstSHDRCBean> mDeviceKeys = null;
	/** 按键监听器 */
	public KeyListener mKeyListener = null;
	public static String[] msgBuffer  = null;
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
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	
	
	private Handler handlerconfigwifi;
	/** 主控MAC地址 */
	public String mMacAdress = null;
	
	
	//转动圆圈
	ProgressDialog myprogress;
    // 超时
    Timer timerdelaygetstatu;
    Timer timerback;
    boolean timeOut_flag = false;//当定时器 到达指定时间时 不接受清码 对码等操作 的标志位
//    boolean cleanTimer = false; //清除定时器 true 表示定时器没作用
    
	// timer
	private final Timer timer=new Timer();
	private TimerTask tast;
		
	private Timer timerconfigwifi;
	private TimerTask tastconfigwifi;
	
	private final int HANDLE_MSG_FROM_CONTROL =1;
	private final int HANDLE_MSG_FORM_PROGRESS =2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csst_use_device_layout);
		initDataSource();
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
		openDevice();
		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
		//读取当前主控MAC地址
		mMacAdress = configPreference.getMacAdress();
		configwifitimer();
	}
	
	@Override
	public void initDataSource() {
		Intent intent = getIntent();
		if (null != intent){
			modelName =(String) intent.getSerializableExtra("modelName");
			mDevice = (CsstSHDeviceBean) intent.getSerializableExtra("device");
			whichAction =(String) intent.getSerializableExtra("whichaction");
			location =(String) intent.getSerializableExtra("location");
			
			curDeviceBean = mDevice;
			if(curDeviceBean.isSearched()==1){
				ssuidLow=(byte)Integer.parseInt(curDeviceBean.getDeviceSSID().split(",")[0]);
				ssuidHight=(byte)Integer.parseInt(curDeviceBean.getDeviceSSID().split(",")[1]);
			}
			System.out.println("the value whichaction is "+whichAction+" the locatin is "+location+"the ssuidlow is "+ssuidLow +"the ssuidHight is"+ssuidHight);
			}
			
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		if (null != mDevice){
			//查询设备对应的按键列表
			mDeviceKeys = CsstSHDeviceRCKeyTable.getInstance().queryByDevice(mDb, mDevice.getDeviceId());
		}
	}
	
	/**
	 * 通过按键标识符查询当前按键对象
	 * @param v
	 * @return
	 */
	public final CsstSHDRCBean getDeviceRCKeyByIdentify(View v){
		int identify = (Integer) v.getTag();
		return getDeviceRCKeyByIdentify(identify);
	}
	
	/**
	 * 根据按钮标识符获取按钮对象
	 * @param identify
	 * @return
	 */
	public final CsstSHDRCBean getDeviceRCKeyByIdentify(int identify){
		CsstSHDRCBean key = null;
		for (int i = 0; i < mDeviceKeys.size(); i++){
			key = mDeviceKeys.get(i);
			if (key.getRCKeyIdentify() == identify){
				return key;
			}
		}
		return null;
	}
	
	public final SQLiteDatabase getDB(){
		return mDb;
	}

	public final void updateDeviceKey(CsstSHDRCBean key){
		CsstSHDeviceRCKeyTable.getInstance().update(mDb, key);
	}
	
	@Override
	public void initWidget() {
		
	}

	@Override
	public void initWidgetState() {
	}

	@Override
	public void initWidgetListener() {
		mKeyListener = new KeyListener();
	}

	@Override
	public void addWidgetListener() {
		
	}

	private final void openDevice(){
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = null;
		switch (mDevice.getRCTypeId()) {
			//电视
			case REMOTE_TYPE_1:
//				CsstSHTVRCFragment tvFragment = (CsstSHTVRCFragment) fm.findFragmentById(R.id.use_device_container);
//				if (null == tvFragment || tvFragment.getDeviceIdentification() != mDevice.getDeviceId()){
//					fragment = CsstSHTVRCFragment.getInstance(mDevice);
//				}
				
				CsstSHTVRCFragment_ZQL tvFragment = (CsstSHTVRCFragment_ZQL) fm.findFragmentById(R.id.use_device_container);
				if (null == tvFragment || tvFragment.getDeviceIdentification() != mDevice.getDeviceId()){
					fragment = CsstSHTVRCFragment_ZQL.getInstance(mDevice);
				}
				break;
			//空调
			case REMOTE_TYPE_2:
//				CsstSHAirConditionRCFragment airConditionFragment = (CsstSHAirConditionRCFragment) fm.findFragmentById(R.id.use_device_container);
//				if (null == airConditionFragment || airConditionFragment.getDeviceIdentification() != mDevice.getDeviceId()){
//					fragment = CsstSHAirConditionRCFragment.getInstance(mDevice);
//				}
				
				CsstSHAirConditionRCFragment_ZQL airConditionFragment = (CsstSHAirConditionRCFragment_ZQL) fm.findFragmentById(R.id.use_device_container);
				if (null == airConditionFragment || airConditionFragment.getDeviceIdentification() != mDevice.getDeviceId()){
					fragment = CsstSHAirConditionRCFragment_ZQL.getInstance(mDevice);
				}
				break;
			//机顶盒
			case REMOTE_TYPE_3:
//				CsstSHSTBRCFragment stbFragment = (CsstSHSTBRCFragment) fm.findFragmentById(R.id.use_device_container);
//				if (null == stbFragment || stbFragment.getDeviceIdentification() != mDevice.getDeviceId()){
//					fragment = CsstSHSTBRCFragment.getInstance(mDevice);
//				}
				CsstSHSTBRCFragment_ZQL stbFragment = (CsstSHSTBRCFragment_ZQL) fm.findFragmentById(R.id.use_device_container);
				if (null == stbFragment || stbFragment.getDeviceIdentification() != mDevice.getDeviceId()){
					fragment = CsstSHSTBRCFragment_ZQL.getInstance(mDevice);
				}
				break;
			//DVD
			case REMOTE_TYPE_4:
				CsstSHDVDCFragment dvdFragment = (CsstSHDVDCFragment) fm.findFragmentById(R.id.use_device_container);
				if (null == dvdFragment || dvdFragment.getDeviceIdentification() != mDevice.getDeviceId()){
					fragment = CsstSHDVDCFragment.getInstance(mDevice);
				}
				break;
			//音响
			case REMOTE_TYPE_5:
//				CsstSHLightingRCFragment lightFragment = (CsstSHLightingRCFragment) fm.findFragmentById(R.id.use_device_container);
//				if (null == lightFragment || lightFragment.getDeviceIdentification() != mDevice.getDeviceId()){
//					fragment = CsstSHLightingRCFragment.getInstance(mDevice);
//				}
				
				CsstSHSOUNDRCFragment lightFragment = (CsstSHSOUNDRCFragment) fm.findFragmentById(R.id.use_device_container);
				if (null == lightFragment || lightFragment.getDeviceIdentification() != mDevice.getDeviceId()){
					fragment = CsstSHSOUNDRCFragment.getInstance(mDevice);
				}
				
				
				break;
			//窗帘
			case REMOTE_TYPE_6:
//				CsstSHCurtainRCFragment curtainFragment = (CsstSHCurtainRCFragment) fm.findFragmentById(R.id.use_device_container);
//				if (null == curtainFragment || curtainFragment.getDeviceIdentification() != mDevice.getDeviceId()){
//					fragment = CsstSHCurtainRCFragment.getInstance(mDevice);
//				}
				CsstSHCurtainRCFragment_ZQL curtainFragment = (CsstSHCurtainRCFragment_ZQL) fm.findFragmentById(R.id.use_device_container);
				if (null == curtainFragment || curtainFragment.getDeviceIdentification() != mDevice.getDeviceId()){
					fragment = CsstSHCurtainRCFragment_ZQL.getInstance(mDevice);
				}
				break;
			//风扇
			case REMOTE_TYPE_7:
//				CsstSHElectricfanRCFragment electricfanFragment = (CsstSHElectricfanRCFragment) fm.findFragmentById(R.id.use_device_container);
//				if (null == electricfanFragment || electricfanFragment.getDeviceIdentification() != mDevice.getDeviceId()){
//					fragment = CsstSHElectricfanRCFragment.getInstance(mDevice);
//				}
				CsstSHElectricfanRCFragment_ZQL electricfanFragment = (CsstSHElectricfanRCFragment_ZQL) fm.findFragmentById(R.id.use_device_container);
				if (null == electricfanFragment || electricfanFragment.getDeviceIdentification() != mDevice.getDeviceId()){
					fragment = CsstSHElectricfanRCFragment_ZQL.getInstance(mDevice);
				}
				break;
		}
		
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.use_device_container, fragment);
		//设置动画效果
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
	}
	
	
	public void powerKeySelecttip()
	 {
			AlertDialog.Builder builder=new Builder(CsstSHUseDeviceActivity.this);
			builder.setMessage(getResources().getString(R.string.power_key_select));
			builder.setPositiveButton(getResources().getString(R.string.csst_action_on), new DialogInterface.OnClickListener()
			{  
				public void onClick(DialogInterface dialog, int which)
				{ 
					
					
				}
			});  
				 builder.setNegativeButton(getResources().getString(R.string.csst_action_off), new DialogInterface.OnClickListener()
				 {  
					 public void onClick(DialogInterface dialog, int which) 
					 {  
						 
						 
					 }	});  
			builder.create().show();
		}	
	
	public final class KeyListener implements View.OnClickListener, View.OnLongClickListener{

		@Override
		public boolean onLongClick(View v) {
			CsstSHDRCBean key = getDeviceRCKeyByIdentify(v);
			//学习
			new StudyCodeTask(key).execute();
			return false;
		}

		@Override
		public void onClick(View v) {
			final CsstSHDRCBean key = getDeviceRCKeyByIdentify(v);
			
			System.out.println("the mDevice is here ");
			
//			System.out.println("the mDevice room is  "+mDevice.getRoomId());
//			System.out.println("the mDevice name is  "+mDevice.getDeviceName().toString());
//			System.out.println("the mDevice ID is  "+mDevice.getDeviceId());
//			System.out.println("the mDevice RCType is  "+mDevice.getRCTypeId());
//			System.out.println("the mDevice SSID is  "+mDevice.getDeviceSSID());
			
			if (key.getDRCCmdCode() != null){
				
//				System.out.println("the mDevice room is  "+mDevice.getRoomId());
//				System.out.println("the mDevice name is  "+mDevice.getDeviceName().toString());
//				System.out.println("the mDevice ID is  "+mDevice.getDeviceId());
//				System.out.println("the mDevice RCType is  "+mDevice.getRCTypeId());
//				System.out.println("the mDevice SSID is  "+mDevice.getDeviceSSID());
				System.out.println("the key.getDRCCmdCode() is  "+key.getDRCCmdCode());
				if(whichAction.equals("whichAction")){
					System.out.println("go back add whichaction  ");
					if(curDeviceBean.isSearched()==1&&key.getRCKeyName().equals(getResources().getString(R.string.tv_power_rckey))){//初步以电源开关的名称来判断是对电源开关进行发码
						
						powerKeySelecttip();
					}else{
						Intent intent = new Intent(CsstSHUseDeviceActivity.this, CsstSHAddModelAddAction.class);
						intent.putExtra("location",location+mDevice.getDeviceName().toString());
						intent.putExtra("keyCode",key.getDRCCmdCode());
						intent.putExtra("modelName", modelName);
						startActivity(intent);
						CsstSHUseDeviceActivity.this.finish();
						if (mDb != null)
						mDb.close();
					}
					
					
				}else{
					LoadProcess_fisrt();
					curkey = key ;
					if(curDeviceBean.isSearched()==1&&key.getRCKeyName().equals(getResources().getString(R.string.tv_power_rckey))){//初步以电源开关的名称来判断是对电源开关进行发码
						if(curDeviceBean.isDeviceIconPersert()){
							new sendChargCmd((byte)0x00).execute();
						}else{
							new sendChargCmd((byte)0x01).execute();
						}
					}else{
						new SendCodeTast(key).execute();
					}
					
				}
				
			}else{
				if(whichAction.equals("whichAction")){
					Toast.makeText(CsstSHUseDeviceActivity.this, R.string.csst_rc_key_notaction, Toast.LENGTH_LONG).show();
					
				}else{
				
//				if(whichAction.equals("whichAction")){
//					System.out.println("go back add whichaction  ");
//					Intent intent = new Intent(CsstSHUseDeviceActivity.this, CsstSHAddModelAddAction.class);
//					intent.putExtra("location",location+mDevice.getDeviceName().toString());
//					intent.putExtra("keyCode",key.getDRCCmdCode());
//					intent.putExtra("modelName", modelName);
//					startActivity(intent);
//					CsstSHUseDeviceActivity.this.finish();
//					if (mDb != null)
//						mDb.close();
//				}else{
				AlertDialog.Builder builder = new AlertDialog.Builder(CsstSHUseDeviceActivity.this);
				builder.setMessage(R.string.csst_study_key_code_message);
				builder.setNegativeButton(R.string.csst_cancel, null);
				builder.setPositiveButton(R.string.csst_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new StudyCodeTask(key).execute();
					}
				});
				builder.show();
				}
			}
//			}
		}
	}
	
	public final class SendCodeTast extends AsyncTask<Void, Void, Boolean>{
		private CsstSHDRCBean keyBean = null;
		
		public SendCodeTast(CsstSHDRCBean key) {
			keyBean = key;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			String[] msgBuffer = keyBean.getDRCCmdCode().split(",");
			
			ServerItemModel sim = new ServerItemModel();
			byte[] buffer = new byte[msgBuffer.length - 1];
			int temp = 0;
			for (int i = 1; i < msgBuffer.length; i++){
				temp = Integer.parseInt(msgBuffer[i].trim().substring(2), 16);
				buffer[i - 1] = (byte)(0xFF & temp);
			}
			sim.setIpaddress("218.244.129.177");
			sim.setPort(12188);
			SendRFCodeReqMessage slrm = new SendRFCodeReqMessage();
			slrm.Direct = MessageDef.BASE_MSG_FT_REQ;
			slrm.setFromId(GobalDef.MOBILEID);
			slrm.MsgType = MessageDef.MESSAGE_TYPE_RFCODE_SEND_REQ;
			slrm.Seq = MessageSeqFactory.GetNextSeq();
			slrm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
			slrm.ToType = MessageDef.BASE_MSG_FT_HUB;
			slrm.setToId(Long.valueOf(msgBuffer[0]));
			System.out.println(TAG+"");
			System.out.println(TAG+"the mac adress is "+Long.valueOf(msgBuffer[0]));
			slrm.setCodeBuf(buffer);
			baseMessage msg = UdpProcess.GetMsgReturn(slrm, sim);
			if(msg == null){
				return false;
			} else {
				//SendRFCodeRspMessage srfrm = (SendRFCodeRspMessage)msg;
				return true;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			int response = result ? R.string.csst_send_cmd_success : R.string.csst_send_cmd_fail;
			if(myprogress!=null)
				myprogress.dismiss();
			Toast.makeText(CsstSHUseDeviceActivity.this, response, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 学码操作
	 * @author liuyang
	 */
	public final class StudyCodeTask extends AsyncTask<Void, Void, Boolean>{
		
		private CsstSHDRCBean mKeyBean = null;
		
		private Dialog mDialog = null;
		
		public StudyCodeTask(CsstSHDRCBean key) {
			mKeyBean = key;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//取消回调接口
			DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (StudyCodeTask.this.getStatus() != AsyncTask.Status.FINISHED){
						StudyCodeTask.this.cancel(true);
					}
					//发送停止命令
					new FinishCodeTask().execute();
					Toast.makeText(CsstSHUseDeviceActivity.this, R.string.csst_study_finish, Toast.LENGTH_LONG).show();
				}
			};
			mDialog =CsstContextUtil.searchDialog(CsstSHUseDeviceActivity.this, null, getString(R.string.csst_study_code_message), callback);
			mDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				ServerItemModel sim = new ServerItemModel();
				sim.setIpaddress("255.255.255.255");
				sim.setPort(80);
				StudyRFCodeReqMessage slrm = new StudyRFCodeReqMessage();
				slrm.Direct = MessageDef.BASE_MSG_FT_REQ;
				slrm.setFromId(GobalDef.MOBILEID);
				slrm.MsgType = MessageDef.MESSAGE_TYPE_RFCODE_STUDY_REQ;
				slrm.Seq = MessageSeqFactory.GetNextSeq();
				slrm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
				slrm.ToType = MessageDef.BASE_MSG_FT_HUB;
				slrm.setToId(toid);
				Log.d(TAG, "will get msg return");
				baseMessage msg = UdpProcess.GetMsgReturn(slrm, sim);
				if (null == msg){
					return false;
				}else{
					StudyRFCodeRspMessage srfrm = (StudyRFCodeRspMessage) msg;
					byte[] codeBuf = srfrm.getCodeBuf();
					String bufferMessage = null;
					if (null != codeBuf){
						bufferMessage = CsstSHDBColumnUtil.bufferToMessge(codeBuf);
						bufferMessage = srfrm.getFromId() + ", " + bufferMessage;
						mKeyBean = getDeviceRCKeyByIdentify(mKeyBean.getRCKeyIdentify());
						mKeyBean.setDRCCmdCode(bufferMessage);
						CsstSHDeviceRCKeyTable.getInstance().update(mDb, mKeyBean);
						
						msgBuffer = mKeyBean.getDRCCmdCode().split(",");
						System.out.println("msgBuffer[0] set mac address "+msgBuffer[0]);
						
						configPreference.setMacAdress(msgBuffer[0]);
						return true;
					}
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result){
				//发送停止命令
				new FinishCodeTask().execute();
			}
			int response = result ? R.string.csst_study_success : R.string.csst_study_fail;
			Toast.makeText(CsstSHUseDeviceActivity.this, response, Toast.LENGTH_LONG).show();
			mDialog.dismiss();
		}
	}
	
	/**
	 * 结束命令
	 * @author liuyang
	 */
	public final class FinishCodeTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			ServerItemModel sim = new ServerItemModel();
			sim.setIpaddress("255.255.255.255");
			sim.setPort(80);
			FinishRFCodeReqMessage slrm = new FinishRFCodeReqMessage();
			slrm.Direct = MessageDef.BASE_MSG_FT_REQ;
			slrm.setFromId(GobalDef.MOBILEID);
			slrm.MsgType = MessageDef.MESSAGE_TYPE_RFCODE_FINISH_REQ;
			slrm.Seq = MessageSeqFactory.GetNextSeq();
			slrm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
			slrm.ToType = MessageDef.BASE_MSG_FT_HUB;
			slrm.setToId(toid);
			UdpProcess.GetMsgReturn(slrm, sim);
			return null;
		}
	}
	
	public void configwifitimer(){
		handlerconfigwifi=new Handler(){
			  @Override
				  public void handleMessage(Message msg) {
				  System.out.println(TAG+" configwifitimer Message is here   ");
				   super.handleMessage(msg);
				   //主要处理seekBar 参数
				   if(curDeviceBean.getDeviceIconPath().equals(getResources().getString(R.string.csst_adddevice_charge))){
					   if(cmdcontentbuffer[5]==(byte)0x01){
							curDeviceBean.setDeviceIconPersert(true);
							CsstSHDeviceTable.getInstance().update(mDb, curDeviceBean);
						   
					   }else{
						   curDeviceBean.setDeviceIconPersert(false);
						   CsstSHDeviceTable.getInstance().update(mDb, curDeviceBean);
						     
					   }   
				   }
				  }
			    };
	}
	
	
	public final class sendChargCmd extends AsyncTask<Void, Void, Boolean>{
  		byte cmd = 0x00;
  		public sendChargCmd(byte cmd){
  			
  			this.cmd = cmd;
  		}
  		@Override
  		protected void onPreExecute() {
  			super.onPreExecute();
  		}
  		@Override
  		protected Boolean doInBackground(Void... params) {
			System.out.println(" sendChargCmd \n");
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
  			byte[] contentBuf = new byte[6];
  			contentBuf[0]=(byte)0x07;
  			contentBuf[1]=(byte)0x01;
  			contentBuf[2]=(byte)ssuidLow;
  			contentBuf[3]=(byte)ssuidHight;
  			//如果是一位面板开关
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
  				System.out.println("sendChargCmd get messega finish ");
  				//先停止原来来定时�?因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
  				Message msg = handlerconfigwifi.obtainMessage(1);
  	  			handlerconfigwifi.sendMessage(msg);
  				return true;
  			}
  			else{
  				return false;
  				
  			}
  			
  		}

  		@Override
  		protected void onPostExecute(Boolean result) {
  			super.onPostExecute(result);
  			try{
  				Thread.sleep(1000);
  			}catch(Exception ex){
  				System.out.println(ex.toString());
  			}
//  			if(result){
  			//成功与否都发指令
  				new SendCodeTast(curkey).execute();
//  			}
  			Message msg = handlerconfigwifi.obtainMessage(1);
  			handlerconfigwifi.sendMessage(msg);
  			//先停止原来来定时�?因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
  		}
  		
  	}
	
   	public void  LoadProcess_fisrt() {
   		myprogress = new ProgressDialog(this);
   		myprogress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
   		myprogress.setMessage(this.getResources().getString(R.string.csst_send_cmd));
   		myprogress.setIndeterminate(false);
   		myprogress.setCancelable(false);
   		myprogress.show();
   	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 销毁时关闭数据库
		if (mDb != null)
			mDb.close();
	}
}
