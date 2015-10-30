package com.wofu.ecommerce.coo8;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import com.coo8.api.Coo8Client;
import com.coo8.api.DefaultCoo8Client;
import com.coo8.api.request.order.OrdersGetRequest;
import com.coo8.api.response.order.OrdersGetResponse;
import com.coo8.open.order.Order;
import com.coo8.open.order.OrderDetail;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;


public class getOrders extends Thread {
	private static String jobName = "��ȡ��Ͷ�����ҵ";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;
	
	public void run() {
		
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {		
			Connection connection = null;
			is_importing = true;
			try {			
				
				connection = PoolHelper.getInstance().getConnection(Params.dbname);
											
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getOrderList(connection);
				
				
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (60 * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	/*
	 * ��ȡһ��֮�ڵ����ж���
	 */
	private void getOrderList(Connection conn) throws Exception
	{		
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<5;)
		{
			try
			{
				while(hasNextPage)
				{
					Date startDate = new Date();
					Coo8Client coo8=new DefaultCoo8Client(Params.url,Params.appKey,Params.secretKey);
					OrdersGetRequest orderrequest=new OrdersGetRequest();
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					orderrequest.setStartDate(startdate);
					orderrequest.setEndDate(enddate);
					orderrequest.setPageNo(pageIndex);
					orderrequest.setPageSize(20);
					OrdersGetResponse response=coo8.execute(orderrequest);
					System.out.println(response.toString());
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
						if (pageIndex==1)		
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
								
							Log.info(jobName,"��������Ҫ����Ķ���!");
							hasNextPage=false;								
							break ;
						}
					}
						
					boolean isNeedDealList=false;
					int ignoreNum=0;  //ͳ��ȡ�صĶ����еľɶ�������--���ȫ���Ǿɶ����������ȡ����ʱ��Ϊ���ݿ�ʱ�����һ����ʱ
					for(int i=0; i<orders.size(); i++){
						try{
							Order order=orders.get(i);
							Log.info(Formatter.format(order.getOrderChangeTime(), Formatter.DATE_TIME_FORMAT));
							//ÿ�ζ�ֻ��ȡһ��������ݣ������ظ�����������ʱ��С�ڵ������´���ʱ��ʱ����
							if (order.getOrderChangeTime().compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))<=0)	{
								ignoreNum++;
								continue;
								
							}				
								
								Log.info(order.getOrderId()+" "+order.getStatus()+" "+order.getOrderChangeTime());
								
								//�Ƿ�����Ҫ����Ķ���
								isNeedDealList=true;
								/*
								 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
								 *2��ɾ���ȴ���Ҹ���ʱ��������� 
								 */		
								String sku;
								long quantity;
								String sql="";
								if (order.getStatus().equals("PR")||order.getStatus().equals("PP"))
								{	
									
									if (!OrderManager.isCheck("����Ϳⶩ��", conn, order.getOrderId()))
									{
										if (!OrderManager.TidLastModifyIntfExists("����Ϳⶩ��", conn, order.getOrderId(),order.getOrderChangeTime()))
										{
											OrderUtils.createInterOrder(conn,order,Params.tradecontactid,Params.username);
															
											for(Iterator ito=order.getOrderDetails().iterator();ito.hasNext();)
											{
												OrderDetail detail=(OrderDetail) ito.next();
												
												sku=detail.getMainId();								
												quantity=detail.getCount();
												
												StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, order.getOrderId(),sku);
												StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, order.getStatus(),order.getOrderId(), sku, -quantity,false);
											}
										
										}
									}
			
									
								}
								//������ǰ�����һ���������رս���
								//�ͷŵȴ���Ҹ���ʱ�����Ŀ��
								else if (order.getStatus().equals("CL"))
								{
									for(Iterator ito=order.getOrderDetails().iterator();ito.hasNext();)
									{
										OrderDetail detail=(OrderDetail) ito.next();
										//����SKUID����ƷID�õ�sku
										sku=detail.getMainId();	
										quantity=detail.getCount();
										
										StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, order.getOrderId(), sku);
										if (StockManager.WaitPayStockExists(jobName,conn,Params.tradecontactid, order.getOrderId(), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
											StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, order.getStatus(),order.getOrderId(), sku, quantity,false);
									}					
						
								}
								//�˻�
								else if (order.getStatus().equals("RV")||order.getStatus().equals("RT")||order.getStatus().equals("RSC")||order.getStatus().equals("RSN")
										||order.getStatus().equals("RPP")||order.getStatus().equals("RWA")||order.getStatus().equals("RFL")||order.getStatus().equals("R2C")
										||order.getStatus().equals("RCP"))
								{
									
									OrderUtils.getRefund(jobName,conn,Params.tradecontactid,order);								
									
					
								}
								//�����
								else if (order.getStatus().equals("DL"))
								{
									for(Iterator ito=order.getOrderDetails().iterator();ito.hasNext();)
									{
										OrderDetail detail=(OrderDetail) ito.next();
										//����SKUID����ƷID�õ�sku
										sku=detail.getMainId();	
							
										StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, order.getOrderId(), sku);								
									}
					
								}
								//����ͬ����������ʱ��
				                if (order.getOrderChangeTime().compareTo(modified)>0)
				                {
				                	modified=order.getOrderChangeTime();
				                }
						}catch(Exception ex){
							if (conn != null && !conn.getAutoCommit())
								conn.rollback();
							Log.error(jobName, ex.getMessage());
							}
						}
						if(ignoreNum==orders.size()){  //���еĶ���������ǰ�Ķ���,��ȡ����ʱ���޸�Ϊ���ݿ�ʱ��ĺ�һ��0��
							try
			                {	
								String value=Formatter.format(startDate,Formatter.DATE_TIME_FORMAT);
								PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
			                }catch(JException je)
			                {
			                	Log.error(jobName, je.getMessage());
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
				break;
			} catch (Exception e) {
				if (++k >= 5)
					throw e;
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
					
			
	}
	
	
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}

}
