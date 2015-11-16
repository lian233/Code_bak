package com.wofu.base.job;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.wofu.common.service.Params;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class ServiceManagerListener implements ServletContextListener {

	private Timer timer;
	

	public void contextDestroyed(ServletContextEvent sce) {
		timer.cancel();
	}

	public void contextInitialized(ServletContextEvent sce) {
		
		Connection conn = null;
		timer = new Timer(true);
		try {
			
			conn = PoolHelper.getInstance().getConnection(
					Params.getInstance().getProperty("dbname"));
			
			String sql="select orgid from ecs_org where state=0 and typeid=12";
			List orgs=SQLHelper.multiRowListSelect(conn, sql);
			for(Iterator it=orgs.iterator();it.hasNext();)
			{
				String orgid=(String) it.next();
				
				sql="select shortname,order_handle_enable,refund_handle_enable,goods_handle_enable,delivery_handle_enable,"
					+"status_handle_enable,stock_handle_enable,customerorder_handle_enable,refundorder_handle_enable,"
					+"isnull(orderhandler,'') as orderhandler,isnull(refundhandler,'') as refundhandler,"
					+"isnull(goodshandler,'') as goodshandler,isnull(deliveryhandler,'') as deliveryhandler,"
					+"isnull(statushandler,'') as statushandler,isnull(stockhandler,'') as stockhandler,waittime,"
					+"isnull(customerorderhandler,'') as customerorderhandler,isnull(refundorderhandler,'') as refundorderhandler "
					+"from ecs_org_params a,ecs_platform b where a.platformid=b.platformid and a.orgid="+orgid;
				
				Hashtable htparams=SQLHelper.oneRowSelect(conn, sql);
				
				int order_handle_enable=Integer.parseInt(htparams.get("order_handle_enable").toString());
				int refund_handle_enable=Integer.parseInt(htparams.get("refund_handle_enable").toString());
				int goods_handle_enable=Integer.parseInt(htparams.get("goods_handle_enable").toString());
				int delivery_handle_enable=Integer.parseInt(htparams.get("delivery_handle_enable").toString());
				int status_handle_enable=Integer.parseInt(htparams.get("status_handle_enable").toString());
				int stock_handle_enable=Integer.parseInt(htparams.get("stock_handle_enable").toString());
				int customerorder_handle_enable=Integer.parseInt(htparams.get("customerorder_handle_enable").toString());
				int refundorder_handle_enable=Integer.parseInt(htparams.get("refundorder_handle_enable").toString());
				
				String platformname=htparams.get("shortname").toString();
				String orderhandler=htparams.get("orderhandler").toString();
				String refundhandler=htparams.get("refundhandler").toString();
				String goodshandler=htparams.get("goodshandler").toString();
				String deliveryhandler=htparams.get("deliveryhandler").toString();
				String statushandler=htparams.get("statushandler").toString();
				String stockhandler=htparams.get("stockhandler").toString();
				String customerorderhandler=htparams.get("customerorderhandler").toString();
				String refundorderhandler=htparams.get("refundorderhandler").toString();
				int waittime=Integer.parseInt(htparams.get("waittime").toString());
				
				Hashtable handlerparams=null;
				if (platformname.equalsIgnoreCase("taobao")){
					
					sql="select orgid,url,appkey,appsecret,token,province,city,district,pagesize"
						+"address,zipcode,linkman,phone,mobileno,iswlb,isdistribution,isb,isrds "
						+"from ecs_org_params where orgid="+orgid;
					handlerparams=SQLHelper.oneRowSelect(conn, sql);
				}				
				else if (platformname.equalsIgnoreCase("360buy")){
					
					sql="select orgid,url,appkey,appsecret,token,province,city,district,"
						+"address,zipcode,linkman,phone,mobileno,encoding,isLBP,pagesize "
						+"from ecs_org_params where orgid="+orgid;
					handlerparams=SQLHelper.oneRowSelect(conn, sql);
				}				
				else if (platformname.equalsIgnoreCase("dangdang")){
					
					sql="select orgid,url,gshopid,key,province,city,district,"
						+"address,zipcode,linkman,phone,mobileno,pagesize "
						+"from ecs_org_params where orgid="+orgid;
					handlerparams=SQLHelper.oneRowSelect(conn, sql);
				}				
				else if (platformname.equalsIgnoreCase("vjia")){
					
					sql="select orgid,url,webserviceurl,uname,password,swsSupplierID,"
						+"decryptkey,decryptRandomCode,province,city,district,"
						+"address,zipcode,linkman,phone,mobileno,pagesize "
						+"from ecs_org_params where orgid="+orgid;
					handlerparams=SQLHelper.oneRowSelect(conn, sql);
				}	
				else if (platformname.equalsIgnoreCase("paipai")){
					
					sql="select orgid,url,uid,appkey,appsecret,token,province,city,district,"
						+"address,zipcode,linkman,phone,mobileno,pagesize "
						+"from ecs_org_params where orgid="+orgid;
					handlerparams=SQLHelper.oneRowSelect(conn, sql);
				}
				else if (platformname.equalsIgnoreCase("qqbuy")){
					
					sql="select orgid,url,uid,appkey,appsecret,token,"
						+"cooperatorid,encoding,province,city,district,"
						+"address,zipcode,linkman,phone,mobileno,pagesize "
						+"from ecs_org_params where orgid="+orgid;
					handlerparams=SQLHelper.oneRowSelect(conn, sql);
				}	
				else if (platformname.equalsIgnoreCase("amazon"))
				{
					sql="select orgid,url,accesskeyid,secretaccesskey,sellerid,marketplaceid,"
						+"applicationname,applicationversion,province,city,district,"
						+"address,zipcode,linkman,phone,mobileno,pagesize "
						+"from ecs_org_params where orgid="+orgid;
					handlerparams=SQLHelper.oneRowSelect(conn, sql);
				}else
				{
					throw new JException("不支持的平台:"+platformname);
				}
				
				if (handlerparams!=null)
				{
					if (order_handle_enable==1 && !orderhandler.equals(""))
					{
						TimerBusinessHandler orderbusinesshandler=(TimerBusinessHandler) Class.forName(orderhandler).newInstance();
						orderbusinesshandler.setParams(handlerparams);
						timer.schedule(orderbusinesshandler, waittime*1000, 10000);// milliseconds
					}
					
					if (refund_handle_enable==1 && !refundhandler.equals(""))
					{
						TimerBusinessHandler refundbusinesshandler=(TimerBusinessHandler) Class.forName(refundhandler).newInstance();
						refundbusinesshandler.setParams(handlerparams);
						timer.schedule(refundbusinesshandler, waittime*1000, 10000);// milliseconds
					}
					
					if (goods_handle_enable==1 && !goodshandler.equals(""))
					{
						TimerBusinessHandler goodsbusinesshandler=(TimerBusinessHandler) Class.forName(goodshandler).newInstance();
						goodsbusinesshandler.setParams(handlerparams);
						timer.schedule(goodsbusinesshandler, waittime*1000, 10000);// milliseconds
					}
					
					if (delivery_handle_enable==1 && !deliveryhandler.equals(""))
					{
						TimerBusinessHandler deliverybusinesshandler=(TimerBusinessHandler) Class.forName(deliveryhandler).newInstance();
						deliverybusinesshandler.setParams(handlerparams);
						timer.schedule(deliverybusinesshandler, waittime*1000, 10000);// milliseconds
					}
					
					if (status_handle_enable==1 && !statushandler.equals(""))
					{
						TimerBusinessHandler statusbusinesshandler=(TimerBusinessHandler) Class.forName(statushandler).newInstance();
						statusbusinesshandler.setParams(handlerparams);
						timer.schedule(statusbusinesshandler, waittime*1000, 10000);// milliseconds
					}
					
					if (stock_handle_enable==1 && !stockhandler.equals(""))
					{
						TimerBusinessHandler stockbusinesshandler=(TimerBusinessHandler) Class.forName(stockhandler).newInstance();
						stockbusinesshandler.setParams(handlerparams);
						timer.schedule(stockbusinesshandler, waittime*1000, 10000);// milliseconds
					}
					
					if (customerorder_handle_enable==1 && !customerorderhandler.equals(""))
					{
						TimerBusinessHandler customerorderbusinesshandler=(TimerBusinessHandler) Class.forName(customerorderhandler).newInstance();
						customerorderbusinesshandler.setParams(handlerparams);
						timer.schedule(customerorderbusinesshandler, waittime*1000, 10000);// milliseconds
					}
					
					if (refundorder_handle_enable==1 && !refundorderhandler.equals(""))
					{
						TimerBusinessHandler refundorderbusinesshandler=(TimerBusinessHandler) Class.forName(refundorderhandler).newInstance();
						refundorderbusinesshandler.setParams(handlerparams);
						timer.schedule(refundorderbusinesshandler, waittime*1000, 10000);// milliseconds
					}
				}
				
			}
		
		}catch(Exception e)
		{	
			Log.error("Service Listener", "启动监控服务出错:"+e.getMessage());
			
			try {
				if (conn != null)
				{
					conn.close();
				}
			} catch (Exception closeexception) {
				Log.error("Service Listener", "关闭数据库连接失败");
			}			
		}
		
		finally {			
			try {
				if (conn != null)
				{
					conn.close();
				}
			} catch (Exception e) {
				Log.error("Service Listener", "关闭数据库连接失败");
			}
		}

	}

}
