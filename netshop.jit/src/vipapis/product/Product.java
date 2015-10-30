package vipapis.product;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class Product {
	
	/**
	* 档期ID
	* @sampleValue schedule_id 13092
	*/
	
	private int schedule_id;
	
	/**
	* 商品ID
	* @sampleValue product_id 29698121
	*/
	
	private int product_id;
	
	/**
	* 商品名称
	* @sampleValue product_name 荔枝纹帅气桔黄色帆船鞋
	*/
	
	private String product_name;
	
	/**
	* 品牌编号
	* @sampleValue brand_store_sn 10010363
	*/
	
	private String brand_store_sn;
	
	/**
	* 品牌中文名称
	* @sampleValue brand_name 木林森
	*/
	
	private String brand_name;
	
	/**
	* 品牌英文名称
	* @sampleValue brand_name_eng MULINSEN
	*/
	
	private String brand_name_eng;
	
	/**
	* 品牌地址
	* @sampleValue brand_url http://brand.vip.com/mulinsen/
	*/
	
	private String brand_url;
	
	/**
	* 商品市场价格
	* @sampleValue market_price 1130
	*/
	
	private double market_price;
	
	/**
	* 商品现卖价格
	* @sampleValue sell_price 219
	*/
	
	private double sell_price;
	
	/**
	* 商品折扣信息
	* @sampleValue agio 1.9折
	*/
	
	private String agio;
	
	/**
	* 商品是否有库存  1=有货 0=没货
	* @sampleValue has_stock 1
	*/
	
	private int has_stock;
	
	/**
	* 商品详情页URL
	* @sampleValue product_url http://www.vip.com/detail-221606-29698121.html
	*/
	
	private String product_url;
	
	/**
	* 商品详情页小图
	* @sampleValue small_image http://a.vpimg1.com/upload/merchandise/221606/MULINSEN-2202293202-5.jpg
	*/
	
	private String small_image;
	
	/**
	* 商品详情页大图
	* @sampleValue product_image 
	*/
	
	private String product_image;
	
	/**
	* 商品详细页展示图片集
	* @sampleValue show_image [""]
	*/
	
	private List<String> show_image;
	
	/**
	* 商品详情页URL(移动端专用)
	* @sampleValue product_mobile_url http://m.vip.com/product-221606-29698121.html
	*/
	
	private String product_mobile_url;
	
	/**
	* 商品详情页图片(移动端专用)
	* @sampleValue product_mobile_image 
	*/
	
	private String product_mobile_image;
	
	/**
	* 商品分类ID
	* @sampleValue category_id 11
	*/
	
	private Integer category_id;
	
	/**
	* 商品导航分类id1
	* @sampleValue nav_category_id1 ["3"]
	*/
	
	private List<String> nav_category_id1;
	
	/**
	* 商品导航分类id2
	* @sampleValue nav_category_id2 ["14"]
	*/
	
	private List<String> nav_category_id2;
	
	/**
	* 商品导航分类id3
	* @sampleValue nav_category_id3 ["505"]
	*/
	
	private List<String> nav_category_id3;
	
	/**
	* 商品导航分类名称1
	* @sampleValue nav_first_name ["鞋类"]
	*/
	
	private List<String> nav_first_name;
	
	/**
	* 商品导航分类名称2
	* @sampleValue nav_second_name ["男鞋"]
	*/
	
	private List<String> nav_second_name;
	
	/**
	* 商品分类名称3
	* @sampleValue nav_third_name ["休闲鞋"]
	*/
	
	private List<String> nav_third_name;
	
	/**
	* 仓库名称
	* @sampleValue warehouses ["VIP_NH","VIP_BJ"]
	*/
	
	private List<String> warehouses;
	
	/**
	* 开售时间
	* @sampleValue sell_time_from 2014-11-08 10:00:00
	*/
	
	private String sell_time_from;
	
	/**
	* 下线时间
	* @sampleValue sell_time_to 2014-11-15 23:59:59
	*/
	
	private String sell_time_to;
	
	/**
	* PC开售时间
	* @sampleValue pc_show_from 2014-11-08 10:00:00
	*/
	
	private String pc_show_from;
	
	/**
	* PC下线时间
	* @sampleValue pc_show_to 2014-11-15 23:59:59
	*/
	
	private String pc_show_to;
	
	/**
	* 移动端开售时间
	* @sampleValue mobile_show_from 2014-11-08 10:00:00
	*/
	
	private String mobile_show_from;
	
	/**
	* 移动端下线时间
	* @sampleValue mobile_show_to 2014-11-15 23:59:59
	*/
	
	private String mobile_show_to;
	
	/**
	* 频道ID
	* @sampleValue channels ["4"]
	*/
	
	private List<String> channels;
	
	public int getSchedule_id(){
		return this.schedule_id;
	}
	
	public void setSchedule_id(int value){
		this.schedule_id = value;
	}
	public int getProduct_id(){
		return this.product_id;
	}
	
	public void setProduct_id(int value){
		this.product_id = value;
	}
	public String getProduct_name(){
		return this.product_name;
	}
	
	public void setProduct_name(String value){
		this.product_name = value;
	}
	public String getBrand_store_sn(){
		return this.brand_store_sn;
	}
	
	public void setBrand_store_sn(String value){
		this.brand_store_sn = value;
	}
	public String getBrand_name(){
		return this.brand_name;
	}
	
	public void setBrand_name(String value){
		this.brand_name = value;
	}
	public String getBrand_name_eng(){
		return this.brand_name_eng;
	}
	
	public void setBrand_name_eng(String value){
		this.brand_name_eng = value;
	}
	public String getBrand_url(){
		return this.brand_url;
	}
	
	public void setBrand_url(String value){
		this.brand_url = value;
	}
	public double getMarket_price(){
		return this.market_price;
	}
	
	public void setMarket_price(double value){
		this.market_price = value;
	}
	public double getSell_price(){
		return this.sell_price;
	}
	
	public void setSell_price(double value){
		this.sell_price = value;
	}
	public String getAgio(){
		return this.agio;
	}
	
	public void setAgio(String value){
		this.agio = value;
	}
	public int getHas_stock(){
		return this.has_stock;
	}
	
	public void setHas_stock(int value){
		this.has_stock = value;
	}
	public String getProduct_url(){
		return this.product_url;
	}
	
	public void setProduct_url(String value){
		this.product_url = value;
	}
	public String getSmall_image(){
		return this.small_image;
	}
	
	public void setSmall_image(String value){
		this.small_image = value;
	}
	public String getProduct_image(){
		return this.product_image;
	}
	
	public void setProduct_image(String value){
		this.product_image = value;
	}
	public List<String> getShow_image(){
		return this.show_image;
	}
	
	public void setShow_image(List<String> value){
		this.show_image = value;
	}
	public String getProduct_mobile_url(){
		return this.product_mobile_url;
	}
	
	public void setProduct_mobile_url(String value){
		this.product_mobile_url = value;
	}
	public String getProduct_mobile_image(){
		return this.product_mobile_image;
	}
	
	public void setProduct_mobile_image(String value){
		this.product_mobile_image = value;
	}
	public Integer getCategory_id(){
		return this.category_id;
	}
	
	public void setCategory_id(Integer value){
		this.category_id = value;
	}
	public List<String> getNav_category_id1(){
		return this.nav_category_id1;
	}
	
	public void setNav_category_id1(List<String> value){
		this.nav_category_id1 = value;
	}
	public List<String> getNav_category_id2(){
		return this.nav_category_id2;
	}
	
	public void setNav_category_id2(List<String> value){
		this.nav_category_id2 = value;
	}
	public List<String> getNav_category_id3(){
		return this.nav_category_id3;
	}
	
	public void setNav_category_id3(List<String> value){
		this.nav_category_id3 = value;
	}
	public List<String> getNav_first_name(){
		return this.nav_first_name;
	}
	
	public void setNav_first_name(List<String> value){
		this.nav_first_name = value;
	}
	public List<String> getNav_second_name(){
		return this.nav_second_name;
	}
	
	public void setNav_second_name(List<String> value){
		this.nav_second_name = value;
	}
	public List<String> getNav_third_name(){
		return this.nav_third_name;
	}
	
	public void setNav_third_name(List<String> value){
		this.nav_third_name = value;
	}
	public List<String> getWarehouses(){
		return this.warehouses;
	}
	
	public void setWarehouses(List<String> value){
		this.warehouses = value;
	}
	public String getSell_time_from(){
		return this.sell_time_from;
	}
	
	public void setSell_time_from(String value){
		this.sell_time_from = value;
	}
	public String getSell_time_to(){
		return this.sell_time_to;
	}
	
	public void setSell_time_to(String value){
		this.sell_time_to = value;
	}
	public String getPc_show_from(){
		return this.pc_show_from;
	}
	
	public void setPc_show_from(String value){
		this.pc_show_from = value;
	}
	public String getPc_show_to(){
		return this.pc_show_to;
	}
	
	public void setPc_show_to(String value){
		this.pc_show_to = value;
	}
	public String getMobile_show_from(){
		return this.mobile_show_from;
	}
	
	public void setMobile_show_from(String value){
		this.mobile_show_from = value;
	}
	public String getMobile_show_to(){
		return this.mobile_show_to;
	}
	
	public void setMobile_show_to(String value){
		this.mobile_show_to = value;
	}
	public List<String> getChannels(){
		return this.channels;
	}
	
	public void setChannels(List<String> value){
		this.channels = value;
	}
	
}