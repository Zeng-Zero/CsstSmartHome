package com.csst.smarthome.rc.custom;

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
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.fragment.CsstSHAddModelAddAction;
import com.csst.smarthome.bean.CsstSHDRCBean;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHDeviceRCKeyTable;
import com.csst.smarthome.rc.custom.util.TitleLayoutUtils;
import com.csst.smarthome.util.CsstContextUtil;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.csst.smarthome.util.CsstSHDBColumnUtil;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.message.baseMessage;
import com.lishate.message.rfcode.FinishRFCodeReqMessage;
import com.lishate.message.rfcode.SendRFCodeReqMessage;
import com.lishate.message.rfcode.StudyRFCodeReqMessage;
import com.lishate.message.rfcode.StudyRFCodeRspMessage;
import com.lishate.net.UdpProcess;

public class CustomRemoteEditActivity extends Activity implements ICsstSHConstant,
        OnClickListener, OnItemLongClickListener ,OnItemClickListener{
    
    public static final int REQUEST_CODE_ADD_KEY  = 1;
    public static final int REQUEST_CODE_EDIT_KEY  = 2;
    public static final String EXTRA_KEY_OF_REMOTE_URI = "current_remote_uri";
    public static final String EXTRA_KEY_OF_REMOTE_KEY_URI = "current_remote_key_uri";
    
    private static final String LOG_TAG = "CustomRemoteEditActivity";
    private String  TAG = LOG_TAG;
    private Button             mAddBtn;
//    private long                mRemoteUri;
    private String             mCustomDeviceName;
    private String             mCustomDeviceIconFilePath;
    
    
    
    /** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	
	private CsstSHDeviceBean curdevice = null;
	
	public static String[] msgBuffer  = null;
	/**
	 * 是否是动作调用是从whichaction 跳到本界面，就将在点遥控器的按键就调回 addaction界面
	 */
	String whichAction =null;
	/**
	 *出来的位置 从whichaction 界面传过来的关于位置的信息
	 */
	String location =null;

	/**
	 * 上层传过来的modelname
	 */
	private String modelName = null;
	
	//转动圆圈
		ProgressDialog myprogress;
	    // 超时
	    Timer timerdelaygetstatu;
	    Timer timerback;
	    boolean timeOut_flag = false;//当定时器 到达指定时间时 不接受清码 对码等操作 的标志位
//	    boolean cleanTimer = false; //清除定时器 true 表示定时器没作用
	    
		// timer
		private final Timer timer=new Timer();
		private TimerTask tast;
			
		private Timer timerconfigwifi;
		private TimerTask tastconfigwifi;
		private Handler handlerconfigwifi;
		
		private final int HANDLE_MSG_FROM_CONTROL =1;
		private final int HANDLE_MSG_FORM_PROGRESS =2;
		
	
	
	//
	private List<CsstSHDRCBean> curListcustomRcKeyBean =null;
	private  CsstSHDRCBean curCustomRcKeyBean = null;
	
	private int curDeviceId = 0;
	
//    private ArrayList<CsstSHCustomRCKeyBean> mKeysInRemote = new ArrayList<CsstSHCustomRCKeyBean>();
	 private ArrayList<CsstSHDRCBean> mKeysInRemote = new ArrayList<CsstSHDRCBean>();
    private RemoteKeysAdapter mRemoteKyesAdatper;
    private GridView mRemoteKeysGridView;
    
    private int[] popupBtnIds =  new int[] {
            R.id.learnKeyCode,
            R.id.editKey,
//            R.id.moveKey,
            R.id.deleteKey,
            R.id.cancel
    };
    private int mEditKeyPosition;
    private LinearLayout mBottomPopupLayout;
    private LinearLayout mFloatLearnCodeLayout;
    private Uri mKeyUri;
    
    private Button mDoneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(LOG_TAG+" onCreate");
        initDataSource();
        curdevice = (CsstSHDeviceBean) getIntent().getSerializableExtra("device");;
        if (curdevice ==null) {
        	System.out.println(LOG_TAG+" CURDEVICE IS NULL");
            finish();
            return;
        }
		modelName =(String) getIntent().getSerializableExtra("modelName");
		whichAction =(String) getIntent().getSerializableExtra("whichaction");
		location =(String) getIntent().getSerializableExtra("location");
		System.out.println("the value whichaction is "+whichAction+" the locatin is "+location);
        curDeviceId  = curdevice.getDeviceId();
        
        setContentView(R.layout.custom_remote_edit_activity);
        

//        Cursor c = getContentResolver().query(mRemoteUri, new String[] {Remotes.ID, Remotes.NAME, Remotes.ICON_PATH}, null, null, null);
//        mCustomDeviceName = c.getString(1);
//        mCustomDeviceIconFilePath = c.getString(2);
        mCustomDeviceName = curdevice.getDeviceName();
        TitleLayoutUtils.setupTitleLayout(this, mCustomDeviceName);
        mAddBtn = (Button) findViewById(R.id.addBtn);
        mAddBtn.setOnClickListener(this);
        
        
        
        
        mDoneBtn = (Button) findViewById(R.id.doneBtn);
        mDoneBtn.setOnClickListener(this);
        
        
        if(whichAction.equals("whichAction")){//在添加情景模式下如果没有学码的提示改动作不能调用
        	mAddBtn.setVisibility(View.GONE);
        	mDoneBtn.setVisibility(View.GONE);
		}
        
        
        mRemoteKeysGridView = (GridView) findViewById(R.id.keysOfRemote);
        mRemoteKyesAdatper = new RemoteKeysAdapter();
        
        mRemoteKeysGridView.setAdapter(mRemoteKyesAdatper);
        if(!whichAction.equals("whichAction")){//在添加情景模式下没有长按机制
        	mRemoteKeysGridView.setOnItemLongClickListener(this);
        }
        
        
        mRemoteKeysGridView.setOnItemClickListener(this);
        
        for (int poputBtnId : popupBtnIds) {
            findViewById(poputBtnId).setOnClickListener(this);
        }
        mBottomPopupLayout = (LinearLayout) findViewById(R.id.bottomPopupLayout);
        mFloatLearnCodeLayout = (LinearLayout) findViewById(R.id.learnCodeFLoatLayout);
        updateKeysLayout();
        configwifitimer();
    }

    private void initDataSource(){
    	// 初始数据源
		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		// 设备封面临时文件
    }
    
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.addBtn:
            tryAddRemoteKey();
            break;
        case R.id.doneBtn:
            backNormalState();
            break;
        case R.id.learnKeyCode:
            doLearnCode();
            break;
        case R.id.editKey:
            editKey();
            break;
