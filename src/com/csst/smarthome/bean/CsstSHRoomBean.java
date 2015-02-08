package com.csst.smarthome.bean;

import java.io.Serializable;

/**
 * 房间对象
 * @author liuyang
 */
public class CsstSHRoomBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4585322479674310056L;

	/** 房间id */
	private int mRoomId = 0;
	
	/** 房间名称 */
	private String mRoomName = null;
	
	/** 房间所属的楼层id */
	private int mFloorId = 0;

	public CsstSHRoomBean() {
	}
	
	public CsstSHRoomBean(String mRoomName, int mFloorId) {
		this.mRoomName = mRoomName;
		this.mFloorId = mFloorId;
	}

	public CsstSHRoomBean(int roomId, String roomName, int floorId) {
		this.mRoomId = roomId;
		this.mRoomName = roomName;
		this.mFloorId = floorId;
	}

	public int getRoomId() {
		return mRoomId;
	}

	public void setRoomId(int roomId) {
		this.mRoomId = roomId;
	}

	public String getRoomName() {
		return mRoomName;
	}

	public void setRoomName(String roomName) {
		this.mRoomName = roomName;
	}

	public int getFloorId() {
		return mFloorId;
	}

	public void setFloorId(int floorId) {
		this.mFloorId = floorId;
	}

	@Override
	public String toString() {
		return mRoomName;
	}
	
}
