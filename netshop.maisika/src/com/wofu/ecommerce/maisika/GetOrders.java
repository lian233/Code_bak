package com.wofu.ecommerce.maisika;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.maisika.util.CommHelper;
import com.wofu.ecommerce.maisika.Order;
import com.wofu.ecommerce.maisika.OrderItem;
import com.wofu.ecommerce.maisika.OrderUtils;
import com.wofu.ecommerce.maisika.Params;
public class GetOrders extends Thread {
	
	private static String jobname = "��ȡ��˹��������ҵ";

	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	
	private static long daymillis=24*60*60*1000L;
	
	private String lasttime;
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private boolean is_importing=false;
	public void run() {

		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;
			is_importing = true;
			try {
				
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	
				System.out.println("��ʼ��ȡ��˹������1");
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				System.out.println("��ʼ��ȡ��˹������2");
				getOrderList(connection) ;
				System.out.println("��ʼ��ȡ��˹������");
				
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
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	
	//��ȡ��˹���¶���
	public void getOrderList(Connection conn) throws Exception
	{ 
		int pageno = 1 ;  //��˹����1��ʼ
		boolean hasNextPage = true ;	

		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{	System.out.println("ID:"+Params.tradecontactid);
					System.out.println("������"+Params.dbname);
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//������
					LinkedHashMap<String,Object> map = new LinkedHashMap<String,Object>();
					map.put("&op","orders");
			        map.put("service","order");
			        map.put("vcode", Params.vcode);
			        map.put("mtime_start", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        map.put("mtime_end", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        map.put("page", String.valueOf(pageno));
			        map.put("page_size", Params.pageSize);
			        map.put("status","2");
			        //��������
			        Log.info("��"+String.valueOf(pageno)+"ҳ");
			        
					String responseOrderListData = CommHelper.doGet(map,Params.url);
//			        String responseOrderListData = json;
					//�ѷ��ص�����ת��json����
					JSONObject responseproduct=new JSONObject(responseOrderListData);
//					Log.info("����"+responseObj.getJSONArray("order_list").length());



					int totalCount=responseproduct.getInt("count");					
					Log.info("����������"+totalCount);
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
										Log.info("�޶���");
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

					
//					JSONArray orderlist=responseproduct.getJSONObject("response").getJSONObject("orderList").getJSONArray("order");
					JSONArray orderlist=responseproduct.getJSONArray("order_list");
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
						Order o=OrderUtils.getOrderByID(order.getString("order_id"));

						//����״̬��0(��ȡ��)10(Ĭ��):δ����;20:�Ѹ���;30:�ѷ���;40:���ջ�;
						
						 //*1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 //*2��ɾ���ȴ���Ҹ���ʱ���������
						if(o.getPayment_code().equals("offline"))
						{
							o.setPayment_code("2");
							Log.info("���������ID:"+o.getOrder_sn()+" "+"����״̬:"+o.getOrder_state()+" "+"֧��ʱ��:"+Formatter.format(new Date(o.getPayment_time()*1000L),Formatter.DATE_TIME_FORMAT));
						}
						else
						{
							o.setPayment_code("1");
							Log.info("����֧������ID:"+o.getOrder_sn()+" "+"����״̬:"+o.getOrder_state()+" "+"֧��ʱ��:"+Formatter.format(new Date(o.getPayment_time()*1000L),Formatter.DATE_TIME_FORMAT));
						}
						// �������
						String buyernick=responseproduct.getJSONArray("order_list").getJSONObject(j).getJSONObject("extend_order_common").optString("reciver_name");
						// �������
						String order_message=responseproduct.getJSONArray("order_list").getJSONObject(j).getJSONObject("extend_order_common").optString("order_message");
						//��ַ
						String address=responseproduct.getJSONArray("order_list").getJSONObject(j).getJSONObject("extend_order_common").getJSONObject("reciver_info").optString("address");
//						String area=responseproduct.getJSONArray("order_list").getJSONObject(j).getJSONObject("extend_order_common").getJSONObject("reciver_info").optString("street");
						String area="";

						//�绰
						String phone=responseproduct.getJSONArray("order_list").getJSONObject(j).getJSONObject("extend_order_common").getJSONObject("reciver_info").optString("phone");
						String mob_phone=responseproduct.getJSONArray("order_list").getJSONObject(j).getJSONObject("extend_order_common").getJSONObject("reciver_info").optString("mob_phone");
						String sku;
						String sql="";
						if (o.getOrder_state().equals("20"))//�Ѹ���
						{	
							System.out.println("�Ѹ���");
							if (!OrderManager.isCheck("�����˹������", conn, o.getOrder_sn()))
							{	
								if (!OrderManager.TidLastModifyIntfExists("�����˹������", conn, o.getOrder_sn(),new Date(o.getPayment_time()*1000L)))
								{ 
									OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username,address,area,phone,mob_phone,buyernick,order_message); //��ת
									
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getSku();
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrder_state(),o.getOrder_sn(), sku, -item.getNum(),false);
									}
								} 
							}	
						}

						
						//����ͬ����������ʱ��
		                if (new Date(o.getPayment_time()*1000L).compareTo(modified)>0)
		                {
		                	modified=new Date(o.getPayment_time()*1000L);
		                }
					}
					//�ж��Ƿ�����һҳ
					if (pageno==(Double.valueOf(Math.ceil(totalCount/20.0))).intValue()) break;
					pageno++;
				}
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{	
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            		System.out.println("���¶�����ȡʱ��"+value);
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
