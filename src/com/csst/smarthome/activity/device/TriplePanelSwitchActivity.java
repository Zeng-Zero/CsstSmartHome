package com.csst.smarthome.activity.device;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHDRCBean;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.bean.CsstSHSwitchBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHDeviceTable;
import com.csst.smarthome.dao.CsstSHSwitchTable;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.encryption.BaseCodeMessage;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.net.UdpJavaEncodingProcess;

public class TriplePanelSwitchActivity extends Activity implements ICsstSHInitialize, ICsstSHConstant {
	private String TAG = "TriplePanelSwitchActivity";
	private boolean debug = true;
	boolean sendflag = false;
	
	boolean sendflagquery = true;
	
	boolean sendcmdquerymain = true;
	
	ProgressDialog myprogress;
	private static int STARTSTATUS_PROGRESS = 1;
	private static int SENDCMD_PROGRESS =2;
	
	
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
	
	private AllonBtnListener allonBtnListener = null;
	
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	/** 主控MAC地址 */
	private String mMacAdress = null;
	//从上个层面发送过来的器件名称
	private String switchname =null;
	
	byte ssuidLow = 0x00;
	
	byte ssuidHight = 0x00;
	byte sendcmd = 0x00;
	byte ssstatus = 0x00;
	
	private Button btnAllOn,btnAllOff,btnreflash;
	private Button cb1,cb2,cb3; 
	private EditText et1,et2,et3;
    private LinearLayout 		mbtnback;
    private TextView          titleView;

	
	//每个按钮的状态标识
	private boolean cb1flag = true,cb2flag = true,cb3flag = true;

	// timer
	private final Timer timer=new Timer();
	private TimerTask tast;
		
		
	    // 超时
	    Timer timerdelaygetstatu;
	    Timer timerback;
	    boolean timeOut_flag = false;//当定时器 到达指定时间时 不接受清码 对码等操作 的标志位
//	    boolean cleanTimer = false; //清除定时器 true 表示定时器没作用
	    
		
//			 seek send cmd timer is here;
		private Timer timerconfigwifi;
		private TimerTask tastconfigwifi;
		private Handler handlerconfigwifi;
		private final int HANDLE_MSG_FROM_CONTROL =1;
		private final int HANDLE_MSG_FORM_PROGRESS =2;
		private final int HANDLE_MSG_QUERY_FAIL =3;
		//第一次查询的结果
		private final int HANDLE_MSG_FORM_FIRST_QUERY =3;
		//只执行一次的查询转圈圈
		//记录搜索失败次数 如果失败次数达到4次就提醒设备不在线 退出界面
		
		private static int MSG_LEGHTE_SEND = 7;
		private static int MSG_UID_LOW_RECIVE = 2;
		private static int MSG_UID_HIGHT_RECIVE = 3;
		private static int MSG_TYPE_RECIVE = 4;
		private static int MSG_STATUE_RECIVE = 5;//表示开关状态量
		byte contentBuffer[] = new byte[MSG_LEGHTE_SEND];
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.triple_panel_switch_activity);
		initDataSource();
