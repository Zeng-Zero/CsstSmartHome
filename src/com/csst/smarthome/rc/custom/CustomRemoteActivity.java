package com.csst.smarthome.rc.custom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.device.CsstSHAddDeviceActivityZQL;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHDeviceTable;
import com.csst.smarthome.rc.custom.util.SoftKeyBoardUtils;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.csst.smarthome.util.CsstSHImageData;

public class CustomRemoteActivity extends Activity implements ICsstSHConstant, OnClickListener {

    public static final int     REQUEST_CODE_PICK_PICTURE = 1;
    public static final int     REQUEST_CODE_TAKE_PICTURE = 2;
    private static final String LOG_TAG                   = "CustomRemoteActivity";
    private ImageView           mRemotePicture;
    private EditText            mRemoteNameEdit;
    private LinearLayout        mBottomPopupLayout;
    private Button              mDoneBtn;
    private LinearLayout 				mbtnback;
    private TextView          titleView;

    
    
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	/** 房间id */
	private int mRoomId = -1;
	
	private CsstSHDeviceBean curdevice = null;
	
//上面传过来的
	private CsstSHDeviceBean formupdevice = null;
	
	
	
	
	/** 通过专辑获得封面 */
	private static final int GET_ICON_FROM_ALBUM = 0x00;
	/** 剪切获得图片 */
	private static final int GET_ICON_FROM_CROP = 0x01;
	/** 通过拍照获得封面 */
	private static final int GET_ICON_FROM_TAKE = 0x02;
	/** 扫描设备id请求 */
	private static final int SCAN_UUID_REQUEST = 0x03;
	/** 设备封面图片临时文件 */
	private File mDeviceIconTempFile = null;
	
	
	
    private String              mLastUpdatedIconFileName  = null;
    private ContentResolver     mContentResolver;
    private long                 mSavedRemoteUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_remote_activity);
