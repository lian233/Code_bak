package org.tempuri;

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
 *         &lt;element name=&quot;ORIGINAL_ORDER_NO&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;emscode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "originalorderno", "emscode" })
@XmlRootElement(name = "cqems_type")
public class CqemsType {

	@XmlElement(name = "ORIGINAL_ORDER_NO")
	protected String originalorderno;
	protected String emscode;

	/**
	 * Gets the value of the originalorderno property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getORIGINALORDERNO() {
		return originalorderno;
	}

	/**
	 * Sets the value of the originalorderno property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setORIGINALORDERNO(String value) {
		this.originalorderno = value;
	}

	/**
	 * Gets the value of the emscode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getEmscode() {
		return emscode;
	}

	/**
	 * Sets the value of the emscode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setEmscode(String value) {
		this.emscode = value;
	}

}
