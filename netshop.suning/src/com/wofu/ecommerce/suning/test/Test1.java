package com.wofu.ecommerce.suning.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.suning.Order;
import com.wofu.ecommerce.suning.OrderItem;
import com.wofu.ecommerce.suning.OrderUtils;
import com.wofu.ecommerce.suning.Params;
import com.wofu.ecommerce.suning.util.CommHelper;

/**
 * @author Administrator
 *
 */
public class Test1 {
	public static final String ss="{\"sn_request\":{\"sn_body\":{\"orderDelivery\":{\"orderCode\":\"1006495915\",\"expressNo\":\"668678223972\",\"sendDetail\":{'productCode': ['105678475']},\"expressCompanyCode\":\"H01\"}}}}";
	  // 访问令牌
 	public static final String url="http://open.suning.com/api/http/sopRequest";
 	 // 访问令牌
 	public static final String appKey="0261dc89af6324eba6dcea1056e1f5be";
     // 请求的appsecret
 	public static final  String appsecret = "1960c3e21f578277d65a6820756af2b9";
     // 请求的api方法名
     String apimethod ="suning.custom.orderdelivery.add";//5006015659  105000376
     String params = "{\"sn_request\": {\"sn_body\": {\"orderDelivery\": {'orderCode': \"3000789529\",\"expressNo\": \"B030103\",\"expressCompanyCode\": \"B01\",\"sendDetail\": {\"productCode\": ['102609881','102609933']}}}}}";
     // 响应格式 xml或者json
     public static final  String format = "json";
     /**
      * <sn_request>
2	  <sn_body>
3	    <logisticCompany>
4	      <pageNo>1</pageNo> 
5	      <pageSize>20</pageSize>
6	    </logisticCompany>
7	  </sn_body>
8	</sn_request>
      */
	public static void sendRequest(){
		try{
			// 请求的api方法名
		    String apimethod ="suning.custom.order.query";
		    HashMap<String,String> reqMap = new HashMap<String,String>();
		    reqMap.put("orderStatus", "10");
		    String ReqParams = CommHelper.getJsonStr(reqMap, "orderQuery");
		    HashMap<String,Object> map = new HashMap<String,Object>();
		    map.put("appSecret", appsecret);
		    map.put("appMethod", apimethod);
		    map.put("format", format);
		    map.put("versionNo", "v1.2");
		    map.put("appRequestTime", CommHelper.getNowTime());
		    map.put("appKey", appKey);
		    map.put("resparams", ReqParams);
		    //发送请求
			String reponseText = CommHelper.doRequest(map,url);
			//把返回的数据转成json对象
			JSONObject responseObj= new JSONObject(reponseText).getJSONObject("responseContent");
			//统计信息
			JSONObject totalInfo = responseObj.getJSONObject("sn_head");
			//错误对象 
			JSONObject errorObj= responseObj.getJSONObject("sn_error");
			if(errorObj==null){
				String operCode=(String)responseObj.getJSONObject("sn_error").get("error_code");
				Log.error("获取苏宁订单列表", "获取订单列表失败，操作码："+operCode);
			}

			//总页数
			String pageTotal = (String)totalInfo.get("pageTotal");
			//订单元素
			JSONArray ordersList = responseObj.getJSONObject("sn_body").getJSONArray("orderQuery");
			for(int i = 0 ; i< ordersList.length() ; i++)
			{	//某个订单
				JSONObject orderInfo = ordersList.getJSONObject(i);
				//订单编号 
				String orderCode = (String)orderInfo.get("orderCode");
				//订单商品集合
				JSONArray items = orderInfo.getJSONArray("orderDetail");
				//构造一个订单对象
				Order o = new Order();
				o.setObjValue(o, orderInfo);
				o.setFieldValue(o, "orderItemList", items);
				//设置商品的某些属性
				for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
				{
					OrderItem item=(OrderItem) ito.next();
					//商品sku
					String itemCode= OrderUtils.getItemCodeByProduceCode(item.getProductCode(),appKey,appsecret,format)[0];
					item.setItemCode(itemCode);
					//商品图片链接
					String itemImg= OrderUtils.getItemCodeByProduceCode(item.getProductCode(),appKey,appsecret,format)[1];
					item.setPicPath(itemImg);
				}
				Date createTime = o.getOrderSaleTime();
				

		}

	}catch(Exception ex){
		ex.printStackTrace();
	}
		
}
	/**
	 * 根据苏宁产品编码获取商家sku,商品图片链接
	 * @return
	 */
	public String[] getItemCodeByProduceCode(String productCode){
		String[] itemInfo= new String[2];
		try{
			for(int k=0;k<5;k++){
				//方法名
				String apiMethod="suning.custom.item.get";
				HashMap<String,String> reqMap = new HashMap<String,String>();
				reqMap.put("productCode", productCode);
			    String ReqParams = CommHelper.getJsonStr(reqMap, "item");
			    HashMap<String,Object> map = new HashMap<String,Object>();
			    map.put("appSecret", Params.appsecret);
			    map.put("appMethod", apiMethod);
			    map.put("format", Params.format);
			    map.put("versionNo", "v1.2");
			    map.put("appRequestTime", CommHelper.getNowTime());
			    map.put("appKey", Params.appKey);
			    map.put("resparams", ReqParams);
			     //发送请求
				String responseText = CommHelper.doRequest(map,Params.url);
				JSONObject item= new JSONObject(responseText).getJSONObject("sn_responseContent").getJSONObject("sn_body").getJSONObject("item");
				//没有子商品的情况
				String productCodeTemp=item.getString("productCode");
				if(productCodeTemp.equals(productCode)){
					itemInfo[0]=item.getString("itemCode");
					itemInfo[1]=item.getString("img1Url");
				}else{
					JSONArray childitems=item.getJSONArray("childItem");
					for(int i=0;i<childitems.length();i++){
						JSONObject chItem=childitems.getJSONObject(i);
						if(productCode.equals(chItem.getString("productCode"))){
							itemInfo[0]=chItem.getString("itemCode");
							itemInfo[1]=chItem.getString("img1Url");
						}
					}
				}
				break;
			}
			return itemInfo;
			
		}catch(Exception ex){
			Log.info("获取苏宁商品详情出错,产品编码: "+productCode);
			return null;
		}
	}
	
