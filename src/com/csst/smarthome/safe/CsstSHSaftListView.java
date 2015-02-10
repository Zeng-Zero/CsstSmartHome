package com.csst.smarthome.safe;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.adapter.CsstSHActionAdapter;
import com.csst.smarthome.activity.adapter.CsstSHSafeNumberAdapter;
import com.csst.smarthome.activity.adapter.CsstSHSafeSensorAdapter;
import com.csst.smarthome.activity.device.CsstSHUseDeviceActivity;
import com.csst.smarthome.bean.CsstSHSafeSensorBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHSafeSensorTable;
import com.csst.smarthome.util.CsstContextUtil;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.encryption.BaseCodeMessage;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.message.rfcode.SendRFCodeReqMessage;
import com.lishate.net.UdpJavaEncodingProcess;

/**
 * 中控配置
 * @author liuyang
 */
public class CsstSHSaftListView extends Activity implements ICsstSHInitialize, ICsstSHConstant{

	/** 返回按钮 */
	private Button mBtnCancel = null;
	/** 完成按钮 */
	private Button mBtnDone = null;
	/** 标题 */
	private TextView mBtnaddaction ,mTVTitle = null;
	/** 动作列表 */
	private ListView mlistSensor = null;
	/** 动作数据 */
	
	/**
	 * 插入标志位，等程序回来重新加载之前删除的数据
	 */
	private boolean insertActionFlag = false;
	/** 动作配器 */
	private CsstSHActionAdapter actionAdapter = null;
	/** 情景模式名称标签 */
	private Button mbtnmanusetting,mbtnautosetting,mbtnaddnumber,mbtndeletenumber,mbtnGetnumer,mbtnsafeclock;
	private EditText metaddnumber,metdeletenumber;
	private ListView listviewNumber = null;
	
	private List<CsstSHSafeSensorBean> ListsafeSensorBeans =null;
	private CsstSHSafeSensorAdapter safeSensorAdapter = null;
	//判断是否已经存在传感器
	private boolean exit_flag = false;
	
	private String TAG = "CsstSHSaftListView";
	private boolean debug = true;
	
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	/** 主控MAC地址 */
	private String mMacAdress = null;
	
	//手动布防
	private boolean alarmFlag = false;
	//自动布防标志
	private boolean autoAlarmFlag = false;
	//表明该指令在什么飞状态下发送
	private int typeWhich = 0;
	
	
	private DoneBtnListener mBtnDoneListener = null;
	private AddActionBtnListener mBtnAddActionListener = null;
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	
	//长按sensor 
	private SensorModifyListener mSensorListner=null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	
	//获取安防返回的数据
	private String getMsgSafe = null;
	//记录所发送的命令
	byte sendcmd = 0x00;
	
	//表示有数据返回，就是发送数据成功的标志位
	private boolean sendSuc = false;
	
	boolean sendflag = false;
	
	boolean sendflagquery = true;
	
	boolean sendcmdquerymain = true;
	//转动圆圈
	ProgressDialog myprogress;
	
	//存储从从服务器中拿到的代码
	private String phoneNumberServer = "00";
	
	private static int STARTSTATUS_PROGRESS = 1;
	
	private static int SENDCMD_PROGRESS =2;
	//使用thread 获取更新状态
	
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
	private final int HANDLE_MSG_FROM_CONTROL =1;
	private final int HANDLE_MSG_FORM_PROGRESS =2;
	private static int MSG_LEGHTE_SEND = 10;
	private static int MSG_UID_LOW_RECIVE = 2;
	private static int MSG_UID_HIGHT_RECIVE = 3;
	private static int MSG_TYPE_RECIVE = 4;
	private static int MSG_STATUE_RECIVE = 1;
	byte contentBuffer[] = new byte[MSG_LEGHTE_SEND];
	
	private final int SEARCHCMD=5;
	private final int PHONECMD =8;
	private final int ALARMCMD =9;
	
	private final int SAFECMD_ADDNUMBER =0x01;
	private final int SAFECMD_DELENUMBER =0x02;
	private final int SAFECMD_GETNUMBER =0x03;
	private final int SAFECMD_DISARM =0x04;
	private final int SAFECMD_ARM =0x05;
	private final int SAFECMD_SINGERDISABLE = 0x06;
	

	/** 长按sensor按键*/
	
