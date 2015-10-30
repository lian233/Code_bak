package com.wofu.ecommerce.vjia;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.ecommerce.vjia.Params;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.business.stock.StockManager;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;

import com.wofu.common.tools.util.log.Log;


public class UpdateStock extends Thread{
	
	private static String jobname = "����vjia�����ҵ";
	private static int orgid = 0;
	
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;

			try {
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.vjia.Params.dbname);
				updateStock(connection);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {

				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.vjia.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	/**
	 * 
	 * @param conn
	 * @param vtinfo
	 * @throws Exception 
	 */
	private void updateStock(Connection conn) throws Exception
	{
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
			{	Thread.sleep(10000L);	
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
						Log.info("���ò���Ҫͬ�����,SKU:"+sku);
						continue;  //����Ҫͬ��
					}
					
					int newqty=0;

					//���ԭ�����ϱ�����ͬ���Ŀ��С�ڵ��ھ�����,����¿��Ϊ��
					if ((stockconfigsku.getStockcount()+qty)<=stockconfig.getAlarmqty())
					{								
						newqty=0;
					}
					else
					{
						newqty=qty+stockconfigsku.getStockcount();
					}


					StockUtils.updateStock(dao,Params.uri,Params.wsurl,Params.supplierid,
							Params.suppliersign,Params.swssupplierid,Params.strkey,Params.striv,stockconfig,stockconfigsku,newqty);
			
				}
				
				
				
			}catch(Exception je)
			{
				
				//StockManager.bakSynReduceStore(jobname, conn, Params.tradecontactid, tid, sku);	
				Log.error(jobname,"����vjia���ʧ��,����:"+tid+" SKU:" +sku+" "+je.getMessage());
			}
		}
		if(vtinfo.size()>0){
			//���´�����־
			StringBuilder sqlstr = new StringBuilder().append("update eco_synreducestore set synflag=1 where id in(");
			for(int i=0;i<vtinfo.size();i++)
			{
				if(i==vtinfo.size()-1){
					sqlstr.append(((Hashtable)(vtinfo.get(i))).get("id")).append(")");
				}else{
					sqlstr.append(((Hashtable)(vtinfo.get(i))).get("id")).append(",");
				}
			}
			Log.info("����ͬ������¼��־sql: "+sqlstr.toString());
			SQLHelper.executeSQL(conn,sqlstr.toString());
			Log.info("���Ŀ��ͬ����¼��־�ɹ�");
		}
	}

	public String toString()
	{
		return jobname;
	}
	
}