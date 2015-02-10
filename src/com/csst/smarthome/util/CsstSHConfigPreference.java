package com.csst.smarthome.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.csst.smarthome.common.ICsstSHConstant;

/**
 * SharedPreferences存储工具类
 * @author liuyang
 */
public final class CsstSHConfigPreference {
	private Context mo_context = null;
	
	private SharedPreferences mo_sharedPreferences = null;
	
	private SharedPreferences.Editor mo_editor = null;
	
	public CsstSHConfigPreference(final Context context, final String preferenceName) {
		mo_context = context;
		mo_sharedPreferences = mo_context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		mo_editor = mo_sharedPreferences.edit();
	}
	
	/**
	 * 删除所有配置项信息
	 * @return
	 */
	public boolean clear() {
		mo_editor.clear();
		return mo_editor.commit(); 
	}
	
	/**
	 * 删除指定配置项信息
	 * @param key 键
	 * @return
	 */
	public boolean remove(final String key) {
		mo_editor.remove(key);
		return mo_editor.commit(); 
	}
	
	/**
	 * 保存字符串
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public boolean writeString(final String key, final String value) {
		mo_editor.putString(key, value);
		return mo_editor.commit();
	}
	
	/**
	 * 取出字符串
	 * @param key 键
	 * @param defValue 默认值
	 * @return
	 */
	public String readString(final String key, final String defValue) {
		return mo_sharedPreferences.getString(key, defValue).trim();
	}
	
	/**
	 * 保存Integer
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public boolean writeInteger(final String key, final int value) {
		return writeString(key, ""+value);
	}
	
	/**
	 * 取出Integer
	 * @param key 键
	 * @return	
	 */
	public Integer readInteger(final String key) {
		return Integer.valueOf(readString(key,"0"));
	}
	
	/**
	 * 取出Integer
	 * @param key
	 * @param defValue
	 * @return
	 */
	public Integer readInteger(final String key, int defValue) {
		return Integer.valueOf(readString(key, "" + defValue));
	}
	
	/**
	 * 保存Boolean
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public boolean writeBoolean(final String key, final Boolean value) {
		return writeString(key, ""+value);
	}
	
	
	/* 存储楼层id
	 * @param floorid
	 * @return
	 */
	public final boolean setAlarmFlag( final Boolean value) {
		return writeBoolean(ICsstSHConstant.SAFE_ALARM_FLAG, value);
	}
	
	/**
	 * 获取当前楼层id
	 * @return
	 */
	public final boolean getAlarmFlag(){
		return readBoolean(ICsstSHConstant.SAFE_ALARM_FLAG);
	}
	
	
	/**
	 * 获取当前楼层id
	 * @return
	 */
	public final boolean getAutoAlarmFlag(){
		return readBoolean(ICsstSHConstant.SAFE_AUTOALARM_FLAG);
	}
	/**
	 * 存储楼层id
	 * @param floorid
	 * @return
	 */
	public final boolean setAutoAlarmFlag(final Boolean value) {
		return writeBoolean(ICsstSHConstant.SAFE_AUTOALARM_FLAG, value);
	}
	
	
	
	
	
	
	/**
	 * 取出Boolean
	 * @param key 键
	 * @return
	 */
	public Boolean readBoolean(final String key) {
		return Boolean.valueOf(readString(key, "false"));
	}
	
	/**
	 * 取出Boolean
	 * @param key 键
	 * @return
	 */
	public Boolean readBoolean(final String key, final String value) {
		return Boolean.valueOf(readString(key, value));
	}
	
	/**
	 * 保存Float
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public boolean writeFloat(final String key, final Float value) {
		return writeString(key, ""+value);
	}
	
	/**
	 * 取出Float
	 * @param key 键
	 * @return
	 */
	public Float readFloat(final String key) {
		return Float.valueOf(readString(key, "0"));
	}
	
	/**
	 * 保存Long
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public boolean writeLong(final String key, final Long value) {
		return writeString(key, ""+value);
	}
	
	/**
	 * 取出Long
	 * @param key 键
	 * @return
	 */
	public Long readLong(final String key) {
		return Long.valueOf(readString(key, "0"));
	}
	
	/**
	 * 获取当前楼层id
	 * @return
	 */
	public final Integer getFloorId(){
		return readInteger(ICsstSHConstant.CUR_FLOOR_ID_KEY, -1);
	}
	
	/**
	 * 存储楼层id
	 * @param floorid
	 * @return
	 */
	public final boolean setFloorId(int floorid){
		return writeInteger(ICsstSHConstant.CUR_FLOOR_ID_KEY, floorid);
	}
	
	/**
	 * 写入MAC地址在配置完成之后
	 * @return
	 */
	public final boolean setMacAdress(String macadress){
		return writeString(ICsstSHConstant.CONTROL_MAC_ADDR, macadress);
	}
	
	/**
	 * 读取主控MAC地址
	 * @return
	 */
	public final String getMacAdress(){
		return readString(ICsstSHConstant.CONTROL_MAC_ADDR,ICsstSHConstant.CONTROL_MAC_ADDR_DEFAUL);
	}
	
	
	
	/**
	 * 当前楼层存储的房间id
	 * @return
	 */
	public final Integer getRoomId(){
		//当前楼层id
		int floorid = readInteger(ICsstSHConstant.CUR_FLOOR_ID_KEY, -1);
		//当前楼层存储的房间idkey
		String key = ICsstSHConstant.CUR_FLOOR_ROOM_KEY + "_" + floorid;
		//当前楼层存储的房间id
		return readInteger(key, -1);
	}
	
	/**
	 * 保存当前的
	 * @param roomid
	 * @return
	 */
	public final boolean setRoomId(int roomid){
		// 当前楼层id
		int floorid = readInteger(ICsstSHConstant.CUR_FLOOR_ID_KEY, -1);
		// 当前楼层存储的房间idkey
		String key = ICsstSHConstant.CUR_FLOOR_ROOM_KEY + "_" + floorid;
		return writeInteger(key, roomid);
	}
	
}
