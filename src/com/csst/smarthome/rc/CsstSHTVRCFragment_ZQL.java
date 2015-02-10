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
import com.csst.smarthome.widget.CsstCircleView;

/**
 * 电视遥控器
 * @author liuyang
 */
public class CsstSHTVRCFragment_ZQL extends Fragment implements ICsstSHConstant{
	
	private CsstSHDeviceBean mDeviceBean = null;
	
	private ImageButton mBtnPower = null;//电源
	private ImageButton mBtnMute = null;//静音
	private Button mBtnSignal = null;//信号源
	private ImageButton mBtnADD = null;//声音
	private ImageButton mBtnDEC = null;//图像
	private ImageButton mBtnVolumeAdd = null;//vol+
	private ImageButton mBtnVolumeDel = null;//vol-
	private ImageButton mBtnChannelAdd = null;//频道+
	private ImageButton mBtnChannelDel = null;//频道-
	private ImageButton mBtnExit = null;//退出
//	private ImageButton mBtnEpg = null;//epg
	private ImageButton mBtnStop = null;//stop
	private Button mBtnMenu = null;//菜单
//	private ImageButton mBtnFav = null;//FAV
	private Button layout_vol_ch = null;//圆圈
	
	public static final CsstSHTVRCFragment_ZQL getInstance(CsstSHDeviceBean device){
		CsstSHTVRCFragment_ZQL tv = new CsstSHTVRCFragment_ZQL();
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
			view = inflater.inflate(R.layout.tv_remote_activity, null);
			mBtnPower = (ImageButton) view.findViewById(R.id.powerBtn);//电源
			mBtnMute = (ImageButton) view.findViewById(R.id.muteBtn);//静音
			mBtnSignal = (Button) view.findViewById(R.id.tvAvBtn);//信号源
//			mBtnSound = (ImageButton) view.findViewById(R.id.addBtn);//加
			mBtnADD = (ImageButton) view.findViewById(R.id.addBtn);//加
//			mBtnImage = (ImageButton) view.findViewById(R.id.minusBtn);//
			mBtnDEC = (ImageButton) view.findViewById(R.id.minusBtn);//减
			mBtnVolumeAdd = (ImageButton) view.findViewById(R.id.rightBtn);//vol+
			mBtnVolumeDel = (ImageButton) view.findViewById(R.id.leftBtn);//vol-
			mBtnChannelAdd = (ImageButton) view.findViewById(R.id.aboveBtn);//频道+
			mBtnChannelDel = (ImageButton) view.findViewById(R.id.belowBtn);//频道-
			mBtnExit = (ImageButton) view.findViewById(R.id.backBtn);//退出
//			mBtnEpg = (ImageButton) view.findViewById(R.id.mBtnEpg);//epg
			mBtnStop = (ImageButton) view.findViewById(R.id.ninesDotsBtn);//stop
			mBtnMenu = (Button) view.findViewById(R.id.menuBtn);//菜单
//			mBtnFav = (ImageButton) view.findViewById(R.id.mBtnFav);//FAV
			layout_vol_ch = (Button) view.findViewById(R.id.centerOKBtn);//圆圈
		
			mBtnPower.setTag(TV_POWER_RCKEY_IDENTIFY);//电源
			mBtnMute.setTag(TV_MUTE_RCKEY_IDENTIFY);//静音
			mBtnSignal.setTag(TV_SIGNAL_RCKEY_IDENTIFY);//信号源
			mBtnADD.setTag(TV_ADD_RCKEY_IDENTIFY);//声音
			mBtnDEC.setTag(TV_DEC_RCKEY_IDENTIFY);//图像
			mBtnVolumeAdd.setTag(TV_VOLADD_RCKEY_IDENTIFY);//vol+
			mBtnVolumeDel.setTag(TV_VOLDEL_RCKEY_IDENTIFY);//vol-
			mBtnChannelAdd.setTag(TV_CHADD_RCKEY_IDENTIFY);//频道+
			mBtnChannelDel.setTag(TV_CHDEL_RCKEY_IDENTIFY);//频道-
			mBtnExit.setTag(TV_EXIT_RCKEY_IDENTIFY);//退出
			mBtnStop.setTag(TV_STOP_RCKEY_IDENTIFY);//epg停止
			mBtnMenu.setTag(TV_MENU_RCKEY_IDENTIFY);//菜单
			layout_vol_ch.setTag(TV_OK_RCKEY_IDENTIFY);//OK
//			mBtnFav.setTag(TV_FAV_RCKEY_IDENTIFY);//FAV
//			layout_vol_ch.setLeftBtnTag(TV_VOLDEL_RCKEY_IDENTIFY);
//			layout_vol_ch.setRightBtnTag(TV_VOLADD_RCKEY_IDENTIFY);
//			layout_vol_ch.setTopBtnTag(TV_CHADD_RCKEY_IDENTIFY);
//			layout_vol_ch.setBottomBtnTag(TV_CHDEL_RCKEY_IDENTIFY);
//			layout_vol_ch.setMiddleBtnTag(TV_OK_RCKEY_IDENTIFY);
			
			mBtnPower.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnPower.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnMute.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnMute.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnSignal.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//信号源
			mBtnSignal.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnADD.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//声音
			mBtnADD.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnDEC.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//图像
			mBtnDEC.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
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
			
//			mBtnEpg.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//epg
//			mBtnEpg.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnMenu.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//菜单
			mBtnMenu.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			layout_vol_ch.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//菜单
			layout_vol_ch.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
//			mBtnFav.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//FAV
//			mBtnFav.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
		
//			layout_vol_ch.setLeftBtnListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			layout_vol_ch.setLeftLongListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			layout_vol_ch.setRightBtnListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			layout_vol_ch.setRightLongListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			layout_vol_ch.setTopBtnListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			layout_vol_ch.setTopLongListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			layout_vol_ch.setBottomBtnListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			layout_vol_ch.setBottomLongListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			layout_vol_ch.setMiddleBtnListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			layout_vol_ch.setMiddleLongListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
		}
		return view;
	}
	
}
