package com.sf.integration.warehouse.service;

public class GetoutsideToLscService {
	public static String getoutsideToLscServices(String xml) throws Exception {
		OutsideToLscmServiceService service = new OutsideToLscmServiceService();
		IOutsideToLscmService is = service.getOutsideToLscmServicePort();
		return is.outsideToLscmService(xml);
	}
}
