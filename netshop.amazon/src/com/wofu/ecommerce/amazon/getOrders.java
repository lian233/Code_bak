package com.wofu.ecommerce.amazon;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrders;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersClient;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersConfig;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersRequest;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersResult;
import com.amazonservices.mws.orders._2013_09_01.model.Order;
import com.amazonservices.mws.orders._2013_09_01.model.OrderItem;
import com.wofu.ecommerce.amazon.Params;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
public class getOrders extends Thread {

	private static String jobname = "��ȡ����ѷ������ҵ";

	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	
	private static long daymillis=24*60*60*1000L;
	
	private Date lasttime;

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			try {					
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.amazon.Params.dbname);	
				
				MarketplaceWebServiceOrdersConfig config = new MarketplaceWebServiceOrdersConfig();
				config.setServiceURL(Params.serviceurl);
				
				MarketplaceWebServiceOrders service = new MarketplaceWebServiceOrdersClient(
						Params.accesskeyid, 
						Params.secretaccesskey, 
						Params.applicationname, 
						Params.applicationversion, 
						config);
				lasttime=new Date(Formatter.parseDate(PublicUtils.getConfig(connection, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime()+1000L);
			
				XMLGregorianCalendar cal=AmazonUtil.convertToXMLGregorianCalendar(lasttime);
		
				ListOrdersRequest request = new ListOrdersRequest();
		         request.setSellerId(Params.sellerid);
		         request.setLastUpdatedAfter(cal);
		         request.setMWSAuthToken(Params.token);
		         ArrayList alist=new ArrayList();
		         alist.add(Params.marketplaceid);
		        
		         //MarketplaceIdList plist=new MarketplaceIdList(alist);
		     
		         request.setMarketplaceId(alist);
		         Log.info("��ȡ��������ʼ");
		         invokeListOrders(connection,service, request);
		         Log.info("��ȡ�����������");
			} catch (Throwable e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
				e.printStackTrace();
			} finally {

				try {
					if (connection != null)
						connection.close();
				} catch (Throwable e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.amazon.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	private void invokeListOrders(Connection conn,MarketplaceWebServiceOrders service, ListOrdersRequest request)  throws Throwable{
			ListOrdersResponse response= service.listOrders(request);
           
            if (response.isSetListOrdersResult()) {
      
                ListOrdersResult  listOrdersResult = response.getListOrdersResult();
     
                
                //��ʱ��ʹ�÷�ҳ
                /*
                if (listOrdersResult.isSetNextToken()) {
                    System.out.println("            NextToken");
                    System.out.println();
                    System.out.println("                " + listOrdersResult.getNextToken());
                    System.out.println();
                }
                */

                if (listOrdersResult.isSetOrders()) {
            
                	java.util.List<Order> orderList = listOrdersResult.getOrders();
                    for (Order order : orderList) {
                    

                  
						Log.info(order.getAmazonOrderId()+" "+order.getOrderStatus()+" "+Formatter.format(AmazonUtil.convertToDate(order.getLastUpdateDate()),Formatter.DATE_TIME_FORMAT));
						//(serviceurl, accesskeyid, secretaccesskey, applicationname, applicationversion, sellerid, AmazonOrderId)
						List<OrderItem> orderitems=OrderUtils.getOrderItemList(Params.serviceurl,Params.accesskeyid,Params.secretaccesskey,
								Params.applicationname,Params.applicationversion,Params.sellerid,order.getAmazonOrderId());
						
						/*
						 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 *2��ɾ���ȴ���Ҹ���ʱ��������� 
						 */		
						String sku;
						String sql="";
						if (order.getOrderStatus().equals("Unshipped"))
						{	
							
							if (!OrderManager.isCheck("�������ѷ����", conn, String.valueOf(order.getAmazonOrderId())))
							{
								if (!OrderManager.TidLastModifyIntfExists("�������ѷ����", conn, String.valueOf(order.getAmazonOrderId()),AmazonUtil.convertToDate(order.getLastUpdateDate())))
								{
									OrderUtils.createInterOrder(jobname,conn,order,orderitems,Params.tradecontactid);
									
									for (OrderItem orderitem : orderitems) 
									{								
										sku=orderitem.getSellerSKU();
									
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, order.getAmazonOrderId(),sku);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, order.getOrderStatus(),order.getAmazonOrderId(), 
												sku, -Integer.valueOf(orderitem.getQuantityOrdered()).longValue(),false);
									}
								
								}
							}
	
							//�ȴ���Ҹ���ʱ��¼�������
						}
						else if (order.getOrderStatus().equals("Pending") )
						{						
							for (OrderItem orderitem : orderitems) 
							{								
								sku=orderitem.getSellerSKU();
							
								StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, order.getAmazonOrderId(), sku, Integer.valueOf(orderitem.getQuantityOrdered()).longValue());
								StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid,order.getOrderStatus(),order.getAmazonOrderId(), sku, 
										-Integer.valueOf(orderitem.getQuantityOrdered()).longValue(),false);
							}
							
				
				  
							//�����Ժ��û��˿�ɹ��������Զ��ر�
							//�ͷſ��,����Ϊ����
						} else if (order.getOrderStatus().equals("Unfulfillable") )
						{					
							OrderManager.CancelOrderByCID(jobname, conn, order.getAmazonOrderId());
							for (OrderItem orderitem : orderitems) 
							{	
								sku=orderitem.getSellerSKU();
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, order.getAmazonOrderId(), sku);
								
							}
		
							//������ǰ�����һ���������رս���
							//�ͷŵȴ���Ҹ���ʱ�����Ŀ��
						}else if (order.getOrderStatus().equals("Canceled"))
						{
							for (OrderItem orderitem : orderitems) 
							{
								sku=orderitem.getSellerSKU();
					
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, order.getAmazonOrderId(), sku);
								if (StockManager.WaitPayStockExists(jobname,conn,Params.tradecontactid, order.getAmazonOrderId(), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
									StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, order.getOrderStatus(),order.getAmazonOrderId(), sku, Long.valueOf(orderitem.getQuantityOrdered()).longValue(),false);
							}
							
					
				
						}
						else if (order.getOrderStatus().equals("Shipped"))
						{
							for (OrderItem orderitem : orderitems) 
							{
								sku=orderitem.getSellerSKU();
					
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, order.getAmazonOrderId(), sku);								
							}
			
						}
			
						
						//	����ͬ����������ʱ��
		                if (AmazonUtil.convertToDate(order.getLastUpdateDate()).compareTo(lasttime)>0)
		                {
		                	lasttime=AmazonUtil.convertToDate(order.getLastUpdateDate());
		                }
		                
		           
						
                    }
                } 
            } 
           
    	
			try
        	{
        		String value=Formatter.format(lasttime,Formatter.DATE_TIME_FORMAT);
        		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
        	}catch(Throwable je)
        	{
        		je.printStackTrace();
        		Log.error(jobname,je.getMessage());
        	}
		

           
      
    }
	
	
}

