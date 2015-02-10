package com.csst.smarthome.rc;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.device.CsstSHUseDeviceActivity;
import com.csst.smarthome.activity.device.CsstSHUseDeviceActivity.StudyCodeTask;
import com.csst.smarthome.bean.CsstSHDRCBean;
import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.message.rfcode.SendRFCodeReqMessage;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 灯光遥控器
 * @author liuyang
 */
public class CsstSHLightingRCFragment extends Fragment implements ICsstSHInitialize, ICsstSHConstant{
	
	public static final String TAG = "CsstSHLightingRCFragment";
	
	private CsstSHDeviceBean mDeviceBean = null;
	private CheckBox mBtnLightPower = null;
	
	private PowerListener mPowerListener = null;
	
	public static final CsstSHLightingRCFragment getInstance(CsstSHDeviceBean device){
		CsstSHLightingRCFragment tv = new CsstSHLightingRCFragment();
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
			view = inflater.inflate(R.layout.csst_lighting_rc_layout, null);
			mBtnLightPower = (CheckBox) view.findViewById(R.id.mBtnLightPower);
			initDataSource();
			initWidgetState();
			initWidgetListener();
			addWidgetListener();
		}
		return view;
	}

	@Override
	public void initDataSource() {
		mBtnLightPower.setTag(LIGHT_POWER_RCKEY_IDENTIFY);
	}

	@Override
	public void initWidget() {
		
	}

	@Override
	public void initWidgetState() {
		
	}

	@Override
	public void initWidgetListener() {
		mPowerListener = new PowerListener();
	}

	@Override
	public void addWidgetListener() {
		mBtnLightPower.setOnLongClickListener(mPowerListener);
		mBtnLightPower.setOnCheckedChangeListener(mPowerListener);
	}
	
	private final class PowerListener implements OnCheckedChangeListener, View.OnLongClickListener{
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			CsstSHUseDeviceActivity deviceActivity = (CsstSHUseDeviceActivity) getActivity();
			CsstSHDRCBean key = deviceActivity.getDeviceRCKeyByIdentify(buttonView);
			if (key.getDRCCmdCode() != null){
				deviceActivity.new SendCodeTast(key).execute();
			}else{
				AlertDialog.Builder builder = new AlertDialog.Builder((CsstSHUseDeviceActivity) getActivity());
				builder.setMessage(R.string.csst_study_key_code_message);
				builder.setPositiveButton(R.string.csst_ok, null);
				builder.show();
			}
		}

		@Override
		public boolean onLongClick(View v) {
			CsstSHUseDeviceActivity deviceActivity = (CsstSHUseDeviceActivity) getActivity();
			CsstSHDRCBean key = deviceActivity.getDeviceRCKeyByIdentify(v);
			deviceActivity.new StudyCodeTask(key).execute();
			return false;
		}
	}
}
