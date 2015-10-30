package com.wofu.ecommerce.suning;


import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;

import com.wofu.common.tools.util.log.Log;

public class UpdateStock extends Thread{
	
	private static String jobName = "更新苏宁库存作业";
	private static int orgid = 0;
	
	private boolean is_updating=false;
	
	public void run() {
		Log.info(jobName, "启动[" + jobName + "]模块");
		do {		
			Connection connection = null;
			is_updating = true;
			try {					
				connection = PoolHelper.getInstance().getConnection(Params.dbname);

	
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

		
		String sql="";
		ECSDao dao=new ECSDao(conn);
		if(orgid==0){
			sql="select orgid from ecs_tradecontactorgcontrast(nolock) where tradecontactid="+Params.tradecontactid;
			orgid=dao.intSelect(sql);
		}
		
		sql="select id,tid,sku,qty from eco_synreducestore(nolock) "
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
					sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and sku='"+sku+"'";
					Vector vtstockconfigsku=SQLHelper.multiRowSelect(conn, sql);
					if(vtstockconfigsku.size()>0){     //存在子商品
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
								Log.info("更新sku库存: ,"+"产品编码："+stockconfig.getItemid()+"货号:　"+stockconfig.getItemcode());
								StockUtils.updateSkuStock(jobName,dao,orgid,Params.url,Params.format,Params.appKey,Params.appsecret,stockconfig,stockconfigsku,newqty,false);
							}catch(Exception ex){
								if(dao!=null && !dao.getConnection().getAutoCommit())
									dao.getConnection().rollback();
								Log.error(jobName, ex.getMessage());
								continue;
							}
							
						}
				
					}else{   //没有子商品
						Log.info("子商品: --");
						sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemcode='"+sku+"'";
						Hashtable htStockconfig =  SQLHelper.oneRowSelect(conn, sql);
						ECS_StockConfig stockconfig = new ECS_StockConfig();
						stockconfig.getMapData(htStockconfig);
						if (stockconfig.getIsneedsyn()==0)
						{
							Log.info("配置不需要同步库存,SKU:"+sku);
							continue;  //不需要同步
						}
						int newqty=0;

						//如果原库存加上本次需同步的库存小于等于警戒库存,则更新库存为零
						if ((stockconfig.getStockcount()+qty)<=stockconfig.getAlarmqty())
						{								
							newqty=-stockconfig.getStockcount();
						}
						else
						{
							newqty=qty+stockconfig.getStockcount();
						}

						if (newqty<0) newqty=0;	

						StockUtils.updateItemStock(jobName,dao,orgid,Params.url,Params.format,stockconfig,Params.appKey,Params.appsecret,newqty);
						
					}
				Log.info("更新完毕: "+sku);
			}catch(Exception je)
			{	if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				//StockManager.bakSynReduceStore(jobName, conn, Params.tradecontactid, tid, sku);	
				Log.error(jobName,"更新苏宁库存失败,单号:"+tid+" SKU:" +sku+" "+je.getMessage());
				continue;
			}
		}
		if(vtinfo.size()>0){
			//更新处理标志
			StringBuilder sqlstr = new StringBuilder().append("update eco_synreducestore set synflag=1 where id in(");
			for(int i=0;i<vtinfo.size();i++)
			{
				if(i==vtinfo.size()-1){
					sqlstr.append(((Hashtable)(vtinfo.get(i))).get("id")).append(")");
				}else{
					sqlstr.append(((Hashtable)(vtinfo.get(i))).get("id")).append(",");
				}
			}
			Log.info("更改同步库存记录标志sql: "+sqlstr.toString());
			SQLHelper.executeSQL(conn,sqlstr.toString());
			Log.info("更改库存同步记录标志成功");
		}
		Log.info("线程更新sku库存完毕！");
	}

	
	public String toString()
	{
		return jobName + " " + (is_updating ? "[updating]" : "[waiting]");
	}

}
