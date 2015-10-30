package com.wofu.ecommerce.oauthpaipai;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;

import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;

import com.wofu.common.tools.util.log.Log;
import com.wofu.oauthpaipai.api.oauth.PaiPaiOpenApiOauth;
import com.wofu.base.job.Executer;


public class CheckOrderExecuter extends Executer {
	
	private String spid="";
	
	private String secretkey="";
	
	private String uid="";
	
	private String token="";
	
	private String tradecontactid="";
	
	private String encoding="";
	
	private String username="";

	private static long daymillis=24*60*60*1000L;
	
	private static String jobName="定时检查拍拍订单";
	

	public void run(){
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		
		spid=prop.getProperty("spid");
		secretkey=prop.getProperty("secretkey");
		uid=prop.getProperty("uid");
		token=prop.getProperty("token");
		tradecontactid=prop.getProperty("tradecontactid");
		encoding=prop.getProperty("encoding");

		username=prop.getProperty("username");


		try 
		{			 
			updateJobFlag(1);			

	
			checkWaitSellerDelivery();
			checkEndTrade();
			checkCancelTrade();
			checkRefundTrade();
			//货到付款等待发货
			checkcod_wait_ship();
			//货到付款取消(关闭OR 拒签后关闭)
			checkcod_cod_state_cancle();
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
				updateJobFlag(0);
				
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
	

	
	private void checkWaitSellerDelivery() throws Exception
	{
		
		int pageindex=1;
		int pagetotal=0;
		for (int n=0;n<5;)
		{
			try {				
	
					while (true)
					{
						PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(spid, secretkey, token, Long.valueOf(uid));
						
						sdk.setCharset(encoding);
						
						HashMap<String, Object> params = sdk.getParams("/deal/sellerSearchDealList.xhtml");
	
						params.put("orderDesc", "0");	
						params.put("sellerUin", uid);
						params.put("listItem", "1");		
						params.put("pageIndex", "1");
						params.put("pageSize", "20");
						params.put("dealState", "DS_WAIT_SELLER_DELIVERY");
						String result = sdk.invoke();;	
						Document doc = DOMHelper.newDocument(result.toString(),encoding);
						Element urlset = doc.getDocumentElement();
						String errorcode = DOMHelper.getSubElementVauleByName(urlset, "errorCode");
						String errormessage = DOMHelper.getSubElementVauleByName(urlset, "errorMessage");
						
					
						if(errorcode.equals("0"))
						{	
							pagetotal=Integer.valueOf(DOMHelper.getSubElementVauleByName(urlset, "pageTotal"));
							
							if(pageindex>pagetotal)
								break;
							
							NodeList dealinfonodes = ((Element) urlset.getElementsByTagName("dealList").item(0)).getElementsByTagName("dealInfo");
							if (dealinfonodes.getLength()>0)
							{
								for (int i = 0; i < dealinfonodes.getLength(); i++) {
									try{
										boolean is_cod=false;
										Element dealinfoelement = (Element) dealinfonodes.item(i);
									
										String dealcode=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealCode");					
										String dealstatus=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealState");
										Date lastUpdatetime=Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "lastUpdateTime"),Formatter.DATE_TIME_FORMAT);
					
								
										Vector vtsku=OrderUtils.getSaleGoods(dealinfoelement);
										
								
										Order o=OrderUtils.getDealDetail(spid,secretkey,token,uid,encoding,dealcode);
									
										/*
										 *1、如果状态为等待卖家发货则生成接口订单
										 *2、删除等待买家付款时的锁定库存 
										 */	
				
										String sku="";
										long qty=0;
										Hashtable htsku;
									
										Log.info("订单号:"+dealcode+" 订单状态:"+dealstatus+" 时间:"+Formatter.format(lastUpdatetime, Formatter.DATE_TIME_FORMAT));
										
										if(dealstatus.equals("DS_WAIT_SELLER_DELIVERY"))
										{
											
											if (!OrderManager.isCheck("检查拍拍订单", this.getDao().getConnection(), dealcode))
											{
												if (!OrderManager.TidLastModifyIntfExists("检查拍拍订单", this.getDao().getConnection(), dealcode,lastUpdatetime))
												{
																						
													try
													{
														OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username,is_cod);
														for(int j=0;j<vtsku.size();j++)
														{
															htsku=(Hashtable) vtsku.get(j);
															sku=htsku.get("sku").toString();
															StockManager.deleteWaitPayStock("检查拍拍订单", this.getDao().getConnection(),tradecontactid, dealcode, sku);												
														}
													} catch(SQLException sqle)
													{
														throw new JException("生成接口订单出错!" + sqle.getMessage());
													}
												}
											}
										}
									}catch(Exception ex){
										if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit())
											this.getDao().getConnection().rollback();
										Log.error(jobName, ex.getMessage());
									}
				
								
								
								
								}
							}
						}
						else
						{
							Log.error(username,"取订单失败:"+errormessage);
							break;
						}
						
						pageindex=pageindex+1;
					}
					
				break;
			}
			catch(Exception e)
			{
				if (++n >= 5)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				Log.warn(jobName+" ,远程连接失败[" + n + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				
				Thread.sleep(10000L);
			}		
		}
	}

	
	private void checkEndTrade() throws Exception
	{
		
		int pageindex=1;
		int pagetotal=0;
		for (int n=0;n<5;)
		{
			try {				
	
					while (true)
					{
						PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(spid, secretkey, token, Long.valueOf(uid));
						
						sdk.setCharset(encoding);
						
						HashMap<String, Object> params = sdk.getParams("/deal/sellerSearchDealList.xhtml");
		
						params.put("timeType", "PAY");
						params.put("orderDesc", "0");	
						params.put("sellerUin", uid);
						params.put("listItem", "1");		
						params.put("pageIndex", "1");
						params.put("pageSize", "20");
						params.put("dealState", "DS_DEAL_END_NORMAL");
						params.put("timeBegin", Formatter.format(new Date((new Date()).getTime()-24*60*60*1000),Formatter.DATE_TIME_FORMAT));
						params.put("timeEnd", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
						String result = sdk.invoke();
		
						Document doc = DOMHelper.newDocument(result.toString(),encoding);
						Element urlset = doc.getDocumentElement();
						String errorcode = DOMHelper.getSubElementVauleByName(urlset, "errorCode");
						String errormessage = DOMHelper.getSubElementVauleByName(urlset, "errorMessage");
					
						if(errorcode.equals("0"))
						{	
							pagetotal=Integer.valueOf(DOMHelper.getSubElementVauleByName(urlset, "pageTotal"));
							
							if(pageindex>pagetotal)
								break;
							
							NodeList dealinfonodes = ((Element) urlset.getElementsByTagName("dealList").item(0)).getElementsByTagName("dealInfo");
							if (dealinfonodes.getLength()>0)
							{
								for (int i = 0; i < dealinfonodes.getLength(); i++) {
				
									try{
										Element dealinfoelement = (Element) dealinfonodes.item(i);
										
										String dealcode=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealCode");					
										String dealstatus=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealState");
										Date updatetime=Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "payTime"),Formatter.DATE_TIME_FORMAT);
					
							
										
										Vector vtsku=OrderUtils.getSaleGoods(dealinfoelement);
										
								
										Order o=OrderUtils.getDealDetail(spid,secretkey,token,uid,encoding,dealcode);
									
										/*
										 *1、如果状态为等待卖家发货则生成接口订单
										 *2、删除等待买家付款时的锁定库存 
										 */	
				
										String sku="";
										long qty=0;
										Hashtable htsku;
									
										Log.info("订单号:"+dealcode+" 订单状态:"+dealstatus+" 时间:"+Formatter.format(updatetime, Formatter.DATE_TIME_FORMAT));
										
										if (dealstatus.equals("DS_DEAL_END_NORMAL"))
										{
											for(int j=0;j<vtsku.size();j++)
											{
												htsku=(Hashtable) vtsku.get(j);
												sku=htsku.get("sku").toString();
												StockManager.deleteWaitPayStock("检查拍拍订单", this.getDao().getConnection(),tradecontactid, dealcode, sku);										
											}
										}
									}catch(Exception ex){
										if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit())
											this.getDao().getConnection().rollback();
										Log.error(jobName, ex.getMessage());
									}
									
								}
							}
						}
						else
						{
							Log.error(username,"取订单失败:"+errormessage);
							break;
						}
						
						pageindex=pageindex+1;
					}
				break;
			}
			catch(Exception e)
			{
				if (++n >= 5)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				Log.warn(jobName+" ,远程连接失败[" + n + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				
				Thread.sleep(10000L);
			}		
		}
	}

	
	private void checkCancelTrade() throws Exception
	{
		
		int pageindex=1;
		int pagetotal=0;
		for (int n=0;n<5;)
		{
			try {				
	
					while (true)
					{
						PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(spid, secretkey, token, Long.valueOf(uid));
						
						sdk.setCharset(encoding);
						
						HashMap<String, Object> params = sdk.getParams("/deal/sellerSearchDealList.xhtml");
		
						params.put("timeType", "PAY");
						params.put("orderDesc", "0");	
						params.put("sellerUin", uid);
						params.put("listItem", "1");		
						params.put("pageIndex", "1");
						params.put("pageSize", "20");
						params.put("dealState", "DS_DEAL_CANCELLED");
						params.put("timeBegin", Formatter.format(new Date((new Date()).getTime()-24*60*60*1000),Formatter.DATE_TIME_FORMAT));
						params.put("timeEnd", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
						
						String result = sdk.invoke();;	
		
			
						Document doc = DOMHelper.newDocument(result.toString(),encoding);
						Element urlset = doc.getDocumentElement();
						String errorcode = DOMHelper.getSubElementVauleByName(urlset, "errorCode");
						String errormessage = DOMHelper.getSubElementVauleByName(urlset, "errorMessage");
						
					
						if(errorcode.equals("0"))
						{	
							pagetotal=Integer.valueOf(DOMHelper.getSubElementVauleByName(urlset, "pageTotal"));
							
							if(pageindex>pagetotal)
								break;
							
							NodeList dealinfonodes = ((Element) urlset.getElementsByTagName("dealList").item(0)).getElementsByTagName("dealInfo");
							if (dealinfonodes.getLength()>0)
							{
								for (int i = 0; i < dealinfonodes.getLength(); i++) {
				
									try{
										Element dealinfoelement = (Element) dealinfonodes.item(i);
										
										String dealcode=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealCode");					
										String dealstatus=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealState");
										Date updatetime=Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "payTime"),Formatter.DATE_TIME_FORMAT);
					
						
										Vector vtsku=OrderUtils.getSaleGoods(dealinfoelement);
										
								
										Order o=OrderUtils.getDealDetail(spid,secretkey,token,uid,encoding,dealcode);

										String sku="";
										long qty=0;
										Hashtable htsku;
									
										Log.info("订单号:"+dealcode+" 订单状态:"+dealstatus+" 时间:"+Formatter.format(updatetime, Formatter.DATE_TIME_FORMAT));
										
										if (dealstatus.equals("DS_DEAL_CANCELLED")||dealstatus.equals("DS_CLOSED"))
										{
											for(int j=0;j<vtsku.size();j++)
											{
												htsku=(Hashtable) vtsku.get(j);
												sku=htsku.get("sku").toString();
												qty=Long.valueOf(htsku.get("qty").toString());	
												StockManager.deleteWaitPayStock("检查拍拍订单", this.getDao().getConnection(),tradecontactid, dealcode, sku);	
												StockManager.addSynReduceStore("检查拍拍订单", this.getDao().getConnection(), tradecontactid, dealstatus,dealcode, sku, qty,false);
											}
										}
									}catch(Exception ex){
										if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit()){
											this.getDao().getConnection().rollback();
										}
										Log.error(jobName, ex.getMessage());
									}
								}
							}
						}
						else
						{
							Log.error(username,"取订单失败:"+errormessage);
							break;
						}
						
						pageindex=pageindex+1;
					}
				break;
			}
			catch(Exception e)
			{
				if (++n >= 100)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				Log.warn(jobName+" ,远程连接失败[" + n + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				
				Thread.sleep(10000L);
			}		
		}
	}
	

	private void checkRefundTrade() throws Exception
	{
		
		int pageindex=1;
		int pagetotal=0;
		for (int n=0;n<5;)
		{
			try {				
	
					while (true)
					{
						PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(spid, secretkey, token, Long.valueOf(uid));
						
						sdk.setCharset(encoding);
						
						HashMap<String, Object> params = sdk.getParams("/deal/sellerSearchDealList.xhtml");
		
						params.put("timeType", "PAY");
						params.put("orderDesc", "0");	
						params.put("sellerUin", uid);
						params.put("listItem", "1");		
						params.put("pageIndex", "1");
						params.put("pageSize", "20");
						params.put("dealState", " DS_DEAL_REFUNDING");
						params.put("timeBegin", Formatter.format(new Date((new Date()).getTime()-24*60*60*1000),Formatter.DATE_TIME_FORMAT));
						params.put("timeEnd", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
						
						String result = sdk.invoke();;	
		
			
						Document doc = DOMHelper.newDocument(result.toString(),encoding);
						Element urlset = doc.getDocumentElement();
						String errorcode = DOMHelper.getSubElementVauleByName(urlset, "errorCode");
						String errormessage = DOMHelper.getSubElementVauleByName(urlset, "errorMessage");
						
					
						if(errorcode.equals("0"))
						{	
							pagetotal=Integer.valueOf(DOMHelper.getSubElementVauleByName(urlset, "pageTotal"));
							
							if(pageindex>pagetotal)
								break;
							
							NodeList dealinfonodes = ((Element) urlset.getElementsByTagName("dealList").item(0)).getElementsByTagName("dealInfo");
							if (dealinfonodes.getLength()>0)
							{
								for (int i = 0; i < dealinfonodes.getLength(); i++) {
				
									try{
										Element dealinfoelement = (Element) dealinfonodes.item(i);
										
										String dealcode=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealCode");					
										String dealstatus=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealState");
										Date updatetime=Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "payTime"),Formatter.DATE_TIME_FORMAT);
					
						
										Vector vtsku=OrderUtils.getSaleGoods(dealinfoelement);
										
								
										Order o=OrderUtils.getDealDetail(spid,secretkey,token,uid,encoding,dealcode);
									
										/*
										 *1、如果状态为等待卖家发货则生成接口订单
										 *2、删除等待买家付款时的锁定库存 
										 */	
				
										String sku="";
										long qty=0;
										Hashtable htsku;
									
										Log.info("订单号:"+dealcode+" 订单状态:"+dealstatus+" 时间:"+Formatter.format(updatetime, Formatter.DATE_TIME_FORMAT));
										
										if (dealstatus.equals("DS_REFUND_WAIT_BUYER_DELIVERY")
												||dealstatus.equals("DS_REFUND_WAIT_SELLER_RECEIVE")
												||dealstatus.equals("DS_REFUND_WAIT_SELLER_AGREE")
												||dealstatus.equals("DS_REFUND_OK")||dealstatus.equals("DS_REFUND_ALL_OK"))
										{
											for(int j=0;j<vtsku.size();j++)
											{
												htsku=(Hashtable) vtsku.get(j);
												sku=htsku.get("sku").toString();
												qty=Long.valueOf(htsku.get("qty").toString());				
												StockManager.addSynReduceStore("检查拍拍订单", this.getDao().getConnection(), tradecontactid, dealstatus,dealcode, sku, qty,true);
											}
											
										}
									}catch(Exception ex){
										if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit())
											this.getDao().getConnection().rollback();
										Log.error(jobName,ex.getMessage());
									}
									
									
								
								}
							}
						}
						else
						{
							Log.error(username,"取订单失败:"+errormessage);
							break;
						}
						
						pageindex=pageindex+1;
					}
				break;
			}
			catch(Exception e)
			{
				if (++n >= 100)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				Log.warn(jobName+" ,远程连接失败[" + n + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				
				Thread.sleep(10000L);
			}		
		}
	}
	
	//   定时检查货到付款等待发货订单
	private void checkcod_wait_ship() throws Exception
	{
		
		int pageindex=1;
		int pagetotal=0;
		for (int n=0;n<5;)
		{
			try {				
	
					while (true)
					{
						PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(spid, secretkey, token, Long.valueOf(uid));
						
						sdk.setCharset(encoding);
						
						HashMap<String, Object> params = sdk.getParams("/deal/sellerSearchDealList.xhtml");
	
						params.put("orderDesc", "0");	
						params.put("sellerUin", uid);
						params.put("listItem", "1");		
						params.put("pageIndex", "1");
						params.put("pageSize", "20");
						params.put("dealState", "STATE_COD_WAIT_SHIP");
						String result = sdk.invoke();;	
			
						Document doc = DOMHelper.newDocument(result.toString(),encoding);
						Element urlset = doc.getDocumentElement();
						String errorcode = DOMHelper.getSubElementVauleByName(urlset, "errorCode");
						String errormessage = DOMHelper.getSubElementVauleByName(urlset, "errorMessage");
						
					
						if(errorcode.equals("0"))
						{	
							pagetotal=Integer.valueOf(DOMHelper.getSubElementVauleByName(urlset, "pageTotal"));
							
							if(pageindex>pagetotal)
								break;
							
							NodeList dealinfonodes = ((Element) urlset.getElementsByTagName("dealList").item(0)).getElementsByTagName("dealInfo");
							if (dealinfonodes.getLength()>0)
							{
								for (int i = 0; i < dealinfonodes.getLength(); i++) {
									try{
										boolean is_cod=false;
										Element dealinfoelement = (Element) dealinfonodes.item(i);
									
										String dealcode=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealCode");					
										String dealstatus=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealState");
										Date lastUpdatetime=Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "lastUpdateTime"),Formatter.DATE_TIME_FORMAT);
					
								
										Vector vtsku=OrderUtils.getSaleGoods(dealinfoelement);
										
								
										Order o=OrderUtils.getDealDetail(spid,secretkey,token,uid,encoding,dealcode);
									
										/*
										 *1、如果状态为等待卖家发货则生成接口订单
										 *2、删除等待买家付款时的锁定库存 
										 */	
				
										String sku="";
										long qty=0;
										Hashtable htsku;
									
										Log.info("订单号:"+dealcode+" 订单状态:"+dealstatus+" 时间:"+Formatter.format(lastUpdatetime, Formatter.DATE_TIME_FORMAT));
										
										if(dealstatus.equals("STATE_COD_WAIT_SHIP"))
										{
											
											is_cod=true;
											if (!OrderManager.isCheck("检查拍拍订单", this.getDao().getConnection(), dealcode))
											{
												if (!OrderManager.TidLastModifyIntfExists("检查拍拍订单", this.getDao().getConnection(), dealcode,lastUpdatetime))
												{
													try{
														OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username,is_cod);
														if(!OrderManager.TidIntfExists("检查拍拍是否在接口表订单已经有记录", this.getDao().getConnection(), dealcode)){  //每次写入接口才添加同步记录
															for(int j=0;j<vtsku.size();j++)
															{
																htsku=(Hashtable) vtsku.get(j);
																sku=htsku.get("sku").toString();
																StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, dealstatus,dealcode, sku, -qty,false);												
															}
														}
													}catch(Exception ex){
														throw new JException("生成接口订单出错!" + ex.getMessage());
													}
													
												}
												
											
											}
										}
									
									}catch(Exception ex){
										if (this.getConnection() != null && !this.getConnection().getAutoCommit())
											this.getConnection().rollback();
										
										if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
											this.getExtconnection().rollback();
										Log.error(jobName,ex.getMessage());
										
									}
				
									
								
								}
							}
						}
						else
						{
							Log.error(username,"取订单失败:"+errormessage);
							break;
						}
						
						pageindex=pageindex+1;
					}
					
				break;
			}
			catch(Exception e)
			{
				if (++n >= 5)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				Log.warn(jobName+" ,远程连接失败[" + n + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				
				Thread.sleep(10000L);
			}		
		}
	}
	
	
	//定时检查货到付款取消(关闭OR 拒签后关闭)
	private void checkcod_cod_state_cancle() throws Exception
	{
		
		int pageindex=1;
		int pagetotal=0;
		for (int n=0;n<5;)
		{
			try {				
	
					while (true)
					{
						PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(spid, secretkey, token, Long.valueOf(uid));
						
						sdk.setCharset(encoding);
						
						HashMap<String, Object> params = sdk.getParams("/deal/sellerSearchDealList.xhtml");
	
						params.put("orderDesc", "0");	
						params.put("sellerUin", uid);
						params.put("listItem", "1");		
						params.put("pageIndex", "1");
						params.put("pageSize", "20");
						params.put("dealState", "STATE_COD_CANCEL");
						String result = sdk.invoke();;	
			
						Document doc = DOMHelper.newDocument(result.toString(),encoding);
						Element urlset = doc.getDocumentElement();
						String errorcode = DOMHelper.getSubElementVauleByName(urlset, "errorCode");
						String errormessage = DOMHelper.getSubElementVauleByName(urlset, "errorMessage");
						
					
						if(errorcode.equals("0"))
						{	
							pagetotal=Integer.valueOf(DOMHelper.getSubElementVauleByName(urlset, "pageTotal"));
							
							if(pageindex>pagetotal)
								break;
							
							NodeList dealinfonodes = ((Element) urlset.getElementsByTagName("dealList").item(0)).getElementsByTagName("dealInfo");
							if (dealinfonodes.getLength()>0)
							{
								for (int i = 0; i < dealinfonodes.getLength(); i++) {
									try{
										boolean is_cod=false;
										Element dealinfoelement = (Element) dealinfonodes.item(i);
									
										String dealcode=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealCode");					
										String dealstatus=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealState");
										Date lastUpdatetime=Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "lastUpdateTime"),Formatter.DATE_TIME_FORMAT);
					
								
										Vector vtsku=OrderUtils.getSaleGoods(dealinfoelement);
										
								
										Order o=OrderUtils.getDealDetail(spid,secretkey,token,uid,encoding,dealcode);
									
										String sku="";
										long qty=0;
										Hashtable htsku;
									
										Log.info("订单号:"+dealcode+" 订单状态:"+dealstatus+" 时间:"+Formatter.format(lastUpdatetime, Formatter.DATE_TIME_FORMAT));
										
										if(dealstatus.equals("STATE_COD_CANCEL"))
										{
											
											if (!OrderManager.isCheck("检查拍拍订单", this.getDao().getConnection(), dealcode))
											{
												if (!OrderManager.TidLastModifyIntfExists("检查拍拍订单", this.getDao().getConnection(), dealcode,lastUpdatetime))
												{
													OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username,is_cod);
													if(!OrderManager.TidIntfExists("检查拍拍是否在接口表订单已经有记录", this.getDao().getConnection(), dealcode)){  //每次写入接口才添加同步记录
														for(int j=0;j<vtsku.size();j++)
														{
															htsku=(Hashtable) vtsku.get(j);
															sku=htsku.get("sku").toString();
															StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, dealstatus,dealcode, sku, qty,false);												
														}
													}
												}
												
											
											}
										}
									}catch(Exception ex){
										if (this.getConnection() != null && !this.getConnection().getAutoCommit())
											this.getConnection().rollback();
										Log.error(jobName,ex.getMessage());
									}
				
								}
							}
						}
						else
						{
							Log.error(username,"取订单失败:"+errormessage);
							break;
						}
						
						pageindex=pageindex+1;
					}
					
				break;
			}
			catch(Exception e)
			{
				if (++n >= 5)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				Log.warn(jobName+",远程连接失败[" + n + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				
				Thread.sleep(10000L);
			}		
		}
	}
}
