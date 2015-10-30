package com.wofu.ecommerce.jit;


import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.jit.utils.Utils;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.order.OrderManager;

public class CheckOrderExecuter extends Executer {

	private String url="";

	private String token = "";

	private String app_key  = "";
	
	private String format="";
	private String app_secret="";
	
	private String ver="";

	private String tradecontactid="";

	private String username="";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String jobName="检查一号店订单";

	public void run()  {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		format=prop.getProperty("format");
		ver=prop.getProperty("ver");
		tradecontactid=prop.getProperty("tradecontactid");

		token=prop.getProperty("token");
		app_key=prop.getProperty("app_key");
		username=prop.getProperty("username");
		app_secret=prop.getProperty("app_secret");

		try {		
			
			updateJobFlag(1);
	
			getOrderList();
			
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

	
	/*
	 * 获取一天之类的所有订单
	 */
	private void getOrderList() throws Exception
	{		
		long pageno=1L;
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //系统级参数设置
					orderlistparams.put("appKey", app_key);
					orderlistparams.put("sessionKey", token);
					orderlistparams.put("format", format);
					orderlistparams.put("method", "yhd.orders.get");
					orderlistparams.put("ver", ver);
					orderlistparams.put("dateType", "5");
					orderlistparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			        
			    	Date startdate=new Date((new Date()).getTime()-daymillis);
					Date enddate=new Date();
			       
					orderlistparams.put("orderStatusList", "ORDER_WAIT_PAY,ORDER_PAYED,"
			        		+"ORDER_WAIT_SEND,ORDER_ON_SENDING,ORDER_RECEIVED,ORDER_FINISH,ORDER_GRT,ORDER_CANCEL");
					orderlistparams.put("dateType", "5");
					orderlistparams.put("curPage", String.valueOf(pageno));
					orderlistparams.put("pageRows", "50");
					orderlistparams.put("startTime", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
					orderlistparams.put("endTime", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
					
			        String responseOrderListData = Utils.sendByPost(orderlistparams, app_secret, url,"");
					
					JSONObject responseproduct=new JSONObject(responseOrderListData);
					
					if (responseOrderListData.indexOf("errInfoList")>=0)
					{
						JSONArray errinfolist=responseproduct.getJSONObject("response").optJSONObject("errInfoList").optJSONArray("errDetailInfo");
						String errdesc="";
						
						for(int j=0;j<errinfolist.length();j++)
						{
							JSONObject errinfo=errinfolist.getJSONObject(j);
							
							errdesc=errdesc+" "+errinfo.getString("errorDes"); 
												
						}
						
						Log.error(username, "取订单列表失败:"+errdesc);
						k=10;
						break;
					}
					
					int totalCount=responseproduct.getJSONObject("response").getInt("totalCount");
					int errorCount=responseproduct.getJSONObject("response").getInt("errorCount");
					
					if (errorCount>0)
					{
						String errdesc="";
						JSONArray errlist=responseproduct.getJSONObject("response").getJSONObject("errInfoList").getJSONArray("errDetailInfo");
						for(int j=0;j<errlist.length();j++)
						{
							JSONObject errinfo=errlist.getJSONObject(j);
							
							errdesc=errdesc+" "+errinfo.getString("errorDes"); 
												
						}
						
						if (errdesc.indexOf("订单列表信息不存在")<0)
						{
							k=10;
							throw new JException(errdesc);
						}
					}
					
										
					
					int i=1;
			
			
								
					if (totalCount==0)
					{									
						k=10;
						break;
					}
					
					
					JSONArray orderlist=responseproduct.getJSONObject("response").getJSONObject("orderList").getJSONArray("order");
					
					
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						
						Map<String, String> orderparams = new HashMap<String, String>();
				        //系统级参数设置
						orderparams.put("appKey", app_key);
						orderparams.put("sessionKey", token);
						orderparams.put("format", format);
						orderparams.put("method", "yhd.order.detail.get");
						orderparams.put("ver", ver);
						orderparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				    
						orderparams.put("orderCode", order.getString("orderCode"));
				     				        
						String responseOrderData = Utils.sendByPost(orderparams, app_secret, url,"");
						

						JSONObject responseorder=new JSONObject(responseOrderData);
						
						int errorOrderCount=responseorder.getJSONObject("response").getInt("errorCount");
						
						if (errorOrderCount>0)
						{
							String errdesc="";
							JSONArray errlist=responseproduct.getJSONObject("response").getJSONObject("errInfoList").getJSONArray("errDetailInfo");
							for(int n=0;n<errlist.length();n++)
							{
								JSONObject errinfo=errlist.getJSONObject(n);
								
								errdesc=errdesc+" "+errinfo.getString("errorDes"); 
													
							}
							
							k=10;
							throw new JException(errdesc);						
						}
						
						
						JSONObject orderdetail=responseorder.getJSONObject("response").getJSONObject("orderInfo").getJSONObject("orderDetail");
						
						
						Order o=new Order();
						o.setObjValue(o, orderdetail);
										
						
						JSONArray orderItemList=responseorder.getJSONObject("response").getJSONObject("orderInfo").getJSONObject("orderItemList").getJSONArray("orderItem");
						
						o.setFieldValue(o, "orderItemList", orderItemList);
						
				
						Log.info(o.getOrderCode()+" "+o.getOrderStatus()+" "+Formatter.format(o.getUpdateTime(),Formatter.DATE_TIME_FORMAT));
						/*
						 *1、如果状态为等待卖家发货则生成接口订单
						 *2、删除等待买家付款时的锁定库存 
						 */		
						String sku;
						String sql="";
						if (o.getOrderStatus().equals("ORDER_PAYED") 
								|| o.getOrderStatus().equals("ORDER_TRUNED_TO_DO")
								|| o.getOrderStatus().equals("ORDER_CAN_OUT_OF_WH"))
						{	
							
							if (!OrderManager.isCheck("检查一号店订单", this.getDao().getConnection(), o.getOrderCode()))
							{
								if (!OrderManager.TidLastModifyIntfExists("检查一号店订单", this.getDao().getConnection(), o.getOrderCode(),o.getUpdateTime()))
								{
									//OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username);
									
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getOuterId();
										
										StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrderCode(),sku);
										StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, o.getOrderStatus(),o.getOrderCode(), sku, -item.getOrderItemNum(),false);
									}
								}
							}
	
							//等待买家付款时记录锁定库存
						}
						
						
						else if (o.getOrderStatus().equals("ORDER_WAIT_PAY"))
						{						
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuterId();
							
								StockManager.addWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrderCode(), sku, item.getOrderItemNum());
								StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, o.getOrderStatus(),o.getOrderCode(), sku, -item.getOrderItemNum(),false);
							}
							
							 
				  
