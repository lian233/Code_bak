package com.wofu.base.util;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.tools.util.JException;

public class BusiUtil {
	
	public static boolean BusiExists(DataCentre dc,String tablename,int busid) throws Exception
	{
		boolean isexists=false;
		String sql="select keyname from ecs_idlist with(nolock) where tablename='"+tablename+"'";
		
		String keyname=dc.strSelect(sql);
		
		if (keyname.equals(""))
			throw new JException("table:["+tablename+"] not key config");
		
		sql="select count(*) from "+tablename+" with(nolock) where "+keyname+"="+busid;
		
		if (dc.intSelect(sql)>0) isexists=true;
		
		return isexists;
	}
	
	public static boolean BusiExists(DataCentre dc,BusinessClass obj,int busid) throws Exception
	{
		boolean isexists=false;
		String tablename=obj.getClass().getSimpleName();
		
		String sql="select keyname from ecs_idlist with(nolock) where tablename='"+tablename+"'";
		
		String keyname=dc.strSelect(sql);
		
		if (keyname.equals(""))
			throw new JException("table:["+tablename+"] not key config");
		
		sql="select count(*) from "+tablename+" with(nolock) where "+keyname+"="+busid;
		
		if (dc.intSelect(sql)>0) isexists=true;
		
		return isexists;
	}

}
