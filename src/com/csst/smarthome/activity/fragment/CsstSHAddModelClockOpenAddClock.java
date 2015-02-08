package com.csst.smarthome.activity.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHModelTable;
import com.csst.smarthome.util.NumericWheelAdapter;
import com.csst.smarthome.util.OnWheelChangedListener;
import com.csst.smarthome.util.OnWheelScrollListener;
import com.csst.smarthome.util.WheelView;
import com.csst.smarthome.util.clock.Alarm;
import com.csst.smarthome.util.clock.Alarms;

/**
 * 定时开启界面
 * @author liuyang
 */
public class CsstSHAddModelClockOpenAddClock extends Activity implements ICsstSHInitialize, ICsstSHConstant{
	private Context context = null;
	/** 返回按钮 */
	private Button mBtnCancel = null;
	/** 完成按钮 */
	private Button mBtnDone = null;
	/** 标题 */
//	private TextView mTVTitle = null;
	
	private BackBtnListener mBackBtnListener = null;
	private DoneBtnListener mBtnDoneListener = null;
	
	private String TAG = "CsstSHAddModelClockOpen";
	private boolean debug = true;
	
	/** 从whichAction 传过来的 keyCode */
	private String keyCode = null;
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	/**
	 * 滑轮
	 */
	private WheelView hour;
	private WheelView Min;
	private boolean timeScrolled = false;
	/**记录时间 小时和分钟*/
	int mHour, mMin;
	/*获取屏幕的宽度和高度*/
	private int displayWidth;
	private int displayHeight;
	/**
	 * 时间设置
	 */
	private TextView mTVtimesetting,mTVtimetap;
	private LinearLayout lltimesetting;
	/**
	 * 星期几
	 */
	private TextView mTV1,mTV2,mTV3,mTV4,mTV5,mTV6,mTV7;
	/**
	 * 添加判断是否是进入的是编辑修改界面
	 */
	private boolean meditFlag = false;
	private byte mbyteDay=0x00;
	/**
	 * modelName 用来查询数据库
	 */
	private String modelName = null ;
	private int modelId = 0;
	 private Alarm.DaysOfWeek mNewDaysOfWeek = new Alarm.DaysOfWeek(0);
	
	public Alarm.DaysOfWeek mDaysOfWeek = new Alarm.DaysOfWeek(0);
	
	//获取上面传过来的闹钟ID
	private int     mId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.context = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csst_model_openclockaddclock);
		Intent intent = getIntent();
		if (null != intent){
			 mId = intent.getIntExtra(Alarms.ALARM_ID, -1);
			modelName =(String)intent.getSerializableExtra("modelName");
			if(mId!=-1){
				meditFlag = true;
			}
		}
		if(debug){
			if(modelName!=null)
			System.out.println(TAG+" modelName is "+modelName+"the mId is "+mId);
			}
		initDataSource();
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
		//LoadLayout();
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
		super.onResume();
	}
	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
//		mTVTitle = (TextView) findViewById(R.id.mTVTitle);
		mBtnDone = (Button) findViewById(R.id.mBtnDone);
		mTVtimesetting =(TextView) findViewById(R.id.tvtimesetting);
