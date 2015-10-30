package com.wofu.ecommerce.weipinhui;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weipinhui.util.CommHelper;
public class GetOrders extends Thread {

	private static String jobname = "获取唯品会订单作业";
	
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	private static String lasttimerefundvalue=Params.username+"取退货订单最新时间";
	
	private static long daymillis=24*60*60*1000L;
	
	private String lasttime;
	private String lastRefundTime;
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Date nowtime = new Date();
			if(Params.startTime.getTime() <= nowtime.getTime())
			{//符合或超过指定的启动时间
				Connection connection = null;
				try {
					connection = PoolHelper.getInstance().getConnection(Params.dbname);	
					WeipinHui.setCurrentDate_getOrder(new Date());
					lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
					lastRefundTime=PublicUtils.getConfig(connection,lasttimerefundvalue,"");
					/**
					 * 订单状态 10待发货，22已发货，21部分发货，60交易成功 ，97交易关闭 ，0未支付订单 ，70用户已拒收 ，54退货已审核
					 */                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
					//获取唯品会新订单 
					getOrderList(connection) ;
					//获取唯品会退货订单
					getRefundOrderList(connection) ;
					
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
				Log.info(jobname + "下次执行等待时间:" + Params.waittime + "秒");
				long startwaittime = System.currentTimeMillis();
				while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))
				{
					try {
						sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
						Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
					}
				}
				//更新一次配置参数(从数据库中读取)
				Params.UpdateSettingFromDB(null);
			}
			else
			{//等待启动
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
			}
		} while (true);
	}
	
	
	//获取唯品会新订单
	public void getOrderList(Connection conn) throws Exception
	{
		Log.info("获取唯品会订单开始:");
		int pageIndex = 1 ;  //唯品会的订单从1页算起
		boolean hasNextPage = true ;	
		
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					//下单时间范围(只取当天的订单)
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//获取订单列表
					JSONObject jsonobj = new JSONObject();
					try {
						//测试
						jsonobj.put("st_add_time", "2015-10-1");
						jsonobj.put("et_add_time", "2015-10-31");
						jsonobj.put("order_id", "15101022592413");
						
//						jsonobj.put("st_add_time", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
//						jsonobj.put("et_add_time", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
						jsonobj.put("vendor_id", Params.vendor_id);
						jsonobj.put("page", pageIndex);
						jsonobj.put("limit", Integer.parseInt(Params.pageSize));
					} catch (JSONException e) {
						Log.warn("准备发送数据时出错!");
						continue;
					}
					//发送请求给唯品会
					String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getOrderList", jsonobj.toString());
					if(responseText.equals("")) break;
					//把返回的数据转成json对象
					JSONObject responseObj=new JSONObject(responseText);
					//发生错误
					if(!responseObj.getString("returnCode").equals("0")){
						String ErrStrCode = responseObj.getString("returnCode");
						String ErrMsg = responseObj.getString("returnMessage");
						Log.warn("取订单出错了,错误码: "+ErrStrCode+"错误信息: "+ErrMsg);
						sleep(10000L);
						continue;
					}
					//页数
					int orderNum= responseObj.getJSONObject("result").getInt("total");
					int pageTotal=0;
					if(orderNum!=0){
						pageTotal = orderNum>=Integer.parseInt(Params.pageSize) ? (orderNum %Integer.parseInt(Params.pageSize)==0?orderNum /Integer.parseInt(Params.pageSize):(orderNum /Integer.parseInt(Params.pageSize)+1)) : 1;
					}
					Log.info("当前页:" + pageIndex + ",总页数： " + pageTotal);
					//当前没订单
					if (pageTotal==0)
					{				
						if (n==1)
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).compareTo(
									this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);
										Log.info("一天之内都取不到订单,而且当前天大于配置天! 已将取订单最新时间更新为当前天的零点");
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
					//读取当前页的订单列表
					JSONArray ordersList = responseObj.getJSONObject("result").getJSONArray("dvd_order_list");
					int tmpcounter = 0;
					for(int i = 0 ; i< ordersList.length() ; i++)
					{
						//获取当前订单
						JSONObject orderJson = ordersList.getJSONObject(i);
						Order o = new Order();
						o.setObjValue(o,orderJson);
						//订单编号 
						String order_sn = orderJson.getString("order_id");
						//下单时间
						Date addTime = Formatter.parseDate(orderJson.getString("add_time"),Formatter.DATE_TIME_FORMAT);
						//获取当前订单详情
						JSONArray itemArrayTemp = OrderUtils.getOrderItem(order_sn);
						if(itemArrayTemp == null)	//获取订单详情失败
						{
							Log.warn("获取订单信息详情失败,忽略处理！");
							continue;
						}
						o.setFieldValue(o, "orderItemList", itemArrayTemp);
						if(o != null)
						{
							Log.info("正在处理订单:" + order_sn + "   订单状态:" + OrderUtils.getOrderStateByCode(o.getOrder_status()));
							//正常订单
							//如果是等待发货订单，创建接口订单成功，减少其它店的库存
							if("10".equals(o.getOrder_status()))
							{
								String nschaverecode = SQLHelper.strSelect(conn, "select count(*) from ns_customerorder where tid = '" + order_sn + "' and TradeContactID = '" + Params.tradecontactid + "'");
								System.out.println("ns_customerorder have recode:" + nschaverecode);
								if (nschaverecode.equals("0") && !OrderManager.isCheck(jobname, conn, order_sn) && !OrderManager.TidLastModifyIntfExists(jobname, conn, order_sn,addTime))
								{
									Log.info("正在生成接口订单");
									try
									{
										//生成接口订单
										OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
										
										for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
										{
											OrderItem item=(OrderItem) ito.next();
											String sku = item.getBarcode();
											//没有等待付款的状态 不需要删除未付款锁定的库存/
											StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, order_sn,sku);
											long qty= (long)item.getAmount();
											//在ecs_rationconfig表中存在机构添加一条库存同步记录(不包括自己）
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrder_status(),o.getOrder_id(), sku, qty,false);
										}
									} catch(SQLException sqle)
									{
										throw new JException("生成接口订单出错!" + sqle.getMessage());
									}
								}
								else
									Log.info("订单:" +order_sn+ "已经存在与数据库中");
							}
							//交易关闭
							else if("97".equals(o.getOrder_status()))
							{
								Log.info("订单号: "+o.getOrder_id()+", 交易取消！");
								for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									String sku = item.getBarcode();
									long qty= (long)(item.getAmount());
									//删除已锁定库存，增加其它店的库存
									StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, order_sn,sku);
									StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrder_status(),o.getOrder_id(), sku, qty,false);
								}
								
								//取消订单
								String sql="declare @ret int;  execute  @ret = IF_CancelCustomerOrder '" + order_sn + "';select @ret ret;";
								int resultCode = SQLHelper.intSelect(conn, sql) ;
								//取消订单失败
								if(resultCode == 2)			
									Log.info("唯品会请求取消订单失败,单号:"+order_sn+"");						
								else
									Log.info("唯品会请求取消订单成功,单号:"+order_sn+"");
								
							}
							//交易成功
							else if ("60".equals(o.getOrder_status()))  
							{
								for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									String sku = item.getBarcode();
									StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, order_sn, sku);								
								}
								Log.info("订单号: "+o.getOrder_id()+", 交易成功！");
							}
							//未支付订单
							else if ("0".equals(o.getOrder_status()))
							{
								for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									String sku = item.getBarcode();
									long qty= (long)(item.getAmount());
									StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(order_sn), sku, qty);
									StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrder_status(),order_sn, sku, -qty,false);								
								}
								Log.info("订单号: "+o.getOrder_id()+", 未支付！");
							}
						}
						else
						{
							Log.warn("获取订单信息出错！");
							break;
						}
						
						//如果当前订单时间大于开始取订单时间，则更新下次取订单时间(现在取订单列表最后修改时间)
						//更新同步订单最新时间
		                if (addTime.compareTo(modified)>0)
		                {
		                	modified=addTime;
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
		Log.info("本次取唯品会订单任务处理完毕!");
	}
	
	
	//获取唯品会退货订单
	public void getRefundOrderList(Connection conn) throws Exception
	{
		Log.info("获取唯品会退货订单开始:");
		int pageIndex = 1 ;  //唯品会的订单从1页算起
		boolean hasNextPage = true ;	
		
		Date modified=Formatter.parseDate(lastRefundTime,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					//下单时间范围(只取当天的订单)
					Date startdate=new Date(Formatter.parseDate(lastRefundTime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lastRefundTime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//获取订单列表
					JSONObject jsonobj = new JSONObject();
					try {
						jsonobj.put("st_create_time", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
						jsonobj.put("et_create_time", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
						jsonobj.put("vendor_id", Params.vendor_id);
						jsonobj.put("page", pageIndex);
						jsonobj.put("limit", Integer.parseInt(Params.pageSize));
					} catch (JSONException e) {
						Log.warn("准备发送数据时出错!");
						continue;
					}
					//发送请求给唯品会
					String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getReturnList", jsonobj.toString());
					if(responseText.equals("")) continue;
					//把返回的数据转成json对象
					JSONObject responseObj=new JSONObject(responseText);
					//发生错误
					if(!responseObj.getString("returnCode").equals("0")){
						String ErrStrCode = responseObj.getString("returnCode");
						String ErrMsg = responseObj.getString("returnMessage");
						Log.warn("取退货订单出错了,错误码: "+ErrStrCode+"错误信息: "+ErrMsg);
						sleep(10000L);
						continue;
					}
					//页数
					int orderNum= responseObj.getJSONObject("result").getInt("total");
					int pageTotal=0;
					if(orderNum!=0){
						pageTotal = orderNum>=Integer.parseInt(Params.pageSize) ? (orderNum %Integer.parseInt(Params.pageSize)==0?orderNum /Integer.parseInt(Params.pageSize):(orderNum /Integer.parseInt(Params.pageSize)+1)) : 1;
					}
					Log.info("当前页:" + pageIndex + ",总页数： " + pageTotal);
					//当前没订单
					if (pageTotal==0)
					{				
						if (n==1)
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).compareTo(
									this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimerefundvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimerefundvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimerefundvalue, value);
										Log.info("一天之内都取不到退货订单,而且当前天大于配置天! 已将取退货订单最新时间更新为当前天的零点");
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
					//读取当前页的退货订单列表
					JSONArray ordersList = responseObj.getJSONObject("result").getJSONArray("dvd_return_list");
					for(int i = 0 ; i< ordersList.length() ; i++)
					{
						//获取当前退货订单
						JSONObject orderJson = ordersList.getJSONObject(i);
						ReturnOrder o = new ReturnOrder();
						o.setObjValue(o,orderJson);
						
						//订单编号 
						String order_sn = orderJson.getString("order_id");
						//客退申请单号
						String back_sn = orderJson.getString("back_sn");
						//从b2c拉取客退订单状态时间
						Date createTime = Formatter.parseDate(orderJson.getString("create_time"),Formatter.DATE_TIME_FORMAT);
						//获取当前订单详情
						JSONArray itemArrayTemp = OrderUtils.getRefundOrderItem(back_sn);
						if(itemArrayTemp == null)	//获取订单详情失败
						{
							Log.warn("获取退货订单信息详情失败,忽略处理！");
							continue;
						}
						
						System.out.println(itemArrayTemp.toString());
						
						o.setFieldValue(o, "orderItemList", itemArrayTemp);
						
						if(o != null)
						{
							Log.info("正在处理退货订单:" + order_sn + "   订单状态:" + OrderUtils.getOrderStateByCode(o.getReturn_status()) + "   退货原因:" + o.getReturn_reason());
							//59已退货
							//60已完成
							//100退货失败
							//54退货已审核
							if ("54".equals(o.getReturn_status()))
							{
								if (!OrderManager.RefundIntfExists("检查唯品会退货订单", conn, o.getOrder_id(),o.getBack_sn()))
								{
									try
									{
										Log.info("生成当前退货订单:" +o.getOrder_id()+ "的退货接口数据...");
										OrderUtils.createRefundOrder(conn,o,Params.tradecontactid);
									} catch(SQLException sqle)
									{
										throw new JException("生成接口退货订单出错!" + sqle.getMessage());
									}
								}
							}
						}
						else
						{
							Log.warn("获取退货订单信息失败！");
							break;
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

				if (modified.compareTo(Formatter.parseDate(lastRefundTime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimerefundvalue, value);
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
		Log.info("本次取唯品会退货订单任务处理完毕!");
	}
}