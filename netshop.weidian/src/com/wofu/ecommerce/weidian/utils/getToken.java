package com.wofu.ecommerce.weidian.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;

import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.ecommerce.weidian.Params;
import com.wofu.common.tools.util.Formatter;

public class getToken
{
	/**
	 * 自用型应用获取Token
	 * 
	 * @throws JException
	 * @throws SQLException
	 **/
	private static JSONObject SQL_return_object;
	private static String access_token;

	public static String getToken_zy(Connection conn) throws ClientProtocolException,
			IOException, JException, SQLException, JSONException,
			ParseException
	{
		String tokenInfo = SQLHelper.strSelect(conn, "select value from config where name='微店Token信息'");
		SQL_return_object = new JSONObject(tokenInfo);
		access_token = SQL_return_object.optString("access_token");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long l = df.parse(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)).getTime() -
		df.parse(SQL_return_object.optString("save_time")).getTime();
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) /*- day * 24*/);
		if (hour > 20)
		{
			System.out.println("access_token已经使用了"+hour+"小时");
			String output = 
				"https://api.vdian.com/token?grant_type=client_credential" + "&appkey="
				+ Params.app_key + "&secret=" + Params.app_Secret;
			String result = Utils.sendbyget(output);
			JSONObject rsp_Object = null;
			try
			{
				rsp_Object = new JSONObject(result); //{"result":{"access_token":"c04b910b5924982ad7ba684cf80fe39b000155332a","expire_in":90000},"status":{"status_code":0,"status_reason":"success"}}
				JSONObject save_to_db =new JSONObject();
				save_to_db.put("access_token", rsp_Object.getJSONObject("result").getString("access_token"));
				save_to_db.put("save_time", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				SQLHelper.executeSQL(conn, "update config set value='"+save_to_db.toString()+"' where name='微店Token信息'");
				return rsp_Object.getJSONObject("result").getString("access_token");
			} catch (JSONException e)
			{
				try
				{
					return rsp_Object.getJSONObject("status").getString(
					"status_code")
					+ "   "
					+ rsp_Object.getJSONObject("status").getString(
					"status_reason");
				} catch (JSONException e1)
				{
					return "fail to get access token";
				}
			}

		}
		return access_token;

	}

	/** 服务型应用获取Token **/
	public String getToken_yy() throws ClientProtocolException, IOException,
			JException, JSONException
	{
		String output = Params.url + "token?grant_type=client_credential"
				+ "&appkey=" + Params.app_key + "&secret=" + Params.app_Secret;
		String result = Utils.sendbyget(output);
		/*
		 * { “status”:{”status_code”:0,”status_reason”:””}, “result”:
		 * {“access_token”:”ACCESS_TOKEN”,”expires_in”:7200} }
		 */
		JSONObject rsp_Object = new JSONObject(result);
		return rsp_Object.getJSONObject("result").getString("access_token");
	}

	private static Connection getConn()
	{
		String driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		//jdbc:sqlserver://jconnhgbuwgi3.sqlserver.rds.aliyuncs.com:3433;DatabaseName=erpbybmstock
		//String url = "jdbc:sqlserver://192.168.1.114;DatabaseName=ErpNWBMStock"; // 123为数据库名字
		String url = "jdbc:sqlserver://jconnhgbuwgi3.sqlserver.rds.aliyuncs.com:3433;DatabaseName=erpbybmstock"; // 123为数据库名字
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
