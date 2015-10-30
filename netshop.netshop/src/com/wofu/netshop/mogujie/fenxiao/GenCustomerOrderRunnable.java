package com.wofu.netshop.mogujie.fenxiao;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import com.wofu.business.fenxiao.intf.IntfUtils;
import com.wofu.business.fenxiao.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.log.Log;

public class GenCustomerOrderRunnable implements Runnable{
	private String jobName="蘑菇街接口订单生成客户订单作业";
	private CountDownLatch watch;
	private String username="";
	private Params param;
	public GenCustomerOrderRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
	}
	public void run() {
		Connection connection = null;
		try {
			connection = PoolHelper.getInstance().getConnection("shop");
			List vts=IntfUtils.getDownNotes(connection, param.shopid, 1);
			boolean is_success=false;
			Log.info(username,"本次共要处理的蘑菇街接口订单生成临时订单总数为: "+vts.size(),null);
			int sheetid=0;
			for (int i=0;i<vts.size();i++) {
				try{
					sheetid=(Integer)vts.get(i);
					//开始事务
					connection.setAutoCommit(false);
					//生成客户订单
					is_success=OrderManager.GenDecOrder(connection, sheetid);
					
					if (is_success)
					{
						//备份接口数据
						IntfUtils.backupDownNote(connection, sheetid,1, "success");
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
