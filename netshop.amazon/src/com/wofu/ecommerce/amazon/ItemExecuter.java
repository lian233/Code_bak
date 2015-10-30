package com.wofu.ecommerce.amazon;


import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.business.stock.StockManager;

import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;



public class ItemExecuter extends Executer {

	private static String serviceurl = "https://mws.amazonservices.com.cn/";

	private static String accesskeyid = "AKIAJARSU7LXSIVYWLHA";

	private static String secretaccesskey = "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";

	private static String applicationname = "javaAmazonclient";

	private static String applicationversion = "1.0";

	private static String sellerid = "AHKPACOI0LTRR";

	private static String marketplaceid = "AAHKV2X7AFYLW";
	
	private static String tradecontactid = "" ;
	
	private static String jobname="��ʱȡ����ѷ��Ʒ";

	@Override
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
		
		try
		{
			String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
			int orgid=this.getDao().intSelect(sql);
			updateJobFlag(1);
			
			int m=0;
				
			
			List skulist=AmazonUtil.getSkuInfo(serviceurl,accesskeyid,
					secretaccesskey,applicationname,applicationversion,
					sellerid,marketplaceid);
			for(int i=0;i<skulist.size();i++)
			{
				Map skuinfo=(Map) skulist.get(i);
				
				String stockqty=skuinfo.get("qty").toString();
				if (stockqty.equals("") || stockqty==null) stockqty="0";
				
				String sku=skuinfo.get("sku").toString();
				int qty=Integer.valueOf(stockqty).intValue();
				
				
				StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid).intValue(),sku,sku,sku,qty) ;
				
				StockManager.addStockConfigSku(this.getDao(),orgid,sku,sku,sku,qty) ;
				
				Log.info("ȡ��SKU:"+sku);

			}

			Log.info("ȡ����SKU��:"+String.valueOf(m));
			
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
				TimerRunner.modifiedErrVect(this.getExecuteobj().getId());
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
		
}
