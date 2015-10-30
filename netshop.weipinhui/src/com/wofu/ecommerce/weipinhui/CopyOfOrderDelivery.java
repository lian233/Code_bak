package com.wofu.ecommerce.weipinhui;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weipinhui.util.CommHelper;
/**
 * ΨƷ�ᷢ������
 * 1:��������
 * 2������
 * @author Administrator
 *
 */
public class CopyOfOrderDelivery extends Thread {

	private static String jobname = "ΨƷ�ᶩ������������ҵ";
	private static String tradecontactid=Params.tradecontactid ;
	private static Hashtable<String, String> htComCode = Params.htComCode ;
	private static Hashtable<String, String> htComName = Params.htComName ;
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.weipinhui.Params.dbname);
				WeipinHui.setCurrentDate_orderDelivery(new Date());
				//��������
				doDelivery(connection,getDeliveryOrders(connection));	
				//�޸�������Ϣ
				editDeliveryInfo(jobname,connection,getModifyDeliveryOrders(connection));	
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.weipinhui.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	

	private Vector<Hashtable> getModifyDeliveryOrders(Connection connection) {
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
		
				sql = "select  a.notetime,a.sheetid,b.tid, b.companycode,b.outsid,a.sheettype from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock)"
					+ "where a.sheettype =4 and a.sheetid=b.sheetid and a.receiver='"
					+ tradecontactid + "' and b.companycode=c.companycode";
			
	
			Vector vt=SQLHelper.multiRowSelect(connection, sql);
			for(int k=0; k<vt.size();k++)
			{	
				Hashtable<String,String> ht=new Hashtable<String,String>();
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(k);
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString().trim());
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

	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Exception
	{
		String sql = "" ;
		Log.info("��������Ϊ:��"+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderID = hto.get("orderid").toString();
			String postCompany = hto.get("express_code").toString();
			String postNo = hto.get("transport_no").toString();
			String sheetType = hto.get("sheettype").toString();
				try 
				{
					
					boolean	success = delivery(jobname, conn, hto) ;
					//Log.info("�����ɹ�״̬��"+success);
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
					Log.info("���·�����Ϣʧ�ܣ�ΨƷ�ᵥ�š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
				}
			}
			
	}
	
	private Vector<Hashtable> getDeliveryOrders(Connection conn)    //�ڱ�it_upnote  sheettype=3,ns_delivery,deliveryref���в�ѯ����Ҫ�����Ķ���
	{	
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
		
				sql = "select  a.notetime,a.sheetid,b.tid, b.companycode,b.outsid,a.sheettype from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock)"
					+ "where a.sheettype =3 and a.sheetid=b.sheetid and a.receiver='"
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
	

	//�Է�������--���·���״̬
	private  static boolean delivery(String jobname,Connection conn,Hashtable<String, String> hto) throws Exception
	{
		HashMap<String,Object> jsonmap = new HashMap(); 
		boolean flag = false ;
		//������
		String orderCode = hto.get("orderid").toString();
		String postCompanyCode = htComCode.get(hto.get("express_code").toString());
		String postCompanyName = htComName.get(hto.get("express_code").toString());
		//�˵���
		String postNo = hto.get("transport_no").toString();
		//��������
		if(!exportOrder(orderCode)){
			Log.error("ΨƷ�ᶩ����������,������: ", orderCode);
			return false;
		}
		jsonmap.put("order_sn", orderCode);
		
		jsonmap.put("carriers_code", Long.parseLong(postCompanyCode));
		
		jsonmap.put("carrier", postCompanyName);
		String sql = "select count(*) from customerorder where flag=100 and refsheetid='"+orderCode+"'";
		int count = SQLHelper.intSelect(conn, sql);
		if(count==0){
			sql = "select count(1) from CustomerOrderRefList where refsheetid='"+orderCode+"'";
			int count2 = SQLHelper.intSelect(conn, sql);
			if(count2==0){
				Log.info("������: ��"+orderCode+"��,״̬�쳣��������������");
				return true;
			}else{    //�ϵ�����
				jsonmap.put("package_type", 1);
				jsonmap.put("packages", getPackages(orderCode,conn,postNo));
			}
			
		}
		else if(count==1){  //û�в�ֶ���
			jsonmap.put("package_type", 1);
			jsonmap.put("packages", getPackages(orderCode,conn,postNo));
			
			
		}else{  //��ֶ���
			sql = "select refsheetid,deliverysheetid from customerorder where flag=100 and refsheetid='"+orderCode+"'";
			Vector customorders = SQLHelper.multiRowSelect(conn, sql);
			sql = "select tid,outsid from ns_delivery where tid='"+orderCode+"'";
			Vector deliverys = SQLHelper.multiRowSelect(conn, sql);
			boolean isFold = false;
			if(customorders.size()!=deliverys.size())
				isFold=false;
			else{
				for(int i=0;i<customorders.size();i++){
					isFold = false;
					Hashtable t = (Hashtable)customorders.get(i);
					String refsheetid = t.get("refsheetid").toString();
					String deliverysheetid = t.get("deliverysheetid").toString();
					for(int j=0;j<deliverys.size();j++){
						Hashtable d = (Hashtable)deliverys.get(i);
						String tid = d.get("tid").toString();
						String outsid = d.get("outsid").toString();
						if(refsheetid.equals(tid)){
							if(deliverysheetid.equals(outsid));
							isFold=true;
							break;
						}
					
				}
				
			}
			}
			
		if(isFold)	{
			jsonmap.put("package_type", 2);
			jsonmap.put("packages", getPackagesCf(conn ,customorders));
		}else{
			Log.info("������: ��"+orderCode+"��  �۵��ֻ���û��ȫ����������һ���ٷ�����");
			return false;
		}
		}
		
		try 
		{	
			//������
			String apimethod="pop/ship.php?";
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("source", Params.source);
	        map.put("sid", Params.sid);
	        map.put("apimethod", apimethod);
	        String order_list = new JSONArray().put(jsonmap).toString().replace("\\", "").replace("{\"packages\":\"[", "{\"packages\":[").replace("}]\"","}]");
	        map.put("order_list", URLEncoder.encode(order_list,"utf-8"));
	        //��������
			String responseText = CommHelper.doGet(map,Params.url);
			//Log.info("�������� ��"+responseText);
			//�ѷ��ص�����ת��json����
			JSONObject responseObj= new JSONObject(responseText);
			int resultCode= responseObj.getInt("status");
			//������� 
			if(resultCode!=1){   //��������
				int operCode = (responseObj.getJSONObject("data").getJSONArray("fail_data").getJSONObject(0)).getJSONObject("fail").getInt("error_code");
				if(operCode==-20326){  //�ظ�����
					Log.info("ΨƷ�ᶩ��������ҵ����,ΨƷ�ᵥ�š�" + orderCode + "��,�Ѿ�����");
					flag=true;
				}else if(operCode==-20329){  //�����Ѿ�ȡ��
					Log.info("ΨƷ�ᶩ��������ҵ����,ΨƷ�ᵥ�š�" + orderCode + "��,�����Ѿ�ȡ��");
					flag=true;
				}else{
					Log.error("ΨƷ�ᶩ��������ҵ����,ΨƷ�ᵥ�š�" + orderCode + "��", "ΨƷ�ᶩ��������ҵ�������룺"+operCode+", �˵���: "+postNo+"������Ϣ: "+new String((responseObj.getJSONObject("data").getJSONArray("fail_data").getJSONObject(0)).getJSONObject("fail").getString("error_msg").getBytes(),"gbk"));
					flag=false;
				}
				
				
			}
			else
			{
			
				int successNum= responseObj.getJSONObject("data").getInt("success_num");
				if(successNum==1){
					flag=true;
				}
				Log.info("���·�����Ϣ�ɹ���ΨƷ�ᵥ�š�" + orderCode + "������ݹ�˾��" + postCompanyName + "������ݵ��š�" + postNo + "��") ;
			}
			
		} catch (Exception e) {
			Log.info("���·�����Ϣʧ�ܣ�ΨƷ�ᵥ�š�" + orderCode + "������ݹ�˾��" + postCompanyName + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
			flag=false ;
		}
		
		return flag ;
		
	}
	
	//��������
	private static Boolean exportOrder(String order_sn) throws Exception{
		Boolean isExport=false;
		String apimethod="pop/export.php?";
		HashMap<String,Object> map = new HashMap<String,Object>();
	    map.put("p", String.valueOf(1));
	    map.put("l", "30");
	    map.put("order_sn", order_sn);
	    map.put("source", Params.source);
	    map.put("sid", Params.sid);
	    map.put("apimethod", apimethod);

	     //��������
	     
		String responseText = CommHelper.doGet(map,Params.url);
		//Log.info("�����������ؽ��: "+responseText);
		if(new JSONObject(responseText).getInt("status")==1) isExport=true;
		return isExport;
	}
	
	private static String getPackages(String order_sn,Connection conn,String transport_no) throws Exception{
		String sql = "select distinct a.outerskuid,a.num from ns_orderitem a,ns_customerorder b  where a.sheetid=b.sheetid and b.tid='"+order_sn+"'";
		Vector result= SQLHelper.multiRowSelect(conn, sql);
		JSONArray arr = new JSONArray();
		JSONObject local= new JSONObject();
		for(int i=0;i<result.size();i++){
			JSONObject obj = new JSONObject();
			Hashtable temp= (Hashtable)result.get(i);
			obj.put("good_sn", (String)temp.get("outerskuid"));
			obj.put("amount", temp.get("num")+"");
			local.put(i+"", obj);
		}
		local.put("transport_no",transport_no);
		return arr.put(local).toString();
		
	}
	
	
	private static String getPackagesCf(Connection conn,Vector<Hashtable> ve) throws Exception{
		JSONArray arr = new JSONArray();
		for(int i=0;i<ve.size();i++){
			Hashtable table = (Hashtable)ve.get(i);
			String order_sn=table.get("refsheetid").toString();
			String transport_no=table.get("deliverysheetid").toString();
			String sql = "select a.outerskuid,a.PurQty from customerorderitem a,customerorder b  where a.sheetid=b.sheetid and b.refsheetid='"+order_sn+"' and b.deliverysheetid='"+transport_no+"'";
			Vector result= SQLHelper.multiRowSelect(conn, sql);
			JSONObject local= new JSONObject();
			for(int j=0;j<result.size();j++){
				Hashtable temp= (Hashtable)result.get(j);
				String outerskuid=temp.get("outerskuid").toString();
				if("".equals(outerskuid)) {
					continue;
				}
				JSONObject obj = new JSONObject();
				obj.put("good_sn", outerskuid);
				obj.put("amount", temp.get("PurQty")+"");
				local.put(j+"", obj);
			}
			local.put("transport_no",transport_no);
			arr.put(local);
		}
		Log.info("�۵��ֻ����ݣ�"+arr.toString());
		return arr.toString();
		
	}
	
	/**
	 * �޸�������Ϣ
	 * @return
	 * @throws Exception
	 */
	private static void editDeliveryInfo(String jobname,Connection conn,Vector<Hashtable> hto) throws Exception {
		
		
		
		String sql = "" ;
		Log.info("�޸�����������Ϣ����Ϊ:��"+hto.size());
		for (int i = 0; i < hto.size(); i++) 
		{
			
			Hashtable tab = (Hashtable) hto.get(i);
			String sheetid = tab.get("sheetid").toString();
			String orderID = tab.get("orderid").toString();
			String postCompany = tab.get("express_code").toString();
			String postNo = tab.get("transport_no").toString();
			try{
				boolean	success = editDeliveryInfoOne(jobname,tab,conn) ;
				//Log.info("�����ɹ�״̬��"+success);
				if(success)
				{
					conn.setAutoCommit(false);
	
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 4";
					SQLHelper.executeSQL(conn, sql);
	
					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=4";
					SQLHelper.executeSQL(conn, sql);
					
					conn.commit();
					conn.setAutoCommit(true);	
				}
			}
			catch (Exception e) 
			{	if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				//e.printStackTrace() ;
				Log.info("���·�����Ϣʧ�ܣ�ΨƷ�ᵥ�š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
			}
			}
		
		
		
		}
	 //�޸ĵ�����������Ϣ
	private static boolean editDeliveryInfoOne(String jobname,Hashtable hto,Connection conn) throws Exception{
		    HashMap<String,Object> jsonmap = new HashMap(); 
			boolean flag = false ;
			//������
			String orderCode = hto.get("orderid").toString();
			String sql ="select count(*) from customerorder where flag=100 and refsheetid='"+orderCode+"'";
			if(SQLHelper.intSelect(conn, sql)>1) {
				sql = "select count(1) from it_upnote a,ns_delivery b where a.sheetid=b.sheetid and b.tid='"+orderCode+"'";
				int count = SQLHelper.intSelect(conn, sql);
				if(count==1){   //�Ѿ�����
					Log.info("������:��"+orderCode+"�� �����۵����������޸�������Ϣ��������!");
					return true;   //�۵��Ĳ����޸�������Ϣ
				}else{         //û�з���
					Log.info("������:��"+orderCode+"�� �����۵����������޸�������Ϣ��������!");
					return false;   //�۵��Ĳ����޸�������Ϣ
				}
				
			}
			jsonmap.put("order_sn", orderCode);
			
			//������˾
			String postCompanyCode = htComCode.get(hto.get("express_code").toString());
			jsonmap.put("carriers_code", Long.parseLong(postCompanyCode));
			String postCompanyName = htComName.get(hto.get("express_code").toString());
			jsonmap.put("carrier", postCompanyName);
			//�˵���
			String postNo = hto.get("transport_no").toString();
			jsonmap.put("package_type", 1);
			jsonmap.put("packages", getPackages(orderCode,conn,postNo));
			try 
			{	
				//������
				String apimethod="pop/edit_transport_no.php?";
				HashMap<String,Object> map = new HashMap<String,Object>();
				map.put("source", Params.source);
		        map.put("sid", Params.sid);
		        map.put("apimethod", apimethod);
		        String order_list = new JSONArray().put(jsonmap).toString().replace("\\", "").replace("{\"packages\":\"[", "{\"packages\":[").replace("}]\"","}]");
		        map.put("order_list", URLEncoder.encode(order_list,"utf-8"));
		        //��������
				String responseText = CommHelper.doGet(map,Params.url);
				//Log.info("�������� ��"+responseText);
				//�ѷ��ص�����ת��json����
				JSONObject responseObj= new JSONObject(responseText);
				int resultCode= responseObj.getInt("status");
				//������� 
				if(resultCode!=1){   //��������
					int operCode = (responseObj.getJSONObject("data").getJSONArray("fail_data").getJSONObject(0)).getJSONObject("fail").getInt("error_code");
					String errmsg = new String((responseObj.getJSONObject("data").getJSONArray("fail_data").getJSONObject(0)).getJSONObject("fail").getString("error_msg").getBytes(),"gbk");
					if(operCode==-20331){  //���˿�״̬
						if(errmsg.indexOf("����״̬=49")!=-1 || errmsg.indexOf("����״̬=54")!=-1){
							flag=true;
						}
					}else{
						flag=false;
					}
					Log.error("ΨƷ�ᶩ���޸�������Ϣ��ҵ����", "ΨƷ�ᶩ���޸�������Ϣ��ҵ�������룺"+operCode+", ������Ϣ: "+errmsg);
					
					
				}
				else
				{
				
					int successNum= responseObj.getJSONObject("data").getInt("success_num");
					if(successNum==1){
						flag=true;
					}
					Log.info("�����޸�������Ϣ�ɹ���ΨƷ�ᵥ�š�" + orderCode + "������ݹ�˾��" + postCompanyName + "������ݵ��š�" + postNo + "��") ;
				}
				
			} catch (Exception e) {
				Log.info("�����޸�������Ϣʧ�ܣ�ΨƷ�ᵥ�š�" + orderCode + "������ݹ�˾��" + postCompanyName + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
				flag=false ;
			}
			
			return flag ;
	}

		
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	
}
