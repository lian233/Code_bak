package com.wofu.ecommerce.jiaju;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.log.Log;


public class GenCustomerOrder extends Thread{
	private static String jobname = "家居就订单生成客户订单作业";
	
	private boolean is_gening=false;
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Date nowtime = new Date();
			if(Params.startTime.getTime() <= nowtime.getTime())
			{//符合或超过指定的启动时间
				Connection connection = null;
				is_gening = true;
				try {
					connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.jiaju.Params.dbname);	
					Vector vts=IntfUtils.getDownNotes(connection, Params.tradecontactid, "1");
					for (int i=0;i<vts.size();i++) {
						try{
							Hashtable hts=(Hashtable) vts.get(i);
							String sheetid=hts.get("sheetid").toString();
							
							//开始事务
							connection.setAutoCommit(false);
							//生成客户订单
							boolean is_success=OrderManager.genCustomerOrder(connection, sheetid, Params.isDelay, Params.tableType);				
							
							
							if (is_success)
							{
								//备份接口数据
								IntfUtils.backupDownNote(connection, "接口", sheetid, "1");
								
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
									Log.error(jobname, "回滚事务失败");
								}
								Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
							}
						}catch(Exception e){
							if(connection!=null && !connection.getAutoCommit()){
								connection.rollback();
								connection.setAutoCommit(true);
							}
						}
						
					}		
				} catch (Exception e) {
					try {
						if (connection != null && !connection.getAutoCommit())
							connection.rollback();
					} catch (Exception e1) {
						Log.error(jobname, "回滚事务失败");
					}
					Log.error("105", jobname, Log.getErrorMessage(e));
				} finally {
					is_gening = false;
					try {
						if (connection != null)
							connection.close();
					} catch (Exception e) {
						Log.error(jobname, "关闭数据库连接失败");
					}
				}
				System.gc();
				long startwaittime = System.currentTimeMillis();
				while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.jiaju.Params.waittime * 1000))
					try {
						sleep(1000L);
					} catch (Exception e) {
						Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
					}
			}
			else
			{//等待启动
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
			}
		} while (true);
	}
	
	public String toString()
	{
		return jobname + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}
