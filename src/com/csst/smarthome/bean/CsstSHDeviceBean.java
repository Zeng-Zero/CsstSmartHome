package com.csst.smarthome.bean;

import java.io.Serializable;

/**
 * 设备对象
 * @author Administrator
 *
 */
public class CsstSHDeviceBean implements Serializable {

	/**
	 */
	private static final long serialVersionUID = -7426232144354690713L;

	/** 设备id */
	private int mDeviceId = 0;
	
	/** 设备名字 */
	private String mDeviceName = null;
	
	/** 设备封面是否内置 */
	private boolean mDeviceIconPersert = false;
	
	/** 设备头像路径 */
	private String mDeviceIconPath = null;
	
	/** 遥控器类型id */
	private int mRCTypeId = 0;
	
	/** 设备物理地址id */
	private String mDeviceSSID = null;

	/** 房间id */
	private int mRoomId = 0;
	
	/** 设备遥控器是否是自定义遥控器 */
	private boolean mRCCustom = false;
	
	/** 是否是搜索的设备 */
	private int mIsSearched = 0;

	public CsstSHDeviceBean() {
	}
	
	/**
	 * 添加设备用的构造器
	 * @param deviceName
	 * @param iconPersert
	 * @param deviceIconPath
	 * @param rCTypeId
	 * @param deviceSSID
	 * @param roomId
	 * @param isSearched
	 */
	public CsstSHDeviceBean(String deviceName, boolean iconPersert, 
			String deviceIconPath, int rCTypeId, String deviceSSID,
			int roomId, int isSearched) {
		this.mDeviceName = deviceName;
		this.mDeviceIconPersert = iconPersert;
		this.mDeviceIconPath = deviceIconPath;
		this.mRCTypeId = rCTypeId;
		this.mDeviceSSID = deviceSSID;
		this.mRoomId = roomId;
		this.mRCCustom = false;
		this.mIsSearched = isSearched;
	}
	
	/**
	 * 创建自定义遥控器用的构造器
	 * @param deviceName
	 * @param iconPersert
	 * @param deviceIconPath
	 * @param rCTypeId
	 * @param deviceSSID
	 * @param roomId
	 * @param rCCustom
	 */
	public CsstSHDeviceBean(String deviceName, boolean iconPersert, 
			String deviceIconPath, int rCTypeId, String deviceSSID,
			int roomId, boolean rCCustom, int isSearched) {
		this.mDeviceName = deviceName;
		this.mDeviceIconPersert = iconPersert;
		this.mDeviceIconPath = deviceIconPath;
		this.mRCTypeId = rCTypeId;
		this.mDeviceSSID = deviceSSID;
		this.mRoomId = roomId;
		this.mRCCustom = rCCustom;
		this.mIsSearched = isSearched;
	}
	
	/**
	 * 查询设备用的构造器
	 * @param deviceId
	 * @param deviceName
	 * @param iconPersert
	 * @param deviceIconPath
	 * @param rCTypeId
	 * @param deviceSSID
	 * @param roomId
	 * @param rCCustom
	 */
	public CsstSHDeviceBean(int deviceId, String deviceName, boolean iconPersert, 
			String deviceIconPath, int rCTypeId, String deviceSSID,
			int roomId, boolean rCCustom, int isSearched) {
		this.mDeviceId = deviceId;
		this.mDeviceName = deviceName;
		this.mDeviceIconPersert = iconPersert;
		this.mDeviceIconPath = deviceIconPath;
		this.mRCTypeId = rCTypeId;
		this.mDeviceSSID = deviceSSID;
		this.mRoomId = roomId;
		this.mRCCustom = rCCustom;
		this.mIsSearched = isSearched;
	}

	public int getDeviceId() {
		return mDeviceId;
	}

	public void setDeviceId(int deviceId) {
		this.mDeviceId = deviceId;
	}

	public String getDeviceName() {
		return mDeviceName;
	}

	public void setDeviceName(String deviceName) {
		this.mDeviceName = deviceName;
	}

	public boolean isDeviceIconPersert() {
		return mDeviceIconPersert;
	}

	public void setDeviceIconPersert(boolean deviceIconPersert) {
		this.mDeviceIconPersert = deviceIconPersert;
	}

	public String getDeviceIconPath() {
		return mDeviceIconPath;
	}

	public void setDeviceIconPath(String deviceIconPath) {
		this.mDeviceIconPath = deviceIconPath;
	}

	public int getRCTypeId() {
		return mRCTypeId;
	}

	public void setRCTypeId(int rCTypeId) {
		this.mRCTypeId = rCTypeId;
	}

	public String getDeviceSSID() {
		return mDeviceSSID;
	}

	public void setDeviceSSID(String deviceSSID) {
		this.mDeviceSSID = deviceSSID;
	}

	public int getRoomId() {
		return mRoomId;
	}

	public void setRoomId(int roomId) {
		this.mRoomId = roomId;
	}

	public boolean isRCCustom() {
		return mRCCustom;
	}

	public void setRCCustom(boolean rCCustom) {
		this.mRCCustom = rCCustom;
	}

	public int isSearched() {
		return mIsSearched;
	}

	public void setSearched(int isSearched) {
		this.mIsSearched = isSearched;
	}

	@Override
	public String toString() {
		return "CsstSHDeviceBean [mDeviceId=" + mDeviceId + ", mDeviceName="
				+ mDeviceName + ", mDeviceIconPersert=" + mDeviceIconPersert
				+ ", mDeviceIconPath=" + mDeviceIconPath + ", mRCTypeId="
				+ mRCTypeId + ", mDeviceSSID=" + mDeviceSSID + ", mRoomId="
				+ mRoomId  + ", mIsSearched=" + mIsSearched + ", mRCCustom=" + mRCCustom + "]";
	}
	
	public String toString2() {
		return  mDeviceName+")"+ mDeviceIconPersert + ")" 
		+ mDeviceIconPath+ ")" + mRCTypeId+ ")" + mDeviceSSID+ ")" + mRoomId+ 
		")" + mRCCustom+ ")" + mIsSearched;
	}
}
