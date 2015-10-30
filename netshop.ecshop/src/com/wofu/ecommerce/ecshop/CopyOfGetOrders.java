package com.wofu.ecommerce.ecshop;
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
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ecshop.util.CommHelper;
public class CopyOfGetOrders extends Thread {

	private static String jobname = "获取ecshop订单作业";
	
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	private static String lasttimeReturnconfvalue=Params.username+"取退货订单最新时间";
	
	private static long daymillis=24*60*60*1000L*365*10;
	
	private String lasttime;
	private String lastReturntime;
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	public void run() {

		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;

			try {
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	
				Ecshop.setCurrentDate_getOrder(new Date());
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
				lastReturntime=PublicUtils.getConfig(connection,lasttimeReturnconfvalue,"");
				/**
				 * 订单状态 10待发货，20已发货，21部分发货，30交易成功 ，40交易关闭
				 */
				//获取ecshop新订单 
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
	
	
	//获取ecshop新订单
	public void getOrderList(Connection conn) throws Exception
	{
		Log.info("获取订单开始!");
		int pageIndex = 1 ;
		boolean hasNextPage = true ;	
		
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//方法名
					String apimethod="search_order_list";
					HashMap<String,Object> reqMap = new HashMap<String,Object>();
			        reqMap.put("last_modify_st_time", startdate.getTime()/1000L);
			        reqMap.put("last_modify_en_time",enddate.getTime()/1000L);
			        reqMap.put("pages", String.valueOf(pageIndex));
			        reqMap.put("counts", Params.pageSize);
			        reqMap.put("return_data", "json");
			        reqMap.put("act", apimethod);
			        reqMap.put("api_version", "1.0");
			        
			        Log.info("第"+pageIndex+"页");
			        Log.info(Params.url);
					String responseText = CommHelper.doRequest(reqMap,Params.url);
					Log.info("返回数据为:　"+responseText);
					//把返回的数据转成json对象
					JSONObject responseObj= new JSONObject(responseText.replaceAll(":null",":\"\""));
					  //sn_error
					if(!"success".equals(responseObj.getString("result"))){   //发生错误
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if("biz.handler.data-get:no-result".equals(operCode)){ //没有结果
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
								return;
							}catch(ParseException e)
							{
								Log.error(jobname, "不可用的日期格式!"+e.getMessage());
							}
							Log.info("没有可用的订单!");
						}else{
							Log.warn("取订单出错了,错误码: "+operCode);
						}
						
						break;
					}
					
					
					
					JSONObject orderInfos = responseObj.getJSONObject("info");

					//总页数
					String orderTotal = String.valueOf(orderInfos.getString("counts"));
					if (orderTotal==null || orderTotal.equals("") || orderTotal.equals("0"))
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
					int orderTotaltemp = Integer.parseInt(orderTotal);
					int pageTotalTemp  = orderTotaltemp<Integer.parseInt(Params.pageSize)?1:orderTotaltemp/Integer.parseInt(Params.pageSize)==0?Integer.parseInt(Params.pageSize):orderTotaltemp/Integer.parseInt(Params.pageSize)+1;
					String pageTotal =String.valueOf(pageTotalTemp);
					Log.info("总订单数为： "+orderTotal);
					Log.info("总页数为： "+pageTotal);
					//订单元素
					JSONArray ordersList = orderInfos.getJSONArray("data_info");
					for(int i = 0 ; i< ordersList.length() ; i++)
					{	//某个订单
						JSONObject orderInfo = ordersList.getJSONObject(i);
						int returnOrderCount =0;
						//订单编号 
						String orderCode = (String)orderInfo.get("order_sn");
						//订单商品集合
						JSONArray items = orderInfo.getJSONArray("shop_info");
						//构造一个订单对象
						Order o = new Order();
						o.setObjValue(o, orderInfo);
						o.setFieldValue(o, "shop_info", items);
						Log.info("发货状态:　"+o.getShipping_status()+"付款状态: "+o.getPay_status());
						if(o != null)
						{	
							if("0".equals(o.getShipping_status()) && "2".equals(o.getPay_status())){   //正常订单
								Log.info("订单号:　"+o.getOrder_sn());
								//如果是等待发货订单，创建接口订单成功，减少其它店的库存
									if (!OrderManager.isCheck(jobname, conn, orderCode))
									{
										if (!OrderManager.TidLastModifyIntfExists(jobname, conn, orderCode,new Date(o.getAdd_time()*1000L)))
										{
											try
											{
												OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
												for(Iterator ito=o.getShop_info().getRelationData().iterator();ito.hasNext();)
												{
													OrderItem item=(OrderItem) ito.next();
													String sku = item.getProduct_sn() ;
													StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
													long qty= (long)item.getGoods_number();
													Log.info(qty+"");
													//在ecs_rationconfig表中存在机构添加一条库存同步记录(不包括自己）
													StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, "未发货",o.getOrder_sn(), sku, qty,false);
													Log.info("ghost");
												}
												
											} catch(SQLException sqle)
											{
												throw new JException("生成接口订单出错!" + sqle.getMessage());
											}
										}
									}     // 订单状态 10待发货，20已发货，21部分发货，30交易成功 ，40交易关闭
							}

								//未付款
								else if("0".equals(o.getPay_status()))
								{
									for(Iterator ito=o.getShop_info().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getProduct_sn() ;
										long qty= (long)item.getGoods_number();
										//
										StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku,qty);
										
									}
									
									
								}else if("4".equals(o.getOrder_status())){  //退货处理
									
								}
						}
								
							//如果当前订单时间大于开始取订单时间，则更新下次取订单时间(现在取订单列表最后修改时间)
							//更新同步订单最新时间
						Log.info("o's add_time: "+new Date(o.getAdd_time()*1000L));
							Log.info("modified: "+modified);
			                if (new Date(o.getAdd_time()*1000L).compareTo(modified)>0)
			                {
			                	modified=new Date(o.getAdd_time()*1000L);
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
		Log.info("本次取ecshop订单任务处理完毕!");
	}

	
	
	
}