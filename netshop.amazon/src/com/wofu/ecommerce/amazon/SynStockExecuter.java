package com.wofu.ecommerce.amazon;


import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;

import com.wofu.base.job.Executer;

public class SynStockExecuter extends Executer {
	
	private static String serviceurl = "https://mws.amazonservices.com.cn/";

	private static String accesskeyid = "AKIAJARSU7LXSIVYWLHA";

	private static String secretaccesskey = "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";

	private static String applicationname = "javaAmazonclient";

	private static String applicationversion = "1.0";

	private static String sellerid = "AHKPACOI0LTRR";

	private static String marketplaceid = "AAHKV2X7AFYLW";
	
	private static String tradecontactid = "" ;
	
	private static String jobname="��ʱͬ������ѷ���";
	
	public void run() {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		serviceurl = prop.getProperty("serviceurl") ;
		accesskeyid = prop.getProperty("accesskeyid") ;
		secretaccesskey = prop.getProperty("secretaccesskey") ;
		applicationname = prop.getProperty("applicationname") ;
	
		applicationversion = prop.getProperty("applicationversion") ;
		sellerid = prop.getProperty("sellerid") ;
		marketplaceid = prop.getProperty("marketplaceid") ;
	
		tradecontactid = prop.getProperty("tradecontactid") ;

		
		
		try {
			updateJobFlag(1);
			
			synStock();
			
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
				
				//updateJobFlag(0);
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit()){
					this.getConnection().rollback();
					this.getConnection().setAutoCommit(true);
				}
					
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit()){
					this.getExtconnection().rollback();
					this.getExtconnection().setAutoCommit(true);
				}
				
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
	
	private void synStock() throws Exception
	{

		int num=0;
		//����ͬ������
		double synrate=1;
		Log.info("��ʼͬ����Ʒ���");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		
		sql="update ecs_stockconfig set errflag=0,errmsg='' where orgid="+orgid;
		this.getDao().execute(sql);
		
		sql="update ecs_stockconfigsku set errflag=0,errmsg='' where orgid="+orgid;
		this.getDao().execute(sql);
		
		//����ͬ��������
		sql  = "select synrate from ecs_shopglobalconfig where shopOrgId="+orgid;
		String temp = this.getDao().strSelect(sql);
		if(!"".equals(temp))
		synrate = Float.parseFloat(temp);
		Vector inventoryitems=new Vector();
		
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
						Log.info("���ò���Ҫͬ�����,����:"+stockconfig.getItemcode());
						continue;  //����Ҫͬ��
					}
		
					sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
					
					Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
					
					for(int j=0;j<vtstockconfigsku.size();j++)
					{
						Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
						
						ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
						stockconfigsku.getMapData(htstockconfigsku);
								
						Log.info("SKU:"+stockconfigsku.getSku()+" ԭ���:"+stockconfigsku.getStockcount());
							
						boolean ismulti=false;
						boolean isfind=true;
						
						sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
						if (this.getDao().intSelect(sql)==0)
						{
							sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
							if (this.getDao().intSelect(sql)==0)
							{
								Log.warn("�Ҳ���SKU��"+stockconfigsku.getSku()+"����Ӧ������,��Ʒ����:"+stockconfig.getTitle());		
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
						
						//������ÿ����������ӵĿ��С�ڵ��ھ�����,�򽫿��ͬ��Ϊ0
						if ((qty+addstockqty)<=stockconfig.getAlarmqty())
						{
							qty=0;
						}
						else
						{
							qty=qty+addstockqty;
						}
						
						Log.info("old qty: "+qty);
						qty = Double.valueOf(Math.floor(qty*synrate)).intValue();
						Log.info("new qty: "+qty);
													
						Hashtable inventoryitem=new Hashtable();
						inventoryitem.put("sku",stockconfigsku.getSku());
						inventoryitem.put("qty", String.valueOf(qty));
						
						inventoryitems.add(inventoryitem);
						num=num+1;
						
						if(num>=500)
						{
							StockUtils.updateStock(this.getDao(),orgid,serviceurl, accesskeyid, secretaccesskey, 
									applicationname, applicationversion, 
									sellerid, marketplaceid, inventoryitems);
							num=0;
							inventoryitems.clear();
						}
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
		
		if (inventoryitems.size()>0)			
			StockUtils.updateStock(this.getDao(),orgid,serviceurl, accesskeyid, secretaccesskey, 
				applicationname, applicationversion, 
				sellerid, marketplaceid, inventoryitems);
		
		Log.info("ͬ�����SKU����:"+String.valueOf(num));
	}
	
	
}