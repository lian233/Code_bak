package com.wofu.ecommerce.meilisuo;
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
import com.wofu.ecommerce.meilisuo.util.CommHelper;
public class GetOrders extends Thread {

	private static String jobname = "��ȡ����˵������ҵ";
	
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
				//��ȡ����˵�¶��� 
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
	
	
	//��ȡ����˵�¶���
	public void getOrderList(Connection conn) throws Exception
	{
		int pageIndex = 0 ;  //����˵�Ķ�����0ҳ����
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
					String apimethod="/order/list_info?";
					HashMap<String,Object> map = new HashMap<String,Object>();
					map.put("ctime_start", URLEncoder.encode(Formatter.format(startdate, Formatter.DATE_TIME_FORMAT)));
			        map.put("ctime_end",URLEncoder.encode(Formatter.format(enddate, Formatter.DATE_TIME_FORMAT)) );
			        map.put("page", String.valueOf(pageIndex));
			        map.put("page_size", Params.pageSize);
			        map.put("status", "0");
			        map.put("vcode", Params.vcode);
			        map.put("apimethod", apimethod);

			        //��������
			        
			        Log.info("��"+pageIndex+"ҳ");
					String responseText = CommHelper.doGet(map,Params.url);
					
					Log.info("9990: "+new String(responseText.getBytes(),"GBK"));
					//�ѷ��ص�����ת��json����
					JSONObject responseObj=new JSONObject(responseText);
					if(responseObj.getInt("code")==1){   //codeΪ1����û�����ݷ���
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
					  //sn_error
					if(responseText.indexOf("error_code")!=-1){   //��������
						String operCode = responseObj.getString("error_code");
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
					
					
					
					/*JSONObject totalInfo = responseObj.getJSONObject("sn_head");
					if(totalInfo==null){
						String operCode=(String)responseObj.getJSONObject("sn_error").get("error_code");
						Log.error("��ȡ����˵�����б�", "��ȡ�����б�ʧ�ܣ������룺"+operCode);
						hasNextPage = false ;
						break ;
					}*/

					//��ҳ��
					int orderNum= Integer.parseInt(responseObj.getString("total_num"));
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
					JSONArray ordersList = responseObj.getJSONArray("info");
					for(int i = 0 ; i< ordersList.length() ; i++)
					{	//ĳ������
						JSONObject orderInfo = ordersList.getJSONObject(i);
						JSONObject order = orderInfo.getJSONObject("order");
						JSONObject address = orderInfo.getJSONObject("address");
						//������� 
						String orderCode = String.valueOf(orderInfo.getJSONObject("order").get("order_id"));
						//������Ʒ����
						JSONArray items = orderInfo.getJSONArray("goods");
						//�������񼯺�
						JSONArray servers = orderInfo.getJSONArray("service");
						//����һ����������
						Order o = new Order();
						o.setObjValue(o, order);
						o.setObjValue(o, address);
						o.setFieldValue(o, "orderItemList", items);
						o.setFieldValue(o, "serviceList", servers);
						
						//������Ʒ��ĳЩ����
						for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
						{	
							OrderItem item=(OrderItem) ito.next();
							String color="";
							String size="";
							for(Iterator it=item.getProp().getRelationData().iterator();it.hasNext();){
								Prop prop = (Prop)it.next();
								if(prop.getName().equals("\u989c\u8272")) {
									color = prop.getValue();
									color=new String(prop.getValue().getBytes(),"GBK");
									Log.info(color);
								}
								if(prop.getName().equals("\u5c3a\u7801")) {
									size = prop.getValue();
									size=new String(prop.getValue().getBytes(),"GBK");
									Log.info(size);
								}
							}
							//��Ʒsku
							
							String sku= OrderUtils.getItemCodeByColSizeCustom(conn,item.getGoods_no(),color,size);
							item.setSku(sku);
							
						}
						Date createTime = o.getCtime();
						if(o != null)
						{	
							Log.info("������־�� "+o.getDeliver_status()+"��");
							Log.info("�����š�"+ o.getOrder_id() +"��,״̬��"+ o.getStatus_text() +"��") ;
							  //��������
								//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
								if("\u7b49\u5f85\u53d1\u8d27".equals(o.getStatus_text()))
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
													String sku = item.getSku() ;
													//û�еȴ������״̬ ����Ҫɾ��δ���������Ŀ��/
													//StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
													long qty= (long)item.getAmount();
													//��ecs_rationconfig���д��ڻ������һ�����ͬ����¼(�������Լ���
													StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus_text(),o.getOrder_id(), sku, qty,false);
												}
												
											} catch(SQLException sqle)
											{
												throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
											}
										}
									}     // ����״̬ 10��������20�ѷ�����21���ַ�����30���׳ɹ� ��40���׹ر�
								}

								//40���׹ر�
								else if("����ȡ��".equals(o.getStatus_text()))
								{
									Log.info("������: "+o.getOrder_id()+", ����ȡ����");
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getSku() ;
										long qty= (long)(item.getAmount());
										//ɾ����������棬����������Ŀ��
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus_text(),o.getOrder_id(), sku, qty,false);
									}
									
									//ȡ������
									String sql="declare @ret int;  execute  @ret = IF_CancelCustomerOrder '" + orderCode + "';select @ret ret;";
									int resultCode = SQLHelper.intSelect(conn, sql) ;
									//ȡ������ʧ��
									if(resultCode == 2)			
										Log.info("����˵����ȡ������ʧ��,����:"+orderCode+"");						
									else
										Log.info("����˵����ȡ�������ɹ�,����:"+orderCode+"");
									
								}
								else if ("���׳ɹ�".equals(o.getStatus_text()))  //���׳ɹ�
								{
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getSku() ;
							
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode, sku);								
									}
					
								}else if ("�ȴ�����".equals(o.getStatus_text()))  //���׳ɹ�
								{
								 
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getSku() ;
										long qty= (long)(item.getAmount());
							
										StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(orderCode), sku, qty);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus_text(),String.valueOf(orderCode), sku, -qty,false);								
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
					
					if(pageIndex >= pageTotal-1)
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
		Log.info("����ȡ����˵�������������!");
	}

	
	
	
}