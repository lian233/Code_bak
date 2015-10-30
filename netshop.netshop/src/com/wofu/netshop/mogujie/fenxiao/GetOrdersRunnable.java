package com.wofu.netshop.mogujie.fenxiao;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import com.wofu.business.fenxiao.util.PublicUtils;
import com.wofu.business.fenxiao.order.OrderManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.common.fenxiao.Utils;

/**
 * 下载蘑菇街订单线程类
 * @author Administrator
 *
 */
public class GetOrdersRunnable implements Runnable{
	private String jobName="下载蘑菇街订单作业";
	private CountDownLatch watch;
	private String lasttime;
	private String refundlasttime;
	private Params param;
	private static long daymillis=24*60*60*1000L;
	public GetOrdersRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
}
	public void run() {
		// TODO Auto-generated method stub
		Connection conn=null;
		try{
			conn=PoolHelper.getInstance().getConnection("shop");
			getOrderList(conn);
		}catch(Throwable e){
			try {
				if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				conn.setAutoCommit(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					Log.error(param.username,"关闭数据库事务出错: "+e1.getMessage(),null);
				}
				Log.info(param.username,jobName+" "+e.getMessage(),null);
			}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.error(param.username,"关闭数据库连接出错  "+e.getMessage());
				}
				watch.countDown();
		}
		
	}
	 
	/*
	 * 获取一天之类的所有订单
	 */
	private void getOrderList(Connection conn) throws Exception
	{		
		long pageno=1L;
		lasttime=PublicUtils.getConfig(conn,"LastOrderTime",param.shopid);
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					Map<String, String> orderlistparam = new HashMap<String, String>();
			        //系统级参数设置
					orderlistparam.put("app_key", param.app_key);
					orderlistparam.put("access_token", param.token);
			        orderlistparam.put("method", "youdian.trade.sold.get");
			        orderlistparam.put("start_updated", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparam.put("end_updated", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        orderlistparam.put("page_no", String.valueOf(pageno));
			        orderlistparam.put("page_size", "50");
			        
					String responseOrderListData = Utils.sendByPost(orderlistparam,"", param.url);
					Log.info(param.username,"result:　"+responseOrderListData,null);
					
					JSONObject responseproduct = new JSONObject(responseOrderListData);
					if (responseproduct.getJSONObject("status").getInt("code")!=10001)
					{
						Log.error(param.username,jobName, "取订单列表失败:"+responseproduct.getJSONObject("status").getString("msg"));
						k=10;
						break;
					}
					JSONObject orders = responseproduct.getJSONObject("result").getJSONObject("data");
					int hasNext= orders.getInt("has_next");  //1代表没有下一页
					JSONArray orderlist=orders.getJSONArray("trades");
					if (1==hasNext && orderlist.length()==1)
					{
						if (pageno==1L)		
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
						k=10;
						break;
						
					}
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						Order o= new Order();
						o.setObjValue(o, order);
						o.setFieldValue(o, "orders", order.getJSONArray("orders"));
						Log.info(param.username,o.getTid()+" "+o.getStatus()+" "+" "+o.getPay_status()+" "+o.getShip_status()+" "+Formatter.format(o.getLastmodify(),Formatter.DATE_TIME_FORMAT),null);
						
						 //*1、如果状态为等待卖家发货则生成接口订单
						 //*2、删除等待买家付款时的锁定库存 
						 		
						String sku;
						String sql="";
						if (o.getStatus().equals("TRADE_ACTIVE") && "PAY_FINISH".equals(o.getPay_status()) && "SHIP_NO".equals(o.getShip_status()))
						{	
							
							//if (!OrderManager.isCheck("检查蘑菇街订单", conn, o.getTid()))
							//{
								if (!OrderManager.TidLastModifyIntfExists("检查蘑菇街订单", conn, o.getTid(),o.getLastmodify()))
								{
									OrderUtils.createInterOrder(conn,o,param.username,param.shopid,20);
								}
							//}
	
							//等待买家付款时记录锁定库存
						}
						
						
						else if (o.getStatus().equals("TRADE_ACTIVE") && "PAY_NO".equals(o.getPay_status()))
						{						
							//付款以后用户退款成功，交易自动关闭
							//释放库存,数量为负数						
						}else if (o.getStatus().equals("TRADE_CLOSED") )
						{
							//交易取消的订单也进系统来
								if (!OrderManager.TidLastModifyIntfExists("检查蘑菇街订单", conn, o.getTid(),o.getLastmodify()))
								{
									OrderUtils.createInterOrder(conn,o,param.username,param.shopid,110);
								}
							
				
						}
						else if ( o.getStatus().equals("TRADE_FINISHED")  )
						{
							//交易取消的订单也进系统来
								if (!OrderManager.TidLastModifyIntfExists("检查蘑菇街订单", conn, o.getTid(),o.getLastmodify()))
								{
									OrderUtils.createInterOrder(conn,o,param.username,param.shopid,100);
								}
							
				
						}
						else if (o.getStatus().equals("TRADE_ACTIVE") && "PAY_FINISH".equals(o.getPay_status()) && "SHIP_PREPARE".equals(o.getShip_status()) )
						{
							//交易取消cf的订单也进系统来
								if (!OrderManager.TidLastModifyIntfExists("检查蘑菇街订单", conn, o.getTid(),o.getLastmodify()))
								{
									OrderUtils.createInterOrder(conn,o,param.username,param.shopid,30);
								}
							
				
						}
						
						else if (o.getStatus().equals("ORDER_CUSTOM_CALLTO_RETUR")
							||o.getStatus().equals("ORDER_CUSTOM_CALLTO_CHANGE")
							||o.getStatus().equals("ORDER_RETURNED")
							||o.getStatus().equals("ORDER_CHANGE_FINISHED"))
						{
							
							//OrderUtils.getRefund(conn,param.tradecontactid,o);
								
				
						}
						
						//更新同步订单最新时间
		                if (o.getLastmodify().compareTo(modified)>0)
		                {
		                	modified=o.getLastmodify();
		                }
					}
					//判断是否有下一页
					if (1==hasNext) break;
					pageno++;
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
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn(param.username,"远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e),null);
				Thread.sleep(10000L);
				
			}
		}
	}
}
