package com.wofu.ecommerce.taobao;

import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.TaobaoResponse;
import com.taobao.api.request.TmallEaiOrderRefundExamineCancelRequest;
import com.taobao.api.response.TmallEaiOrderRefundExamineCancelResponse;

public class RefundExamineCancelProcessor extends TMProcessor {

	public boolean process() throws Exception {
		TaobaoClient client=new DefaultTaobaoClient(this.getUrl(), this.getAppkey(), this.getAppsecret(),"xml");
		TmallEaiOrderRefundExamineCancelRequest req=new TmallEaiOrderRefundExamineCancelRequest();
		req.setRefundId(this.getRefund_id());
		req.setRefundPhase(this.getRefund_phase());
		req.setRefundVersion(this.getRefund_version());
		req.setMessage(this.getMessage());
		req.setOperator(this.getOperator());
		TmallEaiOrderRefundExamineCancelResponse response = client.execute(req , this.getToken());
		return response.isSuccess();
	}
	
}
