package com.wofu.intf.jw;

import java.util.HashMap;
import java.util.Map;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;

public class Test1 {
	private static String appkey="";
	private static String format="";
	private static String url="";
	
	public static void main(String[] args) throws Exception{
		queryItemStock();
		queryOrderStatus();
	}
	
	//查询商品库存
	public static void queryItemStock() throws Exception{
		String goodsId="";
		String skuId="";
		String outerId="";
		String service="subSyncItemStockInfo";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("goodsId", goodsId);
		map.put("skuId", skuId);
		map.put("outerId", outerId);
		String bizData = new JSONObject(map).toString();
		String sign=JwUtil.makeSign(bizData);
		Map requestParams=JwUtil.makeRequestParams(bizData, service, 
				appkey,format, sign);
		String result=CommHelper.sendRequest(url, requestParams, "");
		Log.info("result: "+result);
	}
	
	//查询订单状态
	public static void queryOrderStatus() throws Exception{
		String orderCode="";
		String platFormName="";
		String shopName="";
		String service="subQueryOrderState";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("orderCode", orderCode);
		map.put("platFormName", platFormName);
		map.put("shopName", shopName);
		String bizData = new JSONObject(map).toString();
		String sign=JwUtil.makeSign(bizData);
		Map requestParams=JwUtil.makeRequestParams(bizData, service, 
				appkey,format, sign);
		String result=CommHelper.sendRequest(url, requestParams, "");
		Log.info("result: "+result);
	}
}
