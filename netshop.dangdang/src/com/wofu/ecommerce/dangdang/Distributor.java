package com.wofu.ecommerce.dangdang;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.ecommerce.dangdang.util.CommHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;

public class Distributor {
	


//	更新当当配送结果 
	public static List updateDangdangDeliveryResult(Connection conn,List orders)
	{
		String sql = "" ;
		ArrayList<Hashtable<String, String>> list = new ArrayList<Hashtable<String,String>>() ;
		try 
		{	//循环所有的订单
			for(int i = 0 ; i <= orders.size() ; i++)
			{
				Hashtable<String, String> hashtable = new Hashtable<String, String>() ;
				Hashtable<String, String> ht = (Hashtable<String, String>) orders.get(i) ;
				String ordercode = ht.get("ordercode").toString() ;
				String status = ht.get("status").toString() ; 
				//nolock是不加锁查询，可以读取被事务锁定的数据，也称为脏读
				sql = "select top 1 proctime from ecs_deliverynote with(nolock) where ordercode='"+ordercode+"' order by serialid desc" ;
				String distriTime = SQLHelper.strSelect(conn, sql) ;
				String orderStatus = "" ;//当当配送结果状态 1:成功 2：失败
				if("0".equals(status))
					orderStatus = "1" ;
				else if("1".equals(status))
					orderStatus = "2" ;
				else
				{
					hashtable.put("ordercode", ordercode) ;
					hashtable.put("resultflag", "0") ;
					hashtable.put("msg", "未知订单状态:"+status) ;
					list.add(hashtable) ;
					Log.error("更新当当订单配送结果", "更新失败，未知订单状态:"+status) ;
					continue ;
				}
				StringBuffer sb = new StringBuffer() ;
				sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>") ;
				sb.append("<request>") ;
				sb.append("<functionID>updateMultiOrdersDistriStatus</functionID>") ;
				sb.append("<time>").append(distriTime).append("</time>") ;
				sb.append("<OrdersList>") ;
				sb.append("<OrderInfo>") ;
				sb.append("<orderID>").append(ordercode).append("</orderID>") ;
				sb.append("<distriStatus>").append(orderStatus).append("</distriStatus>") ;
				sb.append("<distriTime>").append(distriTime).append("</distriTime>") ;
				sb.append("</OrderInfo>") ;
				sb.append("</OrdersList>") ;
				sb.append("</request>") ;
				
			//	String gbkValuesStr = Coded.getEncode(gShopID, encoding).concat(Coded.getEncode(key, encoding)) ;
			//	String validateString = MD5Util.getMD5Code(gbkValuesStr.getBytes()) ;
				
				Hashtable<String, String> params = new Hashtable<String, String>() ;
			//	params.put("gShopID", gShopID) ;
			//	params.put("validateString", validateString) ;
				
				String xml = sb.toString() ;
				//发送查询请求
				String requestUrl = Params.url + "/updateMultiOrdersDistriStatus.php" ;
				String responseText = CommHelper.sendRequest(requestUrl,"POST",params, "updateMultiOrdersDistriStatus="+xml) ;
				
				Document doc = DOMHelper.newDocument(responseText, "GBK") ;
				Element result = doc.getDocumentElement() ;
				
				//判断返回是否正确
				try
				{
					Element error = (Element) result.getElementsByTagName("Error").item(0);
					String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
					String operation = DOMHelper.getSubElementVauleByName(error, "orderOperation") ;
					if(!"".equals(operCode))  //记录错误信息
					{
						hashtable.put("ordercode", ordercode) ;
						hashtable.put("resultflag", "0") ;
						hashtable.put("msg", operCode+":"+operation) ;
						list.add(hashtable) ;
						Log.error("更新当当订单配送状态", "上传配送信息失败，operCode="+operCode+",operation="+operation);
						continue ;
					}
				} catch (Exception e) {
				}
				
				Element resultInfo = (Element) result.getElementsByTagName("Result").item(0) ;
				Element ordersList = (Element) resultInfo.getElementsByTagName("OrdersList").item(0) ;
				NodeList orderInfoList = ordersList.getElementsByTagName("OrderInfo") ;
				
				Element orderInfo = (Element) orderInfoList.item(0) ;
				String orderid = DOMHelper.getSubElementVauleByName(orderInfo, "orderID") ;
				String orderOperCode = DOMHelper.getSubElementVauleByName(orderInfo, "orderOperCode") ;
				String orderOperation = DOMHelper.getSubElementVauleByName(orderInfo, "orderOperation") ;
				if("0".equals(orderOperCode) && ordercode.equals(orderid))
				{
					hashtable.put("ordercode", ordercode) ;
					hashtable.put("resultflag", "1") ;
					hashtable.put("msg", "") ;
					Log.info("更新当当订单配送结果成功,状态:"+status+ ",订单号:"+ordercode) ;
				}
				else
				{
					hashtable.put("ordercode", ordercode) ;
					hashtable.put("resultflag", "0") ;
					hashtable.put("msg", orderOperCode+":"+orderOperation) ;
					Log.error("","更新当当订单配送结果失败,错误信息:"+orderOperCode+","+orderOperation) ;
				}
				list.add(hashtable) ;
			}
		} 
		catch (Exception e) 
		{
			Log.error("更新当当配送结果", "更新当当配送结果失败,错误信息:"+e.getMessage()) ;
		}
		return list ;
	}
	
}
