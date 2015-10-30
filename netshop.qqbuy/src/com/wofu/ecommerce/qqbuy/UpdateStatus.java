package com.wofu.ecommerce.qqbuy;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;


import com.wofu.ecommerce.qqbuy.Params;
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class UpdateStatus extends Thread {
	
	private static String jobname = "QQ网购订单状态更新作业";
	private static String accessToken = Params.accessToken ;
	private static String appOAuthID = Params.appOAuthID ;
	private static String secretOAuthKey = Params.secretOAuthKey ;
	private static String cooperatorId = Params.cooperatorId ;
	private static String uin = Params.uin ;
	private static String encoding = Params.encoding ;
	private static String format = Params.format ;
	private static String tradecontactid = Params.tradecontactid ;



	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
	
			try {		
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.qqbuy.Params.dbname);
				//确认审核订单
				doUpdateOrderStatus(jobname, connection, "1") ;
				//取消QQ网购订单订单
				doUpdateOrderStatus(jobname, connection, "2") ;

			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
			
				try {
					if (connection != null)
						connection.close();
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

	//更新订单审校结果updateState 1:审核成功 2：取消该订单
	public static void doUpdateOrderStatus(String jobname,Connection conn,String updateState)
	{
		try 
		{
			Vector vts=IntfUtils.getUpNotes(conn, tradecontactid, updateState);
			if(vts.size() <= 0)
				return ;
			
			String dealCheckResult = "" ;//订单审核结果 0: 审核成功	1: 审核失败
			if("1".equals(updateState))
				dealCheckResult = "0" ;
			else if("2".equals(updateState))
				dealCheckResult = "1" ;
			else
				;
			
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("appOAuthID", appOAuthID) ;
			params.put("secretOAuthKey", secretOAuthKey) ;
			params.put("accessToken", accessToken) ;
			params.put("cooperatorId", cooperatorId) ;
			params.put("encoding", encoding) ;
			params.put("uin", uin) ;
			params.put("format", format) ;
			for (int i=0;i<vts.size();i++)
			{
				Hashtable hts=(Hashtable) vts.get(i);
				String sheetid=hts.get("sheetid").toString();
				String sql = "select top 1 a.tid,b.buyerflag from ns_delivery as a with(nolock),ns_customerOrder as b with(nolock) where a.tid=b.tid and a.sheetid='"+ sheetid +"' order by b.sheetid desc" ;
				Hashtable<String,String> info = SQLHelper.oneRowSelect(conn, sql) ;
				String tid = info.get("tid") ;
				String dealCheckVersion = info.get("buyerflag") ;
				boolean success = OrderUtils.updateOrderStatus(jobname, conn, sheetid, tid, dealCheckVersion, dealCheckResult, params) ;
				if(success)
					IntfUtils.backupUpNote(conn, "yongjun",sheetid, updateState);
			}
		} 
		catch (Exception e) 
		{
			Log.error(jobname, "更新订单审核状态失败,错误信息:"+ e.getMessage()) ;
		}
	}

}