package org.example.servicefororder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for OrderDetailType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;OrderDetailType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;skuNo&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;skuName&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;skuCnName&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;quantity&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderDetailType", propOrder = { "skuNo", "skuName",
		"skuCnName", "quantity" })
public class OrderDetailType {

	@XmlElement(required = true)
	protected String skuNo;
	@XmlElement(required = true)
	protected String skuName;
	@XmlElement(required = true)
	protected String skuCnName;
	@XmlElement(required = true)
	protected String quantity;

	/**
	 * Gets the value of the skuNo property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSkuNo() {
		return skuNo;
	}

	/**
	 * Sets the value of the skuNo property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSkuNo(String value) {
		this.skuNo = value;
	}

	/**
	 * Gets the value of the skuName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSkuName() {
		return skuName;
	}

	/**
	 * Sets the value of the skuName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSkuName(String value) {
		this.skuName = value;
	}

	/**
	 * Gets the value of the skuCnName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSkuCnName() {
		return skuCnName;
	}

	/**
	 * Sets the value of the skuCnName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSkuCnName(String value) {
		this.skuCnName = value;
	}

	/**
	 * Gets the value of the quantity property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getQuantity() {
		return quantity;
	}

	/**
	 * Sets the value of the quantity property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setQuantity(String value) {
		this.quantity = value;
	}

}
