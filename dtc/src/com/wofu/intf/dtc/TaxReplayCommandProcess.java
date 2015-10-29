package com.wofu.intf.dtc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;

/**
 * 预扣税回执处理
 * @author Administrator
 *
 */
public class TaxReplayCommandProcess extends DtcProcess{

	@Override
	public void process() throws Exception {
		Document doc = DOMHelper.newDocument(this.getBizdata(),"gbk");
		Element ele = doc.getDocumentElement();
		Element dtcFlow = DOMHelper.getSubElementsByName(ele, "DTCFlow")[0];
		Element taxReplay = DOMHelper.getSubElementsByName(dtcFlow, "TAX_PREPAY_COMMAND")[0] ;
		String sheetid = DOMHelper.getSubElementVauleByName(taxReplay, "PRE_PAY_NO");
		String orderNo = DOMHelper.getSubElementVauleByName(taxReplay, "ORDER_NO");
		String taxFee = DOMHelper.getSubElementVauleByName(taxReplay, "TAX_FEE");
		Log.info("处理税费预扣反馈数据成功,预扣编号: "+sheetid+" 订单号: "+orderNo+"预扣税额:　"+taxFee);
		
		
		
	}

}
