package com.wofu.ecommerce.dangdang;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.wofu.ecommerce.dangdang.Params;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;

public class UpdateStock extends Thread{
	
	private static String jobname = "更新当当库存作业";
	private static int orgid=0;
	private static String tradecontactid = Params.tradecontactid ;
	
	private boolean is_updating=false;
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection connection = null;
			is_updating = true;
			try {					
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.dangdang.Params.dbname);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.dangdang.Params.waittime * 1000))		
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
			sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid='"+tradecontactid+"'";
			orgid=SQLHelper.intSelect(conn, sql);
		}
		
		
	/*	//备份掉未配置的
		sql="insert into ECO_SynReduceStorebak select * from ECO_SynReduceStore "
			+"where  tradecontactid='"+tradecontactid+"' "
			+" and sku not in(select sku from ecs_stockconfigsku where orgid="+orgid+") ";
		SQLHelper.executeSQL(conn,sql);
		
		sql="delete from ECO_SynReduceStore "
			+"where  tradecontactid='"+tradecontactid+"' "
			+" and sku not in(select sku from ecs_stockconfigsku where orgid="+orgid+") ";
		SQLHelper.executeSQL(conn,sql);*/
		//同步前十个sku
		sql=new StringBuilder().append("select top 50 id,tid,sku, qty from ECO_SynReduceStore ")
			.append("where tradecontactid='")
			.append(tradecontactid)
			.append("' and synflag=0 and sku is not null and sku<>'' ").toString();
		Vector vtinfo=SQLHelper.multiRowSelect(conn, sql);
		
		if (vtinfo.size()>0)
		{
			StringBuffer updateItemsXML=new StringBuffer();
			updateItemsXML.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
			updateItemsXML.append("<request><functionID>dangdang.items.stock.update</functionID>");
			updateItemsXML.append("<time>"+Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)+"</time>");
			updateItemsXML.append("<ItemsList>");
			
			boolean issyn=false;
			for(int i=0;i<vtinfo.size();i++)
			{
				Hashtable htinfo=(Hashtable) vtinfo.get(i);
				String sku=htinfo.get("sku").toString();
				//Date createtime=Formatter.parseDate(htinfo.get("createtime").toString(),Formatter.DATE_TIME_MS_FORMAT);
				
				int qty = Integer.parseInt(htinfo.get("qty").toString()) ;
				
				//如果同步数量为0不处理
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
					updateItemsXML.append("<ItemUpadteInfo>");							
					updateItemsXML.append("<outerItemID>"+sku+"</outerItemID>");
					updateItemsXML.append("<stockCount>"+newqty+"</stockCount>");
					updateItemsXML.append("</ItemUpadteInfo>");	
				}
				
			}
				
			updateItemsXML.append("</ItemsList>");
			updateItemsXML.append("</request>");

			if (issyn)
				StockUtils.batchUpdateStock(conn,orgid,Params.url,updateItemsXML.toString(),Params.encoding,Params.session,Params.app_key,Params.app_Secret);
			Log.info("uuu");
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
		return jobname + " " + (is_updating ? "[updating]" : "[waiting]");
	}
}