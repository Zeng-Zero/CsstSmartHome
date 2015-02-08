package com.csst.smarthome.bean;

import java.io.Serializable;

/**
 * 楼层对象
 * @author liuyang
 */
public class CsstSHSafeSensorBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1657248999697611222L;

	
	//id
	private int mSensorId =0;
	/** 楼层名字 */
	private String mSensorName = null;
	//电量
	private int mbattery =0;
	
	//是否选中
	private int mselect = 0;
	
	//sensor的SSUID_1
	private int mssuidLow = 0;
	//sensor的SSUID_M
	private int mssuidMid = 0;
	//sensor的SSUID_H
	private int mssuidHight = 0;
	//sensor type
	private int mType = 0;
	
	
	public CsstSHSafeSensorBean() {
	}
	
	public CsstSHSafeSensorBean(String mSensorName) {
		this.mSensorName = mSensorName;
	}

	public CsstSHSafeSensorBean(int SensorId, String mSensorName) {
		this.mSensorId = SensorId;
		this.mSensorName = mSensorName;
	}

	public CsstSHSafeSensorBean( String mSensorName,
			int battery, int select,int ssuidLow,int ssuidMid,int ssuidHight,int mType) {
		this.mSensorName = mSensorName;
		this.mbattery = battery;
		this.mselect = select;
		this.mssuidLow = ssuidLow;
		this.mssuidMid = ssuidMid;
		this.mssuidHight = ssuidHight;
		this.mType = mType;
		
	}
	
	public CsstSHSafeSensorBean(int SensorId, String mSensorName,
			int battery, int select,int ssuidLow,int ssuidMid,int ssuidHight,int mType) {
		this.mSensorId = SensorId;
		this.mSensorName = mSensorName;
		this.mbattery = battery;
		this.mselect = select;
		this.mssuidLow = ssuidLow;
		this.mssuidMid = ssuidMid;
		this.mssuidHight = ssuidHight;
		this.mType = mType;
		
	}

	public int getmSensorId() {
		return mSensorId;
	}

	public void setmSensorId(int mSensorId) {
		this.mSensorId = mSensorId;
	}

	public String getmSensorName() {
		return mSensorName;
	}

	public void setmSensorName(String mSensorName) {
		this.mSensorName = mSensorName;
	}

	public int getMbattery() {
		return mbattery;
	}

	public void setMbattery(int mbattery) {
		this.mbattery = mbattery;
	}

	public int isMselect() {
		return mselect;
	}

	public void setMselect(int mselect) {
		this.mselect = mselect;
	}

	public int getMssuidLow() {
		return mssuidLow;
	}

	public void setMssuidLow(int mssuidLow) {
		this.mssuidLow = mssuidLow;
	}

	public int getMssuidMid() {
		return mssuidMid;
	}

	public void setMssuidMid(int mssuidMid) {
		this.mssuidMid = mssuidMid;
	}

	public int getMssuidHight() {
		return mssuidHight;
	}

	public void setMssuidHight(int mssuidHight) {
		this.mssuidHight = mssuidHight;
	}

	public int getmType() {
		return mType;
	}

	public void setmType(int mType) {
		this.mType = mType;
	}
	@Override
	public String toString() {
		return mSensorName + ")" + mbattery+ ")" + mselect+
				")" + mssuidLow+ ")"+ mssuidMid+ ")" + mssuidHight+ ")" + mType;
	}
}
