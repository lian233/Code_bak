package com.wofu.fenxiao.domain;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.wofu.fenxiao.utils.JsonDateSerializer;

public class DecCustomer {
	private int Id;
	private String Name;
	private String Code;
	private int GroupID;
	private int DeliveryGroupID;
	private String Address;
	private String State;
	private String City;
	private String District;
	private int Level;
	
	private String PrintContent1;
	private String PrintContent2;
	private String PrintContent3;
	private String PrintContent4;
	private String PrintContent5;
	private String PrintContent6;
	private String PrintContent7;
	private String PrintContent8;
	private String PrintContent9;
	private String PrintContent10;
	
	
	public String getState() {
		return State;
	}
	public void setState(String state) {
		State = state;
	}
	public String getCity() {
		return City;
	}
	public void setCity(String city) {
		City = city;
	}
	public String getDistrict() {
		return District;
	}
	public void setDistrict(String district) {
		District = district;
	}
	private String ZipCode;
	private String Email;
	private String FaxNo;
	private String Tele;
	private String Mobile;
	private String LinkMan;
	private int GradeID;
	private Date CreateTime;
	private String Creator;
	private Date ModiTime;
	private String ModiID;
	private int Status;
	private String Note;
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getName() {
		return Name!=null?Name.trim():null;
	}
	public void setName(String name) {
		Name = name.trim();
	}
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public int getGroupID() {
		return GroupID;
	}
	public void setGroupID(int groupID) {
		GroupID = groupID;
	}
	public int getDeliveryGroupID() {
		return DeliveryGroupID;
	}
	public void setDeliveryGroupID(int deliveryGroupID) {
		DeliveryGroupID = deliveryGroupID;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getZipCode() {
		return ZipCode;
	}
	public void setZipCode(String zipCode) {
		ZipCode = zipCode;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getFaxNo() {
		return FaxNo;
	}
	public void setFaxNo(String faxNo) {
		FaxNo = faxNo;
	}
	public String getTele() {
		return Tele;
	}
	public void setTele(String tele) {
		Tele = tele;
	}

	public String getMobile() {
		return Mobile;
	}
	public void setMobile(String mobile) {
		Mobile = mobile;
	}
	public String getLinkMan() {
		return LinkMan;
	}
	public void setLinkMan(String linkMan) {
		LinkMan = linkMan;
	}
	public int getGradeID() {
		return GradeID;
	}
	public void setGradeID(int gradeID) {
		GradeID = gradeID;
	}
	@JsonSerialize(using=JsonDateSerializer.class)
	public Date getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(Date createTime) {
		CreateTime = createTime;
	}
	public String getCreator() {
		return Creator;
	}
	public void setCreator(String creator) {
		Creator = creator;
	}
	@JsonSerialize(using=JsonDateSerializer.class)
	public Date getModiTime() {
		return ModiTime;
	}
	public void setModiTime(Date modiTime) {
		ModiTime = modiTime;
	}
	public String getModiID() {
		return ModiID;
	}
	public void setModiID(String modiID) {
		ModiID = modiID;
	}
	public int getStatus() {
		return Status;
	}
	public void setStatus(int status) {
		Status = status;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	
	public String getPrintContent1() {
		return PrintContent1;
	}
	public void setPrintContent1(String printContent1) {
		PrintContent1 = printContent1;
	}
	public String getPrintContent2() {
		return PrintContent2;
	}
	public void setPrintContent2(String printContent2) {
		PrintContent2 = printContent2;
	}
	public String getPrintContent3() {
		return PrintContent3;
	}
	public void setPrintContent3(String printContent3) {
		PrintContent3 = printContent3;
	}
	public String getPrintContent4() {
		return PrintContent4;
	}
	public void setPrintContent4(String printContent4) {
		PrintContent4 = printContent4;
	}
	public String getPrintContent5() {
		return PrintContent5;
	}
	public void setPrintContent5(String printContent5) {
		PrintContent5 = printContent5;
	}
	public String getPrintContent6() {
		return PrintContent6;
	}
	public void setPrintContent6(String printContent6) {
		PrintContent6 = printContent6;
	}
	public String getPrintContent7() {
		return PrintContent7;
	}
	public void setPrintContent7(String printContent7) {
		PrintContent7 = printContent7;
	}
	public String getPrintContent8() {
		return PrintContent8;
	}
	public void setPrintContent8(String printContent8) {
		PrintContent8 = printContent8;
	}
	public String getPrintContent9() {
		return PrintContent9;
	}
	public void setPrintContent9(String printContent9) {
		PrintContent9 = printContent9;
	}
	public String getPrintContent10() {
		return PrintContent10;
	}
	public void setPrintContent10(String printContent10) {
		PrintContent10 = printContent10;
	}
	public void setLevel(int level) {
		Level = level;
	}
	public int getLevel() {
		return Level;
	}
	
	
	
}
