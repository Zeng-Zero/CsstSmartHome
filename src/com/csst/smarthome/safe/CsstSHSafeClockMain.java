package com.csst.smarthome.safe;

import java.net.DatagramPacket;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.adapter.CsstSHClockAdapter;
import com.csst.smarthome.activity.adapter.CsstSHSafeClockAdapter;
import com.csst.smarthome.bean.CsstSafeClockBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHSafeClockTable;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.message.rfcode.SendRFCodeReqMessage;

/**
 * 定时开启界面
 * @author liuyang
 */
public class CsstSHSafeClockMain extends Activity implements ICsstSHInitialize, ICsstSHConstant{

	/** 返回按钮 */
	private Button mBtnCancel = null;
	/** 完成按钮 */
	private Button mBtnDone = null;
	/** 标题 */
	private TextView mTVTitle = null;
	
	private BackBtnListener mBackBtnListener = null;
	private DoneBtnListener mBtnDoneListener = null;
	private AddActionBtnListener mBtnAddActionListener = null;
	private Button mbtnAddClockOpen ;
	
	private String TAG = "CsstSHAddModelClockOpenMain";
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	/** 主控MAC地址 */
	private String mMacAdress = null;
	
	private boolean debug = true;
	/**
	 * 上层传过来的modelname
	 */
	private String modelName = null;
	
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	/** 定时列表 */
	private ListView mlistviewclockopen = null;
	/** 定时配器 */
	private CsstSHClockAdapter clockOpenAdapter = null;
	/** 定时数据集合 */
	private List<CsstSafeClockBean> safeClockBeans = null;
	
	
	//alarm list
//	private List<Alarm> safeClockList = null;
	//alarm list
	private List<CsstSafeClockBean> safeClockList = null;
	private final byte DELETE_MODEL_CLOCK =2;
	
	
	/** alarm配器 */
	private CsstSHSafeClockAdapter alarmAdapter = null;
	
	//定时开启
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csst_safe_openclockmain);
		initDataSource();
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
	}
	
	@Override
	public void initDataSource() {
		
	}

	@Override
	protected void onResume() {
		if(debug){
			System.out.println(TAG+"the onResume");
		}
		//刷新列表
	//查询数据库中是否有以这个名称存储的action 数据，有的话就显示出来
			if(CsstSHSafeClockTable.getInstance().query(mDb)!=null){
				safeClockList=CsstSHSafeClockTable.getInstance().query(mDb);
				//修改用新的alarm 数据库
				if(safeClockList!=null){
					alarmAdapter = new CsstSHSafeClockAdapter(CsstSHSafeClockMain.this, safeClockList);
					mlistviewclockopen.setAdapter(alarmAdapter);
				}
				
			}
		super.onResume();
	}
	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
		mTVTitle = (TextView) findViewById(R.id.mTVTitle);
		mBtnDone = (Button) findViewById(R.id.mBtnDone);
		mbtnAddClockOpen =  (Button) findViewById(R.id.btaddclock);
//		mrgbtn.check(mrbtnauto);
		mlistviewclockopen = (ListView)findViewById(R.id.lv_clocklistview);
		
	
	}

	@Override
	public void initWidgetState() {
		//设置标题
		mTVTitle.setText(R.string.csst_safe_clock);
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
		mMacAdress = configPreference.getMacAdress();
		
		//  以modelName 查询该情景模式的实例化
		//然后再用实例化获取modelID，再用modelID查询 查询改模式下的所有的动作
		if(debug){
			System.out.println("query the safeClockBeans the name is "+modelName);
		}
		//刷新列表
	//查询数据库中是否有以这个名称存储的action 数据，有的话就显示出来
			if(CsstSHSafeClockTable.getInstance().query(mDb)!=null){
				safeClockList=CsstSHSafeClockTable.getInstance().query(mDb);
				//修改用新的alarm 数据库
				if(safeClockList!=null){
					alarmAdapter = new CsstSHSafeClockAdapter(CsstSHSafeClockMain.this, safeClockList);
					mlistviewclockopen.setAdapter(alarmAdapter);
				}
			}
		if(debug){
			System.out.println(" the listview press init is here ");
		}
		// 列表的点击事件
		mlistviewclockopen.setOnItemClickListener(new OnItemClickListener() {
						@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long arg3) {
						if(debug){
							System.out.println(" the listview press is here ");
						}
						Intent intent = new Intent();
						intent.setClass(CsstSHSafeClockMain.this, CsstSHSafeClockAdd.class);
//						intent.putExtra(Alarms.ALARM_ID, safeClockList.get(position).id);
						intent.putExtra("CsstSafeClockBean", safeClockList.get(position));
						startActivity(intent);
				}
			});
		if(debug){
			System.out.println(" the listview long press init is here ");
		}
//		楼层列表的长按监听事件
		mlistviewclockopen.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				if(debug){
					System.out.println(" the listview longpress is here ");
				}
				final CsstSafeClockBean safeClockBean=safeClockList.get(position);
				AlertDialog.Builder builder = new AlertDialog.Builder(CsstSHSafeClockMain.this);
