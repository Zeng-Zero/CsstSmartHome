package com.csst.smarthome.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csst.smarthome.bean.CsstClockOpenBean;
import com.csst.smarthome.bean.CsstSHActionBean;

/**
 * 动作
 * @author liuyang
 */
public class CsstSHClockOpenTable implements ICsstSHDaoManager<CsstClockOpenBean> {
	public static final String TAG = "CsstSHClockOpenTable";
	/** 表名 */
	public static final String GEN_TABLE_NAME = "sh_ClockOpen";
	/** 定时时间id */
	public static final String GEN_ClockOpen_ID = "sh_ClockOpen_id";
	/** 定时时间名字 情景模式名称 */
	public static final String GEN_ClockOpen_NAME = "sh_ClockOpen_name";
	/** 图片封面是否内置 */
	public static final String GEN_ClockOpen_DAY = "sh_ClockOpen_day";
	/** 定时时间封面 */
	public static final String GEN_ClockOpen_TIME_HOUR = "sh_ClockOpen_time_hour";
	/** 定时分钟*/
	public static final String GEN_ClockOpen_TIME_MIN = "sh_ClockOpen_time_min";
	/** 定时时间物理地址id*/
	public static final String GEN_ClockOpen_MODEL_ID = CsstSHModelTable.GEN_MODEL_ID;
	/** 定时时间物理地址id*/
	public static final String GEN_ClockOpen_OPENFLAG = "sh_ClockOpen_openflag";
	
	/** 建表SQL语句 */
	public static String GEN_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + GEN_TABLE_NAME
			+ "(" + GEN_ClockOpen_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ GEN_ClockOpen_NAME + " TEXT,"
			+ GEN_ClockOpen_DAY + " INTEGER,"
			+ GEN_ClockOpen_TIME_HOUR + " INTEGER,"
			+ GEN_ClockOpen_TIME_MIN + " INTEGER,"
			+ GEN_ClockOpen_MODEL_ID + " INTEGER,"
			+ GEN_ClockOpen_OPENFLAG+ " INTEGER"
			+ ")";
	/** 删除表 */
	public static String GEN_DROP_TABLE = "DROP TABLE IF EXISTS " + CsstSHClockOpenTable.GEN_TABLE_NAME;
    private static CsstSHClockOpenTable mInstance = null;
	
	private CsstSHClockOpenTable(){
		
	}
	
	public static final CsstSHClockOpenTable getInstance(){
		if (null == mInstance){
			mInstance = new CsstSHClockOpenTable();
		}
		return mInstance;
	}
	
	@Override
	public long insert(SQLiteDatabase db, CsstClockOpenBean arg0)
			throws RuntimeException {
		System.out.println(" CsstSHClockOpenTable ClockOpen insert IN");
		String table = GEN_TABLE_NAME;
		ContentValues cv = new ContentValues();
		cv.put(GEN_ClockOpen_NAME, arg0.getmClockOpenName());
		cv.put(GEN_ClockOpen_DAY,arg0.getmClockOpenDay());
		cv.put(GEN_ClockOpen_TIME_HOUR, arg0.getmClockOpenTimeHour());
		cv.put(GEN_ClockOpen_TIME_MIN, arg0.getmClockOpenTimeMin());
		cv.put(GEN_ClockOpen_MODEL_ID, arg0.getmClockOpenModelId());
		cv.put(GEN_ClockOpen_OPENFLAG, arg0.getmClockOpenopenFlag());
		return db.insert(table, null, cv);
	}
	