//        TitleLayoutUtils.setupTitleLayout(this, "自定义遥控");
        initDataSource();
        
        
        
        mDoneBtn = (Button) findViewById(R.id.doneBtn);
        mDoneBtn.setOnClickListener(this);
        
        titleView = (TextView)findViewById(R.id.title_textview);
        titleView.setText(getResources().getString(R.string.csst_customkey_title));
        
        mbtnback = (LinearLayout) findViewById(R.id.back_btn);
        mbtnback.setOnClickListener(this);
        
        mRemotePicture = (ImageView) findViewById(R.id.customRemoteIcon);
        mRemotePicture.setOnClickListener(this);
        
        formupdevice = (CsstSHDeviceBean) getIntent().getSerializableExtra("device");;
        mRemoteNameEdit = (EditText) findViewById(R.id.customRemoteName);
        if(formupdevice!=null){
        	//图片
        	mLastUpdatedIconFileName= formupdevice.getDeviceIconPath();
        	mRemoteNameEdit.setText(formupdevice.getDeviceName());
        	if(mLastUpdatedIconFileName!=null){//编辑界面在定义遥控的时候没有选图片就默认的显示
        		mRemotePicture.setImageBitmap(BitmapFactory.decodeFile(mLastUpdatedIconFileName));
        	}
        	
        }
        
        mBottomPopupLayout = (LinearLayout) findViewById(R.id.bottomPopupLayout);

        findViewById(R.id.pickPictureBtn).setOnClickListener(this);
        findViewById(R.id.takePictureBtn).setOnClickListener(this);
        findViewById(R.id.cancelPictureBtn).setOnClickListener(this);

        mContentResolver = getContentResolver();
    }
    private void initDataSource(){
    	// 初始数据源
		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
		// 当前楼层对应的房间id
		mRoomId = configPreference.getRoomId();
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		// 设备封面临时文件
		mDeviceIconTempFile = CsstSHImageData.deviceIconTempFile();
		
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.doneBtn:
            onDoneBtnClicked();
            break;
        case R.id.customRemoteIcon:
            onCustomRemoteIconClicked();
            break;
        case R.id.pickPictureBtn:
//            onUserChoosePickPicture();
        	//将下拉才能去掉
        	mBottomPopupLayout.setVisibility(View.GONE);
        	CsstSHImageData.pickAlbum(CustomRemoteActivity.this, GET_ICON_FROM_ALBUM);
            break;
        case R.id.takePictureBtn:
//            onUserChooseTakePicture();
        	//将下拉才能去掉
        	mBottomPopupLayout.setVisibility(View.GONE);
        	CsstSHImageData.tackPhoto(CustomRemoteActivity.this, mDeviceIconTempFile, GET_ICON_FROM_TAKE);
            break;
        case R.id.cancelPictureBtn:
            mBottomPopupLayout.setVisibility(View.GONE);
            break;
        case R.id.back_btn:
        	 Intent i = new Intent(this, CsstSHAddDeviceActivityZQL.class);
             startActivity(i);
             finish();
        	break;
        

        default:
            break;
        }

    }

    private void onDoneBtnClicked() {
        String remoteName = mRemoteNameEdit.getText().toString();
        if (TextUtils.isEmpty(remoteName)) {
            Toast.makeText(this, "自定义遥控名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(formupdevice!=null){//表示编辑界面
        	//新建设备同一房间存在相同的设备返回
			if (CsstSHDeviceTable.getInstance().roomExisSameDevice(mDb, remoteName, mRoomId)){
				if(remoteName.endsWith(formupdevice.getDeviceName())){//没有做修改就直接退出
					finish();
				}else{
					Toast.makeText(CustomRemoteActivity.this, R.string.csst_home_exists_device, Toast.LENGTH_SHORT).show();
					return;
				}
				
			}
			formupdevice.setDeviceName(remoteName);
			formupdevice.setDeviceIconPath(mLastUpdatedIconFileName);
        	CsstSHDeviceTable.getInstance().update(mDb, formupdevice);
        	finish();
        }else{
             saveNewRemote(remoteName);
             openCustomRemoteEditActivity(remoteName);
        }
       
    }

    private void openCustomRemoteEditActivity(String remoteName) {
        Intent i = new Intent(this, CustomRemoteEditActivity.class);
    	i.putExtra("modelName", "null");
		i.putExtra("whichaction", "nowhichaction");
		i.putExtra("location", "nolocation");
//        i.putExtra(CustomRemoteEditActivity.KEY_OF_REMOTE_DEVICE_NAME,
//                remoteName);
//        i.putExtra(CustomRemoteEditActivity.KEY_OF_CUSTOM_ICON_FILENAME,
//                mLastUpdatedIconFileName);
        if(mSavedRemoteUri ==0) {
            return;
        }
        i.putExtra("device", curdevice);
        startActivity(i);
        finish();
    }

    private void saveNewRemote(String remoteName) {
    	//设备封面图片路径
//		if (TextUtils.isEmpty(mLastUpdatedIconFileName)){
//			Toast.makeText(CustomRemoteActivity.this, R.string.csst_device_icon_empty, Toast.LENGTH_SHORT).show();
//			return;
//		}
    	
//		CsstSHImageData.copyZoomToApp(mLastUpdatedIconFileName, CsstSHImageData.deviceIconFile(CustomRemoteActivity.this));
//		new File(mLastUpdatedIconFileName).delete();
    	CsstSHDeviceBean device;
		if (TextUtils.isEmpty(mLastUpdatedIconFileName)){
			 device = new CsstSHDeviceBean(remoteName, false, null, 8, "aaa", mRoomId, 0);
		}else{
			device = new CsstSHDeviceBean(remoteName, false, mLastUpdatedIconFileName, 8, "aaa", mRoomId, 0);
		}
    	
		
		mSavedRemoteUri = CsstSHDeviceTable.getInstance().insert(mDb, device);
		//插入设备id
    	device.setDeviceId((int)mSavedRemoteUri);
    	curdevice = device;
    	
//        ContentValues values = new ContentValues();
//        values.put(Remotes.NAME, remoteName);
//        if (!TextUtils.isEmpty(mLastUpdatedIconFileName)) {
//            values.put(Remotes.ICON_PATH, mLastUpdatedIconFileName);
//        }
//        try {
//            mSavedRemoteUri = mContentResolver.insert(Remotes.CONTENT_URI, values);
//        } catch (SQLiteConstraintException e) {
//            Toast.makeText(this,"名为" + remoteName + "的遥控器已经存在", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
        
    }

    private void onUserChooseTakePicture() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(),
                "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);

    }

    private void onUserChoosePickPicture() {
        // Intent intent = new Intent();
        // intent.setType("image/*");
        // intent.setAction(Intent.ACTION_GET_CONTENT);
        // startActivityForResult(Intent.createChooser(intent,
        // "Select Picture"), REQUEST_CODE_PICK_PICTURE);
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, REQUEST_CODE_PICK_PICTURE);

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
					try{
						Bundle extras = data.getExtras();
						Bitmap source = extras.getParcelable("data");
						mLastUpdatedIconFileName = CsstSHImageData.zoomIconTempFile().getPath();
						//缩放图片
						source = CsstSHImageData.zoomBitmap(source, mLastUpdatedIconFileName);
						//更新设备封面图片
						mRemotePicture.setImageBitmap(source);
					}catch(Exception ex ){
						System.out.println("the error is "+ex.toString());
					}
					
				}
				break;
				
			case GET_ICON_FROM_ALBUM:
				if (resultCode == RESULT_OK){
					Uri uri = data.getData();
					//剪切拍的照片
					CsstSHImageData.cropDeviceIconPhoto(this, uri, GET_ICON_FROM_CROP);
				}
				break;
		}
	}
