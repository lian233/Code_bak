package org.example.servicefororder;

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
 *         &lt;element name=&quot;HeaderRequest&quot; type=&quot;{http://www.example.org/ServiceForOrder/}HeaderRequest&quot;/&gt;
 *         &lt;element name=&quot;orderCode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "headerRequest", "orderCode" })
@XmlRootElement(name = "getOrderByCode")
public class GetOrderByCode {

	@XmlElement(name = "HeaderRequest", required = true)
	protected HeaderRequest headerRequest;
	@XmlElement(required = true)
	protected String orderCode;

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
	 * Gets the value of the orderCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOrderCode() {
		return orderCode;
	}

	/**
	 * Sets the value of the orderCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOrderCode(String value) {
		this.orderCode = value;
	}

}
