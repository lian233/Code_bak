package com.wofu.ecommerce.taobao;

import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TmallEaiOrderRefundGoodReturnAgreeRequest;
import com.taobao.api.response.TmallEaiOrderRefundGoodReturnAgreeResponse;
import com.wofu.common.tools.util.log.Log;

public class ReturnAgreeProcessor extends TMProcessor{

	public boolean process() throws Exception {
		
		
		TaobaoClient client=new DefaultTaobaoClient(this.getUrl(), this.getAppkey(), this.getAppsecret(),"xml");
		TmallEaiOrderRefundGoodReturnAgreeRequest req=new TmallEaiOrderRefundGoodReturnAgreeRequest();
		req.setRefundId(this.getRefund_id());
		req.setRefundPhase(this.getRefund_phase());
		req.setRefundVersion(this.getRefund_version());
		req.setMessage(this.getMessage());
		req.setSellerLogisticsAddressId(this.getSeller_logistics_address_id());
		TmallEaiOrderRefundGoodReturnAgreeResponse response = client.execute(req , this.getToken());
	
		if (!response.isSuccess())
		{
			if ( response.getSubMsg().indexOf("当前退款状态无法执行该操作")>=0)
			{
				Log.info("当前退款状态无法执行该操作,退货ID:"+this.getRefund_id());
				return true;
			}
			return response.isSuccess();
		}
		else
			return response.isSuccess();

	}

}
