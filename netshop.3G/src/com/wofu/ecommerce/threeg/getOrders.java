package com.wofu.ecommerce.threeg;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.business.util.PublicUtils;


public class getOrders extends Thread {

	private static String jobname = "获取3G订单作业";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	
	private boolean is_importing=false;
	
	private static String cmdcode="1002";
	

	public getOrders() {
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
						com.wofu.ecommerce.threeg.Params.dbname);					
				
				Long starttime=Formatter.parseDate(PublicUtils.getConfig(connection, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime()+60000L;
				Long endtime=Formatter.parseDate(PublicUtils.getConfig(connection, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime()+daymillis;
		
				Hashtable<String,String> htwsinfo=new Hashtable<String,String>();
				htwsinfo.put("cmd",cmdcode);
				htwsinfo.put("wsurl",Params.wsurl);
				htwsinfo.put("CustomerPrivateKeyPath",Params.CustomerPrivateKeyPath);
				htwsinfo.put("GGMallPublicKeyPath",Params.GGMallPublicKeyPath);
				htwsinfo.put("encoding",Params.encoding);
				htwsinfo.put("username",Params.username);
				htwsinfo.put("agentid",Params.agentid);
				htwsinfo.put("style","0");
				htwsinfo.put("tradecontactid",Params.tradecontactid);
				htwsinfo.put("lasttimeconfvalue",lasttimeconfvalue);
				
				OrderUtils.getOrderList(jobname, connection, htwsinfo, "1", new Date(starttime), new Date(endtime));
				OrderUtils.getOrderList(jobname, connection, htwsinfo, "7", new Date(starttime), new Date(endtime));
				
				//apiClient.setRequestData(requestbuffer.toString());
				//boolean api=apiClient.invokeApi(Params.wsurl+orderquerymethod);
				//String s=new String(apiClient.getLastResponseContent());
				
				//Log.info(apiClient.getLastErrMsg());
				
								
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.threeg.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
