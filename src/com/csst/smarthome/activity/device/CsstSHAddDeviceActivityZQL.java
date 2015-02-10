package com.csst.smarthome.activity.device;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHDRCBean;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.bean.CsstSHRCKeyBean;
import com.csst.smarthome.bean.CsstSHSwitchBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHDeviceRCKeyTable;
import com.csst.smarthome.dao.CsstSHDeviceTable;
import com.csst.smarthome.dao.CsstSHRCTemplateKeyTable;
import com.csst.smarthome.dao.CsstSHSwitchTable;
import com.csst.smarthome.rc.CsstSHAddDeviceCloudMainView;
import com.csst.smarthome.rc.custom.CustomRemoteActivity;
import com.csst.smarthome.util.CsstContextUtil;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.csst.smarthome.util.CsstSHImageData;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.encryption.BaseCodeMessage;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.net.UdpJavaEncodingProcess;

/**
 * 添加设备
 * @author liuyang
 */
public class CsstSHAddDeviceActivityZQL extends Activity implements ICsstSHInitialize, ICsstSHConstant{

	public static final String TAG = "CsstSHAddDeviceActivity";
	private boolean debug = true;
	
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
	private EditText mDeviceName = null;
	/** 遥控模板 */
	private Spinner mDeviceRControl = null;
	/** 扫描条码 */
	private Button mResearch = null;
	/** 网络搜索 */
	private Button mBang = null;
	
	/** 扫描条码 */
	private Button mCloud = null;
	/** 网络搜索 */
	private Button mCustom = null;
	
	/** 返回按钮监听�?*/
	private BackBtnListener mBackBtnListener = null;
	/** 完成按钮监听�?*/
	private DoneBtnListener mDoneBtnListener = null;
	/** 扫描设备监听�?*/
	private CloudBtnListener mCloudBtnListener = null;
	/** 搜索设备监听�?*/
	private SearchBtnListener mSearchBtnListener = null;
	
	private customBtnListener  mcustombtnlistener=null;
	
	private bangBtnListener mbangBtnListener=null;
	/** 遥控器类型监听器 */
	private RemoteControlListener mRemoteControlListener = null;
	/** 内置图片监听�?*/
	/** 图片添加控件 */
	private CsstSHAddIconWindow mCsstSHAddIconWindow = null;
	/** 内置图片窗体 */
	private CsstSHAddIconWindow mPerSertWindow = null;
	/** 内置 */
	private ArrayAdapter<String> rControlTypesAdapter = null;
	//下来列表字符串
	String[] rcts =null;
	/** 设备封面图片临时文件 */
	private File mDeviceIconTempFile = null;
	/** 设备封面图片文件路径 */
	private String mDeviceIconPath = null;
	
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	/** 主控MAC地址 */
	private String mMacAdress = null;
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对�?*/
	private SQLiteDatabase mDb = null;
	/** 房间id */
	private int mRoomId = -1;
	/** 设备封面内置图片 */
	private boolean mDeviceIconPersert = false;
	/** 搜索的设�?*/
	private boolean mSearchedDevice = false;
	/** 遥控器id列表 */
	private int[] mRCIDs = null;
	/** 遥控器类�?*/
	private int mRCId = 1;
	
	/** 修改设备对象 */
	private CsstSHDeviceBean mModifyTargetDevice = null;
	
	//debug  
	byte mcmd = 0x01;
	byte scmd =0x01;
	byte[] contentBuftosend = {0};
	
	
// timer
	private final Timer timer=new Timer();
	private TimerTask tast;
	
//		 seek send cmd timer is here;
	private Timer timerconfigwifi;
	private TimerTask tastconfigwifi;
	private Handler handlerconfigwifi;
	
