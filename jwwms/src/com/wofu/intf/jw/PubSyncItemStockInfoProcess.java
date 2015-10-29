package com.wofu.intf.jw;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class PubSyncItemStockInfoProcess extends JWProcess{

	@Override
	public void process() throws Exception {
		JSONArray items = new JSONArray(this.getBizData().replaceAll("\\\\","").replaceAll("\"\\[","\\[").replaceAll("\\]\"", "\\]"));
		for(int i=0;i<items.length();i++){
			JSONObject item = items.getJSONObject(i);
			ItemStock itemStock = new ItemStock();
			itemStock.setObjValue(itemStock, item);
			String sku = itemStock.getSkuId();
			int qty = Integer.parseInt(itemStock.getQuantity());
			int type="全量更新".equals(itemStock.getType())?1:0;
			//String sql ="exec IF_OuterToInvAdjust "+sku+","+qty+","+type;
			//SQLHelper.executeSQL(this.getConn(),sql);
			Log.info("sku: "+sku+",数量: "+itemStock.getQuantity()+"更新方式: "+itemStock.getType());
		}
		
	}

}
