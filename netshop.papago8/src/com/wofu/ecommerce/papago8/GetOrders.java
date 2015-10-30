package com.wofu.ecommerce.papago8;
import java.net.URLEncoder;
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
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.papago8.util.CommHelper;
public class GetOrders extends Thread {

	private static String jobname = "��ȡpapago8������ҵ";
	
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	
	private static long daymillis=24*60*60*1000L;
	
	private String lasttime;
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	public void run() {

		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;

			try {
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	

				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
				/**
				 * ����״̬ 10��������20�ѷ�����21���ַ�����30���׳ɹ� ��40���׹ر�
				 */
				//��ȡpapago8�¶��� 
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
	
	
	//��ȡpapago8�¶���
	public void getOrderList(Connection conn) throws Exception
	{
		int pageIndex = 1 ;  //papago8�Ķ�����0ҳ����
		boolean hasNextPage = true ;	
		
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				while(hasNextPage)
				{
					
					String startdate=Formatter.format(new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L),Formatter.DATE_TIME_FORMAT);
					String enddate=Formatter.format(new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis),Formatter.DATE_TIME_FORMAT);
					//������
					String apimethod="QueryOrder.aspx?";
					HashMap<String,Object> map = new HashMap<String,Object>();
					map.put("Start_modified",URLEncoder.encode(startdate,"utf-8"));
			        map.put("End_modified",URLEncoder.encode(enddate,"utf-8"));
			        map.put("page", String.valueOf(pageIndex));
			        map.put("Pagesize", Params.pageSize);
			        map.put("key", Params.Key);
			        map.put("apimethod", apimethod);
			        map.put("format", "json");
			        //Log.info("url: "+Params.url);

			        //��������
					String responseText = CommHelper.doGet(map,Params.url);
					responseText = responseText.replaceAll("null", "\"\"");
					
					Log.info("9990: "+new String(responseText.getBytes()));
					//�ѷ��ص�����ת��json����
					JSONObject responseObj=new JSONObject(responseText).getJSONObject("trades_response");
					if("0".equals(responseObj.getString("total_results"))){   //codeΪ1����û�����ݷ���
						Log.info("����ȡ��ȡ����");
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
				
					/*JSONObject totalInfo = responseObj.getJSONObject("sn_head");
					if(totalInfo==null){
						String operCode=(String)responseObj.getJSONObject("sn_error").get("error_code");
						Log.error("��ȡpapago8�����б�", "��ȡ�����б�ʧ�ܣ������룺"+operCode);
						hasNextPage = false ;
						break ;
					}*/

					//��ҳ��
					int orderNum= Integer.parseInt(responseObj.getString("total_results"));
					int  pageTotal =orderNum>=Integer.parseInt(Params.pageSize)?(orderNum %Integer.parseInt(Params.pageSize)==0?orderNum /Integer.parseInt(Params.pageSize):(orderNum /Integer.parseInt(Params.pageSize)+1)):1;
					Log.info("��ҳ���� "+pageTotal);
					if (pageTotal==0)
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
					//����Ԫ��
					JSONArray ordersList = responseObj.getJSONObject("trades").getJSONArray("trade");
					for(int i = 0 ; i< ordersList.length() ; i++)
					{	//ĳ������
						JSONObject orderInfo = ordersList.getJSONObject(i);
						
						//������� 
						//������Ʒ����
						JSONArray items = orderInfo.getJSONObject("orders").getJSONArray("order");
						
						//����һ����������
						Order o = new Order();
						o.setObjValue(o, orderInfo);
						o.setFieldValue(o, "orderItemList", items);
						
						Date createTime = o.getModified();
						String orderCode = o.getTid();
						if(o != null)
						{	
							Log.info("�����š�"+ o.getTid() +"��,״̬��"+ o.getStatus() +"��") ;
							  //��������
								//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
								if("3".equals(o.getStatus()))
								{
									if (!OrderManager.isCheck(jobname, conn, orderCode))
									{
										if (!OrderManager.TidLastModifyIntfExists(jobname, conn, orderCode,createTime))
										{
											try
											{
												OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
												for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
												{
													OrderItem item=(OrderItem) ito.next();
													String sku = item.getOuter_sku_id() ;
													//û�еȴ������״̬ ����Ҫɾ��δ���������Ŀ��/
													StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
													long qty= (long)item.getNum();
													//��ecs_rationconfig���д��ڻ������һ�����ͬ����¼(�������Լ���
													StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getTid(), sku, qty,false);
												}
												
											} catch(SQLException sqle)
											{
												throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
											}
										}
									}     // ����״̬ 10��������20�ѷ�����21���ַ�����30���׳ɹ� ��40���׹ر�
								}

								//40���׹ر�
								else if("8".equals(o.getStatus()))
								{
									Log.info("������: "+o.getTid()+", ����ȡ����");
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getOuter_sku_id() ;
										long qty= (long)(item.getNum());
										//ɾ����������棬����������Ŀ��
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),o.getTid(), sku, qty,false);
									}
									
									//ȡ������
									String sql="declare @ret int;  execute  @ret = IF_CancelCustomerOrder '" + orderCode + "';select @ret ret;";
									int resultCode = SQLHelper.intSelect(conn, sql) ;
									//ȡ������ʧ��
									if(resultCode == 2)			
										Log.info("papago8����ȡ������ʧ��,����:"+orderCode+"");						
									else
										Log.info("papago8����ȡ�������ɹ�,����:"+orderCode+"");
									
								}
								else if ("9".equals(o.getStatus()))  //���׳ɹ�
								{
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getOuter_sku_id() ;
							
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode, sku);								
									}
					
								}else if ("1".equals(o.getStatus()))  //���׳ɹ�
								{
								 
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getOuter_sku_id() ;
										long qty= (long)(item.getNum());
							
										StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(orderCode), sku, qty);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),String.valueOf(orderCode), sku, -qty,false);								
									}
					
								}
							}
							
							//�����ǰ����ʱ����ڿ�ʼȡ����ʱ�䣬������´�ȡ����ʱ��(����ȡ�����б�����޸�ʱ��)
							//����ͬ����������ʱ��
							
			                if (createTime.compareTo(modified)>0)
			                {
			                	modified=createTime;
			                }
						}
					//�ж��Ƿ�����һҳ
					
					if(pageIndex >= pageTotal)
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
		Log.info("����ȡpapago8�������������!");
	}

	
	
	
}