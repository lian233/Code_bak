package com.wofu.netshop.alibaba.fenxiao;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.netshop.alibaba.fenxiao.api.ApiCallService;
import com.wofu.netshop.alibaba.fenxiao.util.CommonUtil;
public class StockUtil
{
	private static String find_apiName="offer.get";
	private static String modify_apiName="offer.modify";
	private static String returnFieldsT ="offerId,offerStatus,subject,amount,gmtModified,productFeatureList,skuArray";

	//���°���Ͱ���Ʒ��� 
	public static void updateStock(DataCentre dao,String SERVER_URL,String token,
			String appKey,String appSecret,ECS_StockConfigSku stockconfigsku,int qty,int version
			,String namespace,String requestmodel) throws Exception
	{
		try
		{
			//��ȡ��Ʒ������
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("offerId",stockconfigsku.getItemid());
			params.put("returnFields", "offerId,offerStatus,subject,amount,type,gmtCreate,gmtModified,skuArray,productFeatureList");
			
			String urlPath=CommonUtil.buildInvokeUrlPath(namespace,find_apiName,version,requestmodel,appKey);
			String response =ApiCallService.callApiTest(SERVER_URL, urlPath, appSecret, params);
			JSONObject res=new JSONObject(response);
			
			JSONObject jo=res.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0);
			//��ȡ�������Ʒ���е�sku
			JSONArray jr=jo.getJSONArray("skuArray");
			//����SKU
			for(int i=0;i<jr.length();i++){
				JSONObject skujo=jr.getJSONObject(i);
				//����SKU�µ�children��Ϣ  �Ѷ�Ӧ��children�Ŀ�������޸�Ϊqty
				for(int j=0;j<skujo.getJSONArray("children").length();j++){
					JSONObject childjo=skujo.getJSONArray("children").getJSONObject(j);
					if(stockconfigsku.getSku().equals(childjo.getString("cargoNumber"))){
						childjo.put("canBookCount", qty);
					}
					
				}
			}
			//�ύJSON����ϸ���
			Hashtable<String, String> params1 = new Hashtable<String, String>() ;
			params1.put("offer", res.toString());
			params1.put("access_token", token);
			String urlPath1=CommonUtil.buildInvokeUrlPath(namespace,modify_apiName,version,requestmodel,appKey);
			//ִ���޸�
			String response1 =ApiCallService.callApiTest(SERVER_URL, urlPath1, appSecret, params1);
			Log.info("���¿�淵�ؽ��: "+response1);
			JSONObject mojo=new JSONObject(response1);
			
			
			if(mojo.getBoolean("success"))
			{
				Log.info("���°���ͰͿ��ɹ�,SKU��"+ stockconfigsku.getSku() +"��,���:"+ qty);
				
				stockconfigsku.setStockcount(qty);
				dao.update(stockconfigsku,"stockcount","orgid,itemid,skuid");
			}
			else
			{
				Log.info("���°���ͰͿ��ʧ��!SKU��"+ stockconfigsku.getSku() +"��,������Ϣ:"+mojo.getString("error_message"));
				stockconfigsku.setErrmsg(mojo.getString("error_message"));
				dao.update(stockconfigsku,"errmsg","orgid,itemid,skuid");
			}
		} catch (Exception e) {
			
			Log.info("���°���ͰͿ��ʧ��,SKU��"+stockconfigsku.getSku()+"��,������Ϣ:"+e.getMessage());
			
			stockconfigsku.setErrmsg(e.getMessage());
			dao.update(stockconfigsku,"errmsg","orgid,itemid,skuid");	
			
		}
		
				
	}
	
}
