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
	 * 更新当当库存
	 */
	public static void updateStock(String jobname,Connection conn ,String url,
			String encoding,String tid,String sku,int oldStock,int stocks,String ItemState,String session,String app_key,String app_Secret) throws JException
	{
		try
		{
			Date temp = new Date();
			//方法名
			String methodName="dangdang.item.stock.update";
			//生成验证码 --md5;加密
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
					Log.error(jobname,"更新当当库存失败，订单号【"+tid+"】,SKU【"+ sku +"】,错误信息："+ operCode +"：" +operation);
					return ;
				}
			}
			
			Element resultInfo = (Element)result.getElementsByTagName("Result").item(0) ;
			String operCode = DOMHelper.getSubElementVauleByName(resultInfo, "operCode") ;
			String operation = DOMHelper.getSubElementVauleByName(resultInfo, "operation") ;
		
			if("0".equals(operCode))
			{
				StockManager.bakSynReduceStore(jobname, conn, Params.tradecontactid, sku, tid) ;
				Log.info(jobname,"更新当当库存成功,订单号【"+tid+"】,SKU【"+ sku +"】,原库存:"+ oldStock+",新库存:"+ stocks +",状态:"+ItemState);
			}
			else if("22".endsWith(operCode))
			{			
				StockManager.bakSynReduceStore(jobname, conn, Params.tradecontactid, sku, tid) ;
				Log.error(jobname,"更新当当库存失败，找不到当当商品资料。订单号【"+tid+"】,SKU【"+ sku +"】,错误信息："+ operCode +"：" +operation);
			}
			else
				Log.error(jobname,"更新当当库存失败，订单号【"+tid+"】,SKU【"+ sku +"】,错误信息："+ operCode +"：" +operation);
			
			
		} catch (Exception e) 
		{
			throw new JException("更新当当商品库存失败，sku："+sku+"，库存："+stocks+"。错误信息："+e.getMessage()) ;
		}
	}
	
	/**
	 * 更新当当库存
	 */
	public static void batchUpdateStock(Connection conn,int orgid,String url,
			String updateItemsXML,String encoding,String session,String app_key,String app_Secret) throws Exception
	{
		try
		{
			Date temp = new Date();
			//方法名                                       
			String methodName="dangdang.items.stock.update";
			//生成验证码 --md5;加密
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
					Log.warn("批量更新当当库存失败,错误信息："+ operCode +"：" +operation+",请求数据："+updateItemsXML+" 返回数据："+responseText);
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
					Log.info("更新当当库存成功,SKU【"+ outerItemID +"】,库存数:"+stockcount);
					
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

					Log.info("更新当当库存失败，SKU【"+ outerItemID +"】,错误信息："+ operCode +"：" +operation);
					String sql="update ecs_stockconfigsku set errflag=1,errmsg='"+operation+"' where orgid="+orgid+" and sku='"+outerItemID+"'";
					SQLHelper.executeSQL(conn,sql);
					
					sql="update ecs_stockconfig set errflag=1,errmsg='"+operation+"' where orgid="+orgid
						+" and itemid in(select itemid from ecs_stockconfigsku where orgid="+orgid+" and sku='"+outerItemID+"')";
					SQLHelper.executeSQL(conn,sql);
				}
			
			}
			
			
		} catch (Exception e) 
		{
			Log.error("更新当当库存","批量更新当当商品库存失败,错误信息："+e.getMessage()) ;
			
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
	 * 根据当当商品编码获取商品信息 sku,stock
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
		//方法名
		String methodName="dangdang.item.get";
		//生成验证码 --md5;加密
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
			Log.info("获取当当商品资料失败,SKU:"+ sku +",错误信息："+operCode+":"+operation) ;
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
