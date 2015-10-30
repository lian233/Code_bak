package vipapis.address;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class Address {
	
	/**
	* 地址编码
	* @sampleValue address_code 104104
	*/
	
	private String address_code;
	
	/**
	* 地址中文名
	* @sampleValue address_name 广东省
	*/
	
	private String address_name;
	
	/**
	* 省市区街道全称
	* @sampleValue full_name 广东省
	*/
	
	private String full_name;
	
	/**
	* 父级地址代码
	* @sampleValue parent_code 
	*/
	
	private String parent_code;
	
	/**
	* 是否存在下级  0否 1是
	* @sampleValue has_children 1
	*/
	
	private byte has_children;
	
	/**
	* 是否直辖  0否 1是
	* @sampleValue is_directly 0
	*/
	
	private byte is_directly;
	
	/**
	* 邮资,单位元
	* @sampleValue postage 10
	*/
	
	private double postage;
	
	/**
	* TMS是否支持货到付款  0否 1是
	* @sampleValue is_cod 1
	*/
	
	private byte is_cod;
	
	/**
	* 是否支持pos支付  0否 1是
	* @sampleValue is_pos 0
	*/
	
	private byte is_pos;
	
	/**
	* 是否大件商品  0否 1是
	* @sampleValue is_big 0
	*/
	
	private byte is_big;
	
	/**
	* 是否支持支付宝钱包货到付款  0否 1是
	* @sampleValue is_app 0
	*/
	
	private byte is_app;
	
	/**
	* 承运商收取手续费,单位元
	* @sampleValue cod_fee 0
	*/
	
	private double cod_fee;
	
	/**
	* 是否提供配送服务  0否 1是
	* @sampleValue is_service 1
	*/
	
	private byte is_service;
	
	/**
	* 是否支持EMS  0否 1是
	* @sampleValue is_ems 1
	*/
	
	private byte is_ems;
	
	/**
	* 大件商品费,单位元
	* @sampleValue big_money 5
	*/
	
	private double big_money;
	
	/**
	* 状态： 1正常 ，2无效
	* @sampleValue state 1
	*/
	
	private byte state;
	
	/**
	* 邮编
	* @sampleValue post_code 510000
	*/
	
	private String post_code;
	
	/**
	* 是否支持多承运商  0否 1是
	* @sampleValue more_carrier 0
	*/
	
	private byte more_carrier;
	
	/**
	* 默认承运商简称
	* @sampleValue carrier_name EMS
	*/
	
	private String carrier_name;
	
	/**
	* 到货时效
	* @sampleValue delivery 
	*/
	
	private String delivery;
	
	/**
	* 所属仓库
	* @sampleValue warehouse VIP_NH
	*/
	
	private String warehouse;
	
	/**
	* 是否支持空运  0否 1是
	* @sampleValue is_support_air_embargo 1
	*/
	
	private byte is_support_air_embargo;
	
	/**
	* 地址类型
	* @sampleValue addr_type 0
	*/
	
	private int addr_type;
	
	/**
	* 区域类型
	* @sampleValue area_type 0
	*/
	
	private String area_type;
	
	public String getAddress_code(){
		return this.address_code;
	}
	
	public void setAddress_code(String value){
		this.address_code = value;
	}
	public String getAddress_name(){
		return this.address_name;
	}
	
	public void setAddress_name(String value){
		this.address_name = value;
	}
	public String getFull_name(){
		return this.full_name;
	}
	
	public void setFull_name(String value){
		this.full_name = value;
	}
	public String getParent_code(){
		return this.parent_code;
	}
	
	public void setParent_code(String value){
		this.parent_code = value;
	}
	public byte getHas_children(){
		return this.has_children;
	}
	
	public void setHas_children(byte value){
		this.has_children = value;
	}
	public byte getIs_directly(){
		return this.is_directly;
	}
	
	public void setIs_directly(byte value){
		this.is_directly = value;
	}
	public double getPostage(){
		return this.postage;
	}
	
	public void setPostage(double value){
		this.postage = value;
	}
	public byte getIs_cod(){
		return this.is_cod;
	}
	
	public void setIs_cod(byte value){
		this.is_cod = value;
	}
	public byte getIs_pos(){
		return this.is_pos;
	}
	
	public void setIs_pos(byte value){
		this.is_pos = value;
	}
	public byte getIs_big(){
		return this.is_big;
	}
	
	public void setIs_big(byte value){
		this.is_big = value;
	}
	public byte getIs_app(){
		return this.is_app;
	}
	
	public void setIs_app(byte value){
		this.is_app = value;
	}
	public double getCod_fee(){
		return this.cod_fee;
	}
	
	public void setCod_fee(double value){
		this.cod_fee = value;
	}
	public byte getIs_service(){
		return this.is_service;
	}
	
	public void setIs_service(byte value){
		this.is_service = value;
	}
	public byte getIs_ems(){
		return this.is_ems;
	}
	
	public void setIs_ems(byte value){
		this.is_ems = value;
	}
	public double getBig_money(){
		return this.big_money;
	}
	
	public void setBig_money(double value){
		this.big_money = value;
	}
	public byte getState(){
		return this.state;
	}
	
	public void setState(byte value){
		this.state = value;
	}
	public String getPost_code(){
		return this.post_code;
	}
	
	public void setPost_code(String value){
		this.post_code = value;
	}
	public byte getMore_carrier(){
		return this.more_carrier;
	}
	
	public void setMore_carrier(byte value){
		this.more_carrier = value;
	}
	public String getCarrier_name(){
		return this.carrier_name;
	}
	
	public void setCarrier_name(String value){
		this.carrier_name = value;
	}
	public String getDelivery(){
		return this.delivery;
	}
	
	public void setDelivery(String value){
		this.delivery = value;
	}
	public String getWarehouse(){
		return this.warehouse;
	}
	
	public void setWarehouse(String value){
		this.warehouse = value;
	}
	public byte getIs_support_air_embargo(){
		return this.is_support_air_embargo;
	}
	
	public void setIs_support_air_embargo(byte value){
		this.is_support_air_embargo = value;
	}
	public int getAddr_type(){
		return this.addr_type;
	}
	
	public void setAddr_type(int value){
		this.addr_type = value;
	}
	public String getArea_type(){
		return this.area_type;
	}
	
	public void setArea_type(String value){
		this.area_type = value;
	}
	
}