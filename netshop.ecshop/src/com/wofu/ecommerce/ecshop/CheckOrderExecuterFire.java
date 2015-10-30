package com.wofu.ecommerce.ecshop;
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
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ecshop.util.CommHelper;
/**
 * 
 *���δ�붩��
 *���ȡ������
 *
 */
public class CheckOrderExecuterFire extends Executer {

	private static String pageSize = "10" ;
	private static String jobName="��ʱ���ecshopδ�붩��";
	private static final long daymillis=2*24*60*60*1000L;
	private static String url = "" ;
	private static String tradecontactid = "" ;
	private static String username = "" ;
	
	
	@Override
	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		pageSize = prop.getProperty("pageSize") ;
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
		Connection conn= this.getExtdao().getConnection();
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		
		for (int k=0;k<5;)
		{
			try 
			{
				
				while(hasNextPage)
				{
					Date startdate=new Date(new Date().getTime()-daymillis);
					Date enddate=new Date();
					//������
					String apimethod="search_order_list";
					HashMap<String,Object> reqMap = new HashMap<String,Object>();
			        reqMap.put("last_modify_st_time", startdate.getTime()/1000L);
			        reqMap.put("last_modify_en_time",enddate.getTime()/1000L);
			        reqMap.put("pages", String.valueOf(pageIndex));
			        reqMap.put("counts", pageSize);
			        reqMap.put("return_data", "json");
			        reqMap.put("act", apimethod);
			        reqMap.put("api_version", "1.0");
			        //��������
			        
			        Log.info("��"+pageIndex+"ҳ");
					String responseText = CommHelper.doRequest(reqMap,url);
					Log.info("��������Ϊ:��"+responseText);
					//�ѷ��ص�����ת��json����
					JSONObject responseObj= new JSONObject(responseText.replaceAll(":null",":\"\""));
					//������� 
					if(!"success".equals(responseObj.getString("result"))){   //��������
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if(operCode.indexOf("no-result")!=-1) {  //û������ֱ���˳�������
							Log.error("��ȡecshop�����б�", "��ȡ�����б�ʧ�ܣ������룺"+operCode);
							return;
						}
						hasNextPage = false ;
						break ;
						
					}

					JSONObject orderInfos = responseObj.getJSONObject("info");
					String orderTotal = String.valueOf(orderInfos.getString("counts"));
					int orderTotaltemp = Integer.parseInt(orderTotal);
					int pageTotalTemp  = Double.valueOf(Math.ceil(orderTotaltemp/Double.parseDouble(Params.pageSize))).intValue();
					String pageTotal =String.valueOf(pageTotalTemp);
					Log.info("�ܶ�����Ϊ�� "+orderTotal);
					Log.info("��ҳ��Ϊ�� "+pageTotal);
					if (orderTotal==null || orderTotal.equals("") || orderTotal.equals("0"))
					{				
						break;
					}
					//����Ԫ��
					if(!orderInfos.isNull("data_info")){
						JSONArray ordersList = orderInfos.getJSONArray("data_info");
						for(int i = 0 ; i< ordersList.length() ; i++)
						{	//ĳ������
							JSONObject orderInfo = ordersList.getJSONObject(i);
							//������� 
							String orderCode = (String)orderInfo.get("order_sn");
							//������Ʒ����
							if(orderInfo.isNull("shop_info")) continue;
							JSONArray items = orderInfo.getJSONArray("shop_info");
							//����һ����������
							Order o = new Order();
							o.setObjValue(o, orderInfo);
							o.setFieldValue(o, "shop_info", items);
							Log.info("������: "+o.getOrder_sn()+", ����״̬:��"+o.getShipping_status()+"����״̬: "+o.getPay_status());
							if(o != null)
							{
								if("0".equals(o.getShipping_status()) && "2".equals(o.getPay_status())){   //��������
									Log.info("��鵽һ��������"+orderCode);
									//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
										if (!OrderManager.isCheck(jobName, conn, orderCode))
										{
											if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderCode,new Date(o.getAdd_time()*1000L)))
											{
												try
												{
													OrderUtils.createInterOrder(conn, o, tradecontactid,username);
													for(Iterator ito=o.getShop_info().getRelationData().iterator();ito.hasNext();)
													{
														OrderItem item=(OrderItem) ito.next();
														String sku = item.getProduct_sn() ;
														StockManager.deleteWaitPayStock(jobName, conn,tradecontactid, orderCode,sku);
														long qty= (long)item.getGoods_number();
														//��ecs_rationconfig���д��ڻ������һ�����ͬ����¼(�������Լ���
														StockManager.addSynReduceStore(jobName, conn, tradecontactid, "δ����",o.getOrder_sn(), sku, qty,false);
													}
													
												} catch(SQLException sqle)
												{
													throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
												}
											}
										}     // ����״̬ 10��������20�ѷ�����21���ַ�����30���׳ɹ� ��40���׹ر�
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
					
					//break;
					
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

	
	//���ȡ������  4 ȡ��
	private  void checkCancleOrders() throws Exception
	{
		int pageIndex = 1 ;
		boolean hasNextPage = true ;	
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					Date enddate=new Date();
					Date startdate=new Date(new Date().getTime()-daymillis);
					//������
					String apimethod="search_order_list";
					HashMap<String,Object> reqMap = new HashMap<String,Object>();
			        reqMap.put("last_modify_st_time", startdate.getTime()/1000L);
			        reqMap.put("last_modify_en_time",enddate.getTime()/1000L);
			        reqMap.put("pages", String.valueOf(pageIndex));
			        reqMap.put("counts", pageSize);
			        reqMap.put("return_data", "json");
			        reqMap.put("act", apimethod);
			        reqMap.put("api_version", "1.0");
			        
			        Log.info("��"+pageIndex+"ҳ");
					String responseText = CommHelper.doRequest(reqMap,url);
					Log.info("��������Ϊ:��"+responseText);
					//�ѷ��ص�����ת��json����
					JSONObject responseObj= new JSONObject(responseText);
					  //sn_error
					if(!"success".equals(responseObj.getString("result"))){   //��������
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if("biz.handler.data-get:no-result".equals(operCode)){ //û�н��
							Log.info("û�п��õĶ���!");
						}else{
							Log.warn("ȡ����������,������: "+operCode);
						}
						
						break;
					}
					
					JSONObject orderInfos = responseObj.getJSONObject("info");
					//��ҳ��
					String orderTotal = String.valueOf(orderInfos.getString("counts"));
					int orderTotaltemp = Integer.parseInt(orderTotal);
					int pageTotalTemp  = orderTotaltemp<Integer.parseInt(pageSize)?1:orderTotaltemp/Integer.parseInt(pageSize)==0?Integer.parseInt(pageSize):orderTotaltemp/Integer.parseInt(pageSize)+1;
					String pageTotal =String.valueOf(pageTotalTemp);
					Log.info("�ܶ�����Ϊ�� "+orderTotal);
					Log.info("��ҳ��Ϊ�� "+pageTotal);
					if (orderTotal==null || orderTotal.equals("") || orderTotal.equals("0"))
					{				
						break;
					}
					//����Ԫ��
					if(orderInfos.isNull("data_info")) break;;
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
							if("4".equals(o.getPay_status())){   //��������
								Log.info("������:��"+o.getOrder_sn());
								//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
									
										try
											{
												OrderUtils.createInterOrder(this.getExtdao().getConnection(), o, tradecontactid, username);
												
											} catch(SQLException sqle)
											{
												throw new JException("�����˻���������!" + sqle.getMessage());
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
					
					n++;
						
				}
				break;
			} catch (Exception e) 
			{
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				if(this.getExtdao()!=null && !this.getExtdao().getConnection().getAutoCommit()){
					this.getDao().rollback();
				}
				Log.warn(jobName+" ,Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
		Log.info("����ȡecshop�˻��������������!");
		
		
	}



	
}
