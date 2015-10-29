package com.wofu.fire.deliveryservice;

import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
 * 运单处理类
 */
public class LoaderProcessor extends CProcessor{

	@Override
	public void process() throws Exception {
		JSONObject obj = JSONObject.fromObject(this.getBizData());
		String loadSheetID = obj.getString("load_id");
		JSONArray tids = obj.getJSONArray("order_ids");
		String tid ="";
		String sql ="";
		String sheetId="";
		for(int i=0;i<tids.size();i++){
			tid = (String)tids.get(i);
			if(i==0){
				sql ="declare @sheetid varchar(52);exec IF_CreateCarLoading '"+loadSheetID+"','"+tid+"',@sheetid output";
				sheetId = SQLHelper.strSelect(this.getConnection(),sql);
			}else{
				sql ="declare @sheetid varchar(52);exec IF_CreateCarLoading '"+loadSheetID+"','"+tid+"','"+sheetId+"' output";
				sheetId = SQLHelper.strSelect(this.getConnection(),sql);
			}
		}
		Log.info("生成装载单成功,装载单号: "+sheetId);
	}

}
