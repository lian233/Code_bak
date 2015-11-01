package com.wofu.ecommerce.huasheng;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
/**
 * 
 * //�����¶���
   //�����˻���
 *
 */
public class GenCustomerOrder extends Thread{
	private static String jobName = "�羳�̳�(����API)�������ɿͻ�������ҵ";
	
	private boolean is_gening=false;
	
	public void run() {
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {
			Date nowtime = new Date();
			if(Params.startTime.getTime() <= nowtime.getTime())
			{//���ϻ򳬹�ָ��������ʱ��
				Connection connection = null;
				is_gening = true;
				try 
				{
					connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.weipinhui.Params.dbname);	
					//���ɿ羳�̳�(����API)�¶���
					doGenCustomerOrders(connection) ;
				} 
				catch (Exception e) {
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
				Log.info(jobName + "�´�ִ�еȴ�ʱ��:" + Params.waittime + "��");
				long startwaittime = System.currentTimeMillis();
				while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.weipinhui.Params.waittime * 1000))
					try {
						sleep(1000L);
					} catch (Exception e) {
						Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
					}
			}
			else
			{//�ȴ�����
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
			}
		} while (true);
	}
	
	//���ɿ羳�̳�(����API)�¶���
	public static void doGenCustomerOrders(Connection connection)
	{
		try 
		{
			Log.info("�羳�̳�(����API)�������ɿͻ�������ҵ��ʼ");
			Vector vts=IntfUtils.getDownNotes(connection, Params.tradecontactid, "1");
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
						Log.error(jobName,"���ɿͻ�����ʧ��,�ӿڵ��š�" + sheetid + "��");
					}
				}catch(Exception ex){
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
					Log.error(jobName,ex.getMessage());
				}
				
			}
		
		} catch (Exception e) 
		{
			Log.error(jobName, "���ɿͻ�����ʧ��,"+e.getMessage()) ;
			e.printStackTrace() ;
		}
		Log.info("���ο羳�̳�(����API)�������ɿͻ�������ҵ������ϣ�");
	}
	
	//���ɿ羳�̳�(����API)�˻���		�ݲ�ʹ��
	public static void doGenCustomerRefundOrders(Connection connection)
	{
		try 
		{	
			Log.info("�羳�̳�(����API)�˻��������ɿͻ��˻�������ҵ��ʼ");
			ArrayList<String> slist=new ArrayList<String>();
			String sql="";
			sql="select sheetid from it_downnote with(nolock) where sender='"+Params.tradecontactid+"' and sheettype=2";
			Vector vt=SQLHelper.multiRowSelect(connection, sql);
			for(int i=0;i<vt.size();i++)
			{
				Hashtable ht=(Hashtable) vt.get(i);
				slist.add(ht.get("sheetid").toString());
			}
			
			for (Iterator it = slist.iterator(); it.hasNext();) {
				try{
					String sheetid = (String) it.next();
					sql="declare @ret int;  execute  @ret = eco_GetRefund '"+sheetid+"';select @ret ret;";
					try
					{
						connection.setAutoCommit(false);
						int ret=SQLHelper.intSelect(connection, sql);
				
						if (ret==0)
						{
							sql = "declare @err int ;exec @Err = IP_DownBak '" + sheetid + "' ,2,'yongjun';select @err;";
							int err=SQLHelper.intSelect(connection, sql);
							if (err==-1)
							{							
								throw new JException("��������ʧ��!"+sql);
							}
							Log.info("���ɿͻ��˻��ɹ�,�ӿڵ��š�" + sheetid + "��");
						}
						else if (ret==-1)
						{
							Log.info("���ɿͻ��˻�ʧ��!�ӿڵ��š�" + sheetid + "��");
							throw new JException(sql);
						}
						connection.commit();
						connection.setAutoCommit(true);		
							
					}
					catch (SQLException e1)
					{		
						Log.info("���ɿͻ��˻�ʧ��!�ӿڵ��š�" + sheetid + "����������Ϣ��"+e1.getMessage());
						if (!connection.getAutoCommit())
							try
							{
								connection.rollback();
							}
							catch (Exception e2) { }
						try
						{
							connection.setAutoCommit(true);
						}
						catch (Exception e3) { }
						throw e1;
					}
				}catch(Exception ex){
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
					Log.error(jobName,ex.getMessage());
				}
							
			}	
		
			
		} catch (Exception e) 
		{
			e.printStackTrace() ;
		}
		Log.info("�羳�̳�(����API)�˻��������ɿͻ��˻�������ҵ��ϣ�");
	}
	
	public String toString()
	{
		return jobName + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}
