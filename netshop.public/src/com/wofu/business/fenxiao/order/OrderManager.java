package com.wofu.business.fenxiao.order;


import java.sql.Connection;
import java.sql.SQLException;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;


import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;

public class OrderManager {

	public static void CancelOrderByCID(String modulename,Connection conn,String CID)
	{
		/*if (CIDExists(modulename,conn,CID))
		{
			try
			{
				String sql="select sheetid from customerorder0 where refsheetid='"+CID+"'";
				String sheetid=SQLHelper.strSelect(conn, sql);
				
				sql="update customerorder0 set flag=97 where sheetid='"+sheetid+"'";
				SQLHelper.executeSQL(conn, sql);
				
				sql = "declare @err int; execute @Err=TL_SheetTransfer '"+ sheetid+ "',1,'CustomerOrder0,CustomerOrderItem0','CustomerOrder,CustomerOrderItem'; select @err";

				int ret = SQLHelper.intSelect(conn, sql);
				if (ret == -1) {
					Log.error(modulename,"数据转移正式表失败");
				}
			}catch(JSQLException jsqle)
			{
				Log.error(modulename, "取客户订单号出错!"+jsqle.getMessage());
			}
			catch(SQLException sqle)
			{
				Log.error(modulename, "取消订单出错!"+sqle.getMessage());
			}
		}*/
	}
	
	public static boolean CIDExists(String modulename,Connection conn,String CID)
	{
		boolean ret=false;
		try
		{
			String sql="select count(*) from customerorder0 where refsheetid='"+CID+"'";
			if (SQLHelper.intSelect(conn, sql)!=0)
				ret=true;
		}catch(JSQLException jsqle)
		{
			Log.error(modulename, "判断客户订单是否存在时出错!"+jsqle.getMessage());
		}
		return ret;
	}
	
	public static boolean TidExists(String modulename,Connection conn,String tid) throws JSQLException
	{
		boolean ret=false;

		String sql="select count(*) from customerorder0 with(nolock) where refsheetid='"+tid+"'";
		ret=SQLHelper.intSelect(conn, sql)!=0;
		if (!ret)
		{
			sql="select count(*) from customerorder with(nolock) where refsheetid='"+tid+"'";
			ret=SQLHelper.intSelect(conn, sql)!=0;
			
			if (!ret)
			{
				sql="select count(*) from MerDecOrderLog with(nolock) where refsheetid='"+tid+"'";
				ret=SQLHelper.intSelect(conn, sql)!=0;				
			}
		}

		return ret;
	}
	
	public static boolean isCheck(String modulename,Connection conn,String tid) throws Exception
	{
		boolean ret=false;
		String sql="select count(*) from DecOrder with(nolock) where refsheetid='"+tid+"'";
		ret=SQLHelper.intSelect(conn, sql)!=0;
		if (!ret)
		{
			sql="select count(*) from MerDecOrderLog with(nolock) where refsheetid='"+tid+"'";
			ret=SQLHelper.intSelect(conn, sql)!=0;	
		}
				
		return ret;
	}
	/*
	 * 判断本次修改是否已存在
	 */
	public static boolean TidLastModifyIntfExists(String modulename,Connection conn,String tid,Date lastmodify) throws Exception
	{
		boolean ret=false;
		String sql="select count(*) from itf_DecOrder with(nolock) where tid='"+tid+"' "
				+"and modified>='"+Formatter.format(lastmodify, Formatter.DATE_TIME_FORMAT)+"'";
		ret=SQLHelper.intSelect(conn, sql)!=0;				
		return ret;
	}
	
	
	/*
	 * 判断本次修改是否已存在  mysql
	 */
	public static boolean TidLastModifyIntfExists(String modulename,Connection conn,String tid,Date lastmodify,boolean ismysql) throws Exception
	{
		boolean ret=false;
		String sql="select count(*) from ecs_order_info  where order_sn='"+tid+"' "
				+"and modified='"+Formatter.format(lastmodify, Formatter.DATE_TIME_FORMAT)+"'";
		ret=SQLHelper.intSelect(conn, sql)!=0;				
		return ret;
	}
	
	
	public static boolean RefundisCheck(String modulename,Connection conn,String tid,String sku)throws JSQLException
	{
		boolean ret=false;
		String sql="select count(*) from refund a with(nolock),refunditem b with(nolock),barcode c with(nolock) "
				+"where a.sheetid=b.sheetid and b.barcodeid=c.barcodeid and c.custombc='"+sku+"' and a.refsheetid='"+tid+"'";
		ret=SQLHelper.intSelect(conn, sql)!=0;				
		return ret;
	}
	
