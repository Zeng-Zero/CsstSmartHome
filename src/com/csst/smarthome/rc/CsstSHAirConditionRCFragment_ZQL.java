package com.csst.smarthome.rc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.device.CsstSHUseDeviceActivity;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.common.ICsstSHConstant;

/**
 * 空调遥控器
 * @author dengbo
 */
public class CsstSHAirConditionRCFragment_ZQL extends Fragment implements ICsstSHConstant{
	
	private CsstSHDeviceBean mDeviceBean = null;
	
	private ImageButton mBtnPower = null;//电源
	private ImageButton mBtnAddt = null;//升温
	private ImageButton mBtnDelt = null;//降温
	private Button mBtnWind = null;//风速
	private Button mBtnMode = null;//模式
	private ImageButton mBtnTBWind = null;//上下扫风
	private ImageButton mBtnLRWind = null;//左右扫风
	
	public static final CsstSHAirConditionRCFragment_ZQL getInstance(CsstSHDeviceBean device){
		CsstSHAirConditionRCFragment_ZQL tv = new CsstSHAirConditionRCFragment_ZQL();
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
			view = inflater.inflate(R.layout.air_condition_remote_activity, null);
			mBtnPower = (ImageButton) view.findViewById(R.id.btnPower);//电源
			mBtnAddt = (ImageButton) view.findViewById(R.id.btnIncreaseTemperature);//升温
			mBtnDelt = (ImageButton) view.findViewById(R.id.btnReduceTemperature);//降温
			mBtnWind = (Button) view.findViewById(R.id.btnWind);//风速
			mBtnMode = (Button) view.findViewById(R.id.btnMode);//制冷
			mBtnTBWind = (ImageButton) view.findViewById(R.id.btnTopToBottom);//上下扫风
			mBtnLRWind = (ImageButton) view.findViewById(R.id.btnLeftToRight);//左右扫风
			
			mBtnPower.setTag(AC_POWER_RCKEY_IDENTIFY);//电源
			mBtnAddt.setTag(AC_ADDT_RCKEY_IDENTIFY);//升温
			mBtnDelt.setTag(AC_DELT_RCKEY_IDENTIFY);//降温
			mBtnWind.setTag(AC_WIND_RCKEY_IDENTIFY);//风速
			mBtnMode.setTag(AC_MODE_RCKEY_IDENTIFY);//模式
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
			
			mBtnMode.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//制冷
			mBtnMode.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			
			mBtnTBWind.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//上下扫风
			mBtnTBWind.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnLRWind.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//左右扫风
			mBtnLRWind.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
		}
		
		return view;
	}
	
}
