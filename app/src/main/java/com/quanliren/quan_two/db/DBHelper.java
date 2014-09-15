package com.quanliren.quan_two.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.quanliren.quan_two.bean.CacheBean;
import com.quanliren.quan_two.bean.ChatListBean;
import com.quanliren.quan_two.bean.CustomFilterBean;
import com.quanliren.quan_two.bean.DfMessage;
import com.quanliren.quan_two.bean.DongTaiBeanTable;
import com.quanliren.quan_two.bean.ExchangeRemindBean;
import com.quanliren.quan_two.bean.LoginUser;
import com.quanliren.quan_two.bean.MoreLoginUser;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.bean.UserTable;
import com.quanliren.quan_two.bean.VersionBean;
import com.quanliren.quan_two.bean.emoticon.EmoticonActivityListBean.EmoticonZip;
import com.quanliren.quan_two.util.LogUtil;

import java.sql.SQLException;
import java.util.List;

public class DBHelper extends OrmLiteSqliteOpenHelper{


	private static final String DATABASE_NAME = "toutou.db";
	private static final int DATABASE_VERSION = 5;
	
	private RuntimeExceptionDao<LoginUser,String> loginUserDao = null;
	private RuntimeExceptionDao<UserTable,String> userTableDao = null;
	private RuntimeExceptionDao<CacheBean,String> cacheBeanDao = null;
	private RuntimeExceptionDao<DfMessage,String> messageDao = null;
	
	
	public DBHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	/**
	 * 创建SQLite数据库
	 */
	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, LoginUser.class);
			TableUtils.createTable(connectionSource, UserTable.class);
			TableUtils.createTable(connectionSource, DfMessage.class);
			TableUtils.createTable(connectionSource, CacheBean.class);
			TableUtils.createTable(connectionSource, ChatListBean.class);
			TableUtils.createTable(connectionSource, CustomFilterBean.class);
			TableUtils.createTable(connectionSource, DongTaiBeanTable.class);
			TableUtils.createTable(connectionSource, MoreLoginUser.class);
			TableUtils.createTable(connectionSource, VersionBean.class);
			TableUtils.createTable(connectionSource, EmoticonZip.class);
			TableUtils.createTable(connectionSource, ExchangeRemindBean.class);
		} catch (SQLException e) {
			LogUtil.e(DBHelper.class.getName(), "Unable to create datbases");
		}
	}

	/**
	 * 更新SQLite数据库
	 */
	@Override
	public void onUpgrade(
			SQLiteDatabase sqliteDatabase, 
			ConnectionSource connectionSource, 
			int oldVer,
			int newVer) {
		try {
			TableUtils.dropTable(connectionSource, LoginUser.class, true);
			TableUtils.dropTable(connectionSource, UserTable.class, true);
			TableUtils.dropTable(connectionSource, DfMessage.class, true);
			TableUtils.dropTable(connectionSource, CacheBean.class, true);
			TableUtils.dropTable(connectionSource, ChatListBean.class, true);
			TableUtils.dropTable(connectionSource, CustomFilterBean.class, true);
			TableUtils.dropTable(connectionSource, DongTaiBeanTable.class, true);
			TableUtils.dropTable(connectionSource, MoreLoginUser.class, true);
			TableUtils.dropTable(connectionSource, VersionBean.class, true);
			TableUtils.dropTable(connectionSource, EmoticonZip.class, true);
			TableUtils.dropTable(connectionSource, ExchangeRemindBean.class, true);
			onCreate(sqliteDatabase, connectionSource);
		} catch (SQLException e) {
			LogUtil.e(DBHelper.class.getName(), 
					"Unable to upgrade database from version " + oldVer + " to new "
					+ newVer);
		}
	}
	
	public RuntimeExceptionDao<LoginUser, String> getLoginUserDao() {
		try {
			if(loginUserDao==null){
				loginUserDao=getRuntimeExceptionDao(LoginUser.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loginUserDao;
	}
	public RuntimeExceptionDao<DfMessage, String> getDfMessageDao() {
		try {
			if(messageDao==null){
				messageDao=getRuntimeExceptionDao(DfMessage.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return messageDao;
	}
	

	public RuntimeExceptionDao<UserTable, String> getUserTableDao() {
		try {
			if(userTableDao==null){
				userTableDao=getRuntimeExceptionDao(UserTable.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userTableDao;
	}

	public LoginUser getUser(){
		List<LoginUser> users=getLoginUserDao().queryForAll();
		if(users==null||users.size()==0){
			return null;
		}else{
			return users.get(0);
		}
	}
	public User getUserInfo(){
		LoginUser u=getUser();
		UserTable user=null;
		if(u!=null){
			user=getUserTableDao().queryForId(u.getId());
			if(user!=null){
				return user.getUser();
			}
		}
		return null;
	}
}
