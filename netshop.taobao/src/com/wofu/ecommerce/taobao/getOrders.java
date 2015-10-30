package com.wofu.ecommerce.taobao;


import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;


import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;

import com.taobao.api.request.TradesSoldIncrementGetRequest;

import com.taobao.api.response.TradesSoldIncrementGetResponse;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;

import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;

import com.wofu.common.tools.util.log.Log;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;

public class getOrders extends Thread {

	private static String jobname = "��ȡ�Ա�������ҵ";
	
	private static long daymillis=24*60*60*1000L;
	
	private  String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;
	
	private boolean waitbuyerpayisin=false;


	public getOrders() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.taobao.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				
				String sql="select isnull(value,0) from config where name='�ȴ�������Ƿ��ϵͳ'";
				if (SQLHelper.strSelect(connection, sql).equals("1"))
					waitbuyerpayisin=true;
				
	
				getOrderList(connection);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.taobao.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	
	/*
	 * ��ȡһ��֮������ж���
	 */
	private void getOrderList(Connection conn) throws Exception
	{		
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				TaobaoClient client=new DefaultTaobaoClient(Params.url,Params.appkey, Params.appsecret,"xml");
				TradesSoldIncrementGetRequest req=new TradesSoldIncrementGetRequest();
				req.setFields("tid,modified,status,orders.outer_sku_id,orders.num");	
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				req.setStartModified(startdate);
				req.setEndModified(enddate);
				req.setPageNo(pageno);
				req.setPageSize(40L);
				req.setUseHasNext(true);
				TradesSoldIncrementGetResponse response = client.execute(req , Params.authcode);

				Log.info("ȡ�Ա�������ʼ");
				int i=1;
			
				while(true)
				{
								
					if (response.getTrades()==null || response.getTrades().size()<=0)
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
				                		Log.error(jobname, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobname, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						break;
					}
					
					
					for(Iterator it=response.getTrades().iterator();it.hasNext();)
					{
						Trade td=(Trade) it.next();
						
				
						td=OrderUtils.getFullTrade(String.valueOf(td.getTid()),Params.url,Params.appkey,Params.appsecret,Params.authcode);
						
					
				
						Log.info(td.getTid()+" "+td.getStatus()+" "+Formatter.format(td.getModified(),Formatter.DATE_TIME_FORMAT));
						/*
						 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 *2��ɾ���ȴ���Ҹ���ʱ��������� 
						 */		
						String sku;
						String sql="";
						if (td.getStatus().equals("WAIT_SELLER_SEND_GOODS"))
						{	
							
							if (!OrderManager.isCheck("����Ա�����", conn, String.valueOf(td.getTid())))
							{
								if (!OrderManager.TidLastModifyIntfExists("����Ա�����", conn, String.valueOf(td.getTid()),td.getModified()))
								{
									OrderUtils.createInterOrder(conn,td,Params.tradecontactid,Params.username,true);
									for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
									{
										Order o=(Order) ito.next();
										sku=o.getOuterSkuId();
									
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(td.getTid()),sku);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, td.getStatus(),String.valueOf(td.getTid()), sku, -o.getNum(),false);
									}
								}
							}
	
							//�ȴ���Ҹ���ʱ��¼�������
						}
						
						
						else if (td.getStatus().equals("WAIT_BUYER_PAY") || td.getStatus().equals("TRADE_NO_CREATE_PAY"))
						{						
							if (waitbuyerpayisin)
							{	
								if (!OrderManager.TidLastModifyIntfExists("����Ա�����", conn, String.valueOf(td.getTid()),td.getModified()))
								{
									OrderUtils.createInterOrder(conn,td,Params.tradecontactid,Params.username,false);
									
								}
							}
							for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
							{
								Order o=(Order) ito.next();
								sku=o.getOuterSkuId();
								StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(td.getTid()), sku, o.getNum());
								StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, td.getStatus(),String.valueOf(td.getTid()), sku, -o.getNum(),false);
							}
							
			
				  
							//�����Ժ��û��˿�ɹ��������Զ��ر�
							//�ͷſ��,����Ϊ����
						} else if (td.getStatus().equals("TRADE_CLOSED"))
						{			
							OrderManager.CancelOrderByCID(jobname, conn, String.valueOf(td.getTid()));
							for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
							{
								Order o=(Order) ito.next();		
								sku=o.getOuterSkuId();
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(td.getTid()), sku);
								//StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, td.getStatus(),String.valueOf(td.getTid()),sku, o.getNum(),false);
							}
		
							//������ǰ�����һ���������رս���
							//�ͷŵȴ���Ҹ���ʱ�����Ŀ��
						}else if (td.getStatus().equals("TRADE_CLOSED_BY_TAOBAO"))
						{
							
							if (waitbuyerpayisin)
							{
								
								if (!OrderManager.TidLastModifyIntfExists("����Ա�����", conn, String.valueOf(td.getTid()),td.getModified()))
								{
									OrderUtils.createInterOrder(conn,td,Params.tradecontactid,Params.username,false);
									
								}
							}

				
							for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
							{
								Order o=(Order) ito.next();
								sku=o.getOuterSkuId();
							
								 
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(td.getTid()), sku);
								
								
								if (StockManager.WaitPayStockExists(jobname,conn,Params.tradecontactid, String.valueOf(td.getTid()), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
								{
								
								
									StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, td.getStatus(),String.valueOf(td.getTid()), sku, o.getNum(),false);
								}
							}
					
							
				
						}
						else if (td.getStatus().equals("TRADE_FINISHED"))
						{
							
							for(Iterator ito=td.getOrders().iterator();ito.hasNext();)
							{
								Order o=(Order) ito.next();
								sku=o.getOuterSkuId();
					
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(td.getTid()), sku);	
								
								//���½���ʱ��
								
								OrderUtils.updateFinishedStatus(conn,Params.tradecontactid,td.getTid(),td.getEndTime());
							}
			
						}
						
						//if(Params.isc)
						//{
							//�����˻�
							for(Iterator oit=td.getOrders().iterator();oit.hasNext();)
							{						
								Order o=(Order) oit.next();					
								//Log.info("������:"+String.valueOf(td.getTid())+" �˻�ID:"+String.valueOf(o.getRefundId()));
								if (o.getRefundId()>0)
								{
									OrderUtils.getRefund(jobname,conn,Params.url,Params.appkey,
											Params.appsecret,Params.authcode,Params.tradecontactid,td,o,
											 String.valueOf(td.getTid()),o.getRefundId());
								}
				
							}
						//}
						
						
						//����ͬ����������ʱ��
		                if (td.getModified().compareTo(modified)>0)
		                {
		                	modified=td.getModified();
		                }
					}
					pageno++;
					req.setPageNo(pageno);
					response=client.execute(req , Params.authcode);
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
	            		Log.error(jobname,je.getMessage());
	            	}
				}
				//ִ�гɹ�����ѭ��
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
