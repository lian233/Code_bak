package com.wofu.ecommerce.dangdang.test;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.ecommerce.dangdang.ReturnOrder;
import com.wofu.ecommerce.dangdang.ReturnOrderItem;
import com.wofu.ecommerce.dangdang.util.CommHelper;
import com.wofu.common.tools.conv.Coded;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;

public class Test4 {

	private static String gShopID="6888";
	private static 	String key="yongjunit.818";
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	private static String url = "http://api.dangdang.com/v2" ;
	private static String encoding= "GBK";
	private static long daymillis=24*60*60*1000L;
	private static String refundDesc[] = {"","�˻�","����",""} ;
	public static void main(String[] args) {
		
		//canaleOrder("ȡ����������", "123456789", "800003") ;
		//getRefund("","10") ;
		codPrintOrder("", "5158725805") ;
	}

//	��ȡ����������
	private static boolean codPrintOrder(String jobname,String orderid)
	{
		boolean flag = false ;
		try 
		{
			//System.out.println("sdfslj") ;
			String gbkValuesStr = Coded.getEncode(gShopID, encoding).concat(Coded.getEncode(orderid, encoding)).concat(Coded.getEncode(key, encoding)) ;
			StringBuffer sb = new StringBuffer() ;
			sb.append(url).append("/getMultiOrderCourierReceiptDetails.php") ;
			String validateString = MD5Util.getMD5Code(gbkValuesStr.getBytes()) ;
			
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("gShopID", gShopID) ;
			params.put("o", orderid) ;
			params.put("validateString", validateString) ;
			
			String requestUrl = sb.toString() ;
			String responseText = CommHelper.sendRequest(requestUrl, "GET", params, "=") ;
			System.out.println(responseText) ;
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
			
		} catch (Exception e) {
			Log.error(jobname, "��ȡʧ�ܣ����������ţ�"+orderid+",������Ϣ��"+e.getMessage()) ;
			return false ;
		}
	}
	
