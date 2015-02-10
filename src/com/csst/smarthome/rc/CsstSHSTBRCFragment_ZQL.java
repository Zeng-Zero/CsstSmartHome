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
 * 机顶盒
 * @author liuyang
 */
public class CsstSHSTBRCFragment_ZQL extends Fragment implements ICsstSHConstant {
	
	private CsstSHDeviceBean mDeviceBean = null;
	private ImageButton mBtnMute = null;//静音
	private ImageButton mBtnPower = null;//电源
//	private ImageButton mBtnSetting = null;//USB
//	private ImageButton mBtnAudio = null;//AUDIO
	private ImageButton mBtnVolUp = null;//音量+
//	private ImageButton mBtniTv = null;//itv
	private Button mBtnTvAv = null;//TV/AV
	private ImageButton mBtnVolDown = null;//音量-
	private ImageButton mBtnLPage = null;//上一页
//	private ImageButton mBtnGuide = null;//导视
	private ImageButton mBtnNPage = null;//下一页
	private Button mBtnOK = null;//圆圈CsstCircleView
	private ImageButton mBtnHomePage = null;//主页
//	private ImageButton mBtnInfo = null;//信息
	private ImageButton mBtnBack = null;//返回
//	private ImageButton mBtnExit = null;//退出

	private ImageButton mBtnFR = null;//快退ImageImageButton
	private ImageButton mBtnPuase = null;//记录ImageImageButton
	private ImageButton mBtnFF = null;//快进ImageImageButton
	
	private ImageButton mBtnADD = null;//加
	private ImageButton mBtnStop = null;//STOP
	private ImageButton mBtnDEC = null;//减
	
//	private ImageButton mBtn1 = null;//1
//	private ImageButton mBtn2 = null;//2
//	private ImageButton mBtn3 = null;//3
//	private ImageButton mBtn4 = null;//4
//	private ImageButton mBtn5 = null;//5
//	private ImageButton mBtn6 = null;//6
//	private ImageButton mBtn7 = null;//7
//	private ImageButton mBtn8 = null;//8
//	private ImageButton mBtn9 = null;//9
//	private ImageButton mBtn0 = null;//0
//	private ImageButton mBtnDel = null;//删除
//	private ImageButton mBtnDone = null;//确定
	
