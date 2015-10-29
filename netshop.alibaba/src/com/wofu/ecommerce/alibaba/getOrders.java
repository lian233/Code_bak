package com.wofu.ecommerce.alibaba;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.alibaba.api.ApiCallService;
import com.wofu.ecommerce.alibaba.auth.AuthService;
import com.wofu.ecommerce.alibaba.util.CommonUtil;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;
public class getOrders extends Thread {

	private static String jobName = "获取阿里巴巴订单作业";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private static String apiName="trade.order.orderList.get";
	
	private String lasttime;

	public void run() {
		
		Log.info(jobName, "启动[" + jobName + "]模块");
		
		//获取授权令牌
		Connection connection = null;
		do {		
			is_importing = true;
			String orgId=null;
			//改变获取订单的静态时间 
			Alibaba.setCurrentDate(new Date());
			try {			
		    	Hashtable<String, String> params = new Hashtable<String, String>() ;
				params.put("client_id", Params.appkey);
			    params.put("redirect_uri", Params.redirect_uri);
			    params.put("client_secret", Params.secretKey);
			    params.put("refresh_token", Params.refresh_token);
			    String returns=AuthService.refreshToken(Params.host, params);
			    //String returns=AuthService.getToken(Params.host, params,true);
			    JSONObject access=new JSONObject(returns);
		    	Params.token=access.getString("access_token");
		    	
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				if(orgId==null){
					String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
					orgId=SQLHelper.strSelect(connection,sql);
				}							
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			
				getOrderList(connection,orgId);
				
				
			} catch (Throwable e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Throwable e1) {
					Log.error(jobName, "回滚事务失败");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Throwable e) {
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	
	/*
	 * 获取一天之内的所有订单
	 */
	private void getOrderList(Connection conn,String orgId) throws Throwable
	{		
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<5;)
		{
			try
			{
				while(hasNextPage)
				{
						Hashtable<String, String> params = new Hashtable<String, String>() ;
						params.put("sellerMemberId", Params.sellerMemberId) ;
						Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
						Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
						params.put("modifyStartTime",Formatter.format(startdate,Formatter.DATE_TIME_FORMAT)) ;
						params.put("modifyEndTime", Formatter.format(enddate,Formatter.DATE_TIME_FORMAT));
						params.put("pageSize","20") ;
						params.put("pageNO", String.valueOf(pageIndex)) ;
						String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,apiName,Params.version,Params.requestmodel,Params.appkey);
						params.put("access_token", Params.token);
						String responseText = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
						Log.info("取订单起始时间:"+Formatter.format(startdate,Formatter.DATE_TIME_FORMAT));
						Log.info("取订单返回数据为: "+responseText);
						JSONObject res=new JSONObject(responseText);
						JSONObject jo = res.getJSONObject("result");
						/**
						if (!jo.getBoolean("success"))
						{
							hasNextPage = false ;
							Log.warn(jobName,"取订单失败,错误信息:"+res.optString("message"));
							break ;
						}**/
						
						//获取总条数
						int total=jo.getInt("total");
						//总页数
						int pageTotal=total%20==0?total/20==0?1:total/20:total/20+1;//Double.valueOf(Math.ceil(total/20)).intValue();
						Log.info("总页数为: "+pageTotal);
						
						//返回的订单列表资料
						JSONArray jresult=jo.getJSONArray("toReturn");
						
						if(jresult.length()==0){
							
							if (pageIndex==1)		
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
				                	}catch(Throwable je)
				                	{
				                		Log.error(jobName, je.getMessage());
				                	}
								}
								
								Log.info(jobName,"不存在需要处理的订单!");
								hasNextPage=false;								
								break ;
							}
						}
						
						boolean isNeedDealList=false;
						for(int i=0; i<jresult.length(); i++){
							JSONObject deal=jresult.getJSONObject(i);
							long id = deal.getLong("id");
							//获取订单详情
							params = new Hashtable<String, String>() ;
							params.put("orderId", String.valueOf(id)) ;
							urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"trade.order.orderDetail.get",Params.version,Params.requestmodel,Params.appkey);
							params.put("access_token", Params.token);
							responseText = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
							Log.info("取订单详情返回数据为: "+responseText);
							
							Order o=new Order();
							o.setObjValue(o, deal);
							OrderUtils.setOrderItemSKU(conn,o,orgId);//setsku
							
							//每次都只能取一整天的数据，避免重复处理，当创建时间小于等于最新处理时间时跳过
							if (Formatter.parseDate(o.getGmtModified(), Formatter.DATE_TIME_FORMAT).compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))<=0)					
								continue;
						
							Log.info(o.getId()+" "+o.getStatus()+" "+o.getGmtModified());
							
							
							isNeedDealList=true;
							