	private static void getRefund(String jobname,String tradecontactid)
	{
		String sql = "" ;
		try 
		{
			int pageIndex = 1 ;
//			String beginTime = "2012-05-01" ;
//			String endTime = "2012-05-30" ;
			String status = "1" ;//����״̬ 1:������ 2:�Ѵ��� ���ڵ�����̨���ͬ���ͬ��
			//String result = "1" ;//������ 1:ͬ�� 2:�ܾ� 3:����
			String gbkValuesStr = Coded.getEncode(gShopID, encoding)
								  .concat(Coded.getEncode(String.valueOf(pageIndex), encoding))
//								  .concat(Coded.getEncode(endTime, encoding))
//								  .concat(Coded.getEncode(beginTime, encoding))
//								  .concat(Coded.getEncode(result, encoding))
								  .concat(Coded.getEncode(status, encoding))
								  .concat(Coded.getEncode(key, encoding)) ;
			String validateString = MD5Util.getMD5Code(gbkValuesStr.getBytes()) ;
			
			StringBuffer sb = new StringBuffer() ;
			sb.append(url).append("/searchReturnExchangeOrders.php") ;
			String reqUrl  = sb.toString() ;
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("gShopID", gShopID) ;
			params.put("p", String.valueOf(pageIndex)) ;
//			params.put("reed", endTime) ;
//			params.put("resd", beginTime) ;
//			params.put("result", result) ;
			params.put("status", status) ;
			params.put("validateString",validateString) ;
			
			System.out.println(params.toString()) ;
			
			String resultText = CommHelper.sendRequest(reqUrl, "GET", params, "") ;
			System.out.println(resultText) ;
			
			Document doc = DOMHelper.newDocument(resultText, encoding) ;
			Element resultElement = doc.getDocumentElement() ;
//			�жϷ����Ƿ���ȷ
			try
			{
				Element error = (Element) resultElement.getElementsByTagName("Error").item(0);
				String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
				if(!"".equals(operCode))
				{
					Log.error(jobname, "��ȡ�����˻�������ʧ��,����ԭ��:"+operCode+operation) ;
					return ;
				}
			} catch (Exception e) {
			}
			
			Element totalInfo = (Element) resultElement.getElementsByTagName("totalInfo").item(0) ;
			String pageTotal = DOMHelper.getSubElementVauleByName(totalInfo, "pageTotal") ;
			System.out.println("pageTotal="+pageTotal) ;
			
			Element ordersList = (Element)resultElement.getElementsByTagName("OrdersList").item(0) ;
			NodeList orderInfo = ordersList.getElementsByTagName("OrderInfo") ;
			for(int i = 0 ; i < orderInfo.getLength() ; i++)
			{
				ReturnOrder o = new ReturnOrder() ;
				Element order = (Element) orderInfo.item(i) ;
				String orderID = DOMHelper.getSubElementVauleByName(order, "orderID") ;
				String returnExchangeStatus = DOMHelper.getSubElementVauleByName(order, "returnExchangeStatus") ;
				String returnExchangeCode = DOMHelper.getSubElementVauleByName(order, "returnExchangeCode") ;
				String orderMoney = DOMHelper.getSubElementVauleByName(order, "orderMoney") ;
				String orderTime = DOMHelper.getSubElementVauleByName(order, "orderTime") ;
				String orderStatus = DOMHelper.getSubElementVauleByName(order, "orderStatus") ;
				String returnExchangeOrdersApprStatus = DOMHelper.getSubElementVauleByName(order, "returnExchangeOrdersApprStatus") ;
				
				//�˻����������״̬��ֻ���ɴ���������ͨ����	0:��˲�ͨ�� 1:���ͨ�� 2:�����
				if(!"1".equals(returnExchangeOrdersApprStatus))
					continue ;
				
				if("".equals(orderMoney) || orderMoney == null)
					orderMoney = "0" ;
				o.setOrderID(orderID) ;
				o.setReturnExchangeStatus(returnExchangeStatus) ;
				o.setReturnExchangeCode(returnExchangeCode) ;
				o.setOrderMoney(Float.parseFloat(orderMoney)) ;
				o.setOrderTime(Formatter.parseDate(orderTime, Formatter.DATE_TIME_FORMAT)) ;
				o.setOrderStatus(orderStatus) ;
				o.setReturnExchangeOrdersApprStatus(returnExchangeOrdersApprStatus) ;
				
				System.out.println("-----------------------------------------------") ;
				System.out.println("orderID="+orderID) ;
				System.out.println("returnExchangeStatus="+returnExchangeStatus) ;
				System.out.println("returnExchangeCode="+returnExchangeCode) ;
				System.out.println("orderMoney="+orderMoney) ;
				System.out.println("orderTime="+orderTime) ;
				System.out.println("orderStatus="+orderStatus) ;
				System.out.println("returnExchangeOrdersApprStatus="+returnExchangeOrdersApprStatus) ;
				
				Element itemsList = (Element) order.getElementsByTagName("itemsList").item(0) ;
				NodeList itemInfo = itemsList.getElementsByTagName("ItemInfo") ;
				ArrayList<ReturnOrderItem> itemList = new ArrayList<ReturnOrderItem>() ;
				for(int j = 0 ; j < itemInfo.getLength() ; j ++)
				{
					ReturnOrderItem orderitem = new ReturnOrderItem() ;
					
					Element item = (Element) itemInfo.item(j) ;
					String itemID = DOMHelper.getSubElementVauleByName(item, "itemID") ;
					String itemName = DOMHelper.getSubElementVauleByName(item, "itemName") ;
					String itemSubhead = DOMHelper.getSubElementVauleByName(item, "itemSubhead") ;
					String unitPrice = DOMHelper.getSubElementVauleByName(item, "unitPrice") ;
					String orderCount = DOMHelper.getSubElementVauleByName(item, "orderCount") ;
					String outerItemID = DOMHelper.getSubElementVauleByName(item, "outerItemID") ;
					
					if("".equals(unitPrice) || unitPrice == null)
						unitPrice = "0" ;
					
					if("".equals(orderCount) || orderCount == null)
						orderCount = "0" ;
					
					orderitem.setItemID(itemID) ;
					orderitem.setItemName(itemName) ;
					orderitem.setItemSubhead(itemSubhead) ;
					orderitem.setUnitPrice(Float.parseFloat(unitPrice)) ;
					orderitem.setOrderCount(Integer.parseInt(orderCount)) ;
					orderitem.setOuterItemID(outerItemID) ;
					
					itemList.add(orderitem) ;
					
					System.out.println("itemID="+itemID) ;
					System.out.println("itemName="+itemName) ;
					System.out.println("itemSubhead="+itemSubhead) ;
					System.out.println("unitPrice="+unitPrice) ;
					System.out.println("orderCount="+orderCount) ;
					System.out.println("outerItemID="+outerItemID) ;
				}
				o.setItemList(itemList) ;
				createRefundOrder("", null, tradecontactid, o) ;
			}
			
			
			
		} catch (Exception e) 
		{
			Log.error(jobname, "��ȡ�����˻�����ʧ�ܣ�������Ϣ:"+e.getMessage()) ;
			e.printStackTrace() ;
		}
	}
	
