package com.wofu.ecommerce.mgj;
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
import com.wofu.ecommerce.mgj.utils.Utils;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;

public class CheckOrderExecuter extends Executer {

	private String url="";

	private String token = "";

	private String app_key  = "";
	
	private String format="";
	
	private String tradecontactid="";

	private String username="";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String jobName="���Ģ���ֶ���";

	public void run()  {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		format=prop.getProperty("format");
		tradecontactid=prop.getProperty("tradecontactid");
		app_key=prop.getProperty("app_key");
		username=prop.getProperty("username");

		try {		
			token= PublicUtils.getToken(this.getDao().getConnection(), Integer.parseInt(tradecontactid));
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
		long pageno=1L;
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //ϵͳ����������
					orderlistparams.put("app_key", app_key);
					orderlistparams.put("access_token", token);
					orderlistparams.put("method", "youdian.trade.sold.get");
			    	Date startdate=new Date((new Date()).getTime()-daymillis);
					Date enddate=new Date();
					orderlistparams.put("page_no", String.valueOf(pageno));
					orderlistparams.put("page_size", "50");
					orderlistparams.put("start_updated", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
					orderlistparams.put("end_updated", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
					
			        String responseOrderListData = Utils.sendByPost(orderlistparams, "", url);
					//Log.info("result: "+responseOrderListData);
					JSONObject responseproduct=new JSONObject(responseOrderListData);
					
					if (responseproduct.getJSONObject("status").getInt("code")!=10001)
					{
						Log.error(jobName, "ȡ�����б�ʧ��:"+responseproduct.getJSONObject("status").getString("msg"));
						k=10;
						break;
					}
					
					JSONObject orders = responseproduct.getJSONObject("result").getJSONObject("data");
					int hasNext= orders.getInt("has_next");  //1����û����һҳ
					JSONArray orderlist=orders.getJSONArray("trades");
					
					if (1==hasNext && orderlist.length()==1)
					{
						k=10;
						break;
					}
					int i=1;
					
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						Order o=new Order();
						o.setObjValue(o, order);
						o.setFieldValue(o, "orders", order.getJSONArray("orders"));
						Log.info(o.getTid()+" "+o.getStatus()+" "+" "+o.getPay_status()+" "+o.getShip_status()+" "+Formatter.format(o.getLastmodify(),Formatter.DATE_TIME_FORMAT));
						/*
						 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 *2��ɾ���ȴ���Ҹ���ʱ��������� 
						 */		
						String sku;
						String sql="";
						//276364854 TRADE_ACTIVE  PAY_FINISH SHIP_NO 2015-04-20 23:54:29
						if (o.getStatus().equals("TRADE_ACTIVE") && "PAY_FINISH".equals(o.getPay_status()) && "SHIP_NO".equals(o.getShip_status()))
						{	
							if (!OrderManager.isCheck("���Ģ���ֶ���", this.getDao().getConnection(), o.getTid()))
							{
								if (!OrderManager.TidLastModifyIntfExists("���Ģ���ֶ���", this.getDao().getConnection(), o.getTid(),o.getLastmodify()))
								{
									OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username);
									
									for(Iterator ito=o.getOrders().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getSku_bn();
										
										StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getTid(),sku);
										StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, "�ȴ�����",o.getTid(), sku, -item.getItems_num(),false);
									}
								}
							}
	
							//�ȴ���Ҹ���ʱ��¼�������
						}
						
						
						else if (o.getStatus().equals("TRADE_ACTIVE") && "PAY_NO".equals(o.getShipping_type()))
						{						
							for(Iterator ito=o.getOrders().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getSku_bn();
							
								StockManager.addWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getTid(), sku, item.getItems_num());
								StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, "�ȴ�����",o.getTid(), sku, -item.getItems_num(),false);
							}
							
							 
				  
							//�����Ժ��û��˿�ɹ��������Զ��ر�
							//�ͷſ��,����Ϊ����						
						}else if (o.getStatus().equals("TRADE_CLOSED"))
						{
							for(Iterator ito=o.getOrders().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getSku_bn();
					
								StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getTid(), sku);
								if (StockManager.WaitPayStockExists(jobName,this.getDao().getConnection(),tradecontactid, o.getTid(), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
									StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, "�ȴ�����",o.getTid(), sku, item.getItems_num(),false);
							}
							
							
				
						}
						else if (o.getStatus().equals("TRADE_FINISHED"))
						{
							for(Iterator ito=o.getOrders().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getSku_bn();
					
								StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getTid(), sku);								
							}
			
						}
						else if (o.getStatus().equals("ORDER_CUSTOM_CALLTO_RETUR")
							||o.getStatus().equals("ORDER_CUSTOM_CALLTO_CHANGE")
							||o.getStatus().equals("ORDER_RETURNED")
							||o.getStatus().equals("ORDER_CHANGE_FINISHED"))
						{
							
							//OrderUtils.getRefund(this.getDao().getConnection(),tradecontactid,o);
								
				
						}
						
					
					}
					//�ж��Ƿ�����һҳ
					if (1==hasNext) break;
					pageno++;
					i=i+1;
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
