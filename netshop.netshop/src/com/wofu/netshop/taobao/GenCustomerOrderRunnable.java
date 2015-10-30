package com.wofu.netshop.taobao;
/**
 * 接口订单生成客户订单线程类
 */
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class GenCustomerOrderRunnable implements Runnable{
	private String jobName="接口订单生成客户订单作业";
	private CountDownLatch watch;
	private String username="";
	public GenCustomerOrderRunnable(CountDownLatch watch,String username){
		this.watch=watch;
		this.username=username;
	}
	public void run() {
		Connection connection = null;
		try {
			connection = PoolHelper.getInstance().getConnection("shop");
			Vector vts=IntfUtils.getDownNotes(connection, Params.tradecontactid, "1");
			boolean is_success=false;
			Log.info(username,"本次共要处理的淘宝接口订单生成临时订单总数为: "+vts.size(),null);
			if(vts.size()>0 && 1==Params.tableType){
				//删除已经存在的接口临时表
				String sql = "if not OBJECT_ID('tempdb..#NS_CustomerOrder0') is null drop table #NS_CustomerOrder0";
				SQLHelper.executeSQL(connection, sql);
				sql = "if not OBJECT_ID('tempdb..#NS_Orderitem0') is null drop table #NS_Orderitem0";
				SQLHelper.executeSQL(connection, sql);
				sql = "if not OBJECT_ID('tempdb..#it_downnote0') is null drop table #it_downnote0";
				SQLHelper.executeSQL(connection, sql);
				//建立接口临时表
				sql = "select * into #it_downnote0 from it_downnote where sheettype=1 and sender='"+Params.tradecontactid+"'";
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
					
					//开始事务
					connection.setAutoCommit(false);
					//生成客户订单
					if("1".equals(Params.isEc))
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
								
					
						Log.info(username,"生成客户订单成功,接口单号【" + sheetid + "】",null);
					}
					else
					{
						try {
							if (connection != null && !connection.getAutoCommit())
								connection.rollback();
						} catch (Exception e1) {
							Log.error(username,jobName+" 回滚事务失败",null);
						}
						Log.info(username,"生成客户订单失败,接口单号【" + sheetid + "】",null);
					}
				}catch(Exception ex){
					try {
						if (connection != null && !connection.getAutoCommit())
							connection.rollback();
					} catch (Exception e1) {
						Log.error(username,jobName+" 回滚事务失败",null);
					}
					Log.error(username,jobName+" "+ex.getMessage(),null);
					Log.info(username,"生成客户订单失败,接口单号【" + sheetid + "】,详细信息： "+ex.getMessage(),null);
				}
				
			}
			
		} catch (Exception e) {
			try {
				if (connection != null && !connection.getAutoCommit())
					connection.rollback();
			} catch (Exception e1) {
				Log.error(username,jobName+" 回滚事务失败",null);
			}
			Log.error(username,jobName+" "+Log.getErrorMessage(e),null);
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				Log.error(username,jobName+" 关闭数据库连接失败");
			}
			watch.countDown();
		}
		
	}

}
