package com.csst.smarthome.activity.device;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHDRCBean;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.bean.CsstSHRCKeyBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHDeviceRCKeyTable;
import com.csst.smarthome.dao.CsstSHDeviceTable;
import com.csst.smarthome.dao.CsstSHRCTemplateKeyTable;
import com.csst.smarthome.util.CsstContextUtil;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.csst.smarthome.util.CsstSHImageData;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.encryption.BaseCodeMessage;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.net.UdpJavaEncodingProcess;
import com.zbar.lib.CaptureActivity;

/**
 * 添加设备
 * @author liuyang
 */
public class CsstSHAddDeviceActivity extends Activity implements ICsstSHInitialize, ICsstSHConstant{

	public static final String TAG = "CsstSHAddDeviceActivity";
	
	/** 通过专辑获得封面 */
	private static final int GET_ICON_FROM_ALBUM = 0x00;
	/** 剪切获得图片 */
	private static final int GET_ICON_FROM_CROP = 0x01;
	/** 通过拍照获得封面 */
	private static final int GET_ICON_FROM_TAKE = 0x02;
	/** 扫描设备id请求 */
	private static final int SCAN_UUID_REQUEST = 0x03;
	
	/** 返回按钮 */
	private Button mBtnCancel = null;
	/** 完成按钮 */
	private Button mBtnDone = null;
	/** 设备封面 */
	private ImageView mDeviceIcon = null;
	/** 设备名字 */
	private EditText mDeviceName = null;
	/** 遥控模板 */
	private Spinner mDeviceRControl = null;
	/** 设备id */
	private EditText mDeviceId = null;
	/** 扫描条码 */
	private Button mScanId = null;
	/** 网络搜索 */
	private Button mSerchId = null;
	
	/** 返回按钮监听器 */
	private BackBtnListener mBackBtnListener = null;
	/** 完成按钮监听器 */
	private DoneBtnListener mDoneBtnListener = null;
	/** 设备封面监听器 */
	private DeviceIconListener mDeviceIconListener = null;
	/** 扫描设备监听器 */
	private ScanBtnListener mScanBtnListener = null;
	/** 搜索设备监听器 */
	private SearchBtnListener mSearchBtnListener = null;
	/** 内置图片监听器 */
	private PerSertItemListener mPerSertItemListener = null;
	/** 图片添加控件 */
	private CsstSHAddIconWindow mCsstSHAddIconWindow = null;
	/** 内置图片窗体 */
	private CsstSHAddIconWindow mPerSertWindow = null;
	/** 内置 */
	private PerSertListener mPerSertListener = null;
	/** 拍照 */
	private TackListener mTackListener = null;
	/** 专辑 */
	private AlbumListener mAlbumListener = null;
	/** 遥控器类型监听器 */
	private RemoteControlListener mRemoteControlListener = null;
	/** 遥控器列表 */
	private ArrayAdapter<String> rControlTypesAdapter = null;
	/** 设备封面图片临时文件 */
	private File mDeviceIconTempFile = null;
	/** 设备封面图片文件路径 */
	private String mDeviceIconPath = null;
	
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	/** 房间id */
	private int mRoomId = -1;
	/** 设备封面内置图片 */
	private boolean mDeviceIconPersert = false;
	/** 搜索的设备 */
	private int mSearchedDevice = 0;
	/** 遥控器id列表 */
	private int[] mRCIDs = null;
	/** 遥控器类型 */
	private int mRCId = 1;
	
	/** 修改设备对象 */
	private CsstSHDeviceBean mModifyTargetDevice = null;
	
	//debug  
	byte mcmd = 0x01;
	byte scmd =0x01;
	
	
	boolean sendflag = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sendflag = true;
		setContentView(R.layout.csst_adddevice_layout);
		initDataSource();
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case GET_ICON_FROM_TAKE:
				if (RESULT_OK == resultCode){
					//剪切拍的照片
					CsstSHImageData.cropDeviceIconPhoto(this, Uri.fromFile(mDeviceIconTempFile), GET_ICON_FROM_CROP);
				}
				break;
	
