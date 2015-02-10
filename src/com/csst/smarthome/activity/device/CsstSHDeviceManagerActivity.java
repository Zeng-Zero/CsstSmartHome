package com.csst.smarthome.activity.device;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.SmartStartActivity;
import com.csst.smarthome.bean.CsstClockOpenBean;
import com.csst.smarthome.bean.CsstSHActionBean;
import com.csst.smarthome.bean.CsstSHCameraBean;
import com.csst.smarthome.bean.CsstSHDRCBean;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.bean.CsstSHFloolBean;
import com.csst.smarthome.bean.CsstSHModelBean;
import com.csst.smarthome.bean.CsstSHRoomBean;
import com.csst.smarthome.bean.CsstSHSafeSensorBean;
import com.csst.smarthome.bean.CsstSHSwitchBean;
import com.csst.smarthome.bean.CsstSafeClockBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHActionTable;
import com.csst.smarthome.dao.CsstSHCameraTable;
import com.csst.smarthome.dao.CsstSHClockOpenTable;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHDeviceRCKeyTable;
import com.csst.smarthome.dao.CsstSHDeviceTable;
import com.csst.smarthome.dao.CsstSHFloorTable;
import com.csst.smarthome.dao.CsstSHModelTable;
import com.csst.smarthome.dao.CsstSHRoomTable;
import com.csst.smarthome.dao.CsstSHSafeClockTable;
import com.csst.smarthome.dao.CsstSHSafeSensorTable;
import com.csst.smarthome.dao.CsstSHSwitchTable;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.encryption.BaseCodeMessage;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.net.BroadCast;
import com.lishate.net.UdpJavaEncodingProcess;

/**
 * 设备管理
 * @author liuyang
 */
public class CsstSHDeviceManagerActivity extends Activity implements ICsstSHInitialize, ICsstSHConstant {

	public static final String TAG = "CsstSHDeviceManagerActivity";
	private Button mBtnCancel = null;
	/** 中控配置 */
	private View midcenterset = null;
	/** 中控配置 */
	private View mdataoutput = null;
	/** 中控配置 */
	private View mdatainput = null;
	/** 中控配置 */
	private View mrunmanager = null;
	//甲醛校验
	private View jianquan = null;
	
	private BackBtnListener mBackBtnListener = null;
	private NetConfigListener mNetConfigListener = null;
	private boolean isContinue = true;
	private dataOutListener dataOutListener  = null;
	private dataInListener dataInListener =null;
	ProgressDialog myprogress;
	//定义获取字符串buffer 
	StringBuffer sbSendBuffer = new StringBuffer();
	
	private CsstSHDataBase csstSHDataBase = null;
	
	private final int CsstClockOpenBean = 110;
//	private final int CsstDeviceTypeBean = 11002;
	private final int CsstSHActionBean = 111;
	private final int CsstSHCameraBean = 112;
	private final int CsstSHDRCBean = 113;
	private final int CsstSHDeviceBean = 114;
//	private final int CsstSHDRCBean = 11007;
	private final int CsstSHFloolBean = 115;
	private final int CsstSHModelBean = 116;
//	private final int CsstSHRCKeyBean = 11010;
//	private final int CsstSHRCTypeBean = 11011;
	private final int CsstSHRoomBean = 117;
	private final int CsstSHSafeSensorBean = 118;
	private final int CsstSHSwitchBean = 119;
	private final int CsstSHSafeClockBean = 120;
	
	
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	/** 主控MAC地址 */
	private String mMacAdress = null;
	//buffer 数据
	public static String[] msgBuffer  = null;
	
