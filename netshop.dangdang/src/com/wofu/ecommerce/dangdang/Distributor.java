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
	


//	���µ������ͽ�� 
	public static List updateDangdangDeliveryResult(Connection conn,List orders)
	{
		String sql = "" ;
		ArrayList<Hashtable<String, String>> list = new ArrayList<Hashtable<String,String>>() ;
		try 
		{	//ѭ�����еĶ���
			for(int i = 0 ; i <= orders.size() ; i++)
			{
				Hashtable<String, String> hashtable = new Hashtable<String, String>() ;
				Hashtable<String, String> ht = (Hashtable<String, String>) orders.get(i) ;
				String ordercode = ht.get("ordercode").toString() ;
				String status = ht.get("status").toString() ; 
				//nolock�ǲ�������ѯ�����Զ�ȡ���������������ݣ�Ҳ��Ϊ���
				sql = "select top 1 proctime from ecs_deliverynote with(nolock) where ordercode='"+ordercode+"' order by serialid desc" ;
				String distriTime = SQLHelper.strSelect(conn, sql) ;
				String orderStatus = "" ;//�������ͽ��״̬ 1:�ɹ� 2��ʧ��
				if("0".equals(status))
					orderStatus = "1" ;
				else if("1".equals(status))
					orderStatus = "2" ;
				else
				{
					hashtable.put("ordercode", ordercode) ;
					hashtable.put("resultflag", "0") ;
					hashtable.put("msg", "δ֪����״̬:"+status) ;
					list.add(hashtable) ;
					Log.error("���µ����������ͽ��", "����ʧ�ܣ�δ֪����״̬:"+status) ;
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
				//���Ͳ�ѯ����
				String requestUrl = Params.url + "/updateMultiOrdersDistriStatus.php" ;
				String responseText = CommHelper.sendRequest(requestUrl,"POST",params, "updateMultiOrdersDistriStatus="+xml) ;
				
				Document doc = DOMHelper.newDocument(responseText, "GBK") ;
				Element result = doc.getDocumentElement() ;
				
				//�жϷ����Ƿ���ȷ
				try
				{
					Element error = (Element) result.getElementsByTagName("Error").item(0);
					String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
					String operation = DOMHelper.getSubElementVauleByName(error, "orderOperation") ;
					if(!"".equals(operCode))  //��¼������Ϣ
					{
						hashtable.put("ordercode", ordercode) ;
						hashtable.put("resultflag", "0") ;
						hashtable.put("msg", operCode+":"+operation) ;
						list.add(hashtable) ;
						Log.error("���µ�����������״̬", "�ϴ�������Ϣʧ�ܣ�operCode="+operCode+",operation="+operation);
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
					Log.info("���µ����������ͽ���ɹ�,״̬:"+status+ ",������:"+ordercode) ;
				}
				else
				{
					hashtable.put("ordercode", ordercode) ;
					hashtable.put("resultflag", "0") ;
					hashtable.put("msg", orderOperCode+":"+orderOperation) ;
					Log.error("","���µ����������ͽ��ʧ��,������Ϣ:"+orderOperCode+","+orderOperation) ;
				}
				list.add(hashtable) ;
			}
		} 
		catch (Exception e) 
		{
			Log.error("���µ������ͽ��", "���µ������ͽ��ʧ��,������Ϣ:"+e.getMessage()) ;
		}
		return list ;
	}
	
}
