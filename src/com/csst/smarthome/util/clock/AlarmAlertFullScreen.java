/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.csst.smarthome.util.clock;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHActionBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHModelTable;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.encryption.BaseCodeMessage;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.message.baseMessage;
import com.lishate.message.rfcode.SendRFCodeReqMessage;
import com.lishate.net.UdpJavaEncodingProcess;
import com.lishate.net.UdpProcess;

/**
 * Alarm Clock alarm alert: pops visible indicator and plays alarm
 * tone. This activity is the full screen version which shows over the lock
 * screen with the wallpaper as the background.
 */
public class AlarmAlertFullScreen  extends Activity implements
ICsstSHInitialize, ICsstSHConstant {

    // These defaults must match the values in res/xml/settings.xml
    private static final String DEFAULT_SNOOZE = "10";
    private static final String DEFAULT_VOLUME_BEHAVIOR = "2";
    protected static final String SCREEN_OFF = "screen_off";
    private String TAG = "AlarmAlertFullScreen";
    private boolean debug = true;
    
    
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
    //动作执行
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	/** 主控MAC地址 */
	public String mMacAdress = null;
    /**
	 * model动作执行
	 */
	private ListView listviewrun =null;
	
	/** 动作数据 */
	private List<CsstSHActionBean> actionBeans = null;
	
	
	/**
	 * 定时器
	 */
	private Timer timerconfigwifi;
	private TimerTask tastconfigwifi;
	private Handler handlerconfigwifi;
	private int i = 0 ;
	private int iActionsize = 0;

    protected Alarm mAlarm;
    private int mVolumeBehavior;

    
    

    // Receives the ALARM_KILLED action from the AlarmKlaxon,
    // and also ALARM_SNOOZE_ACTION / ALARM_DISMISS_ACTION from other applications
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	System.out.println(TAG+" the BroadcastReceiver is here \n ");
            String action = intent.getAction();
            if (action.equals(Alarms.ALARM_SNOOZE_ACTION)) {
            	
            } else if (action.equals(Alarms.ALARM_DISMISS_ACTION)) {
                dismiss(false);
            } else {
                Alarm alarm = intent.getParcelableExtra(Alarms.ALARM_INTENT_EXTRA);
                if (alarm != null && mAlarm.id == alarm.id) {
                    dismiss(true);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        System.out.println(TAG+" the onCreate is here \n ");
        mAlarm = getIntent().getParcelableExtra(Alarms.ALARM_INTENT_EXTRA);
        //sign changed by reason
        if(mAlarm!=null){
        	
        	 mAlarm = Alarms.getAlarm(getContentResolver(), mAlarm.id);
        	 //
//        	         requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        	 //
//        	         final Window win = getWindow();
//        	         win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//        	                 | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//        	         // Turn on the screen unless we are being launched from the AlarmAlert
//        	         // subclass.
//        	         if (!getIntent().getBooleanExtra(SCREEN_OFF, false)) {
//        	             win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//        	                     | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//        	                     | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
//        	         }

        	         //去掉对话框
//        	         updateLayout();
        	         
        	        
        	         
        	         
        	         
        	         
        	         
        	         csstSHDataBase = new CsstSHDataBase(this);
        	 		// 数据库对象
        	 		mDb = csstSHDataBase.getWritDatabase();
        	 		
        	 		// 初始数据�?
        	 		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
        	 		// 当前楼层id
        	 		//读取当前主控MAC地址
        	 		mMacAdress = configPreference.getMacAdress();
        	         actionBeans = CsstSHModelTable.getInstance().getActionByModelId(mDb, mAlarm.modelid);
        	 		iActionsize = actionBeans.size();
        	 		//开启定时器
        	 		configwifitimer();
        	 		
        	 		Message msg= handlerconfigwifi.obtainMessage(1);
        	         handlerconfigwifi.sendMessage(msg);
        	
        }
       
		

//        // Register to get the alarm killed/snooze/dismiss intent.
//        IntentFilter filter = new IntentFilter(Alarms.ALARM_KILLED);
//        filter.addAction(Alarms.ALARM_SNOOZE_ACTION);
//        filter.addAction(Alarms.ALARM_DISMISS_ACTION);
//        registerReceiver(mReceiver, filter);
    }

    private void setTitle() {
    	System.out.println(TAG+" the setTitle is here \n ");
//        String label = mAlarm.getLabelOrDefault(this);
        TextView title = (TextView) findViewById(R.id.alertTitle);
//        title.setText(label);
    }

    private void updateLayout() {
    	System.out.println(TAG+" the updateLayout is here \n ");
        LayoutInflater inflater = LayoutInflater.from(this);

        setContentView(inflater.inflate(R.layout.alarm_alert, null));

        /* snooze behavior: pop a snooze confirmation view, kick alarm
           manager. */
//        Button snooze = (Button) findViewById(R.id.snooze);
//        snooze.requestFocus();
//        snooze.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                snooze();
//            }
//        });

        /* dismiss button: close notification */
        findViewById(R.id.dismiss).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        dismiss(false);
                    }
                });

        /* Set the title from the passed in alarm */
        setTitle();
    }

   

    private NotificationManager getNotificationManager() {
    	System.out.println(TAG+" the NotificationManager is here \n ");
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    // Dismiss the alarm.
    private void dismiss(boolean killed) {
        // The service told us that the alarm has been killed, do not modify
        // the notification or stop the service.
        if (!killed) {
            // Cancel the notification and stop playing the alarm
            NotificationManager nm = getNotificationManager();
            nm.cancel(mAlarm.id);
            stopService(new Intent(Alarms.ALARM_ALERT_ACTION));
        }
        finish();
    }

    /**
     * this is called when a second alarm is triggered while a
     * previous alert window is still active.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println(TAG+" the onNewIntent is here \n ");
        Log.v("wangxianming", "AlarmAlert.OnNewIntent()");

        mAlarm = intent.getParcelableExtra(Alarms.ALARM_INTENT_EXTRA);

        setTitle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println(TAG+" the onResume is here \n ");
        // If the alarm was deleted at some point, disable snooze.
//        if (Alarms.getAlarm(getContentResolver(), mAlarm.id) == null) {
//        	
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("wangxianming", "AlarmAlert.onDestroy()");
        // No longer care about the alarm being killed.
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Do this on key down to handle a few of the system keys.
        boolean up = event.getAction() == KeyEvent.ACTION_UP;
        switch (event.getKeyCode()) {
            // Volume keys and camera keys dismiss the alarm
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_CAMERA:
            case KeyEvent.KEYCODE_FOCUS:
                if (up) {
                    switch (mVolumeBehavior) {
                        case 1:
                            break;

                        case 2:
                            dismiss(false);
                            break;

                        default:
                            break;
                    }
                }
                return true;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    public void configwifitimer(){
		handlerconfigwifi=new Handler(){
			  @Override
				  public void handleMessage(Message msg) {
				   super.handleMessage(msg);
				   System.out.println("handleMessage  is here ");
					   if(i<iActionsize){
						   new modelrunTast(actionBeans.get(i).getmKeyCode()).execute() ;  
					   }else{
						   NotificationManager nm = null;
					        String service = Context.NOTIFICATION_SERVICE;         
					        nm = (NotificationManager) getSystemService(service);          //获得系统级服务，用于管理消息
					        Notification n = new Notification();                                        //定义一个消息类
					        n.icon = R.drawable.ic_launcher;                                                //设置图标
					        n.tickerText = "Notification Test!!";                                        // 设置消息
					        n.when = System.currentTimeMillis();                             //设置时间
					        // Notification n1 =new Notification(icon,tickerText,when);    //也可以用这个构造创建
					        Intent intent = new Intent(AlarmAlertFullScreen.this, AlarmAlertFullScreen.class);       
					        PendingIntent pi = PendingIntent.getActivity(AlarmAlertFullScreen.this, 0,intent, 0);       //消息触发后调用
					        n.setLatestEventInfo(AlarmAlertFullScreen.this, mAlarm.getLabelOrDefault(AlarmAlertFullScreen.this), "",pi); //设置事件信息就是拉下标题后显示的内容
					        nm.notify(1, n);      //发送通知
					   }
				  }
			    };
	}

	 public final class modelrunTast extends AsyncTask<Void, Void, Boolean>{
			private String keycode = null;
			private boolean result = false;
			
			public modelrunTast(String key) {
				keycode = key;
			}
			@Override
			protected Boolean doInBackground(Void... params) {
				System.out.println("modelrunTast  IN  ");
				String[] msgBuffer = keycode.split(",");
				if(msgBuffer[0].endsWith(getResources().getString(R.string.csst_adddevice_charge))){
					ServerItemModel sim = new ServerItemModel();
					sim.setIpaddress("218.244.129.177");
					sim.setPort(GobalDef.SERVER_PORT);
					BaseCodeMessage bcm = new BaseCodeMessage();
					bcm.Direct = MessageDef.BASE_MSG_FT_REQ;
					bcm.setFromId(GobalDef.MOBILEID);
					bcm.Seq = MessageSeqFactory.GetNextSeq();
					bcm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
					bcm.ToType = MessageDef.BASE_MSG_FT_HUB;
					
					
					bcm.setToId(Long.valueOf(mMacAdress));
					
		  			bcm.setMCMD((byte) 4);
		  			bcm.setSCMD((byte) 1);
		  			System.out.println(" the cmd  is "+Integer.valueOf(msgBuffer[1]));
		  			//
		  			byte[] contentBuf = new byte[6];
		  			contentBuf[0]=(byte)0x07;
		  			contentBuf[1]=(byte)0x01;
		  			contentBuf[2]=(byte)Byte.valueOf(msgBuffer[2]);
		  			contentBuf[3]=(byte)Byte.valueOf(msgBuffer[3]);
		  			contentBuf[4]=(byte)0x03;
		  			contentBuf[5]=(byte)Byte.valueOf(msgBuffer[1]);
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
		  						System.out.println(TAG+ "content reciver " + i + " value: " + bcmrcv.getContentBuf()[i]);
		  					}
		  				}
		  				return true;
		  			}
		  			else{
		  				result = false;
		  				
		  			}
				}else if(msgBuffer[0].endsWith(getResources().getString(R.string.csst_adddevice_switch))){

					ServerItemModel sim = new ServerItemModel();
					sim.setIpaddress("218.244.129.177");
					sim.setPort(GobalDef.SERVER_PORT);
					BaseCodeMessage bcm = new BaseCodeMessage();
					bcm.Direct = MessageDef.BASE_MSG_FT_REQ;
					bcm.setFromId(GobalDef.MOBILEID);
					bcm.Seq = MessageSeqFactory.GetNextSeq();
					bcm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
					bcm.ToType = MessageDef.BASE_MSG_FT_HUB;
					
					
					bcm.setToId(Long.valueOf(mMacAdress));
					
		  			bcm.setMCMD((byte) 4);
		  			bcm.setSCMD((byte) 1);
		  			System.out.println(" the cmd  is "+Integer.valueOf(msgBuffer[1]));
		  			//
		  			byte[] contentBuf = new byte[6];
		  			contentBuf[0]=(byte)0x07;
		  			contentBuf[1]=(byte)0x01;
		  			contentBuf[2]=(byte)Byte.valueOf(msgBuffer[2]);
		  			contentBuf[3]=(byte)Byte.valueOf(msgBuffer[3]);
		  			contentBuf[4]=(byte)0x02;
		  			contentBuf[5]=(byte)Byte.valueOf(msgBuffer[1]);
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
		  						System.out.println(TAG+ "content reciver " + i + " value: " + bcmrcv.getContentBuf()[i]);
		  					}
		  				}
		  				return true;
		  			}
		  			else{
		  				result = false;
		  				
		  			}
					
					
					
					
				}else{
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
					slrm.setCodeBuf(buffer);
					if(debug){
						System.out.println(TAG+"start to send the  the msg ");
					}
					baseMessage msg = UdpProcess.GetMsgReturn(slrm, sim);
					if(debug){
						System.out.println(TAG+"end the send msg  is return the msg ");
					}
					if(msg == null){
						if(debug){
							System.out.println(TAG+"end the send msg  return is false ");
						}
						result = false;
					} else {
						//SendRFCodeRspMessage srfrm = (SendRFCodeRspMessage)msg;
						if(debug){
							System.out.println(TAG+"end the send msg  is return the msg  return is ture ");
						}
						result = true;
					}
				}
				
				System.out.println("modelrunTast  OUT  ");
				return result;
				
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				int response = result ? R.string.csst_send_cmd_success : R.string.csst_send_cmd_fail;
				
				if(debug){
					System.out.println(TAG+"the response  is "+response);
				}
				i++;
				
				if(i<iActionsize){//最后一个就不执行相应的延时啦
					try{
						   Thread.sleep(actionBeans.get(i-1).getmDelayTime()*1000);
					   }catch(Exception ex){
						   System.out.println(ex.toString());
					   }
				}
					
				 Message msg= handlerconfigwifi.obtainMessage(1);
		         handlerconfigwifi.sendMessage(msg);
				
			}
		}
	 
    
    
    @Override
    public void onBackPressed() {
        // Don't allow back to dismiss. This method is overriden by AlarmAlert
        // so that the dialog is dismissed.
        return;
    }

	@Override
	public void initDataSource() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initWidgetState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initWidgetListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addWidgetListener() {
		// TODO Auto-generated method stub
		
	}
}
