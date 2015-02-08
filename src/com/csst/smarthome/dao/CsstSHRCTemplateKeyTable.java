package com.csst.smarthome.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csst.smarthome.bean.CsstSHRCKeyBean;

/**
 * 遥控器按键表
 * @author liuyang
 */
public final class CsstSHRCTemplateKeyTable implements ICsstSHDaoManager<CsstSHRCKeyBean>{
	
	public static final String TAG = "CsstSHRCTemplateKeyTable";
	/** 表名 */
	public static final String GEN_TABLE_NAME = "sh_rckey";
	/** 遥控器按键id */
	public static final String GEN_RCKEY_ID = "sh_rckey_id";
	/** 按键所属的遥控器类型id */
	public static final String GEN_RCT_ID = CsstSHRemoteControlTypeTable.GEN_RCT_ID;
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
			+ "(" + GEN_RCKEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ GEN_RCT_ID + " INTEGER," 
			+ GEN_RCKEY_NAME + " TEXT," 
			+ GEN_RCKEY_SIZE + " INTEGER," 
			+ GEN_RCKEY_X + " INTEGER," 
			+ GEN_RCKEY_Y + " INTEGER," 
			+ GEN_RCKEY_W + " INTEGER," 
			+ GEN_RCKEY_H + " INTEGER," 
			+ GEN_RCKEY_ICON + " INTEGER," 
			+ GEN_RCT_PAGE + " INTEGER,"
			+ GEN_IDENTIFY_CODE + " INTEGER"
			+ ")";
	
	/** 删除表 */
	public static String GEN_DROP_TABLE = "DROP TABLE IF EXISTS " + CsstSHDeviceTable.GEN_TABLE_NAME;
	
	private static CsstSHRCTemplateKeyTable mInstance = null;
	
	private CsstSHRCTemplateKeyTable(){
		
	}
	
	public static final CsstSHRCTemplateKeyTable getInstance(){
		if (null == mInstance){
			mInstance = new CsstSHRCTemplateKeyTable();
		}
		return mInstance;
	}
	
	@Override
	public long insert(SQLiteDatabase db, CsstSHRCKeyBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		ContentValues cv = new ContentValues();
		cv.put(GEN_RCT_ID, arg0.getRCTypeId());
		cv.put(GEN_RCKEY_NAME, arg0.getRCKeyName());
		cv.put(GEN_RCKEY_SIZE, arg0.getRCKeySize());
		cv.put(GEN_RCKEY_X, arg0.getRCKeyX());
		cv.put(GEN_RCKEY_Y, arg0.getRCKeyY());
		cv.put(GEN_RCKEY_W, arg0.getRCKeyW());
		cv.put(GEN_RCKEY_H, arg0.getRCKeyH());
		cv.put(GEN_RCKEY_ICON, arg0.getRCKeyIcon());
		cv.put(GEN_RCT_PAGE, arg0.getRCKeyPage());
		cv.put(GEN_IDENTIFY_CODE, arg0.getRCKeyIdentify());
		return db.insert(table, null, cv);
	}
	
	@Override
	public long update(SQLiteDatabase db, CsstSHRCKeyBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_RCKEY_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getRCKeyId())};
		ContentValues cv = new ContentValues();
		cv.put(GEN_RCT_ID, arg0.getRCTypeId());
		cv.put(GEN_RCKEY_NAME, arg0.getRCKeyName());
		cv.put(GEN_RCKEY_SIZE, arg0.getRCKeySize());
		cv.put(GEN_RCKEY_X, arg0.getRCKeyX());
		cv.put(GEN_RCKEY_Y, arg0.getRCKeyY());
		cv.put(GEN_RCKEY_W, arg0.getRCKeyW());
		cv.put(GEN_RCKEY_H, arg0.getRCKeyH());
		cv.put(GEN_RCKEY_ICON, arg0.getRCKeyIcon());
		cv.put(GEN_RCT_PAGE, arg0.getRCKeyPage());
		cv.put(GEN_IDENTIFY_CODE, arg0.getRCKeyIdentify());
		return db.update(table, cv, whereClause, whereArgs);
	}
	
	@Override
	public long delete(SQLiteDatabase db, CsstSHRCKeyBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_RCKEY_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getRCKeyId())};
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
	 * 通过遥控器模板查询该模板下所有的遥控器按键
	 * @param db
	 * @param rckid
	 * @return
	 */
	public final List<CsstSHRCKeyBean> queryByRCType(SQLiteDatabase db, int rckid) {
		String table = GEN_TABLE_NAME;
		String selection = GEN_RCT_ID + " = " + rckid;
		Cursor c = db.query(table, null, selection, null, null, null, null);
		List<CsstSHRCKeyBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHRCKeyBean>();
			String rCKeyName;
			int rCKeyId, rCTypeId, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify;
			do{
				rCKeyId = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_ID));
				rCTypeId = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_ID));
				rCKeyName = c.getString(c.getColumnIndexOrThrow(GEN_RCKEY_NAME));
				rCKeySize = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_SIZE));
				rCKeyX = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_X));
				rCKeyY = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_Y));
				rCKeyW = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_W));
				rCKeyH = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_H));
				rCKeyIcon = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_ICON));
				rCKeyPage = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_PAGE));
				rCKeyIdentify = c.getInt(c.getColumnIndexOrThrow(GEN_IDENTIFY_CODE));
				list.add(new CsstSHRCKeyBean(rCKeyId, rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
			}while(c.moveToNext());
		}
		return list;
	}
	
	@Override
	public List<CsstSHRCKeyBean> query(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);
		List<CsstSHRCKeyBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHRCKeyBean>();
			String rCKeyName;
			int rCKeyId, rCTypeId, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify;
			do{
				rCKeyId = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_ID));
				rCTypeId = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_ID));
				rCKeyName = c.getString(c.getColumnIndexOrThrow(GEN_RCKEY_NAME));
				rCKeySize = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_SIZE));
				rCKeyX = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_X));
				rCKeyY = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_Y));
				rCKeyW = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_W));
				rCKeyH = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_H));
				rCKeyIcon = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_ICON));
				rCKeyPage = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_PAGE));
				rCKeyIdentify = c.getInt(c.getColumnIndexOrThrow(GEN_IDENTIFY_CODE));
				list.add(new CsstSHRCKeyBean(rCKeyId, rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
			}while(c.moveToNext());
		}
		return list;
	}
	
	@Override
	public CsstSHRCKeyBean query(SQLiteDatabase db, int id) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);
		if (c != null && c.moveToFirst()){
			String rCKeyName;
			int rCKeyId, rCTypeId, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify;
			rCKeyId = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_ID));
			rCTypeId = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_ID));
			rCKeyName = c.getString(c.getColumnIndexOrThrow(GEN_RCKEY_NAME));
			rCKeySize = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_SIZE));
			rCKeyX = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_X));
			rCKeyY = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_Y));
			rCKeyW = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_W));
			rCKeyH = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_H));
			rCKeyIcon = c.getInt(c.getColumnIndexOrThrow(GEN_RCKEY_ICON));
			rCKeyPage = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_PAGE));
			rCKeyIdentify = c.getInt(c.getColumnIndexOrThrow(GEN_IDENTIFY_CODE));
			return new CsstSHRCKeyBean(rCKeyId, rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify);
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
