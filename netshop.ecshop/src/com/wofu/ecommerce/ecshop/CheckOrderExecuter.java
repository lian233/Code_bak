package com.wofu.ecommerce.ecshop;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import com.wofu.base.job.Executer;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ecshop.util.CommHelper;
/**
 * 
 *检查未入订单
 *检查取消订单
 *
 */
public class CheckOrderExecuter extends Executer {

	private static String pageSize = "10" ;
	private static String jobName="定时检查ecshop未入订单";
	private static final long daymillis=2*24*60*60*1000L;
	private static String url = "" ;
	private static String tradecontactid = "" ;
	private static String username = "" ;
	
	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		pageSize = prop.getProperty("pageSize") ;
		url = prop.getProperty("url") ;
		tradecontactid = prop.getProperty("tradecontactid") ;
		username = prop.getProperty("username") ;

		try 
		{	
			//检查未入订单
			updateJobFlag(1);
			checkWaitStockOutOrders();
			//检查取消订单
			checkCancleOrders();

			UpdateTimerJob();
			
			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
	
		} catch (Exception e) {
			try {
				
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
				
				updateJobFlag(0);
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"回滚事务失败");
			}
			Log.error(jobName,"错误信息:"+Log.getErrorMessage(e));
			
			
			Log.error(jobName, "执行作业失败 [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName,"更新处理标志失败");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"关闭数据库连接失败");
			}
		}
		
	
	}
	
	/**检查未入待发货订单   orderStatus=10  等待发货 
	 *这里检查一天时间的未入订单
	**/
	public  void checkWaitStockOutOrders() throws Exception
	{
		Log.info(jobName+"任务开始!");
		Connection conn= this.getDao().getConnection();
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		
		for (int k=0;k<5;)
		{
			try 
			{
				
				while(hasNextPage)
				{
					Date startdate=new Date(new Date().getTime()-daymillis);
					Date enddate=new Date();
					//方法名
					String apimethod="search_order_list";
					HashMap<String,Object> reqMap = new HashMap<String,Object>();
			        reqMap.put("last_modify_st_time", startdate.getTime()/1000L);
			        reqMap.put("last_modify_en_time",enddate.getTime()/1000L);
			        reqMap.put("pages", String.valueOf(pageIndex));
			        reqMap.put("counts", pageSize);
			        reqMap.put("return_data", "json");
			        reqMap.put("act", apimethod);
			        reqMap.put("api_version", "1.0");
			        //发送请求
			        
			        Log.info("第"+pageIndex+"页");
					String responseText = CommHelper.doRequest(reqMap,url);
					Log.info("返回数据为:　"+responseText);
					//把返回的数据转成json对象
					JSONObject responseObj= new JSONObject(responseText.replaceAll(":null",":\"\""));
					//错误对象 
					if(!"success".equals(responseObj.getString("result"))){   //发生错误
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if(operCode.indexOf("no-result")!=-1) {  //没有数据直接退出方法体
							Log.error("获取ecshop订单列表", "获取订单列表失败，操作码："+operCode);
							return;
						}
						hasNextPage = false ;
						break ;
						
					}

					JSONObject orderInfos = responseObj.getJSONObject("info");
					String orderTotal = String.valueOf(orderInfos.getString("counts"));
					int orderTotaltemp = Integer.parseInt(orderTotal);
					int pageTotalTemp  = Double.valueOf(Math.ceil(orderTotaltemp/Double.parseDouble(Params.pageSize))).intValue();
					String pageTotal =String.valueOf(pageTotalTemp);
					
					
					Log.info("总订单数为： "+orderTotal);
					Log.info("总页数为： "+pageTotal);
					if (orderTotal==null || orderTotal.equals("") || orderTotal.equals("0"))
					{				
						break;
					}
					//订单元素
					JSONArray ordersList = orderInfos.getJSONArray("data_info");
					for(int i = 0 ; i< ordersList.length() ; i++)
					{	//某个订单
						JSONObject orderInfo = ordersList.getJSONObject(i);
						//订单编号 
						String orderCode = (String)orderInfo.get("order_sn");
						if(orderInfo.isNull("shop_info")) continue;
						//订单商品集合
						JSONArray items = orderInfo.getJSONArray("shop_info");
						//构造一个订单对象
						Order o = new Order();
						o.setObjValue(o, orderInfo);
						o.setFieldValue(o, "shop_info", items);
						Log.info("订单号: "+o.getOrder_sn()+", 发货状态:　"+o.getShipping_status()+"付款状态: "+o.getPay_status());
						if(o != null)
						{
							if("0".equals(o.getShipping_status()) && "2".equals(o.getPay_status())){   //正常订单
								Log.info("检查到一条订单："+orderCode);
								//如果是等待发货订单，创建接口订单成功，减少其它店的库存
									if (!OrderManager.isCheck(jobName, conn, orderCode))
									{
										if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderCode,new Date(o.getAdd_time()*1000L)))
										{
											try
											{
												OrderUtils.createInterOrder(conn, o, tradecontactid,username);
												for(Iterator ito=o.getShop_info().getRelationData().iterator();ito.hasNext();)
												{
													OrderItem item=(OrderItem) ito.next();
													String sku = item.getProduct_sn() ;
													StockManager.deleteWaitPayStock(jobName, conn,tradecontactid, orderCode,sku);
													long qty= (long)item.getGoods_number();
													//在ecs_rationconfig表中存在机构添加一条库存同步记录(不包括自己）
													StockManager.addSynReduceStore(jobName, conn, tradecontactid, "未发货",o.getOrder_sn(), sku, qty,false);
												}
												
											} catch(SQLException sqle)
											{
												throw new JException("生成接口订单出错!" + sqle.getMessage());
											}
										}
									}     // 订单状态 10待发货，20已发货，21部分发货，30交易成功 ，40交易关闭
							}

					}
					}
					//判断是否有下一页
					if("".equals(pageTotal) || pageTotal == null)
						pageTotal="0" ;
					if(pageIndex >= Integer.parseInt(pageTotal))
						hasNextPage = false ;
					else
						pageIndex ++ ;
					
					break;
					
				}
				Log.info(jobName+"执行完毕!");
				break;
			}catch(Exception e)
			{
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}

	}

	
	//检查取消订单  4 取消
	private  void checkCancleOrders() throws Exception
	{
		int pageIndex = 1 ;
		boolean hasNextPage = true ;	
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					Date enddate=new Date();
					Date startdate=new Date(new Date().getTime()-daymillis);
					//方法名
					String apimethod="search_order_list";
					HashMap<String,Object> reqMap = new HashMap<String,Object>();
			        reqMap.put("last_modify_st_time", startdate.getTime()/1000L);
			        reqMap.put("last_modify_en_time",enddate.getTime()/1000L);
			        reqMap.put("pages", String.valueOf(pageIndex));
			        reqMap.put("counts", pageSize);
			        reqMap.put("return_data", "json");
			        reqMap.put("act", apimethod);
			        reqMap.put("api_version", "1.0");
			        
			        Log.info("第"+pageIndex+"页");
					String responseText = CommHelper.doRequest(reqMap,url);
					Log.info("返回数据为:　"+responseText);
					//把返回的数据转成json对象
					JSONObject responseObj= new JSONObject(responseText);
					  //sn_error
					if(!"success".equals(responseObj.getString("result"))){   //发生错误
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if("biz.handler.data-get:no-result".equals(operCode)){ //没有结果
							Log.info("没有可用的订单!");
						}else{
							Log.warn("取订单出错了,错误码: "+operCode);
						}
						
						break;
					}
					
					JSONObject orderInfos = responseObj.getJSONObject("info");
					//总页数
					String orderTotal = String.valueOf(orderInfos.getString("counts"));
					int orderTotaltemp = Integer.parseInt(orderTotal);
					int pageTotalTemp  = orderTotaltemp<Integer.parseInt(pageSize)?1:orderTotaltemp/Integer.parseInt(pageSize)==0?Integer.parseInt(pageSize):orderTotaltemp/Integer.parseInt(pageSize)+1;
					String pageTotal =String.valueOf(pageTotalTemp);
					Log.info("总订单数为： "+orderTotal);
					Log.info("总页数为： "+pageTotal);
					if (orderTotal==null || orderTotal.equals("") || orderTotal.equals("0"))
					{				
						break;
					}
					//订单元素
					JSONArray ordersList = orderInfos.getJSONArray("data_info");
					for(int i = 0 ; i< ordersList.length() ; i++)
					{	//某个订单
						JSONObject orderInfo = ordersList.getJSONObject(i);
						int returnOrderCount =0;
						//订单编号 
						String orderCode = (String)orderInfo.get("order_sn");
						if(orderInfo.isNull("shop_info")) continue;
						//订单商品集合
						JSONArray items = orderInfo.getJSONArray("shop_info");
						//构造一个订单对象
						Order o = new Order();
						o.setObjValue(o, orderInfo);
						o.setFieldValue(o, "shop_info", items);
						Log.info("发货状态:　"+o.getShipping_status()+"付款状态: "+o.getPay_status());
						if(o != null)
						{	
							if("4".equals(o.getPay_status())){   //正常订单
								Log.info("订单号:　"+o.getOrder_sn());
								//如果是等待发货订单，创建接口订单成功，减少其它店的库存
									
										try
											{
												OrderUtils.createInterOrder(this.getDao().getConnection(), o, tradecontactid, username);
												
											} catch(SQLException sqle)
											{
												throw new JException("生成退货订单出错!" + sqle.getMessage());
											}
												
								}

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
				break;
			} catch (Exception e) 
			{
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				if(this.getDao()!=null && !this.getDao().getConnection().getAutoCommit()){
					this.getDao().rollback();
				}
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
		Log.info("本次取ecshop退货订单任务处理完毕!");
		
		
	}



	
}
