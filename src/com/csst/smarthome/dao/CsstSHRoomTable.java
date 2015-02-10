package com.csst.smarthome.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csst.smarthome.bean.CsstSHDeviceBean;
import com.csst.smarthome.bean.CsstSHFloolBean;
import com.csst.smarthome.bean.CsstSHRoomBean;

/**
 * 房间表
 * @author liuyang
 */
public class CsstSHRoomTable implements ICsstSHDaoManager<CsstSHRoomBean>{
	
	public static final String TAG = "CsstSHRoomTable";
	
	/** 表名 */
	public static final String GEN_TABLE_NAME = "sh_room";
	/** 房间id */
	public static final String GEN_ROOM_ID = "sh_room_id";
	/** 房间名字 */
	public static final String GEN_ROOM_NAME = "sh_room_name";
	/** 楼层id */
	public static final String GEN_FLOOR_ID = CsstSHFloorTable.GEN_FLOOR_ID;
	
	/** 建表SQL语句 */
	public static String GEN_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + GEN_TABLE_NAME
			+ "(" + GEN_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ GEN_ROOM_NAME + " TEXT," 
			+ GEN_FLOOR_ID + " INTEGER NOT NULL)"; 
	/** 删除表 */
	public static String GEN_DROP_TABLE = "DROP TABLE IF EXISTS " + CsstSHRoomTable.GEN_TABLE_NAME;

	private static CsstSHRoomTable mInstance = null;
	
	private CsstSHRoomTable(){
		
	}
	
	public static final CsstSHRoomTable getInstance(){
		if (null == mInstance){
			mInstance = new CsstSHRoomTable();
		}
		return mInstance;
	}

	@Override
	public long insert(SQLiteDatabase db, CsstSHRoomBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		ContentValues cv = new ContentValues();
		cv.put(GEN_ROOM_NAME, arg0.getRoomName());
		cv.put(GEN_FLOOR_ID, arg0.getFloorId());
		return db.insert(table, null, cv);
	}

