package com.wofu.ecommerce.suning;
import java.util.HashMap;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.ecommerce.suning.util.CommHelper;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class StockUtils {
	/**
	 * �����������-û������Ʒ��
	 */
	public static void updateItemStock(String jobName,DataCentre dao ,int orgId,String url,String format,
			ECS_StockConfig stockconfig,String app_key,String app_Secret,int qty) throws JException
	{
		try
		{
			//������
			String apimethod="suning.custom.inventory.modify";
			HashMap<String,String> reqMap = new HashMap<String,String>();
			reqMap.put("productCode", stockconfig.getItemid());
	        reqMap.put("destInvNum", String.valueOf(qty));
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
			String responseText = CommHelper.doRequest(map,url);
			
			//�ѷ��ص�����ת��json����
			JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
			//������� 
			if(responseText.indexOf("sn_error")!=-1){ 
				 //��������
			  String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
			  if(!"".equals(operCode))
				{
					Log.error(jobName,"�����������ʧ�ܣ����š�"+ stockconfig.getItemcode() +"��,������Ϣ��"+ operCode +"��" +operCode);
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg(operCode.replaceAll("\"", ""));
					stockconfig.setStockcount(qty);
					dao.updateByKeys(stockconfig, "orgid,itemid");
					return ;
				}
			  
			}
			
			String resultInfo = responseObj.getJSONObject("sn_body").getJSONObject("inventory").getString("result");
		
			if("Y".equals(resultInfo))
			{
				Log.info("�����������ɹ�,���š�"+ stockconfig.getItemcode() +"��,�����:"+qty);
				stockconfig.setErrflag(0);
				stockconfig.setErrmsg("");
				stockconfig.setStockcount(qty);
				dao.updateByKeys(stockconfig, "orgid,itemid");
				
			}else
				Log.info("�����������ʧ��,���š�"+ stockconfig.getItemcode() +"��");
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg("");
				stockconfig.setStockcount(qty);
				dao.updateByKeys(stockconfig, "orgid,itemid");
			
			
		} catch (Exception e) 
		{
			Log.error("�����������","����������Ʒ���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"����������Ϣ��"+e.getMessage()) ;
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
	 * �����һ�����ŵ����һ��sku����ѻ�����stockconfig��Ŀ���¼Ҳ����һ��
	 * ����sku���
	 */
	public static void updateSkuStock(String jobName,DataCentre dao,int orgId,String url,String format,
			String app_key,String app_Secret,ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty,Boolean isLast){
		try{
			//������
			if(stockconfig.getItemcode().equals("")||stockconfigsku.getSkuid().equals("")||stockconfigsku.getSku().equals("")){
				System.out.println("skuΪ��,����ͬ��");
				return;
			}
			String apimethod="suning.custom.inventory.modify";
			HashMap<String,String> reqMap = new HashMap<String,String>();
			reqMap.put("productCode", stockconfigsku.getSkuid());
	        reqMap.put("destInvNum", String.valueOf(qty));
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
	        System.out.println("������ͬ����淢�͵Ĳ���  "+map);
			String responseText = CommHelper.doRequest(map,url);
			if(responseText.indexOf("sn_responseContent")!=-1){
				//�ѷ��ص�����ת��json����
				JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
				//������� 
				if(responseText.indexOf("sn_error")!=-1){ 
					 //��������
				  String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
				  if(!"".equals(operCode))
					{
						Log.error(jobName,"����������Ʒ���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��,������Ϣ��"+ operCode +"��" +operCode);
						return ;
					}
					
				} 
				String resultInfo = responseObj.getJSONObject("sn_body").getJSONObject("inventory").getString("result");
				
				if("Y".equals(resultInfo))
				{
					Log.info("��������sku���ɹ�,���š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��,�����:"+qty);
					
					String sql=new StringBuilder().append("update ecs_stockconfigsku set errflag=0,errmsg='',stockcount=qty where orgid='").append(orgId)
						.append("'  and itemid ='").append(stockconfig.getItemid()).append("' and sku='").append(stockconfigsku.getSku()).append("'").toString();
					stockconfigsku.setErrflag(0);
					stockconfigsku.setErrmsg("");
					stockconfigsku.setStockcount(qty);
					dao.updateByKeys(stockconfigsku, "orgid,skuid");
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
					
				}else{
					Log.error(jobName,"����������Ʒ���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��") ;
					stockconfigsku.setErrflag(1);
					stockconfigsku.setErrmsg("���͸��¿���������");
					dao.updateByKeys(stockconfigsku, "orgid,skuid");
				}
				
			}else{
				Log.error(jobName,"����������Ʒ��淢���쳣�����š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��") ;
			}
			
			
		}catch(Exception e){
			Log.error(jobName,"����������Ʒ���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��,������Ϣ��"+e.getMessage()) ;
			stockconfigsku.setErrflag(1);
			stockconfigsku.setErrmsg(e.getMessage().replaceAll("\"", ""));
			try {
				dao.updateByKeys(stockconfigsku, "orgid,skuid");
			} catch (Exception e1) {
				
				Log.error(jobName, "��������´�����Ϣ����");
			}
			
		}
	}
	

	/*
	 * ����������Ʒ�����ȡ��Ʒ���
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
			String responseText = CommHelper.doRequest(map,url);
			Log.info("ȡ��Ʒ��淵������Ϊ��"+responseText);
			if(responseText.indexOf("sn_responseContent")!=-1){
				//�ѷ��ص�����ת��json����
				JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
				//������� 
				if(responseText.indexOf("sn_error")!=-1){
					String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
					  if(!"".equals(operCode))
						{
							Log.error("������ȡ��Ʒ�����ҵ", "��ȡ��Ʒ�����ҵʧ��,operCode:"+operCode);
						}
						return "0";
				} 
					 
				return responseObj.getJSONObject("sn_body").getJSONObject("inventory").getString("invNum");
			}else{
				Log.error("������ȡ��Ʒ�����ҵ", "��ȡ��Ʒ�����ҵʧ��,û�����ݷ���");
				return "0";
			}
			
		}catch(Exception ex){
			Log.error("������ȡ��Ʒ�����ҵ", "��ȡ��Ʒ�����ҵʧ��,operCode:"+ex.getMessage());
			return "0";
		}
		
	}
	


}
