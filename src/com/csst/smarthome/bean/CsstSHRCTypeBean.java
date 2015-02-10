package com.csst.smarthome.bean;

import java.io.Serializable;

/**
 * 遥控器类型
 * @author liuyang
 *
 */
public class CsstSHRCTypeBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9115705840147384303L;

	/** 遥控器类型id */
	private int mRCTypeId = 0;
	
	/** 遥控器类型名称 */
	private String mRCTypeName = null;
	
	/** 遥控器类型页数 */
	private int mRCTypePageCount = 0;

	public CsstSHRCTypeBean() {
	}

	public CsstSHRCTypeBean(int rCTypeId, String rCTypeName,
			int rCTypePageCount) {
		this.mRCTypeId = rCTypeId;
		this.mRCTypeName = rCTypeName;
		this.mRCTypePageCount = rCTypePageCount;
	}

	public int getRCTypeId() {
		return mRCTypeId;
	}

	public void setRCTypeId(int mRCTypeId) {
		this.mRCTypeId = mRCTypeId;
	}

	public String getRCTypeName() {
		return mRCTypeName;
	}

	public void setRCTypeName(String mRCTypeName) {
		this.mRCTypeName = mRCTypeName;
	}

	public int getRCTypePageCount() {
		return mRCTypePageCount;
	}

	public void setRCTypePageCount(int rCTypePageCount) {
		this.mRCTypePageCount = rCTypePageCount;
	}

	@Override
	public String toString() {
		return "CsstSHRCTypeBean [mRCTypeId=" + mRCTypeId + ", mRCTypeName="
				+ mRCTypeName + ", mRCTypePageCount=" + mRCTypePageCount + "]";
	}
	public String toString2() {
		return mRCTypeId + ","
				+ mRCTypeName + "," + mRCTypePageCount;
	}
	
}
