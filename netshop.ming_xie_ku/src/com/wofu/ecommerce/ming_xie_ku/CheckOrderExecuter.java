package com.wofu.ecommerce.ming_xie_ku;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ming_xie_ku.utils.Utils;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.order.OrderManager;

public class CheckOrderExecuter extends Executer 
{

	private String tradecontactid="";

	private String username="";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String jobName="检查名鞋库订单";
	private static String url="检查名鞋库订单";
	private static String app_key="检查名鞋库订单";
	private static String app_Secret="检查名鞋库订单";
	private static String format="检查名鞋库订单";
	private static String ver="检查名鞋库订单";

	public void run()  {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		url=prop.getProperty("url","18");
		app_key=prop.getProperty("app_key","18");
		app_Secret=prop.getProperty("app_Secret","18");
		tradecontactid=prop.getProperty("tradecontactid","18");
		format=prop.getProperty("format","18");
		ver=prop.getProperty("ver","18");

		try {		
			
			updateJobFlag(1);
	
			getOrderList();
			
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
	private void getOrderList() throws Exception
	{		
		UTF8_transformer utf8_transformer=new UTF8_transformer();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		Date now=new Date();
		String method="scn.vendor.order.full.get";
		String ver=Params.ver;
		long pageno=1L;
		//Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					/***data部分***/
					JSONObject data=new JSONObject();
					//需要返回的字段：
					data.put("Fields","seller_id, vendor_id, seller_order_no, vendor_order_no,submit_date,seller_memo,vendor_memo,shipping_fee,goods_price,rcv_name,rcv_addr_id,rcv_addr_detail,rcv_tel,order_status,update_date,suggest_express,detail.seller_order_det_no,detail.vendor_order_det_no,detail.seller_sku_id,detail.vendor_sku_id,detail.unit_price,detail.sale_price,detail.qty,express.express_no,express.express_company_id,express.sku_qty_pair");	
					/**以下都不是必须的**/
					data.put("PageNo", String.valueOf(pageno));          //页码
					/**sign部分***/
					String sign=Utils.get_sign(app_Secret,app_key,data, method, now,ver,format);
					/***合并为输出语句****/
					String output_to_server=Utils.post_data_process(method, data, app_key,now, sign).toString();
					
			        String responseOrderListData = Utils.sendByPost(url,output_to_server);
					
					JSONObject responseproduct=new JSONObject(responseOrderListData);
					
					if(!responseproduct.get("ErrCode").equals(null) || !responseproduct.get("ErrMsg").equals(null))
					{
						String errdesc="";
						errdesc=errdesc+" "+responseproduct.get("ErrCode").toString()+" "+responseproduct.get("ErrMsg").toString(); 
						
						Log.error(username, "取订单列表失败:"+errdesc);
						k=10;
						break;
					}
					int totalCount=responseproduct.getInt("TotalResults");
					if(!responseproduct.get("ErrCode").equals(null) || !responseproduct.get("ErrMsg").equals(null))

					{
						String errdesc="";
						JSONArray errlist=responseproduct.getJSONObject("response").getJSONObject("errInfoList").getJSONArray("errDetailInfo");
						for(int j=0;j<errlist.length();j++)
						{
							JSONObject errinfo=errlist.getJSONObject(j);
							
							errdesc=errdesc+" "+errinfo.getString("errorDes"); 
												
						}
					}
					
										
					
					int i=1;
			
			
								
					if (responseproduct.getInt("TotalResults")==0)
					{									
						k=10;
						break;
					}
					
					
					JSONArray orderlist=responseproduct.getJSONArray("Result").getJSONObject(0).getJSONArray("OrderDets");
					
					
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						/***data部分***/
						data=new JSONObject();
						//需要返回的字段：
						data.put("Fields","seller_id, vendor_id, seller_order_no, vendor_order_no,submit_date,seller_memo,vendor_memo,shipping_fee,goods_price,rcv_name,rcv_addr_id,rcv_addr_detail,rcv_tel,order_status,update_date,suggest_express,detail.seller_order_det_no,detail.vendor_order_det_no,detail.seller_sku_id,detail.vendor_sku_id,detail.unit_price,detail.sale_price,detail.qty,express.express_no,express.express_company_id,express.sku_qty_pair");	
						/**以下都不是必须的**/
						data.put("VendorOrderNo", order.getString("VendorOrderNo"));   //供货商订单号
						/**sign部分***/
						sign=Utils.get_sign(app_Secret,app_key,data, method, now,ver,format);
						/***合并为输出语句****/
						output_to_server=Utils.post_data_process(method, data, app_key,now, sign).toString();
						
						String responseOrderData = Utils.sendByPost(url,output_to_server);
						
						responseproduct=new JSONObject(responseOrderListData);

						JSONObject responseorder=new JSONObject(responseOrderData);
						
						if (responseorder.getBoolean("IsError"))
						{
							String errdesc = "";
							errdesc = errdesc + " "
									+ responseproduct.get("ErrCode").toString()
									+ " "
									+ responseproduct.get("ErrMsg").toString();

							Log.error(username, "取订单列表失败:" + errdesc);
							k = 10;
							break;						
						}
						
						
						JSONObject orderdetail=responseorder.getJSONArray("Result").getJSONObject(0).getJSONArray("OrderDets").getJSONObject(j);
						
						
						Order o=new Order();
						o.setObjValue(o, orderdetail);
										
						
						JSONArray orderItemList=responseorder.getJSONArray("Result").getJSONObject(0).getJSONArray("OrderDets");
						
						o.setFieldValue(o, "OrderDets", orderItemList);
						
				
						Log.info(o.getVendorOrderNo()+" "+o.getOrderStatus()+" "+Formatter.format(o.getUpdateDate(),Formatter.DATE_TIME_FORMAT));
						/*
						 *1、如果状态为等待卖家发货则生成接口订单
						 *2、删除等待买家付款时的锁定库存 
						 */		
						String sku;
						String sql="";
						if(/*o.getOrderStatus()==1||*/o.getOrderStatus()==2||o.getOrderStatus()==3/*||o.getOrderStatus()==4*/) //订单状态(1-未处理 2-已确认 3-已发货 4-已作废)
						{	
							
							if (!OrderManager.isCheck("检查名鞋库订单", this.getDao().getConnection(), o.getVendorOrderNo()/*getOrderCode()*/))
							{
								if (!OrderManager.TidLastModifyIntfExists("检查名鞋库订单", this.getDao().getConnection(), o.getVendorOrderNo(),new Date(o.getUpdateDate())))
								{
									OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username);
									
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getVendorSkuId();
										
										StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getVendorOrderNo(),sku);
										StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, String.valueOf(o.getOrderStatus()),o.getVendorOrderNo(), sku, -0,false);
									}
								}
							}
	
