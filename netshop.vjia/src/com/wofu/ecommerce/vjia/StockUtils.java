package com.wofu.ecommerce.vjia;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.tools.conv.DesUtil;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;

public class StockUtils 
{

	/**
	 * 根据条码更新vjia库存
	 * @param sku		vjia条形码
	 * @param qty		新库存
	 * @param stocks	原库存
	 */
	public static void updateStock(DataCentre dao,String URI,String wsurl,String supplierid,
			String passWord,String swsSupplierID,String strkey,String striv,
			ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int newQty) throws Exception
	{
		try 
		{
			String barCodeAndQuantity = DesUtil.DesEncode((stockconfigsku.getSku()+","+String.valueOf(newQty)), strkey, striv) ;
		   SoapHeader soapheader=new SoapHeader();
		   soapheader.setPassword(passWord);
		   soapheader.setUname(supplierid);
		   soapheader.setUri(URI);
		   
		   SoapBody soapbody=new SoapBody();
		   soapbody.setRequestname("StorageSync");
		   soapbody.setUri(URI);
		   
		   Hashtable<String,String> bodyparams=new Hashtable<String,String>();
		   bodyparams.put("supplierId",swsSupplierID);
		   bodyparams.put("barCodeAndQuantity", barCodeAndQuantity) ;
		   soapbody.setBodyParams(bodyparams);

		   SoapServiceClient client=new SoapServiceClient();
		   client.setUrl(wsurl+"/StorageSyncService.asmx?wsdl");
		   client.setSoapbody(soapbody);
		   client.setSoapheader(soapheader);
		   
		   String result=client.request();
		   Document resultdoc=DOMHelper.newDocument(result);
		   Element resultelement=resultdoc.getDocumentElement();
		   Element resultdetail=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
		   String resultcode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
			   
		   if("0".equals(resultcode))
		   {
			   Log.info("更新vjia库存成功，SKU【"+ stockconfigsku.getSku() +"】,原库存:"+stockconfigsku.getStockcount()+",新库存:"+ newQty);
				stockconfig.setStockcount(stockconfig.getStockcount()-stockconfigsku.getStockcount()+newQty);
				stockconfig.setErrflag(0);
				stockconfig.setErrmsg("");
				dao.updateByKeys(stockconfig,"orgid,itemid");
				
			   stockconfigsku.setStockcount(newQty);
			   stockconfigsku.setErrflag(0);
			   stockconfigsku.setErrmsg("");
			   dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
		   }
		   else
		   {
			   Log.info("更新vjia库存失败，SKU【"+ stockconfigsku.getSku() +"】,错误信息："+ DOMHelper.getSubElementVauleByName(resultdetail, "remark"));
			   
			   stockconfigsku.setErrflag(1);
			   stockconfigsku.setErrmsg(DOMHelper.getSubElementVauleByName(resultdetail, "remark"));
			   dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
			   
			   stockconfig.setErrflag(1);
			   stockconfig.setErrmsg(DOMHelper.getSubElementVauleByName(resultdetail, "remark"));
			   dao.updateByKeys(stockconfig,"orgid,itemid");
			   
			   
		   }
		} 
		catch (Exception e) 
		{
			Log.info("更新vjia库存失败。错误信息："+e.getMessage()) ;
			stockconfigsku.setErrflag(1);
			stockconfigsku.setErrmsg(e.getMessage());
			dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
			
		   stockconfig.setErrflag(1);
		   stockconfig.setErrmsg(e.getMessage());
		   dao.updateByKeys(stockconfig,"orgid,itemid");
		}
	}
	
