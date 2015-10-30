package com.wofu.ecommerce.ylw;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ylw.util.CommHelper;
public class OrderDelivery extends Thread {

	private static String jobname = "������������������ҵ";
	private static String tradecontactid=Params.tradecontactid ;
	private static Hashtable<String, String> htComCode = Params.htComCode ;
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.ylw.Params.dbname);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.ylw.Params.waittime * 1000))
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
			String postCompany = hto.get("post_company").toString();
			String postNo = hto.get("post_no").toString();
			System.out.println(postCompany);
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
				Log.info("���·�����Ϣʧ�ܣ��������š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
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
				ht.put("post_company", hto.get("companycode").toString());
				ht.put("post_no", hto.get("outsid").toString());     //��ݵ���
				ht.put("notetime", Formatter.format(hto.get("notetime"), Formatter.DATE_TIME_FORMAT));  //����ʱ��
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
		//������˾
		String postCompany = htComCode.get(hto.get("post_company").toString());
		//�˵���
		String postNo = hto.get("post_no").toString();
		//��Ʒ���
		String prods = getStrList(conn,orderCode);
		//����ʱ��
		String deveryTime= hto.get("notetime").toString();
		try 
		{	
			//������
			String apimethod="suning.custom.orderdelivery.add";
			HashMap<String,String> reqMap = new HashMap<String,String>();
	        reqMap.put("orderCode", orderCode);
	        reqMap.put("expressNo", postNo);
	        reqMap.put("deliveryTime",deveryTime);
	        reqMap.put("expressCompanyCode", postCompany);
	        reqMap.put("sendDetail", prods);
	      //  reqMap.put("orderLineNumbers", "{'orderLineNumber':[]}");
	        reqMap.put("orderLineNumbers", "{}");
	       // ReqParams=ReqParams.replace("\"{'orderLineNumber':[]}\"","{\"orderLineNumber\":['']}");
	        HashMap<String,String> map = new HashMap<String,String>();
	        map.put("appSecret", Params.appsecret);
	        map.put("appMethod", apimethod);
	        map.put("format", Params.format);
	        map.put("versionNo", "v1.2");
	        map.put("appKey", Params.appKey);
	        //��������
			String responseText = CommHelper.doRequest(map,Params.url);
			//Log.info("�������� ��"+responseText);
			//�ѷ��ص�����ת��json����
			JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
			
			//������� 
			if(responseText.indexOf("sn_error")!=-1){   //��������
				String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
				Log.error("��������������ҵ����", "��������������ҵ�������룺"+operCode);
				flag=false;
				
			}
			else
			{
			
				JSONArray sendDetails= responseObj.getJSONObject("sn_body").getJSONObject("orderDelivery").getJSONArray("sendDetail");
				for(int j = 0 ; j < sendDetails.length() ; j++)
				{
					JSONObject orderInfo =sendDetails.getJSONObject(j) ;
					String result = orderInfo.getString("sendresult");
	
					//�������״̬��Ϊ0���ҷ��ص������ύ������ͬ���򱾴θ��³ɹ�
					if("Y".equals(result))
					{
						flag = true ;
						Log.info("���·�����Ϣ�ɹ����������š�" + orderCode + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "��,��Ʒ���롾"+orderInfo.getString("productCode")+"��") ;
					}

					else
					{
						flag = false ;
						Log.error(jobname,"���·�����Ϣʧ�ܣ��������š�" + orderCode + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "��,��Ʒ���롾"+orderInfo.getString("productCode")+"��") ;
					}
				}
			}
			
		} catch (Exception e) {
			Log.info("���·�����Ϣʧ�ܣ��������š�" + orderCode + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
			flag=false ;
		}
		
		return flag ;
	}
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	

	
	//�������������Ų�ѯ������������в�Ʒ ������Ʒ�б��ַ���
	public static String getStrList(Connection conn,String tid){
		//�������е���Ʒ�������̼ұ��붼д����ns_orderitem���iid�ֶ�
		List<String> listsResult=null;
		try{
			listsResult = new ArrayList<String>();
			String sql = new StringBuilder().append("select a.iid from ns_orderitem a, ns_customerorder b  where b.tid='").append(tid).append("' and a.SheetID=b.sheetid").toString();
			listsResult=SQLHelper.multiRowListSelect(conn, sql);
		}catch(Exception ex){
			Log.info("������ѯ������Ӧ��������Ʒ���������!");
			return "";
		}
		
		//����list��ʽ�ַ���
		StringBuilder sendDetails= new StringBuilder().append("{'productCode': [");
		for(int i=0;i<listsResult.size();i++){
			sendDetails.append("'").append(listsResult.get(i)).append("',");
		}

		return sendDetails.deleteCharAt(sendDetails.length()-1).append("]}").toString();
	}
}
