package com.wofu.intf.jw;
/*
 * ȡ������������
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
		Log.info("ȡ����������: "+ "ƽ̨������: "+result.getOrderCode()+",������: "+result.getOuterCode()
				+"״̬��"+result.getIsSuccess()+",��ע: "+result.getMsg());
	}

}
