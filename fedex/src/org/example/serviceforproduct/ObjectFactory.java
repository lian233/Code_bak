package org.example.serviceforproduct;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the org.example.serviceforproduct package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: org.example.serviceforproduct
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link HeaderRequest }
	 * 
	 */
	public HeaderRequest createHeaderRequest() {
		return new HeaderRequest();
	}

	/**
	 * Create an instance of {@link ErrorType }
	 * 
	 */
	public ErrorType createErrorType() {
		return new ErrorType();
	}

	/**
	 * Create an instance of {@link GetProductResponse }
	 * 
	 */
	public GetProductResponse createGetProductResponse() {
		return new GetProductResponse();
	}

	/**
	 * Create an instance of {@link GetProduct }
	 * 
	 */
	public GetProduct createGetProduct() {
		return new GetProduct();
	}

	/**
	 * Create an instance of {@link ProductInfo }
	 * 
	 */
	public ProductInfo createProductInfo() {
		return new ProductInfo();
	}

	/**
	 * Create an instance of {@link CreateProductResponse }
	 * 
	 */
	public CreateProductResponse createCreateProductResponse() {
		return new CreateProductResponse();
	}

	/**
	 * Create an instance of {@link ProductData }
	 * 
	 */
	public ProductData createProductData() {
		return new ProductData();
	}

	/**
	 * Create an instance of {@link CreateProduct }
	 * 
	 */
	public CreateProduct createCreateProduct() {
		return new CreateProduct();
	}

}
