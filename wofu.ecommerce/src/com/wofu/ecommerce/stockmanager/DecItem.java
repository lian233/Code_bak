package com.wofu.ecommerce.stockmanager;
import java.util.Date;
import com.wofu.base.util.BusinessObject;
public class DecItem extends BusinessObject{
	private int customerid;
	private int shopid;
	private String title;
	private String outerskuid;
	private int num;
	private long  sku_id;
	private Date modified;
	private Date created;
	private Date delist_time;
	private Date list_time;
	private long num_iid;
	private int errflag;
	private String errmsg;
	private int isneedsyn;
	private double synrate;
	private String itemcode;  //  outer_id
	private float price;//基本售价
	private int alarmqty;
	private String approve_status;
	private int cid;
	private String pic_url;
	private String props_name;
	private String colorname;
	private String sizename;
	private String properties_name;
	public int getCustomerid() {
		return customerid;
	}
	public void setCustomerid(int customerid) {
		this.customerid = customerid;
	}
	public int getShopid() {
		return shopid;
	}
	public void setShopid(int shopid) {
		this.shopid = shopid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public long getSku_id() {
		return sku_id;
	}
	public void setSku_id(long sku_id) {
		this.sku_id = sku_id;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public String getOuterskuid() {
		return outerskuid;
	}
	public void setOuterskuid(String outerskuid) {
		this.outerskuid = outerskuid;
	}
	public long getNum_iid() {
		return num_iid;
	}
	public void setNum_iid(long num_iid) {
		this.num_iid = num_iid;
	}
	public int getErrflag() {
		return errflag;
	}
	public void setErrflag(int errflag) {
		this.errflag = errflag;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	public int getIsneedsyn() {
		return isneedsyn;
	}
	public void setIsneedsyn(int isneedsyn) {
		this.isneedsyn = isneedsyn;
	}
	public double getSynrate() {
		return synrate;
	}
	public void setSynrate(double synrate) {
		this.synrate = synrate;
	}

	public int getAlarmqty() {
		return alarmqty;
	}
	public void setAlarmqty(int alarmqty) {
		this.alarmqty = alarmqty;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getDelist_time() {
		return delist_time;
	}
	public void setDelist_time(Date delist_time) {
		this.delist_time = delist_time;
	}
	public Date getList_time() {
		return list_time;
	}
	public void setList_time(Date list_time) {
		this.list_time = list_time;
	}
	public String getItemcode() {
		return itemcode;
	}
	public void setItemcode(String itemcode) {
		this.itemcode = itemcode;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getApprove_status() {
		return approve_status;
	}
	public void setApprove_status(String approve_status) {
		this.approve_status = approve_status;
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

	public String getColorname() {
		return colorname;
	}
	public void setColorname(String colorname) {
		this.colorname = colorname;
	}
	public String getSizename() {
		return sizename;
	}
	public void setSizename(String sizename) {
		this.sizename = sizename;
	}
	public String getProps_name() {
		return props_name;
	}
	public void setProps_name(String props_name) {
		this.props_name = props_name;
	}
	public String getProperties_name() {
		return properties_name;
	}
	public void setProperties_name(String properties_name) {
		this.properties_name = properties_name;
	}
	

	
	
	
	
	
	
	
	
	
	
}
