package com.wofu.ecommerce.oauthpaipai;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.oauthpaipai.api.oauth.PaiPaiOpenApiOauth;


public class StockUtils {
	

	
	public static void updateStock(DataCentre dao,String spid,
			String secretkey,String token,String uid,String encoding,
			ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty) 
	throws Exception
	{
	
		try
		{
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(spid, secretkey, token, Long.valueOf(uid));
			
			sdk.setCharset(encoding);
			
			HashMap<String, Object> params = sdk.getParams("/item/modifyItemStock.xhtml");
			
			params.put("itemCode", stockconfig.getItemid());
			params.put("skuId", stockconfigsku.getSkuid());
			params.put("stockCount", String.valueOf(qty));
	
			
			
			String result = sdk.invoke();;	
			
				
			Document doc = DOMHelper.newDocument(result.toString(),encoding);
			Element urlset = doc.getDocumentElement();
			String errorcode = DOMHelper.getSubElementVauleByName(urlset, "errorCode");
			String errormessage = DOMHelper.getSubElementVauleByName(urlset, "errorMessage").replaceAll("\"","'");
								
			if (errorcode.equals("0"))
			{
	
				Log.info("更新拍拍库存成功,SKU【"+stockconfigsku.getSku()+"】,原库存:"+stockconfigsku.getStockcount()+" 新库存:"+qty);
				stockconfig.setStockcount(stockconfig.getStockcount()-stockconfigsku.getStockcount()+qty);
				stockconfig.setErrflag(0);
				stockconfig.setErrmsg("");
				dao.updateByKeys(stockconfig,"orgid,itemid");
				
				stockconfigsku.setStockcount(qty);
				stockconfigsku.setErrflag(0);
				stockconfigsku.setErrmsg("");
				dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
			}
			else
			{
	
				Log.info("更新拍拍库存失败,SKU【"+stockconfigsku.getSku()+"】,错误信息:"+errormessage);
				stockconfigsku.setErrflag(1);
				stockconfigsku.setErrmsg(errormessage);
				dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
				
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(errormessage);
				dao.updateByKeys(stockconfig,"orgid,itemid");
			}
		} catch (Exception e) {
			
			Log.info("更新拍拍库存失败,SKU【"+stockconfigsku.getSku()+"】,错误信息:"+e.getMessage());
			
			stockconfigsku.setErrflag(1);
			stockconfigsku.setErrmsg(e.getMessage());
			dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");	
			
			stockconfig.setErrflag(1);
			stockconfig.setErrmsg(e.getMessage());
			dao.updateByKeys(stockconfig,"orgid,itemid");
			
		}
	
	}
	
	public static Hashtable<String,String> getSkuInfo(Connection conn,String wsurl,
			String spid,String secretkey,String token,String uid,String encoding,String sku)
		throws Exception
	{
		Hashtable<String,String> htskuinfo=new Hashtable<String,String>();
					
		
		String customno="";
		String sql="select count(*) from barcode where custombc='"+sku+"'";
		if (SQLHelper.intSelect(conn, sql)>0)
		{
			sql="select customno from goods a,barcode b where a.goodsid=b.goodsid and b.custombc='"+sku+"'";
			customno=SQLHelper.strSelect(conn, sql);
		}
		else
			customno=sku;

		PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(spid, secretkey, token, Long.valueOf(uid));
		
		sdk.setCharset(encoding);
		
		HashMap<String, Object> params = sdk.getParams("/item/getItem.xhtml");

		params.put("itemLocalCode", customno);

		String result = sdk.invoke();;	
	
		Document doc = DOMHelper.newDocument(result, encoding);
		Element urlset = doc.getDocumentElement();		
		String errorcode = DOMHelper.getSubElementVauleByName(urlset, "errorCode");
		String errormessage=DOMHelper.getSubElementVauleByName(urlset, "errorMessage");
		
		if(!errorcode.equals("0"))
		{
			Log.info("找不到拍拍资料,SKU【"+sku+"】");
			return htskuinfo;
		}
		
		String itemcode=DOMHelper.getSubElementVauleByName(urlset, "itemCode");
		String itemstate=DOMHelper.getSubElementVauleByName(urlset, "itemState");
		htskuinfo.put("itemcode", itemcode);
		htskuinfo.put("itemstate", itemstate);
		
		
		Element stocklist=(Element) urlset.getElementsByTagName("stockList").item(0);
		NodeList stocknodes = stocklist.getElementsByTagName("stock");
		boolean isexists=false;
		for (int j = 0; j < stocknodes.getLength(); j++) {
			Element stockelement = (Element) stocknodes.item(j);
			String stockLocalCode=DOMHelper.getSubElementVauleByName(stockelement, "stockLocalCode");
			if (stockLocalCode.equalsIgnoreCase(sku))
			{
				String stockid=DOMHelper.getSubElementVauleByName(stockelement, "stockId");
				String stockcount=DOMHelper.getSubElementVauleByName(stockelement, "stockCount");
				htskuinfo.put("stockid", stockid);
				htskuinfo.put("quantity", stockcount);
				isexists=true;
			}					
		}
		if(!isexists)
			throw new JException("找不到拍拍资料,SKU【"+sku+"】");
			
			
	
		return htskuinfo;
	}

}
