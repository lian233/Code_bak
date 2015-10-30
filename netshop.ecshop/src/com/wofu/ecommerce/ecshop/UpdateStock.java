package com.wofu.ecommerce.ecshop;


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
	
	private static String jobName = "�������������ҵ";
	private static int orgid = 0;
	
	private boolean is_updating=false;
	
	public void run() {
		Log.info(jobName, "����[" + jobName + "]ģ��");
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
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_updating = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
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
					if(vtstockconfigsku.size()>0){     //��������Ʒ
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
									Log.info("���ò���Ҫͬ�����,SKU:"+sku);
									StockManager.bakSynReduceStore(jobName, conn, Params.tradecontactid, tid, sku);	
									continue;  //����Ҫͬ��
								}
								
								int newqty=0;

								//���ԭ�����ϱ�����ͬ���Ŀ��С�ڵ��ھ�����,����¿��Ϊ��
								if ((stockconfigsku.getStockcount()+qty)<=stockconfig.getAlarmqty())
								{								
									newqty=-stockconfigsku.getStockcount();
								}
								else
								{
									newqty=qty+stockconfigsku.getStockcount();
								}

								if (newqty<0) newqty=0;	
								Log.info("����sku���: ,"+"��Ʒ���룺"+stockconfig.getItemid()+"����:��"+stockconfig.getItemcode());
								StockUtils.updateSkuStock(jobName,dao,orgid,Params.url,stockconfig,stockconfigsku,newqty,false);
							}catch(Exception ex){
								if(dao!=null && !dao.getConnection().getAutoCommit())
									dao.getConnection().rollback();
								Log.error(jobName, ex.getMessage());
								continue;
							}
							
						}
				
					}else{   //û������Ʒ
						Log.info("����Ʒ: --");
						sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemcode='"+sku+"'";
						Hashtable htStockconfig =  SQLHelper.oneRowSelect(conn, sql);
						ECS_StockConfig stockconfig = new ECS_StockConfig();
						stockconfig.getMapData(htStockconfig);
						if (stockconfig.getIsneedsyn()==0)
						{
							Log.info("���ò���Ҫͬ�����,SKU:"+sku);
							continue;  //����Ҫͬ��
						}
						int newqty=0;

						//���ԭ�����ϱ�����ͬ���Ŀ��С�ڵ��ھ�����,����¿��Ϊ��
						if ((stockconfig.getStockcount()+qty)<=stockconfig.getAlarmqty())
						{								
							newqty=-stockconfig.getStockcount();
						}
						else
						{
							newqty=qty+stockconfig.getStockcount();
						}

						if (newqty<0) newqty=0;	

						StockUtils.updateItemStock(jobName,dao,orgid,Params.url,stockconfig,newqty);
						
					}
				Log.info("�������: "+sku);
			}catch(Exception je)
			{	if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				//StockManager.bakSynReduceStore(jobName, conn, Params.tradecontactid, tid, sku);	
				Log.error(jobName,"�����������ʧ��,����:"+tid+" SKU:" +sku+" "+je.getMessage());
				continue;
			}
		}
		if(vtinfo.size()>0){
			//���´����־
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
		Log.info("�̸߳���sku�����ϣ�");
	}

	
	public String toString()
	{
		return jobName + " " + (is_updating ? "[updating]" : "[waiting]");
	}

}
