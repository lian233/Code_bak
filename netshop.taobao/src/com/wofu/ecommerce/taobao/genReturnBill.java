package com.wofu.ecommerce.taobao;

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

public class genReturnBill extends Thread{
	private static String jobname = "��è�����˻�����ҵ";
	
	private boolean is_gening=false;
	
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_gening = true;
			try {
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.taobao.Params.dbname);	
				createReturnBill(connection);			
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
	
	private void createReturnBill(Connection conn) throws Exception
	{
		
		String sql="select sheetid from it_downnote with(nolock) where sender='"+Params.tradecontactid+"' and sheettype=11";
		List sheetlist=SQLHelper.multiRowListSelect(conn, sql);
		for(Iterator it=sheetlist.iterator();it.hasNext();)
		{				
			String sheetid=(String) it.next();
			
			sql="declare @ret int;  execute  @ret = eco_GetReturnBill '"+sheetid+"';select @ret ret;";
			conn.setAutoCommit(false);
			int ret=SQLHelper.intSelect(conn, sql);
	
			if (ret==0)
			{
				sql = "declare @err int ;exec @Err = IP_DownBak '" + sheetid + "' ,11,'yongjun';select @err;";
				int err=SQLHelper.intSelect(conn, sql);
				if (err==-1)
				{							
					throw new JException("��������ʧ��!"+sql);
				}
				
				Log.info("������è�˻����ɹ�,�ӿڵ��š�" + sheetid + "��");
	
			}
			else if (ret==-1)
			{
				Log.info("������è�˻���ʧ��!�ӿڵ��š�" + sheetid + "��");
				throw new JException(sql);
			}
			conn.commit();
			conn.setAutoCommit(true);			
		}		
	}
	
	public String toString()
	{
		return jobname + " " + (is_gening ? "[gening]" : "[waiting]");
	}	
}
