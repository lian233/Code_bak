package com.wofu.ecommerce.papago8;
import java.util.HashMap;

import com.wofu.ecommerce.papago8.util.CommHelper;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class StockUtils {
	/**
	 * ����papago8���-û������Ʒ��
	 */
	public static void updateItemStock(String jobName,DataCentre dao ,int orgId,String url,
			ECS_StockConfig stockconfig,String vcode,int qty) throws JException
	{
		try
		{
			//������
			String apimethod="UpdateStock.aspx?";
			HashMap<String,Object> reqMap = new HashMap<String,Object>();
			reqMap.put("key", vcode);
			reqMap.put("skuid", stockconfig.getItemcode());
			reqMap.put("apimethod", apimethod);
	        reqMap.put("num", qty+"");
	        //��������
			String responseText = CommHelper.doRequest(reqMap,url,"get");
			Log.info("���¿�淵������: "+responseText);
			//�ѷ��ص�����ת��json����
			JSONObject responseObj= new JSONObject(responseText);
			int code= responseObj.getInt("code");
			//������� 
			if(code!=0){   //{"code":100001,"message":"empty 2rd!","msg":null}
				 //��������
			  String errormessage = responseObj.getString("message");
			  if(!"".equals(errormessage))
				{
					Log.error(jobName,"����papago8���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"��,������Ϣ��"+ errormessage );
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg(errormessage.replaceAll("\"", ""));
					stockconfig.setStockcount(qty);
					dao.updateByKeys(stockconfig, "orgid,itemid");
					return ;
				}
			  
			}
		
			if(code==0)
			{
				Log.info("����papago8���ɹ�,���š�"+ stockconfig.getItemcode() +"��,�����:"+qty);
				stockconfig.setErrflag(0);
				stockconfig.setErrmsg("");
				stockconfig.setStockcount(qty);
				dao.updateByKeys(stockconfig, "orgid,itemid");
				
			}else
				Log.info("����papago8���ʧ��,���š�"+ stockconfig.getItemcode() +"��");
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg("");
				stockconfig.setStockcount(qty);
				dao.updateByKeys(stockconfig, "orgid,itemid");
			
			
		} catch (Exception e) 
		{
			Log.error("����papago8���","����papago8��Ʒ���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"����������Ϣ��"+e.getMessage()) ;
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
	public static void updateSkuStock(String jobName,DataCentre dao,int orgId,String url,
			String vcode,ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty,Boolean isLast){
		try{
			//������
			String apimethod="UpdateStock.aspx?";
			HashMap<String,Object> reqMap = new HashMap<String,Object>();
			reqMap.put("key", vcode);
	        reqMap.put("skuid", stockconfigsku.getSku());
	        reqMap.put("apimethod", apimethod);
	        reqMap.put("num", qty+"");
	        reqMap.put("format", "json");
	       
	        //��������
			String responseText = CommHelper.doRequest(reqMap,url,"get");
			JSONObject response= new JSONObject(responseText);
			Log.info("���¿�淵������: "+responseText);
			//������� 
			if(!response.isNull("error")){ 
				 //��������
			  String errormessage = response.getString("error");
					Log.error(jobName,"����papago8��Ʒ���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��,������Ϣ��"+ errormessage);
					stockconfigsku.setErrflag(1);
					stockconfigsku.setErrmsg(errormessage.replaceAll("\"", ""));
					try {
						dao.updateByKeys(stockconfigsku, "orgid,skuid,sku");
					} catch (Exception e1) {
						
						Log.error(jobName, "д������´�����Ϣ����");
					}
					return ;
				
			} 
			
			JSONObject responseObj= response.getJSONObject("response");
			String result= responseObj.getString("result");
			
				
				
				
				if("SUCCESS".equalsIgnoreCase(result))
				{
					Log.info("����papago8sku���ɹ�,���š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��,�����:"+qty);
					
					stockconfigsku.setErrflag(0);
					stockconfigsku.setErrmsg("");
					stockconfigsku.setStockcount(qty);
					dao.updateByKeys(stockconfigsku, "orgid,skuid,sku");
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
				
		}catch(Exception e){
			Log.error(jobName,"����papago8��Ʒ���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��,������Ϣ��"+e.getMessage()) ;
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
	 * ����papago8��Ʒ�����ȡ��Ʒ���
	 */
	


}
