package com.wofu.netshop.alibaba.fenxiao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import com.wofu.base.dbmanager.ECSDao;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.netshop.taobao.fenxiao.StockUtils;

public class UpdateStockRunnable implements Runnable{
	private String jobName="���°���Ͱ����������ҵ";
	private CountDownLatch watch;
	private String username="";
	private Params param;
	public UpdateStockRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
	}
	
	
	public void run() {
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
					Log.error(username,"�ر����ݿ��������: "+e1.getMessage(),null);
				}
				Log.info(username,jobName+" "+e.getMessage(),null);
			}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.error(username,"�ر����ݿ����ӳ���  "+e.getMessage());
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
				//StockUtils.updateDistributionRealTimeSkuStock(param.url,param.appKey,param.appsecret,param.authcode
						//,num_iid,sku_id,sku,qty);
			}catch(Exception je)
			{
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.error(username,jobName+" ���°���ͰͿ��ʧ��,����:"+id+" SKU:" +sku+" "+je.getMessage(),null);
			}
			temp.append(id).append(",");
		}
		if(vtinfo.size()>0)
		//���´����־
		SQLHelper.executeSQL(conn,temp.deleteCharAt(temp.length()-1).append(")").toString());
		
		
	}

}