	public static boolean TidIntfExists(String modulename,Connection conn,String tid)
	{
		boolean ret=false;
		try
		{
			String sql="select count(*) from ns_customerorder with(nolock) where tid='"+tid+"'";
			ret=SQLHelper.intSelect(conn, sql)!=0;	
			
		}catch(JSQLException jsqle)
		{
			Log.error(modulename, "判断客户订单是否存在接口时出错!"+jsqle.getMessage());
		}
		return ret;
	}
	
	
	public static boolean genCustomerOrder(Connection conn,String sheetid,int isDelay,int tableType) throws Exception
	{	String sql ="";
		if(isDelay==0 && tableType==0){
			sql="declare @ret int; declare @msg varchar(128); "
				+" execute  @ret = eco_genCustomerOrder '"+sheetid+"',@msg output;"
				+" select @ret ret,@msg msg;";
		}else{
			sql="declare @ret int; declare @msg varchar(128); "
				+" execute  @ret = eco_genCustomerOrder '"+sheetid+"',@msg output,'',0,'接口',"+isDelay+","+tableType+";"
				+" select @ret ret,@msg msg;";
		}
		
		boolean is_success=false;
		long starttime = System.currentTimeMillis();
		Hashtable ht=SQLHelper.oneRowSelect(conn, sql);
		Log.info("调用生成一个订单的过程的时间为:"+(System.currentTimeMillis()-starttime));
		int ret =Integer.valueOf(ht.get("ret").toString()).intValue();
		String msg=ht.get("msg").toString();
		
		if (ret==0)
		{
			is_success=true;
			if (!msg.trim().equals(""))
			{
				Log.info("生成客户订单成功,接口单号: "+sheetid+"返回信息: "+msg);
			}
		}
		else if (ret==-1)
		{			
			Log.info("生成客户订单失败,接口单号: "+sheetid+" 错误信息: "+msg);
			is_success=false;			
		}
		
		return is_success;
		
	}
	
