package com.wofu.ecommerce.yhd;


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
import com.wofu.ecommerce.yhd.utils.Utils;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;

public class GetOrders extends Thread {

	private static String jobname = "获取一号店订单作业";
	
	private static long daymillis=24*60*60*1000L;
	private static String  orderLineStatus="ORDER_WAIT_PAY,ORDER_PAYED,"
	+"ORDER_WAIT_SEND,ORDER_ON_SENDING,ORDER_RECEIVED,ORDER_FINISH,ORDER_GRT,ORDER_CANCEL";
	
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
						com.wofu.ecommerce.yhd.Params.dbname);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.yhd.Params.waittime * 1000))		
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
	private void getOrderList(Connection conn) throws Exception
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
					orderlistparams.put("appKey", Params.app_key);
					orderlistparams.put("sessionKey", Params.token);
			        orderlistparams.put("format", Params.format);
			        orderlistparams.put("method", "yhd.orders.get");
			        orderlistparams.put("ver", Params.ver);
			        orderlistparams.put("dateType", "5");
			        orderlistparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("startTime", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("endTime", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			       
			        orderlistparams.put("orderStatusList", orderLineStatus);
			        orderlistparams.put("curPage", String.valueOf(pageno));
			        orderlistparams.put("pageRows", "50");
			        
					String responseOrderListData = Utils.sendByPost(orderlistparams, Params.app_secret, Params.url);
					Log.info("返回的数据"+responseOrderListData);
					
					JSONObject responseproduct = new JSONObject(responseOrderListData);
					if (responseOrderListData.indexOf("errInfoList")>=0)
					{
						JSONArray errinfolist=responseproduct.getJSONObject("response").optJSONObject("errInfoList").optJSONArray("errDetailInfo");
						String errdesc="";
						
						for(int j=0;j<errinfolist.length();j++)
						{
							JSONObject errinfo=errinfolist.getJSONObject(j);
							
							errdesc=errdesc+" "+errinfo.getString("errorDes"); 
												
						}
						if (errdesc.indexOf("订单列表信息不存在")>=0)
						{
							if (pageno==1L)		
							{
								try
								{
									//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
									if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
											compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
									{
										try
					                	{
											String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
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
						}
						Log.error(jobname, "取订单列表失败:"+errdesc);
						k=10;
						break;
					}
	
					int totalCount=responseproduct.getJSONObject("response").getInt("totalCount");
					int errorCount=responseproduct.getJSONObject("response").getInt("errorCount");
					
					if (errorCount>0)
					{
						String errdesc="";
						JSONArray errlist=responseproduct.getJSONObject("response").getJSONObject("errInfoList").getJSONArray("errDetailInfo");
						for(int j=0;j<errlist.length();j++)
						{
							JSONObject errinfo=errlist.getJSONObject(j);
							
							errdesc=errdesc+" "+errinfo.getString("errorDes"); 
												
						}
						
						if (errdesc.indexOf("订单列表信息不存在")<0)
						{
							Log.error(jobname, errdesc);
							k=10;
							break;
						}
					}
							
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
		
					JSONArray orderlist=responseproduct.getJSONObject("response").getJSONObject("orderList").getJSONArray("order");
					System.out.println("订单一共有 "+orderlist.length()+"条");
					for(int j=0;j<orderlist.length();j++)
					{
						System.out.println("当前订单"+j);
						JSONObject order=orderlist.getJSONObject(j);
						Order o=OrderUtils.getOrderByID(order.getString("orderCode"),Params.app_key,Params.token,Params.format,Params.ver);
						Log.info(o.getOrderCode()+" "+o.getOrderStatus()+" "+Formatter.format(o.getUpdateTime(),Formatter.DATE_TIME_FORMAT));
						
						 //*1、如果状态为等待卖家发货则生成接口订单
						 //*2、删除等待买家付款时的锁定库存 
						 		
						String sku;
						String sql="";
						if (o.getOrderStatus().equals("ORDER_PAYED") 
								|| o.getOrderStatus().equals("ORDER_TRUNED_TO_DO")
								|| o.getOrderStatus().equals("ORDER_CAN_OUT_OF_WH"))
						{	
							
							if (!OrderManager.isCheck("检查一号店订单", conn, o.getOrderCode()))
							{
								if (!OrderManager.TidLastModifyIntfExists("检查一号店订单", conn, o.getOrderCode(),o.getUpdateTime()))
								{
									OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username);
									
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getOuterId();
										
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrderCode(),sku);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrderStatus(),o.getOrderCode(), sku, -item.getOrderItemNum(),false);
									}
								}
							}
	
							//等待买家付款时记录锁定库存
						}
						
						
						else if (o.getOrderStatus().equals("ORDER_WAIT_PAY"))
						{						
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuterId();
							
								StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrderCode(), sku, item.getOrderItemNum());
								StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrderStatus(),o.getOrderCode(), sku, -item.getOrderItemNum(),false);
							}
							//付款以后用户退款成功，交易自动关闭
							//释放库存,数量为负数						
						}else if (o.getOrderStatus().equals("ORDER_CANCEL"))
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuterId();
					
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrderCode(), sku);
								if (StockManager.WaitPayStockExists(jobname,conn,Params.tradecontactid, o.getOrderCode(), sku))//有获取到等待买家付款状态时才加库存
									StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrderStatus(),o.getOrderCode(), sku, item.getOrderItemNum(),false);
							}
							
							
				
						}
						else if (o.getOrderStatus().equals("ORDER_FINISH"))
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuterId();
					
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrderCode(), sku);								
							}
			
						}
						else if (o.getOrderStatus().equals("ORDER_CUSTOM_CALLTO_RETUR")
							||o.getOrderStatus().equals("ORDER_CUSTOM_CALLTO_CHANGE")
							||o.getOrderStatus().equals("ORDER_RETURNED")
							||o.getOrderStatus().equals("ORDER_CHANGE_FINISHED"))
						{
							
							OrderUtils.getRefund(conn,Params.tradecontactid,o);
								
				
						}
						
						//更新同步订单最新时间
		                if (o.getUpdateTime().compareTo(modified)>0)
		                {
		                	modified=o.getUpdateTime();
		                }
					}
					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(totalCount/50.0))).intValue()) break;
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