							 //*1、如果状态为等待卖家发货则生成接口订单
							 //*2、删除等待买家付款时的锁定库存 
							 		
							String sku;
							Double quantity;
							String sql="";
							if (o.getStatus().equals("waitsellersend"))
							{	
								
								if (!OrderManager.isCheck("检查阿里巴巴订单", conn, String.valueOf(o.getId())))
								{	
									if (!OrderManager.TidLastModifyIntfExists("检查阿里巴巴订单", conn, String.valueOf(o.getId()),Formatter.parseDate(o.getGmtModified(), Formatter.DATE_TIME_FORMAT)))
									{	
										//----------
										OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username,Params.token,Params.appkey,Params.secretKey);
														
										for(Iterator ito=o.getOrderEntries().getRelationData().iterator();ito.hasNext();)
										{
											OrderItem item=(OrderItem) ito.next();
											//根据SKUID和商品ID得到sku
											sku=item.getSku();											
											quantity=item.getQuantity();
											
											StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, String.valueOf(o.getId()),sku);
											StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getStatus(),String.valueOf(o.getId()), sku, -quantity.longValue(),false);
										}
									
									}
								}
		
							}
							//等待买家付款时记录锁定库存
							else if (o.getStatus().equals("waitbuyerpay")){						
								for(Iterator ito=o.getOrderEntries().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									//根据SKUID和商品ID得到sku
									sku=item.getSku();	
									quantity=item.getQuantity();
									
									StockManager.addWaitPayStock(jobName, conn,Params.tradecontactid,  String.valueOf(o.getId()),sku,quantity.longValue());
									StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getStatus(),String.valueOf(o.getId()), sku, -quantity.longValue(),false);
								}
								
							}
							//付款以前，卖家或买家主动关闭交易
							//释放等待买家付款时锁定的库存
							else if (o.getStatus().equals("cancel"))
							{
								//大唐的取消订单也要进入系统
								if (!OrderManager.TidLastModifyIntfExists("检查阿里巴巴订单", conn, String.valueOf(o.getId()),Formatter.parseDate(o.getGmtModified(), Formatter.DATE_TIME_FORMAT)))
								{
									//---
									//OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username,Params.token,Params.appkey,Params.secretKey);
									//取消订单
									sql="declare @ret int; execute  @ret = IF_CancelCustomerOrder '" + o.getId() + "';select @ret ret;";									//欧培测试
									int resultCode =SQLHelper.intSelect(conn, sql) ;
									//取消订单失败
									if(resultCode == 0)
									{
										Log.info("订单未审核-取消成功,单号:"+o.getId()+"");
									}else if(resultCode == 1)
									{
										Log.info("订单已审核-截单,单号:"+o.getId()+"");
									}else if(resultCode ==2)
									{
										Log.info("订单已经出库-取消失败,单号:"+o.getId()+"");
									}else if(resultCode ==3)
									{
										Log.info("订单不存在或已取消-取消失败,单号:"+o.getId()+"");
									}
									else
									{
										Log.info("取消失败,单号:"+o.getId()+"");
									}
								}
								for(Iterator ito=o.getOrderEntries().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									//根据SKUID和商品ID得到sku
									sku=item.getSku();	
									quantity=item.getQuantity();
									
									StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, String.valueOf(o.getId()), sku);
									if (StockManager.WaitPayStockExists(jobName,conn,Params.tradecontactid, String.valueOf(o.getId()), sku))//有获取到等待买家付款状态时才加库存
										StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getStatus(),String.valueOf(o.getId()), sku, quantity.longValue(),false);
								}					
					
							}
							//已完成
							else if (o.getStatus().equals("success")||o.getStatus().equals("signinsuccess"))
							{
								for(Iterator ito=o.getOrderEntries().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									//根据SKUID和商品ID得到sku
									sku=item.getSku();	
						
									StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, String.valueOf(o.getId()), sku);								
								}
				
							}
							//更新同步订单最新时间
			                if (Formatter.parseDate(o.getGmtModified(),Formatter.DATE_TIME_FORMAT).compareTo(modified)>0)
			                {
			                	modified=Formatter.parseDate(o.getGmtModified(),Formatter.DATE_TIME_FORMAT);
			                }
							
						}//for未
						
					//判断是否有下一页
					if(pageTotal>pageIndex)
						pageIndex ++ ;
					else
					{
						hasNextPage = false ;
						break;
					}
					
					
				}//while未		
				
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
		        	{
		        		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
		        		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
		        	}catch(Throwable je)
		        	{
		        		Log.error(jobName,je.getMessage());
		        	}
				}
			break;
			} catch (Throwable e) {
				e.printStackTrace();
				if (++k >= 5)
					throw e;
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
		Log.info("本次订单处理完毕!");
	}
	
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}
	
	

}
