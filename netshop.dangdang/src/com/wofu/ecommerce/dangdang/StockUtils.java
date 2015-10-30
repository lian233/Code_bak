package com.wofu.ecommerce.dangdang;

import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.ecommerce.dangdang.Params;
import com.wofu.ecommerce.dangdang.util.CommHelper;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;


public class StockUtils {
	/**
	 * ���µ������
	 */
	public static void updateStock(String jobname,Connection conn ,String url,
			String encoding,String tid,String sku,int oldStock,int stocks,String ItemState,String session,String app_key,String app_Secret) throws JException
	{
		try
		{
			Date temp = new Date();
			//������
			String methodName="dangdang.item.stock.update";
			//������֤�� --md5;����
			String sign = CommHelper.getSign(app_Secret, app_key, methodName, session,temp) ;
			
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("sign", sign) ;
			params.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
			params.put("app_key",app_key);
			params.put("method",methodName);
			params.put("format","xml");
			params.put("session",session);
			params.put("sign_method","md5");
			params.put("oit", sku) ;
			params.put("stk", String.valueOf(stocks)) ;
			
			String responseText = CommHelper.sendRequest(url, "GET",params,"") ;

			Document doc = DOMHelper.newDocument(responseText, encoding) ;
			Element result = doc.getDocumentElement() ;
			
			if(DOMHelper.ElementIsExists(result, "Error"))
			{
				Element errorInfo = (Element)result.getElementsByTagName("Error").item(0) ;
				String operCode = DOMHelper.getSubElementVauleByName(errorInfo, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(errorInfo, "operation") ;
				if(!"".equals(operCode))
				{
					Log.error(jobname,"���µ������ʧ�ܣ������š�"+tid+"��,SKU��"+ sku +"��,������Ϣ��"+ operCode +"��" +operation);
					return ;
				}
			}
			
			Element resultInfo = (Element)result.getElementsByTagName("Result").item(0) ;
			String operCode = DOMHelper.getSubElementVauleByName(resultInfo, "operCode") ;
			String operation = DOMHelper.getSubElementVauleByName(resultInfo, "operation") ;
		
			if("0".equals(operCode))
			{
				StockManager.bakSynReduceStore(jobname, conn, Params.tradecontactid, sku, tid) ;
				Log.info(jobname,"���µ������ɹ�,�����š�"+tid+"��,SKU��"+ sku +"��,ԭ���:"+ oldStock+",�¿��:"+ stocks +",״̬:"+ItemState);
			}
			else if("22".endsWith(operCode))
			{			
				StockManager.bakSynReduceStore(jobname, conn, Params.tradecontactid, sku, tid) ;
				Log.error(jobname,"���µ������ʧ�ܣ��Ҳ���������Ʒ���ϡ������š�"+tid+"��,SKU��"+ sku +"��,������Ϣ��"+ operCode +"��" +operation);
			}
			else
				Log.error(jobname,"���µ������ʧ�ܣ������š�"+tid+"��,SKU��"+ sku +"��,������Ϣ��"+ operCode +"��" +operation);
			
			
		} catch (Exception e) 
		{
			throw new JException("���µ�����Ʒ���ʧ�ܣ�sku��"+sku+"����棺"+stocks+"��������Ϣ��"+e.getMessage()) ;
		}
	}
	
	/**
	 * ���µ������
	 */
	public static void batchUpdateStock(Connection conn,int orgid,String url,
			String updateItemsXML,String encoding,String session,String app_key,String app_Secret) throws Exception
	{
		try
		{
			Date temp = new Date();
			//������                                       
			String methodName="dangdang.items.stock.update";
			//������֤�� --md5;����
			String sign = CommHelper.getSign(app_Secret, app_key, methodName, session,temp) ;
			
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("sign", sign) ;
			params.put("timestamp",Formatter.format(temp,Formatter.DATE_TIME_FORMAT));
			params.put("app_key",app_key);
			params.put("method",methodName);
			params.put("format","xml");
			params.put("session",session);
			params.put("sign_method","md5");
			params.put("ver","1.0");
			
			String responseText = CommHelper.sendRequest(url, "POST",params,"multiItemsStock="+updateItemsXML) ;
			Document doc = DOMHelper.newDocument(responseText, "GBK") ;
			Element result = doc.getDocumentElement() ;
			
	
			
			if(DOMHelper.ElementIsExists(result, "Error"))
			{
				Element errorInfo = (Element)result.getElementsByTagName("Error").item(0) ;
				String operCode = DOMHelper.getSubElementVauleByName(errorInfo, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(errorInfo, "operation") ;
				if(!"".equals(operCode))
				{
					Log.warn("�������µ������ʧ��,������Ϣ��"+ operCode +"��" +operation+",�������ݣ�"+updateItemsXML+" �������ݣ�"+responseText);
				}
			}
			
			
			Element itemlist = (Element)result.getElementsByTagName("ItemsIDList").item(0) ;
			
			for (int i=0;i<itemlist.getElementsByTagName("ItemIDInfo").getLength();i++)
			{
				Element iteminfo=(Element) itemlist.getElementsByTagName("ItemIDInfo").item(i);
				
				String operCode = DOMHelper.getSubElementVauleByName(iteminfo, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(iteminfo, "operation") ;
				String outerItemID = DOMHelper.getSubElementVauleByName(iteminfo, "outerItemID") ;
				
				int stockcount=getUpdateStockCount(encoding,updateItemsXML,outerItemID);
			
				if("0".equals(operCode))
				{
					Log.info("���µ������ɹ�,SKU��"+ outerItemID +"��,�����:"+stockcount);
					
					String sql="select stockcount from ecs_stockconfigsku where orgid="+orgid+" and sku='"+outerItemID+"'";
					int origstockcount=SQLHelper.intSelect(conn, sql);
					
					sql="update ecs_stockconfig set errflag=0,errmsg='',stockcount=stockcount-"+origstockcount+"+"+stockcount+" where orgid="+orgid
						+"  and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+outerItemID+"')";
					SQLHelper.executeSQL(conn,sql);
					
					sql="update ecs_stockconfigsku set errflag=0,errmsg='',stockcount="+stockcount+" where orgid="+orgid+" and sku='"+outerItemID+"'";
					SQLHelper.executeSQL(conn,sql);
				}
			//	else if("22".endsWith(operCode))
				else
				{

					Log.info("���µ������ʧ�ܣ�SKU��"+ outerItemID +"��,������Ϣ��"+ operCode +"��" +operation);
					String sql="update ecs_stockconfigsku set errflag=1,errmsg='"+operation+"' where orgid="+orgid+" and sku='"+outerItemID+"'";
					SQLHelper.executeSQL(conn,sql);
					
					sql="update ecs_stockconfig set errflag=1,errmsg='"+operation+"' where orgid="+orgid
						+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+outerItemID+"')";
					SQLHelper.executeSQL(conn,sql);
				}
			
			}
			
			
		} catch (Exception e) 
		{
			Log.error("���µ������","�������µ�����Ʒ���ʧ��,������Ϣ��"+e.getMessage()) ;
			
		}
	}
	
	private static int getUpdateStockCount(String encoding,String updateItemsXML,String sku) throws Exception
	{
		int num=0;
		Document doc = DOMHelper.newDocument(updateItemsXML, encoding) ;
		Element updateitemselement = doc.getDocumentElement() ;
		Element itemlist = (Element) updateitemselement.getElementsByTagName("ItemsList").item(0) ;
		
		for (int i=0;i<itemlist.getElementsByTagName("ItemUpadteInfo").getLength();i++)
		{
			Element iteminfo=(Element) itemlist.getElementsByTagName("ItemUpadteInfo").item(i);
			
			String outerItemID = DOMHelper.getSubElementVauleByName(iteminfo, "outerItemID") ;
			int stockCount = Integer.valueOf(DOMHelper.getSubElementVauleByName(iteminfo, "stockCount")).intValue();
			if (sku.equals(outerItemID))
			{
				num=stockCount;
				break;
			}

		}
		return num;
	}
	
	
	
	/**
	 * ���ݵ�����Ʒ�����ȡ��Ʒ��Ϣ sku,stock
	 * @param jobname
	 * @param gShopID
	 * @param itemID
	 * @return
	 */
	public static int getSkuStockCount(String url,String encoding,String itemid,
			String sku,String session,String app_key,String app_Secret) throws Exception
	{
		int num=0;
		Date temp = new Date();
		//������
		String methodName="dangdang.item.get";
		//������֤�� --md5;����
		String sign = CommHelper.getSign(app_Secret, app_key, methodName, session,temp) ;
		
		Hashtable<String, String> params = new Hashtable<String, String>() ;
		params.put("sign", sign) ;
		params.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
		params.put("app_key",app_key);
		params.put("method",methodName);
		params.put("format","xml");
		params.put("session",session);
		params.put("sign_method","md5");
		params.put("it", itemid) ;
		
		String responseText = CommHelper.sendRequest(url, "GET",params,"") ;
		
		Document doc = DOMHelper.newDocument(responseText, encoding) ;
		Element result = doc.getDocumentElement() ;

		Element error = null ;
		if(DOMHelper.ElementIsExists(result, "Error"))
			error = (Element)result.getElementsByTagName("Error").item(0) ;
		if(DOMHelper.ElementIsExists(result, "Result"))
			error = (Element)result.getElementsByTagName("Result").item(0) ;
		if(error != null && DOMHelper.ElementIsExists(error, "operCode"))
		{
			String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
			String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
			Log.info("��ȡ������Ʒ����ʧ��,SKU:"+ sku +",������Ϣ��"+operCode+":"+operation) ;
			return num;
		}

		Element itemDetail = (Element)result.getElementsByTagName("ItemDetail").item(0)  ;
		if(DOMHelper.ElementIsExists(itemDetail, "SpecilaItemInfo"))
		{
			String itemState = DOMHelper.getSubElementVauleByName(itemDetail, "itemState") ;
			NodeList specilaItemInfo =  result.getElementsByTagName("SpecilaItemInfo") ;
			for(int i = 0 ; i < specilaItemInfo.getLength() ; i++)
			{
				Element itemInfo = (Element)specilaItemInfo.item(i) ;
				String stockCount = DOMHelper.getSubElementVauleByName(itemInfo, "stockCount") ;
				String outerItemID = DOMHelper.getSubElementVauleByName(itemInfo, "outerItemID") ;
				
				if (outerItemID.equals(sku))
				{
					num=Integer.valueOf(stockCount);
					break;
				}
			}
		}

		return num;
	}

	


}
