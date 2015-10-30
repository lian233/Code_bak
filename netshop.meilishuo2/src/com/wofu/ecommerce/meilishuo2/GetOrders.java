package com.wofu.ecommerce.meilishuo2;

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
import com.wofu.ecommerce.meilishuo2.util.Utils;

public class GetOrders extends Thread
{

	private static String jobname = "获取美丽说订单作业";

	private static String lasttimeconfvalue = "美丽说取订单最新时间";
	/** 美丽说取订单最新时间 **/

	private static long daymillis = 24 * 60 * 60 * 1000L;

	private String lasttime;

	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	public void run()
	{

		Log.info(jobname, "启动[" + jobname + "]模块");
		do
		{
			Connection connection = null;

			try
			{
				connection = PoolHelper.getInstance().getConnection(
						Params.dbname);

				lasttime = PublicUtils.getConfig(connection, lasttimeconfvalue,
						"");
				/**
				 * 订单状态 10待发货，20已发货，21部分发货，30交易成功 ，40交易关闭
				 */
				// 获取美丽说新订单
				getOrderList(connection);

			} catch (Exception e)
			{
				try
				{
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1)
				{
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally
			{
				try
				{
					if (connection != null)
						connection.close();
				} catch (Exception e)
				{
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))
				try
				{
					sleep(1000L);
				} catch (Exception e)
				{
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	// 获取美丽说新订单
	public void getOrderList(Connection conn) throws Exception
	{
		int pageIndex = 0; // 美丽说的订单从0页算起
		boolean hasNextPage = true;

		Date modified = Formatter.parseDate(lasttime,
				Formatter.DATE_TIME_FORMAT);
		for (int k = 0; k < 10;)
		{
			try
			{
				int n = 1;
				while (hasNextPage)
				{
					Date startdate = new Date(Formatter.parseDate(lasttime,
							Formatter.DATE_TIME_FORMAT).getTime() + 1000L);
					Date enddate = new Date(Formatter.parseDate(lasttime,
							Formatter.DATE_TIME_FORMAT).getTime()
							+ daymillis);
					String apimethod = "meilishuo.order.list.get";
					Log.info("第" + pageIndex + "页");
					JSONObject object = new JSONObject(PublicUtils.getConfig(
							conn, "美丽说Token信息2", "")); // 获取最新的Token
					String responseText = Utils.sendbyget(Params.url,
							Params.appKey, Params.appsecret, apimethod, object
									.optString("access_token"), new Date(),
							startdate, enddate, Params.pageSize, String
									.valueOf(pageIndex), "0");
					Log.info("返回数据: "
							+ new String(responseText.getBytes(), "GBK"));
					JSONObject responseObj = new JSONObject(responseText);
					// 如果错误的话：
					try
					{
						String errormessage = responseObj.getJSONObject(
								"error_response").getString("message"); // 如果没错整个try都不会执行成功，有错就会执行出错过程
						try
						{
							// 如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
							if (this.dateformat
									.parse(
											Formatter.format(new Date(),
													Formatter.DATE_FORMAT))
									.compareTo(
											this.dateformat
													.parse(Formatter
															.format(
																	Formatter
																			.parseDate(
																					PublicUtils
																							.getConfig(
																									conn,
																									lasttimeconfvalue,
																									""),
																					Formatter.DATE_TIME_FORMAT),
																	Formatter.DATE_FORMAT))) > 0)
							{
								try
								{
									String value = Formatter.format((new Date(
											Formatter.parseDate(
													PublicUtils.getConfig(conn,
															lasttimeconfvalue,
															""),
													Formatter.DATE_TIME_FORMAT)
													.getTime()
													+ daymillis)),
											Formatter.DATE_FORMAT)
											+ " 00:00:00";
									PublicUtils.setConfig(conn,
											lasttimeconfvalue, value);
								} catch (JException je)
								{
									Log.error(jobname, je.getMessage());
								}
							}
							return;
						} catch (ParseException e)
						{
							Log.error(jobname, "不可用的日期格式!" + e.getMessage());
						}

					} catch (Exception e)
					{

					}
					int orderNum;
					try
					{
						orderNum = Integer.parseInt(responseObj.getJSONObject(
						"order_list_get_response").getString("total_num"));
					}catch(Exception e)
					{
						orderNum = responseObj.getJSONObject("order_list_get_response").getInt("total_num");
					}
					int pageTotal = orderNum >= Integer
							.parseInt(Params.pageSize) ? (orderNum
							% Integer.parseInt(Params.pageSize) == 0 ? orderNum
							/ Integer.parseInt(Params.pageSize) : (orderNum
							/ Integer.parseInt(Params.pageSize) + 1)) : 1;
					Log.info("总页数： " + pageTotal);
					if (pageTotal == 0)
					{
						if (n == 1)
						{
							try
							{
								// 如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat
										.parse(
												Formatter.format(new Date(),
														Formatter.DATE_FORMAT))
										.compareTo(
												this.dateformat
														.parse(Formatter
																.format(
																		Formatter
																				.parseDate(
																						PublicUtils
																								.getConfig(
																										conn,
																										lasttimeconfvalue,
																										""),
																						Formatter.DATE_TIME_FORMAT),
																		Formatter.DATE_FORMAT))) > 0)
								{
									try
									{
										String value = Formatter
												.format(
														(new Date(
																Formatter
																		.parseDate(
																				PublicUtils
																						.getConfig(
																								conn,
																								lasttimeconfvalue,
																								""),
																				Formatter.DATE_TIME_FORMAT)
																		.getTime()
																		+ daymillis)),
														Formatter.DATE_FORMAT)
												+ " 00:00:00";
										PublicUtils.setConfig(conn,
												lasttimeconfvalue, value);
									} catch (JException je)
									{
										Log.error(jobname, je.getMessage());
									}
								}
							} catch (ParseException e)
							{
								Log
										.error(jobname, "不可用的日期格式!"
												+ e.getMessage());
							}
						}
						break;
					}
					// 订单元素
					JSONArray ordersList = responseObj.getJSONObject(
							"order_list_get_response").getJSONArray("info");
					for (int i = 0; i < ordersList.length(); i++)
					{ // 某个订单
						JSONObject orderInfo = ordersList.getJSONObject(i);
						JSONObject order = orderInfo.getJSONObject("order");
						JSONObject address = orderInfo.getJSONObject("address");
						// 订单编号
						String orderCode = String.valueOf(orderInfo
								.getJSONObject("order").get("order_id"));
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
						// 设置商品的某些属性
						for (Iterator ito = o.getOrderItemList()
								.getRelationData().iterator(); ito.hasNext();)
						{
							OrderItem item = (OrderItem) ito.next();
							String color = "";
							String size = "";
							for (Iterator it = item.getProp().getRelationData()
									.iterator(); it.hasNext();)
							{
								Prop prop = (Prop) it.next();
								if (prop.getName().equals("\u989c\u8272"))
								{
									color = prop.getValue();
									color = new String(prop.getValue()
											.getBytes(), "GBK");
									Log.info(color);
								}
								if (prop.getName().equals("\u5c3a\u7801"))
								{
									size = prop.getValue();
									size = new String(prop.getValue()
											.getBytes(), "GBK");
									Log.info(size);
								}
							}
							// 商品sku

							String sku = OrderUtils.getItemCodeByColSizeCustom(
									conn, item.getGoods_no(), color, size);
							item.setSku(sku);
						}
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
								if (!OrderManager.isCheck(jobname, conn,
										orderCode))
								{
									if (!OrderManager.TidLastModifyIntfExists(
											jobname, conn, orderCode,
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
												// StockManager.deleteWaitPayStock(jobname,
												// conn,Params.tradecontactid,
												// orderCode,sku);
												long qty = (long) item
														.getAmount();
												// 在ecs_rationconfig表中存在机构添加一条库存同步记录(不包括自己）
												// StockManager.addSynReduceStore(jobname,
												// conn, Params.tradecontactid,
												// o.getStatus_text(),o.getOrder_id(),
												// sku, qty,false);
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
									StockManager.deleteWaitPayStock(jobname,
											conn, Params.tradecontactid,
											orderCode, sku);
									StockManager.addSynReduceStore(jobname,
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

									StockManager.deleteWaitPayStock(jobname,
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

									StockManager.addWaitPayStock(jobname, conn,
											Params.tradecontactid, String
													.valueOf(orderCode), sku,
											qty);
									StockManager.addSynReduceStore(jobname,
											conn, Params.tradecontactid, o
													.getStatus_text(), String
													.valueOf(orderCode), sku,
											-qty, false);
								}

							}

						}

						// 如果当前订单时间大于开始取订单时间，则更新下次取订单时间(现在取订单列表最后修改时间)
						// 更新同步订单最新时间

						if (createTime.compareTo(modified) > 0)
						{
							modified = createTime;
						}

					}
					// 判断是否有下一页

					if (pageIndex >= pageTotal - 1)
						hasNextPage = false;
					else
						pageIndex++;

					n++;

				}
				if (modified.compareTo(Formatter.parseDate(lasttime,
						Formatter.DATE_TIME_FORMAT)) > 0)
				{
					try
					{
						String value = Formatter.format(modified,
								Formatter.DATE_TIME_FORMAT);
						PublicUtils.setConfig(conn, lasttimeconfvalue, value);
					} catch (JException je)
					{
						Log.error(jobname, je.getMessage());
					}
				}

				break;

			} catch (Exception e)
			{
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				if (conn != null && !conn.getAutoCommit())
				{
					conn.rollback();
				}
				Log.warn(jobname + " ,远程连接失败[" + k + "], 10秒后自动重试. "
						+ Log.getErrorMessage(e));
				Thread.sleep(10000L);

			}
		}
		Log.info("本次取美丽说订单任务处理完毕!");
	}

}