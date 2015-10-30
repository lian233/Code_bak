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
	private static String jobname = "������˹�����������ҵ";
	private static int orgid=0;
	private boolean is_updating=false;
	
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_updating = true;
			try {					
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				//�ֿ���
				String sql ="select distinct c.orgcode,b.synstockrate from ecs_tradecontactorgcontrast a,ecs_rationconfig b ,"
					+"ecs_org c where A.tradecontactid="+Params.tradecontactid+"  and a.orgid=b.shoporgid and b.rationorgid=c.orgid ";
				Vector orgs = SQLHelper.multiRowSelect(connection, sql);
				updateStock(connection,orgs);
		
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_updating = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.rke.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
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
						Log.info("���ò���Ҫͬ�����,SKU:"+sku);
						continue;  //����Ҫͬ��
					}
					arr = new JSONArray();
					for(Iterator it = orgs.iterator();it.hasNext();){
						Hashtable tempResult = (Hashtable)it.next();
						int newqty=0;
						//���ԭ�����ϱ�����ͬ���Ŀ��С�ڵ��ھ�����,����¿��Ϊ��
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
				
				Log.error(jobname,"������˹���������ʧ��,����:"+tid+" SKU:" +sku+" "+je.getMessage());
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
	}
	
	public String toString()
	{
		return jobname + " " + (is_updating ? "[updating]" : "[waiting]");
	}

}
