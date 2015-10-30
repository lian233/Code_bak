package com.wofu.ecommerce.mgj;


import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.mgj.utils.Utils;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;

public class GetOrders extends Thread {

	private static String jobname = "��ȡĢ���ֶ�����ҵ";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;


	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.mgj.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				Params.token = PublicUtils.getToken(connection, Integer.parseInt(Params.tradecontactid));
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.mgj.Params.waittime * 1000))		
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
				while(true)
				{
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					Map<String, String> orderlistparams = new HashMap<String, String>();
			        //ϵͳ����������
					orderlistparams.put("app_key", Params.app_key);
					orderlistparams.put("access_token", Params.token);
			        orderlistparams.put("method", "youdian.trade.sold.get");
			        orderlistparams.put("start_updated", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("end_updated", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("page_no", String.valueOf(pageno));
			        orderlistparams.put("page_size", "50");
			        
					String responseOrderListData = Utils.sendByPost(orderlistparams,"", Params.url);
					Log.info("result:��"+responseOrderListData);
					
					JSONObject responseproduct = new JSONObject(responseOrderListData);
					if (responseproduct.getJSONObject("status").getInt("code")!=10001)
					{
						Log.error(jobname, "ȡ�����б�ʧ��:"+responseproduct.getJSONObject("status").getString("msg"));
						k=10;
						break;
					}
					JSONObject orders = responseproduct.getJSONObject("result").getJSONObject("data");
					int hasNext= orders.getInt("has_next");  //1����û����һҳ
					JSONArray orderlist=orders.getJSONArray("trades");
					if (1==hasNext && orderlist.length()==1)
					{
						if (pageno==1L)		
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
						k=10;
						break;
						
					}
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						Order o= new Order();
						o.setObjValue(o, order);
						o.setFieldValue(o, "orders", order.getJSONArray("orders"));
						Log.info(o.getTid()+" "+o.getStatus()+" "+" "+o.getPay_status()+" "+o.getShip_status()+" "+Formatter.format(o.getLastmodify(),Formatter.DATE_TIME_FORMAT));
						
						 //*1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 //*2��ɾ���ȴ���Ҹ���ʱ��������� 
						 		
						String sku;
						String sql="";
						if (o.getStatus().equals("TRADE_ACTIVE") && "PAY_FINISH".equals(o.getPay_status()) && "SHIP_NO".equals(o.getShip_status()))
						{	
							
							if (!OrderManager.isCheck("���Ģ���ֶ���", conn, o.getTid()))
							{
								if (!OrderManager.TidLastModifyIntfExists("���Ģ���ֶ���", conn, o.getTid(),o.getLastmodify()))
								{
									OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username);
									
									for(Iterator ito=o.getOrders().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getSku_bn();
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getTid(),sku);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, "�ȴ�����",o.getTid(), sku, -item.getItems_num(),false);
									}
								}
							}
	
							//�ȴ���Ҹ���ʱ��¼�������
						}
						
						
						else if (o.getStatus().equals("TRADE_ACTIVE") && "PAY_NO".equals(o.getPay_status()))
						{						
							for(Iterator ito=o.getOrders().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getSku_bn();
							
								StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, o.getTid(), sku, item.getItems_num());
								StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, "�ȴ�����",o.getTid(), sku, -item.getItems_num(),false);
							}
							//�����Ժ��û��˿�ɹ��������Զ��ر�
							//�ͷſ��,����Ϊ����						
						}else if (o.getStatus().equals("TRADE_CLOSED"))
						{
							for(Iterator ito=o.getOrders().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getSku_bn();
					
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getTid(), sku);
								if (StockManager.WaitPayStockExists(jobname,conn,Params.tradecontactid, o.getTid(), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
									StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, "�ȴ�����",o.getTid(), sku, item.getItems_num(),false);
							}
							
							
				
						}
						else if (o.getStatus().equals("TRADE_FINISHED"))
						{
							for(Iterator ito=o.getOrders().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getSku_bn();
					
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getTid(), sku);								
							}
			
						}
						else if (o.getStatus().equals("ORDER_CUSTOM_CALLTO_RETUR")
							||o.getStatus().equals("ORDER_CUSTOM_CALLTO_CHANGE")
							||o.getStatus().equals("ORDER_RETURNED")
							||o.getStatus().equals("ORDER_CHANGE_FINISHED"))
						{
							
							//OrderUtils.getRefund(conn,Params.tradecontactid,o);
								
				
						}
						
						//����ͬ����������ʱ��
		                if (o.getLastmodify().compareTo(modified)>0)
		                {
		                	modified=o.getLastmodify();
		                }
					}
					//�ж��Ƿ�����һҳ
					if (1==hasNext) break;
					pageno++;
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
				k=10;
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