//				builder.setTitle(clockopeBean.getmClockOpenName());
				builder.setItems(R.array.csst_clockopendelet, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							//重命名
							case 0:
								CsstSHSafeClockTable.getInstance().delete(mDb, safeClockBean);
								
								
							//刷新列表
							//查询数据库中是否有以这个名称存储的action 数据，有的话就显示出来
								safeClockList=CsstSHSafeClockTable.getInstance().query(mDb);
								//修改用新的alarm 数据库
								
								if(safeClockList!=null){
									alarmAdapter = new CsstSHSafeClockAdapter(CsstSHSafeClockMain.this, safeClockList);
									mlistviewclockopen.setAdapter(alarmAdapter);
						
								
							}
								
								StringBuffer sb = new StringBuffer();
								//唯一识别号
								sb.append(String.format("%02x", (byte)safeClockBean.getmClockOpenId()));
								sb.append(",");
								sb.append("办公室");
								sb.append(",");
								sb.append(1);
								sb.append(",");
								sb.append(String.format("%02x", (byte)safeClockBean.getmClockOpenTimeHour()));
								sb.append(",");
								sb.append(String.format("%02x", (byte)safeClockBean.getmClockOpenTimeMin()));
								sb.append(",");
//								sb.append(String.format("%02x", (byte)safeClockBean.getmClockOpenDay()));
								byte mbyteDay =(byte)safeClockBean.getmClockOpenDay();
								if((mbyteDay&0x01)==0x01){
									sb.append(1);
								}
								if((mbyteDay&0x02)==0x02){
									sb.append(2);
								}
								if((mbyteDay&0x04)==0x04){
									sb.append(3);
								}
								if((mbyteDay&0x08)==0x08){
									sb.append(4);
								}
								if((mbyteDay&0x10)==0x10){
									sb.append(5);
								}
								if((mbyteDay&0x20)==0x20){
									sb.append(6);
								}
								if((mbyteDay&0x40)==0x40){
									sb.append(7);
								}
								
								
								sb.append(",");
								sb.append(String.format("%02x", (byte)safeClockBean.getmClockOpenopenFlag()));
								String sendphone = sb.toString();
								Log.d(TAG,"the send buffer is "+sendphone);
								new sendSafeClockdata(DELETE_MODEL_CLOCK,sendphone.getBytes()).cancel(true);//默认UTF8
								new sendSafeClockdata(DELETE_MODEL_CLOCK,sendphone.getBytes()).execute();//默认UTF8
								break;
							//删除
							case 1:
								
								
								break;
						}
					}
				});
				builder.setNegativeButton(R.string.csst_cancel, null);
				Dialog d = builder.show();
				d.setCanceledOnTouchOutside(true);
				return true;
			}
		});
	}

	@Override
	public void initWidgetListener() {
		mBackBtnListener = new BackBtnListener();
		mBtnDoneListener = new DoneBtnListener();
		mBtnAddActionListener = new AddActionBtnListener();
	}

	@Override
	public void addWidgetListener() {
		mBtnCancel.setOnClickListener(mBackBtnListener);
		mBtnDone.setOnClickListener(mBtnDoneListener);
		mbtnAddClockOpen.setOnClickListener(mBtnAddActionListener);
		
	}
	
	/**
	 * 返回事件
	 */
	private final void backEvent(){
		Intent intent = new Intent();
		intent.setClass(CsstSHSafeClockMain.this, CsstSHSaftListView.class);
		startActivity(intent);
		CsstSHSafeClockMain.this.finish();
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
			//更新数据库
			
			
			//finish本界面
			Intent intent = new Intent();
			intent.setClass(CsstSHSafeClockMain.this, CsstSHSaftListView.class);
			startActivity(intent);
			CsstSHSafeClockMain.this.finish();
			
		}
	}

	private final class AddActionBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(CsstSHSafeClockMain.this, CsstSHSafeClockAdd.class);
			startActivity(intent);
			
		}
	}
	
	@Override
	protected void onDestroy() {
		mDb.close();
		super.onDestroy();
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
	 
	 private final class sendSafeClockdata extends AsyncTask<Void, Void, Boolean>{
			byte[] param;
			byte cmd ;
			private Dialog mDialog = null;
			public sendSafeClockdata(byte cmd,byte[] param){
				//清空数据
				this.cmd = cmd;
				this.param = param;
			}
			
			@Override
			protected Boolean doInBackground(Void... params) {
				ServerItemModel sim = new ServerItemModel();
				String msgBuffer ="1111";
				msgBuffer = mMacAdress;
				//
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
					for(int n=0;n<length;n++){
						System.out.println(TAG+ " safeSendCmd phone contentbuf setting " + n + " value: " + contentBuf[n]);
					}
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
						return false;
					} else {
						byte[] getbuf = new byte[msg.getLength()] ;
						for(int i=0;i<msg.getLength();i++){
							System.out.println(" UdpProcess_ZQL_SAFE get the message the [  "+i+"]"+msg.getData()[i]);
							getbuf[i]= msg.getData()[i];
						}
						System.out.println("safeSendCmd suc ok ok ok  ok  ok the msg is "+new String(getbuf));
						System.out.println("safeSendCmd get messega finish ");
						//先停止原来来定时器 因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
						//停止发送命
					}
					
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				
				if(!result){
//					Toast.makeText(CsstSHSaftListView.this, getResources().getString(R.string.csst_safe_search_failtip), Toast.LENGTH_SHORT).show();
				}
				
			}
		}
	
	

}
