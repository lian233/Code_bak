package com.wofu.ecommerce.wqb;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.wqb.utils.Utils;
public class ItemExecuter extends Executer{
	
	private static String jobName = "取网渠宝商品资料作业";
	
	private String url="";

	private String app_key  = "";
	
	private String username="";
	
	private String app_secret="";
	
	private String format="";
	
	private String tradecontactid="";
	private String pageSize="";

	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		url=prop.getProperty("url");
		format=prop.getProperty("format");
		tradecontactid=prop.getProperty("tradecontactid");
		app_key=prop.getProperty("app_key");
		username=prop.getProperty("username");
		app_secret=prop.getProperty("app_secret");
		pageSize=prop.getProperty("pageSize");
		try {		
			updateJobFlag(1);
			
			getOnSaleItems();
			//getInStockItems();
	
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
		
		Log.info("开始取网渠宝上架商品资料");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					Map<String, String> productparams = new HashMap<String, String>();
			        //系统级参数设置
					productparams.put("appKey", app_key);
					productparams.put("user", app_key);
					productparams.put("format", format);
					productparams.put("method", "IOpenAPI.GetProducts");
					productparams.put("pageSize", pageSize);
			        productparams.put("pageIndex", String.valueOf(pageno));
			        String responseProductData = Utils.sendByPost(productparams, app_secret, "IOpenAPI.GetProducts",url);
			        Log.info("取商品返回数据:　"+responseProductData);
					JSONObject responseproduct=new JSONObject(responseProductData);
					
					int totalCount=Integer.parseInt(responseproduct.getString("SumNum"));
					
					JSONArray productlist=responseproduct.getJSONArray("Result");
					
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
					
						String productId=product.optString("ProId");
						String productCode=product.optString("ProNo");
						String productCname=product.optString("ProTitle");
						
						Log.info("货号:"+productCode+",产品名称:"+productCname);
						if(productCname.indexOf("邮费补差")>0) continue;
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),productId,productCode,productCname,0) ;
						
						JSONArray childseriallist=product.getJSONArray("ProductSpec");
						
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);
							
							String sku=childserial.optString("ProSkuNo");
								
							StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),productCode+"-"+sku,sku,0) ;
								
						}
					}
					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(totalCount/Float.parseFloat(pageSize)))).intValue()) break;
					
					pageno++;
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