	//根据sku查找商品信息
	public static ProductInfo getProductInfoByBarcode(String jobname,String barcode,Hashtable<String, String> params)
	{
		ProductInfo p = new ProductInfo();
		String result = "" ;
		try 
		{
			String userName = params.get("userName") ;
			String passWord = params.get("passWord") ;
			String URI = params.get("URI") ;
			String swsSupplierID = params.get("swsSupplierID") ;
			String strkey = params.get("strkey") ;
			String striv = params.get("striv") ;
			String wsurl = params.get("wsurl") ;
		
			
			Hashtable<String, String> bodyParams = new Hashtable<String, String>() ;
			SoapHeader soapHeader = new SoapHeader() ;
			
			soapHeader.setUname(userName) ;
			soapHeader.setPassword(passWord) ;
			soapHeader.setUri(URI) ;
			
			bodyParams.put("swsSupplierID", swsSupplierID) ;
			bodyParams.put("page", "1");
			bodyParams.put("pageSize", "10");	
			bodyParams.put("barCode", DesUtil.DesEncode(barcode, strkey, striv)) ;
			
			SoapBody soapBody = new SoapBody() ;
			soapBody.setRequestname("GetProductInfoByBarcode") ;
			soapBody.setUri(URI) ;
			
			soapBody.setBodyParams(bodyParams) ;
			SoapServiceClient client = new SoapServiceClient() ;
			client.setUrl(wsurl + "/GetProductInfoService.asmx") ;
			
			client.setSoapheader(soapHeader) ;
			client.setSoapbody(soapBody) ;
			
		   result = client.request() ;
		   

		   Document resultdoc=DOMHelper.newDocument(result);
		   Element resultelement=resultdoc.getDocumentElement();
		   String resultcode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
		   
		   if("5".equals(resultcode) || "7".equals(resultcode))
			   return p;
		   if("-1234699".indexOf(resultcode) >= 0)
		   {
			   Log.error("获取vjia商品信息", "获取vjia商品出错，错误信息："+DOMHelper.getSubElementVauleByName(resultelement, "resultmessage"));
			   return p;
		   }
		   
		   Element productList=(Element) resultelement.getElementsByTagName("productlist").item(0);
		   Element product = (Element) productList.getElementsByTagName("product").item(0);
		    
		   String barcode0 = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "barcode"), strkey, striv) ;//商家sku
		   String sku = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "sku"), strkey, striv) ;//vjia商品编码
		   String fororder = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "fororder"), strkey, striv) ;//商城库存
		   String onsale = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "onsale"), strkey, striv) ;//在架状态：true上架，false下架
		   
		   if("".equals(fororder) || fororder == null)
			   fororder = "0" ;
		   
		   p.setBarcode(barcode0) ;
		   p.setSku(sku) ;
		   p.setFororder(Integer.parseInt(fororder)) ;
		   p.setOnsale(onsale) ;
		    
		} catch (Exception e) {
			// TODO: handle exception
			Log.error(jobname, "获取vjia商品出错，错误信息："+e.getMessage()+",result="+result) ;
		}
		return p;
	}
	  

	
	//判断是否全是数字
	public static boolean isNumeric(String str){ 
		Pattern pattern = Pattern.compile("[0-9]*"); 
		return pattern.matcher(str).matches(); 
		} 
	//快递转件
	public static boolean modifyExpressInfo(String jobname,String orderid,String expressCompanyName,String dispatchNo,Hashtable<String, String> htinfo)
	{
		boolean flag = false ;
		try 
		{
			String userName = htinfo.get("userName") ;
			String passWord = htinfo.get("passWord") ;
			String wsurl = htinfo.get("wsurl") ;
			String strkey = htinfo.get("strkey") ;
			String striv = htinfo.get("striv") ;
			String URI = htinfo.get("URI") ;
			String swsSupplierID = htinfo.get("swsSupplierID") ;
			
			SoapHeader soapheader=new SoapHeader();		   
			soapheader.setUname(userName);
			soapheader.setPassword(passWord);
			soapheader.setUri(URI);
			
			SoapBody soapbody=new SoapBody();
			soapbody.setRequestname("ModifyExpressInfo");
			soapbody.setUri(URI);
			
			Hashtable<String,String> bodyparams=new Hashtable<String,String>();
			bodyparams.put("SWSsupplierId", swsSupplierID);
			bodyparams.put("DECformCode", DesUtil.DesEncode(orderid, strkey, striv));
			bodyparams.put("ExpressCompanyName", expressCompanyName);
			bodyparams.put("DispatchNo", dispatchNo);
			soapbody.setBodyParams(bodyparams);
			
			SoapServiceClient client=new SoapServiceClient();
			client.setUrl(wsurl + "/StockoutAndDistribution.asmx");
			client.setSoapbody(soapbody);
			client.setSoapheader(soapheader);
			
			String result=client.request();
			
			Document resultdoc=DOMHelper.newDocument(result);
			Element resultelement=resultdoc.getDocumentElement();
			String errorCode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
			if("0".equals(errorCode))
			{
				flag = true ;
				Log.info("处理订单【" + orderid + "】转件成功,快递公司【"+ expressCompanyName + "】,快递单号【" + dispatchNo + "】");
			}
			else 
			{
				//与原来相同快递信息
				if("3".equals(errorCode))
					flag = true ;
				Element error=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
		    	Element msgElement = (Element) error.getElementsByTagName("order").item(0);
		    	String mark = DOMHelper.getSubElementVauleByName(msgElement, "remark");
				Log.info("处理订单【" + orderid + "】转件失败,快递公司【"+ expressCompanyName + "】,快递单号【" + dispatchNo + "】。错误信息:"+ errorCode +","+mark) ;
			}
		}
		catch (Exception e) 
		{
			flag = false ;
			Log.error(jobname, "修改转件快递信息失败,订单号:"+ orderid +"快递公司:"+ expressCompanyName +"快递单号:"+ dispatchNo +",错误信息:"+e.getMessage()) ;
		}
		return flag ;
	}

	//发货
	public static boolean delivery(String jobname,String orderid,String expressCompanyName,String dispatchNo,Hashtable<String, String> htinfo)
	{
		boolean flag = false ;
		try 
		{
			String userName = htinfo.get("userName") ;
			String passWord = htinfo.get("passWord") ;
			String wsurl = htinfo.get("wsurl") ;
			String strkey = htinfo.get("strkey") ;
			String striv = htinfo.get("striv") ;
			String URI = htinfo.get("URI") ;
			String swsSupplierID = htinfo.get("swsSupplierID") ;
			
			String result = "" ;
			Hashtable<String, String> bodyParams = new Hashtable<String, String>() ;
			SoapHeader soapHeader = new SoapHeader() ;
			bodyParams.put("SWSsupplierId", swsSupplierID) ;
			bodyParams.put("DECformCode", DesUtil.DesEncode(orderid, strkey, striv)) ;
			bodyParams.put("ExpressCompanyName", expressCompanyName) ;
			bodyParams.put("DispatchNo", dispatchNo) ;
			
			soapHeader.setUname(userName) ;
			soapHeader.setPassword(passWord) ;
			soapHeader.setUri(URI) ;
			
			SoapBody soapBody = new SoapBody() ;
			soapBody.setRequestname("SendGoodsConfirm") ;
			soapBody.setUri(URI) ;
			soapBody.setBodyParams(bodyParams) ;
			
			SoapServiceClient client = new SoapServiceClient() ;
			client.setUrl(wsurl + "/SupplierSendGoodsConfirm.asmx") ;
			client.setSoapheader(soapHeader) ;
			client.setSoapbody(soapBody) ;
			
			result = client.request() ;
			
			//System.out.println(result);
			
			Document resultdoc=DOMHelper.newDocument(result);
		    Element resultelement=resultdoc.getDocumentElement();
		    String resultcode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
		    //当前单号物流信息更新失败，则更新下一单
		    if ("0".equals(resultcode)) 
		    {
		    	flag = true ;
		    	Log.info("处理订单【" + orderid + "】发货成功,快递公司【"+ expressCompanyName + "】,快递单号【" + dispatchNo + "】");
		    }
		    //99补发货,3凡客订单(查无此订单)
		    else
		    {
		    	if("99".equals(resultcode) || "3".equals(resultcode))
		    		flag = true ;
		    	//Element error=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
		    	//Element msgElement = (Element) error.getElementsByTagName("order").item(0);
		    //	String mark = DOMHelper.getSubElementVauleByName(msgElement, "remark");
		    	Log.error(jobname,"处理订单【" + orderid + "】发货失败,快递公司【"+ expressCompanyName + "】,快递单号【" + dispatchNo+ "】,错误信息:" + result );
		    }
		}
		catch (Exception e) 
		{
			flag = false ;
			Log.error(jobname, "处理订单【" + orderid + "】发货失败,快递公司【"+ expressCompanyName + "】,快递单号【" + dispatchNo+ "】,错误信息:" +e.getMessage()) ;
		}
		return flag ;
	}

}
