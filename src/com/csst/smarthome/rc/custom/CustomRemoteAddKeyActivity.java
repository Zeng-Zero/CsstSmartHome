package com.csst.smarthome.rc.custom;


import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHDRCBean;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHDeviceRCKeyTable;
import com.csst.smarthome.rc.custom.util.SoftKeyBoardUtils;
import com.csst.smarthome.rc.custom.util.TitleLayoutUtils;
import com.csst.smarthome.util.CsstSHConfigPreference;

public class CustomRemoteAddKeyActivity extends Activity implements
        OnClickListener, TextWatcher {
    public static final String  KEY_OF_REMOTE_DEVICE_NAME   = "custom_remote_device_name";
    public static final String  KEY_OF_CUSTOM_ICON_FILENAME = "custom_remote_icon_filename";

    public static final String  KEY_OF_CUSTOM_KEY_DRAWABLE_ID     = "key_of_key_drawable_id";
    public static final String  KEY_OF_CUSTOM_KEY_NAME      = "key_of_custom_name";
    private static final String LOG_TAG                     = "CustomRemoteAddKeyActivity";

    public static int[]         mImageDrawables             = {
            R.drawable.key_rewind, R.drawable.key_stop, R.drawable.key_forward,
            R.drawable.key_play_pause, R.drawable.key_down,R.drawable.key_power,
            R.drawable.key_mute, R.drawable.key_previous, R.drawable.key_next,
            R.drawable.key_up,R.drawable.key_back, R.drawable.key_blank ,R.drawable.key_off ,
            R.drawable.key_ok ,R.drawable.key_on ,R.drawable.key_about,
            R.drawable.key_low,R.drawable.key_left,R.drawable.key_right};

    public static int[]         mImageViewIds                   = {
            R.id.addKeyRewind, R.id.addKeyStop, R.id.addKeyForward,
            R.id.addKeyPlayPause,  R.id.addKeyDown,R.id.addKeyPower, R.id.addKeyMute,
            R.id.addKeyPrevious, R.id.addKeyNext,  R.id.addKeyUp,R.id.addKeyBack,
            R.id.addKeyBlank, R.id.addKeyOff,R.id.addKeyOk,R.id.addKeyOn,R.id.addKeyAbout, R.id.addKeyLow, R.id.addKeyLeft,  R.id.addKeyRight };

    private ImageView           mAddKeyIcon;
    private View                mBottomKeysPannel;
    private Button              mDoneBtn;
    private EditText            mCustomRemoteKeyName;
    private int                 mIndexOfSelectedKey         = -1;
//    private Uri                 mRemoteUri;
//    private Uri                 mRemoteKeyUri;
    
	private CsstSHDeviceBean curdevice = null;
	
	private  CsstSHDRCBean curCustomRcKeyBean = null;
	//
	private List<CsstSHDRCBean> curListcustomRcKeyBean =null;
	
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
    /** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        mRemoteUri = getIntent().getParcelableExtra(
//                CustomRemoteEditActivity.EXTRA_KEY_OF_REMOTE_URI);
        
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		// 设备封面临时文件
        
        
        curdevice = (CsstSHDeviceBean) getIntent().getSerializableExtra("device");
        
//        mRemoteKeyUri = getIntent().getParcelableExtra(CustomRemoteEditActivity.EXTRA_KEY_OF_REMOTE_KEY_URI);
        curCustomRcKeyBean = (CsstSHDRCBean) getIntent().getSerializableExtra("CsstSHDRCBean");
        
        
        if (curdevice == null) {
            finish();
            return;
        }
        
//        ContentValues values = RemoteUriUtils.getRemoteUriContentValue(this,
//                mRemoteUri);

        String name = curdevice.getDeviceName();

        setContentView(R.layout.custom_remote_add_key_activity);
        if (name == null) {
            name = "Unknow Device";
        }
        TitleLayoutUtils.setupTitleLayout(this, name);

        for (int id : mImageViewIds) {
            findViewById(id).setOnClickListener(this);
        }

        mAddKeyIcon = (ImageView) findViewById(R.id.addKeyIcon);
        mCustomRemoteKeyName = (EditText) findViewById(R.id.customRemoteKeyName);
        mCustomRemoteKeyName.addTextChangedListener(this);

        findViewById(R.id.addKeyIconLayout).setOnClickListener(this);
        mBottomKeysPannel = findViewById(R.id.bottomKeysPannel);
        mDoneBtn = (Button) findViewById(R.id.doneBtn);
        mDoneBtn.setOnClickListener(this);
        mDoneBtn.setEnabled(false);
        
      //editing exist key;
        if(curCustomRcKeyBean != null) {
//            ContentValues keyValues = RemoteUriUtils.getRemoteKeyUriContentValue(this, mRemoteKeyUri);
//            String keyName = keyValues.getAsString(RemoteKeys.NAME);
            String keyName = curCustomRcKeyBean.getRCKeyName();
            int drawableId = curCustomRcKeyBean.getRCKeyIcon();
            int position = indexOf(drawableId, mImageDrawables);
            if(position != -1 && !TextUtils.isEmpty(keyName)) {
                mCustomRemoteKeyName.setText(keyName);
                mAddKeyIcon.setImageResource(drawableId);
                mIndexOfSelectedKey = position;
                updateDoneBtnState();
            }
        }
        
        
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
        case R.id.addKeyIconLayout:
            showAddKeyIconBottomPannel();
            break;
        case R.id.doneBtn:
            tryReturnKeyAndId();
            break;

        default:
            tryAddKeyById(id);
            break;
        }

    }

    private void tryReturnKeyAndId() {
    	
    	curListcustomRcKeyBean = CsstSHDeviceRCKeyTable.getInstance().queryByDevice(mDb, curdevice.getDeviceId());
    	
    	
    	boolean exist =false;
    	if(curListcustomRcKeyBean!=null){
    		for(int i=0;i<curListcustomRcKeyBean.size();i++){
    			if(curListcustomRcKeyBean.get(i).getRCKeyName().endsWith(mCustomRemoteKeyName.getText()
	                .toString())&&curListcustomRcKeyBean.get(i).getRCKeyIcon()==mImageDrawables[mIndexOfSelectedKey]){
    				exist= true;
    			}
    		}
    	}
    	if(!exist){
    		Intent data = new Intent();
    		System.out.println("the mindexOfSelectKey is "+mIndexOfSelectedKey+"the mImageDrawables [mIndexOfSelectedKey] "+mImageDrawables[mIndexOfSelectedKey]);
    		
	        data.putExtra(KEY_OF_CUSTOM_KEY_DRAWABLE_ID, mImageDrawables[mIndexOfSelectedKey]);
	        data.putExtra(KEY_OF_CUSTOM_KEY_NAME, mCustomRemoteKeyName.getText()
	                .toString());
	        setResult(RESULT_OK, data);
	        finish();
    	}else{
    		if(curCustomRcKeyBean!=null){//表示是编辑界面 且是相同名称和相同图片1、跟本次编辑的一样 就直接退出 2、跟数据库中存在一样 就重新输入
    			if(mCustomRemoteKeyName.getText()
    	                .toString().endsWith(curCustomRcKeyBean.getRCKeyName())){//如果是没有修改就直接跳过 //这里表示已经修改了但是已经存在就需要重新输入
//        		
    				finish();//1、跟本次编辑的一样 就直接退出 
    			
    			}else{//2、跟数据库中存在一样 就重新输入
    				Toast.makeText(CustomRemoteAddKeyActivity.this, R.string.csst_customkey_keyexist, Toast.LENGTH_SHORT).show();	
        		}
    			
    		}else{
    			//新增加按键 如果是已经存在就重新输入
        		Toast.makeText(CustomRemoteAddKeyActivity.this, R.string.csst_customkey_keyexist, Toast.LENGTH_SHORT).show();	
    		}
    			
    			
    		
    	}
       
    }

    private int indexOf(int value, int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    private void tryAddKeyById(int id) {
        int index = indexOf(id, mImageViewIds);
        int drawableId = mImageDrawables[index];
        mAddKeyIcon.setImageResource(drawableId);
        mIndexOfSelectedKey = index;
        System.out.println("the mindexOfSelectKey is "+mIndexOfSelectedKey);
        updateDoneBtnState();
    }

    private void updateDoneBtnState() {
        if (mIndexOfSelectedKey < 0) {
            mDoneBtn.setEnabled(false);
            return;
        }

        if (TextUtils.isEmpty(mCustomRemoteKeyName.getText().toString())) {
            mDoneBtn.setEnabled(false);
            return;
        }

        mDoneBtn.setEnabled(true);
    }

    private void showAddKeyIconBottomPannel() {
        mBottomKeysPannel.setVisibility(View.VISIBLE);
        SoftKeyBoardUtils.dismissKeyBoard(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        updateDoneBtnState();
    }
	@Override
	protected void onDestroy() {
		if (mDb != null)
			mDb.close();
		super.onDestroy();
	}
}
