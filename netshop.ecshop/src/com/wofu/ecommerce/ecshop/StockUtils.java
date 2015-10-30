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
	 * 更新ecshop库存-没有子商品的
	 */
	public static void updateItemStock(String jobName,DataCentre dao ,int orgId,String url,
			ECS_StockConfig stockconfig,int qty) throws JException
	{
		try
		{
			//方法名
			String apimethod="update_goods_number";
			HashMap<String,Object> reqMap = new HashMap<String,Object>();
	        reqMap.put("return_data", "json");
	        reqMap.put("act", apimethod);
	        reqMap.put("api_version", "1.0");
	        reqMap.put("goods_sn",stockconfig.getItemcode());
	        reqMap.put("store",qty);
	        //发送请求
			String responseText = CommHelper.doRequest(reqMap,url);
			Log.info("responseText:　"+responseText);
			//把返回的数据转成json对象
			JSONObject responseObj= new JSONObject(responseText);
			//错误对象 
			if(!"success".equals(responseObj.getString("result"))){ 
				 //发生错误
			  String errorMessage = responseObj.getString("msg");
			  if(!"".equals(errorMessage))
				{
					Log.error(jobName,"更新ecshop库存失败，货号【"+ stockconfig.getItemcode() +"】,错误信息："+ errorMessage);
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg(errorMessage.replaceAll("\"", ""));
					stockconfig.setStockcount(qty);
					dao.updateByKeys(stockconfig, "orgid,itemid");
					return ;
				}
			  
			}else{
				Log.info("更新ecshop库存成功,货号【"+ stockconfig.getItemcode() +"】,库存数:"+qty);
				stockconfig.setErrflag(0);
				stockconfig.setErrmsg("");
				stockconfig.setStockcount(qty);
				dao.updateByKeys(stockconfig, "orgid,itemid");
			}
			
		} catch (Exception e) 
		{
			Log.error("更新ecshop库存","更新ecshop商品库存失败，货号【"+ stockconfig.getItemcode() +"】。错误信息："+e.getMessage()) ;
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
	public static void updateSkuStock(String jobName,DataCentre dao,int orgId,String url,ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty,Boolean isLast){
		try{
			//方法名
			String apimethod="update_products_number";
			HashMap<String,Object> reqMap = new HashMap<String,Object>();
	        reqMap.put("return_data", "json");
	        reqMap.put("act", apimethod);
	        reqMap.put("api_version", "1.0");
	        reqMap.put("product_id",stockconfigsku.getSku());
	        reqMap.put("store",qty);
	        //发送请求
	        
			String responseText = CommHelper.doRequest(reqMap,Params.url);
			Log.info("responseText: "+responseText);
			if(responseText.indexOf("sn_responseContent")!=-1){
				//把返回的数据转成json对象
				JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
				//错误对象 
				if(!"success".equals(responseObj.getString("result"))){ 
					 //发生错误
				  String errorMessage = responseObj.getString("msg");
				  if(!"".equals(errorMessage))
					{
						Log.error(jobName,"更新ecshop商品库存失败，货号【"+ stockconfig.getItemcode() +"】,sku【"+stockconfigsku.getSku()+"】,错误信息："+ errorMessage );
						return ;
					}
					
				} else{
					Log.info("更新ecshopsku库存成功,货号【"+ stockconfig.getItemcode() +"】,sku【"+stockconfigsku.getSku()+"】,库存数:"+qty);
					stockconfigsku.setErrflag(0);
					stockconfigsku.setErrmsg("");
					stockconfigsku.setStockcount(qty);
					dao.updateByKeys(stockconfigsku, "orgid,skuid");
					if(isLast){     //最后一个sku,把货号的库存也一起更新
						//统计这个货号所有的sku的库存
						String sql=new StringBuilder().append("select sum(stockcount) from ecs_stockconfigsku where orgid='").append(orgId)
						.append("' and itemid='").append(stockconfig.getItemid()).append("'").toString();
						int totalCount = SQLHelper.intSelect(dao.getConnection(), sql);
						//更新货号库存
						stockconfig.setErrflag(0);
						stockconfig.setErrmsg("");
						stockconfig.setStockcount(totalCount);
						dao.updateByKeys(stockconfig, "orgid,itemid");
						
					}
				}
				
			}else{
				Log.error(jobName,"更新ecshop商品库存发生异常，货号【"+ stockconfig.getItemcode() +"】,sku【"+stockconfigsku.getSku()+"】") ;
			}
		}catch(Exception e){
			Log.error(jobName,"更新ecshop商品库存失败，货号【"+ stockconfig.getItemcode() +"】,sku【"+stockconfigsku.getSku()+"】,错误信息："+e.getMessage()) ;
			stockconfigsku.setErrflag(1);
			stockconfigsku.setErrmsg(e.getMessage().replaceAll("\"", ""));
			try {
				dao.updateByKeys(stockconfigsku, "orgid,skuid");
			} catch (Exception e1) {
				
				Log.error(jobName, "定入库存更新错误信息出错");
			}
			
		}
	}
	

}
