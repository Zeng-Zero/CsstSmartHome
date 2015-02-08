package com.csst.smarthome.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csst.smarthome.bean.CsstSafeClockBean;

/**
 * 动作
 * @author liuyang
 */
public class CsstSHSafeClockTable implements ICsstSHDaoManager<CsstSafeClockBean> {
	public static final String TAG = "CsstSHClockOpenTable";
	/** 表名 */
	public static final String GEN_TABLE_NAME = "sh_SafeClock";
	/** 定时时间id */
	public static final String GEN_ClockOpen_ID = "sh_SafeClock_id";
	/** 图片封面是否内置 */
	public static final String GEN_ClockOpen_DAY = "sh_SafeClock_day";
	/** 定时时间封面 */
	public static final String GEN_ClockOpen_TIME_HOUR = "sh_SafeClock_time_hour";
	/** 定时分钟*/
	public static final String GEN_ClockOpen_TIME_MIN = "sh_SafeClock_time_min";
	/** 定时时间物理地址id*/
	public static final String GEN_ClockOpen_OPENFLAG = "sh_SafeClock_openflag";
	
	/** 建表SQL语句 */
	public static String GEN_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + GEN_TABLE_NAME
			+ "(" + GEN_ClockOpen_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ GEN_ClockOpen_DAY + " INTEGER,"
			+ GEN_ClockOpen_TIME_HOUR + " INTEGER,"
			+ GEN_ClockOpen_TIME_MIN + " INTEGER,"
			+ GEN_ClockOpen_OPENFLAG+ " INTEGER"
			+ ")";
	/** 删除表 */
	public static String GEN_DROP_TABLE = "DROP TABLE IF EXISTS " + CsstSHSafeClockTable.GEN_TABLE_NAME;
    private static CsstSHSafeClockTable mInstance = null;
	
	private CsstSHSafeClockTable(){
		
	}
	
	public static final CsstSHSafeClockTable getInstance(){
		if (null == mInstance){
			mInstance = new CsstSHSafeClockTable();
		}
		return mInstance;
	}
	
	@Override
	public long insert(SQLiteDatabase db, CsstSafeClockBean arg0)
			throws RuntimeException {
		System.out.println(" CsstSHClockOpenTable ClockOpen insert IN");
		String table = GEN_TABLE_NAME;
		ContentValues cv = new ContentValues();
		cv.put(GEN_ClockOpen_DAY,arg0.getmClockOpenDay());
		cv.put(GEN_ClockOpen_TIME_HOUR, arg0.getmClockOpenTimeHour());
		cv.put(GEN_ClockOpen_TIME_MIN, arg0.getmClockOpenTimeMin());
		cv.put(GEN_ClockOpen_OPENFLAG, arg0.getmClockOpenopenFlag());
		return db.insert(table, null, cv);
	}
	
	@Override
	public long update(SQLiteDatabase db, CsstSafeClockBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_ClockOpen_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getmClockOpenId())};
		ContentValues cv = new ContentValues();
		cv.put(GEN_ClockOpen_DAY,arg0.getmClockOpenDay());
		cv.put(GEN_ClockOpen_TIME_HOUR, arg0.getmClockOpenTimeHour());
		cv.put(GEN_ClockOpen_TIME_MIN, arg0.getmClockOpenTimeMin());
		cv.put(GEN_ClockOpen_OPENFLAG, arg0.getmClockOpenopenFlag());
		return db.update(table, cv, whereClause, whereArgs);
	}
	/**
	 * 删除一条数据
	 */
	@Override
	public long delete(SQLiteDatabase db, CsstSafeClockBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_ClockOpen_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getmClockOpenId())};
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
	public List<CsstSafeClockBean> query(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);List<CsstSafeClockBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSafeClockBean>();
			int clockOpenId; 
			int clockOpenDay, clockOpenTimeHour,clockOpenTimeMin ;
			int clockOpenOpenFlag;
			do{
				clockOpenId = c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_ID));
				clockOpenDay = c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_DAY));
				clockOpenTimeHour = c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_TIME_HOUR));
				clockOpenTimeMin = c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_TIME_MIN));
				clockOpenOpenFlag  =c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_OPENFLAG));
				list.add(new CsstSafeClockBean(clockOpenId, clockOpenDay, clockOpenTimeHour,clockOpenTimeMin,
						clockOpenOpenFlag));
			}while(c.moveToNext());
		}
		c.close();
		return list;
		// TODO Auto-generated method stub
	}

	@Override
	public CsstSafeClockBean query(SQLiteDatabase db, int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