	byte contentBuffer[] = new byte[7];
	
	
	boolean sendflag = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sendflag = true;
		setContentView(R.layout.csst_adddevice_zql_layout);
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
					Toast.makeText(this, R.string.csst_device_id_scan_failed, Toast.LENGTH_SHORT).show();
					return;
				}
				break;
		}
	}
	
	@Override
	public void initDataSource() {
		// 初始数据�?
		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
		// 当前楼层对应的房间id
		mRoomId = configPreference.getRoomId();
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对�?
		mDb = csstSHDataBase.getWritDatabase();
		mMacAdress = configPreference.getMacAdress();
		if(debug){
			System.out.println("the mMacAdress is "+mMacAdress);
		}
		configwifitimer();

	}

	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
		mBtnDone = (Button) findViewById(R.id.mBtnDone);
		mDeviceName = (EditText) findViewById(R.id.mDeviceName);
		mDeviceRControl = (Spinner) findViewById(R.id.mDeviceRControl);
		mResearch = (Button) findViewById(R.id.mSerchId);
		mBang = (Button) findViewById(R.id.mbang);
		mCloud = (Button) findViewById(R.id.mcloud);
		mCustom = (Button) findViewById(R.id.mcustom);
	}

	@Override
	public void initWidgetState() {
		// 遥控器类型适配�?
		rcts= getResources().getStringArray(R.array.csst_remote_control_key);
		rControlTypesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rcts);
		rControlTypesAdapter.setDropDownViewResource(R.layout.csst_remote_control_drop_down_item);
		// 遥控器类型id列表
		mRCIDs = getResources().getIntArray(R.array.csst_remote_control_value);
		mDeviceRControl.setAdapter(rControlTypesAdapter);
		if(mRCId<5){
			mCloud.setEnabled(true);	
			mCloud.setTextColor(getResources().getColor(R.color.white));
		}
		mCloud.setEnabled(false);
		mBang.setEnabled(false);
		//不能输入
		mDeviceName.setFocusable(false);
		mDeviceName.setFocusableInTouchMode(false);
		

	}

	@Override
	public void initWidgetListener() {
		mBackBtnListener = new BackBtnListener();
		mDoneBtnListener = new DoneBtnListener();
		mCloudBtnListener = new CloudBtnListener();
		mbangBtnListener = new bangBtnListener();
		mSearchBtnListener = new SearchBtnListener();
		mcustombtnlistener = new customBtnListener();
		mRemoteControlListener =new RemoteControlListener();
	}

	@Override
	public void addWidgetListener() {
		mBtnCancel.setOnClickListener(mBackBtnListener);
		mBtnDone.setOnClickListener(mDoneBtnListener);
		mResearch.setOnClickListener(mSearchBtnListener);
		mCustom.setOnClickListener(mcustombtnlistener);
		mBang.setOnClickListener(mbangBtnListener);
		mCloud.setOnClickListener(mCloudBtnListener);
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
	protected void onResume() {
		initWidgetState();
		//开启温度查�?和开关状态查�?
		sendflag = true;
		System.out.println(TAG+"onResume");
		super.onResume();
	}
	@Override
	protected void onPause(){
		//开启温度查�?和开关状态查�?
		sendflag = false;
		System.out.println(TAG+"onPause");
		super.onPause();
	}
	protected void onRestart(){
		sendflag = true;
		System.out.println(TAG+"onRestart");
		super.onRestart();	
	}
	
	protected void onStop(){
		sendflag = false;
		System.out.println(TAG+"onStop");
		super.onStop();	
	}
	@Override
	protected void onDestroy() {
		sendflag = false;
		System.out.println(TAG+"onDestroy");
		mDb.close();
		super.onDestroy();
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
			System.out.println("the cRCID is "+mRCId+" the id is "+id);
			if(id>0&&id<5){
				mCloud.setEnabled(true);	
				mCloud.setTextColor(getResources().getColor(R.color.white));
			}else{
				mCloud.setEnabled(false);	
				mCloud.setTextColor(getResources().getColor(R.color.gray));
			}
			mDeviceName.setText(rcts[position]);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			
		}
		
	}
	
	
	public void bangTip()
	 {
			AlertDialog.Builder builder=new Builder(CsstSHAddDeviceActivityZQL.this);
			builder.setTitle(getResources().getString(R.string.dialognotice));
			builder.setMessage(getResources().getString(R.string.bangtip));
			builder.setPositiveButton(getResources().getString(R.string.csst_ok), new DialogInterface.OnClickListener()
			{  
				public void onClick(DialogInterface dialog, int which)
				{ 

					//如果不是指定模板中的遥控器就提示用户选中指定的遥控器
					//就添加遥控器
					if(0<mRCId&&mRCId<8){
						//用Device 中的search位来表示是否绑定插座，但是在主界面还是要以插座的形式进行查询状态
						//智能遥控
						int rCTypeId = mRCId;
						int roomId = mRoomId; 
						//利用最后一位search 来表示是否是绑定插座的遥控器 是绑定的就是真
						CsstSHDeviceBean device = new CsstSHDeviceBean(mDeviceName.getText().toString(), ((contentBuffer[5]&0x0F)!=0),getResources().getString(R.string.csst_adddevice_charge), rCTypeId, contentBuffer[2]+","+contentBuffer[3]+","+contentBuffer[5], roomId, 1);
						//插入设备id
						long deviceid = CsstSHDeviceTable.getInstance().insert(mDb, device);
						 
						System.out.println("the device issearch is "+CsstSHDeviceTable.getInstance().query(mDb, (int)deviceid).isSearched());
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
						
						backEvent();	
					}else{//如果没有选中相应的遥控器模板就提示用户选择遥控器模板才能绑定设备
						Toast.makeText(CsstSHAddDeviceActivityZQL.this, R.string.csst_adddevice_selectmodel, Toast.LENGTH_SHORT).show();
					}
					
//					sendflag=true;
//					System.out.println("customBtnListener is press here the value is "+(byte)Integer.parseInt(mDeviceName.getText().toString()));
//					byte cmd = (byte)Integer.parseInt(mDeviceName.getText().toString());
//					new Updatesenddebugcmdmianban(cmd).cancel(true);
//					new Updatesenddebugcmdmianban(cmd).execute();
				
				}
		 });  
				 builder.setNegativeButton(getResources().getString(R.string.csst_cancel), new DialogInterface.OnClickListener()
				 {  
					 public void onClick(DialogInterface dialog, int which) 
					 {   }	});  
			builder.create().show();
		}	
	
	/**
	 * 扫描设备按钮监听�?
	 * @author liuyang
	 */
	private final class bangBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			bangTip();
		}
	}
		

	/**
	 * 扫描设备按钮监听�?
	 * @author liuyang
	 */
	private final class customBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			
			Intent inent = new Intent(CsstSHAddDeviceActivityZQL.this, CustomRemoteActivity.class);
