package com.wofu.ecommerce.jiaju;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.jiaju.utils.CommHelper;
import com.wofu.ecommerce.jiaju.Order;
import com.wofu.ecommerce.jiaju.OrderUtils;

//家居就获取订单作业
public class GetOrders extends Thread {

	private static String jobname = "获取家居就订单作业";		//作业名称
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Date nowtime = new Date();
			if(Params.startTime.getTime() <= nowtime.getTime())
			{//符合或超过指定的启动时间
				Connection connection = null;
				Log.info("开始本次取家居就订单处理任务!");
				try {
					//获取数据库conn
					connection = PoolHelper.getInstance().getConnection(Params.dbname);

					//获取家居就新订单 
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
	
	
	//获取家居就新订单
	public void getOrderList(Connection conn) throws Exception
	{
		try
		{
			//准备要发送请求的内容
			HashMap<String, String> Data = new HashMap<String, String>();
			Data.put("service", "get_orders_to_send");	//方法名
			Data.put("type", "MD5");	//数字签名处理方式(固定)
			Data.put("partner_id", Params.partner_id);	//合作方ID
			Data.put("doc", "json");	//返回数据格式(固定)
	
			//按Key排序
			String sortStr = CommHelper.sortKey(Data);
			//加上数字签名
			String signed = CommHelper.makeSign(sortStr, Params.Partner_pwd);
			//输出发送请求内容
			//System.out.println("发送请求:" + signed);
			Log.info("发送请求:" + signed);
			//发送请求
			String responseText = CommHelper.sendByPost(Params.url, signed);
			//输出返回的结果
			Log.info(responseText);
			//System.out.println(responseText);
			
			//解析返回的Json
			try
			{
				JSONObject responseObj = new JSONObject(responseText);
				String resultbool = responseObj.get("status").toString();
				int itemCount = responseObj.getInt("count");
				
				Log.info("返回的status:" + resultbool);
				if(resultbool.equals("true"))
				{
					Log.info("当前可能需要处理的订单数为(可能包含已处理的订单):" + itemCount);
					if(itemCount > 0)
					{//有订单
						//处理这些订单
						JSONArray ordersList = responseObj.getJSONArray("orders");
						for(int i = 0 ; i< ordersList.length() ; i++)
						{
							JSONObject orderInfo = ordersList.getJSONObject(i);
							
							JSONObject shipInfo = orderInfo.getJSONObject("ship_info");
							JSONArray goods = orderInfo.getJSONArray("goods");
							
							Order o = new Order();
							o.setObjValue(o, orderInfo);
							o.setObjValue(o, shipInfo);
							o.setFieldValue(o, "goods", goods);
							
							String orderid = o.getTrade_id();	//订单编号
							Date addtime = o.getAdd_time();
							
							//若在设定了"只抓取指定时间之后的订单"时,对下单时间进行判断
							if(Params.orderAddTime != null)
							{
								//若下单时间比设定时间早则跳过
								if(Params.orderAddTime.getTime() > addtime.getTime())
								{
									Log.info("由于设定了只取指定时间[" + Formatter.format(Params.orderAddTime, "yyyy-MM-dd HH:mm:ss") + "]之后的订单,所以该订单不符合条件跳过! 订单号:" + orderid + " 下单时间:" 
											+ Formatter.format(addtime, "yyyy-MM-dd HH:mm:ss"));
									continue;
								}
							}
							//如果是等待发货订单，创建接口订单成功，减少其它店的库存
							if (!OrderManager.isCheck(jobname, conn, orderid))
							{//查询表customerorder(已审核的订单)和CustomerOrderRefList(合并订单列表)是否已经存在该订单号码
								if (!OrderManager.TidLastModifyIntfExists(jobname, conn, orderid, addtime))
								{//查找最新版本的订单是否存在该订单号(ns_customerorder)
									Log.info("正在处理订单:" + orderid);
									try
									{
										OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);	//创建接口订单
										//读取商品列表
										for(Iterator ito=o.getGoods().getRelationData().iterator();ito.hasNext();)
										{
											OrderItem item=(OrderItem) ito.next();
											//获取当前订单商品的sku
											String sku = item.getOuter_id() ;
												//没有等待付款的状态 不需要删除未付款锁定的库存/
												//StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
											//获取当前SKU的订单购买数量
											long qty= (long)item.getAmount();
											//在ecs_rationconfig表中存在机构添加一条库存同步记录(不包括自己）
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),orderid, sku, qty, false);
										}
									} catch(SQLException sqle)
									{
										throw new JException("生成接口订单出错!" + sqle.getMessage());
									}
								}
							}
						}
					}
				}
				else
				{//返回的status为false则抛出异常
					throw new Exception();
				}
			}
			catch(Exception jsonerr)
			{//返回结果不正常
				Log.warn("返回结果不正常,获取订单失败!");
				//jsonerr.printStackTrace();
				//failed = true;
			}
		}
		catch(Exception err)
		{
			Log.error(jobname, "获取订单失败:\n" + err.getMessage());
			//failed = true;
		}
		Log.info("本次取家居就订单任务处理完毕!");
		Thread.sleep(Params.waittime * 1000 * 60);
	}
}