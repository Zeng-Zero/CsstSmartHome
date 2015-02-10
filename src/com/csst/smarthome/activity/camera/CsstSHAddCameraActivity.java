package com.csst.smarthome.activity.camera;

import java.util.Map;
import vstc2.nativecaller.NativeCaller;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHCameraBean;
import com.csst.smarthome.camera.BridgeService;
import com.csst.smarthome.camera.BridgeService.AddCameraInterface;
import com.csst.smarthome.camera.ContentCommon;
import com.csst.smarthome.camera.SearchListAdapter;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHCameraTable;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.util.CsstContextUtil;
import com.zbar.lib.CaptureActivity;

/**
 * 添加视频
 * @author liuyang
 */
public class CsstSHAddCameraActivity extends Activity implements
		ICsstSHInitialize, ICsstSHConstant {

	public static final String TAG = "CsstSHAddCameraActivity";
	
	/** 扫描设备id请求 */
	private static final int SCAN_UUID_REQUEST = 0x01;
	/** 搜索时间 */
	private static final int SEARCH_TIME = 3000;
	
	/** 更新搜索结果命令 */
	private static final int UPDAE_SEARCH_CMD = 0x00;
	
	private Button mBtnCancel = null;
	private TextView mTVTitle = null;
	private Button mBtnDone = null;
	private EditText mETName = null;
	private EditText mETDId = null;
	private EditText mETUser = null;
	private EditText mETPassword = null;
	private CheckBox mCBShowPawd = null;
	private Button mScanCamera = null;
	private Button mSerchCamera = null;
	
	private BackBtnListener mBackBtnListener = null;
	private DoneBtnListener mDoneBtnListener = null;
	private SearchBtnListener mSearchBtnListener = null;
	private ScanBtnListener mScanBtnListener = null;
	private ShowBtnListener mShowBtnListener = null;
	private SearchResultCallback mSearchResultCallback = null;
	
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	/** Camera表 */
	private CsstSHCameraTable mCameraTable = null;
	/** 修改摄像头 */
	private CsstSHCameraBean mModifyCamera = null;
	/** 搜索Camera列表 */
	private SearchListAdapter mAdapter = null;
	/** 搜索对话框 */
	private Dialog mSearchDialog = null;
	/** 取消搜索摄像头回调 */
	private CancelSearchListener mCancelSearchListener = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csst_add_camera_layout);
		initDataSource();
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
	}
	
	@Override
	public void initDataSource() {
		Intent intent = getIntent();
		if (null != intent){
			mModifyCamera = (CsstSHCameraBean) intent.getSerializableExtra("camera");
		}
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		// Camera表
		mCameraTable = CsstSHCameraTable.getInstance();
	}

	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
		mTVTitle = (TextView) findViewById(R.id.mTVTitle);
		mBtnDone = (Button) findViewById(R.id.mBtnDone);
		mETName = (EditText) findViewById(R.id.mETName);
		mETDId = (EditText) findViewById(R.id.mETDId);
		mETUser = (EditText) findViewById(R.id.mETUser);
		mETPassword = (EditText) findViewById(R.id.mETPassword);
		mCBShowPawd = (CheckBox) findViewById(R.id.mCBShowPawd);
		mScanCamera = (Button) findViewById(R.id.mScanCamera);
		mSerchCamera = (Button) findViewById(R.id.mSerchCamera);
	}

	@Override
	public void initWidgetState() {
		if (null != mModifyCamera){
			mTVTitle.setText(R.string.csst_modify_camera_title);
			mETDId.setEnabled(false);
			mScanCamera.setEnabled(false);
			mSerchCamera.setEnabled(false);
			mETName.setText(mModifyCamera.getCameraName());
			mETDId.setText(mModifyCamera.getCameraUuid());
			mETUser.setText(mModifyCamera.getCameraAccount());
			mETPassword.setText(mModifyCamera.getCameraPassword());
			
			mETName.setSelection(mETName.getText().toString().length());
			mETUser.setSelection(mETUser.getText().toString().length());
			mETPassword.setSelection(mETPassword.getText().toString().length());
		}else{
			mTVTitle.setText(R.string.csst_add_camera_title);
			mETDId.setEnabled(true);
			mScanCamera.setEnabled(true);
			mSerchCamera.setEnabled(true);
		}
	}
	
	@Override
	public void initWidgetListener() {
		mBackBtnListener = new BackBtnListener();
		mDoneBtnListener = new DoneBtnListener();
		mSearchBtnListener = new SearchBtnListener();
		mScanBtnListener = new ScanBtnListener();
		mShowBtnListener = new ShowBtnListener();
		mSearchResultCallback = new SearchResultCallback();
		mCancelSearchListener = new CancelSearchListener();
		mAdapter = new SearchListAdapter(this);
	}

	@Override
	public void addWidgetListener() {
		mBtnCancel.setOnClickListener(mBackBtnListener);
		mBtnDone.setOnClickListener(mDoneBtnListener);
		mScanCamera.setOnClickListener(mScanBtnListener);
		mSerchCamera.setOnClickListener(mSearchBtnListener);
		mCBShowPawd.setOnCheckedChangeListener(mShowBtnListener);
		CsstContextUtil.hideInputKeyBoard(this);
		BridgeService.setAddCameraInterface(mSearchResultCallback);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case SCAN_UUID_REQUEST:
				if (null == data){
					return;
				}
				String deviceUUID = data.getStringExtra("result");
				if (TextUtils.isEmpty(deviceUUID)){
					Toast.makeText(this, R.string.csst_device_id_scan_failed, Toast.LENGTH_LONG).show();
					return;
				}
				mETDId.setText(deviceUUID);
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
		setResult(Activity.RESULT_CANCELED);
		backEvent();
		super.onBackPressed();
	}
	
	/**
	 * 启动搜索
	 */
	private final void startSearchCamera(){
		mSearchDialog = CsstContextUtil.searchDialog(CsstSHAddCameraActivity.this, null, getString(R.string.csst_camera_search_message), mCancelSearchListener);
		mSearchDialog.show();
		mAdapter.ClearAll();
		new StartSearchThread().start();
	}
	
	/**
	 * 更新搜索Camera状态
	 * @param isCancel 是否是取消刷新
	 */
	private final void updateSearchResult(boolean isCancel){
		if (null != mSearchDialog){
			mSearchDialog.dismiss();
			mSearchDialog = null;
		}
		new StopSearchThread().start();
		if (mAdapter.getCount() > 0){
			mAdapter.notifyDataSetInvalidated();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.csst_camera_search_result);//搜索结果
			//刷新
			builder.setNegativeButton(R.string.csst_camera_refresh, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startSearchCamera();
				}
			});
			//取消
			builder.setPositiveButton(R.string.csst_cancel, null);
			//选择搜索到的摄像头
			builder.setAdapter(mAdapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Map<String, Object> mapItem = (Map<String, Object>) mAdapter.getItemContent(which);
					if (mapItem == null) {
						return;
					}
					String strName = (String) mapItem.get(ContentCommon.STR_CAMERA_NAME);
					String strDID = (String) mapItem.get(ContentCommon.STR_CAMERA_ID);
					String strUser = ContentCommon.DEFAULT_USER_NAME;
					String strPwd = ContentCommon.DEFAULT_USER_PWD;
					mETName.setText(strName);
					mETDId.setText(strDID);
					mETUser.setText(strUser);
					mETPassword.setText(strPwd);
				}
			});
			builder.show();
		}else{
			if (isCancel){
				return;
			}
			//没有发现摄像机设备,请重试...
			Toast.makeText(this, R.string.csst_camera_search_empty, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onPause() {
		if (null != mSearchDialog){
			mSearchDialog.dismiss();
			mSearchDialog = null;
		}
		new StopSearchThread().start();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case UPDAE_SEARCH_CMD:
					updateSearchResult(false);
					break;
			}
		}
	};
	
	/**
	 * 局域网搜索回调
	 * @author liuyang
	 */
	private final class SearchResultCallback implements AddCameraInterface{
		@Override
		public void callBackSearchResultData(int cameraType, String strMac,
				String strName, String strDeviceID, String strIpAddr, int port) {
			if (!mAdapter.AddCamera(strMac, strName, strDeviceID)) {
				return;
			}
		}
	}
	
	/**
	 * 显示密码按钮监听器
	 * @author liuyang
	 */
	private final class ShowBtnListener implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked){
				mETPassword.setInputType(InputType.TYPE_CLASS_TEXT);
			}else{
				mETPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
			mETPassword.setSelection(mETPassword.getText().toString().trim().length());
		}
	}
	
	/**
	 * 扫描设备按钮监听器
	 * @author liuyang
	 */
	private final class ScanBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent=new Intent(CsstSHAddCameraActivity.this, CaptureActivity.class);
			startActivityForResult(intent, SCAN_UUID_REQUEST);
		}
	}
	
	/**
	 * 搜索设备
	 * @author liuyang
	 */
	private final class SearchBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			startSearchCamera();
		}
	}
	
	/**
	 * 取消搜索摄像头回调
	 * @author liuyang
	 */
	private final class CancelSearchListener implements DialogInterface.OnClickListener{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			mSearchDialog = null;
			mHandler.removeMessages(UPDAE_SEARCH_CMD);
			updateSearchResult(true);
		}
	}
	
	/**
	 * 停止搜索线程
	 * @author liuyang
	 */
	private final class StopSearchThread extends Thread{
		@Override
		public void run() {
			NativeCaller.StopSearch();
		}
	}
	
	/**
	 * 启动搜索线程
	 * @author liuyang
	 */
	private final class StartSearchThread extends Thread{
		@Override
		public void run() {
			NativeCaller.StartSearch();
			//SEARCH_TIME时间后更新搜索结果
			mHandler.sendEmptyMessageDelayed(UPDAE_SEARCH_CMD, SEARCH_TIME);
		}
	}
	
	/**
	 * 完成按钮监听器
	 * @author liuyang
	 */
	private final class DoneBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			String name = mETName.getText().toString();
			String uuid = mETDId.getText().toString();
			String user = mETUser.getText().toString();
			String pswd = mETPassword.getText().toString();
			//摄像机名字不能为空
			if (TextUtils.isEmpty(name)){
				Toast.makeText(CsstSHAddCameraActivity.this, R.string.csst_camera_name_empty, Toast.LENGTH_LONG).show();
				return;
			}
			//uuid不能为空
			if (TextUtils.isEmpty(uuid)){
				Toast.makeText(CsstSHAddCameraActivity.this, R.string.csst_camera_uuid_empty, Toast.LENGTH_LONG).show();
				return;
			}
			//用户名不能为空
			if (TextUtils.isEmpty(user)){
				Toast.makeText(CsstSHAddCameraActivity.this, R.string.csst_camera_user_empty, Toast.LENGTH_LONG).show();
				return;
			}
			//密码不能为空
			if (TextUtils.isEmpty(pswd)){
				Toast.makeText(CsstSHAddCameraActivity.this, R.string.csst_camera_pswd_empty, Toast.LENGTH_LONG).show();
				return;
			}
			//摄像机名字是否存在
			if (mModifyCamera == null && mCameraTable.columnExists(mDb, CsstSHCameraTable.GEN_CAMERA_NAME, name)){
				Toast.makeText(CsstSHAddCameraActivity.this, R.string.csst_camera_name_exists, Toast.LENGTH_LONG).show();
				return;
			}
			//摄像机是否存在
			if (mModifyCamera == null && mCameraTable.columnExists(mDb, CsstSHCameraTable.GEN_CAMERA_UUID, uuid)){
				Toast.makeText(CsstSHAddCameraActivity.this, R.string.csst_camera_uuid_exists, Toast.LENGTH_LONG).show();
				return;
			}
			if (null == mModifyCamera){
				mCameraTable.insert(mDb, new CsstSHCameraBean(name, user, pswd, uuid));
			}else{
				mModifyCamera.setCameraName(name);
				mModifyCamera.setCameraAccount(user);
				mModifyCamera.setCameraPassword(pswd);
				mCameraTable.update(mDb, mModifyCamera);
			}
			//setResult(Activity.RESULT_OK);
			Intent intent = new Intent();
			intent.putExtra("camerabean", mModifyCamera);
			setResult(Activity.RESULT_OK, intent);
			backEvent();
		}
	}
	
	/**
	 * 返回按钮监听器
	 * @author liuyang
	 */
	private final class BackBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			setResult(Activity.RESULT_CANCELED);
			backEvent();
		}
	}

}
