package com.wofu.intf.dtc;

import java.sql.Connection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;

public class SkuInfoProcess extends DtcProcess{

	@Override
	public void process() throws Exception {
		Document doc = DOMHelper.newDocument(this.getBizdata(),"gbk");
		Element ele = doc.getDocumentElement();
		Element dtcFlow = DOMHelper.getSubElementsByName(ele, "DTCFlow")[0];
		Element skuEle = DOMHelper.getSubElementsByName(dtcFlow, "SKU_INFO_FB")[0];
		String sku = DOMHelper.getSubElementVauleByName(skuEle, "SKU");
		String statusCode = DOMHelper.getSubElementVauleByName(skuEle, "STATUS_CODE");
		Log.info("sku: "+sku+" 状态码: "+statusCode);
		if("30".equals(statusCode) || "50".equals(statusCode)){//人工或自动审批通过 ,写入接口表
			String sql ="insert into inf_downnote(sheettype,notetime,opertype,operdata,flag)"
				+" select 900001,getdate(),100,barcodeid, 100 from barcode where custombc='"+sku+"'";
			SQLHelper.executeSQL(this.getConn(), sql);
			}
		}
		//
			
		
		
		
		

}
