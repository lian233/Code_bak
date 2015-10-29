package org.example.servicefororder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Error complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;Error&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;errorCode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;errorMessage&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Error", propOrder = { "errorCode", "errorMessage" })
public class Error {

	@XmlElement(required = true)
	protected String errorCode;
	@XmlElement(required = true)
	protected String errorMessage;

	/**
	 * Gets the value of the errorCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Sets the value of the errorCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setErrorCode(String value) {
		this.errorCode = value;
	}

	/**
	 * Gets the value of the errorMessage property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Sets the value of the errorMessage property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setErrorMessage(String value) {
		this.errorMessage = value;
	}

}
