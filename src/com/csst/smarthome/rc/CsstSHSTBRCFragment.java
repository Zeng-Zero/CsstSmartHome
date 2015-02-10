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
 * 机顶盒
 * @author liuyang
 */
public class CsstSHSTBRCFragment extends Fragment implements ICsstSHConstant {
	
	private CsstSHDeviceBean mDeviceBean = null;
	private Button mBtnMute = null;//静音
	private Button mBtnPower = null;//电源
	private Button mBtnSetting = null;//USB
	private Button mBtnAudio = null;//AUDIO
	private Button mBtnVolUp = null;//音量+
	private Button mBtniTv = null;//itv
	private Button mBtnTvAv = null;//TV/AV
	private Button mBtnVolDown = null;//音量-
	private Button mBtnLPage = null;//上一页
	private Button mBtnGuide = null;//导视
	private Button mBtnNPage = null;//下一页
	private CsstCircleView layout_vol_ch = null;//圆圈CsstCircleView
	private Button mBtnHomePage = null;//主页
	private Button mBtnInfo = null;//信息
	private Button mBtnBack = null;//返回
	private Button mBtnExit = null;//退出

	private ImageButton mBtnFR = null;//快退ImageButton
	private ImageButton mBtnRecord = null;//记录ImageButton
	private ImageButton mBtnFF = null;//快进ImageButton
	private Button mBtn1 = null;//1
	private Button mBtn2 = null;//2
	private Button mBtn3 = null;//3
	private Button mBtn4 = null;//4
	private Button mBtn5 = null;//5
	private Button mBtn6 = null;//6
	private Button mBtn7 = null;//7
	private Button mBtn8 = null;//8
	private Button mBtn9 = null;//9
	private Button mBtn0 = null;//0
	private Button mBtnDel = null;//删除
	private Button mBtnDone = null;//确定
	
