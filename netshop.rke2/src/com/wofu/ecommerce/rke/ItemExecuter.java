package com.wofu.ecommerce.rke;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.rke.utils.Utils;
public class ItemExecuter extends Executer{
	
	private static String jobName = "ȡ��˹��������Ʒ������ҵ";
	
	private String url="";

	private String pageSize  = "";
	
	private String username="";
	
	private String format="";
	
	private String ver="";

	private String tradecontactid="";



	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		ver=prop.getProperty("ver");
		tradecontactid=prop.getProperty("tradecontactid");
		pageSize=prop.getProperty("pageSize");
		username=prop.getProperty("username");
		
		try {		
			updateJobFlag(1);
			
			getOnSaleItems();
			getInStockItems();
	
			UpdateTimerJob();
			
			Log.info(jobName, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
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
				Log.error(jobName,"�ع�����ʧ��");
			}
			Log.error(jobName,"������Ϣ:"+Log.getErrorMessage(e));
			
			
			Log.error(jobName, "ִ����ҵʧ�� [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName,"���´����־ʧ��");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"�ر����ݿ�����ʧ��");
			}
		}
		
		
	}

	private void getOnSaleItems() throws Exception
	{

		int pageno=1;
		
		Log.info("��ʼȡ��˹�������ϼ���Ʒ����");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					/*
					 * http://www.mesuca.com/shop/index.php?
					 *  act=api& 
						op=goods& 
						service=goods& 
						vcode=h9iX0TiKwmxtX0xrfVwKrJ& 
						page=1& 
						page_size=100& 
						status=1
					 */
					StringBuffer optBuffer =new StringBuffer();
					optBuffer.append("http://www.mesuca.com/shop/index.php?");
					optBuffer.append("act=api&");
					optBuffer.append("op=goods&");
					optBuffer.append("service=goods&");
					optBuffer.append("vcode=h9iX0TiKwmxtX0xrfVwKrJ&");
					optBuffer.append("page="+pageno+"&");
					optBuffer.append("page_size="+pageSize+"&");
					optBuffer.append("status=1");
					String responseProductData = Utils.sendbyget(optBuffer.toString());
					Log.info("ȡ��Ʒ��������:��"+responseProductData);
					JSONObject responseproduct=new JSONObject(responseProductData);
					/*�������ˣ�API���ṩ��Ŀ������Ҫ���Լ���������*/
					int totalCount = 0;
					for(int shit=0;;shit++)
					{
						StringBuffer optBuffer1 =new StringBuffer();
						optBuffer1.append("http://www.mesuca.com/shop/index.php?");
						optBuffer1.append("act=api&");
						optBuffer1.append("op=goods&");
						optBuffer1.append("service=goods&");
						optBuffer1.append("vcode=h9iX0TiKwmxtX0xrfVwKrJ&");
						optBuffer1.append("page="+shit+"&");
						optBuffer1.append("page_size="+pageSize+"&");
						optBuffer1.append("status=1");
						String responseProductData1 = Utils.sendbyget(optBuffer.toString());
						JSONObject responseproduct1=new JSONObject(responseProductData1);
						totalCount = totalCount + responseproduct1.getJSONArray("goods_list").length();
						if(responseproduct1.getJSONArray("goods_list").length() == 0) break;
					}
					
					JSONArray productlist=responseproduct.getJSONArray("goods_list");
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
						
						String productId=product.optString("goods_id");
						String productCode=product.optString("goods_commonid");
						String productCname=product.optString("goods_name");
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),String.valueOf(skuid)+"-"+String.valueOf(product.getString("store_id")),sku,product.getString("goods_storage")) ;
					}
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) break;
					
					pageno++;
				}
				
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 	
		}				
	}
	
	private void getInStockItems() throws Exception
	{

		int pageno=1;
		
		Log.info("��ʼȡ��˹�������ֿ���Ʒ����");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					StringBuffer optBuffer =new StringBuffer();
					optBuffer.append("http://www.mesuca.com/shop/index.php?");
					optBuffer.append("act=api&");
					optBuffer.append("op=goods&");
					optBuffer.append("service=goods&");
					optBuffer.append("vcode=h9iX0TiKwmxtX0xrfVwKrJ&");
					optBuffer.append("page="+pageno+"&");
					optBuffer.append("page_size="+pageSize+"&");
					optBuffer.append("status=1");
					String responseProductData = Utils.sendbyget(optBuffer.toString());
					Log.info("ȡ��Ʒ��������:��"+responseProductData);
					JSONObject responseproduct=new JSONObject(responseProductData);
					
					int totalCount = 0;
					for(int shit=0;;shit++)
					{
						StringBuffer optBuffer1 =new StringBuffer();
						optBuffer1.append("http://www.mesuca.com/shop/index.php?");
						optBuffer1.append("act=api&");
						optBuffer1.append("op=goods&");
						optBuffer1.append("service=goods&");
						optBuffer1.append("vcode=h9iX0TiKwmxtX0xrfVwKrJ&");
						optBuffer1.append("page="+shit+"&");
						optBuffer1.append("page_size="+pageSize+"&");
						optBuffer1.append("status=1");
						String responseProductData1 = Utils.sendbyget(optBuffer.toString());
						JSONObject responseproduct1=new JSONObject(responseProductData1);
						totalCount = totalCount + responseproduct1.getJSONArray("goods_list").length();
						if(responseproduct1.getJSONArray("goods_list").length() == 0) break;
					}
					
					JSONArray productlist=responseproduct.getJSONArray("goods_list");
					
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
						
						String productId=product.optString("goods_id");
						String productCode=product.optString("goods_commonid");
						String productCname=product.optString("goods_name");
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),String.valueOf(skuid)+"-"+String.valueOf(product.getString("store_id")),sku,product.getString("goods_storage")) ;
					}

					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) break;
					
					pageno++;
				}
				
			
				break;
				
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn(jobName+", Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 	
		}				
	}
}
