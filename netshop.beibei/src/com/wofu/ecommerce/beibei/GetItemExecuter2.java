package com.wofu.ecommerce.beibei;
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
import com.wofu.ecommerce.beibei.utils.Utils;
public class GetItemExecuter2 extends Executer{
	
	private static String jobName = "取贝贝网商品资料作业";
	
	private String url="";

	private String app_id = "";
	
	private String tradecontactid="";

	private String username="";
	
	private String secret="";
	
	private String session="";



	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		tradecontactid=prop.getProperty("tradecontactid");
		username=prop.getProperty("username");
		app_id=prop.getProperty("app_id");
		secret=prop.getProperty("secret");
		session=prop.getProperty("session");
		
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
		
		Log.info("开始取贝贝网商品资料");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for (int k=0;k<10;)
		{
			try
			{
				
				while(true)
				{
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //系统级参数设置
			        orderlistparams.put("method", "beibei.outer.item.warehouse.get");
					orderlistparams.put("app_id", app_id);
			        orderlistparams.put("session", session);
			        orderlistparams.put("timestamp", time());
			        orderlistparams.put("version", Params.ver);
			        //应用级输入参数
			        orderlistparams.put("page_no", String.valueOf(pageno));
			        orderlistparams.put("page_size","20");
					String responseOrderListData = Utils.sendByPost(orderlistparams, secret, url);
			        Log.info("取商品返回数据:　"+responseOrderListData);
					JSONObject responseproduct=new JSONObject(responseOrderListData);
					int totalCount=responseproduct.getInt("count");
					
					JSONArray productlist=responseproduct.getJSONArray("data");
					
					System.out.println(productlist.length());
					
					for(int i=0;i<productlist.length();i++)
					{
						JSONObject product=productlist.getJSONObject(i);
					
						long productId=product.optLong("iid");//商品编号
						String productCode=product.optString("goods_num");//货号
						String productCname=product.optString("title");//商品名称
						
						Log.info("货号:"+productCode+",产品名称:"+productCname);
						
						StockManager.stockConfig(this.getDao(), orgid,Integer.valueOf(tradecontactid),String.valueOf(productId),productCode,productCname,0) ;
						JSONArray responsestock=product.getJSONArray("sku");
						System.out.println("sku数量"+responsestock.length());
						for(int m=0;m<responsestock.length();m++)
						{
							JSONObject childserial=responsestock.optJSONObject(m);
							String sku=childserial.optString("outer_id");
							long skuid=childserial.optLong("id");
							int qty=childserial.optInt("num");
							System.out.println("sku为"+ sku +" 数量为 "+qty);
							StockManager.addStockConfigSku(this.getDao(), orgid,String.valueOf(productId),String.valueOf(skuid),sku,qty) ;
								
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
	
	public String time() {
		Long time= System.currentTimeMillis()/1000;
		return time.toString();
	}
	
}
