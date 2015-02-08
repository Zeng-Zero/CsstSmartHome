package com.csst.smarthome.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csst.smarthome.bean.CsstSHFloolBean;
import com.csst.smarthome.bean.CsstSHRoomBean;

/**
 * 楼层
 * @author liuyang
 */
public class CsstSHFloorTable implements ICsstSHDaoManager<CsstSHFloolBean> {
	
	public static final String TAG = "CsstSHFloorTable";
	
	/** 表名 */
	public static final String GEN_TABLE_NAME = "sh_floor";
	/** 楼层id */
	public static final String GEN_FLOOR_ID = "sh_floor_id";
	/** 楼层名字 */
	public static final String GEN_FLOOR_NAME = "sh_floor_name";
	/** 建表SQL语句 */
	public static String GEN_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " 
			+ GEN_TABLE_NAME
			+ "(" + GEN_FLOOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ GEN_FLOOR_NAME + " TEXT" + ")";
	/** 删除表 */
	public static String GEN_DROP_TABLE = "DROP TABLE IF EXISTS " + GEN_TABLE_NAME;
	
	private static CsstSHFloorTable mInstance = null;
	
	private CsstSHFloorTable(){
		
	}
	
	public static final CsstSHFloorTable getInstance(){
		if (null == mInstance){
			mInstance = new CsstSHFloorTable();
		}
		return mInstance;
	}

	@Override
	public long insert(SQLiteDatabase db, CsstSHFloolBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		ContentValues cv = new ContentValues();
		cv.put(GEN_FLOOR_NAME, arg0.getFloorName());
		return db.insert(table, null, cv);
	}

	@Override
	public long update(SQLiteDatabase db, CsstSHFloolBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_FLOOR_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getFloorId())};
		ContentValues cv = new ContentValues();
		cv.put(GEN_FLOOR_NAME, arg0.getFloorName());
		return db.update(table, cv, whereClause, whereArgs);
	}

	@Override
	public long delete(SQLiteDatabase db, CsstSHFloolBean arg0)
			throws RuntimeException {
		//删除场景下的数据
		deleteDataOfFloor(db, arg0.getFloorId());
		//删除该场景
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_FLOOR_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getFloorId())};
		return db.delete(table, whereClause, whereArgs);
	}
	
	/**
	 * 删除场景下所有的数据
	 */
	private final void deleteDataOfFloor(SQLiteDatabase db, int floorId){
		//查询楼层对应的房间
		List<Integer> rooms = CsstSHRoomTable.getInstance().queryRoomByFloor(db, floorId);
		//查询房间下所有的设备
		if (null != rooms && !rooms.isEmpty()){
			//删除房间下的设备及设备遥控器
			CsstSHDeviceTable.getInstance().deleteByRooms(db, rooms);
		}
		//删除楼层对应的房间
		CsstSHRoomTable.getInstance().deleteByFloorId(db, floorId);
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

	@Override
	public List<CsstSHFloolBean> query(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);
		List<CsstSHFloolBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHFloolBean>();
			int floorId = 0;
			String floorName = null;
			do{
				floorId = c.getInt(c.getColumnIndexOrThrow(GEN_FLOOR_ID));
				floorName = c.getString(c.getColumnIndexOrThrow(GEN_FLOOR_NAME));
				list.add(new CsstSHFloolBean(floorId, floorName));
			}while(c.moveToNext());
		}
		return list;
	}

	@Override
	public CsstSHFloolBean query(SQLiteDatabase db, int id) {
		String table = GEN_TABLE_NAME;
		String selection = GEN_FLOOR_ID + "=?";
		String[] selectionArgs = {Integer.toString(id)};
		Cursor c = db.query(table, null, selection, selectionArgs, null, null, null);
		if (c != null && c.moveToFirst()){
			int floorId = 0;
			String floorName = null;
			floorId = c.getInt(c.getColumnIndexOrThrow(GEN_FLOOR_ID));
			floorName = c.getString(c.getColumnIndexOrThrow(GEN_FLOOR_NAME));
			return new CsstSHFloolBean(floorId, floorName, getFloorRooms(db, id));
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
	 * 查询楼层对应的房间列表
	 * @param db
	 * @param id
	 * @return
	 */
	public final List<CsstSHRoomBean> getFloorRooms(SQLiteDatabase db, int id){
		//查询楼层对应的房间
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM ");
		sb.append(CsstSHRoomTable.GEN_TABLE_NAME);
		sb.append(" WHERE " + CsstSHRoomTable.GEN_FLOOR_ID + "=" + id);
		Cursor c = db.rawQuery(sb.toString(), null);
		List<CsstSHRoomBean> rooms = null;
		if (c != null && c.moveToFirst()){
			rooms = new ArrayList<CsstSHRoomBean>();
			int roomId = 0;
			int floorId = 0;
			String roomName = null;
			do{
				roomId = c.getInt(c.getColumnIndexOrThrow(CsstSHRoomTable.GEN_ROOM_ID));
				roomName = c.getString(c.getColumnIndexOrThrow(CsstSHRoomTable.GEN_ROOM_NAME));
				floorId = c.getInt(c.getColumnIndexOrThrow(CsstSHRoomTable.GEN_FLOOR_ID));
				rooms.add(new CsstSHRoomBean(roomId, roomName, floorId));
			}while(c.moveToNext());
		}
		return rooms;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
