package org.example.servicefororder;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.wofu.intf.fedex.FedexObject;
/**
 * <p>
 * Java class for OrderInfo complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;OrderInfo&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;oabCounty&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;oabStateName&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;oabCity&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;smCode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;referenceNo&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;oabName&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;oabCompany&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;oabPostcode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;oabStreetAddress1&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;oabStreetAddress2&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;oabPhone&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;oabEmail&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;grossWt&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;netWt&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;currencyCode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;idType&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;idNumber&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;remark&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;orderStatus&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;orderProduct&quot; type=&quot;{http://www.example.org/ServiceForOrder/}productDeatilType&quot; maxOccurs=&quot;unbounded&quot;/&gt;
 *         &lt;element name=&quot;deliveryFee&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;trackingNumber&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderInfo", propOrder = { "oabCounty", "oabStateName",
		"oabCity", "smCode", "referenceNo", "oabName", "oabCompany",
		"oabPostcode", "oabStreetAddress1", "oabStreetAddress2", "oabPhone",
		"oabEmail", "grossWt", "netWt", "currencyCode", "idType", "idNumber",
		"remark", "orderStatus", "orderProduct", "deliveryFee",
		"trackingNumber","serviceType" })
public class OrderInfo extends FedexObject{

	@XmlElement(required = true)
	protected String oabCounty;
	@XmlElement(required = true)
	protected String oabStateName;
	protected String oabCity;
	@XmlElement(required = true)
	protected String smCode;
	@XmlElement(required = true)
	protected String referenceNo;
	@XmlElement(required = true)
	protected String oabName;
	protected String oabCompany;
	protected String oabPostcode;
	@XmlElement(required = true)
	protected String oabStreetAddress1;
	protected String oabStreetAddress2;
	@XmlElement(required = true)
	protected String oabPhone;
	protected String oabEmail;
	protected String grossWt;
	protected String netWt;
	protected String currencyCode;
	@XmlElement(required = true)
	protected String idType;
	protected String idNumber;
	protected String remark;
	protected String orderStatus;
	@XmlElement(required = true)
	protected List<ProductDeatilType> orderProduct;
	protected String deliveryFee;
	protected String trackingNumber;
	
	@XmlElement(required = true)
	protected String serviceType;
		

	/**
	 * Gets the value of the oabCounty property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOabCounty() {
		return oabCounty;
	}

	/**
	 * Sets the value of the oabCounty property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOabCounty(String value) {
		this.oabCounty = value;
	}

	/**
	 * Gets the value of the oabStateName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOabStateName() {
		return oabStateName;
	}

	/**
	 * Sets the value of the oabStateName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOabStateName(String value) {
		this.oabStateName = value;
	}

	/**
	 * Gets the value of the oabCity property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOabCity() {
		return oabCity;
	}

	/**
	 * Sets the value of the oabCity property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOabCity(String value) {
		this.oabCity = value;
	}

	/**
	 * Gets the value of the smCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSmCode() {
		return smCode;
	}

	/**
	 * Sets the value of the smCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSmCode(String value) {
		this.smCode = value;
	}

	/**
	 * Gets the value of the referenceNo property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getReferenceNo() {
		return referenceNo;
	}

	/**
	 * Sets the value of the referenceNo property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setReferenceNo(String value) {
		this.referenceNo = value;
	}

	/**
	 * Gets the value of the oabName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOabName() {
		return oabName;
	}

	/**
	 * Sets the value of the oabName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOabName(String value) {
		this.oabName = value;
	}

	/**
	 * Gets the value of the oabCompany property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOabCompany() {
		return oabCompany;
	}

	/**
	 * Sets the value of the oabCompany property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOabCompany(String value) {
		this.oabCompany = value;
	}

	/**
	 * Gets the value of the oabPostcode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOabPostcode() {
		return oabPostcode;
	}

	/**
	 * Sets the value of the oabPostcode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOabPostcode(String value) {
		this.oabPostcode = value;
	}

	/**
	 * Gets the value of the oabStreetAddress1 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOabStreetAddress1() {
		return oabStreetAddress1;
	}

	/**
	 * Sets the value of the oabStreetAddress1 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOabStreetAddress1(String value) {
		this.oabStreetAddress1 = value;
	}

	/**
	 * Gets the value of the oabStreetAddress2 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOabStreetAddress2() {
		return oabStreetAddress2;
	}

	/**
	 * Sets the value of the oabStreetAddress2 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOabStreetAddress2(String value) {
		this.oabStreetAddress2 = value;
	}

	/**
	 * Gets the value of the oabPhone property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOabPhone() {
		return oabPhone;
	}

	/**
	 * Sets the value of the oabPhone property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOabPhone(String value) {
		this.oabPhone = value;
	}

	/**
	 * Gets the value of the oabEmail property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOabEmail() {
		return oabEmail;
	}

	/**
	 * Sets the value of the oabEmail property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOabEmail(String value) {
		this.oabEmail = value;
	}

	/**
	 * Gets the value of the grossWt property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getGrossWt() {
		return grossWt;
	}

	/**
	 * Sets the value of the grossWt property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setGrossWt(String value) {
		this.grossWt = value;
	}

	/**
	 * Gets the value of the netWt property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNetWt() {
		return netWt;
	}

	/**
	 * Sets the value of the netWt property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNetWt(String value) {
		this.netWt = value;
	}

	/**
	 * Gets the value of the currencyCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * Sets the value of the currencyCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCurrencyCode(String value) {
		this.currencyCode = value;
	}

	/**
	 * Gets the value of the idType property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdType() {
		return idType;
	}

	/**
	 * Sets the value of the idType property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIdType(String value) {
		this.idType = value;
	}

	/**
	 * Gets the value of the idNumber property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdNumber() {
		return idNumber;
	}

	/**
	 * Sets the value of the idNumber property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIdNumber(String value) {
		this.idNumber = value;
	}

	/**
	 * Gets the value of the remark property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * Sets the value of the remark property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRemark(String value) {
		this.remark = value;
	}

	/**
	 * Gets the value of the orderStatus property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOrderStatus() {
		return orderStatus;
	}

	/**
	 * Sets the value of the orderStatus property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOrderStatus(String value) {
		this.orderStatus = value;
	}

	/**
	 * Gets the value of the orderProduct property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the orderProduct property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getOrderProduct().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ProductDeatilType }
	 * 
	 * 
	 */
	public List<ProductDeatilType> getOrderProduct() {
		if (orderProduct == null) {
			orderProduct = new ArrayList<ProductDeatilType>();
		}
		return this.orderProduct;
	}

	/**
	 * Gets the value of the deliveryFee property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDeliveryFee() {
		return deliveryFee;
	}

	/**
	 * Sets the value of the deliveryFee property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDeliveryFee(String value) {
		this.deliveryFee = value;
	}

	/**
	 * Gets the value of the trackingNumber property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTrackingNumber() {
		return trackingNumber;
	}

	/**
	 * Sets the value of the trackingNumber property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTrackingNumber(String value) {
		this.trackingNumber = value;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String value) {
		this.serviceType = value;
	}
	
	
}
