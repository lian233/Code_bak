package vipapis.delivery;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class Pick {
	
	/**
	* PO单编号
	*/
	
	private String po_no;
	
	/**
	* 拣货单编号
	*/
	
	private String pick_no;
	
	/**
	* 合作模式
	*/
	
	private String co_mode;
	
	/**
	* 售卖站点
	*/
	
	private String sell_site;
	
	/**
	* 订单类别
	*/
	
	private String order_cate;
	
	/**
	* 拣货数量
	*/
	
	private Integer pick_num;
	
	/**
	* 拣货单创建时间
	*/
	
	private String create_time;
	
	/**
	* 首次导出时间
	*/
	
	private String first_export_time;
	
	/**
	* 导出次数
	*/
	
	private Integer export_num;
	
	/**
	* 送货状态
	*/
	
	private Integer delivery_status;
	
	public String getPo_no(){
		return this.po_no;
	}
	
	public void setPo_no(String value){
		this.po_no = value;
	}
	public String getPick_no(){
		return this.pick_no;
	}
	
	public void setPick_no(String value){
		this.pick_no = value;
	}
	public String getCo_mode(){
		return this.co_mode;
	}
	
	public void setCo_mode(String value){
		this.co_mode = value;
	}
	public String getSell_site(){
		return this.sell_site;
	}
	
	public void setSell_site(String value){
		this.sell_site = value;
	}
	public String getOrder_cate(){
		return this.order_cate;
	}
	
	public void setOrder_cate(String value){
		this.order_cate = value;
	}
	public Integer getPick_num(){
		return this.pick_num;
	}
	
	public void setPick_num(Integer value){
		this.pick_num = value;
	}
	public String getCreate_time(){
		return this.create_time;
	}
	
	public void setCreate_time(String value){
		this.create_time = value;
	}
	public String getFirst_export_time(){
		return this.first_export_time;
	}
	
	public void setFirst_export_time(String value){
		this.first_export_time = value;
	}
	public Integer getExport_num(){
		return this.export_num;
	}
	
	public void setExport_num(Integer value){
		this.export_num = value;
	}
	public Integer getDelivery_status(){
		return this.delivery_status;
	}
	
	public void setDelivery_status(Integer value){
		this.delivery_status = value;
	}
	
}