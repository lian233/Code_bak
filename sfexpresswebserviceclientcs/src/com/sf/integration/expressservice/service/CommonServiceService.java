package com.sf.integration.expressservice.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.3-hudson-390-
 * Generated source version: 2.0
 * <p>
 * An example of how this class may be used:
 * 
 * <pre>
 * CommonServiceService service = new CommonServiceService();
 * IService portType = service.getCommonServicePort();
 * portType.sfexpressService(...);
 * </pre>
 * 
 * </p>
 * 
 */
@WebServiceClient(name = "CommonServiceService", targetNamespace = "http://service.expressservice.integration.sf.com/", wsdlLocation = "http://bsp-oisp.test.sf-express.com:6080/bsp-oisp/ws/expressService?wsdl")
public class CommonServiceService extends Service {

	private final static URL COMMONSERVICESERVICE_WSDL_LOCATION;
	private final static Logger logger = Logger
			.getLogger(com.sf.integration.expressservice.service.CommonServiceService.class
					.getName());

	static {
		URL url = null;
		try {
			URL baseUrl;
			baseUrl = com.sf.integration.expressservice.service.CommonServiceService.class
					.getResource(".");
			url = new URL(baseUrl,
					"http://bsp-oisp.test.sf-express.com:6080/bsp-oisp/ws/expressService?wsdl");
		} catch (MalformedURLException e) {
			logger
					.warning("Failed to create URL for the wsdl Location: 'http://bsp-oisp.test.sf-express.com:6080/bsp-oisp/ws/expressService?wsdl', retrying as a local file");
			logger.warning(e.getMessage());
		}
		COMMONSERVICESERVICE_WSDL_LOCATION = url;
	}

	public CommonServiceService(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public CommonServiceService() {
		super(COMMONSERVICESERVICE_WSDL_LOCATION, new QName(
				"http://service.expressservice.integration.sf.com/",
				"CommonServiceService"));
	}

	/**
	 * 
	 * @return returns IService
	 */
	@WebEndpoint(name = "CommonServicePort")
	public IService getCommonServicePort() {
		return super.getPort(new QName(
				"http://service.expressservice.integration.sf.com/",
				"CommonServicePort"), IService.class);
	}

}