	private List<String> listNumber = null;
	private CsstSHSafeNumberAdapter safeNumberAdapter = null;
	
			
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(debug){
			Log.d(TAG,TAG+"the onCreate");
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csst_saft_listview);
		initDataSource();
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
//		setListSensorView();
	}
	
	@Override
	public void initDataSource() {
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
		mMacAdress = configPreference.getMacAdress();
		alarmFlag = configPreference.getAlarmFlag();
		autoAlarmFlag = configPreference.getAutoAlarmFlag();
		listNumber = new ArrayList<String>();
		//debug
//		for(int i=0;i<10;i++){
//			listNumber.add("12ee12112e12e");
//		}
		configwifitimer();
		
	}
	
	public void setListSensorView(){
		ListsafeSensorBeans = CsstSHSafeSensorTable.getInstance().query(mDb);
		if(ListsafeSensorBeans!=null){
			for(int i=0;i<CsstSHSafeSensorTable.getInstance().query(mDb).size(); i++){
				if(debug){
					Log.d(TAG,"the name is true setListSensorView "+CsstSHSafeSensorTable.getInstance().query(mDb).get(i).getmSensorName()+"the mselect is "+CsstSHSafeSensorTable.getInstance().query(mDb).get(i).isMselect());
				}
			}
			try{
				safeSensorAdapter = new CsstSHSafeSensorAdapter(this,ListsafeSensorBeans);
				mlistSensor.setAdapter(safeSensorAdapter);
				safeSensorAdapter.notifyDataSetChanged();
			}catch(Exception ex){
				Log.d(TAG,ex.toString());
			}
		
		}else{
			//为了让其在再没有sensorbeans的时候也可以显示 特别是在删除完了还可以刷新列表 所以要new arraylist 初始化
			//当删除或者是没有报警设备的时候需要强制设定为撤防状态 并发指令
			
			ListsafeSensorBeans = new ArrayList<CsstSHSafeSensorBean>();
			ListsafeSensorBeans.clear();
			Log.d(TAG,"the listsafesensorbean size is "+ListsafeSensorBeans.size());
			try{
				safeSensorAdapter = new CsstSHSafeSensorAdapter(this,ListsafeSensorBeans);
				mlistSensor.setAdapter(safeSensorAdapter);
				safeSensorAdapter.notifyDataSetChanged();
			}catch(Exception ex){
				Log.d(TAG,ex.toString());
			}
			
			
			try{
				Thread.sleep(2000);
			}catch(Exception ex){
				Log.d(TAG,ex.toString());
			}
			//为空的时候发送撤防命令
			if(myprogress!=null)
				myprogress.dismiss();
			sendflagquery = false;
			sendflag=true;
			Log.d(TAG,"setListSensorView  the sensor is null ");
			typeWhich = PHONECMD;
			sendcmd = SAFECMD_DISARM;
			new safeSendCmd(sendcmd,null).cancel(true);
			new safeSendCmd(sendcmd,null).execute();
			//强制让图标显示为撤防 以防没有发送成功
			configPreference.setAlarmFlag(false);
			mbtnmanusetting.setBackgroundColor(getResources().getColor(R.color.gray));
			mbtnautosetting.setBackgroundColor(getResources().getColor(R.color.gray));
			
			
		}
		
		
		
		
		
	}
	
	public void setListNumber(List<String> listnumber){
		List<String> mlistnumber =listnumber;
		safeNumberAdapter = new CsstSHSafeNumberAdapter(this,mlistnumber);
		listviewNumber.setAdapter(safeNumberAdapter);
		safeNumberAdapter.notifyDataSetChanged();
		
	}

	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnsafeCancel);
		mTVTitle = (TextView) findViewById(R.id.mTVsafeTitle);
		mBtnDone = (Button) findViewById(R.id.mBtnsafeDone);
		mlistSensor = (ListView) findViewById(R.id.lv_sensorlistview);
		mBtnaddaction = (TextView) findViewById(R.id.btnsafeaddaction);
		mbtnmanusetting = (Button) findViewById(R.id.btnsafemansetting);
		mbtnautosetting = (Button) findViewById(R.id.btnsafeautosetting);
		mbtnaddnumber = (Button) findViewById(R.id.btnsafeaddnumber);
		mbtndeletenumber= (Button) findViewById(R.id.btnsafedeletenumber);
		mbtnsafeclock  = (Button) findViewById(R.id.btnsafeclock);
		
		metaddnumber = (EditText)findViewById(R.id.etsafeaddnumber);
		metdeletenumber = (EditText)findViewById(R.id.etsafedeletepnumber);
		mbtnGetnumer = (Button) findViewById(R.id.btnsafegetnumber);
		listviewNumber = (ListView) findViewById(R.id.lv_safenumber);
			
	}

	@Override
	public void initWidgetState() {
		
		setListNumber(listNumber);
		if(alarmFlag){
			mbtnmanusetting.setBackgroundColor(getResources().getColor(R.color.green));
			setListSensorView();
		}else{
			mbtnmanusetting.setBackgroundColor(getResources().getColor(R.color.gray));
		}
		if(autoAlarmFlag){
			mbtnautosetting.setBackgroundColor(getResources().getColor(R.color.green));
		}else{
			mbtnautosetting.setBackgroundColor(getResources().getColor(R.color.gray));
		}
//		if(CsstSHSafeSensorTable.getInstance().countRecord(mDb)<1){
//			for(int m =0; m<2;m++){
//				CsstSHSafeSensorBean safesensorbean = new CsstSHSafeSensorBean("烟感"+m,4,0,1,2,3,1);
//				CsstSHSafeSensorTable.getInstance().insert(mDb, safesensorbean);
//			}
//		}
		
		ListsafeSensorBeans = CsstSHSafeSensorTable.getInstance().query(mDb);
		if(ListsafeSensorBeans!=null){
			safeSensorAdapter = new CsstSHSafeSensorAdapter(this,ListsafeSensorBeans);
		}else{
			//刚刚加入的时候如果listsensorbeans 为空表示没有传感器 就发送一个设防命令。以免在上一次安装APP是设防的状态下
			//卸载了。 但是服务器还是保持原来的设防状态。当重新安装的时候刚刚加入安防状态系统是默认是撤防的。但是在加入第一个设备
			//同时将他点击启用状态，但是又没有设定为设防。当有报警的时候就会呈现出。手机是撤防状态。但是会受到报警的信号。
			//再次。当进入控制界面的时候如果列表为空。或者是判断现在数据中保存的数据是撤防的状态就发送撤防命令。
			if(!alarmFlag){
				sendflagquery = false;
				sendflag=true;
				Log.d(TAG,"customBtnListener is press here the value is ");
				typeWhich = PHONECMD;
				sendcmd = SAFECMD_DISARM;
				new safeSendCmd(sendcmd,null).cancel(true);
				new safeSendCmd(sendcmd,null).execute();
				//强制让图标显示为撤防 以防没有发送成功
				configPreference.setAlarmFlag(false);
				mbtnmanusetting.setBackgroundColor(getResources().getColor(R.color.gray));
				mbtnautosetting.setBackgroundColor(getResources().getColor(R.color.gray));
			}
		}
		mlistSensor.setAdapter(safeSensorAdapter);
		
	
		
	}

	@Override
	public void initWidgetListener() {
		mBtnDoneListener = new DoneBtnListener();
		mBtnAddActionListener = new AddActionBtnListener();
		mSensorListner = new SensorModifyListener();
		mlistSensor.setOnItemLongClickListener(mSensorListner);
		
	}
	
	
	
	

	/**
	 * 设备item修改设备
	 * @author liuyang
	 */
	private final class SensorModifyListener implements AdapterView.OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			if(debug)
			{
				Log.d(TAG,"the position is "+position);
			}
			try{
				modifySensor(ListsafeSensorBeans.get(position));
			}catch(Exception ex){
				Log.d(TAG,ex.toString());
			}
			 return false;
		}
		
	}
	
	
	/**
	 * buffer转换成存储信息
	 * @param buffer
	 * @return
	 */
	public  String bufferToMessge(byte[] buffer) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buffer.length; i++) {
				sb.append(String.format("%02x", buffer[i]));
		}
		return sb.toString();
	}

	@Override
	public void addWidgetListener() {
		mbtnsafeclock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				Intent intent = new Intent();
				intent.setClass(CsstSHSaftListView.this, CsstSHSafeClockMain.class);
				startActivity(intent);
				CsstSHSaftListView.this.finish();
			}
		});
		
		
		mBtnaddaction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(debug){
					Log.d(TAG,"addcation is here ");
				}
				typeWhich = SEARCHCMD;
				if(myprogress!=null)
					myprogress.dismiss();
				sendflagquery = false;
				sendflag=true;
				Log.d(TAG,"customBtnListener is press here the value is ");
				sendcmd = (byte)0x02;
				new safeSendCmd(sendcmd,null).cancel(true);
				new safeSendCmd(sendcmd,null).execute();
				
			}
		});
		
		mbtnmanusetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ListsafeSensorBeans = CsstSHSafeSensorTable.getInstance().query(mDb);
				if(ListsafeSensorBeans!=null){//如果是没有传感器是不能使用按钮的

					alarmFlag = configPreference.getAlarmFlag();
					if(alarmFlag){
				
						if(myprogress!=null)
							myprogress.dismiss();
						sendflagquery = false;
						sendflag=true;
						Log.d(TAG,"customBtnListener is press here the value is ");
						typeWhich = PHONECMD;
						sendcmd = SAFECMD_DISARM;
						new safeSendCmd(sendcmd,null).cancel(true);
						new safeSendCmd(sendcmd,null).execute();
						
					}else{
						if(myprogress!=null)
							myprogress.dismiss();
						sendflagquery = false;
						sendflag=true;
						Log.d(TAG,"customBtnListener is press here the value is ");
						sendcmd = SAFECMD_ARM;
						StringBuffer sb = new StringBuffer();
						//只有sensor的情况下才有相应的操作
						if(ListsafeSensorBeans!=null){
							for(int i=0;i<ListsafeSensorBeans.size();i++){
								sb.append(String.format("%02x", (byte)ListsafeSensorBeans.get(i).getMssuidLow()));
//								Log.d(TAG,"the sb 1 is "+sb.toString());
								sb.append(String.format("%02x", (byte)ListsafeSensorBeans.get(i).getMssuidMid()));
//								Log.d(TAG,"the sb 2 is "+sb.toString());
								sb.append(String.format("%02x", (byte)ListsafeSensorBeans.get(i).getMssuidHight()));
								sb.append(",");
								sb.append(ListsafeSensorBeans.get(i).getmSensorName());
								sb.append(",");
								if(ListsafeSensorBeans.get(i).isMselect()==1){
									sb.append("1;");
								}else{
									sb.append("0;");
								}
							}
							String sendphone = sb.toString();
							Log.d(TAG," send the cmd sendphone is "+sendphone);
							typeWhich = PHONECMD;
							new safeSendCmd(sendcmd,sendphone.getBytes()).cancel(true);//默认UTF8
							new safeSendCmd(sendcmd,sendphone.getBytes()).execute();//默认UTF8
						}
						
					}
				}
				
			}
			
			
		});
		mbtnautosetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ListsafeSensorBeans = CsstSHSafeSensorTable.getInstance().query(mDb);
				if(ListsafeSensorBeans!=null){ //如果是没有传感器是不能使用按钮的
					autoAlarmFlag = configPreference.getAutoAlarmFlag();
					if(autoAlarmFlag){
						mbtnautosetting.setBackgroundColor(getResources().getColor(R.color.gray));
						configPreference.setAutoAlarmFlag(false);
					}else{
						mbtnautosetting.setBackgroundColor(getResources().getColor(R.color.green));
						configPreference.setAutoAlarmFlag(true);
					}
				}
				
			}
		});
		mbtnaddnumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(debug){
					Log.d(TAG,"the metaddnumber lenght is "+metaddnumber.getText().toString().length());
				}
				if(metaddnumber.getText().toString().equals("")){
					Toast.makeText(CsstSHSaftListView.this, R.string.csst_safe_addphonenumber_null, Toast.LENGTH_SHORT).show();
				}else if(metaddnumber.getText().toString().length()!=11){
					Toast.makeText(CsstSHSaftListView.this, R.string.csst_safe_phonenumber_leag, Toast.LENGTH_SHORT).show();
				}else{
					if(myprogress!=null)
						myprogress.dismiss();
					sendflagquery = false;
					sendflag=true;
					Log.d(TAG,"customBtnListener is press here the value is ");
					typeWhich = PHONECMD;
					sendcmd = SAFECMD_ADDNUMBER;
					new safeSendCmd(sendcmd,metaddnumber.getText().toString().getBytes()).cancel(true);
					new safeSendCmd(sendcmd,metaddnumber.getText().toString().getBytes()).execute();
					
				}
			}
		});
		mbtndeletenumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(debug){
					Log.d(TAG,"the metaddnumber lenght is "+metaddnumber.getText().toString().length());
				}
				if(metdeletenumber.getText().toString().equals("")){
					Toast.makeText(CsstSHSaftListView.this, R.string.csst_safe_deletephonenumber_null, Toast.LENGTH_SHORT).show();
				}else if(metdeletenumber.getText().toString().length()!=11){
					Toast.makeText(CsstSHSaftListView.this, R.string.csst_safe_phonenumber_leag, Toast.LENGTH_SHORT).show();
				}else{
					if(myprogress!=null)
						myprogress.dismiss();
					sendflagquery = false;
					sendflag=true;
					Log.d(TAG,"customBtnListener is press here the value is ");
					typeWhich = PHONECMD;
					sendcmd = SAFECMD_DELENUMBER;
					new safeSendCmd(sendcmd,metdeletenumber.getText().toString().getBytes()).cancel(true);
					new safeSendCmd(sendcmd,metdeletenumber.getText().toString().getBytes()).execute();
				}
			}
		});
		mbtnGetnumer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				if(myprogress!=null)
					myprogress.dismiss();
				sendflagquery = false;
				sendflag=true;
				Log.d(TAG,"customBtnListener is press here the value is ");
				typeWhich = PHONECMD;
				sendcmd = SAFECMD_GETNUMBER;
				new safeSendCmd(sendcmd,null).cancel(true);
				new safeSendCmd(sendcmd,null).execute();
			}
		});
		mBtnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				backEvent();
			}
		});
		
	}
	
	private final class safeSendCmd extends AsyncTask<Void, Void, Boolean>{
		byte[] param;
		byte cmd ;
		private Dialog mDialog = null;
		public safeSendCmd(byte cmd,byte[] param){
			//清空数据
			phoneNumberServer="";
			this.cmd = cmd;
			this.param = param;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			if(typeWhich ==SEARCHCMD){
				DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (safeSendCmd.this.getStatus() != AsyncTask.Status.FINISHED){
							safeSendCmd.this.cancel(true);
						}
						//发送停止命
//						Toast.makeText(CsstSHSaftListView.this, R.string.csst_adddevice_research_finish, Toast.LENGTH_SHORT).show();
					}
				};
				mDialog =CsstContextUtil.searchDialog(CsstSHSaftListView.this, null, getString(R.string.csst_safe_search_sensor), callback);
				mDialog.show();
			}else{
				if(!(sendcmd==0x06||(ListsafeSensorBeans==null)&&(sendcmd==SAFECMD_DISARM)))//是去掉某个器件时不弹出提示 删除 和添加时
				LoadProcess_fisrt(SENDCMD_PROGRESS);
			}
			
			
			
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
//			sim.setIpaddress(GobalDef.SERVER_URL);
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
			Log.d(TAG," the cmd  is "+cmd+"the mac is "+Long.valueOf(msgBuffer));
		
			//
			int length = 0;
			if(param!=null){
				length =param.length+2; 
			}else{
				length = 2;
			}
			byte[] contentBuf = new byte[length];
			if(typeWhich == SEARCHCMD){//发送搜索命令是用新的发指令命令还是用原来的指令集当时加长了超时时间
				contentBuf[0]=(byte)0x05;
				contentBuf[1]=(byte)cmd;
				if(length>2){
					for(int m=0;m<length-2;m++){
						contentBuf[m+2]= param[m];
					}
				}
				for(int n=0;n<length;n++){
					Log.d(TAG,TAG+ " safeSendCmd search  contentbuf setting " + n + " value: " + contentBuf[n]);
				}
				
				bcm.setContentBuf(contentBuf);
				Log.d(TAG,"safeSendCmd start to send MsgReturn ");
//				BaseCodeMessage bcmrcv = UdpJavaEncodingProcess.GetMsgReturn(bcm, sim);
				BaseCodeMessage bcmrcv = UdpJavaEncodingProcess.GetMsgReturnlongtime(bcm, sim);
				Log.d(TAG,"safeSendCmd ready send get MsgReturn ");
				if(bcmrcv != null){
					//return true;
					if(bcmrcv.getContentBuf() != null){
						for(int i = 0; i<bcmrcv.getContentBuf().length; i++){
							contentBuffer[i]=bcmrcv.getContentBuf()[i];
							Log.d(TAG,TAG+ " safeSendCmd content " + i + " value: " + bcmrcv.getContentBuf()[i]);
						}
					}
					Log.d(TAG,"safeSendCmd search get messega finish ");
					//先停止原来来定时器 因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
					sendSuc = true;
					return true;
				}
				else{
					sendSuc = false;
					return false;
					
				}
				
				
				
				
				
			}else {//是用发红外的命令来发明码指令
				if(typeWhich == PHONECMD){
					contentBuf[0]=(byte)0x08;
				}else if(typeWhich == ALARMCMD){
					contentBuf[0]=(byte)0x09;
				}
				
				contentBuf[1]=(byte)cmd;
				if(length>2){
					for(int m=0;m<length-2;m++){
						contentBuf[m+2]= param[m];
					}
				}
				for(int n=0;n<length;n++){
					Log.d(TAG,TAG+ " safeSendCmd phone contentbuf setting " + n + " value: " + contentBuf[n]);
				}
				
				//用发码的形式发送数据
//				sim.setIpaddress("192.168.1.106");
//				sim.setPort(12188);
				
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
				Log.d(TAG,TAG+"");
				Log.d(TAG,TAG+"the mac adress is "+Long.valueOf(msgBuffer));
				slrm.setCodeBuf(contentBuf);
				DatagramPacket msg = null;
				msg = UdpProcess_ZQL.GetMsgReturn(slrm, sim);
				if(msg == null){
					Log.d(TAG,"safeSendCmd suc fail fail fail  fail  fail");
					sendSuc = false;
					return false;
				} else {
					sendSuc = true;
					//SendRFCodeRspMessage srfrm = (SendRFCodeRspMessage)msg;
					byte[] getbuf = new byte[msg.getLength()] ;
					for(int i=0;i<msg.getLength();i++){
						Log.d(TAG," UdpProcess_ZQL_SAFE get the message the [  "+i+"]"+msg.getData()[i]);
						getbuf[i]= msg.getData()[i];
					}
					phoneNumberServer = new String(getbuf);
					Log.d(TAG,"safeSendCmd suc ok ok ok  ok  ok the msg is "+phoneNumberServer);
					if(myprogress!=null){
						myprogress.dismiss();
					}
					Log.d(TAG,"safeSendCmd get messega finish ");
					//先停止原来来定时器 因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
					stopTimer();
					//停止发送命
					startTimer();
				}
				
			}
			
			Log.d(TAG,"safeSendCmd start to sleep for wait");
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
			return true;
			}
			return true;
		}

		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(mDialog!=null){
				mDialog.dismiss();
			}
		 if (timerdelaygetstatu != null) {  
			   timerdelaygetstatu.cancel();  
	            timerdelaygetstatu = null;  
	        }  
			
			//先停止原来来定时器 因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
			stopTimer();
			startTimer();
			if(!result){
				if(typeWhich==SEARCHCMD){
					Toast.makeText(CsstSHSaftListView.this, getResources().getString(R.string.csst_safe_search_failtip), Toast.LENGTH_SHORT).show();
				}
			}else{//如果是设防撤防的话需要想主机发送数据
				
				if(ListsafeSensorBeans!=null){
					//lenght 是多少个传感器 ：是否设防+传感器个数+uuid1 uuid2 uuid3+select+been
					byte[] sendsensortomain = new byte[ListsafeSensorBeans.size()*5+2];
					sendsensortomain[1]=(byte)ListsafeSensorBeans.size();
					for(int i=0;i<ListsafeSensorBeans.size();i++){
						sendsensortomain[2+i*5] =(byte)ListsafeSensorBeans.get(i).getMssuidLow();
						sendsensortomain[2+i*5+1] =(byte)ListsafeSensorBeans.get(i).getMssuidMid();
						sendsensortomain[2+i*5+2] =(byte)ListsafeSensorBeans.get(i).getMssuidHight();
						sendsensortomain[2+i*5+3] =(byte)ListsafeSensorBeans.get(i).getMbattery();
						sendsensortomain[2+i*5+4] =(byte)ListsafeSensorBeans.get(i).isMselect();
					}
					if(sendcmd==SAFECMD_ARM){
						sendsensortomain[0]=(byte)0x01;
						new sendSensortoMain((byte)0x01,sendsensortomain).cancel(true);//默认UTF8
						new sendSensortoMain((byte)0x01,sendsensortomain).execute();//默认UTF8
					}else if(sendcmd==SAFECMD_DISARM){
						sendsensortomain[0]=(byte)0x00;
						new sendSensortoMain((byte)0x01,sendsensortomain).cancel(true);//默认UTF8
						new sendSensortoMain((byte)0x01,sendsensortomain).execute();//默认UTF8
					}else{
						if(myprogress!=null){
							myprogress.dismiss();
						}
					}
					
				}
				if(myprogress!=null){
					myprogress.dismiss();
				}
			}
			
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
  				if(myprogress!=null)
  				myprogress.dismiss();
  				Log.d(TAG,"sendSensortoMain get messega finish ");
  				//先停止原来来定时�?因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
