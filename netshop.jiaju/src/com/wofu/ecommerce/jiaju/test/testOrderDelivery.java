package com.wofu.ecommerce.jiaju.test;

import java.util.HashMap;

import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.jiaju.utils.CommHelper;

public class testOrderDelivery {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String URL = "http://www.jiaju.com/openapi/";
		String service = "order_send";
		String type = "MD5";
		String partner_id = "102452";	//102452
		String Partner_pwd = "OTU4MTZhMjg5NGMwZGVmNmMxMmRjNzMyNDFkNDE5NjkxMDI0NTI=";
		String doc = "json";
		
		HashMap<String, String> Data = new HashMap<String, String>();
		Data.put("service", service);
		Data.put("type", type);
		Data.put("partner_id", partner_id);
		Data.put("doc", doc);
		Data.put("order_id", "1450369,1450368");//9138515194
		Data.put("ship_name", "韵达");
		Data.put("ship_no", "3100374683892");
		
		try
		{
			//排序
			String sortStr = CommHelper.sortKey(Data);
			//Log.info(sortStr);
			//加上数字签名
			String Signed = CommHelper.makeSign(sortStr, Partner_pwd);
			Log.info(Signed);
			//发送请求
			String Result = CommHelper.sendByPost(URL, Signed);

			System.out.println(Result);
		}
		catch(Exception err)
		{
			Log.error("发送请求出错",err.getMessage());
		}
	}

}
