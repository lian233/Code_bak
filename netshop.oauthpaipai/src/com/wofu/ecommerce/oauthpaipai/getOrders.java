package com.wofu.ecommerce.oauthpaipai;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.ecommerce.oauthpaipai.Params;
import com.wofu.oauthpaipai.api.oauth.PaiPaiOpenApiOauth;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class getOrders extends Thread {

	private static String jobname = "获取拍拍订单作业";
	
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	
	private static long daymillis=24*60*60*1000L;
	
	private boolean is_importing=false;
	
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection connection = null;
			is_importing = true;
			try {
				//改变静态时间
				PaiPai.setCurrentDate_getOrder(new Date());
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.oauthpaipai.Params.dbname);				
				getOrderList(connection);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.oauthpaipai.Params.waittime * 1000))		
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
	private static void getOrderList(Connection conn)
			throws Exception
	{
		Log.info("获取订单作业开始");
		Date begintime=new Date(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime()+1000L);
		Date endtime=new Date(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
	       //更新同步订单最新时间
		
		Date lastupdatetime=Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue,""), Formatter.DATE_TIME_FORMAT);
		int pageindex=1;
		int pagetotal=0;
		for (int n=0;n<10;)
		{
				try {		
					while (true)
					{	
						PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(Params.spid, Params.secretkey, Params.token, Long.valueOf(Params.uid));
					
						sdk.setCharset(Params.encoding);
						
						HashMap<String, Object> params = sdk.getParams("/deal/sellerSearchDealList.xhtml");
						// 填充URL请求参数
					
						params.put("sellerUin", ""+Params.uid);
						params.put("timeType", "UPDATE");
						/**
						 * CREATE:表示timeBegin和timeEnd是下单时间 
							PAY:表示timeBegin和timeEnd是付款时间
							UPDATE:表示timeBegin和timeEnd是订单最后更新时间
						 */
						params.put("timeBegin", Formatter.format(begintime, Formatter.DATE_TIME_FORMAT));
						params.put("timeEnd", Formatter.format(endtime, Formatter.DATE_TIME_FORMAT));
						
						params.put("listItem", "1");		
						params.put("pageIndex", String.valueOf(pageindex));
						params.put("pageSize", "20");
								
							
						String result = sdk.invoke();
						//Log.info("result: "+result);		
						
		
						Document doc = DOMHelper.newDocument(result.toString(), Params.encoding);
						Element urlset = doc.getDocumentElement();
						pagetotal=Integer.valueOf(DOMHelper.getSubElementVauleByName(urlset, "pageTotal"));
			
						if (pagetotal>0)
						{
							NodeList dealinfonodes = ((Element) urlset.getElementsByTagName("dealList").item(0)).getElementsByTagName("dealInfo");
					
							for (int i = 0; i < dealinfonodes.getLength(); i++) {
								try{
									boolean is_cod=false;
									Element dealinfoelement = (Element) dealinfonodes.item(i);
								
									String dealcode=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealCode");					
									String dealstatus=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealState");
									Date lastUpdatetimeTemp=Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "lastUpdateTime"),Formatter.DATE_TIME_FORMAT);
									
									//由于拍拍每次都取出全部订单，在此重新判断如果最后更新时间不大于取订单的最新时间，则忽略不处理
									//if (updatetime.compareTo(getLastDateTime(conn))<=0)
									//	continue;
									
									Vector vtsku=OrderUtils.getSaleGoods(dealinfoelement);
							
									Order o=OrderUtils.getDealDetail(Params.spid,Params.secretkey,Params.token,Params.uid,Params.encoding,dealcode);
									/*
									 *1、如果状态为等待卖家发货则生成接口订单
									 *2、删除等待买家付款时的锁定库存 
									 */	
									String sku="";
									long qty=0;
									Hashtable htsku;
								
									Log.info("订单号:"+dealcode+" 订单状态:"+dealstatus+" 最后修改时间:"+Formatter.format(lastUpdatetimeTemp, Formatter.DATE_TIME_FORMAT));
									
									if(dealstatus.equals("DS_WAIT_SELLER_DELIVERY"))
									{
										//检查订单正式表中是否有这个订单中，有的话退出
										if (!OrderManager.isCheck("检查拍拍订单", conn, dealcode))
										{
											if(!OrderManager.TidLastModifyIntfExists("检查拍拍订单", conn, dealcode,lastUpdatetimeTemp)){
												OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username,is_cod);
												for(int j=0;j<vtsku.size();j++)
												{
													htsku=(Hashtable) vtsku.get(j);
													sku=htsku.get("sku").toString();
													StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, dealcode, sku);												
												}
											}
											
											
										
										}
										
									//等待买家付款时记录锁定库存
									} else if (dealstatus.equals("DS_WAIT_BUYER_PAY"))
									{
										for(int j=0;j<vtsku.size();j++)
										{
											htsku=(Hashtable) vtsku.get(j);
											sku=htsku.get("sku").toString();
											qty=Long.valueOf(htsku.get("qty").toString());
											StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, dealcode, sku, qty);
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, dealstatus,dealcode, sku, -qty,false);
																				
										}
										//付款以后用户退款成功，交易自动关闭
										//释放库存,数量为负数
									} else if (dealstatus.equals("DS_REFUND_WAIT_BUYER_DELIVERY")
											||dealstatus.equals("DS_REFUND_WAIT_SELLER_RECEIVE")
											||dealstatus.equals("DS_REFUND_WAIT_SELLER_AGREE")
											||dealstatus.equals("DS_REFUND_OK")||dealstatus.equals("DS_REFUND_ALL_OK"))
									{
										for(int j=0;j<vtsku.size();j++)
										{
											htsku=(Hashtable) vtsku.get(j);
											sku=htsku.get("sku").toString();
											qty=Long.valueOf(htsku.get("qty").toString());				
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, dealstatus,dealcode, sku, qty,true);
										}
										//付款以前，卖家或买家主动关闭交易
										//释放等待买家付款时锁定的库存
									}else if (dealstatus.equals("DS_DEAL_CANCELLED")||dealstatus.equals("DS_CLOSED"))
									{
										for(int j=0;j<vtsku.size();j++)
										{
											htsku=(Hashtable) vtsku.get(j);
											sku=htsku.get("sku").toString();
											qty=Long.valueOf(htsku.get("qty").toString());	
											StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, dealcode, sku);	
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, dealstatus,dealcode, sku, qty,false);
										}
									}else if (dealstatus.equals("DS_DEAL_END_NORMAL"))
									{
										for(int j=0;j<vtsku.size();j++)
										{
											htsku=(Hashtable) vtsku.get(j);
											sku=htsku.get("sku").toString();
											StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, dealcode, sku);										
										}
									}else if (dealstatus.equals("STATE_COD_WAIT_SHIP")){  //货到付款等待发货
										is_cod=true;
										if (!OrderManager.isCheck("检查拍拍订单", conn, dealcode))
										{
											if (!OrderManager.TidLastModifyIntfExists("检查拍拍订单", conn, dealcode,lastUpdatetimeTemp))
											{
												OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username,is_cod);
												if(!OrderManager.TidIntfExists("检查拍拍是否在接口表订单已经有记录", conn, dealcode)){  //每次写入接口才添加同步记录
													for(int j=0;j<vtsku.size();j++)
													{
														htsku=(Hashtable) vtsku.get(j);
														sku=htsku.get("sku").toString();
														StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, dealstatus,dealcode, sku, -qty,false);												
													}
												}
											}
											
										
										}
									}else if (dealstatus.equals("STATE_COD_CANCEL")){  //货到付款取消(关闭OR 拒签后关闭)  删除同步库存记录
										
													for(int j=0;j<vtsku.size();j++)
													{
														htsku=(Hashtable) vtsku.get(j);
														sku=htsku.get("sku").toString();
														StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, dealstatus,dealcode, sku, qty,false);												
													}
									}
											
										
									
								
								
					                if (lastUpdatetimeTemp.compareTo(lastupdatetime)>0)
					                {		                	
					                	PublicUtils.setConfig(conn, lasttimeconfvalue, Formatter.format(lastUpdatetimeTemp,Formatter.DATE_TIME_FORMAT));         
					                }
								}catch(Exception ex){
									if(conn!=null && !conn.getAutoCommit())
										conn.rollback();
									Log.error(jobname, ex.getMessage());
									continue;
								}
													
							}
						}else
						{
							try
							{
								//如该段时间之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{							            	
								
				                	PublicUtils.setConfig(conn,lasttimeconfvalue,Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00");
								}
							}catch(ParseException e)
							{
								throw new JException("不可用的日期格式!"+e.getMessage());
							}
							
							break;					
						}
						
						pageindex=pageindex+1;
						
						
						if(pageindex>pagetotal)
							break;
						
					}
					
					break;	
						
					
				}catch(Exception e)
				{
					if (++n >= 100)
						throw e;
					if(conn!=null && !conn.getAutoCommit())
						conn.rollback();
					Log.warn(jobname+",远程连接失败[" + n + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
					
					Thread.sleep(10000L);
				}	
		}
		Log.info("获取订单作业结束");
		
	}
	
	
}
