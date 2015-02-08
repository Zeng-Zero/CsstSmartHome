package com.csst.smarthome.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csst.smarthome.bean.CsstSHCameraBean;

/**
 * 视频监控表
 * @author liuyang
 */
public class CsstSHCameraTable implements ICsstSHDaoManager<CsstSHCameraBean> {

public static final String TAG = "CsstSHCameraTable";
	
	/** 表名 */
	public static final String GEN_TABLE_NAME = "sh_camera";
	/** Camera id */
	public static final String GEN_CAMERA_ID = "sh_camera_id";
	/** Camera 名字 */
	public static final String GEN_CAMERA_NAME = "sh_camera_name";
	/** Camera 连接用户名 */
	public static final String GEN_CAMERA_ACCOUNT = "sh_camera_account";
	/** Camera 连接密码*/
	public static final String GEN_CAMERA_PASSWORD = "sh_camera_password";
	/** Camera 设备UUID */
	public static final String GEN_CAMERA_UUID = "sh_camera_uuid";
	
	/** 建表SQL语句 */
	public static String GEN_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " 
			+ GEN_TABLE_NAME
			+ "(" + GEN_CAMERA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ GEN_CAMERA_NAME + " TEXT," 
			+ GEN_CAMERA_ACCOUNT + " TEXT," 
			+ GEN_CAMERA_PASSWORD + " TEXT," 
			+ GEN_CAMERA_UUID + " TEXT" 
			+ ")";
	/** 删除表 */
	public static String GEN_DROP_TABLE = "DROP TABLE IF EXISTS " + GEN_TABLE_NAME;
	
	private static CsstSHCameraTable mInstance = null;
	
	private CsstSHCameraTable(){
		
	}
	
	public static final CsstSHCameraTable getInstance(){
		if (null == mInstance){
			mInstance = new CsstSHCameraTable();
		}
		return mInstance;
	}
	
	@Override
	public long insert(SQLiteDatabase db, CsstSHCameraBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		ContentValues cv = new ContentValues();
		cv.put(GEN_CAMERA_NAME, arg0.getCameraName());
		cv.put(GEN_CAMERA_ACCOUNT, arg0.getCameraAccount());
		cv.put(GEN_CAMERA_PASSWORD, arg0.getCameraPassword());
		cv.put(GEN_CAMERA_UUID, arg0.getCameraUuid());
		return db.insert(table, null, cv);
	}

	@Override
	public long update(SQLiteDatabase db, CsstSHCameraBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_CAMERA_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getCameraId())};
		ContentValues cv = new ContentValues();
		cv.put(GEN_CAMERA_NAME, arg0.getCameraName());
		cv.put(GEN_CAMERA_ACCOUNT, arg0.getCameraAccount());
		cv.put(GEN_CAMERA_PASSWORD, arg0.getCameraPassword());
		cv.put(GEN_CAMERA_UUID, arg0.getCameraUuid());
		return db.update(table, cv, whereClause, whereArgs);
	}

	@Override
	public long delete(SQLiteDatabase db, CsstSHCameraBean arg0)
			throws RuntimeException {
		// 删除该场景
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_CAMERA_ID + "=?";
		String[] whereArgs = { Integer.toString(arg0.getCameraId()) };
		return db.delete(table, whereClause, whereArgs);
	}

	/**
	 * 房间存在相同的设备
	 * @param db
	 * @param columnsName
	 * @param roomid
	 * @return
	 */
	/*public final boolean roomExisSameDevice(SQLiteDatabase db, String columnsName, int roomid){
		String table = GEN_TABLE_NAME;
		String selection = GEN_DEVICE_NAME + "='" + columnsName + "' AND " + GEN_ROOM_ID + "=" + roomid;
		Cursor c = db.query(table, null, selection, null, null, null, null);
		return (c != null && c.moveToFirst() && c.getCount() > 0);
	}*/
	
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
	public List<CsstSHCameraBean> query(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, GEN_CAMERA_ID + " DESC");
		List<CsstSHCameraBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHCameraBean>();
			int id = 0;
			String name = null;
			String account = null;
			String password = null;
			String uuid = null;
			do{
				id = c.getInt(c.getColumnIndexOrThrow(GEN_CAMERA_ID));
				name = c.getString(c.getColumnIndexOrThrow(GEN_CAMERA_NAME));
				account = c.getString(c.getColumnIndexOrThrow(GEN_CAMERA_ACCOUNT));
				password = c.getString(c.getColumnIndexOrThrow(GEN_CAMERA_PASSWORD));
				uuid = c.getString(c.getColumnIndexOrThrow(GEN_CAMERA_UUID));
				list.add(new CsstSHCameraBean(id, name, account, password, uuid));
			}while(c.moveToNext());
		}
		return list;
	}

	@Override
	public CsstSHCameraBean query(SQLiteDatabase db, int id) {
		String table = GEN_TABLE_NAME;
		String selection = GEN_CAMERA_ID + "=?";
		String[] selectionArgs = {Integer.toString(id)};
		Cursor c = db.query(table, null, selection, selectionArgs, null, null, null);
		if (c != null && c.moveToFirst()){
			int cameraid = 0;
			String name = null;
			String account = null;
			String password = null;
			String uuid = null;
			cameraid = c.getInt(c.getColumnIndexOrThrow(GEN_CAMERA_ID));
			name = c.getString(c.getColumnIndexOrThrow(GEN_CAMERA_NAME));
			account = c.getString(c.getColumnIndexOrThrow(GEN_CAMERA_ACCOUNT));
			password = c.getString(c.getColumnIndexOrThrow(GEN_CAMERA_PASSWORD));
			uuid = c.getString(c.getColumnIndexOrThrow(GEN_CAMERA_UUID));
			return new CsstSHCameraBean(cameraid, name, account, password, uuid);
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

}
