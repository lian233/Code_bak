package com.wofu.ecommerce.oauthpaipai;
import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
public class GenCustomerOrder extends Thread{

	private static String jobName = "���Ķ������ɿͻ�������ҵ";
	private boolean is_gening=false;
	public void run() {
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {		
			Connection connection = null;
			is_gening = true;
			try {
				//�ı侲̬ʱ��
				PaiPai.setCurrentDate_genOrder(new Date());
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.oauthpaipai.Params.dbname);	
				Vector vts=IntfUtils.getDownNotes(connection, Params.tradecontactid, "1");
				Log.info("���νӿڶ���������ʱ��������Ϊ:��"+vts.size());
				for (int i=0;i<vts.size();i++) {
					try{
						Hashtable hts=(Hashtable) vts.get(i);
						String sheetid=hts.get("sheetid").toString();
						
						//��ʼ����
						connection.setAutoCommit(false);
						//���ɿͻ�����
						boolean is_success=OrderManager.genCustomerOrder(connection, sheetid,Params.isDelay,Params.tableType);
						
										
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
								Log.error(jobName, "�ع�����ʧ��");
							}
							Log.info("���ɿͻ�����ʧ��,�ӿڵ��š�" + sheetid + "��");
						}
					}catch(Exception ex){
						try {
							if (connection != null && !connection.getAutoCommit())
								connection.rollback();
						} catch (Exception e1) {
							Log.error(jobName, "�ع�����ʧ��");
						}
							
						Log.error(jobName, ex.getMessage());
					}
					
				}
				Log.info("���νӿڶ���������ʱ�����������");
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_gening = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.oauthpaipai.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	

	public String toString()
	{
		return jobName + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}
