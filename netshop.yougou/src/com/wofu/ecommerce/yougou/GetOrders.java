package com.wofu.ecommerce.yougou;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.yougou.utils.Utils;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;
public class GetOrders extends Thread {

	private static String jobname = "获取优购订单作业";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.yougou.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getOrderList(connection);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.yougou.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	
	/*
	 * 获取一天之内未发货的所有订单
	 */
	private void getOrderList(Connection conn) throws Exception
	{		
		int pageno=1;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //系统级参数设置
					orderlistparams.put("app_key", Params.app_key);
			        orderlistparams.put("format", Params.format);
			        orderlistparams.put("method", "yougou.order.query");
			        orderlistparams.put("sign_method", "md5");
			        orderlistparams.put("app_version", Params.ver);
			        orderlistparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("start_created", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("end_created", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			       
			        orderlistparams.put("order_status", "1");
			        orderlistparams.put("page_index", String.valueOf(pageno));
			        orderlistparams.put("page_size", Params.pageSize);
			        
					String responseOrderListData = Utils.sendByPost(orderlistparams, Params.app_secret, Params.url);
					Log.info("responseOrderListData: "+responseOrderListData);
					
					JSONObject responseproduct = new JSONObject(responseOrderListData).getJSONObject("yougou_order_query_response");
					if (!"200".equals(responseproduct.optString("code")))
					{
						String errdesc=responseproduct.optString("message");
						Log.error(jobname, "取订单列表失败:"+errdesc);
						k=10;
						break;
					}
	
					int totalCount=responseproduct.getInt("total_count");
					
					if (totalCount==0)
					{				
						if (pageno==1L)		
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(jobname, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobname, "不可用的日期格式!"+e.getMessage());
							}
						}
						k=10;
						break;
					}
		
					JSONArray orderlist=responseproduct.getJSONArray("items");
					//Log.info("订单总数: "+orderlist.length());
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						JSONArray items = order.getJSONArray("item_details");
						Order o=new Order();
						o.setObjValue(o, order);
						o.setFieldValue(o,"orderItem",items);
						
						Log.info(o.getOrder_sub_no()+" "+o.getOrder_status_name()+" "+Formatter.format(o.getModify_time(),Formatter.DATE_TIME_FORMAT));
						
						 //*1、如果状态为等待卖家发货则生成接口订单
						 //*2、删除等待买家付款时的锁定库存 
						 		
						String sku;
						String sql="";
						if (o.getOrder_status_name().equals("待发货"))
						{	
							
							if (!OrderManager.isCheck("检查优购订单", conn, o.getOrder_sub_no()))
							{
								if (!OrderManager.TidLastModifyIntfExists("检查优购订单", conn, o.getOrder_sub_no(),o.getModify_time()))
								{
									OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username);
									
									for(Iterator ito=o.getOrderItem().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getLevel_code();
										
										//StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrder_sub_no(),sku);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrder_status_name(),o.getOrder_sub_no(), sku, -item.getCommodity_num(),false);
									}
								}
							}
	
							//等待买家付款时记录锁定库存
						}
						//更新同步订单最新时间
		                if (o.getModify_time().compareTo(modified)>0)
		                {
		                	modified=o.getModify_time();
		                }
					}
						
					
					int totalPage = totalCount % Integer.parseInt(Params.pageSize)==0?totalCount / Integer.parseInt(Params.pageSize):totalCount>Integer.parseInt(Params.pageSize)?totalCount/Integer.parseInt(Params.pageSize):1;
					Log.info("totalPage: "+totalPage);
					//判断是否有下一页
					if (pageno>=totalPage) break;
					
					pageno++;
					
				}
				
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobname,je.getMessage());
	            	}
					
				}
				Log.info(jobname,"本次任务执行完毕");
				//执行成功后不再循环
				break;
				
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	/*
	 * 获取一天之内未发货的所有订单
	 */
	private void getOrderFinishList(Connection conn) throws Exception
	{		
		int pageno=1;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //系统级参数设置
					orderlistparams.put("app_key", Params.app_key);
			        orderlistparams.put("format", Params.format);
			        orderlistparams.put("method", "yougou.order.query");
			        orderlistparams.put("sign_method", "md5");
			        orderlistparams.put("app_version", Params.ver);
			        orderlistparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("start_created", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("end_created", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			       
			        orderlistparams.put("order_status", "2");
			        orderlistparams.put("page_index", String.valueOf(pageno));
			        orderlistparams.put("page_size", Params.pageSize);
			        
					String responseOrderListData = Utils.sendByPost(orderlistparams, Params.app_secret, Params.url);
					Log.info("responseOrderListData: "+responseOrderListData);
					
					JSONObject responseproduct = new JSONObject(responseOrderListData).getJSONObject("yougou_order_query_response");
					if (!"200".equals(responseproduct.optString("code")))
					{
						String errdesc=responseproduct.optString("message");
						Log.error(jobname, "取订单列表失败:"+errdesc);
						k=10;
						break;
					}
	
					int totalCount=responseproduct.getInt("total_count");
					
					if (totalCount==0)
					{				
						if (pageno==1L)		
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(jobname, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobname, "不可用的日期格式!"+e.getMessage());
							}
						}
						k=10;
						break;
					}
		
					JSONArray orderlist=responseproduct.getJSONArray("items");
					
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						JSONArray items = order.getJSONArray("item_details");
						Order o=new Order();
						o.setObjValue(o, order);
						o.setFieldValue(o,"item_details",items);
						
						Log.info(o.getOrder_sub_no()+" "+o.getOrder_status_name()+" "+Formatter.format(o.getModify_time(),Formatter.DATE_TIME_FORMAT));
						
						 //*1、如果状态为等待卖家发货则生成接口订单
						 //*2、删除等待买家付款时的锁定库存 
						 		
						String sku;
						String sql="";
						if (o.getOrder_status_name().equals("待发货"))
						{	
							
							if (!OrderManager.isCheck("检查优购订单", conn, o.getOrder_sub_no()))
							{
								if (!OrderManager.TidLastModifyIntfExists("检查优购订单", conn, o.getOrder_sub_no(),o.getModify_time()))
								{
									OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username);
									
									for(Iterator ito=o.getOrderItem().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getLevel_code();
										
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrder_sub_no(),sku);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrder_status_name(),o.getOrder_sub_no(), sku, -item.getCommodity_num(),false);
									}
								}
							}
	
							//等待买家付款时记录锁定库存
						}
						
						//更新同步订单最新时间
		                if (o.getModify_time().compareTo(modified)>0)
		                {
		                	modified=o.getModify_time();
		                }
					}
						
					
					int totalPage = totalCount % Integer.parseInt(Params.pageSize)==0?totalCount / Integer.parseInt(Params.pageSize):totalCount>Integer.parseInt(Params.pageSize)?totalCount/Integer.parseInt(Params.pageSize):1;
					//判断是否有下一页
					if (pageno>=totalPage) break;
					
					pageno++;
					
				}
				
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobname,je.getMessage());
	            	}
					
				}
				
				//执行成功后不再循环
				break;
				
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
