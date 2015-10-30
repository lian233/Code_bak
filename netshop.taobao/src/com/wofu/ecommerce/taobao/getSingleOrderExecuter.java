package com.wofu.ecommerce.taobao;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Properties;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;
import com.taobao.api.request.TradeGetRequest;
import com.taobao.api.response.TradeGetResponse;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.business.stock.StockManager;
import com.wofu.business.order.OrderManager;
public class getSingleOrderExecuter extends Executer {

	private static String jobName = "获取单个淘宝订单作业";
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private String url="";
	private String appkey="";
	private String appsecret="";
	private String authcode="";
	private String tid;
	private String tradecontactid;
	private String sellernick;
	private boolean is_importing = false;
	private boolean waitbuyerpayisin=false;

	public void run() {
		Properties proper = StringUtil.getStringProperties(this.getExecuteobj().getParams());
		tid = proper.getProperty("tid");
		tradecontactid = proper.getProperty("tradecontactid");
		url = proper.getProperty("url");
		appkey = proper.getProperty("appkey");
		appsecret = proper.getProperty("appsecret");
		authcode = proper.getProperty("authcode");
		sellernick = proper.getProperty("sellernick");
			try {												
				updateJobFlag(1);
				String sql="select isnull(value,0) from config where name='等待付款订单是否进系统'";
				if ("1".equals(this.getDao().strSelect(sql)));
					waitbuyerpayisin=true;
				
				TaobaoClient client=new DefaultTaobaoClient(url,appkey, appsecret,"xml");
				TradeGetRequest req=new TradeGetRequest();
				req.setFields("tid,modified,status,orders.outer_sku_id,orders.num");	
				req.setTid(Long.parseLong(tid));
				TradeGetResponse response = client.execute(req , authcode);
									
				if (response.getTrade()==null )
				{				
					return;
				}
						
				Trade td=response.getTrade();
							
				if (td.getStatus().equals("WAIT_SELLER_SEND_GOODS"))
				{	
					
					if (!OrderManager.isCheck("检查淘宝订单", this.getDao().getConnection(), String.valueOf(td.getTid())))
					{
						if (!OrderManager.TidLastModifyIntfExists("检查淘宝订单", this.getDao().getConnection(), String.valueOf(td.getTid()),td.getModified()))
						{
							OrderUtils.createInterOrder(this.getDao().getConnection(),td,Params.tradecontactid,Params.username,true);
							for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
							{
								Order o=(Order) ito.next();
								String sku=o.getOuterSkuId();
							
								StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),Params.tradecontactid, String.valueOf(td.getTid()),sku);
								StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), Params.tradecontactid, td.getStatus(),String.valueOf(td.getTid()), sku, -o.getNum(),false);
							}
						}
					}

					//等待买家付款时记录锁定库存
				}
				
				
				else if (td.getStatus().equals("WAIT_BUYER_PAY") || td.getStatus().equals("TRADE_NO_CREATE_PAY"))
				{						
					if (waitbuyerpayisin)
					{	
						if (!OrderManager.TidLastModifyIntfExists("检查淘宝订单", this.getDao().getConnection(), String.valueOf(td.getTid()),td.getModified()))
						{
							OrderUtils.createInterOrder(this.getDao().getConnection(),td,Params.tradecontactid,Params.username,false);
							
						}
					}
					for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
					{
						Order o=(Order) ito.next();
						String sku=o.getOuterSkuId();
						StockManager.addWaitPayStock(jobName, this.getDao().getConnection(),Params.tradecontactid, String.valueOf(td.getTid()), sku, o.getNum());
						StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), Params.tradecontactid, td.getStatus(),String.valueOf(td.getTid()), sku, -o.getNum(),false);
					}
					
	
		  
					//付款以后用户退款成功，交易自动关闭
					//释放库存,数量为负数
				} else if (td.getStatus().equals("TRADE_CLOSED"))
				{			
					OrderManager.CancelOrderByCID(jobName, this.getDao().getConnection(), String.valueOf(td.getTid()));
					for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
					{
						Order o=(Order) ito.next();		
						String sku=o.getOuterSkuId();
						StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),Params.tradecontactid, String.valueOf(td.getTid()), sku);
						//StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), Params.tradecontactid, td.getStatus(),String.valueOf(td.getTid()),sku, o.getNum(),false);
					}

					//付款以前，卖家或买家主动关闭交易
					//释放等待买家付款时锁定的库存
				}else if (td.getStatus().equals("TRADE_CLOSED_BY_TAOBAO"))
				{
					
					if (waitbuyerpayisin)
					{
						
						if (!OrderManager.TidLastModifyIntfExists("检查淘宝订单", this.getDao().getConnection(), String.valueOf(td.getTid()),td.getModified()))
						{
							OrderUtils.createInterOrder(this.getDao().getConnection(),td,Params.tradecontactid,Params.username,false);
							
						}
					}

		
					for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
					{
						Order o=(Order) ito.next();
						String sku=o.getOuterSkuId();
					
						 
						StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),Params.tradecontactid, String.valueOf(td.getTid()), sku);
						
						
						if (StockManager.WaitPayStockExists(jobName,this.getDao().getConnection(),Params.tradecontactid, String.valueOf(td.getTid()), sku))//有获取到等待买家付款状态时才加库存
						{
						
						
							StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), Params.tradecontactid, td.getStatus(),String.valueOf(td.getTid()), sku, o.getNum(),false);
						}
					}
			
					
		
				}
				else if (td.getStatus().equals("TRADE_FINISHED"))
				{
					
					for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
					{
						Order o=(Order) ito.next();
						String sku=o.getOuterSkuId();
			
						StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),Params.tradecontactid, String.valueOf(td.getTid()), sku);	
						
						//更新结束时间
						
						OrderUtils.updateFinishedStatus(this.getDao().getConnection(),Params.tradecontactid,td.getTid(),td.getEndTime());
					}
	
				}
				
				//if(Params.isc)
				//{
					//处理退货
					for(Iterator oit=td.getOrders().iterator();oit.hasNext();)
					{						
						Order o=(Order) oit.next();					
						//Log.info("订单号:"+String.valueOf(td.getTid())+" 退货ID:"+String.valueOf(o.getRefundId()));
						if (o.getRefundId()>0)
						{
							OrderUtils.getRefund(jobName,this.getDao().getConnection(),Params.url,Params.appkey,
									Params.appsecret,Params.authcode,Params.tradecontactid,td,o,
									 String.valueOf(td.getTid()),o.getRefundId());
						}
		
					}
							
				updateJobFlag(0);
				Log.info(jobName, "执行作业成功 ["
						+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
						+ "] 下次处理时间: "
						+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
				
			} catch (Exception e) {
				try {
					
					if (this.getDao().getConnection() != null && !this.getDao().getConnection() .getAutoCommit()){
						this.getDao().getConnection().rollback();
						this.getDao().getConnection().setAutoCommit(true);
					}
					
					
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
					if (this.getDao().getConnection() != null){
						this.getDao().getConnection() .setAutoCommit(true);
						this.getDao().getConnection() .close();
					}
						
					
				} catch (Exception e) {
					Log.error(jobName,"关闭数据库连接失败");
				}
			}
			
	}
	
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
