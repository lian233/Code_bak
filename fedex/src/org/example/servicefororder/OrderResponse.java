package org.example.servicefororder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for OrderResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;OrderResponse&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;ask&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;message&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;orderCode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;orderStatu&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderResponse", propOrder = { "ask", "message", "orderCode",
		"orderStatu" })
public class OrderResponse {

	@XmlElement(required = true)
	protected String ask;
	@XmlElement(required = true)
	protected String message;
	@XmlElement(required = true)
	protected String orderCode;
	protected int orderStatu;

	/**
	 * Gets the value of the ask property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getAsk() {
		return ask;
	}

	/**
	 * Sets the value of the ask property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setAsk(String value) {
		this.ask = value;
	}

	/**
	 * Gets the value of the message property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the value of the message property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMessage(String value) {
		this.message = value;
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

	/**
	 * Gets the value of the orderStatu property.
	 * 
	 */
	public int getOrderStatu() {
		return orderStatu;
	}

	/**
	 * Sets the value of the orderStatu property.
	 * 
	 */
	public void setOrderStatu(int value) {
		this.orderStatu = value;
	}

}
