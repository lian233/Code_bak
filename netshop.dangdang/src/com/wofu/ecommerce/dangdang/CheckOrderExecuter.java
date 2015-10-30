
/**
 * 
 * //检查未入订单
 * //检查取消订单
 *
 */
package com.wofu.ecommerce.dangdang;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.ecommerce.dangdang.util.CommHelper;
import com.wofu.base.job.Executer;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
public class CheckOrderExecuter extends Executer {
	private  String sendMode = "9999" ;
	private  String pageSize = "" ;
	//店铺编号
	private  String encoding = "" ;
	private  String tradecontactid = "" ;
	private  String username = "" ;
	private static String jobName="定时检查当当订单";
	private static long daymillis=24*60*60*1000L;
	private  String session="";
	private  String app_key = "";
	private  String app_Secret = "";
	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		pageSize = prop.getProperty("pageSize") ;
		encoding = prop.getProperty("encoding") ;
		tradecontactid = prop.getProperty("tradecontactid") ;
		username = prop.getProperty("username") ;
		session = prop.getProperty("session") ;
		app_key = prop.getProperty("app_key") ;
		app_Secret = prop.getProperty("app_Secret") ;

		try 
		{	
			//检查未入订单
			updateJobFlag(1);
			
			checkWaitStockOutOrders();
			//检查取消订单
			checkCancleOrders();

			UpdateTimerJob();
			
			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
	
		} catch (Exception e) {
			try {
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				if(!this.getConnection().getAutoCommit()) this.getConnection().setAutoCommit(true);
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
				
				//updateJobFlag(0);
				
				
				
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
				if(!this.getConnection().getAutoCommit()) this.getConnection().setAutoCommit(true);
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
	

	//检查未入待发货订单   101  等待发货 （商家后台页面中显示为“等待配货”状态
	//的订单也会返回为“等待发货”）
	private void checkWaitStockOutOrders() throws Exception
	{
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		
		for (int k=0;k<5;)
		{
			try 
			{
				
				while(hasNextPage)
				{
					Date temp = new Date();
					//方法名
					String methodName="dangdang.orders.list.get";
					//生成验证码 --md5;加密
					String sign = CommHelper.getSign(app_Secret, app_key, methodName, session,temp) ;
					Hashtable<String, String> params = new Hashtable<String, String>() ;
					params.put("sign", sign) ;
					params.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
					params.put("app_key",app_key);
					params.put("method",methodName);
					params.put("format","xml");
					params.put("session",session);
					params.put("sign_method","md5");
					params.put("os", "101") ;
					params.put("p", String.valueOf(pageIndex)) ;
					params.put("pageSize", pageSize) ;
					params.put("sendMode", sendMode) ;
					String responseText = CommHelper.sendRequest(Params.url,"GET",params,"");
					responseText = CommHelper.filterChar(responseText);
					Document doc = DOMHelper.newDocument(responseText, encoding);
					Element urlset = doc.getDocumentElement();
					if(DOMHelper.ElementIsExists(urlset,"Error"))
					{
						Element error = (Element) urlset.getElementsByTagName("Error").item(0);
						String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
						String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
						if(!"".equals(operCode))
						{
							Log.error("获取当当订单列表", "获取订单列表失败，操作码："+operCode+",操作结果信息："+operation);
							hasNextPage = false ;
							break ;
						}
					}
					//统计信息
					Element totalInfo = (Element) urlset.getElementsByTagName("totalInfo").item(0) ;
					//总页数
					String pageTotal = DOMHelper.getSubElementVauleByName(totalInfo, "pageTotal") ;
					//订单元素
					NodeList ordersList = urlset.getElementsByTagName("OrderInfo") ;
					for(int i = 0 ; i< ordersList.getLength() ; i++)
					{	
						try{
							//循环读取所有的订单信息
							Element orderInfo = (Element) ordersList.item(i) ;
							//订单id
							String orderID = DOMHelper.getSubElementVauleByName(orderInfo, "orderID") ;
							
							if (orderID!=null && !orderID.equals(""))
							{
								Order o = OrderUtils.getOrderByID(Params.url,orderID,session,app_key,app_Secret) ;
								
								if (!OrderManager.isCheck(jobName, this.getDao().getConnection(), orderID))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, this.getDao().getConnection(), orderID,o.getLastModifyTime()))
									{
										try
										{
											//往数据库插入订单
											OrderUtils.createInterOrder(this.getDao().getConnection(), o, tradecontactid, username);
											ArrayList<OrderItem> itemList = o.getOrderItemList() ;
											for(int j= 0 ; j < itemList.size() ; j ++)
											{
												String sku = itemList.get(j).getOuterItemID() ;
												StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrderID(),sku);
											}
											
										} catch(Exception ex)
										{
											if (this.getConnection() != null && !this.getConnection().getAutoCommit())
												this.getConnection().rollback();
											
											if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
												this.getExtconnection().rollback();
											Log.error(jobName, ex.getMessage());
										}
											
										}
									}
								}
						}catch(Exception ex){
							if (this.getConnection() != null && !this.getConnection().getAutoCommit())
								this.getConnection().rollback();
							
							if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
								this.getExtconnection().rollback();
							Log.error(jobName, ex.getMessage());
						}
						
					}
					//判断是否有下一页
					if("".equals(pageTotal) || pageTotal == null)
						pageTotal="0" ;
					if(pageIndex >= Integer.parseInt(pageTotal))
						hasNextPage = false ;
					else
						pageIndex ++ ;
				}
				
				break;
				
			}catch(Exception e)
			{
				if (++k >= 5)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}

	}

	
	//检查取消订单  -100 取消
	private  void checkCancleOrders() throws Exception
	{
		//获取取消订单，释放库存(从配置时间往后推一天)
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		
		Date begintime=new Date(System.currentTimeMillis()-daymillis);
		Date endtime=new Date();
		String lastModifyTimeStart = Formatter.format(begintime, Formatter.DATE_TIME_FORMAT) ;
		String lastModifyTimeEnd = Formatter.format(endtime, Formatter.DATE_TIME_FORMAT) ;
		
		for (int k=0;k<5;)
		{
			try 
			{
				
				while(hasNextPage)
				{
					Date temp = new Date();
					//方法名
					String methodName="dangdang.orders.list.get";
					//生成验证码 --md5;加密
					String sign = CommHelper.getSign(app_Secret, app_key, methodName, session,temp) ;
					Hashtable<String, String> params = new Hashtable<String, String>() ;
					params.put("sign", sign) ;
					params.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
					params.put("app_key",app_key);
					params.put("method",methodName);
					params.put("format","xml");
					params.put("session",session);
					params.put("sign_method","md5");
					params.put("os", "-100") ;
					params.put("lastModifyTime_end", URLEncoder.encode(lastModifyTimeEnd, encoding)) ;
					params.put("lastModifyTime_start", URLEncoder.encode(lastModifyTimeStart, encoding)) ;
					params.put("p", String.valueOf(pageIndex)) ;
					params.put("pageSize", pageSize) ;
					params.put("sendMode", sendMode) ;
					String reponseText = CommHelper.sendRequest(Params.url,"GET",params,"");
					
					Document doc = DOMHelper.newDocument(reponseText, encoding);
					Element urlset = doc.getDocumentElement();
					
					if(DOMHelper.ElementIsExists(urlset,"Error"))
					{
						Element error = (Element) urlset.getElementsByTagName("Error").item(0);
						String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
						String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
						if(!"".equals(operCode))
						{
							Log.error("获取当当订单列表", "获取订单列表失败，操作码："+operCode+",操作结果信息："+operation);
							hasNextPage = false ;
							break ;
						}
					}
	
					Element totalInfo = (Element) urlset.getElementsByTagName("totalInfo").item(0) ;
					
					String pageTotal = DOMHelper.getSubElementVauleByName(totalInfo, "pageTotal") ;
					
					NodeList ordersList = urlset.getElementsByTagName("OrderInfo") ;
					for(int i = 0 ; i< ordersList.getLength() ; i++)
					{	
						try{
							Element orderInfo = (Element) ordersList.item(i) ;
							String orderID = DOMHelper.getSubElementVauleByName(orderInfo, "orderID") ;
							
							if (orderID!=null && !orderID.equals(""))
							{	//构造一个订单对象
								Order o = OrderUtils.getOrderByID(Params.url,orderID,session,app_key,app_Secret) ;
								
					
								//取得订单里面所有的商品
								ArrayList<OrderItem> itemList = o.getOrderItemList() ;
								for(int j= 0 ; j < itemList.size() ; j ++)
								{
									String sku = itemList.get(j).getOuterItemID() ;
									//删除库存中的对应的未付款商品
									StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrderID(),sku);
								}
							}
						}catch(Exception ex){
							if (this.getConnection() != null && !this.getConnection().getAutoCommit())
								this.getConnection().rollback();
							
							if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
								this.getExtconnection().rollback();
							Log.error(jobName, ex.getMessage());
						}
						
					}
					//判断是否有下一页
					if("".equals(pageTotal) || pageTotal == null)
						pageTotal="0" ;
					if(pageIndex >= Integer.parseInt(pageTotal))
						hasNextPage = false ;
					else
						pageIndex ++ ;
				}
				break;
			}catch(Exception e)
			{
				if (++k >= 5)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
		
		
		
	}
	//是否已锁库存
	private static boolean hasLockStock(Connection conn,String tradecontactid,String orderID) throws JSQLException
	{
		boolean flag = false ;
		String sql = "select count(*) from eco_waitpaystock with(nolock) where tid='"+ orderID +"' and tradecontactid='"+ tradecontactid +"'" ;
		if(SQLHelper.intSelect(conn, sql) > 0)
			flag = true ;
		return flag ;
	}


	
}