	@Override
	public long update(SQLiteDatabase db, CsstClockOpenBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_ClockOpen_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getmClockOpenId())};
		ContentValues cv = new ContentValues();
		cv.put(GEN_ClockOpen_NAME, arg0.getmClockOpenName());
		cv.put(GEN_ClockOpen_DAY,arg0.getmClockOpenDay());
		cv.put(GEN_ClockOpen_TIME_HOUR, arg0.getmClockOpenTimeHour());
		cv.put(GEN_ClockOpen_TIME_MIN, arg0.getmClockOpenTimeMin());
		cv.put(GEN_ClockOpen_MODEL_ID, arg0.getmClockOpenModelId());
		cv.put(GEN_ClockOpen_OPENFLAG, arg0.getmClockOpenopenFlag());
		return db.update(table, cv, whereClause, whereArgs);
		
	}
	/**
	 * 删除一条数据
	 */
	@Override
	public long delete(SQLiteDatabase db, CsstClockOpenBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_ClockOpen_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getmClockOpenId())};
		return db.delete(table, whereClause, whereArgs);
	}
	
	/**
	 * 通过情景模式删除该情景模式下的全部定时
	 * @param db
	 * @param modelid
	 * @return
	 * @throws RuntimeException
	 */
	public long  deleteByModel(SQLiteDatabase db, int modelid) throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_ClockOpen_MODEL_ID + "=?";
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
	public final boolean modelExisSameClockOpen(SQLiteDatabase db, String columnsName, int modelid){
		String table = GEN_TABLE_NAME;
		String selection = GEN_ClockOpen_NAME + "='" + columnsName + "' AND " + GEN_ClockOpen_MODEL_ID + "=" + modelid;
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
	 * 通过情景模式查询定时
	 * @param db
	 * @param roomid
	 * @return
	 */
	public final List<CsstClockOpenBean> queryByModel(SQLiteDatabase db, int modelid){
		String table = GEN_TABLE_NAME;
		String selection = GEN_ClockOpen_MODEL_ID + "='" + modelid + "'";
		Cursor c = db.query(table, null, selection, null, null, null, null);
		List<CsstClockOpenBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstClockOpenBean>();
			int clockOpenId; 
			String clockOpenName;
			int clockOpenDay, clockOpenTimeHour,clockOpenTimeMin;
			int clockOpenOpenFlag;
			do{
				clockOpenId = c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_ID));
				clockOpenName = c.getString(c.getColumnIndexOrThrow(GEN_ClockOpen_NAME));
				clockOpenDay = c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_DAY));
				clockOpenTimeHour = c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_TIME_HOUR));
				clockOpenTimeMin = c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_TIME_MIN));
				modelid = c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_MODEL_ID));
				clockOpenOpenFlag  =c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_OPENFLAG));
				list.add(new CsstClockOpenBean(clockOpenId, clockOpenName, clockOpenTimeHour,clockOpenTimeMin,
						clockOpenDay,modelid,clockOpenOpenFlag));
			}while(c.moveToNext());
		}
		c.close();
		return list;
	}
	/**
	 * 查看数据库中改情景模式下是否已经存在名称一样的一条数据
	 */
	public boolean columnExistsbyname(SQLiteDatabase db,int modelid,String clockopenname) {
		List<CsstClockOpenBean> list = null;
		list =  queryByModel(db,modelid);
		//如果是list 为空的话说明就没有这条数据
		if(list==null){
			return false;
		}
		for(int i=0;i<list.size();i++){
			if(list.get(i).getmClockOpenName().equals(clockopenname)){
				return true;
			}
		}
		return false;
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
	public List<CsstClockOpenBean> query(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);List<CsstClockOpenBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstClockOpenBean>();
			int clockOpenId; 
			String clockOpenName;
			int clockOpenDay, clockOpenTimeHour,clockOpenTimeMin, modelid ;
			int clockOpenOpenFlag;
			do{
				clockOpenId = c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_ID));
				clockOpenName = c.getString(c.getColumnIndexOrThrow(GEN_ClockOpen_NAME));
				clockOpenDay = c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_DAY));
				clockOpenTimeHour = c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_TIME_HOUR));
				clockOpenTimeMin = c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_TIME_MIN));
				modelid = c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_MODEL_ID));
				clockOpenOpenFlag  =c.getInt(c.getColumnIndexOrThrow(GEN_ClockOpen_OPENFLAG));
				list.add(new CsstClockOpenBean(clockOpenId, clockOpenName, clockOpenTimeHour,clockOpenTimeMin,
						clockOpenDay,modelid,clockOpenOpenFlag));
			}while(c.moveToNext());
		}
		c.close();
		return list;
		// TODO Auto-generated method stub
	}

	@Override
	public CsstClockOpenBean query(SQLiteDatabase db, int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
