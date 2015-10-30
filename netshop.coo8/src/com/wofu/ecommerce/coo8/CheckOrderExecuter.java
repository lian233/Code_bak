package com.wofu.ecommerce.coo8;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import com.coo8.api.Coo8Client;
import com.coo8.api.DefaultCoo8Client;
import com.coo8.api.request.order.OrdersGetRequest;
import com.coo8.api.response.order.OrdersGetResponse;
import com.coo8.open.order.Order;
import com.coo8.open.order.OrderDetail;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
public class CheckOrderExecuter extends Executer {

	private static long daymillis=24*60*60*1000L;
	
	private String url = "" ;
	
	private String appKey = "" ;
	
	private String secretKey = "" ;
	
	private String tradecontactid = "" ;
	
	private String username = "" ;
	
	private static String jobName="��ʱ����Ͷ���";
	
	public void run()  {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		url=prop.getProperty("url") ;
		appKey=prop.getProperty("appKey") ;
		secretKey=prop.getProperty("secretKey") ;
		tradecontactid=prop.getProperty("tradecontactid") ;
		username=prop.getProperty("username") ;

		try 
		{			 
			updateJobFlag(1);
			
			checkcOrders() ;
			
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

				//updateJobFlag(0);
				
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
	 * ��ȡһ��֮�ڵ����ж���
	 */
	private void checkcOrders() throws Exception
	{		
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		for(int k=0;k<5;)
		{
			try
			{
				while(hasNextPage)
				{
					Coo8Client coo8=new DefaultCoo8Client(url,appKey,secretKey);
					OrdersGetRequest orderrequest=new OrdersGetRequest();
					Date enddate=new Date();
					Date startdate=new Date(new Date().getTime()-daymillis);
					orderrequest.setStartDate(startdate);
					orderrequest.setEndDate(enddate);
					orderrequest.setPageNo(pageIndex);
					orderrequest.setPageSize(20);
					OrdersGetResponse response=coo8.execute(orderrequest);
					
					if (response.getMsg()!=null)
					{
						hasNextPage = false ;
						Log.warn(jobName,"ȡ����ʧ��,������Ϣ:"+response.getMsg());
						break ;
					}
						
					//��ȡ������
					int total=response.getTotalResult();
						//��ҳ��
					int pageTotal=Double.valueOf(Math.ceil(total/20.0)).intValue();
						
					//���صĶ����б�����
					List<Order> orders=response.getOrders();
					//Log.info("��������Ϊ:��"+orders.size());
					if(orders==null){
						{
								
							Log.info(jobName,"��������Ҫ����Ķ���!");
							hasNextPage=false;								
							break ;
						}
					}
						
					int ignoreNum=0;  //ͳ��ȡ�صĶ����еľɶ�������--���ȫ���Ǿɶ����������ȡ����ʱ��Ϊ���ݿ�ʱ�����һ����ʱ
					for(int i=0; i<orders.size(); i++){
						try{
							Order order=orders.get(i);
							Log.info(Formatter.format(order.getOrderChangeTime(), Formatter.DATE_TIME_FORMAT));
								
								Log.info(order.getOrderId()+" "+order.getStatus()+" "+order.getOrderChangeTime());
								
								/*
								 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
								 *2��ɾ���ȴ���Ҹ���ʱ��������� 
								 */		
								String sku;
								long quantity;
								String sql="";
								if (order.getStatus().equals("PR")||order.getStatus().equals("PP"))
								{	
									
									if (!OrderManager.isCheck("����Ϳⶩ��", this.getDao().getConnection(), order.getOrderId()))
									{
										if (!OrderManager.TidLastModifyIntfExists("����Ϳⶩ��", this.getDao().getConnection(), order.getOrderId(),order.getOrderChangeTime()))
										{
											OrderUtils.createInterOrder(this.getDao().getConnection(),order,tradecontactid,username);
															
											for(Iterator ito=order.getOrderDetails().iterator();ito.hasNext();)
											{
												OrderDetail detail=(OrderDetail) ito.next();
												
												sku=detail.getMainId();								
												quantity=detail.getCount();
												
												StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, order.getOrderId(),sku);
												StockManager.addSynReduceStore(jobName, this.getDao().getConnection(), tradecontactid, order.getStatus(),order.getOrderId(), sku, -quantity,false);
											}
										
										}
									}
			
									
								}
								
						}catch(Exception ex){
							if (this.getDao() != null && !this.getDao().getConnection().getAutoCommit())
								this.getDao().rollback();
							Log.error(jobName, ex.getMessage());
							}
						}
					
					
					//�ж��Ƿ�����һҳ
					if(pageTotal>pageIndex)
						pageIndex ++ ;
					else
					{
						hasNextPage = false ;
						break;
					}
					
					
				}//whileδ		
				break;
			} catch (Exception e) {
				if (++k >= 5)
					throw e;
				if (this.getDao() != null && !this.getDao().getConnection().getAutoCommit())
					this.getDao().rollback();
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
					
			
	}
		
}
