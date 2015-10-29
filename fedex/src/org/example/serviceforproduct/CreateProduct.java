package org.example.serviceforproduct;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;HeaderRequest&quot; type=&quot;{http://www.example.org/ServiceForProduct/}HeaderRequest&quot;/&gt;
 *         &lt;element name=&quot;ProductInfo&quot; type=&quot;{http://www.example.org/ServiceForProduct/}ProductInfo&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "headerRequest", "productInfo" })
@XmlRootElement(name = "createProduct")
public class CreateProduct {

	@XmlElement(name = "HeaderRequest", required = true)
	protected HeaderRequest headerRequest;
	@XmlElement(name = "ProductInfo", required = true)
	protected ProductInfo productInfo;

	/**
	 * Gets the value of the headerRequest property.
	 * 
	 * @return possible object is {@link HeaderRequest }
	 * 
	 */
	public HeaderRequest getHeaderRequest() {
		return headerRequest;
	}

	/**
	 * Sets the value of the headerRequest property.
	 * 
	 * @param value
	 *            allowed object is {@link HeaderRequest }
	 * 
	 */
	public void setHeaderRequest(HeaderRequest value) {
		this.headerRequest = value;
	}

	/**
	 * Gets the value of the productInfo property.
	 * 
	 * @return possible object is {@link ProductInfo }
	 * 
	 */
	public ProductInfo getProductInfo() {
		return productInfo;
	}

	/**
	 * Sets the value of the productInfo property.
	 * 
	 * @param value
	 *            allowed object is {@link ProductInfo }
	 * 
	 */
	public void setProductInfo(ProductInfo value) {
		this.productInfo = value;
	}

}
