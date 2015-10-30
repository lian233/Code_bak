package com.wofu.ecommerce.dangdang;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.dangdang.util.CommHelper;
public class GetOrders extends Thread {
	private static String jobName = "��ȡ����������ҵ";
	
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	
	private static long daymillis=24*60*60*1000L;
	
	private String lasttime;
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	public void run() {

		Log.info(jobName, "����[" + jobName + "]ģ��");
		do {
			Connection connection = null;

			try {
				Dangdang.setCurrentDate_getOrder(new Date());
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	

				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
	
				//��ȡ�����¶��� ����״̬ 100���ȴ����� 101���ȴ����� 300���ѷ��� 400�����ʹ� 1000�����׳ɹ� -100��ȡ�� 1100������ʧ��
				getOrderList(connection) ;
				
			} catch (Throwable e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Throwable e1) {
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				try {
					if (connection != null)
						connection.close();
				} catch (Throwable e) {
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Throwable e) {
					Log.warn(jobName, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	
	//��ȡ�����¶���
	public void getOrderList(Connection conn) throws Throwable
	{
		int pageIndex = 1 ;
		boolean hasNextPage = true ;	
		
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		Log.info("���λ�ȡ����������ʼ");
		for (int k=0;k<5;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//������
					String methodName="dangdang.orders.list.get";
					//������֤�� --md5;����
					Date temp = new Date();
					String sign = CommHelper.getSign(Params.app_Secret, Params.app_key, methodName, Params.session,temp) ;
					Hashtable<String, String> params = new Hashtable<String, String>() ;
					params.put("sign", sign) ;
					params.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
					params.put("app_key",Params.app_key);
					params.put("method",methodName);
					params.put("format","xml");
					params.put("session",Params.session);
					params.put("sign_method","md5");
					params.put("lastModifyTime_end", URLEncoder.encode(Formatter.format(enddate, Formatter.DATE_TIME_FORMAT), Params.encoding)) ;
					params.put("lastModifyTime_start", URLEncoder.encode(Formatter.format(startdate, Formatter.DATE_TIME_FORMAT), Params.encoding)) ;
					params.put("p", String.valueOf(pageIndex)) ;
					params.put("pageSize", String.valueOf(Params.total)) ;
					params.put("sendMode", Params.sendMode) ;
					
					//��ȡ���ؽ��
					String repsonseText = CommHelper.sendRequest(Params.url,"GET",params,"");
					repsonseText = CommHelper.filterChar(repsonseText);
					Document doc = DOMHelper.newDocument(repsonseText, Params.encoding);
					Element urlset = doc.getDocumentElement();
					
					if(DOMHelper.ElementIsExists(urlset,"Error"))
					{
						Element error = (Element) urlset.getElementsByTagName("Error").item(0);
						String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
						String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
						if(!"".equals(operCode))
						{
							Log.error("��ȡ���������б�", "��ȡ�����б�ʧ�ܣ������룺"+operCode+",���������Ϣ��"+operation);
							hasNextPage = false ;
							break ;
						}
					}
	
					Element totalInfo = (Element) urlset.getElementsByTagName("totalInfo").item(0) ;
					
					String pageTotal = DOMHelper.getSubElementVauleByName(totalInfo, "pageTotal");
					
					if (pageTotal==null || pageTotal.equals("") || pageTotal.equals("0"))
					{				
						if (n==1)		
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
				                	}catch(Throwable je)
				                	{
				                		Log.error(jobName, je.getMessage());
				                	}
								}
							}catch(Throwable e)
							{
								Log.error(jobName, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						break;
					}
					
					NodeList ordersList = urlset.getElementsByTagName("OrderInfo") ;
					for(int i = 0 ; i< ordersList.getLength() ; i++)
					{
						try{
							Element orderInfo = (Element) ordersList.item(i) ;
							
							String orderID = DOMHelper.getSubElementVauleByName(orderInfo, "orderID") ;
							Order o = OrderUtils.getOrderByID(Params.url, orderID,Params.session,Params.app_key,Params.app_Secret) ;
							
							if(o != null)
							{
								Log.info("�����š�"+ o.getOrderID() +"��,״̬��"+ OrderUtils.getOrderStateByCode(o.getOrderState()) +"��,����޸�ʱ�䡾"+ Formatter.format(o.getLastModifyTime(),Formatter.DATE_TIME_FORMAT) +"��") ;

								//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
								if("101".equals(o.getOrderState()))
								{	//����������ڱ�customerorder�ͱ�CustomerOrderRefList����������Ӧ�ļ�¼ʱ������ִ��
									if (!OrderManager.isCheck(jobName, conn, orderID))
									{	//����������ڽӿ�����ns_customerorder��û�ж�Ӧ�ļ�¼�������ź�����޸�ʱ��Ҫ��ͬ������ڣ�������ִ��
										if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
										{
											try
											{	//�Ѷ�����Ϣ����֪ͨ��it_downnote�ͽӿ�����ns_customerorder���Ѷ����е�������Ʒ���붩���ӿ���Ʒ��ϸ��ns_orderitem
												OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
												ArrayList<OrderItem> itemList = o.getOrderItemList() ;
												//ѭ�������������Ʒ�б�
												for(int j= 0 ; j < itemList.size() ; j ++)
												{
													String sku = itemList.get(j).getOuterItemID() ;
													//��������Ʒ��δ������������eco_WaitPayStock���д��ڼ�¼�������eco_WaitPayStock���еĶ�Ӧ��¼ɾ������дһ��ͬ�������ݵ�δ����������汸�ݱ���(eco_WaitPayStockBak)
													//��������Ʒ��δ������������eco_WaitPayStock���в�������Ӧ�ļ�¼�����ѯ�����Ʒ�ǲ��������Ʒ������һ��(MultiSKURef������������Ȱ�MultiSKURef�ж�Ӧ����Ʒ��¼��δ����������汸�ݱ���(eco_WaitPayStockBak)����ɾ��δ������������eco_WaitPayStock����Ӧ�ļ�¼
													StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, o.getOrderID(),sku);
												}
												
											} catch(Throwable sqle)
											{
												throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
											}
										}
									}
								}
								//�ȴ�����,�����,ͬ����������
								else if("100".equals(o.getOrderState()))
								{
									ArrayList<OrderItem> itemList = o.getOrderItemList() ;
									for(int j = 0 ; j < itemList.size() ; j ++)
									{
										String sku = itemList.get(j).getOuterItemID() ;
										long qty= itemList.get(j).getOrderCount();
										
										//������棬����������Ŀ��
										StockManager.addWaitPayStock(jobName, conn,Params.tradecontactid, o.getOrderID(), sku, qty);
										StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getOrderState(),o.getOrderID(), sku, -qty,false);
									}
								}
								//����ȡ��,������ȡ�����
								else if("-100".equals(o.getOrderState()))
								{
									ArrayList<OrderItem> itemList = o.getOrderItemList() ;
									for(int j = 0 ; j < itemList.size() ; j ++)
									{
										String sku = itemList.get(j).getOuterItemID() ;
										long qty= itemList.get(j).getOrderCount();
										//ɾ����������棬����������Ŀ��
										StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, o.getOrderID(),sku);
										StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getOrderState(),o.getOrderID(), sku, qty,false);
									}
									
									//ȡ������
									String sql="declare @ret int;  execute  @ret = IF_CancelCustomerOrder '" + orderID + "';select @ret ret;";
									int resultCode = SQLHelper.intSelect(conn, sql) ;
									//ȡ������ʧ��
									if(resultCode == 2)			
										Log.info("��������ȡ������ʧ��,����:"+orderID+"");						
									else
										Log.info("��������ȡ�������ɹ�,����:"+orderID+"");
									
								}
								else if (o.getOrderState().equals("1000"))  //���׳ɹ�
								{
									ArrayList<OrderItem> itemList = o.getOrderItemList() ;
									for(int j = 0 ; j < itemList.size() ; j ++)
									{
										String sku = itemList.get(j).getOuterItemID() ;
							
										StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, o.getOrderID(), sku);								
									}
					
								}
								
								//�����ǰ����ʱ����ڿ�ʼȡ����ʱ�䣬������´�ȡ����ʱ��(����ȡ�����б�����޸�ʱ��)
								//����ͬ����������ʱ��
				                if (o.getLastModifyTime().compareTo(modified)>0)
				                {
				                	modified=o.getLastModifyTime();
				                }
							}
						}catch(Throwable ex){
							if(conn!=null && !conn.getAutoCommit()) conn.rollback();
							Log.error(jobName, ex.getMessage());
						}
					}
					//�ж��Ƿ�����һҳ
					if("".equals(pageTotal) || pageTotal == null)
						pageTotal="0" ;
					if(pageIndex >= Integer.parseInt(pageTotal))
						hasNextPage = false ;
					else
						pageIndex ++ ;
					
					n++;
				}
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(Throwable je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				
				break;
			} catch (Throwable e) 
			{
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
		Log.info("���λ�ȡ�����������");
	}

	
	
	
}