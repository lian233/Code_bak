package com.wofu.ecommerce.vjia;

import java.sql.Connection;
import java.util.Hashtable;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.log.Log;

public class GetReturnOrders extends Thread {

	private static String jobname = "获取vjia退换货单作业";
	private static String tradecontactid = String.valueOf(Params.tradecontactid);
	private static int timeInterval = Params.timeInterval ; 
	private boolean is_importing=false;
	

	public GetReturnOrders() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
			is_importing = true;
			try 
			{
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	
				Hashtable<String, String> params = new Hashtable<String, String>() ;
				params.put("passWord", Params.suppliersign) ;
				params.put("userName", Params.supplierid) ;
				params.put("URI", Params.uri) ;
				params.put("swsSupplierID", Params.swssupplierid) ;
				params.put("wsurl", Params.wsurl) ;
				params.put("strkey", Params.strkey) ;
				params.put("striv", Params.striv) ;
				params.put("pageSize", Params.pageSize) ;
				OrderUtils.getReturnOrders(jobname, connection, tradecontactid, Params.username+"取退换货单最新时间", timeInterval,params) ;
			} 
			catch (Exception e) 
			{
				try 
				{
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} 
			finally 
			{
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
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	
	
	
}