//    
//    
//    
//    
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//        case REQUEST_CODE_PICK_PICTURE:
//            onUserChoosePictureReturn(resultCode, data);
//            break;
//        case REQUEST_CODE_TAKE_PICTURE:
//            afterUserTakePicture(resultCode, data);
//            break;
//
//        default:
//            break;
//        }
//    }

    private void afterUserTakePicture(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            File imageFile = new File(
                    Environment.getExternalStorageDirectory(), "Pic.jpg");
            String picturePath = imageFile.getAbsolutePath();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(picturePath, options);

            int outWidth = options.outWidth, outHeight = options.outHeight;
            int max = outWidth > outHeight ? outWidth : outHeight;
            options.inSampleSize = max / 200;
            options.inJustDecodeBounds = false;

            Bitmap icon = BitmapFactory.decodeFile(picturePath, options);
            if (icon != null) {
                mRemotePicture.setImageBitmap(icon);
                updatePicture(icon);
            }

        }

    }

    private void updatePicture(Bitmap icon) {
        if (icon == null) {
            Log.e(LOG_TAG, "updatePicture get null icon");
            return;
        }
        if (mLastUpdatedIconFileName != null) {
            if (new File(mLastUpdatedIconFileName).delete()) {
                Log.v(LOG_TAG, "Delete old icon file");
            }
        }
        UUID uuid = UUID.randomUUID();
        File newIconImageFile = new File(getFilesDir(), uuid.toString());
        try {
            icon.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(
                    newIconImageFile));
            mLastUpdatedIconFileName = newIconImageFile.toString();
            Log.v(LOG_TAG, "save new icon to file " + mLastUpdatedIconFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void onUserChoosePictureReturn(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.v(LOG_TAG, "user choose piture with uri: " + uri);

            // try {
            // Bitmap bitmap =
            // MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            // Log.v(LOG_TAG, String.format("bitmap size is %d x %d",
            // bitmap.getWidth(), bitmap.getHeight()));
            // } catch (FileNotFoundException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // } catch (IOException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

            if (uri == null) {
                return;
            }

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            String picturePath = null;
            try {
                Cursor cursor = getContentResolver().query(uri, filePathColumn,
                        null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (picturePath == null) {
                return;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(picturePath, options);
            int outWidth = options.outWidth, outHeight = options.outHeight;
            int max = outWidth > outHeight ? outWidth : outHeight;
            options.inSampleSize = max / 200;
            options.inJustDecodeBounds = false;

            Bitmap icon = BitmapFactory.decodeFile(picturePath, options);
            if (icon != null) {
                mRemotePicture.setImageBitmap(icon);
                updatePicture(icon);
            }
        }

    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	if(keyCode==KeyEvent.KEYCODE_BACK){
		//需要
		 Intent i = new Intent(this, CsstSHAddDeviceActivityZQL.class);
         startActivity(i);
         finish();
		}
		 return true;
	}

    private void onCustomRemoteIconClicked() {
        SoftKeyBoardUtils.dismissKeyBoard(this);
        mBottomPopupLayout.setVisibility(View.VISIBLE);
    }
	@Override
	protected void onDestroy() {
		if (mDb != null)
			mDb.close();
		super.onDestroy();
	}
}
