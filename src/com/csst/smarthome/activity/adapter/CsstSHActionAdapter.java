package com.csst.smarthome.activity.adapter;

import java.util.List;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHActionBean;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 设备列表设配器
 * @author liuyang
 */
public class CsstSHActionAdapter extends BaseAdapter {
	private Context context = null;
	private List<CsstSHActionBean> actionBeans = null;

	public CsstSHActionAdapter(Context context, List<CsstSHActionBean> actionBeans) {
		this.context = context;
		this.actionBeans = actionBeans;
		System.out.println("the size of actionBeans is "+this.actionBeans.size());
		for(int i=0;i<this.actionBeans.size();i++){
			System.out.println("the name of action name is"+actionBeans.get(i).getmActionName());
		}
		
	}
	public CsstSHActionAdapter(){
		
	}
	public final void setDevices(List<CsstSHActionBean> actionBeans){
		this.actionBeans = actionBeans;
		notifyDataSetInvalidated();
	}

	@Override
	public int getCount() {
		return null == actionBeans ? 0 : actionBeans.size();
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
		System.out.println("the CsstSHActionAdapter getview ");
		CsstSHActionBean actionBean = actionBeans.get(position);
		convertView = LayoutInflater.from(context).inflate(R.layout.csst_modeladd_editorlistview_listview, null);
		//Button btn_open = (Button) convertView.findViewById(R.id.item_open);
		TextView tvActionname =(TextView) convertView.findViewById(R.id.tvactionname);
		TextView tvActiondelay =(TextView) convertView.findViewById(R.id.tvactiondelay);
		TextView tvActionlocation =(TextView) convertView.findViewById(R.id.tvactionlocal);
		
		StringBuffer name = new StringBuffer();
		name.append(context.getResources().getString(R.string.csst_model_actionname));
		name.append(actionBean.getmActionName());
		tvActionname.setText(name.toString());
		StringBuffer delaytime = new StringBuffer();
		delaytime.append(context.getResources().getString(R.string.csst_model_actiondelaytime));
		delaytime.append(actionBean.getmDelayTime());
		tvActiondelay.setText(delaytime.toString());
		StringBuffer location = new StringBuffer();
		location.append(context.getResources().getString(R.string.csst_model_actionlocal));
		location.append(actionBean.getmLocation());
		tvActionlocation.setText(location.toString());
		convertView.setTag(actionBean);
		return convertView;
	}
}
