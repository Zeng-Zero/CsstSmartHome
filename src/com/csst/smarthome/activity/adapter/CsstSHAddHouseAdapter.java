package com.csst.smarthome.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHFloolBean;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 楼层列表适配器
 * @author liuyang
 */
public class CsstSHAddHouseAdapter extends BaseAdapter {
	private Context context = null;
	private List<CsstSHFloolBean> datas = null;
	private int mCurFloor = -1;

	public CsstSHAddHouseAdapter(Context context, List<CsstSHFloolBean> list) {
		this.context = context;
		setDatas(list);
	}

	@Override
	public int getCount() {
		return this.datas == null ? 0 : this.datas.size();
	}

	public List<CsstSHFloolBean> getDatas() {
		return datas;
	}

	public void setDatas(List<CsstSHFloolBean> datas) {
		if (datas == null) {
			this.datas = new ArrayList<CsstSHFloolBean>();
		} else {
			this.datas = datas;
		}
		notifyDataSetInvalidated();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public final void setCurFloor(int floorid){
		mCurFloor = floorid;
		notifyDataSetInvalidated();
	}
	
	public final View getView(int position, View paramView, ViewGroup paramViewGroup) {
		CsstSHFloolBean floor = datas.get(position);
		paramView = LayoutInflater.from(context).inflate(R.layout.csst_addfloor_item, null);
		TextView tvName = (TextView) paramView.findViewById(R.id.tv_addfloor_item);
		tvName.setText(floor.getFloorName());
		if (floor.getFloorId() == mCurFloor) {  
			paramView.setBackgroundResource(R.drawable.csst_top_bg_blue);
        } else {  
        	paramView.setBackgroundColor(Color.TRANSPARENT);
        }     
		return paramView;
	}
}
