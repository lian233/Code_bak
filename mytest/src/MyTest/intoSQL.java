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

public class intoSQL extends Thread 
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
		int id=1;
		conn.setAutoCommit(false);
		while ((str = br.readLine()) != null)
		{
			String a[] = str.split(";");
			System.out.println(a[0]);
			System.out.println(a[1]);
			System.out.println("--------------------·Ö¸ô----------------");
			sql = "insert country_code (id,country,code)VALUES('"+id+"','"+a[1]+"','"+a[0]+"')";
			SQLHelper.executeSQL(conn, sql);
			id++;
		}
		System.out.println("Íê³É");
		conn.setAutoCommit(true);
	}
	
	public static Connection getConnection(){
		
		
		try {
			String driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
			String url="jdbc:sqlserver://121.41.172.91:30003;DatabaseName=erpfawmsstock;user=wolf;password=disneyatyongjun";
			Class.forName(driver);
			return DriverManager.getConnection(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}  