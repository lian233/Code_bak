package com.wofu.ecommerce.weipinhui.test;
import java.util.HashMap;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weipinhui.Params;
import com.wofu.ecommerce.weipinhui.util.CommHelper;

/**
 * @author Administrator
 *
 */
public class Test1 {
	public static final String ss="{\"sn_request\":{\"sn_body\":{\"orderDelivery\":{\"orderCode\":\"1006495915\",\"expressNo\":\"668678223972\",\"sendDetail\":{'productCode': ['105678475']},\"expressCompanyCode\":\"H01\"}}}}";
	  // 访问令牌
 	 // 访问令牌
 	public static final String appKey="afb4e8b5194b169607d8399d889b6927";
     // 请求的appsecret
 	public static final  String appsecret = "5e107011f18bc9eabf825df457c590d7";
     // 请求的api方法名
     String apimethod ="suning.custom.orderdelivery.add";//5006015659  105000376
     
     // 响应格式 xml或者json
     public static final  String format = "json";
     
     public static final String sid="238";
     public static final String source="5fab7d4c0c3665041e89248a09d9d3bf";
     public static final String url = "http://visopen.vipshop.com/api/scm/";
     
     


	public static void main(String[] args) throws JSONException {
	String apimethod="carriers/get_carriers_list.php?";
	HashMap<String,Object> map = new HashMap<String,Object>();
    map.put("p", String.valueOf(1));
    map.put("source", source);
    map.put("sid", sid);
    map.put("vendor_id", "6326");
    map.put("apimethod", apimethod);

     //发送请求
     
		String responseText = CommHelper.doGet(map,url);
		Log.info("导出运营商结果: "+responseText);
		JSONObject result = new JSONObject(responseText);
		JSONArray arr = result.getJSONObject("data").getJSONArray("list");
		System.out.println("arr: "+arr.length());
		for(int i=0;i<arr.length();i++){
			JSONObject temp = arr.getJSONObject(i);
			System.out.println("temp: "+temp);
			System.out.println("物流: "+temp.getString("carriers_name"));
		}
	}
	

}
