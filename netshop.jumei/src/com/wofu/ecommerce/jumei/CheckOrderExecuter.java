package com.wofu.ecommerce.jumei;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import com.wofu.base.job.Executer;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

/**
 * 
 *���δ�붩��
 *���ȡ������
 *
 */
public class CheckOrderExecuter extends Executer {

	private static String jobname="��ʱ������δ�붩��";
	private static final long Hourmillis=20*60*1000L;
	private  String client_id = "" ;
	private  String client_key = "" ;
	private static String url = "" ;
	private String tradecontactid = "" ;
	private static String username = "" ;
	private  String signkey = "" ;
	private  String encoding = "" ;
	
	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		client_id = prop.getProperty("client_id") ;
		client_key = prop.getProperty("client_key") ;
		url = prop.getProperty("url") ;
		tradecontactid = prop.getProperty("tradecontactid") ;
		username = prop.getProperty("username") ;
		signkey = prop.getProperty("signkey") ;
		encoding = prop.getProperty("encoding") ;

		try 
		{	
			//���δ�붩��
			updateJobFlag(1);
			
			checkWaitStockOutOrders();
			//���ȡ������
			//checkCancleOrders();

			UpdateTimerJob();
			
			Log.info(jobname, "ִ����ҵ�ɹ� ["
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
				Log.error(jobname,"�ع�����ʧ��");
			}
			Log.error(jobname,"������Ϣ:"+Log.getErrorMessage(e));
			
			
			Log.error(jobname, "ִ����ҵʧ�� [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobname,"���´����־ʧ��");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobname,"�ر����ݿ�����ʧ��");
			}
		}
		
	
	}
	

	/**���δ�����������   orderStatus=10  �ȴ����� 
	 *������һ��ʱ���δ�붩��
	**/
	public  void checkWaitStockOutOrders() throws Exception
	{
		Log.info(jobname+"����ʼ!");
		Connection conn= this.getDao().getConnection();
		for (int k=0;k<10;)
		{
			try 
			{
					String method="Order/GetOrder";
					Date startdate=new Date(new Date().getTime()-Hourmillis);
					Date enddate=new Date();
					Map<String, String> paramMap = new HashMap<String, String>();
			        //ϵͳ����������
			        paramMap.put("client_id", client_id);
			        paramMap.put("client_key", client_key);
			        paramMap.put("start_date", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        paramMap.put("end_date", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        paramMap.put("status", "2,7");
			       
			        String sign=JuMeiUtils.getSign(paramMap, signkey, encoding);
			        
			        paramMap.put("sign", sign);
			        
			        String responseData=CommHelper.sendRequest(url+method, paramMap, "", encoding);
			        //Log.info("�������ض�������:��"+responseData);
			        
					JSONObject responseresult=new JSONObject(responseData);
					
					int errorCount=responseresult.getInt("error");
					
					if (errorCount>0)
					{
						String errdesc=responseresult.getString("message");
						
						k=10;
						throw new JException(errdesc);
						
					}
					
					JSONArray orderlist=responseresult.getJSONObject("result").getJSONArray("response");
								
					if (orderlist.length()==0)
					{				
						k=10;
						break;
					}
									
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
											
						Order o=new Order();
					
						o.setObjValue(o, order);
					
						JSONObject receiverinfojsobj=order.getJSONObject("receiver_infos");
					
						o.setObjValue(o.getReceiver_info(),receiverinfojsobj);
										
						Log.info(o.getOrder_id()+" "+o.getStatus()+" "+Formatter.format(new Date(o.getTimestamp()*1000),Formatter.DATE_TIME_FORMAT));
						/*
						 *1�����״̬Ϊ�ȴ����ҷ��������ɽӿڶ���
						 *2��ɾ���ȴ���Ҹ���ʱ��������� 
						 */		
						String sku;
						String sql="";
						if (o.getStatus()== 2 ||o.getStatus()== 7)
						{	
							
							if (!OrderManager.isCheck("��������Ʒ����", conn, o.getOrder_id()))
							{
								if (!OrderManager.TidLastModifyIntfExists("��������Ʒ����", conn, o.getOrder_id(),new Date(o.getTimestamp()*1000)))
								{
									OrderUtils.createInterOrder(conn,o,tradecontactid,username);
									
									for(Iterator ito=o.getProduct_infos().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getUpc_code();
										
										StockManager.deleteWaitPayStock(jobname, conn,tradecontactid, o.getOrder_id(),sku);
										StockManager.addSynReduceStore(jobname, conn, tradecontactid, String.valueOf(o.getStatus()),String.valueOf(o.getOrder_id()), sku, -item.getQuantity(),false);
									}
								}
							}
	
							//�ȴ���Ҹ���ʱ��¼�������
						}
						
					}
				k=10;
				break;
				//ִ�гɹ�����ѭ��
			}catch(Exception e)
			{
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(jobname+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
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
					Hashtable<String, String>  = new Hashtable<String, String>() ;
					.put("sign", sign) ;
					.put("timestamp",URLEncoder.encode(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT),"GBK"));
					.put("app_key",app_key);
					.put("method",methodName);
					.put("format","xml");
					.put("session",session);
					.put("sign_method","md5");
					.put("os", "-100") ;
					.put("lastModifyTime_end", URLEncoder.encode(lastModifyTimeEnd, encoding)) ;
					.put("lastModifyTime_start", URLEncoder.encode(lastModifyTimeStart, encoding)) ;
					.put("p", String.valueOf(pageIndex)) ;
					.put("pageSize", pageSize) ;
					.put("sendMode", sendMode) ;
					String reponseText = CommHelper.sendRequest(.url,"GET",,"");
					
					Document doc = DOMHelper.newDocument(reponseText, encoding);
					Element urlset = doc.getDocumentElement();
					
					if(DOMHelper.ElementIsExists(urlset,"Error"))
					{
						Element error = (Element) urlset.getElementsByTagName("Error").item(0);
						String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
						String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
						if(!"".equals(operCode))
						{
							Log.error("��ȡ���������б�", "��ȡ�����б�ʧ�ܣ������룺"+operCode+",���������Ϣ��"+operation);
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
							Order o = OrderUtils.getOrderByID(.url,orderID,session,app_key,app_Secret) ;
							
				
							//ȡ�ö����������е���Ʒ
							ArrayList<OrderItem> itemList = o.getOrderItemList() ;
							for(int j= 0 ; j < itemList.size() ; j ++)
							{
								String sku = itemList.get(j).getOuterItemID() ;
								//ɾ������еĶ�Ӧ��δ������Ʒ
								StockManager.deleteWaitPayStock(jobname, this.getDao().getConnection(),tradecontactid, o.getOrderID(),sku);
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
