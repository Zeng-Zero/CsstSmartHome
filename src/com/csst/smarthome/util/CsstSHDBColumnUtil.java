package com.csst.smarthome.util;

import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;

import com.csst.smarthome.R;
import com.csst.smarthome.common.ICsstSHConstant;

public final class CsstSHDBColumnUtil implements ICsstSHConstant {
	
	private static final Map<String, Integer> remote = new HashMap<String, Integer>();

	private static CsstSHDBColumnUtil instance = null;
	
	private CsstSHDBColumnUtil(){
		remote.clear();
		Resources r = Resources.getSystem();
		String[] key = r.getStringArray(R.array.csst_remote_control_key);
		int[] value = r.getIntArray(R.array.csst_remote_control_value);
		for (int i = 0; i < key.length; i++){
			remote.put(key[i], value[i]);
		}
	}
	
	public static final CsstSHDBColumnUtil getInstance(){
		if (null == instance){
			instance = new CsstSHDBColumnUtil();
		}
		return instance;
	}
	
	/**
	 * buffer转换成存储信息
	 * @param buffer
	 * @return
	 */
	public static final String bufferToMessge(byte[] buffer) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buffer.length; i++) {
			if (i == (buffer.length - 1)) {
				sb.append(String.format("0x%02x", buffer[i]));
			} else {
				sb.append(String.format("0x%02x, ", buffer[i]));
			}
		}
		return sb.toString();
	}
	
	/**
	 * 将boolean型数据转换成掺入数据库中的数据
	 * @param inPara
	 * @return
	 */
	public static final int dataBaseBooleanIn(boolean inPara){
		return inPara ? DAOTRUE : DAOFALSE;
	}
	
	/**
	 * 数据库boolean值输出
	 * @param outPara
	 * @return
	 */
	public static final boolean dataBaseBooleanOut(int outPara){
		return (outPara == DAOTRUE);
	}
	
	/**
	 * 根据遥控器名字查询遥控器类型id
	 * @param remoteControl
	 * @return
	 */
	public final int getRemoteControlTypeId(String remoteControl){
		if (!remote.containsKey(remoteControl)){
			return -1;
		}
		return remote.get(remoteControl);
	}
	
	/**
	 * 根据遥控器Id查询遥控器名字
	 * @param rcId
	 * @return
	 */
	public final String getRemoteControlTypeName(int rcId){
		if (!remote.containsValue(rcId)){
			return null;
		}
		Resources r = Resources.getSystem();
		String[] key = r.getStringArray(R.array.csst_remote_control_key);
		int[] value = r.getIntArray(R.array.csst_remote_control_value);
		for (int i = 0; i < value.length; i++){
			if (value[i] == rcId){
				return key[i];
			}
		}
		return null;
	}
}
