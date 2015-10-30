package com.wofu.ecommerce.yougou;
import com.wofu.base.util.BusinessObject;
public class OrderItem extends BusinessObject {

	private String supplier_code ="";  //��Ʒ��ɫ����(��Ӧ��)
	private int commodity_num =0;  //--��Ʒ����
	private String prod_no ="";  //--��Ʒ����
	private float prod_unit_price =0f;  //��Ʒ����
	private String style_no ="";  //---��Ʒ���
	private float prod_discount_amount =0f;  //--��Ʒ�Ż��ܼ�
	private String commodity_specification_str ="";  //--��Ʒ��ɫ���룬�Զ�������
	private String level_code ="";  //�̼һ�Ʒ����
	private String commodity_no ="";  //��Ʒ����
	private String prod_total_amt ="";  //��Ʒ�����ܽ��
	private String prod_name ="";  //��Ʒ����
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
