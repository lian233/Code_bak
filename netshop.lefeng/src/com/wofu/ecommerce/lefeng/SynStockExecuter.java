package com.wofu.ecommerce.lefeng;


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

	private String encoding="";

	private String shopid="";

	private String secretKey="";

	private String tradecontactid="";


	private String username="";
	
	private String methodApi="querySkuInfoByParams";
	
	private static String jobname="定时同步乐峰库存";

	public void execute() throws Exception {

		Properties prop=StringUtil.getStringProperties( this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		encoding=prop.getProperty("encoding");
		shopid=prop.getProperty("shopid");
		secretKey=prop.getProperty("secretKey");
		tradecontactid=prop.getProperty("tradecontactid");

		username=prop.getProperty("username");

		
		
		try 
		{	
			updateJobFlag(1);
	
			SynStock();
	
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

				updateJobFlag(0);
				
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
	
	private void SynStock() throws Exception
	{
		Log.info(username,"开始同步商品库存");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		
		sql="update ecs_stockconfig set errflag=0,errmsg='' where orgid="+orgid;
		this.getDao().execute(sql);
		
		sql="update ecs_stockconfigsku set errflag=0,errmsg='' where orgid="+orgid;
		this.getDao().execute(sql);
		
		for (int k=0;k<10;)
		{
			try
			{
				sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid;
				Vector vtstockconfig=this.getDao().multiRowSelect(sql);
				
				for(int i=0;i<vtstockconfig.size();i++)
				{
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
													
						StockUtils.updateStock(this.getDao(),url, shopid, secretKey, encoding, stockconfig, stockconfigsku,qty, 0);
					}
					
					
					k=0;
				}
			
				break;
			
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 
		}
		
	}
	
	
}