	public static boolean genCustomerOrder(Connection conn,String sheetid) throws Exception
	{	
		String sql="declare @ret int; declare @msg varchar(128); "
				+" execute  @ret = eco_genCustomerOrder '"+sheetid+"',@msg output;"
				+" select @ret ret,@msg msg;";
		
		
		boolean is_success=false;
		long starttime = System.currentTimeMillis();
		Hashtable ht=SQLHelper.oneRowSelect(conn, sql);
		Log.info("调用生成一个订单的过程的时间为:"+(System.currentTimeMillis()-starttime));
		int ret =Integer.valueOf(ht.get("ret").toString()).intValue();
		String msg=ht.get("msg").toString();
		
		if (ret==0)
		{
			is_success=true;
			if (!msg.trim().equals(""))
			{
				Log.info("生成客户订单成功,接口单号: "+sheetid+"返回信息: "+msg);
			}
		}
		else if (ret==-1)
		{			
			Log.info("生成客户订单失败,接口单号: "+sheetid+" 错误信息: "+msg);
			is_success=false;			
		}
		
		return is_success;
		
	}
	//生成分销订单
	public static boolean GenDecOrder(Connection conn,int sheetid) throws Exception
	{	
		String sql="declare @ret int; declare @msg varchar(128); "
				+" execute  @ret = IF_GenDecOrder "+sheetid+",@msg output;"
				+" select @ret ret,@msg msg;";
		
		boolean is_success=false;
		long starttime = System.currentTimeMillis();
		Hashtable ht=SQLHelper.oneRowSelect(conn, sql);
		Log.info("调用生成一个订单的过程的时间为:"+(System.currentTimeMillis()-starttime));
		int ret =Integer.valueOf(ht.get("ret").toString()).intValue();
		String msg=ht.get("msg").toString();
		
		if (ret==0)
		{
			is_success=true;
			if (!msg.trim().equals(""))
			{
				Log.info("生成客户订单成功,接口单号: "+sheetid+"返回信息: "+msg);
			}
		}
		else if (ret==-1)
		{			
			Log.info("生成客户订单失败,接口单号: "+sheetid+" 错误信息: "+msg);
			is_success=false;			
		}
		
		return is_success;
		
	}
	//又一城分销系统用  lineFlag=1代表b2b接口
	public static boolean genCustomerOrder(Connection conn,String sheetid,int lineFlag) throws Exception
	{	
		String sql="declare @ret int; declare @msg varchar(128); "
				+" execute  @ret = eco_genCustomerOrder '"+sheetid+"',@msg output,'',0,'接口',0,0,"+lineFlag+";"
				+" select @ret ret,@msg msg;";
		
		
		boolean is_success=false;
		long starttime = System.currentTimeMillis();
		Hashtable ht=SQLHelper.oneRowSelect(conn, sql);
		Log.info("调用生成一个订单的过程的时间为:"+(System.currentTimeMillis()-starttime));
		int ret =Integer.valueOf(ht.get("ret").toString()).intValue();
		String msg=ht.get("msg").toString();
		
		if (ret==0)
		{
			is_success=true;
			if (!msg.trim().equals(""))
			{
				Log.info("生成客户订单成功,接口单号: "+sheetid+"返回信息: "+msg);
			}
		}
		else if (ret==-1)
		{			
			Log.info("生成客户订单失败,接口单号: "+sheetid+" 错误信息: "+msg);
			is_success=false;			
		}
		
		return is_success;
		
	}
	
	//经销系统用  lineFlag=1代表b2b接口
	public static boolean genCustomerOrder(Connection conn,String sheetid,int lineFlag,String purchaseFlag) throws Exception
	{	
		String sql="declare @ret int; declare @msg varchar(128); "
				+" execute  @ret = eco_genCustomerOrder '"+sheetid+"',@msg output,'',0,'接口',0,0,"+lineFlag+",'"+purchaseFlag+"';"
				+" select @ret ret,@msg msg;";
		
		
		boolean is_success=false;
		long starttime = System.currentTimeMillis();
		Hashtable ht=SQLHelper.oneRowSelect(conn, sql);
		Log.info("调用生成一个订单的过程的时间为:"+(System.currentTimeMillis()-starttime));
		int ret =Integer.valueOf(ht.get("ret").toString()).intValue();
		String msg=ht.get("msg").toString();
		
		if (ret==0)
		{
			is_success=true;
			if (!msg.trim().equals(""))
			{
				Log.info("生成客户订单成功,接口单号: "+sheetid+"返回信息: "+msg);
			}
		}
		else if (ret==-1)
		{			
			Log.info("生成客户订单失败,接口单号: "+sheetid+" 错误信息: "+msg);
			is_success=false;			
		}
		
		return is_success;
		
	}
	
	
	
	public void addFailOrder(Connection conn,String tradecontactid,String tid) throws SQLException
	{
		String sql="insert into ns_FailOrder(tradecontactid,tid) values("+tradecontactid+",'"+tid+"')";
		SQLHelper.executeSQL(conn, sql);
	}
	
	public void deleteFailOrder(Connection conn,String tradecontactid,String tid) throws SQLException
	{
		String sql="delete from ns_FailOrder where tradecontactid="+tradecontactid+" and tid='"+tid+"'";
		SQLHelper.executeSQL(conn, sql);
	}
	
