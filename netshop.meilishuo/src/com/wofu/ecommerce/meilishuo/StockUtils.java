package com.wofu.ecommerce.meilishuo;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import com.wofu.ecommerce.meilishuo.util.Utils;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class StockUtils {

	/**
	 * �����һ�����ŵ����һ��sku����ѻ�����stockconfig��Ŀ���¼Ҳ����һ��
	 * ����sku���
	 */
	public static void updateSkuStock(String token,String jobName,DataCentre dao,int orgId,String url,String modify_type,
			String vcode,ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty,Boolean isLast
			,String appKey,String appsecret)
	{
		String apimethod="meilishuo.item.quantity.update";
		JSONObject object;
		try
		{
			//������
			HashMap<String, String> param = new HashMap<String,String>();
			param.put("method", apimethod);
			param.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			param.put("format", "json");
			param.put("app_key", appKey);
			param.put("v", "1.0");
			param.put("sign_method", "md5");
			param.put("session", token);
			param.put("twitter_id", stockconfigsku.getItemid());
			param.put("sku_id", stockconfigsku.getSkuid());
			param.put("modify_type", modify_type);
			param.put("modify_value",String.valueOf(qty));
			
			String responseText = Utils.sendbyget(url,
					param,appsecret);
			Log.info("skuid: "+stockconfigsku.getSkuid());
			Log.info("���¿�淵������: "+responseText);
			JSONObject responseObj= new JSONObject(responseText);
			//�������Ļ���
			try
			{
				String errormessage = responseObj.getJSONObject("error_response").getString("message");	 //���û������try������ִ�гɹ����д�ͻ�ִ�г������
				Log.error(jobName,"��������˵���ʧ�ܣ����š�"+ stockconfig.getItemcode() +"��,������Ϣ��"+ errormessage );
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(errormessage.replaceAll("\"", ""));
				stockconfig.setStockcount(qty);
				dao.updateByKeys(stockconfig, "orgid,itemid");
				return ;
			}catch (Exception e) 
			{
				//��ȡ������Ϣ��jsonʧ�������û�д�����Ϣ�������ִ�гɹ���ִ���������
				Log.info("��������˵sku���ɹ�,���š�"+ stockconfig.getItemcode() +"��,sku��"+stockconfigsku.getSku()+"��,�����:"+qty);
				
				String sql=new StringBuilder().append("update ecs_stockconfigsku set errflag=0,errmsg='',stockcount=qty where orgid='").append(orgId)
					.append("'  and itemid ='").append(stockconfig.getItemid()).append("' and sku='").append(stockconfigsku.getSku()).append("'").toString();
				stockconfigsku.setErrflag(0);
				stockconfigsku.setErrmsg("");
				stockconfigsku.setStockcount(qty);
				try
				{
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
				} catch (Exception e1)
				{
					// TODO Auto-generated catch block
					//e1.printStackTrace();
					Log.error(jobName, "���ݿ����ʧ��");
				}


			}
		} catch (Exception e)
		{
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
	
	


}
