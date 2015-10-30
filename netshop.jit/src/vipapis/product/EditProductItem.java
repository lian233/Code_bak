package vipapis.product;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class EditProductItem {
	
	/**
	* 供应商ID
	* @sampleValue vendor_id 525
	*/
	
	private int vendor_id;
	
	/**
	* 条形码
	* @sampleValue barcode 113113302011245
	*/
	
	private String barcode;
	
	/**
	* 商品描述
	* @sampleValue product_description 描述或商品描述图
	*/
	
	private String product_description;
	
	/**
	* 分类ID(只可录入三级分类ID)
	* @sampleValue category_id 111
	*/
	
	private Integer category_id;
	
	/**
	* 货号
	* @sampleValue sn 113113302011
	*/
	
	private String sn;
	
	/**
	* 产地
	* @sampleValue area_output 中国广东
	*/
	
	private String area_output;
	
	/**
	* 尺码
	* @sampleValue size 39，若是均码/无码，可填写“均码”
	*/
	
	private String size;
	
	/**
	* 材质
	* @sampleValue material 面材质：头层牛皮，里材质：猪皮，鞋底材料：橡胶底
	*/
	
	private Map<String, String> material;
	
	/**
	* 颜色
	* @sampleValue color 橙色
	*/
	
	private String color;
	
	/**
	* 市场价 （吊牌价）
	* @sampleValue market_price 739
	*/
	
	private Double market_price;
	
	/**
	* 销售价（参考值）
	* @sampleValue sell_price 259
	*/
	
	private Double sell_price;
	
	/**
	* 增值税率
	* @sampleValue tax_rate 0.17
	*/
	
	private Double tax_rate;
	
	/**
	* 售卖单位（台、双、本、支、片、个、套、件、副、束、盒）
	* @sampleValue unit 双
	*/
	
	private vipapis.product.Unit unit;
	
	/**
	* 是否航空禁运品0否1是
	* @sampleValue is_embargo 1
	*/
	
	private Integer is_embargo;
	
	/**
	* 是否易碎品0否1是
	* @sampleValue is_fragile 1
	*/
	
	private Integer is_fragile;
	
	/**
	* 是否大件0否1是
	* @sampleValue is_large 1
	*/
	
	private Integer is_large;
	
	/**
	* 是否贵重品0否1是
	* @sampleValue is_precious 1
	*/
	
	private Integer is_precious;
	
	/**
	* 是否消费税0否1是
	* @sampleValue is_consumption_tax 1
	*/
	
	private Integer is_consumption_tax;
	
	/**
	* 洗涤说明/使用说明
	* @sampleValue washing_instruct 不可机洗
	*/
	
	private String washing_instruct;
	
	/**
	* 售后说明
	* @sampleValue sale_service 凭保修证1年保修
	*/
	
	private String sale_service;
	
	/**
	* 核心概述
	* @sampleValue sub_title 正品保证
	*/
	
	private String sub_title;
	
	/**
	* 备注（其它信息）
	* @sampleValue accessory_info 鞋跟跟高：10cm、防水台高：2cm、筒高：20cm、筒围：20cm
	*/
	
	private String accessory_info;
	
	/**
	* 图片视频
	* @sampleValue video_url http://www.youku.com/
	*/
	
	private String video_url;
	
	/**
	* 长（cm）
	* @sampleValue length 10
	*/
	
	private Double length;
	
	/**
	* 宽（cm）
	* @sampleValue width 10
	*/
	
	private Double width;
	
	/**
	* 高（cm）
	* @sampleValue high 10
	*/
	
	private Double high;
	
	/**
	* 重量（KG）
	* @sampleValue weight 0.1
	*/
	
	private Double weight;
	
	public int getVendor_id(){
		return this.vendor_id;
	}
	
	public void setVendor_id(int value){
		this.vendor_id = value;
	}
	public String getBarcode(){
		return this.barcode;
	}
	
	public void setBarcode(String value){
		this.barcode = value;
	}
	public String getProduct_description(){
		return this.product_description;
	}
	
	public void setProduct_description(String value){
		this.product_description = value;
	}
	public Integer getCategory_id(){
		return this.category_id;
	}
	
	public void setCategory_id(Integer value){
		this.category_id = value;
	}
	public String getSn(){
		return this.sn;
	}
	
	public void setSn(String value){
		this.sn = value;
	}
	public String getArea_output(){
		return this.area_output;
	}
	
	public void setArea_output(String value){
		this.area_output = value;
	}
	public String getSize(){
		return this.size;
	}
	
	public void setSize(String value){
		this.size = value;
	}
	public Map<String, String> getMaterial(){
		return this.material;
	}
	
	public void setMaterial(Map<String, String> value){
		this.material = value;
	}
	public String getColor(){
		return this.color;
	}
	
	public void setColor(String value){
		this.color = value;
	}
	public Double getMarket_price(){
		return this.market_price;
	}
	
	public void setMarket_price(Double value){
		this.market_price = value;
	}
	public Double getSell_price(){
		return this.sell_price;
	}
	
	public void setSell_price(Double value){
		this.sell_price = value;
	}
	public Double getTax_rate(){
		return this.tax_rate;
	}
	
	public void setTax_rate(Double value){
		this.tax_rate = value;
	}
	public vipapis.product.Unit getUnit(){
		return this.unit;
	}
	
	public void setUnit(vipapis.product.Unit value){
		this.unit = value;
	}
	public Integer getIs_embargo(){
		return this.is_embargo;
	}
	
	public void setIs_embargo(Integer value){
		this.is_embargo = value;
	}
	public Integer getIs_fragile(){
		return this.is_fragile;
	}
	
	public void setIs_fragile(Integer value){
		this.is_fragile = value;
	}
	public Integer getIs_large(){
		return this.is_large;
	}
	
	public void setIs_large(Integer value){
		this.is_large = value;
	}
	public Integer getIs_precious(){
		return this.is_precious;
	}
	
	public void setIs_precious(Integer value){
		this.is_precious = value;
	}
	public Integer getIs_consumption_tax(){
		return this.is_consumption_tax;
	}
	
	public void setIs_consumption_tax(Integer value){
		this.is_consumption_tax = value;
	}
	public String getWashing_instruct(){
		return this.washing_instruct;
	}
	
	public void setWashing_instruct(String value){
		this.washing_instruct = value;
	}
	public String getSale_service(){
		return this.sale_service;
	}
	
	public void setSale_service(String value){
		this.sale_service = value;
	}
	public String getSub_title(){
		return this.sub_title;
	}
	
	public void setSub_title(String value){
		this.sub_title = value;
	}
	public String getAccessory_info(){
		return this.accessory_info;
	}
	
	public void setAccessory_info(String value){
		this.accessory_info = value;
	}
	public String getVideo_url(){
		return this.video_url;
	}
	
	public void setVideo_url(String value){
		this.video_url = value;
	}
	public Double getLength(){
		return this.length;
	}
	
	public void setLength(Double value){
		this.length = value;
	}
	public Double getWidth(){
		return this.width;
	}
	
	public void setWidth(Double value){
		this.width = value;
	}
	public Double getHigh(){
		return this.high;
	}
	
	public void setHigh(Double value){
		this.high = value;
	}
	public Double getWeight(){
		return this.weight;
	}
	
	public void setWeight(Double value){
		this.weight = value;
	}
	
}