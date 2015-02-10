package com.csst.smarthome.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csst.smarthome.bean.CsstClockOpenBean;
import com.csst.smarthome.bean.CsstSHActionBean;
import com.csst.smarthome.bean.CsstSHModelBean;

/**
 * 楼层
 * @author liuyang
 */
public class CsstSHModelTable implements ICsstSHDaoManager<CsstSHModelBean> {
	
	public static final String TAG = "CsstSHModelTable";
	
	/** 表名 */
	public static final String GEN_TABLE_NAME = "sh_model";
	/** 情景模式id */
	public static final String GEN_MODEL_ID = "sh_model_id";
	/** 情景模式名称*/
	public static final String GEN_MODEL_NAME = "sh_model_name";
	/** 情景模式图片*/
	public static final String GEN_MODEL_PICTURE = "sh_model_picture";
	
	/** 建表SQL语句 */
	public static String GEN_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " 
			+ GEN_TABLE_NAME
			+ "(" + GEN_MODEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ GEN_MODEL_NAME + " TEXT," + GEN_MODEL_PICTURE + " TEXT" +")";
	/** 删除表 */
	public static String GEN_DROP_TABLE = "DROP TABLE IF EXISTS " + GEN_TABLE_NAME;
	
	private static CsstSHModelTable mInstance = null;
	
	private CsstSHModelTable(){
		
	}
	
	public static final CsstSHModelTable getInstance(){
		if (null == mInstance){
			mInstance = new CsstSHModelTable();
		}
		return mInstance;
	}

	@Override
	public long insert(SQLiteDatabase db, CsstSHModelBean arg0)
			throws RuntimeException {
		System.out.println(" CsstSHModelTable Action insert");
		String table = GEN_TABLE_NAME;
		ContentValues cv = new ContentValues();
		cv.put(GEN_MODEL_NAME, arg0.getmodelName());
		cv.put(GEN_MODEL_PICTURE, arg0.getmModelIconPath());
		return db.insert(table, null, cv);
	}

	@Override
	public long update(SQLiteDatabase db, CsstSHModelBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_MODEL_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getmodelId())};
		ContentValues cv = new ContentValues();
		cv.put(GEN_MODEL_NAME, arg0.getmodelName());
		cv.put(GEN_MODEL_PICTURE, arg0.getmModelIconPath());
		return db.update(table, cv, whereClause, whereArgs);
	}

	@Override
	public long delete(SQLiteDatabase db, CsstSHModelBean arg0)
			throws RuntimeException {
		//删除场景下的数据
		deleteDataOfModel(db, arg0.getmodelId());
		//删除该场景
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_MODEL_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getmodelId())};
		return db.delete(table, whereClause, whereArgs);
	}
	/**
	 * 删除场景下所有的数据(ACtion)
	 */
	private final void deleteDataOfModel(SQLiteDatabase db, int modelId){
		//查询楼层对应的Action
		List<CsstSHActionBean> action = CsstSHActionTable.getInstance().queryByModel(db, modelId);
		//删除楼层对应的Action
		for(int i =0; i<action.size();i++)
		CsstSHActionTable.getInstance().delete(db, action.get(i));
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
	public List<CsstSHModelBean> query(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);
		List<CsstSHModelBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHModelBean>();
			int modelId = 0;
			String modelName = null;
			String modelPicture =null;
			do{
				System.out.println("the dataBeans get movenext ");
				modelId = c.getInt(c.getColumnIndexOrThrow(GEN_MODEL_ID));
				modelName = c.getString(c.getColumnIndexOrThrow(GEN_MODEL_NAME));
				modelPicture = c.getString(c.getColumnIndexOrThrow(GEN_MODEL_PICTURE));
				list.add(new CsstSHModelBean(modelId, modelName,modelPicture));
			}while(c.moveToNext());
		}
		c.close();
		return list;
	}
	
