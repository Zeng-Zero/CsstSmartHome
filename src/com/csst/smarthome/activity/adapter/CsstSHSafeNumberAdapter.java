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
 * 电话号码设配器
 */
public class CsstSHSafeNumberAdapter extends BaseAdapter {
	private Context context = null;
	private List<String> numbers = null;

	public CsstSHSafeNumberAdapter(Context context, List<String> numbers) {
		this.context = context;
		this.numbers = numbers;
		System.out.println("the size of numbers is "+this.numbers.size());
		for(int i=0;i<this.numbers.size();i++){
			System.out.println("the name of action name is"+numbers.get(i));
		}
		
	}
	public CsstSHSafeNumberAdapter(){
		
	}
	public final void setDevices(List<String> numbers){
		this.numbers = numbers;
		notifyDataSetInvalidated();
	}

	@Override
	public int getCount() {
		return null == numbers ? 0 : numbers.size();
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
		String number = numbers.get(position);
		convertView = LayoutInflater.from(context).inflate(R.layout.csst_safe_number_item, null);
		TextView tvnumber =(TextView) convertView.findViewById(R.id.tvsafenumberitem);
		tvnumber.setText(number);
		convertView.setTag(number);
		return convertView;
	}
}
