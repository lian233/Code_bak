package com.wofu.intf.test;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Lazy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fields="ShopPrice_ID,Merchant_ID,Shop_ID,Product_ID,Normal_Price,Price,ShopPrice_Memo";
		String tableName ="Bas_ShopPrice";
		
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
		Runtime runtime=Runtime.getRuntime();
		try{
		runtime.exec("cmd /c start d://懒而已.txt");
		}catch(Exception e){
		System.out.println("Error!");
		}
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
						sqllink.append("strSql.Append(\"@"+sql[i]+",");
					}else{
						sqllink.append("@"+sql[i]+",");
					}
				}
				else{
					sqllink.append("@"+sql[i]+",\");"+"\r\n");
				}
			}
			else
			{
				sqllink.append("@"+sql[i]+")\");");
				
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
				System.out.println("new SqlParameter(\"@"+sql[i]+"\", SqlDbType.VarChar,255),");
			}else
			{
				System.out.println("new SqlParameter(\"@"+sql[i]+"\", SqlDbType.VarChar,255)};");
			}
			if((i+1)%5==0){
				System.out.println("");
			}
		}
		
	}
	private static void sql3(String[] sql) {
		System.out.println();
		for(int i=0;i<sql.length;i++)
		{
			System.out.println("parameters["+i+"].Value = model."+sql[i]+";");
			
			if((i+1)%5==0){
				System.out.println("");
			}
		}
		System.out.println("return  DbHelperSQL.ExecuteSql(strSql.ToString(), parameters);");
	}
}
