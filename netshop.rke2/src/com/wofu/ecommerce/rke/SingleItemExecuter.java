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
public class SingleItemExecuter extends Executer{
	
	private static String jobName = "ȡ�ض�һ�ŵ���Ʒ������ҵ";
	private String url="";
	private String token = "";
	private String app_key  = "";
	private String username="";
	private String app_secret="";
	private String format="";
	private String ver="";

	private String tradecontactid="";
	private String productId="";

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
		productId=prop.getProperty("productId");
		
		try {		
			updateJobFlag(1);
			
			getSingleSaleItems();
	
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

	private void getSingleSaleItems() throws Exception
	{

		int pageno=1;
		
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		
			try
			{
					/**
						long productId=product.optLong("productId");
						String productCode=product.optString("productCode");
						String productCname=product.optString("productCname");
						
						Log.info("����:"+productCode+",��Ʒ����:"+productCname);
						
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						**/
						
						
						Map<String, String> stockparams = new HashMap<String, String>();
				        //ϵͳ����������
						stockparams.put("appKey", app_key);
						stockparams.put("sessionKey", token);
						stockparams.put("format", format);
						stockparams.put("method", "yhd.serial.product.get");
						stockparams.put("ver", ver);
						stockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
						stockparams.put("productId", productId);
			        
						String responseData = Utils.sendByPost(stockparams,url);
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

			} catch (Exception e) {
				Log.error(jobName, e.getMessage());
			} 	
		}				
	
	
}
