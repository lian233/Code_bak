package com.wofu.ecommerce.taobao;
/**
 * api收费
 */
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;
import com.taobao.api.request.TradesSoldGetRequest;
import com.taobao.api.response.TradesSoldGetResponse;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.business.order.OrderManager;
public class CheckOrderExecuter extends Executer {
	private String url="";

	private String appkey="";

	private String appsecret="";

	private String authcode="";

	private String tradecontactid="";

	private String username="";
	
	private String tid="";
	
	private Date nextactive=null;
	
	private static String TradeFields="seller_nick,buyer_nick,title,type,created,tid,"
		+"seller_rate,buyer_flag,buyer_rate,status,payment,"
		+"adjust_fee,post_fee,total_fee,pay_time,end_time,modified,"
		+"consign_time,buyer_obtain_point_fee,point_fee,real_point_fee,"
		+"received_payment,commission_fee,buyer_memo,seller_memo,"
		+"alipay_no,buyer_message,pic_path,num_iid,num,price,buyer_alipay_no,"
		+"receiver_name,receiver_state,receiver_city,receiver_district,"
		+"receiver_address,receiver_zip,receiver_mobile,receiver_phone,"
		+"buyer_email,seller_flag,seller_alipay_no,seller_mobile,trade_from,"
		+"seller_phone,seller_name,seller_email,available_confirm_fee,alipay_url,"
		+"has_post_fee,timeout_action_time,Snapshot,snapshot_url,cod_fee,cod_status,"
		+"shipping_type,trade_memo,is_3D,buyer_email,buyer_memo,buyer_flag,promotion,promotion_details,orders";
	
	private boolean waitbuyerpayisin=false;
	
	private static String jobName="检查淘宝订单";
	

