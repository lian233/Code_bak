package com.wofu.ecommerce.ylw;
import java.util.HashMap;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.ecommerce.ylw.util.CommHelper;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class StockUtils {
	/**
	 * 更新苏宁库存-没有子商品的
	 */
	public static void updateItemStock(String jobName,DataCentre dao ,int orgId,String url,String format,
			ECS_StockConfig stockconfig,String app_key,String app_Secret,int qty) throws JException
	{
		try
		{
			//方法名
			String apimethod="suning.custom.inventory.modify";
			HashMap<String,String> reqMap = new HashMap<String,String>();
			reqMap.put("productCode", stockconfig.getItemid());
	        reqMap.put("destInvNum", String.valueOf(qty));
	        HashMap<String,String> map = new HashMap<String,String>();
	        map.put("appSecret", app_Secret);
	        map.put("appMethod", apimethod);
	        map.put("format", format);
	        map.put("versionNo", "v1.2");
	        map.put("appKey", app_key);
	        //发送请求
			String responseText = CommHelper.doRequest(map,url);
			
			//把返回的数据转成json对象
			JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
			//错误对象 
			if(responseText.indexOf("sn_error")!=-1){ 
				 //发生错误
			  String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
			  if(!"".equals(operCode))
				{
					Log.error(jobName,"更新苏宁库存失败，货号【"+ stockconfig.getItemcode() +"】,错误信息："+ operCode +"：" +operCode);
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
				Log.info("更新苏宁库存成功,货号【"+ stockconfig.getItemcode() +"】,库存数:"+qty);
				stockconfig.setErrflag(0);
				stockconfig.setErrmsg("");
				stockconfig.setStockcount(qty);
				dao.updateByKeys(stockconfig, "orgid,itemid");
				
			}else
				Log.info("更新苏宁库存失败,货号【"+ stockconfig.getItemcode() +"】");
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg("");
				stockconfig.setStockcount(qty);
				dao.updateByKeys(stockconfig, "orgid,itemid");
			
			
		} catch (Exception e) 
		{
			Log.error("更新苏宁库存","更新苏宁商品库存失败，货号【"+ stockconfig.getItemcode() +"】。错误信息："+e.getMessage()) ;
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
	public static void updateSkuStock(String jobName,DataCentre dao,int orgId,String url,String format,
			String app_key,String app_Secret,ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty,Boolean isLast){
		try{
			//方法名
			String apimethod="suning.custom.inventory.modify";
			HashMap<String,String> reqMap = new HashMap<String,String>();
			reqMap.put("productCode", stockconfigsku.getSkuid());
	        reqMap.put("destInvNum", String.valueOf(qty));
	        HashMap<String,String> map = new HashMap<String,String>();
	        map.put("appSecret", app_Secret);
	        map.put("appMethod", apimethod);
	        map.put("format", format);
	        map.put("versionNo", "v1.2");
	        map.put("appKey", app_key);
	        //发送请求
			String responseText = CommHelper.doRequest(map,url);
			if(responseText.indexOf("sn_responseContent")!=-1){
				//把返回的数据转成json对象
				JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
				//错误对象 
				if(responseText.indexOf("sn_error")!=-1){ 
					 //发生错误
				  String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
				  if(!"".equals(operCode))
					{
						Log.error(jobName,"更新苏宁商品库存失败，货号【"+ stockconfig.getItemcode() +"】,sku【"+stockconfigsku.getSku()+"】,错误信息："+ operCode +"：" +operCode);
						return ;
					}
					
				} 
				String resultInfo = responseObj.getJSONObject("sn_body").getJSONObject("inventory").getString("result");
				
				if("Y".equals(resultInfo))
				{
					Log.info("更新苏宁sku库存成功,货号【"+ stockconfig.getItemcode() +"】,sku【"+stockconfigsku.getSku()+"】,库存数:"+qty);
					
					String sql=new StringBuilder().append("update ecs_stockconfigsku set errflag=0,errmsg='',stockcount=qty where orgid='").append(orgId)
						.append("'  and itemid ='").append(stockconfig.getItemid()).append("' and sku='").append(stockconfigsku.getSku()).append("'").toString();
					stockconfigsku.setErrflag(0);
					stockconfigsku.setErrmsg("");
					stockconfigsku.setStockcount(qty);
					dao.updateByKeys(stockconfigsku, "orgid,skuid");
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
					
				}else{
					Log.error(jobName,"更新苏宁商品库存失败，货号【"+ stockconfig.getItemcode() +"】,sku【"+stockconfigsku.getSku()+"】") ;
					stockconfigsku.setErrflag(1);
					stockconfigsku.setErrmsg("发送更新库存请求出错");
					dao.updateByKeys(stockconfigsku, "orgid,skuid");
				}
				
			}else{
				Log.error(jobName,"更新苏宁商品库存发生异常，货号【"+ stockconfig.getItemcode() +"】,sku【"+stockconfigsku.getSku()+"】") ;
			}
			
			
		}catch(Exception e){
			Log.error(jobName,"更新苏宁商品库存失败，货号【"+ stockconfig.getItemcode() +"】,sku【"+stockconfigsku.getSku()+"】,错误信息："+e.getMessage()) ;
			stockconfigsku.setErrflag(1);
			stockconfigsku.setErrmsg(e.getMessage().replaceAll("\"", ""));
			try {
				dao.updateByKeys(stockconfigsku, "orgid,skuid");
			} catch (Exception e1) {
				
				Log.error(jobName, "定入库存更新错误信息出错");
			}
			
		}
	}
	

	/*
	 * 根据苏宁商品编码获取商品库存
	 */
	public static String getInventoryByproductCode(String produceCode,String app_key,String app_Secret,String format,String url){
		try{
			//方法名
			String apimethod="suning.custom.inventory.get";
			HashMap<String,String> reqMap = new HashMap<String,String>();
			reqMap.put("productCode", produceCode);
	        HashMap<String,String> map = new HashMap<String,String>();
	        map.put("appSecret", app_Secret);
	        map.put("appMethod", apimethod);
	        map.put("format", format);
	        map.put("versionNo", "v1.2");
	        map.put("appKey", app_key);
	        //发送请求
			String responseText = CommHelper.doRequest(map,url);
			Log.info("取商品库存返回数据为："+responseText);
			if(responseText.indexOf("sn_responseContent")!=-1){
				//把返回的数据转成json对象
				JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
				//错误对象 
				if(responseText.indexOf("sn_error")!=-1){
					String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
					  if(!"".equals(operCode))
						{
							Log.error("苏宁获取商品库存作业", "获取商品库存作业失败,operCode:"+operCode);
						}
						return "0";
				} 
					 
				return responseObj.getJSONObject("sn_body").getJSONObject("inventory").getString("invNum");
			}else{
				Log.error("苏宁获取商品库存作业", "获取商品库存作业失败,没有数据返回");
				return "0";
			}
			
		}catch(Exception ex){
			Log.error("苏宁获取商品库存作业", "获取商品库存作业失败,operCode:"+ex.getMessage());
			return "0";
		}
		
	}
	


}
