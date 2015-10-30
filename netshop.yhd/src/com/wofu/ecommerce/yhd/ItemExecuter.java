package com.wofu.ecommerce.yhd;
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
import com.wofu.ecommerce.yhd.utils.Utils;
public class ItemExecuter extends Executer{
	
	private static String jobName = "ȡһ�ŵ���Ʒ������ҵ";
	
	private String url="";

	private String token = "";

	private String app_key  = "";
	
	private String username="";
	
	private String app_secret="";
	
	private String format="";
	
	private String ver="";

	private String tradecontactid="";



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
		
		Log.info("��ʼȡһ�ŵ��ϼ���Ʒ����");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					Map<String, String> productparams = new HashMap<String, String>();
			        //ϵͳ����������
					productparams.put("appKey", app_key);
					productparams.put("sessionKey", token);
					productparams.put("format", format);
					productparams.put("method", "yhd.serial.products.search");
					productparams.put("ver", ver);
					productparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			        productparams.put("method", "yhd.serial.products.search");
			        productparams.put("ver", ver);
			       
			        productparams.put("canShow", "1");
			        productparams.put("canSale", "1");
			        productparams.put("curPage", String.valueOf(pageno));
			        productparams.put("pageRows", "20");
			        productparams.put("verifyFlg", "2");
			        
			        String responseProductData = Utils.sendByPost(productparams, app_secret, url);
			        Log.info("ȡ��Ʒ��������:��"+responseProductData);
					JSONObject responseproduct=new JSONObject(responseProductData);
					
					int totalCount=responseproduct.getJSONObject("response").getInt("totalCount");
					
					JSONArray productlist=responseproduct.getJSONObject("response").getJSONObject("serialProductList").getJSONArray("serialProduct");
					
					
					
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
					
						long productId=product.optLong("productId");
						String productCode=product.optString("productCode");
						String productCname=product.optString("productCname");
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						
						
						Map<String, String> stockparams = new HashMap<String, String>();
				        //ϵͳ����������
						stockparams.put("appKey", app_key);
						stockparams.put("sessionKey", token);
						stockparams.put("format", format);
						stockparams.put("method", "yhd.serial.product.get");
						stockparams.put("ver", ver);
						stockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
						stockparams.put("productId", String.valueOf(productId));
			        
						String responseData = Utils.sendByPost(stockparams, app_secret, url);
						 Log.info("ȡ��Ʒ��ϸ��������:��"+responseData);
						
						
						JSONObject responsestock=new JSONObject(responseData);
						
				
						
						JSONArray childseriallist=responsestock.getJSONObject("response").getJSONObject("serialChildProdList").getJSONArray("serialChildProd");
						
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);
							
							String sku=childserial.optString("outerId");
							long skuid=childserial.optLong("productId");
							
							
							JSONArray stocklist=childserial.getJSONObject("allWareHouseStocList").getJSONArray("pmStockInfo");
							
							for (int j=0;j<stocklist.length();j++)
							{
								JSONObject stock=stocklist.optJSONObject(j);
								
								int quantity=stock.optInt("vs");
								long warehouseId=stock.optLong("warehouseId");
								
								StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),String.valueOf(skuid)+"-"+String.valueOf(warehouseId),sku,quantity) ;
								
							}
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
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 	
		}				
	}
	
	private void getInStockItems() throws Exception
	{

		int pageno=1;
		
		Log.info("��ʼȡһ�ŵ�ֿ���Ʒ����");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					Map<String, String> productparams = new HashMap<String, String>();
			        //ϵͳ����������
					productparams.put("appKey", app_key);
					productparams.put("sessionKey", token);
					productparams.put("format", format);
					productparams.put("method", "yhd.serial.products.search");
					productparams.put("ver", ver);
					productparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			       
			        productparams.put("canShow", "1");
			        productparams.put("canSale", "0");
			        productparams.put("curPage", String.valueOf(pageno));
			        productparams.put("pageRows", "20");
			        productparams.put("verifyFlg", "2");
			        
			        String responseProductData = Utils.sendByPost(productparams, app_secret, url);
			        Log.info("ȡ�ֿ���Ʒ��������:��"+responseProductData);
			        
					JSONObject responseproduct=new JSONObject(responseProductData);
					
					int totalCount=responseproduct.getJSONObject("response").getInt("totalCount");
					
					JSONArray productlist=responseproduct.getJSONObject("response").getJSONObject("serialProductList").getJSONArray("serialProduct");
					
					
					
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
					
						long productId=product.optLong("productId");
						String productCode=product.optString("productCode");
						String productCname=product.optString("productCname");
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						
						
						Map<String, String> stockparams = new HashMap<String, String>();
				        //ϵͳ����������
						stockparams.put("appKey", app_key);
						stockparams.put("sessionKey", token);
						stockparams.put("format", format);
						stockparams.put("method", "yhd.serial.product.get");
						stockparams.put("ver", ver);
						stockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
						
						stockparams.put("productId", String.valueOf(productId));
			        
						String responseData = Utils.sendByPost(stockparams, app_secret, url);
						 Log.info("ȡ�ֿ���Ʒ���鷵������:��"+responseData);
						JSONObject responsestock=new JSONObject(responseData);
						
						JSONArray childseriallist=responsestock.getJSONObject("response").getJSONObject("serialChildProdList").getJSONArray("serialChildProd");
						
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);
							
							String sku=childserial.optString("outerId");
							long skuid=childserial.optLong("productId");
							
							
							JSONArray stocklist=childserial.getJSONObject("allWareHouseStocList").getJSONArray("pmStockInfo");
							
							for (int j=0;j<stocklist.length();j++)
							{
								JSONObject stock=stocklist.optJSONObject(j);
								
								int quantity=stock.optInt("vs");
								long warehouseId=stock.optLong("warehouseId");
								
								StockManager.addStockConfigSku(this.getDao(), Integer.valueOf(tradecontactid),String.valueOf(productId),String.valueOf(skuid)+"-"+String.valueOf(warehouseId),sku,quantity) ;
								
							}
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
