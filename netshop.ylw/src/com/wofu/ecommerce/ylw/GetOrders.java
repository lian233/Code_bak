package com.wofu.ecommerce.ylw;
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
import com.wofu.ecommerce.ylw.util.CommHelper;
public class GetOrders extends Thread {

	private static String jobname = "��ȡ������������ҵ";
	
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
				//��ȡ�������¶��� 
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
	
	
	//��ȡ�������¶���
	public void getOrderList(Connection conn) throws Exception
	{
		int pageIndex = 1 ;
		boolean hasNextPage = true ;	
		
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//������
					String apimethod="/orderTradesGet.do";
					HashMap<String,String> reqMap = new HashMap<String,String>();
			        reqMap.put("startTime", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        reqMap.put("endTime",Formatter.format(enddate, Formatter.DATE_TIME_FORMAT) );
			        reqMap.put("pageNo", String.valueOf(pageIndex));
			        reqMap.put("pageSize", Params.pageSize);
			        reqMap.put("appSecret", Params.appsecret);
			        reqMap.put("appMethod", apimethod);
			        reqMap.put("appKey", Params.appKey);
			        reqMap.put("version_no", Params.version_no);
			        //��������
			        
			        Log.info("��"+pageIndex+"ҳ");
					String responseText = CommHelper.doRequest(reqMap,Params.url);
					//Log.info("ȡ������������Ϊ: "+responseText);
					//�ѷ��ص�����ת��json����
					JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
					  //sn_error
					if(responseText.indexOf("sn_error")!=-1){   //��������
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
					
					
					
					JSONObject totalInfo = responseObj.getJSONObject("sn_head");
					if(totalInfo==null){
						String operCode=(String)responseObj.getJSONObject("sn_error").get("error_code");
						Log.error("��ȡ�����������б�", "��ȡ�����б�ʧ�ܣ������룺"+operCode);
						hasNextPage = false ;
						break ;
					}

					//��ҳ��
					String pageTotal = String.valueOf(totalInfo.get("pageTotal"));
					Log.info("��ҳ���� "+pageTotal);
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
					JSONArray ordersList = responseObj.getJSONObject("sn_body").getJSONArray("orderQuery");
					for(int i = 0 ; i< ordersList.length() ; i++)
					{	//ĳ������
						JSONObject orderInfo = ordersList.getJSONObject(i);
						//������� 
						String orderCode = (String)orderInfo.get("orderCode");
						//������Ʒ����
						JSONArray items = orderInfo.getJSONArray("orderDetail");
						//����һ����������
						Order o = new Order();
						o.setObjValue(o, orderInfo);
						o.setFieldValue(o, "orderItemList", items);
						
						String orderLineStatus=o.getOrderLineStatus();
						String returnOrderFlag= o.getReturnOrderFlag();
						//������Ʒ��ĳЩ����
						for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
						{	
							OrderItem item=(OrderItem) ito.next();
							
							//���ö���״̬
							if("".equals(orderLineStatus)){
								o.setOrderLineStatus(String.valueOf(item.getOrderLineStatus()));
							}
							if("".equals(returnOrderFlag)){
								o.setReturnOrderFlag(String.valueOf(item.getReturnOrderFlag()));
							}
								
							
						}
						Date createTime = o.getOrderSaleTime();
						if(o != null)
						{	
							Log.info("״̬�� "+o.getOrderLineStatus()+"��");
							Log.info("������־�� "+o.getReturnOrderFlag()+"��");
							Log.info("�����š�"+ o.getOrderCode() +"��,״̬��"+ OrderUtils.getOrderStateByCode(o.getOrderLineStatus()) +"��,����޸�ʱ�䡾"+ Formatter.format(createTime,Formatter.DATE_TIME_FORMAT) +"��") ;
							if("0".equals(o.getReturnOrderFlag())){   //��������
								//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
								if("10".equals(o.getOrderLineStatus()))
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
													String sku = item.getItemCode() ;
													//û�еȴ������״̬ ����Ҫɾ��δ���������Ŀ��/
													//StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
													long qty= (long)item.getSaleNum();
													//��ecs_rationconfig���д��ڻ������һ�����ͬ����¼(�������Լ���
													StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrderLineStatus(),o.getOrderCode(), sku, qty,false);
												}
												
											} catch(SQLException sqle)
											{
												throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
											}
										}
									}     // ����״̬ 10��������20�ѷ�����21���ַ�����30���׳ɹ� ��40���׹ر�
								}

								//40���׹ر�
								else if("40".equals(o.getOrderLineStatus()))
								{
									Log.info("������: "+o.getOrderCode()+", ���׹رգ�");
									/*for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getItemCode() ;
										long qty= (long)(item.getSaleNum());
										//ɾ����������棬����������Ŀ��
										//StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
										//StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrderLineStatus(),o.getOrderCode(), sku, qty,false);
									}*/
									
									//ȡ������
									String sql="declare @ret int;  execute  @ret = IF_CancelCustomerOrder '" + orderCode + "';select @ret ret;";
									int resultCode = SQLHelper.intSelect(conn, sql) ;
									//ȡ������ʧ��
									if(resultCode == 2)			
										Log.info("����������ȡ������ʧ��,����:"+orderCode+"");						
									else
										Log.info("����������ȡ�������ɹ�,����:"+orderCode+"");
									
								}
								else if ("30".equals(o.getOrderLineStatus()))  //���׳ɹ�
								{
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getItemCode() ;
							
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode, sku);								
									}
					
								}
							}else{  //�����˻�
								Log.info("return1");
								OrderUtils.createRefundOrder(jobname,conn,Params.tradecontactid,o,Params.url,Params.appKey,Params.appsecret,Params.format);
							}
							
							//�����ǰ����ʱ����ڿ�ʼȡ����ʱ�䣬������´�ȡ����ʱ��(����ȡ�����б�����޸�ʱ��)
							//����ͬ����������ʱ��
							
			                if (createTime.compareTo(modified)>0)
			                {
			                	modified=createTime;
			                }
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
		Log.info("����ȡ�������������������!");
	}

	
	
	
}