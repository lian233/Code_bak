package com.wofu.ecommerce.qqbuy;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;


import com.wofu.ecommerce.qqbuy.Params;
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class UpdateStatus extends Thread {
	
	private static String jobname = "QQ��������״̬������ҵ";
	private static String accessToken = Params.accessToken ;
	private static String appOAuthID = Params.appOAuthID ;
	private static String secretOAuthKey = Params.secretOAuthKey ;
	private static String cooperatorId = Params.cooperatorId ;
	private static String uin = Params.uin ;
	private static String encoding = Params.encoding ;
	private static String format = Params.format ;
	private static String tradecontactid = Params.tradecontactid ;



	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;
	
			try {		
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.qqbuy.Params.dbname);
				//ȷ����˶���
				doUpdateOrderStatus(jobname, connection, "1") ;
				//ȡ��QQ������������
				doUpdateOrderStatus(jobname, connection, "2") ;

			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
			
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.qqbuy.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	//���¶�����У���updateState 1:��˳ɹ� 2��ȡ���ö���
	public static void doUpdateOrderStatus(String jobname,Connection conn,String updateState)
	{
		try 
		{
			Vector vts=IntfUtils.getUpNotes(conn, tradecontactid, updateState);
			if(vts.size() <= 0)
				return ;
			
			String dealCheckResult = "" ;//������˽�� 0: ��˳ɹ�	1: ���ʧ��
			if("1".equals(updateState))
				dealCheckResult = "0" ;
			else if("2".equals(updateState))
				dealCheckResult = "1" ;
			else
				;
			
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("appOAuthID", appOAuthID) ;
			params.put("secretOAuthKey", secretOAuthKey) ;
			params.put("accessToken", accessToken) ;
			params.put("cooperatorId", cooperatorId) ;
			params.put("encoding", encoding) ;
			params.put("uin", uin) ;
			params.put("format", format) ;
			for (int i=0;i<vts.size();i++)
			{
				Hashtable hts=(Hashtable) vts.get(i);
				String sheetid=hts.get("sheetid").toString();
				String sql = "select top 1 a.tid,b.buyerflag from ns_delivery as a with(nolock),ns_customerOrder as b with(nolock) where a.tid=b.tid and a.sheetid='"+ sheetid +"' order by b.sheetid desc" ;
				Hashtable<String,String> info = SQLHelper.oneRowSelect(conn, sql) ;
				String tid = info.get("tid") ;
				String dealCheckVersion = info.get("buyerflag") ;
				boolean success = OrderUtils.updateOrderStatus(jobname, conn, sheetid, tid, dealCheckVersion, dealCheckResult, params) ;
				if(success)
					IntfUtils.backupUpNote(conn, "yongjun",sheetid, updateState);
			}
		} 
		catch (Exception e) 
		{
			Log.error(jobname, "���¶������״̬ʧ��,������Ϣ:"+ e.getMessage()) ;
		}
	}

}