package org.example.servicefororder;

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
 * ServiceForOrder service = new ServiceForOrder();
 * ServiceForOrder portType = service.getServiceForOrderSOAP();
 * portType.createOrder(...);
 * </pre>
 * 
 * </p>
 * 
 */
@WebServiceClient(name = "ServiceForOrder", targetNamespace = "http://www.example.org/ServiceForOrder/", wsdlLocation = "http://fedex-import.fd95.cn//default/order-soap")
public class ServiceForOrder_Service extends Service {

	private final static URL SERVICEFORORDER_WSDL_LOCATION;
	private final static Logger logger = Logger
			.getLogger(org.example.servicefororder.ServiceForOrder_Service.class
					.getName());

	static {
		URL url = null;
		try {
			URL baseUrl;
			baseUrl = org.example.servicefororder.ServiceForOrder_Service.class
					.getResource(".");
			url = new URL(baseUrl,
					"http://fedex-import.fd95.cn//default/order-soap");
		} catch (MalformedURLException e) {
			logger
					.warning("Failed to create URL for the wsdl Location: 'http://fedex-import.fd95.cn//default/order-soap', retrying as a local file");
			logger.warning(e.getMessage());
		}
		SERVICEFORORDER_WSDL_LOCATION = url;
	}

	public ServiceForOrder_Service(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public ServiceForOrder_Service() {
		super(SERVICEFORORDER_WSDL_LOCATION, new QName(
				"http://www.example.org/ServiceForOrder/", "ServiceForOrder"));
	}

	/**
	 * 
	 * @return returns ServiceForOrder
	 */
	@WebEndpoint(name = "ServiceForOrderSOAP")
	public ServiceForOrder getServiceForOrderSOAP() {
		return super.getPort(new QName(
				"http://www.example.org/ServiceForOrder/",
				"ServiceForOrderSOAP"), ServiceForOrder.class);
	}

}