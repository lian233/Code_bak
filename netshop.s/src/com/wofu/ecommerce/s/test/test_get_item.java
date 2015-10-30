package com.wofu.ecommerce.s.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.ecommerce.s.Params;
import com.wofu.ecommerce.s.UTF8_transformer;
import com.wofu.ecommerce.s.utils.Utils;

public class test_get_item 
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
		String method="scn.vendor.item.full.get";
		String ver=Params.ver;
		
		JSONObject data=new JSONObject();
		data.put("Fields", "vendor_item_id,item_name,brand_name,tag_price,color_name,item_status,show_date,down_date,update_date,product_no,detail.size_name,detail.update_date, detail.vendor_sku_id");
		data.put("VendorItemId","1");  //供货商商品ID
//		data.put("ProductNo", "111111");  //货号
//		data.put("ItemName", String.valueOf(null)); //商品名称
//		data.put("BrandName", String.valueOf(null)); //品牌名称
//		data.put("ItemStatus", String.valueOf(null));  //商品状态(1-在售 2-下架)
//		data.put("StartShowDate", String.valueOf(null)); //上架开始时间
//		data.put("EndShowDate", String.valueOf(null));    //上架结束时间
//		data.put("StartDownDate", String.valueOf(null));  //下架开始时间
//		data.put("EndDownDate", String.valueOf(null));    //下架结束时间
//		data.put("PageNo", String.valueOf(null));    //页码
//		data.put("PageSize", String.valueOf(null));  //每页条数。默认40，最大100
		String sign=Utils.get_sign(data, method, now);
		String output_to_server=Utils.post_data_process(method, data, now, sign).toString();
		String response=Utils.sendByPost(Params.url, output_to_server);
		JSONObject result=new JSONObject(response);
		System.out.println("记录总数:"+result.getInt("TotalResults"));
		JSONArray item_list=result.getJSONArray("Result");
		for(int i=0;i<item_list.length();i++)
		{
			System.out.println("供货商商品ID:"+item_list.getJSONObject(i).getString("VendorItemId"));
			System.out.println("货号:"+item_list.getJSONObject(i).getString("ProductNo"));
			System.out.println("商品名称:"+item_list.getJSONObject(i).getString("ItemName"));
			System.out.println("品牌名称:"+item_list.getJSONObject(i).getString("BrandName"));
			System.out.println("吊牌价:"+item_list.getJSONObject(i).getString("TagPrice"));
			System.out.println("颜色:"+item_list.getJSONObject(i).getString("ColorName"));
			System.out.println("商品状态:"+item_list.getJSONObject(i).getInt("ItemStatus"));
			System.out.println("上架时间:"+item_list.getJSONObject(i).getString("ShowDate"));
			System.out.println("下架时间:"+item_list.getJSONObject(i).getString("DownDate"));
			System.out.println("更新时间:"+item_list.getJSONObject(i).getString("UpdateDate"));
			JSONArray Skus=item_list.getJSONObject(i).getJSONArray("Skus");
			System.out.println("{");
			for(int j=0;j<Skus.length();j++)
			{
				System.out.println("	供货商SKU ID:"+Skus.getJSONObject(j).getString("VendorSkuId"));
				System.out.println("	尺码:"+Skus.getJSONObject(j).getString("SizeName"));
				System.out.println("	更新时间:"+Skus.getJSONObject(j).getString("UpdateDate"));
			}
			System.out.println("}");
			System.out.println();
		}
		if(result.get("ErrCode").equals(null)) System.out.println("错误代码:空");
		else 								   System.out.println("错误代码:"+result.get("ErrCode"));
		//System.out.println(result.getJSONArray("Result"));
	}

}
