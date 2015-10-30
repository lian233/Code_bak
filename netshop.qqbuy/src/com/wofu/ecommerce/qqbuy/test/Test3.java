package com.wofu.ecommerce.qqbuy.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.qqbuy.Goods;
import com.wofu.ecommerce.qqbuy.OrderUtils;
import com.wofu.ecommerce.qqbuy.SkuInfo;
import com.wofu.ecommerce.qqbuy.StockUtils;
import com.wofu.ecommerce.qqbuy.oauth.PaiPaiOpenApiOauth;

public class Test3 {

	public final static String encoding = "gbk" ; 
	public static String charset = "utf-8" ;
	public static String format = "xml" ;
	public static String host = "http://api.buy.qq.com" ;
	private static long monthMillis = 30 * 24 * 60 * 60 * 1000L ; 
	private static HashMap<String, Object> params;
	//测试
//	public static String cooperatorId = "855010773" ;
//	public static String appOAuthID = "700043070" ;
//	public static String secretOAuthKey = "pEOO6eUeNeU926qK" ;
//	public static String accessToken = "7faff45d7bd43cae61c72f3101c0572b" ;
//	public static String uin = "855010773" ;
	//正式
	public static String cooperatorId = "855005035" ;
	public static String appOAuthID = "700044939" ;
	public static String secretOAuthKey = "s1TGmTwb43gftUYX" ;
	public static String accessToken = "eba8f41a64718ac25c75926d32cb2102" ;
	public static String uin = "855005035" ;
	
	public static void main(String[] args) throws ClientProtocolException, IOException {

		String startTime=Formatter.format(new Date(new Date().getTime()-10*monthMillis), Formatter.DATE_TIME_FORMAT);
		String endTime=Formatter.format((new Date()), Formatter.DATE_TIME_FORMAT);
		Hashtable<String, String> inputParams = new Hashtable<String, String>() ;
		inputParams.put("jobname", "") ;
		inputParams.put("accessToken", accessToken) ;
		inputParams.put("appOAuthID", appOAuthID) ;
		inputParams.put("secretOAuthKey", secretOAuthKey) ;
		inputParams.put("cooperatorId", cooperatorId) ;
		inputParams.put("uin", uin) ;
		inputParams.put("encoding", encoding) ;
		inputParams.put("startTime", startTime) ;
		inputParams.put("endTime", endTime) ;
		inputParams.put("pageSize", "20") ;

		//modifySku("189867845072", "KP070801AN") ;
		/*
		List<Goods> goodsList = StockUtils.getSkuList("", inputParams) ;
		for(int i=0 ; i < goodsList.size() ; i++)
		{
			Goods goods = goodsList.get(i) ;
			SkuInfo skuInfo = goods.getStockList().get(0) ;
			String skuId = goods.getSkuId() ;
			String sku = skuInfo.getStockLocalcode() ;
			String stockState = skuInfo.getStockSaleState() ;
			if(skuId.equals("189867845072"))
				System.out.println("found:") ;
			System.out.println("第"+i+"个:skuId="+skuId+",sku="+sku+",stockState="+stockState) ;
			modifySku(skuId, sku) ;
			
		}
		*/

		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("accessToken", accessToken);
		params.put("appOAuthID", appOAuthID);
		params.put("cooperatorId", cooperatorId);
		params.put("secretOAuthKey", secretOAuthKey);
		params.put("uin", String.valueOf(uin));
		params.put("encoding", encoding);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("pageSize", "30");

		Log.info(params.toString()) ;
		//获取此段时间内修改过的商品
		List<Goods> goodsList = StockUtils.getSkuList("", params) ;

	}
	public static void modifySku(String skuId,String skuLocalCode)
	{
		String encoding = "gbk" ; 
		String format = "xml" ;
		String cooperatorId = "855005035" ;
		String appOAuthID = "700044939" ;
		String secretOAuthKey = "s1TGmTwb43gftUYX" ;
		String accessToken = "eba8f41a64718ac25c75926d32cb2102" ;
		long uin = 855005035L ;
		String uri = "/item/modifySKU.xhtml" ;
		try 
		{
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(appOAuthID, secretOAuthKey, accessToken, uin);
			sdk.setCharset(encoding) ;
			HashMap<String, Object> params = sdk.getParams(uri);
			params.put("charset", encoding) ;
			params.put("format", format) ;
			params.put("cooperatorId", cooperatorId) ;
			params.put("skuId", skuId) ;
			params.put("skuLocalCode", skuLocalCode) ;
			
			String responseText = sdk.invoke() ;
			//System.out.println("responseText="+responseText) ;
			
			Document doc = DOMHelper.newDocument(responseText, encoding);
			Element resultElement = doc.getDocumentElement();
			String errorCode = DOMHelper.getSubElementVauleByName(resultElement, "errorCode").trim() ;
			if("0".equals(errorCode))
			{
				System.out.print("修改商品编码成功,skuid【"+skuId+"】,skuLocalCode【"+skuLocalCode+"】") ;
			}
			else 
			{
				String errorMessage = DOMHelper.getSubElementVauleByName(resultElement, "errorMessage") ;
				System.out.print("修改商品编码失败,skuid【"+skuId+"】,skuLocalCode【"+skuLocalCode+"】") ;
				System.out.println(errorCode+errorMessage) ;
				return ;
			}
		} 
		catch (Exception e) 
		{
			System.out.print("修改商品编码失败,skuid【"+skuId+"】,skuLocalCode【"+skuLocalCode+"】") ;
			e.printStackTrace() ;
		}
	}
}
