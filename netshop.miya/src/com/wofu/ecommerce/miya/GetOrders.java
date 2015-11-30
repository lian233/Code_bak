package com.wofu.ecommerce.miya;


import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.ParseException;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.miya.OrderItem;
import com.wofu.ecommerce.miya.OrderUtils;
import com.wofu.ecommerce.miya.Order;
import com.wofu.ecommerce.miya.Params;
import com.wofu.ecommerce.miya.utils.Utils;
import com.wolf.common.tools.util.JException;

public class GetOrders extends Thread {

	private static String jobname = "获取蜜芽网订单作业";
	
	private static long daymillis=24*60*60*1000L;
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_importing = true;
			try {
				//获取数据库连接
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.miya.Params.dbname);
				//获取上次抓订单的时间
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				//开始抓单
				getOrderList(connection);
				
			} catch (Exception e) {
				try {
					//如果数据库连接不为空并且数据库还没提交Commit。
					if (connection != null && !connection.getAutoCommit())
						//执行回滚操作
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					//如果数据库连接不为空，那么关闭数据库连接
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			//叫java进行回收
			System.gc();
			//设定当前系统时间为开始等待时间
			long startwaittime = System.currentTimeMillis();
			//开始一次循环，为了等待时间一段时间再进行下一步动作。当前系统时间减去开始时间如果小于设定的时间，就为true，sleep1秒。
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.miya.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}


	


	/*
	 * 获取一天之类的所有订单
	 */
	private void getOrderList(Connection conn) throws Exception
	{	
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{	//设定开始抓单的时间
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()-1000L*60*120);
					//设定结束抓单的时间
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					String endtime =Formatter.format(enddate, Formatter.DATE_TIME_FORMAT);
					//设定当前时间，并且做一个判断，是否结束时间大于当前时间，如果大于，就把现在的时间赋值给结束时间
					Date presentTime = new Date();
					SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date=new Date();
					if(enddate.after(presentTime)){
					endtime = dateFormater.format(date.getTime()-10000L);
					}
					//创建一个map，用来放KEY和VALUESE。
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //系统级参数设置
			        orderlistparams.put("method", "mia.orders.search");
					orderlistparams.put("vendor_key", Params.vendor_key);
			        orderlistparams.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
			        orderlistparams.put("version", Params.ver);
			        
			        //应用级输入参数
			        //订单状态1.待付款2. 已付款待发货3. 发货中4. 发货完成5. 订单完结 6. 已取消
			        orderlistparams.put("order_state", "2");
			        orderlistparams.put("start_date", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("end_date", endtime);
//			        orderlistparams.put("start_date", "2015-11-25 11:26:10");
//			        orderlistparams.put("end_date", "2015-11-25 15:27:50");
//			        orderlistparams.put("date_type", "0");//查询时间类型，默认按修改时间查询，1为按订单创建时间查询；其它数字为按订单修改时间 。
			        orderlistparams.put("page", String.valueOf(pageno));
			        orderlistparams.put("page_size", "20");
			        //把系统级参数和应用级参数合起来,post请求过去，里面包含了各钟加密方法，每个网店的加密方法都不一样。
					String responseOrderListData = Utils.sendByPost(orderlistparams, Params.secret_key, Params.url);
					Log.info(Utils.Unicode2GBK(responseOrderListData));
					//获得的responseOrderListData字符串，转换为json对象。
					JSONObject responseproduct = new JSONObject(responseOrderListData);
					System.out.println("从"+Formatter.format(startdate, Formatter.DATE_TIME_FORMAT)+"开始抓取订单"+"到"+endtime);
					//解析json对象
					String msg = responseproduct.optString("msg");
					int code = responseproduct.optInt("code");
					if(code!=200){
						Log.error("失败，退出本次循环,错误信息：", msg);
						break;
					}
					JSONObject orders_list_response=responseproduct.getJSONObject("content").getJSONObject("orders_list_response");
					int count=0;
					count=orders_list_response.optInt("total");
					if (count==0)
					{				
						if (pageno==1L)		
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
									PublicUtils.setConfig(conn, lasttimeconfvalue, value);
								}
							}catch(ParseException e)
							{
								Log.error(jobname, "不可用的日期格式!"+e.getMessage());
							}
						}
						k=10;
						break;
					}
					
					
					JSONArray orderlist=orders_list_response.getJSONArray("order_list");
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject data =orderlist.getJSONObject(j);
						Order o=new Order();
						o.setObjValue(o, data);
						OrderItem item=new OrderItem();
						JSONArray orderItemList =data.getJSONArray("item_info_list");
						Log.info("订单号为:"+o.getOrder_id()+"修改时间为:"+Formatter.format(o.getModify_time(), Formatter.DATE_TIME_FORMAT)+" 第"+pageno+"页 | 订单数量为"+orderlist.length()+" 当前数量为"+(j+1));
						if (o.getOrder_state().equals("2"))
						{
							//蜜芽检测订单专用

							if (!OrderManager.isCheck("检查蜜芽订单", conn, o.getOrder_id()))
							{
								String sql="select count(*) from ns_customerorder with(nolock) where TradeContactID = '"+Params.tradecontactid+"' and tid='"+o.getOrder_id()+"' ";
								if(SQLHelper.intSelect(conn, sql)==0)
								{
									if (!OrderManager.TidLastModifyIntfExists("检查蜜芽订单", conn, o.getOrder_id(),o.getModify_time()))
									{	
										OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username,data);
										for(int i=0;i<orderItemList.length();i++)
										{	
											JSONObject orderItem =orderItemList.getJSONObject(i);
											item.setObjValue(item, orderItem);
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrder_state(),o.getOrder_id(), item.getSku_id(), -item.getItem_total(),false);
										}
									}
								}else{
									System.out.println("在ns找到该订单跳过");
								}
								
							}
							//等待买家付款时记录锁定库存
						}
						
						//更新同步订单最新时间
		                if (o.getModify_time().compareTo(modified)>0)
		                {
		                	modified=o.getModify_time();
		                }
					}
					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(count/20.0))).intValue())
					{
						break;
					}
					pageno++;
				}
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					System.out.println("蜜芽抓单完成，修改抓单时间为"+modified);
					String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
					PublicUtils.setConfig(conn, lasttimeconfvalue, value);
				}
				System.out.println("蜜芽抓单完成");
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	












	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
