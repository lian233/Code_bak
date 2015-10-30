package com.wofu.ecommerce.amazon;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import java.util.Vector;

import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;

public class UpdateStock extends Thread{
	
	private static String jobname = "更新亚马逊库存作业";
	private static int orgid=0;
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
	
	private boolean is_updating=false;
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection connection = null;
			is_updating = true;
			try {					
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				doUpdateStock(jobname,connection);
			
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
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	private void doUpdateStock(String jobname,Connection conn) throws Exception
	{
		
		String sql = "" ;
		if(orgid==0){
			sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid='"+Params.tradecontactid+"'";
			orgid=SQLHelper.intSelect(conn, sql);
		}
		
		/*//备份掉未配置的
		sql="insert into ECO_SynReduceStorebak select * from ECO_SynReduceStore "
			+"where  tradecontactid='"+Params.tradecontactid+"' "
			+" and sku not in(select sku from ecs_stockconfig where orgid="+orgid+") ";
		SQLHelper.executeSQL(conn,sql);
		
		sql="delete from ECO_SynReduceStore "
			+"where  tradecontactid='"+Params.tradecontactid+"' "
			+" and sku not in(select sku from ecs_stockconfig where orgid="+orgid+") ";
		SQLHelper.executeSQL(conn,sql);*/
		
		sql=new StringBuilder().append("select id,tid,sku,qty from eco_synreducestore(nolock) where synflag=0 ")
			.append("and tradecontactid='").append(Params.tradecontactid)
			.append("' and sku<>'' and sku is not null").toString();
		Vector vtinfo=SQLHelper.multiRowSelect(conn, sql);
		
		Log.info("更新库存开始");
		if (vtinfo.size()>0)
		{
			//Date maxCreateTime=Formatter.parseDate("1990-01-01 00:00:00.000",Formatter.DATE_TIME_MS_FORMAT);
			
			boolean issyn=false;
			
			ECSDao dao=new ECSDao(conn);
							
			Vector inventoryitems=new Vector();
			
			for(int i=0;i<vtinfo.size();i++)
			{
				try{
					Hashtable htinfo=(Hashtable) vtinfo.get(i);
					String sku=htinfo.get("sku").toString();
					
					int qty = Integer.parseInt(htinfo.get("qty").toString()) ;
					
		
					if(qty==0)
					{
						Log.info(jobname,"SKU【"+ sku +"】,调整数量为零,忽略!");
						continue ;
					}

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

					
						issyn=true;
						Hashtable inventoryitem=new Hashtable();
						inventoryitem.put("sku",sku);
						inventoryitem.put("qty", String.valueOf(newqty));
						
						inventoryitems.add(inventoryitem);
					}
					
				
				}catch(Exception ex){
					try {
						if (conn != null && !conn.getAutoCommit())
							conn.rollback();
					} catch (Exception e1) {
						Log.error(jobname, "回滚事务失败");
					}
					Log.error("105", jobname, Log.getErrorMessage(ex));
				}
				
			}

			if (issyn)
				StockUtils.updateStock(dao,orgid,Params.serviceurl, Params.accesskeyid, Params.secretaccesskey, 
					Params.applicationname, Params.applicationversion, 
					Params.sellerid, Params.marketplaceid, inventoryitems);
			
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
		Log.info("更新库存结束");
		
	}

	public String toString()
	{
		return jobname + " " + (is_updating ? "[updating]" : "[waiting]");
	}
}