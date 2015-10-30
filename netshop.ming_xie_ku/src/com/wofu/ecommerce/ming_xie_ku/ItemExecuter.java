package com.wofu.ecommerce.ming_xie_ku;
import java.util.Date;
import java.util.Properties;

import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ming_xie_ku.utils.Utils;
public class ItemExecuter extends Executer{
	
	private static String jobName = "取名鞋库商品资料作业";

	private String tradecontactid="";
	private String url="";
	private String app_key="";
	private String app_Secret="";
	private String ver="";
	private String format="";



	@Override
	public void run() {
		//System.out.println("取名鞋库商品资料作业");
		//Log.info("取名鞋库商品资料作业");
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		tradecontactid=prop.getProperty("tradecontactid","18");
		url=prop.getProperty("url","18");
		app_key=prop.getProperty("app_key","18");
		app_Secret=prop.getProperty("app_Secret","18");
		ver=prop.getProperty("ver","18");
		format=prop.getProperty("format","18");
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
		
		Log.info("开始取名鞋库上架商品资料");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					Date now=new Date();
					String method="scn.vendor.item.full.get";
					JSONObject data=new JSONObject();
					data.put("Fields", "vendor_item_id,item_name,brand_name,tag_price,color_name,item_status,show_date,down_date,update_date,product_no,detail.size_name,detail.update_date, detail.vendor_sku_id");
					data.put("PageNo", String.valueOf(pageno));    //页码
					data.put("PageSize", "20");  //每页条数。默认40，最大100
					data.put("ItemStatus", "1");  //商品状态(1-在售 2-下架)
					String sign=Utils.get_sign(app_Secret,app_key,data, method, now,ver,format);
					String output_to_server=Utils.post_data_process(method, data, app_key,now, sign).toString();
			        String responseProductData = Utils.sendByPost(url,output_to_server);
			        //Log.info("取商品返回数据:　"+responseProductData);
					JSONObject responseproduct=new JSONObject(responseProductData);
					
					int totalCount=responseproduct.getInt("TotalResults");
					
					JSONArray productlist=responseproduct.getJSONArray("Result");
					
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
						
						String productId= product.optString("VendorItemId");
						String productCode=product.optString("ProductNo");
						String productCname=product.optString("ItemName");
						
						Log.info("货号:"+productCode+",产品名称:"+productCname);
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),productId,productCode,productCname,0) ;
						JSONArray childseriallist=product.getJSONArray("Skus");
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);

							String sku=childserial.optString("VendorSkuId");
							int quantity=0;
							StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),String.valueOf(sku),sku,quantity) ;
						}
					}
					Log.info("第 "+pageno+" 页");
					//判断是否有下一页
					//if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) break;
					if(!responseproduct.optBoolean("HasNext")) break;
					pageno++;
				}
				k=10;
				break;
			} catch (Exception e) {
				//e.printStackTrace();
				if (++k >= 10)
					throw e;
				Log.warn("1远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 	
		}				
	}
	
	private void getInStockItems() throws Exception
	{

		int pageno=1;
		
		Log.info("开始取名鞋库仓库商品资料");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					Date now=new Date();
					String method="scn.vendor.item.full.get";
					JSONObject data=new JSONObject();
					data.put("Fields", "vendor_item_id,item_name,brand_name,tag_price,color_name,item_status,show_date,down_date,update_date,product_no,detail.size_name,detail.update_date, detail.vendor_sku_id");
					data.put("PageNo", String.valueOf(pageno));    //页码
					data.put("PageSize", "20");  //每页条数。默认40，最大100
					data.put("ItemStatus", "2");  //商品状态(1-在售 2-下架)
					String sign=Utils.get_sign(app_Secret,app_key,data, method, now,ver,format);
					String output_to_server=Utils.post_data_process(method, data, app_key,now, sign).toString();
			        String responseProductData = Utils.sendByPost(url,output_to_server);
			       // Log.info("取仓库商品返回数据:　"+responseProductData);
			        
					JSONObject responseproduct=new JSONObject(responseProductData);
					
					int totalCount=responseproduct.getInt("TotalResults");
					
					JSONArray productlist=responseproduct.getJSONArray("Result");
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
					
						String productId=product.optString("VendorItemId");
						String productCode=product.optString("ProductNo");
						String productCname=product.optString("ItemName");
						
						Log.info("货号:"+productCode+",产品名称:"+productCname);
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),productId,productCode,productCname,0) ;
						
						JSONArray childseriallist=product.getJSONArray("Skus");
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);

							String sku=childserial.optString("VendorSkuId");

							int quantity=0;

							StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),sku,sku,quantity) ;
						}
					}
					Log.info("第 "+pageno+" 页");
					//判断是否有下一页
					if(!responseproduct.optBoolean("HasNext")) break;
					pageno++;
					
				}
				k=10;
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn(jobName+", 远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 	
		}				
	}
}
