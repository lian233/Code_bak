package com.wofu.netshop.meilishuo.fenxiao;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import com.wofu.business.fenxiao.order.OrderManager;
import com.wofu.business.fenxiao.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.common.fenxiao.Utils;
import com.wofu.netshop.meilishuo.fenxiao.Params;
public class GetOrdersRunnable implements Runnable{
	private CountDownLatch watch=null;
	public String jobName="美丽说下载订单作业";
	private String lasttime;
	private String refundlasttime;
	private static long daymillis = 24 * 60 * 60 * 1000L;
	Params param;
	public GetOrdersRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
	}
	public void run() {
		Connection conn =null;
		jobName = param.username+jobName;
		try{
			conn= PoolHelper.getInstance().getConnection("shop");
			getOrderList(conn);
		}catch(Throwable e){
			Log.error(param.username, e.getMessage(),null);
		}finally{
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.error(param.username, e.getMessage(),null);
				}
			}
			watch.countDown();
		}
		
	}
	//下载订单
	private void getOrderList(Connection conn) throws Throwable{
		lasttime=PublicUtils.getConfig(conn,"LastOrderTime",param.shopid);
		System.out.println("lasttime: "+lasttime);
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		boolean hasNextPage=true;
		int pageIndex=0;
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
					Log.info(param.username,"第" + pageIndex + "页",null);
					/**
					JSONObject object = new JSONObject(PublicUtils.getConfig(
							conn, "美丽说Token信息2", "")); // 获取最新的Token
							**/
					HashMap<String,String> request = new HashMap<String,String>();
					request.put("app_key", param.appKey);
					request.put("method", "meilishuo.order.list.get");
					request.put("session", param.token);
					request.put("sign_method", "md5");
					request.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
					request.put("v", "1.0");
					request.put("page", String.valueOf(pageIndex));
					request.put("page_size", param.pageSize);
					request.put("uptime_start", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
					request.put("uptime_end", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
					String responseText = Utils.sendByGet(request,param.url,true,param.appsecret);
					Log.info(param.username,"返回数据: "
							+ new String(responseText.getBytes(), "GBK"),null);
					JSONObject responseObj = new JSONObject(responseText);
					if(!responseObj.isNull("error_response")){
						Log.error(param.username, responseObj.getJSONObject("error_response").getString("message"));
						k=10;
						break;
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
							.parseInt(param.pageSize) ? (orderNum
							% Integer.parseInt(param.pageSize) == 0 ? orderNum
							/ Integer.parseInt(param.pageSize) : (orderNum
							/ Integer.parseInt(param.pageSize) + 1)) : 1;
					Log.info(param.username,"总页数： " + pageTotal,null);
					if (pageTotal == 0)
					{
						if (n == 1)
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (Formatter.parseDate(Formatter.format(new Date(), Formatter.DATE_FORMAT),Formatter.DATE_FORMAT).
										compareTo(Formatter.parseDate(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,"LastOrderTime",param.shopid),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT),Formatter.DATE_FORMAT))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,"LastOrderTime",param.shopid),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, "LastOrderTime", param.shopid,value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(param.username,jobName, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(param.username,jobName, "不可用的日期格式!"+e.getMessage());
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
						Date createTime = o.getPay_time();
						if (o != null)
						{
							Log.info(param.username,"发货标志【 " + o.getDeliver_status() + "】",null);
							Log.info(param.username,"订单号【" + o.getOrder_id() + "】,状态【"
									+ o.getStatus_text() + "】",null);
							// 正常订单
							// 如果是等待发货订单，创建接口订单成功，减少其它店的库存
							if ("\u7b49\u5f85\u53d1\u8d27".equals(o
									.getStatus_text()))
							{
								//if (!OrderManager.isCheck(jobName, conn,
									//	orderCode))
								//{
									if (!OrderManager.TidLastModifyIntfExists(
											jobName, conn, orderCode,
											createTime))
									{
										try
										{
											OrderUtils.createInterOrder(conn,
													o, param.shopid,
													param.username,20);
										} catch (SQLException sqle)
										{
											throw new JException("生成接口订单出错!"
													+ sqle.getMessage());
										}
									}
								//} // 订单状态 10待发货，20已发货，21部分发货，30交易成功 ，40交易关闭
							}//等待确认收货也进系统，防止重复发货
							else if("\u7b49\u5f85\u786e\u8ba4\u6536\u8d27".equals(o.getStatus_text())){
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderCode,createTime))
									{
										try
										{
											OrderUtils.createInterOrder(conn,o, param.shopid,
													param.username,30);
										} catch (SQLException sqle)
										{
											throw new JException("生成接口订单出错!"
													+ sqle.getMessage());
										}
									}
							}
							else if("交易成功".equals(o.getStatus_text())){
								if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderCode,createTime))
								{
									try
									{
										OrderUtils.createInterOrder(conn,o, param.shopid,
												param.username,100);
									} catch (SQLException sqle)
									{
										throw new JException("生成接口订单出错!"
												+ sqle.getMessage());
									}
								}
						}

							// 40交易关闭
							else if ("交易关闭".equals(o.getStatus_text()))
							{
								//if (!OrderManager.isCheck(jobName, conn,orderCode))
								//{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderCode,createTime))
									{
										try
										{
											OrderUtils.createInterOrder(conn,o, param.shopid,
													param.username,110);
										} catch (SQLException sqle)
										{
											throw new JException("生成接口订单出错!"
													+ sqle.getMessage());
										}
									}
								//}
								

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
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, "LastOrderTime", param.shopid,value);	
	            	}catch(JException je)
	            	{
	            		Log.error(param.username,param.username,je.getMessage(),0);
	            	}
				}
				k=10;
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
				Log.warn(jobName + " ,远程连接失败[" + k + "], 10秒后自动重试. "
						+ Log.getErrorMessage(e));
				Thread.sleep(10000L);

			}
		}
		Log.info("本次取美丽说订单任务处理完毕!");
	}

}
