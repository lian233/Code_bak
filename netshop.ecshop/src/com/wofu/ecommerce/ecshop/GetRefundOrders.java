package com.wofu.ecommerce.ecshop;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import com.wofu.base.systemmanager.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ecshop.util.CommHelper;
/**
 * 
 * 获取ecshop退换货单作业
 *
 */
public class GetRefundOrders extends Thread {

	private static String jobName = "获取ecshop退换货单作业";
	private static long daymillis=24*60*60*1000L;
	private static String lastRefundTime=Params.username+"取退货最新时间";
	private static String lastRefundvalue="";
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	public void run() {
		
		Log.info(jobName, "启动[" + jobName + "]模块");
		do {
			Connection connection = null;
			Ecshop.setCurrentDate_getRefundOrder(new Date());
			try {
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.ecshop.Params.dbname);
				lastRefundvalue = PublicUtils.getConfig(connection, lastRefundTime, "");
				getRefund(connection) ;
				
			} catch (Exception e) {
				try {
					e.printStackTrace() ;
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "回滚事务失败");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {

				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000 * Params.timeInterval))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	

	public void getRefund(Connection conn) throws Exception
	{
		int pageIndex = 1 ;
		boolean hasNextPage = true ;	
		
		Date modified=Formatter.parseDate(lastRefundvalue,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					Date startdate=new Date(Formatter.parseDate(lastRefundvalue,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lastRefundvalue,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//方法名
					String apimethod="search_order_list";
					HashMap<String,Object> reqMap = new HashMap<String,Object>();
			        reqMap.put("last_modify_st_time", startdate.getTime()/1000L);
			        reqMap.put("last_modify_en_time",enddate.getTime()/1000L);
			        reqMap.put("pages", String.valueOf(pageIndex));
			        reqMap.put("counts", Params.pageSize);
			        reqMap.put("return_data", "json");
			        reqMap.put("act", apimethod);
			        reqMap.put("api_version", "1.0");
			        
			        Log.info("第"+pageIndex+"页");
					String responseText = CommHelper.doRequest(reqMap,Params.url);
					//Log.info("返回数据为:　"+responseText);
					//把返回的数据转成json对象
					JSONObject responseObj= new JSONObject(responseText);
					  //sn_error
					if(!"success".equals(responseObj.getString("result"))){   //发生错误
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if("biz.handler.data-get:no-result".equals(operCode)){ //没有结果
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lastRefundTime,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lastRefundTime,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lastRefundTime, value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(jobName, je.getMessage());
				                	}
								}
								return;
							}catch(ParseException e)
							{
								Log.error(jobName, "不可用的日期格式!"+e.getMessage());
							}
							Log.info("没有可用的订单!");
						}else{
							Log.warn("取订单出错了,错误码: "+operCode);
						}
						
						break;
					}
					
					
					
					JSONObject orderInfos = responseObj.getJSONObject("info");

					//总页数
					
					String orderTotal = String.valueOf(orderInfos.getString("counts"));
					int orderTotaltemp = Integer.parseInt(orderTotal);
					int pageTotalTemp  = Double.valueOf(Math.ceil(orderTotaltemp/Double.parseDouble(Params.pageSize))).intValue();
					String pageTotal =String.valueOf(pageTotalTemp);
					Log.info("总订单数为： "+orderTotal);
					Log.info("总页数为： "+pageTotal);
					if (orderTotal==null || orderTotal.equals("") || orderTotal.equals("0"))
					{				
						if (n==1)		
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lastRefundTime,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lastRefundTime,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lastRefundTime, value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(jobName, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobName, "不可用的日期格式!"+e.getMessage());
							}
						}
						break;
					}
					//订单元素
					JSONArray ordersList = orderInfos.getJSONArray("data_info");
					for(int i = 0 ; i< ordersList.length() ; i++)
					{	//某个订单
						JSONObject orderInfo = ordersList.getJSONObject(i);
						int returnOrderCount =0;
						//订单编号 
						String orderCode = (String)orderInfo.get("order_sn");
						//订单商品集合
						JSONArray items = orderInfo.getJSONArray("shop_info");
						//构造一个订单对象
						Order o = new Order();
						o.setObjValue(o, orderInfo);
						o.setFieldValue(o, "shop_info", items);
						Log.info("发货状态:　"+o.getShipping_status()+"付款状态: "+o.getPay_status());
						if(o != null)
						{	
							if("4".equals(o.getPay_status())){   //正常订单
								Log.info("订单号:　"+o.getOrder_sn());
								//如果是等待发货订单，创建接口订单成功，减少其它店的库存
									
										try
											{
												OrderUtils.createRefundOrder(jobName,conn, Params.tradecontactid, o);
												
											} catch(Exception sqle)
											{
												throw new JException("生成退货订单出错!" + sqle.getMessage());
											}
											//如果当前订单时间大于开始取订单时间，则更新下次取订单时间(现在取订单列表最后修改时间)
											//更新同步订单最新时间
											
							                if (new Date(o.getAdd_time()*1000L).compareTo(modified)>0)
							                {
							                	modified=new Date(o.getAdd_time()*1000L);
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
					
					n++;
						
				}
				
				if (modified.compareTo(Formatter.parseDate(lastRefundvalue, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lastRefundTime, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				break;
			} catch (Exception e) 
			{
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
		Log.info("本次取ecshop退货订单任务处理完毕!");
	}

}