	public static final CsstSHSTBRCFragment_ZQL getInstance(CsstSHDeviceBean device){
		CsstSHSTBRCFragment_ZQL tv = new CsstSHSTBRCFragment_ZQL();
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
			view = inflater.inflate(R.layout.set_top_remote_activity, null);
			
			mBtnMute = (ImageButton) view.findViewById(R.id.muteBtn);//静音
			mBtnPower = (ImageButton) view.findViewById(R.id.powerBtn);//电源
//			mBtnSetting = (ImageButton) view.findViewById(R.id.mBtnSetting);//USB
//			mBtnAudio = (ImageButton) view.findViewById(R.id.mBtnAudio);//AUDIO
			mBtnVolUp = (ImageButton) view.findViewById(R.id.rightBtn);//音量+
//			mBtniTv = (ImageButton) view.findViewById(R.id.mBtniTv);//itv
			mBtnTvAv = (Button) view.findViewById(R.id.tvAvBtn);//TV/AV
			mBtnVolDown = (ImageButton) view.findViewById(R.id.leftBtn);//音量-
			mBtnLPage = (ImageButton) view.findViewById(R.id.aboveBtn);//上一页
//			mBtnGuide = (ImageButton) view.findViewById(R.id.mBtnGuide);//导视
			mBtnNPage = (ImageButton) view.findViewById(R.id.belowBtn);//下一页
			mBtnOK = (Button) view.findViewById(R.id.centerOKBtn);//圆圈
			mBtnHomePage = (ImageButton) view.findViewById(R.id.ninesDotsBtn);//主页
//			mBtnInfo = (ImageButton) view.findViewById(R.id.mBtnInfo);//信息
			mBtnBack = (ImageButton) view.findViewById(R.id.backBtn);//返回
//			mBtnExit = (ImageButton) view.findViewById(R.id.mBtnExit);//退出

			mBtnFR = (ImageButton) view.findViewById(R.id.rewindBtn);//快退ImageImageButton
			mBtnPuase = (ImageButton) view.findViewById(R.id.playPauseBtn);//记录ImageImageButton
			mBtnFF = (ImageButton) view.findViewById(R.id.fastForwardBtn);//快进ImageImageButton
			
			mBtnADD = (ImageButton) view.findViewById(R.id.addBtn);//快退ImageImageButton
			mBtnStop = (ImageButton) view.findViewById(R.id.stopBtn);//记录ImageImageButton
			mBtnDEC = (ImageButton) view.findViewById(R.id.minusBtn);//快进ImageImageButton
			
			
			
//			mBtn1 = (ImageButton) view.findViewById(R.id.mBtn1);//1
//			mBtn2 = (ImageButton) view.findViewById(R.id.mBtn2);//2
//			mBtn3 = (ImageButton) view.findViewById(R.id.mBtn3);//3
//			mBtn4 = (ImageButton) view.findViewById(R.id.mBtn4);//4
//			mBtn5 = (ImageButton) view.findViewById(R.id.mBtn5);//5
//			mBtn6 = (ImageButton) view.findViewById(R.id.mBtn6);//6
//			mBtn7 = (ImageButton) view.findViewById(R.id.mBtn7);//7
//			mBtn8 = (ImageButton) view.findViewById(R.id.mBtn8);//8
//			mBtn9 = (ImageButton) view.findViewById(R.id.mBtn9);//9
//			mBtn0 = (ImageButton) view.findViewById(R.id.mBtn0);//0
//			mBtnDel = (ImageButton) view.findViewById(R.id.mBtnDel);//删除
//			mBtnDone = (ImageButton) view.findViewById(R.id.mBtnDone);//确定
		
			mBtnMute.setTag(STB_MUTE_RCKEY_IDENTIFY);//静音
			mBtnPower.setTag(STB_POWER_RCKEY_IDENTIFY);//电源
//			mBtnSetting.setTag(STB_SETTING_RCKEY_IDENTIFY);//设置
//			mBtnAudio.setTag(STB_TRACK_RCKEY_IDENTIFY);//声道
			mBtnVolUp.setTag(STB_VOLADD_RCKEY_IDENTIFY);//音量+
//			mBtniTv.setTag(STB_ITV_RCKEY_IDENTIFY);//itv
			mBtnTvAv.setTag(STB_TVAV_RCKEY_IDENTIFY);//TV/AV
			mBtnVolDown.setTag(STB_VOLDEL_RCKEY_IDENTIFY);//音量-
			mBtnLPage.setTag(STB_PREPAGE_RCKEY_IDENTIFY);//上一页
//			mBtnGuide.setTag(STB_NAVIGATION_RCKEY_IDENTIFY);//导视
			mBtnNPage.setTag(STB_NEXTPAGE_RCKEY_IDENTIFY);//下一页
//			layout_vol_ch.setLeftBtnTag(STB_VOLDEL_RCKEY_IDENTIFY);
//			layout_vol_ch.setRightBtnTag(STB_VOLADD_RCKEY_IDENTIFY);
//			layout_vol_ch.setTopBtnTag(STB_CHADD_RCKEY_IDENTIFY);
//			layout_vol_ch.setBottomBtnTag(STB_CHDEL_RCKEY_IDENTIFY);
			mBtnOK.setTag(STB_DONE_RCKEY_IDENTIFY);
			mBtnHomePage.setTag(STB_HOME_RCKEY_IDENTIFY);//主页
//			mBtnInfo.setTag(STB_INFO_RCKEY_IDENTIFY);//信息
			mBtnBack.setTag(STB_BACK_RCKEY_IDENTIFY);//返回
//			mBtnExit.setTag(STB_EXIT_RCKEY_IDENTIFY);//退出
			
			
			mBtnFR.setTag(STB_FR_RCKEY_IDENTIFY);//快退
			mBtnPuase.setTag(STB_RECORD_RCKEY_IDENTIFY);//暂停
			mBtnFF.setTag(STB_FF_RCKEY_IDENTIFY);//快进
			
			mBtnADD.setTag(STB_ADD_RCKEY_IDENTIFY);//加
			mBtnStop.setTag(STB_STOP_RCKEY_IDENTIFY);//停止
			mBtnDEC.setTag(STB_DEC_RCKEY_IDENTIFY);//减
			
			
			
			

//			mBtnFR.setTag(STB_FR_RCKEY_IDENTIFY);//快退ImageImageButton
//			mBtnRecord.setTag(STB_RECORD_RCKEY_IDENTIFY);//记录ImageImageButton
//			mBtnFF.setTag(STB_FF_RCKEY_IDENTIFY);//快进ImageImageButton
//			mBtn1.setTag(STB_1_RCKEY_IDENTIFY);//1
//			mBtn2.setTag(STB_2_RCKEY_IDENTIFY);//2
//			mBtn3.setTag(STB_3_RCKEY_IDENTIFY);//3
//			mBtn4.setTag(STB_4_RCKEY_IDENTIFY);//4
//			mBtn5.setTag(STB_5_RCKEY_IDENTIFY);//5
//			mBtn6.setTag(STB_6_RCKEY_IDENTIFY);//6
//			mBtn7.setTag(STB_7_RCKEY_IDENTIFY);//7
//			mBtn8.setTag(STB_8_RCKEY_IDENTIFY);//8
//			mBtn9.setTag(STB_9_RCKEY_IDENTIFY);//9
//			mBtn0.setTag(STB_0_RCKEY_IDENTIFY);//0
//			mBtnDel.setTag(STB_DEL_RCKEY_IDENTIFY);//删除
//			mBtnDone.setTag(STB_DONE_RCKEY_IDENTIFY);//确定
			
			mBtnMute.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnMute.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnPower.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnPower.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
//			mBtnSetting.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//USB
//			mBtnSetting.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
//			mBtnAudio.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//AUDIO
//			mBtnAudio.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnVolUp.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//音量+
			mBtnVolUp.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
//			mBtniTv.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//itv
//			mBtniTv.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnTvAv.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//TV/AV
			mBtnTvAv.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnVolDown.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//音量-
			mBtnVolDown.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnLPage.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//上一页
			mBtnLPage.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
//			mBtnGuide.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//导视
//			mBtnGuide.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnNPage.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//下一页
			mBtnNPage.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnOK.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//下一页
			mBtnOK.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			
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

			mBtnHomePage.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//主页
			mBtnHomePage.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
//			mBtnInfo.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//信息
//			mBtnInfo.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnBack.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//返回
			mBtnBack.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnFR.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//返回
			mBtnFR.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnPuase.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//返回
			mBtnPuase.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnFF.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//返回
			mBtnFF.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			
			mBtnADD.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//返回
			mBtnADD.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnStop.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//返回
			mBtnStop.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnDEC.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//返回
			mBtnDEC.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			
			
			
			
//			mBtnExit.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//退出
//			mBtnExit.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
//			//第二页
//			mBtnFR.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//快退ImageImageButton
//			mBtnFR.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			mBtnRecord.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//记录ImageImageButton
//			mBtnRecord.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			mBtnFF.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//快进ImageImageButton
//			mBtnFF.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			mBtn1.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//1
//			mBtn1.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			mBtn2.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//2
//			mBtn2.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			mBtn3.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//3
//			mBtn3.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			mBtn4.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//4
//			mBtn4.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			mBtn5.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//5
//			mBtn5.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			mBtn6.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//6
//			mBtn6.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			mBtn7.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//7
//			mBtn7.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			mBtn8.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//8
//			mBtn8.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			mBtn9.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//9
//			mBtn9.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			mBtn0.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//0
//			mBtn0.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			mBtnDel.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//删除
//			mBtnDel.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
//			
//			mBtnDone.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//确定
//			mBtnDone.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
		}
		return view;
	}
	
	
}
