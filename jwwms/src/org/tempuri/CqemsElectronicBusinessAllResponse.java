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
 *         &lt;element name=&quot;cqems_electronic_business_allResult&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "cqemsElectronicBusinessAllResult" })
@XmlRootElement(name = "cqems_electronic_business_allResponse")
public class CqemsElectronicBusinessAllResponse {

	@XmlElement(name = "cqems_electronic_business_allResult")
	protected String cqemsElectronicBusinessAllResult;

	/**
	 * Gets the value of the cqemsElectronicBusinessAllResult property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCqemsElectronicBusinessAllResult() {
		return cqemsElectronicBusinessAllResult;
	}

	/**
	 * Sets the value of the cqemsElectronicBusinessAllResult property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCqemsElectronicBusinessAllResult(String value) {
		this.cqemsElectronicBusinessAllResult = value;
	}

}
