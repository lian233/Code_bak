package com.wofu.ecommerce.oauthpaipai;

public class OrderItem {
	
	private String dealSubCode;			//�Ӷ���id
	private String itemCode;			//��Ʒ����
	private String itemCodeHistory;		//��������Ʒ���ձ���
	private String itemLocalCode;		//�̼ұ���
	private String stockLocalCode;		//��Ʒ������
	private String stockAttr;			//����µ�ʱѡ��Ŀ������
	private String itemDetailLink;		//��Ʒ�����url
	private String itemName;			//��Ʒ����
	private String itemPic80;			//��ƷͼƬURL
	private Double itemRetailPrice;		//��Ʒԭ��(����ȡ��)
	private double itemDealPrice;		//����µ�ʱ����Ʒ�۸�
	private double itemAdjustPrice;		//�����ĵ����۸�:����Ϊ�����Ӽ�,����Ϊ��������
	private double itemDiscountFee;		//������Ʒ�ĺ��ֵ���ۿ��Żݼ�
	private int itemDealCount;			//��������
	private String account;				//��ֵ�ʺţ��㿨����Ʒ�����в������壩
	private String refundState;			//�˿�״̬�����˿�ʱ����ֵ
	private String refundStateDesc;		//��״̬���������˿�ʱ����ֵ
	
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getDealSubCode() {
		return dealSubCode;
	}
	public void setDealSubCode(String dealSubCode) {
		this.dealSubCode = dealSubCode;
	}
	public double getItemAdjustPrice() {
		return itemAdjustPrice;
	}
	public void setItemAdjustPrice(double itemAdjustPrice) {
		this.itemAdjustPrice = itemAdjustPrice;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getItemCodeHistory() {
		return itemCodeHistory;
	}
	public void setItemCodeHistory(String itemCodeHistory) {
		this.itemCodeHistory = itemCodeHistory;
	}
	public int getItemDealCount() {
		return itemDealCount;
	}
	public void setItemDealCount(int itemDealCount) {
		this.itemDealCount = itemDealCount;
	}
	public double getItemDealPrice() {
		return itemDealPrice;
	}
	public void setItemDealPrice(double itemDealPrice) {
		this.itemDealPrice = itemDealPrice;
	}
	public String getItemDetailLink() {
		return itemDetailLink;
	}
	public void setItemDetailLink(String itemDetailLink) {
		this.itemDetailLink = itemDetailLink;
	}
	public double getItemDiscountFee() {
		return itemDiscountFee;
	}
	public void setItemDiscountFee(double itemDiscountFee) {
		this.itemDiscountFee = itemDiscountFee;
	}
	public String getItemLocalCode() {
		return itemLocalCode;
	}
	public void setItemLocalCode(String itemLocalCode) {
		this.itemLocalCode = itemLocalCode;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemPic80() {
		return itemPic80;
	}
	public void setItemPic80(String itemPic80) {
		this.itemPic80 = itemPic80;
	}
	public Double getItemRetailPrice() {
		return itemRetailPrice;
	}
	public void setItemRetailPrice(Double itemRetailPrice) {
		this.itemRetailPrice = itemRetailPrice;
	}
	public String getRefundState() {
		return refundState;
	}
	public void setRefundState(String refundState) {
		this.refundState = refundState;
	}
	public String getRefundStateDesc() {
		return refundStateDesc;
	}
	public void setRefundStateDesc(String refundStateDesc) {
		this.refundStateDesc = refundStateDesc;
	}
	public String getStockAttr() {
		return stockAttr;
	}
	public void setStockAttr(String stockAttr) {
		this.stockAttr = stockAttr;
	}
	public String getStockLocalCode() {
		return stockLocalCode;
	}
	public void setStockLocalCode(String stockLocalCode) {
		this.stockLocalCode = stockLocalCode;
	}
	

}
