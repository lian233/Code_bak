package com.wofu.ecommerce.meilishuo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import com.wofu.base.job.Executer;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.meilishuo.util.CommHelper;
import com.wofu.ecommerce.meilishuo.util.Utils;
/**
 * 
 *检查未入订单
 *检查取消订单
 *
 */
public class CheckOrderExecuter extends Executer
{

	private static String pageSize = "10" ;
	
	private static String jobName="定时检查美丽说未入订单";
	private static long daymillis=24*60*60*1000L;
	private static String vcode = "" ;
	private static String url = "" ;
	private static String appKey = "" ;
	private static String appsecret = "" ;
	private static String token = "" ;
	private static String tradecontactid = "" ;
	private static String username = "" ;
	@Override
	public void run()
	{
		Properties prop = StringUtil.getStringProperties(this.getExecuteobj()
				.getParams());
		pageSize = prop.getProperty("pageSize");
		vcode = prop.getProperty("vcode");
		url = prop.getProperty("url");
		tradecontactid = prop.getProperty("tradecontactid");
		username = prop.getProperty("username");
		try
		{
			// 检查未入订单
			updateJobFlag(1);
			token = PublicUtils.getToken(this.getDao().getConnection(), Integer.parseInt(tradecontactid));
			checkWaitStockOutOrders();
			// 检查取消订单
			// checkCancleOrders();

			UpdateTimerJob();

			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes()
					+ "] ["
					+ this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj()
							.getNextactive()));

		} catch (Exception e)
		{
			try
			{

				if (this.getExecuteobj().getSkip() == 1)
				{
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));

				updateJobFlag(0);

				if (this.getConnection() != null
						&& !this.getConnection().getAutoCommit())
					this.getConnection().rollback();

				if (this.getExtconnection() != null
						&& !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();

			} catch (Exception e1)
			{
				Log.error(jobName, "回滚事务失败");
			}
			Log.error(jobName, "错误信息:" + Log.getErrorMessage(e));

			Log.error(jobName, "执行作业失败 ["
					+ this.getExecuteobj().getActivetimes() + "] ["
					+ this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));

		} finally
		{
			try
			{
				updateJobFlag(0);
			} catch (Exception e)
			{
				Log.error(jobName, "更新处理标志失败");
			}

			try
			{
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();

			} catch (Exception e)
			{
				Log.error(jobName, "关闭数据库连接失败");
			}
		}

	}
	/**
	 * 检查未入待发货订单 orderStatus=10 等待发货 这里检查一天时间的未入订单
	 **/
	public void checkWaitStockOutOrders() throws Exception
	{
		Log.info(jobName + "任务开始!");
		Connection conn = this.getDao().getConnection();

		int pageIndex = 0; // 美丽说的订单从0页算起
		boolean hasNextPage = true;

		for (int k = 0; k < 10;)
		{
			try
			{
				int n = 1;
				while (hasNextPage)
				{

					Date startdate = new Date(new Date().getTime() - daymillis);
					Date enddate = new Date();
					// 方法名
					String apimethod = "meilishuo.order.list.get";
					HashMap<String, String> param = new HashMap<String,String>();
					param.put("method", apimethod);
					param.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
					param.put("format", "json");
					param.put("app_key", Params.appKey);
					param.put("v", "1.0");
					param.put("sign_method", "md5");
					param.put("session", Params.token);
					param.put("page", String.valueOf(pageIndex));
					param.put("page_size", Params.pageSize);
					param.put("uptime_start", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
					param.put("uptime_end", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
					
					Log.info("第" + pageIndex + "页");
					String responseText = Utils.sendbyget(Params.url,
							param,Params.appsecret);
					Log.info("返回了这些东西: "
							+ new String(responseText.getBytes(), "GBK"));
					// 把返回的数据转成json对象
					JSONObject responseObj = new JSONObject(responseText);
					try
					{
						String errormessage = responseObj.getJSONObject(
						"error_response").getString("message"); // 如果没错整个try都不会执行成功，有错就会执行出错过程
						Log.info(jobName, errormessage);
						break;
					}catch(Exception e)
					{
						
					}
					// 总页数
					String pageTotal = String.valueOf(responseObj.getJSONObject("order_list_get_response").getInt("total_num"));
					Log.info("总页数： " + pageTotal);
					if (pageTotal == null || pageTotal.equals("")
							|| pageTotal.equals("0"))
					{
						break;
					}
					// 订单元素
					JSONArray ordersList = responseObj.getJSONObject("order_list_get_response").getJSONArray("info");
					for (int i = 0; i < ordersList.length(); i++)
					{ // 某个订单
						JSONObject orderInfo = ordersList.getJSONObject(i);
						JSONObject order = orderInfo.getJSONObject("order");
						JSONObject address = orderInfo.getJSONObject("address");
						// 订单编号
						String orderCode = String.valueOf(orderInfo
								.getJSONObject("order").getLong("order_id"));
						// 订单商品集合
						JSONArray items = orderInfo.getJSONArray("goods");
						// 订单服务集合
						JSONArray servers = orderInfo.getJSONArray("service");
						// 构造一个订单对象
						Order o = new Order();
						o.setObjValue(o, order);
						o.setObjValue(o, address);
						o.setFieldValue(o, "orderItemList", items);
						o.setFieldValue(o, "serviceList", servers);
						Date createTime = o.getCtime();
						if (o != null)
						{
							Log.info("发货标志【 " + o.getDeliver_status() + "】");
							Log.info("订单号【" + o.getOrder_id() + "】,状态【"
									+ o.getStatus_text() + "】");
							// 正常订单
							// 如果是等待发货订单，创建接口订单成功，减少其它店的库存
							if ("\u7b49\u5f85\u53d1\u8d27".equals(o
									.getStatus_text()))
							{
								if (!OrderManager.isCheck(jobName, conn,
										orderCode))
								{
									if (!OrderManager.TidLastModifyIntfExists(
											jobName, conn, orderCode,
											createTime))
									{
										try
										{
											OrderUtils.createInterOrder(conn,
													o, Params.tradecontactid,
													Params.username);
											for (Iterator ito = o
													.getOrderItemList()
													.getRelationData()
													.iterator(); ito.hasNext();)
											{
												OrderItem item = (OrderItem) ito
														.next();
												String sku = item.getSku();
												// 没有等待付款的状态 不需要删除未付款锁定的库存/
												// StockManager.deleteWaitPayStock(jobName,
												// conn,Params.tradecontactid,
												// orderCode,sku);
												long qty = (long) item
														.getAmount();
												// 在ecs_rationconfig表中存在机构添加一条库存同步记录(不包括自己）
												StockManager.addSynReduceStore(
														jobName, conn,
														Params.tradecontactid,
														o.getStatus_text(), o
																.getOrder_id(),
														sku, qty, false);
											}

										} catch (SQLException sqle)
										{
											throw new JException("生成接口订单出错!"
													+ sqle.getMessage());
										}
									}
								} // 订单状态 10待发货，20已发货，21部分发货，30交易成功 ，40交易关闭
							}

							// 40交易关闭
							else if ("交易取消".equals(o.getStatus_text()))
							{
								Log.info("订单号: " + o.getOrder_id() + ", 交易取消！");
								for (Iterator ito = o.getOrderItemList()
										.getRelationData().iterator(); ito
										.hasNext();)
								{
									OrderItem item = (OrderItem) ito.next();
									String sku = item.getSku();
									long qty = (long) (item.getAmount());
									// 删除已锁定库存，增加其它店的库存
									StockManager.deleteWaitPayStock(jobName,
											conn, Params.tradecontactid,
											orderCode, sku);
									StockManager.addSynReduceStore(jobName,
											conn, Params.tradecontactid, o
													.getStatus_text(), o
													.getOrder_id(), sku, qty,
											false);
								}

								// 取消订单
								String sql = "declare @ret int;  execute  @ret = IF_CancelCustomerOrder '"
										+ orderCode + "';select @ret ret;";
								int resultCode = SQLHelper.intSelect(conn, sql);
								// 取消订单失败
								if (resultCode == 2)
									Log
											.info("美丽说请求取消订单失败,单号:" + orderCode
													+ "");
								else
									Log
											.info("美丽说请求取消订单成功,单号:" + orderCode
													+ "");

							} else if ("交易成功".equals(o.getStatus_text())) // 交易成功
							{
								for (Iterator ito = o.getOrderItemList()
										.getRelationData().iterator(); ito
										.hasNext();)
								{
									OrderItem item = (OrderItem) ito.next();
									String sku = item.getSku();

									StockManager.deleteWaitPayStock(jobName,
											conn, Params.tradecontactid,
											orderCode, sku);
								}

							} else if ("等待付款".equals(o.getStatus_text())) // 交易成功
							{
								for (Iterator ito = o.getOrderItemList()
										.getRelationData().iterator(); ito
										.hasNext();)
								{
									OrderItem item = (OrderItem) ito.next();
									String sku = item.getSku();
									long qty = (long) (item.getAmount());

									StockManager.addWaitPayStock(jobName, conn,
											Params.tradecontactid, orderCode, sku,
											qty);
									StockManager.addSynReduceStore(jobName,
											conn, Params.tradecontactid, o
													.getStatus_text(), orderCode.equals("")?o.getOrder_id():orderCode, sku,
											-qty, false);
								
								}

							}
						}

						// 如果当前订单时间大于开始取订单时间，则更新下次取订单时间(现在取订单列表最后修改时间)
						// 更新同步订单最新时间

					}
					// 判断是否有下一页
					if ("".equals(pageTotal) || pageTotal == null)
						pageTotal = "0";
					if (pageIndex >= Integer.parseInt((pageTotal.equals("") || pageTotal==null)?"0":pageTotal))
					{
						Log.info("Integer.parseInt: "+Integer.parseInt((pageTotal.equals("") || pageTotal==null)?"0":pageTotal));
						hasNextPage = false;
					}
					else
						pageIndex++;

					n++;


				}	

				Log.info(jobName + "执行完毕!");
				break;

			}catch(Exception e)
			{
				e.printStackTrace();
				if (++k >= 5)
					throw e;
				if (conn != null && !conn.getAutoCommit())
				{
					conn.rollback();
				}
				Log.warn(jobName + " ,远程连接失败[" + k + "], 10秒后自动重试. "
						+ Log.getErrorMessage(e));
				Thread.sleep(10000L);

				
			}
		}
	}
}
