package com.wofu.ecommerce.yhd;


import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.yhd.utils.Utils;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.order.OrderManager;

public class CheckOrderExecuter extends Executer {

	private String url="";

	private String token = "";

	private String app_key  = "";
	
	private String format="";
	private String app_secret="";
	
	private String ver="";

	private String tradecontactid="";

	private String username="";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String jobName="���һ�ŵ궩��";

	public void run()  {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		format=prop.getProperty("format");
		ver=prop.getProperty("ver");
		tradecontactid=prop.getProperty("tradecontactid");

		token=prop.getProperty("token");
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
		long pageno=1L;
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //ϵͳ����������
					orderlistparams.put("appKey", app_key);
					orderlistparams.put("sessionKey", token);
					orderlistparams.put("format", format);
					orderlistparams.put("method", "yhd.orders.get");
					orderlistparams.put("ver", ver);
					orderlistparams.put("dateType", "5");
					orderlistparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			        
			    	Date startdate=new Date((new Date()).getTime()-daymillis);
					Date enddate=new Date();
			       
					orderlistparams.put("orderStatusList", "ORDER_WAIT_PAY,ORDER_PAYED,"
			        		+"ORDER_WAIT_SEND,ORDER_ON_SENDING,ORDER_RECEIVED,ORDER_FINISH,ORDER_GRT,ORDER_CANCEL");
					orderlistparams.put("dateType", "5");
					orderlistparams.put("curPage", String.valueOf(pageno));
					orderlistparams.put("pageRows", "50");
					orderlistparams.put("startTime", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
					orderlistparams.put("endTime", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
					
			        String responseOrderListData = Utils.sendByPost(orderlistparams, app_secret, url);
					
					JSONObject responseproduct=new JSONObject(responseOrderListData);
					
					if (responseOrderListData.indexOf("errInfoList")>=0)
					{
						JSONArray errinfolist=responseproduct.getJSONObject("response").optJSONObject("errInfoList").optJSONArray("errDetailInfo");
						String errdesc="";
						
						for(int j=0;j<errinfolist.length();j++)
						{
							JSONObject errinfo=errinfolist.getJSONObject(j);
							
							errdesc=errdesc+" "+errinfo.getString("errorDes"); 
												
						}
						
						Log.error(username, "ȡ�����б�ʧ��:"+errdesc);
						k=10;
						break;
					}
					
					int totalCount=responseproduct.getJSONObject("response").getInt("totalCount");
					int errorCount=responseproduct.getJSONObject("response").getInt("errorCount");
					
					if (errorCount>0)
					{
						String errdesc="";
						JSONArray errlist=responseproduct.getJSONObject("response").getJSONObject("errInfoList").getJSONArray("errDetailInfo");
						for(int j=0;j<errlist.length();j++)
						{
							JSONObject errinfo=errlist.getJSONObject(j);
							
							errdesc=errdesc+" "+errinfo.getString("errorDes"); 
												
						}
						
						if (errdesc.indexOf("�����б���Ϣ������")<0)
						{
							k=10;
							throw new JException(errdesc);
						}
					}
					
										
					
					int i=1;
			
			
								
					if (totalCount==0)
					{									
						k=10;
						break;
					}
					
					
					JSONArray orderlist=responseproduct.getJSONObject("response").getJSONObject("orderList").getJSONArray("order");
					
					
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						
						Map<String, String> orderparams = new HashMap<String, String>();
				        //ϵͳ����������
						orderparams.put("appKey", app_key);
						orderparams.put("sessionKey", token);
						orderparams.put("format", format);
						orderparams.put("method", "yhd.order.detail.get");
						orderparams.put("ver", ver);
						orderparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				    
						orderparams.put("orderCode", order.getString("orderCode"));
				     				        
						String responseOrderData = Utils.sendByPost(orderparams, app_secret, url);
						

						JSONObject responseorder=new JSONObject(responseOrderData);
						
						int errorOrderCount=responseorder.getJSONObject("response").getInt("errorCount");
						
						if (errorOrderCount>0)
						{
							String errdesc="";
							JSONArray errlist=responseproduct.getJSONObject("response").getJSONObject("errInfoList").getJSONArray("errDetailInfo");
							for(int n=0;n<errlist.length();n++)
							{
								JSONObject errinfo=errlist.getJSONObject(n);
								
								errdesc=errdesc+" "+errinfo.getString("errorDes"); 
													
							}
							
							k=10;
							throw new JException(errdesc);						
						}
						
						
						JSONObject orderdetail=responseorder.getJSONObject("response").getJSONObject("orderInfo").getJSONObject("orderDetail");
						
						
						Order o=new Order();
						o.setObjValue(o, orderdetail);
										
						
						JSONArray orderItemList=responseorder.getJSONObject("response").getJSONObject("orderInfo").getJSONObject("orderItemList").getJSONArray("orderItem");
						
						o.setFieldValue(o, "orderItemList", orderItemList);
						
				
						Log.info(o.getOrderCode()+" "+o.getOrderStatus()+" "+Formatter.format(o.getUpdateTime(),Formatter.DATE_TIME_FORMAT));
						/*
						 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 *2��ɾ���ȴ���Ҹ���ʱ��������� 
						 */		
						String sku;
						String sql="";
						if (o.getOrderStatus().equals("ORDER_PAYED") 
								|| o.getOrderStatus().equals("ORDER_TRUNED_TO_DO")
								|| o.getOrderStatus().equals("ORDER_CAN_OUT_OF_WH"))
						{	
							
							if (!OrderManager.isCheck("���һ�ŵ궩��", this.getDao().getConnection(), o.getOrderCode()))
							{
								if (!OrderManager.TidLastModifyIntfExists("���һ�ŵ궩��", this.getDao().getConnection(), o.getOrderCode(),o.getUpdateTime()))
								{
									OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username);
									
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getOuterId();
										
										StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrderCode(),sku);
										StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, o.getOrderStatus(),o.getOrderCode(), sku, -item.getOrderItemNum(),false);
									}
								}
							}
	
							//�ȴ���Ҹ���ʱ��¼�������
						}
						
						
						else if (o.getOrderStatus().equals("ORDER_WAIT_PAY"))
						{						
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuterId();
							
								StockManager.addWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrderCode(), sku, item.getOrderItemNum());
								StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, o.getOrderStatus(),o.getOrderCode(), sku, -item.getOrderItemNum(),false);
							}
							
							 
				  
							//�����Ժ��û��˿�ɹ��������Զ��ر�
							//�ͷſ��,����Ϊ����						
						}else if (o.getOrderStatus().equals("ORDER_CANCEL"))
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuterId();
					
								StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrderCode(), sku);
								if (StockManager.WaitPayStockExists(jobName,this.getDao().getConnection(),tradecontactid, o.getOrderCode(), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
									StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, o.getOrderStatus(),o.getOrderCode(), sku, item.getOrderItemNum(),false);
							}
							
							
				
						}
						else if (o.getOrderStatus().equals("ORDER_FINISH"))
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuterId();
					
								StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrderCode(), sku);								
							}
			
						}
						else if (o.getOrderStatus().equals("ORDER_CUSTOM_CALLTO_RETUR")
							||o.getOrderStatus().equals("ORDER_CUSTOM_CALLTO_CHANGE")
							||o.getOrderStatus().equals("ORDER_RETURNED")
							||o.getOrderStatus().equals("ORDER_CHANGE_FINISHED"))
						{
							
							OrderUtils.getRefund(this.getDao().getConnection(),tradecontactid,o);
								
				
						}
						
					
					}
						
						
						
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/50.0))).intValue()) break;
					
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
