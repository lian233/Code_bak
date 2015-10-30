package com.wofu.ecommerce.qqbuy;

import java.sql.Connection;
import java.util.Hashtable;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.log.Log;

public class GetOrders extends Thread {

	private static String jobname = "获取QQ网购订单作业";
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	
	private static String accessToken = Params.accessToken ;
	private static String appOAuthID = Params.appOAuthID ;
	private static String secretOAuthKey = Params.secretOAuthKey ;
	private static String cooperatorId = Params.cooperatorId ;
	private static String uin = Params.uin ;
	private static String encoding = Params.encoding ;
	private static String pageSize = Params.pageSize ;
	private static String format = Params.format ;
	private static String timeType = Params.timeType ;
	private static String orderState = Params.orderState ;
	private static String tradecontactid = Params.tradecontactid ;
	private static String username = Params.username ;
	
	private boolean is_importing=false;
	
	public GetOrders() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection conn = null;
			is_importing = true;
			try {
				conn = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.qqbuy.Params.dbname);	
				Hashtable<String, String> params = new Hashtable<String, String>() ;
				params.put("accessToken", accessToken) ;
				params.put("appOAuthID", appOAuthID) ;
				params.put("secretOAuthKey", secretOAuthKey) ;
				params.put("cooperatorId", cooperatorId) ;
				params.put("uin", uin) ;
				params.put("encoding", encoding) ;
				params.put("format", format) ;
				params.put("lasttimeconfvalue", lasttimeconfvalue) ;
				params.put("pageSize", pageSize) ;
				
				//获取QQ网购最新待审校订单
				OrderUtils.getOrdersList(jobname, conn, orderState, timeType, lasttimeconfvalue, tradecontactid, username,Params.isNeedInvoice, params) ;
				//获取QQ网购最新退换货单
				OrderUtils.getRefundOrders(jobname, conn, timeType, Params.username + "取退换货订单最新时间", tradecontactid, params) ;
				
			} catch (Exception e) {
				try {
					e.printStackTrace() ;
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.qqbuy.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	
	
	
}