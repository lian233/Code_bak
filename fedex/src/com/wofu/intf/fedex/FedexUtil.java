package com.wofu.intf.fedex;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

import java.sql.Connection;
import java.util.*;
public class FedexUtil
{

	public FedexUtil()
	{
	}
	//xml内容模板
	public static String xmlContent = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://www.example.org/ServiceForOrder/\"><SOAP-ENV:Body><ns1:{servicetype}><HeaderRequest><customerCode>{customerCode}</customerCode><appToken>{appToken}</appToken><appKey>{appKey}</appKey></HeaderRequest></ns1:{servicetype}></SOAP-ENV:Body></SOAP-ENV:Envelope>";
	
	
	//取得接口数据
	public static Vector getInfDownNote(Connection conn,String sheettype) throws Exception
	{		
		String sql="select SerialID, OperData , OperType, Owner  from Inf_DownNote where Flag = 0 and SheetType= "+sheettype;					
		Vector  sheetlist=SQLHelper.multiRowSelect(conn, sql);
		return sheetlist;
	}
	
	public static void bakcUpDownNote(Connection conn,int seriaalid) throws Exception{
		String sql ="insert into Inf_DownNotebak(serialid,sheettype,notetime,handletime,opertype,operdata,flag,result)"
			+ "select serialid,sheettype,notetime,getdate(),opertype,operdata,10,'success' from Inf_DownNote where serialid="+seriaalid;
		SQLHelper.executeSQL(conn, sql);
		sql ="delete Inf_DownNote where serialid="+seriaalid;
		SQLHelper.executeSQL(conn,sql);
	}






	/*
	public static String getWarehouseCode(String shopid,String storecode)
		throws Exception
	{
		String s1 = "";
		Object aobj[] = StringUtil.split(storecode, ";").toArray();
		int i = 0;
		do
		{
			if (i >= aobj.length)
				break;
			String s2 = (String)aobj[i];
			Object aobj1[] = StringUtil.split(s2, ":").toArray();
			String s3 = (String)aobj1[0];
			String s4 = (String)aobj1[1];
			if (s3.equals(shopid))
			{
				s1 = s4;
				break;
			}
			i++;
		} while (true);
		return s1;
	}

	
	public static String getShopID(String warehousecode,String storecode)
		throws Exception
	{
		String shopid = "";
		Object aobj[] = StringUtil.split(storecode, ";").toArray();
		int i = 0;
		do
		{
			if (i >= aobj.length)
				break;
			String s2 = (String)aobj[i];
			Object aobj1[] = StringUtil.split(s2, ":").toArray();
			String s3 = (String)aobj1[0];
			String s4 = (String)aobj1[1];
			if (s4.equals(warehousecode))
			{
				shopid = s3;
				break;
			}
			i++;
		} while (true);
		return shopid;
	}
	*/
	
	public static String getWarehouseCode(Connection conn,String customercode,String shopid)
	throws Exception
	{
		String warehousecode = "";
		String sql="select warehousecode from ecs_bestlogisticswarehousecontrast where customercode='"+customercode+"' and dcshopid='"+shopid+"'";
		warehousecode=SQLHelper.strSelect(conn, sql);
		return warehousecode;
	}
	
	//一个dc对应几个百世仓的情况，要加上服务类型
	public static String getWarehouseCode(Connection conn,String customercode,String shopid,String serviceType)
	throws Exception
	{
		String warehousecode = "";
		String sql="select warehousecode from ecs_bestlogisticswarehousecontrastmulti where customercode='"+customercode+"' and dcshopid='"
		+shopid+"' and servicetype='"+serviceType+"'";
		warehousecode=SQLHelper.strSelect(conn, sql);
		return warehousecode;
	}
	
	public static String getShopID(Connection conn,String customercode,String warehousecode)
	throws Exception
	{
		String shopid = "";
		String sql="select dcshopid from ecs_bestlogisticswarehousecontrast "
			+"where customercode='"+customercode+"' and warehousecode='"+warehousecode+"'";
		shopid=SQLHelper.strSelect(conn, sql);
		return shopid;
	}
	//一个dc对应几个百世仓的情况，要加上服务类型
	public static String getShopID(Connection conn,String customercode,String warehousecode,String serviceType)
	throws Exception
	{
		String shopid = "";
		String sql="select dcshopid from ecs_bestlogisticswarehousecontrastmulti "
			+"where customercode='"+customercode+"' and warehousecode='"+warehousecode+"' and servicetype='"+serviceType+"'";
		shopid=SQLHelper.strSelect(conn, sql);
		return shopid;
	}
	public static String getDSName(Connection conn,String customercode,String warehousecode) throws Exception
	{
		String dsname = "";
		String sql="select dsname from ecs_bestlogisticswarehousecontrast "
			+"where customercode='"+customercode+"' and warehousecode='"+warehousecode+"'";
		dsname=SQLHelper.strSelect(conn, sql);
		if("".equals(dsname) || dsname==null) throw new Exception("外部dc数据库名为null,请检查bm库ecs_bestlogisticswarehousecontrast配置");
		return dsname;
	}
	
	
	public static String filterChar(String xml) throws Exception{
		StringBuilder sb = new StringBuilder();
		char[] arr = xml.toCharArray();
		for(char e:arr){
			
			if((int)e<=0x1f){
			}else if((int)e==0xdde2){
				Log.info("e:"+Integer.toHexString((int)(e)));
			}else if((int)e==38)
				sb.append("&amp;");
			else{
				sb.append(e);
			}
			
		}
		return sb.toString();
	}
	
}
