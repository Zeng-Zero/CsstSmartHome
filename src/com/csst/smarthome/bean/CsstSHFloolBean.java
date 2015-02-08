package com.csst.smarthome.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 楼层对象
 * @author liuyang
 */
public class CsstSHFloolBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1657248999697611222L;

	/** 楼层ID */
	private int mFloorId = 0;
	
	/** 楼层名字 */
	private String mFloorName = null;
	
	/** 楼层对应的房间 */
	private List<CsstSHRoomBean> mRooms = null;
	
	public CsstSHFloolBean() {
	}
	
	public CsstSHFloolBean(String floorName) {
		this.mFloorName = floorName;
	}

	public CsstSHFloolBean(int floorId, String floorName) {
		this.mFloorId = floorId;
		this.mFloorName = floorName;
	}

	public CsstSHFloolBean(int floorId, String floorName,
			List<CsstSHRoomBean> rooms) {
		this.mFloorId = floorId;
		this.mFloorName = floorName;
		this.mRooms = rooms;
	}

	public int getFloorId() {
		return mFloorId;
	}

	public void setFloorId(int floorId) {
		this.mFloorId = floorId;
	}

	public String getFloorName() {
		return mFloorName;
	}

	public void setFloorName(String floorName) {
		this.mFloorName = floorName;
	}
	
	public List<CsstSHRoomBean> getRooms() {
		return mRooms;
	}

	public void setmRooms(List<CsstSHRoomBean> rooms) {
		this.mRooms = rooms;
	}

	@Override
	public String toString() {
		return "CsstSHFloolBean [mFloorId=" + mFloorId + ", mFloorName="
				+ mFloorName + "]";
	}
	public String toString2() {
		return  mFloorName;
	}
}
