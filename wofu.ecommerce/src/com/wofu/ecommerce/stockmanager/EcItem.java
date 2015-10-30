package com.wofu.ecommerce.stockmanager;
/**
 * ecitem表对应数据
 */
import java.util.Date;
import com.wofu.base.util.BusinessObject;
public class EcItem extends BusinessObject{
	private int customerID;
	private String outerSkuId;
	private String outer_id;
	private String barcode;
	private String name;
	private String note;
	private int cid;
	private String pic_url;
	private int	num;
	private Date list_time;
	private Date delist_time;
	private String approve_status;
	private String num_iid;
	private String title;
	private String descript;
	private String sku_id;
	private String iid;
	private int quantity;
	private float price;
	private Date created;
	private Date modified;
	private String status;
	private String properties_name;
	private String colorName;
	private String sizeName;
	private String skuSpecId;
	private int with_hold_quantity;
	private String props_name;
	public EcItem(){}
	public EcItem(int CustomerID){
		this.customerID=CustomerID;
	}
	public int getCustomerID() {
		return customerID;
	}
	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	public String getOuter_id() {
		return outer_id;
	}
	public void setOuter_id(String outer_id) {
		this.outer_id = outer_id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public String getPic_url() {
		return pic_url;
	}
	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public Date getList_time() {
		return list_time;
	}
	public void setList_time(Date list_time) {
		this.list_time = list_time;
	}
	public Date getDelist_time() {
		return delist_time;
	}
	public void setDelist_time(Date delist_time) {
		this.delist_time = delist_time;
	}
	public String getApprove_status() {
		return approve_status;
	}
	public void setApprove_status(String approve_status) {
		this.approve_status = approve_status;
	}
	public String getNum_iid() {
		return num_iid;
	}
	public void setNum_iid(String num_iid) {
		this.num_iid = num_iid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public String getSku_id() {
		return sku_id;
	}
	public void setSku_id(String sku_id) {
		this.sku_id = sku_id;
	}
	public String getIid() {
		return iid;
	}
	public void setIid(String iid) {
		this.iid = iid;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getProperties_name() {
		return properties_name;
	}
	public void setProperties_name(String properties_name) {
		this.properties_name = properties_name;
	}
	public String getColorName() {
		return colorName;
	}
	public void setColorName(String colorName) {
		this.colorName = colorName;
	}
	public String getSizeName() {
		return sizeName;
	}
	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}
	public String getSkuSpecId() {
		return skuSpecId;
	}
	public void setSkuSpecId(String skuSpecId) {
		this.skuSpecId = skuSpecId;
	}
	public int getWith_hold_quantity() {
		return with_hold_quantity;
	}
	public void setWith_hold_quantity(int with_hold_quantity) {
		this.with_hold_quantity = with_hold_quantity;
	}
	public String getProps_name() {
		return props_name;
	}
	public void setProps_name(String props_name) {
		this.props_name = props_name;
	}
	public String getOuterSkuId() {
		return outerSkuId;
	}
	public void setOuterSkuId(String outerSkuId) {
		this.outerSkuId = outerSkuId;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	
	
	
	
}
