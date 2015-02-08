package com.csst.smarthome.dao;

import java.util.List;

import com.csst.smarthome.bean.CsstSHModelBean;

import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库操作接口
 * @author liuyang
 * @param <T> 操作类型
 */
public interface ICsstSHDaoManager<T> {

	/**
	 * 插入新数据
	 * @param db
	 * @param arg0
	 * @return
	 * @throws RuntimeException
	 */
	long insert(final SQLiteDatabase db, final T arg0) throws RuntimeException;
	
	/**
	 * 修改新数据
	 * @param db
	 * @param arg0
	 * @return
	 * @throws RuntimeException
	 */
	long update(final SQLiteDatabase db, final T arg0) throws RuntimeException;
	
	/**
	 * 删除信息数据
	 * @param db
	 * @param arg0
	 * @return
	 * @throws RuntimeException
	 */
	long delete(final SQLiteDatabase db, final T arg0) throws RuntimeException;
	
	/**
	 * 插入的列内容是否在列中已经存在
	 * @param db
	 * @param columnsName
	 * @param columnsValue
	 * @return
	 */
	boolean columnExists(final SQLiteDatabase db, final String columnsName, final Object columnsValue);
	
	/**
	 * 查询全部
	 * @param db
	 * @return
	 */
	List<T> query(final SQLiteDatabase db);
	
	/**
	 * 根据ID查询
	 * @param db
	 * @param id
	 * @return
	 */
	T query(final SQLiteDatabase db,final int id);
	
	/**
	 * 当前表中的记录数
	 * @param db
	 * @return
	 */
	int countRecord(final SQLiteDatabase db);

	
}