			case GET_ICON_FROM_CROP:
				if (null != data){
					Bundle extras = data.getExtras();
					Bitmap source = extras.getParcelable("data");
					mDeviceIconPath = CsstSHImageData.zoomIconTempFile().getPath();
					//缩放图片
					source = CsstSHImageData.zoomBitmap(source, mDeviceIconPath);
					//更新设备封面图片
					mDeviceIcon.setImageBitmap(source);
				}
				break;
				
			case GET_ICON_FROM_ALBUM:
				if (resultCode == RESULT_OK){
					Uri uri = data.getData();
					//剪切拍的照片
					CsstSHImageData.cropDeviceIconPhoto(this, uri, GET_ICON_FROM_CROP);
				}
				break;
				
			case SCAN_UUID_REQUEST:
				if (null == data){
					return;
				}
				String deviceUUID = data.getStringExtra("result");
				if (TextUtils.isEmpty(deviceUUID)){
					Toast.makeText(this, R.string.csst_device_id_scan_failed, Toast.LENGTH_LONG).show();
					return;
				}
				mDeviceId.setText(deviceUUID);
				break;
		}
	}
	
	@Override
	public void initDataSource() {
		if (null != getIntent()){
			Intent intent = getIntent();
			mModifyTargetDevice = (CsstSHDeviceBean) intent.getSerializableExtra("device");
			if (null != mModifyTargetDevice){
				mDeviceIconPersert = mModifyTargetDevice.isDeviceIconPersert();
				mDeviceIconPath = mModifyTargetDevice.getDeviceIconPath();
				mSearchedDevice = mModifyTargetDevice.isSearched();
			}
		}
		// 初始数据源
		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
		// 当前楼层对应的房间id
		mRoomId = configPreference.getRoomId();
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		// 遥控器类型id列表
		mRCIDs = getResources().getIntArray(R.array.csst_remote_control_value);
		// 设备封面临时文件
		mDeviceIconTempFile = CsstSHImageData.deviceIconTempFile();
		// 遥控器类型适配器
		String[] rcts = getResources().getStringArray(R.array.csst_remote_control_key);
		rControlTypesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rcts);
		rControlTypesAdapter.setDropDownViewResource(R.layout.csst_remote_control_drop_down_item);
	}

	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
		mBtnDone = (Button) findViewById(R.id.mBtnDone);
		mDeviceIcon = (ImageView) findViewById(R.id.mDeviceIcon);
		mDeviceName = (EditText) findViewById(R.id.mDeviceName);
		mDeviceRControl = (Spinner) findViewById(R.id.mDeviceRControl);
		mDeviceId = (EditText) findViewById(R.id.mDeviceId);
		mScanId = (Button) findViewById(R.id.mScanId);
		mSerchId = (Button) findViewById(R.id.mSerchId);
	}

	@Override
	public void initWidgetState() {
		mDeviceRControl.setAdapter(rControlTypesAdapter);
		//是否编辑设备
		if (null != mModifyTargetDevice){
			//设备图标是否是内置图标
			if (mModifyTargetDevice.isDeviceIconPersert()){
				mDeviceIcon.setImageResource(Integer.valueOf(mModifyTargetDevice.getDeviceIconPath()));
			}else{
				mDeviceIcon.setImageBitmap(BitmapFactory.decodeFile(mModifyTargetDevice.getDeviceIconPath()));
			}
			//设备名称
			mDeviceName.setText(mModifyTargetDevice.getDeviceName());
			//遥控器模板
			for (int i = 0; i < mRCIDs.length; i++){
				if (mRCIDs[i] == mModifyTargetDevice.getRCTypeId()){
					mDeviceRControl.setSelection(i);
					break;
				}
			}
			//绑定设备id
			mDeviceId.setText(mModifyTargetDevice.getDeviceSSID());
			
			//修改设备，遥控器和扫描按钮不可用
			mScanId.setEnabled(false);
			mDeviceRControl.setEnabled(false);
		}
	}

	@Override
	public void initWidgetListener() {
		mBackBtnListener = new BackBtnListener();
		mDoneBtnListener = new DoneBtnListener();
		mDeviceIconListener = new DeviceIconListener();
		mScanBtnListener = new ScanBtnListener();
		mSearchBtnListener = new SearchBtnListener();
		mPerSertListener = new PerSertListener();
		mTackListener = new TackListener();
		mAlbumListener = new AlbumListener();
		mRemoteControlListener = new RemoteControlListener();
		mPerSertItemListener = new PerSertItemListener();
	}

	@Override
	public void addWidgetListener() {
		mBtnCancel.setOnClickListener(mBackBtnListener);
		mBtnDone.setOnClickListener(mDoneBtnListener);
		mDeviceIcon.setOnClickListener(mDeviceIconListener);
		mScanId.setOnClickListener(mScanBtnListener);
		mSerchId.setOnClickListener(mSearchBtnListener);
		mDeviceRControl.setOnItemSelectedListener(mRemoteControlListener);
		CsstContextUtil.hideInputKeyBoard(this);
	}
	
	/**
	 * 返回事件
	 */
	private final void backEvent(){
		if (null != mModifyTargetDevice){
			setResult(RESULT_CANCELED);
		}
		sendflag = false;
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	if(keyCode==KeyEvent.KEYCODE_BACK){
		sendflag = false;
		backEvent();
	}
		 return true;
	}
	
	@Override
	public void onBackPressed() {
		backEvent();
		//super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		mDb.close();
		super.onDestroy();
	}
	
	/**
	 * 扫描设备按钮监听器
	 * @author liuyang
	 */
	private final class ScanBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent=new Intent(CsstSHAddDeviceActivity.this, CaptureActivity.class);
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
//			mSearchedDevice = true;
//			mScanId.setEnabled(false);
//			mDeviceRControl.setEnabled(false);
			System.out.println("zengqinglin  SearchBtnListener UpdateTempe \n");
			sendflag =true;
			new UpdateTempe().execute();
			
		}
	}
	
	
	

	
	/**
	 * 设备封面监听器
	 * @author liuyang
	 */
	private final class DeviceIconListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			mCsstSHAddIconWindow = new CsstSHAddIconWindow(CsstSHAddDeviceActivity.this, CsstSHAddDeviceActivity.this.findViewById(R.id.addDeviceContent));
			mCsstSHAddIconWindow.setPickPersetListener(mPerSertListener);
			mCsstSHAddIconWindow.setPickTackListener(mTackListener);
			mCsstSHAddIconWindow.setPickAlbumListener(mAlbumListener);
		}
	}
	
	/**
	 * 内置数据按钮监听器
	 * @author liuyang
	 */
	private final class PerSertListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			mDeviceIconPersert = true;
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View presetContainer = inflater.inflate(R.layout.csst_device_icon_prest_layout, null);
			LinearLayout presetIconLayout = (LinearLayout) presetContainer.findViewById(R.id.presetIconLayout);
			ImageView iconItem = null;
			for (int i = 0; i < PRESET_DEVICE_ICON.length; i++){
				iconItem = (ImageView) inflater.inflate(R.layout.csst_preset_icon_item, null);
				iconItem.setImageResource(PRESET_DEVICE_ICON[i]);
				iconItem.setId(PRESET_DEVICE_ICON[i]);
				iconItem.setOnClickListener(mPerSertItemListener);
				presetIconLayout.addView(iconItem);
			}
			mPerSertWindow = new CsstSHAddIconWindow(CsstSHAddDeviceActivity.this, CsstSHAddDeviceActivity.this.findViewById(R.id.addDeviceContent), presetContainer);
		}
	}
	
	/**
	 * 内置图片单机事件
	 * @author liuyang
	 */
	private final class PerSertItemListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			mDeviceIconPath = Integer.valueOf(v.getId()).toString();
			mDeviceIcon.setImageResource(v.getId());
			mPerSertWindow.dismiss();
		}
	}
	
	/**
	 * 拍照按钮监听器
	 * @author liuyang
	 */
	private final class TackListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			mDeviceIconPersert = false;
			CsstSHImageData.tackPhoto(CsstSHAddDeviceActivity.this, mDeviceIconTempFile, GET_ICON_FROM_TAKE);
		}
	}
	
	/**
	 * 专辑按钮监听器
	 * @author liuyang
	 */
	private final class AlbumListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			mDeviceIconPersert = false;
			CsstSHImageData.pickAlbum(CsstSHAddDeviceActivity.this, GET_ICON_FROM_ALBUM);
		}
	}
	
	/**
	 * 遥控器类型监听器
	 * @author liuyang
	 */
	private final class RemoteControlListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			mRCId = mRCIDs[position];
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			
		}
		
	}
	
	/**
	 * 完成按钮监听器
	 * @author liuyang
	 */
	private final class DoneBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			
			String deviceName = mDeviceName.getText().toString();
			boolean iconPersert = mDeviceIconPersert;
			int isSearchDevice = mSearchedDevice;
			String deviceIconPath = mDeviceIconPath;
			int rCTypeId = mRCId;
			String deviceSSID = mDeviceId.getText().toString();
			int roomId = mRoomId; 
			//设备名字为空返回
			if (TextUtils.isEmpty(deviceName)){
				//设备不能为空提示
				Toast.makeText(CsstSHAddDeviceActivity.this, R.string.csst_device_name_not_null, Toast.LENGTH_LONG).show();
				return;
			}
			//新建设备同一房间存在相同的设备返回
			if (null == mModifyTargetDevice && CsstSHDeviceTable.getInstance().roomExisSameDevice(mDb, deviceName, mRoomId)){
				Toast.makeText(CsstSHAddDeviceActivity.this, R.string.csst_home_exists_device, Toast.LENGTH_LONG).show();
				return;
			}
			//设备封面图片路径
			if (TextUtils.isEmpty(mDeviceIconPath)){
				Toast.makeText(CsstSHAddDeviceActivity.this, R.string.csst_device_icon_empty, Toast.LENGTH_LONG).show();
				return;
			}
			//不是内置图片拷贝到app中
			if (!iconPersert){
				CsstSHImageData.copyZoomToApp(mDeviceIconPath, CsstSHImageData.deviceIconFile(CsstSHAddDeviceActivity.this));
				new File(mDeviceIconPath).delete();
			}
			//添加设备
			if (mModifyTargetDevice == null){
				System.out.println("   the iconPersert "+iconPersert+"   the deviceIconPath "+deviceIconPath+"   the isSearchDevice "+isSearchDevice);
				CsstSHDeviceBean device = new CsstSHDeviceBean(deviceName, iconPersert, deviceIconPath, rCTypeId, deviceSSID, roomId, isSearchDevice);
				//插入设备id
				long deviceid = CsstSHDeviceTable.getInstance().insert(mDb, device);
				//查询该设备遥控器类型对应的按键表
				List<CsstSHRCKeyBean> templateKeys = CsstSHRCTemplateKeyTable.getInstance().queryByRCType(mDb, rCTypeId);
				//创建设备默认遥控器按键列表
				if (null != templateKeys && !templateKeys.isEmpty()){
					CsstSHRCKeyBean templateKey = null;
					CsstSHDRCBean drcBean = null;
					for (int i = 0; i < templateKeys.size(); i++){
						templateKey = templateKeys.get(i);
						int deviceId = (int) deviceid;
						String dRCCmdCode = null;
						String rCKeyName = templateKey.getRCKeyName();
						int rCKeySize = templateKey.getRCKeySize();
						int rCKeyX = templateKey.getRCKeyX();
						int rCKeyY = templateKey.getRCKeyY();
						int rCKeyW = templateKey.getRCKeyW();
						int rCKeyH = templateKey.getRCKeyH();
						int rCKeyIcon = templateKey.getRCKeyIcon();
						int rCKeyPage = templateKey.getRCKeyPage();
						int rCKeyIdentify = templateKey.getRCKeyIdentify();
						System.out.println(" here templateKeys.size() i "+i);
						
						drcBean = new CsstSHDRCBean(deviceId, dRCCmdCode, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify);
						CsstSHDeviceRCKeyTable.getInstance().insert(mDb, drcBean);
					}
				}
			}else{//修改设备
				mModifyTargetDevice.setDeviceName(deviceName);
				mModifyTargetDevice.setDeviceIconPersert(iconPersert);
				mModifyTargetDevice.setDeviceIconPath(deviceIconPath);
				mModifyTargetDevice.setRCTypeId(rCTypeId);
				mModifyTargetDevice.setDeviceSSID(deviceSSID);
				CsstSHDeviceTable.getInstance().update(mDb, mModifyTargetDevice);
				setResult(RESULT_OK);
				finish();
				return;
			}
			//返回主界面
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
			backEvent();
		}
	}
	
	

	private final class UpdateTempe extends AsyncTask<Void, Void, Boolean>{
		@Override
		protected Boolean doInBackground(Void... params) {
			int flag = 0;
			
			while(sendflag){
				System.out.println("zengqinglin  UpdateTempe \n");
				//设备的MAC地址
			long toid = 109860815673L;
			ServerItemModel sim = new ServerItemModel();
//			sim.setIpaddress(GobalDef.SERVER_URL);
//			sim.setIpaddress("172.27.167.3");
			//sim.setIpaddress("192.168.2.105");
			
//			sim.setIpaddress("255.255.255.255");
//			sim.setPort(80);
			
			
			
//			sim.setIpaddress("192.168.1.105");
//			sim.setPort(80);
			
			
			
			String msgBuffer = "109861926250";
			
			
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
			
//			//温湿度
//			bcm.setMCMD((byte) 4);
//			bcm.setSCMD((byte) 1);
			
			bcm.setMCMD((byte) 4);
			bcm.setSCMD((byte) 1);
			System.out.println(" the mcmd  is "+mcmd);
			//
//			byte[] contentBuf = {(byte) 5,(byte)1};
//			bcm.setContentBuf(contentBuf);
//			
			BaseCodeMessage bcmrcv = UdpJavaEncodingProcess.GetMsgReturn(bcm, sim);
			if(bcmrcv != null){
				//return true;
				if(bcmrcv.getContentBuf() != null){
					for(int i = 0; i<bcmrcv.getContentBuf().length; i++){
						System.out.println(TAG+ "content " + i + " value: " + bcmrcv.getContentBuf()[i]);
					}
				}
				System.out.println("9999999999999999999999999");
				//停止发送命令
				sendflag =false;
				
//				bcmrcv.getContentBuf().toString();
//				new String(bcmrcv.getContentBuf());
//				mDeviceName.setText(bcmrcv.getContentBuf().toString());
			}
			else{
				//return false;
				
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flag++;
			if(flag >=1000){
				break;
			}
			}
			return false;
			/*
			GetTemperatureReqMessage slrm = new GetTemperatureReqMessage();
			slrm.Direct = MessageDef.BASE_MSG_FT_REQ;
			slrm.setFromId(GobalDef.MOBILEID);
			slrm.MsgType = MessageDef.MESSAGE_TYPE_TEMPE_GET_REQ;
			
			slrm.Seq = MessageSeqFactory.GetNextSeq();
			slrm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
			slrm.ToType = MessageDef.BASE_MSG_FT_HUB;
			//slrm.setToId(tempetoid);
			GetTemperatureRspMessage msg = (GetTemperatureRspMessage) UdpProcess.GetMsgReturn(slrm, sim);
			if (null != msg){
				byte[] buffer = msg.GetTData();
				//
				if (null != buffer){
					
				}
				
				return true;
			}else{
				return false;
			}
			*/
		}
	}
	
	

}
