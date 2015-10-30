package com.wofu.ecommerce.weidian;

import java.net.URLEncoder;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weidian.utils.Utils;
import com.wofu.ecommerce.weidian.utils.getToken;

public class GetOrders extends Thread
{
	private static String jobName = "��ȡ΢�궩����ҵ";
	private static String lasttimeconfvalue="΢��ȡ��������ʱ��";  //Parmas���Ǵ������ط����ƹ����ģ��Ѿ������޸�
	private static long daymillis=24*60*60*1000L;
	private boolean is_importing=false;
	private String lasttime;
	private static String  orderLineStatus="1";
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	@Override
	public void run() 
	{
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do 
		{
			Connection connection = null;
			is_importing = true;
			try 
			{
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.weidian.Params.dbname); //���ݿ�����ʱ����
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getOrderList(connection);  
			} catch (Exception e)
			{
				e.printStackTrace();
				try 
				{
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) 
				{
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally
			{
				is_importing = false;
				try
				{
					if (connection != null) connection.close();
				} catch (Exception e)
				{
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long current = System.currentTimeMillis();
			while(System.currentTimeMillis()-current<Params.waittime*60*1000){
				try {
					Thread.sleep(100L);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} while (true);
//		super.run();
	}
	
	
	/*****�����̻�ȡ������ϸ��Ϣ*****/
	public void getOrderList(Connection conn) throws Throwable
	{ 
		String token =getToken.getToken_zy(conn);
		JSONObject responseResult=new JSONObject();
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
					
					/**ȡ���б�**/
					JSONObject param_Object = new JSONObject();
					JSONObject public_Object = new JSONObject();
					
					param_Object.put("page_num", pageno);
					param_Object.put("order_type", "");
					param_Object.put("add_start", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));//URLEncoder.encode(Formatter.format(startdate, Formatter.DATE_TIME_FORMAT),"UTF-8")
					param_Object.put("add_end",   Formatter.format(enddate  , Formatter.DATE_TIME_FORMAT));//URLEncoder.encode(Formatter.format(enddate  , Formatter.DATE_TIME_FORMAT),"UTF-8")
					public_Object.put("method", "vdian.order.list.get");
					public_Object.put("access_token", token); //д���������ڻ�ȡaccess_token
					public_Object.put("version", "1.1"); 
					public_Object.put("format", "json"); 
					String opt_to_sever = Params.url + "?param=" + URLEncoder.encode(param_Object.toString(),"UTF-8") + "&public=" + URLEncoder.encode(public_Object.toString(),"UTF-8");
					String responseOrderListData = Utils.sendbyget(opt_to_sever);
					responseResult=new JSONObject(responseOrderListData);    //������Ϣ
					String errdesc="";
					/**������Ϣ*/
					System.out.println("΢�귵�ص���Ϣ"+responseOrderListData);
					//{"status":{"status_code":10013,"status_reason":"access_token����"}}
					try
					{
						if(!responseResult.getJSONObject("status").getString("status_reason").equals("success"))
						{
							errdesc=errdesc+" "+responseResult.getJSONObject("status").getString("status_reason"); 
						}
					} catch (Exception e)
					{
						// TODO: handle exception
					}
					/**������Ϣ*/
					if (responseResult.getJSONObject("result").getInt("total_num")==0)  //�����б���Ϣ�����ڣ���¼����һ����û��
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
				                		Log.error(jobName, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobName, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						Log.error(jobName, /*"ȡ�����б�ʧ��:"*/"û�иö����������ѷ�������û�ж�����Ҫ����:"+errdesc);
					}
					k=10;
					break;
				}
				/**�������д��break*/
				if(!responseResult.getJSONObject("status").getString("status_reason").equals("success"))
				{
					String errdesc="";
					errdesc=errdesc+" "+responseResult.getJSONObject("status").getString("status_reason"); 
					Log.error(jobName, errdesc);
					k=10;
					break;
				}
				/**�������д��break*/
				if(responseResult.getJSONObject("result").getInt("total_num")==0)
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
			                		Log.error(jobName, je.getMessage());
			                	}
							}
						}catch(ParseException e)
						{
							Log.error(jobName, "�����õ����ڸ�ʽ!"+e.getMessage());
						}
					}
					k=10;
					break;
				}
				JSONArray orderlist=responseResult.getJSONObject("result").getJSONArray("orders");
				for(int j=0;j<responseResult.getJSONObject("result").getInt("order_num");j++)
				{
					JSONObject order=orderlist.getJSONObject(j);
					Order o = OrderUtils.getOrderByID(order.getString("order_id"),token); //������ϸ
					//System.out.println("o.getTime():"+o.getTime()+","+o.);
					Log.info(o.getOrder_id()+" ���������"+o.getStatus()+" "+Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT));
					String sku;
					String sql="";
					/*����״̬��unpay δ���� pay ������  unship_refunding δ�����������˿�
					ship �ѷ���
					shiped_refunding �ѷ����������˿�
					accept ��ȷ���ջ�
					accept_refunding��ȷ���ջ��������˿�
					finish �������
					close�����ر�*/
					if(/*o.getStatus().equals("unpay") || */o.getStatus().equals("pay")) 
					{
						if (!OrderManager.isCheck("���΢�궩��", conn, o.getOrder_id()))
						{
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date getUpdateDate_Date_type = null;
							getUpdateDate_Date_type = o.getPay_time();
							
							if (!OrderManager.TidLastModifyIntfExists("���΢�궩��", conn, o.getOrder_id(),getUpdateDate_Date_type))
							{
								OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
								for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									sku=item.getSku_id();
									StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getOrder_id(), sku);
									StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, String.valueOf(o.getStatus()), o.getOrder_id(), sku, 0, false);
								}
							}
							//�ȴ���Ҹ���ʱ��¼�������
						}
					}
					else if(o.getStatus().equals("ship") || o.getStatus().equals("finish") || o.getStatus().equals("unpay"))
					{
						for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
						{
							/**�������Ƿ������е�������֣�������**/
//							System.out.println("�������Ƿ������е��������:");
							OrderItem item=(OrderItem) ito.next();
							sku=item.getSku_id();
							StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getOrder_id(), sku);
							if(StockManager.WaitPayStockExists(jobName, conn, Params.tradecontactid, o.getOrder_id(), sku))//�л�ȡ���ȴ���Ҹ���״̬ʱ�żӿ��
								StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, String.valueOf(o.getStatus()), o.getOrder_id(), sku, 0, false);
							/**���δ����Ķ����Ƿ��������д�����ݿ�**/