	@Override
	public long update(SQLiteDatabase db, CsstSHRoomBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_ROOM_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getRoomId())};
		ContentValues cv = new ContentValues();
		cv.put(GEN_ROOM_NAME, arg0.getRoomName());
		cv.put(GEN_FLOOR_ID, arg0.getFloorId());
		return db.update(table, cv, whereClause, whereArgs);
	}

	@Override
	public long delete(SQLiteDatabase db, CsstSHRoomBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_ROOM_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getRoomId())};
		return db.delete(table, whereClause, whereArgs);
	}
	
	/**
	 * 根据楼层删除对应的房间
	 * @param db
	 * @param arg0
	 * @return
	 * @throws RuntimeException
	 */
	public long delete(SQLiteDatabase db, CsstSHFloolBean arg0)
			throws RuntimeException {
		return deleteByFloorId(db, arg0.getFloorId());
	}
	
	/**
	 * 通过场景id删除房间
	 * @param db
	 * @param floorid
	 * @return
	 */
	public final long deleteByFloorId(SQLiteDatabase db, int floorid){
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_FLOOR_ID + "=?";
		String[] whereArgs = {Integer.toString(floorid)};
		return db.delete(table, whereClause, whereArgs);
	}

	@Override
	public boolean columnExists(SQLiteDatabase db, String columnsName,
			Object columnsValue) {
		String table = GEN_TABLE_NAME;
		String[] columns = {columnsName};
		String selection = columnsName + "=?";
		String[] selectionArgs = {columnsValue.toString()};
		Cursor c = db.query(table, columns, selection, selectionArgs, null, null, null);
		return (c != null && c.moveToFirst() && c.getCount() > 0);
	}
	
	/**
	 * 同一场景下是否存在相同的房间名
	 * @param db
	 * @param roomName
	 * @param floorId
	 * @return
	 */
	public final boolean floorExistsSameRoom(SQLiteDatabase db, String roomName, int floorId){
		String table = GEN_TABLE_NAME;
		String selection = GEN_ROOM_NAME + "='" + roomName + "' AND " + GEN_FLOOR_ID + "=" + floorId;
		Cursor c = db.query(table, null, selection, null, null, null, null);
		return (c != null && c.moveToFirst() && c.getCount() > 0);
	}

	@Override
	public List<CsstSHRoomBean> query(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);
		List<CsstSHRoomBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHRoomBean>();
			int roomId = 0;
			int floorId = 0;
			String roomName = null;
			do{
				roomId = c.getInt(c.getColumnIndexOrThrow(GEN_ROOM_ID));
				roomName = c.getString(c.getColumnIndexOrThrow(GEN_ROOM_NAME));
				floorId = c.getInt(c.getColumnIndexOrThrow(GEN_FLOOR_ID));
				list.add(new CsstSHRoomBean(roomId, roomName, floorId));
			}while(c.moveToNext());
		}
		return list;
	}
	
	/**
	 * 根据楼层查询房间id
	 * @param db
	 * @param floor
	 * @return
	 */
	public final List<Integer> queryRoomByFloor(SQLiteDatabase db, int floor){
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, new String[]{GEN_ROOM_ID}, GEN_FLOOR_ID + "=" + floor, null, null, null, null);
		List<Integer> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<Integer>();
			int roomId = 0;
			do{
				roomId = c.getInt(c.getColumnIndexOrThrow(GEN_ROOM_ID));
				list.add(Integer.valueOf(roomId));
			}while(c.moveToNext());
		}
		return list;
	}

	@Override
	public CsstSHRoomBean query(SQLiteDatabase db, int id) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, (GEN_ROOM_ID +"="+id), null, null, null, null);
		if (c != null && c.moveToFirst()){
			int roomId = 0;
			int floorId = 0;
			String roomName = null;
			roomId = c.getInt(c.getColumnIndexOrThrow(GEN_ROOM_ID));
			roomName = c.getString(c.getColumnIndexOrThrow(GEN_ROOM_NAME));
			floorId = c.getInt(c.getColumnIndexOrThrow(GEN_FLOOR_ID));
			return new CsstSHRoomBean(roomId, roomName, floorId);
		}
		return null;
	}

	@Override
	public int countRecord(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		String[] columns = {"count(*)"};
		Cursor c = db.query(table, columns, null, null, null, null, null);
		if (c != null && c.moveToFirst()){
			return c.getInt(0);
		}
		return 0;
	}
	
	
	
	
	
	
	/**
	 * 查询房间对应的设备列表
	 * @param db
	 * @param id
	 * @return
	 */
	public final List<CsstSHDeviceBean> getRoomDevices(SQLiteDatabase db, int id){
		//查询楼层对应的房间
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM ");
		sb.append(CsstSHDeviceTable.GEN_TABLE_NAME);
		sb.append(" WHERE " + CsstSHDeviceTable.GEN_ROOM_ID + "=" + id);
		Cursor c = db.rawQuery(sb.toString(), null);
		List<CsstSHDeviceBean> deviceBeans = null;
		if (c != null && c.moveToFirst()){
			deviceBeans = new ArrayList<CsstSHDeviceBean>();
			int deviceId = 0;
			int roomId = 0;
			String deviceName ;
			boolean persert = false;
			String deviceIconPath; 
			int rCTypeId; 
			String deviceSSID;
			boolean rCCustom = false;
			int isSearched = 0;
			do{
				deviceId = c.getInt(c.getColumnIndexOrThrow(CsstSHDeviceTable.GEN_DEVICE_ID));
				deviceName = c.getString(c.getColumnIndexOrThrow(CsstSHDeviceTable.GEN_DEVICE_NAME));
				persert = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(CsstSHDeviceTable.GEN_DEVICE_ICONPERSERT)));
				roomId = c.getInt(c.getColumnIndexOrThrow(CsstSHDeviceTable.GEN_ROOM_ID));
				deviceIconPath = c.getString(c.getColumnIndexOrThrow(CsstSHDeviceTable.GEN_DEVICE_ICONPATH));
				rCTypeId = c.getInt(c.getColumnIndexOrThrow(CsstSHDeviceTable.GEN_RCT_ID));
				deviceSSID = c.getString(c.getColumnIndexOrThrow(CsstSHDeviceTable.GEN_DEVICE_PHYSICS_ID));
				rCCustom = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(CsstSHDeviceTable.GEN_RC_CUSTOM)));
				isSearched = c.getInt(c.getColumnIndexOrThrow(CsstSHDeviceTable.GEN_DEVICE_ISSEARCHED));
				deviceBeans.add(new CsstSHDeviceBean(deviceId, deviceName, persert, deviceIconPath, rCTypeId, deviceSSID, roomId, rCCustom, isSearched));
			}while(c.moveToNext());
		}
		return deviceBeans;
	}
}
