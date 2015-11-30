package com.wofu.intf.yjn;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;

public class PaymentProcess extends DtcProcess{

	@Override
	public void process() throws Exception {
		Document doc = DOMHelper.newDocument(this.getBizdata(),"gbk");
		Element ele = doc.getDocumentElement();
		Element dtcFlow = DOMHelper.getSubElementsByName(ele, "DTCFlow")[0];
		Element paymentElement = DOMHelper.getSubElementsByName(dtcFlow, "PAYMENT_INFO_FB")[0];
		String statusCode = DOMHelper.getSubElementVauleByName(paymentElement, "STATUS_CODE");
		String paymentNumber = DOMHelper.getSubElementVauleByName(paymentElement, "PAYMENT_NO");
		Log.info("支付单回执数据处理成功,支付号:　"+paymentNumber+" 支付单状态码: "+statusCode);
		
	}

}
