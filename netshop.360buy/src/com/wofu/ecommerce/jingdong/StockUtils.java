package com.wofu.ecommerce.jingdong;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.ware.Sku;
import com.jd.open.api.sdk.domain.ware.Ware;
import com.jd.open.api.sdk.internal.util.CodecUtil;
import com.jd.open.api.sdk.internal.util.StringUtil;
import com.jd.open.api.sdk.request.delivery.EtmsWaybillSendRequest;
import com.jd.open.api.sdk.request.order.OrderLbpOutstorageRequest;
import com.jd.open.api.sdk.request.order.OrderPrintDataGetRequest;
import com.jd.open.api.sdk.request.order.OrderSopOutstorageRequest;
import com.jd.open.api.sdk.request.order.OrderSopWaybillUpdateRequest;
import com.jd.open.api.sdk.request.ware.SkuCustomGetRequest;
import com.jd.open.api.sdk.request.ware.WareGetRequest;
import com.jd.open.api.sdk.request.ware.WareSkuGetRequest;
import com.jd.open.api.sdk.request.ware.WareSkuStockUpdateRequest;
import com.jd.open.api.sdk.response.delivery.EtmsWaybillSendResponse;
import com.jd.open.api.sdk.response.order.OrderLbpOutstorageResponse;
import com.jd.open.api.sdk.response.order.OrderPrintDataGetResponse;
import com.jd.open.api.sdk.response.order.OrderSopOutstorageResponse;
import com.jd.open.api.sdk.response.order.OrderSopWaybillUpdateResponse;
import com.jd.open.api.sdk.response.ware.SkuCustomGetResponse;
import com.jd.open.api.sdk.response.ware.WareGetResponse;
import com.jd.open.api.sdk.response.ware.WareSkuGetResponse;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;

public class StockUtils
{


	//������Ʒ��Ų�ѯ��Ʒ��Ϣ
	public static Ware getGoodsByWareID(String jobname,long wareID,String SERVER_URL,String token,String appKey,String appSecret) throws com.jd.open.api.sdk.JdException
	{
		String queryFields = "ware_id,skus,ware_status,title,item_num,stock_num,creator,status,created,modified,outer_id" ;
		com.jd.open.api.sdk.DefaultJdClient client = new com.jd.open.api.sdk.DefaultJdClient(SERVER_URL,token,appKey,appSecret);
		WareGetRequest  wareGetRequest = new WareGetRequest();
		wareGetRequest.setWareId(String.valueOf(wareID));
		wareGetRequest.setFields(queryFields) ;
		WareGetResponse res= client.execute(wareGetRequest);
		if("0".equals(res.getCode()))
			return res.getWare() ;
		else
		{
			Log.error(jobname, "��ȡ������Ʒ����ʧ��,��Ʒ���:"+ wareID +"������Ϣ:"+ res.getCode() + "," + res.getZhDesc()) ;
			return null ;
		}
		
	}

	//����sku��ȡ������Ʒ��Ϣ V2
	public static Sku getGoodsByCustom(String SERVER_URL,String token,String appKey,String appSecret,String sku) throws Exception
	{
		String qureyFields = "sku_id,ware_id,status,stock_num,outer_id,modified" ;
		DefaultJdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
		SkuCustomGetRequest skuCustomGetRequest = new SkuCustomGetRequest();
		skuCustomGetRequest.setOuterId(sku);
		skuCustomGetRequest.setFields(qureyFields);
		SkuCustomGetResponse res = client.execute(skuCustomGetRequest);

		if(res.getCode().equals("0"))
			return res.getSku() ;	
		else
		{
			Log.info("�Ҳ���������Ʒ����,sku��"+ sku +"��") ;
			return null ;
		}
	}

