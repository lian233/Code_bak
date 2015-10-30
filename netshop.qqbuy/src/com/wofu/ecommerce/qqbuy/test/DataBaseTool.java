package com.wofu.ecommerce.qqbuy.test;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseTool
{
	private static Connection conn;

	private static ResultSet rs;

	private static Statement stmt;

	private static String url;

	// private static String user;
	//
	// private static String psw;
	//
	// public static String DBName;


	public static Connection conDB()
	{
		// DBName = DataBaseName;
		// user = User;
		// psw = PassWord;
		try
		{
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			//System.out.println("加载驱动成功！！") ;
		}
		catch( Exception e )
		{
			// TODO: handle exception
			System.out.println("加载驱动出错！！") ;
			e.printStackTrace() ;
			return  conn ;
		}

		try
		{
			url = "jdbc:jtds:sqlserver://192.168.1.20:1433/" + "ErpNSBMStock";
			// user = "sa" ;
			// psw = "sa" ;

			conn = DriverManager.getConnection(url, "login", "disneyatyongjun");
			
		     //System.out.println("取得连接成功！！") ;
		}
		catch( Exception e )
		{
			// TODO: handle exception
			System.out.println("取得连接失败！！") ;
			e.printStackTrace() ;
		}
		return conn ;
	}


	public void closeDB()
	{
		try
		{
			stmt.close();
			conn.close();

		}
		catch( SQLException e )
		{

		}
	}

	public void executeUpdate(String updateSql)
	{
		try
		{
			stmt.executeUpdate(updateSql);
		}
		catch( SQLException e )
		{

		}
	}


	public ResultSet executeQuery(String querySql)
	{
		try
		{
			rs = stmt.executeQuery(querySql);
		}
		catch( Exception e )
		{

		}
		return rs;
	}


	public String test()
	{
		String test = "test bean success!!";
		return test;
	}
	public static void close(ResultSet rs, Statement pstmt, Connection conn) {
		try {
			if (rs != null)
				rs.close();

			if (pstmt != null)
				pstmt.close();

			if (conn != null)
				conn.close();
			System.out.println("Database disconnect success!!!!");
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void close(Statement pstmt, Connection conn) {
		try {
			if (pstmt != null)
				pstmt.close();

			if (conn != null)
				conn.close();

			//System.out.println("Database disconnect success!!!!!");
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void close(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
			System.out.println("Database disconnect success!!!!!");
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void close(Statement pstmt) {
		try {
			if (pstmt != null)
				pstmt.close();
			System.out.println("Database disconnect success!!!!!");
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void close(Connection conn) {
		try {
			if (conn != null)
				conn.close();
			System.out.println("Database disconnect success!!!!!");
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
