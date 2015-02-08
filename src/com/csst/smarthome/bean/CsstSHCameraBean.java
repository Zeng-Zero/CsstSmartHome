package com.csst.smarthome.bean;

import java.io.Serializable;

/**
 * Camera视频监控
 * @author liuyang
 */
public class CsstSHCameraBean implements Serializable {

	/**
	 */
	private static final long serialVersionUID = 8815767902803749410L;
	
	/** 房间id */
	private int mCameraId = 0;
	
	/** 房间名称 */
	private String mCameraName = null;
	
	private String mCameraAccount = null;
	
	private String mCameraPassword = null;
	
	private String mCameraUuid = null;

	public CsstSHCameraBean() {
	}

	public CsstSHCameraBean(String name, String account,
			String password, String uuid) {
		this.mCameraName = name;
		this.mCameraAccount = account;
		this.mCameraPassword = password;
		this.mCameraUuid = uuid;
	}
	
	public CsstSHCameraBean(int id, String name, String account,
			String password, String uuid) {
		this.mCameraName = name;
		this.mCameraAccount = account;
		this.mCameraPassword = password;
		this.mCameraUuid = uuid;
		this.mCameraId = id;
	}

	public int getCameraId() {
		return mCameraId;
	}

	public void setCameraId(int cameraId) {
		this.mCameraId = cameraId;
	}

	public String getCameraName() {
		return mCameraName;
	}

	public void setCameraName(String cameraName) {
		this.mCameraName = cameraName;
	}

	public String getCameraAccount() {
		return mCameraAccount;
	}

	public void setCameraAccount(String cameraAccount) {
		this.mCameraAccount = cameraAccount;
	}

	public String getCameraPassword() {
		return mCameraPassword;
	}

	public void setCameraPassword(String cameraPassword) {
		this.mCameraPassword = cameraPassword;
	}

	public String getCameraUuid() {
		return mCameraUuid;
	}

	public void setCameraUuid(String cameraUuid) {
		this.mCameraUuid = cameraUuid;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) 
			return true;
        if (o == null || getClass() != o.getClass()) 
        	return false;
        CsstSHCameraBean that = (CsstSHCameraBean) o;
        return mCameraUuid.equals(that.mCameraUuid);
	}

	@Override
	public String toString() {
		return "CsstSHCameraBean [mCameraId=" + mCameraId + ", mCameraName="
				+ mCameraName + ", mCameraAccount=" + mCameraAccount
				+ ", mCameraPassword=" + mCameraPassword + ", mCameraUuid="
				+ mCameraUuid + "]";
	}
	
	public String toString2() {
		return  mCameraName + ")"+ mCameraAccount+")"+ mCameraPassword + ")" 
		+ mCameraUuid;
	}

}
