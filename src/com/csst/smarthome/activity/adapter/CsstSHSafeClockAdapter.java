package com.csst.smarthome.activity.adapter;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSafeClockBean;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHSafeClockTable;


/**
 * 设备列表设配器
 * @author liuyang
 */
public class CsstSHSafeClockAdapter extends BaseAdapter {
	private Context context = null;
	private List<CsstSafeClockBean> clockBeans = null;
	private String TAG = "CsstSHClockAdapter";
	private boolean debug = true;
	private int i=0;
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;

	public CsstSHSafeClockAdapter(Context context, List<CsstSafeClockBean> clockBeans) {
		this.context = context;
		this.clockBeans = clockBeans;
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(context);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();

	}
	public CsstSHSafeClockAdapter(){
		
	}
	public final void setDevices(List<CsstSafeClockBean> clockBeans){
		this.clockBeans = clockBeans;
		notifyDataSetInvalidated();
	}

	@Override
	public int getCount() {
		return null == clockBeans ? 0 : clockBeans.size();
	}
	
	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(debug){
			System.out.println(TAG+"the CsstSHActionAdapter getview ");
		}
		final CsstSafeClockBean clockBean = clockBeans.get(position);
		convertView = LayoutInflater.from(context).inflate(R.layout.csst_safe_clockopen_listview,null);
		//Button btn_open = (Button) convertView.findViewById(R.id.item_open);
		TextView tvActiondelay =(TextView) convertView.findViewById(R.id.tvclocktime_listview);
		TextView tvActionlocation =(TextView) convertView.findViewById(R.id.tvclockrepeat_listview);
		
		TextView tvalarmname_listview =(TextView) convertView.findViewById(R.id.tvalarmname_listview);
		TextView tvstatus = (TextView) convertView.findViewById(R.id.tvalarmstatus_listview);
//		mbtnopen.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				if(debug){
//					System.out.println(TAG+" btn button on click  ");
//				}
//			}
//		});
		tvalarmname_listview.setText(context.getResources().getString(R.string.csst_safe_clockname)+clockBean.getmClockName());
		if(clockBean.getmClockArm()==1){
			
			tvstatus.setText(context.getResources().getString(R.string.csst_safe_alarmstatue)+context.getResources().getString(R.string.csst_safe_arm));
		}else{
			tvstatus.setText(context.getResources().getString(R.string.csst_safe_alarmstatue)+context.getResources().getString(R.string.csst_safe_disarm));
		}
		
		
		final CheckBox clockOnOff = (CheckBox) convertView.findViewById(R.id.btnsafeswitch);
		  final ImageView barOnOff =
                (ImageView) convertView.findViewById(R.id.bar_onoff);
        barOnOff.setImageResource(clockBean.getmClockOpenopenFlag()==1 ?
                R.drawable.ic_indicator_on : R.drawable.ic_indicator_off);
        
        if(clockBean.getmClockOpenopenFlag()==1){
        	clockBean.setmClockOpenopenFlag(1);
        }else{
        	clockBean.setmClockOpenopenFlag(0);
        }
		LinearLayout mllbuton = (LinearLayout) convertView.findViewById(R.id.llsafebutton);
		mllbuton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				   clockOnOff.toggle();
				   barOnOff.setImageResource(clockOnOff.isChecked() ? R.drawable.ic_indicator_on
			    	        : R.drawable.ic_indicator_off);
				   if(clockOnOff.isChecked()){
			        	clockBean.setmClockOpenopenFlag(1);
			        }else{
			        	clockBean.setmClockOpenopenFlag(0);
			        }
				   
				   CsstSHSafeClockTable.getInstance().update(mDb, clockBean);
			}
		});
		
		
		StringBuffer delaytime = new StringBuffer();
		delaytime.append(context.getResources().getString(R.string.csst_model_clocktime));
		String min ="";
		if(clockBean.getmClockOpenTimeMin()<10){
			min ="0"+clockBean.getmClockOpenTimeMin();
		}else{
			min =clockBean.getmClockOpenTimeMin()+"";
		}
		delaytime.append(clockBean.getmClockOpenTimeHour()+":"+min);
		tvActiondelay.setText(delaytime.toString());
		StringBuffer location = new StringBuffer();
		location.append(context.getResources().getString(R.string.csst_model_repeat));
		byte mbyteDay = (byte)clockBean.getmClockOpenDay();
		
		if((mbyteDay&0x01)==0x01){
			location.append(context.getResources().getString(R.string.csst_model_week1)+" ");
		}
		if((mbyteDay&0x02)==0x02){
			location.append(context.getResources().getString(R.string.csst_model_week2)+" ");
		}
		if((mbyteDay&0x04)==0x04){
			location.append(context.getResources().getString(R.string.csst_model_week3)+" ");
		}
		if((mbyteDay&0x08)==0x08){
			location.append(context.getResources().getString(R.string.csst_model_week4)+" ");
		}
		if((mbyteDay&0x10)==0x10){
			location.append(context.getResources().getString(R.string.csst_model_week5)+" ");
		}
		if((mbyteDay&0x20)==0x20){
			location.append(context.getResources().getString(R.string.csst_model_week6)+" ");
		}
		if((mbyteDay&0x40)==0x40){
			location.append(context.getResources().getString(R.string.csst_model_week7)+" ");
		}
		tvActionlocation.setText(location.toString());
		convertView.setTag(clockBean);
		return convertView;
	}
}
