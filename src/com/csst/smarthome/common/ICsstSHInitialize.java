package com.csst.smarthome.common;

/***
 * 初始化回调
 * @author liuyang
 */
public interface ICsstSHInitialize {

	/**
	 * 初始化数据源
	 */
	void initDataSource();
	
	/**
	 * 初始化控件
	 */
	void initWidget();
	
	/**
	 * 初始化控件状态
	 */
	void initWidgetState();
	
	/**
	 * 初始化控件监听器
	 */
	void initWidgetListener();
	
	/**
	 * 控件添加监听器
	 */
	void addWidgetListener();
}
