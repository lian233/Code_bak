package com.wofu.base.systemmanager;

import java.sql.Connection;
import java.sql.SQLException;

import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;

public class PublicUtils {

	public static String getConfig(Connection conn,String name,String defaultvalue) 
		throws JException
	{
		String result="";
		String sql="";
		try
		{			
			sql="select value from config with(nolock) where name='"+name+"'";
			result =SQLHelper.strSelect(conn, sql);
			if (result.equals("") || (result==null))
				result =defaultvalue;
			if (result.equals("") || (result==null))
				throw new JException("δ����["+name+"]");
		}catch(JSQLException jsqle)
		{
			throw new JException("ȡ���á�"+name+"������!"+sql);
		}
		return result;
	}
	
	public static void setConfig(Connection conn,String name,String value) 
		throws JException
	{
		String sql="";
		try
		{
			sql="update config set value='"+value+"' where name='"+name+"'";
			SQLHelper.executeSQL(conn, sql);
		}catch(SQLException jsqle)
		{
			throw new JException("�������á�"+name+"������!"+sql);
		}
	}
}
