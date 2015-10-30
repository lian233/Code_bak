package vipapis.delivery;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;

public class PickDetailT extends BusinessObject{
	/**
	* 
	*/
	
	private String po_no;
	
	/**
	* 
	*/
	
	private String sell_st_time;
	
	/**
	* 
	*/
	
	private String sell_et_time;
	
	/**
	* 
	*/
	
	private String export_time;
	
	/**
	* 
	*/
	
	private Integer export_num;
	
	/**
	* 
	*/
	
	private String warehouse;
	
	/**
	* 
	*/
	
	private String order_cate;
	
	/**
	* 
	*/
	
	private DataRelation pick_product_lists = new DataRelation("pick_product_lists","vipapis.delivery.PickProduct");
	
	/**
	* 
	*/
	
	private Integer total;
	
	public String getPo_no(){
		return this.po_no;
	}
	
	public void setPo_no(String value){
		this.po_no = value;
	}
	public String getSell_st_time(){
		return this.sell_st_time;
	}
	
	public void setSell_st_time(String value){
		this.sell_st_time = value;
	}
	public String getSell_et_time(){
		return this.sell_et_time;
	}
	
	public void setSell_et_time(String value){
		this.sell_et_time = value;
	}
	public String getExport_time(){
		return this.export_time;
	}
	
	public void setExport_time(String value){
		this.export_time = value;
	}
	public Integer getExport_num(){
		return this.export_num;
	}
	
	public void setExport_num(Integer value){
		this.export_num = value;
	}
	public String getWarehouse(){
		return this.warehouse;
	}
	
	public void setWarehouse(String value){
		this.warehouse = value;
	}
	public String getOrder_cate(){
		return this.order_cate;
	}
	
	public void setOrder_cate(String value){
		this.order_cate = value;
	}
	
	public Integer getTotal(){
		return this.total;
	}
	
	public void setTotal(Integer value){
		this.total = value;
	}

	public DataRelation getPick_product_lists() {
		return pick_product_lists;
	}

	public void setPick_product_lists(DataRelation pick_product_lists) {
		this.pick_product_lists = pick_product_lists;
	}
	
}
