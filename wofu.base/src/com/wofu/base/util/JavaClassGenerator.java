package com.wofu.base.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Properties;

import com.wofu.common.tools.util.StringUtil;

public class JavaClassGenerator extends BusinessObject {

	private String getName(String tablename) {  
		return tablename.substring(0, 1).toUpperCase() + tablename.substring(1);  
	};  

		
	private void doGenerate() throws Exception {
		String reqdata = this.getReqData();
		Properties prop = StringUtil.getIniProperties(reqdata);
		String tablename = prop.getProperty("tablename");
		String packagename = prop.getProperty("packagename");		

		Connection con = this.getDao().getConnection();
		Statement stmt = con.createStatement();
		System.out.println(tablename);
		String privatestr = "";
		String setgetstr = "\n";
		ResultSet rs = stmt.executeQuery("SELECT top 1 * FROM " + tablename+" with(nolock) ");
		ResultSetMetaData rowcol = rs.getMetaData();
		int rowi = rowcol.getColumnCount();
		boolean showblob = false;
		boolean showclob = false;
		boolean timestamp = false;
		String lables = "";
		String lablenum = "";

		for (int j = 1; j <= rowi; j++) {

			String Classname = rowcol.getColumnClassName(j).replaceAll("java.sql.", "").replaceAll("java.lang.", "")
							.replaceAll("java.math.", "");

			if (Classname.equalsIgnoreCase("Blob"))
				showblob = true;

			if (Classname.equalsIgnoreCase("Clob"))
				showclob = true;

			if (Classname.equalsIgnoreCase("Timestamp"))
			{
				timestamp = true;
				Classname="Date";
			}
			
			if (Classname.equalsIgnoreCase("Integer"))
				Classname="int";
			
			if (Classname.equalsIgnoreCase("BigDecimal"))
				Classname="double";
			
			String ColumnLabel = rowcol.getColumnLabel(j);
			String lowerColumnLabel = ColumnLabel.toLowerCase();
			lables += lowerColumnLabel + ",";
			lablenum += "?,";
			//privatestr += "\t/**\n\t* " + ColumnLabel + "\t"
			//+ rowcol.getColumnTypeName(j) + "("
			//+ rowcol.getColumnDisplaySize(j) + ")\n\t*/\n";

			privatestr += "\tprivate " + Classname + " " + lowerColumnLabel+ ";\n";
			setgetstr += "\tpublic " + Classname + " get"+ getName(lowerColumnLabel) + "() {\n\t\treturn "
			+ lowerColumnLabel + ";\t\n\t}\n";
			setgetstr += "\tpublic void set" + getName(lowerColumnLabel)
			+ "(" + Classname + " " + lowerColumnLabel
			+ ") {\n\t\tthis." + lowerColumnLabel + " = "
			+ lowerColumnLabel + ";\n\t}\n";

		}

		String Javastr = "package " + packagename + ";\n\n";

		if (showblob)
			Javastr += "import java.sql.Blob;\n";

		if (showclob)
			Javastr += "import java.sql.Clob;\n";

		if (timestamp)
			Javastr += "import java.util.Date;\n";

		//Javastr += "/**\n *" + lables + "\t" + lablenum
		//		+ "\n */\npublic class " + tablename + " {\n";
		Javastr +="public class " + tablename + " {\n";
		Javastr += privatestr + setgetstr + "}\n";
		rs.close();
		
		this.OutputStr(Javastr);
	}

	public void doTransaction(String action) throws Exception {
		if (action.equalsIgnoreCase("generate"))
				doGenerate();
	}

}
