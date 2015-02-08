package com.csst.smarthome.rc;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.device.CsstSHUseDeviceActivity;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.common.ICsstSHConstant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * 风扇遥控器
 * @author liuyang
 */
public class CsstSHElectricfanRCFragment extends Fragment implements ICsstSHConstant {
	
	private CsstSHDeviceBean mDeviceBean = null;
	
	private Button mBtnmenu1 = null;//风量
	private Button mBtnmenu2 = null;//摇头 
	private Button mBtnmenu3 = null;//定时/预约
	private Button mBtnmenu4 = null;//开关
	
	public static final CsstSHElectricfanRCFragment getInstance(CsstSHDeviceBean device){
		CsstSHElectricfanRCFragment tv = new CsstSHElectricfanRCFragment();
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
			view = inflater.inflate(R.layout.csst_electricfan_rc_layout, null);
			mBtnmenu1 = (Button) view.findViewById(R.id.mBtnmenu1);//风量
			mBtnmenu2 = (Button) view.findViewById(R.id.mBtnmenu2);//摇头 
			mBtnmenu3 = (Button) view.findViewById(R.id.mBtnmenu3);//定时/预约
			mBtnmenu4 = (Button) view.findViewById(R.id.mBtnmenu4);//开关
			
			mBtnmenu1.setTag(FAN_WIND_RCKEY_IDENTIFY);
			mBtnmenu2.setTag(FAN_SHAKE_RCKEY_IDENTIFY);
			mBtnmenu3.setTag(FAN_CLOCK_RCKEY_IDENTIFY);
			mBtnmenu4.setTag(FAN_POWER_RCKEY_IDENTIFY);
			
			mBtnmenu1.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			mBtnmenu1.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnmenu2.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			mBtnmenu2.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnmenu3.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			mBtnmenu3.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnmenu4.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			mBtnmenu4.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
		}
		return view;
	}
}
