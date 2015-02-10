package com.csst.smarthome.activity.adapter;

import java.net.DatagramPacket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.device.CsstSHUseDeviceActivity;
import com.csst.smarthome.bean.CsstSHSafeSensorBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHSafeSensorTable;
import com.csst.smarthome.safe.UdpProcess_ZQL;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.encryption.BaseCodeMessage;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.message.rfcode.SendRFCodeReqMessage;
import com.lishate.net.UdpJavaEncodingProcess;


/**
 * 设备列表设配器
 * @author liuyang
 */
public class CsstSHSafeSensorAdapter extends BaseAdapter implements ICsstSHConstant {
	private Context context = null;
	private List<CsstSHSafeSensorBean> safeSensorBeans = null;
	private String TAG = "CsstSHSafeSensorAdapter";
	
	//当前按下的Sensor 
	CsstSHSafeSensorBean cursafeSensorBean = null;

	//存储从从服务器中拿到的代码
	private String phoneNumberServer = "00";
	//表示有数据返回，就是发送数据成功的标志位
	private boolean sendSuc = false;
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
	private Handler handlerconfigwifi;
	
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	/** 主控MAC地址 */
	private String mMacAdress = null;
	
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	
	private final int HANDLE_MSG_FROM_CONTROL =1;
	private final int HANDLE_MSG_FORM_PROGRESS =2;
	
