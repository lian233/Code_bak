package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class PickDetail {
	
	/**
	* PO单编号
	*/
	
	private String po_no;
	
	/**
	* 档期开始销售时间
	*/
	
	private String sell_st_time;
	
	/**
	* 档期结束销售时间
	*/
	
	private String sell_et_time;
	
	/**
	* 导出时间
	*/
	
	private String export_time;
	
	/**
	* 导出次数
	*/
	
	private Integer export_num;
	
	/**
	* 送货仓库
	*/
	
	private String warehouse;
	
	/**
	* 订单类别
	*/
	
	private String order_cate;
	
	/**
	* 拣货单商品信息
	*/
	
	private List<vipapis.delivery.PickProduct> pick_product_list;
	
	/**
	* 总记录
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
	public List<vipapis.delivery.PickProduct> getPick_product_list(){
		return this.pick_product_list;
	}
	
	public void setPick_product_list(List<vipapis.delivery.PickProduct> value){
		this.pick_product_list = value;
	}
	public Integer getTotal(){
		return this.total;
	}
	
	public void setTotal(Integer value){
		this.total = value;
	}
	
}