//			Intent inent = new Intent(CsstSHAddDeviceActivityZQL.this, CsstSHAddDeviceActivity.class);
			inent.putExtra("roomId", configPreference.readInteger("roomId"));
			startActivity(inent);
			CsstSHAddDeviceActivityZQL.this.finish();
			
		}
	}
			
			
	
	/**
	 * 扫描设备按钮监听�?
	 * @author liuyang
	 */
	private final class CloudBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			
			Intent inent = new Intent(CsstSHAddDeviceActivityZQL.this, CsstSHAddDeviceCloudMainView.class);
			startActivity(inent);
			CsstSHAddDeviceActivityZQL.this.finish();
//			sendflag=true;
//			System.out.println("customBtnListener is press here the value is "+(byte)Integer.parseInt(mDeviceName.getText().toString()));
//			byte cmd = (byte)Integer.parseInt(mDeviceName.getText().toString());
//			new Updatesenddebugchazuo(cmd).cancel(true);
//			new Updatesenddebugchazuo(cmd).execute();
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
//			mResearch.setEnabled(false);
//			mDeviceRControl.setEnabled(false);
			System.out.println("zengqinglin  SearchBtnListener UpdateTempe \n");
			sendflag =true;
			new Updatresearch().cancel(true);
			new Updatresearch().execute();
			
		}
	}
	
	/**
	 * 完成按钮监听�?
	 * @author liuyang
	 */
	private final class DoneBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			sendflag =false;
			System.out.println(" the done is press here  the mcmd is "+mcmd);
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ImageView iconItem = null;
			iconItem = (ImageView) inflater.inflate(R.layout.csst_preset_icon_item, null);
			iconItem.setImageResource(R.drawable.switch2);
			iconItem.setId(R.drawable.power1);
			
			
