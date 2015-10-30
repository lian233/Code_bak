package com.wofu.ecommerce.ming_xie_ku;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ming_xie_ku.utils.Utils;

public class GetOrders extends Thread
{
	private static String jobName = "获取名鞋库订单作业";
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";  //Parmas类是从其他地方复制过来的，已经过了修改
	private static long daymillis=24*60*60*1000L;
	private boolean is_importing=false;
	private String lasttime;
	private static String  orderLineStatus="1";
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	@Override
	public void run() 
	{

		System.out.println();
		Log.info(jobName, "启动[" + jobName + "]模块");
		do 
		{
			Connection connection = null;
			is_importing = true;
			try 
			{
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.ming_xie_ku.Params.dbname); //数据库名暂时不明
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getOrderList(connection);  //暂时没做好
			} catch (Exception e)
			{
				try 
				{
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) 
				{
					Log.error(jobName, "回滚事务失败");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally
			{
				is_importing = false;
				try
				{
					if (connection != null) connection.close();
				} catch (Exception e)
				{
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long current = System.currentTimeMillis();
			while(System.currentTimeMillis()-current<Params.waittime*1000){
				try {
					Thread.sleep(100L);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} while (true);
//		super.run();
	}
	
	
	/*****供货商获取订单详细信息*****/
	public void getOrderList(Connection conn) throws Throwable
	{
		JSONObject responseResult=new JSONObject();
		Date now=new Date();
		String method="scn.vendor.order.full.get";
		String ver=Params.ver;
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					/***data部分***/
					JSONObject data=new JSONObject();
					//需要返回的字段：
					data.put("Fields","seller_id, suggest_express_no, is_cod, vendor_id, seller_order_no, vendor_order_no,submit_date,seller_memo,vendor_memo,shipping_fee,goods_price,rcv_name,rcv_addr_id,rcv_addr_detail,rcv_tel,order_status,update_date,suggest_express,detail.seller_order_det_no,detail.vendor_order_det_no,detail.seller_sku_id,detail.vendor_sku_id,detail.unit_price,detail.sale_price,detail.qty,express.express_no,express.express_company_id,express.sku_qty_pair");	
					/**以下都不是必须的**/
//					data.putOpt("StartUpdateDate", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT)); //订单更新开始时间
//					data.putOpt("EndUpdateDate", Formatter.format(enddate,Formatter.DATE_TIME_FORMAT));   //订单更新结束时间
//					data.putOpt("StartSubmitDate","2015-09-01 00:00:01");
//					data.putOpt("EndSubmitDate", "2015-09-11 00:00:01");
					data.putOpt("StartSubmitDate", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT)); //订单提交开始时间
					data.putOpt("EndSubmitDate", Formatter.format(enddate,Formatter.DATE_TIME_FORMAT));   //订单提交结束时间
//					data.putOpt("SellerId", Params.SellerId);        //分销商ID(外部商家该值留空)
//					data.putOpt("SellerOrderNo", Params.SellerOrderNo);   //分销商订单号 	
//					data.putOpt("VendorOrderNo", Params.VendorOrderNo);   //供货商订单号
//					data.putOpt("OrderStatus", Integer.parseInt(orderLineStatus));     //订单状态(1-未处理 2-已确认 3-已发货 4-已作废)
//					data.putOpt("PageNo", pageno);          //页码
//					data.putOpt("PageSize", "50");        //每页条数。默认40，最大100
					/**sign部分***/
					String sign=Utils.get_sign(Params.app_Secret,Params.app_key,data, method,now,Params.ver,Params.format);
					/***合并为输出语句****/
					String output_to_server=Utils.post_data_process(method, data, Params.app_key,now, sign).toString();
					System.out.println("请求的参数"+output_to_server);
					String responseOrderListData=Utils.sendByPost(Params.url, output_to_server);
					
//					Log.info(output_to_server);
					Log.info("订单请求返回的信息"+responseOrderListData);
					responseResult=new JSONObject(responseOrderListData);    //所有信息
					//JSONObject Result = responseResult.getJSONArray("Result").getJSONObject(0);   //一份订单订单的详细信息
//					for(int j=0;j<responseResult.getInt("TotalResults");j++)
//					{
//						
//					}
					String errdesc="";
					if(!responseResult.get("ErrCode").equals(null) || !responseResult.get("ErrMsg").equals(null))
					{
						errdesc=errdesc+" "+responseResult.get("ErrCode").toString()+" "+responseResult.get("ErrMsg").toString(); 
					}
					if (responseResult.getInt("TotalResults")==0)  //订单列表信息不存在，记录总数一条都没有
					{
						if (pageno==1L)
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
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
						Log.error(jobName, /*"取订单列表失败:"*/"没有该订单、订单已发货，或没有订单需要处理:"+errdesc);
					}
					k=10;
					break;
				}
				
				int totalCount=responseResult.getInt("TotalResults");
				
				if(!responseResult.get("ErrCode").equals(null) || !responseResult.get("ErrMsg").equals(null))
				{
					String errdesc="";
					errdesc=errdesc+" "+responseResult.get("ErrCode").toString()+" "+responseResult.get("ErrMsg").toString(); 
					Log.error(jobName, errdesc);
					k=10;
					break;
				}
				if(responseResult.getInt("TotalResults")==0)
				{
					if (pageno==1L)	
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
					k=10;
					break;
				}
				JSONArray orderlist=responseResult.getJSONArray("Result");
				Log.info("订单总数: "+orderlist.length());
				for(int j=0;j<orderlist.length();j++)
				{	
					Log.info("第"+j+"个订单");
					JSONObject order=orderlist.getJSONObject(j);
					Order o=OrderUtils.getOrderByID(Params.app_Secret,order.getString("VendorOrderNo"),Params.app_key,Params.ver,Params.format);
					/**观察是否可以得到order对象**/
					Log.info("orderId: "+o.getVendorOrderNo());
					System.out.println("是否货到付款:"+o.getIsCod()+"     快递公司:"+o.getSuggestExpress()+"    快递号:"+o.getSuggestExpressNo());
					Log.info(o.getVendorOrderNo()+" "+o.getOrderStatus()+" "+Formatter.format(o.getUpdateDate(),Formatter.DATE_TIME_FORMAT));
					 //*2、删除等待买家付款时的锁定库存 
					String sku;
					String sql="";
					if(o.getOrderStatus()==1/*||o.getOrderStatus()==2||o.getOrderStatus()==3||o.getOrderStatus()==4*/) //订单状态(1-未处理 2-已确认 3-已发货 4-已作废)
					{
						if (!OrderManager.isCheck("检查名鞋库订单", conn, o.getVendorOrderNo()))
						{
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date getUpdateDate_Date_type = null;
							try {
								getUpdateDate_Date_type = format.parse(o.getUpdateDate());
							} catch (ParseException e) {
								e.printStackTrace();
							}
							if (!OrderManager.TidLastModifyIntfExists("检查名鞋库订单", conn, o.getVendorOrderNo(),getUpdateDate_Date_type))
							{
								OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
								for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									sku=item.getVendorSkuId();
									StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getVendorOrderNo(), sku);
									StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, String.valueOf(o.getOrderStatus()), o.getVendorOrderNo(), sku, 0, false);
								}
							}
							//等待买家付款时记录锁定库存
						}
					}
						else if(o.getOrderStatus()==2||o.getOrderStatus()==3)
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								/**检查程序是否能运行到这个部分，可以了**/
//								System.out.println("检查程序是否能运行到这个部分:");
								OrderItem item=(OrderItem) ito.next();
								sku=item.getVendorSkuId();
								StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getVendorOrderNo(), sku);
								if(StockManager.WaitPayStockExists(jobName, conn, Params.tradecontactid, o.getVendorOrderNo(), sku))//有获取到等待买家付款状态时才加库存
									StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, String.valueOf(o.getOrderStatus()), o.getVendorOrderNo(), sku, 0, false);
								/**检查未处理的订单是否可以正常写入数据库**/
//								System.out.println(sku);
							}
						}
						//付款以后用户退款成功，交易自动关闭
						//释放库存,数量为负数		
						else if(o.getOrderStatus()==4) //4-已作废
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getVendorSkuId();
								StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getVendorOrderNo(), sku);
								if(StockManager.WaitPayStockExists(jobName, conn, Params.tradecontactid, o.getVendorOrderNo(), sku))
									StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, String.valueOf(o.getOrderStatus()), o.getVendorOrderNo(), sku, 0, false);
							}
						}
						else if(o.getOrderStatus()==2)  //2-已确认
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getVendorSkuId();
								StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getVendorOrderNo(), sku);
							}
						}
						//更新同步订单最新时间
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date getUpdateDate_date = null;
						try 
						{
							getUpdateDate_date = format.parse(o.getUpdateDate());
						} 
						catch (ParseException e)
						{
							e.printStackTrace();
						}
						if(getUpdateDate_date.compareTo(modified)>0)
						{
							modified=getUpdateDate_date;
						}
						
					}
					if(modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
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
					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(totalCount/50.0))).intValue()) break;
					pageno++;
				//做到这里了
			}catch (Exception e) 
			{
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				e.printStackTrace();
				Thread.sleep(10000L);
			}

		}

	}
	
	@Override
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}
	
}
