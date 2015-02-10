package com.csst.smarthome.activity.fragment;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.SmartStartActivity;
import com.csst.smarthome.activity.adapter.CsstSHAlarmAdapter;
import com.csst.smarthome.activity.adapter.CsstSHClockAdapter;
import com.csst.smarthome.bean.CsstClockOpenBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHModelTable;
import com.csst.smarthome.util.clock.Alarm;
import com.csst.smarthome.util.clock.Alarms;

/**
 * 定时开启界面
 * @author liuyang
 */
public class CsstSHAddModelClockOpenMain extends Activity implements ICsstSHInitialize, ICsstSHConstant{

	/** 返回按钮 */
	private Button mBtnCancel = null;
	/** 完成按钮 */
	private Button mBtnDone = null;
	/** 标题 */
	private TextView mTVTitle = null;
	
	private BackBtnListener mBackBtnListener = null;
	private DoneBtnListener mBtnDoneListener = null;
	private AddActionBtnListener mBtnAddActionListener = null;
	private Button mbtnAddClockOpen ;
	
	private String TAG = "CsstSHAddModelClockOpenMain";
	private boolean debug = true;
	/**
	 * 上层传过来的modelname
	 */
	private String modelName = null;
	
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	/** 定时列表 */
	private ListView mlistviewclockopen = null;
	/** 定时配器 */
	private CsstSHClockAdapter clockOpenAdapter = null;
	/** 定时数据集合 */
	private List<CsstClockOpenBean> clockopenBeans = null;
	
	
	//alarm list
	private List<Alarm> alarmList = null;
	
	
	/** alarm配器 */
	private CsstSHAlarmAdapter alarmAdapter = null;
	
	//定时开启
	RadioButton mrbtnauto,mrbtnClockOpen;
	RadioGroup mrgbtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csst_model_openclockmain);
		Intent intent = getIntent();
		if (null != intent){
			modelName =(String) intent.getSerializableExtra("modelName");
		}
		if(debug){
			System.out.println(TAG+"initDataSource initDataSource the name is "+modelName
					);
			}
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
	protected void onResume() {
		if(debug){
			System.out.println(TAG+"the onResume");
		}
		//刷新列表
	//查询数据库中是否有以这个名称存储的action 数据，有的话就显示出来
			if(CsstSHModelTable.getInstance().query(mDb,modelName)!=null){
				alarmList=Alarms.queryByModel(getContentResolver(),CsstSHModelTable.getInstance().query(mDb,modelName).getmodelId());
				//修改用新的alarm 数据库
				if(alarmList!=null){
					alarmAdapter = new CsstSHAlarmAdapter(CsstSHAddModelClockOpenMain.this, alarmList);
					mlistviewclockopen.setAdapter(alarmAdapter);
				}
				
				
//				clockopenBeans = CsstSHModelTable.getInstance().getClockOpenByModelId(mDb, CsstSHModelTable.getInstance().query(mDb,modelName).getmodelId());
//				if(clockopenBeans!=null){
//					clockOpenAdapter = new CsstSHClockAdapter(CsstSHAddModelClockOpenMain.this, clockopenBeans);
//					mlistviewclockopen.setAdapter(clockOpenAdapter);
//				}
				
			}
		super.onResume();
	}
	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
		mTVTitle = (TextView) findViewById(R.id.mTVTitle);
		mBtnDone = (Button) findViewById(R.id.mBtnDone);
		mbtnAddClockOpen =  (Button) findViewById(R.id.btaddclock);
		mrbtnauto =(RadioButton)(Button) findViewById(R.id.radioButtonauto);
		mrbtnClockOpen =(RadioButton)(Button) findViewById(R.id.radioButtonclockopen);
		mrgbtn =(RadioGroup)findViewById(R.id.radioGroup);
