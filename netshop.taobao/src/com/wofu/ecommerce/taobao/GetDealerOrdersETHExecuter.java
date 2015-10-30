//定时获取淘宝经销订单作业
package com.wofu.ecommerce.taobao;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.DealerOrder;
import com.taobao.api.domain.DealerOrderDetail;
import com.taobao.api.request.FenxiaoDealerRequisitionorderGetRequest;
import com.taobao.api.response.FenxiaoDealerRequisitionorderGetResponse;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;

public class GetDealerOrdersETHExecuter extends Executer {

	private static String jobName = "每三小时获取淘宝经销订单作业";
	
	private static long daymillis=5*60*60*1000L;
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	private String username="";
	private String lasttime;
	private String url="";
	private String appkey="";
	private String appsecret="";
	private String authcode="";
	private String tradecontactid="";

	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		username=prop.getProperty("username");
		url=prop.getProperty("url");
		appkey=prop.getProperty("appkey");
		appsecret=prop.getProperty("appsecret");
		authcode=prop.getProperty("authcode");
		tradecontactid=prop.getProperty("tradecontactid");
			try{
				updateJobFlag(1);
				Connection conn=this.getDao().getConnection();
				getPurchaseOrderList(conn);
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
					if (this.getConnection() != null){
						this.getConnection().setAutoCommit(true);
						this.getConnection().close();
					}
						
					if (this.getExtconnection() != null){
						this.getExtconnection().setAutoCommit(true);
						this.getExtconnection().close();
					}
					
				} catch (Exception e) {
					Log.error(jobName,"关闭数据库连接失败");
				}
			}
			
	}

	
	/*
	 * 获取一天之类的所有订单
	 * taobao.fenxiao.dealer.requisitionorder.get  收费api
	 */
	private void getPurchaseOrderList(Connection conn) throws Exception
	{	
		Log.info(username+"-"+jobName+"开始!");
		long pageno=1L;
		for(int k=0;k<3;)
		{
			try
			{
				TaobaoClient client=new DefaultTaobaoClient(url,appkey, appsecret,"xml");
				FenxiaoDealerRequisitionorderGetRequest req=new FenxiaoDealerRequisitionorderGetRequest();		
				Date startdate=new Date(new Date().getTime()-daymillis);
				Date enddate=new Date();
				req.setStartDate(startdate);
				req.setEndDate(enddate);
				req.setPageNo(pageno);
				req.setPageSize(40L);
				req.setIdentity(1L);
				FenxiaoDealerRequisitionorderGetResponse response = client.execute(req ,authcode);
				int i=1;
			
				while(true)
				{
								
					if (response.getDealerOrders()==null || response.getDealerOrders().size()<=0)
					{				
						break;
					}
					
					for(Iterator it=response.getDealerOrders().iterator();it.hasNext();)
					{	
						try{
							DealerOrder po=(DealerOrder) it.next();
							
							
							Log.info(po.getDealerOrderId()+" "+po.getOrderStatus()+" "+Formatter.format(po.getModifiedTime(),Formatter.DATE_TIME_FORMAT));
								
							String sku;
							String sql="";
							
							//付款成功，待供应商发货
							if (po.getOrderStatus().equals("WAIT_FOR_SUPPLIER_DELIVER"))
							{	
							
								if (!OrderManager.isCheck("检查淘宝订单", conn, String.valueOf(po.getDealerOrderId())))
								{
									if (!OrderManager.TidLastModifyIntfExists("检查淘宝订单", conn, String.valueOf(po.getDealerOrderId()),po.getModifiedTime()))
									{
									
										OrderUtils.createDealerOrder(jobName,conn,po,tradecontactid);
										
										for(Iterator ito=po.getDealerOrderDetails().iterator();ito.hasNext();)
										{
											DealerOrderDetail o=(DealerOrderDetail) ito.next();
											sku=o.getSkuNumber();
										
											StockManager.deleteWaitPayStock(jobName, conn,tradecontactid, String.valueOf(po.getDealerOrderId()),sku);		
											StockManager.addSynReduceStore(jobName, conn, tradecontactid, po.getOrderStatus(),String.valueOf(po.getDealerOrderId()), sku, -o.getQuantity(),false);
										}
									}
								}
								
								//等待买家付款时记录锁定库存
							}
							
							else if (po.getOrderStatus().equals("BOTH_AGREE_WAIT_PAY"))
							{						
								for(Iterator ito=po.getDealerOrderDetails().iterator();ito.hasNext();)
								{
									DealerOrderDetail o=(DealerOrderDetail) ito.next();
									sku=o.getSkuNumber();
								
									StockManager.addWaitPayStock(jobName, conn,tradecontactid, String.valueOf(po.getDealerOrderId()), sku, o.getQuantity());
									StockManager.addSynReduceStore(jobName, conn, tradecontactid, po.getOrderStatus(),String.valueOf(po.getDealerOrderId()), sku, -o.getQuantity(),false);
								}
													
								//付款以后用户退款成功，交易自动关闭
								//释放库存,数量为负数
							} else if (po.getOrderStatus().equals("TRADE_CLOSED"))
							{					
								OrderManager.CancelOrderByCID(jobName, conn, String.valueOf(po.getDealerOrderId()));
								for(Iterator ito=po.getDealerOrderDetails().iterator();ito.hasNext();)
								{
									DealerOrderDetail o=(DealerOrderDetail) ito.next();		
									sku=o.getSkuNumber();
									StockManager.deleteWaitPayStock(jobName, conn,tradecontactid, String.valueOf(po.getDealerOrderId()), sku);
									StockManager.addSynReduceStore(jobName, conn, tradecontactid, po.getOrderStatus(),String.valueOf(po.getDealerOrderId()),sku, o.getQuantity(),false);
								}
								//交易成功
								//释放等待买家付款时锁定的库存
							}else if (po.getOrderStatus().equals("TRADE_FINISHED"))
							{
								for(Iterator ito=po.getDealerOrderDetails().iterator();ito.hasNext();)
								{
									DealerOrderDetail o=(DealerOrderDetail) ito.next();
									sku=o.getSkuNumber();
						
									StockManager.deleteWaitPayStock(jobName, conn,tradecontactid, String.valueOf(po.getDealerOrderId()), sku);								
								}
							}
							
						}catch(Exception ex){
							if (this.getConnection() != null && !this.getConnection().getAutoCommit())
								this.getConnection().rollback();
							
							if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
								this.getExtconnection().rollback();
							Log.error(jobName, ex.getMessage());
						}
						
					}
				
					if (pageno==(Double.valueOf(Math.ceil(response.getTotalResults()/40.0))).intValue()) break;
					
					pageno++;
					req.setPageNo(pageno);
					response=client.execute(req , authcode);
					i=i+1;
				}
				break;
			}catch (Exception e) {
				if (++k >= 3)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				Log.warn(jobName," ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
