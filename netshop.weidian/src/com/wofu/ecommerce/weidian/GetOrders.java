package com.wofu.ecommerce.weidian;

import java.net.URLEncoder;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weidian.utils.Utils;
import com.wofu.ecommerce.weidian.utils.getToken;

public class GetOrders extends Thread
{
	private static String jobName = "获取微店订单作业";
	private static String lasttimeconfvalue="微店取订单最新时间";  //Parmas类是从其他地方复制过来的，已经过了修改
	private static long daymillis=24*60*60*1000L;
	private boolean is_importing=false;
	private String lasttime;
	private static String  orderLineStatus="1";
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	@Override
	public void run() 
	{
		Log.info(jobName, "启动[" + jobName + "]模块");
		do 
		{
			Connection connection = null;
			is_importing = true;
			try 
			{
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.weidian.Params.dbname); //数据库名暂时不明
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getOrderList(connection);  
			} catch (Exception e)
			{
				e.printStackTrace();
				try 
				{
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) 
				{
					Log.error(jobName, "回滚事务失败");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally
			{
				is_importing = false;
				try
				{
					if (connection != null) connection.close();
				} catch (Exception e)
				{
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long current = System.currentTimeMillis();
			while(System.currentTimeMillis()-current<Params.waittime*60*1000){
				try {
					Thread.sleep(100L);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} while (true);
//		super.run();
	}
	
	
	/*****供货商获取订单详细信息*****/
	public void getOrderList(Connection conn) throws Throwable
	{ 
		String token =getToken.getToken_zy(conn);
		JSONObject responseResult=new JSONObject();
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					
					/**取简单列表**/
					JSONObject param_Object = new JSONObject();
					JSONObject public_Object = new JSONObject();
					
					param_Object.put("page_num", pageno);
					param_Object.put("order_type", "");
					param_Object.put("add_start", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));//URLEncoder.encode(Formatter.format(startdate, Formatter.DATE_TIME_FORMAT),"UTF-8")
					param_Object.put("add_end",   Formatter.format(enddate  , Formatter.DATE_TIME_FORMAT));//URLEncoder.encode(Formatter.format(enddate  , Formatter.DATE_TIME_FORMAT),"UTF-8")
					public_Object.put("method", "vdian.order.list.get");
					public_Object.put("access_token", token); //写个方法用于获取access_token
					public_Object.put("version", "1.1"); 
					public_Object.put("format", "json"); 
					String opt_to_sever = Params.url + "?param=" + URLEncoder.encode(param_Object.toString(),"UTF-8") + "&public=" + URLEncoder.encode(public_Object.toString(),"UTF-8");
					String responseOrderListData = Utils.sendbyget(opt_to_sever);
					responseResult=new JSONObject(responseOrderListData);    //所有信息
					String errdesc="";
					/**错误信息*/
					System.out.println("微店返回的信息"+responseOrderListData);
					//{"status":{"status_code":10013,"status_reason":"access_token过期"}}
					try
					{
						if(!responseResult.getJSONObject("status").getString("status_reason").equals("success"))
						{
							errdesc=errdesc+" "+responseResult.getJSONObject("status").getString("status_reason"); 
						}
					} catch (Exception e)
					{
						// TODO: handle exception
					}
					/**错误信息*/
					if (responseResult.getJSONObject("result").getInt("total_num")==0)  //订单列表信息不存在，记录总数一条都没有
					{
						if (pageno==1L)
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(jobName, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobName, "不可用的日期格式!"+e.getMessage());
							}
						}
						Log.error(jobName, /*"取订单列表失败:"*/"没有该订单、订单已发货，或没有订单需要处理:"+errdesc);
					}
					k=10;
					break;
				}
				/**错误处理，有错就break*/
				if(!responseResult.getJSONObject("status").getString("status_reason").equals("success"))
				{
					String errdesc="";
					errdesc=errdesc+" "+responseResult.getJSONObject("status").getString("status_reason"); 
					Log.error(jobName, errdesc);
					k=10;
					break;
				}
				/**错误处理，有错就break*/
				if(responseResult.getJSONObject("result").getInt("total_num")==0)
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
			                	}catch(JException je)
			                	{
			                		Log.error(jobName, je.getMessage());
			                	}
							}
						}catch(ParseException e)
						{
							Log.error(jobName, "不可用的日期格式!"+e.getMessage());
						}
					}
					k=10;
					break;
				}
				JSONArray orderlist=responseResult.getJSONObject("result").getJSONArray("orders");
				for(int j=0;j<responseResult.getJSONObject("result").getInt("order_num");j++)
				{
					JSONObject order=orderlist.getJSONObject(j);
					Order o = OrderUtils.getOrderByID(order.getString("order_id"),token); //订单详细
					//System.out.println("o.getTime():"+o.getTime()+","+o.);
					Log.info(o.getOrder_id()+" 订单情况："+o.getStatus()+" "+Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT));
					String sku;
					String sql="";
					/*订单状态：unpay 未付款 pay 待发货  unship_refunding 未发货，申请退款
					ship 已发货
					shiped_refunding 已发货，申请退款
					accept 已确认收货
					accept_refunding已确认收货，申请退款
					finish 订单完成
					close订单关闭*/
					if(/*o.getStatus().equals("unpay") || */o.getStatus().equals("pay")) 
					{
						if (!OrderManager.isCheck("检查微店订单", conn, o.getOrder_id()))
						{
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date getUpdateDate_Date_type = null;
							getUpdateDate_Date_type = o.getPay_time();
							
							if (!OrderManager.TidLastModifyIntfExists("检查微店订单", conn, o.getOrder_id(),getUpdateDate_Date_type))
							{
								OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
								for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									sku=item.getSku_id();
									StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getOrder_id(), sku);
									StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, String.valueOf(o.getStatus()), o.getOrder_id(), sku, 0, false);
								}
							}
							//等待买家付款时记录锁定库存
						}
					}
					else if(o.getStatus().equals("ship") || o.getStatus().equals("finish") || o.getStatus().equals("unpay"))
					{
						for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
						{
							/**检查程序是否能运行到这个部分，可以了**/
//							System.out.println("检查程序是否能运行到这个部分:");
							OrderItem item=(OrderItem) ito.next();
							sku=item.getSku_id();
							StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getOrder_id(), sku);
							if(StockManager.WaitPayStockExists(jobName, conn, Params.tradecontactid, o.getOrder_id(), sku))//有获取到等待买家付款状态时才加库存
								StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, String.valueOf(o.getStatus()), o.getOrder_id(), sku, 0, false);
							/**检查未处理的订单是否可以正常写入数据库**/