	public static final CsstSHSTBRCFragment getInstance(CsstSHDeviceBean device){
		CsstSHSTBRCFragment tv = new CsstSHSTBRCFragment();
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
			view = inflater.inflate(R.layout.csst_stb_rc_layout, null);
			
			mBtnMute = (Button) view.findViewById(R.id.mBtnMute);//静音
			mBtnPower = (Button) view.findViewById(R.id.mBtnPower);//电源
			mBtnSetting = (Button) view.findViewById(R.id.mBtnSetting);//USB
			mBtnAudio = (Button) view.findViewById(R.id.mBtnAudio);//AUDIO
			mBtnVolUp = (Button) view.findViewById(R.id.mBtnVolUp);//音量+
			mBtniTv = (Button) view.findViewById(R.id.mBtniTv);//itv
			mBtnTvAv = (Button) view.findViewById(R.id.mBtnTvAv);//TV/AV
			mBtnVolDown = (Button) view.findViewById(R.id.mBtnVolDown);//音量-
			mBtnLPage = (Button) view.findViewById(R.id.mBtnLPage);//上一页
			mBtnGuide = (Button) view.findViewById(R.id.mBtnGuide);//导视
			mBtnNPage = (Button) view.findViewById(R.id.mBtnNPage);//下一页
			layout_vol_ch = (CsstCircleView) view.findViewById(R.id.layout_vol_ch);//圆圈
			mBtnHomePage = (Button) view.findViewById(R.id.mBtnHomePage);//主页
			mBtnInfo = (Button) view.findViewById(R.id.mBtnInfo);//信息
			mBtnBack = (Button) view.findViewById(R.id.mBtnBack);//返回
			mBtnExit = (Button) view.findViewById(R.id.mBtnExit);//退出

			mBtnFR = (ImageButton) view.findViewById(R.id.mBtnFR);//快退ImageButton
			mBtnRecord = (ImageButton) view.findViewById(R.id.mBtnRecord);//记录ImageButton
			mBtnFF = (ImageButton) view.findViewById(R.id.mBtnFF);//快进ImageButton
			mBtn1 = (Button) view.findViewById(R.id.mBtn1);//1
			mBtn2 = (Button) view.findViewById(R.id.mBtn2);//2
			mBtn3 = (Button) view.findViewById(R.id.mBtn3);//3
			mBtn4 = (Button) view.findViewById(R.id.mBtn4);//4
			mBtn5 = (Button) view.findViewById(R.id.mBtn5);//5
			mBtn6 = (Button) view.findViewById(R.id.mBtn6);//6
			mBtn7 = (Button) view.findViewById(R.id.mBtn7);//7
			mBtn8 = (Button) view.findViewById(R.id.mBtn8);//8
			mBtn9 = (Button) view.findViewById(R.id.mBtn9);//9
			mBtn0 = (Button) view.findViewById(R.id.mBtn0);//0
			mBtnDel = (Button) view.findViewById(R.id.mBtnDel);//删除
			mBtnDone = (Button) view.findViewById(R.id.mBtnDone);//确定
		
			mBtnMute.setTag(STB_MUTE_RCKEY_IDENTIFY);//静音
			mBtnPower.setTag(STB_POWER_RCKEY_IDENTIFY);//电源
			mBtnSetting.setTag(STB_SETTING_RCKEY_IDENTIFY);//设置
			mBtnAudio.setTag(STB_TRACK_RCKEY_IDENTIFY);//声道
			mBtnVolUp.setTag(STB_VOLADD_RCKEY_IDENTIFY);//音量+
			mBtniTv.setTag(STB_ITV_RCKEY_IDENTIFY);//itv
			mBtnTvAv.setTag(STB_TVAV_RCKEY_IDENTIFY);//TV/AV
			mBtnVolDown.setTag(STB_VOLDEL_RCKEY_IDENTIFY);//音量-
			mBtnLPage.setTag(STB_PREPAGE_RCKEY_IDENTIFY);//上一页
			mBtnGuide.setTag(STB_NAVIGATION_RCKEY_IDENTIFY);//导视
			mBtnNPage.setTag(STB_NEXTPAGE_RCKEY_IDENTIFY);//下一页
			layout_vol_ch.setLeftBtnTag(STB_VOLDEL_RCKEY_IDENTIFY);
			layout_vol_ch.setRightBtnTag(STB_VOLADD_RCKEY_IDENTIFY);
			layout_vol_ch.setTopBtnTag(STB_CHADD_RCKEY_IDENTIFY);
			layout_vol_ch.setBottomBtnTag(STB_CHDEL_RCKEY_IDENTIFY);
			layout_vol_ch.setMiddleBtnTag(STB_OK_RCKEY_IDENTIFY);
			mBtnHomePage.setTag(STB_HOME_RCKEY_IDENTIFY);//主页
			mBtnInfo.setTag(STB_INFO_RCKEY_IDENTIFY);//信息
			mBtnBack.setTag(STB_BACK_RCKEY_IDENTIFY);//返回
			mBtnExit.setTag(STB_EXIT_RCKEY_IDENTIFY);//退出

			mBtnFR.setTag(STB_FR_RCKEY_IDENTIFY);//快退ImageButton
			mBtnRecord.setTag(STB_RECORD_RCKEY_IDENTIFY);//记录ImageButton
			mBtnFF.setTag(STB_FF_RCKEY_IDENTIFY);//快进ImageButton
			mBtn1.setTag(STB_1_RCKEY_IDENTIFY);//1
			mBtn2.setTag(STB_2_RCKEY_IDENTIFY);//2
			mBtn3.setTag(STB_3_RCKEY_IDENTIFY);//3
			mBtn4.setTag(STB_4_RCKEY_IDENTIFY);//4
			mBtn5.setTag(STB_5_RCKEY_IDENTIFY);//5
			mBtn6.setTag(STB_6_RCKEY_IDENTIFY);//6
			mBtn7.setTag(STB_7_RCKEY_IDENTIFY);//7
			mBtn8.setTag(STB_8_RCKEY_IDENTIFY);//8
			mBtn9.setTag(STB_9_RCKEY_IDENTIFY);//9
			mBtn0.setTag(STB_0_RCKEY_IDENTIFY);//0
			mBtnDel.setTag(STB_DEL_RCKEY_IDENTIFY);//删除
			mBtnDone.setTag(STB_DONE_RCKEY_IDENTIFY);//确定
			
			mBtnMute.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnMute.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnPower.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnPower.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnSetting.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//USB
			mBtnSetting.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnAudio.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//AUDIO
			mBtnAudio.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnVolUp.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//音量+
			mBtnVolUp.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtniTv.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//itv
			mBtniTv.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnTvAv.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//TV/AV
			mBtnTvAv.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnVolDown.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//音量-
			mBtnVolDown.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnLPage.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//上一页
			mBtnLPage.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnGuide.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//导视
			mBtnGuide.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnNPage.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//下一页
			mBtnNPage.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
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

			mBtnHomePage.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//主页
			mBtnHomePage.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnInfo.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//信息
			mBtnInfo.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnBack.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//返回
			mBtnBack.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnExit.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//退出
			mBtnExit.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			//第二页
			mBtnFR.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//快退ImageButton
			mBtnFR.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnRecord.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//记录ImageButton
			mBtnRecord.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnFF.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//快进ImageButton
			mBtnFF.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtn1.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//1
			mBtn1.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtn2.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//2
			mBtn2.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtn3.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//3
			mBtn3.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtn4.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//4
			mBtn4.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtn5.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//5
			mBtn5.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtn6.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//6
			mBtn6.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtn7.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//7
			mBtn7.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtn8.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//8
			mBtn8.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtn9.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//9
			mBtn9.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtn0.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//0
			mBtn0.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnDel.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//删除
			mBtnDel.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnDone.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//确定
			mBtnDone.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
		}
		return view;
	}
	
	
}
