package com.wofu.ecommerce.yougou;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class genCustomerRet extends Thread{
	private static String jobname = "һ�ŵ궩�����ɿͻ��˻���ҵ";
	
	private boolean is_gening=false;
	
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_gening = true;
			try {
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.yougou.Params.dbname);	
				dogenCustomerRet(connection,getSheetList(connection));			
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.yougou.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	private List<String> getSheetList(Connection conn)
	{
		ArrayList<String> sheetlist=new ArrayList<String>();
		String sql="";
		try
		{
			sql="select sheetid from it_downnote with(nolock) where sender='"+Params.tradecontactid+"' and sheettype=2";
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0;i<vt.size();i++)
			{				
				Hashtable ht=(Hashtable) vt.get(i);
				sheetlist.add(ht.get("sheetid").toString());
			}
		
		}catch(SQLException e)
		{
			Log.error(jobname, "ȡ֪ͨ���ų���!");
		}
		return sheetlist;
	}
	
	private void dogenCustomerRet(Connection conn,List slist) throws SQLException, JException
	{
		for (Iterator it = slist.iterator(); it.hasNext();) {
			String sheetid = (String) it.next();
			
			String sql="select tid from ns_refund with(nolock) where sheetid='"+sheetid+"'";
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
					
					sql="select sheetid from refund0 with(nolock) where refsheetid='"+refsheetid+"' "
					+"union "
					+"select sheetid from refund with(nolock) where refsheetid='"+refsheetid+"'";
					
					String refundsheetid=SQLHelper.strSelect(conn, sql);	
					
					Log.info("���ɿͻ��˻��ɹ�,�˻����š�"+refundsheetid+"��,�Ա����š�"+refsheetid+"��,�ӿڵ��š�" + sheetid + "��");
		
				}
				else if (ret==-1)
				{
					Log.info("���ɿͻ��˻�ʧ��!�Ա����š�"+refsheetid+"��,�ӿڵ��š�" + sheetid + "��");
					throw new JException(sql);
				}
				conn.commit();
				conn.setAutoCommit(true);		
					
			}
			catch (SQLException e1)
			{		
				Log.info("���ɿͻ��˻�ʧ��!�Ա����š�"+refsheetid+"��,�ӿڵ��š�" + sheetid + "��");
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
		}	
	}
	public String toString()
	{
		return jobname + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}
