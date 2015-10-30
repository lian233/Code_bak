package com.wofu.ecommerce.ecshop.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ecshop.Order;
import com.wofu.ecommerce.ecshop.OrderItem;
import com.wofu.ecommerce.ecshop.OrderUtils;
import com.wofu.ecommerce.ecshop.Params;
import com.wofu.ecommerce.ecshop.util.CommHelper;

/**
 * @author Administrator
 *
 */
public class Test1 {
	public static final String ss="{\"sn_request\":{\"sn_body\":{\"orderDelivery\":{\"orderCode\":\"1006495915\",\"expressNo\":\"668678223972\",\"sendDetail\":{'productCode': ['105678475']},\"expressCompanyCode\":\"H01\"}}}}";
	  // ��������
 	public static final String url="http://open.suning.com/api/http/sopRequest";
 	 // ��������
 	public static final String appKey="afb4e8b5194b169607d8399d889b6927";
     // �����appsecret
 	public static final  String appsecret = "5e107011f18bc9eabf825df457c590d7";
     // �����api������
     String apimethod ="suning.custom.orderdelivery.add";//5006015659  105000376
     String params = "{\"sn_request\": {\"sn_body\": {\"orderDelivery\": {'orderCode': \"3000789529\",\"expressNo\": \"B030103\",\"expressCompanyCode\": \"B01\",\"sendDetail\": {\"productCode\": ['102609881','102609933']}}}}}";
     // ��Ӧ��ʽ xml����json
     public static final  String format = "json";
	public static void sendRequest(){
		try{
			// �����api������
			String apimethod="set_product_store";
			HashMap<String,Object> reqMap = new HashMap<String,Object>();
	        reqMap.put("return_data", "json");
	        reqMap.put("act", apimethod);
	        reqMap.put("api_version", "1.0");
	        reqMap.put("product_id","690");
	        reqMap.put("store",199);
	        //��������
			String responseText = CommHelper.doRequest(reqMap,Params.url);
			Log.info("������˾�б�: "+responseText);

	}catch(Exception ex){
		ex.printStackTrace();
	}
		
}
	/**
	 * ����������Ʒ�����ȡ�̼�sku,��ƷͼƬ����
	 * @return
	 */
	public String[] getItemCodeByProduceCode(String productCode){
		String[] itemInfo= new String[2];
		try{
			for(int k=0;k<5;k++){
				//������
				String apiMethod="suning.custom.item.get";
				HashMap<String,String> reqMap = new HashMap<String,String>();
				reqMap.put("productCode", productCode);
			    HashMap<String,Object> map = new HashMap<String,Object>();
			    map.put("appSecret", Params.appsecret);
			    map.put("appMethod", apiMethod);
			    map.put("format", Params.format);
			    map.put("versionNo", "v1.2");
			    map.put("appKey", Params.appKey);
			     //��������
				String responseText = CommHelper.doRequest(map,Params.url);
				JSONObject item= new JSONObject(responseText).getJSONObject("sn_responseContent").getJSONObject("sn_body").getJSONObject("item");
				//û������Ʒ�����
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
			Log.info("��ȡ������Ʒ�������,��Ʒ����: "+productCode);
			return null;
		}
	}
	
	/**
	 * ��ȡ���ʶ���������
	 * @param code
	 */
	public static void getOrderDesByCode(String orderCode){
		try 
		{	
			//������
			String apimethod="suning.custom.order.get";
			HashMap<String,String> reqMap = new HashMap<String,String>();
	        reqMap.put("orderCode", orderCode);
	        HashMap<String,Object> map = new HashMap<String,Object>();
	        map.put("appSecret", appsecret);
	        map.put("appMethod", apimethod);
	        map.put("format", format);
	        map.put("versionNo", "v1.2");
	        map.put("appKey", appKey);
	        //��������
			String reponseText = CommHelper.doRequest(map,url);
			System.out.println(reponseText);
	}catch(Exception ex){
		ex.printStackTrace();
		}
	}
	
	
	public static void sendGood(String ss){
		System.out.println(ss);
		//������
		String apimethod="suning.custom.orderdelivery.add";
		
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("appSecret", appsecret);
        map.put("appMethod", apimethod);
        map.put("format", format);
        map.put("versionNo", "v1.2");
        map.put("appKey", appKey);
        map.put("resparams", ss);
        //��������
		String responseText = CommHelper.doRequest(map,Params.url);
		System.out.println(responseText);
	}
	
	
	public static void updateitemStock(String ss){
		System.out.println(ss);
		//������
		String apimethod="update_goods_number";
		HashMap<String,Object> reqMap = new HashMap<String,Object>();
        reqMap.put("return_data", "json");
        reqMap.put("act", apimethod);
        reqMap.put("api_version", "1.0");
        reqMap.put("goods_sn","JSK901");
        reqMap.put("store",21);
        //��������
		String responseText = CommHelper.doRequest(reqMap,Params.url);
		Log.info("������˾�б�: "+responseText);
	}
	
	
	public static void updateSkuStock(String ss){
		System.out.println(ss);
		//������
		String apimethod="update_products_number";
		HashMap<String,Object> reqMap = new HashMap<String,Object>();
        reqMap.put("return_data", "json");
        reqMap.put("act", apimethod);
        reqMap.put("api_version", "1.0");
        reqMap.put("product_id","JSK901A0831");
        reqMap.put("store",1);
        //��������
		String responseText = CommHelper.doRequest(reqMap,Params.url);
		Log.info("������˾�б�: "+responseText);
	}
	

	public static void main(String[] args) {
		updateitemStock("s");

	}
	

}
