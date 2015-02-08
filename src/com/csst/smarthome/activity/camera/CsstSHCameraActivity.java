package com.csst.smarthome.activity.camera;

import java.util.ArrayList;
import java.util.List;

import vstc2.nativecaller.NativeCaller;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHCameraBean;
import com.csst.smarthome.camera.BridgeService;
import com.csst.smarthome.camera.BridgeService.IpcamClientInterface;
import com.csst.smarthome.camera.ContentCommon;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHCameraTable;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.util.CsstContextUtil;

/**
 * 视频监控列表
 * @author liuyang
 */
public class CsstSHCameraActivity extends Activity implements
		ICsstSHInitialize, ICsstSHConstant {

	public static final String TAG = "CsstSHCameraActivity";
	private static final String STR_DID = "did";
	private static final String STR_MSG_PARAM = "msgparam";
	private Button mBtnCancel = null;
	private TextView mTVTitle = null;
	private Button mBtnDone = null;
	private Button mBtnAddCamera = null;
	private ListView mLVCameras = null;
	
	private AddCameraBtnListener mAddCameraBtnListener = null;
	private BackBtnListener mBackBtnListener = null;
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
	/** 是否搜索中 */
	private boolean isConnecting = false;
	/** 连接对话框 */
	private Dialog mConnectDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csst_camera_layout);
		initDataSource();
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
		mCameras = cameraInfoToView(CsstSHCameraActivity.this, mCameraTable.query(mDb));
		mAdpter.setCameras(mCameras);
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
	
	private final void modifyAndSetCameraOption(final CsstSHCameraBean camera){
		AlertDialog.Builder builder = new AlertDialog.Builder(CsstSHCameraActivity.this);
		builder.setTitle(camera.getCameraName());
		builder.setItems(R.array.csst_device_modify_and_set, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					//修改
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
						startActivity(intent);
						break;
					// 删除
					case 2:
						mCameraTable.delete(mDb, camera);
						mCameras = cameraInfoToView(CsstSHCameraActivity.this, mCameraTable.query(mDb));
						mAdpter.setCameras(mCameras);
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
						startActivity(intent);
						break;
					// 删除
					case 1:
						mCameraTable.delete(mDb, camera);
						mCameras = cameraInfoToView(CsstSHCameraActivity.this, mCameraTable.query(mDb));
						mAdpter.setCameras(mCameras);
						break;
				}
			}
		});
		builder.setNegativeButton(R.string.csst_cancel, null);
		Dialog d = builder.show();
		d.setCanceledOnTouchOutside(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
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
	
	private static final List<CasstSHCamera> cameraInfoToView(Context context, List<CsstSHCameraBean> beans){
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
					mConnectDialog.dismiss();
					isConnecting = false;
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
			if (isConnecting){
				Toast.makeText(CsstSHCameraActivity.this, R.string.csst_camera_connecting, Toast.LENGTH_LONG).show();
				return false;
			}
			CasstSHCamera ci = (CasstSHCamera) view.getTag();
			CsstSHCameraBean camera = ci.getCameraBean();
			if (!ci.isOnLine()){
				modifyCameraOption(camera);
			}else{
				modifyAndSetCameraOption(camera);
			}
			return false;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (isConnecting){
				Toast.makeText(CsstSHCameraActivity.this, R.string.csst_camera_connecting, Toast.LENGTH_LONG).show();
				return;
			}
			CasstSHCamera ci = (CasstSHCamera) view.getTag();
			CsstSHCameraBean camera = ci.getCameraBean();
			if (!ci.isOnLine()){
				Context context = CsstSHCameraActivity.this;
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(R.string.csst_camera_offline);
				builder.setNegativeButton(R.string.csst_cancel, null);
				builder.setPositiveButton(R.string.csst_ok, new StartConnectCallback(ci));
				builder.show();
				return;
			}
			//进入预览
			Intent intent = new Intent(CsstSHCameraActivity.this, PlayActivity.class);
			intent.putExtra("camera", camera);
			startActivity(intent);
		}
	}
	
	/**
	 * 启动连接按钮监听器
	 * @author liuyang
	 */
	private final class StartConnectCallback implements DialogInterface.OnClickListener{
		
		private CasstSHCamera camera = null;
		
		public StartConnectCallback(CasstSHCamera camera) {
			this.camera = camera;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			//搜索回调
			BridgeService.setIpcamClientInterface(new ConnectCallback(camera));
			new StartPPPPThread(camera.getCameraBean()).start();
			mConnectDialog = CsstContextUtil.searchDialog(CsstSHCameraActivity.this, null, null, null);
			mConnectDialog.show();
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
			/*if (type == ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS && param == 2) {
				//相机断线
			}*/
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
			this.cameraBean = cameraBean;
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
			startActivity(inent);
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
			return mCameras.get(position).getCameraView();
		}
		
	}

}
