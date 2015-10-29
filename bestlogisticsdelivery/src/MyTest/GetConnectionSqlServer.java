package MyTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class GetConnectionSqlServer {
	public void getConnectionSqlServer() {

		String driverName = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
		String dbURL = "jdbc:sqlserver://gzwolfsoft.oicp.net:1434;databasename=ErpNWBMStock"; // 1433是端口，"USCSecondhandMarketDB"是数据库名称
		String userName = "login"; // 用户名
		String userPwd = "disneyatyongjun"; // 密码

		Connection con = null;
		try {

			Class.forName(driverName).newInstance();
		} catch (Exception ex) {
			System.out.println("驱动加载失败");
			ex.printStackTrace();
		}
		try {
			 PreparedStatement ps = null;
			con = DriverManager.getConnection(dbURL, userName, userPwd);
			System.out.println("成功连接数据库！");
			Statement stm=con.createStatement();
			String sql ="select * from ecs_timerpolicy where id='7'";
			ResultSet rs=stm.executeQuery(sql);
			
			while (rs.next()) {
				System.out.println(rs.getString("nextActive"));
				System.out.println(rs.getString("notes"));
			}
		 //如果后面不跟where条件，则更新所有列的value字段
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}