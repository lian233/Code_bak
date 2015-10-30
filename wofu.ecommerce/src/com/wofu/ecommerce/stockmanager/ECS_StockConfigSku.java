package com.wofu.ecommerce.stockmanager;

import com.wofu.base.util.BusinessObject;
import com.wofu.common.json.JSONObject;
public class ECS_StockConfigSku extends BusinessObject {
	
	private int orgid;
	private String itemid;
	private String skuid;
	private String sku;
	private int stockcount;
	private int errflag;
	private String errmsg;
	private float synrate;
	
	public int getOrgid() {
		return orgid;
	}
	public void setOrgid(int orgid) {
		this.orgid = orgid;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public String getSkuid() {
		return skuid;
	}
	public void setSkuid(String skuid) {
		this.skuid = skuid;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public int getStockcount() {
		return stockcount;
	}
	public void setStockcount(int stockcount) {
		this.stockcount = stockcount;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	public int getErrflag() {
		return errflag;
	}
	public void setErrflag(int errflag) {
		this.errflag = errflag;
	}
	
	public float getSynrate() {
		return synrate;
	}
	public void setSynrate(float synrate) {
		this.synrate = synrate;
	}
	//删除特定的不存在的skus
	public void delete() throws Exception{
		this.setObjValue(this, new JSONObject(getReqData()));
		this.getDao().deleteByKeys(this, "orgid,itemid,skuid");
		/**
		String sql = new StringBuilder().append("select * from ecs_stockconfigsku where orgid=")
			.append(this.getOrgid()).append(" and itemid='").append(this.getItemid()).append("'").toString();
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
		**/
	}
	
	
}
