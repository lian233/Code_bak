package com.wofu.ecommerce.dangdang;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.dangdang.util.CommHelper;
public class GetOrders extends Thread {
	private static String jobName = "获取当当订单作业";
	
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	
	private static long daymillis=24*60*60*1000L;
	
	private String lasttime;
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	public void run() {

		Log.info(jobName, "启动[" + jobName + "]模块");
		do {
			Connection connection = null;

			try {
				Dangdang.setCurrentDate_getOrder(new Date());
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	

				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
	
				//获取当当新订单 订单状态 100：等待到款 101：等待发货 300：已发货 400：已送达 1000：交易成功 -100：取消 1100：交易失败
				getOrderList(connection) ;
				
			} catch (Throwable e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Throwable e1) {
					Log.error(jobName, "回滚事务失败");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				try {
					if (connection != null)
						connection.close();
				} catch (Throwable e) {
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	
	//获取当当新订单
	public void getOrderList(Connection conn) throws Throwable
	{
		int pageIndex = 1 ;
		boolean hasNextPage = true ;	
		
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		Log.info("本次获取当当订单开始");
		for (int k=0;k<5;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//方法名
					String methodName="dangdang.orders.list.get";
					//生成验证码 --md5;加密
					Date temp = new Date();
					String sign = CommHelper.getSign(Params.app_Secret, Params.app_key, methodName, Params.session,temp) ;
					Hashtable<String, String> params = new Hashtable<String, String>() ;
					params.put("sign", sign) ;
					params.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
					params.put("app_key",Params.app_key);
					params.put("method",methodName);
					params.put("format","xml");
					params.put("session",Params.session);
					params.put("sign_method","md5");
					params.put("lastModifyTime_end", URLEncoder.encode(Formatter.format(enddate, Formatter.DATE_TIME_FORMAT), Params.encoding)) ;
					params.put("lastModifyTime_start", URLEncoder.encode(Formatter.format(startdate, Formatter.DATE_TIME_FORMAT), Params.encoding)) ;
					params.put("p", String.valueOf(pageIndex)) ;
					params.put("pageSize", String.valueOf(Params.total)) ;
					params.put("sendMode", Params.sendMode) ;
					
					//获取返回结果
					String repsonseText = CommHelper.sendRequest(Params.url,"GET",params,"");
					repsonseText = CommHelper.filterChar(repsonseText);
					Document doc = DOMHelper.newDocument(repsonseText, Params.encoding);
					Element urlset = doc.getDocumentElement();
					
					if(DOMHelper.ElementIsExists(urlset,"Error"))
					{
						Element error = (Element) urlset.getElementsByTagName("Error").item(0);
						String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
						String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
						if(!"".equals(operCode))
						{
							Log.error("获取当当订单列表", "获取订单列表失败，操作码："+operCode+",操作结果信息："+operation);
							hasNextPage = false ;
							break ;
						}
					}
	
					Element totalInfo = (Element) urlset.getElementsByTagName("totalInfo").item(0) ;
					
					String pageTotal = DOMHelper.getSubElementVauleByName(totalInfo, "pageTotal");
					
					if (pageTotal==null || pageTotal.equals("") || pageTotal.equals("0"))
					{				
						if (n==1)		
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
				                	}catch(Throwable je)
				                	{
				                		Log.error(jobName, je.getMessage());
				                	}
								}
							}catch(Throwable e)
							{
								Log.error(jobName, "不可用的日期格式!"+e.getMessage());
							}
						}
						break;
					}
					
					NodeList ordersList = urlset.getElementsByTagName("OrderInfo") ;
					for(int i = 0 ; i< ordersList.getLength() ; i++)
					{
						try{
							Element orderInfo = (Element) ordersList.item(i) ;
							
							String orderID = DOMHelper.getSubElementVauleByName(orderInfo, "orderID") ;
							Order o = OrderUtils.getOrderByID(Params.url, orderID,Params.session,Params.app_key,Params.app_Secret) ;
							
							if(o != null)
							{
								Log.info("订单号【"+ o.getOrderID() +"】,状态【"+ OrderUtils.getOrderStateByCode(o.getOrderState()) +"】,最后修改时间【"+ Formatter.format(o.getLastModifyTime(),Formatter.DATE_TIME_FORMAT) +"】") ;

								//如果是等待发货订单，创建接口订单成功，减少其它店的库存
								if("101".equals(o.getOrderState()))
								{	//当这个订单在表customerorder和表CustomerOrderRefList都不存在相应的记录时，往下执行
									if (!OrderManager.isCheck(jobName, conn, orderID))
									{	//当这个订单在接口主表ns_customerorder中没有对应的记录（订单号和最后修改时间要相同才算存在），往下执行
										if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
										{
											try
											{	//把订单信息加入通知表it_downnote和接口主表ns_customerorder，把订单中的所有商品加入订单接口商品明细表ns_orderitem
												OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
												ArrayList<OrderItem> itemList = o.getOrderItemList() ;
												//循环这个订单的商品列表
												for(int j= 0 ; j < itemList.size() ; j ++)
												{
													String sku = itemList.get(j).getOuterItemID() ;
													//如果这个商品在未付款锁定库存表（eco_WaitPayStock）中存在记录，则把在eco_WaitPayStock表中的对应记录删除，再写一条同样的数据到未付款锁定库存备份表中(eco_WaitPayStockBak)
													//如果这个商品在未付款锁定库存表（eco_WaitPayStock）中不存在相应的记录，则查询这个商品是不是组合商品的其中一个(MultiSKURef表），如果是则先把MultiSKURef中对应的商品记录到未付款锁定库存备份表中(eco_WaitPayStockBak)，再删除未付款锁定库存表（eco_WaitPayStock）对应的记录
													StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, o.getOrderID(),sku);
												}
												
											} catch(Throwable sqle)
											{
												throw new JException("生成接口订单出错!" + sqle.getMessage());
											}
										}
									}
								}
								//等待到款,锁库存,同步其它网店
								else if("100".equals(o.getOrderState()))
								{
									ArrayList<OrderItem> itemList = o.getOrderItemList() ;
									for(int j = 0 ; j < itemList.size() ; j ++)
									{
										String sku = itemList.get(j).getOuterItemID() ;
										long qty= itemList.get(j).getOrderCount();
										
										//锁定库存，减少其它店的库存
										StockManager.addWaitPayStock(jobName, conn,Params.tradecontactid, o.getOrderID(), sku, qty);
										StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getOrderState(),o.getOrderID(), sku, -qty,false);
									}
								}
								//订单取消,增加已取消库存
								else if("-100".equals(o.getOrderState()))
								{
									ArrayList<OrderItem> itemList = o.getOrderItemList() ;
									for(int j = 0 ; j < itemList.size() ; j ++)
									{
										String sku = itemList.get(j).getOuterItemID() ;
										long qty= itemList.get(j).getOrderCount();
										//删除已锁定库存，增加其它店的库存
										StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, o.getOrderID(),sku);
										StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getOrderState(),o.getOrderID(), sku, qty,false);
									}
									
									//取消订单
									String sql="declare @ret int;  execute  @ret = IF_CancelCustomerOrder '" + orderID + "';select @ret ret;";
									int resultCode = SQLHelper.intSelect(conn, sql) ;
									//取消订单失败
									if(resultCode == 2)			
										Log.info("当当请求取消订单失败,单号:"+orderID+"");						
									else
										Log.info("当当请求取消订单成功,单号:"+orderID+"");
									
								}
								else if (o.getOrderState().equals("1000"))  //交易成功
								{
									ArrayList<OrderItem> itemList = o.getOrderItemList() ;
									for(int j = 0 ; j < itemList.size() ; j ++)
									{
										String sku = itemList.get(j).getOuterItemID() ;
							
										StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, o.getOrderID(), sku);								
									}
					
								}
								
								//如果当前订单时间大于开始取订单时间，则更新下次取订单时间(现在取订单列表最后修改时间)
								//更新同步订单最新时间
				                if (o.getLastModifyTime().compareTo(modified)>0)
				                {
				                	modified=o.getLastModifyTime();
				                }
							}
						}catch(Throwable ex){
							if(conn!=null && !conn.getAutoCommit()) conn.rollback();
							Log.error(jobName, ex.getMessage());
						}
					}
					//判断是否有下一页
					if("".equals(pageTotal) || pageTotal == null)
						pageTotal="0" ;
					if(pageIndex >= Integer.parseInt(pageTotal))
						hasNextPage = false ;
					else
						pageIndex ++ ;
					
					n++;
				}
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(Throwable je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				
				break;
			} catch (Throwable e) 
			{
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
		Log.info("本次获取当当订单完毕");
	}

	
	
	
}