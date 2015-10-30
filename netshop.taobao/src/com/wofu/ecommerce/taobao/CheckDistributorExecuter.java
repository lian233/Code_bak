package com.wofu.ecommerce.taobao;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Cooperation;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;
import com.taobao.api.request.FenxiaoCooperationGetRequest;
import com.taobao.api.request.TradesSoldGetRequest;
import com.taobao.api.response.FenxiaoCooperationGetResponse;
import com.taobao.api.response.TradesSoldGetResponse;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.business.order.OrderManager;
import com.wofu.business.util.PublicUtils;

public class CheckDistributorExecuter extends Executer {
	private String url="";

	private String appkey="";

	private String appsecret="";

	private String authcode="";

	
	private String tradecontactid="";
	
	private String username="";
		
	private Date nextactive=null;
	
	private static String jobName="检查分销商";
	

	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		appkey=prop.getProperty("appkey");
		appsecret=prop.getProperty("appsecret");
		authcode=prop.getProperty("authcode");
		username=prop.getProperty("username");
		tradecontactid=prop.getProperty("tradecontactid");
		nextactive=this.getExecuteobj().getNextactive();
		

		try {		
			updateJobFlag(1);
		
			getDistributor();
			
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
	
	private void getDistributor() throws Exception
	{
		
		long pageno=1L;
		
		for (int i=0;i<10;)
		{
			try
			{
				Date sdate=Formatter.parseDate(Formatter.format(this.nextactive, Formatter.DATE_FORMAT)+" 00:00:00",Formatter.DATE_TIME_FORMAT);
				Calendar cd = Calendar.getInstance();
				cd.setTime(sdate);
				cd.add(Calendar.DATE, -1);
				
				TaobaoClient client=new DefaultTaobaoClient(url,appkey,appsecret,"xml");
				FenxiaoCooperationGetRequest  req=new FenxiaoCooperationGetRequest();	
				req.setStartDate(Formatter.parseDate(Formatter.format(cd.getTime(),Formatter.DATE_TIME_FORMAT),Formatter.DATE_TIME_FORMAT));
				cd.add(Calendar.DATE, 1);
				req.setEndDate(Formatter.parseDate(Formatter.format(cd.getTime(),Formatter.DATE_TIME_FORMAT),Formatter.DATE_TIME_FORMAT));				
				req.setPageNo(pageno);
				req.setPageSize(100L);
				FenxiaoCooperationGetResponse rsp = client.execute(req, authcode);
					
				while(true)
				{
					if (rsp.getCooperations()==null || rsp.getCooperations().size()<=0)
					{	
						i=10;
						break;
					}
					for(Iterator it=rsp.getCooperations().iterator();it.hasNext();)
					{
						Cooperation cop=(Cooperation) it.next();
												
						Log.info(cop.getDistributorId()+" "+cop.getDistributorNick()+" "+Formatter.format(cop.getStartDate(),Formatter.DATE_TIME_FORMAT));
						
						String sql="select count(*) from ecs_distributor with(nolock) where distributorid="+cop.getDistributorId();
						if (this.getDao().intSelect(sql)==0)
						{
							sql="insert into ecs_distributor(distributorid,distributorname,startdate,shopname,manager,creator,operator,updator) "
								+"values("+cop.getDistributorId()+",'"+cop.getDistributorNick()+"','"
								+Formatter.format(cop.getStartDate(),Formatter.DATE_TIME_FORMAT)+"','','','system','system','system')";
							this.getDao().execute(sql);
						}
						else
						{
							sql="update ecs_distributor set distributorname='"+cop.getDistributorNick()+"',startdate='"+
								Formatter.format(cop.getStartDate(),Formatter.DATE_TIME_FORMAT)+"',"
								+"updatetime='"+Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)+"' "
								+"where distributorid="+cop.getDistributorId();
							this.getDao().execute(sql);
						}
						
					}
					pageno++;
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
