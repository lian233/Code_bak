package com.wofu.intf.jw;
import com.wofu.base.util.BusinessObject;
import com.wofu.base.util.DataRelation;
public class Item extends BusinessObject{
	private String goodsId="";   //��Ʒ����
	private String title="";     //��Ʒ����
	private String num="0";       //����
	private String desc="";      //��Ʒ����
	private String price="";     //��Ʒ�۸�
	private String postFee="";   //ƽ�ʷ���	
	private String expressFee="";//��ݷ���
	private String emsFee="";    //ems����
	private String outerId="";   //�ⲿ����
	private String listTime="";  //�ϼ�ʱ��
	private String type="һ�ڼ�";      //�������� ֵ:fixed(һ�ڼ�),auction(����)
	private String approveStatus="������";//approveStatus
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
