package com.csst.smarthome.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHRCKeyBean;
import com.csst.smarthome.bean.CsstSHRCTypeBean;
import com.csst.smarthome.common.ICsstSHConstant;

/**
 * 遥控器类型表
 * @author liuyang
 */
public final class CsstSHRemoteControlTypeTable implements ICsstSHDaoManager<CsstSHRCTypeBean>, ICsstSHConstant{
	
	public static final String TAG = "CsstSHRemoteControlTypeTable";
	/** 表名 */
	public static final String GEN_TABLE_NAME = "sh_rct";
	/** 遥控器类型id */
	public static final String GEN_RCT_ID = "sh_rct_id";
	/** 遥控器类型名字 */
	public static final String GEN_RCT_NAME = "sh_rct_name";
	/** 遥控器类型页数*/
	public static final String GEN_RCT_PAGE = "sh_rct_page";
	
	/** 建表SQL语句 */
	public static String GEN_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + GEN_TABLE_NAME
			+ "(" + GEN_RCT_ID + " INTEGER PRIMARY KEY," 
			+ GEN_RCT_NAME + " TEXT," 
			+ GEN_RCT_PAGE + " TEXT" 
			+ ")";
	/** 删除表 */
	public static String GEN_DROP_TABLE = "DROP TABLE IF EXISTS " + CsstSHDeviceTable.GEN_TABLE_NAME;
	
	private static CsstSHRemoteControlTypeTable mInstance = null;
	
	private CsstSHRemoteControlTypeTable(){
		
	}
	
	public static final CsstSHRemoteControlTypeTable getInstance(){
		if (null == mInstance){
			mInstance = new CsstSHRemoteControlTypeTable();
		}
		return mInstance;
	}
	
	@Override
	public long insert(SQLiteDatabase db, CsstSHRCTypeBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		ContentValues cv = new ContentValues();
		cv.put(GEN_RCT_NAME, arg0.getRCTypeName());
		cv.put(GEN_RCT_PAGE, arg0.getRCTypePageCount());
		return db.insert(table, null, cv);
	}
	
