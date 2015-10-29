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
 *         &lt;element name=&quot;cqems_electronic_businessResult&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "cqemsElectronicBusinessResult" })
@XmlRootElement(name = "cqems_electronic_businessResponse")
public class CqemsElectronicBusinessResponse {

	@XmlElement(name = "cqems_electronic_businessResult")
	protected String cqemsElectronicBusinessResult;

	/**
	 * Gets the value of the cqemsElectronicBusinessResult property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCqemsElectronicBusinessResult() {
		return cqemsElectronicBusinessResult;
	}

	/**
	 * Sets the value of the cqemsElectronicBusinessResult property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCqemsElectronicBusinessResult(String value) {
		this.cqemsElectronicBusinessResult = value;
	}

}