//		mrgbtn.check(mrbtnauto);
		mlistviewclockopen = (ListView)findViewById(R.id.lv_clocklistview);
		
	
	}

	@Override
	public void initWidgetState() {
		//设置标题
		mTVTitle.setText(R.string.csst_model_clockopen);
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		
		//  以modelName 查询该情景模式的实例化
		//然后再用实例化获取modelID，再用modelID查询 查询改模式下的所有的动作
		if(debug){
			System.out.println("query the clockopenBeans the name is "+modelName);
		}
		//刷新列表
	//查询数据库中是否有以这个名称存储的action 数据，有的话就显示出来
			if(CsstSHModelTable.getInstance().query(mDb,modelName)!=null){
				alarmList=Alarms.queryByModel(getContentResolver(),CsstSHModelTable.getInstance().query(mDb,modelName).getmodelId());
				
				if(alarmList!=null){
					alarmAdapter = new CsstSHAlarmAdapter(CsstSHAddModelClockOpenMain.this, alarmList);
					mlistviewclockopen.setAdapter(alarmAdapter);
				}
				
				
//				clockopenBeans = CsstSHModelTable.getInstance().getClockOpenByModelId(mDb, CsstSHModelTable.getInstance().query(mDb,modelName).getmodelId());
//				if(clockopenBeans!=null){
//					clockOpenAdapter = new CsstSHClockAdapter(CsstSHAddModelClockOpenMain.this, clockopenBeans);
//					mlistviewclockopen.setAdapter(clockOpenAdapter);
//				}
				
			}
		if(debug){
			System.out.println(" the listview press init is here ");
		}
		// 列表的点击事件
		mlistviewclockopen.setOnItemClickListener(new OnItemClickListener() {
						@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long arg3) {
						if(debug){
							System.out.println(" the listview press is here ");
						}
						Intent intent = new Intent();
						intent.setClass(CsstSHAddModelClockOpenMain.this, CsstSHAddModelClockOpenAddClock.class);
						intent.putExtra("modelName", modelName);
						intent.putExtra(Alarms.ALARM_ID, alarmList.get(position).id);
						startActivity(intent);
				}
			});
		if(debug){
			System.out.println(" the listview long press init is here ");
		}
//		楼层列表的长按监听事件
		mlistviewclockopen.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				if(debug){
					System.out.println(" the listview longpress is here ");
				}
				final Alarm alarm=alarmList.get(position);
				final int id = (int) alarm.id;
				AlertDialog.Builder builder = new AlertDialog.Builder(CsstSHAddModelClockOpenMain.this);
//				builder.setTitle(clockopeBean.getmClockOpenName());
				builder.setItems(R.array.csst_clockopendelet, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							//重命名
							case 0:
								Alarms.deleteAlarm(CsstSHAddModelClockOpenMain.this, id);
							//刷新列表
							//查询数据库中是否有以这个名称存储的action 数据，有的话就显示出来
							if(CsstSHModelTable.getInstance().query(mDb,modelName)!=null){
								alarmList=Alarms.queryByModel(getContentResolver(),CsstSHModelTable.getInstance().query(mDb,modelName).getmodelId());
								
								if(alarmList!=null){
									alarmAdapter = new CsstSHAlarmAdapter(CsstSHAddModelClockOpenMain.this, alarmList);
									mlistviewclockopen.setAdapter(alarmAdapter);
								}
							}
								break;
							//删除
							case 1:
								
								
								break;
						}
					}
				});
				builder.setNegativeButton(R.string.csst_cancel, null);
				Dialog d = builder.show();
				d.setCanceledOnTouchOutside(true);
				return true;
			}
		});
	}

	@Override
	public void initWidgetListener() {
		mBackBtnListener = new BackBtnListener();
		mBtnDoneListener = new DoneBtnListener();
		mBtnAddActionListener = new AddActionBtnListener();
	}

	@Override
	public void addWidgetListener() {
		mBtnCancel.setOnClickListener(mBackBtnListener);
		mBtnDone.setOnClickListener(mBtnDoneListener);
		mbtnAddClockOpen.setOnClickListener(mBtnAddActionListener);
		
		 //设置事件监听
		mrgbtn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
             if (checkedId == mrbtnauto.getId()) {
            	 
        } else {
        	
             }
         }
     });   
		
		
		
	}
	
	/**
	 * 返回事件
	 */
	private final void backEvent(){
		Intent intent = new Intent();
		intent.setClass(CsstSHAddModelClockOpenMain.this, SmartStartActivity.class);
		startActivity(intent);
		CsstSHAddModelClockOpenMain.this.finish();
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
			//更新数据库
			
			
			//finish本界面
			Intent intent = new Intent();
			intent.setClass(CsstSHAddModelClockOpenMain.this, SmartStartActivity.class);
			startActivity(intent);
			CsstSHAddModelClockOpenMain.this.finish();
			
		}
	}

	private final class AddActionBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(CsstSHAddModelClockOpenMain.this, CsstSHAddModelClockOpenAddClock.class);
			intent.putExtra("modelName", modelName);
			startActivity(intent);
			
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
