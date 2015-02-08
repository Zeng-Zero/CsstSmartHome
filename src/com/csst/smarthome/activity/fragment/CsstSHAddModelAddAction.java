package com.csst.smarthome.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHActionBean;
import com.csst.smarthome.bean.CsstSHModelBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHActionTable;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHModelTable;

/**
 * 中控配置
 * @author liuyang
 */
public class CsstSHAddModelAddAction extends Activity implements ICsstSHInitialize, ICsstSHConstant{

	/** 返回按钮 */
	private Button mBtnCancel = null;
	/** 完成按钮 */
	private Button mBtnDone = null;
	/** 标题 */
	private TextView mTVTitle = null;
	/** 调用动作按钮 */
	private ImageView mImgWhichAciton = null;
	/** 动作位置 */
	private TextView mTVActionLocation = null;
	/** 动作名称 */
	private EditText mETActionName = null;
	/** 动作延时 */
	private EditText mETActionDelay = null;
	/**
	 *调用动作
	 */
	
	private BackBtnListener mBackBtnListener = null;
	private DoneBtnListener mBtnDoneListener = null;
	private WhichActoinBtnListener mBtnWhichActionListener=null;
	
	private String TAG = "CsstSHAddModelAddAction";
	private boolean debug = true;
	/**
	 * 动作位置
	 */
	private String location =null;
	/**
	 * 上层传过来的modelname
	 */
	private String modelName = null;
	
	/** 从whichAction 传过来的 keyCode */
	private String keyCode = null;
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	/**
	 * 从上层传过来acitonbean
	 */
	private CsstSHActionBean mActionBean =null;
	
