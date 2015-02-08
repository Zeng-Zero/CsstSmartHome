package com.csst.smarthome.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 情景模式对象
 * @author liuyang
 */
public class CsstSHModelBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1457248999697611222L;

	/** 情景模式ID */
	private int mmodelId = 0;
	
	/** 情景模式名称 */
	private String mmodelName = null;
	
	/** 情景模式对应的图片 */
	private String mModelIconPath = null;
	
	public CsstSHModelBean() {
	}
	
	public CsstSHModelBean(String modelName) {
		this.mmodelName = modelName;
	}
/**
 * 用来插入表格时候的构建函数
 * @param modelId
 * @param modelName
 */
	public CsstSHModelBean(String modelName,String mModelIconPath) {
		this.mModelIconPath = mModelIconPath;
		this.mmodelName = modelName;
	}
/**
 * 用来查询用的构建函数
 * @param modelId
 * @param modelName
 * @param mModelIconPath
 */
	public CsstSHModelBean(int modelId, String modelName,
			String mModelIconPath ) {
		this.mmodelId = modelId;
		this.mmodelName = modelName;
		this.mModelIconPath = mModelIconPath;
	}

	public int getmodelId() {
		return mmodelId;
	}

	public void setmodelId(int modelId) {
		this.mmodelId = modelId;
	}

	public String getmodelName() {
		return mmodelName;
	}

	public void setmodelName(String modelName) {
		this.mmodelName = modelName;
	}
	
	public String getmModelIconPath() {
		return mModelIconPath;
	}

	public void setmRooms(String mModelIconPath) {
		this.mModelIconPath = mModelIconPath;
	}

	@Override
	public String toString() {
		return  mmodelName ;
	}
	
}
