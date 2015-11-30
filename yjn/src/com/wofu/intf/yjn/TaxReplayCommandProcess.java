package com.wofu.intf.yjn;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;

/**
 * Ԥ��˰��ִ����
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
		Log.info("����˰��Ԥ�۷������ݳɹ�,Ԥ�۱��: "+sheetid+" ������: "+orderNo+"Ԥ��˰��:��"+taxFee);
		
		
		
	}

}
