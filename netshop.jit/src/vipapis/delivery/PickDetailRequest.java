package vipapis.delivery;

import com.wofu.base.util.BusinessObject;

public class PickDetailRequest extends BusinessObject{
	private String pick_no;
	private String po_no;
	private String vendor_id;
	private int page=1;
	private int limit=100;
	public String getPick_no() {
		return pick_no;
	}
	public void setPick_no(String pick_no) {
		this.pick_no = pick_no;
	}
	public String getPo_no() {
		return po_no;
	}
	public void setPo_no(String po_no) {
		this.po_no = po_no;
	}
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
	
	
}
