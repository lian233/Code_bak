package com.wofu.ecommerce.s.test;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.ecommerce.s.Params;
import com.wofu.ecommerce.s.UTF8_transformer;
import com.wofu.ecommerce.s.utils.Utils;

public class test_get_OrderDelivery 
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
		String method="scn.vendor.order.delivery";
		String ver=Params.ver;
		/**快递信息**/
		JSONArray kuai_di=new JSONArray();
		JSONObject item_1=new JSONObject();
		try {
			item_1.put("ExpressCompanyId",new String("顺丰".getBytes("ISO-8859-1"),"utf-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		item_1.put("ExpressNo", "966808137833");
		item_1.put("SkuQtyPair", "TestSkuId001:3");
		JSONObject item_2=new JSONObject();
		try {
			item_2.put("ExpressCompanyId",new String("顺丰".getBytes("ISO-8859-1"),"utf-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		item_2.put("ExpressNo", "966808137834");
		item_2.put("SkuQtyPair", "TestSkuId002:2");
		kuai_di.put(item_1);
		kuai_di.put(item_2);
		/***data部分***/
		JSONObject data=new JSONObject();
		//需要返回的字段：
		data.put("ShippingFee", Params.ShippingFee);   //配送费用
		data.put("VendorOrderNo", Params.VendorOrderNo);   //供货商订单号
		//data.put("VendorMemo", Params.VendorMemo);    //供货商备注
		data.put("OrdExpress", kuai_di);    //快递明细
		/**sign部分***/
		System.out.println(df.format(now));
		String sign=Utils.get_sign(data, method, now);
		System.out.println("sign:   "+sign);
		/***合并为输出语句****/
		String output_to_server = Utils.post_data_process(method, data, now, sign).toString();	
		String responseOrderListData=Utils.sendByPost(Params.url, output_to_server);
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
