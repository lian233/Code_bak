package com.wofu.ecommerce.ylw;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.wofu.ecommerce.ylw.util.CommHelper;

/**
 * 
 *检查未入订单
 *检查取消订单
 *
 */
public class CheckOrderExecuter extends Executer {

	private static String pageSize = "10" ;
	
	private static String jobName="定时检查苏宁未入订单";
	private static long daymillis=24*60*60*1000L;
	private static String appKey = "" ;
	private static String appsecret = "" ;
	private static String format = "" ;
	private static String url = "" ;
	private static String tradecontactid = "" ;
	private static String username = "" ;
	
	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		pageSize = prop.getProperty("pageSize") ;
		appKey = prop.getProperty("appkey") ;
		appsecret = prop.getProperty("appsecret") ;
		format = prop.getProperty("format") ;
		url = prop.getProperty("url") ;
		tradecontactid = prop.getProperty("tradecontactid") ;
		username = prop.getProperty("username") ;

		try 
		{	
			//检查未入订单
			updateJobFlag(1);
			
			checkWaitStockOutOrders();
			//检查取消订单
			//checkCancleOrders();

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
				
				updateJobFlag(0);
				
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
	

	/**检查未入待发货订单   orderStatus=10  等待发货 
	 *这里检查一天时间的未入订单
	**/
	public  void checkWaitStockOutOrders() throws Exception
	{
		Log.info(jobName+"任务开始!");
		Connection conn= this.getDao().getConnection();
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		
		for (int k=0;k<5;)
		{
			try 
			{
				
				while(hasNextPage)
				{
			        // 请求的api方法名
			        String apimethod ="suning.custom.order.query";
			        HashMap<String,String> reqMap = new HashMap<String,String>();
			        reqMap.put("orderStatus", "10");
			        reqMap.put("startTime", Formatter.format(new Date(System.currentTimeMillis()-daymillis), Formatter.DATE_TIME_FORMAT));
			        reqMap.put("endTime",Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT) );
			        reqMap.put("pageNo", String.valueOf(pageIndex));
			        reqMap.put("pageSize", pageSize);
			        HashMap<String,String> map = new HashMap<String,String>();
			        map.put("appSecret", appsecret);
			        map.put("appMethod", apimethod);
			        map.put("format", format);
			        map.put("versionNo", "v1.2");
			        map.put("appKey", appKey);
			        //发送请求
					String responseText = CommHelper.doRequest(map,url);
					//Log.info(jobName+" 返回数据 ："+responseText);
					//把返回的数据转成json对象
					JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
					//错误对象 
					if(!responseObj.isNull("sn_error")){   //发生错误
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if(operCode.indexOf("no-result")!=-1) {  //没有数据直接退出方法体
							Log.error("获取苏宁订单列表", "获取订单列表失败，操作码："+operCode);
							return;
						}
						hasNextPage = false ;
						break ;
						
					}
					//统计信息
					JSONObject totalInfo = responseObj.getJSONObject("sn_head");
					//总页数
					String pageTotal = String.valueOf(totalInfo.get("pageTotal"));
					//订单元素
					JSONArray ordersList = responseObj.getJSONObject("sn_body").getJSONArray("orderQuery");
					for(int i = 0 ; i< ordersList.length() ; i++)
					{	//某个订单
						JSONObject orderInfo = ordersList.getJSONObject(i);
						//订单编号 
						String orderCode = (String)orderInfo.get("orderCode");
						//订单商品集合
						JSONArray items = orderInfo.getJSONArray("orderDetail");
						//构造一个订单对象
						Order o = new Order();
						o.setObjValue(o, orderInfo);
						o.setFieldValue(o, "orderItemList", items);
						String orderLineStatus=o.getOrderLineStatus();
						String returnOrderFlag= o.getReturnOrderFlag();
						//设置商品的某些属性
						for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
						{
							OrderItem item=(OrderItem) ito.next();
							
							if("".equals(orderLineStatus)){
								o.setOrderLineStatus(String.valueOf(item.getOrderLineStatus()));
							}
							if("".equals(returnOrderFlag)){
								o.setReturnOrderFlag(String.valueOf(item.getReturnOrderFlag()));
							}
						}
						Date createTime = o.getOrderSaleTime();
						if(o != null)
						{
							Log.info("订单号【"+ o.getOrderCode() +"】,状态【"+ OrderUtils.getOrderStateByCode(o.getOrderLineStatus()) +"】,最后修改时间【"+ Formatter.format(createTime,Formatter.DATE_TIME_FORMAT) +"】") ;
							if("0".equals(o.getReturnOrderFlag())){   //正常订单
								//如果是等待发货订单，创建接口订单成功，减少其它店的库存
								if("10".equals(o.getOrderLineStatus()))
								{
									if (!OrderManager.isCheck(jobName, conn, orderCode))
									{
										if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderCode,createTime))
										{
											try
											{
												OrderUtils.createInterOrder(conn, o, tradecontactid, username);
												for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
												{
													OrderItem item=(OrderItem) ito.next();
													String sku = item.getItemCode() ;
													StockManager.deleteWaitPayStock(jobName, conn,tradecontactid, orderCode,sku);
												}
												
											} catch(SQLException sqle)
											{
												throw new JException("生成接口订单出错!" + sqle.getMessage());
											}
										}
									}    
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
					
				}
				Log.info(jobName+"执行完毕!");
				break;
			}catch(Exception e)
			{
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
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
					Hashtable<String, String> params = new Hashtable<String, String>() ;
					params.put("sign", sign) ;
					params.put("timestamp",URLEncoder.encode(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT),"GBK"));
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
							Log.error("获取苏宁订单列表", "获取订单列表失败，操作码："+operCode+",操作结果信息："+operation);
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
