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
	 * 更新美丽说库存-没有子商品的
	 */
	public static void updateItemStock(String jobName,DataCentre dao ,int orgId,String url,String modify_type,
			ECS_StockConfig stockconfig,String vcode,int qty) throws JException
	{
		try
		{
			//方法名
			String apimethod="/goods/goods_stocks_edit?";
			HashMap<String,Object> reqMap = new HashMap<String,Object>();
			reqMap.put("twitter_id", stockconfig.getItemid());
			reqMap.put("modify_type", modify_type);
			reqMap.put("modify_value", String.valueOf(qty));
			reqMap.put("vcode", vcode);
			reqMap.put("skuid", stockconfig.getItemcode());
	        reqMap.put("apimethod", apimethod);
	        //发送请求
			String responseText = CommHelper.doRequest(reqMap,url,"get");
			//Log.info("更新库存返回数据: "+responseText);
			//把返回的数据转成json对象
			JSONObject responseObj= new JSONObject(responseText);
			int code= responseObj.getInt("code");
			//错误对象 
			if(code!=0){   //{"code":100001,"message":"empty 2rd!","msg":null}
				 //发生错误
			  String errormessage = responseObj.getString("message");
			  if(!"".equals(errormessage))
				{
					Log.error(jobName,"更新美丽说库存失败，货号【"+ stockconfig.getItemcode() +"】,错误信息："+ errormessage );
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg(errormessage.replaceAll("\"", ""));
					stockconfig.setStockcount(qty);
					dao.updateByKeys(stockconfig, "orgid,itemid");
					return ;
				}
			  
			}
		
			if(code==0)
			{
				Log.info("更新美丽说库存成功,货号【"+ stockconfig.getItemcode() +"】,库存数:"+qty);
				stockconfig.setErrflag(0);
				stockconfig.setErrmsg("");
				stockconfig.setStockcount(qty);
				dao.updateByKeys(stockconfig, "orgid,itemid");
				
			}else
				Log.info("更新美丽说库存失败,货号【"+ stockconfig.getItemcode() +"】");
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg("");
				stockconfig.setStockcount(qty);
				dao.updateByKeys(stockconfig, "orgid,itemid");
			
			
		} catch (Exception e) 
		{
			Log.error("更新美丽说库存","更新美丽说商品库存失败，货号【"+ stockconfig.getItemcode() +"】。错误信息："+e.getMessage()) ;
			stockconfig.setErrflag(1);
			stockconfig.setErrmsg(e.getMessage().replaceAll("\"", ""));
			stockconfig.setStockcount(qty);
			try {
				dao.updateByKeys(stockconfig, "orgid,itemid");
			} catch (Exception e1) {
				Log.error(jobName, "写更新库存错误信息错误");
			}
		}
}
	/**
	 * 如果是一个货号的最后一个sku，则把货号在stockconfig表的库存记录也更新一次
	 * 更新sku库存
	 */
	public static void updateSkuStock(String jobName,DataCentre dao,int orgId,String url,String modify_type,
			String vcode,ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty,Boolean isLast){
		try{
			//方法名
			String apimethod="/goods/goods_stocks_edit?";
			HashMap<String,Object> reqMap = new HashMap<String,Object>();
			reqMap.put("twitter_id", stockconfig.getItemid());
			reqMap.put("modify_type", modify_type);
			reqMap.put("modify_value", String.valueOf(qty));
			reqMap.put("vcode", vcode);
	        reqMap.put("skuid", stockconfigsku.getSkuid());
	        Log.info("skuid: "+stockconfigsku.getSkuid());
	        reqMap.put("apimethod", apimethod);
	       
	        //发送请求
			String responseText = CommHelper.doRequest(reqMap,url,"get");
			JSONObject responseObj= new JSONObject(responseText);
			int code= responseObj.getInt("code");
			Log.info("更新库存返回数据: "+responseText);
				
				//错误对象 
				if(code!=0){ 
					 //发生错误
				  String errormessage = responseObj.getString("message");
						Log.error(jobName,"更新美丽说商品库存失败，货号【"+ stockconfig.getItemcode() +"】,sku【"+stockconfigsku.getSku()+"】,错误信息："+ errormessage);
						return ;
					
				} 
				
				if(code==0)
				{
					Log.info("更新美丽说sku库存成功,货号【"+ stockconfig.getItemcode() +"】,sku【"+stockconfigsku.getSku()+"】,库存数:"+qty);
					
					String sql=new StringBuilder().append("update ecs_stockconfigsku set errflag=0,errmsg='',stockcount=qty where orgid='").append(orgId)
						.append("'  and itemid ='").append(stockconfig.getItemid()).append("' and sku='").append(stockconfigsku.getSku()).append("'").toString();
					stockconfigsku.setErrflag(0);
					stockconfigsku.setErrmsg("");
					stockconfigsku.setStockcount(qty);
					dao.updateByKeys(stockconfigsku, "orgid,skuid,sku");
					if(isLast){     //最后一个sku,把货号的库存也一起更新
						//统计这个货号所有的sku的库存
						sql=new StringBuilder().append("select sum(stockcount) from ecs_stockconfigsku where orgid='").append(orgId)
						.append("' and itemid='").append(stockconfig.getItemid()).append("'").toString();
						int totalCount = SQLHelper.intSelect(dao.getConnection(), sql);
						//更新货号库存
						stockconfig.setErrflag(0);
						stockconfig.setErrmsg("");
						stockconfig.setStockcount(totalCount);
						dao.updateByKeys(stockconfig, "orgid,itemid");
						
					}
					
				}
				
		}catch(Exception e){
			Log.error(jobName,"更新美丽说商品库存失败，货号【"+ stockconfig.getItemcode() +"】,sku【"+stockconfigsku.getSku()+"】,错误信息："+e.getMessage()) ;
			stockconfigsku.setErrflag(1);
			stockconfigsku.setErrmsg(e.getMessage().replaceAll("\"", ""));
			try {
				dao.updateByKeys(stockconfigsku, "orgid,skuid,sku");
			} catch (Exception e1) {
				
				Log.error(jobName, "定入库存更新错误信息出错");
			}
			
		}
	}
	

	/*
	 * 根据美丽说商品编码获取商品库存
	 */
	public static String getInventoryByproductCode(String produceCode,String app_key,String app_Secret,String format,String url){
		try{
			//方法名
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
	        //发送请求
			String responseText = CommHelper.doRequest(map,url,"get");
			Log.info("取商品库存返回数据为："+responseText);
			if(responseText.indexOf("sn_responseContent")!=-1){
				//把返回的数据转成json对象
				JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
				//错误对象 
				if(responseText.indexOf("sn_error")!=-1){
					String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
					  if(!"".equals(operCode))
						{
							Log.error("美丽说获取商品库存作业", "获取商品库存作业失败,operCode:"+operCode);
						}
						return "0";
				} 
					 
				return responseObj.getJSONObject("sn_body").getJSONObject("inventory").getString("invNum");
			}else{
				Log.error("美丽说获取商品库存作业", "获取商品库存作业失败,没有数据返回");
				return "0";
			}
			
		}catch(Exception ex){
			Log.error("美丽说获取商品库存作业", "获取商品库存作业失败,operCode:"+ex.getMessage());
			return "0";
		}
		
	}
	


}
