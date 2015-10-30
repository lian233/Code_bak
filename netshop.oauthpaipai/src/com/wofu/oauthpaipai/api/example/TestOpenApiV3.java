package com.wofu.oauthpaipai.api.example;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.util.DOMHelper;
import com.wofu.oauthpaipai.api.oauth.OpenApiException;
import com.wofu.oauthpaipai.api.oauth.PaiPaiOpenApiOauth;


public class TestOpenApiV3 {

	//private static String appOAuthID = "700142691";
	//private static String appOAuthkey = "Wlf3Gs7dkZrDlHGV";
	//private static String accessToken = "2d26edef38a103b0f8f556d91356711c";
	//private static long uin = 855003832;
	
	private static String appOAuthID = "700176754";
	private static String appOAuthkey = "9NIbDJr2M9C1oxf6";
	private static String accessToken = "62717852878355ef01657dd97d967119";
	private static long uin = 1565380247;
	
	public static void main(String args[]) throws Exception{

	

		//getOrderList();
		
		//getDealDetail();
		getSingleOrder("1565380247-20140308-1240499546");
		
	}
	
	private static void getDealDetail()
	{
		PaiPaiOpenApiOauth sdk = null;
		sdk = new PaiPaiOpenApiOauth(appOAuthID, appOAuthkey, accessToken, uin);
	
		sdk.setCharset("gbk");
		
		HashMap<String, Object> params = sdk.getParams("/deal/getDealDetail.xhtml");
		// 填充URL请求参数
	
		params.put("sellerUin", ""+uin);
		params.put("dealCode", "855002187-20130911-1145708469");
		params.put("listItem", "1");
		
		try {
			String resp = sdk.invoke();
			System.out.println("result:"+resp);
			
		} catch (OpenApiException e) {
			System.out.printf("Request Failed. code:%d, msg:%s\n", e.getErrorCode(), e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	private static void getSingleOrder(String orderCode) throws Exception
	{
		PaiPaiOpenApiOauth sdk = null;
		sdk = new PaiPaiOpenApiOauth(appOAuthID, appOAuthkey, accessToken, uin);
	
		sdk.setCharset("GBK");
		
		HashMap<String, Object> params = sdk.getParams("/deal/getDealDetail.xhtml");
		// 填充URL请求参数
	
		params.put("sellerUin", uin);
		params.put("dealCode", orderCode);
		params.put("listItem", "1");
		
		try {
			String resp = sdk.invoke();
			System.out.println("result:"+resp);
			
		} catch (OpenApiException e) {
			System.out.printf("Request Failed. code:%d, msg:%s\n", e.getErrorCode(), e.getMessage());
			e.printStackTrace();
		}

	}
	


}
