package com.csst.smarthome.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csst.smarthome.bean.CsstSHDeviceBean;

/**
 * 设备
 * @author liuyang
 */
public class CsstSHDeviceTable implements ICsstSHDaoManager<CsstSHDeviceBean> {
	public static final String TAG = "CsstSHDeviceTable";
	/** 表名 */
	public static final String GEN_TABLE_NAME = "sh_device";
	/** 设备id */
	public static final String GEN_DEVICE_ID = "sh_device_id";
	/** 设备名字 */
	public static final String GEN_DEVICE_NAME = "sh_device_name";
	/** 图片封面是否内置 */
	public static final String GEN_DEVICE_ICONPERSERT = "sh_device_icon_persert";
	/** 设备封面 */
	public static final String GEN_DEVICE_ICONPATH = "sh_device_iconPath";
	/** 设备状态 */
	public static final String GEN_DEVICE_ISOPEN = "sh_device_isopen";
	/** 遥控器类型id */
	public static final String GEN_RCT_ID = CsstSHRemoteControlTypeTable.GEN_RCT_ID;
	/** 设备物理地址id*/
	public static final String GEN_DEVICE_PHYSICS_ID = "sh_device_physics_id";
	/** 设备所在房间 */
	public static final String GEN_ROOM_ID = CsstSHRoomTable.GEN_ROOM_ID;
	/** 设备遥控器是否是自定义遥控器 */
	public static final String GEN_RC_CUSTOM = "sh_rct_custom";
	/** 设备是否是搜索的设备 */
	public static final String GEN_DEVICE_ISSEARCHED = "sh_device_isSearched";
	
	/** 建表SQL语句 */
	public static String GEN_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + GEN_TABLE_NAME
			+ "(" + GEN_DEVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ GEN_DEVICE_NAME + " TEXT,"
			+ GEN_DEVICE_ICONPERSERT + " TEXT,"
			+ GEN_DEVICE_ICONPATH + " TEXT,"
			+ GEN_RCT_ID + " INTEGER,"
			+ GEN_DEVICE_PHYSICS_ID + " TEXT,"
			+ GEN_ROOM_ID + " INTEGER NOT NULL,"
			+ GEN_RC_CUSTOM	+ " TEXT," 
			+ GEN_DEVICE_ISSEARCHED + " INTEGER"
			+ ")";
	/** 删除表 */
	public static String GEN_DROP_TABLE = "DROP TABLE IF EXISTS " + CsstSHDeviceTable.GEN_TABLE_NAME;
    private static CsstSHDeviceTable mInstance = null;
	
	private CsstSHDeviceTable(){
		
	}
	
	public static final CsstSHDeviceTable getInstance(){
		if (null == mInstance){
			mInstance = new CsstSHDeviceTable();
		}
		return mInstance;
	}
	
	@Override
	public long insert(SQLiteDatabase db, CsstSHDeviceBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		ContentValues cv = new ContentValues();
		cv.put(GEN_DEVICE_NAME, arg0.getDeviceName());
		cv.put(GEN_DEVICE_ICONPERSERT, Boolean.valueOf(arg0.isDeviceIconPersert()).toString());
		cv.put(GEN_DEVICE_ICONPATH, arg0.getDeviceIconPath());
		cv.put(GEN_RCT_ID, arg0.getRCTypeId());
		cv.put(GEN_DEVICE_PHYSICS_ID, arg0.getDeviceSSID());
		cv.put(GEN_ROOM_ID, arg0.getRoomId());
		cv.put(GEN_RC_CUSTOM, Boolean.valueOf(arg0.isRCCustom()));
		cv.put(GEN_DEVICE_ISSEARCHED, arg0.isSearched());
		return db.insert(table, null, cv);
	}
	
