package com.wofu.ecommerce.taobao;



import java.io.File;
import java.io.FileOutputStream;


import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.FileItem;
import com.taobao.api.TaobaoClient;

import com.taobao.api.request.TmallEaiOrderRefundRefuseRequest;
import com.taobao.api.response.TmallEaiOrderRefundRefuseResponse;
import com.wofu.common.tools.util.StreamHelper;

public class RefundRefuseProcessor extends TMProcessor {

	public boolean process() throws Exception {
		TaobaoClient client=new DefaultTaobaoClient(this.getUrl(), this.getAppkey(), this.getAppsecret(),"xml");
		
		TmallEaiOrderRefundRefuseRequest req=new TmallEaiOrderRefundRefuseRequest();
		req.setRefundId(this.getRefund_id());
		req.setRefundPhase(this.getRefund_phase());
		req.setRefundVersion(this.getRefund_version());
		req.setRefuseMessage(this.getRefuse_message());

		File file = new File("refundrefuse.tmp");
		
		StreamHelper.copy(this.getRefuse_proof(),new FileOutputStream(file));
		
		FileItem fItem = new FileItem(file);
		req.setRefuseProof(fItem);
		TmallEaiOrderRefundRefuseResponse response = client.execute(req , this.getToken());
		return response.isSuccess();
	}
	
	
}
