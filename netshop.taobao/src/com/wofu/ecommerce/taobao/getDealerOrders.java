package com.wofu.ecommerce.taobao;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.DealerOrder;
import com.taobao.api.domain.DealerOrderDetail;
import com.taobao.api.request.FenxiaoDealerRequisitionorderGetRequest;
import com.taobao.api.response.FenxiaoDealerRequisitionorderGetResponse;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;

public class getDealerOrders extends Thread {

	private static String jobname = "获取淘宝经销订单作业";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"经销取订单最新时间";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;


	public getDealerOrders() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.taobao.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
		
				getPurchaseOrderList(connection);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.taobao.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	
	/*
	 * 获取一天之类的所有订单
	 * taobao.fenxiao.dealer.requisitionorder.get  收费api
	 */
	private void getPurchaseOrderList(Connection conn) throws Exception
	{		
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{

				TaobaoClient client=new DefaultTaobaoClient(Params.url,Params.appkey, Params.appsecret,"xml");
				FenxiaoDealerRequisitionorderGetRequest req=new FenxiaoDealerRequisitionorderGetRequest();		
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				req.setStartDate(startdate);
				req.setEndDate(enddate);
				req.setPageNo(pageno);
				req.setPageSize(40L);
				req.setIdentity(1L);
				FenxiaoDealerRequisitionorderGetResponse response = client.execute(req ,Params.authcode);

				
				int i=1;
			
				while(true)
				{
								
					if (response.getDealerOrders()==null || response.getDealerOrders().size()<=0)
					{				
						if (i==1)		
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
				                		Log.error(jobname, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobname, "不可用的日期格式!"+e.getMessage());
							}
						}
						break;
					}
					
					
					
					for(Iterator it=response.getDealerOrders().iterator();it.hasNext();)
					{

						DealerOrder po=(DealerOrder) it.next();
						
												
						Log.info(po.getDealerOrderId()+" "+po.getOrderStatus()+" "+Formatter.format(po.getModifiedTime(),Formatter.DATE_TIME_FORMAT));
							
						String sku;
						String sql="";
						
						//付款成功，待供应商发货
						if (po.getOrderStatus().equals("WAIT_FOR_SUPPLIER_DELIVER"))
						{	
						
							if (!OrderManager.isCheck("检查淘宝订单", conn, String.valueOf(po.getDealerOrderId())))
							{
								if (!OrderManager.TidLastModifyIntfExists("检查淘宝订单", conn, String.valueOf(po.getDealerOrderId()),po.getModifiedTime()))
								{
								
									OrderUtils.createDealerOrder(jobname,conn,po,Params.tradecontactid);
									
									for(Iterator ito=po.getDealerOrderDetails().iterator();ito.hasNext();)
									{
										DealerOrderDetail o=(DealerOrderDetail) ito.next();
										sku=o.getSkuNumber();
									
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(po.getDealerOrderId()),sku);		
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, po.getOrderStatus(),String.valueOf(po.getDealerOrderId()), sku, -o.getQuantity(),false);
									}
								}
							}
							
							//等待买家付款时记录锁定库存
						}
						
						else if (po.getOrderStatus().equals("BOTH_AGREE_WAIT_PAY"))
						{						
							for(Iterator ito=po.getDealerOrderDetails().iterator();ito.hasNext();)
							{
								DealerOrderDetail o=(DealerOrderDetail) ito.next();
								sku=o.getSkuNumber();
							
								StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(po.getDealerOrderId()), sku, o.getQuantity());
								StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, po.getOrderStatus(),String.valueOf(po.getDealerOrderId()), sku, -o.getQuantity(),false);
							}
												
							//付款以后用户退款成功，交易自动关闭
							//释放库存,数量为负数
						} else if (po.getOrderStatus().equals("TRADE_CLOSED"))
						{					
							OrderManager.CancelOrderByCID(jobname, conn, String.valueOf(po.getDealerOrderId()));
							for(Iterator ito=po.getDealerOrderDetails().iterator();ito.hasNext();)
							{
								DealerOrderDetail o=(DealerOrderDetail) ito.next();		
								sku=o.getSkuNumber();
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(po.getDealerOrderId()), sku);
								StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, po.getOrderStatus(),String.valueOf(po.getDealerOrderId()),sku, o.getQuantity(),false);
							}
							//交易成功
							//释放等待买家付款时锁定的库存
						}else if (po.getOrderStatus().equals("TRADE_FINISHED"))
						{
							for(Iterator ito=po.getDealerOrderDetails().iterator();ito.hasNext();)
							{
								DealerOrderDetail o=(DealerOrderDetail) ito.next();
								sku=o.getSkuNumber();
					
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(po.getDealerOrderId()), sku);								
							}
						}
						
				
						
						//更新同步订单最新时间
		                if (po.getModifiedTime().compareTo(modified)>0)
		                {
		                	modified=po.getModifiedTime();
		                }
					}
				
					if (pageno==(Double.valueOf(Math.ceil(response.getTotalResults()/40.0))).intValue()) break;
					
					pageno++;
					req.setPageNo(pageno);
					response=client.execute(req , Params.authcode);
					i=i+1;
				}
				
	
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
				
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobname,je.getMessage());
	            	}
				}
				//执行成功后不再循环
				break;
			}catch (JException e) {
				
				throw e;
				
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
