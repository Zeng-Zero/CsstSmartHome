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
public class CsstSHSOUNDRCFragment extends Fragment implements ICsstSHConstant {
	private CsstSHDeviceBean mDeviceBean = null;
	private ImageButton mBtnMute = null;//静音
	private ImageButton mBtnPower = null;//电源
	private Button mBtnSoundChanal = null;//USB
	private ImageButton mBtnUppage = null;//AUDIO
	private ImageButton mBtnDownpage = null;//音量+
	private ImageButton mBtnStop = null;//itv
	private ImageButton mBtnNext = null;//TV/AV
	private ImageButton mBtnLast = null;//音量-
	private ImageButton mBtnPuase = null;//上一页
	private ImageButton mBtnUp = null;//导视
	private ImageButton mBtnDown = null;//下一页
	private ImageButton mBtnLefe = null;//圆圈CsstCircleView
	private ImageButton mBtnRight = null;//主页
	private Button mBtnOk = null;//信息
	private ImageButton mBtnMune = null;//返回
	private ImageButton mBtnBack = null;//退出
	private Button mBtnSetting= null;//返回
	private ImageButton mBtnAdd = null;//退出
	private ImageButton mBtnDec = null;//退出

	
	public static final CsstSHSOUNDRCFragment getInstance(CsstSHDeviceBean device){
		CsstSHSOUNDRCFragment tv = new CsstSHSOUNDRCFragment();
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
			view = inflater.inflate(R.layout.sound_remote_activity, null);
			
			mBtnMute = (ImageButton) view.findViewById(R.id.muteBtn);//静音
			mBtnPower = (ImageButton) view.findViewById(R.id.powerBtn);//电源
			mBtnSoundChanal = (Button) view.findViewById(R.id.ninesDotsBtn);//静音
			mBtnUppage = (ImageButton) view.findViewById(R.id.previewBtn);//电源
			mBtnDownpage = (ImageButton) view.findViewById(R.id.nextBtn);//静音
			mBtnStop = (ImageButton) view.findViewById(R.id.stopBtn);//电源
			mBtnNext = (ImageButton) view.findViewById(R.id.rewindBtn);//静音
			mBtnLast = (ImageButton) view.findViewById(R.id.playPauseBtn);//电源
			mBtnPuase = (ImageButton) view.findViewById(R.id.fastForwardBtn);//静音
			mBtnUp = (ImageButton) view.findViewById(R.id.aboveBtn);//电源
			mBtnDown = (ImageButton) view.findViewById(R.id.belowBtn);//静音
			mBtnLefe = (ImageButton) view.findViewById(R.id.leftBtn);//电源
			mBtnRight = (ImageButton) view.findViewById(R.id.rightBtn);//静音
			mBtnOk = (Button) view.findViewById(R.id.centerOKBtn);//电源
			mBtnMune = (ImageButton) view.findViewById(R.id.nineDotsBtn);//静音
			mBtnBack = (ImageButton) view.findViewById(R.id.backBtn);//电源
			mBtnSetting = (Button) view.findViewById(R.id.settingsBtn);//静音
			mBtnAdd = (ImageButton) view.findViewById(R.id.rightIncreaseBtn);//电源
			mBtnDec = (ImageButton) view.findViewById(R.id.leftDecreaseBtn);//静音
			
		
			mBtnMute.setTag(SOUND_MUTE_RCKEY_IDENTIFY);//静音
			mBtnPower.setTag(SOUND_POWER_RCKEY_IDENTIFY);//电源
			mBtnSoundChanal.setTag(SOUND_SOUNDCHANEL_RCKEY_IDENTIFY);//静音
			mBtnUppage.setTag(SOUND_UPPAGE_RCKEY_IDENTIFY);//静音
			mBtnDownpage.setTag(SOUND_DOWNPAGE_RCKEY_IDENTIFY);//电源
			mBtnStop.setTag(SOUND_STOP_RCKEY_IDENTIFY);//静音
			mBtnNext.setTag(SOUND_NEXT_RCKEY_IDENTIFY);//静音
			mBtnLast.setTag(SOUND_LAST_RCKEY_IDENTIFY);//电源
			mBtnPuase.setTag(SOUND_PAUSE_RCKEY_IDENTIFY);//静音
			mBtnUp.setTag(SOUND_UP_RCKEY_IDENTIFY);//静音
			mBtnDown.setTag(SOUND_DOWN_RCKEY_IDENTIFY);//电源
			mBtnLefe.setTag(SOUND_LEFT_RCKEY_IDENTIFY);//电源
			mBtnRight.setTag(SOUND_RIGHT_RCKEY_IDENTIFY);//电源
			mBtnOk.setTag(SOUND_OK_RCKEY_IDENTIFY);//电源
			mBtnMune.setTag(SOUND_MENU_RCKEY_IDENTIFY);//电源
			mBtnBack.setTag(SOUND_BACK_RCKEY_IDENTIFY);//电源
			mBtnSetting.setTag(SOUND_SETTING_RCKEY_IDENTIFY);//电源
			mBtnAdd.setTag(SOUND_ADD_RCKEY_IDENTIFY);//电源
			mBtnDec.setTag(SOUND_DEC_RCKEY_IDENTIFY);//电源
			
			
			mBtnMute.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnMute.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnPower.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnPower.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
		
			mBtnSoundChanal.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnSoundChanal.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnUppage.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnUppage.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnDownpage.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnDownpage.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnStop.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnStop.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			
			mBtnNext.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnNext.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnLast.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnLast.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnPuase.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnPuase.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnUp.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnUp.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnDown.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnDown.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
		
			mBtnLefe.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnLefe.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnRight.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnRight.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnOk.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnOk.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnMune.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnMune.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			
			mBtnBack.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnBack.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnSetting.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnSetting.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnAdd.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnAdd.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnDec.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnDec.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
		}
		return view;
	}
	
	
}
