package com.wofu.intf.tiantu;
import java.util.List;
import java.util.Map;
import com.wofu.common.tools.util.log.Log;
/**
 * 检查一个订单的百世仓库状态
 * @author Administrator
 *
 */
public class CheckOrderStatus {
	
	public static void main(String[] as){
		String sheetid="571Z0A1501310008";
		String customercode="85000346";
		String warehousecode="EC_HZ_CHNT";
		String serviceType = "GetSalesOrderStatus";
		String msgId="891Z0L964222";
		String partnerkey="af896kg88tl";
		String partnerid="E-WOLF-85000346";
		String serviceversion="1.0";
		String callbackurl="http://gzwolfsoft.oicp.net:8002/BestLogisticsService";
		String msgtype="sync";
		String url="http://edi-gateway.800best.com/eoms/api/process";
		StringBuffer bizData = new StringBuffer();
		bizData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bizData.append("<"+serviceType+">");
		bizData.append("<customerCode>"+customercode+"</customerCode>");
		bizData.append("<warehouseCode>"+warehousecode+"</warehouseCode>");
		bizData.append("<orderCode>"+sheetid+"</orderCode>");
		bizData.append("</"+serviceType+">");

		
		
		//Log.info("bizData:　"+bizData.toString());
		List signParams;
		try {
			signParams = TianTuUtil.makeSignParams(bizData.toString(), serviceType,msgtype,
				partnerid,partnerkey,serviceversion,callbackurl,msgId);
			String sign=TianTuUtil.makeSign(signParams);


			Map requestParams=TianTuUtil.makeRequestParams(bizData.toString(), serviceType, 
				msgId, msgtype, sign,callbackurl,
				serviceversion,partnerid);

			String result=CommHelper.sendRequestT(url, requestParams, "");
			Log.info("result:　"+result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
