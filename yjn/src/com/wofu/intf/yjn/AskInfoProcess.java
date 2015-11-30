package com.wofu.intf.yjn;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;

public class AskInfoProcess extends DtcProcess{

	@Override
	public void process() throws Exception {
		Document doc = DOMHelper.newDocument(this.getBizdata(),"gbk");
		Element ele = doc.getDocumentElement();
		Element dtcFlow = DOMHelper.getSubElementsByName(ele, "DTCFlow")[0];
		Element askInfo = DOMHelper.getSubElementsByName(dtcFlow, "MES_ASK_INFO")[0];
		String infoType = DOMHelper.getSubElementVauleByName(askInfo, "MESSAGE_TYPE");
		String sheetid = DOMHelper.getSubElementVauleByName(askInfo, "WORK_NO");
		String memo = DOMHelper.getSubElementVauleByName(askInfo, "MEMO");
		Log.info("��ʱ�������ݴ���ɹ�,ԭʼ������: "+sheetid+" ��������: "+infoType+" ��ע: "+memo);
		
		
	}

}