	private LinearLayout llImageButton ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csst_modeladd_actioneditor);
		Intent intent = getIntent();
		if (null != intent){
			modelName =(String) intent.getSerializableExtra("modelName");
			location =(String) intent.getSerializableExtra("location");
			keyCode =(String) intent.getSerializableExtra("keyCode");
			mActionBean =(CsstSHActionBean) intent.getSerializableExtra("actionBean");
			
		}
		if(mActionBean!=null){
			if(debug){
				System.out.println(TAG+" the actionbean is null ");
			}
		}
		if(debug){
			System.out.println(TAG+"initDataSource initDataSource the name is "+modelName
					+"the location is "+location+"the keycode is "+keyCode);
			}
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
		
	}

	@Override
	protected void onResume() {
		
		if(location !=null){
			mTVActionLocation.setText(location);
		}
		super.onResume();
	}
	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
		mTVTitle = (TextView) findViewById(R.id.mTVTitle);
		mBtnDone = (Button) findViewById(R.id.mBtnDone);
		mImgWhichAciton = (ImageView) findViewById(R.id.btwhichaction);
		mTVActionLocation = (TextView) findViewById(R.id.tvactionlocal);
		mETActionName =	(EditText) findViewById(R.id.evnameaciton);
		mETActionDelay = (EditText) findViewById(R.id.evactiondelaytime);
		llImageButton= (LinearLayout)findViewById(R.id.llimgbtn);
	}

	@Override
	public void initWidgetState() {
		//设置标题
		if(modelName!=null){
			mTVTitle.setText(modelName);
		}else{
			mTVTitle.setText(R.string.csst_model_actionadd);	
		}
		if(location !=null){
		if(debug){
			System.out.println(TAG+" initWidgetState the location is "+location);
		}
			mTVActionLocation.setText(location);
		}
		if(mActionBean !=null){
			mTVActionLocation.setText(mActionBean.getmLocation());
			mETActionName.setText(mActionBean.getmActionName());
			mETActionDelay.setText(Integer.toString(mActionBean.getmDelayTime()));
		}
		//隐藏完成按钮
//		mBtnDone.setVisibility(View.GONE);
		//链接热点
	}

	@Override
	public void initWidgetListener() {
		mBackBtnListener = new BackBtnListener();
		mBtnDoneListener = new DoneBtnListener();
		mBtnWhichActionListener = new WhichActoinBtnListener();
		
	}

	@Override
	public void addWidgetListener() {
		mBtnCancel.setOnClickListener(mBackBtnListener);
		mBtnDone.setOnClickListener(mBtnDoneListener);
		mImgWhichAciton.setOnClickListener(mBtnWhichActionListener);
		llImageButton.setOnClickListener(mBtnWhichActionListener);
		
	}
	
	/**
	 * 返回事件
	 */
	private final void backEvent(){
//		Intent intent = new Intent();
//		intent.setClass(CsstSHAddModelAddAction.this, CsstSHAddModelListActionView.class);
//		intent.putExtra("modelName", modelName);
//		startActivity(intent);
		CsstSHAddModelAddAction.this.finish();
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
	 * 完成按钮监听器
	 * @author liuyang
	 */
	private final class DoneBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			String mActionName = mETActionName.getText().toString();
			String mLocation = mTVActionLocation.getText().toString();
		
			String mKeyCode=keyCode;
			
			if(mActionName.equals("")){
				Toast.makeText(CsstSHAddModelAddAction.this, CsstSHAddModelAddAction.this.getResources().getString(R.string.csst_model_actionnamenulltip), Toast.LENGTH_LONG).show();
				
			}else if(mLocation.equals("")){
				Toast.makeText(CsstSHAddModelAddAction.this, CsstSHAddModelAddAction.this.getResources().getString(R.string.csst_model_actionlocalnulltip), Toast.LENGTH_LONG).show();
			}else if(mETActionDelay.getText().toString().equals("")){
				Toast.makeText(CsstSHAddModelAddAction.this, CsstSHAddModelAddAction.this.getResources().getString(R.string.csst_model_actiondelaytimenulltip), Toast.LENGTH_LONG).show();
			}else{
				long mModelId = 0;
				// 实例化modelBean 用来查数据库
				if(debug){
					System.out.println(" init the modelBean to database 1 the modelName "+modelName);
				}
				if(modelName!=null){
					
					if(mActionBean!=null){//属于编辑action
						
						mActionBean.setmActionName(mActionName);
						mActionBean.setmDelayTime(Integer.parseInt(mETActionDelay.getText().toString()));
						mActionBean.setmLocation(mLocation);
						CsstSHActionTable.getInstance().update(mDb, mActionBean);
						CsstSHAddModelAddAction.this.finish();
						
					}else{
						int mDelayTime = Integer.parseInt(mETActionDelay.getText().toString());
						CsstSHModelBean model = new CsstSHModelBean(modelName, "String");
						//插入设备id 以名字查看数据库中是否存在该名称的数据
						if(CsstSHModelTable.getInstance().columnExists(mDb, CsstSHModelTable.GEN_MODEL_NAME, modelName)){
							
							if(debug){
								System.out.println(" the modelName is "+modelName+"already in database");
							}
							mModelId = CsstSHModelTable.getInstance().query(mDb,modelName).getmodelId();
							
						}else{
							//没有就插入数据
							if(debug){
								System.out.println(" the modelName is "+modelName+" is new data in database");
							}
							mModelId =CsstSHModelTable.getInstance().insert(mDb, model);
						}
						 
						//ID
						int mModelid = (int)mModelId; 
						// 实例化ActionBean 用来查数据库
						CsstSHActionBean action = new CsstSHActionBean( mActionName,  mLocation,
								 mKeyCode, mDelayTime,  mModelid,1) ;
						// 实例化ActionBean 插入数据库
						long insert = CsstSHActionTable.getInstance().insert(mDb, action);
						System.out.println(" init insert is "+insert);
		//				Intent intent = new Intent();
		//				intent.setClass(CsstSHAddModelAddAction.this, CsstSHAddModelListActionView.class);
		//				intent.putExtra("modelName", modelName);
		//				startActivity(intent);
						CsstSHAddModelAddAction.this.finish();
					}
					
				}
			}
		}
	}
	
	/**
	 * 完成按钮监听器
	 * @author liuyang
	 */
	private final class WhichActoinBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(CsstSHAddModelAddAction.this, CsstSHAddModelWhichAction.class);
			intent.putExtra("modelName", modelName);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			CsstSHAddModelAddAction.this.finish();
		}
	}
	@Override
	protected void onDestroy() {
		mDb.close();
		super.onDestroy();
	}
	
	 public boolean onKeyDown(int keyCode,KeyEvent event) {   
	       // 是否触发按键为back键   
	       if (keyCode == KeyEvent.KEYCODE_BACK) {   
	           // 弹出 退出确认框   
	    	   backEvent();
	            return true;   
	        } 
	       return true;
	    }

}
