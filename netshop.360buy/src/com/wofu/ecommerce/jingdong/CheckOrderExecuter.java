package com.wofu.ecommerce.jingdong;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.domain.order.ItemInfo;
import com.jd.open.api.sdk.domain.order.OrderInfo;
import com.jd.open.api.sdk.domain.order.OrderResult;
import com.jd.open.api.sdk.domain.order.OrderSearchInfo;
import com.jd.open.api.sdk.request.order.OrderSearchRequest;
import com.jd.open.api.sdk.response.order.OrderSearchResponse;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
public class CheckOrderExecuter extends Executer {

	private static long daymillis=24*60*60*1000L;
	
	private String orderStatus = "" ;
	
	private String SERVER_URL = "" ;
	
	private String token = "" ;
	
	private String appKey = "" ;
	
	private String appSecret = "" ;
	
	private String tradecontactid = "" ;
	
	private String username = "" ;
	
	private String JBDCustomerCode="";
	
	private boolean isLBP = false ;
	private boolean isNeedGetDeliverysheetid = true ;
	
	private static String jobName="定时检查京东订单";
	
	public void run()  {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		SERVER_URL=prop.getProperty("SERVER_URL") ;
		appKey=prop.getProperty("appKey") ;
		appSecret=prop.getProperty("appSecret");
		tradecontactid=prop.getProperty("tradecontactid") ;
		username=prop.getProperty("username") ;
		JBDCustomerCode=prop.getProperty("JBDCustomerCode") ;
		isLBP = Boolean.parseBoolean(prop.getProperty("isLBP")) ;
		isNeedGetDeliverysheetid = Boolean.parseBoolean(prop.getProperty("isNeedGetDeliverysheetid","true")) ;

		try 
		{	
			token= PublicUtils.getToken(this.getDao().getConnection(), Integer.parseInt(tradecontactid));
			updateJobFlag(1);
			
			checkcOrders() ;
			
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

				//updateJobFlag(0);
				
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
	
	private void checkcOrders() throws Exception
	{
		boolean hasNextPage = true ;
		int pageIndex = 1 ;

		for (int n=0;n<5;)
		{
			try
			{
				Log.error("连接池数监测,CheckOrderExecuter连接数为"+this.getDao().getConnection().getMetaData(),"");
				Log.error("连接池数监测,CheckOrderExecuter Ex连接数为"+this.getExtdao().getConnection().getMetaData(),"");
				//取当前时间为结束时间，取当前时间前7天内的待出库订单，检查是否有漏单
				Log.info(username+",定时检查京东漏单任务开始");	
				DefaultJdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
				OrderSearchRequest request = new OrderSearchRequest();
				
				Date endDate = new Date() ;
				Date startDate = new Date(endDate.getTime()-daymillis) ;
				String startTime = Formatter.format(startDate, Formatter.DATE_TIME_FORMAT) ;
				String endTime = Formatter.format(endDate, Formatter.DATE_TIME_FORMAT) ;
				
				request.setStartDate(startTime);
				request.setEndDate(endTime);
				request.setOrderState("WAIT_SELLER_STOCK_OUT");
				request.setOptionalFields("order_id,modified,order_state");
				request.setPageSize("20");
				request.setPage(String.valueOf(pageIndex));
				
				while(hasNextPage)
				{
					//com.jd.open.api.sdk.response.order.OrderSearchResponse response = client.execute(request);
					OrderSearchResponse response = client.execute(request);
					if(!"0".equals(response.getCode()))
					{
						Log.error(jobName,"获取京东订单列表失败,状态【"+orderStatus+"】,错误信息:"+response.getCode()+","+response.getZhDesc()) ;
						hasNextPage = false ;
						break ;
					}
					
					OrderResult result = response.getOrderInfoResult() ;
					List<OrderSearchInfo> orderSerachInfoList = result.getOrderInfoList() ;
					Log.info(username+","+jobName+"本次到得的订单总数为:　"+orderSerachInfoList.size());
					for(int i = 0 ; i < orderSerachInfoList.size() ; i++)
					{	
						try{
							OrderSearchInfo info = orderSerachInfoList.get(i) ;
							//Log.info("订单号: "+info.getOrderId());
							if("WAIT_SELLER_STOCK_OUT".equalsIgnoreCase(info.getOrderState())){
								String orderID = info.getOrderId() ;
								OrderInfo order=OrderUtils.getFullTrade( orderID,SERVER_URL,token,appKey,appSecret);
								if (!OrderManager.isCheck("检查京东订单", this.getDao().getConnection(), orderID))
								{
									if (!OrderManager.TidLastModifyIntfExists("检查京东订单", this.getDao().getConnection(), orderID,Formatter.parseDate(info.getModified(),Formatter.DATE_TIME_FORMAT)))
									{
										try
										{
																	
											OrderUtils.createInterOrder(this.getDao().getConnection(),SERVER_URL,appKey,appSecret,token,
													order, tradecontactid, username,JBDCustomerCode,isLBP,isNeedGetDeliverysheetid);
											List<ItemInfo> itemList = order.getItemInfoList() ;
											for(int k = 0 ; k < itemList.size() ; k ++)
											{
												String sku = itemList.get(k).getOuterSkuId() ;
												//long qty=Integer.valueOf(itemList.get(k).getItemTotal());
												StockManager.deleteWaitPayStock("检查京东订单", this.getDao().getConnection(),tradecontactid, orderID, sku);										
																		
											}
											
										} catch(SQLException sqle)
										{
											throw new JException("生成接口订单出错!" + sqle.getMessage());
										}
									}
								}
							}
							
						}catch(Exception ex){
							if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit()){
								this.getDao().getConnection().rollback();
							}
							Log.error(jobName, ex.getMessage());
						}
					}
					//判断是否有下一页
					if(orderSerachInfoList == null || orderSerachInfoList.size() == 0)
						hasNextPage = false ;
					else
						pageIndex ++ ;
					
					request.setPage(String.valueOf(pageIndex));
				}
				
				break;
				
			}
			catch(Exception e)
			{
				if (++n >= 5)
					throw e;
				if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit()){
					this.getDao().getConnection().rollback();
				}
				Log.warn(jobName+" ,远程连接失败[" + n + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				e.printStackTrace();
				//Thread.sleep(10000L);
			}
		}
		Log.info(username+",定时检查京东漏单任务完成");
	}
		
}