	@Override
	public long update(SQLiteDatabase db, CsstSHRCTypeBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_RCT_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getRCTypeId())};
		ContentValues cv = new ContentValues();
		cv.put(GEN_RCT_NAME, arg0.getRCTypeName());
		cv.put(GEN_RCT_PAGE, arg0.getRCTypePageCount());
		return db.update(table, cv, whereClause, whereArgs);
	}
	
	@Override
	public long delete(SQLiteDatabase db, CsstSHRCTypeBean arg0)
			throws RuntimeException {
		String table = GEN_TABLE_NAME;
		String whereClause = GEN_RCT_ID + "=?";
		String[] whereArgs = {Integer.toString(arg0.getRCTypeId())};
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
	public List<CsstSHRCTypeBean> query(SQLiteDatabase db) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);
		List<CsstSHRCTypeBean> list = null;
		if (c != null && c.moveToFirst()){
			list = new ArrayList<CsstSHRCTypeBean>();
			int rCTypeId = 0;
			String rCTypeName = null;
			int rCTypePageCount = 0;
			do{
				rCTypeId = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_ID));
				rCTypeName = c.getString(c.getColumnIndexOrThrow(GEN_RCT_NAME));
				rCTypePageCount = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_PAGE));
				list.add(new CsstSHRCTypeBean(rCTypeId, rCTypeName, rCTypePageCount));
			}while(c.moveToNext());
		}
		return list;
	}
	
	@Override
	public CsstSHRCTypeBean query(SQLiteDatabase db, int id) {
		String table = GEN_TABLE_NAME;
		Cursor c = db.query(table, null, null, null, null, null, null);
		if (c != null && c.moveToFirst()){
			int rCTypeId = 0;
			String rCTypeName = null;
			int rCTypePageCount = 0;
			rCTypeId = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_ID));
			rCTypeName = c.getString(c.getColumnIndexOrThrow(GEN_RCT_NAME));
			rCTypePageCount = c.getInt(c.getColumnIndexOrThrow(GEN_RCT_PAGE));
			return new CsstSHRCTypeBean(rCTypeId, rCTypeName, rCTypePageCount);
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
	
	/**
	 * 初始化遥控器
	 * @param db
	 */
	public static final void initRemoteControlType(Context context, SQLiteDatabase db){
		try {
			CsstSHRemoteControlTypeTable crt = CsstSHRemoteControlTypeTable.getInstance();
			Resources r = context.getResources();
			String[] key = r.getStringArray(R.array.csst_remote_control_key);
			int[] value = r.getIntArray(R.array.csst_remote_control_value);
			int rCTypeId = 0;
			String rCTypeName = null;
			int rCTypePageCount = 1;
			db.beginTransaction();
			for (int i = 0; i < value.length; i++){
				rCTypeId = value[i];
				rCTypeName = key[i];
				crt.insert(db, new CsstSHRCTypeBean(rCTypeId, rCTypeName, rCTypePageCount));
				initRemoteControlTempletKey(context, db, rCTypeId);
			}
			db.setTransactionSuccessful();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	/**
	 * 初始化遥控器模板按键
	 * @param context
	 * @param db
	 * @param rcid
	 */
	public static final void initRemoteControlTempletKey(Context context, SQLiteDatabase db, int rcid){
		switch (rcid) {
				/** 电视类型 */
			case REMOTE_TYPE_1:
//				initTVTemplet(context, db, rcid);
				initTVTempletZQL(context, db, rcid);
				break;
				/** 空调类型 */
			case REMOTE_TYPE_2:
//				initAirConditionTemplet(context, db, rcid);
				initAirConditionTempletZQL(context, db, rcid);
				break;
				/** 机顶盒类型 */
			case REMOTE_TYPE_3:
//				initSTBTemplet(context, db, rcid);
				initSTBTempletZQL(context, db, rcid);
				break;
				/** DVD类型 */
			case REMOTE_TYPE_4:
				initDVDTempletZQL(context, db, rcid);
				break;
				/** 音响 */
			case REMOTE_TYPE_5:
//				initLightTemplet(context, db, rcid);
				initSoundTemplet(context, db, rcid);
				break;
				/** 窗帘类型 */
			case REMOTE_TYPE_6:
				initCurtainTemplet(context, db, rcid);
				break;
				/** 风扇类型 */
			case REMOTE_TYPE_7:
				initElectricFanTemplet(context, db, rcid);
				break;
		}
	}
	

	/**
	 * 电视遥控器按键模板
	 * @param context
	 * @param db
	 * @param rcid
	 */
	private static final void initTVTempletZQL(Context context, SQLiteDatabase db, int rcid){
		Resources res = context.getResources();
		CsstSHRCTemplateKeyTable rckt = CsstSHRCTemplateKeyTable.getInstance();
		int rCTypeId = rcid;
		String rCKeyName = res.getString(R.string.tv_power_rckey);
		int rCKeySize = 1; 
		int rCKeyX = 0;
		int rCKeyY = 0;
		int rCKeyW = 0;
		int rCKeyH = 0;
		int rCKeyIcon = 0;
		int rCKeyPage = 1;
		int rCKeyIdentify = TV_POWER_RCKEY_IDENTIFY;
		// 电源按键
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
	
		// 静音按键
		rCKeyName = res.getString(R.string.tv_mute_rckey);
		rCKeyIdentify = TV_MUTE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 信号按键
		rCKeyName = res.getString(R.string.tv_signal_rckey);
		rCKeyIdentify = TV_SIGNAL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// 声音按键
//		rCKeyName = res.getString(R.string.tv_sound_rckey);
//		rCKeyIdentify = TV_SOUND_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
//		 +键
		rCKeyName = res.getString(R.string.tv_add_rckey);
		rCKeyIdentify = TV_ADD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// 图像按键
//		rCKeyName = res.getString(R.string.tv_image_rckey);
//		rCKeyIdentify = TV_IMAGE_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//		 -键
		rCKeyName = res.getString(R.string.tv_dec_rckey);
		rCKeyIdentify = TV_DEC_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		
		// 菜单按键
		rCKeyName = res.getString(R.string.tv_menu_rckey);
		rCKeyIdentify = TV_MENU_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
//		// FAV按键
//		rCKeyName = res.getString(R.string.tv_fav_rckey);
//		rCKeyIdentify = TV_FAV_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// CH+按键
		rCKeyName = res.getString(R.string.tv_chadd_rckey);
		rCKeyIdentify = TV_CHADD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// CH-按键
		rCKeyName = res.getString(R.string.tv_chdel_rckey);
		rCKeyIdentify = TV_CHDEL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// VOL+按键
		rCKeyName = res.getString(R.string.tv_voladd_rckey);
		rCKeyIdentify = TV_VOLADD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// VOL-按键
		rCKeyName = res.getString(R.string.tv_voldel_rckey);
		rCKeyIdentify = TV_VOLDEL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// OK按键
		rCKeyName = res.getString(R.string.tv_ok_rckey);
		rCKeyIdentify = TV_OK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// EPG按键
//		rCKeyName = res.getString(R.string.tv_epg_rckey);
//		rCKeyIdentify = TV_EPG_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// STOP按键
		rCKeyName = res.getString(R.string.tv_stop_rckey);
		rCKeyIdentify = TV_STOP_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));


		// EXIT按键
		rCKeyName = res.getString(R.string.tv_exit_rckey);
		rCKeyIdentify = TV_EXIT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
	}
	
	
	
	
	/**
	 * 电视遥控器按键模板
	 * @param context
	 * @param db
	 * @param rcid
	 */
	private static final void initTVTemplet(Context context, SQLiteDatabase db, int rcid){
		Resources res = context.getResources();
		CsstSHRCTemplateKeyTable rckt = CsstSHRCTemplateKeyTable.getInstance();
		int rCTypeId = rcid;
		String rCKeyName = res.getString(R.string.tv_power_rckey);
		int rCKeySize = 1; 
		int rCKeyX = 0;
		int rCKeyY = 0;
		int rCKeyW = 0;
		int rCKeyH = 0;
		int rCKeyIcon = 0;
		int rCKeyPage = 1;
		int rCKeyIdentify = TV_POWER_RCKEY_IDENTIFY;
		// 电源按键
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
	
		// 静音按键
		rCKeyName = res.getString(R.string.tv_mute_rckey);
		rCKeyIdentify = TV_MUTE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 信号按键
		rCKeyName = res.getString(R.string.tv_signal_rckey);
		rCKeyIdentify = TV_SIGNAL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// 声音按键
		rCKeyName = res.getString(R.string.tv_sound_rckey);
		rCKeyIdentify = TV_SOUND_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// 图像按键
		rCKeyName = res.getString(R.string.tv_image_rckey);
		rCKeyIdentify = TV_IMAGE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// 菜单按键
		rCKeyName = res.getString(R.string.tv_menu_rckey);
		rCKeyIdentify = TV_MENU_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// FAV按键
		rCKeyName = res.getString(R.string.tv_fav_rckey);
		rCKeyIdentify = TV_FAV_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// CH+按键
		rCKeyName = res.getString(R.string.tv_chadd_rckey);
		rCKeyIdentify = TV_CHADD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// CH-按键
		rCKeyName = res.getString(R.string.tv_chdel_rckey);
		rCKeyIdentify = TV_CHDEL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// VOL+按键
		rCKeyName = res.getString(R.string.tv_voladd_rckey);
		rCKeyIdentify = TV_VOLADD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// VOL-按键
		rCKeyName = res.getString(R.string.tv_voldel_rckey);
		rCKeyIdentify = TV_VOLDEL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// OK按键
		rCKeyName = res.getString(R.string.tv_ok_rckey);
		rCKeyIdentify = TV_OK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		// EPG按键
		rCKeyName = res.getString(R.string.tv_epg_rckey);
		rCKeyIdentify = TV_EPG_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));

		// EXIT按键
		rCKeyName = res.getString(R.string.tv_exit_rckey);
		rCKeyIdentify = TV_EXIT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
	}
	
	
	/**
	 * 初始化空调遥控器模板
	 * @param context
	 * @param db
	 * @param rcid
	 */
	private static final void initAirConditionTempletZQL(Context context, SQLiteDatabase db, int rcid){
		Resources res = context.getResources();
		CsstSHRCTemplateKeyTable rckt = CsstSHRCTemplateKeyTable.getInstance();
		int rCTypeId = rcid;
		String rCKeyName = res.getString(R.string.ac_power_rckey);
		int rCKeySize = 1; 
		int rCKeyX = 0;
		int rCKeyY = 0;
		int rCKeyW = 0;
		int rCKeyH = 0;
		int rCKeyIcon = 0;
		int rCKeyPage = 1;
		int rCKeyIdentify = AC_POWER_RCKEY_IDENTIFY;
		//开关按键
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//升温
		rCKeyName = res.getString(R.string.ac_addt_rckey);
		rCKeyIdentify = AC_ADDT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//降温
		rCKeyName = res.getString(R.string.ac_delt_rckey);
		rCKeyIdentify = AC_DELT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//风速
		rCKeyName = res.getString(R.string.ac_wind_rckey);
		rCKeyIdentify = AC_WIND_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//模式
		rCKeyName = res.getString(R.string.ac_mode_rckey);
		rCKeyIdentify = AC_MODE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//左右风
		rCKeyName = res.getString(R.string.ac_lrwind_rckey);
		rCKeyIdentify = AC_LRWIND_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//上下风
		rCKeyName = res.getString(R.string.ac_tbwind_rckey);
		rCKeyIdentify = AC_TBWIND_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
	}
	
	
	
	
	
	/**
	 * 初始化空调遥控器模板
	 * @param context
	 * @param db
	 * @param rcid
	 */
	private static final void initAirConditionTemplet(Context context, SQLiteDatabase db, int rcid){
		Resources res = context.getResources();
		CsstSHRCTemplateKeyTable rckt = CsstSHRCTemplateKeyTable.getInstance();
		int rCTypeId = rcid;
		String rCKeyName = res.getString(R.string.ac_power_rckey);
		int rCKeySize = 1; 
		int rCKeyX = 0;
		int rCKeyY = 0;
		int rCKeyW = 0;
		int rCKeyH = 0;
		int rCKeyIcon = 0;
		int rCKeyPage = 1;
		int rCKeyIdentify = AC_POWER_RCKEY_IDENTIFY;
		//开关按键
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//升温
		rCKeyName = res.getString(R.string.ac_addt_rckey);
		rCKeyIdentify = AC_ADDT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//降温
		rCKeyName = res.getString(R.string.ac_delt_rckey);
		rCKeyIdentify = AC_DELT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//风速
		rCKeyName = res.getString(R.string.ac_wind_rckey);
		rCKeyIdentify = AC_WIND_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//制冷
		rCKeyName = res.getString(R.string.ac_cold_rckey);
		rCKeyIdentify = AC_COLD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//制热
		rCKeyName = res.getString(R.string.ac_hot_rckey);
		rCKeyIdentify = AC_HOT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//左右风
		rCKeyName = res.getString(R.string.ac_lrwind_rckey);
		rCKeyIdentify = AC_LRWIND_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//上下风
		rCKeyName = res.getString(R.string.ac_tbwind_rckey);
		rCKeyIdentify = AC_TBWIND_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
	}
	
	
	
	/**
	 * 初始化机顶盒模板
	 * @param context
	 * @param db
	 * @param rcid
	 */
	private static final void initSTBTempletZQL(Context context, SQLiteDatabase db, int rcid){
		Resources res = context.getResources();
		CsstSHRCTemplateKeyTable rckt = CsstSHRCTemplateKeyTable.getInstance();
		int rCTypeId = rcid;
		String rCKeyName = null;
		int rCKeySize = 1; 
		int rCKeyX = 0;
		int rCKeyY = 0;
		int rCKeyW = 0;
		int rCKeyH = 0;
		int rCKeyIcon = 0;
		int rCKeyPage = 1;
		int rCKeyIdentify = -1;
		
		// 静音按键
		rCKeyName = res.getString(R.string.tv_mute_rckey);
		rCKeyIdentify = STB_MUTE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 电源按键
		rCKeyName = res.getString(R.string.tv_power_rckey);
		rCKeyIdentify = STB_POWER_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//		// 设置按键
//		rCKeyName = res.getString(R.string.csst_stbrc_setting_key);
//		rCKeyIdentify = STB_SETTING_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 声道按键
		rCKeyName = res.getString(R.string.csst_stbrc_audio_key);
		rCKeyIdentify = STB_TRACK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// VOL+按键
		rCKeyName = res.getString(R.string.tv_voladd_rckey);
		rCKeyIdentify = STB_VOLADD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// VOL-按键
		rCKeyName = res.getString(R.string.tv_voldel_rckey);
		rCKeyIdentify = STB_VOLDEL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//		// ITV按键
//		rCKeyName = res.getString(R.string.csst_stbrc_itv_key);
//		rCKeyIdentify = STB_ITV_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// TV/AV按键
		rCKeyName = res.getString(R.string.csst_stbrc_tvav_key);
		rCKeyIdentify = STB_TVAV_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 上一页按键
		rCKeyName = res.getString(R.string.csst_stbrc_lastpage_key);
		rCKeyIdentify = STB_PREPAGE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 导航按键
//		rCKeyName = res.getString(R.string.csst_stbrc_guide_key);
//		rCKeyIdentify = STB_NAVIGATION_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 下一页按键
		rCKeyName = res.getString(R.string.csst_stbrc_nextpage_key);
		rCKeyIdentify = STB_NEXTPAGE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 主页按键
		rCKeyName = res.getString(R.string.csst_stbrc_homepage_key);
		rCKeyIdentify = STB_HOME_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 信息按键
//		rCKeyName = res.getString(R.string.csst_stbrc_info_key);
//		rCKeyIdentify = STB_INFO_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// CH+按键
		rCKeyName = res.getString(R.string.tv_chadd_rckey);
		rCKeyIdentify = STB_CHADD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// CH-按键
		rCKeyName = res.getString(R.string.tv_chdel_rckey);
		rCKeyIdentify = STB_CHDEL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// OK按键
		rCKeyName = res.getString(R.string.tv_ok_rckey);
		rCKeyIdentify = STB_OK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 返回按键
		rCKeyName = res.getString(R.string.csst_stbrc_back_key);
		rCKeyIdentify = STB_BACK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// EXIT按键
//		rCKeyName = res.getString(R.string.csst_stbrc_exit_key);
//		rCKeyIdentify = STB_EXIT_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		//第二页
		rCKeyPage = 2;
		// 快退按键
		rCKeyName = res.getString(R.string.stb_fr_rckey);
		rCKeyIdentify = STB_FR_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 停止按键
		rCKeyName = res.getString(R.string.stb_stop_rckey);
		rCKeyIdentify = STB_RECORD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 快进按键
		rCKeyName = res.getString(R.string.stb_ff_rckey);
		rCKeyIdentify = STB_FF_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 确定按键
		rCKeyName = res.getString(R.string.csst_dvdrc_ok_key);
		rCKeyIdentify = STB_DONE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		
		
		// ADD
		rCKeyName = res.getString(R.string.csst_stbrc_add_key);
		rCKeyIdentify = STB_ADD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		
		// STOP
		rCKeyName = res.getString(R.string.csst_stbrc_stop_key);
		rCKeyIdentify = STB_STOP_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
			
		
		// DEC
		rCKeyName = res.getString(R.string.csst_stbrc_dec_key);
		rCKeyIdentify = STB_DEC_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
			
				
		
		
//		// 0按键
//		rCKeyName = res.getString(R.string.csst_rc_0);
//		rCKeyIdentify = STB_0_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//		// 1按键
//		rCKeyName = res.getString(R.string.csst_rc_1);
//		rCKeyIdentify = STB_1_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//		// 2按键
//		rCKeyName = res.getString(R.string.csst_rc_2);
//		rCKeyIdentify = STB_2_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//		// 3按键
//		rCKeyName = res.getString(R.string.csst_rc_3);
//		rCKeyIdentify = STB_3_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//		// 4按键
//		rCKeyName = res.getString(R.string.csst_rc_4);
//		rCKeyIdentify = STB_4_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//		// 5按键
//		rCKeyName = res.getString(R.string.csst_rc_5);
//		rCKeyIdentify = STB_5_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//		// 6按键
//		rCKeyName = res.getString(R.string.csst_rc_6);
//		rCKeyIdentify = STB_6_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//		// 7按键
//		rCKeyName = res.getString(R.string.csst_rc_7);
//		rCKeyIdentify = STB_7_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//		// 8按键
//		rCKeyName = res.getString(R.string.csst_rc_8);
//		rCKeyIdentify = STB_8_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//		// 9按键
//		rCKeyName = res.getString(R.string.csst_rc_9);
//		rCKeyIdentify = STB_9_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//		// 确定按键
//		rCKeyName = res.getString(R.string.csst_dvdrc_ok_key);
//		rCKeyIdentify = STB_DONE_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
//		// 删除按键
//		rCKeyName = res.getString(R.string.csst_dvdrc_delete_key);
//		rCKeyIdentify = STB_DEL_RCKEY_IDENTIFY;
//		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
	}
	
	
	
	/**
	 * 初始化机顶盒模板
	 * @param context
	 * @param db
	 * @param rcid
	 */
	private static final void initSTBTemplet(Context context, SQLiteDatabase db, int rcid){
		Resources res = context.getResources();
		CsstSHRCTemplateKeyTable rckt = CsstSHRCTemplateKeyTable.getInstance();
		int rCTypeId = rcid;
		String rCKeyName = null;
		int rCKeySize = 1; 
		int rCKeyX = 0;
		int rCKeyY = 0;
		int rCKeyW = 0;
		int rCKeyH = 0;
		int rCKeyIcon = 0;
		int rCKeyPage = 1;
		int rCKeyIdentify = -1;
		
		// 静音按键
		rCKeyName = res.getString(R.string.tv_mute_rckey);
		rCKeyIdentify = STB_MUTE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 电源按键
		rCKeyName = res.getString(R.string.tv_power_rckey);
		rCKeyIdentify = STB_POWER_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 设置按键
		rCKeyName = res.getString(R.string.csst_stbrc_setting_key);
		rCKeyIdentify = STB_SETTING_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 声道按键
		rCKeyName = res.getString(R.string.csst_stbrc_audio_key);
		rCKeyIdentify = STB_TRACK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// VOL+按键
		rCKeyName = res.getString(R.string.tv_voladd_rckey);
		rCKeyIdentify = STB_VOLADD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// VOL-按键
		rCKeyName = res.getString(R.string.tv_voldel_rckey);
		rCKeyIdentify = STB_VOLDEL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// ITV按键
		rCKeyName = res.getString(R.string.csst_stbrc_itv_key);
		rCKeyIdentify = STB_ITV_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// TV/AV按键
		rCKeyName = res.getString(R.string.csst_stbrc_tvav_key);
		rCKeyIdentify = STB_TVAV_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 上一页按键
		rCKeyName = res.getString(R.string.csst_stbrc_lastpage_key);
		rCKeyIdentify = STB_PREPAGE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 导航按键
		rCKeyName = res.getString(R.string.csst_stbrc_guide_key);
		rCKeyIdentify = STB_NAVIGATION_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 下一页按键
		rCKeyName = res.getString(R.string.csst_stbrc_nextpage_key);
		rCKeyIdentify = STB_NEXTPAGE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 主页按键
		rCKeyName = res.getString(R.string.csst_stbrc_homepage_key);
		rCKeyIdentify = STB_HOME_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 信息按键
		rCKeyName = res.getString(R.string.csst_stbrc_info_key);
		rCKeyIdentify = STB_INFO_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// CH+按键
		rCKeyName = res.getString(R.string.tv_chadd_rckey);
		rCKeyIdentify = STB_CHADD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// CH-按键
		rCKeyName = res.getString(R.string.tv_chdel_rckey);
		rCKeyIdentify = STB_CHDEL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// OK按键
		rCKeyName = res.getString(R.string.tv_ok_rckey);
		rCKeyIdentify = STB_OK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 返回按键
		rCKeyName = res.getString(R.string.csst_stbrc_back_key);
		rCKeyIdentify = STB_BACK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// EXIT按键
		rCKeyName = res.getString(R.string.csst_stbrc_exit_key);
		rCKeyIdentify = STB_EXIT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		//第二页
		rCKeyPage = 2;
		// 快退按键
		rCKeyName = res.getString(R.string.stb_fr_rckey);
		rCKeyIdentify = STB_FR_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 停止按键
		rCKeyName = res.getString(R.string.stb_stop_rckey);
		rCKeyIdentify = STB_RECORD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 快进按键
		rCKeyName = res.getString(R.string.stb_ff_rckey);
		rCKeyIdentify = STB_FF_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 0按键
		rCKeyName = res.getString(R.string.csst_rc_0);
		rCKeyIdentify = STB_0_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 1按键
		rCKeyName = res.getString(R.string.csst_rc_1);
		rCKeyIdentify = STB_1_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 2按键
		rCKeyName = res.getString(R.string.csst_rc_2);
		rCKeyIdentify = STB_2_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 3按键
		rCKeyName = res.getString(R.string.csst_rc_3);
		rCKeyIdentify = STB_3_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 4按键
		rCKeyName = res.getString(R.string.csst_rc_4);
		rCKeyIdentify = STB_4_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 5按键
		rCKeyName = res.getString(R.string.csst_rc_5);
		rCKeyIdentify = STB_5_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 6按键
		rCKeyName = res.getString(R.string.csst_rc_6);
		rCKeyIdentify = STB_6_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 7按键
		rCKeyName = res.getString(R.string.csst_rc_7);
		rCKeyIdentify = STB_7_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 8按键
		rCKeyName = res.getString(R.string.csst_rc_8);
		rCKeyIdentify = STB_8_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 9按键
		rCKeyName = res.getString(R.string.csst_rc_9);
		rCKeyIdentify = STB_9_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 确定按键
		rCKeyName = res.getString(R.string.csst_dvdrc_ok_key);
		rCKeyIdentify = STB_DONE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 删除按键
		rCKeyName = res.getString(R.string.csst_dvdrc_delete_key);
		rCKeyIdentify = STB_DEL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
	}
	
	
	
	/**
	 * 初始化DVD模板
	 * @param context
	 * @param db
	 * @param rcid
	 */
	private static final void initDVDTempletZQL(Context context, SQLiteDatabase db, int rcid){
		Resources res = context.getResources();
		CsstSHRCTemplateKeyTable rckt = CsstSHRCTemplateKeyTable.getInstance();
		int rCTypeId = rcid;
		String rCKeyName = null;
		int rCKeySize = 1; 
		int rCKeyX = 0;
		int rCKeyY = 0;
		int rCKeyW = 0;
		int rCKeyH = 0;
		int rCKeyIcon = 0;
		int rCKeyPage = 1;
		int rCKeyIdentify = -1;
		
		// 静音按键
		rCKeyName = res.getString(R.string.csst_dvdzql_mute_key);
		rCKeyIdentify = DVDZQL_MUTE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 电源按键
		rCKeyName = res.getString(R.string.csst_dvdzql_power_key);
		rCKeyIdentify = DVDZQL_POWER_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 左声道按键
		rCKeyName = res.getString(R.string.csst_dvdzql_soundchanel_key);
		rCKeyIdentify = DVDZQL_SOUNDCHANEL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 右声道按键
		rCKeyName = res.getString(R.string.csst_dvdzql_uppage_key);
		rCKeyIdentify = DVDZQL_UPPAGE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 立体声按键
		rCKeyName = res.getString(R.string.csst_dvdzql_downpage_key);
		rCKeyIdentify = DVDZQL_DOWNPAGE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 画面按键
		rCKeyName = res.getString(R.string.csst_dvdzql_stop_key);
		rCKeyIdentify = DVDZQL_STOP_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 播放按键
		rCKeyName = res.getString(R.string.csst_dvdzql_next_key);
		rCKeyIdentify = DVDZQL_NEXT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 暂停按键
		rCKeyName = res.getString(R.string.csst_dvdzql_last_key);
		rCKeyIdentify = DVDZQL_LAST_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 停止按键
		rCKeyName = res.getString(R.string.csst_dvdzql_puase_key);
		rCKeyIdentify = DVDZQL_PAUSE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 回放按键
		rCKeyName = res.getString(R.string.csst_dvdzql_up_key);
		rCKeyIdentify = DVDZQL_UP_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 上一首按键
		rCKeyName = res.getString(R.string.csst_dvdzql_down_key);
		rCKeyIdentify = DVDZQL_DOWN_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 快退按键
		rCKeyName = res.getString(R.string.csst_dvdzql_left_key);
		rCKeyIdentify = DVDZQL_LEFT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 快进按键
		rCKeyName = res.getString(R.string.csst_dvdzql_right_key);
		rCKeyIdentify = DVDZQL_RIGHT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 下一首按键
		rCKeyName = res.getString(R.string.csst_dvdzql_OK_key);
		rCKeyIdentify = DVDZQL_OK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 目录按键
		rCKeyName = res.getString(R.string.csst_dvdzql_setting_key);
		rCKeyIdentify = DVDZQL_SETTING_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 弹出按键
		// 目录按键
		rCKeyName = res.getString(R.string.csst_dvdzql_menu_key);
		rCKeyIdentify = DVDZQL_MENU_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 弹出按键
		rCKeyName = res.getString(R.string.csst_dvdzql_back_key);
		rCKeyIdentify = DVDZQL_BACK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		}
	
	
	/**
	 * 初始化DVD模板
	 * @param context
	 * @param db
	 * @param rcid
	 */
	private static final void initSoundTemplet(Context context, SQLiteDatabase db, int rcid){
		Resources res = context.getResources();
		CsstSHRCTemplateKeyTable rckt = CsstSHRCTemplateKeyTable.getInstance();
		int rCTypeId = rcid;
		String rCKeyName = null;
		int rCKeySize = 1; 
		int rCKeyX = 0;
		int rCKeyY = 0;
		int rCKeyW = 0;
		int rCKeyH = 0;
		int rCKeyIcon = 0;
		int rCKeyPage = 1;
		int rCKeyIdentify = -1;
		
		// 静音按键
		rCKeyName = res.getString(R.string.csst_sound_mute_key);
		rCKeyIdentify = SOUND_MUTE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 电源按键
		rCKeyName = res.getString(R.string.csst_sound_power_key);
		rCKeyIdentify = SOUND_POWER_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 左声道按键
		rCKeyName = res.getString(R.string.csst_sound_soundchanel_key);
		rCKeyIdentify = SOUND_SOUNDCHANEL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 右声道按键
		rCKeyName = res.getString(R.string.csst_sound_uppage_key);
		rCKeyIdentify = SOUND_UPPAGE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 立体声按键
		rCKeyName = res.getString(R.string.csst_sound_downpage_key);
		rCKeyIdentify = SOUND_DOWNPAGE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 画面按键
		rCKeyName = res.getString(R.string.csst_sound_stop_key);
		rCKeyIdentify = SOUND_STOP_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 播放按键
		rCKeyName = res.getString(R.string.csst_sound_next_key);
		rCKeyIdentify = SOUND_NEXT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 暂停按键
		rCKeyName = res.getString(R.string.csst_sound_last_key);
		rCKeyIdentify = SOUND_LAST_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 停止按键
		rCKeyName = res.getString(R.string.csst_sound_puase_key);
		rCKeyIdentify = SOUND_PAUSE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 回放按键
		rCKeyName = res.getString(R.string.csst_sound_up_key);
		rCKeyIdentify = SOUND_UP_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 上一首按键
		rCKeyName = res.getString(R.string.csst_sound_down_key);
		rCKeyIdentify = SOUND_DOWN_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 快退按键
		rCKeyName = res.getString(R.string.csst_sound_left_key);
		rCKeyIdentify = SOUND_LEFT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 快进按键
		rCKeyName = res.getString(R.string.csst_sound_right_key);
		rCKeyIdentify = SOUND_RIGHT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 下一首按键
		rCKeyName = res.getString(R.string.csst_sound_OK_key);
		rCKeyIdentify = SOUND_OK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 目录按键
		rCKeyName = res.getString(R.string.csst_sound_setting_key);
		rCKeyIdentify = SOUND_SETTING_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 弹出按键
		// 目录按键
		rCKeyName = res.getString(R.string.csst_sound_menu_key);
		rCKeyIdentify = SOUND_MENU_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 弹出按键
		rCKeyName = res.getString(R.string.csst_sound_back_key);
		rCKeyIdentify = SOUND_BACK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		
		// 目录按键
		rCKeyName = res.getString(R.string.csst_sound_add_key);
		rCKeyIdentify = SOUND_ADD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 弹出按键
		rCKeyName = res.getString(R.string.csst_sound_dec_key);
		rCKeyIdentify = SOUND_DEC_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 初始化DVD模板
	 * @param context
	 * @param db
	 * @param rcid
	 */
	private static final void initDVDTemplet(Context context, SQLiteDatabase db, int rcid){
		Resources res = context.getResources();
		CsstSHRCTemplateKeyTable rckt = CsstSHRCTemplateKeyTable.getInstance();
		int rCTypeId = rcid;
		String rCKeyName = null;
		int rCKeySize = 1; 
		int rCKeyX = 0;
		int rCKeyY = 0;
		int rCKeyW = 0;
		int rCKeyH = 0;
		int rCKeyIcon = 0;
		int rCKeyPage = 1;
		int rCKeyIdentify = -1;
		
		// 静音按键
		rCKeyName = res.getString(R.string.tv_mute_rckey);
		rCKeyIdentify = DVD_MUTE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 电源按键
		rCKeyName = res.getString(R.string.tv_power_rckey);
		rCKeyIdentify = DVD_POWER_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 左声道按键
		rCKeyName = res.getString(R.string.csst_dvdrc_leftaudio_key);
		rCKeyIdentify = DVD_LTRACK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 右声道按键
		rCKeyName = res.getString(R.string.csst_dvdrc_rightaudio_key);
		rCKeyIdentify = DVD_RTRACK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 立体声按键
		rCKeyName = res.getString(R.string.csst_dvdrc_audio_key);
		rCKeyIdentify = DVD_RTRACK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 画面按键
		rCKeyName = res.getString(R.string.csst_dvdrc_img_key);
		rCKeyIdentify = DVD_IMAGE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 播放按键
		rCKeyName = res.getString(R.string.csst_dvdrc_play_key);
		rCKeyIdentify = DVD_PLAY_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 暂停按键
		rCKeyName = res.getString(R.string.csst_dvdrc_pause_key);
		rCKeyIdentify = DVD_PAUSE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 停止按键
		rCKeyName = res.getString(R.string.csst_dvdrc_stop_key);
		rCKeyIdentify = DVD_STOP_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 回放按键
		rCKeyName = res.getString(R.string.csst_dvdrc_replay_key);
		rCKeyIdentify = DVD_RPLAY_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 上一首按键
		rCKeyName = res.getString(R.string.csst_dvdrc_last_key);
		rCKeyIdentify = DVD_PREV_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 快退按键
		rCKeyName = res.getString(R.string.csst_dvdrc_rev_key);
		rCKeyIdentify = DVD_REVERSE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 快进按键
		rCKeyName = res.getString(R.string.csst_dvdrc_fwd_key);
		rCKeyIdentify = DVD_FORWARD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 下一首按键
		rCKeyName = res.getString(R.string.csst_dvdrc_next_key);
		rCKeyIdentify = DVD_NEXT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 目录按键
		rCKeyName = res.getString(R.string.csst_dvdrc_menu_key);
		rCKeyIdentify = DVD_LIST_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 弹出按键
		rCKeyName = res.getString(R.string.csst_dvdrc_popup_key);
		rCKeyIdentify = DVD_POP_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 返回按键
		rCKeyName = res.getString(R.string.csst_stbrc_back_key);
		rCKeyIdentify = DVD_BACK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 退出按键
		rCKeyName = res.getString(R.string.csst_stbrc_exit_key);
		rCKeyIdentify = DVD_EXIT_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// VOL+按键
		rCKeyName = res.getString(R.string.tv_voladd_rckey);
		rCKeyIdentify = DVD_VOLADD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// VOL-按键
		rCKeyName = res.getString(R.string.tv_voldel_rckey);
		rCKeyIdentify = DVD_VOLDEL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// CH+按键
		rCKeyName = res.getString(R.string.tv_chadd_rckey);
		rCKeyIdentify = DVD_CHADD_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// CH-按键
		rCKeyName = res.getString(R.string.tv_chdel_rckey);
		rCKeyIdentify = DVD_CHDEL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// OK按键
		rCKeyName = res.getString(R.string.tv_ok_rckey);
		rCKeyIdentify = DVD_OK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		
		//第二页
		rCKeyPage = 2;
		// 快退按键
		rCKeyName = res.getString(R.string.stb_fr_rckey);
		rCKeyIdentify = DVD_FR_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 停止按键
		rCKeyName = res.getString(R.string.stb_stop_rckey);
		rCKeyIdentify = DVD_STOP_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 快进按键
		rCKeyName = res.getString(R.string.stb_ff_rckey);
		rCKeyIdentify = DVD_FF_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 0按键
		rCKeyName = res.getString(R.string.csst_rc_0);
		rCKeyIdentify = DVD_0_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 1按键
		rCKeyName = res.getString(R.string.csst_rc_1);
		rCKeyIdentify = DVD_1_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 2按键
		rCKeyName = res.getString(R.string.csst_rc_2);
		rCKeyIdentify = DVD_2_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 3按键
		rCKeyName = res.getString(R.string.csst_rc_3);
		rCKeyIdentify = DVD_3_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 4按键
		rCKeyName = res.getString(R.string.csst_rc_4);
		rCKeyIdentify = DVD_4_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 5按键
		rCKeyName = res.getString(R.string.csst_rc_5);
		rCKeyIdentify = DVD_5_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 6按键
		rCKeyName = res.getString(R.string.csst_rc_6);
		rCKeyIdentify = DVD_6_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 7按键
		rCKeyName = res.getString(R.string.csst_rc_7);
		rCKeyIdentify = DVD_7_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 8按键
		rCKeyName = res.getString(R.string.csst_rc_8);
		rCKeyIdentify = DVD_8_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 9按键
		rCKeyName = res.getString(R.string.csst_rc_9);
		rCKeyIdentify = DVD_9_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 确定按键
		rCKeyName = res.getString(R.string.csst_dvdrc_ok_key);
		rCKeyIdentify = DVD_DONE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		// 删除按键
		rCKeyName = res.getString(R.string.csst_dvdrc_delete_key);
		rCKeyIdentify = DVD_DEL_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));

	}
	
	/**
	 * 初始化灯光模板
	 * @param context
	 * @param db
	 * @param rcid
	 */
	private static final void initLightTemplet(Context context, SQLiteDatabase db, int rcid){
		Resources res = context.getResources();
		CsstSHRCTemplateKeyTable rckt = CsstSHRCTemplateKeyTable.getInstance();
		int rCTypeId = rcid;
		String rCKeyName = res.getString(R.string.light_power_rckey);
		int rCKeySize = 1; 
		int rCKeyX = 0;
		int rCKeyY = 0;
		int rCKeyW = 0;
		int rCKeyH = 0;
		int rCKeyIcon = 0;
		int rCKeyPage = 1;
		int rCKeyIdentify = LIGHT_POWER_RCKEY_IDENTIFY;
		//开关按键
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
	}
	
	

	
	
	
	/**
	 * 初始化窗帘模板
	 * @param context
	 * @param db
	 * @param rcid
	 */
	private static final void initCurtainTemplet(Context context, SQLiteDatabase db, int rcid){
		Resources res = context.getResources();
		CsstSHRCTemplateKeyTable rckt = CsstSHRCTemplateKeyTable.getInstance();
		int rCTypeId = rcid;
		String rCKeyName = res.getString(R.string.csst_curtainrc_open_key);
		int rCKeySize = 1; 
		int rCKeyX = 0;
		int rCKeyY = 0;
		int rCKeyW = 0;
		int rCKeyH = 0;
		int rCKeyIcon = 0;
		int rCKeyPage = 1;
		int rCKeyIdentify = CURTAIN_OPEN_RCKEY_IDENTIFY;
		//打开
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//暂停
		rCKeyName = res.getString(R.string.csst_curtainrc_pause_key);
		rCKeyIdentify = CURTAIN_PAUSE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//关闭
		rCKeyName = res.getString(R.string.csst_curtainrc_close_key);
		rCKeyIdentify = CURTAIN_CLOSE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
	}
	
	/**
	 * 初始化风扇模板
	 * @param context
	 * @param db
	 * @param rcid
	 */
	private static final void initElectricFanTemplet(Context context, SQLiteDatabase db, int rcid){
		Resources res = context.getResources();
		CsstSHRCTemplateKeyTable rckt = CsstSHRCTemplateKeyTable.getInstance();
		int rCTypeId = rcid;
		String rCKeyName = res.getString(R.string.csst_electricfan_menu_1);
		int rCKeySize = 1; 
		int rCKeyX = 0;
		int rCKeyY = 0;
		int rCKeyW = 0;
		int rCKeyH = 0;
		int rCKeyIcon = 0;
		int rCKeyPage = 1;
		int rCKeyIdentify = FAN_WIND_RCKEY_IDENTIFY;
		//风量
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//摇头
		rCKeyName = res.getString(R.string.csst_electricfan_menu_2);
		rCKeyIdentify = FAN_SHAKE_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//定时、预约
		rCKeyName = res.getString(R.string.csst_electricfan_menu_3);
		rCKeyIdentify = FAN_CLOCK_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
		//开关
		rCKeyName = res.getString(R.string.csst_electricfan_menu_4);
		rCKeyIdentify = FAN_POWER_RCKEY_IDENTIFY;
		rckt.insert(db, new CsstSHRCKeyBean(rCTypeId, rCKeyName, rCKeySize, rCKeyX, rCKeyY, rCKeyW, rCKeyH, rCKeyIcon, rCKeyPage, rCKeyIdentify));
	}
	
	
}
