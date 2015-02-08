package com.csst.smarthome.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csst.smarthome.bean.CsstSHDRCBean;

/**
 * 设备对应的遥控器表
 * @author liuyang
 */
public final class CsstSHDeviceRCKeyTable implements ICsstSHDaoManager<CsstSHDRCBean> {
	
	public static final String TAG = "CsstSHDeviceRemoteControlTable";
	
	/** 表名 */
	public static final String GEN_TABLE_NAME = "sh_drc";
	/** id */
	public static final String GEN_DRC_ID = "sh_drc_id";
	/** 设备id*/
	public static final String GEN_DEVICE_ID = CsstSHDeviceTable.GEN_DEVICE_ID;
	/** 遥控器按键对应的命令 */
	public static final String GEN_DRC_CODE = "sh_drc_code";
	/** 遥控器按键名称*/
	public static final String GEN_RCKEY_NAME = "sh_rckey_name";
	/** 遥控器按键对应的大小 */
	public static final String GEN_RCKEY_SIZE = "sh_rckey_size";
	/** 遥控器按键x坐标 */
	public static final String GEN_RCKEY_X = "sh_rckey_x";
	/** 遥控器按键y坐标 */
	public static final String GEN_RCKEY_Y = "sh_rckey_y";
	/** 遥控器按键w宽度 */
	public static final String GEN_RCKEY_W = "sh_rckey_w";
	/** 遥控器按键h高度 */
	public static final String GEN_RCKEY_H = "sh_rckey_h";
	/** 遥控器按键h高度 */
	public static final String GEN_RCKEY_ICON = "sh_rckey_icon";
	/** 遥控器按键所在的页码 */
	public static final String GEN_RCT_PAGE = "sh_rckey_page";
	/** 遥控器按键标识符 */
	public static final String GEN_IDENTIFY_CODE = "sh_identify_code";
	
	/** 建表SQL语句 */
	public static String GEN_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + GEN_TABLE_NAME
			+ "(" + GEN_DRC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ GEN_DEVICE_ID + " TEXT," //设备id
			+ GEN_DRC_CODE + " TEXT," //按键码
			+ GEN_RCKEY_NAME + " TEXT," //按键名称
			+ GEN_RCKEY_SIZE + " INTEGER," //按键大小
			+ GEN_RCKEY_X + " INTEGER," //按键横坐标
			+ GEN_RCKEY_Y + " INTEGER," //按键纵坐标
			+ GEN_RCKEY_W + " INTEGER," //按键宽度
			+ GEN_RCKEY_H + " INTEGER," //按键高度
			+ GEN_RCKEY_ICON + " INTEGER," //按键图标文件id
			+ GEN_RCT_PAGE + " INTEGER,"//按键所在的页码
			+ GEN_IDENTIFY_CODE + " INTEGER"
			+ ")";
	/** 删除表 */
	public static String GEN_DROP_TABLE = "DROP TABLE IF EXISTS " + CsstSHDeviceTable.GEN_TABLE_NAME;
	
	private static CsstSHDeviceRCKeyTable mInstance = null;
	
	public static final CsstSHDeviceRCKeyTable getInstance(){
		if (null == mInstance){
			mInstance = new CsstSHDeviceRCKeyTable();
		}
		return mInstance;
	}
	
	@Override
	public long insert(SQLiteDatabase db, CsstSHDRCBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		ContentValues cv = new ContentValues();
		cv.put(GEN_DEVICE_ID, arg0.getDeviceId()); //设备id
		cv.put(GEN_DRC_CODE, arg0.getDRCCmdCode()); //按键码
		cv.put(GEN_RCKEY_NAME, arg0.getRCKeyName()); //按键名称
		cv.put(GEN_RCKEY_SIZE, arg0.getRCKeySize()); //按键大小
		cv.put(GEN_RCKEY_X, arg0.getRCKeyX()); //按键横坐标
		cv.put(GEN_RCKEY_Y, arg0.getRCKeyY()); //按键纵坐标
		cv.put(GEN_RCKEY_W, arg0.getRCKeyW()); //按键宽度
		cv.put(GEN_RCKEY_H, arg0.getRCKeyH()); //按键高度
		cv.put(GEN_RCKEY_ICON, arg0.getRCKeyIcon()); //按键图标文件id
		cv.put(GEN_RCT_PAGE, arg0.getRCKeyPage());//按键所在的页码
		cv.put(GEN_IDENTIFY_CODE, arg0.getRCKeyIdentify());
		return db.insert(table, null, cv);
	}
	
