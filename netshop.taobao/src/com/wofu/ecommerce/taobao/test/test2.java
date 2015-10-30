package com.wofu.ecommerce.taobao.test;


import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.ItemcatsGetRequest;
import com.taobao.api.response.ItemcatsGetResponse;

public class test2 {

	/**
	 * @param args
	 */
	
	public static String url = "http://gw.api.taobao.com/router/rest";
	public static String secret="766bce17fd8ac852ea02a740277f1289";
	public static String appkey="21520535";
	
	public static void main(String[] args) throws Exception {
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		ItemcatsGetRequest req=new ItemcatsGetRequest();
		req.setFields("cid");
		ItemcatsGetResponse response = client.execute(req);
		System.out.println("ww");
		System.out.println(response.getBody());

		
	}

}
