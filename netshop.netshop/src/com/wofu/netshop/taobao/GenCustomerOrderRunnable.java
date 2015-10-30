package com.wofu.netshop.taobao;
/**
 * �ӿڶ������ɿͻ������߳���
 */
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class GenCustomerOrderRunnable implements Runnable{
	private String jobName="�ӿڶ������ɿͻ�������ҵ";
	private CountDownLatch watch;
	private String username="";
	public GenCustomerOrderRunnable(CountDownLatch watch,String username){
		this.watch=watch;
		this.username=username;
	}
	public void run() {
		Connection connection = null;
		try {
			connection = PoolHelper.getInstance().getConnection("shop");
			Vector vts=IntfUtils.getDownNotes(connection, Params.tradecontactid, "1");
			boolean is_success=false;
			Log.info(username,"���ι�Ҫ������Ա��ӿڶ���������ʱ��������Ϊ: "+vts.size(),null);
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
					
					//��ʼ����
					connection.setAutoCommit(false);
					//���ɿͻ�����
					if("1".equals(Params.isEc))
						is_success=OrderManager.genEcCustomerOrder(connection, sheetid);
					else
						is_success=OrderManager.genCustomerOrder(connection, sheetid,Params.isDelay,Params.tableType);
					
					if (is_success)
					{
						//���ݽӿ�����
						IntfUtils.backupDownNote(connection, "yongjun",sheetid, "1");
						
						//�ύ����
						connection.commit();
						connection.setAutoCommit(true);
								
					
						Log.info(username,"���ɿͻ������ɹ�,�ӿڵ��š�" + sheetid + "��",null);
					}
					else
					{
						try {
							if (connection != null && !connection.getAutoCommit())
								connection.rollback();
						} catch (Exception e1) {
							Log.error(username,jobName+" �ع�����ʧ��",null);
						}
						Log.info(username,"���ɿͻ�����ʧ��,�ӿڵ��š�" + sheetid + "��",null);
					}
				}catch(Exception ex){
					try {
						if (connection != null && !connection.getAutoCommit())
							connection.rollback();
					} catch (Exception e1) {
						Log.error(username,jobName+" �ع�����ʧ��",null);
					}
					Log.error(username,jobName+" "+ex.getMessage(),null);
					Log.info(username,"���ɿͻ�����ʧ��,�ӿڵ��š�" + sheetid + "��,��ϸ��Ϣ�� "+ex.getMessage(),null);
				}
				
			}
			
		} catch (Exception e) {
			try {
				if (connection != null && !connection.getAutoCommit())
					connection.rollback();
			} catch (Exception e1) {
				Log.error(username,jobName+" �ع�����ʧ��",null);
			}
			Log.error(username,jobName+" "+Log.getErrorMessage(e),null);
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				Log.error(username,jobName+" �ر����ݿ�����ʧ��");
			}
			watch.countDown();
		}
		
	}

}
