package com.wofu.ecommerce.oauthpaipai;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.ecommerce.oauthpaipai.Params;
import com.wofu.oauthpaipai.api.oauth.PaiPaiOpenApiOauth;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class getOrders extends Thread {

	private static String jobname = "��ȡ���Ķ�����ҵ";
	
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	
	private static long daymillis=24*60*60*1000L;
	
	private boolean is_importing=false;
	
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_importing = true;
			try {
				//�ı侲̬ʱ��
				PaiPai.setCurrentDate_getOrder(new Date());
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.oauthpaipai.Params.dbname);				
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.oauthpaipai.Params.waittime * 1000))		
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
	private static void getOrderList(Connection conn)
			throws Exception
	{
		Log.info("��ȡ������ҵ��ʼ");
		Date begintime=new Date(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime()+1000L);
		Date endtime=new Date(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
	       //����ͬ����������ʱ��
		
		Date lastupdatetime=Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue,""), Formatter.DATE_TIME_FORMAT);
		int pageindex=1;
		int pagetotal=0;
		for (int n=0;n<10;)
		{
				try {		
					while (true)
					{	
						PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(Params.spid, Params.secretkey, Params.token, Long.valueOf(Params.uid));
					
						sdk.setCharset(Params.encoding);
						
						HashMap<String, Object> params = sdk.getParams("/deal/sellerSearchDealList.xhtml");
						// ���URL�������
					
						params.put("sellerUin", ""+Params.uid);
						params.put("timeType", "UPDATE");
						/**
						 * CREATE:��ʾtimeBegin��timeEnd���µ�ʱ�� 
							PAY:��ʾtimeBegin��timeEnd�Ǹ���ʱ��
							UPDATE:��ʾtimeBegin��timeEnd�Ƕ���������ʱ��
						 */
						params.put("timeBegin", Formatter.format(begintime, Formatter.DATE_TIME_FORMAT));
						params.put("timeEnd", Formatter.format(endtime, Formatter.DATE_TIME_FORMAT));
						
						params.put("listItem", "1");		
						params.put("pageIndex", String.valueOf(pageindex));
						params.put("pageSize", "20");
								
							
						String result = sdk.invoke();
						//Log.info("result: "+result);		
						
		
						Document doc = DOMHelper.newDocument(result.toString(), Params.encoding);
						Element urlset = doc.getDocumentElement();
						pagetotal=Integer.valueOf(DOMHelper.getSubElementVauleByName(urlset, "pageTotal"));
			
						if (pagetotal>0)
						{
							NodeList dealinfonodes = ((Element) urlset.getElementsByTagName("dealList").item(0)).getElementsByTagName("dealInfo");
					
							for (int i = 0; i < dealinfonodes.getLength(); i++) {
								try{
									boolean is_cod=false;
									Element dealinfoelement = (Element) dealinfonodes.item(i);
								
									String dealcode=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealCode");					
									String dealstatus=DOMHelper.getSubElementVauleByName(dealinfoelement, "dealState");
									Date lastUpdatetimeTemp=Formatter.parseDate(DOMHelper.getSubElementVauleByName(dealinfoelement, "lastUpdateTime"),Formatter.DATE_TIME_FORMAT);
									
									//��������ÿ�ζ�ȡ��ȫ���������ڴ������ж����������ʱ�䲻����ȡ����������ʱ�䣬����Բ�����
									//if (updatetime.compareTo(getLastDateTime(conn))<=0)
									//	continue;
									
									Vector vtsku=OrderUtils.getSaleGoods(dealinfoelement);
							
									Order o=OrderUtils.getDealDetail(Params.spid,Params.secretkey,Params.token,Params.uid,Params.encoding,dealcode);
									/*
									 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
									 *2��ɾ���ȴ���Ҹ���ʱ��������� 
									 */	
									String sku="";
									long qty=0;
									Hashtable htsku;
								
									Log.info("������:"+dealcode+" ����״̬:"+dealstatus+" ����޸�ʱ��:"+Formatter.format(lastUpdatetimeTemp, Formatter.DATE_TIME_FORMAT));
									
									if(dealstatus.equals("DS_WAIT_SELLER_DELIVERY"))
									{
										//��鶩����ʽ�����Ƿ�����������У��еĻ��˳�
										if (!OrderManager.isCheck("������Ķ���", conn, dealcode))
										{
											if(!OrderManager.TidLastModifyIntfExists("������Ķ���", conn, dealcode,lastUpdatetimeTemp)){
												OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username,is_cod);
												for(int j=0;j<vtsku.size();j++)
												{
													htsku=(Hashtable) vtsku.get(j);
													sku=htsku.get("sku").toString();
													StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, dealcode, sku);												
												}
											}
											
											
										
										}
										
									//�ȴ���Ҹ���ʱ��¼�������
									} else if (dealstatus.equals("DS_WAIT_BUYER_PAY"))
									{
										for(int j=0;j<vtsku.size();j++)
										{
											htsku=(Hashtable) vtsku.get(j);
											sku=htsku.get("sku").toString();
											qty=Long.valueOf(htsku.get("qty").toString());
											StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, dealcode, sku, qty);
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, dealstatus,dealcode, sku, -qty,false);
																				
										}
										//�����Ժ��û��˿�ɹ��������Զ��ر�
										//�ͷſ��,����Ϊ����
									} else if (dealstatus.equals("DS_REFUND_WAIT_BUYER_DELIVERY")
											||dealstatus.equals("DS_REFUND_WAIT_SELLER_RECEIVE")
											||dealstatus.equals("DS_REFUND_WAIT_SELLER_AGREE")
											||dealstatus.equals("DS_REFUND_OK")||dealstatus.equals("DS_REFUND_ALL_OK"))
									{
										for(int j=0;j<vtsku.size();j++)
										{
											htsku=(Hashtable) vtsku.get(j);
											sku=htsku.get("sku").toString();
											qty=Long.valueOf(htsku.get("qty").toString());				
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, dealstatus,dealcode, sku, qty,true);
										}
										//������ǰ�����һ���������رս���
										//�ͷŵȴ���Ҹ���ʱ�����Ŀ��
									}else if (dealstatus.equals("DS_DEAL_CANCELLED")||dealstatus.equals("DS_CLOSED"))
									{
										for(int j=0;j<vtsku.size();j++)
										{
											htsku=(Hashtable) vtsku.get(j);
											sku=htsku.get("sku").toString();
											qty=Long.valueOf(htsku.get("qty").toString());	
											StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, dealcode, sku);	
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, dealstatus,dealcode, sku, qty,false);
										}
									}else if (dealstatus.equals("DS_DEAL_END_NORMAL"))
									{
										for(int j=0;j<vtsku.size();j++)
										{
											htsku=(Hashtable) vtsku.get(j);
											sku=htsku.get("sku").toString();
											StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, dealcode, sku);										
										}
									}else if (dealstatus.equals("STATE_COD_WAIT_SHIP")){  //��������ȴ�����
										is_cod=true;
										if (!OrderManager.isCheck("������Ķ���", conn, dealcode))
										{
											if (!OrderManager.TidLastModifyIntfExists("������Ķ���", conn, dealcode,lastUpdatetimeTemp))
											{
												OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username,is_cod);
												if(!OrderManager.TidIntfExists("��������Ƿ��ڽӿڱ����Ѿ��м�¼", conn, dealcode)){  //ÿ��д��ӿڲ����ͬ����¼
													for(int j=0;j<vtsku.size();j++)
													{
														htsku=(Hashtable) vtsku.get(j);
														sku=htsku.get("sku").toString();
														StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, dealstatus,dealcode, sku, -qty,false);												
													}
												}
											}
											
										
										}
									}else if (dealstatus.equals("STATE_COD_CANCEL")){  //��������ȡ��(�ر�OR ��ǩ��ر�)  ɾ��ͬ������¼
										
													for(int j=0;j<vtsku.size();j++)
													{
														htsku=(Hashtable) vtsku.get(j);
														sku=htsku.get("sku").toString();
														StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, dealstatus,dealcode, sku, qty,false);												
													}
									}
											
										
									
								
								
					                if (lastUpdatetimeTemp.compareTo(lastupdatetime)>0)
					                {		                	
					                	PublicUtils.setConfig(conn, lasttimeconfvalue, Formatter.format(lastUpdatetimeTemp,Formatter.DATE_TIME_FORMAT));         
					                }
								}catch(Exception ex){
									if(conn!=null && !conn.getAutoCommit())
										conn.rollback();
									Log.error(jobname, ex.getMessage());
									continue;
								}
													
							}
						}else
						{
							try
							{
								//��ö�ʱ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{							            	
								
				                	PublicUtils.setConfig(conn,lasttimeconfvalue,Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00");
								}
							}catch(ParseException e)
							{
								throw new JException("�����õ����ڸ�ʽ!"+e.getMessage());
							}
							
							break;					
						}
						
						pageindex=pageindex+1;
						
						
						if(pageindex>pagetotal)
							break;
						
					}
					
					break;	
						
					
				}catch(Exception e)
				{
					if (++n >= 100)
						throw e;
					if(conn!=null && !conn.getAutoCommit())
						conn.rollback();
					Log.warn(jobname+",Զ������ʧ��[" + n + "], 10����Զ�����. "+ Log.getErrorMessage(e));
					
					Thread.sleep(10000L);
				}	
		}
		Log.info("��ȡ������ҵ����");
		
	}
	
	
}
