package com.csst.smarthome.bean;

import java.io.Serializable;

import com.csst.smarthome.common.ICsstSHConstant;

/**
 * 遥控器类型对应的按键
 * @author liuyang
 *
 */
public class CsstSHRCKeyBean implements Serializable, ICsstSHConstant {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6868797657668712415L;

	/** 按键id */
	private int mRCKeyId = 0;
	
	/** 遥控器类型id */
	private int mRCTypeId = 0;
	
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

	public CsstSHRCKeyBean() {
	}
	
	public CsstSHRCKeyBean(int rCTypeId, String rCKeyName,
			int rCKeySize, int rCKeyX, int rCKeyY, int rCKeyW, int rCKeyH,
			int rCKeyIcon, int rCKeyPage, int rCKeyIdentify) {
		this.mRCTypeId = rCTypeId;
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

	public CsstSHRCKeyBean(int rCKeyId, int rCTypeId, String rCKeyName,
			int rCKeySize, int rCKeyX, int rCKeyY, int rCKeyW, int rCKeyH,
			int rCKeyIcon, int rCKeyPage, int rCKeyIdentify) {
		this.mRCKeyId = rCKeyId;
		this.mRCTypeId = rCTypeId;
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

	public int getRCKeyId() {
		return mRCKeyId;
	}

	public void setRCKeyId(int rCKeyId) {
		this.mRCKeyId = rCKeyId;
	}

	public int getRCTypeId() {
		return mRCTypeId;
	}

	public void setRCTypeId(int rCTypeId) {
		this.mRCTypeId = rCTypeId;
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
	public String toString() {
		return "CsstSHRCKeyBean [mRCKeyId=" + mRCKeyId + ", mRCTypeId="
				+ mRCTypeId + ", mRCKeyName=" + mRCKeyName + ", mRCKeySize="
				+ mRCKeySize + ", mRCKeyX=" + mRCKeyX + ", mRCKeyY=" + mRCKeyY
				+ ", mRCKeyW=" + mRCKeyW + ", mRCKeyH=" + mRCKeyH
				+ ", mRCKeyIcon=" + mRCKeyIcon + ", mRCKeyPage=" + mRCKeyPage
				+ ", mRCKeyIdentify=" + mRCKeyIdentify +  "]";
	}
	public String toString2() {
		return mRCKeyId + ","
				+ mRCTypeId + "," + mRCKeyName + ","
				+ mRCKeySize + "," + mRCKeyX + "," + mRCKeyY
				+ "," + mRCKeyW + "," + mRCKeyH
				+ "," + mRCKeyIcon + "," + mRCKeyPage
				+ "," + mRCKeyIdentify;
	}
	
}
