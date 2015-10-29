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
 *         &lt;element name=&quot;cqems_typeResult&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "cqemsTypeResult" })
@XmlRootElement(name = "cqems_typeResponse")
public class CqemsTypeResponse {

	@XmlElement(name = "cqems_typeResult")
	protected String cqemsTypeResult;

	/**
	 * Gets the value of the cqemsTypeResult property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCqemsTypeResult() {
		return cqemsTypeResult;
	}

	/**
	 * Sets the value of the cqemsTypeResult property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCqemsTypeResult(String value) {
		this.cqemsTypeResult = value;
	}

}
