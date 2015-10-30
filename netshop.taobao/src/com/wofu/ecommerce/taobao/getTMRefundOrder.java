package com.wofu.ecommerce.taobao;


import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.RefundBill;
import com.taobao.api.domain.Trade;
import com.taobao.api.request.TmallEaiOrderRefundMgetRequest;
import com.taobao.api.response.TmallEaiOrderRefundMgetResponse;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.business.util.PublicUtils;

public class getTMRefundOrder extends Thread {

	private static String jobname = "获取天猫退款订单作业";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"取天猫退款单最新时间";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;
	

	public getTMRefundOrder() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.taobao.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				
				getRefundBillList(connection);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.taobao.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	
	/*
	 * 获取一天之类的退款订单
	 */
	private void getRefundBillList(Connection conn) throws Exception
	{		
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				TaobaoClient client=new DefaultTaobaoClient(Params.url,Params.appkey, Params.appsecret,"json");				
				TmallEaiOrderRefundMgetRequest req=new TmallEaiOrderRefundMgetRequest();
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				req.setStartTime(startdate);
				req.setEndTime(enddate);
				req.setPageNo(pageno);
				req.setPageSize(40L);
				req.setUseHasNext(true);
				TmallEaiOrderRefundMgetResponse response = client.execute(req , Params.authcode);
				
				while(true)
				{
					if (response.getRefundBillList()==null || response.getRefundBillList().size()<=0)
					{				
						if (pageno==1)		
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
				                		Log.error(jobname, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobname, "不可用的日期格式!"+e.getMessage());
							}
						}
						break;
					}
					
					
			
					for(Iterator it=response.getRefundBillList().iterator();it.hasNext();)
					{
						RefundBill refundbill=(RefundBill) it.next();
						
						Log.info(refundbill.getTid()+" "+refundbill.getStatus()+" "+refundbill.getModified());
						
						if (!RefundUtil.RefundBillisCheck(conn, String.valueOf(refundbill.getTid()),String.valueOf(refundbill.getRefundId()),refundbill.getModified()))
						{
							Trade td=OrderUtils.getFullTrade(String.valueOf(refundbill.getTid()), Params.url, Params.address, Params.appsecret, Params.authcode);
							
							RefundUtil.createRefundBill(conn,refundbill,td,Params.tradecontactid);
								
						}
						if (Formatter.parseDate(refundbill.getModified(), Formatter.DATE_TIME_FORMAT).compareTo(modified)>0)
		                {
		                	modified=Formatter.parseDate(refundbill.getModified(), Formatter.DATE_TIME_FORMAT);
		                }
						
					}
					
					//判断是否下一页
					if(response.getHasNext()){
						pageno++;
					}else{
						break;
					}
					
						
				}
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobname,je.getMessage());
	            	}
				}
				//执行成功后不再循环
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
