package com.wofu.netshop.meilishuo.fenxiao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class DeliveryRunnable implements Runnable{
	private String jobName="美丽说发货作业";
	private CountDownLatch watch;
	private Params param;
	public DeliveryRunnable(CountDownLatch watch ,Params param){
		this.watch = watch;
		this.param = param;
	}
	public void run() {
		jobName = param.username+jobName;
		Connection conn=null;
		try{
			conn = PoolHelper.getInstance().getConnection("shop");
			delivery(conn);
		}catch(Throwable e){
			Log.error(param.username, e.getMessage(), null);
		}finally{
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			watch.countDown();
		}
		
	}
	
	private void delivery(Connection conn) throws Exception{
		String sql = "select a.id,a.tid,a.companycode,a.outsid from itf_delivery a,Inf_UpNote b "
			+"where a.id=b.OperData and a.sheettype=3 and a.shopid="+param.shopid;
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		Log.info("本次要处理的订单发货条数为: "+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) {
			Hashtable ht =(Hashtable) vdeliveryorder.get(i);
			
		}
	}

}
