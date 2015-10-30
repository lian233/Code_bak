package com.wofu.ecommerce.mgj;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.mgj.utils.Utils;
public class ItemExecuter extends Executer{
	
	private static String jobName = "ȡĢ������Ʒ������ҵ";
	
	private String url="";

	private String token = "";
	private String app_key  = "";
	
	private String username="";
	
	private String app_secret="";

	private String tradecontactid="";



	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		url=prop.getProperty("url");
		tradecontactid=prop.getProperty("tradecontactid");
		app_key=prop.getProperty("app_key");
		username=prop.getProperty("username");
		app_secret=prop.getProperty("app_secret");
		
		try {		
			updateJobFlag(1);
			token= PublicUtils.getToken(this.getDao().getConnection(), Integer.parseInt(tradecontactid));
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
		
		Log.info("��ʼȡĢ�����ϼ���Ʒ����");
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
					productparams.put("app_key", app_key);
					productparams.put("access_token", token);
					productparams.put("method", "youdian.item.queryList");
			       
			        productparams.put("isShelf", "0");
			        productparams.put("page_no", String.valueOf(pageno));
			        productparams.put("page_size", "20");
			        
			        String responseProductData = Utils.sendByPost(productparams, app_secret, url);
			        Log.info("�� "+pageno+" ҳ");
			        Log.info("ȡ��Ʒ��������:��"+responseProductData);
					JSONObject responseproduct=new JSONObject(responseProductData);
					JSONObject result = responseproduct.getJSONObject("status");
					if(10001==result.getInt("code")){//���óɹ�
						JSONObject data = responseproduct.getJSONObject("result").getJSONObject("data");
						int totalCount=data.getInt("list_total");
						if(totalCount==0){
							k=10;
							break;
						}
						JSONArray productlist=data.getJSONArray("list_items");
						for(int i=0;i<productlist.length();i++)
						{
							JSONObject product=productlist.getJSONObject(i);
						
							long productId=product.optLong("item_id");
							String productCode=product.optString("item_code");
							String productCname=product.optString("item_name");
							int itemstock = product.getInt("item_stock");
							Log.info("����:"+productCode+",��Ʒ����:"+productCname);
							
							StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,itemstock) ;
							
							Map<String, String> stockparams = new HashMap<String, String>();
					        //ϵͳ����������
							stockparams.put("app_key", app_key);
							stockparams.put("access_token", token);
							stockparams.put("method", "youdian.item.getItemInfo");
							stockparams.put("itemId", String.valueOf(productId));
				        
							String responseData = Utils.sendByPost(stockparams, app_secret, url);
							 Log.info("ȡ��Ʒ��ϸ��������:��"+responseData);
							
							
							JSONObject responsestock=new JSONObject(responseData);
							if(responsestock.getJSONObject("status").getInt("code")==10001){
								JSONArray childseriallist=responsestock.getJSONObject("result").getJSONObject("data").getJSONArray("item_skus");
								
								for(int m=0;m<childseriallist.length();m++)
								{
									JSONObject childserial=childseriallist.optJSONObject(m);
									String sku=childserial.optString("sku_code");
									long skuid=childserial.optLong("sku_id");
									
									int quantity=childserial.optInt("sku_stock");
									
									StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),String.valueOf(skuid),sku,quantity) ;
								}
							}
						}
						//�ж��Ƿ�����һҳ
						if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) {
							k=10;
							break;
						}
						
						pageno++;
					}else{//{"status":{"code":10018,"msg":"\u6388\u6743\u901a\u884c\u8bc1\u4e0d\u5b58\u5728\u6216\u5df2\u8fc7\u671f"},"result":null}
						Log.error(jobName, result.getString("msg"));
						k=10;
						break;
					}
					
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
		
		Log.info("��ʼȡĢ���ֲֿ���Ʒ����");
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
					productparams.put("app_key", app_key);
					productparams.put("access_token", token);
					productparams.put("method", "youdian.item.queryList");
			       
			        productparams.put("isShelf", "1");
			        productparams.put("page_no", String.valueOf(pageno));
			        productparams.put("page_size", "20");
			        
			        String responseProductData = Utils.sendByPost(productparams, app_secret, url);
			        Log.info("ȡ��Ʒ��������:��"+responseProductData);
					JSONObject responseproduct=new JSONObject(responseProductData);
					JSONObject result = responseproduct.getJSONObject("status");
					if(10001==result.getInt("code")){//���óɹ�
						JSONObject data = responseproduct.getJSONObject("result").getJSONObject("data");
						int totalCount=data.getInt("list_total");
						if(totalCount==0){
							k=10;
							break;
						}
						JSONArray productlist=data.getJSONArray("list_items");
						for(int i=0;i<productlist.length();i++)
						{
							JSONObject product=productlist.getJSONObject(i);
						
							long productId=product.optLong("item_id");
							String productCode=product.optString("item_code");
							String productCname=product.optString("item_name");
							int itemstock = product.getInt("item_stock");
							Log.info("����:"+productCode+",��Ʒ����:"+productCname);
							
							StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,itemstock) ;
							
							Map<String, String> stockparams = new HashMap<String, String>();
					        //ϵͳ����������
							stockparams.put("app_key", app_key);
							stockparams.put("access_token", token);
							stockparams.put("method", "youdian.item.getItemInfo");
							stockparams.put("itemId", String.valueOf(productId));
				        
							String responseData = Utils.sendByPost(stockparams, app_secret, url);
							 Log.info("ȡ��Ʒ��ϸ��������:��"+responseData);
							
							
							JSONObject responsestock=new JSONObject(responseData);
							if(responsestock.getJSONObject("status").getInt("code")==10001){
								JSONArray childseriallist=responsestock.getJSONObject("result").getJSONObject("data").getJSONArray("item_skus");
								
								for(int m=0;m<childseriallist.length();m++)
								{
									JSONObject childserial=childseriallist.optJSONObject(m);
									String sku=childserial.optString("sku_code");
									long skuid=childserial.optLong("sku_id");
									
									int quantity=childserial.optInt("sku_stock");
									
									StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),String.valueOf(skuid),sku,quantity) ;
								}
							}
					
							
						}
						//�ж��Ƿ�����һҳ
						if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) break;
						
						pageno++;
					}
					k=10;
					break;
					
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
