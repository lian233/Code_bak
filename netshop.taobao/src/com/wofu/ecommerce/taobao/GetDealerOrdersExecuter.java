//��ʱ��ȡ�Ա�����������ҵ
package com.wofu.ecommerce.taobao;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.DealerOrder;
import com.taobao.api.domain.DealerOrderDetail;
import com.taobao.api.request.FenxiaoDealerRequisitionorderGetRequest;
import com.taobao.api.response.FenxiaoDealerRequisitionorderGetResponse;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;

public class GetDealerOrdersExecuter extends Executer {

	private static String jobName = "��ȡ�Ա�����������ҵ";
	
	private static long daymillis=24*60*60*1000L;
	
	private String lasttimeconfvalue="";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	private String username="";
	private String lasttime;
	private String url="";
	private String appkey="";
	private String appsecret="";
	private String authcode="";
	private String tradecontactid="";

	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		username=prop.getProperty("username");
		url=prop.getProperty("url");
		appkey=prop.getProperty("appkey");
		appsecret=prop.getProperty("appsecret");
		authcode=prop.getProperty("authcode");
		tradecontactid=prop.getProperty("tradecontactid");
		lasttimeconfvalue=username+"����ȡ��������ʱ��";
			try{
				updateJobFlag(1);
				Connection conn=this.getDao().getConnection();
				lasttime=PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getPurchaseOrderList(conn);
				UpdateTimerJob();
				Log.info(jobName, "ִ����ҵ�ɹ� ["
						+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
						+ "] �´δ���ʱ��: "
						+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
			} catch (Exception e) {
				try {
					
					if (this.getConnection() != null && !this.getConnection().getAutoCommit())
						this.getConnection().rollback();
					
					if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
						this.getExtconnection().rollback();
					
				} catch (Exception e1) {
					Log.error(jobName,"�ع�����ʧ��");
					Log.error(jobName, e1.getMessage());
				}
				
				try{
					if (this.getExecuteobj().getSkip() == 1) {
						UpdateTimerJob();
					} else
						UpdateTimerJob(Log.getErrorMessage(e));
				}catch(Exception ex){
					Log.error(jobName,"����������Ϣʧ��");
					Log.error(jobName, ex.getMessage());
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
					Log.error(jobName, e.getMessage());
					Log.error(jobName,"���´����־ʧ��");
					TimerRunner.modifiedErrVect(this.getExecuteobj().getId());
				}
				
				try {
					if (this.getConnection() != null){
						this.getConnection().setAutoCommit(true);
						this.getConnection().close();
					}
						
					if (this.getExtconnection() != null){
						this.getExtconnection().setAutoCommit(true);
						this.getExtconnection().close();
					}
					
				} catch (Exception e) {
					Log.error(jobName,"�ر����ݿ�����ʧ��");
				}
			}
			
	}

	
	/*
	 * ��ȡһ��֮������ж���
	 * taobao.fenxiao.dealer.requisitionorder.get  �շ�api
	 */
	private void getPurchaseOrderList(Connection conn) throws Exception
	{	
		Log.info(username+"-"+jobName+"��ʼ!");
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<3;)
		{
			try
			{
				TaobaoClient client=new DefaultTaobaoClient(url,appkey, appsecret,"xml");
				FenxiaoDealerRequisitionorderGetRequest req=new FenxiaoDealerRequisitionorderGetRequest();		
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				req.setStartDate(startdate);
				req.setEndDate(enddate);
				req.setPageNo(pageno);
				req.setPageSize(40L);
				req.setIdentity(1L);
				FenxiaoDealerRequisitionorderGetResponse response = client.execute(req ,authcode);
				int i=1;
			
				while(true)
				{
								
					if (response.getDealerOrders()==null || response.getDealerOrders().size()<=0)
					{				
						if (i==1)		
						{
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(jobName, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobName, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						break;
					}
					
					for(Iterator it=response.getDealerOrders().iterator();it.hasNext();)
					{	
						try{
							DealerOrder po=(DealerOrder) it.next();
							
							
							Log.info(po.getDealerOrderId()+" "+po.getOrderStatus()+" "+Formatter.format(po.getModifiedTime(),Formatter.DATE_TIME_FORMAT));
								
							String sku;
							String sql="";
							
							//����ɹ�������Ӧ�̷���
							if (po.getOrderStatus().equals("WAIT_FOR_SUPPLIER_DELIVER"))
							{	
							
								if (!OrderManager.isCheck("����Ա�����", conn, String.valueOf(po.getDealerOrderId())))
								{
									if (!OrderManager.TidLastModifyIntfExists("����Ա�����", conn, String.valueOf(po.getDealerOrderId()),po.getModifiedTime()))
									{
									
										OrderUtils.createDealerOrder(jobName,conn,po,tradecontactid);
										
										for(Iterator ito=po.getDealerOrderDetails().iterator();ito.hasNext();)
										{
											DealerOrderDetail o=(DealerOrderDetail) ito.next();
											sku=o.getSkuNumber();
										
											StockManager.deleteWaitPayStock(jobName, conn,tradecontactid, String.valueOf(po.getDealerOrderId()),sku);		
											StockManager.addSynReduceStore(jobName, conn, tradecontactid, po.getOrderStatus(),String.valueOf(po.getDealerOrderId()), sku, -o.getQuantity(),false);
										}
									}
								}
								
								//�ȴ���Ҹ���ʱ��¼�������
							}
							
							else if (po.getOrderStatus().equals("BOTH_AGREE_WAIT_PAY"))
							{						
								for(Iterator ito=po.getDealerOrderDetails().iterator();ito.hasNext();)
								{
									DealerOrderDetail o=(DealerOrderDetail) ito.next();
									sku=o.getSkuNumber();
								
									StockManager.addWaitPayStock(jobName, conn,tradecontactid, String.valueOf(po.getDealerOrderId()), sku, o.getQuantity());
									StockManager.addSynReduceStore(jobName, conn, tradecontactid, po.getOrderStatus(),String.valueOf(po.getDealerOrderId()), sku, -o.getQuantity(),false);
								}
													
								//�����Ժ��û��˿�ɹ��������Զ��ر�
								//�ͷſ��,����Ϊ����
							} else if (po.getOrderStatus().equals("TRADE_CLOSED"))
							{					
								OrderManager.CancelOrderByCID(jobName, conn, String.valueOf(po.getDealerOrderId()));
								for(Iterator ito=po.getDealerOrderDetails().iterator();ito.hasNext();)
								{
									DealerOrderDetail o=(DealerOrderDetail) ito.next();		
									sku=o.getSkuNumber();
									StockManager.deleteWaitPayStock(jobName, conn,tradecontactid, String.valueOf(po.getDealerOrderId()), sku);
									//StockManager.addSynReduceStore(jobName, conn, tradecontactid, po.getOrderStatus(),String.valueOf(po.getDealerOrderId()),sku, o.getQuantity(),false);
								}
								//���׳ɹ�
								//�ͷŵȴ���Ҹ���ʱ�����Ŀ��
							}else if (po.getOrderStatus().equals("TRADE_FINISHED"))
							{
								for(Iterator ito=po.getDealerOrderDetails().iterator();ito.hasNext();)
								{
									DealerOrderDetail o=(DealerOrderDetail) ito.next();
									sku=o.getSkuNumber();
						
									StockManager.deleteWaitPayStock(jobName, conn,tradecontactid, String.valueOf(po.getDealerOrderId()), sku);								
								}
							}
							
					
							
							//����ͬ����������ʱ��
			                if (po.getModifiedTime().compareTo(modified)>0)
			                {
			                	modified=po.getModifiedTime();
			                }
						}catch(Exception ex){
							if (this.getConnection() != null && !this.getConnection().getAutoCommit())
								this.getConnection().rollback();
							
							if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
								this.getExtconnection().rollback();
							Log.error(jobName, ex.getMessage());
						}
						
					}
				
					if (pageno==(Double.valueOf(Math.ceil(response.getTotalResults()/40.0))).intValue()) break;
					
					pageno++;
					req.setPageNo(pageno);
					response=client.execute(req , authcode);
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
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				//ִ�гɹ�����ѭ��
				break;
			}catch (JException e) {
				
				throw e;
				
			} catch (Exception e) {
				if (++k >= 3)
					throw e;
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				Log.warn(jobName," ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
