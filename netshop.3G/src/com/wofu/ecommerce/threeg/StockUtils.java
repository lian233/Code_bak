package com.wofu.ecommerce.threeg;

import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.ecommerce.threeg.util.CommonHelper;
import com.wofu.ecommerce.threeg.util.Utility;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class StockUtils {
	
	private static String productstockquerymethod="ProductStockQuery.ashx";
	
	private static String updatestockmethod="ProductStock.ashx";
	
	public static int getSkuInfo(Hashtable htwfinfo,String sku) 
		throws Exception
	{
		int stockqty=-1;
		
		String wsurl=htwfinfo.get("wsurl").toString();
		String CustomerPrivateKeyPath=htwfinfo.get("CustomerPrivateKeyPath").toString();
		String GGMallPublicKeyPath=htwfinfo.get("GGMallPublicKeyPath").toString();		
		String encoding=htwfinfo.get("encoding").toString();
		String agentid=htwfinfo.get("agentid").toString();				
		String cmdcode="1004";
		
		String body=getQueryBody("1");
		
		String requestdata=CommonHelper.getXML(CustomerPrivateKeyPath, agentid, cmdcode, body);
					
		String s=CommonHelper.SendRequest(wsurl+productstockquerymethod, requestdata);
		
		String bodystr=s.substring(s.indexOf("<body>"), s.indexOf("</body>")+7);
		
		Document doc = DOMHelper.newDocument(s, encoding);
		Element msgelement = doc.getDocumentElement();
		Element bodyElement=(Element) msgelement.getElementsByTagName("body").item(0);
		Element ctrlElement=(Element) msgelement.getElementsByTagName("ctrl").item(0);		
		String messagedigest=DOMHelper.getSubElementVauleByName(ctrlElement, "md");
	
		if (!Utility.ValifyDigest(bodystr,messagedigest,GGMallPublicKeyPath))
		{
			throw new JException("签名验证失败!");
		}
		else
		{
			NodeList productlists=bodyElement.getElementsByTagName("ProductList");
			if (productlists.getLength()>0)
			{
				for (int i=0;i<productlists.getLength();i++)
				{
					
					Element productlist =(Element) productlists.item(i);
					if (productlist.getChildNodes().getLength()>0)
					{
						NodeList products=productlist.getElementsByTagName("Product");
						for (int k=0;k<products.getLength();k++)
						{
							Element product=(Element) products.item(k);
							Element productmodellist=(Element) product.getElementsByTagName("ProductModelList").item(0);
							NodeList externallist=productmodellist.getElementsByTagName("ProductModel");
							for(int j=0;j<externallist.getLength();j++)
							{
								Element productmodel=(Element) externallist.item(j);
								String externalid=DOMHelper.getSubElementVauleByName(productmodel, "ProductModelExternalId");
								if (externalid.toUpperCase().equals(sku))
								{
									
									stockqty=Integer.valueOf(DOMHelper.getSubElementVauleByName(productmodel, "Stock")).intValue();
								}
								
							}
						}
					}
				}
			}
		}
		return stockqty;
	}
	
	public static void updateStock(String modulename,Hashtable htwfinfo,Hashtable htskuinfo,String tid) 
	throws Exception
	{
	
		String wsurl=htwfinfo.get("wsurl").toString();
		String CustomerPrivateKeyPath=htwfinfo.get("CustomerPrivateKeyPath").toString();
		String GGMallPublicKeyPath=htwfinfo.get("GGMallPublicKeyPath").toString();		
		String encoding=htwfinfo.get("encoding").toString();
		String agentid=htwfinfo.get("agentid").toString();				
		String cmdcode="1003";
		String sku=htskuinfo.get("sku").toString();
		String stockqty=htskuinfo.get("stockqty").toString();
		
		String body=getUpdateBody(sku,stockqty);
		
		String requestdata=CommonHelper.getXML(CustomerPrivateKeyPath, agentid, cmdcode, body);
	
		String s=CommonHelper.SendRequest(wsurl+updatestockmethod, requestdata);
		
		
		String bodystr=s.substring(s.indexOf("<body>"), s.indexOf("</body>")+7);
	
		Document doc = DOMHelper.newDocument(s, encoding);
		Element msgelement = doc.getDocumentElement();
		Element bodyElement=(Element) msgelement.getElementsByTagName("body").item(0);
		Element ctrlElement=(Element) msgelement.getElementsByTagName("ctrl").item(0);		
		String messagedigest=DOMHelper.getSubElementVauleByName(ctrlElement, "md");
	
		if (!Utility.ValifyDigest(bodystr,messagedigest,GGMallPublicKeyPath))
		{
			throw new JException("签名验证失败!");
		}
		else
		{
			String message=DOMHelper.getSubElementVauleByName(bodyElement, "message");
			if (message.toUpperCase().equals("Y"))
			{
				if (htskuinfo.get("type").toString().equals("1"))
					Log.info(modulename,"更新3G库存成功,SKU【"+sku+"】,原库存:"+htskuinfo.get("quantity").toString()+" 新库存:"+stockqty);
				else
					Log.info(modulename,"更新3G库存成功,订单号:"+tid+" SKU【"+sku+"】,原库存:"+htskuinfo.get("quantity").toString()+" 调整库存:"+htskuinfo.get("qty").toString());
				
			}else
			{
				throw new JException("更新3G库存失败,订单号:"+tid+" SKU【"+sku+"】,错误信息:"+message);
			}
		}
		
	}
	
	
	public static String getQueryBody(String status)
	{
		StringBuffer bodybuffer=new StringBuffer();
		bodybuffer.append("<body>");
		bodybuffer.append("<status>").append(status).append("</status>");
		bodybuffer.append("</body>");
		return bodybuffer.toString();
	}
	
	public static String getUpdateBody(String sku,String stockqty)
	{
		StringBuffer bodybuffer=new StringBuffer();
		bodybuffer.append("<body>");
		bodybuffer.append("<productModel>");
		bodybuffer.append("<ExternalId>").append(sku).append("</ExternalId>");
		bodybuffer.append("<stock>").append(stockqty).append("</stock>");
		bodybuffer.append("</productModel>");
		bodybuffer.append("</body>");
		return bodybuffer.toString();
	}

}
