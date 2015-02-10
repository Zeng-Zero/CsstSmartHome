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
 * 窗帘遥控器
 * @author liuyang
 */
public class CsstSHCurtainRCFragment_ZQL extends Fragment implements ICsstSHConstant{
	
	private CsstSHDeviceBean mDeviceBean = null;
	
	private Button mBtnOpenCurtain = null;
	private Button mBtnPauseCurtain = null;
	private Button mBtnCloseCurtain = null;
	
	public static final CsstSHCurtainRCFragment_ZQL getInstance(CsstSHDeviceBean device){
		CsstSHCurtainRCFragment_ZQL tv = new CsstSHCurtainRCFragment_ZQL();
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
		mDeviceBean = (CsstSHDeviceBean) getArguments().getSerializable("device");
		View view = null;
		if (!mDeviceBean.isRCCustom()){
			view = inflater.inflate(R.layout.curtain_activity, null);
			mBtnOpenCurtain = (Button) view.findViewById(R.id.openCurtain);
			mBtnPauseCurtain = (Button) view.findViewById(R.id.pauseAction);
			mBtnCloseCurtain = (Button) view.findViewById(R.id.closeCurtain);
			
			mBtnOpenCurtain.setTag(CURTAIN_OPEN_RCKEY_IDENTIFY);
			mBtnPauseCurtain.setTag(CURTAIN_PAUSE_RCKEY_IDENTIFY);
			mBtnCloseCurtain.setTag(CURTAIN_CLOSE_RCKEY_IDENTIFY);
			
			mBtnOpenCurtain.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			mBtnOpenCurtain.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnPauseCurtain.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			mBtnPauseCurtain.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnCloseCurtain.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			mBtnCloseCurtain.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
		}
		return view;
	}
	
}
