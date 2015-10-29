package com.wofu.intf.jw;
/*
 * 取消订单处理类
 */
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class PubCancelOrderResultProcess extends JWProcess{
	@Override
	public void process() throws Exception {
		PubCancelOrderResult result = new PubCancelOrderResult();
		result.setObjValue(result, new JSONObject(this.getBizData().substring(1,this.getBizData().length()-1).replaceAll("\\\\","")));
		String sql="exec IF_OuterToOutStock '"+result.getOrderCode()+"',"+99;
		SQLHelper.executeSQL(this.getConn(), sql);
		Log.info("取消订单处理: "+ "平台订单号: "+result.getOrderCode()+",订单号: "+result.getOuterCode()
				+"状态："+result.getIsSuccess()+",备注: "+result.getMsg());
	}

}
