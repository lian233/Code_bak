package com.wofu.netshop.dangdang.fenxiao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import java.net.URLEncoder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.netshop.dangdang.fenxiao.CommHelper;
import com.wofu.netshop.dangdang.fenxiao.Params;
import com.wofu.common.tools.util.DOMHelper;
/**
 * Ģ���ַ����߳���
 * @author bolinli
 *
 */
public class DeliveryRunnable implements Runnable{
	private String jobname="����������ҵ";
	private CountDownLatch watch;
	private String username="";
	private Params param;
	private  String tradecontactid=param.tradecontactid ;
	private  String encoding = param.encoding ;
	private  String codDeliveryBeginTime = param.codDeliveryBeginTime ;
	private  String codDeliveryEndTime = param.codDeliveryEndTime ;
	private  Hashtable<String, String> htComCode = param.htComCode ;
	private  Hashtable<String, String> htComTel = param.htComTel ;
	private boolean is_exporting = false;
	public DeliveryRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
	}
	public void run() {
		Connection conn=null;
		username = param.username;
		Connection connection = null;
		try{
			conn=PoolHelper.getInstance().getConnection("shop");
			connection = PoolHelper.getInstance().getConnection(param.dbname);
			delivery(conn);
		}catch(Exception e){
			try {
				if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				conn.setAutoCommit(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					Log.error(username,"�ر����ݿ��������  "+e1.getMessage(),null);
				}
				Log.info(username,"�����̴߳���: "+e.getMessage(),null);
			}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.error(username,"�ر����ݿ����ӳ���: "+e.getMessage(),null);
				}
				watch.countDown();
		}
		
	}
	
	
	private void delivery(Connection conn)  throws Exception
	{
		String sql = "select a.id,a.tid,a.companycode,a.outsid from itf_delivery a,Inf_UpNote b "
			+"where a.id=b.OperData and a.sheettype=3 and a.shopid="+param.shopid;
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		Log.info("���ζ�������������Ϊ:��"+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderID = hto.get("orderid").toString().replaceAll("[?]", "");
			String postCompany = hto.get("post_company").toString();
			String postNo = hto.get("post_no").toString();
			System.out.println(postCompany);
			try 
			{
				boolean success = false ;
				if("COD".equalsIgnoreCase(postCompany) || "�����Է�".equals(postCompany))
					success = codOrderPrint(jobname,orderID) ;//ֻ��Ҫ�򵥼��ɣ�����Ҫ��־ȡ������success = codDelivery(jobname, orderID) ;
				else
					success = delivery(jobname, conn, hto) ;
				if(success)
				{
					conn.setAutoCommit(false);
	
					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
							+ " where SheetID = '"+ sheetid+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
	
					sql = "delete from IT_UpNote where SheetID='"+ sheetid + "' and sheettype=3";
					SQLHelper.executeSQL(conn, sql);
					
					/*
					if(!"COD".equalsIgnoreCase(postCompany) && !"�����Է�".equals(postCompany))
					{
						sql = "select count(*) from ecs_deliveryresult with(nolock) where ordercode='"+orderID+"'";
						if(SQLHelper.intSelect(conn, sql) <= 0)
						{
							sql = "select purdate from customerorder with(nolock) where refsheetid='"+ orderID +"'" ;
							String createTime = SQLHelper.strSelect(conn, sql) ;
							sql = "insert into ecs_deliveryresult(orgid,ordercode,companycode,outsid,trancompanycode,tranoutsid,status,isupdate,resultflag,msg,createtime,updatetime) "
			            		+ "values('28','"+ orderID +"','"+ postCompany +"','"+ postNo +"','','','-2','0','0','','"+ createTime +"','"+createTime+"')" ;
			        		SQLHelper.executeSQL(conn, sql) ;
						}
						else
						{
							sql = "update ecs_deliveryresult set companycode='"+ postCompany +"',outsid='"+ postNo +"' where ordercode='"+ orderID +"'" ;
							Log.info(sql) ;
							SQLHelper.executeSQL(conn, sql) ;
						}
					}
					*/
					
					conn.commit();
					conn.setAutoCommit(true);	
				}
			}
			catch (Throwable e) 
			{
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.info("���·�����Ϣʧ�ܣ��������š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
				continue ;
			}
			
		}
		Log.info("���ζ��������������!");
	}
	

	
	
	private Vector<Hashtable> getDeliveryOrders(Connection conn)
	{	
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			//����COD����ֻ�ڸ�ʱ����ڸ���ȡ����־
			Date currentDateTime = new Date(System.currentTimeMillis()) ;
			Date begin = Formatter.parseDate(Formatter.format(currentDateTime, Formatter.DATE_FORMAT) + " "+codDeliveryBeginTime, Formatter.DATE_TIME_FORMAT) ;
			Date end = Formatter.parseDate(Formatter.format(currentDateTime, Formatter.DATE_FORMAT) + " "+codDeliveryEndTime, Formatter.DATE_TIME_FORMAT) ;
				
			int beginFlag = currentDateTime.compareTo(begin) ;
			int endFlag = currentDateTime.compareTo(end) ;
			if(beginFlag >= 0 && endFlag <= 0)
			{
				sql = "select  a.sheetid,b.tid, b.companycode,b.outsid,c.defaultarrivedays from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock)"
					+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
					+ tradecontactid + "' and b.companycode=c.companycode";
			}
			else
			{
				sql = "select  a.sheetid,b.tid, b.companycode,b.outsid,c.defaultarrivedays from it_upnote a with(nolock), ns_delivery b with(nolock),deliveryref c with(nolock)"
					+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
					+ tradecontactid + "' and b.companycode=c.companycode and b.companyCode <> 'COD'";
			}
			
	
			Vector vt=SQLHelper.multiRowSelect(conn, sql);

			for(int k=0; k<vt.size();k++)
			{
				Hashtable<String,String> ht=new Hashtable<String,String>();
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(k);
				ht.put("sheetid", hto.get("sheetid").toString());
				ht.put("orderid", hto.get("tid").toString());
				ht.put("post_company", hto.get("companycode").toString());
				ht.put("post_company_tel", hto.get("companycode")) ;
				ht.put("post_no", hto.get("outsid").toString());
				//Log.info(ht.toString()) ;
				vtorders.add(ht);
			}
		}
		catch(Throwable e)
		{
			Log.error(jobname, "��ѯ��������Ϣ����:"+e.getMessage());
			e.printStackTrace() ;
		}
		return vtorders;
	}
	
	private  String getDeliveryXmlStr(Connection conn,Hashtable ht)
	{
		StringBuffer sb = new StringBuffer() ;
		//String sheetid = ht.get("sheetid").toString() ;
		String orderid = ht.get("orderid").toString() ;
		String post_company = htComCode.get(ht.get("post_company").toString()) ;
		String post_company_tel = htComTel.get(ht.get("post_company_tel").toString()) ;
		String post_no = ht.get("post_no").toString() ;
		String sql = "select skuid,num from ns_orderItem with(nolock) where sheetid=(select top 1 sheetid from ns_customerOrder with(nolock) where tid='"+orderid+"' order by sheetid desc)" ;
		try 
		{	
			sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>") ;
			sb.append("<request>") ;
			sb.append("<functionID>dangdang.order.goods.send</functionID>");
			sb.append("<time>").append(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)).append("</time>");
			sb.append("<OrdersList>");
			sb.append("<OrderInfo>");
			sb.append("<orderID>").append(orderid).append("</orderID>");
			sb.append("<logisticsName>").append(post_company).append("</logisticsName>");
			sb.append("<logisticsTel>").append(post_company_tel).append("</logisticsTel>");
			sb.append("<logisticsOrderID>").append(post_no).append("</logisticsOrderID>");
			sb.append("<SendGoodsList>") ;
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			if(vt.size()==0){
				sql="select skuid,num from ns_orderItembak with(nolock) where sheetid=(select top 1 sheetid from ns_customerOrderbak with(nolock) where tid='"+orderid+"' order by sheetid desc)" ;
				vt=SQLHelper.multiRowSelect(conn, sql);
			}
			for(int i=0; i<vt.size();i++)
			{
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(i);
				sb.append("<ItemInfo>");
				sb.append("<itemID>").append(hto.get("skuid")).append("</itemID>");
				sb.append("<sendGoodsCount>").append(String.valueOf(hto.get("num"))).append("</sendGoodsCount>");
				sb.append("</ItemInfo>");
			}
			sb.append("</SendGoodsList>");
			sb.append("</OrderInfo>");
			sb.append("</OrdersList>");
			sb.append("</request> ");
		} catch (Throwable e) {
			Log.error("���µ���������Ϣ", "����xml�ַ���ʧ�ܣ�������Ϣ��"+e.getMessage()) ;
			return "" ;
		}
		return sb.toString() ;
	}
		
	//��ȡ����������
	private  boolean codOrderPrint(String jobname,String orderid)
	{
		boolean flag = false ;
		try 
		{
			Date temp = new Date();
			//������
			String methodName="dangdang.order.receipt.details.get";
			//������֤�� --md5;����
			String sign = CommHelper.getSign(param.app_Secret, param.app_key, methodName, param.session,temp) ;
			
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("sign", sign) ;
			params.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
			params.put("app_key",param.app_key);
			params.put("method",methodName);
			params.put("format","xml");
			params.put("session",param.session);
			params.put("sign_method","md5");
			params.put("o", orderid) ;
			//Log.info(requestUrl);
			String responseText = CommHelper.sendRequest(param.url,"GET",params,"");
			
			Document doc = DOMHelper.newDocument(responseText, encoding) ;
			Element result = doc.getDocumentElement() ;
			
			//�ж����޴���
			if(DOMHelper.ElementIsExists(result, "Error"))
			{
				Element error = (Element) result.getElementsByTagName("Error").item(0);
				String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
				
				Log.error("COD������", "��ʧ��,������:"+ orderid +",������Ϣ:"+operCode+","+operation);
				return flag;
			}
			
			Element orderCourierReceiptDetails = (Element) result.getElementsByTagName("orderCourierReceiptDetails").item(0) ;
			Element courierReceiptDetail = (Element) orderCourierReceiptDetails.getElementsByTagName("courierReceiptDetail").item(0) ;
			String orderID = DOMHelper.getSubElementVauleByName(courierReceiptDetail, "orderID") ;
			String operCode = DOMHelper.getSubElementVauleByName(courierReceiptDetail, "operCode") ;
			if(orderid.equals(orderID) && "".equals(operCode))
			{
				Log.info("COD�����򵥳ɹ������������ţ�"+orderid) ;
				flag = true ;
			}
			else
			{
				String operation = DOMHelper.getSubElementVauleByName(courierReceiptDetail, "operation") ;
				Log.error("COD������", "��ʧ��,������:"+ orderid +",������Ϣ:"+operCode+","+operation);
				flag = false ;
			}
			
			return flag ;
			
		} catch (Throwable e) {
			Log.error(jobname, "��ȡʧ�ܣ����������ţ�"+orderid+",������Ϣ��"+e.getMessage()) ;
			return false ;
		}
	}
	//����COD�������� ����Ҫ����
	/*private static boolean codDelivery(String jobname,String orderid)
	{
		boolean flag = false ;
		try 
		{
			if(!codOrderPrint(jobname,orderid))
				return flag;
			String gbkValuesStr = Coded.getEncode(gShopID, encoding).concat(Coded.getEncode(orderid, encoding)).concat(Coded.getEncode(key, encoding)) ;
			StringBuffer sb = new StringBuffer() ;
			sb.append(url).append("/updateOrdersSendGoodsStatus.php") ;
			String validateString = MD5Util.getMD5Code(gbkValuesStr.getBytes()) ;
			
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("gShopID", gShopID) ;
			params.put("o", orderid) ;
			params.put("validateString", validateString) ;
			
			String requestUrl = sb.toString() ;
			String responseText = CommHelper.sendRequest(requestUrl, "GET", params, "=") ;
			//System.out.println(responseText) ;
			Document doc = DOMHelper.newDocument(responseText, encoding) ;
			Element result = doc.getDocumentElement() ;
//			�жϷ����Ƿ���ȷ
			try
			{
				Element error = (Element) result.getElementsByTagName("Error").item(0);
				String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
				if(!"".equals(operCode))
				{
					Log.error("���µ���COD����", "�ϴ�������Ϣʧ�ܣ�"+operCode+":"+operation);
					return flag;
				}
			} catch (Exception e) {
			}
			Element itemsIDList = (Element) result.getElementsByTagName("ItemsIDList").item(0) ;
			NodeList itemIDInfoList = result.getElementsByTagName("ItemIDInfo") ;
			for(int i = 0 ; i < itemIDInfoList.getLength() ; i++ )
			{
				Element itemIDInfo = (Element) itemIDInfoList.item(i) ;
				String itemID = DOMHelper.getSubElementVauleByName(itemIDInfo, "itemID") ;
				String operCode = DOMHelper.getSubElementVauleByName(itemIDInfo, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(itemIDInfo, "operation") ;
				if("0".equals(operCode))
				{
					Log.info("���µ���COD���������ɹ���������:"+orderid) ;
					flag = true ;
				}
				else
				{
					Log.error("���µ���COD��������","���µ���COD��������ʧ�ܣ�������:"+orderid+"��������Ϣ:"+operCode+operation) ;
					flag = false ;
				}
			}
			return flag ;
		} catch (Exception e) {
			// TODO: handle exception
			Log.error("���µ���COD��������","���µ���COD���������ɹ���������:"+orderid+".������Ϣ:"+e.getMessage()) ;
			return false ;
		}
	}
*/
	//�Է�������--���·���״̬
	private   boolean delivery(String jobname,Connection conn,Hashtable<String, String> hto)
	{
		boolean flag = false ;
		String orderID = hto.get("orderid").toString();
		String postCompany = htComCode.get(hto.get("post_company").toString());
		String postNo = hto.get("post_no").toString();
		try 
		{	
			//������
			Date temp = new Date();
			String methodName="dangdang.order.goods.send";
			//������֤�� --md5;����
			String sign =  CommHelper.getSign(param.app_Secret, param.app_key, methodName, param.session,temp)  ;
			String xml = getDeliveryXmlStr(conn,hto) ;
		
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("sign", sign) ;
			//SimpleDateFormat sf = 
			params.put("timestamp",Formatter.format(temp,Formatter.DATE_TIME_FORMAT));
			params.put("app_key",param.app_key);
			params.put("method",methodName);
			params.put("format","xml");
			params.put("session",param.session);
			params.put("sign_method","md5");
			//Log.info("xml: "+xml);
			String responseText = CommHelper.sendRequest(param.url,"POST",params, "sendGoods="+xml) ;
			//Log.info("resonseText: "+responseText);
			
			Document doc = DOMHelper.newDocument(responseText, "GBK") ;
			Element result = doc.getDocumentElement() ;
			
			//�жϷ����Ƿ���ȷ
		
			if (DOMHelper.ElementIsExists(result, "Error"))
			{
				Element error = (Element) result.getElementsByTagName("Error").item(0);
				String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
				if(!"".equals(operCode))
				{
					if("1207".equals(operCode) || "51".equals(operCode)){//1207 ������Ų����ڣ���������  51 ����״̬����
						Log.error("���µ�������", "�ϴ�������Ϣʧ�ܣ�����������: "+orderID+",operCode="+operCode+",operation="+operation);
						return true;
					}else{
						Log.error("���µ�������", "�ϴ�������Ϣʧ�ܣ�����������: "+orderID+",operCode="+operCode+",operation="+operation);
						return flag;
					}
					
				}
			}else if(DOMHelper.ElementIsExists(result, "errorCode")){
				
				if("SHOP����APIһ����Ƶ�γ���".equals(DOMHelper.getSubElementVauleByName(result, "errorMessage")) ||DOMHelper.getSubElementVauleByName(result, "errorMessage").indexOf("����Ƶ�ʹ���")!=-1){
					long starttime = System.currentTimeMillis();
					while(System.currentTimeMillis()<starttime+2*60*1000L){
						Thread.sleep(1000L);
					}
				}
			}
			else if(DOMHelper.ElementIsExists(result, "errorCode")){
				String operCode = DOMHelper.getSubElementVauleByName(result, "errorCode") ;
				String errorMessage = DOMHelper.getSubElementVauleByName(result, "errorMessage") ;
				Log.error("���µ�������", "�ϴ�������Ϣʧ�ܣ�����������: "+orderID+",errorCode="+operCode+",errorMessage="+errorMessage);
			}
			else
			{
				Element resultInfo = (Element) result.getElementsByTagName("Result").item(0) ;
				Element ordersList = (Element) resultInfo.getElementsByTagName("OrdersList").item(0) ;
				NodeList orderInfoList = ordersList.getElementsByTagName("OrderInfo") ;
				for(int j = 0 ; j < orderInfoList.getLength() ; j++)
				{
					Element orderInfo = (Element) orderInfoList.item(j) ;
					String orderid = DOMHelper.getSubElementVauleByName(orderInfo, "orderID") ;
					String orderOperCode = DOMHelper.getSubElementVauleByName(orderInfo, "orderOperCode") ;
					String orderOperation = DOMHelper.getSubElementVauleByName(orderInfo, "orderOperation") ;
	
					//�������״̬��Ϊ0���ҷ��ص������ύ������ͬ���򱾴θ��³ɹ�
					if("0".equals(orderOperCode) && orderID.equals(orderid))
					{
						flag = true ;
						Log.info("���·�����Ϣ�ɹ����������š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����") ;
					}
					//�������ţ������ϴ����ظ�����
					else if("35".equals(orderOperCode) || "605".equals(orderOperCode))  //�����Ŵ���Ͷ���״̬����
					{
						Log.error(jobname,"���·�����Ϣʧ�ܣ��������š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������룺" + orderOperCode + "������Ϣ��" + orderOperation) ;
						flag = true ;
					}
					else
					{
						flag = false ;
						Log.error(jobname,"���·�����Ϣʧ�ܣ��������š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������룺" + orderOperCode + "������Ϣ��" + orderOperation) ;
					}
				}
			}
			
		} catch (Throwable e) {
			Log.info("���·�����Ϣʧ�ܣ��������š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������Ϣ��" + e.getMessage()) ;
			flag=false ;
		}
		
		return flag ;
	}
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
	
}
