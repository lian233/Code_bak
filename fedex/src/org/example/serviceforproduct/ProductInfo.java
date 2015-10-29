package org.example.serviceforproduct;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.wofu.intf.fedex.FedexObject;

/**
 * <p>
 * Java class for ProductInfo complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;ProductInfo&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;skuNo&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;skuName&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;skuEnName&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;skuCategory&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;UOM&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;barcodeType&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;barcodeDefine&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;length&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}float&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;width&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}float&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;height&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}float&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;netWeight&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}float&quot;/&gt;
 *         &lt;element name=&quot;weight&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}float&quot;/&gt;
 *         &lt;element name=&quot;productDeclaredValue&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}float&quot;/&gt;
 *         &lt;element name=&quot;productLink&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;hsCode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;hsGoodsName&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;originCountry&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;brand&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;specificationsAndModels&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;goodTaxCode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;applyEnterpriseCode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;applyEnterpriseCodeCIQ&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductInfo", propOrder = { "skuNo", "skuName", "skuEnName",
		"skuCategory", "uom", "barcodeType", "barcodeDefine", "length",
		"width", "height", "netWeight", "weight", "productDeclaredValue",
		"productLink", "hsCode", "hsGoodsName", "originCountry", "brand",
		"specificationsAndModels", "goodTaxCode", "applyEnterpriseCode",
		"applyEnterpriseCodeCIQ" })
public class ProductInfo extends FedexObject{

	protected String skuNo;
	protected String skuName;
	protected String skuEnName;
	protected Integer skuCategory;
	@XmlElement(name = "UOM")
	protected String uom;
	protected Integer barcodeType;
	protected String barcodeDefine;
	protected Float length;
	protected Float width;
	protected Float height;
	protected float netWeight;
	protected float weight;
	protected float productDeclaredValue;
	@XmlElement(required = true)
	protected String productLink;
	@XmlElement(required = true)
	protected String hsCode;
	@XmlElement(required = true)
	protected String hsGoodsName;
	@XmlElement(required = true)
	protected String originCountry;
	@XmlElement(required = true)
	protected String brand;
	protected String specificationsAndModels;
	protected String goodTaxCode;
	protected String applyEnterpriseCode;
	protected String applyEnterpriseCodeCIQ;//产品商检备案号

	/**
	 * Gets the value of the skuNo property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSkuNo() {
		return skuNo;
	}

	/**
	 * Sets the value of the skuNo property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSkuNo(String value) {
		this.skuNo = value;
	}

	/**
	 * Gets the value of the skuName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSkuName() {
		return skuName;
	}

	/**
	 * Sets the value of the skuName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSkuName(String value) {
		this.skuName = value;
	}

	/**
	 * Gets the value of the skuEnName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSkuEnName() {
		return skuEnName;
	}

	/**
	 * Sets the value of the skuEnName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSkuEnName(String value) {
		this.skuEnName = value;
	}

	/**
	 * Gets the value of the skuCategory property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getSkuCategory() {
		return skuCategory;
	}

	/**
	 * Sets the value of the skuCategory property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setSkuCategory(Integer value) {
		this.skuCategory = value;
	}

	/**
	 * Gets the value of the uom property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUOM() {
		return uom;
	}

	/**
	 * Sets the value of the uom property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUOM(String value) {
		this.uom = value;
	}

	/**
	 * Gets the value of the barcodeType property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getBarcodeType() {
		return barcodeType;
	}

	/**
	 * Sets the value of the barcodeType property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setBarcodeType(Integer value) {
		this.barcodeType = value;
	}

	/**
	 * Gets the value of the barcodeDefine property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBarcodeDefine() {
		return barcodeDefine;
	}

	/**
	 * Sets the value of the barcodeDefine property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBarcodeDefine(String value) {
		this.barcodeDefine = value;
	}

	/**
	 * Gets the value of the length property.
	 * 
	 * @return possible object is {@link Float }
	 * 
	 */
	public Float getLength() {
		return length;
	}

	/**
	 * Sets the value of the length property.
	 * 
	 * @param value
	 *            allowed object is {@link Float }
	 * 
	 */
	public void setLength(Float value) {
		this.length = value;
	}

	/**
	 * Gets the value of the width property.
	 * 
	 * @return possible object is {@link Float }
	 * 
	 */
	public Float getWidth() {
		return width;
	}

	/**
	 * Sets the value of the width property.
	 * 
	 * @param value
	 *            allowed object is {@link Float }
	 * 
	 */
	public void setWidth(Float value) {
		this.width = value;
	}

	/**
	 * Gets the value of the height property.
	 * 
	 * @return possible object is {@link Float }
	 * 
	 */
	public Float getHeight() {
		return height;
	}

	/**
	 * Sets the value of the height property.
	 * 
	 * @param value
	 *            allowed object is {@link Float }
	 * 
	 */
	public void setHeight(Float value) {
		this.height = value;
	}

	/**
	 * Gets the value of the netWeight property.
	 * 
	 */
	public float getNetWeight() {
		return netWeight;
	}

	/**
	 * Sets the value of the netWeight property.
	 * 
	 */
	public void setNetWeight(float value) {
		this.netWeight = value;
	}

	/**
	 * Gets the value of the weight property.
	 * 
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 * Sets the value of the weight property.
	 * 
	 */
	public void setWeight(float value) {
		this.weight = value;
	}

	/**
	 * Gets the value of the productDeclaredValue property.
	 * 
	 */
	public float getProductDeclaredValue() {
		return productDeclaredValue;
	}

	/**
	 * Sets the value of the productDeclaredValue property.
	 * 
	 */
	public void setProductDeclaredValue(float value) {
		this.productDeclaredValue = value;
	}

	/**
	 * Gets the value of the productLink property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductLink() {
		return productLink;
	}

	/**
	 * Sets the value of the productLink property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductLink(String value) {
		this.productLink = value;
	}

	/**
	 * Gets the value of the hsCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getHsCode() {
		return hsCode;
	}

	/**
	 * Sets the value of the hsCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setHsCode(String value) {
		this.hsCode = value;
	}

	/**
	 * Gets the value of the hsGoodsName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getHsGoodsName() {
		return hsGoodsName;
	}

	/**
	 * Sets the value of the hsGoodsName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setHsGoodsName(String value) {
		this.hsGoodsName = value;
	}

	/**
	 * Gets the value of the originCountry property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOriginCountry() {
		return originCountry;
	}

	/**
	 * Sets the value of the originCountry property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOriginCountry(String value) {
		this.originCountry = value;
	}

	/**
	 * Gets the value of the brand property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBrand() {
		return brand;
	}

	/**
	 * Sets the value of the brand property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBrand(String value) {
		this.brand = value;
	}

	/**
	 * Gets the value of the specificationsAndModels property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSpecificationsAndModels() {
		return specificationsAndModels;
	}

	/**
	 * Sets the value of the specificationsAndModels property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSpecificationsAndModels(String value) {
		this.specificationsAndModels = value;
	}

	/**
	 * Gets the value of the goodTaxCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getGoodTaxCode() {
		return goodTaxCode;
	}

	/**
	 * Sets the value of the goodTaxCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setGoodTaxCode(String value) {
		this.goodTaxCode = value;
	}

	/**
	 * Gets the value of the applyEnterpriseCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getApplyEnterpriseCode() {
		return applyEnterpriseCode;
	}

	/**
	 * Sets the value of the applyEnterpriseCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setApplyEnterpriseCode(String value) {
		this.applyEnterpriseCode = value;
	}

	/**
	 * Gets the value of the applyEnterpriseCodeCIQ property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getApplyEnterpriseCodeCIQ() {
		return applyEnterpriseCodeCIQ;
	}

	/**
	 * Sets the value of the applyEnterpriseCodeCIQ property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setApplyEnterpriseCodeCIQ(String value) {
		this.applyEnterpriseCodeCIQ = value;
	}

}
