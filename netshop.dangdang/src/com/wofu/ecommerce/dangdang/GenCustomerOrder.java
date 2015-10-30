package com.wofu.ecommerce.dangdang;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.wofu.ecommerce.dangdang.Params;
import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
/**
 * 
 * //���ɵ����¶���
   //���ɵ����˻���
 *
 */
public class GenCustomerOrder extends Thread{
	private static String jobName = "�����������ɿͻ�������ҵ";
	
	private boolean is_gening=false;
	
	public void run() {
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {
			Connection connection = null;
			is_gening = true;
			try 
			{
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.dangdang.Params.dbname);	
				//���ɵ����¶���
				doGenCustomerOrders(connection) ;
				//���ɵ����˻���
				if(Params.isgenorderRet)
				doGenCustomerRefundOrders(connection) ;
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
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.dangdang.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	//���ɵ����¶���
	public static void doGenCustomerOrders(Connection connection)
	{
		try 
		{
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
						Log.error(jobName,"���ɿͻ�����ʧ��,�ӿڵ��š�" + sheetid + "��");
					}
				}catch(Exception ex){
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
					Log.error(jobName, ex.getMessage());
				}
				
				
			}
			Log.info("���νӿڶ���������ʱ�����������");
		
		} catch (Exception e) 
		{
			Log.error(jobName, "���ɿͻ�����ʧ��,"+e.getMessage()) ;
			e.printStackTrace() ;
		}
	}
	//���ɵ����˻���
	public static void doGenCustomerRefundOrders(Connection connection)
	{
		try 
		{
			ArrayList<String> slist=new ArrayList<String>();
			String sql="";
			sql="select sheetid from it_downnote with(nolock) where sender='"+Params.tradecontactid+"' and sheettype=2";
			Vector vt=SQLHelper.multiRowSelect(connection, sql);
			Log.info("���νӿڶ���������ʱ�˻���������Ϊ:��"+vt.size());
			for(int i=0;i<vt.size();i++)
			{
				Hashtable ht=(Hashtable) vt.get(i);
				slist.add(ht.get("sheetid").toString());
			}
			
			for (Iterator it = slist.iterator(); it.hasNext();) {
				String sheetid = (String) it.next();
				
//				sql="select tid from ns_refund with(nolock) where sheetid='"+sheetid+"'";
//				String refsheetid=SQLHelper.strSelect(connection, sql);
				
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
				catch (Exception e1)
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
					//throw e1;
				}			
			}	
			Log.info("���νӿڶ���������ʱ�˻������������");
			
		} catch (Exception e) 
		{
			e.printStackTrace() ;
		}
	}
	
	public String toString()
	{
		return jobName + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}