//        case R.id.moveKey:
//            moveKey();
//            break;
        case R.id.deleteKey:
            deleteKey();
            break;
        case R.id.cancel:
            mBottomPopupLayout.setVisibility(View.GONE);
            break;

        default:
            break;
        }

    }
    
    private void backNormalState() {
        mBottomPopupLayout.setVisibility(View.GONE);
        mFloatLearnCodeLayout.setVisibility(View.GONE);
        mAddBtn.setVisibility(View.VISIBLE);
        mDoneBtn.setVisibility(View.GONE);
        
    }

    private void deleteKey() {
        int position = mEditKeyPosition;
        CsstSHDRCBean key = mKeysInRemote.get(position);
        
        
        CsstSHDeviceRCKeyTable.getInstance().delete(mDb, key);
//        CsstSHCustomRCKeyTable.getInstance().delete(mDb, key);
//        RemoteUriUtils.deleteKeyWithId(this,  key.id);
        mBottomPopupLayout.setVisibility(View.GONE);
        
        updateKeysLayout();
    }

    private void moveKey() {
        // TODO Auto-generated method stub
        
    }

    private void editKey() {
        int position = mEditKeyPosition;
        CsstSHDRCBean key = mKeysInRemote.get(position);
        
        
        
//        Uri keyUri = ContentUris.withAppendedId(RemoteKeys.CONTENT_URI, key.id);
        Intent i = new Intent(this, CustomRemoteAddKeyActivity.class);
        
        i.putExtra("device",curdevice);
        
        i.putExtra("CsstSHDRCBean",curCustomRcKeyBean);
//        i.putExtra(EXTRA_KEY_OF_REMOTE_URI, key);
//        i.putExtra(EXTRA_KEY_OF_REMOTE_KEY_URI, key);
//        mKeyUri = keyUri;
        curCustomRcKeyBean = key;
        mBottomPopupLayout.setVisibility(View.GONE);
        startActivityForResult(i, REQUEST_CODE_EDIT_KEY);
        
    }

    private void doLearnCode() {
        mBottomPopupLayout.setVisibility(View.GONE);
//        mFloatLearnCodeLayout.setVisibility(View.VISIBLE);
//        onClickKey(curCustomRcKeyBean);
        
		new StudyCodeTask(curCustomRcKeyBean).execute();
		
        mDoneBtn.setVisibility(View.GONE);
        mAddBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CODE_ADD_KEY:
            if(resultCode == RESULT_OK) {
                int drawableId = data.getIntExtra(CustomRemoteAddKeyActivity.KEY_OF_CUSTOM_KEY_DRAWABLE_ID, -1);
                String customNameOfSelectedKey = data.getStringExtra(CustomRemoteAddKeyActivity.KEY_OF_CUSTOM_KEY_NAME);
//                Toast.makeText(this, 
//                        String.format("Return key drawable id:%d, and custom name:%s.",
//                                drawableId, customNameOfSelectedKey),
//                        Toast.LENGTH_SHORT).show();
                
                if(drawableId == -1 || TextUtils.isEmpty(customNameOfSelectedKey)) {
                    Log.v(LOG_TAG, "onActivityResult drawable or custom key name invalid.");
                    return;
                }
                
//                CsstSHDRCBean drcBean = new CsstSHDRCBean(deviceId, dRCCmdCode, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify);
                  CsstSHDRCBean drcBean = new CsstSHDRCBean(curDeviceId, null, customNameOfSelectedKey, 0, 0, 0, 0, 0, drawableId, 1, 0);
//                CsstSHCustomRCKeyBean customRcKeyBean = new CsstSHCustomRCKeyBean(customNameOfSelectedKey,drawableId,null,curDeviceId);
//                ContentValues values = new ContentValues();
//                values.put(RemoteKeys.DRAWABLE_ID, drawableId);
//                values.put(RemoteKeys.NAME, customNameOfSelectedKey);
//                values.put(RemoteKeys.REMOTE_ID, mRemoteUri.getLastPathSegment());
                
                try {
//                    getContentResolver().insert(RemoteKeys.CONTENT_URI, values);
//                	CsstSHCustomRCKeyTable.getInstance().insert(mDb, customRcKeyBean);
                	CsstSHDeviceRCKeyTable.getInstance().insert(mDb, drcBean);
                } catch (SQLiteConstraintException e) {
                    Toast.makeText(this, "按键名已经存在", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "无法保存自定义按键", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                
                updateKeysLayout();
            }
            break;
        case REQUEST_CODE_EDIT_KEY:
            if(resultCode == RESULT_OK) {
                int drawableId = data.getIntExtra(CustomRemoteAddKeyActivity.KEY_OF_CUSTOM_KEY_DRAWABLE_ID, -1);
                String customNameOfSelectedKey = data.getStringExtra(CustomRemoteAddKeyActivity.KEY_OF_CUSTOM_KEY_NAME);
//                Toast.makeText(this, 
//                        String.format("Return key drawable id:%d, and custom name:%s.",
//                                drawableId, customNameOfSelectedKey),
//                        Toast.LENGTH_SHORT).show();
                
                if(drawableId == -1 || TextUtils.isEmpty(customNameOfSelectedKey)) {
                    Log.v(LOG_TAG, "onActivityResult drawable or custom key name invalid.");
                    return;
                }
//                ContentValues values = new ContentValues();
//                values.put(RemoteKeys.DRAWABLE_ID, drawableId);
//                values.put(RemoteKeys.NAME, customNameOfSelectedKey);
//                if(mKeyUri == null) {
//                    Log.w(LOG_TAG, "onActivityResult edit null mKeyUri");
//                }
                
//                curCustomRcKeyBean.setmCustomRCKeyDrawbleId(drawableId);
//                curCustomRcKeyBean.setmCustommCustomRCKeyName(customNameOfSelectedKey);
                System.out.println("before  the drawableId is  "+drawableId+"the curCustomRcKeyBean.getRCKeyIcon() is  "+curCustomRcKeyBean.getRCKeyIcon());
                curCustomRcKeyBean.setRCKeyIcon(drawableId);
                System.out.println("after the drawableId is  "+curCustomRcKeyBean.getRCKeyIcon());
                curCustomRcKeyBean.setRCKeyName(customNameOfSelectedKey);
                
                try {
//                    getContentResolver().update(mKeyUri, values, null, null);
                	
                	CsstSHDeviceRCKeyTable.getInstance().update(mDb, curCustomRcKeyBean);
                } catch (SQLiteConstraintException e) {
                    Toast.makeText(this, "按键名已经存在", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "无法保存自定义按键", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                
                updateKeysLayout();
            }
            break;

        default:
            break;
        }
    }

    private void updateKeysLayout() {
//    	curListcustomRcKeyBean = CsstSHCustomRCKeyTable.getInstance().queryByModel(mDb, curDeviceId);
    	
    	curListcustomRcKeyBean = CsstSHDeviceRCKeyTable.getInstance().queryByDevice(mDb, curDeviceId);
        mKeysInRemote.clear();
        if(curListcustomRcKeyBean!=null){
        	 for(int i=0;i<curListcustomRcKeyBean.size();i++){
        		 mKeysInRemote.add(curListcustomRcKeyBean.get(i)); 
        	 }
        }
        Log.v(LOG_TAG, String.format("remote has %d keys", mKeysInRemote.size()));
        mRemoteKyesAdatper.notifyDataSetChanged();
    }

    private void tryAddRemoteKey() {
        Intent  i= new Intent(this, CustomRemoteAddKeyActivity.class);
        
        i.putExtra("device",curdevice);
//        i.putExtra("CsstSHDRCBean",curCustomRcKeyBean);
        startActivityForResult(i, REQUEST_CODE_ADD_KEY);
    }
    
    private class RemoteKeysAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mKeysInRemote.size();
        }

        @Override
        public Object getItem(int position) {
            return mKeysInRemote.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mKeysInRemote.get(position).getDRCId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.keys_of_remote_grid_view_item, parent, false);
            }
            bindView(convertView, position);
            return convertView;
        }

        private void bindView(View convertView, int position) {
            ImageView icon = (ImageView) convertView.findViewById(R.id.keyIcon);
            TextView name = (TextView) convertView.findViewById(R.id.keyName);
            CsstSHDRCBean  key = mKeysInRemote.get(position);
            icon.setBackgroundResource(key.getRCKeyIcon());
            name.setText(key.getRCKeyName());
//            CsstSHCustomRCKeyBean key = mKeysInRemote.get(position);
//            icon.setBackgroundResource(key.getmCustomRCKeyDrawbleId());
//            name.setText(key.getmCustommCustomRCKeyName());
        }
        
    }
    
