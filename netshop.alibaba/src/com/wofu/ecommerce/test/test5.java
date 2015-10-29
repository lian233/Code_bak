package com.wofu.ecommerce.test;

import java.util.Date;
import java.util.Hashtable;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.service.Main;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.ecommerce.alibaba.Order;
import com.wofu.ecommerce.alibaba.Params;
import com.wofu.ecommerce.alibaba.api.ApiCallService;
import com.wofu.ecommerce.alibaba.util.CommonUtil;
/***
 * 测试：根据订单ID获取单独的订单信息
 * @author Administrator
 *
 */
public class test5 {
	public static void main(String[] args)throws Exception {
		Hashtable<String, String> params = new Hashtable<String, String>() ;
		params.put("orderId", "452894764244481");
		params.put("sellerMemberId", "b2b-1704364314");
		params.put("access_token", "4cf6e4dd-993a-43f7-b9b4-8f9a5c1f32b8");
		String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"trade.order.orderList.get",Params.version,Params.requestmodel,Params.appkey);
		String response = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
		System.out.println(response);
		JSONObject res=new JSONObject(response);
		JSONArray orderEntries=res.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0).getJSONArray("orderEntries");
		System.out.println(res.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0).getLong("id"));
		String orderdetailids="";
		for(int i=0; i<orderEntries.length();i++){
			JSONObject o=orderEntries.getJSONObject(i);
			orderdetailids=orderdetailids+o.getLong("id");
			if(i>=0&&i<orderEntries.length()-1){
				orderdetailids=orderdetailids+",";
			}
		}
		
		System.out.println("订单明细："+orderdetailids);
		System.out.println(orderEntries);
		
		//根据SKUID和商品ID得到sku
		String sqls="select sku from ecs_StockConfigsku where itemid="+"111"+" and skuid="+"14";
		System.out.println(sqls);
	}
}
