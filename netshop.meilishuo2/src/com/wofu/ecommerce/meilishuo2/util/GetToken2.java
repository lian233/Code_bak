package com.wofu.ecommerce.meilishuo2.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wolf.common.tools.util.Formatter;
import com.wolf.common.tools.util.JException;


public class GetToken2
{
	public static String rt="";
	public String access_token;
	public String refresh_token;
	private JSONObject SQL_return_object;
	public GetToken2(Connection conn) throws SQLException, ParseException, JException, JSONException
	{
		Connection connection;
		if(conn==null) connection=getConn();
		else		   connection=conn;
		File file=new File("refresh_token");
		String refresh_token = null;
		if(!file.exists())
		{
			try 
			{
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else
		{
			BufferedReader reader;
			try 
			{
				reader = new BufferedReader(new FileReader(file));
				StringBuffer buffer=new StringBuffer();
				String temp;
				while((temp=reader.readLine())!=null)
				{
					buffer.append(temp);
				}
				//refresh_token=buffer.toString();
				PreparedStatement preparedStatement = connection.prepareStatement("select Value from config where name='美丽说Token信息2'");
				ResultSet rs = preparedStatement.executeQuery();
				while(rs.next())
				{
					SQL_return_object=new JSONObject(rs.getString("Value"));
					refresh_token=SQL_return_object.optString("refresh_token",buffer.toString());
					access_token =SQL_return_object.optString("access_token",buffer.toString());
					System.out.println(access_token);
					if(refresh_token!=null || !refresh_token.equals("")) break;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		/**如果现在的时间比数据库的最后获取数据的时间还大20小时，就重新获取一次，否则还是用原来的**/
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long l=df.parse(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)).getTime() - 
			   df.parse(SQL_return_object.optString("save_time")).getTime();
		long day=l/(24*60*60*1000);
		long hour=(l/(60*60*1000)-day*24);
		if(hour>20)
		{
			Map<String, String> param = new HashMap<String, String>();
			param.put("grant_type", "refresh_token");
			param.put("refresh_token", refresh_token);
			param.put("client_id", "MJ186861556156");
			param.put("client_secret", "041c9877e8eaedf774fd611f7909edb0");
			//param.put("state", state);
			String responseJson=Utils.sendByPost2(param,"http://oauth.open.meilishuo.com/authorize/token");
			System.out.println(responseJson);
			try {
				JSONObject object=new JSONObject(responseJson);
				access_token=object.getJSONObject("data").getString("access_token");
				System.out.println("access_token:  "+access_token);
				refresh_token=object.getJSONObject("data").getString("refresh_token");
				System.out.println("refresh_token:  "+refresh_token);
				OutputStream outputStream=new FileOutputStream(file);
				outputStream.write(object.getJSONObject("data").getString("refresh_token").getBytes());
			
				
				JSONObject save_to_db =new JSONObject();
				save_to_db.put("access_token", access_token);
				save_to_db.put("refresh_token", refresh_token);
				save_to_db.put("save_time", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Config SET Value = '" + save_to_db.toString() + "'  WHERE name='美丽说Token信息2'");
				preparedStatement.execute();

			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	public String get_access_token()
	{
		return access_token;
	}
	
	public String get_refresh_token()
	{
		return refresh_token;
	}
	
	public static Connection getConn()
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
