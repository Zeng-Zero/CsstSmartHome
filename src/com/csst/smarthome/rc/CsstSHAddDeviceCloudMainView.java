package com.csst.smarthome.rc;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.device.CsstSHAddDeviceActivity;
import com.csst.smarthome.activity.device.CsstSHUseDeviceActivity;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.util.CsstContextUtil;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.encryption.BaseCodeMessage;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.net.UdpJavaEncodingProcess;

/**
 * 添加设备
 * @author liuyang
 */
public class CsstSHAddDeviceCloudMainView extends Activity implements ICsstSHInitialize, ICsstSHConstant{

	public static final String TAG = "CsstSHAddDeviceCloudMainView";
	private boolean debug = true;
	
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	/** 主控MAC地址 */
	private String mMacAdress = null;
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对�?*/
	private SQLiteDatabase mDb = null;
	/** 房间id */
	private int mRoomId = -1;
	
	/** 返回按钮 */
	private Button mBtnCancel = null;
	/** 完成按钮 */
	private Button mBtnDone = null;
	
	/** 返回按钮监听�?*/
	private BackBtnListener mBackBtnListener = null;
	/** 完成按钮监听�?*/
	private DoneBtnListener mDoneBtnListener = null;
	
// timer
	private final Timer timer=new Timer();
	private TimerTask tast;
	
//		 seek send cmd timer is here;
	private Timer timerconfigwifi;
	private TimerTask tastconfigwifi;
	private Handler handlerconfigwifi;
	
	byte contentBuffer[] = new byte[7];
	
	byte[] contentBuftosend = {0};
	
	
	boolean sendflag = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sendflag = true;
		setContentView(R.layout.csst_rc_tableview);
		initDataSource();
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		
		}
	}
	
	@Override
	public void initDataSource() {
		// 初始数据�?
		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
		// 当前楼层对应的房间id
		mRoomId = configPreference.getRoomId();
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对�?
		mDb = csstSHDataBase.getWritDatabase();
		mMacAdress = configPreference.getMacAdress();
		if(debug){
			System.out.println("the mMacAdress is "+mMacAdress);
		}
		configwifitimer();

	}

	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnrctableCancel);
		mBtnDone = (Button) findViewById(R.id.mBtnrctableDone);
	}

	@Override
	public void initWidgetState() {
	}

	@Override
	public void initWidgetListener() {
		mBackBtnListener = new BackBtnListener();
		mDoneBtnListener = new DoneBtnListener();
	}

	@Override
	public void addWidgetListener() {
		mBtnCancel.setOnClickListener(mBackBtnListener);
		mBtnDone.setOnClickListener(mDoneBtnListener);
		CsstContextUtil.hideInputKeyBoard(this);
	}
	
	/**
	 * 返回事件
	 */
	private final void backEvent(){
		sendflag = false;
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	if(keyCode==KeyEvent.KEYCODE_BACK){
		sendflag = false;
		backEvent();
	}
		 return true;
	}
	
	@Override
	public void onBackPressed() {
		backEvent();
		//super.onBackPressed();
	}
	

	@Override
	protected void onResume() {
		initWidgetState();
		//开启温度查�?和开关状态查�?
		sendflag = true;
		System.out.println(TAG+"onResume");
		super.onResume();
	}
	@Override
	protected void onPause(){
		//开启温度查�?和开关状态查�?
		sendflag = false;
		System.out.println(TAG+"onPause");
		super.onPause();
	}
	protected void onRestart(){
		sendflag = true;
		System.out.println(TAG+"onRestart");
		super.onRestart();	
	}
	
	protected void onStop(){
		sendflag = false;
		System.out.println(TAG+"onStop");
		super.onStop();	
	}
	@Override
	protected void onDestroy() {
		sendflag = false;
		System.out.println(TAG+"onDestroy");
		mDb.close();
		super.onDestroy();
	}
	
	
	/**
	 * 完成按钮监听�?
	 * @author liuyang
	 */
	private final class DoneBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			
		}
	}
	
	/**
	 * 返回按钮监听�?
	 * @author liuyang
	 */
	private final class BackBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			backEvent();
		}
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
				   super.handleMessage(msg);
				   //接收的的第五位为设备类型  1�?位无效，3位为面板开�?插座的位数，4位为uid�?位为设备类型,6无效�?
					
				   stopTimer();
					
				  }
			    };
	}
	
	

	private final class sendcmd extends AsyncTask<Void, Void, Boolean>{
		private Dialog mDialog = null;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//取消回调接口
			DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (sendcmd.this.getStatus() != AsyncTask.Status.FINISHED){
						sendcmd.this.cancel(true);
					}
					//发送停止命�?
					Toast.makeText(CsstSHAddDeviceCloudMainView.this, R.string.csst_adddevice_research_finish, Toast.LENGTH_LONG).show();
				}
			};
			mDialog =CsstContextUtil.searchDialog(CsstSHAddDeviceCloudMainView.this, null, getString(R.string.csst_adddevice_research_device_message), callback);
			mDialog.show();
		}

		
		@Override
		protected Boolean doInBackground(Void... params) {
			int flag = 0;
			
			while(sendflag){
				System.out.println("zengqinglin  UpdateTempe \n");
				//设备的MAC地址
			long toid = 109860815673L;
			ServerItemModel sim = new ServerItemModel();
			
			
//			String msgBuffer = "109861926250";
			//测试用的MAC地址
//			String msgBuffer = "109861926250";
			String msgBuffer ="1111";
			if(CsstSHUseDeviceActivity.msgBuffer!=null){
				msgBuffer=  CsstSHUseDeviceActivity.msgBuffer[0];
			}
//			//调试刘总的
//			msgBuffer ="109861926250";
			//调试刘总的
//			msgBuffer ="109861926226";
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
			
//			//温湿�?
//			bcm.setMCMD((byte) 4);
//			bcm.setSCMD((byte) 1);
			
			bcm.setMCMD((byte) 4);//22
			bcm.setSCMD((byte) 1);//23
			//
			byte[] contentBuf = {(byte) 5,(byte)1};
			bcm.setContentBuf(contentBuf);
//			
			BaseCodeMessage bcmrcv = UdpJavaEncodingProcess.GetMsgReturn(bcm, sim);
			if(bcmrcv != null){
				//return true;
				if(bcmrcv.getContentBuf() != null){
					contentBuftosend = bcmrcv.getContentBuf();
					for(int i = 0; i<bcmrcv.getContentBuf().length; i++){
						
						contentBuffer[i]=bcmrcv.getContentBuf()[i];
						
						System.out.println(TAG+ "content " + i + " value: " + bcmrcv.getContentBuf()[i]);
						
					}
				}
				System.out.println("9999999999999999999999999");
				//停止发送命
				sendflag =false;
				startTimer();
				return true;
//				bcmrcv.getContentBuf().toString();
//				new String(bcmrcv.getContentBuf());
//				mDeviceName.setText(bcmrcv.getContentBuf().toString());
			}
			else{
				//return false;
				
			}
			try {
				Thread.sleep(6000);
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
			int response = result ? R.string.csst_adddevice_research_success : R.string.csst_adddevice_research_fail;
			Toast.makeText(CsstSHAddDeviceCloudMainView.this, response, Toast.LENGTH_LONG).show();
			//debug 不消�?
			mDialog.dismiss();
			
		}
	}

}
