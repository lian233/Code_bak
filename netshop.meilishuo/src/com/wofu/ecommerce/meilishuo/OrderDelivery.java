package com.wofu.ecommerce.meilishuo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.meilishuo.util.CommHelper;
import com.wofu.ecommerce.meilishuo.util.Utils;

public class OrderDelivery extends Thread
{
	private static String jobname = "����˵��������������ҵ";
	private static String tradecontactid=Params.tradecontactid ;
	private static Hashtable<String, String> htComCode = Params.htComCode ;
	private boolean is_exporting = false;
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.meilishuo.Params.dbname);
				Params.token = PublicUtils.getToken(connection, Integer.parseInt(Params.tradecontactid));
				doDelivery(connection,getDeliveryOrders(connection));		
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
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.meilishuo.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	private Vector<Hashtable> getDeliveryOrders(Connection conn)    //�ڱ�it_upnote  sheettype=3,ns_delivery,deliveryref���в�ѯ����Ҫ�����Ķ���
	{	
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
		
				sql = "select  a.notetime,a.sheetid,b.tid, b.companycode,b.outsid from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock)"
					+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
					+ tradecontactid + "' and b.companycode=c.companycode";
			
	
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int k=0; k<vt.size();k++)
			{	
				Hashtable<String,String> ht=new Hashtable<String,String>();
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(k);
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString());
				ht.put("express_code", hto.get("companycode").toString());
				ht.put("post_no", hto.get("outsid").toString());     //��ݵ���
				//ht.put("notetime", Formatter.format(hto.get("notetime"), Formatter.DATE_TIME_FORMAT));  //����ʱ��
				vtorders.add(ht);
				//Log.info("getDeliveryOrders.express_code: "+hto.get("companycode").toString());
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
	
	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws SQLException
	{
		String sql = "" ;
		Log.info("��������Ϊ:��"+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString().trim();
			String orderID = hto.get("orderid").toString().trim();
			String postCompany = hto.get("express_code").toString().trim();
			String postNo = hto.get("post_no").toString().trim();
			//Log.info("doDelivery.express_code: "+hto.get("companycode").toString());
			//System.out.println(postCompany);
			try
			{
				
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
			}catch(Exception e)
			{
				if(conn!=null && !conn.getAutoCommit())
					conn.rollback();
					//e.printStackTrace() ;
					Log.info("���·�����Ϣʧ�ܣ�����˵���š�" + orderID + "������ݹ�˾��" + postCompany.toLowerCase() + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
		
			}
		}
	}
	
	private  static boolean delivery(String jobname,Connection conn,Hashtable<String, String> hto)
	{
		boolean flag = false ;
		//������
		String orderCode = hto.get("orderid").trim().toString();
		Log.info("orderCode:"+orderCode);
		//������˾
		String postCompany = htComCode.get(hto.get("express_code").toString());
		String postCompany_trans = "";
		if(postCompany.indexOf("��")>=0) postCompany_trans = "shentong";
		if(postCompany.indexOf("��")>=0) postCompany_trans = "yunda";
		if(postCompany.indexOf("˳")>=0) postCompany_trans = "shunfeng";
		if(postCompany.indexOf("˳�����մ�")>=0) postCompany_trans = "shunfengsirida";
		if(postCompany.indexOf("լ")>=0) postCompany_trans = "zhaijisong";
		if(postCompany.indexOf("��")>=0) postCompany_trans = "ziti";
		if(postCompany.indexOf("��")>=0) postCompany_trans = "tiantian";
		if(postCompany.indexOf("Բ")>=0) postCompany_trans = "yuantong";
		if(postCompany.indexOf("E")>=0) postCompany_trans = "EMS";
		//String postCompany = hto.get("express_code").toString();
		//Log.info("postCompany: "+postCompany);
		//�˵���
		String postNo = hto.get("post_no").trim().toString();
		Log.info("postNo: "+postNo);
		try 
		{
			//������
			String apimethod="meilishuo.order.deliver";
			HashMap<String, String> param = new HashMap<String,String>();
			param.put("method", apimethod);
			param.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			param.put("format", "json");
			param.put("app_key", Params.appKey);
			param.put("v", "1.0");
			param.put("sign_method", "MD5");
			param.put("session", Params.token);
			param.put("order_id", orderCode);
			param.put("express_company", postCompany_trans);
			param.put("express_id", postNo);
			
			String responseText = Utils.sendbyget(Params.url,
					param,Params.appsecret);
			 //��������
			//Log.info("��������˵��postCompany: "+postCompany_trans);
			Log.info("�������� ��"+responseText);
			//�ѷ��ص�����ת��json����
			JSONObject responseObj= new JSONObject(responseText);					
			try
			{
				String errormessage = responseObj.getJSONObject(
						"error_response").getString("message"); // ���û������try������ִ�гɹ����д�ͻ�ִ�г������
				Log.error("����˵����������ҵ����", "����˵����������ҵ�������룺"+errormessage);
				flag=false;
			}catch(Exception e)
			{
				int successNum = responseObj.getJSONObject("order_deliver_response").getJSONObject("info").getInt("affect");
				if(successNum>=1){
					flag=true;
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			Log.info("���·�����Ϣʧ�ܣ�����˵���š�" + orderCode + "������ݹ�˾��" + postCompany_trans.toLowerCase() + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
			flag=false ;
		}
		return flag;
	}
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
