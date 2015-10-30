package vipapis.product;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class VendorProductResponse {
	
	/**
	* 操作成功的数量
	* @sampleValue success_num 1
	*/
	
	private int success_num;
	
	/**
	* 成功数据列表
	*/
	
	private List<String> success_barcode_list;
	
	/**
	* 操作失败的数量
	* @sampleValue fail_num 0
	*/
	
	private int fail_num;
	
	/**
	* 操作失败的数据列表
	*/
	
	private List<vipapis.product.VendorProductFailItem> fail_item_list;
	
	public int getSuccess_num(){
		return this.success_num;
	}
	
	public void setSuccess_num(int value){
		this.success_num = value;
	}
	public List<String> getSuccess_barcode_list(){
		return this.success_barcode_list;
	}
	
	public void setSuccess_barcode_list(List<String> value){
		this.success_barcode_list = value;
	}
	public int getFail_num(){
		return this.fail_num;
	}
	
	public void setFail_num(int value){
		this.fail_num = value;
	}
	public List<vipapis.product.VendorProductFailItem> getFail_item_list(){
		return this.fail_item_list;
	}
	
	public void setFail_item_list(List<vipapis.product.VendorProductFailItem> value){
		this.fail_item_list = value;
	}
	
}