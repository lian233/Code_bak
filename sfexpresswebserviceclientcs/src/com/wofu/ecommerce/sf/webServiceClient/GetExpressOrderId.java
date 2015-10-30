package com.wofu.ecommerce.sf.webServiceClient;

import com.sf.integration.expressservice.service.CommonServiceService;
import com.sf.integration.expressservice.service.IService;

public class GetExpressOrderId {

	/**
	 ** ��ȡ˳�����˵���
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
	
	//��ȡ�˵���
	protected static String getExpressId(String xml) throws Exception{
		String result="";
		//ʵ����һ��service����
		CommonServiceService server = new CommonServiceService();
		//����WebService�ı��ش���
		IService isserver = server.getCommonServicePort();
		//���÷���
		result  = isserver.sfexpressService(xml);
		return result;
	}

}