/**
 * 通过名称查询ID
 * @param db
 * @param name
 * @return
 */
	public  CsstSHModelBean query(SQLiteDatabase db, String modelname) {
		String table = GEN_TABLE_NAME;
		String selection = GEN_MODEL_NAME + "=?";
		String[] selectionArgs = {modelname};
		Cursor c = db.query(table, null, selection, selectionArgs, null, null, null);
		if (c != null && c.moveToFirst()){
			int modelId = 0;
			String modelName = null;
			String modelPicture =null;
			modelId = c.getInt(c.getColumnIndexOrThrow(GEN_MODEL_ID));
			modelName = c.getString(c.getColumnIndexOrThrow(GEN_MODEL_NAME));
			modelPicture = c.getString(c.getColumnIndexOrThrow(GEN_MODEL_PICTURE));
			c.close();
			return new CsstSHModelBean(modelId, modelName,modelPicture);
		}
		System.out.println("CsstSHModelBean query(SQLiteDatabase db, String modelname) ");
		return null;
	}


	@Override
	public CsstSHModelBean query(SQLiteDatabase db, int id) {
		String table = GEN_TABLE_NAME;
		String selection = GEN_MODEL_ID + "=?";
		String[] selectionArgs = {Integer.toString(id)};
		Cursor c = db.query(table, null, selection, selectionArgs, null, null, null);
		if (c != null && c.moveToFirst()){
			int modelId = 0;
			String modelName = null;
			String modelPicture =null;
			modelId = c.getInt(c.getColumnIndexOrThrow(GEN_MODEL_ID));
			modelName = c.getString(c.getColumnIndexOrThrow(GEN_MODEL_NAME));
			modelPicture = c.getString(c.getColumnIndexOrThrow(GEN_MODEL_PICTURE));
			c.close();
			return new CsstSHModelBean(modelId, modelName,modelPicture);
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
		c.close();
		return 0;
	}

	/**
	 * 查询楼层对应的房间列表
	 * @param db
	 * @param id
	 * @return
	 */
	public final List<CsstSHActionBean> getActionByModelId(SQLiteDatabase db, int modelid){
		//查询楼层对应的房间
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM ");
		sb.append(CsstSHActionTable.GEN_TABLE_NAME);
		sb.append(" WHERE " + CsstSHActionTable.GEN_ACTION_MODEL_ID + "=" + modelid);
		Cursor c = db.rawQuery(sb.toString(), null);
		List<CsstSHActionBean> action = null;
		if (c != null && c.moveToFirst()){
			action = new ArrayList<CsstSHActionBean>();
			int actionId; 
			String actionName;
			String actionKeyCode, location;
			int delayTime;
			int result;
			do{
				actionId = c.getInt(c.getColumnIndexOrThrow(CsstSHActionTable.GEN_ACTION_ID));
				actionName = c.getString(c.getColumnIndexOrThrow(CsstSHActionTable.GEN_ACTION_NAME));
				location = c.getString(c.getColumnIndexOrThrow(CsstSHActionTable.GEN_ACTION_LOCATION));
				actionKeyCode = c.getString(c.getColumnIndexOrThrow(CsstSHActionTable.GEN_ACTION_KEYCODE));
				modelid = c.getInt(c.getColumnIndexOrThrow(CsstSHActionTable.GEN_ACTION_MODEL_ID));
				delayTime = c.getInt(c.getColumnIndexOrThrow(CsstSHActionTable.GEN_ACTION_DELAYTIME));
				result = c.getInt(c.getColumnIndexOrThrow(CsstSHActionTable.GEN_ACTION_RESULT));
				action.add(new CsstSHActionBean(actionId, actionName, location, actionKeyCode, delayTime, modelid,result));
			}while(c.moveToNext());
		}
		c.close();
		return action;
	}
	

	/**
	 * 查询情景模式下的定时列表
	 * @param db
	 * @param id
	 * @return
	 */
	public final List<CsstClockOpenBean> getClockOpenByModelId(SQLiteDatabase db, int modelid){
		//查询楼层对应的房间
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM ");
		sb.append(CsstSHClockOpenTable.GEN_TABLE_NAME);
		sb.append(" WHERE " + CsstSHClockOpenTable.GEN_ClockOpen_MODEL_ID + "=" + modelid);
		Cursor c = db.rawQuery(sb.toString(), null);
		List<CsstClockOpenBean> clockOpenlist = null;
		if (c != null && c.moveToFirst()){
			clockOpenlist = new ArrayList<CsstClockOpenBean>();
			int clockOpenId; 
			String clockOpenName;
			int clockOpenDay, clockOpenTimeHour,clockOpenTimeMin;
			int clockOpenOpenFlag;
			do{
				clockOpenId = c.getInt(c.getColumnIndexOrThrow(CsstSHClockOpenTable.GEN_ClockOpen_ID));
				clockOpenName = c.getString(c.getColumnIndexOrThrow(CsstSHClockOpenTable.GEN_ClockOpen_NAME));
				clockOpenDay = c.getInt(c.getColumnIndexOrThrow(CsstSHClockOpenTable.GEN_ClockOpen_DAY));
				clockOpenTimeHour = c.getInt(c.getColumnIndexOrThrow(CsstSHClockOpenTable.GEN_ClockOpen_TIME_HOUR));
				clockOpenTimeMin = c.getInt(c.getColumnIndexOrThrow(CsstSHClockOpenTable.GEN_ClockOpen_TIME_MIN));
				modelid = c.getInt(c.getColumnIndexOrThrow(CsstSHClockOpenTable.GEN_ClockOpen_MODEL_ID));
				clockOpenOpenFlag  =c.getInt(c.getColumnIndexOrThrow(CsstSHClockOpenTable.GEN_ClockOpen_OPENFLAG));
				clockOpenlist.add(new CsstClockOpenBean(clockOpenId, clockOpenName, clockOpenTimeHour,clockOpenTimeMin,
						clockOpenDay,modelid,clockOpenOpenFlag));
			}while(c.moveToNext());
		}
		c.close();
		return clockOpenlist;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
