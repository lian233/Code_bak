package com.wofu.ecommerce.dangdang;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;


import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;

import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;

public class SynStockExecuter extends Executer {
	private String url="";
	private String tradecontactid="";
	private String username="";
	private String jobname = "" ;
	private String encoding = "" ;
	private static String session="";
	private static String app_key = "";
	private static String app_Secret = "";
	
	public void run() {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		url=prop.getProperty("url");
		tradecontactid=prop.getProperty("tradecontactid");
		username=prop.getProperty("username");
		jobname=prop.getProperty("jobname");
		encoding=prop.getProperty("encoding");
		session = prop.getProperty("session") ;
		app_key = prop.getProperty("app_key") ;
		app_Secret = prop.getProperty("app_Secret") ;
	
		try {		
			
			updateJobFlag(1);
			
			synStock();
			
			UpdateTimerJob();
			
			Log.info(jobname, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
	
		} catch (Exception e) {
			try {
				
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
				
				//updateJobFlag(0);
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobname,"回滚事务失败");
			}
			Log.error(jobname,"错误信息:"+Log.getErrorMessage(e));
			
			
			Log.error(jobname, "执行作业失败 [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobname,"更新处理标志失败");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobname,"关闭数据库连接失败");
			}
		}
		
		
	}
	
	private void synStock() throws Exception	
	{
		//店铺同步比例
		double synrate=1;
		Log.info(username,"开始同步商品库存");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		
		sql="update ecs_stockconfig set errflag=0,errmsg='' where orgid="+orgid;
		this.getDao().execute(sql);
		
		sql="update ecs_stockconfigsku set errflag=0,errmsg='' where orgid="+orgid;
		this.getDao().execute(sql);
		//店铺同步比例表
		sql  = "select synrate,isNeedSyn from ecs_shopglobalconfig where shopOrgId="+orgid;
		Hashtable tab = this.getDao().oneRowSelect(sql);
		if(tab!=null && tab.size()>0){
			Log.info("isNeedSyn: "+tab.get("isNeedSyn"));
			String isNeedSyn = tab.get("isNeedSyn").toString();
			Log.info("isNeedSyn: "+tab.get("synrate"));
			String temp = tab.get("synrate").toString();
			if(!"".equals(temp))
			synrate = Float.parseFloat(temp);
			if("0".equals(isNeedSyn)) {
				Log.info(username,"整店不需要同步库存");
				return;
			}
		}
		
		StringBuffer updateItemsXML=new StringBuffer();
		
		int n=0;
		
		for (int k=0;k<5;)
		{
			try
			{
				sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid;
				Vector vtstockconfig=this.getDao().multiRowSelect(sql);
				
				for(int i=0;i<vtstockconfig.size();i++)
				{
					try{
						Hashtable htstockconfig=(Hashtable) vtstockconfig.get(i);
						
						ECS_StockConfig stockconfig=new ECS_StockConfig();
						stockconfig.getMapData(htstockconfig);
						
						Log.info("商品ID:"+stockconfig.getItemid()+" 货号:"+stockconfig.getItemcode());
							
						if (stockconfig.getIsneedsyn()==0)
						{
							Log.info(username,"配置不需要同步库存,货号:"+stockconfig.getItemcode());
							continue;  //不需要同步
						}
			
						sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
						
						Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
						
						for(int j=0;j<vtstockconfigsku.size();j++)
						{
							try{
								Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
								
								ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
								stockconfigsku.getMapData(htstockconfigsku);
										
								Log.info(username,"SKU:"+stockconfigsku.getSku()+" 原库存:"+stockconfigsku.getStockcount());
									
								boolean ismulti=false;
								boolean isfind=true;
								
								sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
								if (this.getDao().intSelect(sql)==0)
								{
									sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
									if (this.getDao().intSelect(sql)==0)
									{
										Log.warn(username,"找不到SKU【"+stockconfigsku.getSku()+"】对应的条码,商品标题:"+stockconfig.getTitle());	
										stockconfigsku.setErrflag(1);
										stockconfigsku.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
										this.getDao().updateByKeys(stockconfigsku, "orgid,itemid,skuid");
										
										stockconfig.setErrflag(1);
										stockconfig.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
										this.getDao().updateByKeys(stockconfig, "orgid,itemid");
										isfind=false;
									}else							
										ismulti=true;
								}
								
								int qty =0;
								
								if(isfind)
								{
									if (ismulti)
									{
										int minqty=1000000;
										sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
										Vector multiskulist=this.getDao().multiRowSelect(sql);
										for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
										{
											Hashtable skuref=(Hashtable) itmulti.next();
											String customercode= skuref.get("customercode").toString();
											double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
											qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
											
											qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
											
											if (qty<minqty)
											{
												minqty=qty;
											}
										}
										
										qty=minqty;
									}
									else
									{
										qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfigsku.getSku());
									}
									
									if (qty<0) qty=0;	
								}
						
								int addstockqty=0;
								if(Math.abs(stockconfig.getAddstockqty())<1)
									addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
								else
									addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
								
								//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
								if ((qty+addstockqty)<=stockconfig.getAlarmqty())
								{
									qty=0;
								}
								else
								{
									qty=qty+addstockqty;
								}
								
								if(synrate!=1){
								qty = Double.valueOf(Math.floor(qty*synrate)).intValue();
								}
								//sku同步比例
								qty = Double.valueOf(Math.floor(qty*stockconfigsku.getSynrate())).intValue();
								if(qty<0) qty=0;
								updateItemsXML.append("<ItemUpadteInfo>");							
								updateItemsXML.append("<outerItemID>"+stockconfigsku.getSku()+"</outerItemID>");
								updateItemsXML.append("<stockCount>"+qty+"</stockCount>");
								updateItemsXML.append("</ItemUpadteInfo>");		
								n=n+1;
								if (n>=20)
								{
									submitDangdangData(orgid,url,encoding,updateItemsXML.toString());				
									updateItemsXML.delete(0, updateItemsXML.length());
									n=0;
								}
							}catch(Exception ex){
								Log.error(jobname, ex.getMessage());
								if (this.getConnection() != null && !this.getConnection().getAutoCommit())
									this.getConnection().rollback();
								
								if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
									this.getExtconnection().rollback();
								
							}
							
						}
						k=0;
					}catch(Exception ex){
						Log.error(jobname, ex.getMessage());
						if (this.getConnection() != null && !this.getConnection().getAutoCommit())
							this.getConnection().rollback();
						
						if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
							this.getExtconnection().rollback();
					}
					
				}
			
				if (updateItemsXML.length()>0)
				{
					submitDangdangData(orgid,url,encoding,updateItemsXML.toString());				
				}
				
				break;
			
			} catch (Exception e) {
				if (++k >= 5)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				Log.warn(jobname+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 
		}
		Log.info(username,"同步商品库存任务完成!");
		
	}
	
	private void submitDangdangData(int orgid,String url,String encoding,String updateItemsstr) throws Exception
	{
		StringBuffer updateItemsXML=new StringBuffer();
		updateItemsXML.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		updateItemsXML.append("<request><functionID>dangdang.items.stock.update</functionID>");
		updateItemsXML.append("<time>"+Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)+"</time>");
		updateItemsXML.append("<ItemsList>");
		updateItemsXML.append(updateItemsstr);
		updateItemsXML.append("</ItemsList>");
		updateItemsXML.append("</request>");
		
		StockUtils.batchUpdateStock(this.getDao().getConnection(),orgid,url,updateItemsXML.toString(),encoding,session,app_key,app_Secret);
	}
	
}