	/**
	 * 获取单笔订单的详情
	 * @param code
	 */
	public static void getOrderDesByCode(String orderCode){
		try 
		{	
			//方法名
			String apimethod="suning.custom.order.get";
			HashMap<String,String> reqMap = new HashMap<String,String>();
	        reqMap.put("orderCode", orderCode);
	        String ReqParams = CommHelper.getJsonStr(reqMap, "orderGet");
	        HashMap<String,Object> map = new HashMap<String,Object>();
	        map.put("appSecret", appsecret);
	        map.put("appMethod", apimethod);
	        map.put("format", format);
	        map.put("versionNo", "v1.2");
	        map.put("appRequestTime", CommHelper.getNowTime());
	        map.put("appKey", appKey);
	        map.put("resparams", ReqParams);
	        //发送请求
			String reponseText = CommHelper.doRequest(map,url);
			System.out.println(reponseText);
	}catch(Exception ex){
		ex.printStackTrace();
		}
	}
	
	
	public static void sendGood(String ss){
		System.out.println(ss);
		//方法名
		String apimethod="suning.custom.orderdelivery.add";
		
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("appSecret", appsecret);
        map.put("appMethod", apimethod);
        map.put("format", format);
        map.put("versionNo", "v1.2");
        map.put("appRequestTime", CommHelper.getNowTime());
        map.put("appKey", appKey);
        map.put("resparams", ss);
        //发送请求
		String responseText = CommHelper.doRequest(map,Params.url);
		System.out.println(responseText);
	}
	

	public static void main(String[] args) {
		//sendGood(ss);
		getCompanyCode();

	}
	
	//查询物流公司
	private static void getCompanyCode(){
		//方法名
		String apimethod="suning.custom.logisticcompany.query";
		HashMap<String,String> reqMap = new HashMap<String,String>();
		reqMap.put("pageSize", "50");
        reqMap.put("pageNo", "1");
        String ReqParams = CommHelper.getJsonStr(reqMap, "logisticCompany");
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("appSecret", appsecret);
        map.put("appMethod", apimethod);
        map.put("format", format);
        map.put("versionNo", "v1.2");
        map.put("appRequestTime", CommHelper.getNowTime());
        map.put("appKey", appKey);
        map.put("resparams", ReqParams);
        //发送请求
		String responseText = CommHelper.doRequest(map,Params.url);
		System.out.println(responseText);
	}
	
	
	/**
	 * 获取物流公司代码
	 * @param code
	 */
	public static void getDeliveryComCode(String orderCode){
		try 
		{	
			//方法名
			String apimethod="suning.custom.logisticcompany.query";
			HashMap<String,String> reqMap = new HashMap<String,String>();
	        reqMap.put("orderCode", orderCode);
	        String ReqParams = CommHelper.getJsonStr(reqMap, "orderGet");
	        HashMap<String,Object> map = new HashMap<String,Object>();
	        map.put("appSecret", appsecret);
	        map.put("appMethod", apimethod);
	        map.put("format", format);
	        map.put("versionNo", "v1.2");
	        map.put("appRequestTime", CommHelper.getNowTime());
	        map.put("appKey", appKey);
	        map.put("resparams", ReqParams);
	        //发送请求
			String reponseText = CommHelper.doRequest(map,url);
			System.out.println(reponseText);
	}catch(Exception ex){
		ex.printStackTrace();
		}
	}
	

}
