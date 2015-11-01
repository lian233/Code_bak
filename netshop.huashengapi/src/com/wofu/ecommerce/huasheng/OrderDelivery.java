package com.wofu.ecommerce.huasheng;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.huasheng.util.*;

public class OrderDelivery extends Thread {

	private static String jobname = "�羳�̳�(����API)��������������ҵ";
	private static String tradecontactid = Params.tradecontactid ;
	private boolean is_exporting = false;
	
	//ִ�в���
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Date nowtime = new Date();
			if(Params.startTime.getTime() <= nowtime.getTime())
			{//���ϻ򳬹�ָ��������ʱ��
				Connection connection = null;
				is_exporting = true;
				try {		
					connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.weipinhui.Params.dbname);
					//��¼��ǰִ��ʱ��(�����ж��Ƿ��̼߳���)
					huasheng.setCurrentDate_orderDelivery(new Date());
					//��ѯ��������Ϣ�󶩵�����
					doDelivery(connection,getDeliveryOrders(connection,3));
				} catch (Exception e) { 
					try {
						if (connection != null && !connection.getAutoCommit())
							connection.rollback();
					} catch (Exception e1) {
						Log.error(jobname, "�ع�����ʧ��");
					}
					Log.error("105", jobname, Log.getErrorMessage(e));
				} finally {
					is_exporting = false;
					try {
						if (connection != null)
							connection.close();
					} catch (Exception e) {
						Log.error(jobname, "�ر����ݿ�����ʧ��");
					}
				}
				System.gc();
				Log.info(jobname + "�´�ִ�еȴ�ʱ��:" + Params.waittime + "��");
				long startwaittime = System.currentTimeMillis();
				while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.weipinhui.Params.waittime * 1000))
					try {
						sleep(1000L);
					} catch (Exception e) {
						Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
					}
			}
			else
			{//�ȴ�����
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
			}
		} while (true);
	}
	
	///////////////////////////////////////////////////////////
	/**
	 * ��ѯ��������Ϣ(�ڱ�it_upnote  sheettype=3,ns_delivery,deliveryref���в�ѯ����Ҫ�����Ķ���)
	 * @param conn ���ݿ�����
	 * @param sheettype �������ͣ�3Ϊ������4Ϊ�޸�������Ϣ
	 * @return ��������Ϣ��
	 */
	private Vector<Hashtable> getDeliveryOrders(Connection conn,int sheettype)
	{	
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			sql = "select a.notetime,a.sheetid,b.tid, b.companycode,b.outsid,a.sheettype from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock)"
				+ "where a.sheettype = " + sheettype + " and a.sheetid=b.sheetid and a.receiver='"
				+ tradecontactid + "' and b.companycode=c.companycode";
			
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int k=0; k<vt.size();k++)
			{	
				Hashtable<String,String> ht=new Hashtable<String,String>();
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(k);
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString().trim().replaceAll("[?]", ""));
				ht.put("express_code", hto.get("companycode").toString().trim());
				ht.put("transport_no", hto.get("outsid").toString().trim());     //��ݵ���
				ht.put("sheettype", String.valueOf(hto.get("sheettype")));     //�������ͣ�3Ϊ������4Ϊ�޸�������Ϣ
				vtorders.add(ht);
			}
		}
		catch(SQLException sqle)
		{
			Log.error(jobname, "��ѯ��������Ϣ����:"+sqle.getMessage());
		}
		catch(Exception e)
		{
			Log.error(jobname, "��ѯ��������Ϣ����:"+e.getMessage());
			//e.printStackTrace() ;
		}
		return vtorders;
	}
	
	/**
	 * ��������(IT_UpNote -> IT_UpNoteBak)
	 * @param conn ���ݿ�����
	 * @param vdeliveryorder ��������Ϣ��
	 * @throws Exception
	 */
	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Exception
	{
		String sql = "" ;
		Log.info("����������Ϊ:��"+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			//��ǰ��������Ϣ
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();		//ϵͳ���ݲ�������
			String orderID = hto.get("orderid").toString();		//������
			String postCompany = hto.get("express_code").toString();		//��ݹ�˾���
			String postNo = hto.get("transport_no").toString();		//��ݵ���
			String sheetType = hto.get("sheettype").toString();		//�������ͣ�3Ϊ������4Ϊ�޸�������Ϣ
			try 
			{
				//���·���״̬
				boolean	success = delivery(jobname, conn, hto) ;
				Log.info("�����ɹ�״̬��"+success);
				if(success)
				{
					conn.setAutoCommit(false);
	
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
	
					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";
					SQLHelper.executeSQL(conn, sql);
					
					conn.commit();
					conn.setAutoCommit(true);
				}
			}
			catch (Exception e) 
			{	if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				Log.info("����ʧ��,��" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
			}
		}
	}
	
	/**
	 * �Է�������--���·���״̬
	 * @param jobname
	 * @param conn
	 * @param hto ��ǰ��������Ϣ
	 * @return �Ƿ�ɹ�
	 * @throws Exception
	 */
	private static boolean delivery(String jobname,Connection conn,Hashtable<String, String> hto) throws Exception
	{
		boolean flag = false ;
		//������
		String orderCode = hto.get("orderid").toString();
		//��ݹ�˾
		String postCompanyCode = hto.get("express_code").toString();
		try
		{
			//�����ҳ���Ӧ��ݹ�˾����
			String tmp = Params.htPostCompany.get(postCompanyCode);
			if(!tmp.equals("") && !tmp.equals(null))
				postCompanyCode = tmp;
		}
		catch(Exception err) {}
		//�˵���
		String postNo = hto.get("transport_no").toString();
		
		//����Ƿ���Է���
		//��ѯ�ͻ�������
		String sql = "select count(*) from customerorder where flag=100 and refsheetid='"+orderCode+"'";
		//String sql = "select count(*) from customerorder0 where flag=100 and refsheetid='"+orderCode+"'";	//������.
		int counter = SQLHelper.intSelect(conn, sql);
		if(counter != 1){
			//�����˲𵥻��ߺϵ��Ĳ��ܷ���
			//��:�����ٲ�,�Է��Ѿ�����˴�������
			//�ϵ�:�Է��ӿڲ�����
			Log.error(jobname,"������" + orderCode + "�������Ϸ���Ҫ��:�������й��𵥻��ߺϵ�,�Է������ӿڲ��������!");
			return false;
		}
		
		//׼����������
		String paramStr = "order_id=" + orderCode + "&express_company=" + postCompanyCode + "&express_id=" + postNo;

		//��������
		try 
		{	
			Log.info("���ͷ������� ...");
			String result = Utils.doRequest("deliver", paramStr, false);
			if(result.equals(""))
				return false;
			
			JSONObject resultJson = new JSONObject(result);
			if(resultJson.getBoolean("state"))
			{
				Log.info("���������ɹ������š�" + orderCode + "������ݹ�˾��" + postCompanyCode + "������ݵ��š�" + postNo + "��") ;
				flag = true;
			}
			else
			{
				Log.info("��������ʧ�ܣ����š�" + orderCode + "������ݹ�˾��" + postCompanyCode + "������ݵ��š�" + postNo + "����������Ϣ��" + resultJson.getString("msg")) ;
				flag=false;
			}
		} catch (Exception e) {
			Log.info("��������ʧ�ܣ����š�" + orderCode + "������ݹ�˾��" + postCompanyCode + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
			flag=false ;
		}
		return flag ;
	}
	

	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	
}
