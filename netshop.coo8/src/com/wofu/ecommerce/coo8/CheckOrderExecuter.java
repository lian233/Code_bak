package com.wofu.ecommerce.coo8;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import com.coo8.api.Coo8Client;
import com.coo8.api.DefaultCoo8Client;
import com.coo8.api.request.order.OrdersGetRequest;
import com.coo8.api.response.order.OrdersGetResponse;
import com.coo8.open.order.Order;
import com.coo8.open.order.OrderDetail;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
public class CheckOrderExecuter extends Executer {

	private static long daymillis=24*60*60*1000L;
	
	private String url = "" ;
	
	private String appKey = "" ;
	
	private String secretKey = "" ;
	
	private String tradecontactid = "" ;
	
	private String username = "" ;
	
	private static String jobName="定时检查库巴订单";
	
	public void run()  {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		url=prop.getProperty("url") ;
		appKey=prop.getProperty("appKey") ;
		secretKey=prop.getProperty("secretKey") ;
		tradecontactid=prop.getProperty("tradecontactid") ;
		username=prop.getProperty("username") ;

		try 
		{			 
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
	
	/*
	 * 获取一天之内的所有订单
	 */
	private void checkcOrders() throws Exception
	{		
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		for(int k=0;k<5;)
		{
			try
			{
				while(hasNextPage)
				{
					Coo8Client coo8=new DefaultCoo8Client(url,appKey,secretKey);
					OrdersGetRequest orderrequest=new OrdersGetRequest();
					Date enddate=new Date();
					Date startdate=new Date(new Date().getTime()-daymillis);
					orderrequest.setStartDate(startdate);
					orderrequest.setEndDate(enddate);
					orderrequest.setPageNo(pageIndex);
					orderrequest.setPageSize(20);
					OrdersGetResponse response=coo8.execute(orderrequest);
					
					if (response.getMsg()!=null)
					{
						hasNextPage = false ;
						Log.warn(jobName,"取订单失败,错误信息:"+response.getMsg());
						break ;
					}
						
					//获取总条数
					int total=response.getTotalResult();
						//总页数
					int pageTotal=Double.valueOf(Math.ceil(total/20.0)).intValue();
						
					//返回的订单列表资料
					List<Order> orders=response.getOrders();
					//Log.info("订单总数为:　"+orders.size());
					if(orders==null){
						{
								
							Log.info(jobName,"不存在需要处理的订单!");
							hasNextPage=false;								
							break ;
						}
					}
						
					int ignoreNum=0;  //统计取回的订单中的旧订单总数--如果全部是旧订单，则更新取订单时间为数据库时间的下一天零时
					for(int i=0; i<orders.size(); i++){
						try{
							Order order=orders.get(i);
							Log.info(Formatter.format(order.getOrderChangeTime(), Formatter.DATE_TIME_FORMAT));
								
								Log.info(order.getOrderId()+" "+order.getStatus()+" "+order.getOrderChangeTime());
								
								/*
								 *1、如果状态为等待卖家发货则生成接口订单
								 *2、删除等待买家付款时的锁定库存 
								 */		
								String sku;
								long quantity;
								String sql="";
								if (order.getStatus().equals("PR")||order.getStatus().equals("PP"))
								{	
									
									if (!OrderManager.isCheck("检查库巴库订单", this.getDao().getConnection(), order.getOrderId()))
									{
										if (!OrderManager.TidLastModifyIntfExists("检查库巴库订单", this.getDao().getConnection(), order.getOrderId(),order.getOrderChangeTime()))
										{
											OrderUtils.createInterOrder(this.getDao().getConnection(),order,tradecontactid,username);
															
											for(Iterator ito=order.getOrderDetails().iterator();ito.hasNext();)
											{
												OrderDetail detail=(OrderDetail) ito.next();
												
												sku=detail.getMainId();								
												quantity=detail.getCount();
												
												StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, order.getOrderId(),sku);
												StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, order.getStatus(),order.getOrderId(), sku, -quantity,false);
											}
										
										}
									}
			
									
								}
								
						}catch(Exception ex){
							if (this.getDao() != null && !this.getDao().getConnection().getAutoCommit())
								this.getDao().rollback();
							Log.error(jobName, ex.getMessage());
							}
						}
					
					
					//判断是否有下一页
					if(pageTotal>pageIndex)
						pageIndex ++ ;
					else
					{
						hasNextPage = false ;
						break;
					}
					
					
				}//while未		
				break;
			} catch (Exception e) {
				if (++k >= 5)
					throw e;
				if (this.getDao() != null && !this.getDao().getConnection().getAutoCommit())
					this.getDao().rollback();
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
					
			
	}
		
}
