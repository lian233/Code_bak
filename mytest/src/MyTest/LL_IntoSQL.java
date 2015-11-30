package MyTest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;

public class LL_IntoSQL extends Thread 
{
	public static void main(String[] args) throws Exception
	{
		InputStream is = new FileInputStream("D:/aaa.txt");
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		Connection conn=null;
		String str = null;
		String sql = "";
		conn= getConnection();
		StringBuilder SqlTid = new StringBuilder();
		int id=1;
		conn.setAutoCommit(false);
		System.out.println("开始");
		while ((str = br.readLine()) != null)
		{
			if(!str.equals("12240412416974"))
			{
				SqlTid.append("''");
				SqlTid.append(str);
				SqlTid.append("'',");
			}else{
				SqlTid.append("''");
				SqlTid.append(str);
				SqlTid.append("''");
			}

		}
		System.out.println("拼接完成，准备写入数据库");
		sql = "UPDATE ecs_timerpolicy SET params='sellernick= 莱利百货欣贺网络供应商 ;tableName=eco_rds_fx_trade;tid ="+SqlTid+"' where id ='122' AND lastActive='2015-11-28 12:02:39.977'";
		SQLHelper.executeSQL(conn, sql);
		System.out.println(SqlTid);
		System.out.println("完成");
		conn.setAutoCommit(true);
	}
	
	public static Connection getConnection(){
		
		
		try {
			String driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
			String url="jdbc:sqlserver://112.124.33.196:30003;DatabaseName=erpemnbmstock;user=login;password=disneyatyongjun";
			Class.forName(driver);
			return DriverManager.getConnection(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}  