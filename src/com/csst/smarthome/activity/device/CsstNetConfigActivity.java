package com.csst.smarthome.activity.device;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import wlt_Config.Config;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.device.CsstSHUseDeviceActivity.FinishCodeTask;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.csst.smarthome.util.CsstSHDBColumnUtil;
import com.integrity_project.smartconfiglib.FirstTimeConfig;
import com.integrity_project.smartconfiglib.FirstTimeConfigListener;
import com.lishate.data.GobalDef;
import com.lishate.data.model.DeviceItemModel;
import com.lishate.data.model.ServerItemModel;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.message.baseMessage;
import com.lishate.message.rfcode.StudyRFCodeReqMessage;
import com.lishate.message.rfcode.StudyRFCodeRspMessage;
import com.lishate.net.UdpProcess;

/**
 * 中控配置
 * @author liuyang
 */
public class CsstNetConfigActivity extends Activity implements ICsstSHInitialize, ICsstSHConstant{

	/** 返回按钮 */
	private Button mBtnCancel = null;
	/** 完成按钮 */
	private Button mBtnDone = null;
	/** 标题 */
	private TextView mTVTitle = null;
	/** SSID */
	private EditText mEDSsid = null;
	/** 密码 */
	private EditText mEDPawd = null;
	/** 显示密码 */
	private CheckBox mCBShowPawd = null;
	/** 配置 */
	private Button mBtnConfig = null;
	
	String TAG = "CsstNetConfigActivity";
	
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	
	
	private BackBtnListener mBackBtnListener = null;
	private ConfigBtnListener mConfigBtnListener = null;
	private ShowBtnListener mShowBtnListener = null;
	private ConfigListener mConfigListener = null;
	
	private WifiManager mWifiManager = null;
	private Config mConfig = null;
	private FirstTimeConfig mFirstConfig = null;
	private ProgressDialog mProgressDialog = null;
	private boolean isCalled = false;
	private String mGateway = null;
	
