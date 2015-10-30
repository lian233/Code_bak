package com.wofu.ecommerce.weipinhui.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import org.apache.http.util.EntityUtils;

import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.ecommerce.weipinhui.Params;
import com.wofu.ecommerce.weipinhui.util.CommHelper;

public class Test {
	public static void main(String[] args) {
		
//		JSONObject Ship = new JSONObject(); 
//		//准备要发出的数据
//		try {
//			
//			;
//			Ship.put("carrier_name","百世汇通(直发)");
//			
//			CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "test", Ship.toString());
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		
//		int pageIndex = 1;
//		
//		//获取订单列表
//		JSONObject jsonobj = new JSONObject();
//		//order_status	OrderStatus
//		try {
//			jsonobj.put("st_add_time", "2014-07-01 10:00:00");
//			jsonobj.put("et_add_time", "2014-11-01 10:00:00");
//			jsonobj.put("vendor_id", 550);
//			jsonobj.put("page", pageIndex);
//			jsonobj.put("limit", Integer.parseInt(Params.pageSize));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		//发送请求给唯品会
//		CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getOrderList", jsonobj.toString());
//		
//		///////////////////////////////////////////////////////////////
//		
//		//获取订单明细
//		jsonobj = new JSONObject();
//		try {
//			jsonobj.put("order_id", "14102800184722");
//			jsonobj.put("vendor_id", 550);
//			jsonobj.put("page", pageIndex);
//			jsonobj.put("limit", Integer.parseInt(Params.pageSize));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getOrderDetail", jsonobj.toString());
//		
//		///////////////////////////////////////////////////////////////
//		
//		//获取退货单列表
//		jsonobj = new JSONObject();
//		try {
//			jsonobj.put("st_create_time", "2014-07-01 10:00:00");
//			jsonobj.put("et_create_time", "2014-11-01 10:00:00");
//			jsonobj.put("vendor_id", 550);
//			jsonobj.put("page", pageIndex);
//			jsonobj.put("limit", Integer.parseInt(Params.pageSize));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getReturnList", jsonobj.toString());
//		
//		
//		//获取退货单明细
//		jsonobj = new JSONObject();
//		try {
//			jsonobj.put("back_sn", "6509");
//			jsonobj.put("vendor_id", 550);
//			jsonobj.put("page", pageIndex);
//			jsonobj.put("limit", Integer.parseInt(Params.pageSize));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getReturnProduct", jsonobj.toString());
		
	}
}
