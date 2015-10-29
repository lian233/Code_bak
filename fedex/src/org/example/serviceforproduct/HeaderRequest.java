package org.example.serviceforproduct;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for HeaderRequest complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;HeaderRequest&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;customerCode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;appToken&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;appKey&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HeaderRequest", propOrder = { "customerCode", "appToken",
		"appKey" })
public class HeaderRequest {

	@XmlElement(required = true)
	protected String customerCode;
	@XmlElement(required = true)
	protected String appToken;
	@XmlElement(required = true)
	protected String appKey;

	/**
	 * Gets the value of the customerCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCustomerCode() {
		return customerCode;
	}

	/**
	 * Sets the value of the customerCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCustomerCode(String value) {
		this.customerCode = value;
	}

	/**
	 * Gets the value of the appToken property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getAppToken() {
		return appToken;
	}

	/**
	 * Sets the value of the appToken property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setAppToken(String value) {
		this.appToken = value;
	}

	/**
	 * Gets the value of the appKey property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getAppKey() {
		return appKey;
	}

	/**
	 * Sets the value of the appKey property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setAppKey(String value) {
		this.appKey = value;
	}

}
