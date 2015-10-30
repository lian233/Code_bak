package vipapis.delivery;
import java.util.Map;
import java.util.List;
import java.util.Set;

import com.wofu.base.util.BusinessObject;
public  class PickProduct extends BusinessObject{
	private Integer stock;

	private String barcode;
	
	private String art_no;
	
	private String product_name;
	
	private String size;
	private float actual_market_price;
	
	public Integer getStock(){
		return this.stock;
	}
	
	public void setStock(Integer value){
		this.stock = value;
	}
	public String getBarcode(){
		return this.barcode;
	}
	
	public void setBarcode(String value){
		this.barcode = value;
	}
	public String getArt_no(){
		return this.art_no;
	}
	
	public void setArt_no(String value){
		this.art_no = value;
	}
	public String getProduct_name(){
		return this.product_name;
	}
	
	public void setProduct_name(String value){
		this.product_name = value;
	}
	public String getSize(){
		return this.size;
	}
	
	public void setSize(String value){
		this.size = value;
	}

	public float getActual_market_price() {
		return actual_market_price;
	}

	public void setActual_market_price(float actual_market_price) {
		this.actual_market_price = actual_market_price;
	}
	
	
}