package vipapis.schedule;

import java.util.Map;
import java.util.List;
import java.util.Set;



public  class Schedule {
	
	/**
	* 档期ID
	* @sampleValue schedule_id 204565
	*/
	
	private int schedule_id;
	
	/**
	* 档期名
	* @sampleValue schedule_name XXX唯品会专场
	*/
	
	private String schedule_name;
	
	/**
	* 档期上线时间(格式yyyy-MM-dd HH:mm:ss)
	* @sampleValue start_time 2014-07-01 10:00:00
	*/
	
	private String start_time;
	
	/**
	* 档期下线时间(格式yyyy-MM-dd HH:mm:ss)
	* @sampleValue end_time 2014-08-15 23:59:59
	*/
	
	private String end_time;
	
	/**
	* 首页大图(预售图)
	* @sampleValue index_image http://a.vpimg1.com/upload/brand/201406/2014062718434639161.jpg
	*/
	
	private String index_image;
	
	/**
	* 首页开售预告图
	* @sampleValue index_advance_image http://a.vpimg1.com/upload/brand/201406/2014062718434640182.jpg
	*/
	
	private String index_advance_image;
	
	/**
	* 档期LOGO
	* @sampleValue schedule_self_logo http://a.vpimg1.com/upload/brand/201406/2014060614445262401.jpg
	*/
	
	private String schedule_self_logo;
	
	/**
	* (放在首页)导航LOGO
	* @sampleValue logo http://a.vpimg1.com/upload/brand/201406/2014060614445237392.jpg
	*/
	
	private String logo;
	
	/**
	* 档期折扣信息
	* @sampleValue agio 
	*/
	
	private String agio;
	
	/**
	* 品牌库编号
	* @sampleValue brand_store_sn ["10014734"]
	*/
	
	private List<String> brand_store_sn;
	
	/**
	* 品牌中文名
	* @sampleValue brand_name ["广汽"]
	*/
	
	private List<String> brand_name;
	
	/**
	* 品牌英文名
	* @sampleValue brand_name_eng ["TOYOTA"]
	*/
	
	private List<String> brand_name_eng;
	
	/**
	* 品牌地址
	* @sampleValue brand_url [""]
	*/
	
	private List<String> brand_url;
	
	/**
	* 档期对应PC端的跳转URL
	* @sampleValue schedule_url http://www.vip.com/show-204565.html
	*/
	
	private String schedule_url;
	
	/**
	* 档期对应的观看flash
	* @sampleValue schedule_flash 
	*/
	
	private String schedule_flash;
	
	/**
	* 档期对应移动端的跳转url
	* @sampleValue schedule_mobile_url http://m.vip.com/brand-204565-0-0-0-1-0-1-40.html
	*/
	
	private String schedule_mobile_url;
	
	/**
	* 移动端档期图url
	* @sampleValue mobile_image_one 
	*/
	
	private String mobile_image_one;
	
	/**
	* 移动端预售图url
	* @sampleValue mobile_image_two 
	*/
	
	private String mobile_image_two;
	
	/**
	* 移动端开售时间(格式yyyy-MM-dd HH:mm:ss))
	* @sampleValue mobile_show_from 2014-07-01 10:00:00
	*/
	
	private String mobile_show_from;
	
	/**
	* 移动端截止时间(格式yyyy-MM-dd HH:mm:ss))
	* @sampleValue mobile_show_to 2014-08-15 23:59:59
	*/
	
	private String mobile_show_to;
	
	/**
	* Pc端开售时间(格式yyyy-MM-dd HH:mm:ss))
	* @sampleValue pc_show_from 2014-07-01 10:00:00
	*/
	
	private String pc_show_from;
	
	/**
	* Pc端截至时间(格式yyyy-MM-dd HH:mm:ss))
	* @sampleValue pc_show_to 2014-08-15 23:59:59
	*/
	
	private String pc_show_to;
	
	/**
	* 频道ID,0: 首页,1：女士,2：男士,3：儿童,4：生活,5：奢侈品,6：闪购,8：品牌夜宴,9：服饰鞋包,10：美妆,11：亲子,12：居家
	*/
	
	private List<String> channels;
	
	public int getSchedule_id(){
		return this.schedule_id;
	}
	
	public void setSchedule_id(int value){
		this.schedule_id = value;
	}
	public String getSchedule_name(){
		return this.schedule_name;
	}
	
	public void setSchedule_name(String value){
		this.schedule_name = value;
	}
	public String getStart_time(){
		return this.start_time;
	}
	
	public void setStart_time(String value){
		this.start_time = value;
	}
	public String getEnd_time(){
		return this.end_time;
	}
	
	public void setEnd_time(String value){
		this.end_time = value;
	}
	public String getIndex_image(){
		return this.index_image;
	}
	
	public void setIndex_image(String value){
		this.index_image = value;
	}
	public String getIndex_advance_image(){
		return this.index_advance_image;
	}
	
	public void setIndex_advance_image(String value){
		this.index_advance_image = value;
	}
	public String getSchedule_self_logo(){
		return this.schedule_self_logo;
	}
	
	public void setSchedule_self_logo(String value){
		this.schedule_self_logo = value;
	}
	public String getLogo(){
		return this.logo;
	}
	
	public void setLogo(String value){
		this.logo = value;
	}
	public String getAgio(){
		return this.agio;
	}
	
	public void setAgio(String value){
		this.agio = value;
	}
	public List<String> getBrand_store_sn(){
		return this.brand_store_sn;
	}
	
	public void setBrand_store_sn(List<String> value){
		this.brand_store_sn = value;
	}
	public List<String> getBrand_name(){
		return this.brand_name;
	}
	
	public void setBrand_name(List<String> value){
		this.brand_name = value;
	}
	public List<String> getBrand_name_eng(){
		return this.brand_name_eng;
	}
	
	public void setBrand_name_eng(List<String> value){
		this.brand_name_eng = value;
	}
	public List<String> getBrand_url(){
		return this.brand_url;
	}
	
	public void setBrand_url(List<String> value){
		this.brand_url = value;
	}
	public String getSchedule_url(){
		return this.schedule_url;
	}
	
	public void setSchedule_url(String value){
		this.schedule_url = value;
	}
	public String getSchedule_flash(){
		return this.schedule_flash;
	}
	
	public void setSchedule_flash(String value){
		this.schedule_flash = value;
	}
	public String getSchedule_mobile_url(){
		return this.schedule_mobile_url;
	}
	
	public void setSchedule_mobile_url(String value){
		this.schedule_mobile_url = value;
	}
	public String getMobile_image_one(){
		return this.mobile_image_one;
	}
	
	public void setMobile_image_one(String value){
		this.mobile_image_one = value;
	}
	public String getMobile_image_two(){
		return this.mobile_image_two;
	}
	
	public void setMobile_image_two(String value){
		this.mobile_image_two = value;
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
	public List<String> getChannels(){
		return this.channels;
	}
	
	public void setChannels(List<String> value){
		this.channels = value;
	}
	
}