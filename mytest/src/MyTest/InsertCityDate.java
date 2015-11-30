package MyTest;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;

public class InsertCityDate {

	/**
	 * 插入城市数据到数据库
	 */
	public static void main(String[] args) {
		insertIntoCityCode();
		/**
		Connection conn= getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setAutoCommit(false);
			System.out.println("ss");
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}**/

	}
	/**
	 * 生成城市代码
	 */
	public static void insertIntoCityCode(){
		Connection conn=null;
		Scanner sc = null;
		try {
			long currentTime = System.currentTimeMillis();
			File file = new File("E:\\程序资料\\软件备份\\苏宁建表\\city244.java");
			sc = new Scanner(file);
			conn= getConnection();
			JSONObject obj=null;
			PreparedStatement pstm = conn.prepareStatement("insert into sn_citycode(id,regionCode,regionName,provinceCode,provinceName,cityCode,cityName,districtCode,districtName) values(?,?,?,?,?,?,?,?,?)");;
			int i=1;
			//System.out.println(strs);
			
			while(sc.hasNextLine()){
				String strr= sc.nextLine();
				System.out.println(strr);
				obj=new JSONObject(strr.substring(0,strr.length()-1));
				pstm.setInt(1, i++);
				pstm.setString(2, (String)obj.get("regionCode"));
				pstm.setString(3, (String)obj.get("regionName"));
				pstm.setString(4, (String)obj.get("provinceCode"));
				pstm.setString(5, (String)obj.get("provinceName"));
				pstm.setString(6, (String)obj.get("cityCode"));
				pstm.setString(7, (String)obj.get("cityName"));
				pstm.setString(8, (String)obj.get("districtCode"));
				pstm.setString(9, (String)obj.get("districtName"));
				pstm.addBatch();
				if(i%300==0) pstm.executeBatch();
			}
			pstm.executeBatch();
			Log.info("花费时间: "+(System.currentTimeMillis()-currentTime));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				sc.close();
		}
		
	}
	
	/**
	 * 物流公司信息表
	 */
	public void insertIntoExpressInfo(){
		try {
			//FileInputStream fis = new FileInputStream("c:\\city244.java");
			File file = new File("c:\\city244.java");
			Scanner sc = new Scanner(file);
			//InputStreamReader isr = new InputStreamReader(fis, "utf-8");
			//BufferedReader   br = new BufferedReader(isr);
			//String strs=br.readLine();
			Connection conn= getConnection();
			String str=null;
			JSONObject obj=null;
			PreparedStatement pstm = conn.prepareStatement("insert into sn_citycode(id,regioncode,regionname,privincecode,privincename,citycode,cityname,districtcode,districtname) values(1,'001','华东','100','江苏','000001000175','镇江市','00000002','润州区')");;
			int i=1;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection(){
		
		
		try {
			String driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
			String url="jdbc:sqlserver://121.199.172.205:30003;DatabaseName=erpztbmstock;user=wolf;password=ASDF23wert12";
			Class.forName(driver);
			return DriverManager.getConnection(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static void closeConnection(Connection  conn){
		try{
			conn.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
