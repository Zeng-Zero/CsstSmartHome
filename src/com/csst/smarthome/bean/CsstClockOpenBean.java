package com.csst.smarthome.bean;

import java.io.Serializable;

/**
 * 定时对象
 * @author liuyang
 */
public class CsstClockOpenBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1657248999697611232L;

	/** 定时ID */
	private int mClockOpenId = 0;
	
	/** 定时名字 */
	private String mClockOpenName = null;
	
	/** 定时时间小时 */
	private int mClockOpenTimeHour ;
	/** 定时时间分钟 */
	private int mClockOpenTimeMin ;

	/** 定时键值 */
	private int mClockOpenDay ;
	/** 情景模式ID*/
	private int mClockOpenModelId ;
	/** mClockOpenopenFlag*/
	private int mClockOpenopenFlag ;
	

	public CsstClockOpenBean() {
	}
	
	public CsstClockOpenBean(String mClockOpenName) {
		this.mClockOpenName = mClockOpenName;
	}

	public CsstClockOpenBean(int mClockOpenId, String mClockOpenName) {
		this.mClockOpenId = mClockOpenId;
		this.mClockOpenName = mClockOpenName;
	}
/**
 *  用来查询用的构建函数
 * @param mClockOpenId
 * @param mClockOpenName
 * @param mClockOpenTime
 * @param mClockOpenDay
 * @param mClockOpenAutoFlag
 * @param mClockOpenModelId
 */
	public CsstClockOpenBean(int mClockOpenId, String mClockOpenName, int mClockOpenTimeHour,
			int mClockOpenTimeMin,int mClockOpenDay, int mClockOpenModelId,int mClockOpenopenFlag) {
		this.mClockOpenId = mClockOpenId;
		this.mClockOpenName = mClockOpenName;
		this.mClockOpenTimeHour = mClockOpenTimeHour;
		this.mClockOpenTimeMin = mClockOpenTimeMin;
		this.mClockOpenDay = mClockOpenDay;
		this.mClockOpenModelId = mClockOpenModelId;
		this.mClockOpenopenFlag = mClockOpenopenFlag;
	}
	/**
	 * 用来插入一条数据时候用的
	 * @param mClockOpenName
	 * @param mClockOpenTime
	 * @param mClockOpenDay
	 * @param mClockOpenAutoFlag
	 * @param mClockOpenModelId
	 */
	public CsstClockOpenBean(String mClockOpenName, int mClockOpenTimeHour,
			int mClockOpenTimeMin,int mClockOpenDay, int mClockOpenModelId,int mClockOpenopenFlag) {
		this.mClockOpenName = mClockOpenName;
		this.mClockOpenTimeHour = mClockOpenTimeHour;
		this.mClockOpenTimeMin = mClockOpenTimeMin;
		this.mClockOpenDay = mClockOpenDay;
		this.mClockOpenModelId = mClockOpenModelId;
		this.mClockOpenopenFlag = mClockOpenopenFlag;
	}
	

	public int getmClockOpenId() {
		return mClockOpenId;
	}

	public void setmClockOpenId(int mClockOpenId) {
		this.mClockOpenId = mClockOpenId;
	}

	public String getmClockOpenName() {
		return mClockOpenName;
	}

	public void setmClockOpenName(String mClockOpenName) {
		this.mClockOpenName = mClockOpenName;
	}


	public int getmClockOpenDay() {
		return mClockOpenDay;
	}

	public void setmClockOpenDay(int mClockOpenDay) {
		this.mClockOpenDay = mClockOpenDay;
	}
	public int getmClockOpenModelId() {
		return mClockOpenModelId;
	}

	public void setmClockOpenModelId(int mClockOpenModelId) {
		this.mClockOpenModelId = mClockOpenModelId;
	}

	public int getmClockOpenTimeHour() {
		return mClockOpenTimeHour;
	}

	public void setmClockOpenTimeHour(int mClockOpenTimeHour) {
		this.mClockOpenTimeHour = mClockOpenTimeHour;
	}

	public int getmClockOpenTimeMin() {
		return mClockOpenTimeMin;
	}

	public void setmClockOpenTimeMin(int mClockOpenTimeMin) {
		this.mClockOpenTimeMin = mClockOpenTimeMin;
	}

	public int getmClockOpenopenFlag() {
		return mClockOpenopenFlag;
	}

	public void setmClockOpenopenFlag(int mClockOpenopenFlag) {
		this.mClockOpenopenFlag = mClockOpenopenFlag;
	}

	@Override
	public String toString() {
		return  mClockOpenName+")"+ mClockOpenTimeHour + ")" 
		+ mClockOpenTimeMin+ ")" + mClockOpenDay+ ")" + mClockOpenModelId+ ")" + mClockOpenopenFlag;
	}

	
}
