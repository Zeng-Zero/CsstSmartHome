package com.csst.smarthome.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csst.smarthome.bean.CsstSHActionBean;
import com.csst.smarthome.bean.CsstSHCustomRCKeyBean;

/**
 * 动作
 * @author liuyang
 */
public class CsstSHActionTable implements ICsstSHDaoManager<CsstSHActionBean> {
	public static final String TAG = "CsstSHACTIONTable";
	/** 表名 */
	public static final String GEN_TABLE_NAME = "sh_action";
	/** 设备id */
	public static final String GEN_ACTION_ID = "sh_action_id";
	/** 设备名字 */
	public static final String GEN_ACTION_NAME = "sh_action_name";
	/** 图片封面是否内置 */
	public static final String GEN_ACTION_LOCATION = "sh_action_location";
	/** 设备封面 */
	public static final String GEN_ACTION_KEYCODE = "sh_action_keycode";
	/** 设备状态 */
	public static final String GEN_ACTION_DELAYTIME = "sh_action_delaytime";
	/** 设备状态 */
	public static final String GEN_ACTION_RESULT = "sh_action_result";
	/** 设备物理地址id*/
	public static final String GEN_ACTION_MODEL_ID = CsstSHModelTable.GEN_MODEL_ID;
	
	/** 建表SQL语句 */
	public static String GEN_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + GEN_TABLE_NAME
			+ "(" + GEN_ACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ GEN_ACTION_NAME + " TEXT,"
			+ GEN_ACTION_LOCATION + " TEXT,"
			+ GEN_ACTION_KEYCODE + " TEXT,"
			+ GEN_ACTION_MODEL_ID + " TEXT,"
			+ GEN_ACTION_RESULT+ " INTEGER,"
			+ GEN_ACTION_DELAYTIME + " INTEGER "
			+ ")";
	/** 删除表 */
	public static String GEN_DROP_TABLE = "DROP TABLE IF EXISTS " + CsstSHActionTable.GEN_TABLE_NAME;
    private static CsstSHActionTable mInstance = null;
	
	private CsstSHActionTable(){
		
	}
	
	public static final CsstSHActionTable getInstance(){
		if (null == mInstance){
			mInstance = new CsstSHActionTable();
		}
		return mInstance;
	}
	
	@Override
	public long insert(SQLiteDatabase db, CsstSHActionBean arg0)
			throws RuntimeException {
		System.out.println(" CsstSHActionTable Action insert IN");
		String table = GEN_TABLE_NAME;
		ContentValues cv = new ContentValues();
		cv.put(GEN_ACTION_NAME, arg0.getmActionName());
		cv.put(GEN_ACTION_LOCATION,arg0.getmLocation());
		cv.put(GEN_ACTION_KEYCODE, arg0.getmKeyCode());
		cv.put(GEN_ACTION_MODEL_ID, arg0.getmModelId());
		cv.put(GEN_ACTION_RESULT, arg0.getResultAction());
		cv.put(GEN_ACTION_DELAYTIME, arg0.getmDelayTime());
		return db.insert(table, null, cv);
	}
	
	@Override
	public long update(SQLiteDatabase db, CsstSHActionBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_ACTION_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getmActionId())};
		ContentValues cv = new ContentValues();
		cv.put(GEN_ACTION_NAME, arg0.getmActionName());
		cv.put(GEN_ACTION_LOCATION, arg0.getmLocation());
		cv.put(GEN_ACTION_KEYCODE, arg0.getmKeyCode());
		cv.put(GEN_ACTION_MODEL_ID, arg0.getmModelId());
		cv.put(GEN_ACTION_RESULT, arg0.getResultAction());
		cv.put(GEN_ACTION_DELAYTIME, arg0.getmDelayTime());
		return db.update(table, cv, whereClause, whereArgs);
	}
	/**
	 * 删除一条数据
	 */
	@Override
	public long delete(SQLiteDatabase db, CsstSHActionBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_ACTION_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getmActionId())};
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
		String whereClause = GEN_ACTION_MODEL_ID + "=?";
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
	public final boolean roomExisSameACTION(SQLiteDatabase db, String columnsName, int modelid){
		String table = GEN_TABLE_NAME;
		String selection = GEN_ACTION_NAME + "='" + columnsName + "' AND " + GEN_ACTION_MODEL_ID + "=" + modelid;
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
	public final List<CsstSHActionBean> queryByModel(SQLiteDatabase db, int modelid){
		String table = GEN_TABLE_NAME;
		String selection = GEN_ACTION_MODEL_ID + "='" + modelid + "'";
		Cursor c = db.query(table, null, selection, null, null, null, null);
		List<CsstSHActionBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHActionBean>();
			int actionId; 
			String actionName;
			String actionKeyCode, location;
			int delayTime;
			int result;
			do{
				actionId = c.getInt(c.getColumnIndexOrThrow(GEN_ACTION_ID));
				actionName = c.getString(c.getColumnIndexOrThrow(GEN_ACTION_NAME));
				location = c.getString(c.getColumnIndexOrThrow(GEN_ACTION_LOCATION));
				actionKeyCode = c.getString(c.getColumnIndexOrThrow(GEN_ACTION_KEYCODE));
				modelid = c.getInt(c.getColumnIndexOrThrow(GEN_ACTION_MODEL_ID));
				delayTime = c.getInt(c.getColumnIndexOrThrow(GEN_ACTION_DELAYTIME));
				result = c.getInt(c.getColumnIndexOrThrow(GEN_ACTION_RESULT));
				list.add(new CsstSHActionBean(actionId, actionName, location, actionKeyCode, delayTime, modelid,result));
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
	public List<CsstSHActionBean> query(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);
		List<CsstSHActionBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHActionBean>();
			int actionId; 
			String actionName;
			String actionKeyCode, location;
			int delayTime,modelid;
			int result;
			do{
				actionId = c.getInt(c.getColumnIndexOrThrow(GEN_ACTION_ID));
				actionName = c.getString(c.getColumnIndexOrThrow(GEN_ACTION_NAME));
				location = c.getString(c.getColumnIndexOrThrow(GEN_ACTION_LOCATION));
				actionKeyCode = c.getString(c.getColumnIndexOrThrow(GEN_ACTION_KEYCODE));
				modelid = c.getInt(c.getColumnIndexOrThrow(GEN_ACTION_MODEL_ID));
				delayTime = c.getInt(c.getColumnIndexOrThrow(GEN_ACTION_DELAYTIME));
				result = c.getInt(c.getColumnIndexOrThrow(GEN_ACTION_RESULT));
				list.add(new CsstSHActionBean(actionId, actionName, location, actionKeyCode, delayTime, modelid,result));
			}while(c.moveToNext());
		}
		c.close();
		return list;
		// TODO Auto-generated method stub
	}

	@Override
	public CsstSHActionBean query(SQLiteDatabase db, int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
