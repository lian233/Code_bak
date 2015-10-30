package vipapis.delivery;

import com.wofu.base.util.BusinessObject;
//{"vendor_id":"2010","et_sell_st_time":"2015-08-12","page":1,"limit":100}
public class GetPoListRequest extends BusinessObject{
	private String vendor_id;
	private String st_sell_et_time;
	private String et_sell_et_time;
	private int page;
	private int limit=100;
	public String getVendor_id() {
		return vendor_id;
	}
	public void setVendor_id(String vendor_id) {
		this.vendor_id = vendor_id;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public String getSt_sell_et_time() {
		return st_sell_et_time;
	}
	public void setSt_sell_et_time(String st_sell_et_time) {
		this.st_sell_et_time = st_sell_et_time;
	}
	public String getEt_sell_et_time() {
		return et_sell_et_time;
	}
	public void setEt_sell_et_time(String et_sell_et_time) {
		this.et_sell_et_time = et_sell_et_time;
	}
	
	
}
