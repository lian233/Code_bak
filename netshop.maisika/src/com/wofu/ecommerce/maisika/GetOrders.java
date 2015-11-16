package com.wofu.ecommerce.maisika;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.maisika.util.CommHelper;
import com.wofu.ecommerce.maisika.Order;
import com.wofu.ecommerce.maisika.OrderItem;
import com.wofu.ecommerce.maisika.OrderUtils;
import com.wofu.ecommerce.maisika.Params;
public class GetOrders extends Thread {
	
	private static String jobname = "获取麦斯卡订单作业";

	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	
	private static long daymillis=24*60*60*1000L;
	
	private String lasttime;
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private boolean is_importing=false;
	public void run() {

		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_importing = true;
			try {
				
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
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
				is_importing = false;
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
		} while (true);
	}
	
	
	//获取麦斯卡新订单
	public void getOrderList(Connection conn) throws Exception
	{ 
		int pageno = 1 ;  //麦斯卡从1开始
		boolean hasNextPage = true ;	

		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{	System.out.println("ID:"+Params.tradecontactid);
					System.out.println("店铺名"+Params.dbname);
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//方法名
					LinkedHashMap<String,Object> map = new LinkedHashMap<String,Object>();
					map.put("&op","orders");
			        map.put("service","order");
			        map.put("vcode", Params.vcode);
			        map.put("mtime_start", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        map.put("mtime_end", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        map.put("page", String.valueOf(pageno));
			        map.put("page_size", Params.pageSize);
			        map.put("status","2");
			        //发送请求
			        Log.info("第"+String.valueOf(pageno)+"页");
			        
					String responseOrderListData = CommHelper.doGet(map,Params.url);
//			        String responseOrderListData = json;
					//把返回的数据转成json对象
					JSONObject responseproduct=new JSONObject(responseOrderListData);
//					Log.info("测试"+responseObj.getJSONArray("order_list").length());



					int totalCount=responseproduct.getInt("totalcount");					
					Log.info("订单总数："+totalCount);
					if (totalCount==0)
					{
						if (pageno==1L)		
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);	
										Log.info("无订单");
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
						k=10;
					    
						break;
					}

					
//					JSONArray orderlist=responseproduct.getJSONObject("response").getJSONObject("orderList").getJSONArray("order");
					JSONArray orderlist=responseproduct.getJSONArray("order_list");
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						Order o=OrderUtils.getOrderByID(order.getString("order_id"));

						//订单状态：0(已取消)10(默认):未付款;20:已付款;30:已发货;40:已收货;
						
						 //*1、如果状态为等待卖家发货则生成接口订单
						 //*2、删除等待买家付款时的锁定库存
						if(o.getPayment_code().equals("offline"))
						{
							o.setPayment_code("2");
							Log.info("货到付款订单ID:"+o.getOrder_sn()+" "+"订单状态:"+o.getOrder_state()+" "+"支付时间:"+Formatter.format(new Date(o.getPayment_time()*1000L),Formatter.DATE_TIME_FORMAT));
						}
						else
						{
							o.setPayment_code("1");
							Log.info("线上支付订单ID:"+o.getOrder_sn()+" "+"订单状态:"+o.getOrder_state()+" "+"支付时间:"+Formatter.format(new Date(o.getPayment_time()*1000L),Formatter.DATE_TIME_FORMAT));
						}
						// 买家名称
						String buyernick=responseproduct.getJSONArray("order_list").getJSONObject(j).getJSONObject("extend_order_common").optString("reciver_name");
						// 买家留言
						String order_message=responseproduct.getJSONArray("order_list").getJSONObject(j).getJSONObject("extend_order_common").optString("order_message");
						//地址
						String address=responseproduct.getJSONArray("order_list").getJSONObject(j).getJSONObject("extend_order_common").getJSONObject("reciver_info").optString("address");
//						String area=responseproduct.getJSONArray("order_list").getJSONObject(j).getJSONObject("extend_order_common").getJSONObject("reciver_info").optString("street");
						String area="";

						//电话
						String phone=responseproduct.getJSONArray("order_list").getJSONObject(j).getJSONObject("extend_order_common").getJSONObject("reciver_info").optString("phone");
						String mob_phone=responseproduct.getJSONArray("order_list").getJSONObject(j).getJSONObject("extend_order_common").getJSONObject("reciver_info").optString("mob_phone");
						String sku;
						String sql="";
						if (o.getOrder_state().equals("20"))//已付款
						{	
							System.out.println("已付款");
							if (!OrderManager.isCheck("检查麦斯卡订单", conn, o.getOrder_sn()))
							{	
								if (!OrderManager.TidLastModifyIntfExists("检查麦斯卡订单", conn, o.getOrder_sn(),new Date(o.getPayment_time()*1000L)))
								{ 
									OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username,address,area,phone,mob_phone,buyernick,order_message); //跳转
									
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getSku();
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrder_state(),o.getOrder_sn(), sku, -item.getNum(),false);
									}
								} 
							}	
						}

						
						//更新同步订单最新时间
		                if (new Date(o.getPayment_time()*1000L).compareTo(modified)>0)
		                {
		                	modified=new Date(o.getPayment_time()*1000L);
		                }
					}
					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) break;
					pageno++;
				}
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{	
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            		System.out.println("更新订单获取时间"+value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobname,je.getMessage());
	            	}
	            	
				}
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
