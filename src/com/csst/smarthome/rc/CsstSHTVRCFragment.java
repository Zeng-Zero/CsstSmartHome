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
import com.csst.smarthome.widget.CsstCircleView;

/**
 * 电视遥控器
 * @author liuyang
 */
public class CsstSHTVRCFragment extends Fragment implements ICsstSHConstant{
	
	private CsstSHDeviceBean mDeviceBean = null;
	
	private Button mBtnPower = null;//电源
	private Button mBtnMute = null;//静音
	private Button mBtnSignal = null;//信号源
	private Button mBtnSound = null;//声音
	private Button mBtnImage = null;//图像
	private Button mBtnVolumeAdd = null;//vol+
	private Button mBtnVolumeDel = null;//vol-
	private Button mBtnChannelAdd = null;//频道+
	private Button mBtnChannelDel = null;//频道-
	private Button mBtnExit = null;//退出
	private Button mBtnEpg = null;//epg
	private Button mBtnMenu = null;//菜单
	private Button mBtnFav = null;//FAV
	private CsstCircleView layout_vol_ch = null;//圆圈
	
	public static final CsstSHTVRCFragment getInstance(CsstSHDeviceBean device){
		CsstSHTVRCFragment tv = new CsstSHTVRCFragment();
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
			view = inflater.inflate(R.layout.csst_tv_rc_layout, null);
			mBtnPower = (Button) view.findViewById(R.id.mBtnPower);//电源
			mBtnMute = (Button) view.findViewById(R.id.mBtnMute);//静音
			mBtnSignal = (Button) view.findViewById(R.id.mBtnSignal);//信号源
			mBtnSound = (Button) view.findViewById(R.id.mBtnSound);//声音
			mBtnImage = (Button) view.findViewById(R.id.mBtnImage);//图像
			mBtnVolumeAdd = (Button) view.findViewById(R.id.mBtnVolumeAdd);//vol+
			mBtnVolumeDel = (Button) view.findViewById(R.id.mBtnVolumeDel);//vol-
			mBtnChannelAdd = (Button) view.findViewById(R.id.mBtnChannelAdd);//频道+
			mBtnChannelDel = (Button) view.findViewById(R.id.mBtnChannelDel);//频道-
			mBtnExit = (Button) view.findViewById(R.id.mBtnExit);//退出
			mBtnEpg = (Button) view.findViewById(R.id.mBtnEpg);//epg
			mBtnMenu = (Button) view.findViewById(R.id.mBtnMenu);//菜单
			mBtnFav = (Button) view.findViewById(R.id.mBtnFav);//FAV
			layout_vol_ch = (CsstCircleView) view.findViewById(R.id.layout_vol_ch);//圆圈
		
			mBtnPower.setTag(TV_POWER_RCKEY_IDENTIFY);//电源
			mBtnMute.setTag(TV_MUTE_RCKEY_IDENTIFY);//静音
			mBtnSignal.setTag(TV_SIGNAL_RCKEY_IDENTIFY);//信号源
			mBtnSound.setTag(TV_SOUND_RCKEY_IDENTIFY);//声音
			mBtnImage.setTag(TV_IMAGE_RCKEY_IDENTIFY);//图像
			mBtnVolumeAdd.setTag(TV_VOLADD_RCKEY_IDENTIFY);//vol+
			mBtnVolumeDel.setTag(TV_VOLDEL_RCKEY_IDENTIFY);//vol-
			mBtnChannelAdd.setTag(TV_CHADD_RCKEY_IDENTIFY);//频道+
			mBtnChannelDel.setTag(TV_CHDEL_RCKEY_IDENTIFY);//频道-
			mBtnExit.setTag(TV_EXIT_RCKEY_IDENTIFY);//退出
			mBtnEpg.setTag(TV_EPG_RCKEY_IDENTIFY);//epg
			mBtnMenu.setTag(TV_MENU_RCKEY_IDENTIFY);//菜单
			mBtnFav.setTag(TV_FAV_RCKEY_IDENTIFY);//FAV
			layout_vol_ch.setLeftBtnTag(TV_VOLDEL_RCKEY_IDENTIFY);
			layout_vol_ch.setRightBtnTag(TV_VOLADD_RCKEY_IDENTIFY);
			layout_vol_ch.setTopBtnTag(TV_CHADD_RCKEY_IDENTIFY);
			layout_vol_ch.setBottomBtnTag(TV_CHDEL_RCKEY_IDENTIFY);
			layout_vol_ch.setMiddleBtnTag(TV_OK_RCKEY_IDENTIFY);
			
			mBtnPower.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnPower.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnMute.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnMute.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnSignal.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//信号源
			mBtnSignal.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnSound.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//声音
			mBtnSound.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnImage.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//图像
			mBtnImage.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnVolumeAdd.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//vol+
			mBtnVolumeAdd.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnVolumeDel.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//vol-
			mBtnVolumeDel.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnChannelAdd.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//频道+
			mBtnChannelAdd.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnChannelDel.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//频道-
			mBtnChannelDel.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnExit.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//退出
			mBtnExit.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnEpg.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//epg
			mBtnEpg.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnMenu.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//菜单
			mBtnMenu.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnFav.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//FAV
			mBtnFav.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
		
			layout_vol_ch.setLeftBtnListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			layout_vol_ch.setLeftLongListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			layout_vol_ch.setRightBtnListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			layout_vol_ch.setRightLongListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			layout_vol_ch.setTopBtnListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			layout_vol_ch.setTopLongListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			layout_vol_ch.setBottomBtnListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			layout_vol_ch.setBottomLongListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			layout_vol_ch.setMiddleBtnListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			layout_vol_ch.setMiddleLongListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
		}
		return view;
	}
	
}