	@Override
	public long update(SQLiteDatabase db, CsstSHDeviceBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_DEVICE_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getDeviceId())};
		ContentValues cv = new ContentValues();
		cv.put(GEN_DEVICE_NAME, arg0.getDeviceName());
		cv.put(GEN_DEVICE_ICONPERSERT, Boolean.valueOf(arg0.isDeviceIconPersert()).toString());
		cv.put(GEN_DEVICE_ICONPATH, arg0.getDeviceIconPath());
		cv.put(GEN_RCT_ID, arg0.getRCTypeId());
		cv.put(GEN_DEVICE_PHYSICS_ID, arg0.getDeviceSSID());
		cv.put(GEN_ROOM_ID, arg0.getRoomId());
		cv.put(GEN_RC_CUSTOM, Boolean.valueOf(arg0.isRCCustom()));
		cv.put(GEN_DEVICE_ISSEARCHED, arg0.isSearched());
		return db.update(table, cv, whereClause, whereArgs);
	}
	
	@Override
	public long delete(SQLiteDatabase db, CsstSHDeviceBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_DEVICE_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getDeviceId())};
		return db.delete(table, whereClause, whereArgs);
	}
	
	/**
	 * 通过房间删除设备
	 * @param db
	 * @param roomid
	 * @return
	 * @throws RuntimeException
	 */
	public final void deleteByRoom(SQLiteDatabase db, int roomid) throws RuntimeException {
		//查询该房间下所有的设备
		List<Integer> deviceids = queryDeviceIdsByRoom(db, roomid);
		//删除设备对应的遥控器
		if (null != deviceids && !deviceids.isEmpty()){
			StringBuffer delDRCSql = new StringBuffer();
			delDRCSql.append("DELETE FROM " + CsstSHDeviceRCKeyTable.GEN_TABLE_NAME);
			delDRCSql.append(" WHERE " + CsstSHDeviceRCKeyTable.GEN_DEVICE_ID);
			delDRCSql.append(" IN(");
			for (int i = 0; i < deviceids.size(); i++){
				if (i != (deviceids.size() - 1)){
					delDRCSql.append(deviceids.get(i) + ", ");
				}else{
					delDRCSql.append(deviceids.get(i));
				}
			}
			delDRCSql.append(")");
			db.execSQL(delDRCSql.toString());
		}
		//删除该房间下对应的设备
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_ROOM_ID + "=" + roomid;
		db.delete(table, whereClause, null);
	}
	
	/**
	 * 批量删除房间
	 * @param db
	 * @param rooms
	 * @throws RuntimeException
	 */
	public final void deleteByRooms(SQLiteDatabase db, List<Integer> rooms) throws RuntimeException {
		if (rooms != null && !rooms.isEmpty()){
			for (int i = 0; i < rooms.size(); i++){
				deleteByRoom(db, rooms.get(i));
			}
		}
	}
	
	/**
	 * 删除设备及设备对应的遥控器
	 * @param db
	 * @param roomid
	 */
	public final void deleteByDevice(SQLiteDatabase db, int deviceid){
		//删除设备对应的遥控器
		CsstSHDeviceRCKeyTable.getInstance().deleteByDeviceId(db, deviceid);
		//删除设备
		String whereClause = GEN_DEVICE_ID + "=" + deviceid;
		db.delete(GEN_TABLE_NAME, whereClause, null);
	}
	
	/**
	 * 房间存在相同的设备
	 * @param db
	 * @param columnsName
	 * @param roomid
	 * @return
	 */
	public final boolean roomExisSameDevice(SQLiteDatabase db, String columnsName, int roomid){
		String table = GEN_TABLE_NAME;
		String selection = GEN_DEVICE_NAME + "='" + columnsName + "' AND " + GEN_ROOM_ID + "=" + roomid;
		Cursor c = db.query(table, null, selection, null, null, null, null);
		return (c != null && c.moveToFirst() && c.getCount() > 0);
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
	 * 通过房间查询房间下所有的设备id
	 * @param db
	 * @param roomid
	 * @return
	 */
	public final List<Integer> queryDeviceIdsByRoom(SQLiteDatabase db, int roomid){
		String table = GEN_TABLE_NAME;
		String[] columns = {GEN_DEVICE_ID};
		String selection = GEN_ROOM_ID + "=" + roomid;
		Cursor c = db.query(table, columns, selection, null, null, null, null);
		List<Integer> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<Integer>();
			int deviceId; 
			do{
				deviceId = c.getInt(0);
				list.add(Integer.valueOf(deviceId));
			}while(c.moveToNext());
		}
		return list;
	}
	
	/**
	 * 通过房间查询设备
	 * @param db
	 * @param roomid
	 * @return
	 */
	public final List<CsstSHDeviceBean> queryByRoom(SQLiteDatabase db, int roomid){
		String table = GEN_TABLE_NAME;
		String selection = GEN_ROOM_ID + "='" + roomid + "'";
		Cursor c = db.query(table, null, selection, null, null, null, null);
		List<CsstSHDeviceBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHDeviceBean>();
			int deviceId; 
			String deviceName;
			boolean persert = false;
			String deviceIconPath; 
			int rCTypeId; 
			String deviceSSID;
			int roomId;
			boolean rCCustom = false;
			int isSearched = 0;
			do{
				deviceId = c.getInt(c.getColumnIndexOrThrow(GEN_DEVICE_ID));
				deviceName = c.getString(c.getColumnIndexOrThrow(GEN_DEVICE_NAME));
				persert = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(GEN_DEVICE_ICONPERSERT)));
				deviceIconPath = c.getString(c.getColumnIndexOrThrow(GEN_DEVICE_ICONPATH));
				rCTypeId = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_ID));
				deviceSSID = c.getString(c.getColumnIndexOrThrow(GEN_DEVICE_PHYSICS_ID));
				roomId = c.getInt(c.getColumnIndexOrThrow(GEN_ROOM_ID));
				rCCustom = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(GEN_RC_CUSTOM)));
				isSearched = c.getInt(c.getColumnIndexOrThrow(GEN_DEVICE_ISSEARCHED));
				list.add(new CsstSHDeviceBean(deviceId, deviceName, persert, deviceIconPath, rCTypeId, deviceSSID, roomId, rCCustom, isSearched));
			}while(c.moveToNext());
		}
		return list;
	}
	
	@Override
	public List<CsstSHDeviceBean> query(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);
		List<CsstSHDeviceBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHDeviceBean>();
			int deviceId; 
			String deviceName;
			boolean persert = false;
			String deviceIconPath; 
			int rCTypeId; 
			String deviceSSID;
			int roomId;
			boolean rCCustom = false;
			int isSearched = 0;
			do{
				deviceId = c.getInt(c.getColumnIndexOrThrow(GEN_DEVICE_ID));
				deviceName = c.getString(c.getColumnIndexOrThrow(GEN_DEVICE_NAME));
				persert = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(GEN_DEVICE_ICONPERSERT)));
				deviceIconPath = c.getString(c.getColumnIndexOrThrow(GEN_DEVICE_ICONPATH));
				rCTypeId = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_ID));
				deviceSSID = c.getString(c.getColumnIndexOrThrow(GEN_DEVICE_PHYSICS_ID));
				roomId = c.getInt(c.getColumnIndexOrThrow(GEN_ROOM_ID));
				rCCustom = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(GEN_RC_CUSTOM)));
				isSearched = c.getInt(c.getColumnIndexOrThrow(GEN_DEVICE_ISSEARCHED));
				list.add(new CsstSHDeviceBean(deviceId, deviceName, persert, deviceIconPath, rCTypeId, deviceSSID, roomId, rCCustom, isSearched));
			}while(c.moveToNext());
		}
		return list;
	}
	
	@Override
	public CsstSHDeviceBean query(SQLiteDatabase db, int id) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);
		if (c != null && c.moveToFirst()){
			int deviceId; 
			String deviceName;
			boolean persert = false;
			String deviceIconPath; 
			int rCTypeId; 
			String deviceSSID;
			int roomId;
			boolean rCCustom = false;
			int isSearched = 0;
			deviceId = c.getInt(c.getColumnIndexOrThrow(GEN_DEVICE_ID));
			deviceName = c.getString(c.getColumnIndexOrThrow(GEN_DEVICE_NAME));
			persert = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(GEN_DEVICE_ICONPERSERT)));
			deviceIconPath = c.getString(c.getColumnIndexOrThrow(GEN_DEVICE_ICONPATH));
			rCTypeId = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_ID));
			deviceSSID = c.getString(c.getColumnIndexOrThrow(GEN_DEVICE_PHYSICS_ID));
			roomId = c.getInt(c.getColumnIndexOrThrow(GEN_ROOM_ID));
			rCCustom = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(GEN_RC_CUSTOM)));
			isSearched = c.getInt(c.getColumnIndexOrThrow(GEN_DEVICE_ISSEARCHED));
			return new CsstSHDeviceBean(deviceId, deviceName, persert, deviceIconPath, rCTypeId, deviceSSID, roomId, rCCustom, isSearched);
		}
		return null;
	}
	
	@Override
	public int countRecord(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		String[] columns = {"COUNT(*)"};
		Cursor c = db.query(table, columns, null, null, null, null, null);
		if (c != null && c.moveToFirst()){
			return c.getInt(0);
		}
		return 0;
	}

}
