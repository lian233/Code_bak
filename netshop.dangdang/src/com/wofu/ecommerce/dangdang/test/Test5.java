package com.wofu.ecommerce.dangdang.test;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.ecommerce.dangdang.util.CommHelper;
import com.wofu.common.tools.conv.Coded;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;

public class Test5 {

	private static String shopid="6888";
	private static 	String key="yongjunit.818";
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	private static String url = "http://api.dangdang.com/v2" ;
	private static String encoding= "GBK";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Hashtable<String, String>> list = getOrderIdList(shopid, "101", "9999", "10", key, encoding) ;
		for(int i = (list.size()-1) ; i >= 0 ; i--)
		{
			System.out.println(list.get(i)) ;
			Hashtable<String, String> ht = (Hashtable<String, String>)list.get(i) ;
			System.out.println("orderID="+ht.get("orderID").toString()) ;
			System.out.println("lastModifyTime="+ht.get("lastModifyTime").toString()) ;
		}
		System.out.println(list.size()) ;
	}

	public static ArrayList<Hashtable<String, String>> getOrderIdList(String gShopID,String orderState,String sendMode,String pageSize,String key,String encoding)
	{
		ArrayList<Hashtable<String, String>> orderIDList = new ArrayList<Hashtable<String,String>>() ;
		try 
		{
			int pageIndex = 1 ;
			boolean hasNextPage = true ;
			while(hasNextPage)
			{
				String gbkValueStr = Coded.getDecode(gShopID, encoding)
									 .concat(Coded.getEncode(orderState, encoding))
									 .concat(Coded.getEncode(String.valueOf(pageIndex), encoding))
									 .concat(Coded.getEncode(pageSize, encoding))
									 .concat(Coded.getEncode(sendMode, encoding))
									 .concat(Coded.getDecode(key, encoding));
				String validateString = MD5Util.getMD5Code(gbkValueStr.getBytes()) ;
				StringBuffer sb = new StringBuffer() ;
				sb.append(url).append("/searchOrders.php");
				
				Hashtable<String, String> params = new Hashtable<String, String>() ;
				params.put("gShopID", gShopID) ;
				params.put("os", orderState) ;
				params.put("p", String.valueOf(pageIndex)) ;
				params.put("pageSize", pageSize) ;
				params.put("sendMode", sendMode) ;
				params.put("validateString", validateString) ;
				
				Log.info(String.valueOf(pageIndex)) ;
				
				String requestUrl = sb.toString() ;
				Log.info(requestUrl) ;	
				Log.info(params.toString());
				String reponseText = CommHelper.sendRequest(requestUrl,"GET",params,"");
				Log.info(reponseText) ;
				
				Document doc = DOMHelper.newDocument(reponseText, encoding);
				Element urlset = doc.getDocumentElement();
				
				try 
				{
					Element error = (Element) urlset.getElementsByTagName("Error").item(0);
					String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
					String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
					if(!"".equals(operCode))
					{
						Log.error("当当获取订单列表", "获取订单列表失败，操作码："+operCode+",操作结果信息："+operation);
						hasNextPage = false ;
						break ;
					}
				} catch (Exception e) {
					
				}
				Element totalInfo = (Element) urlset.getElementsByTagName("totalInfo").item(0) ;
				
//				String sendGoodsOrderCount = DOMHelper.getSubElementVauleByName(totalInfo, "sendGoodsOrderCount") ;
//				String needExchangeOrderCount = DOMHelper.getSubElementVauleByName(totalInfo, "needExchangeOrderCount") ;
//				String orderCount = DOMHelper.getSubElementVauleByName(totalInfo, "orderCount") ;
//				String totalOrderMoney = DOMHelper.getSubElementVauleByName(totalInfo, "totalOrderMoney") ;
//				String pageSizeStr = DOMHelper.getSubElementVauleByName(totalInfo, "pageSize") ;
				String pageTotal = DOMHelper.getSubElementVauleByName(totalInfo, "pageTotal") ;
//				String currentPage = DOMHelper.getSubElementVauleByName(totalInfo, "currentPage") ;
//				Log.info("sendGoodsOrderCount="+sendGoodsOrderCount) ;
//				Log.info("needExchangeOrderCount="+needExchangeOrderCount) ;
//				Log.info("orderCount="+orderCount) ;
//				Log.info("totalOrderMoney="+totalOrderMoney) ;
//				Log.info("pageSizeStr="+pageSizeStr) ;
//				Log.info("pageTotal="+pageTotal) ;
//				Log.info("currentPage="+currentPage) ;
				
				NodeList ordersList = urlset.getElementsByTagName("OrderInfo") ;
				for(int i = 0 ; i< ordersList.getLength() ; i++)
				{
					Element orderInfo = (Element) ordersList.item(i) ;
					String orderID = DOMHelper.getSubElementVauleByName(orderInfo, "orderID") ;
	//				String consigneeName = DOMHelper.getSubElementVauleByName(orderInfo, "consigneeName") ;
	//				String consigneeTel = DOMHelper.getSubElementVauleByName(orderInfo, "consigneeTel") ;
	//				String consigneeMobileTel = DOMHelper.getSubElementVauleByName(orderInfo, "consigneeMobileTel") ;
	//				String consigneeAddr = DOMHelper.getSubElementVauleByName(orderInfo, "consigneeAddr") ;
	//				String sendGoodsMode = DOMHelper.getSubElementVauleByName(orderInfo, "sendGoodsMode") ;
	//				String orderMoney = DOMHelper.getSubElementVauleByName(orderInfo, "orderMoney") ;
	//				String orderTimeStart = DOMHelper.getSubElementVauleByName(orderInfo, "orderTimeStart") ;
					String lastModifyTime = DOMHelper.getSubElementVauleByName(orderInfo, "lastModifyTime") ;
	//				String remark = DOMHelper.getSubElementVauleByName(orderInfo, "remark") ;
	//				String Label = DOMHelper.getSubElementVauleByName(orderInfo, "Label") ;
					
					Log.info("orderID="+orderID) ;
	//				Log.info("consigneeName="+consigneeName) ;
	//				Log.info("consigneeTel="+consigneeTel) ;
	//				Log.info("consigneeMobileTel="+consigneeMobileTel) ;
	//				Log.info("consigneeAddr="+consigneeAddr) ;
	//				Log.info("sendGoodsMode="+sendGoodsMode) ;
	//				Log.info("orderMoney="+orderMoney) ;
	//				Log.info("orderTimeStart="+orderTimeStart) ;
					Log.info("lastModifyTime="+lastModifyTime) ;
	//				Log.info("remark="+remark) ;
	//				Log.info("Label="+Label) ;
					
					if("".equals(orderID) || orderID == null)
					{
						//orderID为空不处理
					}
					else
					{
						Hashtable<String, String> ht = new Hashtable<String, String>() ;
						ht.put("orderID", orderID) ;
						ht.put("lastModifyTime", lastModifyTime) ;
						orderIDList.add(ht) ;
					}
				}
				//判断是否有下一页
				if("".equals(pageTotal) || pageTotal == null)
					pageTotal="0" ;
				if(pageIndex >= Integer.parseInt(pageTotal))
					hasNextPage = false ;
				else
					pageIndex ++ ;
			}
		} catch (Exception e) {
			Log.error("获取当当订单列表", "获取订单列表失败，错误信息"+e.getMessage()) ;
			e.printStackTrace() ;
			return null ;
		}
		return orderIDList;
	}
	
}
