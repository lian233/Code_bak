package com.wofu.ecommerce.weipinhui;

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
import com.wofu.ecommerce.weipinhui.util.CommHelper;
/**
 * ΨƷ�ᷢ������
 * 1:��������
 * 2:����
 * @author Administrator
 *
 */
public class OrderDelivery extends Thread {

	private static String jobname = "ΨƷ�ᶩ������������ҵ";
	private static String tradecontactid=Params.tradecontactid ;
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
					WeipinHui.setCurrentDate_orderDelivery(new Date());
					//��ѯ��������Ϣ�󶩵�����
					doDelivery(connection,getDeliveryOrders(connection,3));	
					//�޸�������Ϣ
					editDeliveryInfo(connection,getDeliveryOrders(connection,4));	
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
				//����һ�����ò���(�����ݿ��ж�ȡ)
				Params.UpdateSettingFromDB(null);
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
				Log.info("��" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
			}
		}
	}
	
	/**
	 * ��ѯ����ΨƷ�����������
	 * @param WEIPINHUI_DeliveryCompanyCode ΨƷ�������Code
	 * @return ����������
	 */
	private static String GetDeliveryCompanyName(String WEIPINHUI_DeliveryCompanyCode)
	{
		String returnname = "";
		if(!WEIPINHUI_DeliveryCompanyCode.equals(""))
		{
			//����û��ʼ����ݹ�˾�б��ʱ���ʼ��
			if(Params.DeliveryCompanyJsonData.equals(""))
			{
				Log.info("��ʼ��ʼ���������б�����...");
				JSONArray carriersList = new JSONArray();
				int pageIndex = 1;
				boolean hasNextPage = true;
				while(hasNextPage)
				{
					try {
						//������
						JSONObject jsonobj = new JSONObject();
						jsonobj.put("page", pageIndex);
						jsonobj.put("limit", 100);
						jsonobj.put("vendor_id", Params.vendor_id);
						//��������
						String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getCarrierList", jsonobj.toString());
						//�ж�������
						String returnCode = new JSONObject(responseText).getString("returnCode");
						if(!returnCode.equals("0"))
							break;
						//ҳ������
						int orderNum= new JSONObject(responseText).getJSONObject("result").getInt("total");
						int pageTotal=0;
						if(orderNum!=0){
							pageTotal = orderNum>=Integer.parseInt(Params.pageSize) ? (orderNum %Integer.parseInt(Params.pageSize)==0?orderNum /Integer.parseInt(Params.pageSize):(orderNum /Integer.parseInt(Params.pageSize)+1)) : 1;
						}
						//��ȡ����
						JSONArray carriers = new JSONObject(responseText).getJSONObject("result").getJSONArray("carriers");
						//���ҿ�ݹ�˾����
						for(int i=0;i<carriers.length();i++)
						{
							JSONObject carrier = carriers.getJSONObject(i);
							carriersList.put(carrier);
						}
						//�ж��Ƿ�����һҳ
						if(pageIndex >= pageTotal)
							hasNextPage = false ;
						else
							pageIndex ++ ;
					} catch (JSONException e) {
						Log.error(jobname, "��ȡ�������б����!");
						returnname = "";
					}
				}
				
				if(carriersList.length() > 0)
				{
					Params.DeliveryCompanyJsonData = carriersList.toString();
					Log.info("��ʼ���������б��������!");
				}
				else
					Log.info("��ʼ���������б�����ʧ��!");
			}
			//�Ѿ��г�����������ʼ���ڴ��в�ѯ
			if(!Params.DeliveryCompanyJsonData.equals(""))
			{
				try {
					JSONArray carriersList = new JSONArray(Params.DeliveryCompanyJsonData);
					for(int i=0;i<carriersList.length();i++)
					{
						JSONObject carrier = carriersList.getJSONObject(i);
						String carrierCode = carrier.getString("carrier_code");
						String carrierName = carrier.getString("carrier_name");
						if(WEIPINHUI_DeliveryCompanyCode.equals(carrierCode))
						{
							//Log.info("�ҵ���ݹ�˾��:" + carrierName + ",��ݹ�˾���:" + carrierCode);
							returnname = carrierName;
							break;
						}
					}
				} catch (JSONException e) {
					Log.error(jobname, "��ѯ���������Ƴ���!");
					returnname = "";
				}
			}
		}
		return returnname;
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
		String CompanyCode = hto.get("express_code").toString();
		String postCompanyCode = Params.htPostCompany.get(hto.get("express_code").toString());	//�ӿ�ݴ��� תΪ ΨƷ�����(��:SF -> 1800000604)
		String postCompanyName = GetDeliveryCompanyName(postCompanyCode);		//��ѯΨƷ���ݱ�ŵĶ�Ӧ��ݹ�˾��(1800000604 -> ˳����)
		if(postCompanyName.equals(""))
		{
			Log.warn("��ѯ����ָ����ΨƷ����˹�˾Code:" + postCompanyCode + "�Ķ�Ӧ���˹�˾����!");
			return false;
		}
		
		//�˵���
		String postNo = hto.get("transport_no").toString();
		//��������
		if(!exportOrder(orderCode)){
			Log.error("ΨƷ�ᶩ����������,������: ", orderCode);
			return false;
		}
		
		JSONObject jsonobj = new JSONObject();
		JSONObject Ship = new JSONObject(); 
		//׼��Ҫ����������
		Ship.put("order_id", orderCode);
		Ship.put("carrier_code", postCompanyCode);
		Ship.put("carrier_name", postCompanyName);
		
		//��ѯ�ͻ�������
		String sql = "select count(*) from customerorder where flag=100 and refsheetid='"+orderCode+"'";
		//String sql = "select count(*) from customerorder0 where flag=100 and refsheetid='"+orderCode+"'";	//������.
		int counterA = SQLHelper.intSelect(conn, sql);
		if(counterA==0){	//�ڿͻ��������ϲ�ѯ�����ö�����ȥ�ϵ����ϲ�ѯ
			//��ѯ�ϵ���
			sql = "select count(1) from CustomerOrderRefList where refsheetid='"+orderCode+"'";
			int counterB = SQLHelper.intSelect(conn, sql);
			if(counterB==0){
				Log.info("������: ��"+orderCode+"��,״̬�쳣��������������");
				return true;
			}else{    //�ϵ�����
				Ship.put("package_type", 1);
				Ship.put("packages", getPackages(orderCode,conn,postNo));
			}
		}
		else if(counterA==1){	//û�в�ֶ���
			Ship.put("package_type", 1);
			Ship.put("packages", getPackages(orderCode,conn,postNo));
		}else{  //��ֶ���  customerorder��  ͬһ������������2�����ϵļ�¼
			sql = "select refsheetid,deliverysheetid from customerorder where flag=100 and refsheetid='"+orderCode+"'";		//ȡ��customerorderͬһ�������ŵĲ�ͬ��ݵ���
			Vector customorders = SQLHelper.multiRowSelect(conn, sql);
			sql = "select tid,outsid from ns_delivery where tid='"+orderCode+"'";		//��ѯns_deliveryͬһ�������ŵĶ����ݵ���
			Vector deliverys = SQLHelper.multiRowSelect(conn, sql);
			boolean isFold = false;
			if(customorders.size() != deliverys.size())		//������¼������һ��
				isFold=false;
			else{
				//customerorder��
				for(int i=0;i<customorders.size();i++){	
					isFold = false;
					Hashtable t = (Hashtable)customorders.get(i);
					String refsheetid = t.get("refsheetid").toString();		//������
					String deliverysheetid = t.get("deliverysheetid").toString();		//�˵���
					//ns_delivery��
					for(int j=0;j<deliverys.size();j++){
						Hashtable d = (Hashtable)deliverys.get(i);
						String tid = d.get("tid").toString();	//������
						String outsid = d.get("outsid").toString();		//�˵���
						if(refsheetid.equals(tid) && deliverysheetid.equals(outsid)){
							isFold=true;
							break;
						}
					}
				}
			}
			if(isFold)	{
				Ship.put("package_type", 2);
				Ship.put("packages", getPackagesCf(conn ,customorders));
			}else{
				Log.info("������: ��"+orderCode+"��  �𵥷ֻ���û��ȫ����������һ���ٷ�����");
				return false;
			}
		}
		jsonobj.put("vendor_id",Params.vendor_id);
		jsonobj.put("ship_list", new JSONArray().put(Ship));
		//��������
		try 
		{	
			Log.info("���ͷ������� ...");
			String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "ship", jsonobj.toString());
			//�ѷ��ص�����ת��json����
			JSONObject responseObj= new JSONObject(responseText);
			//�ж�������
			String returnCode = new JSONObject(responseText).getString("returnCode");
			if(!returnCode.equals("0"))
				return false;
			int fail_num= responseObj.getJSONObject("result").getInt("fail_num");
			int successNum= responseObj.getJSONObject("result").getInt("success_num");
			//�����ɹ��Ķ���
			if(successNum>0){
				flag=true;
				Log.info("�����ɹ���ΨƷ�ᵥ�š�" + orderCode + "������ݹ�˾��" + postCompanyName + "������ݵ��š�" + postNo + "��") ;
			}
			else if(fail_num > 0)
				throw new Exception("�ӿڷ��ط���ʧ��");
		} catch (Exception e) {
			Log.info("����ʧ�ܣ�ΨƷ�ᵥ�š�" + orderCode + "������ݹ�˾��" + postCompanyName + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
			flag=false ;
		}
		return flag ;
	}
	
	/**
	 * ��Ӧ�̸��ݶ��������޸Ķ�������״̬
	 * @param order_sn ������
	 * @return ����״̬
	 * @throws Exception
	 */
	private static Boolean exportOrder(String order_id) throws Exception{
		//����ɹ�״̬
		Boolean isExport=false;
		try {
			//������
			JSONObject jsonobj = new JSONObject();
			jsonobj.put("order_id", order_id);
			jsonobj.put("vendor_id", Params.vendor_id);
			//��������
			String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "exportOrderById", jsonobj.toString());
			//Log.info("�����������ؽ��: "+responseText);
			if(new JSONObject(responseText).getJSONObject("result").getInt("success_num")==1)
				isExport=true;
		} catch (JSONException e) {
			isExport=false;
		}
		return isExport;
	}
	
	/**
	 * ��ȡָ�������İ�����Ϣ(û�в�ֶ����İ���)
	 * @param order_sn ������
	 * @param conn ���ݿ�����
	 * @param transport_no �˵���
	 * @return JSONArray
	 * @throws Exception
	 */
	private static JSONArray getPackages(String order_sn,Connection conn,String transport_no) throws Exception{
		String sql = "select distinct a.outerskuid,a.num from ns_orderitem a,ns_customerorder b  where a.sheetid=b.sheetid and b.tid='"+order_sn+"'";
		Vector result= SQLHelper.multiRowSelect(conn, sql);
		
		JSONArray packages = new JSONArray();
		JSONObject Package = new JSONObject();
		JSONArray package_product_list = new JSONArray();
		
		/////Package/////
		//package_product_list(arr)
		for(int i=0;i<result.size();i++){
			//PackageProduct(obj)
			JSONObject PackageProduct = new JSONObject();
			Hashtable temp= (Hashtable)result.get(i);
			PackageProduct.put("barcode", (String)temp.get("outerskuid"));
			PackageProduct.put("amount", Integer.parseInt(temp.get("num").toString()));
			package_product_list.put(PackageProduct);
		}
		//transport_no
		Package.put("package_product_list",package_product_list);
		Package.put("transport_no",transport_no);
		
		/////packages/////
		packages.put(Package);
		return packages;
	}
	
	/**
	 * ��ȡָ�������İ�����Ϣ(��ֶ���)
	 * @param conn
	 * @param ve customorders
	 * @return
	 * @throws Exception
	 */
	private static JSONArray getPackagesCf(Connection conn,Vector<Hashtable> ve) throws Exception{
		JSONArray packages = new JSONArray();
		for(int i=0;i<ve.size();i++){
			Hashtable table = (Hashtable)ve.get(i);
			String order_sn=table.get("refsheetid").toString();		//������
			String transport_no=table.get("deliverysheetid").toString();		//��ݵ���
			String sql = "select a.outerskuid,a.PurQty from customerorderitem a,customerorder b  where a.sheetid=b.sheetid and b.refsheetid='"+order_sn+"' and b.deliverysheetid='"+transport_no+"'";
			Vector result= SQLHelper.multiRowSelect(conn, sql);
			JSONObject local= new JSONObject();
			JSONArray package_product_list = new JSONArray();
			for(int j=0;j<result.size();j++){
				Hashtable temp= (Hashtable)result.get(j);
				String outerskuid=temp.get("outerskuid").toString();
				if("".equals(outerskuid)) {
					continue;
				}
				JSONObject obj = new JSONObject();
				obj.put("barcode", outerskuid);
				obj.put("amount", Integer.parseInt(temp.get("PurQty").toString()));
				package_product_list.put(obj);
			}
			local.put("package_product_list",package_product_list);
			local.put("transport_no",transport_no);
			packages.put(local);
		}
		Log.info("�۵��ֻ����ݣ�"+packages.toString());
		return packages;
		
	}
	
	///////////////////////////////////////////////////////////////
	
	/**
	 * �޸�������Ϣ
	 * @return
	 * @throws Exception
	 */
	private static void editDeliveryInfo(Connection conn,Vector<Hashtable> hto) throws Exception
	{
		String sql = "" ;
		Log.info("�޸�����������Ϣ����Ϊ:"+hto.size());
		for (int i = 0; i < hto.size(); i++) 
		{
			Hashtable tab = (Hashtable) hto.get(i);
			String sheetid = tab.get("sheetid").toString();
			String orderID = tab.get("orderid").toString();
			String postCompany = tab.get("express_code").toString();
			String postNo = tab.get("transport_no").toString();
			try{
				boolean	success = editDeliveryInfoOne(tab,conn) ;
				Log.info("�޸�����������Ϣ״̬��" + success + " orderID:" + orderID + " sheetid:" + sheetid);
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
				Log.info("�޸�������Ϣʧ�ܣ�ΨƷ�ᵥ�š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
			}
		}
	}
	
	/**
	 * �޸ĵ�����������Ϣ
	 * @param jobname
	 * @param hto
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private static boolean editDeliveryInfoOne(Hashtable hto,Connection conn) throws Exception
	{
		boolean flag = false ;
		//������
		String orderCode = hto.get("orderid").toString();
		//��ݹ�˾
		String CompanyCode = hto.get("express_code").toString();
		String postCompanyCode = Params.htPostCompany.get(hto.get("express_code").toString());	//�ӿ�ݴ��� תΪ ΨƷ�����(��:SF -> 1800000604)
		String postCompanyName = GetDeliveryCompanyName(postCompanyCode);		//��ѯΨƷ���ݱ�ŵĶ�Ӧ��ݹ�˾��(1800000604 -> ˳����)
		if(postCompanyName.equals(""))
		{
			Log.warn("��ѯ����ָ����ΨƷ����˹�˾Code:" + postCompanyCode + "�Ķ�Ӧ���˹�˾����!");
			return false;
		}

		String sql ="select count(*) from customerorder where flag=100 and refsheetid='"+orderCode+"'";
		if(SQLHelper.intSelect(conn, sql)>1) {
			sql = "select count(1) from it_upnote a,ns_delivery b where a.sheetid=b.sheetid and b.tid='"+orderCode+"'";
			int count = SQLHelper.intSelect(conn, sql);
			if(count==1){   //�Ѿ�����
				Log.info("������:��"+orderCode+"�� ���˲𵥷��������޸�������Ϣ��������!");
				return true;   //�𵥵Ĳ����޸�������Ϣ
			}else{         //û�з���
				Log.info("������:��"+orderCode+"�� ���˲𵥷��������޸�������Ϣ��������!");
				return false;   //�𵥵Ĳ����޸�������Ϣ
			}
			
		}
		JSONObject jsonobj = new JSONObject();
		JSONObject Ship = new JSONObject(); 
		//������
		Ship.put("order_id", orderCode);
		//��ݹ�˾
		Ship.put("carrier_code", postCompanyCode);
		Ship.put("carrier_name", postCompanyName);
		//�˵���
		String postNo = hto.get("transport_no").toString();
		//����
		Ship.put("package_type", 1);
		Ship.put("packages", getPackages(orderCode,conn,postNo));
		//�������͹�Ӧ��
		jsonobj.put("vendor_id",Params.vendor_id);
		jsonobj.put("ship_list", new JSONArray().put(Ship));
		//��������
		try 
		{	
			Log.info("�����޸Ķ�����������Ϣ���� ...");
			String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "editShipInfo", jsonobj.toString());
			//�ѷ��ص�����ת��json����
			JSONObject responseObj= new JSONObject(responseText);
			//�ж�������
			String returnCode = new JSONObject(responseText).getString("returnCode");
			if(!returnCode.equals("0"))
				return false;
			int fail_num= responseObj.getJSONObject("result").getInt("fail_num");
			int successNum= responseObj.getJSONObject("result").getInt("success_num");
			if(successNum>0){
				flag=true;
				Log.info("�����޸�������Ϣ�ɹ���ΨƷ�ᵥ�š�" + orderCode + "������ݹ�˾��" + postCompanyName + "������ݵ��š�" + postNo + "��") ;
			}
			else if(fail_num > 0)
				throw new Exception("�ӿڷ����޸�������Ϣʧ��");
			
		} catch (Exception e) {
			Log.warn("�����޸�������Ϣʧ�ܣ�ΨƷ�ᵥ�š�" + orderCode + "������ݹ�˾��" + postCompanyName + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
			flag=false ;
		}
		return flag ;
	}
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	
}
