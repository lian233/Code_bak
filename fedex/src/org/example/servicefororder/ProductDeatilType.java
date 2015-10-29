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
 * Java class for productDeatilType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;productDeatilType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;productSku&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;transactionPrice&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}float&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;dealPrice&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}float&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;opQuantity&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "productDeatilType", propOrder = { "productSku",
		"transactionPrice", "dealPrice", "opQuantity" })
public class ProductDeatilType extends FedexObject{

	@XmlElement(required = true)
	protected String productSku;
	@XmlElement(type = Float.class)
	protected List<Float> transactionPrice;
	@XmlElement(type = Float.class)
	protected List<Float> dealPrice;
	protected int opQuantity;

	/**
	 * Gets the value of the productSku property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductSku() {
		return productSku;
	}

	/**
	 * Sets the value of the productSku property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductSku(String value) {
		this.productSku = value;
	}

	/**
	 * Gets the value of the transactionPrice property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the transactionPrice property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getTransactionPrice().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Float }
	 * 
	 * 
	 */
	public List<Float> getTransactionPrice() {
		if (transactionPrice == null) {
			transactionPrice = new ArrayList<Float>();
		}
		return this.transactionPrice;
	}

	/**
	 * Gets the value of the dealPrice property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the dealPrice property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDealPrice().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Float }
	 * 
	 * 
	 */
	public List<Float> getDealPrice() {
		if (dealPrice == null) {
			dealPrice = new ArrayList<Float>();
		}
		return this.dealPrice;
	}

	public void setTransactionPrice(List<Float> transactionPrice) {
		this.transactionPrice = transactionPrice;
	}

	public void setDealPrice(List<Float> dealPrice) {
		this.dealPrice = dealPrice;
	}

	/**
	 * Gets the value of the opQuantity property.
	 * 
	 */
	public int getOpQuantity() {
		return opQuantity;
	}

	/**
	 * Sets the value of the opQuantity property.
	 * 
	 */
	public void setOpQuantity(int value) {
		this.opQuantity = value;
	}

}
