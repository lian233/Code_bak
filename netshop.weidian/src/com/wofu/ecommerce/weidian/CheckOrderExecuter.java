package com.wofu.ecommerce.weidian;
import java.net.URLEncoder;
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
import com.wofu.ecommerce.weidian.utils.getToken;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.order.OrderManager;
public class CheckOrderExecuter extends Executer {
	private String url="";
	private String format="";
	private String access_token="";
	private String ver="";
	private String tradecontactid="";
	private String username="";
	private String pageSize="";
	private static long daymillis=24*60*60*1000L;
	private static String jobName="���΢��δ�붩��";
	public void run()  {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		url=prop.getProperty("url");
		format=prop.getProperty("format");
		ver=prop.getProperty("ver");
		tradecontactid=prop.getProperty("tradecontactid");
		pageSize=prop.getProperty("pageSize");
		username=prop.getProperty("username");
		try {		
			access_token =getToken.getToken_zy(this.getDao().getConnection());
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
					JSONObject param_Object = new JSONObject();
					JSONObject public_Object = new JSONObject();
			        //ϵͳ����������
					param_Object.put("page_num", String.valueOf(pageno));
					param_Object.put("order_type", "");
					param_Object.put("add_start", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
					param_Object.put("add_end", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        
					public_Object.put("format", format);
					public_Object.put("method", "vdian.order.list.get");
					public_Object.put("version", ver);
					public_Object.put("access_token", access_token);
					String opt_to_sever = Params.url + "?param=" + URLEncoder.encode(param_Object.toString(),"UTF-8") + "&public=" + URLEncoder.encode(public_Object.toString(),"UTF-8");
					String responseOrderListData = com.wofu.ecommerce.weidian.utils.Utils.sendbyget(opt_to_sever);
					Log.info(responseOrderListData);
					JSONObject responseResult = new JSONObject(responseOrderListData);
					if(!responseResult.getJSONObject("status").getString("status_reason").equals("success"))
					{
						String errdesc=responseResult.getJSONObject("status").getString("status_reason");
						Log.info("���δ�붩������: "+errdesc);
						k=10;
						break;
					}
					int totalCount=responseResult.getJSONObject("result").getInt("total_num");
					if (totalCount==0)
					{				
						k=10;
						break;
					}
					
					JSONArray orderlist=responseResult.getJSONObject("result").getJSONArray("orders");
					
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						Order o = OrderUtils.getOrderByID(order.getString("order_id"),access_token); //������ϸ
						Log.info(o.getOrder_id()+" ���������"+o.getStatus()+" "+Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT));
						String sku;
						String sql="";
						
						 //*1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 //*2��ɾ���ȴ���Ҹ���ʱ��������� 
						if (o.getStatus().equals("pay"))
						{	
							if (!OrderManager.isCheck("���΢�궩��", this.getConnection(), o.getOrder_id()))
							{
								if (!OrderManager.TidLastModifyIntfExists("���΢�궩��", this.getDao().getConnection(), o.getOrder_id(),o.getPay_time()))
								{
									OrderUtils.createInterOrder(this.getConnection(),o,Params.tradecontactid,Params.username);
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getSku_id();
										StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(), Params.tradecontactid, o.getOrder_id(), sku);
										StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), Params.tradecontactid, String.valueOf(o.getStatus()), o.getOrder_id(), sku, 0, false);
									}
								}
							}
	
						}
					}
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