	public CsstSHSafeSensorAdapter(Context context, List<CsstSHSafeSensorBean> safeSensorBeans) {
		this.context = context;
		this.safeSensorBeans = safeSensorBeans;
		System.out.println("the size of safeSensorBeans is "+this.safeSensorBeans.size());
		for(int i=0;i<this.safeSensorBeans.size();i++){
			System.out.println("the name of action name is"+safeSensorBeans.get(i).getmSensorName());
		}
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this.context);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		configPreference = new CsstSHConfigPreference(this.context, CsstSHPreference);
		mMacAdress = configPreference.getMacAdress();
		configwifitimer();
		
	}
	public CsstSHSafeSensorAdapter(){
		
	}
	public final void setDevices(List<CsstSHSafeSensorBean> safeSensorBeans){
		this.safeSensorBeans = safeSensorBeans;
		notifyDataSetInvalidated();
	}

	@Override
	public int getCount() {
		return null == safeSensorBeans ? 0 : safeSensorBeans.size();
	}
	
	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		System.out.println("the CsstSHActionAdapter getview ");
		final CsstSHSafeSensorBean safeSensorBean = safeSensorBeans.get(position);
		convertView = LayoutInflater.from(context).inflate(R.layout.csst_safe_sensor_listview, null);
		TextView tvSafeSensorname =(TextView) convertView.findViewById(R.id.tvsafesensorname);
		final ImageView imgbattery =(ImageView) convertView.findViewById(R.id.imgsafebattery);
		if(safeSensorBean.getMbattery()==1){
			imgbattery.setImageDrawable(context.getResources().getDrawable(R.drawable.safesensor_been_on));
		}else{
			imgbattery.setImageDrawable(context.getResources().getDrawable(R.drawable.safesensor_been_off));
		}
		//鸣笛的标志
		imgbattery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cursafeSensorBean = safeSensorBean;
				// TODO Auto-generated method stub
				if(safeSensorBean.getMbattery()==1){
					imgbattery.setImageDrawable(context.getResources().getDrawable(R.drawable.safesensor_been_off));
					safeSensorBean.setMbattery(0);
					CsstSHSafeSensorTable.getInstance().update(mDb, safeSensorBean);
				}else{
					safeSensorBean.setMbattery(1);
					CsstSHSafeSensorTable.getInstance().update(mDb, safeSensorBean);
					imgbattery.setImageDrawable(context.getResources().getDrawable(R.drawable.safesensor_been_on));
				}
				
				//lenght 是多少个传感器 ：报警+uuid1 uuid2 uuid3+select+been
				byte[] sendsensortomain = new byte[5];
			
				sendsensortomain[0] =(byte)safeSensorBean.getMssuidLow();
				sendsensortomain[1] =(byte)safeSensorBean.getMssuidMid();
				sendsensortomain[2] =(byte)safeSensorBean.getMssuidHight();
				sendsensortomain[3] =(byte)safeSensorBean.getMbattery();
				sendsensortomain[4] =(byte)safeSensorBean.isMselect();
				new sendSensortoMain((byte)0x02,sendsensortomain).cancel(true);//默认UTF8
				new sendSensortoMain((byte)0x02,sendsensortomain).execute();//默认UTF8
			
			}
		});
		final Button btnselect =(Button) convertView.findViewById(R.id.btnsafeselect);
		btnselect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(safeSensorBean.isMselect()==1){
//					configPreference.setAlarmFlag(false);
					btnselect.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_off_safe));
					safeSensorBean.setMselect(0);
					CsstSHSafeSensorTable.getInstance().update(mDb, safeSensorBean);
					StringBuffer sb = new StringBuffer();
					sb.append(String.format("%02x", (byte)safeSensorBean.getMssuidLow()));
					sb.append(String.format("%02x", (byte)safeSensorBean.getMssuidMid()));
					sb.append(String.format("%02x", (byte)safeSensorBean.getMssuidHight()));
					sb.append(",");
					sb.append(safeSensorBean.getmSensorName());
					sb.append(",");
					sb.append("0;");
					System.out.println(" send the cmd sendphone is "+sb.toString());
					cursafeSensorBean = safeSensorBean;
					System.out.println("the name is false  "+safeSensorBean.getmSensorName());
					LoadProcess_fisrt(context.getResources().getString(R.string.csst_safe_close)+safeSensorBean.getmSensorName()+context.getResources().getString(R.string.csst_safe_sensor));
					new safeSendCmd((byte)0x08,(byte)0x06,sb.toString().getBytes()).cancel(true);//默认UTF8
					new safeSendCmd((byte)0x08,(byte)0x06,sb.toString().getBytes()).execute();//默认UTF8
					
				}else{
//					configPreference.setAlarmFlag(true);
					StringBuffer sb = new StringBuffer();
					sb.append(String.format("%02x", (byte)safeSensorBean.getMssuidLow()));
					sb.append(String.format("%02x", (byte)safeSensorBean.getMssuidMid()));
					sb.append(String.format("%02x", (byte)safeSensorBean.getMssuidHight()));
					sb.append(",");
					sb.append(safeSensorBean.getmSensorName());
					sb.append(",");
					sb.append("1");
					sb.append(";");
					System.out.println(" send the cmd sendphone is "+sb.toString());
					cursafeSensorBean = safeSensorBean;
					new safeSendCmd((byte)0x08,(byte)0x06,sb.toString().getBytes()).cancel(true);//默认UTF8
					new safeSendCmd((byte)0x08,(byte)0x06,sb.toString().getBytes()).execute();//默认UTF8
					safeSensorBean.setMselect(1);
					System.out.println("the name is true "+safeSensorBean.getmSensorName()+"the mselect is "+safeSensorBean.isMselect());
					CsstSHSafeSensorTable.getInstance().update(mDb, safeSensorBean);
					LoadProcess_fisrt(context.getResources().getString(R.string.csst_safe_open)+safeSensorBean.getmSensorName()+context.getResources().getString(R.string.csst_safe_sensor));
					for(int i=0;i<CsstSHSafeSensorTable.getInstance().query(mDb).size(); i++){
						
						System.out.println("the name is true "+CsstSHSafeSensorTable.getInstance().query(mDb).get(i).getmSensorName()+"the mselect is "+CsstSHSafeSensorTable.getInstance().query(mDb).get(i).isMselect());
					}
					btnselect.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_on_safe));
				}
			}
		});
		tvSafeSensorname.setText(safeSensorBean.getmSensorName());
		
		
		if(safeSensorBean.isMselect()==1){
			btnselect.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_on_safe));
		}else{
			btnselect.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.switch_off_safe));
		}
		convertView.setTag(safeSensorBean);
		return convertView;
	}
	
	

	private final class safeSendCmd extends AsyncTask<Void, Void, Boolean>{
		byte[] param;
		int type = 0;
		byte cmd ;
		public safeSendCmd(int type,byte cmd,byte[] param){
			this.type = type;
			this.cmd = cmd;
			this.param = param;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			int flag = 0;
			System.out.println("zengqinglin  UpdateTempe Updatesenddebugcmdmianban \n");
				//设备的MAC地址
			long toid = 109860815673L;
			ServerItemModel sim = new ServerItemModel();
			String msgBuffer ="1111";
			if(CsstSHUseDeviceActivity.msgBuffer!=null){
				msgBuffer=  CsstSHUseDeviceActivity.msgBuffer[0];
			}
			
			//局域网控制
//			sim.setIpaddress("255.255.255.255");
//			sim.setPort(80);
			
			
//			//调试刘总的
//			msgBuffer ="109861926250";
			//调试刘总的
//			msgBuffer ="109861926226";
			msgBuffer = mMacAdress;
//			sim.setIpaddress("218.244.129.177");
			sim.setIpaddress(GobalDef.SERVER_URL);
			sim.setPort(GobalDef.SERVER_PORT);
			
//			sim.setIpaddress("192.168.1.106");
//			sim.setPort(GobalDef.SERVER_PORT);
			
			
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
			System.out.println(" the cmd  is "+cmd+"the mac is "+Long.valueOf(msgBuffer));
		
			//
			int length = 0;
			if(param!=null){
				length =param.length+2; 
			}else{
				length = 2;
			}
			byte[] contentBuf = new byte[length];
			
			contentBuf[0]=(byte)0x08;
				
				contentBuf[1]=(byte)cmd;
				if(length>2){
					for(int m=0;m<length-2;m++){
						contentBuf[m+2]= param[m];
					}
				}
				for(int n=0;n<length;n++){
					System.out.println(TAG+ " safeSendCmd phone contentbuf setting " + n + " value: " + contentBuf[n]);
				}
				
				//用发码的形式发送数据
//				sim.setIpaddress("218.244.129.177");
				sim.setIpaddress(GobalDef.SERVER_URL);
				sim.setPort(GobalDef.SERVER_PORT);
				SendRFCodeReqMessage slrm = new SendRFCodeReqMessage();
				slrm.Direct = MessageDef.BASE_MSG_FT_REQ;
				slrm.setFromId(GobalDef.MOBILEID);
				slrm.MsgType = MessageDef.MESSAGE_TYPE_RFCODE_SEND_REQ;
				slrm.Seq = MessageSeqFactory.GetNextSeq();
				slrm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
				slrm.ToType = MessageDef.BASE_MSG_FT_HUB;
				slrm.setToId(Long.valueOf(msgBuffer));
				System.out.println(TAG+"");
				System.out.println(TAG+"the mac adress is "+Long.valueOf(msgBuffer));
				slrm.setCodeBuf(contentBuf);
				DatagramPacket msg = null;
				msg = UdpProcess_ZQL.GetMsgReturn(slrm, sim);
				if(msg == null){
					System.out.println("safeSendCmd suc fail fail fail  fail  fail");
					sendSuc = false;
					return false;
				} else {
					sendSuc = true;
					//SendRFCodeRspMessage srfrm = (SendRFCodeRspMessage)msg;
					System.out.println("safeSendCmd suc ok ok ok  ok  ok the msg is ");
					byte[] getbuf = new byte[msg.getLength()] ;
					for(int i=0;i<msg.getLength();i++){
						System.out.println(" UdpProcess_ZQL_SAFE get the message the [  "+i+"]"+msg.getData()[i]);
						getbuf[i]= msg.getData()[i];
					}
					phoneNumberServer = new String(getbuf);
					System.out.println("safeSendCmd suc ok ok ok  ok  ok the msg is "+phoneNumberServer);
					if(myprogress!=null){
						myprogress.dismiss();
					}
					System.out.println("safeSendCmd get messega finish ");
					//先停止原来来定时器 因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
					stopTimer();
					//停止发送命
					startTimer();
				}
				
			
			System.out.println("safeSendCmd start to sleep for wait");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			int response = result ? R.string.csst_suc : R.string.csst_fail;
			Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
			//debug 不消
//			mDialog.dismiss();
			if(myprogress!=null){
				myprogress.dismiss();
			}
			//先停止原来来定时器 因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
			stopTimer();
			startTimer();
			
			//lenght 是多少个传感器 ：报警+uuid1 uuid2 uuid3+select+been
			byte[] sendsensortomain = new byte[5];
		
			sendsensortomain[0] =(byte)cursafeSensorBean.getMssuidLow();
			sendsensortomain[1] =(byte)cursafeSensorBean.getMssuidMid();
			sendsensortomain[2] =(byte)cursafeSensorBean.getMssuidHight();
			sendsensortomain[3] =(byte)cursafeSensorBean.getMbattery();
			sendsensortomain[4] =(byte)cursafeSensorBean.isMselect();
			new sendSensortoMain((byte)0x02,sendsensortomain).cancel(true);//默认UTF8
			new sendSensortoMain((byte)0x02,sendsensortomain).execute();//默认UTF8
			
		
		}
	}
	
	
	
	//sendSensortoMain to 
		private final class sendSensortoMain extends AsyncTask<Void, Void, Boolean>{
			byte cmd;
	  		byte[] param;
	  		public sendSensortoMain(byte cmd, byte[] param){
	  			this.cmd = cmd;
				this.param = param;
			}
			
	  		@Override
	  		protected void onPreExecute() {
	  			super.onPreExecute();
	  		}
	  		@Override
	  		protected Boolean doInBackground(Void... params) {
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
	  			
	  			int length = 0;
				if(param!=null){
					length =param.length+2; 
				}else{
					length = 2;
				}
				byte[] contentBuf = new byte[length];
				contentBuf[0]=(byte)0x10;
				contentBuf[1]=(byte)cmd;
				if(length>2){
					for(int m=0;m<length-2;m++){
						contentBuf[m+2]= param[m];
					}
				}
					
	  			Log.d(TAG," the cmd  is "+cmd);
	  			//
	  			for(int n=0;n<length;n++){
	  				Log.d(TAG,"contentbuf sendSensortoMain send" + n + " value: " + contentBuf[n]);
	  			}
	  			
	  			bcm.setContentBuf(contentBuf);
	  			Log.d(TAG,"sendSensortoMain start to send MsgReturn ");
	  			BaseCodeMessage bcmrcv = UdpJavaEncodingProcess.GetMsgReturn(bcm, sim);
	  			Log.d(TAG,"sendSensortoMain ready send get MsgReturn ");
	  			if(bcmrcv != null){
	  				//return true;
	  				if(bcmrcv.getContentBuf() != null){
	  					for(int i = 0; i<bcmrcv.getContentBuf().length; i++){
	  						Log.d(TAG,TAG+ "content " + i + " value: " + bcmrcv.getContentBuf()[i]);
	  					}
	  				}
	  				myprogress.dismiss();
	  				Log.d(TAG,"sendSensortoMain get messega finish ");
	  				//先停止原来来定时�?因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
//	  				Message msg = handlerconfigwifi.obtainMessage(SENDCMD);
//	  	  			handlerconfigwifi.sendMessage(msg);
	  				return true;
	  			}
	  			else{
	  				
	  			}
	  			
	  			Log.d(TAG,"Updatesenddebugcmdmianban start to sleep for wait");
	  			return true;
	  		}

	  		
	  	}
	    
	
	  
    //seek 定时发
    private void startTimer(){  
        if (timerconfigwifi == null) {  
            timerconfigwifi = new Timer();  
        }  
  
        if (tastconfigwifi == null) {  
            tastconfigwifi = new TimerTask() {  
                @Override  
                public void run() {  
			          Message msg= handlerconfigwifi.obtainMessage(HANDLE_MSG_FROM_CONTROL);
			         handlerconfigwifi.sendMessage(msg);
                      
                     
                }  
            };  
        }  
  
        if(timerconfigwifi != null && tastconfigwifi != null )  
            timerconfigwifi.schedule(tastconfigwifi,  100);  
  
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
				   System.out.println("handleMessage  is here ");
				  
//				   System.out.println("contentBuffer[5]  is here "+contentBuffer[MSG_STATUE_RECIVE]);
				   switch(msg.what){
				   case HANDLE_MSG_FROM_CONTROL:
					   break;
				   case HANDLE_MSG_FORM_PROGRESS:
					   if(myprogress!=null)
						   myprogress.dismiss();
					   break;
				   }
				  
			  }
			    };
	}
    
    
    
 // 延时几秒才执行查询的任务
 		 public void timerOut(int ms){
 			 timerdelaygetstatu =new Timer();
 		    	TimerTask task;
 		    	task = new TimerTask() {
 					
 					@Override
 					public void run() {
 							Message msg = handlerconfigwifi.obtainMessage(HANDLE_MSG_FORM_PROGRESS);
 							handlerconfigwifi.sendMessage(msg);
 					}
 				};	
 				timerdelaygetstatu.schedule(task, ms);
 		    }
 		    
	public void  LoadProcess_fisrt(String str) {
		 if(myprogress!=null)
			   myprogress.dismiss();
		myprogress = new ProgressDialog(this.context);
		myprogress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		myprogress.setMessage(str);
		timerOut(17000);
		myprogress.setIndeterminate(false);
		myprogress.setCancelable(false);
		myprogress.show();
	}

		
	
	
}
