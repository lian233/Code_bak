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
	 * 如果是一个货号的最后一个sku，则把货号在stockconfig表的库存记录也更新一次
	 * 更新sku库存
	 */
	public static void updateSkuStock(String token,String jobName,DataCentre dao,int orgId,String url,String modify_type,
			String vcode,ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty,Boolean isLast
			,String appKey,String appsecret)
	{
		String apimethod="meilishuo.item.quantity.update";
		JSONObject object;
		try
		{
			//方法名
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
			Log.info("更新库存返回数据: "+responseText);
			JSONObject responseObj= new JSONObject(responseText);
			//如果错误的话：
			try
			{
				String errormessage = responseObj.getJSONObject("error_response").getString("message");	 //如果没错整个try都不会执行成功，有错就会执行出错过程
				Log.error(jobName,"更新美丽说库存失败，货号【"+ stockconfig.getItemcode() +"】,错误信息："+ errormessage );
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(errormessage.replaceAll("\"", ""));
				stockconfig.setStockcount(qty);
				dao.updateByKeys(stockconfig, "orgid,itemid");
				return ;
			}catch (Exception e) 
			{
				//获取错误信息的json失败则代表没有错误信息，则代表执行成功，执行如下语句
				Log.info("更新美丽说sku库存成功,货号【"+ stockconfig.getItemcode() +"】,sku【"+stockconfigsku.getSku()+"】,库存数:"+qty);
				
				String sql=new StringBuilder().append("update ecs_stockconfigsku set errflag=0,errmsg='',stockcount=qty where orgid='").append(orgId)
					.append("'  and itemid ='").append(stockconfig.getItemid()).append("' and sku='").append(stockconfigsku.getSku()).append("'").toString();
				stockconfigsku.setErrflag(0);
				stockconfigsku.setErrmsg("");
				stockconfigsku.setStockcount(qty);
				try
				{
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
				} catch (Exception e1)
				{
					// TODO Auto-generated catch block
					//e1.printStackTrace();
					Log.error(jobName, "数据库操作失常");
				}


			}
		} catch (Exception e)
		{
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
	
	


}
