package com.csst.smarthome.bean;

import java.io.Serializable;

import com.csst.smarthome.common.ICsstSHConstant;

/***
 * 设备绑定的遥控器
 * @author liuyang
 */
public class CsstSHDRCBean implements Serializable, ICsstSHConstant {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8799429335033153442L;

	/** 设备绑定遥控器id */
	private int mDRCId = 0;
	
	/** 设备id */
	private int mDeviceId = 0;
	
	/** 遥控器对应的按键命令 */
	private String mDRCCmdCode = null;
	
	/** 按键名 */
	private String mRCKeyName = null;
	
	/** 按键大小类型 */
	private int mRCKeySize = LSIZE;
	
	/** 按键X坐标 */
	private int mRCKeyX = 0;
	
	/** 按键Y坐标 */
	private int mRCKeyY = 0;
	
	/** 按键宽度 */
	private int mRCKeyW = 0;
	
	/** 按键高度 */
	private int mRCKeyH = 0;
	
	/** 按键图片id */
	private int mRCKeyIcon = 0;
	
	/** 按键所在的页 */
	private int mRCKeyPage = 0;
	
	/** 按键标示符 */
	private int mRCKeyIdentify = 0;

	public CsstSHDRCBean() {
	}

	public CsstSHDRCBean(int deviceId, String dRCCmdCode,
			String rCKeyName, int rCKeySize, int rCKeyX, int rCKeyY,
			int rCKeyW, int rCKeyH, int rCKeyIcon, int rCKeyPage, int rCKeyIdentify) {
		this.mDeviceId = deviceId;
		this.mDRCCmdCode = dRCCmdCode;
		this.mRCKeyName = rCKeyName;
		this.mRCKeySize = rCKeySize;
		this.mRCKeyX = rCKeyX;
		this.mRCKeyY = rCKeyY;
		this.mRCKeyW = rCKeyW;
		this.mRCKeyH = rCKeyH;
		this.mRCKeyIcon = rCKeyIcon;
		this.mRCKeyPage = rCKeyPage;
		this.mRCKeyIdentify = rCKeyIdentify;
	}

	public CsstSHDRCBean(int dRCId, int deviceId, String dRCCmdCode,
			String rCKeyName, int rCKeySize, int rCKeyX, int rCKeyY,
			int rCKeyW, int rCKeyH, int rCKeyIcon, int rCKeyPage, int rCKeyIdentify) {
		this.mDRCId = dRCId;
		this.mDeviceId = deviceId;
		this.mDRCCmdCode = dRCCmdCode;
		this.mRCKeyName = rCKeyName;
		this.mRCKeySize = rCKeySize;
		this.mRCKeyX = rCKeyX;
		this.mRCKeyY = rCKeyY;
		this.mRCKeyW = rCKeyW;
		this.mRCKeyH = rCKeyH;
		this.mRCKeyIcon = rCKeyIcon;
		this.mRCKeyPage = rCKeyPage;
		this.mRCKeyIdentify = rCKeyIdentify;
	}

	public int getDRCId() {
		return mDRCId;
	}

	public void setDRCId(int dRCId) {
		this.mDRCId = dRCId;
	}

	public int getDeviceId() {
		return mDeviceId;
	}

	public void setDeviceId(int deviceId) {
		this.mDeviceId = deviceId;
	}

	public String getDRCCmdCode() {
		return mDRCCmdCode;
	}

	public void setDRCCmdCode(String dRCCmdCode) {
		this.mDRCCmdCode = dRCCmdCode;
	}

	public String getRCKeyName() {
		return mRCKeyName;
	}

	public void setRCKeyName(String rCKeyName) {
		this.mRCKeyName = rCKeyName;
	}

	public int getRCKeySize() {
		return mRCKeySize;
	}

	public void setRCKeySize(int rCKeySize) {
		this.mRCKeySize = rCKeySize;
	}

	public int getRCKeyX() {
		return mRCKeyX;
	}

	public void setRCKeyX(int rCKeyX) {
		this.mRCKeyX = rCKeyX;
	}

	public int getRCKeyY() {
		return mRCKeyY;
	}

	public void setRCKeyY(int rCKeyY) {
		this.mRCKeyY = rCKeyY;
	}

	public int getRCKeyW() {
		return mRCKeyW;
	}

	public void setRCKeyW(int rCKeyW) {
		this.mRCKeyW = rCKeyW;
	}

	public int getRCKeyH() {
		return mRCKeyH;
	}

	public void setRCKeyH(int rCKeyH) {
		this.mRCKeyH = rCKeyH;
	}

	public int getRCKeyIcon() {
		return mRCKeyIcon;
	}

	public void setRCKeyIcon(int rCKeyIcon) {
		this.mRCKeyIcon = rCKeyIcon;
	}

	public int getRCKeyPage() {
		return mRCKeyPage;
	}

	public void setRCKeyPage(int rCKeyPage) {
		this.mRCKeyPage = rCKeyPage;
	}

	public int getRCKeyIdentify() {
		return mRCKeyIdentify;
	}

	public void setRCKeyIdentify(int rCKeyIdentify) {
		this.mRCKeyIdentify = rCKeyIdentify;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) 
			return true;
        if (o == null || getClass() != o.getClass()) 
        	return false;
        CsstSHDRCBean rc = (CsstSHDRCBean) o;
        return mRCKeyIdentify == rc.mRCKeyIdentify;
	}

	@Override
	public String toString() {
		return "CsstSHDRCBean [mDRCId=" + mDRCId + ", mDeviceId=" + mDeviceId
				+ ", mDRCCmdCode=" + mDRCCmdCode + ", mRCKeyName=" + mRCKeyName
				+ ", mRCKeySize=" + mRCKeySize + ", mRCKeyX=" + mRCKeyX
				+ ", mRCKeyY=" + mRCKeyY + ", mRCKeyW=" + mRCKeyW
				+ ", mRCKeyH=" + mRCKeyH + ", mRCKeyIcon=" + mRCKeyIcon
				+ ", mRCKeyPage=" + mRCKeyPage + ", mRCKeyIdentify=" + mRCKeyIdentify + "]";
	}
	public String toString2() {
		return  mDeviceId+")"+ mDRCCmdCode + ")" 
		+ mRCKeyName+ ")" + mRCKeySize+ ")" + mRCKeyX+ ")" + mRCKeyY+ 
		")" + mRCKeyW+ ")" + mRCKeyH+ ")" + mRCKeyIcon+")" + mRCKeyPage+")" + mRCKeyIdentify;
	}
}
