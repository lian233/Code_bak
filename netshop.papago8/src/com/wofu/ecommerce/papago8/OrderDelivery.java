package com.wofu.ecommerce.papago8;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.papago8.util.CommHelper;
public class OrderDelivery extends Thread {

	private static String jobname = "papago8��������������ҵ";
	private static String tradecontactid=Params.tradecontactid ;
	private static Hashtable<String, String> htComCode = Params.htComCode ;
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.papago8.Params.dbname);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.papago8.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws SQLException
	{
		String sql = "" ;
		Log.info("��������Ϊ:��"+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderID = hto.get("orderid").toString();
			String postCompany = hto.get("express_code").toString();
			String postNo = hto.get("post_no").toString();
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
			}
			catch (Exception e) 
			{	if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				//e.printStackTrace() ;
				Log.info("���·�����Ϣʧ�ܣ�papago8���š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
			}
			
		}
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
	

	//�Է�������--���·���״̬
	private  static boolean delivery(String jobname,Connection conn,Hashtable<String, String> hto)
	{
		boolean flag = false ;
		//������
		String orderCode = hto.get("orderid").toString();
		//Log.info("orderCode: "+orderCode);
		//������˾
		String postCompany = htComCode.get(hto.get("express_code").toString());
		//Log.info("postCompany: "+postCompany);
		//�˵���
		String postNo = hto.get("post_no").toString();
		//Log.info("postNo: "+postNo);
		try 
		{	
			//������
			String apimethod="DeliverOrder.aspx?";
			HashMap<String,Object> map = new HashMap<String,Object>();
	        map.put("tid", orderCode);
	        map.put("Loginame", URLEncoder.encode(postCompany,"utf-8"));
	        map.put("key", Params.Key);
	        map.put("apimethod", apimethod);
	        map.put("Logino", postNo);
	        map.put("format", "json");
	        //��������
			String responseText = CommHelper.doGet(map,Params.url);
			Log.info("�������� ��"+responseText);
			//�ѷ��ص�����ת��json����
			JSONObject responseObj= new JSONObject(responseText).getJSONObject("response");
			String result= responseObj.getString("result");
			String code = responseObj.getString("status");
			//������� 
			if("SUCCESS".equals(result) && "4".equals(code)){   //��������
				flag=true;
				
			}
			else if("ERROR:Current status does not allow shipment".equals(result) && "4".equals(code)){
				Log.error("���·�����Ϣʧ��", "papago8���š�" + orderCode + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "���������ѷ���");
				flag=true;
				
			}else
			{
				flag=false;
				Log.error("���·�����Ϣʧ��","papago8���š�" + orderCode + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������Ϣ��" + result) ;
			}
			
		} catch (Exception e) {
			Log.info("���·�����Ϣʧ�ܣ�papago8���š�" + orderCode + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
			flag=false ;
		}
		
		return flag ;
	}
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	
}
