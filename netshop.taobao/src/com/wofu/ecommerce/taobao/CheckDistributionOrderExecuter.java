/**
 * 检查淘宝分销订单    api收费
 */
package com.wofu.ecommerce.taobao;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.DealerOrder;
import com.taobao.api.domain.DealerOrderDetail;
import com.taobao.api.domain.PurchaseOrder;
import com.taobao.api.domain.SubPurchaseOrder;
import com.taobao.api.request.FenxiaoDealerRequisitionorderGetRequest;
import com.taobao.api.request.FenxiaoOrdersGetRequest;
import com.taobao.api.response.FenxiaoDealerRequisitionorderGetResponse;
import com.taobao.api.response.FenxiaoOrdersGetResponse;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.business.order.OrderManager;
public class CheckDistributionOrderExecuter extends Executer {
	private String url="";

	private String appkey="";

	private String appsecret="";

	private String authcode="";

	private String tradecontactid="";

	
	private String username="";
		
	private Date nextactive=null;
	
	private static long daymillis=24*60*60*1000L;
	
	private static String jobName="检查淘宝分销订单";

	@Override
	public void run(){
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		appkey=prop.getProperty("appkey");
		appsecret=prop.getProperty("appsecret");
		authcode=prop.getProperty("authcode");
		tradecontactid=prop.getProperty("tradecontactid");

		username=prop.getProperty("username");
		nextactive=this.getExecuteobj().getNextactive();
		

		try {			 

			updateJobFlag(1);
			
			checkWaitSendGoods(); 
			checkClosedByTaobao();	
			
			checkForSupplierDeliver();
			
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
	
	//批量查询采购申请/经销采购单  taobao.fenxiao.dealer.requisitionorder.get 收费 
	private void checkForSupplierDeliver() throws Exception
	{
		
		long pageno=1L;
		
		for (int i=0;i<100;)
		{
			try
			{
				
				TaobaoClient client=new DefaultTaobaoClient(url,appkey,appsecret);
				FenxiaoDealerRequisitionorderGetRequest req=new FenxiaoDealerRequisitionorderGetRequest();		
				req.setOrderStatus(7L);
				Date startdate=new Date(nextactive.getTime()-daymillis);
				Date enddate=nextactive;
				req.setStartDate(startdate);
				req.setEndDate(enddate);
				req.setPageNo(pageno);
				req.setPageSize(40L);
				FenxiaoDealerRequisitionorderGetResponse rsp = client.execute(req , authcode);
							
				while(true)
				{
					if (rsp.getDealerOrders()==null || rsp.getDealerOrders().size()<=0)
					{	
						i=100;
						break;
					}
					for(Iterator it=rsp.getDealerOrders().iterator();it.hasNext();)
					{
						DealerOrder po=(DealerOrder) it.next();
						
				
						Log.info(po.getDealerOrderId()+" "+po.getOrderStatus()+" "+Formatter.format(po.getModifiedTime(),Formatter.DATE_TIME_FORMAT));
						
						for(Iterator ito=po.getDealerOrderDetails().iterator();ito.hasNext();)
						{
							DealerOrderDetail o=(DealerOrderDetail) ito.next();
							
							StockManager.deleteWaitPayStock("检查分销订单", this.getDao().getConnection(),tradecontactid, String.valueOf(po.getDealerOrderId()), o.getSkuNumber());
														
						}
						
						if (!OrderManager.isCheck("检查分销订单", this.getDao().getConnection(), String.valueOf(po.getDealerOrderId())))
						{
							if (!OrderManager.TidLastModifyIntfExists("检查分销订单", this.getDao().getConnection(), String.valueOf(po.getDealerOrderId()),po.getModifiedTime()))
							{
								try
								{
															
									OrderUtils.createDealerOrder("检查分销订单",this.getDao().getConnection(),po,tradecontactid);
																		
									
								} catch(SQLException sqle)
								{
									throw new JException("生成接口订单出错!" + sqle.getMessage());
								}
							}
						}			
					}
					pageno++;
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
	

	private void checkClosedBySupplier() throws Exception
	{
		
		long pageno=1L;
		
		for (int i=0;i<10;)
		{
			try
			{
				TaobaoClient client=new DefaultTaobaoClient(url,appkey,appsecret);
				FenxiaoDealerRequisitionorderGetRequest req=new FenxiaoDealerRequisitionorderGetRequest();		
				req.setOrderStatus(10L);
				Date startdate=new Date(nextactive.getTime()-daymillis);
				Date enddate=nextactive;
				req.setStartDate(startdate);
				req.setEndDate(enddate);
				req.setPageNo(pageno);
				req.setPageSize(40L);
				FenxiaoDealerRequisitionorderGetResponse rsp = client.execute(req , authcode);
							
				while(true)
				{
					if (rsp.getDealerOrders()==null || rsp.getDealerOrders().size()<=0)
					{	
						i=100;
						break;
					}
					for(Iterator it=rsp.getDealerOrders().iterator();it.hasNext();)
					{
						DealerOrder po=(DealerOrder) it.next();
						
				
						Log.info(po.getDealerOrderId()+" "+po.getOrderStatus()+" "+Formatter.format(po.getModifiedTime(),Formatter.DATE_TIME_FORMAT));
						
						for(Iterator ito=po.getDealerOrderDetails().iterator();ito.hasNext();)
						{
							DealerOrderDetail o=(DealerOrderDetail) ito.next();
							
							StockManager.deleteWaitPayStock("检查分销订单", this.getDao().getConnection(),tradecontactid, String.valueOf(po.getDealerOrderId()), o.getSkuNumber());
														
													
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
	 * 查询分销采购单信息  taobao.fenxiao.orders.get 收费 
	 * @throws Exception
	 */
	private void checkWaitSendGoods() throws Exception
	{
		
		long pageno=1L;
		
		for (int i=0;i<100;)
		{
			try
			{
				
				TaobaoClient client=new DefaultTaobaoClient(url,appkey,appsecret);
				FenxiaoOrdersGetRequest req=new FenxiaoOrdersGetRequest();
				req.setStatus("WAIT_SELLER_SEND_GOODS");
				Date startdate=new Date(nextactive.getTime()-daymillis);
				Date enddate=nextactive;
				req.setStartCreated(startdate);
				req.setEndCreated(enddate);
				req.setPageNo(pageno);
				req.setPageSize(40L);
				FenxiaoOrdersGetResponse rsp = client.execute(req , authcode);
							
				while(true)
				{
					if (rsp.getPurchaseOrders()==null || rsp.getPurchaseOrders().size()<=0)
					{	
						i=100;
						break;
					}
					for(Iterator it=rsp.getPurchaseOrders().iterator();it.hasNext();)
					{
						PurchaseOrder po=(PurchaseOrder) it.next();
						
				
						Log.info(po.getId()+" "+po.getStatus()+" "+Formatter.format(po.getModified(),Formatter.DATE_TIME_FORMAT));
						
						for(Iterator ito=po.getSubPurchaseOrders().iterator();ito.hasNext();)
						{
							SubPurchaseOrder o=(SubPurchaseOrder) ito.next();
							
							StockManager.deleteWaitPayStock("检查分销订单", this.getDao().getConnection(),tradecontactid, String.valueOf(po.getId()), o.getSkuOuterId());
														
						}
						
						if (!OrderManager.isCheck("检查分销订单", this.getDao().getConnection(), String.valueOf(po.getId())))
						{
							if (!OrderManager.TidLastModifyIntfExists("检查分销订单", this.getDao().getConnection(), String.valueOf(po.getId()),po.getModified()))
							{
								try
								{
															
									OrderUtils.createDistributionOrder("检查分销订单",this.getDao().getConnection(),po,tradecontactid);
																		
									
								} catch(SQLException sqle)
								{
									throw new JException("生成接口订单出错!" + sqle.getMessage());
								}
							}
						}			
					}
					pageno++;
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
	/**
	 * 上面一样的api   收费
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
				FenxiaoOrdersGetRequest req=new FenxiaoOrdersGetRequest();
				req.setStatus("TRADE_CLOSED");
				Date startdate=new Date(nextactive.getTime()-daymillis);
				Date enddate=nextactive;
				req.setStartCreated(startdate);
				req.setEndCreated(enddate);
				req.setPageNo(pageno);
				req.setPageSize(40L);
				FenxiaoOrdersGetResponse rsp = client.execute(req , authcode);
				
				while(true)
				{
					if (rsp.getPurchaseOrders()==null || rsp.getPurchaseOrders().size()<=0)
					{	
						i=100;
						break;
					}
					
					for(Iterator it=rsp.getPurchaseOrders().iterator();it.hasNext();)
					{
						PurchaseOrder po=(PurchaseOrder) it.next();
						
						
						Log.info(po.getId()+" "+po.getStatus()+" "+Formatter.format(po.getModified(),Formatter.DATE_TIME_FORMAT));
						
						for(Iterator ito=po.getSubPurchaseOrders().iterator();ito.hasNext();)
						{
							SubPurchaseOrder o=(SubPurchaseOrder) ito.next();
							
							StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, String.valueOf(po.getId()), o.getSkuOuterId());
														
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
	
		
}
