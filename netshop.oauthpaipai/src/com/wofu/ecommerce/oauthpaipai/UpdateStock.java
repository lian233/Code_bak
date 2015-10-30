package com.wofu.ecommerce.oauthpaipai;
import java.sql.Connection;
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
	private static String jobname = "�������Ŀ����ҵ";
	private static int orgid=0;
	private boolean is_updating=false;
	public UpdateStock() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
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
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	private void updateStock(Connection conn) throws Exception
	{
		String sql="";
		if(orgid==0){
			sql ="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
			int orgid=SQLHelper.intSelect(conn, sql);
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

						StockUtils.updateStock(dao,Params.spid,Params.secretkey,Params.token,
								Params.uid,Params.encoding,stockconfig,stockconfigsku,newqty);
					}catch(Exception ex){
						if(conn!=null && !conn.getAutoCommit())
							conn.rollback();
						Log.warn(jobname+", "+ex.getMessage());
						continue;
					}
					
					
			
				}
				
				
			}catch(Exception je)
			{
				if(conn!=null && !conn.getAutoCommit())
					conn.rollback();
				//StockManager.bakSynReduceStore(jobname, conn, Params.tradecontactid, tid, sku);	
				Log.error(jobname,"�������Ŀ��ʧ��,����:"+tid+" SKU:" +sku+" "+je.getMessage());
			}
		}
		if(vtinfo.size()>0){
			//���´����־
			StringBuilder sqlstr = new StringBuilder().append("update eco_synreducestore set synflag=1,syntime='").append(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)).append("' where id in(");
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
