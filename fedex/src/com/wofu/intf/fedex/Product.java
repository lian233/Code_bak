package com.wofu.intf.fedex;
/**
 * ��Ʒ��
 */
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.wofu.base.util.BusinessObject;
@XmlRootElement
public class Product extends BusinessObject{
	private String customBC;     //��ƷSku����
	private String name;         //��Ʒ����
	private String customno;     //��ƷӢ������
	private String deptid;  //��Ʒ����skuCategory
	private String customslistno;            //��Ʒ������
	private String uom;                     //��Ʒ������λ����UOM 
	private String barcodeType;             //��������  0
	private String Spec; //����ͺ� specificationsAndModels
	private String HSCode;        //���ر���HS_CODE
	private String Price;         //�걨��ֵ   productDeclaredValue
	private String hsGoodsName;  //����Ʒ��  g.Name
	private String code;    //���Ҷ��ִ���   originCountry
	private String brand;   //Ʒ��  brand
	private String Weigh;   //��Ʒë�� (kg)
	private String NetWeigh;//��Ʒ����(kg)
	private String DetailURL;//��Ʒ����  productLink
	@XmlElement(name="skuNo")
	public String getCustomBC() {
		return customBC;
	}
	public void setCustomBC(String customBC) {
		this.customBC = customBC;
	}
	@XmlElement(name="skuName")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlElement(name="skuEnName")
	public String getCustomno() {
		return customno;
	}
	public void setCustomno(String customno) {
		this.customno = customno;
	}
	@XmlElement(name="skuCategory")
	public String getDeptid() {
		return deptid;
	}
	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}
	@XmlElement(name="applyEnterpriseCode")
	public String getCustomslistno() {
		return customslistno;
	}
	public void setCustomslistno(String customslistno) {
		this.customslistno = customslistno;
	}
	@XmlElement(name="UOM")
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	@XmlElement(name="barcodeType")
	public String getBarcodeType() {
		return barcodeType;
	}
	public void setBarcodeType(String barcodeType) {
		this.barcodeType = barcodeType;
	}
	@XmlElement(name="specificationsAndModels")
	public String getSpec() {
		return Spec;
	}
	public void setSpec(String spec) {
		Spec = spec;
	}
	@XmlElement(name="hsCode")
	public String getHSCode() {
		return HSCode;
	}
	public void setHSCode(String code) {
		HSCode = code;
	}
	@XmlElement(name="productDeclaredValue")
	public String getPrice() {
		return Price;
	}
	public void setPrice(String price) {
		Price = price;
	}
	@XmlElement(name="hsGoodsName")
	public String getHsGoodsName() {
		return hsGoodsName;
	}
	public void setHsGoodsName(String hsGoodsName) {
		this.hsGoodsName = hsGoodsName;
	}
	@XmlElement(name="originCountry")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@XmlElement(name="brand")
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	@XmlElement(name="weight")
	public String getWeigh() {
		return Weigh;
	}
	public void setWeigh(String weigh) {
		Weigh = weigh;
	}
	@XmlElement(name="netWeight")
	public String getNetWeigh() {
		return NetWeigh;
	}
	public void setNetWeigh(String netWeigh) {
		NetWeigh = netWeigh;
	}
	@XmlElement(name="productLink")
	public String getDetailURL() {
		return DetailURL;
	}
	public void setDetailURL(String detailURL) {
		DetailURL = detailURL;
	}
	
}
