package com.csst.smarthome.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.SmartStartActivity;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;

/**
 * 中控配置
 * @author liuyang
 */
public class CsstSHAddModelName extends Activity implements ICsstSHInitialize, ICsstSHConstant{

	/** 返回按钮 */
	private Button mBtnCancel = null;
	/** 完成按钮 */
	private Button mBtnDone = null;
	/** 标题 */
	private TextView mTVTitle = null;
	/** 情景模式图片 */
	private ImageView mImgModel = null;
	/** 情景模式名称标签 */
	private TextView mTVModelName = null;
	/** 情景模式名称 */
	private EditText mETModelName = null;
	
	private BackBtnListener mBackBtnListener = null;
	private DoneBtnListener mBtnDoneListener = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csst_modeladd_name);
		initDataSource();
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
	}
	
	@Override
	public void initDataSource() {
		
	}

	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
		mTVTitle = (TextView) findViewById(R.id.mTVTitle);
		mBtnDone = (Button) findViewById(R.id.mBtnDone);
		mETModelName = (EditText) findViewById(R.id.evactiondelaytime);
		mTVModelName = (TextView) findViewById(R.id.tvactiondelaytime);
	}

	@Override
	public void initWidgetState() {
		//设置标题
		mTVTitle.setText(R.string.csst_addmodel_title);
		//隐藏完成按钮
//		mBtnDone.setVisibility(View.GONE);
		//链接热点
	}

	@Override
	public void initWidgetListener() {
		mBackBtnListener = new BackBtnListener();
		mBtnDoneListener = new DoneBtnListener();
	}

	@Override
	public void addWidgetListener() {
		mBtnCancel.setOnClickListener(mBackBtnListener);
		mBtnDone.setOnClickListener(mBtnDoneListener);
	}
	
	/**
	 * 返回事件
	 */
	private final void backEvent(){
		Intent intent = new Intent();
		intent.setClass(CsstSHAddModelName.this, SmartStartActivity.class);
//		intent.putExtra("IP", modemIp);
		startActivity(intent);
		CsstSHAddModelName.this.finish();
	}
	
	/**
	 * 返回按钮监听器
	 * @author liuyang
	 */
	private final class BackBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(CsstSHAddModelName.this, SmartStartActivity.class);
//			intent.putExtra("IP", modemIp);
			startActivity(intent);
			CsstSHAddModelName.this.finish();
		}
	}
	
	/**
	 * 完成按钮监听器
	 * @author liuyang
	 */
	private final class DoneBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if(mETModelName.getText().toString().equals("")){
				Toast.makeText(CsstSHAddModelName.this, R.string.csst_model_name_null_tip, Toast.LENGTH_LONG).show();;
			}else{
				Intent intent = new Intent();
				intent.setClass(CsstSHAddModelName.this, CsstSHAddModelListActionView.class);
				intent.putExtra("EditModel", "ADDModel");
				intent.putExtra("modelName", mETModelName.getText().toString());
				CsstSHAddModelName.this.finish();
				startActivity(intent);
			}
		
			
		}
	}
	
	public boolean onKeyDown(int keyCode,KeyEvent event) {   
	       // 是否触发按键为back键   
	       if (keyCode == KeyEvent.KEYCODE_BACK) {   
	           // 弹出 退出确认框
	    	   backEvent();
    		   System.out.println(" press the delete key is here ");
	            return true;   
	        } 
	       
	       System.out.println(" press the delete key is here ");
	         return true;
	    }
	

}
