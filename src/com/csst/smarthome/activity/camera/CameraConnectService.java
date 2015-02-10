package com.csst.smarthome.activity.camera;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import vstc2.nativecaller.NativeCaller;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.csst.smarthome.bean.CsstSHCameraBean;
import com.csst.smarthome.camera.BridgeService;
import com.csst.smarthome.camera.BridgeService.IpcamClientInterface;
import com.csst.smarthome.camera.ContentCommon;
import com.csst.smarthome.dao.CsstSHCameraTable;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.util.CsstSHNetUtil;

/**
 * Camera连接服务
 * @author Administrator
 */
public class CameraConnectService extends Service {
	
	/** Camera资源改变广播 */
	public static final String CAMERA_SOURCE_CHANGED_ACTION = "com.csst.smarthome.CAMERA_SOURCE_CHANGED_ACTION";
	/** 网络状态监听器 */
	private NetReceiver mNetReceiver = null;
	/** Binder对象 */
	private ConnectServiceBinder mConnectServiceBinder = null;
	/** Camera状态 */
	private Map<String, Integer> mCameraStateMap = null;
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	/** Camera表 */
	private CsstSHCameraTable mCameraTable = null;
	/** Camera列表 */
	private List<CasstSHCamera> mCameraList = null;
	/** Camera状态改变监听器 */
	private ICameraStateChangeListener mICameraStateChangeListener = null;
	/** 是否连接中 */
	private boolean isConnecting = false;
	
	@Override
	public void onCreate() {
		super.onCreate();
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		// Camera表
		mCameraTable = CsstSHCameraTable.getInstance();
		mCameraStateMap = new HashMap<String, Integer>();
		mConnectServiceBinder = new ConnectServiceBinder();
		//添加广播监听器
		addNetReceiver();
		//加载并连接Camera
		loadAndConnectCamera();
	}
	
	/**
	 * 是否连接Camera设备
	 * @return
	 */
	public final boolean isConnectingCameraDevice(){
		return isConnecting;
	}
	
	/**
	 * 设置Camera状态改变监听器
	 * @param listener
	 */
	public final void setCameraStateChangeListener(ICameraStateChangeListener listener){
		mICameraStateChangeListener = listener;
	}
	