//		TitleLayoutUtils.setupTitleLayout(this, switchname);
		initWidget();
		initsatus();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
		//进入进行数据的获取
		new getstatus().cancel(true);
		new getstatus().execute();
		
		
    }

	public void addWidgetListener() {
		// TODO Auto-generated method stub
		btnAllOn.setOnClickListener(allonBtnListener);
		
	}
	


	public void initDataSource() {
		// TODO Auto-generated method stub
		// 初始数据源
		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
		mMacAdress = configPreference.getMacAdress();
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对�?
		mDb = csstSHDataBase.getWritDatabase();
		if(debug){
			Log.d(TAG,"the mMacAdress is "+mMacAdress);
		}
		Intent intent = getIntent();
		if (null != intent){
			mDevice = (CsstSHDeviceBean) intent.getSerializableExtra("device");
			String strTemp =mDevice.getDeviceSSID();
			
			curswitchbean = CsstSHSwitchTable.getInstance().queryByDeviceId(mDb, mDevice.getDeviceId());
			String[] msgBuffer = strTemp.split(",");
		    ssuidLow = (byte)Integer.parseInt(msgBuffer[0]);
		    ssuidHight = (byte)Integer.parseInt(msgBuffer[1]);
			ssstatus = (byte)Integer.parseInt(msgBuffer[2]);
			Log.d(TAG,"the value whichaction is "+" the locatin is "+mDevice.getDeviceName()+"the ssuidLow "+ssuidLow+" ssuidhight "+ssuidHight+" the status is "+ssstatus);
			switchname =(String) intent.getSerializableExtra("switchname");
		}
		if(switchname ==null){
			switchname = getResources().getString(R.string.csst_adddevice_switch_three);
		}
		
		
		
	}


	public void initWidget() {
		// TODO Auto-generated method stub
		btnAllOff = (Button)findViewById(R.id.allOffBtn);
		btnAllOn = (Button) findViewById(R.id.allOnBtn);
		cb1 = (Button) findViewById(R.id.drop_light_checkbox);
		cb2 = (Button) findViewById(R.id.track_light_checkbox);
		cb3 = (Button) findViewById(R.id.wall_light_checkbox);
		et1 = (EditText) findViewById(R.id.drop_light_name_input);
		et2 = (EditText) findViewById(R.id.track_light_name_input);
		et3 = (EditText) findViewById(R.id.wall_light_name_input);
	    titleView = (TextView)findViewById(R.id.title_textview);
        titleView.setText(switchname);
        
        mbtnback = (LinearLayout) findViewById(R.id.back_btn);
        
        btnreflash = (Button) findViewById(R.id.mBtnreflash);
		
		
	}
	public void initsatus(){
		if(curswitchbean!=null){
			et1.setText(curswitchbean.getmSwitchName1());
			et2.setText(curswitchbean.getmSwitchName2());
			et3.setText(curswitchbean.getmSwitchName3());
			int status = curswitchbean.getSwitchonoff();
			//查询获取的状态  因为发送命令和查询的状态是不同的
			   if(((byte)status&(byte)0x01)==0){
				   Log.d(TAG,"status[4]==0x10");
				   cb1flag = false;
				   cb1.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
			   }
			   if(((byte)status&(byte)0x01)==1){
				   Log.d(TAG,"status[4]==0x11");
				   cb1flag = true;
				   cb1.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
			   }
			   if(((byte)status&(byte)0x02)==0){
				   Log.d(TAG,"status[4]==0x20");
				   cb2flag = false;
				   cb2.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
				   
			   }
			   if(((byte)status&(byte)0x02)==2){
				   Log.d(TAG,"status[4]==0x21");
				   cb2flag = true;
				   cb2.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
			   }
			   if(((byte)status&(byte)0x04)==0){
				   Log.d(TAG,"satus[4]==0x40");
				   cb3flag = false;
				   cb3.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
			   }
			   if(((byte)status&(byte)0x04)==4){
				   Log.d(TAG,"satus[4]==0x41");
				   cb3flag = true;
				   cb3.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
			   }
			   if(((byte)status&(byte)0x08)==0){
			  
			   }
			   if(((byte)status&(byte)0x08)==8){
			  
			   }
			   Log.d(TAG,"satus false out ");
		   
			
		}
	}

	public void initWidgetState() {
		// TODO Auto-generated method stub
	
		configwifitimer();
		
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
//		try{
//			Thread.sleep(1000);
//		}catch (Exception ex){
//			
//		}
//		sendflagquery = true;
//		queryStatus_thread = new queryStatus();//ZQL
//		queryStatus_flag = true; //ZQL
//		queryStatus_thread.start();//ZQL
		
//		timerOut(3000);
		LoadProcess_fisrt(STARTSTATUS_PROGRESS);
		
		Log.d(TAG,"timerdelaygetstatu timerOut ");
		sendflagquery = true;
		
	}
    // 延时几秒才执行查询的任务
	 public void timerOut(int ms,final int type){
		 //现将之前的去掉
		   if (timerdelaygetstatu != null) {  
			   timerdelaygetstatu.cancel();  
	            timerdelaygetstatu = null;  
	        }  
		  if (timerdelaygetstatu == null) {  
			  timerdelaygetstatu =new Timer();
		  }
	    	TimerTask task;
	    	task = new TimerTask() {
				
				@Override
				public void run() {
//					// TODO Auto-generated method stub
////					if(!cleanTimer){//当操作成功之后    清除定时器
//						Message msg = handler.obtainMessage(TIMEOUT);
//						handler.sendMessage(msg);
//					if(type ==STARTSTATUS_PROGRESS){
//					Log.d(TAG,"timerdelaygetstatu timerOut ");
//					sendflagquery = true;
//					queryStatus_thread = new queryStatus();//ZQL
//					queryStatus_flag = true; //ZQL
//					queryStatus_thread.start();//ZQL
//					Message msg = handlerconfigwifi.obtainMessage(SENDCMD_PROGRESS);
//					handlerconfigwifi.sendMessage(msg);
//					}else if(type ==SENDCMD_PROGRESS){
						Message msg = handlerconfigwifi.obtainMessage(HANDLE_MSG_FORM_PROGRESS);
						handlerconfigwifi.sendMessage(msg);
//					}
				}
			};	
			timerdelaygetstatu.schedule(task, ms);
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
			         Message msg ;
			         msg = handlerconfigwifi.obtainMessage(HANDLE_MSG_FROM_CONTROL);
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
				   Log.d(TAG,"handleMessage  is here ");
				   Log.d(TAG,"contentBuffer[5]  is here "+contentBuffer[MSG_STATUE_RECIVE]);
				   switch(msg.what){
				   
				   case HANDLE_MSG_FROM_CONTROL:
//					if(ssuidLow ==3){
						 //
//						   if(contentBuffer[MSG_STATUE_RECIVE]==-16||contentBuffer[MSG_STATUE_RECIVE]==0x70){
//							   Log.d(TAG,"contentBuffer[4]==-16");
//							   cb1.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
//							   cb2.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
//							   cb3.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
//							   cb1flag = false;
//							   cb2flag = false;
//							   cb3flag = false;
//							   
//							   
//						   }else if(contentBuffer[MSG_STATUE_RECIVE]==-15||contentBuffer[MSG_STATUE_RECIVE]==0x77){
//							   Log.d(TAG,"contentBuffer[4]==-15");
//							   cb1.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
//							   cb2.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
//							   cb3.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
//							   cb1flag = true;
//							   cb2flag = true;
//							   cb3flag = true;
//							   
//						   }else {
//							   if(sendflag){//发送命令获取的状态  因为发送命令和查询的状态是不同的
//								   Log.d(TAG,"contentBuffer[5]==0x10  sendflag true ");
//								   if(contentBuffer[MSG_STATUE_RECIVE]==0x10){
//									   Log.d(TAG,"contentBuffer[4]==0x10");
//									   cb1flag = false;
//									   cb1.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
//								   }
//								   if(contentBuffer[MSG_STATUE_RECIVE]==0x11){
//									   Log.d(TAG,"contentBuffer[4]==0x11");
//									   cb1flag = true;
//									   cb1.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
//								   }
//								   if(contentBuffer[MSG_STATUE_RECIVE]==0x20){
//									   Log.d(TAG,"contentBuffer[4]==0x20");
//									   cb2flag = false;
//									   cb2.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
//									   
//								   }
//								   if(contentBuffer[MSG_STATUE_RECIVE]==0x21){
//									   Log.d(TAG,"contentBuffer[4]==0x21");
//									   cb2flag = true;
//									   cb2.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
//								   }
//								   if(contentBuffer[MSG_STATUE_RECIVE]==0x40){
//									   Log.d(TAG,"contentBuffer[4]==0x40");
//									   cb3flag = false;
//									   cb3.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
//								   }
//								   if(contentBuffer[MSG_STATUE_RECIVE]==0x41){
//									   Log.d(TAG,"contentBuffer[4]==0x41");
//									   cb3flag = true;
//									   cb3.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
//								   }
//								   if(contentBuffer[MSG_STATUE_RECIVE]==0x80){
//								  
//								   }
//								   if(contentBuffer[MSG_STATUE_RECIVE]==0x81){
//								  
//								   }
//							   }else{//查询获取的状态  因为发送命令和查询的状态是不同的
//								   Log.d(TAG,"contentBuffer[5]==0x10  sendflag false "+(byte)contentBuffer[MSG_STATUE_RECIVE]);
//								   if(((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x01)==0){
//									   Log.d(TAG,"contentBuffer[4]==0x10");
//									   cb1flag = false;
//									   cb1.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
//								   }
//								   if(((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x01)==1){
//									   Log.d(TAG,"contentBuffer[4]==0x11");
//									   cb1flag = true;
//									   cb1.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
//								   }
//								   Log.d(TAG,"contentBuffer[5]==0x10  sendflag false 2 "+(byte)contentBuffer[MSG_STATUE_RECIVE]);
//								   if(((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x02)==0){
//									   Log.d(TAG,"contentBuffer[4]==0x20");
//									   cb2flag = false;
//									   cb2.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
//									   
//								   }
//								   if(((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x02)==2){
//									   Log.d(TAG,"contentBuffer[4]==0x21");
//									   cb2flag = true;
//									   cb2.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
//								   }
//								   Log.d(TAG,"contentBuffer[5]==0x10  sendflag false 3 "+(byte)contentBuffer[MSG_STATUE_RECIVE]);
//								   if(((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x04)==0){
//									   Log.d(TAG,"contentBuffer[4]==0x40");
//									   cb3flag = false;
//									   cb3.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
//								   }
//								   Log.d(TAG,"contentBuffer[5]==0x10  sendflag false 4 "+(byte)contentBuffer[MSG_STATUE_RECIVE]);
//								   Log.d(TAG," ((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x04)) is  "+((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x04));
//								   if(((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x04)==4){
//									   Log.d(TAG,"contentBuffer[4]==0x41");
//									   cb3flag = true;
//									   cb3.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
//								   }
//								   if((contentBuffer[MSG_STATUE_RECIVE]&(byte)0x08)==0){
//								  
//								   }
//								   if((contentBuffer[MSG_STATUE_RECIVE]&(byte)0x08)==8){
//								  
//								   }
//								   Log.d(TAG,"contentBuffer[5]==0x10  sendflag false out ");
//							   }
//							  
//							   
//							   
//							   if(sendflag){
//								   Log.d(TAG,"set cancle Updatesenddebugcmdmianban and start Updatesenddebugcmdmianbanquery ");
//								   queryStatus_flag = true;
//								   	sendflagquery = true;
//									sendflag = false;
//									new Updatesenddebugcmdmianban((byte)0x00).cancel(true);
//									
//							   }
//							   Log.d(TAG,"contentBuffer[5]==0x10   out ");
//						   }
////					} else if(ssuidLow==2){
////						
////					}else if(ssuidLow==1){
////						
////					}
//				  
//				   stopTimer();
				   
					   if(contentBuffer[MSG_STATUE_RECIVE]==-16||contentBuffer[MSG_STATUE_RECIVE]==0x70){
						   Log.d(TAG,"contentBuffer[4]==-16");
						   cb1.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
						   cb2.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
						   cb3.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
						   cb1flag = false;
						   cb2flag = false;
						   cb3flag = false;
						   
						   
					   }else if(contentBuffer[MSG_STATUE_RECIVE]==-15||contentBuffer[MSG_STATUE_RECIVE]==0x77){
						   Log.d(TAG,"contentBuffer[4]==-15");
						   cb1.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
						   cb2.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
						   cb3.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
						   cb1flag = true;
						   cb2flag = true;
						   cb3flag = true;
						   
					   }else {
							   Log.d(TAG,"contentBuffer[5]==0x10  sendflag false "+(byte)contentBuffer[MSG_STATUE_RECIVE]);
							   if(((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x01)==0){
								   Log.d(TAG,"contentBuffer[4]==0x10");
								   cb1flag = false;
								   cb1.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
							   }
							   if(((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x01)==1){
								   Log.d(TAG,"contentBuffer[4]==0x11");
								   cb1flag = true;
								   cb1.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
							   }
							   Log.d(TAG,"contentBuffer[5]==0x10  sendflag false 2 "+(byte)contentBuffer[MSG_STATUE_RECIVE]);
							   if(((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x02)==0){
								   Log.d(TAG,"contentBuffer[4]==0x20");
								   cb2flag = false;
								   cb2.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
								   
							   }
							   if(((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x02)==2){
								   Log.d(TAG,"contentBuffer[4]==0x21");
								   cb2flag = true;
								   cb2.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
							   }
							   Log.d(TAG,"contentBuffer[5]==0x10  sendflag false 3 "+(byte)contentBuffer[MSG_STATUE_RECIVE]);
							   if(((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x04)==0){
								   Log.d(TAG,"contentBuffer[4]==0x40");
								   cb3flag = false;
								   cb3.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
							   }
							   Log.d(TAG,"contentBuffer[5]==0x10  sendflag false 4 "+(byte)contentBuffer[MSG_STATUE_RECIVE]);
							   Log.d(TAG," ((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x04)) is  "+((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x04));
							   if(((byte)contentBuffer[MSG_STATUE_RECIVE]&(byte)0x04)==4){
								   Log.d(TAG,"contentBuffer[4]==0x41");
								   cb3flag = true;
								   cb3.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
							   }
							   if((contentBuffer[MSG_STATUE_RECIVE]&(byte)0x08)==0){
							  
							   }
							   if((contentBuffer[MSG_STATUE_RECIVE]&(byte)0x08)==8){
							  
							   }
							   Log.d(TAG,"contentBuffer[5]==0x10  sendflag false out ");
						   }
						   
						   if(sendflag){//恢复查询线程
							   Log.d(TAG,"set cancle Updatesenddebugcmdmianban and start Updatesenddebugcmdmianbanquery ");
							   	sendflagquery = true;
								sendflag = false;
								new Updatesenddebugcmdmianban((byte)0x00).cancel(true);
								
						   Log.d(TAG,"contentBuffer[5]==0x10   out ");
					   }
			  
				   stopTimer();
				   break;
				   
				   case HANDLE_MSG_FORM_PROGRESS:
					   if(myprogress!=null)
						   myprogress.dismiss();
					   break;
				   case HANDLE_MSG_QUERY_FAIL:
					   if(myprogress!=null)
						   myprogress.dismiss();
					   //查询失败4次就提醒查询失败 回到主界面
					   Log.d(TAG,"query status faile here  HANDLE_MSG_QUERY_FAIL");
					   Toast.makeText(TriplePanelSwitchActivity.this, getResources().getString(R.string.csst_searchswitch_fail), Toast.LENGTH_SHORT).show();
					   try{
						   Thread.sleep(3000);
					   }catch(Exception ex){
						   Log.d(TAG,ex.toString());
					   }
					   finish();
					   break;
					   
				   }
				  }
			    };
	}
    

    
	private final class Updatesenddebugcmdmianban extends AsyncTask<Void, Void, Boolean>{
		byte cmd = 0x00;
		public Updatesenddebugcmdmianban(byte cmd){
			
			this.cmd = cmd;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			LoadProcess_fisrt(SENDCMD_PROGRESS);
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			int flag = 0;
			while(sendflag){
				Log.d(TAG,"zengqinglin  UpdateTempe Updatesenddebugcmdmianban \n");
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
			Log.d(TAG," the cmd  is "+cmd);
			//
			byte[] contentBuf = new byte[6];
			contentBuf[0]=(byte)0x07;
			contentBuf[1]=(byte)0x01;
			contentBuf[2]=(byte)ssuidLow;
			contentBuf[3]=(byte)ssuidHight;
			contentBuf[4]=(byte)0x02;
			contentBuf[5]=(byte)cmd;
			for(int n=0;n<6;n++){
				Log.d(TAG,TAG+ "Updatesenddebugcmdmianban send " + n + " value: " + contentBuf[n]);
			}
			
			bcm.setContentBuf(contentBuf);
			Log.d(TAG,"Updatesenddebugcmdmianban start to send MsgReturn ");
			BaseCodeMessage bcmrcv = UdpJavaEncodingProcess.GetMsgReturn(bcm, sim);
			Log.d(TAG,"Updatesenddebugcmdmianban ready send get MsgReturn ");
			if(bcmrcv != null){
				//return true;
				if(bcmrcv.getContentBuf() != null){
					for(int i = 0; i<bcmrcv.getContentBuf().length; i++){
						contentBuffer[i]=bcmrcv.getContentBuf()[i];
						Log.d(TAG,TAG+ "Updatesenddebugcmdmianban  recive " + i + " value: " + bcmrcv.getContentBuf()[i]);
					}
				}
				myprogress.dismiss();
				Log.d(TAG,"Updatesenddebugcmdmianban get messega finish ");
				//先停止原来来定时器 因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
				stopTimer();
				//停止发送命
				startTimer();
//				sendflag =false;
				return true;
			}
			else{
				//return false;
				
			}
			
			Log.d(TAG,"Updatesenddebugcmdmianban start to sleep for wait");
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
			Toast.makeText(TriplePanelSwitchActivity.this, response, Toast.LENGTH_SHORT).show();
			//debug 不消
//			mDialog.dismiss();
			//现将之前的去掉
			if (timerdelaygetstatu != null) {  
				   timerdelaygetstatu.cancel();  
		            timerdelaygetstatu = null;  
		      }  
			myprogress.dismiss();
			//先停止原来来定时器 因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
			stopTimer();
			startTimer();
		}
		
	}
		
	
	  
	private final class getstatus extends AsyncTask<Void, Void, Boolean>{
			public getstatus(){
				
			}
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				LoadProcess_fisrt(SENDCMD_PROGRESS);
			}
			@Override
			protected Boolean doInBackground(Void... params) {
				//只有不发送数据才查找数据
				//设备的MAC地址
				long toid = 109860815673L;
				ServerItemModel sim = new ServerItemModel();
				String msgBuffer ="1111";
				if(CsstSHUseDeviceActivity.msgBuffer!=null){
					msgBuffer=  CsstSHUseDeviceActivity.msgBuffer[0];
				}
				
				Log.d(TAG,"queryStatus  MAC \n");
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
				bcm.setMCMD((byte) 4);//22
				bcm.setSCMD((byte) 1);//23
				//
				byte[] contentBuf = new byte[5];
	        	   
						contentBuf[0]=(byte)0x04;//24
						contentBuf[1]=(byte)0x05;//25
						contentBuf[2]=(byte)ssuidLow;//26
						contentBuf[3]=(byte)ssuidHight;//27
						contentBuf[4]=(byte)0x02;//28
						for(int n=0;n<contentBuf.length;n++){
							Log.d(TAG,TAG+ "queryStatus  send  " + n + " value: " + contentBuf[n]);
						}
						
						bcm.setContentBuf(contentBuf);
	//					
						BaseCodeMessage bcmrcv = UdpJavaEncodingProcess.GetMsgReturn(bcm, sim);
						if(bcmrcv != null){
							//return true;
							if(bcmrcv.getContentBuf() != null){
								for(int i = 0; i<bcmrcv.getContentBuf().length; i++){
									contentBuffer[i]=bcmrcv.getContentBuf()[i];
									Log.d(TAG,TAG+ "queryStatus recive " + i + " value: " + bcmrcv.getContentBuf()[i]);
								}
							}
							//停止发送命
							Log.d(TAG,"getstatus get messega finish ready to startTimer ");
							//先停止原来来定时器 因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
//						stopTimer();
//						startTimer();
							stopTimer();
							//停止发送命
							startTimer();
							//清空查询失败命令
							 if(myprogress!=null)
								   myprogress.dismiss();
						}else{
								Log.d(TAG,"getstatus get messega fail want to exit this activity ");
								Message msg = handlerconfigwifi.obtainMessage(HANDLE_MSG_QUERY_FAIL);
								handlerconfigwifi.sendMessage(msg);
						}
						return true;
		
			}
	}

			
		
	public void initWidgetListener() {
		// TODO Auto-generated method stub
		allonBtnListener = new AllonBtnListener();
		
		
		btnreflash.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				new getstatus().cancel(true);
				new getstatus().execute();
			}
		});
		
		mbtnback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				backEvent();
			}
		});
		
		btnAllOff.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(myprogress!=null)
					myprogress.dismiss();
				sendflagquery = false;
