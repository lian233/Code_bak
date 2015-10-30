package com.wofu.ecommerce.yougou;
import com.wofu.base.util.BusinessObject;
public class OrderItem extends BusinessObject {

	private String supplier_code ="";  //商品款色编码(供应商)
	private int commodity_num =0;  //--商品数量
	private String prod_no ="";  //--货品编码
	private float prod_unit_price =0f;  //货品单价
	private String style_no ="";  //---商品款号
	private float prod_discount_amount =0f;  //--货品优惠总价
	private String commodity_specification_str ="";  //--商品颜色尺码，以逗号劈开
	private String level_code ="";  //商家货品条码
	private String commodity_no ="";  //商品编码
	private String prod_total_amt ="";  //货品结算总金额
	private String prod_name ="";  //货品名称
	public String getSupplier_code() {
		return supplier_code;
	}
	public void setSupplier_code(String supplier_code) {
		this.supplier_code = supplier_code;
	}
	public int getCommodity_num() {
		return commodity_num;
	}
	public void setCommodity_num(int commodity_num) {
		this.commodity_num = commodity_num;
	}
	public String getProd_no() {
		return prod_no;
	}
	public void setProd_no(String prod_no) {
		this.prod_no = prod_no;
	}
	public float getProd_unit_price() {
		return prod_unit_price;
	}
	public void setProd_unit_price(float prod_unit_price) {
		this.prod_unit_price = prod_unit_price;
	}
	public String getStyle_no() {
		return style_no;
	}
	public void setStyle_no(String style_no) {
		this.style_no = style_no;
	}
	public float getProd_discount_amount() {
		return prod_discount_amount;
	}
	public void setProd_discount_amount(float prod_discount_amount) {
		this.prod_discount_amount = prod_discount_amount;
	}
	public String getCommodity_specification_str() {
		return commodity_specification_str;
	}
	public void setCommodity_specification_str(String commodity_specification_str) {
		this.commodity_specification_str = commodity_specification_str;
	}
	public String getLevel_code() {
		return level_code;
	}
	public void setLevel_code(String level_code) {
		this.level_code = level_code;
	}
	public String getCommodity_no() {
		return commodity_no;
	}
	public void setCommodity_no(String commodity_no) {
		this.commodity_no = commodity_no;
	}
	public String getProd_total_amt() {
		return prod_total_amt;
	}
	public void setProd_total_amt(String prod_total_amt) {
		this.prod_total_amt = prod_total_amt;
	}
	public String getProd_name() {
		return prod_name;
	}
	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}
	
	
	
	
}
