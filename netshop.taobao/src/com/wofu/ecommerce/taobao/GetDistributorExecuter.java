package com.wofu.ecommerce.taobao;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Cooperation;
import com.taobao.api.request.FenxiaoCooperationGetRequest;
import com.taobao.api.response.FenxiaoCooperationGetResponse;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.business.util.PublicUtils;
public class GetDistributorExecuter extends Executer {

	private static String jobName = "获取淘宝分销取分销商作业";
	
	private static long daymillis=24*60*60*1000L;
	private static String username="";
	private String lasttimeconfvalue="";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	private String url="";
	private String appkey="";
	private String appsecret="";
	private String lasttime="";
	private String authcode="";

	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		username=prop.getProperty("sellernick");	
		lasttimeconfvalue=username+"分销取分销商最新时间";
		url=prop.getProperty("url");
		appkey=prop.getProperty("appkey");
		appsecret=prop.getProperty("appsecret");
		authcode=prop.getProperty("authcode");
		
		is_importing = true;
			try {
				updateJobFlag(1);
				lasttime=PublicUtils.getConfig(this.getConnection(),lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
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
	 * 获取一天之类的所有分销商
	 */
	private void getDistributor() throws Exception
	{	
		Connection conn=this.getConnection();
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<3;)
		{
			try
			{
				TaobaoClient client=new DefaultTaobaoClient(url,appkey, appsecret,"xml");
				FenxiaoCooperationGetRequest req=new FenxiaoCooperationGetRequest();		
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				req.setStartDate(startdate);
				req.setEndDate(enddate);
				req.setPageNo(pageno);
				req.setPageSize(40L);
				FenxiaoCooperationGetResponse response = client.execute(req , authcode);
				
		
				int i=1;
			
				while(true)
				{
								
					if (response.getCooperations()==null || response.getCooperations().size()<=0)
					{				
						if (i==1)		
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(jobName, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobName, "不可用的日期格式!"+e.getMessage());
							}
						}
						break;
					}
					
					
					
					for(Iterator it=response.getCooperations().iterator();it.hasNext();)
					{
						try{
							Cooperation cop=(Cooperation) it.next();
							
							Log.info(cop.getDistributorId()+" "+cop.getDistributorNick()+" "+Formatter.format(cop.getStartDate(),Formatter.DATE_TIME_FORMAT));
							//taobao.fenxiao.distributors.get  api收费
							String shopname=OrderUtils.getDistributorShopName(url,appkey,appsecret,authcode,cop.getDistributorNick());
							
							shopname=StringUtil.replace(shopname, "'"," ");
							
							
							String sql="select count(*) from ecs_distributor with(nolock) where distributorid="+cop.getDistributorId();
							if (SQLHelper.intSelect(conn, sql)==0)
							{
								sql="insert into ecs_distributor(distributorid,distributorname,startdate,shopname,manager,creator,operator,updator) "
									+"values("+cop.getDistributorId()+",'"+cop.getDistributorNick()+"','"
									+Formatter.format(cop.getStartDate(),Formatter.DATE_TIME_FORMAT)+"','"+shopname+"','','system','system','system')";
								SQLHelper.executeSQL(conn, sql);
							}
							else
							{
								sql="update ecs_distributor set distributorname='"+cop.getDistributorNick()+"',startdate='"+
									Formatter.format(cop.getStartDate(),Formatter.DATE_TIME_FORMAT)+"',"
									+"updatetime='"+Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)+"', "
									+"shopname='"+shopname+"' "
									+"where distributorid="+cop.getDistributorId();
								SQLHelper.executeSQL(conn, sql);
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
				
	
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
				
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				//执行成功后不再循环
				break;
			}catch (JException e) {
				
				throw e;
				
			} catch (Exception e) {
				if (++k >= 3)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
	
	

	
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
