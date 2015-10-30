package com.wofu.retail.baseinformation;
import java.util.ArrayList;
import java.util.Date;
import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.JException;
public class ECS_Brand extends PageBusinessObject {
	private int brandid;
	private String customid;
	private String shortname;
	private String name;
	private String note;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private int merchantid;

	private DataRelation ecsbrandofecsbrands =new DataRelation("ecsbrandofecsbrand","com.wofu.retail.baseinformation.ECS_Brand");
	
	
	public ECS_Brand()
	{
		this.searchOrderFieldName="createtime";
		this.uniqueFields1="name";
	}
	public void delete() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecsbrandofecsbrands.getRelationData().size();i++)
		{
			ECS_Brand brand=(ECS_Brand) this.ecsbrandofecsbrands.getRelationData().get(i);
						
			String sql="select count(*) from ecs_item where brandid="+brand.getBrandid();
			if (this.getDao().intSelect(sql)>0)
				throw new JException("品牌:【"+brand.getName()+"】已被使用,不能删除!");
			
			this.getDao().delete(brand);
		}
	}
	
	public void insert() throws Exception
	{
			
		this.getJSONData();

		ArrayList list=new ArrayList();
		for (int i=0;i<this.ecsbrandofecsbrands.getRelationData().size();i++)
		{
			ECS_Brand brand=(ECS_Brand) this.ecsbrandofecsbrands.getRelationData().get(i);
						
			//检查唯一性
			this.checkUnique(this);

			
			brand.brandid=this.getDao().IDGenerator(brand, "brandid");
			brand.creator=this.getUserInfo().getName();
			brand.createtime=new Date();			
			brand.updator=this.getUserInfo().getName();
			brand.updatetime=new Date();
			brand.note=brand.name;
			brand.merchantid=this.getUserInfo().getMerchantid();
			this.getDao().insert(brand);
			list.add(brand);			
		}
		this.OutputStr(this.toJSONArray(list));
	
	}
	public void update() throws Exception
	{
		this.getJSONData();
		for (int i=0;i<this.ecsbrandofecsbrands.getRelationData().size();i++)
		{
			ECS_Brand brand=(ECS_Brand) this.ecsbrandofecsbrands.getRelationData().get(i);
						
			//检查唯一性
			this.checkUnique(this);
			
			brand.updator=this.getUserInfo().getLogin();
			brand.updatetime=new Date();
			this.getDao().update(brand);
		}
	}
	
	public int getBrandid() {
		return brandid;	
	}
	public void setBrandid(int brandid) {
		this.brandid = brandid;
	}
	public String getCustomid() {
		return customid;	
	}
	public void setCustomid(String customid) {
		this.customid = customid;
	}
	public String getShortname() {
		return shortname;	
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public String getName() {
		return name;	
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNote() {
		return note;	
	}
	public void setNote(String note) {
		this.note = note;
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
	public DataRelation getEcsbrandofecsbrands() {
		return ecsbrandofecsbrands;
	}
	public void setEcsbrandofecsbrands(DataRelation ecsbrandofecsbrands) {
		this.ecsbrandofecsbrands = ecsbrandofecsbrands;
	}
}