//							System.out.println(sku);
						}
					}
					//�����Ժ��û��˿�ɹ��������Զ��ر�
					//�ͷſ��,����Ϊ����	
					else if(o.getStatus().equals("unship_refunding")||o.getStatus().equals("shiped_refunding")
								||o.getStatus().equals("accept_refunding")||o.getStatus().equals("close")) 
					{
						for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
						{
							OrderItem item=(OrderItem) ito.next();
							sku=item.getSku_id();
							StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getOrder_id(), sku);
							if(StockManager.WaitPayStockExists(jobName, conn, Params.tradecontactid, o.getOrder_id(), sku))
								StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, String.valueOf(o.getStatus()), o.getOrder_id(), sku, 0, false);
						}
					}
					else if(o.getStatus().equals("accept"))  //2-��ȷ��
					{
						for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
						{
							OrderItem item=(OrderItem) ito.next();
							sku=item.getSku_id();
							StockManager.deleteWaitPayStock(jobName, conn, Params.tradecontactid, o.getOrder_id(), sku);
						}
					}
					//����ͬ����������ʱ��
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date getUpdateDate_date = null;
					getUpdateDate_date = o.getPay_time();
					
					if(getUpdateDate_date.compareTo(modified)>0)
					{
						modified=getUpdateDate_date;
					}
					//�ж��Ƿ�����һҳ
					//if (pageno==(Double.valueOf(Math.ceil(totalCount/50.0))).intValue()) break;
					pageno++;
				}
				if(modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
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
				
			}catch (Exception e)
			{
				if (++k >= 10)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				e.printStackTrace();
				Thread.sleep(10000L);
			}
		}
	}
	
	@Override
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}
	
}
