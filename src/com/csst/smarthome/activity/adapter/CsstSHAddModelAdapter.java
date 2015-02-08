package com.csst.smarthome.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHModelBean;

/**
 * 楼层列表适配器
 * @author 
 */
public class CsstSHAddModelAdapter extends BaseAdapter {
	private Context context = null;
	private List<CsstSHModelBean> datas = null;
	private int mCurModel = -1;

	public CsstSHAddModelAdapter(Context context, List<CsstSHModelBean> list) {
		this.context = context;
		setDatas(list);
	}

	@Override
	public int getCount() {
		return this.datas == null ? 0 : this.datas.size();
	}

	public List<CsstSHModelBean> getDatas() {
		return datas;
	}

	public void setDatas(List<CsstSHModelBean> datas) {
		if (datas == null) {
			this.datas = new ArrayList<CsstSHModelBean>();
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

	public final void setCurFloor(int modelid){
		mCurModel = modelid;
		notifyDataSetInvalidated();
	}
	
	public final View getView(int position, View paramView, ViewGroup paramViewGroup) {
		System.out.println(" addmodeladapter is  here ok !");
		CsstSHModelBean model = datas.get(position);
		paramView = LayoutInflater.from(context).inflate(R.layout.csst_modeladd_item, null);
		TextView tvName = (TextView) paramView.findViewById(R.id.tv_addmodel_item);
		tvName.setText(model.getmodelName());
		if (model.getmodelId() == mCurModel) {  
			paramView.setBackgroundResource(R.drawable.csst_top_bg_blue);
        } else {  
        	paramView.setBackgroundColor(Color.TRANSPARENT);
        }     
		return paramView;
	}
}
