package com.wofu.ecommerce.sf.webServiceClient;

public class GetExpressOrderId {

	/**
	 ** ��ȡ˳�����˵���
	 */
	public static void main(String[] args) {
		getExpressId("hello");

	}
	
	//��ȡ�˵���
	public static String getExpressId(String xml){
		//ʵ����һ��service����
		CommonServiceService server = new CommonServiceService();
		//����WebService�ı��ش���
		IService isserver = server.getCommonServicePort();
		//���÷���
		String result  = isserver.sfexpressService(xml);
		System.out.println(result);
		return "";
	}

}