	@Override
	public void run() {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		appkey=prop.getProperty("appkey");
		appsecret=prop.getProperty("appsecret");
		authcode=prop.getProperty("authcode");
		tradecontactid=prop.getProperty("tradecontactid");
		username=prop.getProperty("username");
		tid=prop.getProperty("tid");
		//lasttimeconfvalue=username+"取订单最新时间";
		//lastchecktimeconfvalue=username+"检查订单时间";
		

		try {		
			
			updateJobFlag(1);
			/**
			String sql="select isnull(value,0) from config where name='等待付款订单是否进系统'";
			if (this.getDao().strSelect(sql).equals("1"))
				waitbuyerpayisin=true;
				**/
			
			checkWaitSendGoods(); 

			//checkWaitBuyerPay();
			//checkClosedByTaobao();		

			
			UpdateTimerJob();
			
			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
	
		} catch (Exception e) {
			try {
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"回滚事务失败");
				Log.error(jobName, e1.getMessage());
			}
			
			try{
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
			}catch(Exception ex){
				Log.error(jobName,"更新任务信息失败");
				Log.error(jobName, ex.getMessage());
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
				Log.error(jobName, e.getMessage());
				Log.error(jobName,"更新处理标志失败");
				TimerRunner.modifiedErrVect(this.getExecuteobj().getId());
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
	/**
	 * 获取等待发货的订单数据   taobao.trades.sold.get 收费api
	 *                  taobao.trade.fullinfo.get
	 * @throws Exception
	 */
	private void checkWaitSendGoods() throws Exception
	{
		Log.info("start----");
		long pageno=1L;
		
		for (int i=0;i<100;)
		{
			try
			{
				/**
				TaobaoClient client=new DefaultTaobaoClient(url,appkey,appsecret);
				TradesSoldGetRequest req=new TradesSoldGetRequest();
				req.setFields(TradeFields);
				req.setStatus("WAIT_SELLER_SEND_GOODS");
				req.set
				//req.setStartCreated(Formatter.parseDate("2013-09-23 10:30:00",Formatter.DATE_TIME_FORMAT));
				//req.setStartCreated(lastchecktime);
				//req.setEndCreated(Formatter.parseDate("2013-09-23 10:35:00",Formatter.DATE_TIME_FORMAT));
				req.setPageNo(pageno);
				req.setPageSize(20L);
				TradesSoldGetResponse rsp = client.execute(req , authcode);
				**/
				if(tid.indexOf(",")>0){
					Log.info("more");
					String[] tids= tid.split(",");
					for(int j=0;j<tids.length;j++)
					{
							Trade td=null;
					
							td=OrderUtils.getFullTrade(tids[j],url,appkey,appsecret,authcode);
							
							Log.info(td.getTid()+" "+td.getStatus()+" "+Formatter.format(td.getCreated(),Formatter.DATE_TIME_FORMAT));
							
							for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
							{
								Order o=(Order) ito.next();
								StockManager.deleteWaitPayStock("检查淘宝订单", this.getDao().getConnection(),tradecontactid, String.valueOf(td.getTid()), o.getOuterSkuId());
								
								if (o.getRefundId()>0)
								{
									if (!OrderManager.RefundisCheck("检查淘宝订单", this.getDao().getConnection(), String.valueOf(td.getTid()), o.getOuterSkuId()))
									{
										OrderUtils.getRefund("检查淘宝订单",this.getDao().getConnection(),url,appkey,
											appsecret,authcode,tradecontactid,td,o,
											 String.valueOf(td.getTid()),o.getRefundId());
									}
								}
								
							}
							
							if (!OrderManager.isCheck("检查淘宝订单", this.getDao().getConnection(), String.valueOf(td.getTid())))
							{
								if (!OrderManager.TidLastModifyIntfExists("检查淘宝订单", this.getDao().getConnection(), String.valueOf(td.getTid()),td.getModified()))
								{
									try
									{
																
										OrderUtils.createInterOrder(this.getDao().getConnection(),td,tradecontactid,username,true);
										
										for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
										{
											Order o=(Order) ito.next();
											StockManager.deleteWaitPayStock("检查淘宝订单", this.getDao().getConnection(),tradecontactid, String.valueOf(td.getTid()), o.getOuterSkuId());										
																	
										}
										
									} catch(SQLException sqle)
									{
										throw new JException("生成接口订单出错!" + sqle.getMessage());
									}
								}
							}
					}
				}else{
					Trade td=null;
					
					td=OrderUtils.getFullTrade(tid,url,appkey,appsecret,authcode);
				
					
					Log.info(td.getTid()+" "+td.getStatus()+" "+Formatter.format(td.getCreated(),Formatter.DATE_TIME_FORMAT));
					
					for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
					{
						Order o=(Order) ito.next();
						StockManager.deleteWaitPayStock("检查淘宝订单", this.getDao().getConnection(),tradecontactid, String.valueOf(td.getTid()), o.getOuterSkuId());
						
						if (o.getRefundId()>0)
						{
							if (!OrderManager.RefundisCheck("检查淘宝订单", this.getDao().getConnection(), String.valueOf(td.getTid()), o.getOuterSkuId()))
							{
								OrderUtils.getRefund("检查淘宝订单",this.getDao().getConnection(),url,appkey,
									appsecret,authcode,tradecontactid,td,o,
									 String.valueOf(td.getTid()),o.getRefundId());
							}
						}
						
					}
					
					if (!OrderManager.isCheck("检查淘宝订单", this.getDao().getConnection(), String.valueOf(td.getTid())))
					{
						if (!OrderManager.TidLastModifyIntfExists("检查淘宝订单", this.getDao().getConnection(), String.valueOf(td.getTid()),td.getModified()))
						{
							try
							{
														
								OrderUtils.createInterOrder(this.getDao().getConnection(),td,tradecontactid,username,true);
								
								for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
								{
									Order o=(Order) ito.next();
									StockManager.deleteWaitPayStock("检查淘宝订单", this.getDao().getConnection(),tradecontactid, String.valueOf(td.getTid()), o.getOuterSkuId());										
															
								}
								
							} catch(SQLException sqle)
							{
								throw new JException("生成接口订单出错!" + sqle.getMessage());
							}
						}
					}
				}
				i=100;
			}catch(Exception e)
			{
				if (++i >= 100)
					throw e;
				Log.warn("远程连接失败[" + i + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
	}
	/**
	 * 跟上面一样的api   收费
	 * @throws Exception
	 */
	private void checkClosedByTaobao() throws Exception
	{
		
		long pageno=1L;
		
		for (int i=0;i<10;)
		{
			try
			{
				TaobaoClient client=new DefaultTaobaoClient(url,appkey,appsecret);
				TradesSoldGetRequest req=new TradesSoldGetRequest();
				req.setFields(TradeFields);
				req.setStatus("TRADE_CLOSED_BY_TAOBAO");
				//req.setStatus("TRADE_CLOSED");		
				req.setStartCreated(Formatter.parseDate(Formatter.format(new Date((new Date()).getTime()-1*24*60*60*1000),Formatter.DATE_TIME_FORMAT),Formatter.DATE_TIME_FORMAT));
				req.setEndCreated(new Date());
				req.setPageNo(pageno);
				req.setPageSize(20L);
				TradesSoldGetResponse rsp = client.execute(req , authcode);
				
				while(true)
				{
					if (rsp.getTrades()==null || rsp.getTrades().size()<=0)
					{	
						i=10;
						break;
					}
					for(Iterator it=rsp.getTrades().iterator();it.hasNext();)
					{
						Trade td=(Trade) it.next();
						
						td=OrderUtils.getFullTrade(String.valueOf(td.getTid()),url,appkey,appsecret,authcode);
						
						Log.info(td.getTid()+" "+td.getStatus()+" "+Formatter.format(td.getCreated(),Formatter.DATE_TIME_FORMAT));
						
						if (waitbuyerpayisin)
						{
							if (!OrderManager.TidLastModifyIntfExists("检查淘宝订单", this.getDao().getConnection(), String.valueOf(td.getTid()),td.getModified()))
							{
								OrderUtils.createInterOrder(this.getDao().getConnection(),td,tradecontactid,username,false);
								
							}
						}
						
						for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
						{
							Order o=(Order) ito.next();
							String sku=o.getOuterSkuId();
				
							StockManager.deleteWaitPayStock("检查淘宝未入订单", this.getDao().getConnection(),tradecontactid, String.valueOf(td.getTid()), sku);
							if (StockManager.WaitPayStockExists("检查淘宝未入订单",this.getDao().getConnection(),tradecontactid, String.valueOf(td.getTid()), sku))  //有获取到等待买家付款状态时才加库存
								StockManager.addSynReduceStore("检查淘宝未入订单", this.getDao().getConnection(), tradecontactid, td.getStatus(),String.valueOf(td.getTid()), sku, o.getNum(),false);
						}
					}
					pageno=pageno+1;
					req.setPageNo(pageno);
					rsp=client.execute(req , authcode);
				}
			}catch(Exception e)
			{
				if (++i >= 10)
					throw e;
				Log.warn("远程连接失败[" + i + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
	}
	/**
	 * 跟上面一样的api  收费
	 * @throws Exception
	 */
	private void checkWaitBuyerPay() throws Exception
	{
		
		long pageno=1L;
				
		for (int i=0;i<100;)
		{
			try
			{
				TaobaoClient client=new DefaultTaobaoClient(url,appkey,appsecret);
				TradesSoldGetRequest req=new TradesSoldGetRequest();
				req.setFields(TradeFields);
				req.setStatus("WAIT_BUYER_PAY");	
				req.setPageNo(pageno);
				req.setPageSize(100L);
				TradesSoldGetResponse rsp = client.execute(req , authcode);
				
				while(true)
				{
					if (rsp.getTrades()==null || rsp.getTrades().size()<=0)
					{	
						i=100;
						break;
					}
					
					for(Iterator it=rsp.getTrades().iterator();it.hasNext();)
					{
						Trade td=(Trade) it.next();
						//taobao.trade.fullinfo.get 收费
						td=OrderUtils.getFullTrade(String.valueOf(td.getTid()),url,appkey,appsecret,authcode);
						
						if (!td.getStatus().equals("WAIT_BUYER_PAY")) continue;
						
					     						
						Log.info(td.getTid()+" "+td.getStatus()+" "+Formatter.format(td.getCreated(),Formatter.DATE_TIME_FORMAT));
						
						
						if (waitbuyerpayisin)
						{
							if (!OrderManager.TidLastModifyIntfExists("检查淘宝订单", this.getDao().getConnection(), String.valueOf(td.getTid()),td.getModified()))
							{
								OrderUtils.createInterOrder(this.getDao().getConnection(),td,tradecontactid,username,false);
								
							}
						}
						
						for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
						{
							Order o=(Order) ito.next();
							String sku=o.getOuterSkuId();
							
							if (!StockManager.WaitPayStockExists("检查未入未付款订单",this.getDao().getConnection(),tradecontactid,String.valueOf(td.getTid()),sku))
							{
								StockManager.addWaitPayStock("检查未入未付款订单", this.getDao().getConnection(),tradecontactid, String.valueOf(td.getTid()), sku, o.getNum());
								StockManager.addSynReduceStore("检查未入未付款订单", this.getDao().getConnection(), tradecontactid, td.getStatus(),String.valueOf(td.getTid()), sku, -o.getNum(),false);
							}
						}
							
					}
					
					pageno=pageno+1;
					req.setPageNo(pageno);
					rsp=client.execute(req , authcode);
				}
				i=100;
			}catch(Exception e)
			{
				if (++i >= 100)
					throw e;
				Log.warn("远程连接失败[" + i + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
	}
	
		
}