	/** 配置进度条 */
	private static final int CONFIG_PROGRESS_CMD = 0x00;
	/** 配置错误 */
	private static final int CONFIG_ERROR_CMD = 0x01;
	/** 配置成功 */
	private static final int CONFIG_SUCCESS_CMD = 0x02;
	/** 配置超时 */
	private static final int CONFIG_TIMEOUT_CMD = 0x03;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csst_net_config_layout);
		initDataSource();
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
	}
	
	@Override
	public void initDataSource() {
		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		DhcpInfo di = mWifiManager.getDhcpInfo();
		if (null != di){
			int gatwayVal = di.gateway;
			mGateway =  (String.format("%d.%d.%d.%d", (gatwayVal & 0xff),(gatwayVal >> 8 & 0xff),(gatwayVal >> 16 & 0xff),	(gatwayVal >> 24 & 0xff))).toString();
		}
		mConfig = new Config();
	}

	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
		mTVTitle = (TextView) findViewById(R.id.mTVTitle);
		mBtnDone = (Button) findViewById(R.id.mBtnDone);
		mEDSsid = (EditText) findViewById(R.id.mEDSsid);
		mEDPawd = (EditText) findViewById(R.id.mEDPawd);
		mCBShowPawd = (CheckBox) findViewById(R.id.mCBShowPawd);
		mBtnConfig = (Button) findViewById(R.id.mBtnConfig);
	}

	@Override
	public void initWidgetState() {
		//设置标题
		mTVTitle.setText(R.string.csst_midcenterset);
		//隐藏完成按钮
		mBtnDone.setVisibility(View.GONE);
		//链接热点
		mEDSsid.setText(mConfig.initData(this));
		//初始化配置文件
		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
	}

	@Override
	public void initWidgetListener() {
		mBackBtnListener = new BackBtnListener();
		mShowBtnListener = new ShowBtnListener();
		mConfigBtnListener = new ConfigBtnListener();
		mConfigListener = new ConfigListener();
	}

	@Override
	public void addWidgetListener() {
		mBtnCancel.setOnClickListener(mBackBtnListener);
		mCBShowPawd.setOnCheckedChangeListener(mShowBtnListener);
		mBtnConfig.setOnClickListener(mConfigBtnListener);
	}
	
	/**
	 * 返回事件
	 */
	private final void backEvent(){
		finish();
	}
	
	@Override
	public void onBackPressed() {
		backEvent();
		//super.onBackPressed();
	}
	
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				switch (msg.what) {
					case CONFIG_PROGRESS_CMD:
						if ((Boolean) msg.obj){
							mProgressDialog = ProgressDialog.show(CsstNetConfigActivity.this, null, getString(R.string.csst_net_search_device), true, true);
							mProgressDialog.setCanceledOnTouchOutside(false);
						}else{
							mProgressDialog.dismiss();
						}
						break;
						
					case CONFIG_ERROR_CMD:
						obtainMessage(CONFIG_PROGRESS_CMD, false).sendToTarget();
						if (mFirstConfig != null){
							if (isCalled){
								isCalled = false;
								mFirstConfig.stopTransmitting();
							}
						}
						Toast.makeText(CsstNetConfigActivity.this, R.string.renwu_config_fail, Toast.LENGTH_LONG).show(); //zengqinglin
						break;

					case CONFIG_SUCCESS_CMD:
						obtainMessage(CONFIG_PROGRESS_CMD, false).sendToTarget();
						if (mFirstConfig != null){
							if (isCalled){
								isCalled = false;
								mFirstConfig.stopTransmitting();
							}
						}
						Toast.makeText(CsstNetConfigActivity.this, R.string.renwu_config_sus, Toast.LENGTH_LONG).show(); //zengqinglin
						break;
						
					case CONFIG_TIMEOUT_CMD:
						obtainMessage(CONFIG_PROGRESS_CMD, false).sendToTarget();
						if (mFirstConfig != null){
							if (isCalled){
								isCalled = false;
								mFirstConfig.stopTransmitting();
							}
						}
						Toast.makeText(CsstNetConfigActivity.this, R.string.renwu_config_timeout, Toast.LENGTH_LONG).show();//zengqinglin
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * 配置操作回调
	 * @author liuyang
	 */
	private final class ConfigListener implements FirstTimeConfigListener{
		@Override
		public void onFirstTimeConfigEvent(FtcEvent fe, Exception e) {
			switch (fe) {
				case FTC_ERROR:
					mHandler.obtainMessage(CONFIG_ERROR_CMD).sendToTarget();
					break;
	
				case FTC_SUCCESS:
//					mHandler.obtainMessage(CONFIG_SUCCESS_CMD).sendToTarget();
					
//					SearchTask st = new SearchTask();
//					st.execute(new Integer[0]);
					System.out.println(" start the task StudyCodeTask ");
					new StudyCodeTask().execute();
					
					break;
				case FTC_TIMEOUT:
					mHandler.obtainMessage(CONFIG_TIMEOUT_CMD).sendToTarget();
					break;
			}
		}
	}
	
	/**
	 * 返回按钮监听器
	 * @author liuyang
	 */
	private final class BackBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			backEvent();
		}
	}
	
	/**
	 * 配置按钮监听器
	 * @author liuyang
	 */
	private final class ConfigBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			try {
				//不是wifi环境下退出
				if (!mWifiManager.isWifiEnabled()){
					Toast.makeText(CsstNetConfigActivity.this, R.string.csst_net_wifi_disEnabled, Toast.LENGTH_LONG).show();
					return;
				}
				String ssid = mEDSsid.getText().toString().trim();
				String pawd = mEDPawd.getText().toString().trim();
				if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(pawd)){
					Toast.makeText(CsstNetConfigActivity.this, R.string.csst_net_para_empty, Toast.LENGTH_LONG).show();
					return;
				}
				isCalled = false;
				mHandler.obtainMessage(CONFIG_PROGRESS_CMD, true).sendToTarget();
				if (!isCalled){
					isCalled = true;
					mFirstConfig = new FirstTimeConfig(mConfigListener, pawd, null,	mGateway, ssid, "CC3000");
					mFirstConfig.transmitSettings();
				}else{
					if (mFirstConfig != null){
						if (isCalled){
							isCalled = false;
							mFirstConfig.stopTransmitting();
						}
					}
				}
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 显示密码按钮监听器
	 * @author liuyang
	 */
	private final class ShowBtnListener implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked){
				mEDPawd.setInputType(InputType.TYPE_CLASS_TEXT);
			}else{
				mEDPawd.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
		}
	}
	
	public class getMacAdressThread extends Thread{
        public void run() {
			ServerItemModel sim = new ServerItemModel();
			sim.setIpaddress("255.255.255.255");
			sim.setPort(80);
			System.out.println("will get msg return 111");
			StudyRFCodeReqMessage slrm = new StudyRFCodeReqMessage();
			slrm.Direct = MessageDef.BASE_MSG_FT_REQ;
			slrm.setFromId(GobalDef.MOBILEID);
			System.out.println("will get msg return 222");
			slrm.MsgType = MessageDef.MESSAGE_TYPE_RFCODE_STUDY_REQ;
			slrm.Seq = MessageSeqFactory.GetNextSeq();
			System.out.println("will get msg return 3333");
			slrm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
			slrm.ToType = MessageDef.BASE_MSG_FT_HUB;
			slrm.setToId(toid);
			
			System.out.println("will get msg return");
			baseMessage msg = UdpProcess.GetMsgReturn(slrm, sim);
			if (null == msg){
				
			}else{
				StudyRFCodeRspMessage srfrm = (StudyRFCodeRspMessage) msg;
				byte[] codeBuf = srfrm.getCodeBuf();
				System.out.println("will get msg return 222");
				String bufferMessage = null;
				if (null != codeBuf){
					bufferMessage = CsstSHDBColumnUtil.bufferToMessge(codeBuf);
					bufferMessage = srfrm.getFromId() + ", " + bufferMessage;
					
					System.out.println("msgBuffer[0]8888888 8888888888 "+bufferMessage);
					String[] msgBuffer =bufferMessage.split(",");
					System.out.println("the mac string is "+msgBuffer[0]);
					configPreference.setMacAdress(msgBuffer[0]);
				}
			}
        	
        }
	}
	/**
	 * 学码操作
	 * @author liuyang
	 */
	public  class StudyCodeTask extends AsyncTask<Void, Void, Boolean>{
		
		public StudyCodeTask() {
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				ServerItemModel sim = new ServerItemModel();
				sim.setIpaddress("255.255.255.255");
				sim.setPort(80);
				System.out.println("will get msg return 111");
				StudyRFCodeReqMessage slrm = new StudyRFCodeReqMessage();
				slrm.Direct = MessageDef.BASE_MSG_FT_REQ;
				slrm.setFromId(GobalDef.MOBILEID);
				System.out.println("will get msg return 222");
				slrm.MsgType = MessageDef.MESSAGE_TYPE_RFCODE_STUDY_REQ;
				slrm.Seq = MessageSeqFactory.GetNextSeq();
				System.out.println("will get msg return 3333");
				slrm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
				slrm.ToType = MessageDef.BASE_MSG_FT_HUB;
				slrm.setToId(toid);
				
				System.out.println("will get msg return");
				baseMessage msg = UdpProcess.GetMsgReturn(slrm, sim);
				if (null == msg){
					return false;
				}else{
					StudyRFCodeRspMessage srfrm = (StudyRFCodeRspMessage) msg;
					byte[] codeBuf = srfrm.getCodeBuf();
					System.out.println("will get msg return 222");
					String bufferMessage = null;
					if (null != codeBuf){
						bufferMessage = CsstSHDBColumnUtil.bufferToMessge(codeBuf);
						bufferMessage = srfrm.getFromId() + ", " + bufferMessage;
						
						System.out.println("msgBuffer[0]8888888 8888888888 "+bufferMessage);
						String[] msgBuffer =bufferMessage.split(",");
						System.out.println("the mac string is "+msgBuffer[0]);
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
			if(result){
				mHandler.obtainMessage(CONFIG_SUCCESS_CMD).sendToTarget();
			}else{
				mHandler.obtainMessage(CONFIG_TIMEOUT_CMD).sendToTarget();
			}
			
		}

	}
	
	


}