	public List getFailOrder(Connection conn,String tradecontactid) throws JSQLException
	{
		String sql="select tid from ns_FailOrder where tradecontactid="+tradecontactid;
		return SQLHelper.multiRowListSelect(conn, sql);
	}
	
	public static int getPromotionID(Connection conn,String promotionname) throws Exception
	{
		int promotionid;
		String sql="select count(*) from ecs_promotion where name='"+promotionname+"'";
		if (SQLHelper.intSelect(conn,sql)>0)
		{
			sql="select promotionid from ecs_promotion where name='"+promotionname+"'";
			promotionid=SQLHelper.intSelect(conn,sql);
		}
		else
		{
			sql="select isnull(max(promotionid),0) from ecs_promotion";
			promotionid=SQLHelper.intSelect(conn,sql)+1;
			
			sql="insert into ecs_promotion(promotionid,name,notes) "
				+"values("+promotionid+",'"+promotionname+"','"
				+promotionname+"')";
			SQLHelper.executeSQL(conn, sql);
		}
		return promotionid;
	}
	
	public static void CancelOrderByCID(String modulename,DataCentre dc,String CID) throws Exception
	{
		if (CIDExists(modulename,dc,CID))
		{
			try
			{
				String sql="select sheetid from customerorder0 where refsheetid='"+CID+"'";
				String sheetid=dc.strSelect(sql);
				
				sql="update customerorder0 set flag=97 where sheetid='"+sheetid+"'";
				dc.execute(sql);
				
				sql = "declare @err int; execute @Err=TL_SheetTransfer '"+ sheetid+ "',1,'CustomerOrder0,CustomerOrderItem0','CustomerOrder,CustomerOrderItem'; select @err";

				int ret = dc.intSelect(sql);
				if (ret == -1) {
					Log.error(modulename,"数据转移正式表失败");
				}
			}catch(JSQLException jsqle)
			{
				Log.error(modulename, "取客户订单号出错!"+jsqle.getMessage());
			}
			catch(SQLException sqle)
			{
				Log.error(modulename, "取消订单出错!"+sqle.getMessage());
			}
		}
	}
	
	public static boolean CIDExists(String modulename,DataCentre dc,String CID) throws Exception
	{
		boolean ret=false;
		try
		{
			String sql="select count(*) from customerorder0 where refsheetid='"+CID+"'";
			if (dc.intSelect(sql)!=0)
				ret=true;
		}catch(JSQLException jsqle)
		{
			Log.error(modulename, "判断客户订单是否存在时出错!"+jsqle.getMessage());
		}
		return ret;
	}
	
	public static boolean TidExists(String modulename,DataCentre dc,String tid) throws Exception
	{
		boolean ret=false;

		String sql="select count(*) from customerorder0 with(nolock) where refsheetid='"+tid+"'";
		ret=dc.intSelect(sql)!=0;
		if (!ret)
		{
			sql="select count(*) from customerorder with(nolock) where refsheetid='"+tid+"'";
			ret=dc.intSelect(sql)!=0;
			
			if (!ret)
			{
				sql="select count(*) from CustomerOrderRefList with(nolock) where refsheetid='"+tid+"'";
				ret=dc.intSelect(sql)!=0;				
			}
		}

		return ret;
	}
	
	public static boolean isCheck(String modulename,DataCentre dc,String tid) throws Exception
	{
		boolean ret=false;
		String sql="select count(*) from customerorder with(nolock) where refsheetid='"+tid+"'";
		ret=dc.intSelect( sql)!=0;
		if (!ret)
		{
			sql="select count(*) from CustomerOrderRefList with(nolock) where refsheetid='"+tid+"'";
			ret=dc.intSelect( sql)!=0;				
		}
				
		return ret;
	}
	/*
	 * 判断本次修改是否已存在
	 */
	public static boolean TidLastModifyIntfExists(String modulename,DataCentre dc,String tid,Date lastmodify) throws Exception
	{
		boolean ret=false;
		String sql="select count(*) from ns_customerorder with(nolock) where tid='"+tid+"' "
				+"and modified='"+Formatter.format(lastmodify, Formatter.DATE_TIME_FORMAT)+"'";
		ret=dc.intSelect( sql)!=0;				
		return ret;
	}
	
