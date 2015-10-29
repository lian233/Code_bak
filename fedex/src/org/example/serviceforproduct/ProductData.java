package org.example.serviceforproduct;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for productData complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;productData&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;productSku&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productBarcode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productTitle&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productTitleEN&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productStatus&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;hsCode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;hsGoodsName&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;currencyCode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;unitCode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;unitName&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productLength&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productWidth&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productHeight&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productNetWeight&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productWeight&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productDeclaredValue&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productBarcodeType&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productCateoryName&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productAddTime&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productUpdateTime&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;originCountry&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;applyEnterpriseCode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;applyEnterpriseCodeCIQ&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;goodTaxCode&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;goodTaxRate&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;brand&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productDescription&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;specificationsAndModels&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;productLink&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "productData", propOrder = { "productSku", "productBarcode",
		"productTitle", "productTitleEN", "productStatus", "hsCode",
		"hsGoodsName", "currencyCode", "unitCode", "unitName", "productLength",
		"productWidth", "productHeight", "productNetWeight", "productWeight",
		"productDeclaredValue", "productBarcodeType", "productCateoryName",
		"productAddTime", "productUpdateTime", "originCountry",
		"applyEnterpriseCode", "applyEnterpriseCodeCIQ", "goodTaxCode",
		"goodTaxRate", "brand", "productDescription",
		"specificationsAndModels", "productLink" })
public class ProductData {

	protected String productSku;
	protected String productBarcode;
	protected String productTitle;
	protected String productTitleEN;
	protected String productStatus;
	protected String hsCode;
	protected String hsGoodsName;
	protected String currencyCode;
	protected String unitCode;
	protected String unitName;
	protected String productLength;
	protected String productWidth;
	protected String productHeight;
	protected String productNetWeight;
	protected String productWeight;
	protected String productDeclaredValue;
	protected String productBarcodeType;
	protected String productCateoryName;
	protected String productAddTime;
	protected String productUpdateTime;
	protected String originCountry;
	@XmlElement(required = true)
	protected String applyEnterpriseCode;
	protected String applyEnterpriseCodeCIQ;
	protected String goodTaxCode;
	protected String goodTaxRate;
	protected String brand;
	protected String productDescription;
	protected String specificationsAndModels;
	protected String productLink;

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
	 * Gets the value of the productBarcode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductBarcode() {
		return productBarcode;
	}

	/**
	 * Sets the value of the productBarcode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductBarcode(String value) {
		this.productBarcode = value;
	}

	/**
	 * Gets the value of the productTitle property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductTitle() {
		return productTitle;
	}

	/**
	 * Sets the value of the productTitle property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductTitle(String value) {
		this.productTitle = value;
	}

	/**
	 * Gets the value of the productTitleEN property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductTitleEN() {
		return productTitleEN;
	}

	/**
	 * Sets the value of the productTitleEN property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductTitleEN(String value) {
		this.productTitleEN = value;
	}

	/**
	 * Gets the value of the productStatus property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductStatus() {
		return productStatus;
	}

	/**
	 * Sets the value of the productStatus property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductStatus(String value) {
		this.productStatus = value;
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
	 * Gets the value of the unitCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUnitCode() {
		return unitCode;
	}

	/**
	 * Sets the value of the unitCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUnitCode(String value) {
		this.unitCode = value;
	}

	/**
	 * Gets the value of the unitName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUnitName() {
		return unitName;
	}

	/**
	 * Sets the value of the unitName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUnitName(String value) {
		this.unitName = value;
	}

	/**
	 * Gets the value of the productLength property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductLength() {
		return productLength;
	}

	/**
	 * Sets the value of the productLength property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductLength(String value) {
		this.productLength = value;
	}

	/**
	 * Gets the value of the productWidth property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductWidth() {
		return productWidth;
	}

	/**
	 * Sets the value of the productWidth property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductWidth(String value) {
		this.productWidth = value;
	}

	/**
	 * Gets the value of the productHeight property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductHeight() {
		return productHeight;
	}

	/**
	 * Sets the value of the productHeight property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductHeight(String value) {
		this.productHeight = value;
	}

	/**
	 * Gets the value of the productNetWeight property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductNetWeight() {
		return productNetWeight;
	}

	/**
	 * Sets the value of the productNetWeight property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductNetWeight(String value) {
		this.productNetWeight = value;
	}

	/**
	 * Gets the value of the productWeight property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductWeight() {
		return productWeight;
	}

	/**
	 * Sets the value of the productWeight property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductWeight(String value) {
		this.productWeight = value;
	}

	/**
	 * Gets the value of the productDeclaredValue property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductDeclaredValue() {
		return productDeclaredValue;
	}

	/**
	 * Sets the value of the productDeclaredValue property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductDeclaredValue(String value) {
		this.productDeclaredValue = value;
	}

	/**
	 * Gets the value of the productBarcodeType property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductBarcodeType() {
		return productBarcodeType;
	}

	/**
	 * Sets the value of the productBarcodeType property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductBarcodeType(String value) {
		this.productBarcodeType = value;
	}

	/**
	 * Gets the value of the productCateoryName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductCateoryName() {
		return productCateoryName;
	}

	/**
	 * Sets the value of the productCateoryName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductCateoryName(String value) {
		this.productCateoryName = value;
	}

	/**
	 * Gets the value of the productAddTime property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductAddTime() {
		return productAddTime;
	}

	/**
	 * Sets the value of the productAddTime property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductAddTime(String value) {
		this.productAddTime = value;
	}

	/**
	 * Gets the value of the productUpdateTime property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductUpdateTime() {
		return productUpdateTime;
	}

	/**
	 * Sets the value of the productUpdateTime property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductUpdateTime(String value) {
		this.productUpdateTime = value;
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
	 * Gets the value of the goodTaxRate property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getGoodTaxRate() {
		return goodTaxRate;
	}

	/**
	 * Sets the value of the goodTaxRate property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setGoodTaxRate(String value) {
		this.goodTaxRate = value;
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
	 * Gets the value of the productDescription property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProductDescription() {
		return productDescription;
	}

	/**
	 * Sets the value of the productDescription property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProductDescription(String value) {
		this.productDescription = value;
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

}
