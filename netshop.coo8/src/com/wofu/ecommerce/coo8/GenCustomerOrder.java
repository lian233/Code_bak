package com.wofu.ecommerce.coo8;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.wofu.business.intf.IntfUtils;
import com.wofu.business.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class GenCustomerOrder extends Thread{
	private static String jobName = "��Ͷ������ɿͻ�������ҵ";
	
	private boolean is_gening=false;
	
	public void run() {
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do 
		{
			Connection connection = null;
			is_gening = true;
			try
			{
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	
				//���ɿ���¿ͻ�����
				if(Params.isgenorder)
				doGenCustomerOrder(connection) ;
				//���ɿ���˻�����
				if(Params.isgenorderRet)
				dogenReturnCustomerOrder(connection) ;
			}
			catch (Exception e)
			{	try{
					if(connection!=null && !connection.getAutoCommit()) connection.rollback();
				}catch(Exception ex){
					Log.error(jobName, ex.getMessage());
				}
				
				e.printStackTrace() ;
			}
			finally {
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
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))
				try 
				{
					sleep(1000L);
				} 
				catch (Exception e) 
				{
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		}while (true);
	}
	//���ɿ���µ�
	private void doGenCustomerOrder(Connection connection)
	{
		try 
		{
			Vector vts=IntfUtils.getDownNotes(connection, String.valueOf(Params.tradecontactid), "1");
			for (int i=0;i<vts.size();i++)
			{	
				try{
					Hashtable hts=(Hashtable) vts.get(i);
					String sheetid=hts.get("sheetid").toString();
					// ��ʼ����
					connection.setAutoCommit(false);
					// ���ɿͻ�����
					boolean is_success=OrderManager.genCustomerOrder(connection, sheetid,Params.isDelay,Params.tableType);
					if (is_success)
					{
						// ���ݽӿ�����
						IntfUtils.backupDownNote(connection, "yongjun",sheetid, "1");
						// �ύ����
						connection.commit();
						connection.setAutoCommit(true);
						Log.info("���ɿͻ������ɹ�,�ӿڵ��š�" + sheetid + "��");
					}
					else
					{
						try
						{
							if (connection != null && !connection.getAutoCommit())
								connection.rollback();
						}
						catch (Exception e1) 
						{
							Log.error(jobName, "�ع�����ʧ��");
						}
						Log.info("���ɿͻ�����ʧ��,�ӿڵ��š�" + sheetid + "��");
					}
				}catch(Exception ex){
					if(connection!=null && !connection.getAutoCommit()) connection.rollback();
					Log.error(jobName, ex.getMessage());
				}
				
			}
		}
		catch (Exception e)
		{
			Log.error(jobName, "���ɿ�Ͷ���ʧ�ܣ�������Ϣ��"+e.getMessage()) ;
		}
	}
	//�����˻�����
	private void dogenReturnCustomerOrder(Connection conn) throws SQLException, JException
	{
		try 
		{
			ArrayList<String> slist=new ArrayList<String>();
			String sql="";
			sql="select sheetid from it_downnote with(nolock) where sender='"+Params.tradecontactid+"' and sheettype=2";
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0;i<vt.size();i++)
			{
				Hashtable ht=(Hashtable) vt.get(i);
				slist.add(ht.get("sheetid").toString());
			}
			
			for (Iterator it = slist.iterator(); it.hasNext();) {
				try{
					String sheetid = (String) it.next();
					
					sql="select tid from ns_refund with(nolock) where sheetid='"+sheetid+"'";
					String refsheetid=SQLHelper.strSelect(conn, sql);
					
					sql="declare @ret int;  execute  @ret = eco_GetRefund '"+sheetid+"';select @ret ret;";
					try
					{
						conn.setAutoCommit(false);
						int ret=SQLHelper.intSelect(conn, sql);
				
						if (ret==0)
						{
							sql = "declare @err int ;exec @Err = IP_DownBak '" + sheetid + "' ,2,'yongjun';select @err;";
							int err=SQLHelper.intSelect(conn, sql);
							if (err==-1)
							{							
								throw new JException("��������ʧ��!"+sql);
							}
							Log.info("���ɿͻ��˻��ɹ�,��͵��š�"+refsheetid+"��,�ӿڵ��š�" + sheetid + "��");
						}
						else if (ret==-1)
						{
							Log.info("���ɿͻ��˻�ʧ��!��͵��š�"+refsheetid+"��,�ӿڵ��š�" + sheetid + "��");
							throw new JException(sql);
						}
						conn.commit();
						conn.setAutoCommit(true);		
					}
					catch (SQLException e1)
					{		
						Log.info("���ɿͻ��˻�ʧ��!��͵��š�"+refsheetid+"��,�ӿڵ��š�" + sheetid + "��");
						if (!conn.getAutoCommit())
							try
							{
								conn.rollback();
							}
							catch (Exception e2) { }
						try
						{
							conn.setAutoCommit(true);
						}
						catch (Exception e3) { }
						throw e1;
					}
				}catch(Exception ex){
					if(conn!=null && !conn.getAutoCommit()) conn.rollback();
					Log.error(jobName, ex.getMessage());
				}
							
			}	
		}
		catch (Exception e) 
		{
			Log.error(jobName, "���ɿ���˻�����ʧ�ܣ�������Ϣ��"+e.getMessage()) ;
		}
	}

	public String toString()
	{
		return jobName + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}