	//���¾�����Ʒ��� V2
	public static void updateStock(DataCentre dao,String SERVER_URL,String token,
			String appKey,String appSecret,ECS_StockConfig stockconfig,
			ECS_StockConfigSku stockconfigsku,int qty) throws Exception
	{
		try
		{
			
			DefaultJdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
			WareSkuStockUpdateRequest request = new WareSkuStockUpdateRequest();
			request.setOuterId(stockconfigsku.getSku());
			request.setSkuId(stockconfigsku.getSkuid()) ;
			request.setQuantity(String.valueOf(qty));
			request.setTradeNo(OrderUtils.getTradeNo()) ;
			com.jd.open.api.sdk.response.ware.WareSkuStockUpdateResponse res = client.execute(request);
			Log.info("--�������¿�淵����Ϣ: "+res.getCode());
			if(res.getCode().equals("0"))
			{
				Log.info("���¾������ɹ�,SKU��"+ stockconfigsku.getSku() +"��,���:"+ qty);
				
				stockconfigsku.setStockcount(qty);
				stockconfigsku.setErrflag(0);
				stockconfigsku.setErrmsg("");
				dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
				
				stockconfig.setStockcount(stockconfig.getStockcount()-stockconfigsku.getStockcount()+qty);
				stockconfig.setErrflag(0);
				stockconfig.setErrmsg("");
				dao.updateByKeys(stockconfig,"orgid,itemid");
			}
			else
			{
				Log.info("���¾������ʧ��!SKU��"+ stockconfigsku.getSku() +"��,������Ϣ:"+res.getZhDesc());
				stockconfigsku.setErrmsg(res.getZhDesc());
				
				dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
				

				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(res.getZhDesc());
				dao.updateByKeys(stockconfig,"orgid,itemid");
			}
		} catch (Exception e) {
			String errorMessage=e.getMessage();
			Log.info("���¾������ʧ��,SKU��"+stockconfigsku.getSku()+"��,������Ϣ:"+e.getMessage());
			stockconfigsku.setErrmsg(errorMessage);
			dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");	
			stockconfig.setErrflag(1);
			stockconfig.setErrmsg(errorMessage);
			dao.updateByKeys(stockconfig,"orgid,itemid");
			
		}
		
				
	}
	//��ȡ����SKU��Ϣ V2
	public static Sku getSkuInfoBySkuId(String jobname,String skuID,String SERVER_URL,String token,String appKey,String appSecret) throws com.jd.open.api.sdk.JdException
	{
		String queryFields = "sku_id,shop_id,ware_id,status,attributes,stock_num,jd_price,cost_price,market_price,outer_id,created,modified" ;
		com.jd.open.api.sdk.DefaultJdClient client = new com.jd.open.api.sdk.DefaultJdClient(SERVER_URL,token,appKey,appSecret);
		WareSkuGetRequest wareSkuGetRequest = new WareSkuGetRequest();
		wareSkuGetRequest.setSkuId(skuID);
		wareSkuGetRequest.setFields(queryFields);
		WareSkuGetResponse res = client.execute(wareSkuGetRequest);
		 
		if("0".equals(res.getCode()))
			return res.getSku() ;
		else
		{
			Log.info("��ȡ������Ʒsku��Ϣ����,skuid:"+skuID+",������Ϣ:"+res.getCode() +","+ res.getZhDesc()) ;
			return null ;
		}
	}
	//�޸Ŀ����Ϣ V2
	public static boolean SOPModifyExpressInfo(String jobname,String orderId,String logisticsId,String waybill,String SERVER_URL,String token,String appKey,String appSecret)
	{
		boolean flag = false ;
		try 
		{
			JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
			OrderSopWaybillUpdateRequest request = new OrderSopWaybillUpdateRequest();
			request.setOrderId(orderId);
			request.setLogisticsId(logisticsId);
			request.setWaybill(waybill);
			request.setTradeNo(OrderUtils.getTradeNo()) ;
			OrderSopWaybillUpdateResponse response=client.execute(request);
			if("0".equals(response.getCode()))
			{
				flag = true ;
				Log.info("���ת���ɹ�,�����š�"+ orderId +"��,��ݹ�˾��"+ logisticsId +"��,��ݵ��š�" + waybill +"��") ;
			}
			else
			{
				flag = false ;
				Log.error(jobname, "���ת��ʧ��,�����š�"+ orderId +"��,��ݹ�˾��"+ logisticsId +"��,��ݵ��š�" + waybill + "��,������Ϣ:"+response.getCode()+","+response.getZhDesc()) ;
			}
			 
		} catch (Exception e) {
			flag = false ;
			Log.error(jobname, "���ת��ʧ��,�����š�"+ orderId +"��,��ݹ�˾��"+ logisticsId +"��,��ݵ��š�" + waybill + "��,������Ϣ:"+e.getMessage()) ;
		}
		return flag ;
	}
	//SOP����
	public static boolean SOPOrderDelivery(String jobname,String orderId,String logisticsId,String waybill,String SERVER_URL,String token,String appKey,String appSecret)
	{
		boolean flag = false ;
		try 
		{
			JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
			OrderSopOutstorageRequest request = new OrderSopOutstorageRequest();
			request.setOrderId(orderId);
			request.setLogisticsId(logisticsId) ;
			request.setWaybill(waybill) ;
			request.setTradeNo(OrderUtils.getTradeNo());
			/**
			Log.info("request: "+request.getAppJsonParams());
			Map<String, String> map = request.getSysParams();
			for(Iterator it = map.keySet().iterator();it.hasNext();){
				String name= (String)it.next();
				String value= (String)map.get(name);
				Log.info("name:��"+name+", value: "+value);
			}
			Map sysParams = request.getSysParams();
			Map pmap = new TreeMap();
	        pmap.put("360buy_param_json", request.getAppJsonParams());
	        sysParams.put("method", request.getApiMethod());
	        sysParams.put("access_token", token);
	        sysParams.put("app_key", appKey);
	        pmap.putAll(sysParams);
	        String sign = sign(pmap, appSecret);
	        Log.info("sign: "+sign);
	        **/
			OrderSopOutstorageResponse response = client.execute(request);
			//״̬��
			String code = response.getCode() ;
			//Log.info("result: "+response.getMsg());
			if("0".equals(code))
			{
				flag = true ;
				Log.info("���·�����Ϣ�ɹ����������š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "��") ;
			}
			else
			{
				if("35".equals(code) || "61".equals(code) || "10300004".equals(code)|| response.getZhDesc().indexOf("�����ѳ���")>=0)
					flag = true ;
				Log.info("���·�����Ϣʧ�ܣ��������š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "����������Ϣ111��" + code + "," + response.getZhDesc()) ;
			}
		} catch (Throwable e) {
			flag = false ;
			Log.info("���·�����Ϣʧ�ܣ��������š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "����������Ϣ��" + e.getMessage()) ;
		}
		return flag ;
	}
	//LBP����
	public static boolean LBPOrderDelivery(String jobname,String orderId,String logisticsId,String waybill,String SERVER_URL,String token,String appKey,String appSecret)
	{
		boolean flag = false ;
		try 
		{
			JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
			OrderLbpOutstorageRequest request = new OrderLbpOutstorageRequest();
			request.setOrderId(orderId);
			request.setLogisticsId(logisticsId) ;
			request.setPackageNum("1");
			if (!waybill.equals(""))
				request.setWaybill(waybill);
			else
				request.setWaybill("100000000000") ;
			request.setTradeNo(OrderUtils.getTradeNo());
			OrderLbpOutstorageResponse response = client.execute(request);
			
			//System.out.println(response.getZhDesc());
			//״̬��
			String code = response.getCode() ;
			
			if("0".equals(code))
			{
				flag = true ;
				Log.info("���·�����Ϣ�ɹ����������š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "��") ;
			}
			else
			{
				if("35".equals(code) || "61".equals(code) || response.getZhDesc().indexOf("�����ѳ���")>=0)
					flag = true ;
				Log.info("���·�����Ϣʧ�ܣ��������š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "����������Ϣ��" + code + "," + response.getZhDesc()) ;
			}
		} catch (Exception e) {
			flag = false ;
			Log.info("���·�����Ϣʧ�ܣ��������š�" + orderId + "������ݹ�˾��" + logisticsId + "������ݵ��š�" + waybill + "����������Ϣ��" + e.getMessage()) ;
		}
		return flag ;
	}
	
	
	
	/**
	 * ��ѯ�������ջ�����Ϣ
	 * @param tid  ������
	 * @param conn
	 * @return
	 */
	public static Hashtable<String,String> getReceiverInfo(String tid,Connection conn){
		try{
			String sql = "select receivername,receiveraddress ,receivermobile ,payment from ns_customerorder where tid='"+tid+"'";
			return SQLHelper.oneRowSelect(conn, sql);
		}catch(Exception ex){
			Log.error("ȡ�ջ�����Ϣ����", ex.getMessage());
			return null;
		}
		
		
		
		
	}
	
	
	
	 public static String sign(Map pmap, String appSecret)
     throws Exception
 {
     StringBuilder sb = new StringBuilder(appSecret);
     Iterator i$ = pmap.entrySet().iterator();
     do
     {
         if(!i$.hasNext())
             break;
         java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
         String name = (String)entry.getKey();
         String value = (String)entry.getValue();
         if(StringUtil.areNotEmpty(new String[] {
 name, value
}))
             sb.append(name).append(value);
     } while(true);
     sb.append(appSecret);
     String result = CodecUtil.md5(sb.toString());
     return result;
 }
	
	
	
}
