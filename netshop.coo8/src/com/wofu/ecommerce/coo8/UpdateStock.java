package com.wofu.ecommerce.coo8;
import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
public class UpdateStock extends Thread{
	
	private static String jobName = "更新库巴库存作业";
	private static int orgid=0;
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
		if(orgid==0){
			sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
			orgid=SQLHelper.intSelect(conn, sql);
		}
		
		ECSDao dao=new ECSDao(conn);
		
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
				for (int j=0;j<vtstockconfigsku.size();j++)
				{
					try{
						Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
						ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
						stockconfigsku.getMapData(htstockconfigsku);
						sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+stockconfigsku.getItemid()+"'";
						Hashtable htstockconfig=SQLHelper.oneRowSelect(conn, sql);
						ECS_StockConfig stockconfig=new ECS_StockConfig();
						stockconfig.getMapData(htstockconfigsku);
						
						if (stockconfig.getIsneedsyn()==0)
						{
							Log.info("配置不需要同步库存,SKU:"+sku);
							continue;  //不需要同步
						}
						
						int newqty=0;

						//如果原库存加上本次需同步的库存小于等于警戒库存,则更新库存为零
						if ((stockconfigsku.getStockcount()+qty)<=stockconfig.getAlarmqty())
						{								
							newqty=0;
						}
						else
						{
							newqty=qty+stockconfigsku.getStockcount();
						}

						StockUtil.updateStock(dao,stockconfigsku,stockconfig,Params.url,Params.appKey,Params.secretKey,newqty);
					}catch(Exception ex){
						if(conn!=null && !conn.getAutoCommit()) conn.rollback();
						Log.error(jobName, ex.getMessage());
					}
					
			
				}
				
				
				
			}catch(Exception je)
			{
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				//StockManager.bakSynReduceStore(jobName, conn, Params.tradecontactid, tid, sku);	
				Log.error(jobName,"更新库巴库存失败,单号:"+tid+" SKU:" +sku+" "+je.getMessage());
			}
		}
		
		if(vtinfo.size()>0){
			//更新处理标志
			StringBuilder sqlstr = new StringBuilder().append("update eco_synreducestore set synflag=1,syntime='").append(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)).append("' where id in(");
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
	}
	
	public String toString()
	{
		return jobName + " " + (is_updating ? "[updating]" : "[waiting]");
	}

}