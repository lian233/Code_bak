package com.wofu.ecommerce.s;
import java.util.Date;
import java.util.Properties;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.s.utils.Utils;
public class SingleItemExecuter extends Executer{
	
	private static String jobName = "取特定名鞋库商品资料作业";
	private String url="";
	private String token = "";
	private String app_key  = "";
	private String username="";
	private String app_secret="";
	private String format="";
	private String ver="";

	private String tradecontactid="";
	private String productId="";

	@Override
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
						
						Log.info("货号:"+productCode+",产品名称:"+productCname);
						
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						**/
						
						
//						Map<String, String> stockparams = new HashMap<String, String>();
//				        //系统级参数设置
//						stockparams.put("appKey", app_key);
//						stockparams.put("sessionKey", token);
//						stockparams.put("format", format);
//						stockparams.put("method", "yhd.serial.product.get");
//						stockparams.put("ver", ver);
//						stockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
//						stockparams.put("productId", productId);
						Date now=new Date();
						String method="scn.vendor.item.full.get";
						JSONObject data=new JSONObject();
						data.put("Fields", "vendor_item_id,item_name,brand_name,tag_price,color_name,item_status,show_date,down_date,update_date,product_no,detail.size_name,detail.update_date, detail.vendor_sku_id");
		//				data.put("VendorItemId","111128");  //供货商商品ID
						data.put("ProductNo", productId);  //货号
		//				data.put("ItemName", String.valueOf(null)); //商品名称
		//				data.put("BrandName", String.valueOf(null)); //品牌名称
		//				data.put("ItemStatus", String.valueOf(null));  //商品状态(1-在售 2-下架)
		//				data.put("StartShowDate", String.valueOf(null)); //上架开始时间
		//				data.put("EndShowDate", String.valueOf(null));    //上架结束时间
		//				data.put("StartDownDate", String.valueOf(null));  //下架开始时间
		//				data.put("EndDownDate", String.valueOf(null));    //下架结束时间
						data.put("PageNo", String.valueOf(pageno));    //页码
						data.put("PageSize", "20");  //每页条数。默认40，最大100
						String sign=Utils.get_sign(data, method, now);
						String output_to_server=Utils.post_data_process(method, data, now, sign).toString();
					        
						String responseData = Utils.sendByPost(Params.url,output_to_server);
						 Log.info("取商品详细返回数据:　"+responseData);
						
						
						JSONObject responsestock=new JSONObject(responseData);
						
				
						
						JSONArray childseriallist=responsestock.getJSONArray("Result").getJSONObject(0).getJSONArray("Skus");
						
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);
							
							String sku=childserial.optString("VendorSkuId");
//							long skuid=childserial.optLong("productId");
							
							StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),String.valueOf(sku)+"-"+String.valueOf(0),sku,0) ;
								
						}

			} catch (Exception e) {
				Log.error(jobName, e.getMessage());
			} 	
		}				
	
	
}
