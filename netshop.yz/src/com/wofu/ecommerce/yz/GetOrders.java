package com.wofu.ecommerce.yz;
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
import com.wofu.ecommerce.yz.utils.Utils;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;
public class GetOrders extends Thread {
	private static String jobname = "��ȡ���޶�����ҵ";
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
						com.wofu.ecommerce.yz.Params.dbname);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.yz.Params.waittime * 1000))		
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
					orderlistparams.put("app_id", Params.app_id);
			        orderlistparams.put("format", Params.format);
			        orderlistparams.put("method", "kdt.trades.sold.get");
			        orderlistparams.put("sign_method", "MD5");
			        orderlistparams.put("v", Params.ver);
			        orderlistparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("start_update", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("end_update", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        orderlistparams.put("page_no", String.valueOf(pageno));
			        orderlistparams.put("page_size", Params.pageSize);
			        
					String responseOrderListData = Utils.sendByPost(orderlistparams, Params.AppSecret, Params.url);
					Log.info(responseOrderListData);
					
					JSONObject responseproduct = new JSONObject(responseOrderListData);
					if (!responseproduct.isNull("error_response"))
					{
						String errdesc=responseproduct.getJSONObject("error_response").getString("msg");
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
					JSONObject orderInfo = responseproduct.getJSONObject("response");
					int totalCount=orderInfo.getInt("total_results");
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
						Log.info("�˴ζ�����Ϊ0");
						break;
					}
					JSONArray orderlist=orderInfo.getJSONArray("trades");
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						
						Order o=new Order();
						o.setObjValue(o,order);
				
						Log.info(o.getTid()+" "+o.getStatus()+" "+Formatter.format(o.getUpdate_time(),Formatter.DATE_TIME_FORMAT));
						
						 //*1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 //*2��ɾ���ȴ���Ҹ���ʱ��������� 
						 		
						String sku;
						String sql="";
						if (o.getStatus().equals("WAIT_SELLER_SEND_GOODS")) 
						{	
							if (!OrderManager.isCheck("������޶���", conn, o.getTid()))
							{
								if (!OrderManager.TidLastModifyIntfExists("������޶���", conn, o.getTid(),o.getUpdate_time()))
								{
									OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username);
									
									for(Iterator ito=o.getOrders().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getOuter_sku_id();
										
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getTid(),sku);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getTid(), sku, -item.getNum(),false);
									}
								}
							}
	
							//�ȴ���Ҹ���ʱ��¼�������
						}
						
						else if (o.getStatus().equals("WAIT_BUYER_PAY"))
						{						
							for(Iterator ito=o.getOrders().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuter_sku_id();
							
								StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, o.getTid(), sku, item.getNum());
								StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getTid(), sku, -item.getNum(),false);
							}
							
							//�����Ժ��û��˿�ɹ��������Զ��ر�  �����û�����ǰ�رս���
							//�ͷſ��,����Ϊ����						
						}else if (o.getStatus().equals("TRADE_CLOSED") || "TRADE_CLOSED_BY_USER ".equals(o.getStatus()))
						{
							for(Iterator ito=o.getOrders().getRelationData().iterator();ito.hasNext();)
							{
								OrderItem item=(OrderItem) ito.next();
								sku=item.getOuter_sku_id();
					
								StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getTid(), sku);
								if (StockManager.WaitPayStockExists(jobname,conn,Params.tradecontactid, o.getTid(), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
									StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getTid(), sku, item.getNum(),false);
							}
							
							
				
						}
						//����ͬ����������ʱ��
		                if (o.getUpdate_time().compareTo(modified)>0)
		                {
		                	modified=o.getUpdate_time();
		                }
					}
					//�ж��Ƿ�����һҳ
					if (pageno==(int)Math.ceil(totalCount/Float.parseFloat(Params.pageSize))) break;
					
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
