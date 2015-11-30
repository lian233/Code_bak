package com.wofu.ecommerce.taobao;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.ecommerce.taobao.Params;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class UpdateStock extends Thread{
	private static String jobName = "更新淘宝库存作业";
	private boolean is_updating=false;
	public UpdateStock() {
		setDaemon(true);
		setName(jobName);
	}

	public void run() {
		Log.info(jobName, "启动[" + jobName + "]模块");
		do {		
			Connection connection = null;
			is_updating = true;
			try {					
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				Params.authcode = PublicUtils.getToken(connection, Integer.parseInt(Params.tradecontactid));
				updateStock(connection);
		
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "回滚事务失败");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_updating = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	private void updateStock(Connection conn) throws Exception
	{

		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
		int orgid=SQLHelper.intSelect(conn, sql);
		
		ECSDao dao=new ECSDao(conn);
		
		sql="select tid,sku,qty from eco_synreducestore "
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
								Log.info("配置不需要同步库存,SKU:"+sku);
								StockManager.bakSynReduceStore(jobName, conn, Params.tradecontactid, tid, sku);	
								continue;  //不需要同步
							}
							
							int newqty=0;
							if(Math.abs(qty)>10){   //对于数量大于10的实时更新，先取一下当时的可用库存
								 int useableStore =StockManager.getTradeContactUseableStock(conn, Integer.valueOf(Params.tradecontactid).intValue(), stockconfigsku.getSku());
								 Log.info("sku: "+stockconfigsku.getSku()+",可用库存为: "+useableStore);
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
							Log.error(jobName, ex.getMessage());
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
								Log.info("配置不需要同步库存,SKU:"+sku);
								StockManager.bakSynReduceStore(jobName, conn, Params.tradecontactid, tid, sku);	
								continue;  //不需要同步
							}
							
							int newqty=0;
							if(Math.abs(qty)>10){   //对于数量大于10的实时更新，先取一下当时的可用库存
								 int useableStore =StockManager.getTradeContactUseableStock(conn, Integer.valueOf(Params.tradecontactid).intValue(), stockconfigsku.getSku());
								 Log.info("sku: "+stockconfigsku.getSku()+",可用库存为: "+useableStore);
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
							Log.error(jobName, ex.getMessage());
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
					
				Log.error(jobName,"更新淘宝库存失败,单号:"+tid+" SKU:" +sku+" "+je.getMessage());
			}
		}
	}

	
	public String toString()
	{
		return jobName + " " + (is_updating ? "[updating]" : "[waiting]");
	}

}