	@Override
	public long update(SQLiteDatabase db, CsstSHDRCBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_DRC_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getDRCId())};
		ContentValues cv = new ContentValues();
		cv.put(GEN_DEVICE_ID, arg0.getDeviceId()); //设备id
		cv.put(GEN_DRC_CODE, arg0.getDRCCmdCode()); //按键码
		cv.put(GEN_RCKEY_NAME, arg0.getRCKeyName()); //按键名称
		cv.put(GEN_RCKEY_SIZE, arg0.getRCKeySize()); //按键大小
		cv.put(GEN_RCKEY_X, arg0.getRCKeyX()); //按键横坐标
		cv.put(GEN_RCKEY_Y, arg0.getRCKeyY()); //按键纵坐标
		cv.put(GEN_RCKEY_W, arg0.getRCKeyW()); //按键宽度
		cv.put(GEN_RCKEY_H, arg0.getRCKeyH()); //按键高度
		cv.put(GEN_RCKEY_ICON, arg0.getRCKeyIcon()); //按键图标文件id
		cv.put(GEN_RCT_PAGE, arg0.getRCKeyPage());//按键所在的页码
		cv.put(GEN_IDENTIFY_CODE, arg0.getRCKeyIdentify());
		return db.update(table, cv, whereClause, whereArgs);
	}
	
	@Override
	public long delete(SQLiteDatabase db, CsstSHDRCBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_DRC_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getDRCId())};
		return db.delete(table, whereClause, whereArgs);
	}
	
	/**
	 * 删除设备对应的遥控器id
	 * @param db
	 * @param deviceid
	 * @return
	 */
	public final long deleteByDeviceId(SQLiteDatabase db, int deviceid){
		String whereClause = GEN_DEVICE_ID + "=" + deviceid;
		return db.delete(GEN_TABLE_NAME, whereClause, null);
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
	 * 通过设备查询遥控器按键
	 * @param db
	 * @param dId
	 * @return
	 */
	public List<CsstSHDRCBean> queryByDevice(SQLiteDatabase db, int dId) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, GEN_DEVICE_ID + " = " + dId, null, null, null, null);
		List<CsstSHDRCBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHDRCBean>();
			int dRCId;
			int deviceId;
			String dRCCmdCode;
			String rCKeyName; 
			int rCKeySize;
			int rCKeyX; 
			int rCKeyY;
			int rCKeyW; 
			int rCKeyH; 
			int rCKeyIcon; 
			int rCKeyPage;
			int rCKeyIdentify;
			do{
				dRCId = c.getInt(c.getColumnIndexOrThrow(GEN_DRC_ID));
				deviceId = c.getInt(c.getColumnIndexOrThrow(GEN_DEVICE_ID));
				dRCCmdCode = c.getString(c.getColumnIndexOrThrow(GEN_DRC_CODE));
				rCKeyName = c.getString(c.getColumnIndexOrThrow(GEN_RCKEY_NAME));
				rCKeySize = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_SIZE));
				rCKeyX = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_X));
				rCKeyY = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_Y));
				rCKeyW = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_W));
				rCKeyH = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_H));
				rCKeyIcon = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_ICON));
				rCKeyPage = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_PAGE));
				rCKeyIdentify = c.getInt(c.getColumnIndexOrThrow(GEN_IDENTIFY_CODE));
				list.add(new CsstSHDRCBean(dRCId, deviceId, dRCCmdCode, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
			}while(c.moveToNext());
		}
		return list;
	}
	
	@Override
	public List<CsstSHDRCBean> query(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);
		List<CsstSHDRCBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHDRCBean>();
			int dRCId;
			int deviceId;
			String dRCCmdCode;
			String rCKeyName; 
			int rCKeySize;
			int rCKeyX; 
			int rCKeyY;
			int rCKeyW; 
			int rCKeyH; 
			int rCKeyIcon; 
			int rCKeyPage;
			int rCKeyIdentify;
			do{
				dRCId = c.getInt(c.getColumnIndexOrThrow(GEN_DRC_ID));
				deviceId = c.getInt(c.getColumnIndexOrThrow(GEN_DEVICE_ID));
				dRCCmdCode = c.getString(c.getColumnIndexOrThrow(GEN_DRC_CODE));
				rCKeyName = c.getString(c.getColumnIndexOrThrow(GEN_RCKEY_NAME));
				rCKeySize = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_SIZE));
				rCKeyX = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_X));
				rCKeyY = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_Y));
				rCKeyW = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_W));
				rCKeyH = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_H));
				rCKeyIcon = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_ICON));
				rCKeyPage = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_PAGE));
				rCKeyIdentify = c.getInt(c.getColumnIndexOrThrow(GEN_IDENTIFY_CODE));
				list.add(new CsstSHDRCBean(dRCId, deviceId, dRCCmdCode, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
			}while(c.moveToNext());
		}
		return list;
	}
	
	@Override
	public CsstSHDRCBean query(SQLiteDatabase db, int id) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, GEN_DRC_ID + "=" + id, null, null, null, null);
		if (c != null && c.moveToFirst()){
			int dRCId;
			int deviceId;
			String dRCCmdCode;
			String rCKeyName; 
			int rCKeySize;
			int rCKeyX; 
			int rCKeyY;
			int rCKeyW; 
			int rCKeyH; 
			int rCKeyIcon; 
			int rCKeyPage;
			int rCKeyIdentify;
			dRCId = c.getInt(c.getColumnIndexOrThrow(GEN_DRC_ID));
			deviceId = c.getInt(c.getColumnIndexOrThrow(GEN_DEVICE_ID));
			dRCCmdCode = c.getString(c.getColumnIndexOrThrow(GEN_DRC_CODE));
			rCKeyName = c.getString(c.getColumnIndexOrThrow(GEN_RCKEY_NAME));
			rCKeySize = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_SIZE));
			rCKeyX = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_X));
			rCKeyY = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_Y));
			rCKeyW = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_W));
			rCKeyH = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_H));
			rCKeyIcon = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_ICON));
			rCKeyPage = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_PAGE));
			rCKeyIdentify = c.getInt(c.getColumnIndexOrThrow(GEN_IDENTIFY_CODE));
			return new CsstSHDRCBean(dRCId, deviceId, dRCCmdCode, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify);
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
