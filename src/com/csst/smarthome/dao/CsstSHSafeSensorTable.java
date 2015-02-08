package com.csst.smarthome.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csst.smarthome.bean.CsstSHSafeSensorBean;

/**
 * 动作
 * @author liuyang
 */
public class CsstSHSafeSensorTable implements ICsstSHDaoManager<CsstSHSafeSensorBean> {
	public static final String TAG = "CsstSHSafeSensorTable";
	/** 表名 */
	public static final String GEN_TABLE_NAME = "sh_safesensor";
	/** sensor_id */
	public static final String GEN_SAFESENSOR_ID = "sh_safesensor_id";
	/** sensor名字 */
	public static final String GEN_SAFESENSOR_NAME = "sh_safesensor_name";
	/** sensor电量 */
	public static final String GEN_SAFESENSOR_BATTERY = "sh_safesensor_battery";
	/** Sensor 是否选中 */
	public static final String GEN_SAFESENSOR_SELECT = "sh_safesensor_select";
	/** Sensor SSUIDLOW */
	public static final String GEN_SAFESENSOR_SSUIDLOW = "sh_safesensor_ssuidlow";
	/** Sensor SSUIDLOW */
	public static final String GEN_SAFESENSOR_SSUIDMID = "sh_safesensor_ssuidmid";
	/** Sensor SSUIDLOW */
	public static final String GEN_SAFESENSOR_SSUIDHIGHT = "sh_safesensor_ssuidhight";
	/** Sensor SSUIDLOW */
	public static final String GEN_SAFESENSOR_TYPE= "sh_safesensor_type";
	
	public static String GEN_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + GEN_TABLE_NAME
			+ "(" + GEN_SAFESENSOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ GEN_SAFESENSOR_NAME + " TEXT,"
			+ GEN_SAFESENSOR_BATTERY + " INTEGER,"
			+ GEN_SAFESENSOR_SELECT + " INTEGER,"
			+ GEN_SAFESENSOR_SSUIDLOW + " INTEGER,"
			+ GEN_SAFESENSOR_SSUIDMID + " INTEGER,"
			+ GEN_SAFESENSOR_SSUIDHIGHT + " INTEGER,"
			+ GEN_SAFESENSOR_TYPE + " INTEGER"
			+ ")";
	/** 删除表 */
	public static String GEN_DROP_TABLE = "DROP TABLE IF EXISTS " + CsstSHSafeSensorTable.GEN_TABLE_NAME;
    private static CsstSHSafeSensorTable mInstance = null;
	
	private CsstSHSafeSensorTable(){
		
	}
	
	public static final CsstSHSafeSensorTable getInstance(){
		if (null == mInstance){
			mInstance = new CsstSHSafeSensorTable();
		}
		return mInstance;
	}
	
	@Override
	public long insert(SQLiteDatabase db, CsstSHSafeSensorBean arg0)
			throws RuntimeException {
		System.out.println(" CsstSHSafeSensorTable Action insert IN");
		String table = GEN_TABLE_NAME;
		ContentValues cv = new ContentValues();
		cv.put(GEN_SAFESENSOR_NAME, arg0.getmSensorName());
		cv.put(GEN_SAFESENSOR_BATTERY,arg0.getMbattery());
		cv.put(GEN_SAFESENSOR_SELECT, arg0.isMselect());
		cv.put(GEN_SAFESENSOR_BATTERY,arg0.getMbattery());
		cv.put(GEN_SAFESENSOR_SSUIDLOW,arg0.getMssuidLow());
		cv.put(GEN_SAFESENSOR_SSUIDMID,arg0.getMssuidMid());
		cv.put(GEN_SAFESENSOR_SSUIDHIGHT,arg0.getMssuidHight());
		cv.put(GEN_SAFESENSOR_TYPE,arg0.getmType());
		return db.insert(table, null, cv);
	}
	
	@Override
	public long update(SQLiteDatabase db, CsstSHSafeSensorBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_SAFESENSOR_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getmSensorId())};
		ContentValues cv = new ContentValues();
		cv.put(GEN_SAFESENSOR_NAME, arg0.getmSensorName());
		cv.put(GEN_SAFESENSOR_BATTERY, arg0.getMbattery());
		cv.put(GEN_SAFESENSOR_SELECT, arg0.isMselect());
		cv.put(GEN_SAFESENSOR_SSUIDLOW,arg0.getMssuidLow());
		cv.put(GEN_SAFESENSOR_SSUIDMID,arg0.getMssuidMid());
		cv.put(GEN_SAFESENSOR_SSUIDHIGHT,arg0.getMssuidHight());
		cv.put(GEN_SAFESENSOR_TYPE,arg0.getmType());
		return db.update(table, cv, whereClause, whereArgs);
	}
	/**
	 * 删除一条数据
	 */
	@Override
	public long delete(SQLiteDatabase db, CsstSHSafeSensorBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_SAFESENSOR_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getmSensorId())};
		return db.delete(table, whereClause, whereArgs);
	}
	
	/**
	 * 通过情景模式删除该情景模式下的全部动作
	 * @param db
	 * @param modelid
	 * @return
	 * @throws RuntimeException
	 */
	public long  deleteByModel(SQLiteDatabase db, int sensorid) throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_SAFESENSOR_ID + "=?";
		String[] whereArgs = {Integer.toString(sensorid)};
		return db.delete(table, whereClause, whereArgs);
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
	public List<CsstSHSafeSensorBean> query(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);
		List<CsstSHSafeSensorBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHSafeSensorBean>();
			int sensorId = 0;
			int battery = 0;
			int select = 0;
			String sensorname = null;
			int ssuidlow,ssuidmid,ssuidhight,type;
			do{
				sensorId = c.getInt(c.getColumnIndexOrThrow(GEN_SAFESENSOR_ID));
				sensorname = c.getString(c.getColumnIndexOrThrow(GEN_SAFESENSOR_NAME));
				battery = c.getInt(c.getColumnIndexOrThrow(GEN_SAFESENSOR_BATTERY));
				select = c.getInt(c.getColumnIndexOrThrow(GEN_SAFESENSOR_SELECT));
				ssuidlow = c.getInt(c.getColumnIndexOrThrow(GEN_SAFESENSOR_SSUIDLOW));
				ssuidmid = c.getInt(c.getColumnIndexOrThrow(GEN_SAFESENSOR_SSUIDMID));
				ssuidhight = c.getInt(c.getColumnIndexOrThrow(GEN_SAFESENSOR_SSUIDHIGHT));
				type = c.getInt(c.getColumnIndexOrThrow(GEN_SAFESENSOR_TYPE));
				list.add(new CsstSHSafeSensorBean(sensorId, sensorname, battery,select,ssuidlow,ssuidmid,ssuidhight,type));
			}while(c.moveToNext());
		}
		return list;
	}

	@Override
	public CsstSHSafeSensorBean query(SQLiteDatabase db, int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean columnExists(SQLiteDatabase db, String columnsName,
			Object columnsValue) {
		// TODO Auto-generated method stub
		String table = GEN_TABLE_NAME;
		String[] columns = {columnsName};
		String selection = columnsName + "=?";
		String[] selectionArgs = {columnsValue.toString()};
		Cursor c = db.query(table, columns, selection, selectionArgs, null, null, null);
		return (c != null && c.moveToFirst() && c.getCount() > 0);
	}

}
