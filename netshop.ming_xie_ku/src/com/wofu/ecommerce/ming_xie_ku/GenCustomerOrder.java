package com.wofu.ecommerce.ming_xie_ku;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.log.Log;

public class GenCustomerOrder extends Thread
{
	private static String jobname = "��Ь�ⶩ�����ɿͻ�������ҵ";
	
	private boolean is_gening=false;


	@Override
	public void run() 
	{
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do{
			Connection connection = null;
			is_gening = true;
			try {
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.ming_xie_ku.Params.dbname);	
				Vector vts=IntfUtils.getDownNotes(connection, Params.tradecontactid, "1");
				for (int i=0;i<vts.size();i++) {
					try{
						Hashtable hts=(Hashtable) vts.get(i);
						String sheetid=hts.get("sheetid").toString();
						
						//��ʼ����
						connection.setAutoCommit(false);
						//���ɿͻ�����
						//boolean is_success=OrderManager.genCustomerOrder(connection, sheetid,Params.isDelay,Params.tableType);				
						//���ɿͻ�����  //	A������
						//boolean is_success=OrderManager.genCustomerOrder(connection, sheetid,1,"A");	
						boolean is_success=OrderManager.genCustomerOrder(connection, sheetid, 1, "C",0);
						//boolean is_success=true;
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
					}catch(Exception ex){
						Log.error(jobname, ex.getMessage());
						try {
							if (connection != null && !connection.getAutoCommit())
								connection.rollback();
						} catch (Exception e1) {
							Log.error(jobname, "�ع�����ʧ��");
						}
						continue;
					}
					
					
				}
			} catch (Throwable e) {
				e.printStackTrace();
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			}finally {
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
			while (System.currentTimeMillis() - startwaittime < (com.wofu.ecommerce.ming_xie_ku.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		}while(true);
//		super.run();
	}
	
	@Override
	public String toString()
	{
		return jobname + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}
