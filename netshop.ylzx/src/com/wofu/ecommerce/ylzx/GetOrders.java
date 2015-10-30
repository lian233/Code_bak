package com.wofu.ecommerce.ylzx;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ylzx.utils.AuthTokenManager;
import com.wofu.ecommerce.ylzx.utils.Content;
import com.wofu.ecommerce.ylzx.utils.Utils;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;
public class GetOrders extends Thread {
	private static String jobname = "获取银联在线商城订单作业";
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
			AuthTokenManager authTokenManager;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.ylzx.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				authTokenManager = new AuthTokenManager(Params.app_key,Params.app_secret,Params.ver
						,Params.user_name,Params.password);
				authTokenManager.init();
				getOrderList(connection,authTokenManager);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				authTokenManager=null;
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.ylzx.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	
	/*
	 * 获取一天之类的所有订单
	 */
	private void getOrderList(Connection conn,AuthTokenManager authTokenManager) throws Exception
	{		
		long pageno=1L;
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
					orderlistparams.put("oauth_consumer_key", Params.app_key);
					orderlistparams.put("oauth_signature_method", Content.HMAC_SHA1);
					orderlistparams.put("oauth_timestamp", String.valueOf(new Date().getTime()/1000L));
					orderlistparams.put("oauth_nonce", String.valueOf(System.currentTimeMillis()));
					orderlistparams.put("oauth_version", Params.ver);
					orderlistparams.put("fields", "orders");
			        orderlistparams.put("time_reference", "2");
			        orderlistparams.put("start_created", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("end_created", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("page_no", String.valueOf(pageno));
			        orderlistparams.put("page_size", "50");
			        orderlistparams.put("oauth_token", authTokenManager.getToken());
					String responseOrderListData = Utils.sendByPost(Content.getOrder_url,
							orderlistparams,"POST",Params.app_secret,authTokenManager.getOauth_token_secret());
					Log.info(responseOrderListData);
					
					Document doc = DOMHelper.newDocument(responseOrderListData);
					Element elementOrder = doc.getDocumentElement();
					String status = DOMHelper.getSubElementVauleByName(elementOrder, "status").trim();
					
					if (!"200".equals(status))
					{
						String errmsg = DOMHelper.getSubElementVauleByName(elementOrder, "reason").trim();
						Log.error(jobname, errmsg);
						k=10;
						break;
					
					}
					Element body = DOMHelper.getSubElementsByName(elementOrder, "body")[0];
					String totalOrder = DOMHelper.getSubElementVauleByName(body, "totalResults");
					if ("0".equals(totalOrder))
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
					
		
					Element[] orders  = DOMHelper.getSubElementsByName(body, "order");
					for(Element e:orders)
					{
						Order o=OrderUtils.getOrder(e);
						Log.info("-----");
						OrderUtils.setOrderItem(o,e,authTokenManager);
				
						Log.info(o.getOrder_sn()+" "+o.getStatus()+" "+Formatter.format(o.getPay_time(),Formatter.DATE_TIME_FORMAT));
						
						 //*1、如果状态为等待卖家发货则生成接口订单
						 //*2、删除等待买家付款时的锁定库存 
						 		
						String sku;
						String sql="";
						if (o.getStatus().equals("20"))
						{	
							
							if (!OrderManager.isCheck("检查银联在线商城订单", conn, o.getOrder_sn()))
							{
								if (!OrderManager.TidLastModifyIntfExists("检查银联在线商城订单", conn, o.getOrder_sn(),o.getPay_time()))
								{
									OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username);
									
									for(Iterator ito=o.getOrderItems().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getSku(); 
										
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrder_sn(),sku);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getOrder_sn(), sku, -item.getQuantity(),false);
									}
								}
							}
	
							//等待买家付款时记录锁定库存
						}
						
						
						else if (o.getStatus().equals("11"))
						{						
							for(Iterator ito=o.getOrderItems().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getSku();
							
								StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrder_sn(), sku, item.getQuantity());
								StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getOrder_sn(), sku, -item.getQuantity(),false);
							}
							
							 
				  
							//付款以后用户退款成功，交易自动关闭
							//释放库存,数量为负数						
						}else if (o.getStatus().equals("0"))
						{
							for(Iterator ito=o.getOrderItems().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getSku();
					
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrder_sn(), sku);
								if (StockManager.WaitPayStockExists(jobname,conn,Params.tradecontactid, o.getOrder_sn(), sku))//有获取到等待买家付款状态时才加库存
									StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getOrder_sn(), sku, item.getQuantity(),false);
							}
							
							
				
						}
						else if (o.getStatus().equals("40"))
						{
							for(Iterator ito=o.getOrderItems().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getSku();
					
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrder_sn(), sku);								
							}
			
						}
						else if (o.getStatus().equals("ORDER_CUSTOM_CALLTO_RETUR")
							||o.getStatus().equals("ORDER_CUSTOM_CALLTO_CHANGE")
							||o.getStatus().equals("ORDER_RETURNED")
							||o.getStatus().equals("ORDER_CHANGE_FINISHED"))
						{
							
							//OrderUtils.getRefund(conn,Params.tradecontactid,o);
								
				
						}
						
						//更新同步订单最新时间
		                if (o.getPay_time().compareTo(modified)>0)
		                {
		                	modified=o.getPay_time();
		                }
					}
						
						
						
					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(Integer.parseInt(totalOrder)/50.0))).intValue()) break;
					
					pageno++;
					
					
				}
				/**
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
				**/
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
