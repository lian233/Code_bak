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
	
	private static String jobname="��ʱͬ���ַ���";

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
			
			Log.info(jobname, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
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
				Log.error(jobname,"�ع�����ʧ��");
			}
			Log.error(jobname,"������Ϣ:"+Log.getErrorMessage(e));
			
			
			Log.error(jobname, "ִ����ҵʧ�� [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobname,"���´����־ʧ��");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobname,"�ر����ݿ�����ʧ��");
			}
		}
		
	}
	
	private void SynStock() throws Exception
	{
		Log.info(username,"��ʼͬ����Ʒ���");
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
					
					
					Log.info("��ƷID:"+stockconfig.getItemid()+" ����:"+stockconfig.getItemcode());
						
										
					if (stockconfig.getIsneedsyn()==0)
					{
						Log.info(username,"���ò���Ҫͬ�����,����:"+stockconfig.getItemcode());
						continue;  //����Ҫͬ��
					}
		
					sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
					
					Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
					
					for(int j=0;j<vtstockconfigsku.size();j++)
					{
						Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
						
						ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
						stockconfigsku.getMapData(htstockconfigsku);
								
						Log.info(username,"SKU:"+stockconfigsku.getSku()+" ԭ���:"+stockconfigsku.getStockcount());
							
						boolean ismulti=false;
						boolean isfind=true;
						
						sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
						if (this.getDao().intSelect(sql)==0)
						{
							sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
							if (this.getDao().intSelect(sql)==0)
							{
								Log.warn(username,"�Ҳ���SKU��"+stockconfigsku.getSku()+"����Ӧ������,��Ʒ����:"+stockconfig.getTitle());	
								stockconfigsku.setErrflag(1);
								stockconfigsku.setErrmsg("�Ҳ���SKU��"+stockconfigsku.getSku()+"����Ӧ������");
								this.getDao().updateByKeys(stockconfigsku, "orgid,itemid,skuid");
								
								stockconfig.setErrflag(1);
								stockconfig.setErrmsg("�Ҳ���SKU��"+stockconfigsku.getSku()+"����Ӧ������");
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
						
						//������ÿ����������ӵĿ��С�ڵ��ھ�����,�򽫿��ͬ��Ϊ0
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
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 
		}
		
	}
	
	
}