//				new Updatesenddebugcmdmianbanquery().cancel(true);
				
				sendflag=true;
				Log.d(TAG,"customBtnListener is press here the value is ");
				sendcmd = (byte)0xf0;
//				LoadProcess_fisrt();
				new Updatesenddebugcmdmianban(sendcmd).cancel(true);
				new Updatesenddebugcmdmianban(sendcmd).execute();
			}
		});
		
		cb1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(myprogress!=null)
					myprogress.dismiss();
				sendflagquery = false;
				// TODO Auto-generated method stub
				if(cb1flag){
					sendflag=true;
					Log.d(TAG,"customBtnListener is press here the value is ");
					sendcmd = (byte)0x10;
					new Updatesenddebugcmdmianban(sendcmd).cancel(true);
					new Updatesenddebugcmdmianban(sendcmd).execute();
				}else{
					sendflag=true;
					Log.d(TAG,"customBtnListener is press here the value is ");
					sendcmd = (byte)0x11;
					new Updatesenddebugcmdmianban(sendcmd).cancel(true);
					new Updatesenddebugcmdmianban(sendcmd).execute();
				}
			}
		});
			
		cb2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(myprogress!=null)
					myprogress.dismiss();
				sendflagquery = false;
				// TODO Auto-generated method stub
				if(cb2flag){
					sendflag=true;
					Log.d(TAG,"customBtnListener is press here the value is ");
					sendcmd = (byte)0x20;
					new Updatesenddebugcmdmianban(sendcmd).cancel(true);
					new Updatesenddebugcmdmianban(sendcmd).execute();
				}else{
					sendflag=true;
					Log.d(TAG,"customBtnListener is press here the value is ");
					sendcmd = (byte)0x21;
					new Updatesenddebugcmdmianban(sendcmd).cancel(true);
					new Updatesenddebugcmdmianban(sendcmd).execute();
				}
			}
			});
		cb3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(myprogress!=null)
					myprogress.dismiss();
				sendflagquery = false;
				// TODO Auto-generated method stub
				if(cb3flag){
					sendflag=true;
					Log.d(TAG,"customBtnListener is press here the value is ");
					sendcmd = (byte)0x40;
					new Updatesenddebugcmdmianban(sendcmd).cancel(true);
					new Updatesenddebugcmdmianban(sendcmd).execute();
				}else{
					sendflag=true;
					Log.d(TAG,"customBtnListener is press here the value is ");
					sendcmd = (byte)0x41;
					new Updatesenddebugcmdmianban(sendcmd).cancel(true);
					new Updatesenddebugcmdmianban(sendcmd).execute();
				}
			}
		});
			
		
	}
	
	/**
	 * 扫描设备按钮监听�?
	 * @author liuyang
	 */
	private final class AllonBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			
			sendflagquery = false;
