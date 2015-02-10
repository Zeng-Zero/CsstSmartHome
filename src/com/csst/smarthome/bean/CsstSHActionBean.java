package com.csst.smarthome.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 动作对象
 * @author liuyang
 */
public class CsstSHActionBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1657248999697611222L;

	/** 动作ID */
	private int mActionId = 0;
	
	/** 动作名字 */
	private String mActionName = null;
	
	/** 动作位置 */
	private String mLocation = null;

	/** 动作键值 */
	private int resultAction = 0;
	

	/** 动作键值 */
	private String mKeyCode = null;
	
	/** 动作延时 */
	private int mDelayTime ;
	/** 情景模式ID*/
	private int mModelId ;
	
	public CsstSHActionBean() {
	}
	
	public CsstSHActionBean(String mActionName) {
		this.mActionName = mActionName;
	}

	public CsstSHActionBean(int mActionId, String mActionName) {
		this.mActionId = mActionId;
		this.mActionName = mActionName;
	}
/**
 *  用来查询用的构建函数
 * @param mActionId
 * @param mActionName
 * @param mLocation
 * @param mKeyCode
 * @param mDelayTime
 * @param mModelId
 */
	public CsstSHActionBean(int mActionId, String mActionName, String mLocation,
			String mKeyCode,int mDelayTime, int mModelId,int resultAction) {
		this.mActionId = mActionId;
		this.mActionName = mActionName;
		this.mLocation = mLocation;
		this.mKeyCode = mKeyCode;
		this.mDelayTime = mDelayTime;
		this.mModelId = mModelId;
		this.resultAction = resultAction;
	}
	/**
	 * 用来插入一条数据时候用的
	 * @param mActionName
	 * @param mLocation
	 * @param mKeyCode
	 * @param mDelayTime
	 * @param mModelId
	 */
	public CsstSHActionBean(String mActionName, String mLocation,
			String mKeyCode,int mDelayTime, int mModelId,int resultAction) {
		this.mActionName = mActionName;
		this.mLocation = mLocation;
		this.mKeyCode = mKeyCode;
		this.mDelayTime = mDelayTime;
		this.mModelId = mModelId;
		this.resultAction = resultAction;
	}
	

	public int getmActionId() {
		return mActionId;
	}

	public void setmActionId(int mActionId) {
		this.mActionId = mActionId;
	}

	public String getmActionName() {
		return mActionName;
	}

	public void setmActionName(String mActionName) {
		this.mActionName = mActionName;
	}

	public String getmLocation() {
		return mLocation;
	}

	public void setmLocation(String mLocation) {
		this.mLocation = mLocation;
	}

	public String getmKeyCode() {
		return mKeyCode;
	}

	public void setmKeyCode(String mKeyCode) {
		this.mKeyCode = mKeyCode;
	}

	public int getmDelayTime() {
		return mDelayTime;
	}

	public void setmDelayTime(int mDelayTime) {
		this.mDelayTime = mDelayTime;
	}

	public int getmModelId() {
		return mModelId;
	}

	public int getResultAction() {
		return resultAction;
	}

	public void setResultAction(int resultAction) {
		this.resultAction = resultAction;
	}
	public void setmModelId(int mModelId) {
		this.mModelId = mModelId;
	}
	
	@Override
	public String toString() {
		return  mActionName+")"+ mLocation + ")" 
		+ mKeyCode+ ")" + mDelayTime+ ")" + mModelId+ ")" + resultAction;
	}


	
}
