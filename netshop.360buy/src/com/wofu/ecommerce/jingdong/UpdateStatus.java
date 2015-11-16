package com.wofu.ecommerce.jingdong;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.request.after.AfterStateUpdateRequest;
import com.jd.open.api.sdk.response.after.AfterStateUpdateResponse;
import com.wofu.ecommerce.jingdong.Params;
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class UpdateStatus extends Thread {
	
	private static String jobName = "��������״̬������ҵ";
	
//	api V2
	private static String SERVER_URL = Params.SERVER_URL ;
	private static String token = Params.token ;
	private static String appKey = Params.appKey ;
	private static String appSecret = Params.appSecret ;
	public void run() {
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {
			Connection connection = null;
	
			try {		
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.jingdong.Params.dbname);
				Log.info("���ӳ������,UpdateStatus��������Ϊ"+connection.getMetaData());
				//ȷ����˶���
				doUpdateCheckStatus(connection,Params.tradecontactid);
				//ȷ���˻�����
				doUpdateRefundOrders(connection,Params.tradecontactid);
				
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
			
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.jingdong.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	//������˺󣬸��¾�������״̬Ϊ������sop����
	private static void doUpdateCheckStatus(Connection conn ,String tradecontactid) throws Exception
	{
		Vector vts=IntfUtils.getUpNotes(conn, tradecontactid, "1");
		for (int i=0;i<vts.size();i++)
		{
			Hashtable hts=(Hashtable) vts.get(i);
			String sheetid=hts.get("sheetid").toString();
			String sql="select tid from ns_delivery with(nolock) where sheetid='"+sheetid+"'";
			String tid=SQLHelper.strSelect(conn, sql);
			
			try
			{
				IntfUtils.backupUpNote(conn, "yongjun",sheetid, "1");
				Log.info("�������״̬�ɹ�,����:" + tid);
			}
			catch(Exception je)
			{
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				//throw new JException(je.getMessage()+" ����:"+tid+" ����״̬:1");
				Log.error(jobName+" ����:"+tid+" ����״̬:1", je.getMessage());
			}
		}
	}

	private static void doUpdateRefundOrders(Connection conn ,String tradecontactid) throws Exception
	{
		Vector vts=IntfUtils.getUpNotes(conn, tradecontactid, "2");
		for (int i=0;i<vts.size();i++)
		{
			Hashtable hts=(Hashtable) vts.get(i);
			String sheetid=hts.get("sheetid").toString();
			//ȡ�þ���ȡ������
			String sql="select refundID from ns_refund with(nolock) where sheetid='"+sheetid+"'";
			String tid=SQLHelper.strSelect(conn, sql);
			try
			{
				JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
				AfterStateUpdateRequest request = new AfterStateUpdateRequest();
				request.setReturnId("299054");
				request.setTradeNo(Long.toString(System.currentTimeMillis()));
				AfterStateUpdateResponse response = client.execute(request);
				if("0".equals(response.getCode()))
				{
					IntfUtils.backupUpNote(conn, "yongjun",sheetid, "2");
					Log.info("�˻��ջ�ȷ�ϸ���״̬�ɹ�,����:" + tid);
				}
				else
				{
					Log.error(jobName, "�˻��ջ�ȷ�ϸ���״̬ʧ��,����:" + tid + "��������룺" + response.getZhDesc()) ;
				}

			}
			catch(Exception je)
			{
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				//throw new JException(je.getMessage()+" ����:"+tid+" ����״̬:2");
				Log.error(jobName+" ����:"+tid+" ����״̬:2", je.getMessage());
			}
		}
	}
}