package com.csst.smarthome.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 面板开关 1位 2位 3 位的面板开关都是用同样一个数据库。
 * @author liuyang
 */
public class CsstSHSwitchBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1657248999697611222L;

	/** 面板开关ID */
	private int mSwitchId = 0;
	//器件ID 通过该器件绑定DEVICE
	private int mDeviceId = 0;
	/** 面板开关名字1 */
	private String mSwitchName1 = null;
	/** 面板开关名字2 */
	private String mSwitchName2 = null;
	/** 面板开关名字3 */
	private String mSwitchName3 = null;
	/**开关量
	 * 
	 */
	private int switchonoff = 0;
	
	public CsstSHSwitchBean() {
	}
	
	public CsstSHSwitchBean(String floorName) {
		this.mSwitchName1 = floorName;
	}

	public CsstSHSwitchBean(int mSwitchId, String mSwitchName1,String mSwitchName2,
			String mSwitchName3,int switchonoff,int mDeviceId) {
		this.mSwitchId = mSwitchId;
		this.mSwitchName1 = mSwitchName1;
		this.mSwitchName2 = mSwitchName2;
		this.mSwitchName3 = mSwitchName3;
		this.switchonoff = switchonoff;
		this.mDeviceId = mDeviceId;
	}

	public CsstSHSwitchBean(String mSwitchName1,String mSwitchName2,
			String mSwitchName3,int switchonoff,int mDeviceId) {
		this.mSwitchName1 = mSwitchName1;
		this.mSwitchName2 = mSwitchName2;
		this.mSwitchName3 = mSwitchName3;
		this.switchonoff = switchonoff;
		this.mDeviceId = mDeviceId;
	}

	public int getmSwitchId() {
		return mSwitchId;
	}

	public void setmSwitchId(int mSwitchId) {
		this.mSwitchId = mSwitchId;
	}

	public int getmDeviceId() {
		return mDeviceId;
	}

	public void setmDeviceId(int mDeviceId) {
		this.mDeviceId = mDeviceId;
	}

	public String getmSwitchName1() {
		return mSwitchName1;
	}

	public void setmSwitchName1(String mSwitchName1) {
		this.mSwitchName1 = mSwitchName1;
	}

	public String getmSwitchName2() {
		return mSwitchName2;
	}

	public void setmSwitchName2(String mSwitchName2) {
		this.mSwitchName2 = mSwitchName2;
	}

	public String getmSwitchName3() {
		return mSwitchName3;
	}

	public void setmSwitchName3(String mSwitchName3) {
		this.mSwitchName3 = mSwitchName3;
	}

	public int getSwitchonoff() {
		return switchonoff;
	}

	public void setSwitchonoff(int switchonoff) {
		this.switchonoff = switchonoff;
	}
	@Override
	public String toString() {
		return mSwitchName1 + ")" + mSwitchName2+ ")" + mSwitchName3+
				")" + switchonoff+ ")"+ mDeviceId;
	}

	
}
