package com.wofu.retail.baseinformation;

import java.util.ArrayList;
import java.util.Date;

import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.JException;

public class ECS_Supplier extends PageBusinessObject {
	private int sid;
	private String name;
	private String linker;
	private String telephone;
	private String fax;
	private String mobilephone;
	private String address;
	private String email;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private int merchantid;
	private String customid;
	
	private DataRelation ecssupplierofecssuppliers =new DataRelation("ecssupplierofecssupplier","com.wofu.retail.baseinformation.ECS_Supplier");
	
	public ECS_Supplier()
	{
		this.searchOrderFieldName="createtime";
		this.uniqueFields1="name";
	}
	public void delete() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecssupplierofecssuppliers.getRelationData().size();i++)
		{
			ECS_Supplier supplier=(ECS_Supplier) this.ecssupplierofecssuppliers.getRelationData().get(i);
			
			String sql="select count(*) from ecs_itemsupplier where supplierid="+supplier.getSid();
			if (this.getDao().intSelect(sql)>0)
				throw new JException("供应商:【"+supplier.getName()+"】已被使用,不能删除!");
			
			this.getDao().delete(supplier);
		}
	}
	
	public void insert() throws Exception
	{
			
		this.getJSONData();

		ArrayList list=new ArrayList();
		for (int i=0;i<this.ecssupplierofecssuppliers.getRelationData().size();i++)
		{
			ECS_Supplier supplier=(ECS_Supplier) this.ecssupplierofecssuppliers.getRelationData().get(i);
			
			//检查唯一性
			this.checkUnique(this);

			
			supplier.sid=this.getDao().IDGenerator(supplier, "sid");
			supplier.creator=this.getUserInfo().getName();
			supplier.createtime=new Date();			
			supplier.updator=this.getUserInfo().getName();
			supplier.updatetime=new Date();
			supplier.merchantid=this.getUserInfo().getMerchantid();
			this.getDao().insert(supplier);
			list.add(supplier);
			
		}
		this.OutputStr(this.toJSONArray(list));
	
	}
	public void update() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecssupplierofecssuppliers.getRelationData().size();i++)
		{
			ECS_Supplier supplier=(ECS_Supplier) this.ecssupplierofecssuppliers.getRelationData().get(i);
			
			//检查唯一性
			this.checkUnique(this);
			
			supplier.updator=this.getUserInfo().getLogin();
			supplier.updatetime=new Date();
			this.getDao().update(supplier);
		}
	}

	
	public void getSupplier() throws Exception
	{
		String sql="select sid,name from ecs_supplier order by sid";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}

	public int getSid() {
		return sid;	
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public String getName() {
		return name;	
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLinker() {
		return linker;	
	}
	public void setLinker(String linker) {
		this.linker = linker;
	}
	public String getTelephone() {
		return telephone;	
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getFax() {
		return fax;	
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getMobilephone() {
		return mobilephone;	
	}
	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}
	public String getAddress() {
		return address;	
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;	
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCreator() {
		return creator;	
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreatetime() {
		return createtime;	
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getUpdator() {
		return updator;	
	}
	public void setUpdator(String updator) {
		this.updator = updator;
	}
	public Date getUpdatetime() {
		return updatetime;	
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public int getMerchantid() {
		return merchantid;	
	}
	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}
	public String getCustomid() {
		return customid;	
	}
	public void setCustomid(String customid) {
		this.customid = customid;
	}
	public DataRelation getEcssupplierofecssuppliers() {
		return ecssupplierofecssuppliers;
	}
	public void setEcssupplierofecssuppliers(DataRelation ecssupplierofecssuppliers) {
		this.ecssupplierofecssuppliers = ecssupplierofecssuppliers;
	}

}