//     new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//       drcBean = new CsstSHDRCBean(deviceId, dRCCmdCode, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify);
//    CsstSHDeviceRCKeyTable.getInstance().insert(mDb, drcBean);
    
    private void onClickKey(final CsstSHDRCBean key ){
    	if (key.getDRCCmdCode() != null){
			
//			System.out.println("the mDevice room is  "+mDevice.getRoomId());
//			System.out.println("the mDevice name is  "+mDevice.getDeviceName().toString());
//			System.out.println("the mDevice ID is  "+mDevice.getDeviceId());
//			System.out.println("the mDevice RCType is  "+mDevice.getRCTypeId());
//			System.out.println("the mDevice SSID is  "+mDevice.getDeviceSSID());
			System.out.println("the key.getDRCCmdCode() is  "+key.getDRCCmdCode());
			if(whichAction.equals("whichAction")){
				System.out.println("go back add whichaction  ");
				Intent intent = new Intent(CustomRemoteEditActivity.this, CsstSHAddModelAddAction.class);
				intent.putExtra("location",location+curdevice.getDeviceName().toString());
				intent.putExtra("keyCode",key.getDRCCmdCode());
				intent.putExtra("modelName", modelName);
				startActivity(intent);
				CustomRemoteEditActivity.this.finish();
				if (mDb != null)
				mDb.close();
				
			}else{
				new SendCodeTast(key).execute();
			}
			
		}else{
//			if(whichAction.equals("whichAction")){
//				System.out.println("go back add whichaction  ");
//				Intent intent = new Intent(CustomRemoteEditActivity.this, CsstSHAddModelAddAction.class);
//				intent.putExtra("location",location+mDevice.getDeviceName().toString());
//				intent.putExtra("keyCode",key.getDRCCmdCode());
//				intent.putExtra("modelName", modelName);
//				startActivity(intent);
//				CustomRemoteEditActivity.this.finish();
//				if (mDb != null)
//					mDb.close();
//			}else{
			if(whichAction.equals("whichAction")){//在添加情景模式下如果没有学码的提示改动作不能调用
				Toast.makeText(CustomRemoteEditActivity.this, R.string.csst_rc_key_notaction, Toast.LENGTH_LONG).show();
				
			}else{
			AlertDialog.Builder builder = new AlertDialog.Builder(CustomRemoteEditActivity.this);
			builder.setMessage(R.string.csst_study_key_code_message);
			builder.setNegativeButton(R.string.csst_cancel, null);
			builder.setPositiveButton(R.string.csst_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new StudyCodeTask(key).execute();
				}
			});
			builder.show();
			}
		}
    	
    	
    	
    }
    
	
	public final class SendCodeTast extends AsyncTask<Void, Void, Boolean>{
		private CsstSHDRCBean keyBean = null;
		
		public SendCodeTast(CsstSHDRCBean key) {
			keyBean = key;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			LoadProcess_fisrt();
			
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			String[] msgBuffer = keyBean.getDRCCmdCode().split(",");
			
			ServerItemModel sim = new ServerItemModel();
			byte[] buffer = new byte[msgBuffer.length - 1];
			int temp = 0;
			for (int i = 1; i < msgBuffer.length; i++){
				temp = Integer.parseInt(msgBuffer[i].trim().substring(2), 16);
				buffer[i - 1] = (byte)(0xFF & temp);
			}
			sim.setIpaddress("218.244.129.177");
			sim.setPort(12188);
			SendRFCodeReqMessage slrm = new SendRFCodeReqMessage();
			slrm.Direct = MessageDef.BASE_MSG_FT_REQ;
			slrm.setFromId(GobalDef.MOBILEID);
			slrm.MsgType = MessageDef.MESSAGE_TYPE_RFCODE_SEND_REQ;
			slrm.Seq = MessageSeqFactory.GetNextSeq();
			slrm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
			slrm.ToType = MessageDef.BASE_MSG_FT_HUB;
			slrm.setToId(Long.valueOf(msgBuffer[0]));
			System.out.println(TAG+"");
			System.out.println(TAG+"the mac adress is "+Long.valueOf(msgBuffer[0]));
			slrm.setCodeBuf(buffer);
			baseMessage msg = UdpProcess.GetMsgReturn(slrm, sim);
			if(msg == null){
				return false;
			} else {
				//SendRFCodeRspMessage srfrm = (SendRFCodeRspMessage)msg;
				return true;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			int response = result ? R.string.csst_send_cmd_success : R.string.csst_send_cmd_fail;
			Toast.makeText(CustomRemoteEditActivity.this, response, Toast.LENGTH_LONG).show();
			if(myprogress!=null){
				myprogress.dismiss();
			}
			//先停止原来来定时器 因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
			stopTimer();
			startTimer();
		}
	}

	/**
	 * 学码操作
	 * @author liuyang
	 */
	public final class StudyCodeTask extends AsyncTask<Void, Void, Boolean>{
		
		private CsstSHDRCBean mKeyBean = null;
		
		private Dialog mDialog = null;
		
		public StudyCodeTask(CsstSHDRCBean key) {
			mKeyBean = key;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//取消回调接口
			DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (StudyCodeTask.this.getStatus() != AsyncTask.Status.FINISHED){
						StudyCodeTask.this.cancel(true);
					}
					//发送停止命令
					new FinishCodeTask().execute();
					Toast.makeText(CustomRemoteEditActivity.this, R.string.csst_study_finish, Toast.LENGTH_LONG).show();
				}
			};
			mDialog =CsstContextUtil.searchDialog(CustomRemoteEditActivity.this, null, getString(R.string.csst_study_code_message), callback);
			mDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				ServerItemModel sim = new ServerItemModel();
				sim.setIpaddress("255.255.255.255");
				sim.setPort(80);
				StudyRFCodeReqMessage slrm = new StudyRFCodeReqMessage();
				slrm.Direct = MessageDef.BASE_MSG_FT_REQ;
				slrm.setFromId(GobalDef.MOBILEID);
				slrm.MsgType = MessageDef.MESSAGE_TYPE_RFCODE_STUDY_REQ;
				slrm.Seq = MessageSeqFactory.GetNextSeq();
				slrm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
				slrm.ToType = MessageDef.BASE_MSG_FT_HUB;
				slrm.setToId(toid);
				Log.d(TAG, "will get msg return");
				baseMessage msg = UdpProcess.GetMsgReturn(slrm, sim);
				if (null == msg){
					return false;
				}else{
					StudyRFCodeRspMessage srfrm = (StudyRFCodeRspMessage) msg;
					byte[] codeBuf = srfrm.getCodeBuf();
					String bufferMessage = null;
					if (null != codeBuf){
						bufferMessage = CsstSHDBColumnUtil.bufferToMessge(codeBuf);
						bufferMessage = srfrm.getFromId() + ", " + bufferMessage;
//						mKeyBean = getDeviceRCKeyByIdentify(mKeyBean.getRCKeyIdentify());
						mKeyBean.setDRCCmdCode(bufferMessage);
						CsstSHDeviceRCKeyTable.getInstance().update(mDb, mKeyBean);
						
						msgBuffer = mKeyBean.getDRCCmdCode().split(",");
						System.out.println("msgBuffer[0] set mac address "+msgBuffer[0]);
						
						configPreference.setMacAdress(msgBuffer[0]);
						
						return true;
					}
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result){
				//发送停止命令
				new FinishCodeTask().execute();
			}
			int response = result ? R.string.csst_study_success : R.string.csst_study_fail;
			Toast.makeText(CustomRemoteEditActivity.this, response, Toast.LENGTH_LONG).show();
			mDialog.dismiss();
		}
	}
	
	/**
	 * 结束命令
	 * @author liuyang
	 */
	public final class FinishCodeTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			ServerItemModel sim = new ServerItemModel();
			sim.setIpaddress("255.255.255.255");
			sim.setPort(80);
			FinishRFCodeReqMessage slrm = new FinishRFCodeReqMessage();
			slrm.Direct = MessageDef.BASE_MSG_FT_REQ;
			slrm.setFromId(GobalDef.MOBILEID);
			slrm.MsgType = MessageDef.MESSAGE_TYPE_RFCODE_FINISH_REQ;
			slrm.Seq = MessageSeqFactory.GetNextSeq();
			slrm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
			slrm.ToType = MessageDef.BASE_MSG_FT_HUB;
			slrm.setToId(toid);
			UdpProcess.GetMsgReturn(slrm, sim);
			return null;
		}
	}

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
            int position, long id) {
    	curCustomRcKeyBean = mKeysInRemote.get(position);
        showKeyEditPrompt(position);
        return true;
    }

    private void showKeyEditPrompt(int position) {
    	mEditKeyPosition = position;
        mBottomPopupLayout.setVisibility(View.VISIBLE);;
        
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		mEditKeyPosition = arg2;
		CsstSHDRCBean key = mKeysInRemote.get(mEditKeyPosition);
	    onClickKey(key);
		
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
				   System.out.println("handleMessage  is here ");
				  
//				   System.out.println("contentBuffer[5]  is here "+contentBuffer[MSG_STATUE_RECIVE]);
				   switch(msg.what){
				   case HANDLE_MSG_FROM_CONTROL:
					   break;
				   case HANDLE_MSG_FORM_PROGRESS:
					   if(myprogress!=null)
						   myprogress.dismiss();
					   break;
				   }
				  
			  }
			    };
	}
    

    
    
    // 延时几秒才执行查询的任务
	 public void timerOut(int ms){
    			 timerdelaygetstatu =new Timer();
    		    	TimerTask task;
    		    	task = new TimerTask() {
    					
    					@Override
    					public void run() {
    							Message msg = handlerconfigwifi.obtainMessage(HANDLE_MSG_FORM_PROGRESS);
    							handlerconfigwifi.sendMessage(msg);
    					}
    				};	
    				timerdelaygetstatu.schedule(task, ms);
    		    }
    		    
   	public void  LoadProcess_fisrt() {
   		myprogress = new ProgressDialog(this);
   		myprogress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
   		myprogress.setMessage(this.getResources().getString(R.string.csst_send_cmd));
   		timerOut(5000);
   		myprogress.setIndeterminate(false);
   		myprogress.setCancelable(false);
   		myprogress.show();
   	}
	@Override
	protected void onDestroy() {
		if (mDb != null)
			mDb.close();
		super.onDestroy();
	}
    
}
