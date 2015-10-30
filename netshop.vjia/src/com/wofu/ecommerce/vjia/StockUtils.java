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
	 * �����������vjia���
	 * @param sku		vjia������
	 * @param qty		�¿��
	 * @param stocks	ԭ���
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
			   Log.info("����vjia���ɹ���SKU��"+ stockconfigsku.getSku() +"��,ԭ���:"+stockconfigsku.getStockcount()+",�¿��:"+ newQty);
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
			   Log.info("����vjia���ʧ�ܣ�SKU��"+ stockconfigsku.getSku() +"��,������Ϣ��"+ DOMHelper.getSubElementVauleByName(resultdetail, "remark"));
			   
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
			Log.info("����vjia���ʧ�ܡ�������Ϣ��"+e.getMessage()) ;
			stockconfigsku.setErrflag(1);
			stockconfigsku.setErrmsg(e.getMessage());
			dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
			
		   stockconfig.setErrflag(1);
		   stockconfig.setErrmsg(e.getMessage());
		   dao.updateByKeys(stockconfig,"orgid,itemid");
		}
	}
	
	//����sku������Ʒ��Ϣ
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
			   Log.error("��ȡvjia��Ʒ��Ϣ", "��ȡvjia��Ʒ����������Ϣ��"+DOMHelper.getSubElementVauleByName(resultelement, "resultmessage"));
			   return p;
		   }
		   
		   Element productList=(Element) resultelement.getElementsByTagName("productlist").item(0);
		   Element product = (Element) productList.getElementsByTagName("product").item(0);
		    
		   String barcode0 = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "barcode"), strkey, striv) ;//�̼�sku
		   String sku = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "sku"), strkey, striv) ;//vjia��Ʒ����
		   String fororder = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "fororder"), strkey, striv) ;//�̳ǿ��
		   String onsale = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(product, "onsale"), strkey, striv) ;//�ڼ�״̬��true�ϼܣ�false�¼�
		   
		   if("".equals(fororder) || fororder == null)
			   fororder = "0" ;
		   
		   p.setBarcode(barcode0) ;
		   p.setSku(sku) ;
		   p.setFororder(Integer.parseInt(fororder)) ;
		   p.setOnsale(onsale) ;
		    
		} catch (Exception e) {
			// TODO: handle exception
			Log.error(jobname, "��ȡvjia��Ʒ����������Ϣ��"+e.getMessage()+",result="+result) ;
		}
		return p;
	}
	  

	
	//�ж��Ƿ�ȫ������
	public static boolean isNumeric(String str){ 
		Pattern pattern = Pattern.compile("[0-9]*"); 
		return pattern.matcher(str).matches(); 
		} 
	//���ת��
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
				Log.info("��������" + orderid + "��ת���ɹ�,��ݹ�˾��"+ expressCompanyName + "��,��ݵ��š�" + dispatchNo + "��");
			}
			else 
			{
				//��ԭ����ͬ�����Ϣ
				if("3".equals(errorCode))
					flag = true ;
				Element error=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
		    	Element msgElement = (Element) error.getElementsByTagName("order").item(0);
		    	String mark = DOMHelper.getSubElementVauleByName(msgElement, "remark");
				Log.info("��������" + orderid + "��ת��ʧ��,��ݹ�˾��"+ expressCompanyName + "��,��ݵ��š�" + dispatchNo + "����������Ϣ:"+ errorCode +","+mark) ;
			}
		}
		catch (Exception e) 
		{
			flag = false ;
			Log.error(jobname, "�޸�ת�������Ϣʧ��,������:"+ orderid +"��ݹ�˾:"+ expressCompanyName +"��ݵ���:"+ dispatchNo +",������Ϣ:"+e.getMessage()) ;
		}
		return flag ;
	}

	//����
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
		    //��ǰ����������Ϣ����ʧ�ܣ��������һ��
		    if ("0".equals(resultcode)) 
		    {
		    	flag = true ;
		    	Log.info("��������" + orderid + "�������ɹ�,��ݹ�˾��"+ expressCompanyName + "��,��ݵ��š�" + dispatchNo + "��");
		    }
		    //99������,3���Ͷ���(���޴˶���)
		    else
		    {
		    	if("99".equals(resultcode) || "3".equals(resultcode))
		    		flag = true ;
		    	//Element error=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
		    	//Element msgElement = (Element) error.getElementsByTagName("order").item(0);
		    //	String mark = DOMHelper.getSubElementVauleByName(msgElement, "remark");
		    	Log.error(jobname,"��������" + orderid + "������ʧ��,��ݹ�˾��"+ expressCompanyName + "��,��ݵ��š�" + dispatchNo+ "��,������Ϣ:" + result );
		    }
		}
		catch (Exception e) 
		{
			flag = false ;
			Log.error(jobname, "��������" + orderid + "������ʧ��,��ݹ�˾��"+ expressCompanyName + "��,��ݵ��š�" + dispatchNo+ "��,������Ϣ:" +e.getMessage()) ;
		}
		return flag ;
	}

}
