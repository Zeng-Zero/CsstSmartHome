package com.csst.smarthome.activity.camera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHCameraBean;
import com.csst.smarthome.camera.ContentCommon;

public class CasstSHCamera {
	
	private Context mContext = null;
	
	/** Cameras状态 */
	private int mCameraState = ContentCommon.PPPP_STATUS_UNKNOWN;
	private CsstSHCameraBean mCameraBean = null;
	private View mView = null;
	private ImageView mIVLastFrame = null;
	private TextView mTVCameraName = null;
	private TextView mTVCameraUuid = null;
	private TextView mTVCameraState = null;
	private LayoutInflater mInflater = null;

	public CasstSHCamera(Context mContext, CsstSHCameraBean mCameraBean) {
		this.mContext = mContext;
		this.mCameraBean = mCameraBean;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = mInflater.inflate(R.layout.csst_camera_item_layout, null);
		mIVLastFrame = (ImageView) mView.findViewById(R.id.mIVLastFrame);
		mTVCameraName = (TextView) mView.findViewById(R.id.mTVCameraName);
		mTVCameraUuid = (TextView) mView.findViewById(R.id.mTVCameraUuid);
		mTVCameraState = (TextView) mView.findViewById(R.id.mTVCameraState);
		mIVLastFrame.setImageResource(R.drawable.csst_preset_030);
		mTVCameraName.setText(mCameraBean.getCameraName());
		mTVCameraUuid.setText(mCameraBean.getCameraUuid());
		mTVCameraState.setText(cameraState(mContext, mCameraState));
		mView.setTag(this);
	}
	
	public final View getCameraView(){
		return mView;
	}
	
	public final void setCameraState(int state){
		mCameraState = state;
		mTVCameraState.setText(cameraState(mContext, mCameraState));
	}
	
	public CsstSHCameraBean getCameraBean() {
		return mCameraBean;
	}

	public boolean isOnLine(){
		return mCameraState == ContentCommon.PPPP_STATUS_ON_LINE;
	}
	
	/**
	 * 摄像机状态
	 * @param context
	 * @param state
	 * @return
	 */
	public static final String cameraState(Context context,int state){
		int stateRes = R.string.csst_camera_pppp_status_unknown;
		switch (state) {
			case ContentCommon.PPPP_STATUS_CONNECTING://0
				//正在连接
				stateRes = R.string.csst_camera_pppp_status_connecting;
				break;
			case ContentCommon.PPPP_STATUS_CONNECT_FAILED://3
				//连接失败
				stateRes = R.string.csst_camera_pppp_status_connect_failed;
				break;
			case ContentCommon.PPPP_STATUS_DISCONNECT://4
				//断线
				stateRes = R.string.csst_camera_pppp_status_disconnect;
				break;
			case ContentCommon.PPPP_STATUS_INITIALING://1
				//已连接, 正在初始化
				stateRes = R.string.csst_camera_pppp_status_initialing;
				break;
			case ContentCommon.PPPP_STATUS_INVALID_ID://5
				//ID号无效
				stateRes = R.string.csst_camera_pppp_status_invalid_id;
				break;
			case ContentCommon.PPPP_STATUS_ON_LINE://2
				//在线
				stateRes = R.string.csst_camera_pppp_status_online;
				break;
			case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE://6
				//摄像机不在线
				stateRes = R.string.csst_camera_device_not_on_line;
				break;
			case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT://7
				//连接超时
				stateRes = R.string.csst_camera_pppp_status_connect_timeout;
				break;
			case ContentCommon.PPPP_STATUS_CONNECT_ERRER://8
				//密码错误
				stateRes =R.string.csst_camera_pppp_status_pwd_error;
				break;
			default:
				//未知状态
				stateRes = R.string.csst_camera_pppp_status_unknown;
		}
		return context.getString(stateRes);
	}
	
	
}
