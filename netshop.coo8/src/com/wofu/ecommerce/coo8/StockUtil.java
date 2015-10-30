package com.wofu.ecommerce.coo8;

import com.coo8.api.Coo8Client;
import com.coo8.api.DefaultCoo8Client;
import com.coo8.api.request.items.ItemQuantityUpdateRequest;
import com.coo8.api.response.items.ItemQuantityUpdateResponse;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;


public class StockUtil{

	//更新阿里巴巴商品库存 
	public static void updateStock(DataCentre dao,
			ECS_StockConfigSku stockconfigsku,ECS_StockConfig stockconfig,String url,String appKey,String secretKey,int qty) throws Exception
	{
		try
		{
			Coo8Client cc = new DefaultCoo8Client(url, appKey, secretKey);
			ItemQuantityUpdateRequest request=new ItemQuantityUpdateRequest();
			request.setItemId(stockconfigsku.getSkuid());
			request.setProductId(stockconfigsku.getItemid());
			request.setQuantity(String.valueOf(qty));
			ItemQuantityUpdateResponse response=cc.execute(request);
			
			Log.info("更新库存量："+qty);
			JSONObject obj=new JSONObject(response.getBody());
			if(obj.getJSONObject("item_quantity_update_response").getString("success").equals("Y"))
			{
				Log.info("更新库巴库存成功,SKU【"+ stockconfigsku.getSku() +"】,库存:"+ qty);
				stockconfig.setStockcount(stockconfig.getStockcount()-stockconfigsku.getStockcount()+qty);
				dao.updateByKeys(stockconfig,"orgid,itemid");
				
				stockconfigsku.setStockcount(qty);
				dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
			}
			else
			{
				Log.info("更新库巴库存失败!SKU【"+ stockconfigsku.getSku() +"】,错误信息:"+response.getMsg());
				stockconfigsku.setErrflag(1);
				stockconfigsku.setErrmsg(response.getMsg());
				dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
			}
		} catch (Exception e) {
			
			Log.info("更新库巴库存失败,SKU【"+stockconfigsku.getSku()+"】,错误信息:"+e.getMessage());
			stockconfigsku.setErrflag(1);
			stockconfigsku.setErrmsg(e.getMessage());
			dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");	
			
		}
		
				
	}
	
	
}
