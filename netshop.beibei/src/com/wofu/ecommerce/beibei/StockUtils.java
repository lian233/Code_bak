package com.wofu.ecommerce.beibei;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wofu.base.dbmanager.DataCentre;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.beibei.utils.Utils;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;


public class StockUtils {
	public static void UpdateSkuStock(DataCentre dao, int orgid, String url,
			String app_id, String secret, String session, String sku, int qty, ECS_StockConfig stockconfig, ECS_StockConfigSku stockconfigsku) throws Exception {
		
		Map<String, String> orderlistparams = new HashMap<String, String>();
        //ϵͳ����������
        orderlistparams.put("method", "beibei.outer.item.qty.update");
		orderlistparams.put("app_id", app_id);
        orderlistparams.put("session", session);
        orderlistparams.put("timestamp", time());
        //Ӧ�ü���������
        orderlistparams.put("iid", stockconfig.getItemid());
        orderlistparams.put("outer_id", sku);
        orderlistparams.put("qty", String.valueOf(qty));
		String responseOrderListData = Utils.sendByPost(orderlistparams, secret, url);
		JSONObject responseproduct = new JSONObject(responseOrderListData);
		System.out.println(responseOrderListData);
		String message=responseproduct.optString("message");
		boolean success = responseproduct.optBoolean("success");
		if(!success){
			Log.error("����ʧ�ܣ��˳�����ѭ��"+"������Ϣ��"+message, "itemidΪ ��"+stockconfig.getItemid()+"SKUΪ ��"+sku);
			stockconfigsku.setErrflag(1);
			stockconfigsku.setErrmsg(message);
			dao.updateByKeys(stockconfigsku, "orgid,skuid");
			return ;
		}
		else{
			Log.error("�����³ɹ���"+"������Ϣ��"+message, "itemidΪ: "+stockconfig.getItemid()+"SKUΪ: "+sku+" ����ǰ���Ϊ :"+stockconfigsku.getStockcount()+"" +
					" ���º�Ϊ :"+qty);
			stockconfig.setErrflag(0);
			stockconfig.setErrmsg("");
			dao.updateByKeys(stockconfig, "orgid,itemid");
		}
		
	}
	public static String time() {
		Long time= System.currentTimeMillis()/1000;
		return time.toString();
	}

}
