package com.wofu.netshop.jingdong;

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

public class UpdateStockRunnable implements Runnable{
	private String jobName="更新京东增量库存作业";
	private CountDownLatch watch;
	private String username="";
	private int orgid;
	public UpdateStockRunnable(CountDownLatch watch,String username,int orgid){
		this.watch=watch;
		this.username=username;
		this.orgid=orgid;
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
		String sql="";
		
		ECSDao dao=new ECSDao(conn);
		
		sql="select id,tid,sku,qty from eco_synreducestore (nolock) "
			+"where tradecontactid='"+Params.tradecontactid+"' "
			+"and synflag=0 and sku is not null and sku<>''";
		Vector vtinfo=SQLHelper.multiRowSelect(conn, sql);
		
		for(int i=0;i<vtinfo.size();i++)
		{
			String tid="";
			String sku="";
			try{
				Hashtable htinfo=(Hashtable) vtinfo.get(i);
				
				tid=htinfo.get("tid").toString();
				sku=htinfo.get("sku").toString();
				int qty=Integer.valueOf(htinfo.get("qty").toString()).intValue();

					sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and sku='"+sku+"'";
					
					Vector vtstockconfigsku=SQLHelper.multiRowSelect(conn, sql);
					
					for (int j=0;j<vtstockconfigsku.size();j++)
					{	
						ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
						try{
							Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
							
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


							StockUtils.updateStock(dao,Params.SERVER_URL,Params.token,Params.appKey,Params.appSecret,stockconfig,stockconfigsku,newqty);
						}catch(Exception ex){
							if(conn!=null && !conn.getAutoCommit()) conn.rollback();
							Log.error(jobName,"更新淘宝库存失败,单号:"+tid+" SKU:" +stockconfigsku+" "+ex.getMessage());
						}
					}
					
					
					
			}catch(Exception ex){
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.error(jobName,"更新淘宝库存失败,单号:"+tid+" SKU:" +sku+" "+ex.getMessage());
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

}
