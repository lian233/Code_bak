package com.wofu.ecommerce.weidian;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Properties;

import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weidian.utils.Utils;
import com.wofu.ecommerce.weidian.utils.getToken;
public class ItemExecuter extends Executer{
	private static String jobName = "ȡ΢����Ʒ������ҵ";
	private String tradecontactid="34";
	@Override
	public void run() {
		//System.out.println("ȡ΢����Ʒ������ҵ");
		//Log.info("ȡ΢����Ʒ������ҵ");
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		tradecontactid=prop.getProperty("tradecontactid","34");
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
		
		Log.info("��ʼȡ΢���ϼ���Ʒ����");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{				
					JSONObject param_Object = new JSONObject();
					JSONObject public_Object = new JSONObject();
					param_Object.put("page_num", pageno);
					param_Object.put("page_size", "20");
					param_Object.put("orderby", 1);
					public_Object.put("method", "vdian.item.list.get");
					public_Object.put("access_token", getToken.getToken_zy(this.getDao().getConnection())); //д���������ڻ�ȡaccess_token
					public_Object.put("version", "1.0"); 
					public_Object.put("format", "json"); 
					String opt_to_sever = Params.url + "?param=" + URLEncoder.encode(param_Object.toString(),"UTF-8") + "&public=" + URLEncoder.encode(public_Object.toString(),"UTF-8");
					String responseProductData = Utils.sendbyget(opt_to_sever);
					Log.info("ȡ��Ʒ��������:��"+responseProductData);
					JSONObject responseproduct=new JSONObject(responseProductData);
					int totalCount=responseproduct.getJSONObject("result").getInt("total_num");
					JSONArray productlist=responseproduct.getJSONObject("result").getJSONArray("items");
					
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
						
						String productId= product.optString("itemid");
						String productCode=product.optString("itemid"); //6.17֮ǰΪmerchant_code
						String productCname=product.optString("item_name");
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						JSONArray childseriallist=product.getJSONArray("skus");
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);
							
							String skuid=childserial.optString("id");
							String sku=childserial.optString("sku_merchant_code");
							StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),skuid,sku,0) ;

						}
					}
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) break;
					pageno++;
				}
				break;
			} catch (Exception e) {
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				Log.warn("1Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 	
		}				
	}
	
	private void getInStockItems() throws Exception
	{

		int pageno=1;
		
		Log.info("��ʼȡ΢��ֿ���Ʒ����");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					JSONObject param_Object = new JSONObject();
					JSONObject public_Object = new JSONObject();
					param_Object.put("page_num", pageno);
					param_Object.put("page_size", "20");
					param_Object.put("orderby", 1);
					public_Object.put("method", "vdian.item.list.get");
					public_Object.put("access_token", getToken.getToken_zy(this.getDao().getConnection())); //д���������ڻ�ȡaccess_token
					public_Object.put("version", "1.0"); 
					public_Object.put("format", "json"); 
					String opt_to_sever = Params.url + "?param=" + URLEncoder.encode(param_Object.toString(),"UTF-8") + "&public=" + URLEncoder.encode(public_Object.toString(),"UTF-8");
					String responseProductData = Utils.sendbyget(opt_to_sever);
					Log.info("ȡ��Ʒ��������:��"+responseProductData);
					JSONObject responseproduct=new JSONObject(responseProductData);
					int totalCount=responseproduct.getJSONObject("result").getInt("item_num");
					JSONArray productlist=responseproduct.getJSONObject("result").getJSONArray("items");
					
					
					
					
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
					
						String productId= product.optString("itemid");
						String productCode=product.optString("itemid"); //6.17֮ǰΪmerchant_code
						String productCname=product.optString("item_name");
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
	
						JSONArray childseriallist=product.getJSONArray("skus");
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);
							
							String skuid=childserial.optString("id");
							String sku=childserial.optString("sku_merchant_code");
							StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),skuid,sku,0) ;

						}
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
