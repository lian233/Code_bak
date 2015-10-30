package com.wofu.ecommerce.taobao;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class GenCustomerOrder extends Thread{
	private static String jobname = "�Ա��������ɿͻ�������ҵ";
	
	private boolean is_gening=false;
	
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_gening = true;
			try {
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.taobao.Params.dbname);	
				Vector vts=IntfUtils.getDownNotes(connection, Params.tradecontactid, "1");
				boolean is_success=false;
				Log.info("���ι�Ҫ������Ա��ӿڶ���������ʱ��������Ϊ: "+vts.size());
				if(vts.size()>0 && 1==Params.tableType){
					//ɾ���Ѿ����ڵĽӿ���ʱ��
					String sql = "if not OBJECT_ID('tempdb..#NS_CustomerOrder0') is null drop table #NS_CustomerOrder0";
					SQLHelper.executeSQL(connection, sql);
					sql = "if not OBJECT_ID('tempdb..#NS_Orderitem0') is null drop table #NS_Orderitem0";
					SQLHelper.executeSQL(connection, sql);
					sql = "if not OBJECT_ID('tempdb..#it_downnote0') is null drop table #it_downnote0";
					SQLHelper.executeSQL(connection, sql);
					//�����ӿ���ʱ��
					sql = "select * into #it_downnote0 from it_downnote where sheettype=1 and sender='"+Params.tradecontactid+"'";
					SQLHelper.executeSQL(connection,sql);
					sql = "select a.* into #ns_customerorder0 from ns_customerorder a(nolock),#it_downnote0 b where a.sheetid=b.sheetid";
					SQLHelper.executeSQL(connection, sql);
					sql = "select a.* into #ns_orderitem0 from ns_orderitem a(nolock),#it_downnote0 b where a.sheetid=b.sheetid";
					SQLHelper.executeSQL(connection, sql);
				}
				
				for (int i=0;i<vts.size();i++) {
					String sheetid="";
					try{
						Hashtable hts=(Hashtable) vts.get(i);
						sheetid=hts.get("sheetid").toString();
						long currentTime = System.currentTimeMillis();
						//��ʼ����
						connection.setAutoCommit(false);
						//���ɿͻ�����
						if(Params.isEc)
							is_success=OrderManager.genEcCustomerOrder(connection, sheetid);
						else if(Params.isDistrictMode){
							is_success=OrderManager.genCustomerOrder(connection, sheetid,0,"C",0);	
						}
						else
							is_success=OrderManager.genCustomerOrder(connection, sheetid,Params.isDelay,Params.tableType);
						
						if (is_success)
						{
							//���ݽӿ�����
							IntfUtils.backupDownNote(connection, "yongjun",sheetid, "1");
							
							//�ύ����
							connection.commit();
							connection.setAutoCommit(true);
									
						
							Log.info("���ɿͻ������ɹ�,�ӿڵ��š�" + sheetid + "��");
						}
						else
						{
							try {
								if (connection != null && !connection.getAutoCommit())
									connection.rollback();
							} catch (Exception e1) {
								Log.error(jobname, "�ع�����ʧ��");
							}
							Log.info("���ɿͻ�����ʧ��,�ӿڵ��š�" + sheetid + "��");
						}
						long needTime = System.currentTimeMillis()-currentTime;
						Log.info("����һ�����������ʱ��Ϊ: "+needTime);
//						if(needTime>300){
//							long starttime= System.currentTimeMillis();
//							while(System.currentTimeMillis()-starttime<30*1000L){
//								Thread.sleep(100L);
//							}
//						}
						//��Ӽ��
						//Thread.sleep(2000L);
					}catch(Exception ex){
						try {
							if (connection != null && !connection.getAutoCommit())
								connection.rollback();
						} catch (Exception e1) {
							Log.error(jobname, "�ع�����ʧ��");
						}
						Log.error(jobname, ex.getMessage());
						Log.info("���ɿͻ�����ʧ��,�ӿڵ��š�" + sheetid + "��,��ϸ��Ϣ�� "+ex.getMessage());
					}
					
				}
				
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_gening = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.taobao.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	public String toString()
	{
		return jobname + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}
