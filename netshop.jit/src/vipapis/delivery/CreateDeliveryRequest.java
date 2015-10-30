package vipapis.delivery;

import com.wofu.base.util.BusinessObject;

public class CreateDeliveryRequest extends BusinessObject{
	private String vendor_id;
	private String po_no;
	private String delivery_no;
	private String warehouse;
	private String arrival_time;
	private String carrier_name;
	private String driver_tel;
	public String getVendor_id() {
		return vendor_id;
	}
	public void setVendor_id(String vendor_id) {
		this.vendor_id = vendor_id;
	}
	public String getPo_no() {
		return po_no;
	}
	public void setPo_no(String po_no) {
		this.po_no = po_no;
	}
	public String getDelivery_no() {
		return delivery_no;
	}
	public void setDelivery_no(String delivery_no) {
		this.delivery_no = delivery_no;
	}
	public String getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	public String getArrival_time() {
		return arrival_time;
	}
	public void setArrival_time(String arrival_time) {
		this.arrival_time = arrival_time;
	}
	public String getCarrier_name() {
		return carrier_name;
	}
	public void setCarrier_name(String carrier_name) {
		this.carrier_name = carrier_name;
	}
	public String getDriver_tel() {
		return driver_tel;
	}
	public void setDriver_tel(String driver_tel) {
		this.driver_tel = driver_tel;
	}
	
}
