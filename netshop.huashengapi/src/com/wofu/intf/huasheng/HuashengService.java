package com.wofu.intf.huasheng;
//import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStreamReader;
import java.io.PrintWriter;
//import java.net.URLDecoder;
//import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
//import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.service.Params;
import com.wofu.common.tools.sql.PoolHelper;
//import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.huasheng.Order;
import com.wofu.ecommerce.huasheng.OrderItem;
import com.wofu.ecommerce.huasheng.OrderUtils;
//import com.wofu.ecommerce.huasheng.util.*;


@SuppressWarnings("serial")
public class HuashengService extends HttpServlet {
	private String jobname = "花生API接收订单数据作业";
	//错误信息
	String errResult = "{\"code\":-5,\"msg\":\"Internal server error!\"}";	//内部服务器错误
	
	//不允许get推送数据
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		JSONObject responseData = new JSONObject();
		String result = errResult;
		try
		{
			responseData.put("code", -1);
			responseData.put("msg", "Please use Post to submit data!");	//请使用POST提交数据
			result = responseData.toString();
		}
		catch(Exception e){ result = errResult; }
		//返回结果给对方
		Log.info("响应:" + result);	//Log.info("响应:" + result);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();
		
//		response.getOutputStream().write(result.getBytes());
//		response.getOutputStream().flush();	
//		response.getOutputStream().close();	
		//doPost(request,response);
	}
	
	//接收推送订单数据
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		Connection conn=null;
		String returnResult = errResult;
		try
		{
			Log.info(jobname + "   开始接收数据 ...");
			//返回的json信息
			JSONObject responseData = new JSONObject();
			//设置编码
			request.setCharacterEncoding("UTF-8");
			//获取数据库连接
			conn = PoolHelper.getInstance().getConnection(Params.getInstance().getProperty("dbname"));
			//读取配置参数
			String vcode = this.getInitParameter("vcode");	//验证码
			String tradecontactid = this.getInitParameter("tradecontactid");		//店铺id
			String username = new String(this.getInitParameter("username").getBytes("ISO-8859-1"), "GBK");		//店铺名称
			
			//获取传入的参数信息
			String Par_service = request.getParameter("service");
			String Par_vcode = request.getParameter("vcode");
			String Par_order = request.getParameter("order");
			
			//输出提交过来的参数
//			Map<String,String[]> map = request.getParameterMap();
//			for(Iterator it = map.keySet().iterator();it.hasNext();){
//				String name=(String)it.next();
//				String value = map.get(name)[0];
//				Log.info("name:" + name + " value:" + value);
//			}
			
			//检查传入参数
			if(Par_service == null)
			{
				responseData.put("code", -2);
				responseData.put("msg", "Necessary parameters: 'service' does not exist!");
				returnResult = responseData.toString();
				Log.info("必要参数service为空!");
				throw new Exception("normal");
			}
			if(Par_vcode == null)
			{
				responseData.put("code", -2);
				responseData.put("msg", "Necessary parameters: 'vcode' does not exist!");
				returnResult = responseData.toString();
				Log.info("必要参数vcode为空!");
				throw new Exception("normal");
			}
			if(Par_order == null)
			{
				responseData.put("code", -2);
				responseData.put("msg", "Necessary parameters: 'order' does not exist!");
				returnResult = responseData.toString();
				Log.info("必要参数Order为空!");
				throw new Exception("normal");
			}
			
			//输出提交过来的order参数
			Log.info("order[]:" + request.getParameter("order"));
			
			//验证vcode
			if(vcode == null) throw new Exception("验证码未设置!");
			if(vcode.equals("")) throw new Exception("验证码未设置!");
			if(!vcode.equals(Par_vcode))
			{
				responseData.put("code", -3);
				responseData.put("msg", "Verification code is incorrect!");
				returnResult = responseData.toString();
				Log.info("传入的验证码不匹配,无法处理!");
				throw new Exception("normal");
			}
			
			//验证service
			if(!Par_service.toLowerCase().equals("order"))
			{
				responseData.put("code", -4);
				responseData.put("msg", "Service name is incorrect!");
				returnResult = responseData.toString();
				Log.info("传入的服务名称不匹配,无法处理!");
				throw new Exception("normal");
			}
			
			//读取订单列表
			JSONArray errOrderList = new JSONArray();
			try {
				JSONArray OrderList = new JSONArray(Par_order);
				Log.info("待处理的订单数:" + OrderList.length());
				for(int idxOrder = 0;idxOrder < OrderList.length();idxOrder++)
				{
					//获取当前订单
					JSONObject orderJson = OrderList.getJSONObject(idxOrder);
					Order o = new Order();
					o.setObjValue(o, orderJson);
					String orderid = orderJson.getString("order_id");
					Date mtime =  Formatter.parseDate(orderJson.getString("mtime"),Formatter.DATE_TIME_FORMAT);	//修改时间
					
					
					Log.info(String.format("正在处理订单:%s   订单状态:%s   付款方式:%s   发货状态:%s",
							orderid,
							OrderUtils.getOrderStateByCode(o.getStatus()),
							OrderUtils.getPayWayByCode(o.getPay_id()),
							OrderUtils.getDeliverStatusByCode(o.getPay_status())
							));

					//尝试处理订单
					try {
						int Deliver_status = o.getDeliver_status() == null ? 0 : Integer.valueOf(o.getDeliver_status());
						int Order_Status  = o.getStatus() == null ? -1 : Integer.valueOf(o.getStatus());
						int Pay_id = o.getPay_id() == null ? -1 : Integer.valueOf(o.getPay_id());
						
						//检查订单状态
						//只取未发货, 已付款且线上支付 或 未付款且货到付款  的订单
						if(Deliver_status == 0 && ((Order_Status == Pay_id && Order_Status == 1) || (Order_Status == Pay_id && Order_Status == 0)))
						{
							if (!OrderManager.isCheck(jobname, conn, orderid) && !OrderManager.TidLastModifyIntfExists(jobname, conn, orderid, mtime))
							{
								Log.info("正在生成["+orderid+"]的接口订单");
								try
								{
									//生成接口订单
									OrderUtils.createInterOrder(conn, o, tradecontactid, username);
									for(Iterator ito=o.getDetail().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getSku();
										long qty= (long)item.getNum();
										
										//没有等待付款的状态 不需要删除未付款锁定的库存
										//StockManager.deleteWaitPayStock(jobname, conn,tradecontactid, orderid, sku);
										
										//在ecs_rationconfig表中存在机构添加一条库存同步记录(不包括自己）
										StockManager.addSynReduceStore(jobname, conn, tradecontactid, o.getStatus(), o.getOrder_id(), sku, qty, false);
									}
								} catch(SQLException sqle)
								{
									throw new JException("生成接口订单出错!" + sqle.getMessage());
								}
							}
							else
								Log.info("订单:" +orderid+ "已经存在与数据库中");
						}
						else
						{
							Log.info("当前订单:" + orderid + "状态不符合处理要求,忽略处理!");
						}
					} catch (Exception e) {
						try {
							JSONObject err = new JSONObject();
							err.put("orderid", orderid);
							err.put("errmsg", e.getMessage());
							errOrderList.put(err);
						} catch (Exception err) { throw new Exception("写入错误订单列表出错!"); }
					}
				}
			} catch (Exception e) {
				if(!e.getMessage().equals("写入错误订单列表出错!"))
				{
					e.printStackTrace();
					responseData.put("code", -7);
					responseData.put("msg", "Order list data is not standardized!");	//订单列表数据不规范
					returnResult = responseData.toString();
					Log.info("传入的订单列表数据不规范,无法处理!");
					throw new Exception("normal");
				}
				else
					throw e;
			}
			if(errOrderList.length() == 0)
			{
				responseData.put("code", 0);
				responseData.put("msg", "Successfully received!");
				returnResult = responseData.toString();
				Log.info(jobname + "   接收数据成功!");
			}
			else
			{
				responseData.put("code", -6);
				responseData.put("msg", "Some orders received failed!");
				responseData.put("fail_orders", errOrderList);
				returnResult = responseData.toString();
				Log.info(jobname + "   接收数据部分成功!");
			}
		}
		catch(Exception e)
		{
			try
			{
				if (!conn.getAutoCommit())
				{
					try
					{
						conn.rollback();
					}
					catch (Exception rollbackexception) 
					{ 
						Log.error(jobname, "回滚事务失败:"+rollbackexception.getMessage());
					}
					try
					{
						conn.setAutoCommit(true);
					}
					catch (Exception commitexception) 
					{ 
						Log.error(jobname, "设置自动提交事务失败:"+commitexception.getMessage());
					}
				}
			}catch(SQLException sqle)
			{
				Log.error(jobname, "设置自动提交事务失败:"+sqle.getMessage());
			}
			try {
				if (conn != null)
				{
					conn.close();
					conn = null;
				}
			} catch (Exception closeexception) {
				Log.error(jobname, "关闭数据库连接失败:"+closeexception.getMessage());
			}
			if(!e.getMessage().equals("normal"))
			{
				returnResult = errResult;
				Log.error(jobname, "发生内部错误:" + e.getMessage());
			}
			Log.info(jobname + "   接收数据失败!");
		}
		finally {			
			try {
				if (conn != null)
				{
					conn.close();
				}
			} catch (Exception e) {
				Log.error(jobname, "关闭数据库连接失败:"+e.getMessage());
			}
		}
		
		//返回结果给对方
		Log.info("响应:" + returnResult);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(returnResult);
		out.close();
	}
}
