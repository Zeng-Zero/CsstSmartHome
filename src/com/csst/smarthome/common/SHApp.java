package com.csst.smarthome.common;

import java.io.File;

import vstc2.nativecaller.NativeCaller;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.csst.smarthome.camera.BridgeService;
import com.lishate.encryption.Encryption;

/**
 * 程序Application
 * @author liuyang
 */
public class SHApp extends Application implements ICsstSHConstant {
	public static final String TAG = "SHApp";

	private static SHApp mInstance = null;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		new Init().start();
	}
	
	private final class Init extends Thread{
		@Override
		public void run() {
			// 创建程序目录
			createAppPath();
			// 初始化通信协议
			Encryption.init();
			// 初始化Camera参数
			initCamersPara();
		}
	}
	
	/**
	 * 初始化摄像机参数
	 */
	private final void initCamersPara(){
		Intent intent = new Intent();
		intent.setClass(this, BridgeService.class);
		startService(intent);
		NativeCaller.PPPPInitial("ABC");//初始化默认服务器,如无定制服务器则不需要修改 
		NativeCaller.PPPPNetworkDetect();
	}
	
	private final void createAppPath(){
		//设备图片文件目录
		File file = new File(DEVICE_ICON_PATH);
		if (!file.exists()){
			file.mkdirs();
		}
	}

	public static Context getAppContext() {
		return mInstance;
	}
}
