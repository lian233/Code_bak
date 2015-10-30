package com.wofu.ecommerce.meilishuo2.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.ecommerce.meilishuo2.Params;
import com.wofu.ecommerce.meilishuo2.util.GetToken2;
import com.wofu.ecommerce.meilishuo2.util.Utils;
import com.wolf.common.tools.util.JException;

public class test_get_refund
{
	public static void main(String args[])
	{
		Connection connection=conn();
		try
		{
			GetToken2 getToken2=new GetToken2(null);
			JSONObject object=new JSONObject(PublicUtils.getConfig(connection, "美丽说Token信息2", ""));
			String result = Utils.sendbyget(Params.url, Params.appKey, Params.appsecret, "meilishuo.aftersales.list.get", object.optString("access_token"), new Date(), null, null, "", "", "", "");
			JSONObject object_result = new JSONObject(result);
			System.out.println(object_result.getJSONObject("aftersales_list_get_response").getInt("total_num"));
			JSONArray info = object_result.getJSONObject("aftersales_list_get_response").getJSONArray("info");
			System.out.println(info.toString());
			System.out.println(result);
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
