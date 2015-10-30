package com.wofu.ecommerce.yz;
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
import com.wofu.ecommerce.yz.utils.Utils;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.order.OrderManager;
public class CheckOrderExecuter extends Executer {

	private String url="";

	private String app_key  = "";
	
	private String format="";
	private String app_secret="";
	
	private String ver="";

	private String tradecontactid="";

	private String username="";
	private String pageSize="";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String jobName="������޶���";

	public void run()  {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		url=prop.getProperty("url");
		format=prop.getProperty("format");
		ver=prop.getProperty("ver");
		tradecontactid=prop.getProperty("tradecontactid");
		pageSize=prop.getProperty("pageSize");
		app_key=prop.getProperty("app_id");
		username=prop.getProperty("username");
		app_secret=prop.getProperty("AppSecret");

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
					int i=1;
					Date startdate=new Date((new Date()).getTime()-daymillis);
					Date enddate=new Date();
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //ϵͳ����������
					orderlistparams.put("app_id", app_key);
			        orderlistparams.put("format", format);
			        orderlistparams.put("method", "kdt.trades.sold.get");
			        orderlistparams.put("sign_method", "MD5");
			        orderlistparams.put("v", ver);
			        orderlistparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("start_update", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("end_update", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("page_no", String.valueOf(pageno));
			        orderlistparams.put("page_size", pageSize);
			        Log.info("app_secret: "+app_secret);
					String responseOrderListData = Utils.sendByPost(orderlistparams, app_secret, url);
					Log.info(responseOrderListData);
					JSONObject responseproduct = new JSONObject(responseOrderListData);
					if (!responseproduct.isNull("error_response"))
					{
						String errdesc=responseproduct.getJSONObject("error_response").getString("msg");
						Log.error(jobName, "ȡ�����б�ʧ��:"+errdesc);
						k=10;
						break;
					}
					JSONObject orderInfo = responseproduct.getJSONObject("response");
					int totalCount=orderInfo.getInt("total_results");
					if (totalCount==0)
					{				
						k=10;
						break;
					}
					
					JSONArray orderlist=orderInfo.getJSONArray("trades");
					
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						Order o=new Order();
						o.setObjValue(o,order);
						Log.info(o.getTid()+" "+o.getStatus()+" "+Formatter.format(o.getUpdate_time(),Formatter.DATE_TIME_FORMAT));
						
						 //*1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 //*2��ɾ���ȴ���Ҹ���ʱ��������� 
						 		
						String sku;
						String sql="";
						if (o.getStatus().equals("WAIT_SELLER_SEND_GOODS"))
						{	
							if (!OrderManager.isCheck("������޶���", this.getConnection(), o.getTid()))
							{
								if (!OrderManager.TidLastModifyIntfExists("������޶���", this.getConnection(), o.getTid(),o.getUpdate_time()))
								{
									OrderUtils.createInterOrder(this.getConnection(),o,Params.tradecontactid,Params.username);
									
									for(Iterator ito=o.getOrders().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getOuter_sku_id();
										
										StockManager.deleteWaitPayStock(jobName, this.getConnection(),Params.tradecontactid, o.getTid(),sku);
										StockManager.addSynReduceStore(jobName, this.getConnection(), Params.tradecontactid, o.getStatus(),o.getTid(), sku, -item.getNum(),false);
									}
								}
							}
	
							//�ȴ���Ҹ���ʱ��¼�������
						}
						
						else if (o.getStatus().equals("WAIT_BUYER_PAY"))
						{						
							for(Iterator ito=o.getOrders().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuter_sku_id();
								StockManager.addWaitPayStock(jobName, this.getConnection(),Params.tradecontactid, o.getTid(), sku, item.getNum());
								StockManager.addSynReduceStore(jobName, this.getConnection(), Params.tradecontactid, o.getStatus(),o.getTid(), sku, -item.getNum(),false);
							}
							
							//�����Ժ��û��˿�ɹ��������Զ��ر�
							//�ͷſ��,����Ϊ����						
						}else if (o.getStatus().equals("TRADE_CLOSED") || "TRADE_CLOSED_BY_USER ".equals(o.getStatus()))
						{
							for(Iterator ito=o.getOrders().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuter_sku_id();
					
								StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),Params.tradecontactid, o.getTid(), sku);
								if (StockManager.WaitPayStockExists(jobName,this.getDao().getConnection(),Params.tradecontactid, o.getTid(), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
									StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), Params.tradecontactid, o.getStatus(),o.getTid(), sku, item.getNum(),false);
							}
						}
					}
					//�ж��Ƿ�����һҳ
					if (pageno==(int)Math.ceil(totalCount/Float.parseFloat(pageSize))) break;
					pageno++;
					i++;
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
