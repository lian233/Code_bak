package com.wofu.ecommerce.qqbuy.test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.common.tools.util.DOMHelper;
import com.wofu.ecommerce.qqbuy.Goods;
import com.wofu.ecommerce.qqbuy.SkuInfo;
import com.wofu.ecommerce.qqbuy.StockUtils;
import com.wofu.ecommerce.qqbuy.oauth.OpenApiException;
import com.wofu.ecommerce.qqbuy.oauth.PaiPaiOpenApiOauth;


public class Test5 {

	//正式
	public static String accessToken = "eba8f41a64718ac25c75926d32cb2102" ;
	public static String appOAuthID = "700044939" ;
	public static String cooperatorId = "855005035" ;
	public static String secretOAuthKey = "s1TGmTwb43gftUYX" ;
	public static long uin = 855005035L ;

	public static String encoding = "gbk" ;
	
	public static void main(String[] args){
		String startTime = "2012-08-01 00:00:00" ;
		String endTime = "2012-08-17 00:00:00" ;
		String pageSize = "50" ;

		Hashtable<String, String> params = new Hashtable<String, String>() ;
		params.put("accessToken", accessToken) ;
		params.put("appOAuthID", appOAuthID) ;
		params.put("cooperatorId", cooperatorId) ;
		params.put("secretOAuthKey", secretOAuthKey) ;
		params.put("uin", String.valueOf(uin)) ;
		params.put("encoding", encoding) ;
		params.put("startTime", startTime) ;
		params.put("endTime", endTime) ;
		params.put("pageSize", pageSize) ;
		String skuid = "178208799261" ;

		System.out.println((Float.parseFloat("102")/100)) ;
		//updateStock() ;
		
		String tempStr = "圆通" ;
		try {
			String tempStr2 = new String(tempStr.getBytes("gb2312"),"utf-8") ;
			System.out.println(tempStr2) ;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//更新库存
	public static void updateStock()
	{
		try {
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, uin);
			sdk.setCharset(encoding) ;
			HashMap<String, Object> params = sdk.getParams("/item/modifySKUStock.xhtml");
			params.put("charset", encoding) ;
			params.put("format", "xml") ;
			params.put("cooperatorId", "855005035") ;
			params.put("skuId", "180249234478") ;
			params.put("stockhouseId", "112") ;
			params.put("stockCount", "644") ;
			
			String responseText = sdk.invoke() ;
			
			System.out.println(responseText) ;
		} catch (Exception e) {
			e.printStackTrace() ;
		}
	}
	public static void test2()
	{
		String startTime = "2012-08-01 00:00:00" ;
		String endTime = "2012-08-17 00:00:00" ;
		String pageSize = "50" ;
		//KP070801AN,KP070801AN,KP070801AN,KP070801AN,KP070801AN
		Hashtable<String, String> params = new Hashtable<String, String>() ;
		params.put("accessToken", accessToken) ;
		params.put("appOAuthID", appOAuthID) ;
		params.put("cooperatorId", cooperatorId) ;
		params.put("secretOAuthKey", secretOAuthKey) ;
		params.put("uin", String.valueOf(uin)) ;
		params.put("encoding", encoding) ;
		params.put("startTime", startTime) ;
		params.put("endTime", endTime) ;
		params.put("pageSize", pageSize) ;
		try 
		{
			StockUtils.setAllSkuInfo("", params) ;
			ArrayList<String> list = new ArrayList<String>() ;
			list.add("KP070801AN") ;
			list.add("KP070801AN") ;
			list.add("KP070801AN") ;
			list.add("KP070801AN") ;
			list.add("KP070801AN") ;
			list.add("KP070801ANKP070801AN") ;
			for(int j = 0 ; j < list.size() ; j ++)
			{
				String sku = list.get(j) ;
				String skuid = StockUtils.getSkuID(sku) ;
				if(skuid == null)
				{
					System.out.println("找不到商品资料,sku【"+sku+"】") ;
					continue ;
				}
				Goods goods = StockUtils.getSKUInfo("", skuid, params) ;
				SkuInfo skuInfo = goods.getStockList().get(0) ;
				String skuLocalCode = skuInfo.getStockLocalcode() ;//商家sku
				String stockhouseId = skuInfo.getStockhouseId() ;
				int stockCount = skuInfo.getStockCount() ;
				String stockSaleState = skuInfo.getStockSaleState() ;
				int stockPayedNum = skuInfo.getStockPayedNum() ;
				
				System.out.println("----------------"+ (j+1) +"---------------------") ;
				System.out.println("skuLocalCode="+skuLocalCode) ;
				System.out.println("stockhouseId="+stockhouseId) ;
				System.out.println("stockCount="+stockCount) ;
				System.out.println("stockSaleState="+stockSaleState) ;
				System.out.println("stockPayedNum="+stockPayedNum) ;
			}
		} catch (Exception e) {
			e.printStackTrace() ;
		}
	}
	public static void test()
	{
		String startTime = "2012-08-01 00:00:00" ;
		String endTime = "2012-08-17 00:00:00" ;
		String pageSize = "50" ;

		Hashtable<String, String> params = new Hashtable<String, String>() ;
		params.put("accessToken", accessToken) ;
		params.put("appOAuthID", appOAuthID) ;
		params.put("cooperatorId", cooperatorId) ;
		params.put("secretOAuthKey", secretOAuthKey) ;
		params.put("uin", String.valueOf(uin)) ;
		params.put("encoding", encoding) ;
		params.put("startTime", startTime) ;
		params.put("endTime", endTime) ;
		params.put("pageSize", pageSize) ;
		
		try 
		{
			List<Goods> goodsList = StockUtils.getSkuList("获取QQ网购商品资料", params) ;
			for(int i = 0 ; i< goodsList.size() ; i++)
			{
				Goods g = goodsList.get(i) ;
				ArrayList<SkuInfo> stockList = g.getStockList() ;
				for(int j = 0 ; j < stockList.size() ; j ++)
				{
					SkuInfo skuInfo = stockList.get(j) ;
					String title = g.getSkuTitle() ;
					String skuLocalCode = skuInfo.getStockLocalcode() ;//商家sku
					String stockhouseId = skuInfo.getStockhouseId() ;
					int stockCount = skuInfo.getStockCount() ;
					String stockSaleState = skuInfo.getStockSaleState() ;
					int stockPayedNum = skuInfo.getStockPayedNum() ;
					System.out.println("----------------"+ (i+1) +"---------------------") ;
					System.out.println("title="+title) ;
					System.out.println("skuLocalCode="+skuLocalCode) ;
					System.out.println("stockhouseId="+stockhouseId) ;
					System.out.println("stockCount="+stockCount) ;
					System.out.println("stockSaleState="+stockSaleState) ;
					System.out.println("stockPayedNum="+stockPayedNum) ;
				}
			}
		} catch (Exception e) {
			e.printStackTrace() ;
		}
	}
}
