package com.wofu.ecommerce.s;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.s.utils.Utils;
public class ItemExecuter extends Executer{
	
	private static String jobName = "取名鞋库商品资料作业";

	private String tradecontactid="";



	@Override
	public void run() {
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
		
		Log.info("开始取一号店上架商品资料");
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
//					data.put("VendorItemId","111128");  //供货商商品ID
//					data.put("ProductNo", "111128");  //货号
//					data.put("ItemName", String.valueOf(null)); //商品名称
//					data.put("BrandName", String.valueOf(null)); //品牌名称
//					data.put("ItemStatus", String.valueOf(null));  //商品状态(1-在售 2-下架)
//					data.put("StartShowDate", String.valueOf(null)); //上架开始时间
//					data.put("EndShowDate", String.valueOf(null));    //上架结束时间
//					data.put("StartDownDate", String.valueOf(null));  //下架开始时间
//					data.put("EndDownDate", String.valueOf(null));    //下架结束时间
					data.put("PageNo", String.valueOf(pageno));    //页码
					data.put("PageSize", "20");  //每页条数。默认40，最大100
					String sign=Utils.get_sign(data, method, now);
					String output_to_server=Utils.post_data_process(method, data, now, sign).toString();
			        String responseProductData = Utils.sendByPost(Params.url,output_to_server);
			        Log.info("取商品返回数据:　"+responseProductData);
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
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						
						
//						Map<String, String> stockparams = new HashMap<String, String>();
//				        //系统级参数设置
//						stockparams.put("appKey", app_key);
//						stockparams.put("sessionKey", token);
//						stockparams.put("format", format);
//						stockparams.put("method", "yhd.serial.product.get");
//						stockparams.put("ver", ver);
//						stockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
//						stockparams.put("productId", String.valueOf(productId));				
						now=new Date();
						data=new JSONObject();
						data.put("Fields", "vendor_item_id,item_name,brand_name,tag_price,color_name,item_status,show_date,down_date,update_date,product_no,detail.size_name,detail.update_date, detail.vendor_sku_id");
						data.put("VendorItemId",String.valueOf(productId));  //供货商商品ID
//						data.put("ProductNo", "111128");  //货号
//						data.put("ItemName", String.valueOf(null)); //商品名称
//						data.put("BrandName", String.valueOf(null)); //品牌名称
//						data.put("ItemStatus", String.valueOf(null));  //商品状态(1-在售 2-下架)
//						data.put("StartShowDate", String.valueOf(null)); //上架开始时间
//						data.put("EndShowDate", String.valueOf(null));    //上架结束时间
//						data.put("StartDownDate", String.valueOf(null));  //下架开始时间
//						data.put("EndDownDate", String.valueOf(null));    //下架结束时间
//						data.put("PageNo", String.valueOf(null));    //页码
//						data.put("PageSize", String.valueOf(null));  //每页条数。默认40，最大100
						sign=Utils.get_sign(data, method, now);
						output_to_server=Utils.post_data_process(method, data, now, sign).toString();
			        
						String responseData = Utils.sendByPost(Params.url,output_to_server);
						 Log.info("取商品详细返回数据:　"+responseData);
						
						
						JSONObject responsestock=new JSONObject(responseData);
						
				
						
						//JSONArray childseriallist=responsestock.getJSONObject("response").getJSONObject("serialChildProdList").getJSONArray("serialChildProd");
						JSONArray childseriallist=responsestock.getJSONArray("Result").getJSONObject(0).getJSONArray("Skus");
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);
							
							String sku=childserial.optString("VendorSkuId");
							//long skuid=childserial.optLong("VendorSkuId");
							
							
							//JSONArray stocklist=childserial.getJSONObject("allWareHouseStocList").getJSONArray("pmStockInfo");
							

								int quantity=0;
								long warehouseId=0;
								
								StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),String.valueOf(sku)+"-"+String.valueOf(warehouseId),sku,quantity) ;

						}
					}

					
					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) break;
					
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
//					Map<String, String> productparams = new HashMap<String, String>();
//			        //系统级参数设置
//					productparams.put("appKey", app_key);
//					productparams.put("sessionKey", token);
//					productparams.put("format", format);
//					productparams.put("method", "yhd.serial.products.search");
//					productparams.put("ver", ver);
//					productparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
//			       
//			        productparams.put("canShow", "1");
//			        productparams.put("canSale", "0");
//			        productparams.put("curPage", String.valueOf(pageno));
//			        productparams.put("pageRows", "20");
//			        productparams.put("verifyFlg", "2");
					UTF8_transformer utf8_transformer=new UTF8_transformer();
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
					Date now=new Date();
					String method="scn.vendor.item.full.get";
					String ver=Params.ver;
					JSONObject data=new JSONObject();
					data.put("Fields", "vendor_item_id,item_name,brand_name,tag_price,color_name,item_status,show_date,down_date,update_date,product_no,detail.size_name,detail.update_date, detail.vendor_sku_id");
//					data.put("VendorItemId",String.valueOf(productId));  //供货商商品ID
//					data.put("ProductNo", "111128");  //货号
//					data.put("ItemName", String.valueOf(null)); //商品名称
//					data.put("BrandName", String.valueOf(null)); //品牌名称
//					data.put("ItemStatus", String.valueOf(null));  //商品状态(1-在售 2-下架)
//					data.put("StartShowDate", String.valueOf(null)); //上架开始时间
//					data.put("EndShowDate", String.valueOf(null));    //上架结束时间
//					data.put("StartDownDate", String.valueOf(null));  //下架开始时间
//					data.put("EndDownDate", String.valueOf(null));    //下架结束时间
					data.put("PageNo", String.valueOf(pageno));    //页码
					data.put("PageSize", "20");  //每页条数。默认40，最大100
					String sign=Utils.get_sign(data, method, now);
					String output_to_server=Utils.post_data_process(method, data, now, sign).toString();
			        String responseProductData = Utils.sendByPost(Params.url,output_to_server);
			        Log.info("取仓库商品返回数据:　"+responseProductData);
			        
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
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						
						
//						Map<String, String> stockparams = new HashMap<String, String>();
//				        //系统级参数设置
//						stockparams.put("appKey", app_key);
//						stockparams.put("sessionKey", token);
//						stockparams.put("format", format);
//						stockparams.put("method", "yhd.serial.product.get");
//						stockparams.put("ver", ver);
//						stockparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
//						
//						stockparams.put("productId", String.valueOf(productId));
						now=new Date();
						data=new JSONObject();
						data.put("Fields", "vendor_item_id,item_name,brand_name,tag_price,color_name,item_status,show_date,down_date,update_date,product_no,detail.size_name,detail.update_date, detail.vendor_sku_id");
//						data.put("VendorItemId",String.valueOf(productId));  //供货商商品ID
						data.put("ProductNo", String.valueOf(productId));  //货号
//						data.put("ItemName", String.valueOf(null)); //商品名称
//						data.put("BrandName", String.valueOf(null)); //品牌名称
//						data.put("ItemStatus", String.valueOf(null));  //商品状态(1-在售 2-下架)
//						data.put("StartShowDate", String.valueOf(null)); //上架开始时间
//						data.put("EndShowDate", String.valueOf(null));    //上架结束时间
//						data.put("StartDownDate", String.valueOf(null));  //下架开始时间
//						data.put("EndDownDate", String.valueOf(null));    //下架结束时间
//						data.put("PageNo", String.valueOf(pageno));    //页码
//						data.put("PageSize", "20");  //每页条数。默认40，最大100
						sign=Utils.get_sign(data, method, now);
						output_to_server=Utils.post_data_process(method, data, now, sign).toString();        
						String responseData = Utils.sendByPost(Params.url,output_to_server);
						 Log.info("取仓库商品详情返回数据:　"+responseData);
						JSONObject responsestock=new JSONObject(responseData);
						
						
						JSONArray childseriallist=responsestock.getJSONArray("Result").getJSONObject(0).getJSONArray("Skus");
						for(int m=0;m<childseriallist.length();m++)
						{
							JSONObject childserial=childseriallist.optJSONObject(m);
							
							String sku=childserial.optString("VendorSkuId");
							//long skuid=childserial.optLong("VendorSkuId");
							
							
							//JSONArray stocklist=childserial.getJSONObject("allWareHouseStocList").getJSONArray("pmStockInfo");
							

								int quantity=0;
								long warehouseId=0;
								
								StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),sku+"-"+String.valueOf(warehouseId),sku,quantity) ;

						}
					}

					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) break;
					
					pageno++;
				}
				
			
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
