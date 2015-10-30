package com.wofu.ecommerce.papago8;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import com.wofu.base.job.Executer;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.papago8.util.CommHelper;

/**
 * 
 *���δ�붩��
 *���ȡ������
 *
 */
public class CheckOrderExecuter extends Executer {

	private static String pageSize = "10" ;
	
	private static String jobName="��ʱ���papago8δ�붩��";
	private static long daymillis=24*60*60*1000L;
	private static String vcode = "" ;
	private static String url = "" ;
	private static String tradecontactid = "" ;
	private static String username = "" ;
	
	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		pageSize = prop.getProperty("pageSize") ;
		vcode = prop.getProperty("vcode") ;
		url = prop.getProperty("url") ;
		tradecontactid = prop.getProperty("tradecontactid") ;
		username = prop.getProperty("username") ;

		try 
		{	
			//���δ�붩��
			updateJobFlag(1);
			
			checkWaitStockOutOrders();
			//���ȡ������
			//checkCancleOrders();

			UpdateTimerJob();
			
			Log.info(jobName, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
	
		} catch (Exception e) {
			try {
				
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
				
				updateJobFlag(0);
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				
			} catch (Exception e1) {
				Log.error(jobName,"�ع�����ʧ��");
			}
			Log.error(jobName,"������Ϣ:"+Log.getErrorMessage(e));
			
			
			Log.error(jobName, "ִ����ҵʧ�� [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName,"���´�����־ʧ��");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"�ر����ݿ�����ʧ��");
			}
		}
		
	
	}
	

