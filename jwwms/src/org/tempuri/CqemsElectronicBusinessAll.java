package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name=&quot;xmlstring&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
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
@XmlType(name = "", propOrder = { "xmlstring", "emscode" })
@XmlRootElement(name = "cqems_electronic_business_all")
public class CqemsElectronicBusinessAll {

	protected String xmlstring;
	protected String emscode;

	/**
	 * Gets the value of the xmlstring property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getXmlstring() {
		return xmlstring;
	}

	/**
	 * Sets the value of the xmlstring property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setXmlstring(String value) {
		this.xmlstring = value;
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
