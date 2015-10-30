package com.wofu.ecommerce.yhd;


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
import com.wofu.ecommerce.yhd.utils.Utils;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;

public class GetOrders extends Thread {

	private static String jobname = "��ȡһ�ŵ궩����ҵ";
	
	private static long daymillis=24*60*60*1000L;
	private static String  orderLineStatus="ORDER_WAIT_PAY,ORDER_PAYED,"
	+"ORDER_WAIT_SEND,ORDER_ON_SENDING,ORDER_RECEIVED,ORDER_FINISH,ORDER_GRT,ORDER_CANCEL";
	
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
						com.wofu.ecommerce.yhd.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.yhd.Params.waittime * 1000))		
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
					orderlistparams.put("appKey", Params.app_key);
					orderlistparams.put("sessionKey", Params.token);
			        orderlistparams.put("format", Params.format);
			        orderlistparams.put("method", "yhd.orders.get");
			        orderlistparams.put("ver", Params.ver);
			        orderlistparams.put("dateType", "5");
			        orderlistparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("startTime", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("endTime", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			       
			        orderlistparams.put("orderStatusList", orderLineStatus);
			        orderlistparams.put("curPage", String.valueOf(pageno));
			        orderlistparams.put("pageRows", "50");
			        
					String responseOrderListData = Utils.sendByPost(orderlistparams, Params.app_secret, Params.url);
					//System.out.println(responseOrderListData);
					
					JSONObject responseproduct = new JSONObject(responseOrderListData);
					if (responseOrderListData.indexOf("errInfoList")>=0)
					{
						JSONArray errinfolist=responseproduct.getJSONObject("response").optJSONObject("errInfoList").optJSONArray("errDetailInfo");
						String errdesc="";
						
						for(int j=0;j<errinfolist.length();j++)
						{
							JSONObject errinfo=errinfolist.getJSONObject(j);
							
							errdesc=errdesc+" "+errinfo.getString("errorDes"); 
												
						}
						if (errdesc.indexOf("�����б���Ϣ������")>=0)
						{
							if (pageno==1L)		
							{
								try
								{
									//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
									if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
											compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
									{
										try
					                	{
											String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
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
						}
						Log.error(jobname, "ȡ�����б�ʧ��:"+errdesc);
						k=10;
						break;
					}
	
					int totalCount=responseproduct.getJSONObject("response").getInt("totalCount");
					int errorCount=responseproduct.getJSONObject("response").getInt("errorCount");
					
					if (errorCount>0)
					{
						String errdesc="";
						JSONArray errlist=responseproduct.getJSONObject("response").getJSONObject("errInfoList").getJSONArray("errDetailInfo");
						for(int j=0;j<errlist.length();j++)
						{
							JSONObject errinfo=errlist.getJSONObject(j);
							
							errdesc=errdesc+" "+errinfo.getString("errorDes"); 
												
						}
						
						if (errdesc.indexOf("�����б���Ϣ������")<0)
						{
							Log.error(jobname, errdesc);
							k=10;
							break;
						}
					}
							
					if (totalCount==0)
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
		
					JSONArray orderlist=responseproduct.getJSONObject("response").getJSONObject("orderList").getJSONArray("order");
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						Order o=OrderUtils.getOrderByID(order.getString("orderCode"),Params.app_key,Params.token,Params.format,Params.ver);
						Log.info(o.getOrderCode()+" "+o.getOrderStatus()+" "+Formatter.format(o.getUpdateTime(),Formatter.DATE_TIME_FORMAT));
						
						 //*1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 //*2��ɾ���ȴ���Ҹ���ʱ��������� 
						 		
						String sku;
						String sql="";
						if (o.getOrderStatus().equals("ORDER_PAYED") 
								|| o.getOrderStatus().equals("ORDER_TRUNED_TO_DO")
								|| o.getOrderStatus().equals("ORDER_CAN_OUT_OF_WH"))
						{	
							
							if (!OrderManager.isCheck("���һ�ŵ궩��", conn, o.getOrderCode()))
							{
								if (!OrderManager.TidLastModifyIntfExists("���һ�ŵ궩��", conn, o.getOrderCode(),o.getUpdateTime()))
								{
									OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username);
									
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getOuterId();
										
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrderCode(),sku);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrderStatus(),o.getOrderCode(), sku, -item.getOrderItemNum(),false);
									}
								}
							}
	
							//�ȴ���Ҹ���ʱ��¼�������
						}
						
						
						else if (o.getOrderStatus().equals("ORDER_WAIT_PAY"))
						{						
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuterId();
							
								StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrderCode(), sku, item.getOrderItemNum());
								StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrderStatus(),o.getOrderCode(), sku, -item.getOrderItemNum(),false);
							}
							//�����Ժ��û��˿�ɹ��������Զ��ر�
							//�ͷſ��,����Ϊ����						
						}else if (o.getOrderStatus().equals("ORDER_CANCEL"))
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuterId();
					
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrderCode(), sku);
								if (StockManager.WaitPayStockExists(jobname,conn,Params.tradecontactid, o.getOrderCode(), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
									StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrderStatus(),o.getOrderCode(), sku, item.getOrderItemNum(),false);
							}
							
							
				
						}
						else if (o.getOrderStatus().equals("ORDER_FINISH"))
						{
							for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuterId();
					
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrderCode(), sku);								
							}
			
						}
						else if (o.getOrderStatus().equals("ORDER_CUSTOM_CALLTO_RETUR")
							||o.getOrderStatus().equals("ORDER_CUSTOM_CALLTO_CHANGE")
							||o.getOrderStatus().equals("ORDER_RETURNED")
							||o.getOrderStatus().equals("ORDER_CHANGE_FINISHED"))
						{
							
							OrderUtils.getRefund(conn,Params.tradecontactid,o);
								
				
						}
						
						//����ͬ����������ʱ��
		                if (o.getUpdateTime().compareTo(modified)>0)
		                {
		                	modified=o.getUpdateTime();
		                }
					}
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/50.0))).intValue()) break;
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
