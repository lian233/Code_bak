package MyTest;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Lazy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fields="Barcode_ID,Product_ID,Merchant_ID,SKU,Barcode,AddTime,Color_ID,Size_ID,Operator,Del,OperationTime,Gross_Weight,Net_Weight";
		String tableName ="Bas_Barcode";
		
		String[] sql = fields.split(",");
		PrintStream out = System.out;
		//定义为输出TXT
    	try {
			System.setOut(new PrintStream("d:/懒而已.txt"));
		} catch (FileNotFoundException e) {
			System.setOut(out);
			System.out.println("文件输出错误");
		}
    	sqlName(tableName);
    	//上面上部分
		sql0(sql);
    	//上面下部分
		sql1(sql);
		//中间
		sql2(sql);
		//下面
		sql3(sql);
		
		System.setOut(out);
		System.out.println("成功");
	}
	
	private static void sqlName(String tableName) {
		System.out.println("StringBuilder strSql = new StringBuilder();");
		System.out.println("strSql.Append(\"insert into "+tableName+"(\");");
		
	}

	private static void sql0(String[] sql) {
		
			StringBuilder sqllink = new StringBuilder();
			for(int i=0;i<sql.length;i++)
			{
				if(1!=sql.length-i)
				{ 
					
					if((i+1)%5!=0){
						if((i+1)%5==1){
							sqllink.append("strSql.Append(\""+sql[i]+",");
						}else{
							sqllink.append(sql[i]+",");
						}
					}
					else{
						sqllink.append(sql[i]+",\");"+"\r\n");
					}
				}
				else
				{
					sqllink.append(sql[i]+")\");");
					
				}
				
			}
			System.out.println(sqllink);
			System.out.println("strSql.Append(\" values (\");");
		}
		

	private static void sql1(String[] sql) {
		StringBuilder sqllink = new StringBuilder();
		for(int i=0;i<sql.length;i++)
		{
			if(1!=sql.length-i)
			{ 
				
				if((i+1)%5!=0){
					if((i+1)%5==1){
						if(!sql[i].toLowerCase().contains("time")){
							sqllink.append("strSql.Append(\"@"+sql[i]+",");
						}
						else
						{
							sqllink.append("strSql.Append(\"getdate(),");
						}
					}else{
						if(!sql[i].toLowerCase().contains("time")){
							sqllink.append("@"+sql[i]+",");
						}
						else{
							sqllink.append("getdate(),");
						}
					}
				}
				else{
					if(!sql[i].toLowerCase().contains("time")){
						sqllink.append("@"+sql[i]+",\");"+"\r\n");
					}
					else{
						sqllink.append("getdate(),\");"+"\r\n");
					}
				}
			}
			else
			{
				if(!sql[i].toLowerCase().contains("time")){
					sqllink.append("@"+sql[i]+")\");");
				}
				else{
					sqllink.append("getdate())\");");
				}
				
			}
			
		}
		System.out.println(sqllink);
	}

	private static void sql2(String[] sql) {
		System.out.println();
		System.out.println("SqlParameter[] parameters = {");
		for(int i=0;i<sql.length;i++)
		{
			if(1!=sql.length-i)
			{
				if(!sql[i].toLowerCase().contains("time")){
				System.out.println("new SqlParameter(\"@"+sql[i]+"\", SqlDbType.VarChar,255),");
				}
			}else
			{
				if(!sql[i].toLowerCase().contains("time")){
				System.out.println("new SqlParameter(\"@"+sql[i]+"\", SqlDbType.VarChar,255)};");
				}
			}
			if((i+1)%5==0){
				System.out.println("");
			}
		}
		
	}
	private static void sql3(String[] sql) {
		System.out.println();
		int l=0;
		for(int i=0;i<sql.length;i++)
		{
			if(!sql[i].toLowerCase().contains("time")){
			System.out.println("parameters["+l+"].Value = model."+sql[i]+";");
			l++;
			}
			if((i+1)%5==0){
				System.out.println("");
			}
			
		}
		System.out.println("return  DbHelperSQL.ExecuteSql(strSql.ToString(), parameters);");
	}
}
