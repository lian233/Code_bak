package com.wofu.base.interfacemanager;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.base.util.*;

public class IntfHelper {
	
	public static void setInterfaceSheetList(DataCentre dc,int orgid,int busid,int bustype) throws Exception
	{
		String sql="select a.interfaceid from ecs_interface a,ecs_interfacebustype b "
				+"where a.interfacetype=b.interfacetype and a.orgid="+orgid+"and b.bustype="+bustype;
		Vector vt=dc.multiRowSelect(sql);
		for(int i=0;i<vt.size();i++)
		{
			Hashtable ht=(Hashtable) vt.get(i);
			
			ht.put("busid", Integer.valueOf(busid));
			ht.put("bustype", Integer.valueOf(bustype));
			ht.put("executeflag", Integer.valueOf(0));
			ht.put("stime", new Timestamp(System.currentTimeMillis()));
			
			ECS_IntfBusList intfsheetlist=new ECS_IntfBusList();
			
			intfsheetlist.getMapData(ht);
			
			dc.insert(intfsheetlist);
		}
	}
	
	
	public static Map getInterfaceInfo(DataCentre dc,int bustype,int interfaceid) throws Exception
	{
		Hashtable htintf=new Hashtable();
		String sql="select b.exportclass from ecs_interface a,ecs_interfacebustype b "
			+"where a.interfacetype=b.interfacetype and a.interfaceid="+interfaceid+" and b.bustype="+bustype;
		Hashtable htsheettpye=dc.oneRowSelect(sql);
		htintf.putAll(htsheettpye);
		
		sql="select orgid,vertifycode,extinterfaceid from ecs_interface where interfaceid="+interfaceid;
		Hashtable htinterface=dc.oneRowSelect(sql);
		htintf.putAll(htinterface);
		
		int noteid=dc.IDGenerator("ecs_downnote", "noteid");
		
		htintf.put("noteid", Integer.valueOf(noteid));
		
		return htintf;
	}
	
	public static Map getDownNoteInterfaceInfo(DataCentre dc,int bustype,String owner) throws Exception
	{
		String sql="select b.downnoteclass,c.id merchantid from ecs_interface a," +
				"ecs_interfacebustype b,ecs_merchant c "
				+"where a.interfacetype=b.interfacetype " 
						+" and a.vertifycode='"+owner+"' " 
						+" and b.bustype="+bustype
						+" and c.vertifycode='"+owner+"'";

		return dc.oneRowSelect(sql);
	}
	
	public static Map getImportInterfaceInfo(DataCentre dc,int bustype,String owner) throws Exception
	{
		String sql="select b.upnoteclass,c.merchantid from ecs_interface a," +
				"ecs_interfacebustype b,ecs_org c "
				+"where a.interfacetype=b.interfacetype " 
						+" and a.vertifycode='"+owner+"' " 
						+" and b.bustype="+bustype
						+" and a.orgid=c.orgid ";
	
		return dc.oneRowSelect(sql);
	}
	
}