	public static boolean RefundisCheck(String modulename,DataCentre dc,String tid,String sku)throws Exception
	{
		boolean ret=false;
		String sql="select count(*) from refund a with(nolock),refunditem b with(nolock),barcode c with(nolock) "
				+"where a.sheetid=b.sheetid and b.barcodeid=c.barcodeid and c.custombc='"+sku+"' and a.refsheetid='"+tid+"'";
		ret=dc.intSelect(sql)!=0;				
		return ret;
	}
	
	public static boolean RefundIntfExists(String modulename,Connection conn,String tid,String refundcode)  throws Exception
	{
		boolean ret=false;
		try
		{
			String sql="select count(*) from ns_refund with(nolock) where tid='"+tid+"' and refundid='"+refundcode+"'";
			ret=SQLHelper.intSelect(conn, sql)!=0;	
			
		}catch(JSQLException jsqle)
		{
			Log.error(modulename, "判断退货是否存在接口时出错!"+jsqle.getMessage());
		}
		return ret;
	}
	
	public static boolean TidIntfExists(String modulename,DataCentre dc,String tid)  throws Exception
	{
		boolean ret=false;
		try
		{
			String sql="select count(*) from ns_customerorder with(nolock) where tid='"+tid+"'";
			ret=dc.intSelect( sql)!=0;	
			
		}catch(JSQLException jsqle)
		{
			Log.error(modulename, "判断客户订单是否存在接口时出错!"+jsqle.getMessage());
		}
		return ret;
	}
	

	public static boolean genCustomerOrder(DataCentre dc,String sheetid) throws Exception
	{			
		String sql="declare @ret int; declare @msg varchar(128); "
			+" execute  @ret = eco_genCustomerOrder '"+sheetid+"',@msg output;"
			+" select @ret ret,@msg msg;";
			
		boolean is_success=false;
				
		Hashtable ht=dc.oneRowSelect(sql);
		int ret =Integer.valueOf(ht.get("ret").toString()).intValue();
		String msg=ht.get("msg").toString();
		
		if (ret==0)
		{
			is_success=true;
			if (!msg.trim().equals(""))
			{
				Log.info(msg);
			}
		}
		else if (ret==-1)
		{			
			Log.info(msg);
			is_success=false;			
		}
		
		return is_success;
	}
	
	public void addFailOrder(DataCentre dc,String tradecontactid,String tid) throws Exception
	{
		String sql="insert into ns_FailOrder(tradecontactid,tid) values("+tradecontactid+",'"+tid+"')";
		dc.execute( sql);
	}
	
	public void deleteFailOrder(DataCentre dc,String tradecontactid,String tid) throws SQLException
	{
		String sql="delete from ns_FailOrder where tradecontactid="+tradecontactid+" and tid='"+tid+"'";
		dc.execute( sql);
	}
	
	public List getFailOrder(DataCentre dc,String tradecontactid) throws Exception
	{
		String sql="select tid from ns_FailOrder where tradecontactid="+tradecontactid;
		return dc.multiRowSelect( sql);
	}
	
	public static int getPromotionID(DataCentre dc,String promotionname) throws Exception
	{
		int promotionid;
		String sql="select count(*) from ecs_promotion where name='"+promotionname+"'";
		if (dc.intSelect(sql)>0)
		{
			sql="select promotionid from ecs_promotion where name='"+promotionname+"'";
			promotionid=dc.intSelect(sql);
		}
		else
		{
			sql="select isnull(max(promotionid),0) from ecs_promotion";
			promotionid=dc.intSelect(sql)+1;
			
			sql="insert into ecs_promotion(promotionid,name,notes) "
				+"values("+promotionid+",'"+promotionname+"','"
				+promotionname+"')";
			dc.execute(sql);
		}
		return promotionid;
	}
}
