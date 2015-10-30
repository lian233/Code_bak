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
	private static String jobName="������������̳Ƕ���";
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
			authTokenManager=null;
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
			        //ϵͳ����������
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
						
						 //*1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 //*2��ɾ���ȴ���Ҹ���ʱ��������� 
						 		
						String sku;
						String sql="";
						if (o.getStatus().equals("20"))
						{	
							
							if (!OrderManager.isCheck("������������̳Ƕ���", this.getDao().getConnection(), o.getOrder_sn()))
							{
								if (!OrderManager.TidLastModifyIntfExists("������������̳Ƕ���", this.getDao().getConnection(), o.getOrder_sn(),o.getPay_time()))
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
	
							//�ȴ���Ҹ���ʱ��¼�������
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
							//�����Ժ��û��˿�ɹ��������Զ��ر�
							//�ͷſ��,����Ϊ����						
						}else if (o.getStatus().equals("0"))
						{
							for(Iterator ito=o.getOrderItems().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getSku();
					
								StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrder_sn(), sku);
								if (StockManager.WaitPayStockExists(jobName,this.getDao().getConnection(),tradecontactid, o.getOrder_sn(), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
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
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(Float.parseFloat(totalOrder)/Integer.parseInt(page_size)))).intValue()) break;
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