//			
//			int roomId = mRoomId; 
//			CsstSHDeviceBean device = new CsstSHDeviceBean(getResources().getString(R.string.csst_adddevice_switch_three), false,Integer.valueOf(iconItem.getId()).toString(), 3,12+","+23 , roomId, false);
//			//插入设备id
//			long deviceid = CsstSHDeviceTable.getInstance().insert(mDb, device);
		
			//新建设备同一房间存在相同的设备返回
			if (null == mModifyTargetDevice && CsstSHDeviceTable.getInstance().roomExisSameDevice(mDb, mDeviceName.getText().toString(), mRoomId)){
				Toast.makeText(CsstSHAddDeviceActivityZQL.this, R.string.csst_home_exists_device, Toast.LENGTH_SHORT).show();
				return;
			}
			//面板开关
			if(contentBuffer[4]==2){
				int roomId = mRoomId; 
				CsstSHDeviceBean device = new CsstSHDeviceBean(mDeviceName.getText().toString(), ((contentBuffer[5]&0x0F)!=0),getResources().getString(R.string.csst_adddevice_switch), (int)contentBuffer[2],contentBuffer[2]+","+contentBuffer[3]+","+contentBuffer[5] , roomId, 0);
//				CsstSHDeviceBean device = new CsstSHDeviceBean(mDeviceName.getText().toString(), ((contentBuffer[5]&0x0F)!=0),getResources().getString(R.string.csst_adddevice_switch), 2,contentBuffer[2]+","+contentBuffer[3]+","+contentBuffer[5] , roomId, false);
				//插入设备id
				long deviceid = CsstSHDeviceTable.getInstance().insert(mDb, device);
				//添加面板开关的同时并同时添加相应的Switch 表
				 CsstSHSwitchBean switchbean = new CsstSHSwitchBean(getResources().getString(R.string.csst_switch_diaodeng),getResources().getString(R.string.csst_switch_caodeng),getResources().getString(R.string.csst_switch_bideng),contentBuffer[5],(int)deviceid);
				 CsstSHSwitchTable.getInstance().insert(mDb, switchbean);
				 
			}else if(contentBuffer[4]==3){//插座
				int roomId = mRoomId; 
				CsstSHDeviceBean device = new CsstSHDeviceBean(mDeviceName.getText().toString(), ((contentBuffer[5]&0x0F)!=0),getResources().getString(R.string.csst_adddevice_charge), (int)contentBuffer[2], contentBuffer[2]+","+contentBuffer[3]+","+contentBuffer[5], roomId, 0);
				//插入设备id
				long deviceid = CsstSHDeviceTable.getInstance().insert(mDb, device);
			}else{//智能遥控
				int rCTypeId = mRCId;
				int roomId = mRoomId; 
//				System.out.println("   the iconPersert "+iconPersert+"   the deviceIconPath "+deviceIconPath+"   the isSearchDevice "+isSearchDevice);
//				CsstSHDeviceBean device = new CsstSHDeviceBean(mDeviceName.getText().toString(), false, null, rCTypeId, "123", roomId, false);
				//
				if(rCTypeId==0){
					Toast.makeText(CsstSHAddDeviceActivityZQL.this, R.string.csst_adddevice_selectmodel, Toast.LENGTH_SHORT).show();
					return;
				}
				CsstSHDeviceBean device = new CsstSHDeviceBean(mDeviceName.getText().toString(), false, null, rCTypeId, "123", roomId, 0);
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
			
				
				
				
				
			}
			
			
			backEvent();
			
		}
	}
	
	/**
	 * 返回按钮监听�?
	 * @author liuyang
	 */
	private final class BackBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			backEvent();
		}
	}
	
	 
    //seek 定时发�?
    private void startTimer(){  
        if (timerconfigwifi == null) {  
            timerconfigwifi = new Timer();  
        }  
  
        if (tastconfigwifi == null) {  
            tastconfigwifi = new TimerTask() {  
                @Override  
                public void run() {  
                    Message message=new Message();
			         message.what=handlerconfigwifi.hashCode();
			         handlerconfigwifi.sendMessage(message);
                      
                     
                }  
            };  
        }  
  
        if(timerconfigwifi != null && tastconfigwifi != null )  
            timerconfigwifi.schedule(tastconfigwifi,  300);  
  
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
				   //接收的的第五位为设备类型  1�?位无效，3位为面板开�?插座的位数，4位为uid�?位为设备类型,6无效�?
				   //恢复可输入状态
				   mDeviceName.setFocusableInTouchMode(true);
				   mDeviceName.setFocusable(true);
				   mDeviceName.requestFocus();
				   switch (contentBuffer[4]){
				   case 0:
					   
					   break;
				   case 1:
					   
					   break;
				   case 2:
					   //让下来才能失去选择
					   mDeviceRControl.setSelection(0);
					   mDeviceRControl.setEnabled(false);
					   if(contentBuffer[2]==1){
						   mDeviceName.setText(getResources().getString(R.string.csst_adddevice_switch_one));
					   }else if(contentBuffer[2]==2){
						   mDeviceName.setText(getResources().getString(R.string.csst_adddevice_switch_two));
					   }else if(contentBuffer[2]==3){
						   mDeviceName.setText(getResources().getString(R.string.csst_adddevice_switch_three));
					   }
					   break;
				   case 3:
					   mBang.setEnabled(true);
					   mBang.setTextColor(getResources().getColor(R.color.white));
					   if(contentBuffer[2]==0x05){//等于5是排插
						   mDeviceName.setText(getResources().getString(R.string.csst_adddevice_chargemany));
					   }else if(contentBuffer[2]==1){//等于1是插座 
						   mDeviceName.setText(getResources().getString(R.string.csst_adddevice_charge));
					   }
					   Toast.makeText(CsstSHAddDeviceActivityZQL.this,getResources().getString(R.string.csst_adddevice_chargetip), Toast.LENGTH_SHORT).show();
					   break;
					default:
						
						break;
				   }
					
				   stopTimer();
					
				  }
			    };
	}
	
	

	private final class Updatresearch extends AsyncTask<Void, Void, Boolean>{
		private Dialog mDialog = null;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//取消回调接口
			DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (Updatresearch.this.getStatus() != AsyncTask.Status.FINISHED){
						sendflag = false;
						Updatresearch.this.cancel(true);
					}
					//发送停止命�?
					Toast.makeText(CsstSHAddDeviceActivityZQL.this, R.string.csst_adddevice_research_finish, Toast.LENGTH_SHORT).show();
				}
			};
			mDialog =CsstContextUtil.searchDialog(CsstSHAddDeviceActivityZQL.this, null, getString(R.string.csst_adddevice_research_device_message), callback);
			mDialog.show();
		}

		
		@Override
		protected Boolean doInBackground(Void... params) {
			int flag = 0;
			
			while(sendflag){
				System.out.println("zengqinglin  UpdateTempe \n");
				//设备的MAC地址
			long toid = 109860815673L;
			ServerItemModel sim = new ServerItemModel();
			
			
//			String msgBuffer = "109861926250";
			//测试用的MAC地址
//			String msgBuffer = "109861926250";
			String msgBuffer ="1111";
			if(CsstSHUseDeviceActivity.msgBuffer!=null){
				msgBuffer=  CsstSHUseDeviceActivity.msgBuffer[0];
			}
//			//调试刘总的
//			msgBuffer ="109861926250";
			//调试刘总的
//			msgBuffer ="109861926226";
			msgBuffer = mMacAdress;
			
			
			sim.setIpaddress("218.244.129.177");
//			sim.setIpaddress("192.168.1.110");
			sim.setPort(GobalDef.SERVER_PORT);
			
			BaseCodeMessage bcm = new BaseCodeMessage();
			bcm.Direct = MessageDef.BASE_MSG_FT_REQ;
			bcm.setFromId(GobalDef.MOBILEID);
			bcm.Seq = MessageSeqFactory.GetNextSeq();
			bcm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
			bcm.ToType = MessageDef.BASE_MSG_FT_HUB;
			bcm.setToId(toid);
			
			
			bcm.setToId(Long.valueOf(msgBuffer));
			
//			//温湿�?
//			bcm.setMCMD((byte) 4);
//			bcm.setSCMD((byte) 1);
			
			bcm.setMCMD((byte) 4);//22
			bcm.setSCMD((byte) 1);//23
			System.out.println(" the mcmd  is "+mcmd);
			//
			byte[] contentBuf = {(byte) 5,(byte)1};
			bcm.setContentBuf(contentBuf);
//			
//			BaseCodeMessage bcmrcv = UdpJavaEncodingProcess.GetMsgReturn(bcm, sim);
			BaseCodeMessage bcmrcv = UdpJavaEncodingProcess.GetMsgReturnlongtime(bcm, sim);
			
			
			if(bcmrcv != null){
				//return true;
				if(bcmrcv.getContentBuf() != null){
					contentBuftosend = bcmrcv.getContentBuf();
					for(int i = 0; i<bcmrcv.getContentBuf().length; i++){
						
						contentBuffer[i]=bcmrcv.getContentBuf()[i];
						
						System.out.println(TAG+ "content " + i + " value: " + bcmrcv.getContentBuf()[i]);
						
					}
				}
				System.out.println("9999999999999999999999999");
				//停止发送命
				sendflag =false;
				startTimer();
				return true;
//				bcmrcv.getContentBuf().toString();
//				new String(bcmrcv.getContentBuf());
//				mDeviceName.setText(bcmrcv.getContentBuf().toString());
			}
			else{
				//return false;
				
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flag++;
			if(flag >=0){
				break;
			}
			}
			return false;
		}
		
		
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			int response = result ? R.string.csst_adddevice_research_success : R.string.csst_adddevice_research_fail;
			Toast.makeText(CsstSHAddDeviceActivityZQL.this, response, Toast.LENGTH_SHORT).show();
			//debug 不消�?
			mDialog.dismiss();
			
		}
		
		
		
	}
	
	
	
	

	private final class Updatesenddebugcmdmianban extends AsyncTask<Void, Void, Boolean>{
		byte cmd = 0x00;
		public Updatesenddebugcmdmianban(byte cmd){
			this.cmd = cmd;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			int flag = 0;
			
			while(sendflag){
				System.out.println("zengqinglin  UpdateTempe Updatesenddebugcmdmianban \n");
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
			
			
			
//			String msgBuffer = "109861926250";
			
			String msgBuffer = mMacAdress;
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
			
//			//温湿�?
//			bcm.setMCMD((byte) 4);
//			bcm.setSCMD((byte) 1);
			
			bcm.setMCMD((byte) 4);
			bcm.setSCMD((byte) 1);
			System.out.println(" the cmd  is "+cmd);
			//
			byte[] contentBuf = new byte[6];
			contentBuf[0]=(byte)0x07;
			contentBuf[1]=(byte)0x01;
			contentBuf[2]=(byte)0x03;
			contentBuf[3]=(byte)0x03;
			contentBuf[4]=(byte)0x02;
			contentBuf[5]=(byte)cmd;
//			for(int m=2;m<contentBuftosend.length+2;m++){
//				contentBuf[m]=contentBuftosend[m-2];
//			}
//			contentBuf[contentBuftosend.length+1]=(byte)0x21;
			
			for(int n=0;n<6;n++){
				System.out.println(TAG+ "contentbuf setting " + n + " value: " + contentBuf[n]);
			}
			
			bcm.setContentBuf(contentBuf);
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
				//停止发送命�?
				sendflag =false;
				
//				bcmrcv.getContentBuf().toString();
//				new String(bcmrcv.getContentBuf());
//				mDeviceName.setText(bcmrcv.getContentBuf().toString());
			}
			else{
				//return false;
				
			}
			try {
				Thread.sleep(6000);
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
	
	

	private final class Updatesenddebugchazuo extends AsyncTask<Void, Void, Boolean>{
		byte cmd = 0x00;
		public Updatesenddebugchazuo(byte cmd){
			this.cmd = cmd;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			int flag = 0;
			
			while(sendflag){
				System.out.println("zengqinglin chazuo  Updatesenddebugchazuo \n");
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
			
//			sim.setIpaddress("192.168.1.105");
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
			
//			//温湿�?
//			bcm.setMCMD((byte) 4);
//			bcm.setSCMD((byte) 1);
			
			bcm.setMCMD((byte) 4);
			bcm.setSCMD((byte) 1);
			System.out.println(" the cmd  is "+cmd);
			//
			byte[] contentBuf = new byte[6];
			contentBuf[0]=(byte)0x07;
			contentBuf[1]=(byte)0x01;
			contentBuf[2]=(byte)0x01;
			contentBuf[3]=(byte)0x01;
			contentBuf[4]=(byte)0x03;
			contentBuf[5]=(byte)cmd;
//			for(int m=2;m<contentBuftosend.length+2;m++){
//				contentBuf[m]=contentBuftosend[m-2];
//			}
//			contentBuf[contentBuftosend.length+1]=(byte)0x21;
			
			for(int n=0;n<6;n++){
				System.out.println(TAG+ "contentbuf setting " + n + " value: " + contentBuf[n]);
			}
			
			bcm.setContentBuf(contentBuf);
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
				//停止发送命�?
				sendflag =false;
				
//				bcmrcv.getContentBuf().toString();
//				new String(bcmrcv.getContentBuf());
//				mDeviceName.setText(bcmrcv.getContentBuf().toString());
			}
			else{
				//return false;
				
			}
			try {
				Thread.sleep(6000);
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