							//等待买家付款时记录锁定库存
						}
						
						
						else if (o.getOrderStatus()==1)
						{						
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getVendorSkuId();
							
								StockManager.addWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getVendorSkuId(), sku,Integer.parseInt(item.getVendorSkuId())/*getOrderItemNum()*/);
								StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, String.valueOf(o.getOrderStatus()),o.getVendorOrderNo(), sku, Integer.parseInt(item.getVendorSkuId()),false);
							}
							
							 
				  
							//付款以后用户退款成功，交易自动关闭
							//释放库存,数量为负数						
						}						else if(o.getOrderStatus()==1)//1-未处理
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getVendorSkuId();
								StockManager.deleteWaitPayStock(jobName, this.getDao(), tradecontactid, o.getVendorOrderNo(), sku);
								if(StockManager.WaitPayStockExists(jobName, this.getDao(), tradecontactid, o.getVendorOrderNo(), sku))//有获取到等待买家付款状态时才加库存
									StockManager.addSynReduceStore(jobName, this.getDao(), tradecontactid, String.valueOf(o.getOrderStatus()), o.getVendorOrderNo(), sku, 0, false);
								
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
								StockManager.deleteWaitPayStock(jobName, this.getDao(), tradecontactid, o.getVendorOrderNo(), sku);
								if(StockManager.WaitPayStockExists(jobName, this.getDao(), tradecontactid, o.getVendorOrderNo(), sku))
									StockManager.addSynReduceStore(jobName, this.getDao(), tradecontactid, String.valueOf(o.getOrderStatus()), o.getVendorOrderNo(), sku, 0, false);
							}
						}
						else if(o.getOrderStatus()==2)  //2-已确认
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getVendorSkuId();
								StockManager.deleteWaitPayStock(jobName, this.getDao(), tradecontactid, o.getVendorOrderNo(), sku);
							}
						}
					
					}
						
						
						
					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(totalCount/50.0))).intValue()) break;
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
