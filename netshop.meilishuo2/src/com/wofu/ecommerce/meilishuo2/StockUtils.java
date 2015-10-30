package com.wofu.ecommerce.meilishuo2;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import com.wofu.ecommerce.meilishuo2.util.Utils;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONException;
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
		//������
		String apimethod="meilishuo.item.quantity.update";
		Log.info(jobName + "  :����ִ�еķ���:  "+apimethod);
		try
		{
			JSONObject object=new JSONObject(PublicUtils.getConfig(dao.getConnection(), "����˵Token��Ϣ2", "")); //��ȡ���µ�Token
			String responseText = Utils.sendbyget(Params.url, Params.appKey, Params.appsecret, apimethod, object.optString("access_token"), new Date(), stockconfig.getItemid(), stockconfig.getItemcode(), modify_type.equals("")?"set":modify_type, String.valueOf(qty));
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
				Log.info("��������˵���ɹ�,���š�"+ stockconfig.getItemcode() +"��,�����:"+qty);
				stockconfig.setErrflag(0);
				stockconfig.setErrmsg("");
				stockconfig.setStockcount(qty);
				try
				{
					dao.updateByKeys(stockconfig, "orgid,itemid");
				} catch (Exception e1)
				{
					Log.error("����˵����", "���ݿ����ʧ��");
				}
			}
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
	 * �����һ�����ŵ����һ��sku����ѻ�����stockconfig��Ŀ���¼Ҳ����һ��
	 * ����sku���
	 */
	public static void updateSkuStock(String jobName,DataCentre dao,int orgId,String url,String modify_type,
			String vcode,ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty,Boolean isLast)
	{
		String apimethod="meilishuo.item.quantity.update";
		Log.info(jobName + "  :����ִ�еķ���:  "+apimethod);
		JSONObject object;
		try
		{
			object = new JSONObject(PublicUtils.getConfig(dao.getConnection(), "����˵Token��Ϣ2", ""));//��ȡ���µ�Token
			String responseText = Utils.sendbyget(Params.url, Params.appKey, Params.appsecret, apimethod, object.optString("access_token"), new Date(), stockconfig.getItemid(), stockconfigsku.getSkuid(), modify_type.equals("")?"set":modify_type, String.valueOf(qty));
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
	

	/*
	 * ��������˵��Ʒ�����ȡ��Ʒ���
	 * 20150512��ʼ����
	 */
	public static String getInventoryByproductCode(DataCentre dao, String produceCode,String app_key,String app_Secret,String format,String url)
	{
		String method="meilishuo.items.inventory.get";
		JSONObject object;
		try
		{
			object = new JSONObject(PublicUtils.getConfig(dao.getConnection(), "����˵Token��Ϣ2", ""));
			String responseText=Utils.sendbyget(Params.url,Params.appKey,Params.appsecret,method,object.optString("access_token"),new Date(),produceCode);
			Log.info("ȡ��Ʒ��淵������Ϊ��"+responseText);
			JSONObject responseObj= new JSONObject(responseText);
			//�������Ļ���
			try
			{
				String errormessage = responseObj.getJSONObject("error_response").getString("message");	 //���û������try������ִ�гɹ����д�ͻ�ִ�г������
				Log.error("����˵��ȡ��Ʒ�����ҵ", "��ȡ��Ʒ�����ҵʧ��"+errormessage);
				return "0";
			}catch(Exception e)
			{
				//����޷���ȡ������������û�д���ִ�У�
				return responseObj.toString();
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "0";
	}
	


}
