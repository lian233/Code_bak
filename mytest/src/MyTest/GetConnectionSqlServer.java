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
		String dbURL = "jdbc:sqlserver://gzwolfsoft.oicp.net:1434;databasename=ErpNWBMStock"; // 1433�Ƕ˿ڣ�"USCSecondhandMarketDB"�����ݿ�����
		String userName = "login"; // �û���
		String userPwd = "disneyatyongjun"; // ����

		Connection con = null;
		try {

			Class.forName(driverName).newInstance();
		} catch (Exception ex) {
			System.out.println("��������ʧ��");
			ex.printStackTrace();
		}
		try {
			 PreparedStatement ps = null;
			con = DriverManager.getConnection(dbURL, userName, userPwd);
			System.out.println("�ɹ��������ݿ⣡");
			Statement stm=con.createStatement();
			String sql ="select * from ecs_timerpolicy where id='7'";
			ResultSet rs=stm.executeQuery(sql);
			
			while (rs.next()) {
				System.out.println(rs.getString("nextActive"));
				System.out.println(rs.getString("notes"));
			}
		 //������治��where����������������е�value�ֶ�
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