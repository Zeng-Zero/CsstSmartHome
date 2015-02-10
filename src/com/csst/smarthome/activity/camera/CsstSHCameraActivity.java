package com.csst.smarthome.activity.camera;

import java.util.ArrayList;
import java.util.List;

import vstc2.nativecaller.NativeCaller;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.camera.CameraConnectService.ICameraStateChangeListener;
import com.csst.smarthome.bean.CsstSHCameraBean;
import com.csst.smarthome.camera.BridgeService;
import com.csst.smarthome.camera.BridgeService.IpcamClientInterface;
import com.csst.smarthome.camera.ContentCommon;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHCameraTable;
import com.csst.smarthome.dao.CsstSHDataBase;

/**
 * 视频监控列表
 * @author liuyang
 */
public class CsstSHCameraActivity extends Activity implements
		ICsstSHInitialize, ICsstSHConstant, ICameraStateChangeListener {

	public static final String TAG = "CsstSHCameraActivity";
	
	public static final String STR_DID = "did";
	public static final String STR_MSG_PARAM = "msgparam";
	
	/** 添加Camera请求Code */
	public static final int ADD_CAMERA_REQUEST_CODE = 0x00;
	/** 修改Camera请求Code */
	public static final int UPDATE_CAMERA_REQUEST_CODE = 0x01;
	
	/** 返回按钮 */
	private Button mBtnCancel = null;
	/** 标题栏 */
	private TextView mTVTitle = null;
	/** 完成 */
	private Button mBtnDone = null;
	/** 添加Camera */
	private Button mBtnAddCamera = null;
	/** Camera列表 */
	private ListView mLVCameras = null;
	
	/** 添加Camera按钮监听器 */
	private AddCameraBtnListener mAddCameraBtnListener = null;
	/** 返回按钮监听器 */
	private BackBtnListener mBackBtnListener = null;
	/** 相机列表监听器 */
	private CameraListener mCameraListener = null;
	
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	/** Camera表 */
	private CsstSHCameraTable mCameraTable = null;
	/** 摄像机列表 */
	private CameraAdpter mAdpter = null;
	/** 摄像机列表 */
	private List<CasstSHCamera> mCameras = null;
	/** 连接服务 */
	private CameraConnectService mConnectService = null;
	/** 加载对话框 */
	private ProgressDialog mLoadDialog = null;
	/** 当前正在修改的Camera */
	private CasstSHCamera mCurModifyCamera = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		bindService(new Intent(this, CameraConnectService.class), connection, Context.BIND_AUTO_CREATE);
		mLoadDialog = ProgressDialog.show(this, "", getString(R.string.connecting), true, true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csst_camera_layout);
	}
	
	@Override
	public void initDataSource() {
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		// Camera表
		mCameraTable = CsstSHCameraTable.getInstance();
		mAdpter = new CameraAdpter();
	}

	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
		mTVTitle = (TextView) findViewById(R.id.mTVTitle);
		mBtnDone = (Button) findViewById(R.id.mBtnDone);
		mBtnAddCamera = (Button) findViewById(R.id.mBtnAddCamera);
		mLVCameras = (ListView) findViewById(R.id.mLVCameras);
	}

	@Override
	public void initWidgetState() {
		mTVTitle.setText(R.string.csst_Videomonitoring);
		mBtnDone.setVisibility(View.INVISIBLE);
		mLVCameras.setAdapter(mAdpter);
	}

	@Override
	public void initWidgetListener() {
		mBackBtnListener = new BackBtnListener();
		mAddCameraBtnListener = new AddCameraBtnListener();
		mCameraListener = new CameraListener();
	}

	@Override
	public void addWidgetListener() {
		mBtnCancel.setOnClickListener(mBackBtnListener);
		mBtnAddCamera.setOnClickListener(mAddCameraBtnListener);
		mLVCameras.setOnItemClickListener(mCameraListener);
		mLVCameras.setOnItemLongClickListener(mCameraListener);
	}
	
	@Override
	public void cameraState(String cameraUid, int cameraState) {
		mAdpter.notifyDataSetInvalidated();
	}
	
	/**
	 * 通知后台服务，Camera源变动
	 */
	private final void notifyCameraSourceChanged(){
		sendBroadcast(new Intent(CameraConnectService.CAMERA_SOURCE_CHANGED_ACTION));
	}
	
	private final void modifyAndSetCameraOption(final CsstSHCameraBean camera){
		AlertDialog.Builder builder = new AlertDialog.Builder(CsstSHCameraActivity.this);
		builder.setTitle(camera.getCameraName());
		builder.setItems(R.array.csst_device_modify_and_set, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					//设置
					case 0:
						Intent intent1 = new Intent(CsstSHCameraActivity.this, SettingWifiActivity.class);
						intent1.putExtra("camera", camera);
						startActivity(intent1);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
						break;
				
					// 修改
					case 1:
						Intent intent = new Intent(CsstSHCameraActivity.this, CsstSHAddCameraActivity.class);
						intent.putExtra("camera", camera);
						//startActivity(intent);
						startActivityForResult(intent, UPDATE_CAMERA_REQUEST_CODE);
						break;
					// 删除
					case 2:
						mCameraTable.delete(mDb, camera);
						mCameras = cameraInfoToView(CsstSHCameraActivity.this, mCameraTable.query(mDb));
						mAdpter.setCameras(mCameras);
						//通知后台服务，Camera源变动
						notifyCameraSourceChanged();
						break;
				}
			}
		});
		builder.setNegativeButton(R.string.csst_cancel, null);
		Dialog d = builder.show();
		d.setCanceledOnTouchOutside(true);
	}
	
	private final void modifyCameraOption(final CsstSHCameraBean camera){
		AlertDialog.Builder builder = new AlertDialog.Builder(CsstSHCameraActivity.this);
		builder.setTitle(camera.getCameraName());
		builder.setItems(R.array.csst_device_modify, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					// 修改
					case 0:
						Intent intent = new Intent(CsstSHCameraActivity.this, CsstSHAddCameraActivity.class);
						intent.putExtra("camera", camera);
						//startActivity(intent);
						startActivityForResult(intent, UPDATE_CAMERA_REQUEST_CODE);
						break;
					// 删除
					case 1:
						mCameraTable.delete(mDb, camera);
						mCameras = cameraInfoToView(CsstSHCameraActivity.this, mCameraTable.query(mDb));
						mAdpter.setCameras(mCameras);
						//通知后台服务，Camera源变动
						notifyCameraSourceChanged();
						break;
				}
			}
		});
		builder.setNegativeButton(R.string.csst_cancel, null);
		Dialog d = builder.show();
		d.setCanceledOnTouchOutside(true);
	}
	
	/**
	 * 加载Camera列表
	 */
	private final void loadCameraList(){
		mCameras = cameraInfoToView(CsstSHCameraActivity.this, mCameraTable.query(mDb));
		mAdpter.setCameras(mCameras);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case ADD_CAMERA_REQUEST_CODE:
				if (Activity.RESULT_OK == resultCode){
					loadCameraList();
					//通知后台服务，Camera源变动
					notifyCameraSourceChanged();
				}
				break;
				
			case UPDATE_CAMERA_REQUEST_CODE:
				if (Activity.RESULT_OK == resultCode && null != data){
					CsstSHCameraBean camerabean = (CsstSHCameraBean) data.getSerializableExtra("camerabean");
					if (null != camerabean){
						mCurModifyCamera.updateCameraBean(camerabean);
					}
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
				break;
		}
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
		mConnectService.setCameraStateChangeListener(null);
		unbindService(connection);
		super.onDestroy();
	}
	
	public static final List<CasstSHCamera> cameraInfoToView(Context context, List<CsstSHCameraBean> beans){
		List<CasstSHCamera> cameras = new ArrayList<CasstSHCamera>();
		if (null == beans){
			return cameras;
		}
		if (beans.isEmpty()){
			return cameras;
		}
		for (int i = 0; i < beans.size(); i++){
			cameras.add(new CasstSHCamera(context, beans.get(i)));
		}
		return cameras;
	}
	
	private final ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			
		}
		
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			mConnectService = ((CameraConnectService.ConnectServiceBinder) arg1).getCameraConnectService();
			mConnectService.setCameraStateChangeListener(CsstSHCameraActivity.this);
			initDataSource();
			initWidget();
			initWidgetState();
			initWidgetListener();
			addWidgetListener();
			loadCameraList();
			if (null != mLoadDialog && mLoadDialog.isShowing()){
				mLoadDialog.dismiss();
				mLoadDialog = null;
			}
		}
	};
	
	@SuppressLint("HandlerLeak")
	private Handler PPPPMsgHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bd = msg.getData();
			int msgParam = bd.getInt(STR_MSG_PARAM);
			int msgType = msg.what;
			String did = bd.getString(STR_DID);
			CasstSHCamera camera = (CasstSHCamera) msg.obj;
			switch (msgType) {
				case ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS:
					camera.setCameraState(msgParam);
					mConnectService.setCameraState(did, msgParam);
					//在线
					if (msgParam == ContentCommon.PPPP_STATUS_ON_LINE) {
						NativeCaller.PPPPGetSystemParams(did,
								ContentCommon.MSG_TYPE_GET_PARAMS);
					}
					if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID//ID号无效
							|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED//连接失败
							|| msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE//摄像机不在线
							|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT//连接超时
							//密码错误
							|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
						NativeCaller.StopPPPP(did);
					}
					break;
	
				default:
					break;
			}
		}
	};
	
	/**
	 * camera监听器
	 * @author liuyang
	 */
	private final class CameraListener implements OnItemClickListener, OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			CasstSHCamera ci = (CasstSHCamera) view.getTag();
			mCurModifyCamera = ci;
			CsstSHCameraBean camera = mCameraTable.query(mDb, ci.getCameraBean().getCameraId());
			if (!mConnectService.cameraIsOnLine(ci.getCameraBean().getCameraUuid())){
				modifyCameraOption(camera);
			}else{
				modifyAndSetCameraOption(camera);
			}
			return true;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			final CasstSHCamera ci = (CasstSHCamera) view.getTag();
			new Thread(new Runnable() {
				@Override
				public void run() {
					//CsstSHCameraBean camera = ci.getCameraBean();
					//数据库中重新拉取Camera数据
					CsstSHCameraBean camera = mCameraTable.query(mDb, ci.getCameraBean().getCameraId());
					//camera不在线，停止camera
					if (!mConnectService.cameraIsOnLine(ci.getCameraBean().getCameraUuid())){
						//停止Camera连接
						NativeCaller.StopPPPP(ci.getCameraBean().getCameraUuid());
						//搜索回调
						BridgeService.setIpcamClientInterface(new ConnectCallback(ci));
						new StartPPPPThread(camera).start();
						return;
					}
					//进入预览
					Intent intent = new Intent(CsstSHCameraActivity.this, PlayActivity.class);
					intent.putExtra("camera", camera);
					startActivity(intent);
				}
			}).start();
		}
	}
	
	private final class ConnectCallback implements IpcamClientInterface{

		private CasstSHCamera camera = null;
		
		public ConnectCallback(CasstSHCamera camera) {
			this.camera = camera;
		}

		@Override
		public void BSMsgNotifyData(String did, int type, int param) {
			Bundle bd = new Bundle();
			Message msg = PPPPMsgHandler.obtainMessage();
			msg.what = type;
			msg.obj = camera;
			bd.putInt(STR_MSG_PARAM, param);
			bd.putString(STR_DID, did);
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
				Log.i("ip", "result:"+result);
			} catch (Exception e) {

			}
		}
	}
	
	/**
	 * 添加摄像头按钮
	 * @author liuyang
	 */
	private final class AddCameraBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			Intent inent = new Intent(CsstSHCameraActivity.this, CsstSHAddCameraActivity.class);
			//startActivity(inent);
			startActivityForResult(inent, ADD_CAMERA_REQUEST_CODE);
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
	 * 摄像头列表
	 * @author liuyang
	 */
	protected final class CameraAdpter extends BaseAdapter{

		private List<CasstSHCamera> mCameras = null;
		
		public CameraAdpter() {
		}
		
		public CameraAdpter(List<CasstSHCamera> cameras) {
			this.mCameras = cameras;
		}

		public void setCameras(List<CasstSHCamera> cameras) {
			this.mCameras = cameras;
			notifyDataSetInvalidated();
		}

		@Override
		public int getCount() {
			return mCameras == null ? 0 : mCameras.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null != mConnectService){
				mCameras.get(position).setCameraState(mConnectService.cameraState(mCameras.get(position).getCameraBean().getCameraUuid()));
			}
			return mCameras.get(position).getCameraView();
		}
		
	}

}
