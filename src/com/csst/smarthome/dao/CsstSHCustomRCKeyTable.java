package com.csst.smarthome.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csst.smarthome.bean.CsstSHCustomRCKeyBean;
import com.csst.smarthome.bean.CsstSHSwitchBean;

/**
 * 动作
 * @author liuyang
 */
public class CsstSHCustomRCKeyTable implements ICsstSHDaoManager<CsstSHCustomRCKeyBean> {
	public static final String TAG = "CsstSHACTIONTable";
	/** 表名 */
	public static final String GEN_TABLE_NAME = "sh_customrckey";
	/** 设备id */
	public static final String GEN_CUSTOMRCKEY_ID = "sh_customrckey_id";
	/** 设备名字 */
	public static final String GEN_CUSTOMRCKEY_NAME = "sh_customrckey_name";
	/** 图片封面是否内置 */
	public static final String GEN_CUSTOMRCKEY_DRAWBLEID = "sh_customrckey_drawbleid";
	/** 设备封面 */
	public static final String GEN_CUSTOMRCKEY_CODE = "sh_customrckey_keycode";
	/** 设备状态 */
	public static final String GEN_CUSTOMRCKEY_DEVICEID = CsstSHDeviceTable.GEN_DEVICE_ID;
	
	/** 建表SQL语句 */
	public static String GEN_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + GEN_TABLE_NAME
			+ "(" + GEN_CUSTOMRCKEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ GEN_CUSTOMRCKEY_NAME + " INTEGER,"
			+ GEN_CUSTOMRCKEY_DRAWBLEID + " INTEGER,"
			+ GEN_CUSTOMRCKEY_CODE + " TEXT,"
			+ GEN_CUSTOMRCKEY_DEVICEID + " INTEGER "
			+ ")";
	/** 删除表 */
	public static String GEN_DROP_TABLE = "DROP TABLE IF EXISTS " + CsstSHCustomRCKeyTable.GEN_TABLE_NAME;
    private static CsstSHCustomRCKeyTable mInstance = null;
	
	private CsstSHCustomRCKeyTable(){
		
	}
	
	public static final CsstSHCustomRCKeyTable getInstance(){
		if (null == mInstance){
			mInstance = new CsstSHCustomRCKeyTable();
		}
		return mInstance;
	}
	
	@Override
	public long insert(SQLiteDatabase db, CsstSHCustomRCKeyBean arg0)
			throws RuntimeException {
		System.out.println(" CsstSHActionTable Action insert IN");
		String table = GEN_TABLE_NAME;
		ContentValues cv = new ContentValues();
		cv.put(GEN_CUSTOMRCKEY_NAME, arg0.getmCustommCustomRCKeyName());
		cv.put(GEN_CUSTOMRCKEY_DRAWBLEID,arg0.getmCustomRCKeyDrawbleId());
		cv.put(GEN_CUSTOMRCKEY_CODE, arg0.getmCustomRCKeyCode());
		cv.put(GEN_CUSTOMRCKEY_DEVICEID, arg0.getmCustomRCKeyDeviceId());
		return db.insert(table, null, cv);
	}
	
