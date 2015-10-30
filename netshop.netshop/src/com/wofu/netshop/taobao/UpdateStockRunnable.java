package com.wofu.netshop.taobao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;

/**
 * 更新淘宝增量库存线程类
 * @author Administrator
 *
 */
public class UpdateStockRunnable implements Runnable{
	private String jobName="更新淘宝增量库存作业";
	private CountDownLatch watch;
	private String username="";
	private int orgid;
	public UpdateStockRunnable(CountDownLatch watch,String username,int orgid){
		this.watch=watch;
		this.username=username;
		this.orgid=orgid;
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

		//String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
		//int orgid=SQLHelper.intSelect(conn, sql);
		
		ECSDao dao=new ECSDao(conn);
		
		String sql="select tid,sku,qty from eco_synreducestore "
			+"where tradecontactid='"+Params.tradecontactid+"' "
			+"and synflag=0 and sku is not null and sku<>'' ";
		Vector vtinfo=SQLHelper.multiRowSelect(conn, sql);
		
		for(int i=0;i<vtinfo.size();i++)
		{
		
			Hashtable htinfo=(Hashtable) vtinfo.get(i);
			
			String tid=htinfo.get("tid").toString();
			String sku=htinfo.get("sku").toString();
			int qty=Integer.valueOf(htinfo.get("qty").toString()).intValue();
			
			try 
			{		
				StockManager.bakSynReduceStore(jobName, conn, Params.tradecontactid, tid, sku);
				Thread.sleep(10000L);
				if (Params.isdistribution)
				{
					sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and sku='"+sku+"'";
					
					Vector vtstockconfigsku=SQLHelper.multiRowSelect(conn, sql);
					
					for (int j=0;j<vtstockconfigsku.size();j++)
					{
						try{
							Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
							
							ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
							stockconfigsku.getMapData(htstockconfigsku);
							
							sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+stockconfigsku.getItemid()+"'";
							Hashtable htstockconfig=SQLHelper.oneRowSelect(conn, sql);
							
							
							ECS_StockConfig stockconfig=new ECS_StockConfig();
							stockconfig.getMapData(htstockconfig);
							
							if (stockconfig.getIsneedsyn()==0)
							{
								Log.info(username,"配置不需要同步库存,SKU:"+sku,null);
								StockManager.bakSynReduceStore(jobName, conn, Params.tradecontactid, tid, sku);	
								continue;  //不需要同步
							}
							
							int newqty=0;
							if(Math.abs(qty)>10){   //对于数量大于10的实时更新，先取一下当时的可用库存
								 int useableStore =StockManager.getTradeContactUseableStock(conn, Integer.valueOf(Params.tradecontactid).intValue(), stockconfigsku.getSku());
								 Log.info(username,"sku: "+stockconfigsku.getSku()+",可用库存为: "+useableStore,null);
								 StockUtils.updateDistributionSkuStock(dao,Params.url,Params.appkey,Params.appsecret,Params.authcode,stockconfig,stockconfigsku,useableStore);
							}else{
								//如果原库存加上本次需同步的库存小于等于警戒库存,则更新库存为零
								if ((stockconfigsku.getStockcount()+qty)<=stockconfig.getAlarmqty())
								{								
									newqty=-stockconfigsku.getStockcount();
								}
								else
								{
									newqty=qty+stockconfigsku.getStockcount();
								}

								if (newqty<0) newqty=0;	
								
								StockUtils.updateDistributionSkuStock(dao,Params.url,Params.appkey,Params.appsecret,Params.authcode,stockconfig,stockconfigsku,newqty);
							}

						}catch(Exception ex){
							if(conn!=null &&  !conn.getAutoCommit()) conn.rollback();
							Log.error(username,jobName+" "+ex.getMessage());
						}
						
						
					}
			
				}else
				{
					
					sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and sku='"+sku+"'";
					
					Vector vtstockconfigsku=SQLHelper.multiRowSelect(conn, sql);
					
					for (int j=0;j<vtstockconfigsku.size();j++)
					{
						try{
							Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
							
							ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
							stockconfigsku.getMapData(htstockconfigsku);
							
							sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+stockconfigsku.getItemid()+"'";
							Hashtable htstockconfig=SQLHelper.oneRowSelect(conn, sql);
							
							
							ECS_StockConfig stockconfig=new ECS_StockConfig();
							stockconfig.getMapData(htstockconfig);
							
							if (stockconfig.getIsneedsyn()==0)
							{
								Log.info(username,"配置不需要同步库存,SKU:"+sku,null);
								StockManager.bakSynReduceStore(jobName, conn, Params.tradecontactid, tid, sku);	
								continue;  //不需要同步
							}
							
							int newqty=0;
							if(Math.abs(qty)>10){   //对于数量大于10的实时更新，先取一下当时的可用库存
								 int useableStore =StockManager.getTradeContactUseableStock(conn, Integer.valueOf(Params.tradecontactid).intValue(), stockconfigsku.getSku());
								 Log.info(username,"sku: "+stockconfigsku.getSku()+",可用库存为: "+useableStore,null);
								 StockUtils.updateSkuStock(dao,Params.url,Params.appkey,Params.appkey,Params.appkey,stockconfig,stockconfigsku,useableStore,1);
							}else{
								//如果原库存加上本次需同步的库存小于等于警戒库存,则更新库存为零
								if ((stockconfigsku.getStockcount()+qty)<=stockconfig.getAlarmqty())
								{								
									newqty=0;
									StockUtils.updateSkuStock(dao,Params.url,Params.appkey,Params.appkey,Params.appkey,stockconfig,stockconfigsku,newqty,1);
								}
								else
								{
									newqty=qty;
									StockUtils.updateSkuStock(dao,Params.url,Params.appkey,Params.appsecret,Params.authcode,stockconfig,stockconfigsku,newqty,2);
								}
							}
							


							
						}catch(Exception ex){
							if(conn!=null &&  !conn.getAutoCommit()) conn.rollback();
							Log.error(username,jobName+" "+ex.getMessage());
						}
						
				
					}
				}
				
			}catch(Exception je)
			{
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				/*conn.setAutoCommit(false);
				try{
					StockManager.bakSynReduceStore(jobName, conn, Params.tradecontactid, tid, sku);
					conn.commit();
				}catch(Exception ex){
					if(conn!=null && !conn.getAutoCommit()) conn.rollback();
					Log.error(jobName, "备份数据失败");
				}*/
					
				Log.error(username,jobName+" 更新淘宝库存失败,单号:"+tid+" SKU:" +sku+" "+je.getMessage(),null);
			}
		}
	}
	
	

}