//  				Message msg = handlerconfigwifi.obtainMessage(SENDCMD);
//  	  			handlerconfigwifi.sendMessage(msg);
  				return true;
  			}
  			else{
  				
  			}
  			
  			Log.d(TAG,"Updatesenddebugcmdmianban start to sleep for wait");
  			return true;
  		}

  		
  	}
    
			
	
	
	
	
	/**
	 * 返回事件
	 */
	private final void backEvent(){
		Intent intent = new Intent();
		intent.setClass(CsstSHSaftListView.this, CsstSHSaftListView.class);
		startActivity(intent);
		CsstSHSaftListView.this.finish();
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
	 * 完成按钮监听器
	 * @author liuyang
	 */
	private final class DoneBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(CsstSHSaftListView.this, CsstSHSaftListView.class);
			startActivity(intent);
			CsstSHSaftListView.this.finish();
		}
	}
	
	
	
	
	private final class AddActionBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {}
	}
	@Override
	protected void onDestroy() {
		if(debug){
			Log.d(TAG,TAG+"the onDestroy");
		}
		if(mDb!=null){
			mDb.close();	
		}
		
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		if(debug){
			Log.d(TAG,TAG+"the onResume");
		}
		//刷新列表
		super.onResume();
	}
	
	
	public boolean onKeyDown(int keyCode,KeyEvent event) {   
	       // 是否触发按键为back键   
	       if (keyCode == KeyEvent.KEYCODE_BACK) {   
	           // 弹出 退出确认框
	    	   backEvent();
	            return true;   
	        } 
	       return true; 
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
//						// TODO Auto-generated method stub
////						if(!cleanTimer){//当操作成功之后    清除定时器
//							Message msg = handler.obtainMessage(TIMEOUT);
//							handler.sendMessage(msg);
						if(type ==STARTSTATUS_PROGRESS){
						Log.d(TAG,"timerdelaygetstatu timerOut ");
//						sendflagquery = true;
//						queryStatus_thread = new queryStatus();//ZQL
//						queryStatus_flag = true; //ZQL
//						queryStatus_thread.start();//ZQL
						}else if(type ==SENDCMD_PROGRESS){
							Message msg = handlerconfigwifi.obtainMessage(HANDLE_MSG_FORM_PROGRESS);
							handlerconfigwifi.sendMessage(msg);
						}
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
					   Log.d(TAG,"handleMessage  is here ");
					  
//					   Log.d(TAG,"contentBuffer[5]  is here "+contentBuffer[MSG_STATUE_RECIVE]);
					   switch(msg.what){
					   
					   case HANDLE_MSG_FROM_CONTROL:
						   switch(typeWhich){
						   case SEARCHCMD:
							   if(sendSuc){
								   Log.d(TAG,"contentBuffer[5]  is here "+contentBuffer[MSG_STATUE_RECIVE]);
								   exit_flag = false;
								 //通过UID 判断是否在系统里面有这个传感器。如果有就提示不添加
								   int sensorcount = CsstSHSafeSensorTable.getInstance().countRecord(mDb)+1;
								   //重写获取数量
								   ListsafeSensorBeans = CsstSHSafeSensorTable.getInstance().query(mDb);
								   if(ListsafeSensorBeans!=null){
									   for(int i=0;i<ListsafeSensorBeans.size();i++){
										   if(((byte)ListsafeSensorBeans.get(i).getMssuidLow()==contentBuffer[2])&&((byte)ListsafeSensorBeans.get(i).getMssuidMid()==contentBuffer[3])&&((byte)ListsafeSensorBeans.get(i).getMssuidHight()==contentBuffer[4])){
											   Toast.makeText(CsstSHSaftListView.this, R.string.csst_please_input_sensorname_repeat, Toast.LENGTH_SHORT).show();
											   exit_flag = true;
											   break;
											   
										   }
									   }
								   }
								   if(!exit_flag){
									   CsstSHSafeSensorBean sensor = new CsstSHSafeSensorBean("传感器"+sensorcount,1,0,(int)contentBuffer[2],(int)contentBuffer[3],(int)contentBuffer[4],1);
									   CsstSHSafeSensorTable.getInstance().insert(mDb, sensor);
									   setListSensorView();  
									   
									   //刚刚加入就将它失效
										StringBuffer sb = new StringBuffer();
										sb.append(String.format("%02x", (byte)sensor.getMssuidLow()));
										sb.append(String.format("%02x", (byte)sensor.getMssuidMid()));
										sb.append(String.format("%02x", (byte)sensor.getMssuidHight()));
										sb.append(",");
										sb.append(sensor.getmSensorName());
										sb.append(",");
										sb.append("0;");
										sendcmd = 0x06;
										typeWhich = PHONECMD;
										new safeSendCmd(sendcmd,sb.toString().getBytes()).cancel(true);//默认UTF8
										new safeSendCmd(sendcmd,sb.toString().getBytes()).execute();//默认UTF8
										
										
									   exit_flag = false;
								   }
								  
							   }
							   break;
						   case PHONECMD:
							   switch (sendcmd) {
							case SAFECMD_ADDNUMBER :
								if(phoneNumberServer.equals("1")){
									metaddnumber.setText("");
									Toast.makeText(CsstSHSaftListView.this, getResources().getString(R.string.csst_safe_addnumber_suc), Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(CsstSHSaftListView.this, getResources().getString(R.string.csst_safe_addnumber_fail), Toast.LENGTH_SHORT).show();
								}
								break;
							case SAFECMD_DELENUMBER:
								if(phoneNumberServer.equals("1")){
									metdeletenumber.setText("");
									Toast.makeText(CsstSHSaftListView.this, getResources().getString(R.string.csst_safe_deletenumber_suc), Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(CsstSHSaftListView.this, getResources().getString(R.string.csst_safe_deletenumber_fail), Toast.LENGTH_SHORT).show();
								}
								break;
							case SAFECMD_GETNUMBER:
								if(sendSuc){
										if(phoneNumberServer.length()>10){
//											Toast.makeText(CsstSHSaftListView.this, getResources().getString(R.string.csst_safe_getnumber_suc), Toast.LENGTH_SHORT).show();
											//清除之前所有的数据
											listNumber.clear();
											String[] msgBuffer = phoneNumberServer.split(",");
											//将获取的电话号码解析出来
											for(int i=0;i<msgBuffer.length;i++){
												//逐个添加到数组当中
												listNumber.add(msgBuffer[i]);
											}
											//将字符串添加到相应的adpter中更新显示
											setListNumber(listNumber);
											}else{//没有的话就清除所有数据
												Toast.makeText(CsstSHSaftListView.this, getResources().getString(R.string.csst_safe_getnumber_null), Toast.LENGTH_SHORT).show();
												listNumber.clear();
												setListNumber(listNumber);
											}
								
								}else{
									Toast.makeText(CsstSHSaftListView.this, getResources().getString(R.string.csst_safe_getnumber_fail), Toast.LENGTH_SHORT).show();
								}
								break;
							case SAFECMD_DISARM :
								if(phoneNumberServer.equals("1")){
									configPreference.setAlarmFlag(false);
									mbtnmanusetting.setBackgroundColor(getResources().getColor(R.color.gray));
									mbtnautosetting.setBackgroundColor(getResources().getColor(R.color.gray));
									Toast.makeText(CsstSHSaftListView.this, getResources().getString(R.string.csst_safe_disarm_suc), Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(CsstSHSaftListView.this, getResources().getString(R.string.csst_safe_disarm_fail), Toast.LENGTH_SHORT).show();
								}
								break;
							case SAFECMD_ARM :
								if(phoneNumberServer.equals("1")){
									Toast.makeText(CsstSHSaftListView.this, getResources().getString(R.string.csst_safe_arm_suc), Toast.LENGTH_SHORT).show();
									configPreference.setAlarmFlag(true);
									mbtnmanusetting.setBackgroundColor(getResources().getColor(R.color.green));
								}else{
									Toast.makeText(CsstSHSaftListView.this, getResources().getString(R.string.csst_safe_arm_fail), Toast.LENGTH_SHORT).show();
								}
								break;
							case SAFECMD_SINGERDISABLE :
								if(phoneNumberServer.equals("1")){
									
								}
								break;

							default:
								break;
							}
							   break;
						   case ALARMCMD:
							   break;
						   }
					  
					   stopTimer();
					   break;
					   
					   case HANDLE_MSG_FORM_PROGRESS:
						   if(myprogress!=null)
							   myprogress.dismiss();
						   break;
					   }
					  }
				    };
		}
	    
	    
	    /**
		 * 房间修改事件
		 * @author liuyang
		 */
			public boolean modifySensor(final CsstSHSafeSensorBean sensorbean) {
				AlertDialog.Builder builder = new AlertDialog.Builder(CsstSHSaftListView.this);
				builder.setTitle(sensorbean.getmSensorName());
				builder.setItems(R.array.csst_floor_modify, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							// 重命�?
							case 0:
								modifySensorName(sensorbean);
								break;
							// 删除
							case 1:
								deleteSensor(sensorbean);
								break;
						}
					}
				});
				builder.setNegativeButton(R.string.csst_cancel, null);
				Dialog d = builder.show();
				d.setCanceledOnTouchOutside(true);
				return false;
			}
	    

		/**
		 * 删除房间
		 * @param room
		 */
		private final void deleteSensor(CsstSHSafeSensorBean sensorbean){
			CsstSHSafeSensorTable.getInstance().delete(mDb, sensorbean);
			setListSensorView();
			
			StringBuffer sb = new StringBuffer();
			sb.append(String.format("%02x", (byte)sensorbean.getMssuidLow()));
			sb.append(String.format("%02x", (byte)sensorbean.getMssuidMid()));
			sb.append(String.format("%02x", (byte)sensorbean.getMssuidHight()));
			sb.append(",");
			sb.append(sensorbean.getmSensorName());
			sb.append(",");
			sb.append("0;");
			Log.d(TAG," send the cmd sendphone is "+sb.toString());
			Log.d(TAG,"the name is false  "+sensorbean.getmSensorName());
			sendcmd = 0x06;
			typeWhich = PHONECMD;
			new safeSendCmd(sendcmd,sb.toString().getBytes()).cancel(true);//默认UTF8
			new safeSendCmd(sendcmd,sb.toString().getBytes()).execute();//默认UTF8
			
		}
		
		/**
		 * 修改房间
		 * @param room
		 */
		private final void modifySensorName( final CsstSHSafeSensorBean sensorbean){
			//显示天剑房间对话�?
			final EditText inputServer = new EditText(CsstSHSaftListView.this);
			inputServer.setText(sensorbean.getmSensorName());
			inputServer.setSelection(sensorbean.getmSensorName().length());
			AlertDialog.Builder builder = new AlertDialog.Builder(CsstSHSaftListView.this);
			builder.setTitle(R.string.csst_please_input_sensorname);
			builder.setView(inputServer);
			builder.setNegativeButton(R.string.csst_cancel, null);
			builder.setPositiveButton(R.string.csst_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String sensorName = inputServer.getText().toString();
					//房间名为�?
					if (TextUtils.isEmpty(sensorName)){
						Toast.makeText(CsstSHSaftListView.this, R.string.csst_please_input_sensorname_nulltip, Toast.LENGTH_SHORT).show();
						return;
					}
					//同一场景下是否存在同一房间名称
					for(int i=0;i<ListsafeSensorBeans.size();i++){
						if(ListsafeSensorBeans.get(i).getmSensorName().equals(sensorName)){
							Toast.makeText(CsstSHSaftListView.this, R.string.csst_please_input_sensorname_repeat, Toast.LENGTH_SHORT).show();
						}
					}
					//修改房传感器名称
					sensorbean.setmSensorName(sensorName);
					CsstSHSafeSensorTable.getInstance().update(mDb, sensorbean);
					//同时更新列表显示
					setListSensorView();
					//同时需要把修改的内容更新到服务器还需要带内容
					StringBuffer sb = new StringBuffer();
					try{
						Thread.sleep(500);
					}catch(Exception ex){
						Log.d(TAG,ex.toString());
					}
					
					sb.append(String.format("%02x", (byte)sensorbean.getMssuidLow()));
					sb.append(String.format("%02x", (byte)sensorbean.getMssuidMid()));
					sb.append(String.format("%02x", (byte)sensorbean.getMssuidHight()));
					sb.append(",");
					sb.append(sensorbean.getmSensorName());
					sb.append(",");
					if(sensorbean.isMselect()==1){
						sb.append("1;");	
					}else{
						sb.append("0;");	
					}
					Log.d(TAG," send the cmd sendphone is "+sb.toString());
					Log.d(TAG,"the name is   "+sensorbean.getmSensorName());
					sendcmd = 0x06;
					typeWhich = PHONECMD;
					new safeSendCmd(sendcmd,sb.toString().getBytes()).cancel(true);//默认UTF8
					new safeSendCmd(sendcmd,sb.toString().getBytes()).execute();//默认UTF8
				}
			});
			builder.show();
		}
		
	public void  LoadProcess_fisrt(int type) {
		 if(myprogress!=null)
			   myprogress.dismiss();
		myprogress = new ProgressDialog(this);
		myprogress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		if(type== SENDCMD_PROGRESS){
			switch (sendcmd) {
			case SAFECMD_ADDNUMBER :
				myprogress.setMessage(getResources().getString(R.string.csst_safe_addnumbering));
				break;
			case SAFECMD_DELENUMBER:
				myprogress.setMessage(getResources().getString(R.string.csst_safe_deletenumbering));
				break;
			case SAFECMD_GETNUMBER:
				myprogress.setMessage(getResources().getString(R.string.csst_safe_getnumbering));
				break;
			case SAFECMD_DISARM :
				myprogress.setMessage(getResources().getString(R.string.csst_safe_disarming));
				break;
			case SAFECMD_ARM :
				myprogress.setMessage(getResources().getString(R.string.csst_safe_arming));
				break;
			case SAFECMD_SINGERDISABLE :
				myprogress.setMessage(getResources().getString(R.string.csst_send_cmd));
				break;

			default:
				break;
			}
			timerOut(1700, SENDCMD_PROGRESS);//17S 超时 根据最长的定时 5*3 =15S
		}else if(type== STARTSTATUS_PROGRESS){
			timerOut(1000, SENDCMD_PROGRESS);
			myprogress.setMessage(getResources().getString(R.string.csst_triple_start_msg));
		}
		
		myprogress.setIndeterminate(false);
		myprogress.setCancelable(false);
		myprogress.show();
	}
	

}
