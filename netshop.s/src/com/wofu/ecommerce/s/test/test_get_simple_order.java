package com.wofu.ecommerce.s.test;

import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;

public class test_get_simple_order
{
	//static JSONArray result=new JSONArray();
	static String temp=
					  "{"
					 +"  \"HasNext\": false,"
					 +"  \"Result\": ["
					 +"    {"
					 +"      \"GoodsPrice\": \"500.000\","
					 +"      \"OrderStatus\": 1,"
					 +"      \"RcvAddrDetail\": \"����ʡ ���Ƹ��� ������ ĳĳ����100��\","
					 +"      \"RcvAddrId\": \"320703\","
					 +"      \"RcvName\": \"�ջ���\","
					 +"      \"RcvTel\": \"13688888888\","
					 +"      \"SellerId\": \"scn\","
					 +"      \"SellerMemo\": \"���췢��\","
					 +"      \"SellerOrderNo\": \"20130608001\","
					 +"      \"SubmitDate\": \"2013-06-08 14:42:56\","
					 +"      \"UpdateDate\": \"2013-06-08 14:42:56\","
					 +"      \"VendorId\": \"test\","
					 +"      \"VendorOrderNo\": \"100000003979\""
					 +"    }"
					 +"  ],"
					 +"  \"TotalResults\": 1,"
					 +"  \"ErrCode\": null,"
					 +"  \"ErrMsg\": null,"
					 +"  \"IsError\": false"
					 +"}";
	public static void main(String args[])
	{
		//System.out.println(temp);
		try {
			init();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void init() throws JSONException
	{
		/****JSONArray��JSONObject�Ķ�ȡʵ��*****/
		JSONObject result=new JSONObject(temp); //����json
		JSONObject Result=result.getJSONArray("Result").getJSONObject(0);
		
		System.out.println(result.toString()+"\n\n\n");
		System.out.println(result.getJSONArray("Result").get(0)+"\n\n\n");
		
		System.out.println(Result.get("GoodsPrice"));
		//System.out.println(result.get);
	}
}