	@Override
	public long update(SQLiteDatabase db, CsstSHCustomRCKeyBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_CUSTOMRCKEY_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getmCustomRCKeyId())};
		ContentValues cv = new ContentValues();
		cv.put(GEN_CUSTOMRCKEY_NAME, arg0.getmCustommCustomRCKeyName());
		cv.put(GEN_CUSTOMRCKEY_DRAWBLEID,arg0.getmCustomRCKeyDrawbleId());
		cv.put(GEN_CUSTOMRCKEY_CODE, arg0.getmCustomRCKeyCode());
		cv.put(GEN_CUSTOMRCKEY_DEVICEID, arg0.getmCustomRCKeyDeviceId());
		return db.update(table, cv, whereClause, whereArgs);
	}
	/**
	 * 删除一条数据
	 */
	@Override
	public long delete(SQLiteDatabase db, CsstSHCustomRCKeyBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_CUSTOMRCKEY_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getmCustomRCKeyId())};
		return db.delete(table, whereClause, whereArgs);
	}
	
	/**
	 * 通过情景模式删除该情景模式下的全部动作
	 * @param db
	 * @param modelid
	 * @return
	 * @throws RuntimeException
	 */
	public long  deleteByModel(SQLiteDatabase db, int modelid) throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_CUSTOMRCKEY_DEVICEID + "=?";
		String[] whereArgs = {Integer.toString(modelid)};
		return db.delete(table, whereClause, whereArgs);
	}
	
	/**
	 * 情景模式内存在相同名字的动作
	 *  @param db
	 * @param columnsName
	 * @param modelid
	 * @return
	 */
	public final boolean roomExisSameACTION(SQLiteDatabase db, String columnsName, int deviceid){
		String table = GEN_TABLE_NAME;
		String selection = GEN_CUSTOMRCKEY_NAME + "='" + columnsName + "' AND " + GEN_CUSTOMRCKEY_DEVICEID + "=" + deviceid;
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
	 * 通过房间查询设备
	 * @param db
	 * @param roomid
	 * @return
	 */
	public final List<CsstSHCustomRCKeyBean> queryByModel(SQLiteDatabase db, int deviceid){
		String table = GEN_TABLE_NAME;
		String selection = GEN_CUSTOMRCKEY_DEVICEID + "='" + deviceid + "'";
		Cursor c = db.query(table, null, selection, null, null, null, null);
		List<CsstSHCustomRCKeyBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHCustomRCKeyBean>();
			int customRcKeyId; 
			String customRcKeyName;
			int customRcKeyDrawbleID, deviceId;
			String customRcKeyCode;
			do{
				customRcKeyId = c.getInt(c.getColumnIndexOrThrow(GEN_CUSTOMRCKEY_ID));
				customRcKeyName = c.getString(c.getColumnIndexOrThrow(GEN_CUSTOMRCKEY_NAME));
				customRcKeyDrawbleID = c.getInt(c.getColumnIndexOrThrow(GEN_CUSTOMRCKEY_DRAWBLEID));
				customRcKeyCode = c.getString(c.getColumnIndexOrThrow(GEN_CUSTOMRCKEY_CODE));
				deviceId = c.getInt(c.getColumnIndexOrThrow(GEN_CUSTOMRCKEY_DEVICEID));
				list.add(new CsstSHCustomRCKeyBean(customRcKeyId, customRcKeyName, customRcKeyDrawbleID, customRcKeyCode, deviceId));
			}while(c.moveToNext());
		}
		c.close();
		return list;
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

	@Override
	public List<CsstSHCustomRCKeyBean> query(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);
		List<CsstSHCustomRCKeyBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHCustomRCKeyBean>();
			int customRcKeyId; 
			String customRcKeyName;
			int customRcKeyDrawbleID, deviceId;
			String customRcKeyCode;
			do{
				customRcKeyId = c.getInt(c.getColumnIndexOrThrow(GEN_CUSTOMRCKEY_ID));
				customRcKeyName = c.getString(c.getColumnIndexOrThrow(GEN_CUSTOMRCKEY_NAME));
				customRcKeyDrawbleID = c.getInt(c.getColumnIndexOrThrow(GEN_CUSTOMRCKEY_DRAWBLEID));
				customRcKeyCode = c.getString(c.getColumnIndexOrThrow(GEN_CUSTOMRCKEY_CODE));
				deviceId = c.getInt(c.getColumnIndexOrThrow(GEN_CUSTOMRCKEY_DEVICEID));
				list.add(new CsstSHCustomRCKeyBean(customRcKeyId, customRcKeyName, customRcKeyDrawbleID, customRcKeyCode, deviceId));
			}while(c.moveToNext());
		}
		c.close();
		return list;
		// TODO Auto-generated method stub
	}

	@Override
	public CsstSHCustomRCKeyBean query(SQLiteDatabase db, int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
