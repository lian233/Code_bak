package com.wofu.ecommerce.jumei;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import com.wofu.base.job.Executer;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

/**
 * 
 *检查未入订单
 *检查取消订单
 *
 */
public class CheckOrderExecuter extends Executer {

	private static String jobname="定时检查聚美未入订单";
	private static final long Hourmillis=20*60*1000L;
	private  String client_id = "" ;
	private  String client_key = "" ;
	private static String url = "" ;
	private String tradecontactid = "" ;
	private static String username = "" ;
	private  String signkey = "" ;
	private  String encoding = "" ;
	
	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		client_id = prop.getProperty("client_id") ;
		client_key = prop.getProperty("client_key") ;
		url = prop.getProperty("url") ;
		tradecontactid = prop.getProperty("tradecontactid") ;
		username = prop.getProperty("username") ;
		signkey = prop.getProperty("signkey") ;
		encoding = prop.getProperty("encoding") ;

		try 
		{	
			//检查未入订单
			updateJobFlag(1);
			
			checkWaitStockOutOrders();
			//检查取消订单
			//checkCancleOrders();

			UpdateTimerJob();
			
			Log.info(jobname, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
	
		} catch (Exception e) {
			try {
				
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
				
				updateJobFlag(0);
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobname,"回滚事务失败");
			}
			Log.error(jobname,"错误信息:"+Log.getErrorMessage(e));
			
			
			Log.error(jobname, "执行作业失败 [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobname,"更新处理标志失败");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobname,"关闭数据库连接失败");
			}
		}
		
	
	}
	

	/**检查未入待发货订单   orderStatus=10  等待发货 
	 *这里检查一天时间的未入订单
	**/
	public  void checkWaitStockOutOrders() throws Exception
	{
		Log.info(jobname+"任务开始!");
		Connection conn= this.getDao().getConnection();
		for (int k=0;k<10;)
		{
			try 
			{
					String method="Order/GetOrder";
					Date startdate=new Date(new Date().getTime()-Hourmillis);
					Date enddate=new Date();
					Map<String, String> paramMap = new HashMap<String, String>();
			        //系统级参数设置
			        paramMap.put("client_id", client_id);
			        paramMap.put("client_key", client_key);
			        paramMap.put("start_date", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        paramMap.put("end_date", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        paramMap.put("status", "2,7");
			       
			        String sign=JuMeiUtils.getSign(paramMap, signkey, encoding);
			        
			        paramMap.put("sign", sign);
			        
			        String responseData=CommHelper.sendRequest(url+method, paramMap, "", encoding);
			        //Log.info("聚美返回订单数据:　"+responseData);
			        
					JSONObject responseresult=new JSONObject(responseData);
					
					int errorCount=responseresult.getInt("error");
					
					if (errorCount>0)
					{
						String errdesc=responseresult.getString("message");
						
						k=10;
						throw new JException(errdesc);
						
					}
					
					JSONArray orderlist=responseresult.getJSONObject("result").getJSONArray("response");
								
					if (orderlist.length()==0)
					{				
						k=10;
						break;
					}
									
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
											
						Order o=new Order();
					
						o.setObjValue(o, order);
					
						JSONObject receiverinfojsobj=order.getJSONObject("receiver_infos");
					
						o.setObjValue(o.getReceiver_info(),receiverinfojsobj);
										
						Log.info(o.getOrder_id()+" "+o.getStatus()+" "+Formatter.format(new Date(o.getTimestamp()*1000),Formatter.DATE_TIME_FORMAT));
						/*
						 *1、如果状态为等待卖家发货则生成接口订单
						 *2、删除等待买家付款时的锁定库存 
						 */		
						String sku;
						String sql="";
						if (o.getStatus()== 2 ||o.getStatus()== 7)
						{	
							
							if (!OrderManager.isCheck("检查聚美优品订单", conn, o.getOrder_id()))
							{
								if (!OrderManager.TidLastModifyIntfExists("检查聚美优品订单", conn, o.getOrder_id(),new Date(o.getTimestamp()*1000)))
								{
									OrderUtils.createInterOrder(conn,o,tradecontactid,username);
									
									for(Iterator ito=o.getProduct_infos().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getUpc_code();
										
										StockManager.deleteWaitPayStock(jobname, conn,tradecontactid, o.getOrder_id(),sku);
										StockManager.addSynReduceStore(jobname, conn, tradecontactid, String.valueOf(o.getStatus()),String.valueOf(o.getOrder_id()), sku, -item.getQuantity(),false);
									}
								}
							}
	
							//等待买家付款时记录锁定库存
						}
						
					}
				k=10;
				break;
				//执行成功后不再循环
			}catch(Exception e)
			{
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(jobname+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}

	}

	
	//检查取消订单  -100 取消
	/*private  void checkCancleOrders() throws Exception
	{
		//获取取消订单，释放库存(从配置时间往后推一天)
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		
		Date begintime=new Date(System.currentTimeMillis()-daymillis);
		Date endtime=new Date();
		String lastModifyTimeStart = Formatter.format(begintime, Formatter.DATE_TIME_FORMAT) ;
		String lastModifyTimeEnd = Formatter.format(endtime, Formatter.DATE_TIME_FORMAT) ;
		
		for (int k=0;k<10;)
		{
			try 
			{
				
				while(hasNextPage)
				{

					//方法名
					String methodName="dangdang.orders.list.get";
					//生成验证码 --md5;加密
					String sign = CommHelper.getSign(app_Secret, app_key, methodName, session) ;
					Hashtable<String, String>  = new Hashtable<String, String>() ;
					.put("sign", sign) ;
					.put("timestamp",URLEncoder.encode(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT),"GBK"));
					.put("app_key",app_key);
					.put("method",methodName);
					.put("format","xml");
					.put("session",session);
					.put("sign_method","md5");
					.put("os", "-100") ;
					.put("lastModifyTime_end", URLEncoder.encode(lastModifyTimeEnd, encoding)) ;
					.put("lastModifyTime_start", URLEncoder.encode(lastModifyTimeStart, encoding)) ;
					.put("p", String.valueOf(pageIndex)) ;
					.put("pageSize", pageSize) ;
					.put("sendMode", sendMode) ;
					String reponseText = CommHelper.sendRequest(.url,"GET",,"");
					
					Document doc = DOMHelper.newDocument(reponseText, encoding);
					Element urlset = doc.getDocumentElement();
					
					if(DOMHelper.ElementIsExists(urlset,"Error"))
					{
						Element error = (Element) urlset.getElementsByTagName("Error").item(0);
						String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
						String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
						if(!"".equals(operCode))
						{
							Log.error("获取聚美订单列表", "获取订单列表失败，操作码："+operCode+",操作结果信息："+operation);
							hasNextPage = false ;
							break ;
						}
					}
	
					Element totalInfo = (Element) urlset.getElementsByTagName("totalInfo").item(0) ;
					
					String pageTotal = DOMHelper.getSubElementVauleByName(totalInfo, "pageTotal") ;
					
					NodeList ordersList = urlset.getElementsByTagName("OrderInfo") ;
					for(int i = 0 ; i< ordersList.getLength() ; i++)
					{
						Element orderInfo = (Element) ordersList.item(i) ;
						String orderID = DOMHelper.getSubElementVauleByName(orderInfo, "orderID") ;
						
						if (orderID!=null && !orderID.equals(""))
						{	//构造一个订单对象
							Order o = OrderUtils.getOrderByID(.url,orderID,session,app_key,app_Secret) ;
							
				
							//取得订单里面所有的商品
							ArrayList<OrderItem> itemList = o.getOrderItemList() ;
							for(int j= 0 ; j < itemList.size() ; j ++)
							{
								String sku = itemList.get(j).getOuterItemID() ;
								//删除库存中的对应的未付款商品
								StockManager.deleteWaitPayStock(jobname, this.getDao().getConnection(),tradecontactid, o.getOrderID(),sku);
							}
	
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
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
		
		
		
	}*/



	
}
