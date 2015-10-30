package com.wofu.ecommerce.meilishuo.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.ecommerce.meilishuo.Params;
import com.wofu.ecommerce.meilishuo.util.GetToken2;
import com.wofu.ecommerce.meilishuo.util.Utils;
import com.wolf.common.tools.util.JException;

public class test_get_refund
{
	public static void main(String args[])
	{
		Connection connection=conn();
		try
		{
			
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Connection conn()
	{
		String driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String url = "jdbc:sqlserver://192.168.1.114;DatabaseName=ErpNWBMStock"; // 123为数据库名字
		String password = "disneyatyongjun";
		String user1 = "login";
		Connection conn = null;
		try
		{
			Class.forName(driverClassName);
		} catch (ClassNotFoundException ex)
		{
			System.out.println("加载错误！");
		}
		try
		{
			conn = DriverManager.getConnection(url, user1, password);
			System.out.println("成功");
		} catch (SQLException ex1)
		{
			ex1.printStackTrace();
			System.out.println("失败");
		}
		return conn;
	}
}
