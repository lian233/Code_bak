package org.example.serviceforproduct;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.3-hudson-390-
 * Generated source version: 2.0
 * 
 */
@WebService(name = "ServiceForProduct", targetNamespace = "http://www.example.org/ServiceForProduct/")
public interface ServiceForProduct {

	/**
	 * 
	 * @param ask
	 * @param message
	 * @param headerRequest
	 * @param error
	 * @param productInfo
	 */
	@WebMethod(action = "http://www.example.org/ServiceForProduct/createProduct")
	@RequestWrapper(localName = "createProduct", targetNamespace = "http://www.example.org/ServiceForProduct/", className = "org.example.serviceforproduct.CreateProduct")
	@ResponseWrapper(localName = "createProductResponse", targetNamespace = "http://www.example.org/ServiceForProduct/", className = "org.example.serviceforproduct.CreateProductResponse")
	public void createProduct(
			@WebParam(name = "HeaderRequest", targetNamespace = "") HeaderRequest headerRequest,
			@WebParam(name = "ProductInfo", targetNamespace = "") ProductInfo productInfo,
			@WebParam(name = "ask", targetNamespace = "", mode = WebParam.Mode.OUT) Holder<String> ask,
			@WebParam(name = "message", targetNamespace = "", mode = WebParam.Mode.OUT) Holder<String> message,
			@WebParam(name = "error", targetNamespace = "", mode = WebParam.Mode.OUT) Holder<List<ErrorType>> error);

	/**
	 * 
	 * @param ask
	 * @param productSku
	 * @param data
	 * @param headerRequest
	 * @param error
	 */
	@WebMethod(action = "http://www.example.org/ServiceForProduct/getProduct")
	@RequestWrapper(localName = "getProduct", targetNamespace = "http://www.example.org/ServiceForProduct/", className = "org.example.serviceforproduct.GetProduct")
	@ResponseWrapper(localName = "getProductResponse", targetNamespace = "http://www.example.org/ServiceForProduct/", className = "org.example.serviceforproduct.GetProductResponse")
	public void getProduct(
			@WebParam(name = "HeaderRequest", targetNamespace = "") HeaderRequest headerRequest,
			@WebParam(name = "productSku", targetNamespace = "") String productSku,
			@WebParam(name = "ask", targetNamespace = "", mode = WebParam.Mode.OUT) Holder<String> ask,
			@WebParam(name = "data", targetNamespace = "", mode = WebParam.Mode.OUT) Holder<List<ProductData>> data,
			@WebParam(name = "error", targetNamespace = "", mode = WebParam.Mode.OUT) Holder<List<ErrorType>> error);

}
