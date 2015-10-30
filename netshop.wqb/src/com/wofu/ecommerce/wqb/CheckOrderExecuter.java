package com.wofu.ecommerce.wqb;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.wqb.utils.Utils;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.order.OrderManager;
public class CheckOrderExecuter extends Executer {
	private String url="";
	private String pageSize="";
	private String app_key  = "";
	private String app_secret  = "";
	private String format="";
	private String user="";
	private String tradecontactid="";
	private String username="";
	private static long daymillis=60*60*3000L;
	private static String jobName="检查网上经销订单";
	public void run()  {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		url=prop.getProperty("url");
		format=prop.getProperty("format");
		tradecontactid=prop.getProperty("tradecontactid");
		app_key=prop.getProperty("app_key");
		app_secret=prop.getProperty("app_secret");
		username=prop.getProperty("username");
		user=prop.getProperty("app_key");
		pageSize=prop.getProperty("pageSize");
		try {		
			updateJobFlag(1);
	
			getOrderList();
			
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

	/*
	 * 获取一天之类的所有订单
	 */
	private void getOrderList() throws Exception
	{		
		int pageno=1;
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					Map<String, String> orderlistparams = new HashMap<String, String>();
			    	Date startdate=new Date((new Date()).getTime()-daymillis);
					Date enddate=new Date();
					orderlistparams.put("user", app_key);
					orderlistparams.put("appKey", app_key);
			        orderlistparams.put("format", format);
			        orderlistparams.put("method", "IOpenAPI.GetSaleStock");
			        orderlistparams.put("startTime", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("endTime", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("status", "0");
			        orderlistparams.put("pageIndex", String.valueOf(pageno));
			        orderlistparams.put("pageSize", pageSize);
			        
					String responseOrderListData = Utils.sendByPost(orderlistparams,app_secret,"IOpenAPI.GetSaleStock", url).replace("\"ReceiptSpec\":null","\"ReceiptSpec\":[]");
					//Log.info("responseOrderListData: "+responseOrderListData);
					System.out.println("开始时间"+startdate);
					System.out.println("结束时间"+enddate);
					
					JSONObject responseproduct = new JSONObject(responseOrderListData);
					if (!"101".equals(responseproduct.optString("Code")))
					{
						String errdesc=responseproduct.optString("Message");
						Log.error(jobName, "取订单列表失败:"+errdesc);
						k=10;
						break;
					}
					int totalCount=responseproduct.getInt("SumNum");
					
					if (totalCount==0)
					{				
						k=10;
						break;
					}
					JSONArray orderlist=responseproduct.getJSONArray("Result");
					Log.info("订单总数: "+orderlist.length());
					
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						JSONArray items = order.getJSONArray("ProSpec");
						JSONArray spec = order.getJSONArray("ReceiptSpec");
						Order o=new Order();
						o.setObjValue(o, order);
						o.setFieldValue(o,"proSpec",items);
						o.setFieldValue(o,"receiptSpec",spec);
						Log.info(o.getOrderNo()+" "+o.getStockOrder_Flag()+" "+Formatter.format(o.getAddTime(),Formatter.DATE_TIME_FORMAT));
						String sku;
						String sql="";
						if (o.getStockOrder_Flag().equals("正常"))
						{	
							
							if (!OrderManager.isCheck("检查网渠宝订单", this.getDao().getConnection(), o.getOrderId()))
							{
								if (!OrderManager.TidLastModifyIntfExists("检查优购订单", this.getDao().getConnection(), o.getOrderId(),o.getAddTime()))
								{
									OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username);
									
									for(Iterator ito=o.getProSpec().getRelationData().iterator();ito.hasNext();)
									{
										ProSpec item=(ProSpec) ito.next();
										sku=item.getProSku();
										StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), Params.tradecontactid, o.getStockOrder_Flag(),o.getOrderId(), sku, -item.getProCount(),false);
									}
								}
							}
	
						}
					}
					int totalPage = totalCount % Integer.parseInt(Params.pageSize)==0?totalCount / Integer.parseInt(Params.pageSize):totalCount>Integer.parseInt(Params.pageSize)?totalCount/Integer.parseInt(Params.pageSize):1;
					//判断是否有下一页
					if (pageno>=totalPage) break;
					pageno++;
				}
				//执行成功后不再循环
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
