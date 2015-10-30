package com.wofu.netshop.dangdang.fenxiao;
import java.sql.Connection;
import com.wofu.common.tools.util.Formatter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import com.wofu.business.fenxiao.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.business.fenxiao.util.PublicUtils;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.netshop.dangdang.fenxiao.CommHelper;
import com.wofu.netshop.dangdang.fenxiao.Order;
import com.wofu.netshop.dangdang.fenxiao.OrderUtils;
import com.wofu.netshop.dangdang.fenxiao.Params;
/**
 * 下载当当订单线程类
 * @author Administrator
 *
 */
public class GetOrdersRunnable implements Runnable{
	private String jobName="下载当当订单作业";
	private Params param;
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private String lasttime="";
	private String refundlasttime;
	private static long daymillis=24*60*60*1000L;
	private CountDownLatch watch;
	private String username="";
	private boolean is_exporting = false;
	
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
	//获取当当新订单
	public void getOrderList(Connection conn) throws Exception
	{
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		lasttime=PublicUtils.getConfig(conn,"LastOrderTime",param.shopid);
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		Log.info("本次获取当当订单开始");
		for (int k=0;k<5;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//方法名
					String methodName="dangdang.orders.list.get";
					//生成验证码 --md5;加密
					Date temp = new Date();
					
//					System.out.println("app_Secret"+param.app_Secret);
//					System.out.println("app_key"+param.app_key);
					
//					System.out.println("测试4"+temp);
					String sign = CommHelper.getSign(param.app_Secret, param.app_key, methodName, param.session,temp) ;
					Hashtable<String, String> params = new Hashtable<String, String>() ;
					params.put("sign", sign) ;
					params.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
					params.put("app_key",param.app_key);
					params.put("method",methodName);
					params.put("format","xml");
					params.put("session",param.session);
					params.put("sign_method","md5");
					params.put("lastModifyTime_end", URLEncoder.encode(Formatter.format(enddate, Formatter.DATE_TIME_FORMAT), param.encoding)) ;
					params.put("lastModifyTime_start", URLEncoder.encode(Formatter.format(startdate, Formatter.DATE_TIME_FORMAT), param.encoding)) ;
					params.put("p", String.valueOf(pageIndex)) ;
					params.put("pageSize", String.valueOf(param.total)) ;
					params.put("sendMode", param.sendMode) ;
					//获取返回结果
					String repsonseText = CommHelper.sendRequest(param.url,"GET",params,"");
//					Log.info("参数"+params);
//					Log.info("返回结果"+repsonseText);
					repsonseText = CommHelper.filterChar(repsonseText);
					Document doc = DOMHelper.newDocument(repsonseText, param.encoding);
					Element urlset = doc.getDocumentElement();
					if(DOMHelper.ElementIsExists(urlset,"Error"))
					{
						Element error = (Element) urlset.getElementsByTagName("Error").item(0);
						String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
						String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
						if(!"".equals(operCode))
						{
							Log.error("获取当当订单列表", "获取订单列表失败，操作码："+operCode+",操作结果信息："+operation);
							hasNextPage = false ;
							break ;
						}
					}
	
					Element totalInfo = (Element) urlset.getElementsByTagName("totalInfo").item(0) ;//
					
					String pageTotal = DOMHelper.getSubElementVauleByName(totalInfo, "pageTotal");
					
					if (pageTotal==null || pageTotal.equals("") || pageTotal.equals("0"))
					{				
						if (n==1)		
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
						k=5;
						break;
					}
					
					NodeList ordersList = urlset.getElementsByTagName("OrderInfo") ;
					for(int i = 0 ; i< ordersList.getLength() ; i++)
					{
						try{
							Element orderInfo = (Element) ordersList.item(i) ;
							
							String orderID = DOMHelper.getSubElementVauleByName(orderInfo, "orderID") ;
							Order o = OrderUtils.getOrderByID(param.url, orderID,param.session,param.app_key,param.app_Secret) ;
							
							if(o != null)
							{
								Log.info("订单号【"+ o.getOrderID() +"】,状态【"+ OrderUtils.getOrderStateByCode(o.getOrderState()) +"】,最后修改时间【"+ Formatter.format(o.getLastModifyTime(),Formatter.DATE_TIME_FORMAT) +"】") ;

								//如果是等待发货订单，创建接口订单成功，减少其它店的库存
								if("101".equals(o.getOrderState()))
								{	//当这个订单在表customerorder和表CustomerOrderRefList都不存在相应的记录时，往下执行

										if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
										{
											try
											{	//把订单信息加入通知表it_downnote和接口主表ns_customerorder，把订单中的所有商品加入订单接口商品明细表ns_orderitem
												OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);
												
											} catch(Throwable sqle)
											{
												throw new JException("生成接口订单出错!" + sqle.getMessage());
											}
										}
									
								}
								
								else if("50".equals(o.getOrderState()))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										//
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);	
										
									}
								}
								else if("100".equals(o.getOrderState()))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										//
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);	
										
									}
								}
								//订单取消,增加已取消库存
								else if("-100".equals(o.getOrderState()))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										//
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);
									}
									
								}
								else if("300".equals(o.getOrderState()))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										//
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);
									}
									
								}
								else if("400".equals(o.getOrderState()))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										//
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);
									}
									
								}
								
								else if("-200".equals(o.getOrderState()))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										//
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);
									}
									
								}
								
								else if("1100".equals(o.getOrderState()))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										//
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);
									}
									
								}
								else if (o.getOrderState().equals("1000"))  //交易成功
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);							
									}
					
								}
								
								//如果当前订单时间大于开始取订单时间，则更新下次取订单时间(现在取订单列表最后修改时间)
								//更新同步订单最新时间
				                if (o.getLastModifyTime().compareTo(modified)>0)
				                {
				                	modified=o.getLastModifyTime();
				                }
							}
						}catch(Throwable ex){
							if(conn!=null && !conn.getAutoCommit()) conn.rollback();
							Log.error(jobName, ex.getMessage());
						}
					}
					//判断是否有下一页
					if("".equals(pageTotal) || pageTotal == null)
						pageTotal="0" ;
					if(pageIndex >= Integer.parseInt(pageTotal))
						hasNextPage = false ;
					else
						pageIndex ++ ;
					
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
				
				break;
			} catch (Throwable e) 
			{
				e.printStackTrace();
				if (++k >= 5)
					try {
						throw e;
					} catch (Throwable e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
		Log.info("本次获取当当订单完毕");
	}

	
	
	
}