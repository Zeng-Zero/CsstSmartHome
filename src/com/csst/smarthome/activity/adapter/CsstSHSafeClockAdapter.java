package com.csst.smarthome.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSafeClockBean;


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

	public CsstSHSafeClockAdapter(Context context, List<CsstSafeClockBean> clockBeans) {
		this.context = context;
		this.clockBeans = clockBeans;
		System.out.println("the size of clockBeans is "+this.clockBeans.size());
		for(int i=0;i<this.clockBeans.size();i++){
//			System.out.println("the name of action name is"+clockBeans.get(i).getmClockOpenName());
		}
		
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
		CsstSafeClockBean clockBean = clockBeans.get(position);
		convertView = LayoutInflater.from(context).inflate(R.layout.csst_safe_clockopen_listview,null);
		//Button btn_open = (Button) convertView.findViewById(R.id.item_open);
		TextView tvActiondelay =(TextView) convertView.findViewById(R.id.tvclocktime_listview);
		TextView tvActionlocation =(TextView) convertView.findViewById(R.id.tvclockrepeat_listview);
		TextView tvstatus = (TextView) convertView.findViewById(R.id.tvstatus);
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
		
		if(clockBean.getmClockOpenopenFlag()==1){
			tvstatus.setText(context.getResources().getString(R.string.csst_safe_arm));
		}else{
			tvstatus.setText(context.getResources().getString(R.string.csst_safe_disarm));
		}
		
		
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
