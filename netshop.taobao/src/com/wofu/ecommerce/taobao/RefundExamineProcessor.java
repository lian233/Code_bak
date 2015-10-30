package com.wofu.ecommerce.taobao;

import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.TaobaoResponse;
import com.taobao.api.request.TmallEaiOrderRefundExamineRequest;
import com.taobao.api.response.TmallEaiOrderRefundExamineResponse;
import com.wofu.common.tools.util.log.Log;

public class RefundExamineProcessor extends TMProcessor{

	public boolean process() throws Exception {
		TaobaoClient client=new DefaultTaobaoClient(this.getUrl(), this.getAppkey(), this.getAppsecret(),"xml");
		TmallEaiOrderRefundExamineRequest req=new TmallEaiOrderRefundExamineRequest();
		req.setRefundId(this.getRefund_id());
		req.setRefundPhase(this.getRefund_phase());
		req.setRefundVersion(this.getRefund_version());
		req.setMessage(this.getMessage());
		req.setOperator(this.getOperator());
		TmallEaiOrderRefundExamineResponse response = client.execute(req , this.getToken());

		if (!response.isSuccess())
		{
			if ( response.getSubMsg().indexOf("操作状态错误")>=0)
			{
				Log.info("退款单已审核,退货ID:"+this.getRefund_id());
				return true;
			}
			return response.isSuccess();
		}
		else
			return response.isSuccess();
	}
	
}
