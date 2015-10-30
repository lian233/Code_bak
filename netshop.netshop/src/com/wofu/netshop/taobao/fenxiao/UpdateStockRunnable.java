package com.wofu.netshop.taobao.fenxiao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.taobao.fenxiao.Params;
/**
 * 更新淘宝增量库存线程类
 * @author Administrator
 *
 */
public class UpdateStockRunnable implements Runnable{
	private String jobName="更新淘宝分销增量库存作业";
	private CountDownLatch watch;
	private String username="";
	private int orgid;
	private Params param;
	public UpdateStockRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
		
	}
	public void run() {
		// TODO Auto-generated method stub
		Connection conn=null;
		try{
			conn=PoolHelper.getInstance().getConnection("shop");
			Log.info(username,jobName,null);
			updateStock(conn);
		}catch(Exception e){
			try {
				if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				conn.setAutoCommit(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					Log.error(username,"关闭数据库事务出错: "+e1.getMessage(),null);
				}
				Log.info(username,jobName+" "+e.getMessage(),null);
			}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.error(username,"关闭数据库连接出错  "+e.getMessage());
				}
				watch.countDown();
		}
		
	}
	
	private void updateStock(Connection conn) throws Exception
	{
		String sql=new StringBuilder("select b.id,num_iid,a.sku_id,sku,b.qty from DecItem a (nolock),")
		.append("des_SynReduceStore b (nolock) where a.OuterSkuID=b.sku and b.flag=0 and b.shopid=").append(param.shopid).toString();
		Vector vtinfo=SQLHelper.multiRowSelect(conn, sql);
		StringBuilder temp =new StringBuilder("update des_SynReduceStore set flag=1 where id in(");
		for(int i=0;i<vtinfo.size();i++)
		{
			Hashtable htinfo=(Hashtable) vtinfo.get(i);
			
			String sku=htinfo.get("sku").toString();
			String sku_id=htinfo.get("sku_id").toString();
			long num_iid=(Long)htinfo.get("num_iid");
			int qty=(Integer)htinfo.get("sku");
			int id = (Integer)htinfo.get("id");
			try 
			{		
				StockUtils.updateDistributionRealTimeSkuStock(param.url,param.appkey,param.appsecret,param.authcode
						,num_iid,sku_id,sku,qty);
			}catch(Exception je)
			{
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.error(username,jobName+" 更新淘宝库存失败,单号:"+id+" SKU:" +sku+" "+je.getMessage(),null);
			}
			temp.append(id).append(",");
		}
		if(vtinfo.size()>0)
		//更新处理标志
		SQLHelper.executeSQL(conn,temp.deleteCharAt(temp.length()-1).append(")").toString());
		
	}
	
	

}