	private static void createRefundOrder(String jobname,Connection conn,String tradecontactid,ReturnOrder o)
	{
		String sql = "" ;
		try 
		{
			ArrayList<ReturnOrderItem> itemList = o.getItemList() ;
			sql = "select count(*) as num from ns_refund with(nolock) where refundid='"+o.getReturnExchangeCode()+"'" ;
			Log.info(sql) ;
			int count = 0 ;//SQLHelper.intSelect(conn, sql) ;
			if(count == itemList.size())
				return ;
			for(int i=0 ; i<itemList.size() ; i++)
			{
				try 
				{
					ReturnOrderItem item = itemList.get(i) ;
					sql="select shopid from ContactShopContrast with(nolock) where tradecontactid="+tradecontactid;
		            String inshopid="";//SQLHelper.strSelect(conn, sql);
		            //conn.setAutoCommit(false);
		            
		            sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";			
					Log.info(sql) ;
		            String sheetid="";//SQLHelper.strSelect(conn, sql);
//					if (sheetid.trim().equals(""))
//						throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");
					
		            //SQLHelper.executeSQL(conn, sql);
					
					sql = "insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , BuyerNick , "
						+ "Created , Modified , OrderStatus , Status , GoodStatus , "
	                    + " HasGoodReturn ,RefundFee , Payment , Reason,Description ,"
	                    + " Title , Price , Num , GoodReturnTime , Sid , "
	                    + " TotalFee , Iid , OuterIid , OuterSkuId , CompanyName ," 
	                    + " Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
	                    + " values('" + sheetid + "' , '" + o.getReturnExchangeCode() + "' , '" + o.getReturnExchangeCode() + "' , '' , '' ,"
	                    + "'" + Formatter.format(o.getOrderTime(), Formatter.DATE_TIME_FORMAT) + "','" + Formatter.format(o.getOrderTime(), Formatter.DATE_TIME_FORMAT) + "','"+ o.getOrderStatus() +"','','"+ o.getReturnExchangeStatus() +"',"
	                    + "'1','0','"+ o.getOrderMoney() +"','','"+getRefundDesc(o.getReturnExchangeStatus())+"',"
	                    + "'" + item.getItemName() + "','" + item.getUnitPrice() + "','"+ item.getOrderCount() +"','" + Formatter.format(o.getOrderTime(), Formatter.DATE_TIME_FORMAT) + "','',"
	                    + "'"+ o.getOrderMoney() +"','"+ item.getItemID() +"','','" + item.getOuterItemID() + "',''," 
	                    + "'','','" + inshopid + "','" + o.getOrderID() + "','','','')" ;

					Log.info(sql) ;
					SQLHelper.executeSQL(conn,sql);
					
//					���뵽֪ͨ��
		            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
		                + sheetid +"',2 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";				
					Log.info(sql) ;
					//SQLHelper.executeSQL(conn,sql);
					Log.info(jobname,"�ӿڵ���:"+sheetid+" �˻�������:"+o.getReturnExchangeCode()+"����������ʱ��:"+Formatter.format(o.getOrderTime(),Formatter.DATE_TIME_FORMAT));
//					conn.commit();
//					conn.setAutoCommit(true);
				}
				catch (SQLException e1)
				{			
					if (!conn.getAutoCommit())
						try
						{
							conn.rollback();
						}
						catch (Exception e2) { }
					try
					{
						conn.setAutoCommit(true);
					}
					catch (Exception e3) { }
					throw new JSQLException("�����˻���" + o.getOrderID() + "���ӿ�����ʧ��!"+e1.getMessage());
				}
			}
		}
		catch (Exception e) 
		{
			Log.error(jobname, "���ɽӿ��˻���ʧ��,������:"+o.getOrderID() + ",�˻�������:"+o.getReturnExchangeCode()+",�˻�������:"+o.getReturnExchangeStatus()+",������Ϣ:"+e.getMessage()) ;
			e.printStackTrace() ;
		}
	}
	
