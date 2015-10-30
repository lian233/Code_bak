package com.wofu.ecommerce.meilishuo2.trash;
//package com.wofu.ecommerce.meilishuo2;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.Date;
//import com.wofu.base.dbmanager.DataCentre;
//import com.wofu.common.tools.util.Formatter;
//import com.wofu.ecommerce.meilishuo2.util.Utils;
//import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
//
//public class StockUtils2
//{
//	public static void updateItemStock(String jobName,DataCentre dao ,int orgId,String url,String modify_type,
//			ECS_StockConfig stockconfig,String vcode,int qty)
//	{
//		
////		HashMap<String,Object> reqMap = new HashMap<String,Object>();
////		reqMap.put("twitter_id", stockconfig.getItemid());
////		reqMap.put("modify_type", modify_type); //用set可以直接设定，add用来加减
////		reqMap.put("modify_value", String.valueOf(qty));
////		reqMap.put("skuid", stockconfig.getItemcode());
//        //reqMap.put("apimethod", apimethod);
//		//组装：
//		Date now=new Date();
//		StringBuffer out_to_server=new StringBuffer();
//		out_to_server.append("http://api.open.meilishuo.com/router/rest?");
//		out_to_server.append("app_key=");
//		out_to_server.append(Params.appKey);
//		out_to_server.append("&");
//		out_to_server.append("fields=");
//		out_to_server.append("&");
//		out_to_server.append("format=");
//		out_to_server.append("&");
//		out_to_server.append("method=");
//		out_to_server.append("meilishuo.item.quantity.update"); //方法经常要改，以后包装成一个类的方法
//		out_to_server.append("&");
//		out_to_server.append("session=");
//		out_to_server.append("4b40946459c1d5f6e4c9e83285ca71e9"); //session也就是access_token一天过后就会过时，所以也要从数据库或者文件等地方动态获取
//		out_to_server.append("&");
//		out_to_server.append("sign_method=md5");
//		out_to_server.append("&");
//		out_to_server.append("timestamp=");
//		try
//		{
//			out_to_server.append(URLEncoder.encode(Formatter.format(now,
//					Formatter.DATE_TIME_FORMAT), "UTF-8"));
//		} catch (UnsupportedEncodingException e)
//		{
//			e.printStackTrace();
//		}
//		out_to_server.append("&");
//		out_to_server.append("v=1.0");
//		out_to_server.append("&");
//		out_to_server.append("twitter_id=");
//		out_to_server.append(stockconfig.getItemid());
//		out_to_server.append("&");
//		out_to_server.append("1st=");
//		out_to_server.append("&");
//		out_to_server.append("2rd=");
//		out_to_server.append("&");
//		out_to_server.append("goods_code=");
//		out_to_server.append("&");
//		out_to_server.append("sku_id=");
//		out_to_server.append(stockconfig.getItemcode());
//		out_to_server.append("&");
//		out_to_server.append("modify_type=");
//		out_to_server.append("set");
//		out_to_server.append("&");
//		out_to_server.append("modify_value=");
//		out_to_server.append(String.valueOf(qty));
//		out_to_server.append("&");
//		out_to_server.append("sign=");
//		out_to_server.append(Utils.get_sign("meilishuo.item.quantity.update", "4b40946459c1d5f6e4c9e83285ca71e9", now));
//		//发送请求并获取结果
//		String responseText=Utils.http_get(out_to_server.toString());
//		
//	}
//}
