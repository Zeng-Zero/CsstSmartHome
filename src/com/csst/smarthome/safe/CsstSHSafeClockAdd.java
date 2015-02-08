package com.csst.smarthome.safe;

import java.net.DatagramPacket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSafeClockBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHSafeClockTable;
import com.csst.smarthome.util.CsstSHConfigPreference;
import com.csst.smarthome.util.NumericWheelAdapter;
import com.csst.smarthome.util.OnWheelChangedListener;
import com.csst.smarthome.util.OnWheelScrollListener;
import com.csst.smarthome.util.WheelView;
import com.csst.smarthome.util.clock.Alarm;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.message.rfcode.SendRFCodeReqMessage;

/**
 * 定时开启界面
 * @author liuyang
 */
public class CsstSHSafeClockAdd extends Activity implements ICsstSHInitialize, ICsstSHConstant{
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
	
	private final byte MODIFY_SAFE_CLOCK=3;
	private final byte ADD_SAFE_CLOCK =1;
	
	private final byte SELECT_ARM=1;
	private final byte SELECT_DISARM =0;
	
	private boolean timeScrolled = false;
	/**记录时间 小时和分钟*/
	int mHour, mMin;
	/*获取屏幕的宽度和高度*/
	private int displayWidth;
	private int displayHeight;
	/**
	 * 时间设置
	 */
	private TextView mTVtimesetting,mTVtimetap,etmodelname;
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
	private CsstSafeClockBean modifyClockBean=null;
	
	/** 配置文件 */
	private CsstSHConfigPreference configPreference = null;
	/** 主控MAC地址 */
	private String mMacAdress = null;
	
