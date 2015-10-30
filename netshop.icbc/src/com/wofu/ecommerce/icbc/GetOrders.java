package com.wofu.ecommerce.icbc;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.icbc.util.CommHelper;
public class GetOrders extends Thread {

	private static String jobname = "��ȡ�����̳Ƕ�����ҵ";
	
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
				//��ȡ�����̳��¶��� 
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
	
	
	//��ȡ�����̳��¶���
	public void getOrderList(Connection conn) throws Exception
	{
		
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
					int n=1;
				
					String startdate=Formatter.format(new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L),Formatter.DATE_TIME_FORMAT);
					String enddate=Formatter.format(new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis),Formatter.DATE_TIME_FORMAT);
					//������
					String apimethod="icbcb2c.order.list";
					HashMap<String,Object> map = new HashMap<String,Object>();
					map.put("modify_time_from",startdate);
			        map.put("modify_time_to",enddate);
			        map.put("method", apimethod);
			        map.put("req_sid", CommHelper.getReq_sid());
			        map.put("version", Params.OUT_API_VERSION);
			        map.put("format", Params.OUT_API_FORMAT);
			        map.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
			        map.put("app_key", Params.OUT_APP_KEY);
			        map.put("auth_code", Params.OUT_AUTH_CODE);
			        StringBuilder sb = new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			        sb.append("<body><modify_time_from>").append(startdate)
			        .append("</modify_time_from><modify_time_to>")
			        .append(enddate)
			        .append("</modify_time_to></body>");
			        map.put("sign", CommHelper.getSign("HMACSHA256",Params.OUT_APP_KEY,Params.OUT_AUTH_CODE,Params.OUT_APP_SECRET,sb.toString()));
			        map.put("req_data", sb.toString());
			        
			        
			        //��������
					String responseText = CommHelper.doPost(map,Params.url);
					
					Log.info("��������Ϊ: "+responseText);
					//�ѷ��ص�����ת��Document����
					Document document = DOMHelper.newDocument(responseText, "utf-8");
					Element rootElement = document.getDocumentElement();
					Element head = (Element)rootElement.getElementsByTagName("head").item(0);
					String result = DOMHelper.getSubElementVauleByName(head, "ret_code");
					Log.info("result:��"+result);
					
					
					if(!"0".equals(result)) {
						String errmsg = DOMHelper.getSubElementVauleByName(head, "ret_msg");
						Log.info("errmsg:��"+errmsg);
						Log.error(jobname,errmsg);
						if("�������Ƶ�ʹ���".equals(errmsg)){
							long now = System.currentTimeMillis();
							while(System.currentTimeMillis()-now<(long)Params.waittime*1000L){
								Thread.sleep(1000L);
							}
						}
						return;
					}
					Log.info("test");
					NodeList order_list = rootElement.getElementsByTagName("order_list");
					if(order_list==null){
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
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis*1000L)),Formatter.DATE_FORMAT)+" 00:00:00";
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
					NodeList lists = rootElement.getElementsByTagName("order");
					
					for(int i = 0 ; i< lists.getLength() ; i++)
					{	//ĳ������
						Element orderInfo = (Element)lists.item(i);
						//������� 
						String orderCode = DOMHelper.getSubElementVauleByName(orderInfo, "order_id");
						
						//ȡ��������
						Order o =OrderUtils.getOrderById(orderCode,Params.url,Params.OUT_API_VERSION,
								Params.OUT_APP_KEY,Params.OUT_APP_SECRET,Params.OUT_AUTH_CODE,Params.OUT_API_FORMAT);
						if(!orderCode.equals(o.getOrder_id())) continue;
						Date createTime = Formatter.parseDate(o.getOrder_modify_time(), Formatter.DATE_TIME_FORMAT);
						if(o != null)
						{	
							  //��������
								//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
								if("2".equals(o.getPay_status()) && "0".equals(o.getShipping_status()))
								{
									if (!OrderManager.isCheck(jobname, conn, orderCode))
									{
										if (!OrderManager.TidLastModifyIntfExists(jobname, conn, orderCode,createTime))
										{
											try
											{
												OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
												for(Iterator ito=o.getGoods_list().getRelationData().iterator();ito.hasNext();)
												{
													OrderItem item=(OrderItem) ito.next();
													String sku = item.getGoods_sn() ;
													//û�еȴ������״̬ ����Ҫɾ��δ���������Ŀ��/
													StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
													long qty= Integer.parseInt(item.getGoods_number());
													//��ecs_rationconfig���д��ڻ������һ�����ͬ����¼(�������Լ���
													StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getShipping_status(),o.getOrder_sn(), sku, qty,false);
												}
												
											} catch(SQLException sqle)
											{
												throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
											}
										}
									}     // ����״̬ 10��������20�ѷ�����21���ַ�����30���׳ɹ� ��40���׹ر�
								}

								//40���׹ر�
								else if("2".equals(o.getOrder_status()))
								{
									Log.info("������: "+o.getOrder_sn()+", ����ȡ����");
									for(Iterator ito=o.getGoods_list().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getGoods_sn() ;
										long qty= Integer.parseInt(item.getGoods_number());
										//ɾ����������棬����������Ŀ��
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
									}
									
									//ȡ������
									String sql="declare @ret int;  execute  @ret = IF_CancelCustomerOrder '" + orderCode + "';select @ret ret;";
									int resultCode = SQLHelper.intSelect(conn, sql) ;
									//ȡ������ʧ��
									if(resultCode == 2)			
										Log.info("�����̳�����ȡ������ʧ��,����:"+orderCode+"");						
									else
										Log.info("�����̳�����ȡ�������ɹ�,����:"+orderCode+"");
									
								}
								else if ("1".equals(o.getOrder_status()))  //���׳ɹ�
								{
									for(Iterator ito=o.getGoods_list().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getGoods_sn() ;
							
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode, sku);								
									}
					
								}else if ("0".equals(o.getPay_status()))  //δ����
								{
								 
									for(Iterator ito=o.getGoods_list().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getGoods_sn() ;
										long qty= Integer.parseInt(item.getGoods_number());
							
										StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(orderCode), sku, qty);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, "δ����",String.valueOf(orderCode), sku, -qty,false);								
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
					
					n++;
						
					
					
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
				long now = System.currentTimeMillis();
				while(System.currentTimeMillis()-now<(long)Params.waittime*1000L){
					Thread.sleep(1000L);
				}
			}
		}
		Log.info("����ȡ�����̳Ƕ������������!");
	}

	
	
	
}