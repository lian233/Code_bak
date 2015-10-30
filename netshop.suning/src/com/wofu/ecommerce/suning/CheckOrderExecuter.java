package com.wofu.ecommerce.suning;
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
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.suning.util.CommHelper;

/**
 * 
 *���δ�붩��
 *���ȡ������
 *
 */
public class CheckOrderExecuter extends Executer {

	private static String pageSize = "10" ;
	
	private static String jobName="��ʱ�������δ�붩��";
	private static long daymillis=24*60*60*1000L;
	private static String appKey = "" ;
	private static String appsecret = "" ;
	private static String format = "" ;
	private static String url = "" ;
	private static String tradecontactid = "" ;
	private static String username = "" ;
	
	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		pageSize = prop.getProperty("pageSize") ;
		appKey = prop.getProperty("appkey") ;
		appsecret = prop.getProperty("appsecret") ;
		format = prop.getProperty("format") ;
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
				Log.error(jobName,"���´����־ʧ��");
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
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		
		for (int k=0;k<5;)
		{
			try 
			{
				while(hasNextPage)
				{
			        // �����api������
			        String apimethod ="suning.custom.order.query";
			        HashMap<String,String> reqMap = new HashMap<String,String>();
			        reqMap.put("orderStatus", "10");
			        reqMap.put("startTime", Formatter.format(new Date(System.currentTimeMillis()-daymillis), Formatter.DATE_TIME_FORMAT));
			        reqMap.put("endTime",Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT) );
			        reqMap.put("pageNo", String.valueOf(pageIndex));
			        reqMap.put("pageSize", pageSize);
			        String ReqParams = CommHelper.getJsonStr(reqMap, "orderQuery");
			        HashMap<String,Object> map = new HashMap<String,Object>();
			        map.put("appSecret", appsecret);
			        map.put("appMethod", apimethod);
			        map.put("format", format);
			        map.put("versionNo", "v1.2");
			        map.put("appRequestTime", CommHelper.getNowTime());
			        map.put("appKey", appKey);
			        map.put("resparams", ReqParams);
			        //��������
					String responseText = CommHelper.doRequest(map,url);
					//Log.info(jobName+" �������� ��"+responseText);
					//�ѷ��ص�����ת��json����
					JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
					//������� 
					if(!responseObj.isNull("sn_error")){   //��������
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if(operCode.indexOf("no-result")!=-1) {  //û������ֱ���˳�������
							Log.error("��ȡ���������б�", "��ȡ�����б�ʧ�ܣ������룺"+operCode);
							return;
						}
						hasNextPage = false ;
						break ;
						
					}
					//ͳ����Ϣ
					JSONObject totalInfo = responseObj.getJSONObject("sn_head");
					//��ҳ��
					String pageTotal = String.valueOf(totalInfo.get("pageTotal"));
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
							//��Ʒsku
							String itemCode= OrderUtils.getItemCodeByProduceCode(item.getProductCode(),appKey,appsecret,format)[0];
							item.setItemCode(itemCode);
							//��ƷͼƬ����
							String itemImg= OrderUtils.getItemCodeByProduceCode(item.getProductCode(),appKey,appsecret,format)[1];
							item.setPicPath(itemImg);
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
							Log.info("�����š�"+ o.getOrderCode() +"��,״̬��"+ OrderUtils.getOrderStateByCode(o.getOrderLineStatus()) +"��,����޸�ʱ�䡾"+ Formatter.format(createTime,Formatter.DATE_TIME_FORMAT) +"��") ;
							if("0".equals(o.getReturnOrderFlag())){   //��������
								//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
								if("10".equals(o.getOrderLineStatus()))
								{
									if (!OrderManager.isCheck(jobName, conn, orderCode))
									{
										if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderCode,createTime))
										{
											try
											{
												OrderUtils.createInterOrder(conn, o, tradecontactid, username);
												for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
												{
													OrderItem item=(OrderItem) ito.next();
													String sku = item.getItemCode() ;
													StockManager.deleteWaitPayStock(jobName, conn,tradecontactid, orderCode,sku);
												}
												
											} catch(SQLException sqle)
											{
												throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
											}
										}
									}    
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
