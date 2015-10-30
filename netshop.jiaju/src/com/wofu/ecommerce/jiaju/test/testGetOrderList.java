package com.wofu.ecommerce.jiaju.test;

import java.util.HashMap;

import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.jiaju.utils.CommHelper;

public class testGetOrderList {

	public static void main(String[] args) throws Exception {
		/*http://zf.www.jiaju.com/openapi/?service=get_orders_to_send&sign=c764cd660faa58a29704fccf0c656724&type=MD5&doc=xml&partner_id=201307180002
		
		签名机制
数据签名需要使用客户的私有密钥，对提交数据按照自然排序(a-z升序)，然后构造成一个请求的querystring,如果querystring中含有
中文编码，请使用urlencode进行转义。 例如对下面的请求数据使用私有密钥进行加密的PHP实例:

		*/
		String URL = "http://www.jiaju.com/openapi/";
		String service = "get_orders_to_send";
		String type = "MD5";
		String partner_id = "102452";	//102452
		String Partner_pwd = "OTU4MTZhMjg5NGMwZGVmNmMxMmRjNzMyNDFkNDE5NjkxMDI0NTI=";
		String doc = "json";
		
		HashMap<String, String> Data = new HashMap<String, String>();
		Data.put("service", service);
		Data.put("type", type);
		Data.put("partner_id", partner_id);
		Data.put("doc", doc);
		
		try
		{
			//获取数据库conn
			//从数据库获取配置信息
			
			
			//排序
			String sortStr = CommHelper.sortKey(Data);
			Log.info(sortStr);
			//加上数字签名
			String Signed = CommHelper.makeSign(sortStr, Partner_pwd);
			Log.info(Signed);
			//发送请求
			String Result = CommHelper.sendByPost(URL, Signed);
			//Log.info(Result);
			System.out.println(Result);
			
			//解析返回的Json
			
			//根据配置信息判断:
			//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
			
			//*获取更多订单信息
			
			//OrderManager.isCheck(
			
			//检查订单是否存在
			//录入订单信息到数据库
			//OrderManager.
		}
		catch(Exception err)
		{
			Log.error("发送请求出错",err.getMessage());
		}
	}

}
