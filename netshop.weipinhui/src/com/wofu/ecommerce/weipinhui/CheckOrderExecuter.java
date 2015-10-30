package com.wofu.ecommerce.weipinhui;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import com.wofu.base.job.Executer;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weipinhui.util.CommHelper;

/**
 * 
 *检查未入订单
 *检查取消订单
 *
 */
public class CheckOrderExecuter extends Executer {

	private static String pageSize = "10" ;
	
	private static String jobName="定时检查唯品会未入订单";
	private static long daymillis=24*60*60*1000L;
	
	@Override
	public void run() {
		try 
		{	
			//读取参数
			Connection conn = this.getDao().getConnection();
			Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
			Params.tradecontactid = prop.getProperty("tradecontactid","10");
			Params.username = prop.getProperty("username","");
			Params.UpdateSettingFromDB(conn);

			
			//检查未入订单
			updateJobFlag(1);
			
			checkWaitStockOutOrders(conn);

			UpdateTimerJob();
			
			conn.close();
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
	public  void checkWaitStockOutOrders(Connection conn) throws Exception
	{
		Log.info(jobName+"任务开始!");
		int pageIndex = 1 ;  //唯品会的订单从0页算起
		boolean hasNextPage = true ;	
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					//下单时间(一天前的订单)
					Date startdate=new Date(new Date().getTime()-daymillis);
					Date enddate=new Date();
					//获取订单列表
					JSONObject jsonobj = new JSONObject();
					try {
						jsonobj.put("st_add_time", "2014-01-01");  //Formatter.format(startdate, Formatter.DATE_TIME_FORMAT)
						jsonobj.put("et_add_time", "2015-01-01");  //Formatter.format(enddate, Formatter.DATE_TIME_FORMAT)
//						jsonobj.put("st_add_time", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
//						jsonobj.put("et_add_time", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
						jsonobj.put("vendor_id", Params.vendor_id);
						jsonobj.put("order_status", 10);
						jsonobj.put("page", pageIndex);
						jsonobj.put("limit", Integer.parseInt(Params.pageSize));
					} catch (JSONException e) {
						Log.warn("准备发送数据时出错!");
						continue;
					}
					//发送请求给唯品会
					String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getOrderList", jsonobj.toString());
					if(responseText.equals("")) break;
					//把返回的数据转成json对象
					JSONObject responseObj=new JSONObject(responseText);
					//发生错误
					if(!responseObj.getString("returnCode").equals("0")){
						String ErrStrCode = responseObj.getString("returnCode");
						String ErrMsg = responseObj.getString("returnMessage");
						Log.warn("取订单出错了,错误码: "+ErrStrCode+"错误信息: "+ErrMsg);
						sleep(10000L);
						break;
					}
					//页数
					int orderNum = responseObj.getJSONObject("result").getInt("total");
					int pageTotal=0;
					if(orderNum!=0){
						pageTotal = orderNum>=Integer.parseInt(Params.pageSize) ? (orderNum %Integer.parseInt(Params.pageSize)==0?orderNum /Integer.parseInt(Params.pageSize):(orderNum /Integer.parseInt(Params.pageSize)+1)) : 1;
					}
					Log.info("当前页:" + pageIndex + ",总页数： " + pageTotal);
					//当前没订单
					if(pageTotal==0){
						Log.info("本次订单数为0");
						break;
					}
					//读取当前页的订单列表
					JSONArray ordersList = responseObj.getJSONObject("result").getJSONArray("dvd_order_list");
					for(int i = 0 ; i< ordersList.length() ; i++)
					{	
						//获取当前订单
						JSONObject orderJson = ordersList.getJSONObject(i);
						Order o = new Order();
						o.setObjValue(o,orderJson);

						//订单编号 
						String order_sn = orderJson.getString("order_id");
						//下单时间
						Date addTime = Formatter.parseDate(orderJson.getString("add_time"),Formatter.DATE_TIME_FORMAT);

						Log.info("正在处理订单:" + order_sn + "   订单状态:" + OrderUtils.getOrderStateByCode(o.getOrder_status()));
						
						//获取当前订单详情
						JSONArray itemArrayTemp = OrderUtils.getOrderItem(order_sn);
						o.setFieldValue(o, "orderItemList", itemArrayTemp);
						
						if(o != null)
						{
							//等待发货订单，创建接口订单成功，减少其它店的库存
							if("10".equals(o.getOrder_status()))
							{
								String nschaverecode = SQLHelper.strSelect(conn, "select count(*) from ns_customerorder where tid = '" + order_sn + "' and sellernick = '" + Params.username + "'");
								System.out.println("ns_customerorder have recode:" + nschaverecode);
								if (nschaverecode.equals("0") && !OrderManager.isCheck(jobName, conn, order_sn) && !OrderManager.TidLastModifyIntfExists(jobName, conn, order_sn,addTime))
								{
									Log.info("正在生成接口订单");
									try
									{
										OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
										for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
										{
											OrderItem item=(OrderItem) ito.next();
											String sku = item.getBarcode();
											//没有等待付款的状态 不需要删除未付款锁定的库存/
											StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, order_sn,sku);
											long qty= (long)item.getAmount();
											//在ecs_rationconfig表中存在机构添加一条库存同步记录(不包括自己）
											StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getOrder_status(),o.getOrder_id(), sku, qty,false);
										}
									} catch(SQLException sqle)
									{
										throw new JException("生成接口订单出错!" + sqle.getMessage());
									}
								}
								else
									Log.info("订单:" +order_sn+ "已经存在与数据库中");
							}
						}
					}
					if(pageIndex >= pageTotal)
						hasNextPage = false ;
					else
						pageIndex ++ ;
					
					n++;
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

}
