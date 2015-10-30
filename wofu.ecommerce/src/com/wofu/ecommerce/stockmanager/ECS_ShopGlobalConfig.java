package com.wofu.ecommerce.stockmanager;

import java.util.Properties;

import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

/**
 * 店级别库存同步全局配置
 * @author Administrator
 *
 */
public class ECS_ShopGlobalConfig extends PageBusinessObject{
	private int alarmQty;
	private int isNeedSyn;
	private double synrate;
	private int shopOrgId;
	private int serialid;
	public void update() throws Exception{
		try{
			this.getJSONData();
			for (int i=0;i<this.getEcsshopglobalconfigofecsshopglobalconfigs().getRelationData().size();i++)
			{
				ECS_ShopGlobalConfig shopGlobalConfig=(ECS_ShopGlobalConfig) this.getEcsshopglobalconfigofecsshopglobalconfigs().getRelationData().get(i);
				Log.info("同步比例："+shopGlobalConfig.getSynrate()+"警界："+shopGlobalConfig.getAlarmQty()+"是否同步:"+shopGlobalConfig.getIsNeedSyn()+"orgid: "+shopGlobalConfig.getShopOrgId());
				this.getDao().updateByKeys(shopGlobalConfig, ",serialid,shopOrgId");
				String sql = new StringBuilder().append("update tradecontacts  set tradecontacts.defaultalarmqty=").append(shopGlobalConfig.getAlarmQty())
				.append( " from ecs_tradecontactorgcontrast a where a.tradecontactid=tradecontacts.tradecontactid and  a.orgid= ").append(shopGlobalConfig.getShopOrgId()).toString();
				this.getDao().execute(sql);
				sql = new StringBuilder().append("update ecs_stockconfig set alarmqty=")
					.append(shopGlobalConfig.getAlarmQty())
					.append(" where orgid=").append(shopGlobalConfig.getShopOrgId()).toString();
				this.getDao().execute(sql);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		
	}
	
	public void insert() throws Exception{
		StringBuilder sqlwhere=new StringBuilder("select * from ecs_shopglobalconfig where shopOrgId in(");
		try{
			this.getJSONData();
			for(int i=0;i<this.getEcsshopglobalconfigofecsshopglobalconfigs().getRelationData().size();i++){
				ECS_ShopGlobalConfig shopGlobalConfig = (ECS_ShopGlobalConfig)this.getEcsshopglobalconfigofecsshopglobalconfigs().getRelationData().get(i);
				this.getDao().insert(shopGlobalConfig,"serialid");
				String sql = new StringBuilder().append("update tradecontacts  set tradecontacts.defaultalarmqty=").append(shopGlobalConfig.getAlarmQty())
				.append( " from ecs_tradecontactorgcontrast a where a.tradecontactid=tradecontacts.tradecontactid and  a.orgid= ").append(shopGlobalConfig.getShopOrgId()).toString();
				this.getDao().execute(sql);
				sql = new StringBuilder().append("update ecs_stockconfig set alarmqty=")
					.append(shopGlobalConfig.getAlarmQty()).append(",isneedsyn=").append(shopGlobalConfig.getIsNeedSyn())
					.append(" where orgid=").append(shopGlobalConfig.getShopOrgId()).toString();
				this.getDao().execute(sql);
				sqlwhere.append("'").append(shopGlobalConfig.getShopOrgId()).append("',");
			}
			sqlwhere.delete(sqlwhere.length()-1, sqlwhere.length());
			sqlwhere.append(")");
			Log.info("插入成功");
			this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sqlwhere.toString())));
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	public void synglobalshop() throws Exception{
		try{
			String reqData = this.getReqData();
			Properties p = StringUtil.getIniProperties(reqData);
			String orgId = p.getProperty("orgid");
			String tradecontactid = this.getDao().strSelect("select tradecontactid from ecs_tradecontactorgcontrast where orgid="+orgId);
			String sql = new StringBuilder().append("update ecs_timerpolicy set nextactive=getdate() where Executer like '%Syn%' and ")
				.append("params like '%tradecontactid=").append(tradecontactid).append("'").toString();
			this.getDao().execute(sql);
			Log.info("开启同步成功");
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	public int getSerialid() {
		return serialid;
	}
	public void setSerialid(int serialid) {
		this.serialid = serialid;
	}
	private DataRelation ecsshopglobalconfigofecsshopglobalconfigs =new DataRelation("ecsshopglobalconfigofecsshopglobalconfig","com.wofu.ecommerce.stockmanager.ECS_ShopGlobalConfig");

	
	public DataRelation getEcsshopglobalconfigofecsshopglobalconfigs() {
		return ecsshopglobalconfigofecsshopglobalconfigs;
	}
	public void setEcsshopglobalconfigofecsshopglobalconfigs(
			DataRelation ecsshopglobalconfigofecsshopglobalconfigs) {
		this.ecsshopglobalconfigofecsshopglobalconfigs = ecsshopglobalconfigofecsshopglobalconfigs;
	}
	public int isNeedSyn() {
		return isNeedSyn;
	}
	public void setNeedSyn(int isNeedSyn) {
		this.isNeedSyn = isNeedSyn;
	}
	public int getAlarmQty() {
		return alarmQty;
	}
	public void setAlarmQty(int alarmQty) {
		this.alarmQty = alarmQty;
	}
	public double getSynrate() {
		return synrate;
	}
	public void setSynrate(double synrate) {
		this.synrate = synrate;
	}
	public int getShopOrgId() {
		return shopOrgId;
	}
	public void setShopOrgId(int shopOrgId) {
		this.shopOrgId = shopOrgId;
	}
	
	
	public int getIsNeedSyn() {
		return isNeedSyn;
	}
	public void setIsNeedSyn(int isNeedSyn) {
		this.isNeedSyn = isNeedSyn;
	}


	
	
	
	
}
