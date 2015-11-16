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


	//根据商品编号查询商品信息
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
			Log.error(jobname, "获取京东商品资料失败,商品编号:"+ wareID +"错误信息:"+ res.getCode() + "," + res.getZhDesc()) ;
			return null ;
		}
		
	}

	//根据sku获取京东商品信息 V2
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
			Log.info("找不到京东商品资料,sku【"+ sku +"】") ;
			return null ;
		}
	}

	//更新京东商品库存 V2
	public static void updateStock(DataCentre dao,String SERVER_URL,String token,
			String appKey,String appSecret,ECS_StockConfig stockconfig,
			ECS_StockConfigSku stockconfigsku,int qty) throws Exception
	{
		try
		{
			if(stockconfigsku.getSku().equals("")){
				System.out.println("sku为空,跳过同步");
				return;
			}
			DefaultJdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
			WareSkuStockUpdateRequest request = new WareSkuStockUpdateRequest();
			request.setOuterId(stockconfigsku.getSku());
			request.setSkuId(stockconfigsku.getSkuid()) ;
			request.setQuantity(String.valueOf(qty));
			request.setTradeNo(OrderUtils.getTradeNo()) ;
			com.jd.open.api.sdk.response.ware.WareSkuStockUpdateResponse res = client.execute(request);
			Log.info("--京东更新库存返回信息: "+res.getCode());
			if(res.getCode().equals("0"))
			{
				Log.info("更新京东库存成功,SKU【"+ stockconfigsku.getSku() +"】,库存:"+ qty);
				
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
				Log.info("更新京东库存失败!SKU【"+ stockconfigsku.getSku() +"】,错误信息:"+res.getZhDesc());
				stockconfigsku.setErrmsg(res.getZhDesc());
				
				dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
				

				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(res.getZhDesc());
				dao.updateByKeys(stockconfig,"orgid,itemid");
			}
		} catch (Exception e) {
			String errorMessage=e.getMessage();
			Log.info("更新京东库存失败,SKU【"+stockconfigsku.getSku()+"】,错误信息:"+e.getMessage());
			stockconfigsku.setErrmsg(errorMessage);
			dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");	
			stockconfig.setErrflag(1);
			stockconfig.setErrmsg(errorMessage);
			dao.updateByKeys(stockconfig,"orgid,itemid");
			
		}
		
				
	}
	//获取单个SKU信息 V2
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
			Log.info("获取京东商品sku信息出错,skuid:"+skuID+",错误信息:"+res.getCode() +","+ res.getZhDesc()) ;
			return null ;
		}
	}
	//修改快递信息 V2
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
				Log.info("快递转件成功,订单号【"+ orderId +"】,快递公司【"+ logisticsId +"】,快递单号【" + waybill +"】") ;
			}
			else
			{
				flag = false ;
				Log.error(jobname, "快递转件失败,订单号【"+ orderId +"】,快递公司【"+ logisticsId +"】,快递单号【" + waybill + "】,错误信息:"+response.getCode()+","+response.getZhDesc()) ;
			}
			 
		} catch (Exception e) {
			flag = false ;
			Log.error(jobname, "快递转件失败,订单号【"+ orderId +"】,快递公司【"+ logisticsId +"】,快递单号【" + waybill + "】,错误信息:"+e.getMessage()) ;
		}
		return flag ;
	}
	//SOP发货
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
				Log.info("name:　"+name+", value: "+value);
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
			//状态码
			String code = response.getCode() ;
			//Log.info("result: "+response.getMsg());
			if("0".equals(code))
			{
				flag = true ;
				Log.info("更新发货信息成功，京东单号【" + orderId + "】，快递公司【" + logisticsId + "】，快递单号【" + waybill + "】") ;
			}
			else
			{
				if("35".equals(code) || "61".equals(code) || "10300004".equals(code)|| response.getZhDesc().indexOf("订单已出库")>=0)
					flag = true ;
				Log.info("更新发货信息失败，京东单号【" + orderId + "】，快递公司【" + logisticsId + "】，快递单号【" + waybill + "】。错误信息111：" + code + "," + response.getZhDesc()) ;
			}
		} catch (Throwable e) {
			flag = false ;
			Log.info("更新发货信息失败，京东单号【" + orderId + "】，快递公司【" + logisticsId + "】，快递单号【" + waybill + "】。错误信息：" + e.getMessage()) ;
		}
		return flag ;
	}
	//LBP发货
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
			//状态码
			String code = response.getCode() ;
			
			if("0".equals(code))
			{
				flag = true ;
				Log.info("更新发货信息成功，京东单号【" + orderId + "】，快递公司【" + logisticsId + "】，快递单号【" + waybill + "】") ;
			}
			else
			{
				if("35".equals(code) || "61".equals(code) || response.getZhDesc().indexOf("订单已出库")>=0)
					flag = true ;
				Log.info("更新发货信息失败，京东单号【" + orderId + "】，快递公司【" + logisticsId + "】，快递单号【" + waybill + "】。错误信息：" + code + "," + response.getZhDesc()) ;
			}
		} catch (Exception e) {
			flag = false ;
			Log.info("更新发货信息失败，京东单号【" + orderId + "】，快递公司【" + logisticsId + "】，快递单号【" + waybill + "】。错误信息：" + e.getMessage()) ;
		}
		return flag ;
	}
	
	
	
	/**
	 * 查询订单的收货人信息
	 * @param tid  订单号
	 * @param conn
	 * @return
	 */
	public static Hashtable<String,String> getReceiverInfo(String tid,Connection conn){
		try{
			String sql = "select receivername,receiveraddress ,receivermobile ,payment from ns_customerorder where tid='"+tid+"'";
			return SQLHelper.oneRowSelect(conn, sql);
		}catch(Exception ex){
			Log.error("取收货人信息出错！", ex.getMessage());
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
