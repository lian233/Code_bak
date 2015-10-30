package com.wofu.ecommerce.qqbuy;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import java.util.Properties;

import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.timer.TimerJob;
import com.wofu.base.job.Executer;
public class CheckOrderExecuter extends Executer {

	private String dbname="";
	
	private String jobname="";
	
	private static long daymillis=24*60*60*1000L;
	
	private String orderStatus = "" ;
	
	private String accessToken = "" ;
	
	private String appOAuthID = "" ;
	
	private String secretOAuthKey = "" ;
	
	private String cooperatorId = "" ;
	
	private String uin = "" ;
	
	private String encoding = "" ;
	
	private String format = "" ;
	
	private String tradecontactid = "" ;
	
	private String username = "" ;
	
	private boolean isNeedInvoice = false ;
	
	private Date nextactive=null;
	

	@Override
	public void execute() throws Exception {
		TimerJob job=(TimerJob) this.getExecuteobj();
		Properties prop=StringUtil.getStringProperties(job.getParams());

		dbname=prop.getProperty("dbname");
		jobname = prop.getProperty("jobname") ;
		orderStatus=prop.getProperty("orderStatus") ;
		accessToken=prop.getProperty("accessToken") ;
		appOAuthID=prop.getProperty("appOAuthID") ;
		secretOAuthKey=prop.getProperty("secretOAuthKey") ;
		cooperatorId=prop.getProperty("cooperatorId") ;
		uin=prop.getProperty("uin") ;
		encoding=prop.getProperty("encoding") ;
		format=prop.getProperty("format") ;
		tradecontactid=prop.getProperty("tradecontactid") ;
		username=prop.getProperty("username") ;
		isNeedInvoice = Boolean.parseBoolean(prop.getProperty("isNeedInvoice","false")) ;
		
		nextactive=job.getNextactive();

		Connection conn=null;
		try 
		{			 
			conn= PoolHelper.getInstance().getConnection(dbname);
			checkcOrders(conn) ;
		}catch (Exception e) 
		{
			Log.error("检查QQ网购未入订单","错误信息:" + e.getMessage());
			throw new JException("访问远程方法失败,错误信息:" + e.getMessage());
			
		} finally 
		{
			try 
			{
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				throw new JException("关闭数据库连接失败");
			}
		}
	}
	
	private void checkcOrders(Connection conn) throws Exception
	{
		int orderCount = 0 ;
		for (int i=0;i<100;)
		{
			try
			{
				//取当前时间为结束时间，取当前时间前7天内的待出库订单，检查是否有漏单
				Date endDate = new Date() ;
				Date startDate = new Date(endDate.getTime()-7*daymillis) ;
				String startTime = Formatter.format(startDate, Formatter.DATE_TIME_FORMAT) ;
				String endTime = Formatter.format(endDate, Formatter.DATE_TIME_FORMAT) ;
				Log.info("startTime="+startTime) ;
				Log.info("endTime="+endTime) ;
				
				Hashtable<String, String> params = new Hashtable<String, String>() ;
				params.put("accessToken", accessToken) ;
				params.put("appOAuthID", appOAuthID) ;
				params.put("secretOAuthKey", secretOAuthKey) ;
				params.put("cooperatorId", cooperatorId) ;
				params.put("uin", uin) ;
				params.put("encoding", encoding) ;
				params.put("format", format) ;
				
				ArrayList<Hashtable<String, String>> orderIdList = new ArrayList<Hashtable<String,String>>() ;
				String orderStateArray[] = orderStatus.split(":") ;
				for(int k = 0 ; k < orderStateArray.length ; k++)
					orderIdList.addAll(OrderUtils.getOrderIdList(jobname, orderStateArray[k], "UPDATE", startTime, endTime, params)) ;
				
				//遍历每个订单
				for(int j = 0 ; j < orderIdList.size() ; j++)
				{
					Hashtable<String, String> ht = orderIdList.get(j) ;
					String orderID = ht.get("dealId") ;
					String lastUpdateTime = ht.get("lastUpdateTime") ;
					Order order = OrderUtils.getOrderByID(jobname, orderID, params) ;
					if(order == null)
					{
						Log.error(jobname, "查询QQ网购订单详细信息失败,订单号【"+ orderID +"】") ;
						return ;
					}
					String state = order.getDealState() ;
					Log.info("订单号【"+ orderID +"】,状态【"+ state +"】,最后修改时间【"+ lastUpdateTime +"】") ;
					//等待买家付款
					if("STATE_POL_WAIT_PAY".equals(state))
					{
						for(int k=0;k<order.getItemList().size();k++)
						{
							OrderItem item = order.getItemList().get(k) ;
							String sku = item.getSkuLocalCode() ;
							long qty = item.getBuyNum() ;
							StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, order.getDealId(), sku, qty);
							StockManager.addSynReduceStore(jobname, conn, tradecontactid, order.getDealState(),order.getDealId(), sku, -qty,false);
						}
					}
					//等待发货
					else if("STATE_WAIT_SHIPPING".equalsIgnoreCase(state))
					{
						if(OrderUtils.createInterOrder(conn, order, tradecontactid, username, state,lastUpdateTime,isNeedInvoice))
						{
							//创建订单成功减其它店库存
							for(int k=0;k<order.getItemList().size();k++)
							{
								OrderItem item = order.getItemList().get(k) ;
								String sku = item.getSkuLocalCode() ;
								StockManager.deleteWaitPayStock(jobname, conn,tradecontactid, order.getDealId(),sku);
							}
							orderCount ++ ;
						}
					}
				}
				
				Log.info("检查QQ网购未入订单成功，本次新入订单数："+orderCount) ;
				i=100 ;
			}
			catch(Exception e)
			{
				if (++i >= 100)
					throw e;
				Log.warn("远程连接失败[" + i + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				e.printStackTrace() ;
				Thread.sleep(10000L);
			}
		}
	}
		
}
