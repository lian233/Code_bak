package com.wofu.ecommerce.taobao.fenxiao;
import java.util.Date;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
public class Item extends BusinessObject {
	private long num_iid;
	private String title;
	private String detail_url;
	private String nick;
	private String type;
	private String desc;
	private String props_name;
	private Date created;
	private String promoted_service;
	private long num;
	private Date modified;
	private String approve_status;
	public String getPic_url() {
		return pic_url;
	}

	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
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

	private String outer_id;
	private String pic_url;
	private long cid;
	private Date list_time;
	private Date delist_time;
	
	private DataRelation skus =new DataRelation("promotion_detail","com.wofu.ecommerce.taobao.fenxiao.Sku");

	public long getNum_iid() {
		return num_iid;
	}

	public void setNum_iid(long num_iid) {
		this.num_iid = num_iid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetail_url() {
		return detail_url;
	}

	public void setDetail_url(String detail_url) {
		this.detail_url = detail_url;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getProps_name() {
		return props_name;
	}

	public void setProps_name(String props_name) {
		this.props_name = props_name;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getPromoted_service() {
		return promoted_service;
	}

	public void setPromoted_service(String promoted_service) {
		this.promoted_service = promoted_service;
	}

	public long getNum() {
		return num;
	}

	public void setNum(long num) {
		this.num = num;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public String getApprove_status() {
		return approve_status;
	}

	public void setApprove_status(String approve_status) {
		this.approve_status = approve_status;
	}

	public String getOuter_id() {
		return outer_id;
	}

	public void setOuter_id(String outer_id) {
		this.outer_id = outer_id;
	}

	public DataRelation getSkus() {
		return skus;
	}

	public void setSkus(DataRelation skus) {
		this.skus = skus;
	}
	
	

}
