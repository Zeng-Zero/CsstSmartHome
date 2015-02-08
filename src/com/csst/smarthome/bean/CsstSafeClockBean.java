package com.csst.smarthome.bean;

import java.io.Serializable;

/**
 * 定时对象
 * @author liuyang
 */
public class CsstSafeClockBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1657248999697631232L;

	/** 定时ID */
	private int mClockOpenId = 0;
	
	/** 定时时间小时 */
	private int mClockOpenTimeHour ;
	/** 定时时间分钟 */
	private int mClockOpenTimeMin ;

	/** 定时键值 */
	private int mClockOpenDay ;
	/** mClockOpenopenFlag*/
	private int mClockOpenopenFlag ;
	

	public CsstSafeClockBean() {
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
	public CsstSafeClockBean(int mClockOpenId, int mClockOpenDay,int mClockOpenTimeHour,
			int mClockOpenTimeMin,int mClockOpenopenFlag) {
		this.mClockOpenId = mClockOpenId;
		this.mClockOpenTimeHour = mClockOpenTimeHour;
		this.mClockOpenTimeMin = mClockOpenTimeMin;
		this.mClockOpenDay = mClockOpenDay;
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
	public CsstSafeClockBean(int mClockOpenDay, int mClockOpenTimeHour,
			int mClockOpenTimeMin,int mClockOpenopenFlag) {
		this.mClockOpenTimeHour = mClockOpenTimeHour;
		this.mClockOpenTimeMin = mClockOpenTimeMin;
		this.mClockOpenDay = mClockOpenDay;
		this.mClockOpenopenFlag = mClockOpenopenFlag;
	}
	

	public int getmClockOpenId() {
		return mClockOpenId;
	}

	public void setmClockOpenId(int mClockOpenId) {
		this.mClockOpenId = mClockOpenId;
	}


	public int getmClockOpenDay() {
		return mClockOpenDay;
	}

	public void setmClockOpenDay(int mClockOpenDay) {
		this.mClockOpenDay = mClockOpenDay;
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
		return   mClockOpenDay+ ")"+mClockOpenTimeHour + ")" 
		+ mClockOpenTimeMin+ ")" + mClockOpenopenFlag;
	}

	
}
