package com.wofu.ecommerce.yhd;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;

import com.wofu.base.job.Executer;

public class SynStockExecuter extends Executer {
	
	private String url="";

	private String app_key = "";

	private String token  = "";
	
	private String app_secret="";
	
	private String format="";
	
	private String ver="";

	private String tradecontactid="";

	private String username="";
	
	private static String jobName="定时更新一号店库存";
	

	public void run() {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		format=prop.getProperty("format");
		ver=prop.getProperty("ver");
		tradecontactid=prop.getProperty("tradecontactid");

		token=prop.getProperty("token");
		app_key=prop.getProperty("app_key");
		username=prop.getProperty("username");
		app_secret=prop.getProperty("app_secret");

		try {		
			
			updateJobFlag(1);
			
			synStock();
	
			UpdateTimerJob();
			
			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
	
		} catch (Exception e) {
			try {
				
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));

				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"回滚事务失败");
			}
			Log.error(jobName,"错误信息:"+Log.getErrorMessage(e));
			
			Log.error(jobName, "执行作业失败 [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName,"更新处理标志失败");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"关闭数据库连接失败");
			}
		}
		
	
	
	}
	

	private void synStock() throws Exception
	{
		//店铺同步比例
		double synrate=1;
		Log.info(username,"开始同步商品库存");
		
		StringBuffer updateItemsXML=new StringBuffer();
		
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		
		sql="update ecs_stockconfig set errflag=0,errmsg='' where orgid="+orgid;
		this.getDao().execute(sql);
		
		sql="update ecs_stockconfigsku set errflag=0,errmsg='' where orgid="+orgid;
		this.getDao().execute(sql);
		//店铺同步比例表
		sql  = "select synrate from ecs_shopglobalconfig where shopOrgId="+orgid;
		String temp = this.getDao().strSelect(sql);
		if(!"".equals(temp))
		synrate = Float.parseFloat(temp);
		int n=0;
		
		for (int k=0;k<10;)
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
						
						String itemid=stockconfig.getItemid();
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
										/**
										stockconfigsku.setErrflag(1);
										stockconfigsku.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
										this.getDao().updateByKeys(stockconfigsku, "orgid,itemid,skuid");
										*/
										this.getDao().deleteByKeys(stockconfigsku, "orgid,itemid,skuid");
										
										stockconfig.setErrflag(1);
										stockconfig.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
										this.getDao().updateByKeys(stockconfig, "orgid,itemid");
										
										isfind=false;
									}else							
										ismulti=true;
								}
								
								int qty =0;
								
								if (isfind)
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
								
								qty = Double.valueOf(Math.floor(qty*synrate)).intValue();
								
								Object[] skuid=StringUtil.split(stockconfigsku.getSkuid(),"-").toArray();
								
								updateItemsXML.append(stockconfigsku.getSku()+":"+String.valueOf(skuid[1])+":"+qty+",");
								
								n=n+1;
								
								if (n>=50){
																	
									String outerstocklist=updateItemsXML.toString();
									outerstocklist=outerstocklist.substring(0, outerstocklist.length()-1);
									StockUtils.batchUpdateStock(this.getDao(),orgid,url,app_key,token,app_secret,
											format,outerstocklist,ver,1);
									
									updateItemsXML.delete(0, updateItemsXML.length());
									
									n=0;
								}
							}catch(Exception ex){
								if (this.getConnection() != null && !this.getConnection().getAutoCommit())
									this.getConnection().rollback();
								
								if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
									this.getExtconnection().rollback();
								Log.error(jobName, ex.getMessage());
							}
							
						}
						
						k=0;
					}catch(Exception ex){
						if (this.getConnection() != null && !this.getConnection().getAutoCommit())
							this.getConnection().rollback();
						
						if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
							this.getExtconnection().rollback();
						Log.error(jobName, ex.getMessage());
					}
					
				}
				
				String outerstocklist=updateItemsXML.toString();
				if (!outerstocklist.equals(""))
				{
					outerstocklist=outerstocklist.substring(0, outerstocklist.length()-1);
					StockUtils.batchUpdateStock(this.getDao(),orgid,url,app_key,token,app_secret,
							format,outerstocklist,ver,1);
					updateItemsXML.delete(0, updateItemsXML.length());
				}	
				
				break;
			
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				Log.warn(jobName+", 远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 
		}
	}
	
	
	
}
