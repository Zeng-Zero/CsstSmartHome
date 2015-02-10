package com.csst.smarthome.bean;

import java.io.Serializable;

import com.csst.smarthome.common.ICsstSHConstant;

/**
 * 遥控器类型对应的按键
 * @author liuyang
 *
 */
public class CsstSHCustomRCKeyBean implements Serializable, ICsstSHConstant {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6868797657668712415L;

	/** 按键id */
	private int mCustomRCKeyId = 0;
	
	/** 按键名称 */
	private String mCustommCustomRCKeyName = null;
	
	/** 按键图标Id */
	private int mCustomRCKeyDrawbleId = 0;
	
	/** 按键代码 */
	private String mCustomRCKeyCode = null;
	
	/** Device ID*/
	private int mCustomRCKeyDeviceId = 0;

	public CsstSHCustomRCKeyBean() {
	}
	
	public CsstSHCustomRCKeyBean(int mCustomRCKeyId, String mCustomRCKeyName,
			int mCustomRCKeyDrawbleId, String mCustomRCKeyCode, int mCustomRCKeyDeviceId) {
		this.mCustomRCKeyId = mCustomRCKeyId;
		this.mCustommCustomRCKeyName = mCustomRCKeyName;
		this.mCustomRCKeyDrawbleId = mCustomRCKeyDrawbleId;
		this.mCustomRCKeyCode = mCustomRCKeyCode;
		this.mCustomRCKeyDeviceId = mCustomRCKeyDeviceId;
	}

	public CsstSHCustomRCKeyBean(String mCustomRCKeyName,
			int mCustomRCKeyDrawbleId, String mCustomRCKeyCode, int mCustomRCKeyDeviceId) {
		this.mCustommCustomRCKeyName = mCustomRCKeyName;
		this.mCustomRCKeyDrawbleId = mCustomRCKeyDrawbleId;
		this.mCustomRCKeyCode = mCustomRCKeyCode;
		this.mCustomRCKeyDeviceId = mCustomRCKeyDeviceId;
	}

	public int getmCustomRCKeyId() {
		return mCustomRCKeyId;
	}

	public void setmCustomRCKeyId(int mCustomRCKeyId) {
		this.mCustomRCKeyId = mCustomRCKeyId;
	}

	public String getmCustommCustomRCKeyName() {
		return mCustommCustomRCKeyName;
	}

	public void setmCustommCustomRCKeyName(String mCustommCustomRCKeyName) {
		this.mCustommCustomRCKeyName = mCustommCustomRCKeyName;
	}

	public int getmCustomRCKeyDrawbleId() {
		return mCustomRCKeyDrawbleId;
	}

	public void setmCustomRCKeyDrawbleId(int mCustomRCKeyDrawbleId) {
		this.mCustomRCKeyDrawbleId = mCustomRCKeyDrawbleId;
	}

	public String getmCustomRCKeyCode() {
		return mCustomRCKeyCode;
	}

	public void setmCustomRCKeyCode(String mCustomRCKeyCode) {
		this.mCustomRCKeyCode = mCustomRCKeyCode;
	}

	public int getmCustomRCKeyDeviceId() {
		return mCustomRCKeyDeviceId;
	}

	public void setmCustomRCKeyDeviceId(int mCustomRCKeyDeviceId) {
		this.mCustomRCKeyDeviceId = mCustomRCKeyDeviceId;
	}
	public String toString() {
		return mCustommCustomRCKeyName+")"+ mCustomRCKeyDrawbleId + ")" 
		+ mCustomRCKeyCode+ ")" + mCustomRCKeyDeviceId;
	}
	
}
