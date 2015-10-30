package com.wofu.ecommerce.yougou;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.yougou.utils.Utils;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.order.OrderManager;

public class CheckOrderExecuter extends Executer {

	private String url="";
	private String pageSize="";

	private String app_key  = "";
	
	private String format="";
	private String app_secret="";
	
	private String ver="";

	private String tradecontactid="";

	private String username="";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String jobName="����Ź�����";

	public void run()  {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		format=prop.getProperty("format");
		pageSize=prop.getProperty("pageSize");
		ver=prop.getProperty("ver");
		tradecontactid=prop.getProperty("tradecontactid");

		app_key=prop.getProperty("app_key");
		username=prop.getProperty("username");
		app_secret=prop.getProperty("app_secret");
		try {		
			updateJobFlag(1);
	
			getOrderList();
			
			UpdateTimerJob();
			
			Log.info(jobName, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
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
				Log.error(jobName,"�ع�����ʧ��");
			}
			Log.error(jobName,"������Ϣ:"+Log.getErrorMessage(e));
			
			Log.error(jobName, "ִ����ҵʧ�� [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName,"���´����־ʧ��");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"�ر����ݿ�����ʧ��");
			}
		}
		
	
	
	}

	/*
	 * ��ȡһ��֮������ж���
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
					orderlistparams.put("app_key", app_key);
			        orderlistparams.put("format", format);
			        orderlistparams.put("method", "yougou.order.increment.query");
			        orderlistparams.put("sign_method", "md5");
			        orderlistparams.put("app_version", ver);
			        orderlistparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("start_modified", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("end_modified", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			       
			        orderlistparams.put("order_status", "1");
			        orderlistparams.put("page_index", String.valueOf(pageno));
			        orderlistparams.put("page_size", pageSize);
			        
					String responseOrderListData = Utils.sendByPost(orderlistparams, app_secret, url);
					//Log.info("responseOrderListData: "+responseOrderListData);
					
					JSONObject responseproduct = new JSONObject(responseOrderListData).getJSONObject("yougou_order_increment_query_response");
					if (!"200".equals(responseproduct.optString("code")))
					{
						String errdesc=responseproduct.optString("message");
						Log.error(jobName, "ȡ�����б�ʧ��:"+errdesc);
						k=10;
						break;
					}
	
					int totalCount=responseproduct.getInt("total_count");
					
					if (totalCount==0)
					{				
						k=10;
						break;
					}
		
					JSONArray orderlist=responseproduct.getJSONArray("items");
					
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						JSONArray items = order.getJSONArray("item_details");
						Order o=new Order();
						o.setObjValue(o, order);
						o.setFieldValue(o,"orderItem",items);
						
						Log.info(o.getOrder_sub_no()+" "+o.getOrder_status_name()+" "+Formatter.format(o.getModify_time(),Formatter.DATE_TIME_FORMAT));
						
						 //*1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 //*2��ɾ���ȴ���Ҹ���ʱ��������� 
						 		
						String sku;
						String sql="";
						if (o.getOrder_status_name().equals("������"))
						{	
							
							if (!OrderManager.isCheck("����Ź�����", this.getDao().getConnection(), o.getOrder_sub_no()))
							{
								if (!OrderManager.TidLastModifyIntfExists("����Ź�����", this.getDao().getConnection(), o.getOrder_sub_no(),o.getModify_time()))
								{
									OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username);
									
									for(Iterator ito=o.getOrderItem().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getLevel_code();
										
										//StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrder_sub_no(),sku);
										StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, o.getOrder_status_name(),o.getOrder_sub_no(), sku, -item.getCommodity_num(),false);
									}
								}
							}
	
						}
					}
					int totalPage = totalCount % Integer.parseInt(pageSize)==0?totalCount / Integer.parseInt(pageSize):totalCount>Integer.parseInt(pageSize)?totalCount/Integer.parseInt(pageSize):1;
					//�ж��Ƿ�����һҳ
					if (pageno>=totalPage) break;
					pageno++;
					
				}
				//ִ�гɹ�����ѭ��
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn(jobName+", Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	

}