//							System.out.println(sku);
						}
					}
					//付款以后用户退款成功，交易自动关闭
					//释放库存,数量为负数	
					else if(o.getStatus().equals("unship_refunding")||o.getStatus().equals("shiped_refunding")
								||o.getStatus().equals("accept_refunding")||o.getStatus().equals("close")) 
					{
						for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
						{
							OrderItem item=(OrderItem) ito.next();
							sku=item.getSku_id();
							StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getOrder_id(), sku);
							if(StockManager.WaitPayStockExists(jobName, conn, Params.tradecontactid, o.getOrder_id(), sku))
								StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, String.valueOf(o.getStatus()), o.getOrder_id(), sku, 0, false);
						}
					}
					else if(o.getStatus().equals("accept"))  //2-已确认
					{
						for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
						{
							OrderItem item=(OrderItem) ito.next();
							sku=item.getSku_id();
							StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getOrder_id(), sku);
						}
					}
					//更新同步订单最新时间
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date getUpdateDate_date = null;
					getUpdateDate_date = o.getPay_time();
					
					if(getUpdateDate_date.compareTo(modified)>0)
					{
						modified=getUpdateDate_date;
					}
					//判断是否有下一页
					//if (pageno==(Double.valueOf(Math.ceil(totalCount/50.0))).intValue()) break;
					pageno++;
				}
				if(modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				break;
				
			}catch (Exception e)
			{
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				e.printStackTrace();
				Thread.sleep(10000L);
			}
		}
	}
	
	@Override
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}
	
}
