package com.wofu.ecommerce.rke;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.rke.utils.Utils;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.order.OrderManager;

public class CheckOrderExecuter extends Executer {

	private String url="";
	private String pageSize="";

	
	
	private String ver="";

	private String tradecontactid="";

	private String username="";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String jobName="检查麦斯卡经销订单";

	public void run()  {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		ver=prop.getProperty("ver");
		tradecontactid=prop.getProperty("tradecontactid");
		username=prop.getProperty("username");
		pageSize=prop.getProperty("pageSize");
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
		int pageno=1;
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					Map<String, String> orderlistparams = new HashMap<String, String>();
			    	Date startdate=new Date((new Date()).getTime()-daymillis);
					Date enddate=new Date();
					orderlistparams.put("api_version", ver);
			        orderlistparams.put("act", "search_order_list");
			        orderlistparams.put("last_modify_st_time", String.valueOf(startdate.getTime()/1000L));
			        orderlistparams.put("last_modify_en_time", String.valueOf(enddate.getTime()/1000L));
			       
			        orderlistparams.put("pages", String.valueOf(pageno));
			        orderlistparams.put("counts", pageSize);
			        
					String responseOrderListData = Utils.sendByPost(orderlistparams, url);
					//Log.info("responseOrderListData: "+responseOrderListData);
					
					Document doc = DOMHelper.newDocument(responseOrderListData, "GBK");
					Element ele = doc.getDocumentElement();
					String result = DOMHelper.getSubElementVauleByName(ele, "result");
					if (!"success".equals(result))
					{
						String errdesc=DOMHelper.getSubElementVauleByName(ele, "msg");
						Log.error(jobName, "取订单列表失败:"+errdesc);
						k=10;
						break;
					}
					Element info = DOMHelper.getSubElementsByName(ele, "info")[0];
					int totalCount=Integer.parseInt(DOMHelper.getSubElementVauleByName(info, "counts"));
					Log.info("totalCount: "+totalCount);
					
					if (totalCount==0)
					{				
						k=10;
						break;
					}
		
					Element[] orderList = DOMHelper.getSubElementsByName(ele,"item");
					
					for(int j=0;j<orderList.length;j++)
					{
						Element order=orderList[j];
						if(!DOMHelper.ElementIsExists(order, "order_id")) continue;
						Order o = OrderUtils.getOrderByElement(order);
						
						Log.info(o.getOrder_sn()+" 订单状态: "+o.getOrder_status()+" 付款状态: "+o.getPay_status()+"付款时间: "+Formatter.format(o.getPay_time(),Formatter.DATE_TIME_FORMAT));
						
						 //*2、删除等待买家付款时的锁定库存 
						 		
						String sku;
						if ("2".equals(o.getPay_status()) && "0".equals(o.getShipping_status()))
						{	
							
							if (!OrderManager.isCheck("检查麦斯卡经销订单", this.getDao().getConnection(), o.getOrder_sn()))
							{
								if (!OrderManager.TidLastModifyIntfExists("检查麦斯卡经销订单", this.getDao().getConnection(), o.getOrder_sn(),o.getPay_time()))
								{
									OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username);
									
									for(Iterator ito=o.getOrderItems().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getProduct_sn();
										StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, o.getOrder_status(),o.getOrder_sn(), sku, -item.getGoods_number(),false);
									}
								}
							}
	
						}
					}
					int totalPage = totalCount % Integer.parseInt(pageSize)==0?totalCount / Integer.parseInt(pageSize):totalCount>Integer.parseInt(pageSize)?totalCount/Integer.parseInt(pageSize):1;
					//判断是否有下一页
					if (pageno>=totalPage) break;
					pageno++;
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
