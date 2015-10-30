package com.wofu.ecommerce.icbc;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.icbc.util.CommHelper;
public class GetOrders extends Thread {

	private static String jobname = "获取工行商城订单作业";
	
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	
	private static long daymillis=24*60*60*1000L;
	
	private String lasttime;
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	public void run() {

		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;

			try {
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	

				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
				/**
				 * 订单状态 10待发货，20已发货，21部分发货，30交易成功 ，40交易关闭
				 */
				//获取工行商城新订单 
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
		} while (true);
	}
	
	
	//获取工行商城新订单
	public void getOrderList(Connection conn) throws Exception
	{
		
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
					int n=1;
				
					String startdate=Formatter.format(new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L),Formatter.DATE_TIME_FORMAT);
					String enddate=Formatter.format(new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis),Formatter.DATE_TIME_FORMAT);
					//方法名
					String apimethod="icbcb2c.order.list";
					HashMap<String,Object> map = new HashMap<String,Object>();
					map.put("modify_time_from",startdate);
			        map.put("modify_time_to",enddate);
			        map.put("method", apimethod);
			        map.put("req_sid", CommHelper.getReq_sid());
			        map.put("version", Params.OUT_API_VERSION);
			        map.put("format", Params.OUT_API_FORMAT);
			        map.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			        map.put("app_key", Params.OUT_APP_KEY);
			        map.put("auth_code", Params.OUT_AUTH_CODE);
			        StringBuilder sb = new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			        sb.append("<body><modify_time_from>").append(startdate)
			        .append("</modify_time_from><modify_time_to>")
			        .append(enddate)
			        .append("</modify_time_to></body>");
			        map.put("sign", CommHelper.getSign("HMACSHA256",Params.OUT_APP_KEY,Params.OUT_AUTH_CODE,Params.OUT_APP_SECRET,sb.toString()));
			        map.put("req_data", sb.toString());
			        
			        
			        //发送请求
					String responseText = CommHelper.doPost(map,Params.url);
					
					Log.info("返回数据为: "+responseText);
					//把返回的数据转成Document对象
					Document document = DOMHelper.newDocument(responseText, "utf-8");
					Element rootElement = document.getDocumentElement();
					Element head = (Element)rootElement.getElementsByTagName("head").item(0);
					String result = DOMHelper.getSubElementVauleByName(head, "ret_code");
					Log.info("result:　"+result);
					
					
					if(!"0".equals(result)) {
						String errmsg = DOMHelper.getSubElementVauleByName(head, "ret_msg");
						Log.info("errmsg:　"+errmsg);
						Log.error(jobname,errmsg);
						if("请求访问频率过快".equals(errmsg)){
							long now = System.currentTimeMillis();
							while(System.currentTimeMillis()-now<(long)Params.waittime*1000L){
								Thread.sleep(1000L);
							}
						}
						return;
					}
					Log.info("test");
					NodeList order_list = rootElement.getElementsByTagName("order_list");
					if(order_list==null){
						if (n==1)		
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis*1000L)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
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
						break;
					}
					NodeList lists = rootElement.getElementsByTagName("order");
					
					for(int i = 0 ; i< lists.getLength() ; i++)
					{	//某个订单
						Element orderInfo = (Element)lists.item(i);
						//订单编号 
						String orderCode = DOMHelper.getSubElementVauleByName(orderInfo, "order_id");
						
						//取订单详情
						Order o =OrderUtils.getOrderById(orderCode,Params.url,Params.OUT_API_VERSION,
								Params.OUT_APP_KEY,Params.OUT_APP_SECRET,Params.OUT_AUTH_CODE,Params.OUT_API_FORMAT);
						if(!orderCode.equals(o.getOrder_id())) continue;
						Date createTime = Formatter.parseDate(o.getOrder_modify_time(), Formatter.DATE_TIME_FORMAT);
						if(o != null)
						{	
							  //正常订单
								//如果是等待发货订单，创建接口订单成功，减少其它店的库存
								if("2".equals(o.getPay_status()) && "0".equals(o.getShipping_status()))
								{
									if (!OrderManager.isCheck(jobname, conn, orderCode))
									{
										if (!OrderManager.TidLastModifyIntfExists(jobname, conn, orderCode,createTime))
										{
											try
											{
												OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
												for(Iterator ito=o.getGoods_list().getRelationData().iterator();ito.hasNext();)
												{
													OrderItem item=(OrderItem) ito.next();
													String sku = item.getGoods_sn() ;
													//没有等待付款的状态 不需要删除未付款锁定的库存/
													StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
													long qty= Integer.parseInt(item.getGoods_number());
													//在ecs_rationconfig表中存在机构添加一条库存同步记录(不包括自己）
													StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getShipping_status(),o.getOrder_sn(), sku, qty,false);
												}
												
											} catch(SQLException sqle)
											{
												throw new JException("生成接口订单出错!" + sqle.getMessage());
											}
										}
									}     // 订单状态 10待发货，20已发货，21部分发货，30交易成功 ，40交易关闭
								}

								//40交易关闭
								else if("2".equals(o.getOrder_status()))
								{
									Log.info("订单号: "+o.getOrder_sn()+", 交易取消！");
									for(Iterator ito=o.getGoods_list().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getGoods_sn() ;
										long qty= Integer.parseInt(item.getGoods_number());
										//删除已锁定库存，增加其它店的库存
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
									}
									
									//取消订单
									String sql="declare @ret int;  execute  @ret = IF_CancelCustomerOrder '" + orderCode + "';select @ret ret;";
									int resultCode = SQLHelper.intSelect(conn, sql) ;
									//取消订单失败
									if(resultCode == 2)			
										Log.info("工行商城请求取消订单失败,单号:"+orderCode+"");						
									else
										Log.info("工行商城请求取消订单成功,单号:"+orderCode+"");
									
								}
								else if ("1".equals(o.getOrder_status()))  //交易成功
								{
									for(Iterator ito=o.getGoods_list().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getGoods_sn() ;
							
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode, sku);								
									}
					
								}else if ("0".equals(o.getPay_status()))  //未付款
								{
								 
									for(Iterator ito=o.getGoods_list().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getGoods_sn() ;
										long qty= Integer.parseInt(item.getGoods_number());
							
										StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(orderCode), sku, qty);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, "未付款",String.valueOf(orderCode), sku, -qty,false);								
									}
					
								}
							}
							
							//如果当前订单时间大于开始取订单时间，则更新下次取订单时间(现在取订单列表最后修改时间)
							//更新同步订单最新时间
							
			                if (createTime.compareTo(modified)>0)
			                {
			                	modified=createTime;
			                }
						}
					
					n++;
						
					
					
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobname,je.getMessage());
	            	}
				}
				
				break;
			} catch (Exception e) 
			{
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(jobname+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				long now = System.currentTimeMillis();
				while(System.currentTimeMillis()-now<(long)Params.waittime*1000L){
					Thread.sleep(1000L);
				}
			}
		}
		Log.info("本次取工行商城订单任务处理完毕!");
	}

	
	
	
}