	/**
	 * 设置Camera状态
	 * @param camerUid
	 * @param cameraState
	 */
	public final void setCameraState(String camerUid, int cameraState){
		mCameraStateMap.put(camerUid, Integer.valueOf(cameraState));
		if (null != mICameraStateChangeListener){
			mICameraStateChangeListener.cameraState(camerUid, cameraState);
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mConnectServiceBinder;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		new Thread(new Runnable() {
			@Override
			public void run() {
				releaseCameraConnect();
			}
		}).start();
		csstSHDataBase.closeDatabase();
		mConnectServiceBinder = null;
		unregisterNetReceiver();
	}
	
	/**
	 * 指定Camera是否在线
	 * @param uuid
	 * @return
	 */
	public boolean cameraIsOnLine(String uuid){
		if (TextUtils.isEmpty(uuid)){
			return false;
		}
		if (null == mCameraStateMap || mCameraStateMap.isEmpty()){
			return false;
		}
		if (null == mCameraStateMap.get(uuid)){
			mCameraStateMap.put(uuid, ContentCommon.PPPP_STATUS_UNKNOWN);
			return false;
		}
		return (mCameraStateMap.get(uuid) == ContentCommon.PPPP_STATUS_ON_LINE);
	}
	
	/**
	 * Camera状态
	 * @param uuid
	 * @return
	 */
	public final int cameraState(String uuid){
		if (TextUtils.isEmpty(uuid)){
			return ContentCommon.PPPP_STATUS_UNKNOWN;
		}
		if (null == mCameraStateMap || mCameraStateMap.isEmpty()){
			return ContentCommon.PPPP_STATUS_UNKNOWN;
		}
		if (!mCameraStateMap.containsKey(uuid)){
			return ContentCommon.PPPP_STATUS_UNKNOWN;
		}
		return mCameraStateMap.get(uuid);
	}
	
	/**
	 * 释放Camera连接
	 */
	private final void releaseCameraConnect(){
		try {
			if (null != mCameraStateMap && !mCameraStateMap.isEmpty() && CsstSHNetUtil.isNetConnected(CameraConnectService.this)){
				//查询Camera列表
				Set<String> cameraUids = mCameraStateMap.keySet();
				if (null != cameraUids && !cameraUids.isEmpty()){
					for (String uuid : cameraUids){
						if (null != uuid){
							//断开Camera
							NativeCaller.StopPPPP(uuid);
							mCameraStateMap.remove(uuid);
						}
					}
				}
			}
			isConnecting = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 释放Camera连接
	 * @param uuid
	 */
	public static final void releaseCameraConnect(final String uuid){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//断开Camera
					NativeCaller.StopPPPP(uuid);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * 启动Camera连接服务
	 * @param context
	 */
	public static final void startCameraConnectService(Context context){
		Intent intent = new Intent(context, CameraConnectService.class);
		context.startService(intent);
	}

	/**
	 * 关闭Camera连接服务
	 * @param context
	 */
	public static final void stopCameraConnectService(Context context){
		Intent intent = new Intent(context, CameraConnectService.class);
		context.startService(intent);
	}
	
	/**
	 * 添加网络监听器
	 */
	private final void addNetReceiver(){
		try {
			mNetReceiver = new NetReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			registerReceiver(mNetReceiver, filter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 取消注册网络监听器
	 */
	private final void unregisterNetReceiver(){
		try {
			unregisterReceiver(mNetReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 加载Camera
	 */
	private final void loadAndConnectCamera(){
		new LoadCamertThead().start();
	}
	
	/**
	 * 连接Camera设备
	 */
	private final void connectCameraDevice(){
		if (null != mCameraList && !mCameraList.isEmpty()){
			new StartPPPPThread(mCameraList.remove(0).getCameraBean()).start();
		}else{
			isConnecting = false;
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler PPPPMsgHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bd = msg.getData();
			int msgParam = bd.getInt(CsstSHCameraActivity.STR_MSG_PARAM);
			int msgType = msg.what;
			String did = bd.getString(CsstSHCameraActivity.STR_DID);
			switch (msgType) {
				case ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS:
					//通知更新状态
					setCameraState(did, msgParam);
					//在线
					if (msgParam == ContentCommon.PPPP_STATUS_ON_LINE) {
						NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_PARAMS);
					}
					if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID//ID号无效
							|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED//连接失败
							|| msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE//摄像机不在线
							|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT//连接超时
							//密码错误
							|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
						NativeCaller.StopPPPP(did);
					}
					connectCameraDevice();
					break;
					
				default:
					break;
			}
			
		}
	};
	
	/**
	 * 连接回调接口
	 * @author liuyang
	 */
	private final class ConnectCallback implements IpcamClientInterface{

		@Override
		public void BSMsgNotifyData(String did, int type, int param) {
			Bundle bd = new Bundle();
			Message msg = PPPPMsgHandler.obtainMessage();
			msg.what = type;
			bd.putInt(CsstSHCameraActivity.STR_MSG_PARAM, param);
			bd.putString(CsstSHCameraActivity.STR_DID, did);
			msg.setData(bd);
			PPPPMsgHandler.sendMessage(msg);
		}

		@Override
		public void BSSnapshotNotify(String did, byte[] bImage, int len) {
			
		}

		@Override
		public void callBackUserParams(String did, String user1, String pwd1,
				String user2, String pwd2, String user3, String pwd3) {
			
		}

		@Override
		public void CameraStatus(String did, int status) {
			
		}
		
	}
	
	/**
	 * 连接Camera设备线程
	 * @author liuyang
	 */
	private class StartPPPPThread extends Thread {

		private CsstSHCameraBean cameraBean = null;

		public StartPPPPThread(CsstSHCameraBean cameraBean) {
			this.cameraBean = mCameraTable.query(mDb, cameraBean.getCameraId());
		}

		@Override
		public void run() {
			try {
				NativeCaller.Init();
				Thread.sleep(200);
				int result = NativeCaller.StartPPPP(cameraBean.getCameraUuid(), cameraBean.getCameraAccount(), cameraBean.getCameraPassword());
				Log.i("ip", "result:" + result);
			} catch (Exception e) {

			}
		}
	}
	
	/**
	 * 网络状态监听器
	 * @author liuyang
	 */
	private final class NetReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
				//网络不可用
				if (!CsstSHNetUtil.isNetConnected(context)){
					//设置Camera为断线
					if (null != mCameraStateMap && !mCameraStateMap.isEmpty()){
						//查询Camera列表
						Set<String> cameraUids = mCameraStateMap.keySet();
						if (null != cameraUids && !cameraUids.isEmpty()){
							for (String uuid : cameraUids){
								if (null != uuid){
									//断开Camera
									mCameraStateMap.put(uuid, ContentCommon.PPPP_STATUS_DISCONNECT);
								}
							}
						}
					}
					isConnecting = false;
				}else{
					//网络可用
					new Thread(new Runnable() {
						@Override
						public void run() {
							//为了能正常连接，先断开网络连接
							releaseCameraConnect();
							//启动连接
							loadAndConnectCamera();
						}
					}).start();
				}
			}
			//camera列表改动
			if (CAMERA_SOURCE_CHANGED_ACTION.equals(action)){
				new Thread(new Runnable() {
					@Override
					public void run() {
						//为了能正常连接，先断开网络连接
						releaseCameraConnect();
						if (null != mCameraStateMap){
							mCameraStateMap.clear();
						}else{
							mCameraStateMap = new HashMap<String, Integer>();
						}
						loadAndConnectCamera();
					}
				}).start();
			}
		}
	}
	
	/**
	 * 服务绑定类
	 * @author liuyang
	 */
	public final class ConnectServiceBinder extends Binder{
		public final CameraConnectService getCameraConnectService(){
			return CameraConnectService.this;
		}
	}

	/**
	 * Camera状态改变监听器
	 * @author liuyang
	 */
	public static interface ICameraStateChangeListener{
		void cameraState(String cameraUid, int cameraState);
	}
	
	/**
	 * 加载Camera
	 * @author liuyang
	 */
	private final class LoadCamertThead extends Thread{
		@Override
		public void run() {
			//网络不可用返回，结束线程
			if (!CsstSHNetUtil.isNetConnected(CameraConnectService.this)){
				return;
			}
			//查询Camera列表
			mCameraList = CsstSHCameraActivity.cameraInfoToView(CameraConnectService.this, mCameraTable.query(mDb));
			if (null == mCameraList || mCameraList.isEmpty()){
				//没有Camera，返回结束线程
				return;
			}
			isConnecting = true;
			//设置连接回调接口
			BridgeService.setIpcamClientInterface(new ConnectCallback());
			connectCameraDevice();
		}
	}
	
}
