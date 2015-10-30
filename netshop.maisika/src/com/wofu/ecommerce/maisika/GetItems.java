package com.wofu.ecommerce.maisika;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.maisika.util.CommHelper;
import com.wofu.ecommerce.maisika.util.Utils;

public class GetItems extends Executer
{
	
	private static String jobName = "取麦斯卡商品资料作业";
	
	private String tradecontactid="15";
	@Override
	public void run() 
	{	
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		tradecontactid=prop.getProperty("tradecontactid","15");
		try {
			updateJobFlag(1);
			
			getOnSaleItems();
	
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
		
		Log.info("开始取麦斯卡上架商品资料");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					
					LinkedHashMap<String,Object> map = new LinkedHashMap<String,Object>();
					map.put("&op","goods");
			        map.put("service","goods");
			        map.put("vcode", Params.vcode);
			        map.put("page_size", Params.pageSize);
			        map.put("page",String.valueOf(pageno));
			        map.put("status","1");
			        String responseText = CommHelper.doGet(map,Params.url);
					//把返回的数据转成json对象
					JSONObject responseproduct= new JSONObject(responseText);
//					System.out.println(responseText);
					int totalCount=responseproduct.getInt("counts");	
					JSONArray productlist=responseproduct.getJSONArray("productlist");
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
						String productId= product.optString("mid");//sku
						String productCode=product.optString("goods_no");//goods_no货号
						String productCname=product.optString("title");//产品名称
						
						Log.info("货号:"+productCode+",产品名称:"+productCname);
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						JSONArray childseriallist=product.getJSONArray("skuinfo");
						for(int m=0;m<childseriallist.length();m++)
						{
//							System.out.println("数组长度"+childseriallist.length());
							String skuid=product.getJSONArray("skuinfo").getJSONObject(m).optString("skuid");
							String sku=product.getJSONArray("skuinfo").getJSONObject(m).optString("sku");
							int quantity=product.getJSONArray("skuinfo").getJSONObject(m).optInt("num");
							StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),skuid/*+"-"+String.valueOf(warehouseId)*/,sku,quantity) ;

						}
					}
					
					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(totalCount/10.0))).intValue()) break;
					pageno++;
					
				}
				break;
			}catch (Exception e) 
			{
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				Log.warn("1远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
			
				Thread.sleep(10000L);
			} 	
		}
	}
}
