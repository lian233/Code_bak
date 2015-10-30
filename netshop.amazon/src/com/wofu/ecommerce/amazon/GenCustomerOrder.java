package com.wofu.ecommerce.amazon;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;


public class GenCustomerOrder extends Thread{
	private static String jobName = "亚马逊订单生成客户订单作业";
	
	private boolean is_gening=false;
	
	public void run() {
		Log.info(jobName, "启动[" + jobName + "]模块");
		do {		
			Connection connection = null;
			is_gening = true;
			try {
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.amazon.Params.dbname);	
				Vector vts=IntfUtils.getDownNotes(connection, Params.tradecontactid, "1");
				Log.info("生成本地订单开始");
				for (int i=0;i<vts.size();i++) {
					
					try{
						Hashtable hts=(Hashtable) vts.get(i);
						String sheetid=hts.get("sheetid").toString();
						
						//开始事务
						connection.setAutoCommit(false);
						//生成客户订单
						boolean is_success=OrderManager.genCustomerOrder(connection, sheetid,Params.isDelay,Params.tableType);				
											
						
						if (is_success)
						{
							//备份接口数据
							IntfUtils.backupDownNote(connection, "yongjun",sheetid, "1");
							
							//提交事务
							connection.commit();
							connection.setAutoCommit(true);
									
						
							Log.info("生成客户订单成功,接口单号【" + sheetid + "】");
						}
						else
						{
							try {
								if (connection != null && !connection.getAutoCommit())
									connection.rollback();
							} catch (Exception e1) {
								Log.error(jobName, "回滚事务失败");
							}
							Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
						}
					}catch(Throwable ex){
						try {
							if (connection != null && !connection.getAutoCommit())
								connection.rollback();
						} catch (Throwable e1) {
							Log.error(jobName, "回滚事务失败");
						}
						Log.error(jobName, ex.getMessage());
					}
					
					
				}
				Log.info("生成本地订单结束");
			} catch (Throwable e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Throwable e1) {
					Log.error(jobName, "回滚事务失败");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_gening = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Throwable e) {
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.amazon.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	public String toString()
	{
		return jobName + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}