//		mTVtimetap =(TextView) findViewById(R.id.tvtimemid);
		lltimesetting =(LinearLayout) findViewById(R.id.lltimesetting);
		hour = (WheelView) findViewById(R.id.hour);
		hour.setLabel(getString(R.string.csst_model_hour));
		Min = (WheelView) findViewById(R.id.min);
		Min.setLabel(getString(R.string.csst_model_mine));
		hour.setAdapter(new NumericWheelAdapter(0, 23));
		Min.setAdapter(new NumericWheelAdapter(0, 59));
		hour.setVisibleItems(5);
		Min.setVisibleItems(5);
		mTV1= (TextView) findViewById(R.id.tvmonday);
		mTV2= (TextView) findViewById(R.id.tvtuesday);
		mTV3= (TextView) findViewById(R.id.tvwednseday);
		mTV4= (TextView) findViewById(R.id.tvthurseday);
		mTV5= (TextView) findViewById(R.id.tvfriday);
		mTV6= (TextView) findViewById(R.id.tvsaturday);
		mTV7= (TextView) findViewById(R.id.tvsunday);
		
		
	
	}

	@Override
	public void initWidgetState() {
		//设置标题如果是编辑修改的话需要将数据进行填充
		if(meditFlag){
//			mTVTitle.setText(clockOpenBean.getmClockOpenName());
			//将设定的某天数据进行取出
			Alarm alarm = null;
			 /* load alarm details from database */
            alarm = Alarms.getAlarm(getContentResolver(), mId);
            // Bad alarm, bail to avoid a NPE.
            if (alarm == null) {
                finish();
                return;
            }
			mbyteDay = (byte)alarm.daysOfWeek.getCoded();
			if(debug){
				System.out.println(TAG+"the mbyteDay is  "+mbyteDay);
			}
			initDay();
			//设定滚轮到指定的初始值
			hour.setCurrentItem(alarm.hour);
			Min.setCurrentItem(alarm.minutes);
			
		}else{
//			mTVTitle.setText(context.getResources().getString(R.string.csst_model_addclock));	
		}
		
		mBtnCancel.setVisibility(View.GONE);
	}
	public void initDay(){
		System.out.println(" initDay the mbyteDay "+mbyteDay );
		if((mbyteDay&0x01)==0x01){
			mNewDaysOfWeek.set(0, true);
			mTV1.setBackgroundColor(context.getResources().getColor(R.color.orange));
		}else{
			mNewDaysOfWeek.set(0, false);
			mTV1.setBackgroundColor(context.getResources().getColor(R.color.currentbackground));
		}
		if((mbyteDay&0x02)==0x02){
			mNewDaysOfWeek.set(1, true);
			mTV2.setBackgroundColor(context.getResources().getColor(R.color.orange));
		}else{
			mNewDaysOfWeek.set(1, false);
			mTV2.setBackgroundColor(context.getResources().getColor(R.color.currentbackground));
			
		}
		if((mbyteDay&0x04)==0x04){
			mNewDaysOfWeek.set(2, true);
			mTV3.setBackgroundColor(context.getResources().getColor(R.color.orange));
		}else{
			mNewDaysOfWeek.set(2, false);
			mTV3.setBackgroundColor(context.getResources().getColor(R.color.currentbackground));
			
		}
		if((mbyteDay&0x08)==0x08){
			mNewDaysOfWeek.set(3, true);
			mTV4.setBackgroundColor(context.getResources().getColor(R.color.orange));
		}else{
			mNewDaysOfWeek.set(3, false);
			mTV4.setBackgroundColor(context.getResources().getColor(R.color.currentbackground));
			
		}
		if((mbyteDay&0x10)==0x10){
			mNewDaysOfWeek.set(4, true);
			mTV5.setBackgroundColor(context.getResources().getColor(R.color.orange));
		}else{
			mNewDaysOfWeek.set(4, false);
			mTV5.setBackgroundColor(context.getResources().getColor(R.color.currentbackground));
			
		}
		if((mbyteDay&0x20)==0x20){
			mNewDaysOfWeek.set(5, true);
			mTV6.setBackgroundColor(context.getResources().getColor(R.color.orange));
		}else{
			mNewDaysOfWeek.set(5, false);
			mTV6.setBackgroundColor(context.getResources().getColor(R.color.currentbackground));
			
		}
		if((mbyteDay&0x40)==0x40){
			mNewDaysOfWeek.set(6, true);
			mTV7.setBackgroundColor(context.getResources().getColor(R.color.orange));
		}else{
			mNewDaysOfWeek.set(6, false);
			mTV7.setBackgroundColor(context.getResources().getColor(R.color.currentbackground));
			
		}
	}

	@Override
	public void initWidgetListener() {
		mBackBtnListener = new BackBtnListener();
		mBtnDoneListener = new DoneBtnListener();
		Min.addChangingListener(wheelListener);
		hour.addChangingListener(wheelListener);
		hour.addScrollingListener(scrollListener);
		Min.addScrollingListener(scrollListener);
		mTV1.setOnClickListener(dayListener);
		mTV2.setOnClickListener(dayListener);
		mTV3.setOnClickListener(dayListener);
		mTV4.setOnClickListener(dayListener);
		mTV5.setOnClickListener(dayListener);
		mTV6.setOnClickListener(dayListener);
		mTV7.setOnClickListener(dayListener);
		
	}
	
	
	View.OnClickListener dayListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(debug){
					System.out.println(TAG+"the mbyteday is"+mbyteDay);
				}
				switch (v.getId()) {
				case R.id.tvmonday:
					if((mbyteDay&0x01)==0x01){
						mTV1.setBackgroundColor(context.getResources().getColor(R.color.currentbackground));
						mNewDaysOfWeek.set(0, false);
						mbyteDay=(byte)((mbyteDay&0xfe));
					}else{
						mbyteDay=(byte)(mbyteDay|0x01);
						mNewDaysOfWeek.set(0, true);
						mTV1.setBackgroundColor(context.getResources().getColor(R.color.orange));
					}
					break;
				case R.id.tvtuesday:
					if((mbyteDay&0x02)==0x02){
						mNewDaysOfWeek.set(1, false);
						mTV2.setBackgroundColor(context.getResources().getColor(R.color.currentbackground));
						mbyteDay=(byte)((mbyteDay&0xfd));
					}else{
						mNewDaysOfWeek.set(1, true);
						mbyteDay=(byte)(mbyteDay|0x02);
						mTV2.setBackgroundColor(context.getResources().getColor(R.color.orange));
					}
					break;
				case R.id.tvwednseday:
					if((mbyteDay&0x04)==0x04){
						mNewDaysOfWeek.set(2, false);
						mTV3.setBackgroundColor(context.getResources().getColor(R.color.currentbackground));
						mbyteDay=(byte)((mbyteDay&0xfb));
					}else{
						mNewDaysOfWeek.set(2, true);
						mbyteDay=(byte)(mbyteDay|0x04);
						mTV3.setBackgroundColor(context.getResources().getColor(R.color.orange));
					}
					break;
				case R.id.tvthurseday:
					if((mbyteDay&0x08)==0x08){
						mNewDaysOfWeek.set(3, false);
						mTV4.setBackgroundColor(context.getResources().getColor(R.color.currentbackground));
						mbyteDay=(byte)((mbyteDay&0xf7));
					}else{
						mNewDaysOfWeek.set(3, true);
						mbyteDay=(byte)(mbyteDay|0x08);
						mTV4.setBackgroundColor(context.getResources().getColor(R.color.orange));
					}
					break;
				case R.id.tvfriday:
					if((mbyteDay&0x10)==0x10){
						mNewDaysOfWeek.set(4, false);
						mTV5.setBackgroundColor(context.getResources().getColor(R.color.currentbackground));
						mbyteDay=(byte)((mbyteDay&0xef));
					}else{
						mNewDaysOfWeek.set(4, true);
						mbyteDay=(byte)(mbyteDay|0x10);
						mTV5.setBackgroundColor(context.getResources().getColor(R.color.orange));
					}
					break;
				case R.id.tvsaturday:
					if((mbyteDay&0x20)==0x20){
						mNewDaysOfWeek.set(5, false);
						mTV6.setBackgroundColor(context.getResources().getColor(R.color.currentbackground));
						mbyteDay=(byte)((mbyteDay&0xdf));
					}else{
						mNewDaysOfWeek.set(5, true);
						mbyteDay=(byte)(mbyteDay|0x20);
						mTV6.setBackgroundColor(context.getResources().getColor(R.color.orange));
					}
					break;
				case R.id.tvsunday:
					if((mbyteDay&0x40)==0x40){
						mNewDaysOfWeek.set(6, false);
						mTV7.setBackgroundColor(context.getResources().getColor(R.color.currentbackground));
						mbyteDay=(byte)((mbyteDay&0xbf));
					}else{
						mNewDaysOfWeek.set(6, true);
						mbyteDay=(byte)(mbyteDay|0x40);
						mTV7.setBackgroundColor(context.getResources().getColor(R.color.orange));
					}
					break;
				default:
					break;
				}	
			}
		};
	
	
	
	
	OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (!timeScrolled) {
				mHour = hour.getCurrentItem()+1;	
				mMin =  Min.getCurrentItem()+1;	
			}
		}
	};
	
	OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {
			timeScrolled = true;
		}

		public void onScrollingFinished(WheelView wheel) {
			timeScrolled = false;
			mHour = hour.getCurrentItem()+1;	
			mMin =  Min.getCurrentItem()+1;	
		}
	};
	@Override
	public void addWidgetListener() {
		mBtnCancel.setOnClickListener(mBackBtnListener);
		mBtnDone.setOnClickListener(mBtnDoneListener);
	}
	
	/**
	 * 返回事件
	 */
	private final void backEvent(){
//		Intent intent = new Intent();
//		intent.setClass(CsstSHAddModelClockOpenAddClock.this, CsstSHAddModelListActionView.class);
//		intent.putExtra("modelName", modelName);
//		startActivity(intent);
		CsstSHAddModelClockOpenAddClock.this.finish();
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
			//判断名字是否为空 重复时间是否选择
			 if(mbyteDay==0x00){
				Toast.makeText(CsstSHAddModelClockOpenAddClock.this, R.string.csst_model_clockdaynulltip, Toast.LENGTH_LONG).show();;
			}else{
				modelId= CsstSHModelTable.getInstance().query(mDb,modelName).getmodelId();
				//以modelname 查询到ID ，再用ID 查询，和名称查询在数据库中是否存在这个名字 在columnExistsbyname函数中是查询到这个model id 下的全部 定时数据 再看看这些数据中有没有一个跟传进去的
				//clockOpenName 一样的，因为在clockOpentable 中存在不同的model 下的clockopen数据 
//				if(CsstSHClockOpenTable.getInstance().columnExistsbyname(mDb,modelId,mEVtimename.getText().toString())){
					if(meditFlag){//是修改的数据则根据已经有的ID更新数据
//						clockOpenBean.setmClockOpenDay(mbyteDay);
//						clockOpenBean.setmClockOpenTimeHour(hour.getCurrentItem()+1);
//						clockOpenBean.setmClockOpenTimeMin(Min.getCurrentItem()+1);
//						CsstSHClockOpenTable.getInstance().update(mDb, clockOpenBean);
//					}else{//提醒数据库中已经存在名称为此的数据
//						Toast.makeText(CsstSHAddModelClockOpenAddClock.this, R.string.csst_model_clocknamenrepeat, Toast.LENGTH_LONG).show();;
//					}
						
				  Alarm alarm = new Alarm();
					alarm.id = mId;
			        alarm.enabled =true;
			        alarm.hour = hour.getCurrentItem();
			        alarm.minutes = Min.getCurrentItem();
			        mDaysOfWeek.set(mNewDaysOfWeek);
			        alarm.daysOfWeek = mDaysOfWeek;
			        alarm.vibrate = false;
			        alarm.label = modelName+getResources().getString(R.string.csst_model_readystartup);
			        alarm.alert = null;
			        alarm.modelid = modelId;	
			        Alarms.setAlarm(CsstSHAddModelClockOpenAddClock.this, alarm);	
			        CsstSHAddModelClockOpenAddClock.this.finish();
						
				}else{//插入数据
					// 实例化ClockOpenBean 用来查数据库
//					clockOpenBean = new CsstClockOpenBean();
//					clockOpenBean.setmClockOpenDay(mbyteDay);
////					clockOpenBean.setmClockOpenName(mEVtimename.getText().toString());
//					clockOpenBean.setmClockOpenTimeHour(hour.getCurrentItem()+1);
//					clockOpenBean.setmClockOpenTimeMin(Min.getCurrentItem()+1);
//					clockOpenBean.setmClockOpenModelId(modelId);
//					clockOpenBean.setmClockOpenopenFlag(0);
					// 实例化ActionBean 插入数据库
					
					Alarm alarm = new Alarm();
					alarm.id = mId;
			        alarm.enabled =true;
			        alarm.hour = hour.getCurrentItem();
			        alarm.minutes = Min.getCurrentItem();
			        mDaysOfWeek.set(mNewDaysOfWeek);
			        alarm.daysOfWeek = mDaysOfWeek;
			        alarm.vibrate = false;
			        alarm.label = modelName+getResources().getString(R.string.csst_model_readystartup);
			        alarm.alert = null;
			        alarm.modelid = modelId;
					
			        Alarms.addAlarm(CsstSHAddModelClockOpenAddClock.this, alarm);
					
					Toast.makeText(CsstSHAddModelClockOpenAddClock.this, R.string.csst_model_clockaddsuc, Toast.LENGTH_LONG).show();
					CsstSHAddModelClockOpenAddClock.this.finish();
				}
			}
			
			
		}
	}
	
	@Override
	protected void onDestroy() {
		mDb.close();
		super.onDestroy();
	}
	

	/**
	 * 删除楼层
	 * @param floor
	 */
	private final void insertClockOpenTip(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(getString(R.string.csst_del_model_message, modelName));
		builder.setNegativeButton(R.string.csst_cancel, null);
		builder.setPositiveButton(R.string.csst_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		builder.show();
	}
	

	public void LoadLayout() {
		System.out.println("111");
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				displayWidth, (int) (displayHeight * 0.077f + 0.5f)); 
		System.out.println("55");

//		RelativeLayout.LayoutParams paramsDone = new RelativeLayout.LayoutParams(
//				 (int) (displayWidth * 0.175f + 0.5f), 
//				 (int) (displayHeight * 0.075f + 0.5f)); 
//		paramsDone.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//		done.setLayoutParams(paramsDone);
//
//		RelativeLayout.LayoutParams paramsCancel = new RelativeLayout.LayoutParams(
//				 (int) (displayWidth * 0.175f + 0.5f), 
//				 (int) (displayHeight * 0.075f + 0.5f)); 
//		cancel.setLayoutParams(paramsCancel);
//		
//		RelativeLayout.LayoutParams paramsquery = new RelativeLayout.LayoutParams(
//				 (int) (displayWidth * 0.2f + 0.5f), 
//				 (int) (displayHeight * 0.085f + 0.5f));
//		paramsquery.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//		paramsquery.setMargins(10, 10, 10, 10);
//		btnQuery.setLayoutParams(paramsquery);
//		
		
		LinearLayout.LayoutParams paramsLinaer1 = new LinearLayout.LayoutParams(
				 (int) (displayWidth * 0.99f + 0.5f), 
				 (int) (displayHeight * 0.52f + 0.5f)); 
		paramsLinaer1.setMargins(10, 10, 10, 10);
		paramsLinaer1.gravity=Gravity.CENTER;
		lltimesetting.setLayoutParams(paramsLinaer1);
		
		
//		LinearLayout.LayoutParams paramsLinaer2 = new LinearLayout.LayoutParams(
//				 (int) (displayWidth * 0.22f + 0.5f), 
//				 (int) (displayHeight * 0.52f + 0.5f)); 
//		hour.setLayoutParams(paramsLinaer2);
//		Min.setLayoutParams(paramsLinaer2);
//		System.out.println("33");
		
		
		
//		LinearLayout.LayoutParams paramsText1 = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.WRAP_CONTENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT);
//		paramsText1.leftMargin =(int) (displayWidth * 0.212f + 0.5f);
//		show1.setLayoutParams(paramsText1);
//		LinearLayout.LayoutParams paramsText3 = new LinearLayout.LayoutParams(
//		LinearLayout.LayoutParams.WRAP_CONTENT,
//		LinearLayout.LayoutParams.WRAP_CONTENT);
//		paramsText3.leftMargin =(int) (displayWidth * 0.182f + 0.5f);		
//		textView.setLayoutParams(paramsText3);
//		System.out.println("22222222222");
//		LinearLayout.LayoutParams paramsText2 = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.WRAP_CONTENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT);
//		paramsText2.rightMargin =  (int) (displayWidth * 0.19f + 0.5f);
//		show2.setLayoutParams(paramsText2);
//		textView2.setLayoutParams(paramsText2);
//		System.out.println("888");

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
