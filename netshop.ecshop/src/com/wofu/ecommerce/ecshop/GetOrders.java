package com.wofu.ecommerce.ecshop;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import com.wofu.ecommerce.ecshop.util.CommHelper;
public class GetOrders extends Thread {

	private static String jobname = "��ȡecshop������ҵ";
	
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	private static String lasttimeReturnconfvalue=Params.username+"ȡ�˻���������ʱ��";
	
	private static long daymillis=24*60*60*1000L;
	
	private String lasttime;
	private String lastReturntime;
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	public void run() {

		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;

			try {
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	
				Ecshop.setCurrentDate_getOrder(new Date());
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
				lastReturntime=PublicUtils.getConfig(connection,lasttimeReturnconfvalue,"");
				/**
				 * ����״̬ 10��������20�ѷ�����21���ַ�����30���׳ɹ� ��40���׹ر�
				 */
				//��ȡecshop�¶��� 
				getOrderList(connection) ;
				
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
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
	
	
	//��ȡecshop�¶���
	public void getOrderList(Connection conn) throws Exception
	{
		Log.info("��ȡ������ʼ!");
		int pageIndex = 1 ;
		boolean hasNextPage = true ;	
		
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				int waitSend  = 0;
				while(hasNextPage)
				{
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//������
					String apimethod="search_order_list";
					HashMap<String,Object> reqMap = new HashMap<String,Object>();
			        reqMap.put("last_modify_st_time", startdate.getTime()/1000L);
			        reqMap.put("last_modify_en_time",enddate.getTime()/1000L);
			        reqMap.put("pages", String.valueOf(pageIndex));
			        reqMap.put("counts", Params.pageSize);
			        reqMap.put("return_data", "json");
			        reqMap.put("act", apimethod);
			        reqMap.put("api_version", "1.0");
			        
			        Log.info("��"+pageIndex+"ҳ");
					String responseText = CommHelper.doRequest(reqMap,Params.url);
					Log.info("��������Ϊ:��"+responseText);
					//�ѷ��ص�����ת��json����
					JSONObject responseObj= new JSONObject(responseText.replaceAll(":null",":\"\""));
					  //sn_error
					if(!"success".equals(responseObj.getString("result"))){   //��������
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if("biz.handler.data-get:no-result".equals(operCode)){ //û�н��
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
								return;
							}catch(ParseException e)
							{
								Log.error(jobname, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
							Log.info("û�п��õĶ���!");
						}else{
							Log.warn("ȡ����������,������: "+operCode);
						}
						
						break;
					}
					
					
					
					JSONObject orderInfos = responseObj.getJSONObject("info");

					//��ҳ��
					String orderTotal = String.valueOf(orderInfos.getString("counts"));
					if (orderTotal==null || orderTotal.equals("") || orderTotal.equals("0"))
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
					int orderTotaltemp = Integer.parseInt(orderTotal);
					int pageTotalTemp  = Double.valueOf(Math.ceil(orderTotaltemp/Double.parseDouble(Params.pageSize))).intValue();
					String pageTotal =String.valueOf(pageTotalTemp);
					//����Ԫ��
					JSONArray ordersList = orderInfos.getJSONArray("data_info");
					for(int i = 0 ; i< ordersList.length() ; i++)
					{	//ĳ������
						JSONObject orderInfo = ordersList.getJSONObject(i);
						int returnOrderCount =0;
						//������� 
						String orderCode = (String)orderInfo.get("order_sn");
						//������Ʒ����
						if(orderInfo.isNull("shop_info")) continue;
						JSONArray items = orderInfo.getJSONArray("shop_info");
						//����һ����������
						Order o = new Order();
						o.setObjValue(o, orderInfo);
						o.setFieldValue(o, "shop_info", items);
						Log.info("����״̬:��"+o.getShipping_status()+"����״̬: "+o.getPay_status());
						if(o != null)
						{	
							if("0".equals(o.getShipping_status()) && "2".equals(o.getPay_status())){   //��������
								Log.info("������:��"+o.getOrder_sn());
								//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
									if (!OrderManager.isCheck(jobname, conn, orderCode))
									{
										if (!OrderManager.TidLastModifyIntfExists(jobname, conn, orderCode,new Date(o.getAdd_time()*1000L)))
										{
											try
											{
												OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
												for(Iterator ito=o.getShop_info().getRelationData().iterator();ito.hasNext();)
												{
													OrderItem item=(OrderItem) ito.next();
													String sku = item.getProduct_sn() ;
													StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
													long qty= (long)item.getGoods_number();
													//��ecs_rationconfig���д��ڻ������һ�����ͬ����¼(�������Լ���
													StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, "δ����",o.getOrder_sn(), sku, qty,false);
												}
												waitSend++;
												
											} catch(SQLException sqle)
											{
												throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
											}
											//Log.info("o's add_time: "+new Date(o.getAdd_time()*1000L));
											//Log.info("modified: "+modified);
							                if (new Date(o.getAdd_time()*1000L).compareTo(modified)>0)
							                {
							                	modified=new Date(o.getAdd_time()*1000L);
							                }
										}
									}     // ����״̬ 10��������20�ѷ�����21���ַ�����30���׳ɹ� ��40���׹ر�
							}

								//δ����
								else if("0".equals(o.getPay_status()))
								{
									for(Iterator ito=o.getShop_info().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getProduct_sn() ;
										long qty= (long)item.getGoods_number();
										//
										StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku,qty);
										
									}
									
									
								}else if("4".equals(o.getOrder_status())){  //�˻�����
									
								}
						}
								
							//�����ǰ����ʱ����ڿ�ʼȡ����ʱ�䣬������´�ȡ����ʱ��(����ȡ�����б�����޸�ʱ��)
							//����ͬ����������ʱ��
						
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
	            	}catch(JException je)
	            	{
	            		Log.error(jobname,je.getMessage());
	            	}
				}
				//û��ץȡ����Ч�Ķ���
				if(waitSend==0){
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
						return;
					}catch(ParseException e)
					{
						Log.error(jobname, "�����õ����ڸ�ʽ!"+e.getMessage());
					}
					Log.info("û�п��õ���Ч����!");
				}
				break;
			} catch (Exception e) 
			{
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(jobname+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
		Log.info("����ȡecshop�������������!");
	}

	
	
	
}