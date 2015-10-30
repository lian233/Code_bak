package com.wofu.ecommerce.rke;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.rke.utils.Utils;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
public class StockUtils {
	public static boolean updateStock(String url,String ver,ECS_StockConfigSku ecs_stockconfigsku,String qtyStr) throws Exception
	{
		boolean flag=false;
		Map<String, String> updatestockparams = new HashMap<String, String>();
        //系统级参数设置
        updatestockparams.put("act", "update_products_number");
        updatestockparams.put("api_version", ver);
        updatestockparams.put("product_id", ecs_stockconfigsku.getSku());
        updatestockparams.put("store", qtyStr);
       // Log.info("sku: "+ecs_stockconfigsku.getSku()+",arr: "+qtyStr);
        String responseOrderListData = Utils.sendByPost(updatestockparams, url);
       // Log.info("resonse: "+responseOrderListData);
		Document doc = DOMHelper.newDocument(responseOrderListData, "utf-8");
		Element result = doc.getDocumentElement();
		String res = DOMHelper.getSubElementVauleByName(result, "result");
		
		if (!"success".equals(res))
		{
			flag=false;
			String errorMsg = DOMHelper.getSubElementVauleByName(result, "msg");
			Log.warn("库存更新失败,SKU:["+ecs_stockconfigsku.getSku()+"],更新数量:["+qtyStr+"] 错误信息:"+errorMsg);
		}
		else
		{
			flag=true;
			Log.info("库存更新成功,SKU:["+ecs_stockconfigsku.getSku()+"],更新数量:["+qtyStr+"]");
		}
		
		return flag;
		
	}

	public static Element getItemById(String goodId,String itemcolumns) throws Exception{
		String method ="search_goods_detail";
		Map<String, String> orderlistparams = new HashMap<String, String>();
        //系统级参数设置
		orderlistparams.put("api_version", Params.ver);
        orderlistparams.put("act", method);
        orderlistparams.put("goods_id", goodId);
        orderlistparams.put("columns", itemcolumns);
		String responseOrderListData = Utils.sendByPost(orderlistparams, Params.url);
		Log.info("detailResult: "+responseOrderListData);
		Document doc = DOMHelper.newDocument(responseOrderListData, "GBK");
		Element ele = doc.getDocumentElement();
		String result = DOMHelper.getSubElementVauleByName(ele, "result");
		if("success".equals(result)){
			Element info = DOMHelper.getSubElementsByName(ele, "info")[0];
			Element data_info = DOMHelper.getSubElementsByName(info, "data_info")[0];
			Element items = DOMHelper.getSubElementsByName(data_info,"item")[0];
			responseOrderListData=null;
			ele=null;
			data_info=null;
			return items;
		}
		return null;
	}
}
