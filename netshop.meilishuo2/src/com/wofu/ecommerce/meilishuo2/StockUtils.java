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
	 * 更新美丽说库存-没有子商品的
	 */
	public static void updateItemStock(String jobName,DataCentre dao ,int orgId,String url,String modify_type,
			ECS_StockConfig stockconfig,String vcode,int qty) throws JException
	{
		//方法名
		String apimethod="meilishuo.item.quantity.update";
		Log.info(jobName + "  :正在执行的方法:  "+apimethod);
		try
		{
			JSONObject object=new JSONObject(PublicUtils.getConfig(dao.getConnection(), "美丽说Token信息2", "")); //获取最新的Token
			String responseText = Utils.sendbyget(Params.url, Params.appKey, Params.appsecret, apimethod, object.optString("access_token"), new Date(), stockconfig.getItemid(), stockconfig.getItemcode(), modify_type.equals("")?"set":modify_type, String.valueOf(qty));
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
				Log.info("更新美丽说库存成功,货号【"+ stockconfig.getItemcode() +"】,库存数:"+qty);
				stockconfig.setErrflag(0);
				stockconfig.setErrmsg("");
				stockconfig.setStockcount(qty);
				try
				{
					dao.updateByKeys(stockconfig, "orgid,itemid");
				} catch (Exception e1)
				{
					Log.error("美丽说出错", "数据库操作失常");
				}
			}
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
			String vcode,ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty,Boolean isLast)
	{
		String apimethod="meilishuo.item.quantity.update";
		Log.info(jobName + "  :正在执行的方法:  "+apimethod);
		JSONObject object;
		try
		{
			object = new JSONObject(PublicUtils.getConfig(dao.getConnection(), "美丽说Token信息2", ""));//获取最新的Token
			String responseText = Utils.sendbyget(Params.url, Params.appKey, Params.appsecret, apimethod, object.optString("access_token"), new Date(), stockconfig.getItemid(), stockconfigsku.getSkuid(), modify_type.equals("")?"set":modify_type, String.valueOf(qty));
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
	

	/*
	 * 根据美丽说商品编码获取商品库存
	 * 20150512开始更新
	 */
	public static String getInventoryByproductCode(DataCentre dao, String produceCode,String app_key,String app_Secret,String format,String url)
	{
		String method="meilishuo.items.inventory.get";
		JSONObject object;
		try
		{
			object = new JSONObject(PublicUtils.getConfig(dao.getConnection(), "美丽说Token信息2", ""));
			String responseText=Utils.sendbyget(Params.url,Params.appKey,Params.appsecret,method,object.optString("access_token"),new Date(),produceCode);
			Log.info("取商品库存返回数据为："+responseText);
			JSONObject responseObj= new JSONObject(responseText);
			//如果错误的话：
			try
			{
				String errormessage = responseObj.getJSONObject("error_response").getString("message");	 //如果没错整个try都不会执行成功，有错就会执行出错过程
				Log.error("美丽说获取商品库存作业", "获取商品库存作业失败"+errormessage);
				return "0";
			}catch(Exception e)
			{
				//如果无法获取错误代码则代表没有错，则执行：
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
