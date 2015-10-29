package com.wofu.intf.jw;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
public class Item extends BusinessObject{
	private String goodsId="";   //商品货号
	private String title="";     //商品名称
	private String num="0";       //数量
	private String desc="";      //商品描述
	private String price="";     //商品价格
	private String postFee="";   //平邮费用	
	private String expressFee="";//快递费用
	private String emsFee="";    //ems费用
	private String outerId="";   //外部编码
	private String listTime="";  //上架时间
	private String type="一口价";      //发布类型 值:fixed(一口价),auction(拍卖)
	private String approveStatus="出售中";//approveStatus
	private DataRelation skuList = new DataRelation("skuList","com.wofu.intf.jw.skuList");
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsid(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getPostFee() {
		return postFee;
	}
	public void setPostfee(String postFee) {
		this.postFee = postFee;
	}
	public String getExpressFee() {
		return expressFee;
	}
	public void setExpressfee(String expressFee) {
		this.expressFee = expressFee;
	}
	public String getEmsFee() {
		return emsFee;
	}
	public void setEmsfee(String emsFee) {
		this.emsFee = emsFee;
	}
	public String getOuterId() {
		return outerId;
	}
	public void setOuterid(String outerId) {
		this.outerId = outerId;
	}
	public String getListTime() {
		return listTime;
	}
	public void setListtime(String listTime) {
		this.listTime = listTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getApproveStatus() {
		return approveStatus;
	}
	public void setApprovestatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}
	public DataRelation getSkuList() {
		return skuList;
	}
	public void setSkulist(DataRelation skuList) {
		this.skuList = skuList;
	}
	

}
