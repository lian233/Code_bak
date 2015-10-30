package com.wofu.ecommerce.rke;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
public class UpdateStock extends Thread{
	private static String jobname = "更新麦斯卡经销库存作业";
	private static int orgid=0;
	private boolean is_updating=false;
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection connection = null;
			is_updating = true;
			try {					
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				//仓库数
				String sql ="select distinct c.orgcode,b.synstockrate from ecs_tradecontactorgcontrast a,ecs_rationconfig b ,"
					+"ecs_org c where A.tradecontactid="+Params.tradecontactid+"  and a.orgid=b.shoporgid and b.rationorgid=c.orgid ";
				Vector orgs = SQLHelper.multiRowSelect(connection, sql);
				updateStock(connection,orgs);
		
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_updating = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.rke.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	private void updateStock(Connection conn,Vector orgs) throws Exception
	{
		JSONArray arr = null;
		int n=0;
		String sql="";
		if(orgid==0){
			sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
			orgid=SQLHelper.intSelect(conn, sql);
		}
		
		ECSDao dao=new ECSDao(conn);
		
		sql="select id,tid,sku,qty from eco_synreducestore "
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
				StockManager.bakSynReduceStore(jobname, conn, Params.tradecontactid, tid, sku);	
				sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and sku='"+sku+"'";
				
				Vector vtstockconfigsku=SQLHelper.multiRowSelect(conn, sql);
				
				for (int j=0;j<vtstockconfigsku.size();j++)
				{
					
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
						continue;  //不需要同步
					}
					arr = new JSONArray();
					for(Iterator it = orgs.iterator();it.hasNext();){
						Hashtable tempResult = (Hashtable)it.next();
						int newqty=0;
						//如果原库存加上本次需同步的库存小于等于警戒库存,则更新库存为零
						if ((stockconfigsku.getStockcount()+qty)<=stockconfig.getAlarmqty())
						{								
							newqty=-stockconfigsku.getStockcount();
						}
						else
						{
							newqty=qty;
						}
						newqty=StockManager.getTradeContactJxUseableStock(conn, tempResult.get("orgcode").toString(),Float.valueOf(tempResult.get("synstockrate").toString()).floatValue(),stockconfigsku.getSku());
						if(newqty<0) newqty=0;
						arr.put(new JSONObject().put(tempResult.get("orgcode").toString(), qty));
					}
					StockUtils.updateStock(Params.url, Params.ver, stockconfigsku, arr.toString());
				}
				
			}catch(Exception je)
			{
				
				Log.error(jobname,"更新麦斯卡经销库存失败,单号:"+tid+" SKU:" +sku+" "+je.getMessage());
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
	}
	
	public String toString()
	{
		return jobname + " " + (is_updating ? "[updating]" : "[waiting]");
	}

}
