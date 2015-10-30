package com.wofu.ecommerce.icbc.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.icbc.Order;
import com.wofu.ecommerce.icbc.OrderItem;
import com.wofu.ecommerce.icbc.OrderUtils;
import com.wofu.ecommerce.icbc.Params;
import com.wofu.ecommerce.icbc.util.CommHelper;

/**
 * @author Administrator
 *
 */
public class Test1 {
	public static final String ss="{\"sn_request\":{\"sn_body\":{\"orderDelivery\":{\"orderCode\":\"1006495915\",\"expressNo\":\"668678223972\",\"sendDetail\":{'productCode': ['105678475']},\"expressCompanyCode\":\"H01\"}}}}";
	  // 访问令牌
 	public static final String url="http://open.suning.com/api/http/sopRequest";
 	 // 访问令牌
 	public static final String appKey="afb4e8b5194b169607d8399d889b6927";
     // 请求的appsecret
 	public static final  String appsecret = "5e107011f18bc9eabf825df457c590d7";
     // 请求的api方法名
     String apimethod ="suning.custom.orderdelivery.add";//5006015659  105000376
     String params = "{\"sn_request\": {\"sn_body\": {\"orderDelivery\": {'orderCode': \"3000789529\",\"expressNo\": \"B030103\",\"expressCompanyCode\": \"B01\",\"sendDetail\": {\"productCode\": ['102609881','102609933']}}}}}";
     // 响应格式 xml或者json
     public static final  String format = "json";


	public static void main(String[] args) {
		//sendGood(ss);

	}
	

}
