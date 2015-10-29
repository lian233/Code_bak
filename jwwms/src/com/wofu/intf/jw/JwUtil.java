package com.wofu.intf.jw;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

import java.sql.Connection;
import java.util.*;

import sun.misc.BASE64Encoder;
public class JwUtil
{
	public static BASE64Encoder encoder = null;
	public JwUtil()
	{
	}
	static {
		encoder = new BASE64Encoder();
	}
	//secret = MD5( base64(内容)+6C08B0BEF86484AC34486102D765090C)
	public static String makeSign(String content)
		throws Exception
	{
		String encodeContent = encoder.encode(content.getBytes("utf-8"));
		String sign = MD5Util.getMD5Code((encodeContent+Params.vcode).getBytes("UTF-8"));
		return sign;
	}

	public static Map<String,String>  makeRequestParams(String bizdata, String servicetype,  
			String appKey, String format, String sign)
		throws Exception
	{

		HashMap<String,String> params = new HashMap<String,String>();
		params.put("service", servicetype);
		params.put("appkey", appKey);
		params.put("secret", sign);
		params.put("format", format);
		params.put("content", new BASE64Encoder().encode(bizdata.getBytes("utf-8")));
		params.put("encrypt", "1");
		/**
		for(Iterator it =params.keySet().iterator();it.hasNext();){
			String name = (String)it.next();
			String value = params.get(name);
			Log.info(name+" "+value);
		}
		**/
		return params;
	}


	public static void recordMsg(Connection connection, String msgid, String infsheetid, int infsheettype, String servicetype)
		throws Exception
	{
		String sql = "insert into ecs_bestlogisticsmsg(msgid,infsheetid,infsheettype,servicetype,reqdate) "
				+"values('"+msgid+"','"+infsheetid+"',"+infsheettype+",'"+servicetype+"',getdate())";
		SQLHelper.executeSQL(connection, sql);
	}

	public static void updateMsg(Connection connection, String infsheetid, int infsheettype, int status)
		throws Exception
	{
		String sql = "update ecs_bestlogisticsmsg set replydate=getdate(),status="+status
			+" where infsheetid='"+infsheetid+"' and infsheettype="+infsheettype;
		SQLHelper.executeSQL(connection, sql);
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
	
	public static String getShopID(Connection conn,String customercode,String warehousecode)
	throws Exception
	{
		String shopid = "";
		String sql="select dcshopid from ecs_bestlogisticswarehousecontrast "
			+"where customercode='"+customercode+"' and warehousecode='"+warehousecode+"'";
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

	public static List getintfsheetlist(Connection conn,int sheettype,int opertype) throws Exception{
		String sql ="select SerialID,OperData from Inf_downNote where sheettype="+sheettype
		+" and opertype="+opertype;
		Vector result  = SQLHelper.multiRowSelect(conn, sql);
		return result;
	}
	//删除重复订单，暂时的办法
	public static void delBackUpIntsheetData(Connection conn,Integer serialID) throws Exception{
		String sql ="insert into Inf_downNotebak select SerialID,sheettype,"+
		"NoteTime,getdate(),OperType,OperData,100,'repeat','' from Inf_downNote where SerialID="+serialID;
		SQLHelper.executeSQL(conn, sql);
		sql = "delete Inf_downNote where SerialID="+serialID;
		SQLHelper.executeSQL(conn, sql);
	}
	
	public static void backUpIntsheetData(Connection conn,Integer serialID) throws Exception{
		String sql ="insert into Inf_downNotebak select SerialID,sheettype,"+
		"NoteTime,getdate(),OperType,OperData,100,'success','' from Inf_downNote where SerialID="+serialID;
		SQLHelper.executeSQL(conn, sql);
		sql = "delete Inf_downNote where SerialID="+serialID;
		SQLHelper.executeSQL(conn, sql);
	}
	
}
