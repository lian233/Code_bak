package com.wofu.ecommerce.yz;
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
import com.wofu.ecommerce.yz.utils.Utils;
public class ItemExecuter extends Executer{
	private static String jobName = "取有赞商品资料作业";
	private String url="";
	private String app_id  = "";
	private String username="";
	private String page_size="";
	private String format="";
	private String ver="";
	private String tradecontactid="";
	private String AppSecret="";
	//private final String fields="num_iid,title,outer_id,created,skus,num";

	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		format=prop.getProperty("format");
		ver=prop.getProperty("ver");
		tradecontactid=prop.getProperty("tradecontactid");
		app_id=prop.getProperty("app_id");
		username=prop.getProperty("username");
		page_size=prop.getProperty("page_size");
		AppSecret=prop.getProperty("AppSecret");
		
		try {		
			updateJobFlag(1);
			
			getOnSaleItems();
			getInStockItems();
	
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

	private void getOnSaleItems() throws Exception
	{

		int pageno=1;
		
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for(int i=0;i<10;i++){
			try
			{
					while(true){
						Map<String, String> stockparams = new HashMap<String, String>();
				        //系统级参数设置
						stockparams.put("app_id", app_id);
						stockparams.put("format", format);
						stockparams.put("method", "kdt.items.onsale.get");
						stockparams.put("sign_method", "MD5");
						stockparams.put("v", ver);
						stockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
						Log.info("pageno: "+pageno);
						stockparams.put("page_no", String.valueOf(pageno));
						stockparams.put("page_size", page_size);
			        
						String responseData = Utils.sendByPost(stockparams, AppSecret, url);
						Log.info("取商品详细返回数据:　"+responseData);

						JSONObject responsestock=new JSONObject(responseData).getJSONObject("response");
						String itemCount = responsestock.getString("total_results");
						if("0".equals(itemCount)) {
							i=10;
							break;
						}
						JSONArray itemlist = responsestock.getJSONArray("items");
						for(int j=0;j<itemlist.length();j++){
							JSONObject item = itemlist.getJSONObject(j);
							String productId=item.optString("num_iid");
							String productCode=item.optString("outer_id");
							String productName=item.optString("title");
							Log.info("货号:"+productCode+",产品名称:"+productName);
							StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productName,0) ;
							
							JSONArray childseriallist = item.getJSONArray("skus");
							
							for(int m=0;m<childseriallist.length();m++)
							{
								JSONObject childserial=childseriallist.optJSONObject(m);
								String sku=childserial.optString("outer_id");
								String skuid=childserial.optString("sku_id");
								String quantity=childserial.optString("quantity");
								StockManager.addStockConfigSku(this.getDao(), orgid,productId,skuid,sku,Integer.parseInt(quantity)) ;
							}
						}
						pageno++;
						if(Math.ceil(Float.parseFloat(itemCount)/Integer.parseInt(page_size))<pageno) {
							i=10;
						    break;
						}
					}
					i=10;
					break;
			} catch (Exception e) {
				Log.error(jobName, e.getMessage());
				if(++i>10) throw e;
			}
		}
			 	
		}	
	
	
	
	private void getInStockItems() throws Exception
	{

		int pageno=1;
		
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for(int i=0;i<10;i++){
			try
			{
					while(true){
						Map<String, String> stockparams = new HashMap<String, String>();
				        //系统级参数设置
						stockparams.put("app_id", app_id);
						stockparams.put("format", format);
						stockparams.put("method", "kdt.items.inventory.get");
						stockparams.put("sign_method", "MD5");
						stockparams.put("v", ver);
						stockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
						Log.info("pageno: "+pageno);
						stockparams.put("page_no", String.valueOf(pageno));
						stockparams.put("page_size", page_size);
			        
						String responseData = Utils.sendByPost(stockparams, AppSecret, url);
						Log.info("取商品详细返回数据:　"+responseData);

						JSONObject responsestock=new JSONObject(responseData).getJSONObject("response");
						String itemCount = responsestock.getString("total_results");
						if("0".equals(itemCount)) {
							i=10;
							break;
						}
						JSONArray itemlist = responsestock.getJSONArray("items");
						for(int j=0;j<itemlist.length();j++){
							JSONObject item = itemlist.getJSONObject(j);
							String productId=item.optString("num_iid");
							String productCode=item.optString("outer_id");
							String productName=item.optString("title");
							Log.info("货号:"+productCode+",产品名称:"+productName);
							StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productName,0) ;
							
							JSONArray childseriallist = item.getJSONArray("skus");
							
							for(int m=0;m<childseriallist.length();m++)
							{
								JSONObject childserial=childseriallist.optJSONObject(m);
								String sku=childserial.optString("outer_id");
								String skuid=childserial.optString("sku_id");
								String quantity=childserial.optString("quantity");
								StockManager.addStockConfigSku(this.getDao(), orgid,productId,skuid,sku,Integer.parseInt(quantity)) ;
							}
						}
						pageno++;
						if(Math.ceil(Float.parseFloat(itemCount)/Integer.parseInt(page_size))<pageno) {
							i=10;
						    break;
						}
					}
					i=10;
					break;
			} catch (Exception e) {
				Log.error(jobName, e.getMessage());
				if(++i>10) throw e;
			}
		}
			 	
		}
	
	
}