	public static String[] msgBuffersend  = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csst_device_manager_layout);
		initDataSource();
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
	}
	
	@Override
	public void initDataSource() {
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
		mMacAdress = configPreference.getMacAdress();
	}
	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
		midcenterset = findViewById(R.id.midcenterset);
		mdataoutput = findViewById(R.id.dataExport);
		mdatainput = findViewById(R.id.dataInport);
		mrunmanager = findViewById(R.id.runmanager);
		jianquan =findViewById(R.id.jianquanjiaoyan);
	}

	@Override
	public void initWidgetState() {
		
	}

	@Override
	public void initWidgetListener() {
		mBackBtnListener = new BackBtnListener();
		//中控配置按钮监听器
		mNetConfigListener = new NetConfigListener();
		dataOutListener = new dataOutListener();
		dataInListener = new dataInListener();
		jianquan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new sendcmd((byte)0x03,null).cancel(true);//默认UTF8
				new sendcmd((byte)0x03,null).execute();//默认UTF8
			}
		}) ;
	}

	@Override
	public void addWidgetListener() {
		mBtnCancel.setOnClickListener(mBackBtnListener);
		midcenterset.setOnClickListener(mNetConfigListener);
		mdataoutput.setOnClickListener(dataOutListener);
		mdatainput.setOnClickListener(dataInListener);
		
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
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
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
	 * 数据输出
	 * @author liuyang
	 */
	private final class dataOutListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			BroadCastTask bct = new BroadCastTask();
			bct.execute(new Integer[]{0});
		}
	}
	
	
	/**
	 * 数据输出
	 * @author liuyang
	 */
	private final class dataInListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			BroadRecvTask brt = new BroadRecvTask();
			brt.execute(new Integer[]{0});
		}
	}
	
	
	
	/**
	 * 中控配置
	 * @author liuyang
	 */
	private final class NetConfigListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(CsstSHDeviceManagerActivity.this, CsstNetConfigActivity.class);
			startActivity(intent);
		}
	}
	//从接收的数据中解析数据并插入到数据库中
	//
	private void setMsgtoDataBase(String type){
		//以！号分组，分为四组 Floor 、model、cameara、sensor 
		//floor 内有房间 DEVICE  switch sensor
		//model 有 action clocktime
		//第一个 组数为类型，后面就是接着 每条数据 
		
		String[] msg  = null;
		//获取标签值
		msg = type.split("\\|");
		for(int o=0;o<msg.length;o++){
			Log.d(TAG,"the msg is "+msg[o]);
		}
		System.out.println("setMsgtoDataBase msg[0] "+msg[0]);
		switch (Integer.parseInt(msg[0])) {
		case CsstSHFloolBean:
			//从里面拿出数据 ,第一个数Floor本身的数据
			String[] msgroom  = null;
			int foolerId = 0;
			//获取标签值
			Log.d(TAG,"the msg[1] is "+msg[1]);
			msgroom = msg[1].split("\\[");
			//截取第一个数据位floor 的数据
			Log.d(TAG,"the msgroom[0] is "+msgroom[0]+"the msgroom.length is "+msgroom.length);
			CsstSHFloolBean bean = new CsstSHFloolBean(msgroom[0]);
			foolerId = (int)CsstSHFloorTable.getInstance().insert(mDb, bean);
			//再从后面的数据中解析出每个房间的数据
			for(int i=1;i<msgroom.length;i++){
			int roomId = 0;
			String[] msgdevice  = null;
			//截取Device数据
			msgdevice = msgroom[i].split("\\(");
//			//第一个数房间的数据
//			String[] msgroombean = null;
//			//解析出房间数
			Log.d(TAG,"the msgdevice.length is "+msgdevice.length);
			if(msgdevice.length==0){//如果没有device的话就直接加入数据
				CsstSHRoomBean roombean = new CsstSHRoomBean(msgroom[i],foolerId);
				roomId = (int)CsstSHRoomTable.getInstance().insert(mDb, roombean);
			}else{
				CsstSHRoomBean roombean = new CsstSHRoomBean(msgdevice[0],foolerId);
				roomId = (int)CsstSHRoomTable.getInstance().insert(mDb, roombean);
			}
			
			//从device 中获取 数据
			for(int j=1;j<msgdevice.length;j++){
				int deviceId = 0;
				String[] msgbean  = null;
				//截取Device分量数据有switch 和customrckey
				msgbean = msgdevice[j].split("\\;");
				Log.d(TAG,"the msgdevice.length is "+msgbean.length);
				//第一个Device数据
				String[] msgdevicebean = null;
				//解析出Device数据
				if(msgbean.length==0){//如果只有数据一条数据的话就直接解析了
					msgdevicebean =msgdevice[j].split("\\)");
				}else{
					msgdevicebean = msgbean[0].split("\\)");	
				}
				CsstSHDeviceBean shdevicebean = new CsstSHDeviceBean(msgdevicebean[0],Boolean.getBoolean(msgdevicebean[1]),msgdevicebean[2],
						Integer.parseInt(msgdevicebean[3]),msgdevicebean[4],roomId,Boolean.getBoolean(msgdevicebean[6])
						,Integer.parseInt(msgdevicebean[7]));
				deviceId = (int)CsstSHDeviceTable.getInstance().insert(mDb, shdevicebean);
				for(int m=1;m<msgbean.length;m++){
					String[] beanswitchorcustomkey  = null;
					//截取分量 判断数据是switch 或者customrckey
					Log.d(TAG,"the msgdevice["+m+"]"+" is "+msgbean[m]);
					beanswitchorcustomkey = msgbean[m].split("\\)");
					if(Integer.parseInt(beanswitchorcustomkey[0]) ==CsstSHSwitchBean){//判断是开关
						Log.d(TAG,"the msgdevice["+m+"]"+" is" +"CsstSHSwitchBean");
						CsstSHSwitchBean SwitchBean  = new CsstSHSwitchBean(beanswitchorcustomkey[1],beanswitchorcustomkey[2],beanswitchorcustomkey[3],Integer.parseInt(beanswitchorcustomkey[4]),
								deviceId);
						
						CsstSHSwitchTable.getInstance().insert(mDb, SwitchBean);
					}else if(Integer.parseInt(beanswitchorcustomkey[0]) ==CsstSHDRCBean){//判断自定义开关
						Log.d(TAG,"the msgdevice["+m+"]"+" is" +"CsstSHDRCBean");
						CsstSHDRCBean customkeyBean  = new CsstSHDRCBean(deviceId,beanswitchorcustomkey[2],beanswitchorcustomkey[3],Integer.parseInt(beanswitchorcustomkey[4]),Integer.parseInt(beanswitchorcustomkey[5]),
								Integer.parseInt(beanswitchorcustomkey[6]),Integer.parseInt(beanswitchorcustomkey[7]),Integer.parseInt(beanswitchorcustomkey[8]),Integer.parseInt(beanswitchorcustomkey[9]),Integer.parseInt(beanswitchorcustomkey[10]),Integer.parseInt(beanswitchorcustomkey[11]));
						CsstSHDeviceRCKeyTable.getInstance().insert(mDb, customkeyBean);
					}
					
				}
			}
		}
			break;
//		case CsstClockOpenBean:
//			//从里面拿出数据 数据是以,分开
//			for(int i=1;i<msg.length;i++){
//				String[] msgbean  = null;
//				msgbean = msg[i].split(",");
//				CsstClockOpenBean bean = new CsstClockOpenBean(msgbean[0],Integer.parseInt(msgbean[1]),
//						Integer.parseInt(msgbean[2]),Integer.parseInt(msgbean[3]),Integer.parseInt(msgbean[4]),Integer.parseInt(msgbean[5]));
//				CsstSHClockOpenTable.getInstance().insert(mDb, bean);
//			}
//			break;
//		case CsstSHActionBean:
//			//从里面拿出数据 数据是以,分开
//			for(int i=1;i<msg.length;i++){
//				String[] msgbean  = null;
//				msgbean = msg[i].split(",");
//				CsstSHActionBean bean = new CsstSHActionBean(msgbean[0],msgbean[1],
//						msgbean[2],Integer.parseInt(msgbean[3]),Integer.parseInt(msgbean[4]),Integer.parseInt(msgbean[5]));
//				CsstSHActionTable.getInstance().insert(mDb, bean);
//			}
//				break;
		case CsstSHSafeSensorBean:
			//从里面拿出数据 
			String[] msgsafesensor = null;
			//获取标签值
			msgsafesensor = msg[1].split("\\[");
			
			
			for(int i=0;i<msgsafesensor.length;i++){
				String[] msgsafesensorbean  = null;
				//获取Camera数据
				msgsafesensorbean = msgsafesensor[i].split("\\)");
				
				CsstSHSafeSensorBean safesensorbean = new CsstSHSafeSensorBean(msgsafesensorbean[0],Integer.parseInt(msgsafesensorbean[1]),Integer.parseInt(msgsafesensorbean[2]),
						Integer.parseInt(msgsafesensorbean[3]),Integer.parseInt(msgsafesensorbean[4]),Integer.parseInt(msgsafesensorbean[5]),Integer.parseInt(msgsafesensorbean[6]));
				CsstSHSafeSensorTable.getInstance().insert(mDb, safesensorbean);
			}
			break;
			
			
		case CsstSHSafeClockBean:
			//从里面拿出数据 
			String[] msgsafeclock = null;
			//获取标签值
			msgsafeclock = msg[1].split("\\[");
			
			
			for(int i=0;i<msgsafeclock.length;i++){
				String[] msgsafeclockbean  = null;
				//获取Camera数据
				msgsafeclockbean = msgsafeclock[i].split("\\)");
				
				CsstSafeClockBean safeclock = new CsstSafeClockBean(msgsafeclockbean[0],Integer.parseInt(msgsafeclockbean[1]),Integer.parseInt(msgsafeclockbean[2]),
						Integer.parseInt(msgsafeclockbean[3]),Integer.parseInt(msgsafeclockbean[4]),Integer.parseInt(msgsafeclockbean[5]));
				CsstSHSafeClockTable.getInstance().insert(mDb, safeclock);
			}
			break;
			
		case CsstSHCameraBean:
			//从里面拿出数据 
			String[] msgcamera  = null;
			//获取标签值
			msgcamera = msg[1].split("\\[");
			
			
			for(int i=0;i<msgcamera.length;i++){
				String[] msgcamerabean  = null;
				//获取Camera数据
				msgcamerabean = msgcamera[i].split("\\)");
				CsstSHCameraBean camerabean = new CsstSHCameraBean(msgcamerabean[0],msgcamerabean[1],msgcamerabean[2],msgcamerabean[3]);
				CsstSHCameraTable.getInstance().insert(mDb, camerabean);
			}
			break;
//		case CsstSHDRCBean:
//			//从里面拿出数据 数据是以,分开
//			for(int i=1;i<msg.length;i++){
//				String[] msgbean  = null;
//				msgbean = msg[i].split(",");
//				CsstSHDRCBean bean = new CsstSHDRCBean(msgbean[0],Integer.parseInt(msgbean[1]),msgbean[2],Integer.parseInt(msgbean[3]));
//				CsstSHCustomRCKeyTable.getInstance().insert(mDb, bean);
//			}
//			break;
//		case CsstSHDeviceBean:
//			//从里面拿出数据 数据是以,分开
//			for(int i=1;i<msg.length;i++){
//				String[] msgbean  = null;
//				msgbean = msg[i].split(",");
//				CsstSHDeviceBean bean = new CsstSHDeviceBean(msgbean[0],Boolean.getBoolean(msgbean[1]),msgbean[2],Integer.parseInt(msgbean[3]),msgbean[4],Integer.parseInt(msgbean[5]),Integer.parseInt(msgbean[6]));
//				CsstSHDeviceTable.getInstance().insert(mDb, bean);
//			}
//			break;
		
		case CsstSHModelBean:
			//从里面拿出数据 ,第一个数Floor本身的数据
			//!CsstSHModelBean[CsstSHActionBean
			String[] msgmodel = null;
			int modelId = 0;
			//获取标签值
			msgmodel = msg[1].split("\\[");
			//截取第一个数据位model 的数据
			CsstSHModelBean modelbean = new CsstSHModelBean(msgmodel[0]);
			modelId = (int)CsstSHModelTable.getInstance().insert(mDb, modelbean);
			//从model 中获取 数据
			for(int m=1;m<msgmodel.length;m++){
					String[] actionortimer  = null;
					//截取分量 判断数据是Action 或者定时器
					actionortimer = msgmodel[m].split("\\)");
					if(Integer.parseInt(actionortimer[0]) ==CsstSHActionBean){//判断是Action
						CsstSHActionBean actionBean = new CsstSHActionBean(actionortimer[1],actionortimer[2], actionortimer[3],
								Integer.parseInt(actionortimer[4]),modelId,Integer.parseInt(actionortimer[6]));
						CsstSHActionTable.getInstance().insert(mDb, actionBean);
					}else if(Integer.parseInt(actionortimer[0]) ==CsstClockOpenBean){//判断自定义定时器
						CsstClockOpenBean clockbean = new CsstClockOpenBean(actionortimer[0],Integer.parseInt(actionortimer[1]), Integer.parseInt(actionortimer[2]),
								Integer.parseInt(actionortimer[3]),modelId,Integer.parseInt(actionortimer[5]));
						CsstSHClockOpenTable.getInstance().insert(mDb, clockbean);
					}
				}
			break;
			default:
				break;
			}
		
	}
	//获取数据函数
	//数据组成 因为有数据的附属关系。需要构件一个数据表格来确定数据的结构
	//大组别用大括号分开组别分别用楼层、情景模式、Camera、SafeSensor四大类
	//在楼层中的房间用］包括，房间内包括Device用)囊括，在Device中又有Switch 和customkey用;隔开
	//如果是楼层的话!楼层标签|楼层1参数]房间1参数)Device1参数;Switch标签+参数或者是customkey标签+参数)Device2参数;Switch标签+参数或者customkey标签+参数]房间2参数)Device参数;Switch标签+参数或者是customkey标签+参数)Device2参数;Switch参数或者是customkey参数
	//如果是情景模式的话就是!情景模式标签|情景模式1参数【action1标签+参数或者是定时器1参数[action2标签+参数或者是定时器2参数
	//如果是safesensor的话!safesensor 标签{safesensor1参数{safesensor2参数
	//如果是cameare的话！camear标签{camera1参数{camera2参数
	private String getSendString(){
		int i =0;
		StringBuffer sb = new StringBuffer();
		//先获取MAC地址
		sb.append(mMacAdress);
		
		
		//楼层进行数据添加
		if(CsstSHFloorTable.getInstance().query(mDb)!=null){
			List<CsstSHFloolBean> list = CsstSHFloorTable.getInstance().query(mDb);
			for(i=0;i<list.size();i++){
				sb.append("!");
				//标签
				sb.append(CsstSHFloolBean+"|");
				//楼层参数
				sb.append(list.get(i).toString2());
				//每一楼层次对应的房间
				if(CsstSHFloorTable.getInstance().getFloorRooms(mDb,list.get(i).getFloorId())!=null){
					List<CsstSHRoomBean> listroom = CsstSHFloorTable.getInstance().getFloorRooms(mDb,list.get(i).getFloorId());
					for(int j=0;j<listroom.size();j++){
						sb.append("[");
						//房间参数
						sb.append(listroom.get(j).toString());
						//每个房间对应的Device 
						if(CsstSHRoomTable.getInstance().getRoomDevices(mDb, listroom.get(j).getRoomId())!=null){
							List<CsstSHDeviceBean> listdevice = CsstSHRoomTable.getInstance().getRoomDevices(mDb, listroom.get(j).getRoomId());
							
							for(int m=0;m<listdevice.size();m++){
								sb.append("(");
								//添加device参数
//								Log.d(TAG,"device:"+listdevice.get(m).toString2());
								sb.append(listdevice.get(m).toString2());
								if(CsstSHSwitchTable.getInstance().queryByDeviceId(mDb,listdevice.get(m).getDeviceId())!=null){
									sb.append(";");
									//添加switch参数
									//因为一个device只能对应一个swithch 面板开关是用来没变开关存储的名称等属性为了和整体配合
									//Switch标签
									
									sb.append(CsstSHSwitchBean+")");
									sb.append(CsstSHSwitchTable.getInstance().queryByDeviceId(mDb,listdevice.get(m).getDeviceId()).toString());
								}
								if(CsstSHDeviceRCKeyTable.getInstance().queryByDevice(mDb,listdevice.get(m).getDeviceId())!=null){
									List<CsstSHDRCBean> listcustomRckey = CsstSHDeviceRCKeyTable.getInstance().queryByDevice(mDb,listdevice.get(m).getDeviceId());
									for(int n=0;n<listcustomRckey.size();n++){
										sb.append(";");
										//customRckey标签
//										Log.d(TAG,"the customRcKEY IS DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
										sb.append(CsstSHDRCBean+")");
										//添加customRckey参数
										sb.append(listcustomRckey.get(n).toString2());
										Log.d(TAG,"CsstSHDeviceRCKeyTable:"+listcustomRckey.get(n).toString2());
									}
								}
							}
						}
					}
				}
				
			}
		}
		
		
		
		if(CsstSHModelTable.getInstance().query(mDb)!=null){
			List<CsstSHModelBean> listmodel = CsstSHModelTable.getInstance().query(mDb);
			
			for(i=0;i<listmodel.size();i++){
				sb.append("!");
				//情景模式标签
				sb.append(CsstSHModelBean+"|");
				//情景模式参数
				sb.append(listmodel.get(i).toString());
				if(CsstSHActionTable.getInstance().queryByModel(mDb, listmodel.get(i).getmodelId())!=null){
					List<CsstSHActionBean> listaction= CsstSHActionTable.getInstance().queryByModel(mDb, listmodel.get(i).getmodelId());
					for(int j=0;j<listaction.size();j++){
						sb.append("[");
						//标签
						sb.append(CsstSHActionBean+")");
						//添加customRckey参数
						sb.append(listaction.get(j).toString());
					}
				}
				if(CsstSHClockOpenTable.getInstance().queryByModel(mDb, listmodel.get(i).getmodelId())!=null){
					List<CsstClockOpenBean> listclock= CsstSHClockOpenTable.getInstance().queryByModel(mDb, listmodel.get(i).getmodelId());
					for(int j=0;j<listclock.size();j++){
						sb.append("[");
						//标签
						sb.append(CsstClockOpenBean+")");
						//添加customRckey参数
						sb.append(listclock.get(j).toString());
					}
					
				}
			}
		}
		
		//Camera
		if(CsstSHCameraTable.getInstance().query(mDb)!=null){
			List<CsstSHCameraBean> listcamere = CsstSHCameraTable.getInstance().query(mDb);
			sb.append("!");
			//情景模式标签
			sb.append(CsstSHCameraBean+"|");
			for(i=0;i<listcamere.size();i++){
				sb.append(listcamere.get(i).toString2());
				sb.append("[");
			}
		}

		//SafeSensorBean
		if(CsstSHSafeSensorTable.getInstance().query(mDb)!=null){
			List<CsstSHSafeSensorBean> list = CsstSHSafeSensorTable.getInstance().query(mDb);
			sb.append("!");
			//情景模式标签
			sb.append(CsstSHSafeSensorBean+"|");
			for(i=0;i<list.size();i++){
				sb.append(list.get(i).toString());
				sb.append("[");
			}
		}
		
		//SafeSensorBean
		if(CsstSHSafeClockTable.getInstance().query(mDb)!=null){
			List<CsstSafeClockBean> list = CsstSHSafeClockTable.getInstance().query(mDb);
			sb.append("!");
			//情景模式标签
			sb.append(CsstSHSafeClockBean+"|");
			for(i=0;i<list.size();i++){
				sb.append(list.get(i).toString());
				sb.append("[");
			}
		}
		
		
		
		return sb.toString();
	}

	class BroadCastTask extends AsyncTask<Integer, Integer, Integer>{

		//public boolean IsContinue = true;
		public Dialog dialog;
		public BroadCast bc = new BroadCast();
		@Override
		protected Integer doInBackground(Integer... arg0) {
			String tempresult = "888888888888888888888888888888888888";
			tempresult = getSendString();
			msgBuffersend = tempresult.split("!");
			for(int i=0;i<msgBuffersend.length;i++){
				Log.d(TAG, "the getSendString is "+msgBuffersend[i]);
			}
			byte[] buf = null;
			try {
				buf = tempresult.getBytes(GobalDef.STR_CODE);
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while(isContinue == true){
				Log.d(TAG, "IsContinue is: " + isContinue);
				bc.SendDevice(buf,1000);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(dialog != null){
				dialog.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new Dialog(CsstSHDeviceManagerActivity.this, R.style.exitdialog);
			dialog.setContentView(R.layout.alertdialog);
			dialog.show();
			isContinue = true;
			Button yes = (Button)dialog.findViewById(R.id.alertdialog_yes);
			//Button no = (Button)dialog.findViewById(R.id.exitdialog_no);
			yes.setText(R.string.no);
			TextView text = (TextView)dialog.findViewById(R.id.alertdialog_text);
			text.setText(R.string.renwu_more_outdata);
			yes.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					isContinue = false;
					if(dialog != null){
						dialog.dismiss();
					}
				}
				
			});
			
			
		}
	}
	
	
	class BroadRecvTask extends AsyncTask<Integer, Integer, Integer>{

		//public boolean IsContinue = true;
		public Dialog dialog;
		
		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			byte[] buf = new byte[1024];
			while(CsstSHDeviceManagerActivity.this.isContinue == true){
				DatagramSocket socket = null; 
				try{
			        socket = new DatagramSocket(GobalDef.BROADCAST_PORT);  
			        DatagramPacket pack = new DatagramPacket(buf,buf.length);
			        socket.setSoTimeout(40000);
			        socket.receive(pack);
			        byte[] tempbuf = new byte[pack.getLength()];
			        System.arraycopy(buf, 0, tempbuf, 0, pack.getLength());
			        String getmsg = new String(tempbuf,GobalDef.STR_CODE);
			        Log.d(TAG, " get the data is "+getmsg);

					msgBuffer = getmsg.split("!");
					isContinue= false;
					break;
					
					
	        	}
	        	catch(Exception e){
	        		Log.d(TAG, e.toString());
	        		return 0;
	        	}
	        	finally{
	        		if(socket != null){
	        			socket.close();
	        		}
	        	}
			}
			System.out.println("msgBuffer[0] set mac address "+msgBuffer[0]+"the setMsgtoDataBase is "+msgBuffer.length);
			//第一个大括号的数据位MAC地址
			configPreference.setMacAdress(msgBuffer[0]);
			//解析每段数据
			for(int i=1;i<msgBuffer.length;i++){
				Log.d(TAG, "the i is "+i+"  the msgBuffer[i] is "+msgBuffer[i]);
				setMsgtoDataBase(msgBuffer[i]);
			}
			return 1;
		
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result == 0){
				Toast.makeText(CsstSHDeviceManagerActivity.this, CsstSHDeviceManagerActivity.this.getString(R.string.renwu_more_recv_fail), Toast.LENGTH_SHORT).show();
			}
			else if(result == 1){
				Toast.makeText(CsstSHDeviceManagerActivity.this, CsstSHDeviceManagerActivity.this.getString(R.string.renwu_more_recv_sus), Toast.LENGTH_SHORT).show();
			
				Intent intent = new Intent();
				intent.setClass(CsstSHDeviceManagerActivity.this, SmartStartActivity.class);
				startActivity(intent);
				CsstSHDeviceManagerActivity.this.finish();
			}
			if(dialog != null){
				dialog.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			CsstSHDeviceManagerActivity.this.isContinue = true;
			dialog = new Dialog(CsstSHDeviceManagerActivity.this, R.style.exitdialog);
			dialog.setContentView(R.layout.alertdialog);
			dialog.show();
			
			Button yes = (Button)dialog.findViewById(R.id.alertdialog_yes);
			yes.setText(R.string.no);
			//Button no = (Button)dialog.findViewById(R.id.exitdialog_no);
			TextView text = (TextView)dialog.findViewById(R.id.alertdialog_text);
			text.setText(R.string.renwu_more_indata);
			yes.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					isContinue = false;
					if(dialog != null){
						dialog.dismiss();
					}
				}
				
			});
			
			
		}
		
		
	}
	
	
	//sendSensortoMain to 
	private final class sendcmd extends AsyncTask<Void, Void, Boolean>{
		byte cmd;
  		byte[] param;
  		public sendcmd(byte cmd, byte[] param){
  			this.cmd = cmd;
			this.param = param;
		}
		
  		@Override
  		protected void onPreExecute() {
  			super.onPreExecute();
  			LoadProcess_fisrt();
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
			contentBuf[0]=(byte)0x07;
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
  				Log.d(TAG,"sendSensortoMain get messega finish ");
  				return true;
  			}
  			else{
  				
  			}
			if(myprogress!=null)
				myprogress.cancel();
  			Log.d(TAG,"Updatesenddebugcmdmianban start to sleep for wait");
  			return true;
  		}

  		
  	}
	
	public void  LoadProcess_fisrt() {
		if(myprogress!=null){
			myprogress.cancel();
		}
		myprogress = new ProgressDialog(this);
		myprogress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		myprogress.setMessage(getResources().getString(R.string.csst_send_cmd));
		myprogress.setIndeterminate(false);
		myprogress.setCancelable(true);
		myprogress.show();
	}
    
	
	
	
	
}
