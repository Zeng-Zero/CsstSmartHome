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
	 * @author liuyang
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context){
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mInfo = mConnectivityManager.getActiveNetworkInfo();
		if(mInfo != null){
			return mInfo.isAvailable();
		}
		return false;
	}
	
	/**
	 * wifi网络是否可用
	 * @author liuyang
	 * @param context
	 * @return
	 */
	public static final boolean isWifiConnected(Context context){
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(mInfo != null){
			return mInfo.isAvailable();
		}
		return false;
	}
	
	/**
	 * 手机网络是否可用
	 * @author liuyang
	 * @param context
	 * @return
	 */
	public static final boolean isMobileConnected(Context context){
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if(mInfo != null){
			return mInfo.isAvailable();
		}
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
