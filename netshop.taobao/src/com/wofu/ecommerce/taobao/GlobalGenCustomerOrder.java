package com.wofu.ecommerce.taobao;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class GlobalGenCustomerOrder extends Thread{
	private static String jobname = "全局淘宝订单生成客户订单作业";
	
	private boolean is_gening=false;
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection connection = null;
			is_gening = true;
			try {
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.taobao.Params.dbname);
				
				Vector vts=IntfUtils.getGlobalDownNotes(connection,"1",Params.notingetOrder);
				boolean is_success=false;
				Log.info("本次共要处理的淘宝接口订单生成临时订单总数为: "+vts.size());
				if(vts.size()>0 && 1==Params.tableType){
					//删除已经存在的接口临时表
					String sql = "if not OBJECT_ID('tempdb..#NS_CustomerOrder0') is null drop table #NS_CustomerOrder0";
					SQLHelper.executeSQL(connection, sql);
					sql = "if not OBJECT_ID('tempdb..#NS_Orderitem0') is null drop table #NS_Orderitem0";
					SQLHelper.executeSQL(connection, sql);
					sql = "if not OBJECT_ID('tempdb..#it_downnote0') is null drop table #it_downnote0";
					SQLHelper.executeSQL(connection, sql);
					//建立接口临时表
					sql = "select * into #it_downnote0 from it_downnote where sheettype=1 order by notetime";
					SQLHelper.executeSQL(connection,sql);
					sql = "select a.* into #ns_customerorder0 from ns_customerorder a(nolock),#it_downnote0 b where a.sheetid=b.sheetid";
					SQLHelper.executeSQL(connection, sql);
					sql = "select a.* into #ns_orderitem0 from ns_orderitem a(nolock),#it_downnote0 b where a.sheetid=b.sheetid";
					SQLHelper.executeSQL(connection, sql);
				}
				
				for (int i=0;i<vts.size();i++) {
					String sheetid="";
					try{
						Hashtable hts=(Hashtable) vts.get(i);
						sheetid=hts.get("sheetid").toString();
						long currentTime = System.currentTimeMillis();
						//开始事务
						connection.setAutoCommit(false);
						//生成客户订单
						if(Params.isEc)
							is_success=OrderManager.genEcCustomerOrder(connection, sheetid);
						else
							is_success=OrderManager.genCustomerOrder(connection, sheetid,Params.isDelay,Params.tableType);
						
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
								Log.error(jobname, "回滚事务失败");
							}
							Log.info("生成客户订单失败,接口单号【" + sheetid + "】");
						}
						long needTime = System.currentTimeMillis()-currentTime;
						Log.info("生成一个订单所需的时间为: "+needTime);
						if(needTime>1000){
							long starttime= System.currentTimeMillis();
							while(System.currentTimeMillis()-starttime<10*1000L){
								Thread.sleep(100L);
							}
						}
						//添加间隔
						//Thread.sleep(2000L);
					}catch(Exception ex){
						try {
							if (connection != null && !connection.getAutoCommit())
								connection.rollback();
						} catch (Exception e1) {
							Log.error(jobname, "回滚事务失败");
						}
						Log.error(jobname, ex.getMessage());
						Log.info("生成客户订单失败,接口单号【" + sheetid + "】,详细信息： "+ex.getMessage());
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.taobao.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	public String toString()
	{
		return jobname + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}
