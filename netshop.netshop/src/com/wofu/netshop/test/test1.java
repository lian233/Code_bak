package com.wofu.netshop.test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import com.wofu.common.pool.TinyPool;
import com.wofu.netshop.common.GetConfig;
import com.wofu.netshop.taobao.Params;

public class test1 {

	/**
	 * @param args
	 */
	private static String encoding="gbk";
	private static String driverName="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static String password="disneyatyongjun";
	private static String url="jdbc:sqlserver://127.0.0.1;DatabaseName=ErpBYBMStock";
	private static String user="login";
	
	public static void main(String[] args) throws Exception{
		//getConfig(String config ,int orgid,Connection conn)
		Connection conn=null;
		TinyPool pool=null;
		loadDriver();
		try{
			pool = new TinyPool();
			pool.setDriver(driverName);
			pool.setEncoding(encoding);
			pool.setInitSize(1);
			pool.setMaxConnection(2);
			pool.setMaxPool(1);
			pool.setName("test");
			pool.setPassword(password);
			pool.setUrl(url);
			pool.setUser(user);
			pool.init();
			conn =pool.getConnection();
			System.out.println(conn);
			String sql ="url;appkey;appsecret;token;refreshtoken";
			GetConfig.init(sql,conn,101);
			System.out.println(Params.appkey);
			System.out.println("test");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(conn!=null) conn.close();
			if(pool!=null) pool.release();
		}
		
		
		

	}
	
	private static void loadDriver() throws Exception{
		Driver driver = (Driver)Class.forName(driverName).newInstance();
		DriverManager.registerDriver(driver);
	}

}
