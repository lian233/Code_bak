package com.wofu.ecommerce.s.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.ecommerce.s.Params;
import com.wofu.ecommerce.s.UTF8_transformer;
import com.wofu.ecommerce.s.utils.Utils;

public class test_get_detail_orders 
{
	private static String jobName = "获取名鞋库订单作业";
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";  //Parmas类是从其他地方复制过来的，已经过了修改
	private static long daymillis=24*60*60*1000L;
	private static String lasttime;
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	public static void main(String[] args)
	{
		try {
			test2();
			System.out.println("end");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void test2() throws JSONException, ParseException
	{
		UTF8_transformer utf8_transformer=new UTF8_transformer();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		Date now=new Date();
		//Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
		//Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
		String method="scn.vendor.order.full.get";
		String ver=Params.ver;

		/***data部分***/
		JSONObject data=new JSONObject();
		//需要返回的字段：
		data.put("Fields",Params.Fields);	
		/**以下都不是必须的**/
		data.put("StartUpdateDate", Params.StartUpdateDate); //订单更新开始时间
		data.put("EndUpdateDate", Params.EndUpdateDate);   //订单更新结束时间
		data.put("StartSubmitDate", Params.StartSubmitDate); //订单提交开始时间
		data.put("EndSubmitDate", Params.EndSubmitDate);   //订单提交结束时间
		data.put("SellerId", Params.SellerId);        //分销商ID(外部商家该值留空)
		data.put("SellerOrderNo", Params.SellerOrderNo);   //分销商订单号 	
		data.put("VendorOrderNo", Params.VendorOrderNo);   //供货商订单号
		data.put("OrderStatus", Params.OrderStatus);     //订单状态(1-未处理 2-已确认 3-已发货 4-已作废)
		data.put("PageNo", Params.PageNo);          //页码
		data.put("PageSize", Params.PageSize);        //每页条数。默认40，最大100
		/**sign部分***/
		String sign=Params.app_Secret
		+"app_key"+Params.app_key
		+"data"+data.toString()
		+"format"+Params.format
		+"method"+method
		+"timestamp"+df.format(now)
		+"v"+ver;
		sign=MD5Util.getMD5Code(sign.getBytes());
		/***合并为输出语句****/
		String output_to_server=
			"data="+utf8_transformer.getUTF8String(data.toString())+"&"+
			"method="+utf8_transformer.getUTF8String(method)+"&"+
			"v="+utf8_transformer.getUTF8String(Params.ver)+"&"+
			"app_key="+utf8_transformer.getUTF8String(Params.app_key)+"&"+
			"format="+utf8_transformer.getUTF8String("json")+"&"+
			"timestamp="+utf8_transformer.getUTF8String(df.format(now))+"&"+
			"sign="+utf8_transformer.getUTF8String(sign.toUpperCase());		
		String responseOrderListData=Utils.sendByPost(Params.url, output_to_server);	
		//String responseOrderListData=Utils.sendByPost(Params.url, utf8_transformer.getUTF8String(output_to_server));
		System.out.println(data+"\n");
		System.out.println(output_to_server+"\n");
		System.out.println(responseOrderListData+"\n");
		JSONObject responseResult=new JSONObject(responseOrderListData);
		System.out.println("记录总数:"+responseResult.getInt("TotalResults"));
		if(responseResult.get("ErrCode").equals(null))
			System.out.println("错误代码:空");
		else
			System.out.println("错误代码:"+responseResult.get("ErrCode"));
		System.out.println("错误信息:"+responseResult.get("ErrMsg"));
		/**订单结果集合**/
		JSONArray orderlist=responseResult.getJSONArray("Result");
		System.out.println("订单结果集合:"+orderlist.toString());
		for(int j=0;j<orderlist.length();j++)
		{
			JSONObject order=orderlist.getJSONObject(j);
			System.out.println("结果集合"+j+":"+order.toString());
			/**订单号**/
			System.out.println("供货商订单号:"+order.getString("VendorOrderNo")); //供货商订单号
		}
		/**订单明细**/
		JSONArray  tempArray1=responseResult.getJSONArray("Result");
		JSONObject tempObject1=tempArray1.getJSONObject(0);
		JSONArray  tempArray2=tempObject1.getJSONArray("OrderDets");
		JSONObject tempObject2=tempArray2.getJSONObject(0);
		System.out.println("订单明细:"+tempObject2.toString());
		
		JSONArray OrderDets=responseResult.getJSONArray("Result").getJSONObject(0).getJSONArray("OrderDets");
		for(int j=0;j<OrderDets.length();j++)
		{
			JSONObject object=OrderDets.getJSONObject(j);
			System.out.println("订单明细："+object.toString());
		}
		/**记录总数**/
		System.out.println("记录总数:"+responseResult.getInt("TotalResults"));
//		for(int j=0;j<responseResult.getInt("TotalResults");j++)
//		{
//			
//		}
	}

}
