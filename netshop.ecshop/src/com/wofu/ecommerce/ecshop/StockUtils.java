package com.wofu.ecommerce.ecshop;
import java.util.HashMap;

import com.wofu.ecommerce.ecshop.util.CommHelper;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class StockUtils {
	/**
	 * ����ecshop���-û������Ʒ��
	 */
	public static void updateItemStock(String jobName,DataCentre dao ,int orgId,String url,
			ECS_StockConfig stockconfig,int qty) throws JException
	{
		try
		{
			//������
			String apimethod="update_goods_number";
			HashMap<String,Object> reqMap = new HashMap<String,Object>();
	        reqMap.put("return_data", "json");
	        reqMap.put("act", apimethod);
	        reqMap.put("api_version", "1.0");
	        reqMap.put("goods_sn",stockconfig.getItemcode());
	        reqMap.put("store",qty);
	        //��������
			String responseText = CommHelper.doRequest(reqMap,url);
			Log.info("responseText:��"+responseText);
			//�ѷ��ص�����ת��json����
			JSONObject responseObj= new JSONObject(responseText);
			//������� 
			if(!"success".equals(responseObj.getString("result"))){ 
				 //��������
			  String errorMessage = responseObj.getString("msg");
			  if(!"".equals(errorMessage))
				{
					Log.error(jobName,"����ecshop���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"��,������Ϣ��"+ errorMessage);
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg(errorMessage.replaceAll("\"", ""));
					stockconfig.setStockcount(qty);
					dao.updateByKeys(stockconfig, "orgid,itemid");
					return ;
				}
			  
			}else{
				Log.info("����ecshop���ɹ�,���š�"+ stockconfig.getItemcode() +"��,�����:"+qty);
				stockconfig.setErrflag(0);
				stockconfig.setErrmsg("");
				stockconfig.setStockcount(qty);
				dao.updateByKeys(stockconfig, "orgid,itemid");
			}
			
		} catch (Exception e) 
		{
			Log.error("����ecshop���","����ecshop��Ʒ���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"����������Ϣ��"+e.getMessage()) ;
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
	public static void updateSkuStock(String jobName,DataCentre dao,int orgId,String url,ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty,Boolean isLast){
		try{
			//������
			String apimethod="update_products_number";
			HashMap<String,Object> reqMap = new HashMap<String,Object>();
	        reqMap.put("return_data", "json");
	        reqMap.put("act", apimethod);
	        reqMap.put("api_version", "1.0");
	        reqMap.put("product_id",stockconfigsku.getSku());
	        reqMap.put("store",qty);
	        //��������
	        
			String responseText = CommHelper.doRequest(reqMap,Params.url);
			Log.info("responseText: "+responseText);
			if(responseText.indexOf("sn_responseContent")!=-1){
				//�ѷ��ص�����ת��json����
				JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
				//������� 
				if(!"success".equals(responseObj.getString("result"))){ 
					 //��������
				  String errorMessage = responseObj.getString("msg");
				  if(!"".equals(errorMessage))
					{
						Log.error(jobName,"����ecshop��Ʒ���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��,������Ϣ��"+ errorMessage );
						return ;
					}
					
				} else{
					Log.info("����ecshopsku���ɹ�,���š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��,�����:"+qty);
					stockconfigsku.setErrflag(0);
					stockconfigsku.setErrmsg("");
					stockconfigsku.setStockcount(qty);
					dao.updateByKeys(stockconfigsku, "orgid,skuid");
					if(isLast){     //���һ��sku,�ѻ��ŵĿ��Ҳһ�����
						//ͳ������������е�sku�Ŀ��
						String sql=new StringBuilder().append("select sum(stockcount) from ecs_stockconfigsku where orgid='").append(orgId)
						.append("' and itemid='").append(stockconfig.getItemid()).append("'").toString();
						int totalCount = SQLHelper.intSelect(dao.getConnection(), sql);
						//���»��ſ��
						stockconfig.setErrflag(0);
						stockconfig.setErrmsg("");
						stockconfig.setStockcount(totalCount);
						dao.updateByKeys(stockconfig, "orgid,itemid");
						
					}
				}
				
			}else{
				Log.error(jobName,"����ecshop��Ʒ��淢���쳣�����š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��") ;
			}
		}catch(Exception e){
			Log.error(jobName,"����ecshop��Ʒ���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��,������Ϣ��"+e.getMessage()) ;
			stockconfigsku.setErrflag(1);
			stockconfigsku.setErrmsg(e.getMessage().replaceAll("\"", ""));
			try {
				dao.updateByKeys(stockconfigsku, "orgid,skuid");
			} catch (Exception e1) {
				
				Log.error(jobName, "��������´�����Ϣ����");
			}
			
		}
	}
	

}
