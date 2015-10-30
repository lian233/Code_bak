package com.wofu.netshop.alibaba.fenxiao;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.domain.after.ReturnGoods;
import com.jd.open.api.sdk.domain.after.ReturnInfo;
import com.jd.open.api.sdk.domain.after.ReturnItem;
import com.jd.open.api.sdk.domain.order.ItemInfo;
import com.jd.open.api.sdk.domain.order.OrderInfo;
import com.jd.open.api.sdk.domain.order.OrderResult;
import com.jd.open.api.sdk.domain.order.OrderSearchInfo;
import com.jd.open.api.sdk.domain.ware.Sku;
import com.jd.open.api.sdk.request.Field;
import com.jd.open.api.sdk.request.after.AfterSearchRequest;
import com.jd.open.api.sdk.request.order.OrderSearchRequest;
import com.jd.open.api.sdk.response.after.AfterSearchResponse;
import com.jd.open.api.sdk.response.order.OrderSearchResponse;
import com.wofu.business.fenxiao.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.fenxiao.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.alibaba.fenxiao.api.ApiCallService;
import com.wofu.netshop.alibaba.fenxiao.auth.AuthService;
import com.wofu.netshop.alibaba.fenxiao.util.CommonUtil;
/**
 * 下载阿里巴巴订单线程类
 * @author Administrator
 *
 */
