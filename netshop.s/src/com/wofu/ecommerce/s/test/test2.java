package com.wofu.ecommerce.s.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.ecommerce.s.Params;
import com.wofu.ecommerce.s.UTF8_transformer;
import com.wofu.ecommerce.s.utils.Utils;

public class test2
{

	/**
	 * @param args
	 */
	private static String jobName = "获取名鞋库订单作业";
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";  //Parmas类是从其他地方复制过来的，已经过了修改
	private static long daymillis=24*60*60*1000L;
	private static String lasttime;
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	public static void main(String[] args)
	{
		try {
			test2();
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
		String method="scn.vendor.order.brief.get";
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
			"v="+Params.ver+"&"+
			"app_key="+utf8_transformer.getUTF8String(Params.app_key)+"&"+
			"format=json"+"&"+
			"timestamp="+utf8_transformer.getUTF8String(df.format(now))+"&"+
			"sign="+sign.toUpperCase();		
		String responseOrderListData=Utils.sendByPost(Params.url, output_to_server);
		System.out.println(data+"\n");
		System.out.println(output_to_server+"\n");
		System.out.println(responseOrderListData+"\n");
	}

}
