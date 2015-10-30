package com.wofu.ecommerce.coo8;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import com.coo8.api.Coo8Client;
import com.coo8.api.DefaultCoo8Client;
import com.coo8.api.request.order.OrdersGetRequest;
import com.coo8.api.response.order.OrdersGetResponse;
import com.coo8.open.order.Order;
import com.coo8.open.order.OrderDetail;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;


public class getOrders extends Thread {
	private static String jobName = "获取库巴订单作业";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;
	
	public void run() {
		
		Log.info(jobName, "启动[" + jobName + "]模块");
		do {		
			Connection connection = null;
			is_importing = true;
			try {			
				
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
											
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getOrderList(connection);
				
				
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "回滚事务失败");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (60 * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	/*
	 * 获取一天之内的所有订单
	 */
	private void getOrderList(Connection conn) throws Exception
	{		
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<5;)
		{
			try
			{
				while(hasNextPage)
				{
					Date startDate = new Date();
					Coo8Client coo8=new DefaultCoo8Client(Params.url,Params.appKey,Params.secretKey);
					OrdersGetRequest orderrequest=new OrdersGetRequest();
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					orderrequest.setStartDate(startdate);
					orderrequest.setEndDate(enddate);
					orderrequest.setPageNo(pageIndex);
					orderrequest.setPageSize(20);
					OrdersGetResponse response=coo8.execute(orderrequest);
					System.out.println(response.toString());
					if (response.getMsg()!=null)
					{
						hasNextPage = false ;
						Log.warn(jobName,"取订单失败,错误信息:"+response.getMsg());
						break ;
					}
						
					//获取总条数
					int total=response.getTotalResult();
						//总页数
					int pageTotal=Double.valueOf(Math.ceil(total/20.0)).intValue();
						
					//返回的订单列表资料
					List<Order> orders=response.getOrders();
					//Log.info("订单总数为:　"+orders.size());
					if(orders==null){
						if (pageIndex==1)		
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
				                	Log.error(jobName, je.getMessage());
				                }
							}
								
							Log.info(jobName,"不存在需要处理的订单!");
							hasNextPage=false;								
							break ;
						}
					}
						
					boolean isNeedDealList=false;
					int ignoreNum=0;  //统计取回的订单中的旧订单总数--如果全部是旧订单，则更新取订单时间为数据库时间的下一天零时
					for(int i=0; i<orders.size(); i++){
						try{
							Order order=orders.get(i);
							Log.info(Formatter.format(order.getOrderChangeTime(), Formatter.DATE_TIME_FORMAT));
							//每次都只能取一整天的数据，避免重复处理，当创建时间小于等于最新处理时间时跳过
							if (order.getOrderChangeTime().compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))<=0)	{
								ignoreNum++;
								continue;
								
							}				
								
								Log.info(order.getOrderId()+" "+order.getStatus()+" "+order.getOrderChangeTime());
								
								//是否有需要处理的订单
								isNeedDealList=true;
								/*
								 *1、如果状态为等待卖家发货则生成接口订单
								 *2、删除等待买家付款时的锁定库存 
								 */		
								String sku;
								long quantity;
								String sql="";
								if (order.getStatus().equals("PR")||order.getStatus().equals("PP"))
								{	
									
									if (!OrderManager.isCheck("检查库巴库订单", conn, order.getOrderId()))
									{
										if (!OrderManager.TidLastModifyIntfExists("检查库巴库订单", conn, order.getOrderId(),order.getOrderChangeTime()))
										{
											OrderUtils.createInterOrder(conn,order,Params.tradecontactid,Params.username);
															
											for(Iterator ito=order.getOrderDetails().iterator();ito.hasNext();)
											{
												OrderDetail detail=(OrderDetail) ito.next();
												
												sku=detail.getMainId();								
												quantity=detail.getCount();
												
												StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, order.getOrderId(),sku);
												StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, order.getStatus(),order.getOrderId(), sku, -quantity,false);
											}
										
										}
									}
			
									
								}
								//付款以前，卖家或买家主动关闭交易
								//释放等待买家付款时锁定的库存
								else if (order.getStatus().equals("CL"))
								{
									for(Iterator ito=order.getOrderDetails().iterator();ito.hasNext();)
									{
										OrderDetail detail=(OrderDetail) ito.next();
										//根据SKUID和商品ID得到sku
										sku=detail.getMainId();	
										quantity=detail.getCount();
										
										StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, order.getOrderId(), sku);
										if (StockManager.WaitPayStockExists(jobName,conn,Params.tradecontactid, order.getOrderId(), sku))//有获取到等待买家付款状态时才加库存
											StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, order.getStatus(),order.getOrderId(), sku, quantity,false);
									}					
						
								}
								//退货
								else if (order.getStatus().equals("RV")||order.getStatus().equals("RT")||order.getStatus().equals("RSC")||order.getStatus().equals("RSN")
										||order.getStatus().equals("RPP")||order.getStatus().equals("RWA")||order.getStatus().equals("RFL")||order.getStatus().equals("R2C")
										||order.getStatus().equals("RCP"))
								{
									
									OrderUtils.getRefund(jobName,conn,Params.tradecontactid,order);								
									
					
								}
								//已完成
								else if (order.getStatus().equals("DL"))
								{
									for(Iterator ito=order.getOrderDetails().iterator();ito.hasNext();)
									{
										OrderDetail detail=(OrderDetail) ito.next();
										//根据SKUID和商品ID得到sku
										sku=detail.getMainId();	
							
										StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, order.getOrderId(), sku);								
									}
					
								}
								//更新同步订单最新时间
				                if (order.getOrderChangeTime().compareTo(modified)>0)
				                {
				                	modified=order.getOrderChangeTime();
				                }
						}catch(Exception ex){
							if (conn != null && !conn.getAutoCommit())
								conn.rollback();
							Log.error(jobName, ex.getMessage());
							}
						}
						if(ignoreNum==orders.size()){  //所有的订单都是以前的订单,把取订单时间修改为数据库时间的后一天0点
							try
			                {	
								String value=Formatter.format(startDate,Formatter.DATE_TIME_FORMAT);
								PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
			                }catch(JException je)
			                {
			                	Log.error(jobName, je.getMessage());
			                }
						}
					
					//判断是否有下一页
					if(pageTotal>pageIndex)
						pageIndex ++ ;
					else
					{
						hasNextPage = false ;
						break;
					}
					
					
				}//while未		
				
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
		        	{
		        		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
		        		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
		        	}catch(JException je)
		        	{
		        		Log.error(jobName,je.getMessage());
		        	}
				}
				break;
			} catch (Exception e) {
				if (++k >= 5)
					throw e;
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
					
			
	}
	
	
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}

}
