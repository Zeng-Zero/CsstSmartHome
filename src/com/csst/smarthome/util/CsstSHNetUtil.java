package com.csst.smarthome.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络相关的工具
 * @author liuyang
 */
public final class CsstSHNetUtil {
	
	/**
	 * 当前网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean CheckNetwork(Context context){
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mInfo = mConnectivityManager.getActiveNetworkInfo();
		if(mInfo != null){
			return mInfo.isConnected();
		}
		return false;
	}
}
