package com.wofu.fire.deliveryservice;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

import java.sql.Connection;
import java.util.*;
public class crossborderUtil
{

	public crossborderUtil()
	{
	}

	public static String makeSign(Map params)
		throws Exception
	{
		String signstr = "";
		for (Iterator iterator = params.keySet().iterator(); iterator.hasNext();)
		{
			String paramname = (String) iterator.next();
			String paramvalue = (String) params.get(paramname);
			signstr = signstr+paramname+"="+paramvalue+"&";
		}

		signstr = signstr.substring(0, signstr.length() - 1);
		String sign = MD5Util.getMD5Code(signstr.getBytes("UTF-8"));
		return sign;
	}

	public static String makeSign(List params)
		throws Exception
	{
		String signstr = "";
		for (int i = 0; i < params.size(); i++)
		{
			String param = params.get(i).toString();
			signstr = signstr+param+"&";
		}

		signstr = signstr.substring(0,signstr.length() - 1);
		String sign = MD5Util.getMD5Code(signstr.getBytes("UTF-8"));
		return sign;
	}

	public static List<String> makeSignParams(String bizdata, String servicetype, String msgtype, 
			String partnerid, String partnerkey, String serviceversion, String callbackurl, String msgid)
		throws Exception
	{
		ArrayList<String> params = new ArrayList<String>();
		params.add("partnerId="+partnerid);
		params.add("bizData="+bizdata);
		params.add("partnerKey="+partnerkey);
		params.add("msgId="+msgid);
		params.add("msgType="+msgtype);
		params.add("serviceType="+servicetype);
		params.add("serviceVersion="+serviceversion);
		params.add("notifyUrl="+callbackurl);
		return params;
	}

	public static Map<String,String>  makeRequestParams(String bizdata, String servicetype, String msgid, 
			String msgtype, String sign, String callbackurl, String serviceversion, String partnerid)
		throws Exception
	{

		HashMap<String,String> params = new HashMap<String,String>();
		params.put("bizData", bizdata);
		params.put("msgType", msgtype);
		params.put("serviceType", servicetype);
		params.put("msgId", msgid);
		params.put("notifyUrl", callbackurl);
		params.put("serviceVersion", serviceversion);
		params.put("partnerId", partnerid);
		params.put("sign", sign);
		return params;
	}

	public static String makeMsgId(Connection connection)
		throws Exception
	{
		String msgid = "";
		String sql = "select count(*) from ecs_idlist where tablename='bestlogistics'";
		if (SQLHelper.intSelect(connection, sql) > 0)
		{
			sql = "update ecs_idlist set keyid=keyid+1 where  tablename='bestlogistics'";
			SQLHelper.executeSQL(connection, sql);
		} else
		{
			sql = "insert into ecs_idlist(keyid,keyname,tablename) values(1,'msgid','bestlogistics')";
			SQLHelper.executeSQL(connection, sql);
		}
		sql = "select keyid from ecs_idlist where tablename='bestlogistics'";
		msgid = SQLHelper.strSelect(connection, sql);
		
		sql="select value from config where name='本店号'";
		String myshopid=SQLHelper.strSelect(connection, sql);
		
		return myshopid+msgid;
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