	private static void canaleOrder(String jobname,String orderID,String cancleReason)
	{
		try 
		{
			String gbkValuesStr = Coded.getEncode(cancleReason, encoding)
								 .concat(Coded.getDecode(gShopID, encoding))
								 .concat(Coded.getEncode(orderID, encoding))
								 .concat(Coded.getEncode(key, encoding)) ;
			String validateString = MD5Util.getMD5Code(gbkValuesStr.getBytes()) ;
			StringBuffer sb = new StringBuffer() ;
			sb.append(url).append("/cancelOrder.php") ;
			String reqUrl  = sb.toString() ;
			System.out.println(reqUrl) ;
			
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("cr", cancleReason) ;
			params.put("gShopID", gShopID) ;
			params.put("o", orderID) ;
			params.put("validateString", validateString) ;
			
			String resultText = CommHelper.sendRequest(reqUrl, "GET", params, "") ;
			System.out.println("resultText="+resultText) ;
			Document doc = DOMHelper.newDocument(resultText, encoding) ;
			Element result = doc.getDocumentElement() ;
//			�жϷ����Ƿ���ȷ
			try
			{
				Element error = (Element) result.getElementsByTagName("Error").item(0);
				String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
				if(!"".equals(operCode))
				{
					Log.error(jobname, "ȡ����������ʧ��,�����š�"+orderID+"��,ȡ��ԭ��:"+cancleReason+",����ԭ��:"+operCode+operation) ;
					return ;
				}
			} catch (Exception e) {
			}
			
			Element resultInfo = (Element)result.getElementsByTagName("Result").item(0) ; 
			String orderid = DOMHelper.getSubElementVauleByName(resultInfo, "orderID") ;
			String operCode = DOMHelper.getSubElementVauleByName(resultInfo, "operCode") ;
			String operation = DOMHelper.getSubElementVauleByName(resultInfo, "operation") ;
			if(orderID.equals(orderid) && "0".equals(operCode))
			{
				Log.info("ȡ�����������ɹ�,�����š�"+orderID+"��,ȡ��ԭ��:"+cancleReason) ;
			}
			else
			{
				Log.error(jobname, "ȡ����������ʧ��,�����š�"+orderID+"��,ȡ��ԭ��:"+cancleReason+",����ԭ��:"+operCode+operation) ;
			}
				
		} 
		catch (Exception e) 
		{
			Log.error(jobname, "ȡ����������ʧ��,�����š�"+orderID+"��,ȡ��ԭ��:"+cancleReason+",����ԭ��:"+e.getMessage()) ;
			e.printStackTrace() ;
		}
	}

	private static String getRefundDesc(String index)
	{
		try 
		{
			return refundDesc[Integer.parseInt(index)] ;
		} catch (Exception e) {
			return index ;
		}
	}
}