	//定时开启
	RadioButton mrbtArm,mrbtnDisarm;
	RadioGroup mrgbtn;
	int selectwho = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.context = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csst_safe_addclock);
		Intent intent = getIntent();
		if (null != intent){
			modifyClockBean = (CsstSafeClockBean)intent.getSerializableExtra("CsstSafeClockBean");
			if(modifyClockBean!=null){
			meditFlag = true;
		}
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
		configPreference = new CsstSHConfigPreference(this, CsstSHPreference);
		mMacAdress = configPreference.getMacAdress();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
		mBtnDone = (Button) findViewById(R.id.mBtnDone);
		mTVtimesetting =(TextView) findViewById(R.id.tvtimesetting);
		mrbtArm =(RadioButton)(Button) findViewById(R.id.radioButtonauto);
		mrbtnDisarm =(RadioButton)(Button) findViewById(R.id.radioButtonclockopen);
		mrgbtn =(RadioGroup)findViewById(R.id.radioGroup);
		
		
		etmodelname = (TextView)findViewById(R.id.etmodelname);
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
			mbyteDay = (byte)modifyClockBean.getmClockOpenDay();
			if(debug){
				System.out.println(TAG+"the mbyteDay is  "+mbyteDay);
			}
			etmodelname.setText(modifyClockBean.getmClockName());
			initDay();
			//设定滚轮到指定的初始值
			hour.setCurrentItem(modifyClockBean.getmClockOpenTimeHour());
			Min.setCurrentItem(modifyClockBean.getmClockOpenTimeMin());
			if(modifyClockBean.getmClockOpenopenFlag()==1){
				mrbtArm.setChecked(true);
				selectwho = SELECT_ARM;
			}else{
				mrbtnDisarm.setChecked(true);
				selectwho = SELECT_DISARM;
			}
			
		}else{
			selectwho = SELECT_DISARM;
			mrbtnDisarm.setChecked(true);	
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

		 //设置事件监听
		mrgbtn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
       public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == mrbtnDisarm.getId()) {
           	 selectwho = SELECT_DISARM;
       } else {
    	   selectwho = SELECT_ARM;
            }
        }
    });   
		
	}
	
	/**
	 * 返回事件
	 */
	private final void backEvent(){
//		Intent intent = new Intent();
//		intent.setClass(CsstSHAddModelClockOpenAddClock.this, CsstSHAddModelListActionView.class);
//		intent.putExtra("modelName", modelName);
//		startActivity(intent);
		CsstSHSafeClockAdd.this.finish();
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
			if(etmodelname.getText().toString()==null){
				Toast.makeText(CsstSHSafeClockAdd.this, R.string.csst_model_clocknamenulltip, Toast.LENGTH_LONG).show();;
			}else if(mbyteDay==0x00){
				Toast.makeText(CsstSHSafeClockAdd.this, R.string.csst_model_clockdaynulltip, Toast.LENGTH_LONG).show();;
			}else{
				//以modelname 查询到ID ，再用ID 查询，和名称查询在数据库中是否存在这个名字 在columnExistsbyname函数中是查询到这个model id 下的全部 定时数据 再看看这些数据中有没有一个跟传进去的
				//clockOpenName 一样的，因为在clockOpentable 中存在不同的model 下的clockopen数据 
//				if(CsstSHClockOpenTable.getInstance().columnExistsbyname(mDb,modelId,mEVtimename.getText().toString())){
					if(meditFlag){//是修改的数据则根据已经有的ID更新数据
						modifyClockBean.setmClockName(etmodelname.getText().toString());
						modifyClockBean.setmClockOpenDay(mbyteDay);
						modifyClockBean.setmClockOpenTimeHour(hour.getCurrentItem());
						modifyClockBean.setmClockOpenTimeMin(Min.getCurrentItem());
						modifyClockBean.setmClockOpenopenFlag(selectwho);
						CsstSHSafeClockTable.getInstance().update(mDb, modifyClockBean);
						StringBuffer sb = new StringBuffer();
						//唯一识别号
						sb.append(String.format("%02x", (byte)modifyClockBean.getmClockOpenId()));
						sb.append(",");
						sb.append(etmodelname.getText().toString());
						sb.append(",");
						sb.append(modifyClockBean.getmClockArm());
						sb.append(",");
						sb.append(String.format("%02x", (byte)hour.getCurrentItem()));
						sb.append(",");
						sb.append(String.format("%02x", (byte)Min.getCurrentItem()));
						sb.append(",");
//						sb.append(String.format("%02x", (byte)mbyteDay));
						
						if((mbyteDay&0x01)==0x01){
							sb.append(1);
						}
						if((mbyteDay&0x02)==0x02){
							sb.append(2);
						}
						if((mbyteDay&0x04)==0x04){
							sb.append(3);
						}
						if((mbyteDay&0x08)==0x08){
							sb.append(4);
						}
						if((mbyteDay&0x10)==0x10){
							sb.append(5);
						}
						if((mbyteDay&0x20)==0x20){
							sb.append(6);
						}
						if((mbyteDay&0x40)==0x40){
							sb.append(7);
						}
						sb.append(",");
						sb.append(String.format("%02x", (byte)selectwho));
						
						String sendphone = sb.toString();
						Log.d(TAG,"the send buffer is "+sendphone);
						new sendClockdata(MODIFY_SAFE_CLOCK,sendphone.getBytes()).cancel(true);//默认UTF8
						new sendClockdata(MODIFY_SAFE_CLOCK,sendphone.getBytes()).execute();//默认UTF8
						
						
						
//					}else{//提醒数据库中已经存在名称为此的数据
//						Toast.makeText(CsstSHAddModelClockOpenAddClock.this, R.string.csst_model_clocknamenrepeat, Toast.LENGTH_LONG).show();;
//					}
						
//				  Alarm alarm = new Alarm();
//					alarm.id = mId;
//			        alarm.enabled =true;
//			        alarm.hour = hour.getCurrentItem();
//			        alarm.minutes = Min.getCurrentItem();
//			        mDaysOfWeek.set(mNewDaysOfWeek);
//			        alarm.daysOfWeek = mDaysOfWeek;
//			        alarm.vibrate = false;
//			        alarm.label = modelName+getResources().getString(R.string.csst_model_readystartup);
//			        alarm.alert = null;
//			        alarm.modelid = modelId;	
//			        Alarms.setAlarm(CsstSHAddModelClockOpenAddClock.this, alarm);	
			        CsstSHSafeClockAdd.this.finish();
						
				}else{//插入数据
					// 实例化ClockOpenBean 用来查数据库
					CsstSafeClockBean clockOpenBean = new CsstSafeClockBean();
					clockOpenBean.setmClockName(etmodelname.getText().toString());
					clockOpenBean.setmClockOpenDay(mbyteDay);
					clockOpenBean.setmClockOpenTimeHour(hour.getCurrentItem());
					clockOpenBean.setmClockOpenTimeMin(Min.getCurrentItem());
					clockOpenBean.setmClockOpenopenFlag(selectwho);
					clockOpenBean.setmClockArm(1);
//					 实例化ActionBean 插入数据库
					int clockid =(int)CsstSHSafeClockTable.getInstance().insert(mDb, clockOpenBean);
					
//					Alarm alarm = new Alarm();
//					alarm.id = mId;
//			        alarm.enabled =true;
//			        alarm.hour = hour.getCurrentItem();
//			        alarm.minutes = Min.getCurrentItem();
//			        mDaysOfWeek.set(mNewDaysOfWeek);
//			        alarm.daysOfWeek = mDaysOfWeek;
//			        alarm.vibrate = false;
//			        alarm.label = modelName+getResources().getString(R.string.csst_model_readystartup);
//			        alarm.alert = null;
//			        alarm.modelid = modelId;
//					
//			        Alarms.addAlarm(CsstSHAddModelClockOpenAddClock.this, alarm);
					
					Toast.makeText(CsstSHSafeClockAdd.this, R.string.csst_model_clockaddsuc, Toast.LENGTH_LONG).show();
					CsstSHSafeClockAdd.this.finish();
					
					
					
					StringBuffer sb = new StringBuffer();
					//唯一识别号
					sb.append(String.format("%02x", (byte)clockid));
					sb.append(",");
					sb.append(etmodelname.getText().toString());
					sb.append(",");
					sb.append(1);
					sb.append(",");
					sb.append(String.format("%02x", (byte)hour.getCurrentItem()));
					sb.append(",");
					sb.append(String.format("%02x", (byte)Min.getCurrentItem()));
					sb.append(",");
//					sb.append(String.format("%02x", (byte)mbyteDay));
					
					if((mbyteDay&0x01)==0x01){
						sb.append(1);
					}
					if((mbyteDay&0x02)==0x02){
						sb.append(2);
					}
					if((mbyteDay&0x04)==0x04){
						sb.append(3);
					}
					if((mbyteDay&0x08)==0x08){
						sb.append(4);
					}
					if((mbyteDay&0x10)==0x10){
						sb.append(5);
					}
					if((mbyteDay&0x20)==0x20){
						sb.append(6);
					}
					if((mbyteDay&0x40)==0x40){
						sb.append(7);
					}
					
					
					
					
					sb.append(",");
					sb.append(String.format("%02x", (byte)selectwho));
					
					String sendphone = sb.toString();
					Log.d(TAG,"the send buffer is "+sendphone);
					new sendClockdata(ADD_SAFE_CLOCK,sendphone.getBytes()).cancel(true);//默认UTF8
					new sendClockdata(ADD_SAFE_CLOCK,sendphone.getBytes()).execute();//默认UTF8
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
	 private final class sendClockdata extends AsyncTask<Void, Void, Boolean>{
			byte[] param;
			byte cmd ;
			private Dialog mDialog = null;
			public sendClockdata(byte cmd,byte[] param){
				//清空数据
				this.cmd = cmd;
				this.param = param;
			}
			
			@Override
			protected Boolean doInBackground(Void... params) {
				int flag = 0;
				long toid = 109860815673L;
				ServerItemModel sim = new ServerItemModel();
				String msgBuffer ="1111";
				msgBuffer = mMacAdress;
				//
				int length = 0;
				if(param!=null){
					length =param.length+2; 
				}else{
					length = 2;
				}
				byte[] contentBuf = new byte[length];
			
					contentBuf[0]=(byte)0x10;
					
					contentBuf[1]=(byte)cmd;
					if(length>2){
						for(int m=0;m<length-2;m++){
							contentBuf[m+2]= param[m];
						}
					}
					for(int n=0;n<length;n++){
						System.out.println(TAG+ " safeSendCmd phone contentbuf setting " + n + " value: " + contentBuf[n]);
					}
					sim.setIpaddress(GobalDef.SERVER_URL);
					sim.setPort(GobalDef.SERVER_PORT);
					
					
					SendRFCodeReqMessage slrm = new SendRFCodeReqMessage();
					slrm.Direct = MessageDef.BASE_MSG_FT_REQ;
					slrm.setFromId(GobalDef.MOBILEID);
					slrm.MsgType = MessageDef.MESSAGE_TYPE_RFCODE_SEND_REQ;
					slrm.Seq = MessageSeqFactory.GetNextSeq();
					slrm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
					slrm.ToType = MessageDef.BASE_MSG_FT_HUB;
					slrm.setToId(Long.valueOf(msgBuffer));
					System.out.println(TAG+"");
					System.out.println(TAG+"the mac adress is "+Long.valueOf(msgBuffer));
					slrm.setCodeBuf(contentBuf);
					DatagramPacket msg = null;
					msg = UdpProcess_ZQL.GetMsgReturn(slrm, sim);
					if(msg == null){
						System.out.println("safeSendCmd suc fail fail fail  fail  fail");
						return false;
					} else {
						byte[] getbuf = new byte[msg.getLength()] ;
						for(int i=0;i<msg.getLength();i++){
							System.out.println(" UdpProcess_ZQL_SAFE get the message the [  "+i+"]"+msg.getData()[i]);
							getbuf[i]= msg.getData()[i];
						}
						System.out.println("safeSendCmd suc ok ok ok  ok  ok the msg is "+new String(getbuf));
						System.out.println("safeSendCmd get messega finish ");
						//先停止原来来定时器 因为两个共用一个定时器 如果不先取消原来的定时器的话就会造成错乱
						//停止发送命
					}
					
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				
				if(!result){
//					Toast.makeText(CsstSHSaftListView.this, getResources().getString(R.string.csst_safe_search_failtip), Toast.LENGTH_SHORT).show();
				}
				
			}
		}
	

}