	/**���δ�����������   orderStatus=10  �ȴ����� 
	 *������һ��ʱ���δ�붩��
	**/
	public  void checkWaitStockOutOrders() throws Exception
	{
		Log.info(jobName+"����ʼ!");
		Connection conn= this.getDao().getConnection();

		int pageIndex = 0 ;  //papago8�Ķ�����0ҳ����
		boolean hasNextPage = true ;	
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					
					Date startdate=new Date(new Date().getTime()-daymillis);
					Date enddate=new Date();
					//������
					String apimethod="/order/list_info?";
					HashMap<String,Object> map = new HashMap<String,Object>();
					map.put("ctime_start", URLEncoder.encode(Formatter.format(startdate, Formatter.DATE_TIME_FORMAT)));
			        map.put("ctime_end",URLEncoder.encode(Formatter.format(enddate, Formatter.DATE_TIME_FORMAT)) );
			        map.put("page", String.valueOf(pageIndex));
			        map.put("page_size", Params.pageSize);
			        map.put("status", 0);
			        map.put("vcode", Params.Key);
			        map.put("apimethod", apimethod);

			        //��������
			        
			        Log.info("��"+pageIndex+"ҳ");
					String responseText = CommHelper.doGet(map,Params.url);
					
					Log.info("9990: "+new String(responseText.getBytes(),"GBK"));
					//�ѷ��ص�����ת��json����
					JSONObject responseObj=new JSONObject(responseText);
					if(responseObj.getInt("code")==1){   //codeΪ1����û�����ݷ���
						Log.info("����ȡ��ȡ����");
						break;
					}
					  //sn_error
					if(responseText.indexOf("error_code")!=-1){   //��������
						String operCode = responseObj.getString("error_code");
						Log.info(jobName, operCode);
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
					String pageTotal = String.valueOf(responseObj.get("total_num"));
					Log.info("��ҳ���� "+pageTotal);
					if (pageTotal==null || pageTotal.equals("") || pageTotal.equals("0"))
					{				
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
						String orderCode = (String)orderInfo.getJSONObject("order").get("order_id");
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
						
						Date createTime = o.getModified();
						if(o != null)
						{	
							Log.info("�����š�"+ o.getTid() +"��,״̬��"+ o.getStatus() +"��") ;
							  //��������
								//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
								if("\u7b49\u5f85\u53d1\u8d27".equals(o.getStatus()))
								{
									if (!OrderManager.isCheck(jobName, conn, orderCode))
									{
										if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderCode,createTime))
										{
											try
											{
												OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
												for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
												{
													OrderItem item=(OrderItem) ito.next();
													String sku = item.getOuter_sku_id() ;
													//û�еȴ������״̬ ����Ҫɾ��δ���������Ŀ��/
													//StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, orderCode,sku);
													long qty= (long)item.getNum();
													//��ecs_rationconfig���д��ڻ�������һ�����ͬ����¼(�������Լ���
													StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getStatus(),o.getTid(), sku, qty,false);
												}
												
											} catch(SQLException sqle)
											{
												throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
											}
										}
									}     // ����״̬ 10��������20�ѷ�����21���ַ�����30���׳ɹ� ��40���׹ر�
								}

								//40���׹ر�
								else if("����ȡ��".equals(o.getStatus()))
								{
									Log.info("������: "+o.getTid()+", ����ȡ����");
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getOuter_sku_id() ;
										long qty= (long)(item.getNum());
										//ɾ����������棬����������Ŀ��
										StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, orderCode,sku);
										StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getStatus(),o.getTid(), sku, qty,false);
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
								else if ("���׳ɹ�".equals(o.getStatus()))  //���׳ɹ�
								{
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getOuter_sku_id() ;
							
										StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, orderCode, sku);								
									}
					
								}else if ("�ȴ�����".equals(o.getStatus()))  //���׳ɹ�
								{
									for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										String sku = item.getOuter_sku_id() ;
										long qty= (long)(item.getNum());
							
										StockManager.addWaitPayStock(jobName, conn,Params.tradecontactid, String.valueOf(orderCode), sku, qty);
										StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getStatus(),String.valueOf(orderCode), sku, -qty,false);								
									}
					
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
					
				Log.info(jobName+"ִ�����!");
				break;
			}catch(Exception e)
			{
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}

	}

	
	//���ȡ������  -100 ȡ��
	/*private  void checkCancleOrders() throws Exception
	{
		//��ȡȡ���������ͷſ��(������ʱ��������һ��)
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		
		Date begintime=new Date(System.currentTimeMillis()-daymillis);
		Date endtime=new Date();
		String lastModifyTimeStart = Formatter.format(begintime, Formatter.DATE_TIME_FORMAT) ;
		String lastModifyTimeEnd = Formatter.format(endtime, Formatter.DATE_TIME_FORMAT) ;
		
		for (int k=0;k<10;)
		{
			try 
			{
				
				while(hasNextPage)
				{

					//������
					String methodName="dangdang.orders.list.get";
					//������֤�� --md5;����
					String sign = CommHelper.getSign(app_Secret, app_key, methodName, session) ;
					Hashtable<String, String> params = new Hashtable<String, String>() ;
					params.put("sign", sign) ;
					params.put("timestamp",URLEncoder.encode(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT),"GBK"));
					params.put("app_key",app_key);
					params.put("method",methodName);
					params.put("format","xml");
					params.put("session",session);
					params.put("sign_method","md5");
					params.put("os", "-100") ;
					params.put("lastModifyTime_end", URLEncoder.encode(lastModifyTimeEnd, encoding)) ;
					params.put("lastModifyTime_start", URLEncoder.encode(lastModifyTimeStart, encoding)) ;
					params.put("p", String.valueOf(pageIndex)) ;
					params.put("pageSize", pageSize) ;
					params.put("sendMode", sendMode) ;
					String reponseText = CommHelper.sendRequest(Params.url,"GET",params,"");
					
					Document doc = DOMHelper.newDocument(reponseText, encoding);
					Element urlset = doc.getDocumentElement();
					
					if(DOMHelper.ElementIsExists(urlset,"Error"))
					{
						Element error = (Element) urlset.getElementsByTagName("Error").item(0);
						String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
						String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
						if(!"".equals(operCode))
						{
							Log.error("��ȡpapago8�����б�", "��ȡ�����б�ʧ�ܣ������룺"+operCode+",���������Ϣ��"+operation);
							hasNextPage = false ;
							break ;
						}
					}
	
					Element totalInfo = (Element) urlset.getElementsByTagName("totalInfo").item(0) ;
					
					String pageTotal = DOMHelper.getSubElementVauleByName(totalInfo, "pageTotal") ;
					
					NodeList ordersList = urlset.getElementsByTagName("OrderInfo") ;
					for(int i = 0 ; i< ordersList.getLength() ; i++)
					{
						Element orderInfo = (Element) ordersList.item(i) ;
						String orderID = DOMHelper.getSubElementVauleByName(orderInfo, "orderID") ;
						
						if (orderID!=null && !orderID.equals(""))
						{	//����һ����������
							Order o = OrderUtils.getOrderByID(Params.url,orderID,session,app_key,app_Secret) ;
							
				
							//ȡ�ö����������е���Ʒ
							ArrayList<OrderItem> itemList = o.getOrderItemList() ;
							for(int j= 0 ; j < itemList.size() ; j ++)
							{
								String sku = itemList.get(j).getOuterItemID() ;
								//ɾ������еĶ�Ӧ��δ������Ʒ
								StockManager.deleteWaitPayStock(jobName, this.getDao().getConnection(),tradecontactid, o.getOrderID(),sku);
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
				}
				
				break;
				
			}catch(Exception e)
			{
				if (++k >= 10)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
		
		
		
	}*/



	
}