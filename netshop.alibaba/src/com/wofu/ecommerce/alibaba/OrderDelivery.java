package com.wofu.ecommerce.alibaba;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.alibaba.api.ApiCallService;
import com.wofu.ecommerce.alibaba.auth.AuthService;
import com.wofu.ecommerce.alibaba.util.CommonUtil;


public class OrderDelivery extends Thread {

	private static String jobname = "����ͰͶ�������������ҵ";

	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				Alibaba.setCurrentDate_Order_delivery(new Date());
			 	Hashtable<String, String> params = new Hashtable<String, String>() ;
				params.put("client_id", Params.appkey);
			    params.put("redirect_uri", Params.redirect_uri);
			    params.put("client_secret", Params.secretKey);
			    params.put("refresh_token", Params.refresh_token);
			    String returns=AuthService.refreshToken(Params.host, params);
			    JSONObject access=new JSONObject(returns);
		    	Params.token=access.getString("access_token");
		    	
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
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	
	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Exception
	{
		String sql = "" ;
		Log.info("����Ҫ����������Ϊ: "+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderID = hto.get("orderid").toString();
			String postCompany = hto.get("post_company").toString().toUpperCase();
			String postNo = hto.get("post_no").toString();
			String sheetType = String.valueOf(hto.get("sheetType"));

			//���Ӷ���ID��ȡ��Ʒ��������Ķ�����ϸID
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("orderId", orderID);
			//Log.info("tid: "+orderID);
			params.put("sellerMemberId", Params.sellerMemberId);
			params.put("access_token",Params.token);
			//trade.order.orderList.get
			String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"trade.order.orderList.get",Params.version,Params.requestmodel,Params.appkey);
			String response = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
			Log.info("�������ص����� result: "+response);
			JSONObject res=new JSONObject(response);
			boolean resuccess =  res.getJSONObject("result").optBoolean("success");
			if(!resuccess){
				continue;
			}
			JSONArray orderEntries=res.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0).getJSONArray("orderEntries");
			
			
			
			String orderdetailids="";
			for(int j=0; j<orderEntries.length();j++){
				JSONObject o=orderEntries.getJSONObject(j);
				orderdetailids=orderdetailids+o.getLong("id");
				if(j>=0&&j<orderEntries.length()-1){
					orderdetailids=orderdetailids+",";
				}
			}
			
