package com.wofu.ecommerce.jingdong;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class getOrders extends Thread {
	private static String jobName = "获取京东订单作业的";
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	private static String refundlasttimeconfvalue=Params.username+"取退货订单最新时间";
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private static long daymillis=24*60*60*1000L;
	private String lasttime;
	private String refundlasttime;
	private boolean is_importing=false;

	public void run() {
		Log.info(jobName, "启动[" + jobName + "]模块");
		do {
			Connection connection = null;
			is_importing = true;
			try {
				Jingdong.setCurrentDate_getOrder(new Date());
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
				Params.token = PublicUtils.getToken(connection, Integer.parseInt(Params.tradecontactid));
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				refundlasttime=PublicUtils.getConfig(connection,refundlasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				Log.error("连接池数监测,getOrders连接数为"+connection.getMetaData().toString(),"");
				getOrderIdList(connection);
				//获取京东退货订单  百丽停
				getRefund(connection) ;
			} catch (Throwable e) {
				try {
					e.printStackTrace() ;
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.jingdong.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	//根据时间、状态获取订单
	public void getOrderIdList(Connection conn) throws Throwable
	{
		Log.info("开始获取京东的订单!");
		int pageIndex = 1 ;
		boolean hasNextPage = true ;		
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<5;)
		{
			
			try
			{
		
				DefaultJdClient client = new DefaultJdClient(Params.SERVER_URL,Params.token,Params.appKey,Params.appSecret);
				OrderSearchRequest request = new OrderSearchRequest();
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				
				request.setStartDate(Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
				request.setEndDate(Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
				request.setOrderState("WAIT_SELLER_STOCK_OUT,TRADE_CANCELED,LOCKED");
				request.setOptionalFields("order_id,modified,order_state");
				request.setPageSize("20");
				while(hasNextPage)
				{
					request.setPage(String.valueOf(pageIndex));
					OrderSearchResponse response = client.execute(request);
					
					if(!"0".equals(response.getCode()))
					{
						Log.error(jobName,"获取京东订单列表失败,错误信息:"+response.getCode()+","+response.getZhDesc()) ;
						hasNextPage = false ;
						break ;
					}
					OrderResult result = response.getOrderInfoResult() ;
					
					List<OrderSearchInfo> orderSerachInfoList = result.getOrderInfoList() ;
					Log.info("本次订单数: "+orderSerachInfoList.size());
					if (orderSerachInfoList==null || orderSerachInfoList.size()<=0)
					{				
						if (pageIndex==1)		
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
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
					//列出礼品卡优惠
					for(int i = 0 ; i < orderSerachInfoList.size() ; i++)
					{
						
						try{
							OrderSearchInfo info = orderSerachInfoList.get(i) ;
							/*if(info.getCouponDetailList()!=null){
								for(int j=0;i<info.getCouponDetailList().size();j++){
									Log.info("test");
									CouponDetail detail = info.getCouponDetailList().get(j);
									Log.info("优惠类型: "+detail.getCouponType());
									Log.info("优惠金额: "+detail.getCouponPrice());
									if(detail.getCouponType().equals("52")) Log.info("订单: "+info.getOrderId()+",礼品卡优惠金额:　"+detail.getCouponPrice());
								}
							}*/
							Log.info("订单号【"+info.getOrderId()+"】,最后修改时间【"+info.getModified()+"】,状态【"+info.getOrderState()+"】") ;
							
							OrderInfo order=OrderUtils.getFullTrade(info.getOrderId(), Params.SERVER_URL, Params.token, Params.appKey, Params.appSecret);
							//Log.info("订单id: "+order.getOrderId()+", 应付金额:　"+order.getOrderPayment());
							//等待出库
							if(order.getOrderState().equalsIgnoreCase("WAIT_SELLER_STOCK_OUT"))
							{
								
								
								if (!OrderManager.isCheck("检查淘宝订单", conn, order.getOrderId()))
								{
									if (!OrderManager.TidLastModifyIntfExists("检查淘宝订单", conn, order.getOrderId(),Formatter.parseDate(order.getModified(),Formatter.DATE_TIME_FORMAT)))
									{
											
										
										//创建订单 如果创建成功，减少库存--欧培测试
										OrderUtils.createInterOrder(conn, Params.SERVER_URL,Params.appKey,Params.appSecret,Params.token,
												order,Params.tradecontactid, Params.username,Params.JBDCustomerCode,Params.isLBP,Params.isNeedGetDeliverysheetid);
										
										//减其它店库存
										List<ItemInfo> itemList = order.getItemInfoList() ;
										for(int j = 0 ; j < itemList.size() ; j ++)
										{
											String sku = itemList.get(j).getOuterSkuId() ;
											long qty=Integer.valueOf(itemList.get(j).getItemTotal());
											StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, order.getOrderState(),order.getOrderId(), sku, -qty,false);
										}
									}
								}
									
							
							}
							//锁定、取消
							else if(order.getOrderState().equalsIgnoreCase("LOCKED")|| order.getOrderState().equalsIgnoreCase("TRADE_CANCELED"))
							{
								//取消订单
								String sql="declare @ret int; execute  @ret = IF_CancelCustomerOrder '" + order.getOrderId() + "';select @ret ret;";
								//欧培测试
								int resultCode =SQLHelper.intSelect(conn, sql) ;
								//取消订单失败
								if(resultCode == 0)
								{
									Log.info("订单未审核-取消成功,单号:"+order.getOrderId()+"");
								}else if(resultCode == 1)
								{
									Log.info("订单已审核-截单,单号:"+order.getOrderId()+"");
								}else if(resultCode ==2)
								{
									Log.info("订单已经出库-取消失败,单号:"+order.getOrderId()+"");
								}else if(resultCode ==3)
								{
									Log.info("订单不存在或已取消-取消失败,单号:"+order.getOrderId()+"");
								}
								else
								{
									Log.info("取消失败,单号:"+order.getOrderId()+"");
								}
							}
							
							//如果该商品最后修改时间大于配置时间，设此时间为下次取商品开始时间
							if(Formatter.parseDate(order.getModified(), Formatter.DATE_TIME_FORMAT).compareTo(modified) > 0)
							{
								modified = Formatter.parseDate(order.getModified(), Formatter.DATE_TIME_FORMAT) ;
							}
						}catch(Exception ex){
							if(conn!=null && !conn.getAutoCommit()){
								conn.rollback();
							}
							Log.error(jobName, ex.getMessage());
						}
					}
					//判断是否有下一页
					if(orderSerachInfoList == null || orderSerachInfoList.size() == 0)
						hasNextPage = false ;
					else
						pageIndex ++ ;
				}
				
				//如果取不到订单且取单配置时间小于当前天，则将配置天设置为配置天的第二天零点
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobName,je.getMessage());
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
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
		
	}
	

	//获取退货信息 V2
	public void getRefund(Connection conn) throws Throwable
	{
		boolean hasNextPage = true ;
		int pageIndex = 1 ;
		Date modified=Formatter.parseDate(refundlasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<5;)
		{
			try 
			{

				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				
				DefaultJdClient client = new DefaultJdClient(Params.SERVER_URL,Params.token,Params.appKey,Params.appSecret);
				AfterSearchRequest request = new AfterSearchRequest();
				String selectFields = "return_id,vender_id,send_type,receive_state,linkman,phone,return_address,consignee,consignor,send_time,receive_time,modifid_time,return_item_list" ;
				Field time_type = new Field("time_type", "MODIFIEDTIME");
				Field start_time = new Field("start_time", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
				Field end_time = new Field("end_time", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
				Field receive_state = new Field("receive_state", "WAITING");
				List<Field> queryFields = new ArrayList<Field>();
				queryFields.add(time_type);
				queryFields.add(start_time);
				queryFields.add(end_time);
				queryFields.add(receive_state);
				
				request.setQueryFields(queryFields);
				request.setSelectFields(selectFields) ;
				request.setPageSize("10");
				
	
				while(hasNextPage)
				{
					request.setPage(String.valueOf(pageIndex));
					AfterSearchResponse response = client.execute(request);
	
					
					//判断是否正常返回
					if(!"0".equals(response.getCode()))
					{
						Log.info("本次获取京东退货单失败,错误信息:"+response.getCode()+ "," + response.getZhDesc()) ;
						hasNextPage = false ;
						break ;
					}
					
					ReturnGoods returnGoods = response.getReturnGoods() ; ;
					List<ReturnInfo> returnInfoList = returnGoods.getReturnInfos() ;
					
					if (returnInfoList==null || returnInfoList.size()<=0)
					{				
						if (pageIndex==1)		
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,refundlasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,refundlasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, refundlasttimeconfvalue, value);			    
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
					
					for(int i = 0 ; i < returnInfoList.size() ; i++)
					{
						try{
							ReturnInfo returnInfo = returnInfoList.get(i) ;
							List<ReturnItem> itemList = returnInfo.getReturnItemList() ;
							for(int j = 0 ; j < itemList.size() ; j++)
							{
								try{
									ReturnItem item = itemList.get(j) ;
									Sku sku= StockUtils.getSkuInfoBySkuId(jobName, item.getSkuId(), Params.SERVER_URL, Params.token, Params.appKey, Params.appSecret) ;
									String sql="select shopid from ContactShopContrast with(nolock) where tradecontactid="+Params.tradecontactid;
						            String inshopid=SQLHelper.strSelect(conn, sql);
						            conn.setAutoCommit(false);		
									sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";			
									String sheetid=SQLHelper.strSelect(conn, sql);
									if (sheetid.trim().equals(""))
										throw new JSQLException(sql,"取接口单号出错!");
			
									sql = "insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , BuyerNick , "
										+ "Created , Modified , OrderStatus , Status , GoodStatus , "
					                    + " HasGoodReturn ,RefundFee , Payment , Reason,Description ,"
					                    + " Title , Price , Num , GoodReturnTime , Sid , "
					                    + " TotalFee , Iid , OuterIid , OuterSkuId , CompanyName ," 
					                    + " Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo,skuid)"
					                    + " values('" + sheetid + "' , '" + item.getOrderId() + "' , '" + returnInfo.getReturnId() + "' , '' , '' ,"
					                    + "'" + returnInfo.getSendTime() + "','" + item.getModifidTime() + "','" + returnInfo.getReceiveTime()+ "','','',"
					                    + "'1','0','0','" + item.getReturnType() + "','" + item.getReturnReason() + "',"
					                    + "'" + item.getSkuName() + "','" + item.getPrice() + "','','" + item.getModifidTime() + "','',"
					                    + "'0','','','" + sku.getOuterId() + "',''," 
					                    + "'" + returnInfo.getReturnAddress() + "','','" + inshopid + "','" + returnInfo.getReturnId() + "','" + returnInfo.getConsignor() + "','','','"+ item.getSkuId() +"')" ;
									SQLHelper.executeSQL(conn,sql);
											
									//加入到通知表
						            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
						                + sheetid +"',2 , '"+Params.tradecontactid+"' , 'yongjun' , getdate() , null) ";				
									SQLHelper.executeSQL(conn, sql);
									
									Log.info(jobName,"接口单号:"+sheetid+" 退货订单号:"+returnInfo.getReturnId()+",明细单号："+item.getOrderId()+",时间:"+returnInfo.getModifidTime());
									
									conn.commit();
									conn.setAutoCommit(true);
								}catch(Throwable ex){
									if(conn!=null && !conn.getAutoCommit()){
										conn.rollback();
									}
									Log.error(jobName, ex.getMessage());
								}
								
							}
							
							//如果该商品最后修改时间大于配置时间，设此时间为下次取商品开始时间
							if(Formatter.parseDate(returnInfo.getModifidTime(), Formatter.DATE_TIME_FORMAT).compareTo(modified) < 0)
							{
								modified = Formatter.parseDate(returnInfo.getModifidTime(), Formatter.DATE_TIME_FORMAT) ;
							}
						}catch(Throwable ex){
							if(conn!=null && !conn.getAutoCommit()){
								conn.rollback();
							}
							Log.error(jobName, ex.getMessage());
						}
						
					}
					
					//判断是否有下一页
					if(returnInfoList.size() > 0)
						pageIndex ++ ;
					else
						hasNextPage = false ;
				}
				
				//如果取不到订单且取单配置时间小于当前天，则将配置天设置为配置天的第二天零点
				if (modified.compareTo(Formatter.parseDate(refundlasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, refundlasttimeconfvalue, value);
	            	}catch(Throwable je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				
				//执行成功后不再循环
				break;
			} catch (Throwable e) 
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