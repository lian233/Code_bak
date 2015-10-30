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
	  // ��������
 	public static final String url="http://open.suning.com/api/http/sopRequest";
 	 // ��������
 	public static final String appKey="0261dc89af6324eba6dcea1056e1f5be";
     // �����appsecret
 	public static final  String appsecret = "1960c3e21f578277d65a6820756af2b9";
     // �����api������
     String apimethod ="suning.custom.orderdelivery.add";//5006015659  105000376
     String params = "{\"sn_request\": {\"sn_body\": {\"orderDelivery\": {'orderCode': \"3000789529\",\"expressNo\": \"B030103\",\"expressCompanyCode\": \"B01\",\"sendDetail\": {\"productCode\": ['102609881','102609933']}}}}}";
     // ��Ӧ��ʽ xml����json
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
			// �����api������
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
		    //��������
			String reponseText = CommHelper.doRequest(map,url);
			//�ѷ��ص�����ת��json����
			JSONObject responseObj= new JSONObject(reponseText).getJSONObject("responseContent");
			//ͳ����Ϣ
			JSONObject totalInfo = responseObj.getJSONObject("sn_head");
			//������� 
			JSONObject errorObj= responseObj.getJSONObject("sn_error");
			if(errorObj==null){
				String operCode=(String)responseObj.getJSONObject("sn_error").get("error_code");
				Log.error("��ȡ���������б�", "��ȡ�����б�ʧ�ܣ������룺"+operCode);
			}

			//��ҳ��
			String pageTotal = (String)totalInfo.get("pageTotal");
			//����Ԫ��
			JSONArray ordersList = responseObj.getJSONObject("sn_body").getJSONArray("orderQuery");
			for(int i = 0 ; i< ordersList.length() ; i++)
			{	//ĳ������
				JSONObject orderInfo = ordersList.getJSONObject(i);
				//������� 
				String orderCode = (String)orderInfo.get("orderCode");
				//������Ʒ����
				JSONArray items = orderInfo.getJSONArray("orderDetail");
				//����һ����������
				Order o = new Order();
				o.setObjValue(o, orderInfo);
				o.setFieldValue(o, "orderItemList", items);
				//������Ʒ��ĳЩ����
				for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
				{
					OrderItem item=(OrderItem) ito.next();
					//��Ʒsku
					String itemCode= OrderUtils.getItemCodeByProduceCode(item.getProductCode(),appKey,appsecret,format)[0];
					item.setItemCode(itemCode);
					//��ƷͼƬ����
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
			    String ReqParams = CommHelper.getJsonStr(reqMap, "item");
			    HashMap<String,Object> map = new HashMap<String,Object>();
			    map.put("appSecret", Params.appsecret);
			    map.put("appMethod", apiMethod);
			    map.put("format", Params.format);
			    map.put("versionNo", "v1.2");
			    map.put("appRequestTime", CommHelper.getNowTime());
			    map.put("appKey", Params.appKey);
			    map.put("resparams", ReqParams);
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
	        String ReqParams = CommHelper.getJsonStr(reqMap, "orderGet");
	        HashMap<String,Object> map = new HashMap<String,Object>();
	        map.put("appSecret", appsecret);
	        map.put("appMethod", apimethod);
	        map.put("format", format);
	        map.put("versionNo", "v1.2");
	        map.put("appRequestTime", CommHelper.getNowTime());
	        map.put("appKey", appKey);
	        map.put("resparams", ReqParams);
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
        map.put("appRequestTime", CommHelper.getNowTime());
        map.put("appKey", appKey);
        map.put("resparams", ss);
        //��������
		String responseText = CommHelper.doRequest(map,Params.url);
		System.out.println(responseText);
	}
	

	public static void main(String[] args) {
		//sendGood(ss);
		getCompanyCode();

	}
	
	//��ѯ������˾
	private static void getCompanyCode(){
		//������
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
        //��������
		String responseText = CommHelper.doRequest(map,Params.url);
		System.out.println(responseText);
	}
	
	
	/**
	 * ��ȡ������˾����
	 * @param code
	 */
	public static void getDeliveryComCode(String orderCode){
		try 
		{	
			//������
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
	        //��������
			String reponseText = CommHelper.doRequest(map,url);
			System.out.println(reponseText);
	}catch(Exception ex){
		ex.printStackTrace();
		}
	}
	

}
