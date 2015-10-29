package org.example.servicefororder;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name=&quot;ask&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;Data&quot; type=&quot;{http://www.example.org/ServiceForOrder/}OrderData&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;Error&quot; type=&quot;{http://www.example.org/ServiceForOrder/}errorType&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "ask", "data", "error" })
@XmlRootElement(name = "getOrderByCodeResponse")
public class GetOrderByCodeResponse {

	@XmlElement(required = true)
	protected String ask;
	@XmlElement(name = "Data")
	protected List<OrderData> data;
	@XmlElement(name = "Error")
	protected List<ErrorType> error;

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
	 * Gets the value of the data property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the data property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getData().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link OrderData }
	 * 
	 * 
	 */
	public List<OrderData> getData() {
		if (data == null) {
			data = new ArrayList<OrderData>();
		}
		return this.data;
	}

	/**
	 * Gets the value of the error property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the error property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getError().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ErrorType }
	 * 
	 * 
	 */
	public List<ErrorType> getError() {
		if (error == null) {
			error = new ArrayList<ErrorType>();
		}
		return this.error;
	}

}
