package com.wofu.ecommerce.ylzx;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ylzx.utils.AuthTokenManager;
import com.wofu.ecommerce.ylzx.utils.Utils;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.order.OrderManager;
public class CheckOrderExecuter extends Executer {
	private String url="";
	private String app_key  = "";
	private String app_secret="";
	private String ver="";
	private String tradecontactid="";
	private String username="";
	private String user_name;
	private String password;
	private String hmac_sha1;
	private String page_size;
	private static long daymillis=24*60*60*1000L;
	private static String jobName="检查银联在线商城订单";
	public void run()  {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		url=prop.getProperty("url");
		ver=prop.getProperty("ver");
		tradecontactid=prop.getProperty("tradecontactid");
		app_key=prop.getProperty("app_key");
		username=prop.getProperty("username");
		app_secret=prop.getProperty("app_secret");
		user_name=prop.getProperty("user_name");
		password=prop.getProperty("password");
		hmac_sha1=prop.getProperty("hmac_sha1");
		page_size=prop.getProperty("page_size");
		AuthTokenManager authTokenManager;
		try {		
			authTokenManager = new AuthTokenManager(app_key,app_secret,ver
						,user_name,password);
				authTokenManager.init();;
			updateJobFlag(1);
	
			getOrderList(authTokenManager);
			
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
			authTokenManager=null;
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

	/*
	 * 获取一天之类的所有订单
	 */
	private void getOrderList(AuthTokenManager authTokenManager) throws Exception
	{		
		long pageno=1L;
		int i=0;
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{

			    	Date startdate=new Date((new Date()).getTime()-daymillis);
					Date enddate=new Date();
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //系统级参数设置
					orderlistparams.put("oauth_consumer_key", app_key);
					orderlistparams.put("oauth_signature_method", hmac_sha1);
					orderlistparams.put("oauth_timestamp", String.valueOf(new Date().getTime()/1000L));
					orderlistparams.put("oauth_nonce", String.valueOf(System.currentTimeMillis()));
					orderlistparams.put("oauth_version", ver);
					orderlistparams.put("fields", "orders");
			        orderlistparams.put("time_reference", "2");
			        orderlistparams.put("start_created", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("end_created", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("page_no", String.valueOf(pageno));
			        orderlistparams.put("page_size", "50");
			        orderlistparams.put("oauth_token", authTokenManager.getToken());
			        String responseOrderListData = Utils.sendByPost(url,
							orderlistparams,"POST",app_secret,authTokenManager.getOauth_token_secret());
					
			        Document doc = DOMHelper.newDocument(responseOrderListData);
					Element elementOrder = doc.getDocumentElement();
					String status = DOMHelper.getSubElementVauleByName(elementOrder, "status").trim();
					
					if (!"200".equals(status))
					{
						String errmsg = DOMHelper.getSubElementVauleByName(elementOrder, "reason").trim();
						Log.error(jobName, errmsg);
						k=10;
						break;
					
					}
					Element body = DOMHelper.getSubElementsByName(elementOrder, "body")[0];
					String totalOrder = DOMHelper.getSubElementVauleByName(body, "totalResults");
					if ("0".equals(totalOrder))
					{				
						k=10;
						break;
					}
					Element[] orders  = DOMHelper.getSubElementsByName(body, "order");
					for(Element e:orders)
					{
						Order o=OrderUtils.getOrder(e);
						OrderUtils.setOrderItem(o,e,authTokenManager);
						Log.info(o.getOrder_sn()+" "+o.getStatus()+" "+Formatter.format(o.getPay_time(),Formatter.DATE_TIME_FORMAT));
						
						 //*1、如果状态为等待卖家发货则生成接口订单
						 //*2、删除等待买家付款时的锁定库存 
						 		
						String sku;
						String sql="";
						if (o.getStatus().equals("20"))
						{	
							
							if (!OrderManager.isCheck("检查银联在线商城订单", this.getDao().getConnection(), o.getOrder_sn()))
							{
								if (!OrderManager.TidLastModifyIntfExists("检查银联在线商城订单", this.getDao().getConnection(), o.getOrder_sn(),o.getPay_time()))
								{
									OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username);
									
									for(Iterator ito=o.getOrderItems().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getSku(); 
										
										StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrder_sn(),sku);
										StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, o.getStatus(),o.getOrder_sn(), sku, -item.getQuantity(),false);
									}
								}
							}
	
							//等待买家付款时记录锁定库存
						}
						
						
						else if (o.getStatus().equals("11"))
						{						
							for(Iterator ito=o.getOrderItems().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getSku();
							
								StockManager.addWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrder_sn(), sku, item.getQuantity());
								StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, o.getStatus(),o.getOrder_sn(), sku, -item.getQuantity(),false);
							}
							//付款以后用户退款成功，交易自动关闭
							//释放库存,数量为负数						
						}else if (o.getStatus().equals("0"))
						{
							for(Iterator ito=o.getOrderItems().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getSku();
					
								StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrder_sn(), sku);
								if (StockManager.WaitPayStockExists(jobName,this.getDao().getConnection(),tradecontactid, o.getOrder_sn(), sku))//有获取到等待买家付款状态时才加库存
									StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, o.getStatus(),o.getOrder_sn(), sku, item.getQuantity(),false);
							}
							
						}
						else if (o.getStatus().equals("40"))
						{
							for(Iterator ito=o.getOrderItems().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getSku();
					
								StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrder_sn(), sku);								
							}
			
						}
					}
					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(Float.parseFloat(totalOrder)/Integer.parseInt(page_size)))).intValue()) break;
					pageno++;
					i=i+1;
				}
				//执行成功后不再循环
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn(jobName+", 远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
}
