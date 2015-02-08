package com.csst.smarthome.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.util.CsstSHConfigPreference;

/**
 * 数据库管理
 * @author liuyang
 */
public class CsstSHDataBase extends SQLiteOpenHelper {
	
	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "csstSmarthome.db";
	private final Context mContext;

	public CsstSHDataBase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		updateDatabase(mContext, db, 0, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		updateDatabase(mContext, db, oldVersion, newVersion);
	}
	
	private static void updateDatabase(Context context, SQLiteDatabase db, int fromVersion, int toVersion){
		// 如果表存在，先删除表
		db.execSQL(CsstSHFloorTable.GEN_DROP_TABLE);
		db.execSQL(CsstSHModelTable.GEN_DROP_TABLE);
		db.execSQL(CsstSHActionTable.GEN_DROP_TABLE);
		db.execSQL(CsstSHRoomTable.GEN_DROP_TABLE);
		db.execSQL(CsstSHRemoteControlTypeTable.GEN_DROP_TABLE);
		db.execSQL(CsstSHRCTemplateKeyTable.GEN_DROP_TABLE);
		db.execSQL(CsstSHDeviceRCKeyTable.GEN_DROP_TABLE);
		db.execSQL(CsstSHDeviceTable.GEN_DROP_TABLE);
		db.execSQL(CsstSHCameraTable.GEN_DROP_TABLE);
		db.execSQL(CsstSHClockOpenTable.GEN_DROP_TABLE);
		db.execSQL(CsstSHSafeSensorTable.GEN_DROP_TABLE);
		db.execSQL(CsstSHCustomRCKeyTable.GEN_DROP_TABLE);
		db.execSQL(CsstSHSwitchTable.GEN_DROP_TABLE);
		db.execSQL(CsstSHSafeClockTable.GEN_DROP_TABLE);
		
		
		// 创建表
		db.execSQL(CsstSHFloorTable.GEN_CREATE_TABLE);
		db.execSQL(CsstSHModelTable.GEN_CREATE_TABLE);
		db.execSQL(CsstSHActionTable.GEN_CREATE_TABLE);
		db.execSQL(CsstSHRoomTable.GEN_CREATE_TABLE);
		db.execSQL(CsstSHRemoteControlTypeTable.GEN_CREATE_TABLE);
		db.execSQL(CsstSHRCTemplateKeyTable.GEN_CREATE_TABLE);
		db.execSQL(CsstSHDeviceRCKeyTable.GEN_CREATE_TABLE);
		db.execSQL(CsstSHDeviceTable.GEN_CREATE_TABLE);
		db.execSQL(CsstSHCameraTable.GEN_CREATE_TABLE);
		db.execSQL(CsstSHClockOpenTable.GEN_CREATE_TABLE);
		db.execSQL(CsstSHSafeSensorTable.GEN_CREATE_TABLE);
		db.execSQL(CsstSHCustomRCKeyTable.GEN_CREATE_TABLE);
		db.execSQL(CsstSHSwitchTable.GEN_CREATE_TABLE);
		db.execSQL(CsstSHSafeClockTable.GEN_CREATE_TABLE);
		//初始化数据库默认值
		initDataBase(context, db);
		
	}
	
	/**
	 * 初始化数据库默认值
	 * @param context
	 * @param db
	 */
	private static final void initDataBase(Context context, SQLiteDatabase db){
		CsstSHConfigPreference cp = new CsstSHConfigPreference(context, ICsstSHConstant.CsstSHPreference);
		//数据库已初始化返回
		if (cp.readBoolean(ICsstSHConstant.DATABASE_INIT, "false")){
			return;
		}
		//第一次初始化数据库
		//初始化遥控器模板
		CsstSHRemoteControlTypeTable.initRemoteControlType(context, db);
		cp.writeBoolean(ICsstSHConstant.DATABASE_INIT, true);
	}
	
	public final Context getContext(){
		return mContext;
	}

	/**
	 * 获取只读数据库
	 */
	public final SQLiteDatabase getReadDatabase(){
		return getReadableDatabase();
	}
	
	/**
	 * 获取只写数据库
	 */
	public final SQLiteDatabase getWritDatabase(){
		return getWritableDatabase();
	}
	
	/**
	 * 关闭数据库
	 */
	public final void closeDatabase(){
		close();
	}
	
}
