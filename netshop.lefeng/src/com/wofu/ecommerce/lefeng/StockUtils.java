package com.wofu.ecommerce.lefeng;

import java.util.Hashtable;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;

public class StockUtils {

	public static void updateStock(DataCentre dao,String url,String shopid,String secretKey,String encoding,
				ECS_StockConfig stockconfig,ECS_StockConfigSku stockconfigsku,int qty,int updatetype) throws Exception
	{
		try
		{
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			String methodApi="sellerModifyStock";
			
			params.put("shopId", shopid) ;
			params.put("itemCode", stockconfigsku.getSku()) ;
			params.put("updateCount", String.valueOf(qty));
			params.put("updateType", String.valueOf(updatetype)) ;
			
	
			String sign=LefengUtil.getSign(params, methodApi, secretKey, encoding);
			
			params.put("sign", sign);
	
			
			String reponseText = LefengUtil.filterResponseText(CommHelper.sendRequest(url+methodApi+".htm",params,"",encoding));
		
			JSONObject jo = new JSONObject(reponseText);
	
			
			int retcode=jo.optInt("result");
			
			if (retcode!=0)
			{
				Log.warn("乐峰库存更新失败,SKU:["+stockconfigsku.getSku()+"],原数量:["+stockconfigsku.getStockcount()+"],更新数量:["+qty+"] 错误信息:"+LefengUtil.errList.get(retcode));
				
				stockconfigsku.setErrflag(1);
				stockconfigsku.setErrmsg(LefengUtil.errList.get(retcode));
				dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
				
				stockconfig.setErrflag(1);
				stockconfig.setErrmsg(LefengUtil.errList.get(retcode));
				dao.updateByKeys(stockconfig,"orgid,itemid");
				
			}else
			{		
				if (updatetype==0)
				{
					Log.info("乐峰库存更新成功,SKU:["+stockconfigsku.getSku()+"],原数量:["+stockconfigsku.getStockcount()+"],新库存:["+qty+"]");
					
					stockconfig.setStockcount(stockconfig.getStockcount()-stockconfigsku.getStockcount()+qty);
					stockconfig.setErrflag(0);
					stockconfig.setErrmsg("");
					dao.updateByKeys(stockconfig,"orgid,itemid");
					
					stockconfigsku.setStockcount(qty);
					stockconfigsku.setErrflag(0);
					stockconfigsku.setErrmsg("");
					dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
				}
				else
				{
					Log.info("乐峰库存更新成功,SKU【"+stockconfigsku.getSku()+"】,原库存:"+stockconfigsku.getStockcount()+" 调整库存:"+qty);
					
					stockconfigsku.setStockcount(stockconfigsku.getStockcount()+qty);
					dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");
					
					stockconfig.setStockcount(stockconfig.getStockcount()+qty);
					dao.updateByKeys(stockconfig,"orgid,itemid");
				}
	
			}
		} catch (Exception e) {
			
			Log.info("乐峰库存更新失败,SKU【"+stockconfigsku.getSku()+"】,错误信息:"+e.getMessage());
			
			stockconfigsku.setErrflag(1);
			stockconfigsku.setErrmsg(e.getMessage());
			dao.updateByKeys(stockconfigsku,"orgid,itemid,skuid");	
			
			stockconfig.setErrflag(1);
			stockconfig.setErrmsg(e.getMessage());
			dao.updateByKeys(stockconfig,"orgid,itemid");
			
		}
	}
}