public class GetOrdersRunnable implements Runnable{
	private String jobName="下载阿里巴巴分销订单作业";
	private CountDownLatch watch;
	private String username="";
	private String lasttime;
	private String refundlasttime;
	private static long daymillis=24*60*60*1000L;
	private Params param;
	public GetOrdersRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
}
	public void run() {
		// TODO Auto-generated method stub
		Connection conn=null;
		try{
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("client_id", param.appkey);
		    params.put("redirect_uri", param.redirect_uri);
		    params.put("client_secret", param.secretKey);
		    params.put("refresh_token", param.refresh_token);
		    String returns=AuthService.refreshToken(param.host, params);
		    //String returns=AuthService.getToken(Params.host, params,true);
		    JSONObject access=new JSONObject(returns);
		    param.token=access.getString("access_token");
			conn=PoolHelper.getInstance().getConnection("shop");
			getOrderIdList(conn);
			//getRefund(conn);
		}catch(Throwable e){
			try {
				if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				conn.setAutoCommit(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					Log.error(username,"关闭数据库事务出错: "+e1.getMessage(),null);
				}
				Log.info(username,jobName+" "+e.getMessage(),null);
			}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.error(username,"关闭数据库连接出错  "+e.getMessage());
				}
				watch.countDown();
		}
		
	}
	
	//根据时间、状态获取订单
	public void getOrderIdList(Connection conn) throws Throwable
	{
		String lasttimeconfvalue=param.username+"取订单最新时间";
		lasttime=PublicUtils.getConfig(conn,"LastOrderTime",param.shopid);
		Log.info(param.username,"开始获取阿里巴巴订单!",null);
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
					params.put("sellerMemberId", param.sellerMemberId) ;
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					params.put("modifyStartTime",Formatter.format(startdate,Formatter.DATE_TIME_FORMAT)) ;
					params.put("modifyEndTime", Formatter.format(enddate,Formatter.DATE_TIME_FORMAT));
					params.put("pageSize","20") ;
					params.put("pageNO", String.valueOf(pageIndex)) ;
					String urlPath=CommonUtil.buildInvokeUrlPath(param.namespace,"trade.order.orderList.get",param.version,param.requestmodel,param.appkey);
					params.put("access_token", param.token);
					String responseText = ApiCallService.callApiTest(param.url, urlPath, param.secretKey, params);
					Log.info(param.username,"取订单返回数据为: "+responseText,null);
					JSONObject res=new JSONObject(responseText);
					JSONObject jo = res.getJSONObject("result");
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
								//Object param;
								try
								{
									//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
									if (Formatter.parseDate(Formatter.format(new Date(), Formatter.DATE_FORMAT),Formatter.DATE_FORMAT).
											compareTo(Formatter.parseDate(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,"LastOrderTime",param.shopid),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT),Formatter.DATE_FORMAT))>0)
									{
										try
					                	{
											String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,"LastOrderTime",param.shopid),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
											PublicUtils.setConfig(conn, "LastOrderTime", param.shopid,value);			    
					                	}catch(JException je)
					                	{
					                		Log.error(param.username,jobName, je.getMessage());
					                	}
									}
								}catch(ParseException e)
								{
									Log.error(param.username,jobName, "不可用的日期格式!"+e.getMessage());
								}
							}
							Log.info(param.username,"不存在需要处理的订单!",null);
							hasNextPage=false;								
							break ;
						}
					boolean isNeedDealList=false;
					for(int i=0; i<jresult.length(); i++){
						JSONObject deal=jresult.getJSONObject(i);
						long id = deal.getLong("id");
						//获取订单详情
						params = new Hashtable<String, String>() ;
						params.put("orderId", String.valueOf(id)) ;
						urlPath=CommonUtil.buildInvokeUrlPath(param.namespace,"trade.order.orderDetail.get",param.version,param.requestmodel,param.appkey);
						params.put("access_token", param.token);
						responseText = ApiCallService.callApiTest(param.url, urlPath, param.secretKey, params);
						Log.info(param.username,"取订单详情返回数据为: "+responseText,null);
						
						Order o=new Order();
						o.setObjValue(o, deal);
						//OrderUtils.setOrderItemCode(conn,o,orgId);
						
						//每次都只能取一整天的数据，避免重复处理，当创建时间小于等于最新处理时间时跳过
						if (Formatter.parseDate(o.getGmtModified(), Formatter.DATE_TIME_FORMAT).compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))<=0)					
							continue;
						Log.info(o.getId()+" "+o.getStatus()+" "+o.getGmtModified());
						isNeedDealList=true;
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
									OrderUtils.createInterOrder(conn,o,param.tradecontactid,param.username,param.token,param.appkey,param.secretKey,
											param.namespace,param.version,param.requestmodel,param.url,param.shopid,0);
									for(Iterator ito=o.getOrderEntries().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										//根据SKUID和商品ID得到sku
										sku=item.getSku();											
										quantity=item.getQuantity();
										
										//StockManager.deleteWaitPayStock(jobName, conn,param.tradecontactid, String.valueOf(o.getId()),sku);
										//StockManager.addSynReduceStore(jobName, conn, param.tradecontactid, o.getStatus(),String.valueOf(o.getId()), sku, -quantity.longValue(),false);
									}
								
								}
							}
	
						}
						//等待买家付款时记录锁定库存
						else if (o.getStatus().equals("waitbuyerreceive")){		//等待买家收货				
							if (!OrderManager.isCheck("检查阿里巴巴订单", conn, String.valueOf(o.getId())))
							{	
								if (!OrderManager.TidLastModifyIntfExists("检查阿里巴巴订单", conn, String.valueOf(o.getId()),Formatter.parseDate(o.getGmtModified(), Formatter.DATE_TIME_FORMAT)))
								{	
									//----------
									OrderUtils.createInterOrder(conn,o,param.tradecontactid,param.username,param.token,param.appkey,param.secretKey,
											param.namespace,param.version,param.requestmodel,param.url,param.shopid,30);
									for(Iterator ito=o.getOrderEntries().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										//根据SKUID和商品ID得到sku
										sku=item.getSku();											
										quantity=item.getQuantity();
										
										//StockManager.deleteWaitPayStock(jobName, conn,param.tradecontactid, String.valueOf(o.getId()),sku);
										//StockManager.addSynReduceStore(jobName, conn, param.tradecontactid, o.getStatus(),String.valueOf(o.getId()), sku, -quantity.longValue(),false);
									}
								
								}
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
								
							}
							for(Iterator ito=o.getOrderEntries().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								//根据SKUID和商品ID得到sku
								sku=item.getSku();	
								quantity=item.getQuantity();
								
								StockManager.deleteWaitPayStock(jobName, conn,param.tradecontactid, String.valueOf(o.getId()), sku);
								if (StockManager.WaitPayStockExists(jobName,conn,param.tradecontactid, String.valueOf(o.getId()), sku))//有获取到等待买家付款状态时才加库存
									StockManager.addSynReduceStore(jobName, conn, param.tradecontactid, o.getStatus(),String.valueOf(o.getId()), sku, quantity.longValue(),false);
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
					
								StockManager.deleteWaitPayStock(jobName, conn,param.tradecontactid, String.valueOf(o.getId()), sku);								
							}
			
						}
						//更新同步订单最新时间
		                if (Formatter.parseDate(o.getGmtModified(),Formatter.DATE_TIME_FORMAT).compareTo(modified)>0)
		                {
		                	modified=Formatter.parseDate(o.getGmtModified(),Formatter.DATE_TIME_FORMAT);
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
					}
				//如果取不到订单且取单配置时间小于当前天，则将配置天设置为配置天的第二天零点
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, "LastOrderTime", param.shopid,value);
	            	}catch(JException je)
	            	{
	            		Log.error(param.username,je.getMessage(),null);
	            	}
				}
				//执行成功后不再循环
				break;
			} catch (Throwable e) {
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(param.username,jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e),null);
				Thread.sleep(10000L);
				
			}
		}
		
	}
	
}
