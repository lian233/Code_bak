package com.wofu.netshop.dangdang.fenxiao;
import java.sql.Connection;
import com.wofu.common.tools.util.Formatter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import com.wofu.business.fenxiao.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.business.fenxiao.util.PublicUtils;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.netshop.dangdang.fenxiao.CommHelper;
import com.wofu.netshop.dangdang.fenxiao.Order;
import com.wofu.netshop.dangdang.fenxiao.OrderUtils;
import com.wofu.netshop.dangdang.fenxiao.Params;
/**
 * ���ص��������߳���
 * @author Administrator
 *
 */
public class GetOrdersRunnable implements Runnable{
	private String jobName="���ص���������ҵ";
	private Params param;
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private String lasttime="";
	private String refundlasttime;
	private static long daymillis=24*60*60*1000L;
	private CountDownLatch watch;
	private String username="";
	private boolean is_exporting = false;
	
	public GetOrdersRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
}
	public void run() {
		// TODO Auto-generated method stub
		Connection conn=null;
		try{
			conn=PoolHelper.getInstance().getConnection("shop");
			getOrderList(conn);
		}catch(Throwable e){
			try {
				if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				conn.setAutoCommit(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					Log.error(param.username,"�ر����ݿ��������: "+e1.getMessage(),null);
				}
				Log.info(param.username,jobName+" "+e.getMessage(),null);
			}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.error(param.username,"�ر����ݿ����ӳ���  "+e.getMessage());
				}
				watch.countDown();
		}
		
	}
	
	/*
	 * ��ȡһ��֮������ж���
	 */
	//��ȡ�����¶���
	public void getOrderList(Connection conn) throws Exception
	{
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		lasttime=PublicUtils.getConfig(conn,"LastOrderTime",param.shopid);
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
					
//					System.out.println("app_Secret"+param.app_Secret);
//					System.out.println("app_key"+param.app_key);
					
//					System.out.println("����4"+temp);
					String sign = CommHelper.getSign(param.app_Secret, param.app_key, methodName, param.session,temp) ;
					Hashtable<String, String> params = new Hashtable<String, String>() ;
					params.put("sign", sign) ;
					params.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
					params.put("app_key",param.app_key);
					params.put("method",methodName);
					params.put("format","xml");
					params.put("session",param.session);
					params.put("sign_method","md5");
					params.put("lastModifyTime_end", URLEncoder.encode(Formatter.format(enddate, Formatter.DATE_TIME_FORMAT), param.encoding)) ;
					params.put("lastModifyTime_start", URLEncoder.encode(Formatter.format(startdate, Formatter.DATE_TIME_FORMAT), param.encoding)) ;
					params.put("p", String.valueOf(pageIndex)) ;
					params.put("pageSize", String.valueOf(param.total)) ;
					params.put("sendMode", param.sendMode) ;
					//��ȡ���ؽ��
					String repsonseText = CommHelper.sendRequest(param.url,"GET",params,"");
//					Log.info("����"+params);
//					Log.info("���ؽ��"+repsonseText);
					repsonseText = CommHelper.filterChar(repsonseText);
					Document doc = DOMHelper.newDocument(repsonseText, param.encoding);
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
	
					Element totalInfo = (Element) urlset.getElementsByTagName("totalInfo").item(0) ;//
					
					String pageTotal = DOMHelper.getSubElementVauleByName(totalInfo, "pageTotal");
					
					if (pageTotal==null || pageTotal.equals("") || pageTotal.equals("0"))
					{				
						if (n==1)		
						{
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (Formatter.parseDate(Formatter.format(new Date(), Formatter.DATE_FORMAT),Formatter.DATE_FORMAT).
										compareTo(Formatter.parseDate(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,"LastOrderTime",param.shopid),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT),Formatter.DATE_FORMAT))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,"LastOrderTime",param.shopid),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, "LastOrderTime", param.shopid,value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(param.username,jobName, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(param.username,jobName, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						k=5;
						break;
					}
					
					NodeList ordersList = urlset.getElementsByTagName("OrderInfo") ;
					for(int i = 0 ; i< ordersList.getLength() ; i++)
					{
						try{
							Element orderInfo = (Element) ordersList.item(i) ;
							
							String orderID = DOMHelper.getSubElementVauleByName(orderInfo, "orderID") ;
							Order o = OrderUtils.getOrderByID(param.url, orderID,param.session,param.app_key,param.app_Secret) ;
							
							if(o != null)
							{
								Log.info("�����š�"+ o.getOrderID() +"��,״̬��"+ OrderUtils.getOrderStateByCode(o.getOrderState()) +"��,����޸�ʱ�䡾"+ Formatter.format(o.getLastModifyTime(),Formatter.DATE_TIME_FORMAT) +"��") ;

								//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
								if("101".equals(o.getOrderState()))
								{	//����������ڱ�customerorder�ͱ�CustomerOrderRefList����������Ӧ�ļ�¼ʱ������ִ��

										if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
										{
											try
											{	//�Ѷ�����Ϣ����֪ͨ��it_downnote�ͽӿ�����ns_customerorder���Ѷ����е�������Ʒ���붩���ӿ���Ʒ��ϸ��ns_orderitem
												OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);
												
											} catch(Throwable sqle)
											{
												throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
											}
										}
									
								}
								
								else if("50".equals(o.getOrderState()))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										//
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);	
										
									}
								}
								else if("100".equals(o.getOrderState()))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										//
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);	
										
									}
								}
								//����ȡ��,������ȡ�����
								else if("-100".equals(o.getOrderState()))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										//
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);
									}
									
								}
								else if("300".equals(o.getOrderState()))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										//
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);
									}
									
								}
								else if("400".equals(o.getOrderState()))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										//
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);
									}
									
								}
								
								else if("-200".equals(o.getOrderState()))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										//
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);
									}
									
								}
								
								else if("1100".equals(o.getOrderState()))
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										//
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);
									}
									
								}
								else if (o.getOrderState().equals("1000"))  //���׳ɹ�
								{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderID,o.getLastModifyTime()))
									{
										OrderUtils.createInterOrder(conn, o, param.tradecontactid, param.username,param.shopid);							
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
	            		PublicUtils.setConfig(conn, "LastOrderTime", param.shopid,value);	
	            	}catch(JException je)
	            	{
	            		Log.error(param.username,param.username,je.getMessage(),0);
	            	}
	            	
				}
				
				break;
			} catch (Throwable e) 
			{
				e.printStackTrace();
				if (++k >= 5)
					try {
						throw e;
					} catch (Throwable e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
		Log.info("���λ�ȡ�����������");
	}

	
	
	
}