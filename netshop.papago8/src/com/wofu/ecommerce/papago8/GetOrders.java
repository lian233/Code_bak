package com.wofu.ecommerce.papago8;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.papago8.util.CommHelper;
public class GetOrders extends Thread {

	private static String jobname = "获取papago8订单作业";
	
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	
	private static long daymillis=24*60*60*1000L;
	
	private String lasttime;
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	public void run() {

		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;

			try {
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	

				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
				/**
				 * 订单状态 10待发货，20已发货，21部分发货，30交易成功 ，40交易关闭
				 */
				//获取papago8新订单 
				getOrderList(connection) ;
				
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	
	//获取papago8新订单
	public void getOrderList(Connection conn) throws Exception
	{
		int pageIndex = 1 ;  //papago8的订单从0页算起
		boolean hasNextPage = true ;	
		
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				while(hasNextPage)
				{
					
					String startdate=Formatter.format(new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L),Formatter.DATE_TIME_FORMAT);
					String enddate=Formatter.format(new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis),Formatter.DATE_TIME_FORMAT);
					//方法名
					String apimethod="QueryOrder.aspx?";
					HashMap<String,Object> map = new HashMap<String,Object>();
					map.put("Start_modified",URLEncoder.encode(startdate,"utf-8"));
			        map.put("End_modified",URLEncoder.encode(enddate,"utf-8"));
			        map.put("page", String.valueOf(pageIndex));
			        map.put("Pagesize", Params.pageSize);
			        map.put("key", Params.Key);
			        map.put("apimethod", apimethod);
			        map.put("format", "json");
			        //Log.info("url: "+Params.url);

			        //发送请求
					String responseText = CommHelper.doGet(map,Params.url);
					responseText = responseText.replaceAll("null", "\"\"");
					
					Log.info("9990: "+new String(responseText.getBytes()));
					//把返回的数据转成json对象
					JSONObject responseObj=new JSONObject(responseText).getJSONObject("trades_response");
					if("0".equals(responseObj.getString("total_results"))){   //code为1代表没有数据返回
						Log.info("本次取不取订单");
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
						break;
					}
				
					/*JSONObject totalInfo = responseObj.getJSONObject("sn_head");
					if(totalInfo==null){
						String operCode=(String)responseObj.getJSONObject("sn_error").get("error_code");
						Log.error("获取papago8订单列表", "获取订单列表失败，操作码："+operCode);
						hasNextPage = false ;
						break ;
					}*/

					//总页数
					int orderNum= Integer.parseInt(responseObj.getString("total_results"));
					int  pageTotal =orderNum>=Integer.parseInt(Params.pageSize)?(orderNum %Integer.parseInt(Params.pageSize)==0?orderNum /Integer.parseInt(Params.pageSize):(orderNum /Integer.parseInt(Params.pageSize)+1)):1;
					Log.info("总页数： "+pageTotal);
					if (pageTotal==0)
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
						break;
					}
					//订单元素
					JSONArray ordersList = responseObj.getJSONObject("trades").getJSONArray("trade");
					for(int i = 0 ; i< ordersList.length() ; i++)
					{	//某个订单
						JSONObject orderInfo = ordersList.getJSONObject(i);
						
						//订单编号 
						//订单商品集合
						JSONArray items = orderInfo.getJSONObject("orders").getJSONArray("order");
						
						//构造一个订单对象
						Order o = new Order();
						o.setObjValue(o, orderInfo);
						o.setFieldValue(o, "orderItemList", items);
						
						Date createTime = o.getModified();
						String orderCode = o.getTid();
						if(o != null)
						{	
							Log.info("订单号【"+ o.getTid() +"】,状态【"+ o.getStatus() +"】") ;
							  //正常订单
								//如果是等待发货订单，创建接口订单成功，减少其它店的库存
								if("3".equals(o.getStatus()))
								{
									if (!OrderManager.isCheck(jobname, conn, orderCode))
									{
										if (!OrderManager.TidLastModifyIntfExists(jobname, conn, orderCode,createTime))
										{
											try
											{
												OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
												for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
												{
													OrderItem item=(OrderItem) ito.next();
													String sku = item.getOuter_sku_id() ;
													//没有等待付款的状态 不需要删除未付款锁定的库存/
													StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
													long qty= (long)item.getNum();
													//在ecs_rationconfig表中存在机构添加一条库存同步记录(不包括自己）
													StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getTid(), sku, qty,false);
												}
												
											} catch(SQLException sqle)
											{
												throw new JException("生成接口订单出错!" + sqle.getMessage());
											}
										}
									}     // 订单状态 10待发货，20已发货，21部分发货，30交易成功 ，40交易关闭
								}

								//40交易关闭
								else if("8".equals(o.getStatus()))
								{
									Log.info("订单号: "+o.getTid()+", 交易取消！");
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getOuter_sku_id() ;
										long qty= (long)(item.getNum());
										//删除已锁定库存，增加其它店的库存
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getTid(), sku, qty,false);
									}
									
									//取消订单
									String sql="declare @ret int;  execute  @ret = IF_CancelCustomerOrder '" + orderCode + "';select @ret ret;";
									int resultCode = SQLHelper.intSelect(conn, sql) ;
									//取消订单失败
									if(resultCode == 2)			
										Log.info("papago8请求取消订单失败,单号:"+orderCode+"");						
									else
										Log.info("papago8请求取消订单成功,单号:"+orderCode+"");
									
								}
								else if ("9".equals(o.getStatus()))  //交易成功
								{
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getOuter_sku_id() ;
							
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode, sku);								
									}
					
								}else if ("1".equals(o.getStatus()))  //交易成功
								{
								 
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getOuter_sku_id() ;
										long qty= (long)(item.getNum());
							
										StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(orderCode), sku, qty);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),String.valueOf(orderCode), sku, -qty,false);								
									}
					
								}
							}
							
							//如果当前订单时间大于开始取订单时间，则更新下次取订单时间(现在取订单列表最后修改时间)
							//更新同步订单最新时间
							
			                if (createTime.compareTo(modified)>0)
			                {
			                	modified=createTime;
			                }
						}
					//判断是否有下一页
					
					if(pageIndex >= pageTotal)
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
	            	}catch(JException je)
	            	{
	            		Log.error(jobname,je.getMessage());
	            	}
				}
				
				break;
			} catch (Exception e) 
			{
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(jobname+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
		Log.info("本次取papago8订单任务处理完毕!");
	}

	
	
	
}