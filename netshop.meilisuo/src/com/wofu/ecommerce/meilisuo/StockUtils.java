package com.wofu.ecommerce.meilisuo;
import java.util.HashMap;

import com.wofu.ecommerce.meilisuo.util.CommHelper;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class StockUtils {
	/**
	 * ��������˵���-û������Ʒ��
	 */
	public static void updateItemStock(String jobName,DataCentre dao ,int orgId,String url,String modify_type,
			ECS_StockConfig stockconfig,String vcode,int qty) throws JException
	{
		try
		{
			//������
			String apimethod="/goods/goods_stocks_edit?";
			HashMap<String,Object> reqMap = new HashMap<String,Object>();
			reqMap.put("twitter_id", stockconfig.getItemid());
			reqMap.put("modify_type", modify_type);
			reqMap.put("modify_value", String.valueOf(qty));
			reqMap.put("vcode", vcode);
			reqMap.put("skuid", stockconfig.getItemcode());
	        reqMap.put("apimethod", apimethod);
	        //��������
			String responseText = CommHelper.doRequest(reqMap,url,"get");
			//Log.info("���¿�淵������: "+responseText);
			//�ѷ��ص�����ת��json����
			JSONObject responseObj= new JSONObject(responseText);
			int code= responseObj.getInt("code");
			//������� 
			if(code!=0){   //{"code":100001,"message":"empty 2rd!","msg":null}
				 //��������
			  String errormessage = responseObj.getString("message");
			  if(!"".equals(errormessage))
				{
					Log.error(jobName,"��������˵���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"��,������Ϣ��"+ errormessage );
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg(errormessage.replaceAll("\"", ""));
					stockconfig.setStockcount(qty);
					dao.updateByKeys(stockconfig, "orgid,itemid");
					return ;
				}
			  
			}
		
			if(code==0)
			{
				Log.info("��������˵���ɹ�,���š�"+ stockconfig.getItemcode() +"��,�����:"+qty);
				stockconfig.setErrflag(0);
				stockconfig.setErrmsg("");
				stockconfig.setStockcount(qty);
				dao.updateByKeys(stockconfig, "orgid,itemid");
				
			}else
				Log.info("��������˵���ʧ��,���š�"+ stockconfig.getItemcode() +"��");
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg("");
				stockconfig.setStockcount(qty);
				dao.updateByKeys(stockconfig, "orgid,itemid");
			
			
		} catch (Exception e) 
		{
			Log.error("��������˵���","��������˵��Ʒ���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"����������Ϣ��"+e.getMessage()) ;
			stockconfig.setErrflag(1);
			stockconfig.setErrmsg(e.getMessage().replaceAll("\"", ""));
			stockconfig.setStockcount(qty);
			try {
				dao.updateByKeys(stockconfig, "orgid,itemid");
			} catch (Exception e1) {
				Log.error(jobName, "д���¿�������Ϣ����");
			}
		}
}
	/**
	 * �����һ�����ŵ����һ��sku����ѻ�����stockconfig���Ŀ���¼Ҳ����һ��
	 * ����sku���
	 */
	public static void updateSkuStock(String jobName,DataCentre dao,int orgId,String url,String modify_type,
			String vcode,ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty,Boolean isLast){
		try{
			//������
			String apimethod="/goods/goods_stocks_edit?";
			HashMap<String,Object> reqMap = new HashMap<String,Object>();
			reqMap.put("twitter_id", stockconfig.getItemid());
			reqMap.put("modify_type", modify_type);
			reqMap.put("modify_value", String.valueOf(qty));
			reqMap.put("vcode", vcode);
	        reqMap.put("skuid", stockconfigsku.getSkuid());
	        Log.info("skuid: "+stockconfigsku.getSkuid());
	        reqMap.put("apimethod", apimethod);
	       
	        //��������
			String responseText = CommHelper.doRequest(reqMap,url,"get");
			JSONObject responseObj= new JSONObject(responseText);
			int code= responseObj.getInt("code");
			Log.info("���¿�淵������: "+responseText);
				
				//������� 
				if(code!=0){ 
					 //��������
				  String errormessage = responseObj.getString("message");
						Log.error(jobName,"��������˵��Ʒ���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��,������Ϣ��"+ errormessage);
						return ;
					
				} 
				
				if(code==0)
				{
					Log.info("��������˵sku���ɹ�,���š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��,�����:"+qty);
					
					String sql=new StringBuilder().append("update ecs_stockconfigsku set errflag=0,errmsg='',stockcount=qty where orgid='").append(orgId)
						.append("'  and itemid ='").append(stockconfig.getItemid()).append("' and sku='").append(stockconfigsku.getSku()).append("'").toString();
					stockconfigsku.setErrflag(0);
					stockconfigsku.setErrmsg("");
					stockconfigsku.setStockcount(qty);
					dao.updateByKeys(stockconfigsku, "orgid,skuid,sku");
					if(isLast){     //���һ��sku,�ѻ��ŵĿ��Ҳһ�����
						//ͳ������������е�sku�Ŀ��
						sql=new StringBuilder().append("select sum(stockcount) from ecs_stockconfigsku where orgid='").append(orgId)
						.append("' and itemid='").append(stockconfig.getItemid()).append("'").toString();
						int totalCount = SQLHelper.intSelect(dao.getConnection(), sql);
						//���»��ſ��
						stockconfig.setErrflag(0);
						stockconfig.setErrmsg("");
						stockconfig.setStockcount(totalCount);
						dao.updateByKeys(stockconfig, "orgid,itemid");
						
					}
					
				}
				
		}catch(Exception e){
			Log.error(jobName,"��������˵��Ʒ���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��,������Ϣ��"+e.getMessage()) ;
			stockconfigsku.setErrflag(1);
			stockconfigsku.setErrmsg(e.getMessage().replaceAll("\"", ""));
			try {
				dao.updateByKeys(stockconfigsku, "orgid,skuid,sku");
			} catch (Exception e1) {
				
				Log.error(jobName, "��������´�����Ϣ����");
			}
			
		}
	}
	

	/*
	 * ��������˵��Ʒ�����ȡ��Ʒ���
	 */
	public static String getInventoryByproductCode(String produceCode,String app_key,String app_Secret,String format,String url){
		try{
			//������
			String apimethod="suning.custom.inventory.get";
			HashMap<String,String> reqMap = new HashMap<String,String>();
			reqMap.put("productCode", produceCode);
	        String ReqParams = CommHelper.getJsonStr(reqMap, "inventory");
	        HashMap<String,Object> map = new HashMap<String,Object>();
	        map.put("appSecret", app_Secret);
	        map.put("appMethod", apimethod);
	        map.put("format", format);
	        map.put("versionNo", "v1.2");
	        map.put("appRequestTime", CommHelper.getNowTime());
	        map.put("appKey", app_key);
	        map.put("resparams", ReqParams);
	        //��������
			String responseText = CommHelper.doRequest(map,url,"get");
			Log.info("ȡ��Ʒ��淵������Ϊ��"+responseText);
			if(responseText.indexOf("sn_responseContent")!=-1){
				//�ѷ��ص�����ת��json����
				JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
				//������� 
				if(responseText.indexOf("sn_error")!=-1){
					String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
					  if(!"".equals(operCode))
						{
							Log.error("����˵��ȡ��Ʒ�����ҵ", "��ȡ��Ʒ�����ҵʧ��,operCode:"+operCode);
						}
						return "0";
				} 
					 
				return responseObj.getJSONObject("sn_body").getJSONObject("inventory").getString("invNum");
			}else{
				Log.error("����˵��ȡ��Ʒ�����ҵ", "��ȡ��Ʒ�����ҵʧ��,û�����ݷ���");
				return "0";
			}
			
		}catch(Exception ex){
			Log.error("����˵��ȡ��Ʒ�����ҵ", "��ȡ��Ʒ�����ҵʧ��,operCode:"+ex.getMessage());
			return "0";
		}
		
	}
	


}