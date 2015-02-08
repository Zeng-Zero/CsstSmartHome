package com.csst.smarthome.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csst.smarthome.bean.CsstSHRCTypeBean;
import com.csst.smarthome.bean.CsstSHSwitchBean;

/**
 * 动作
 * @author liuyang
 */
public class CsstSHSwitchTable implements ICsstSHDaoManager<CsstSHSwitchBean> {
	public static final String TAG = "CsstSHSwitchTable";
	/** 表名 */
	public static final String GEN_TABLE_NAME = "sh_switch";
	/** 设备id */
	public static final String GEN_SWITCH_ID = "sh_switch_id";
	/** 设备名字 */
	public static final String GEN_SWITCH_NAME1 = "sh_switch_name1";
	/** 图片封面是否内置 */
	public static final String GEN_SWITCH_NAME2 = "sh_switch_name2";
	/** 设备封面 */
	public static final String GEN_SWITCH_NAME3 = "sh_switch_name3";
	/** 设备状态 */
	public static final String GEN_SWITCH_ONOFF = "sh_switch_onoff";
	/** 设备物理地址id*/
	public static final String GEN_SWITCH_DEVICEID = CsstSHDeviceTable.GEN_DEVICE_ID;
	
	/** 建表SQL语句 */
	public static String GEN_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + GEN_TABLE_NAME
			+ "(" + GEN_SWITCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ GEN_SWITCH_NAME1 + " TEXT,"
			+ GEN_SWITCH_NAME2 + " TEXT,"
			+ GEN_SWITCH_NAME3 + " TEXT,"
			+ GEN_SWITCH_ONOFF + " INTEGER,"
			+ GEN_SWITCH_DEVICEID + " INTEGER "
			+ ")";
	/** 删除表 */
	public static String GEN_DROP_TABLE = "DROP TABLE IF EXISTS " + CsstSHSwitchTable.GEN_TABLE_NAME;
    private static CsstSHSwitchTable mInstance = null;
	
	private CsstSHSwitchTable(){
		
	}
	
	public static final CsstSHSwitchTable getInstance(){
		if (null == mInstance){
			mInstance = new CsstSHSwitchTable();
		}
		return mInstance;
	}
	
	@Override
	public long insert(SQLiteDatabase db, CsstSHSwitchBean arg0)
			throws RuntimeException {
		System.out.println(" CsstSHActionTable Action insert IN");
		String table = GEN_TABLE_NAME;
		ContentValues cv = new ContentValues();
		cv.put(GEN_SWITCH_NAME1, arg0.getmSwitchName1());
		cv.put(GEN_SWITCH_NAME2,arg0.getmSwitchName2());
		cv.put(GEN_SWITCH_NAME3, arg0.getmSwitchName3());
		cv.put(GEN_SWITCH_DEVICEID, arg0.getmDeviceId());
		cv.put(GEN_SWITCH_ONOFF, arg0.getSwitchonoff());
		return db.insert(table, null, cv);
	}
	
	@Override
	public long update(SQLiteDatabase db, CsstSHSwitchBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_SWITCH_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getmSwitchId())};
		ContentValues cv = new ContentValues();
		cv.put(GEN_SWITCH_NAME1, arg0.getmSwitchName1());
		cv.put(GEN_SWITCH_NAME2,arg0.getmSwitchName2());
		cv.put(GEN_SWITCH_NAME3, arg0.getmSwitchName3());
		cv.put(GEN_SWITCH_DEVICEID, arg0.getmDeviceId());
		cv.put(GEN_SWITCH_ONOFF, arg0.getSwitchonoff());
		return db.update(table, cv, whereClause, whereArgs);
	}
	/**
	 * 删除一条数据
	 */
	@Override
	public long delete(SQLiteDatabase db, CsstSHSwitchBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_SWITCH_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getmSwitchId())};
		return db.delete(table, whereClause, whereArgs);
	}
	
	/**
	 * 通过器件删除器件下的switch 数据
	 * @param db
	 * @param modelid
	 * @return
	 * @throws RuntimeException
	 */
	public long  deleteByDeviceId(SQLiteDatabase db, int deviceid) throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_SWITCH_DEVICEID + "=?";
		String[] whereArgs = {Integer.toString(deviceid)};
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
	 * 通过房间查询设备
	 * @param db
	 * @param roomid
	 * @return
	 */
	public final CsstSHSwitchBean queryByDeviceId(SQLiteDatabase db, int deviceId){
		String table = GEN_TABLE_NAME;
		String selection = GEN_SWITCH_DEVICEID + "='" + deviceId + "'";
		Cursor c = db.query(table, null, selection, null, null, null, null);
		CsstSHSwitchBean switchbean = null;
		if (c != null && c.moveToFirst()){
			 
			int switchId; 
			String switchName1,switchName2,switchName3;
			int mdeviceId, switchonoff;
			do{
				switchId = c.getInt(c.getColumnIndexOrThrow(GEN_SWITCH_ID));
				switchName1 = c.getString(c.getColumnIndexOrThrow(GEN_SWITCH_NAME1));
				switchName2 = c.getString(c.getColumnIndexOrThrow(GEN_SWITCH_NAME2));
				switchName3 = c.getString(c.getColumnIndexOrThrow(GEN_SWITCH_NAME3));
				mdeviceId = c.getInt(c.getColumnIndexOrThrow(GEN_SWITCH_DEVICEID));
				switchonoff = c.getInt(c.getColumnIndexOrThrow(GEN_SWITCH_ONOFF));
				switchbean = new CsstSHSwitchBean(switchId, switchName1, switchName2, switchName3, switchonoff, mdeviceId);
			}while(c.moveToNext());
		}
		c.close();
		return switchbean;
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
	public List<CsstSHSwitchBean> query(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);
		List<CsstSHSwitchBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHSwitchBean>();
			int switchId; 
			String switchName1,switchName2,switchName3;
			int mdeviceId, switchonoff;
			do{
				switchId = c.getInt(c.getColumnIndexOrThrow(GEN_SWITCH_ID));
				switchName1 = c.getString(c.getColumnIndexOrThrow(GEN_SWITCH_NAME1));
				switchName2 = c.getString(c.getColumnIndexOrThrow(GEN_SWITCH_NAME2));
				switchName3 = c.getString(c.getColumnIndexOrThrow(GEN_SWITCH_NAME3));
				mdeviceId = c.getInt(c.getColumnIndexOrThrow(GEN_SWITCH_DEVICEID));
				switchonoff = c.getInt(c.getColumnIndexOrThrow(GEN_SWITCH_ONOFF));
				list.add( new CsstSHSwitchBean(switchId, switchName1, switchName2, switchName3, switchonoff, mdeviceId));;
			}while(c.moveToNext());
		}
		c.close();
		return list;
		// TODO Auto-generated method stub
	}

	@Override
	public CsstSHSwitchBean query(SQLiteDatabase db, int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
