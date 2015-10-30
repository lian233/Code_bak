package com.wofu.ecommerce.beibei;


import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.ParseException;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.beibei.Params;
import com.wofu.ecommerce.beibei.utils.Utils;
import com.wofu.ecommerce.beibei.Order;
import com.wolf.common.tools.util.JException;

public class CheckOrder extends Thread {

	private static String jobname = "检查贝贝网订单漏单作业";
	
	private static long daymillis=60*60*3*1000L;
	
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
						com.wofu.ecommerce.beibei.Params.dbname);
				//获取上次抓订单的时间
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				//开始抓单
				sleep(10000L);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.beibei.Params.waittime * 1000 *2*60*2))		
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
//		System.out.println("防漏单线程开始运行");
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{	
			    	Date startdate=new Date((new Date()).getTime()-daymillis);
					Date enddate=new Date();
					System.out.println("防漏单线程开始运行,抓单范围"+Formatter.format(startdate, Formatter.DATE_TIME_FORMAT)+"到"+Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
					//设定开始抓单的时间
					//创建一个map，用来放KEY和VALUESE。
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //系统级参数设置
			        orderlistparams.put("method", "beibei.outer.trade.order.get");
					orderlistparams.put("app_id", Params.appid);
			        orderlistparams.put("session", Params.session);
			        orderlistparams.put("timestamp", time());
			        orderlistparams.put("version", Params.ver);
//			        System.out.println(Params.appid);
//			        System.out.println( Params.session);
			        //应用级输入参数
			        //订单状态 -1:返回所有,1:待发货,2:已发货,3:已完成,4:已关闭
			        orderlistparams.put("status", "1");
			        orderlistparams.put("time_range", "modified_time");
			        orderlistparams.put("start_time", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("end_time", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
//			        orderlistparams.put("start_time", "2015-10-27 08:00:00");
//			        orderlistparams.put("end_time", "2015-10-27 10:00:00");
			        orderlistparams.put("page_no", String.valueOf(pageno));
			        orderlistparams.put("page_size", "20");
			        //把系统级参数和应用级参数合起来,post请求过去，里面包含了各钟加密方法，每个网店的加密方法都不一样。
					String responseOrderListData = Utils.sendByPost(orderlistparams, Params.secret, Params.url);
//					System.out.println("测试 "+responseOrderListData);
					//获得的responseOrderListData字符串，转换为json对象。
					JSONObject responseproduct = new JSONObject(responseOrderListData);
//					System.out.println("从"+Formatter.format(startdate, Formatter.DATE_TIME_FORMAT)+"开始抓取订单");
					//解析json对象
					int count=responseproduct.optInt("count");
					String message=responseproduct.optString("message");
					boolean success = responseproduct.optBoolean("success");
					if(!success){
						Log.error("失败，退出本次循环"+"错误信息："+message, message);
						break;
					}
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
					
					JSONArray orderlist=responseproduct.getJSONArray("data");
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject data =orderlist.getJSONObject(j);
						Order o=new Order();
						o.setObjValue(o, data);
						OrderItem item=new OrderItem();
						JSONArray orderItemList =data.getJSONArray("item");
						Log.info("订单号为:"+o.getOid()+" 第"+pageno+"页 | 当前页订单数量为"+orderlist.length()+" 当前数量为"+(j+1)+" 总数为"+count);				 		
						if (o.getStatus().equals("1"))
						{
							if (!OrderManager.isCheck("检查贝贝网订单", conn, o.getOid()))
							{	
								if (!OrderManager.TidLastModifyIntfExists("检查贝贝网订单", conn, o.getOid(),o.getModified_time()))
								{	
									OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username,data);
									Log.info("抓取到漏单"+o.getModified_time()+"订单号"+o.getOid());
									for(int i=0;i<orderItemList.length();i++)
									{	
										JSONObject orderItem =orderItemList.getJSONObject(i);
										item.setObjValue(item, orderItem);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getOid(), item.getOuter_id(), -item.getNum(),false);
									}
								}
							}
	
							//等待买家付款时记录锁定库存
						}
						
						//更新同步订单最新时间
//		                if (o.getModified_time().compareTo(modified)>0)
//		                {
//		                	modified=o.getModified_time();
//		                }
					}
					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(count/20.0))).intValue())
					{	
						break;
					}
					pageno++;
				}
//				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
//				{
//					String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
//					PublicUtils.setConfig(conn, lasttimeconfvalue, value);
//				}
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	



	public String time() {
		Long time= System.currentTimeMillis()/1000;
		return time.toString();
	}


	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
