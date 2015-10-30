package com.wofu.ecommerce.jiaju;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.jiaju.utils.CommHelper;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.order.OrderManager;

public class CheckOrderExecuter extends Executer {
	public static String username = "";	//商城名称(如:家居就、贝贝怡苏宁商城...)
	public static String dbname = "";	//数据库连接池
	public static int waittime = 10;
	public static final String url = "http://www.jiaju.com/openapi/";	//接口地址
	public static String partner_id = "";	//帐号
	public static String Partner_pwd = "";	//密码
	public static String tradecontactid = "7";		//数据库中:select * from TradeContacts,正式的时候需要添加一条记录进去使用
	public static String company = "";		//快递公司对应表
	private static String jobName="检查家居就订单作业";

	public void run()  {
		try {		
			
			updateJobFlag(1);
	
			getOrderList();
			
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

	
	/*
	 * 获取一天之内的所有订单
	 */
	private void getOrderList() throws Exception
	{
		try
		{
			Connection conn = this.getDao().getConnection();
			Properties prop = StringUtil.getStringProperties(this.getExecuteobj().getParams());
			username = prop.getProperty("username","家居就");
			dbname =  prop.getProperty("dbname", "jiaju");
			waittime= Integer.parseInt(prop.getProperty("waittime","10"));
			partner_id = prop.getProperty("partner_id", "");
			Partner_pwd = prop.getProperty("Partner_pwd", "");
			//tradecontactid = prop.getProperty("tradecontactid","7");
			company = prop.getProperty("company","");
			
			//准备要发送请求的内容
			HashMap<String, String> Data = new HashMap<String, String>();
			Data.put("service", "get_orders_to_send");	//方法名
			Data.put("type", "MD5");	//数字签名处理方式(固定)
			Data.put("partner_id", partner_id);	//合作方ID
			Data.put("doc", "json");	//返回数据格式(固定)
	
			//按Key排序
			String sortStr = CommHelper.sortKey(Data);
			//加上数字签名
			String signed = CommHelper.makeSign(sortStr, Partner_pwd);
			//输出发送请求内容
			//System.out.println("发送请求:" + signed);
			Log.info("发送获取订单列表请求");
			//发送请求
			String responseText = CommHelper.sendByPost(url, signed);
			//输出返回的结果
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
					Log.info("当前需要检查的订单数为:" + itemCount);
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
							
							String orderid = o.getOrder_id();	//订单编号
							Date addtime = o.getAdd_time();

							//Log.info("正在检查订单是否存在:" + orderid);
							//如果是等待发货订单，创建接口订单成功，减少其它店的库存
							if (!OrderManager.isCheck(jobName, conn, orderid))
							{//查询表customerorder(已审核的订单)和CustomerOrderRefList(合并订单列表)是否已经存在该订单号码
								if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderid, addtime))
								{//查找最新版本的订单是否存在该订单号(ns_customerorder)
									try
									{
										Log.info("新增订单:" + orderid);
										OrderUtils.createInterOrder(conn, o, tradecontactid, username);	//创建接口订单
										//读取商品列表
										for(Iterator ito=o.getGoods().getRelationData().iterator();ito.hasNext();)
										{
											OrderItem item=(OrderItem) ito.next();
											//获取当前订单商品的sku
											String sku = item.getOuter_id() ;
												//没有等待付款的状态 不需要删除未付款锁定的库存/
												//StockManager.deleteWaitPayStock(jobName, conn,tradecontactid, orderCode,sku);
											//获取当前SKU的订单购买数量
											long qty= (long)item.getAmount();
											//在ecs_rationconfig表中存在机构添加一条库存同步记录(不包括自己）
											StockManager.addSynReduceStore(jobName, conn, tradecontactid, o.getStatus(),orderid, sku, qty, false);
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
			catch(Exception err)
			{Log.warn("返回结果不正常,检查家居就订单失败!");}
		}
		catch(Exception err)
		{Log.error(jobName, "检查家居就订单失败:\n" + err.getMessage());}
	}
}
