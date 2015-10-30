package com.wofu.ecommerce.sf.webServiceClient;

public class GetExpressOrderId {

	/**
	 ** 获取顺丰快递运单号
	 */
	public static void main(String[] args) {
		getExpressId("hello");

	}
	
	//获取运单号
	public static String getExpressId(String xml){
		//实例化一个service子类
		CommonServiceService server = new CommonServiceService();
		//生成WebService的本地代理
		IService isserver = server.getCommonServicePort();
		//调用服务
		String result  = isserver.sfexpressService(xml);
		System.out.println(result);
		return "";
	}

}
