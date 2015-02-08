package com.csst.smarthome.rc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.device.CsstSHUseDeviceActivity;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.common.ICsstSHConstant;

/**
 * 空调遥控器
 * @author dengbo
 */
public class CsstSHAirConditionRCFragment extends Fragment implements ICsstSHConstant{
	
	private CsstSHDeviceBean mDeviceBean = null;
	
	private Button mBtnPower = null;//电源
	private Button mBtnAddt = null;//升温
	private Button mBtnDelt = null;//降温
	private Button mBtnWind = null;//风速
	private Button mBtnCold = null;//制冷
	private Button mBtnHot = null;//制热
	private Button mBtnTBWind = null;//上下扫风
	private Button mBtnLRWind = null;//左右扫风
	
	public static final CsstSHAirConditionRCFragment getInstance(CsstSHDeviceBean device){
		CsstSHAirConditionRCFragment tv = new CsstSHAirConditionRCFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("device", device);
		tv.setArguments(bundle);
		return tv;
	}

	public int getDeviceIdentification(){
		CsstSHDeviceBean device = (CsstSHDeviceBean) getArguments().getSerializable("device");
		return device.getDeviceId();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = null;
		mDeviceBean = (CsstSHDeviceBean) getArguments().getSerializable("device");
		if (!mDeviceBean.isRCCustom()){
			view = inflater.inflate(R.layout.csst_air_condition_rc_layout, null);
			mBtnPower = (Button) view.findViewById(R.id.mBtnPower);//电源
			mBtnAddt = (Button) view.findViewById(R.id.mBtnAddt);//升温
			mBtnDelt = (Button) view.findViewById(R.id.mBtnDelt);//降温
			mBtnWind = (Button) view.findViewById(R.id.mBtnWind);//风速
			mBtnCold = (Button) view.findViewById(R.id.mBtnCold);//制冷
			mBtnHot = (Button) view.findViewById(R.id.mBtnHot);//制热
			mBtnTBWind = (Button) view.findViewById(R.id.mBtnTBWind);//上下扫风
			mBtnLRWind = (Button) view.findViewById(R.id.mBtnLRWind);//左右扫风
			
			mBtnPower.setTag(AC_POWER_RCKEY_IDENTIFY);//电源
			mBtnAddt.setTag(AC_ADDT_RCKEY_IDENTIFY);//升温
			mBtnDelt.setTag(AC_DELT_RCKEY_IDENTIFY);//降温
			mBtnWind.setTag(AC_WIND_RCKEY_IDENTIFY);//风速
			mBtnCold.setTag(AC_COLD_RCKEY_IDENTIFY);//制冷
			mBtnHot.setTag(AC_HOT_RCKEY_IDENTIFY);//制热
			mBtnTBWind.setTag(AC_LRWIND_RCKEY_IDENTIFY);//上下扫风
			mBtnLRWind.setTag(AC_TBWIND_RCKEY_IDENTIFY);//左右扫风
			
			mBtnPower.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnPower.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnAddt.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//升温
			mBtnAddt.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnDelt.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//降温
			mBtnDelt.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnWind.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//风速
			mBtnWind.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnCold.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//制冷
			mBtnCold.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnHot.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//制热
			mBtnHot.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnTBWind.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//上下扫风
			mBtnTBWind.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnLRWind.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//左右扫风
			mBtnLRWind.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
		}
		
		return view;
	}
	
}