			try 
			{
				boolean success = false ;
				//����
				if("3".equals(sheetType))
				{
					success = delivery(jobname, orderID, postCompany, postNo,orderdetailids, Params.url, Params.token, Params.appkey, Params.secretKey) ;
				}
				else if("4".equals(sheetType))
					success = ModifyExpressInfo(jobname, orderID, postCompany, postNo,orderdetailids, Params.url, Params.token, Params.appkey, Params.secretKey) ;
				else
				{
					Log.error(jobname, "δ֪��������:"+sheetType) ;
					continue ;
				}
				
				if(success)
				{
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
							+ " where SheetID = '"+ sheetid+ "' and SheetType = "+sheetType;
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype="+sheetType;

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);	
					Log.info("���·�����Ϣ�ɹ�������Ͱ͵��š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����") ;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace() ;
				Log.info("���·�����Ϣʧ�ܣ�����Ͱ͵��š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������룺" + e.getMessage()) ;
			}
			
		}
	}
	
	private Vector<Hashtable> getDeliveryOrders(Connection conn) throws Exception
	{	

		
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			
			sql = "select  a.sheetid,b.tid, b.companycode,b.outsid,c.defaultarrivedays,a.sheettype from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock) "
				+ "where (a.sheettype=3 or a.sheettype=4) and a.sheetid=b.sheetid and a.receiver='"
				+ Params.tradecontactid + "' and b.companycode=c.companycode";

			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0; i<vt.size();i++)
			{
				
				
				Hashtable<String,String> ht=new Hashtable<String,String>();
			
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(i);
				
				
				
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString().trim());
				String companyid=getCompnayID(hto.get("companycode").toString().trim());
				
				if (companyid.equals("")) 
				{
					Log.info("δ����������˾����:"+hto.get("companycode").toString());
					continue;
				}
				
				ht.put("post_company", companyid);
				
				String postno=hto.get("outsid").toString().trim();
				
				ht.put("post_no", postno);
				ht.put("sheetType", String.valueOf(hto.get("sheettype"))) ;
				vtorders.add(ht);
			}
		}
		catch(SQLException sqle)
		{
			Log.error(jobname, "��ѯ��������Ϣ����:"+sqle.getMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace() ;
		}
		return vtorders;
	}
	
	private String getCompnayID(String companycode)
	{
		String companyid="";
		
	
		String com[] = Params.company.split(";") ;
		for(int i = 0 ; i < com.length ; i++)
		{
			String s[] = com[i].split(":") ;
			if(s[0].equals(companycode))
			{
				companyid=s[1];
				break;
			}
		}
		
		return companyid;
		
	}
	

	//����  --�Լ���ϵ����������������
	private boolean delivery(String jobname,String orderId,String logisticsId,String waybill,String orderdetailids,String SERVER_URL,String token,String appKey,String appSecret)
	{
		boolean flag = false ;
		try 
		{

			String timenow=Formatter.format(new Date(), Formatter.DATE_TIME_MS_FORMAT);
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("memberId",Params.sellerMemberId);
			params.put("orderId", orderId);
			params.put("orderEntryIds", orderdetailids);				//������ϸIDs
			params.put("tradeSourceType", "cbu-trade");
			params.put("logisticsCompanyId", logisticsId);			//������˾ID
																	//����������˾����
			params.put("logisticsBillNo", waybill);					//�˻�����
			params.put("gmtSystemSend", timenow);						//ϵͳ����ʱ��
			params.put("gmtLogisticsCompanySend", timenow);				//���ҷ���ʱ��
			params.put("gmtLogisticsCom", timenow);				//���ҷ���ʱ��
			params.put("access_token", token);
			String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"e56.logistics.offline.send",Params.version,Params.requestmodel,Params.appkey);
			String response = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
			
			JSONObject res=new JSONObject(response);
			//״̬��
			boolean code = res.getBoolean("success") ;
			
			if(code)
			{
				flag = true ;
				Log.info("���·�����Ϣ�ɹ�������Ͱ͵��š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "��") ;
			}
			else
			{
				if("35".equals(code) || "61".equals(code))
					flag = true ;
				Log.info("���·�����Ϣʧ�ܣ�����Ͱ͵��š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "����������Ϣ��" + res.getString("resultMsg") + "," + res.getString("resultCode")) ;
			}
		} catch (Exception e) {
			flag = false ;
			Log.info("���·�����Ϣʧ�ܣ�����Ͱ͵��š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "����������Ϣ��" + e.getMessage()) ;
		}
		return flag ;
	}


	//�޸Ŀ����Ϣ V2
	private boolean ModifyExpressInfo(String jobname,String orderId,String logisticsId,String waybill,String orderdetailids,String SERVER_URL,String token,String appKey,String appSecret)
	{
		boolean flag = false ;
		try 
		{
			String timenow=Formatter.format(new Date(), Formatter.DATE_TIME_MS_FORMAT);
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("memberId",Params.sellerMemberId);
			params.put("orderId", orderId);
			params.put("orderEntryIds", orderdetailids);				//������ϸIDs
			params.put("tradeSourceType", "cbu-trade");
			params.put("logisticsCompanyId", logisticsId);			//������˾ID
																//����������˾����
			params.put("logisticsBillNo", waybill);					//�˻�����
			params.put("gmtSystemSend", timenow);						//ϵͳ����ʱ��
			params.put("gmtLogisticsCompanySend", timenow);				//���ҷ���ʱ��
			
			params.put("access_token", token);
			String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"e56.logistics.offline.send",Params.version,Params.requestmodel,Params.appkey);
			String response = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
			
			JSONObject res=new JSONObject(response);
			//״̬��
			boolean code = res.getBoolean("success") ;
			if(code)
			{
				flag = true ;
				Log.info("���ת���ɹ�,�����š�"+ orderId +"��,��ݹ�˾��"+ logisticsId +"��,��ݵ��š�" + waybill +"��") ;
			}
			else
			{
				flag = false ;
				Log.error(jobname, "���ת��ʧ��,�����š�"+ orderId +"��,��ݹ�˾��"+ logisticsId +"��,��ݵ��š�" + waybill + "��,������Ϣ:"+res.getString("resultMsg") + "," + res.getString("resultCode")) ;
			}
			 
		} catch (Exception e) {
			flag = false ;
			Log.error(jobname, "���ת��ʧ��,�����š�"+ orderId +"��,��ݹ�˾��"+ logisticsId +"��,��ݵ��š�" + waybill + "��,������Ϣ:"+e.getMessage()) ;
		}
		return flag ;
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	
}
