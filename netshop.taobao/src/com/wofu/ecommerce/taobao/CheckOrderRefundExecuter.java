/**
 * api收费
 */
package com.wofu.ecommerce.taobao;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Refund;
import com.taobao.api.domain.Trade;
import com.taobao.api.request.RefundsReceiveGetRequest;
import com.taobao.api.response.RefundsReceiveGetResponse;
import com.wofu.business.order.OrderManager;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
public class CheckOrderRefundExecuter extends Executer {
	private String url="";

	private String appkey="";

	private String appsecret="";

	private String authcode="";

	private String tradecontactid="";

	private String dbname="";
	
	private String username="";

	
	private static String RefundFields="refund_id,tid";
	
	private static String jobName="检查退货单";
	

	@Override
	public void run() {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		appkey=prop.getProperty("appkey");
		appsecret=prop.getProperty("appsecret");
		authcode=prop.getProperty("authcode");
		tradecontactid=prop.getProperty("tradecontactid");
		dbname=prop.getProperty("dbname");
		username=prop.getProperty("username");
	
		try {			 
			updateJobFlag(1);
			
			checkWaitSellerAgree();
			
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
	 * 查询卖家收到的退款列表  taobao.refunds.receive.get  收费
	 * taobao.refund.get
	 * taobao.trade.fullinfo.get
	 * 
	 * @throws Exception
	 */
	private void checkWaitSellerAgree() throws Exception
	{
		
		long pageno=1L;
		
		for (int i=0;i<10;)
		{
			try
			{
				
				TaobaoClient client=new DefaultTaobaoClient(url,appkey,appsecret);
				RefundsReceiveGetRequest req=new RefundsReceiveGetRequest();
				req.setFields(RefundFields);
				//req.setStatus("WAIT_SELLER_AGREE");		
				//req.setStatus("WAIT_BUYER_RETURN_GOODS");
				req.setStartModified(Formatter.parseDate(Formatter.format(new Date((new Date()).getTime()-1*24*60*60*1000),Formatter.DATE_TIME_FORMAT),Formatter.DATE_TIME_FORMAT));
				req.setEndModified(new Date());
				req.setPageNo(pageno);
				req.setPageSize(20L);
				RefundsReceiveGetResponse rsp = client.execute(req , authcode);
					
				while(true)
				{
					if (rsp.getRefunds()==null || rsp.getRefunds().size()<=0)
					{	
						i=10;
						break;
					}
					for(Iterator it=rsp.getRefunds().iterator();it.hasNext();)
					{
						Refund refund=(Refund) it.next();
						
						long tid=refund.getTid();
						// taobao.trade.fullinfo.get 收费 
						Trade td=OrderUtils.getFullTrade(String.valueOf(tid),url,appkey,appsecret,authcode);
						
						Log.info(td.getTid()+" "+td.getStatus()+" "+Formatter.format(td.getCreated(),Formatter.DATE_TIME_FORMAT));
						
						//处理退货
						for(Iterator oit=td.getOrders().iterator();oit.hasNext();)
						{						
							Order o=(Order) oit.next();					
							
							if ((o.getRefundId())>0 && (!OrderManager.RefundIntfExists(jobName,this.getDao().getConnection(),String.valueOf(td.getTid()),String.valueOf(o.getRefundId()))))
							{
								//taobao.refund.get 收费 
								OrderUtils.getRefund(jobName,this.getDao().getConnection(),url,appkey,
										appsecret,authcode,tradecontactid,td,o,
										 String.valueOf(td.getTid()),o.getRefundId());
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
				if (++i >= 10)
					throw e;
				Log.warn("远程连接失败[" + i + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
	}
	

			
}