//			new Updatesenddebugcmdmianbanquery().cancel(true);
			
			sendflag=true;
			Log.d(TAG,"customBtnListener is press here the value is ");
			sendcmd = (byte)0xf1;
//			LoadProcess_fisrt();
			new Updatesenddebugcmdmianban(sendcmd).cancel(true);
			new Updatesenddebugcmdmianban(sendcmd).execute();
		}
	}
		
	

	
	public void  LoadProcess_fisrt(int type) {
		if(myprogress!=null)
			myprogress.dismiss();
		myprogress = new ProgressDialog(this);
		myprogress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		if(type== SENDCMD_PROGRESS){
			myprogress.setMessage(getResources().getString(R.string.csst_send_cmd));
			timerOut(20000, SENDCMD_PROGRESS);
		}else if(type== STARTSTATUS_PROGRESS){
//			timerOut(17000, STARTSTATUS_PROGRESS);//不需要定时取消直到搜索到才消失myprogress 
			myprogress.setMessage(getResources().getString(R.string.csst_triple_start_msg));
		}
		
		myprogress.setIndeterminate(false);
		myprogress.setCancelable(false);
		myprogress.show();
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	if(keyCode==KeyEvent.KEYCODE_BACK){
		Log.d(TAG,TAG+"backEvent() is here 12");
		sendflag = false;
		backEvent();
	}
		 return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 销毁时关闭数据
		backEvent();
		if (mDb != null)
			mDb.close();
	}
	/**
	 * 返回事件
	 */
	private final void backEvent(){
		sendflag = false;
		sendflagquery = false;
		sendcmdquerymain = false;
		
		new Updatesenddebugcmdmianban((byte)0x00).cancel(true);
//		new Updatesenddebugcmdmianbanquery().cancel(true);
		if(myprogress!=null)
		myprogress.dismiss();
		//退出保存现在的状态
		curswitchbean.setmSwitchName1(et1.getText().toString());
		curswitchbean.setmSwitchName2(et2.getText().toString());
		curswitchbean.setmSwitchName3(et3.getText().toString());
		int statusbuf = 0;
		
		statusbuf = cb1flag ? statusbuf|0x01 : statusbuf&0xfe;
		statusbuf = cb2flag ? statusbuf|0x02 : statusbuf&0xfd;
		statusbuf = cb3flag ? statusbuf|0x04 : statusbuf&0xfb;
		Log.d(TAG,"the status is "+statusbuf);
		curswitchbean.setSwitchonoff(statusbuf);
		CsstSHSwitchTable.getInstance().update(mDb,curswitchbean);
//		mDevice.setDeviceIconPersert(true);
		if((statusbuf&0x0f)!=0){
			Log.d(TAG,TAG+" backEvent() is here "+"the device poweroff is true ");
			mDevice.setDeviceIconPersert(true);
			CsstSHDeviceTable.getInstance().update(mDb, mDevice);
		}else{
			Log.d(TAG,TAG+" backEvent() is here "+"the device poweroff is false ");
			mDevice.setDeviceIconPersert(false);
			CsstSHDeviceTable.getInstance().update(mDb, mDevice);
		}
		Log.d(TAG,TAG+" backEvent() is here "+"the device poweroff is"+CsstSHDeviceTable.getInstance().query(mDb, mDevice.getDeviceId()).isDeviceIconPersert());
		
		finish();
	}
	
	
    
    
    
}
