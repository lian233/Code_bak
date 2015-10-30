package com.wofu.ecommerce.sf.webServiceClient;

import com.sf.integration.expressservice.service.CommonServiceService;
import com.sf.integration.expressservice.service.IService;

public class GetExpressOrderId {

	/**
	 ** 获取顺丰快递运单号
	 */
	public static void main(String[] args) {
		String xml="<Request service='RouteService' lang='zh-CN'><Head> GUANGYIMAOYI, 8yrN}Z-7CZ8xwXJLP0QY</Head><Body><RouteRequest tracking_type='1'  method_type='1' tracking_number='444404296775' /></Body>";
		try {
			System.out.println(getExpressId(xml));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	//获取运单号
	protected static String getExpressId(String xml) throws Exception{
		String result="";
		//实例化一个service子类
		CommonServiceService server = new CommonServiceService();
		//生成WebService的本地代理
		IService isserver = server.getCommonServicePort();
		//调用服务
		result  = isserver.sfexpressService(xml);
		return result;
	}

}
