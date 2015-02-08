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
public class CsstDVDRCFragment extends Fragment implements ICsstSHConstant{
	
	private CsstSHDeviceBean mDeviceBean = null;
	private Button mBtnMute = null;//静音
	private Button mBtnPower = null;//电源
	private Button mBtnLtrack = null;//左声道
	private Button mBtnRtrack = null;//右声道
	private Button mBtnStereo = null;//立体声
	private Button mBtnImage = null;//画面
	private Button mBtnPlay = null;//播放
	private Button mBtnPause = null;//暂停
	private Button mBtnStop = null;//停止
	private Button mBtnRplay = null;//回放
	private Button mBtnPrev = null;//上一首
	private Button mBtnReverse = null;//快退
	private Button mBtnForward = null;//快进
	private Button mBtnNext = null;//下一首
	private CsstCircleView layout_vol_ch = null;//圆圈
	private Button mBtnMenu = null;//目录
	private Button mBtnPopOut = null;//弹出
	private Button mBtnBack = null;//返回
	private Button mBtnExit = null;//退出

	private ImageButton mBtnFR = null;//快退
	private ImageButton mBtnRecord = null;//记录
	private ImageButton mBtnFF = null;//快进
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
	
	public static final CsstDVDRCFragment getInstance(CsstSHDeviceBean device){
		CsstDVDRCFragment tv = new CsstDVDRCFragment();
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
			view = inflater.inflate(R.layout.csst_dvd_rc_layout, null);
			mBtnMute = (Button) view.findViewById(R.id.mBtnMute);//静音
			mBtnPower = (Button) view.findViewById(R.id.mBtnPower);//电源
			mBtnLtrack = (Button) view.findViewById(R.id.mBtnLtrack);//左声道
			mBtnRtrack = (Button) view.findViewById(R.id.mBtnRtrack);//右声道
			mBtnStereo = (Button) view.findViewById(R.id.mBtnStereo);//立体声
			mBtnImage = (Button) view.findViewById(R.id.mBtnImage);//画面
			mBtnPlay = (Button) view.findViewById(R.id.mBtnPlay);//播放
			mBtnPause = (Button) view.findViewById(R.id.mBtnPause);//暂停
			mBtnStop = (Button) view.findViewById(R.id.mBtnStop);//停止
			mBtnRplay = (Button) view.findViewById(R.id.mBtnRplay);//回放
			mBtnPrev = (Button) view.findViewById(R.id.mBtnPrev);//上一首
			mBtnReverse = (Button) view.findViewById(R.id.mBtnReverse);//快退
			mBtnForward = (Button) view.findViewById(R.id.mBtnForward);//快进
			mBtnNext = (Button) view.findViewById(R.id.mBtnNext);//下一首
			layout_vol_ch = (CsstCircleView) view.findViewById(R.id.layout_vol_ch);//圆圈
			mBtnMenu = (Button) view.findViewById(R.id.mBtnMenu);//目录
			mBtnPopOut = (Button) view.findViewById(R.id.mBtnPopOut);//弹出
			mBtnBack = (Button) view.findViewById(R.id.mBtnBack);//返回
			mBtnExit = (Button) view.findViewById(R.id.mBtnExit);//退出
			//下一页
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
			
			mBtnMute.setTag(DVD_MUTE_RCKEY_IDENTIFY);//静音
			mBtnPower.setTag(DVD_POWER_RCKEY_IDENTIFY);//电源
			mBtnLtrack.setTag(DVD_LTRACK_RCKEY_IDENTIFY);//左声道
			mBtnRtrack.setTag(DVD_RTRACK_RCKEY_IDENTIFY);//右声道
			mBtnStereo.setTag(DVD_STEREO_RCKEY_IDENTIFY);//立体声
			mBtnImage.setTag(DVD_IMAGE_RCKEY_IDENTIFY);//画面
			mBtnPlay.setTag(DVD_PLAY_RCKEY_IDENTIFY);//播放
			mBtnPause.setTag(DVD_PAUSE_RCKEY_IDENTIFY);//暂停
			mBtnStop.setTag(DVD_STOP_RCKEY_IDENTIFY);//停止
			mBtnRplay.setTag(DVD_RPLAY_RCKEY_IDENTIFY);//回放
			mBtnPrev.setTag(DVD_PREV_RCKEY_IDENTIFY);//上一首
			mBtnReverse.setTag(DVD_REVERSE_RCKEY_IDENTIFY);//快退
			mBtnForward.setTag(DVD_FORWARD_RCKEY_IDENTIFY);//快进
			mBtnNext.setTag(DVD_NEXT_RCKEY_IDENTIFY);//下一首
			layout_vol_ch.setLeftBtnTag(DVD_VOLDEL_RCKEY_IDENTIFY);
			layout_vol_ch.setRightBtnTag(DVD_VOLADD_RCKEY_IDENTIFY);
			layout_vol_ch.setTopBtnTag(DVD_CHADD_RCKEY_IDENTIFY);
			layout_vol_ch.setBottomBtnTag(DVD_CHDEL_RCKEY_IDENTIFY);
			layout_vol_ch.setMiddleBtnTag(DVD_OK_RCKEY_IDENTIFY);
			mBtnMenu.setTag(DVD_LIST_RCKEY_IDENTIFY);//目录
			mBtnPopOut.setTag(DVD_POP_RCKEY_IDENTIFY);//弹出
			mBtnBack.setTag(DVD_BACK_RCKEY_IDENTIFY);//返回
			mBtnExit.setTag(DVD_EXIT_RCKEY_IDENTIFY);//退出
			
			mBtnFR.setTag(DVD_FR_RCKEY_IDENTIFY);//快退ImageButton
			mBtnRecord.setTag(DVD_RECORD_RCKEY_IDENTIFY);//记录ImageButton
			mBtnFF.setTag(DVD_FF_RCKEY_IDENTIFY);//快进ImageButton
			mBtn1.setTag(DVD_1_RCKEY_IDENTIFY);//1
			mBtn2.setTag(DVD_2_RCKEY_IDENTIFY);//2
			mBtn3.setTag(DVD_3_RCKEY_IDENTIFY);//3
			mBtn4.setTag(DVD_4_RCKEY_IDENTIFY);//4
			mBtn5.setTag(DVD_5_RCKEY_IDENTIFY);//5
			mBtn6.setTag(DVD_6_RCKEY_IDENTIFY);//6
			mBtn7.setTag(DVD_7_RCKEY_IDENTIFY);//7
			mBtn8.setTag(DVD_8_RCKEY_IDENTIFY);//8
			mBtn9.setTag(DVD_9_RCKEY_IDENTIFY);//9
			mBtn0.setTag(DVD_0_RCKEY_IDENTIFY);//0
			mBtnDel.setTag(DVD_DEL_RCKEY_IDENTIFY);//删除
			mBtnDone.setTag(DVD_DONE_RCKEY_IDENTIFY);//确定
			
			mBtnMute.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//静音
			mBtnMute.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnPower.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//电源
			mBtnPower.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnLtrack.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//左声道
			mBtnLtrack.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnRtrack.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//右声道
			mBtnRtrack.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnStereo.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//立体声
			mBtnStereo.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnImage.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//画面
			mBtnImage.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnPlay.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//播放
			mBtnPlay.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnPause.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//暂停
			mBtnPause.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnStop.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//停止
			mBtnStop.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnRplay.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//回放
			mBtnRplay.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnPrev.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//上一首
			mBtnPrev.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnReverse.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//快退
			mBtnReverse.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnForward.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//快进
			mBtnForward.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnNext.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//下一首
			mBtnNext.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
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

			mBtnMenu.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//目录
			mBtnMenu.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
			mBtnPopOut.setOnClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);//弹出
			mBtnPopOut.setOnLongClickListener(((CsstSHUseDeviceActivity) getActivity()).mKeyListener);
			
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
