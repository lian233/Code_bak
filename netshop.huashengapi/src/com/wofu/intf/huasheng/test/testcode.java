package com.wofu.intf.huasheng.test;

import com.wofu.common.json.JSONObject;
import com.wofu.ecommerce.huasheng.Params;
import com.wofu.ecommerce.huasheng.util.Utils;

public class testcode {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//发货测试
		
		//Params.apiurl = "http://seryhappy.imwork.net/wscenter/UpdateServlet";
		
//		String orderCode = "20015102917280397650";
//		String postCompanyCode = "顺丰";
//		String postNo = "102777468986";
//		
//		try {
//			System.out.println("发送发货请求 ...");
//			
//			String ParamData = "order_id=" + orderCode + "&express_company=" + postCompanyCode + "&express_id=" + postNo;
//
//			String result = Utils.doRequest("deliver", ParamData, false);
//			
//			if(result.equals(""))
//			{
//				System.out.println("无返回结果!");
//				return;
//			}
//			
//			JSONObject resultJson = new JSONObject(result);
//			if(resultJson.getBoolean("state"))
//			{
//				System.out.println("订单发货成功，单号【" + orderCode + "】，快递公司【" + postCompanyCode + "】，快递单号【" + postNo + "】") ;
//			}
//			else
//			{
//				System.out.println("订单发货失败，单号【" + orderCode + "】，快递公司【" + postCompanyCode + "】，快递单号【" + postNo + "】。错误信息：" + resultJson.getString("msg")) ;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		
		//同步库存测试
//		String ParamData = "type=set&skulist=6933102210975&qtylist=100";
//		Utils.doRequest("stock", ParamData, true);
	}

}
