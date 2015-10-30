package com.wofu.ecommerce.qqbuy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.qqbuy.Params;
import com.wofu.ecommerce.qqbuy.oauth.PaiPaiOpenApiOauth;

public class OrderDelivery extends Thread {

	private static String jobname = "QQ������������������ҵ";

	private static String accessToken = Params.accessToken ;
	private static String appOAuthID = Params.appOAuthID ;
	private static String secretOAuthKey = Params.secretOAuthKey ;
	private static String cooperatorId = Params.cooperatorId ;
	private static String uin = Params.uin ;
	private static String encoding = Params.encoding ;
	private static String format = Params.format ;
	private static Hashtable<String, String> expressCompanyIdTable = Params.expressCompanyIdTable ;

	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;
			is_exporting = true;
			try {		
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.qqbuy.Params.dbname);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.qqbuy.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	public static void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Exception
	{
		if(vdeliveryorder.size() <= 0)
			return ;
		Hashtable<String, String> params = new Hashtable<String, String>() ;
		params.put("accessToken", accessToken) ;
		params.put("appOAuthID", appOAuthID) ;
		params.put("secretOAuthKey", secretOAuthKey) ;
		params.put("cooperatorId", cooperatorId) ;
		params.put("uin", uin) ;
		params.put("encoding", encoding) ;
		params.put("format", format) ;
		String sql = "" ;
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderID = hto.get("orderid").toString();
			String postCompany = hto.get("post_company").toString();
			String postNo = hto.get("post_no").toString();
			String expressCompanyId = hto.get("expressCompanyId").toString() ;
			String expressCompanyName = Params.expressCompanyNameTable.get(postCompany) ;
			//���·�����Ϣ
			boolean success = orderDelivery(jobname, orderID, postCompany, postNo, expressCompanyId,expressCompanyName,params) ;
			if(success)
			{
				conn.setAutoCommit(false);

				sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
						+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);

				sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";
				SQLHelper.executeSQL(conn, sql);
				
				//���뵽ecs_deliveryresult,ͬ�����ͽ��
				sql = "select count(*) from ecs_deliveryresult with(nolock) where ordercode='"+hto.get("orderid").toString()+"'";
				if(SQLHelper.intSelect(conn, sql) <= 0)
				{
					sql = "select purdate from customerorder with(nolock) where refsheetid='"+ hto.get("orderid").toString() +"'" ;
					String createTime = SQLHelper.strSelect(conn, sql) ;
					sql = "insert into ecs_deliveryresult(orgid,ordercode,companycode,outsid,trancompanycode,tranoutsid,status,isupdate,resultflag,msg,createtime,updatetime) "
	            		+ "values('31','"+ hto.get("orderid").toString() +"','"+ postCompany +"','"+ postNo +"','','','-2','0','0','','"+ createTime +"','"+ createTime +"')" ;
	        		SQLHelper.executeSQL(conn, sql) ;
				}
				else
				{
					sql = "update ecs_deliveryresult set companycode='"+ postCompany +"',outsid='"+ postNo +"' where ordercode='"+ hto.get("orderid").toString() +"'" ;
					SQLHelper.executeSQL(conn, sql) ;
				}
				
				conn.commit();
				conn.setAutoCommit(true);
			}
		}
	}

	//���¶���������Ϣ
	public static boolean orderDelivery(String jobname,String orderId,String companyCode,String outsid,String expressCompanyId,String expressCompanyName,Hashtable<String, String> inputParams)
	{
		boolean flag = false ;
		String responseText = "" ;
		String uri = "/deal/signShipV2.xhtml" ;
		String transportType = "" ;//����������ʽ 1:��������� 2:�Խ����� 3:EMS���� 4:�û���ȡ
		
		String appOAuthID = inputParams.get("appOAuthID") ;
		String secretOAuthKey = inputParams.get("secretOAuthKey") ;
		String accessToken = inputParams.get("accessToken") ;
		String cooperatorId = inputParams.get("cooperatorId") ;
		String encoding = inputParams.get("encoding") ;
		String uin = inputParams.get("uin") ;
		String format = inputParams.get("format") ;
		try 
		{
			if("ems".equalsIgnoreCase(companyCode))
				transportType = "3" ;
			else if("����".equals(companyCode))
				transportType = "4" ;
			else
				transportType = "1" ;
				
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, Long.parseLong(uin));
			sdk.setCharset(encoding) ;
			HashMap<String, Object> params = sdk.getParams(uri);
			params.put("charset", encoding) ;
			params.put("format", format) ;
			params.put("cooperatorId", cooperatorId) ;
			params.put("dealId", orderId) ;
			params.put("transportType", transportType) ;
			params.put("expArriveDays", "5") ;
			params.put("expressCompanyId", expressCompanyId) ;
			params.put("expressName", expressCompanyName) ;
			params.put("expressDealId", outsid) ;
			
			responseText = sdk.invoke() ;
			Document doc = DOMHelper.newDocument(responseText, encoding);
			Element resultElement = doc.getDocumentElement();
			String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
			String dealId = DOMHelper.getSubElementVauleByName(resultElement, "dealId").trim() ;
			if("0".equals(errorCode) && orderId.equals(dealId))
			{
				flag = true ;
				Log.info("����QQ�����������������Ϣ�ɹ�,�����š�"+ orderId +"��,��ݹ�˾��"+ companyCode +"��,��ݵ��š�"+ outsid +"��") ;
			}
			else
			{
				//����������,����Ҫ����
				if("2304".equals(errorCode))
					flag = true ;
				else
					flag = false ;
				String errorMessage  = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
				Log.error(jobname,"����QQ�����������������Ϣʧ��,�����š�"+ orderId +"��,������Ϣ:"+errorCode+errorMessage) ;
			}
		} 
		catch (Exception e) {
			flag = false ;
			Log.error(jobname, "����QQ�����������������Ϣʧ��,������Ϣ:"+ e.getMessage() + ",����ֵ:"+ responseText) ;
		}
		return flag ;
	}
	
	private Vector<Hashtable> getDeliveryOrders(Connection conn)
	{	
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			sql = "select  a.sheetid,b.tid, b.companycode,b.outsid from it_upnote a with(nolock), ns_delivery b with(nolock) "
				+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
				+ Params.tradecontactid + "'";
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0; i<vt.size();i++)
			{
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(i);
				
				Hashtable<String,String> ht=new Hashtable<String,String>();
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString());
				ht.put("post_company", hto.get("companycode").toString());
				ht.put("post_no", hto.get("outsid").toString());
				ht.put("expressCompanyId", expressCompanyIdTable.get(hto.get("companycode").toString())) ;
				
				vtorders.add(ht);
			}
		}
		catch(SQLException sqle)
		{
			Log.error(jobname, "��ѯ��������Ϣ����:sql="+sql+",������Ϣ"+sqle.getMessage());
		}
		catch(Exception e)
		{
			Log.error(jobname, "��ѯ��������Ϣ����:"+e.getMessage());
		}
		return vtorders;
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	
}