							//付款以后用户退款成功，交易自动关闭
							//释放库存,数量为负数						
						}else if (o.getOrderStatus().equals("ORDER_CANCEL"))
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuterId();
					
								StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrderCode(), sku);
								if (StockManager.WaitPayStockExists(jobName,this.getDao().getConnection(),tradecontactid, o.getOrderCode(), sku))//有获取到等待买家付款状态时才加库存
									StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, o.getOrderStatus(),o.getOrderCode(), sku, item.getOrderItemNum(),false);
							}
							
							
				
						}
						else if (o.getOrderStatus().equals("ORDER_FINISH"))
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuterId();
					
								StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrderCode(), sku);								
							}
			
						}
						else if (o.getOrderStatus().equals("ORDER_CUSTOM_CALLTO_RETUR")
							||o.getOrderStatus().equals("ORDER_CUSTOM_CALLTO_CHANGE")
							||o.getOrderStatus().equals("ORDER_RETURNED")
							||o.getOrderStatus().equals("ORDER_CHANGE_FINISHED"))
						{
							
							OrderUtils.getRefund(this.getDao().getConnection(),tradecontactid,o);
								
				
						}
						
					
					}
						
						
						
					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(totalCount/50.0))).intValue()) break;
					
					pageno++;
					
					i=i+1;
				}
				
			
				//执行成功后不再循环
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn(jobName+", 远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	

}
