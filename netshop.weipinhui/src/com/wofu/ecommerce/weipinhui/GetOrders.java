package com.wofu.ecommerce.weipinhui;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weipinhui.util.CommHelper;
public class GetOrders extends Thread {

	private static String jobname = "��ȡΨƷ�ᶩ����ҵ";
	
	private static String lasttimeconfvalue=Params.username+"ȡ��������ʱ��";
	private static String lasttimerefundvalue=Params.username+"ȡ�˻���������ʱ��";
	
	private static long daymillis=24*60*60*1000L;
	
	private String lasttime;
	private String lastRefundTime;
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Date nowtime = new Date();
			if(Params.startTime.getTime() <= nowtime.getTime())
			{//���ϻ򳬹�ָ��������ʱ��
				Connection connection = null;
				try {
					connection = PoolHelper.getInstance().getConnection(Params.dbname);	
					WeipinHui.setCurrentDate_getOrder(new Date());
					lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
					lastRefundTime=PublicUtils.getConfig(connection,lasttimerefundvalue,"");
					/**
					 * ����״̬ 10��������22�ѷ�����21���ַ�����60���׳ɹ� ��97���׹ر� ��0δ֧������ ��70�û��Ѿ��� ��54�˻������
					 */                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
					//��ȡΨƷ���¶��� 
					getOrderList(connection) ;
					//��ȡΨƷ���˻�����
					getRefundOrderList(connection) ;
					
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
				Log.info(jobname + "�´�ִ�еȴ�ʱ��:" + Params.waittime + "��");
				long startwaittime = System.currentTimeMillis();
				while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))
				{
					try {
						sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
						Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
					}
				}
				//����һ�����ò���(�����ݿ��ж�ȡ)
				Params.UpdateSettingFromDB(null);
			}
			else
			{//�ȴ�����
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
			}
		} while (true);
	}
	
	
	//��ȡΨƷ���¶���
	public void getOrderList(Connection conn) throws Exception
	{
		Log.info("��ȡΨƷ�ᶩ����ʼ:");
		int pageIndex = 1 ;  //ΨƷ��Ķ�����1ҳ����
		boolean hasNextPage = true ;	
		
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					//�µ�ʱ�䷶Χ(ֻȡ����Ķ���)
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//��ȡ�����б�
					JSONObject jsonobj = new JSONObject();
					try {
						//����
						jsonobj.put("st_add_time", "2015-10-1");
						jsonobj.put("et_add_time", "2015-10-31");
						jsonobj.put("order_id", "15101022592413");
						
//						jsonobj.put("st_add_time", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
//						jsonobj.put("et_add_time", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
						jsonobj.put("vendor_id", Params.vendor_id);
						jsonobj.put("page", pageIndex);
						jsonobj.put("limit", Integer.parseInt(Params.pageSize));
					} catch (JSONException e) {
						Log.warn("׼����������ʱ����!");
						continue;
					}
					//���������ΨƷ��
					String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getOrderList", jsonobj.toString());
					if(responseText.equals("")) break;
					//�ѷ��ص�����ת��json����
					JSONObject responseObj=new JSONObject(responseText);
					//��������
					if(!responseObj.getString("returnCode").equals("0")){
						String ErrStrCode = responseObj.getString("returnCode");
						String ErrMsg = responseObj.getString("returnMessage");
						Log.warn("ȡ����������,������: "+ErrStrCode+"������Ϣ: "+ErrMsg);
						sleep(10000L);
						continue;
					}
					//ҳ��
					int orderNum= responseObj.getJSONObject("result").getInt("total");
					int pageTotal=0;
					if(orderNum!=0){
						pageTotal = orderNum>=Integer.parseInt(Params.pageSize) ? (orderNum %Integer.parseInt(Params.pageSize)==0?orderNum /Integer.parseInt(Params.pageSize):(orderNum /Integer.parseInt(Params.pageSize)+1)) : 1;
					}
					Log.info("��ǰҳ:" + pageIndex + ",��ҳ���� " + pageTotal);
					//��ǰû����
					if (pageTotal==0)
					{				
						if (n==1)
						{
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).compareTo(
									this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);
										Log.info("һ��֮�ڶ�ȡ��������,���ҵ�ǰ�����������! �ѽ�ȡ��������ʱ�����Ϊ��ǰ������");
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
					//��ȡ��ǰҳ�Ķ����б�
					JSONArray ordersList = responseObj.getJSONObject("result").getJSONArray("dvd_order_list");
					int tmpcounter = 0;
					for(int i = 0 ; i< ordersList.length() ; i++)
					{
						//��ȡ��ǰ����
						JSONObject orderJson = ordersList.getJSONObject(i);
						Order o = new Order();
						o.setObjValue(o,orderJson);
						//������� 
						String order_sn = orderJson.getString("order_id");
						//�µ�ʱ��
						Date addTime = Formatter.parseDate(orderJson.getString("add_time"),Formatter.DATE_TIME_FORMAT);
						//��ȡ��ǰ��������
						JSONArray itemArrayTemp = OrderUtils.getOrderItem(order_sn);
						if(itemArrayTemp == null)	//��ȡ��������ʧ��
						{
							Log.warn("��ȡ������Ϣ����ʧ��,���Դ���");
							continue;
						}
						o.setFieldValue(o, "orderItemList", itemArrayTemp);
						if(o != null)
						{
							Log.info("���ڴ�����:" + order_sn + "   ����״̬:" + OrderUtils.getOrderStateByCode(o.getOrder_status()));
							//��������
							//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
							if("10".equals(o.getOrder_status()))
							{
								String nschaverecode = SQLHelper.strSelect(conn, "select count(*) from ns_customerorder where tid = '" + order_sn + "' and TradeContactID = '" + Params.tradecontactid + "'");
								System.out.println("ns_customerorder have recode:" + nschaverecode);
								if (nschaverecode.equals("0") && !OrderManager.isCheck(jobname, conn, order_sn) && !OrderManager.TidLastModifyIntfExists(jobname, conn, order_sn,addTime))
								{
									Log.info("�������ɽӿڶ���");
									try
									{
										//���ɽӿڶ���
										OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
										
										for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
										{
											OrderItem item=(OrderItem) ito.next();
											String sku = item.getBarcode();
											//û�еȴ������״̬ ����Ҫɾ��δ���������Ŀ��/
											StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, order_sn,sku);
											long qty= (long)item.getAmount();
											//��ecs_rationconfig���д��ڻ������һ�����ͬ����¼(�������Լ���
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrder_status(),o.getOrder_id(), sku, qty,false);
										}
									} catch(SQLException sqle)
									{
										throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
									}
								}
								else
									Log.info("����:" +order_sn+ "�Ѿ����������ݿ���");
							}
							//���׹ر�
							else if("97".equals(o.getOrder_status()))
							{
								Log.info("������: "+o.getOrder_id()+", ����ȡ����");
								for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									String sku = item.getBarcode();
									long qty= (long)(item.getAmount());
									//ɾ����������棬����������Ŀ��
									StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, order_sn,sku);
									StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrder_status(),o.getOrder_id(), sku, qty,false);
								}
								
								//ȡ������
								String sql="declare @ret int;  execute  @ret = IF_CancelCustomerOrder '" + order_sn + "';select @ret ret;";
								int resultCode = SQLHelper.intSelect(conn, sql) ;
								//ȡ������ʧ��
								if(resultCode == 2)			
									Log.info("ΨƷ������ȡ������ʧ��,����:"+order_sn+"");						
								else
									Log.info("ΨƷ������ȡ�������ɹ�,����:"+order_sn+"");
								
							}
							//���׳ɹ�
							else if ("60".equals(o.getOrder_status()))  
							{
								for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									String sku = item.getBarcode();
									StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, order_sn, sku);								
								}
								Log.info("������: "+o.getOrder_id()+", ���׳ɹ���");
							}
							//δ֧������
							else if ("0".equals(o.getOrder_status()))
							{
								for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
								{
									OrderItem item=(OrderItem) ito.next();
									String sku = item.getBarcode();
									long qty= (long)(item.getAmount());
									StockManager.addWaitPayStock(jobname, conn,Params.tradecontactid, String.valueOf(order_sn), sku, qty);
									StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getOrder_status(),order_sn, sku, -qty,false);								
								}
								Log.info("������: "+o.getOrder_id()+", δ֧����");
							}
						}
						else
						{
							Log.warn("��ȡ������Ϣ����");
							break;
						}
						
						//�����ǰ����ʱ����ڿ�ʼȡ����ʱ�䣬������´�ȡ����ʱ��(����ȡ�����б�����޸�ʱ��)
						//����ͬ����������ʱ��
		                if (addTime.compareTo(modified)>0)
		                {
		                	modified=addTime;
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
		Log.info("����ȡΨƷ�ᶩ�����������!");
	}
	
	
	//��ȡΨƷ���˻�����
	public void getRefundOrderList(Connection conn) throws Exception
	{
		Log.info("��ȡΨƷ���˻�������ʼ:");
		int pageIndex = 1 ;  //ΨƷ��Ķ�����1ҳ����
		boolean hasNextPage = true ;	
		
		Date modified=Formatter.parseDate(lastRefundTime,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					//�µ�ʱ�䷶Χ(ֻȡ����Ķ���)
					Date startdate=new Date(Formatter.parseDate(lastRefundTime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lastRefundTime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//��ȡ�����б�
					JSONObject jsonobj = new JSONObject();
					try {
						jsonobj.put("st_create_time", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
						jsonobj.put("et_create_time", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
						jsonobj.put("vendor_id", Params.vendor_id);
						jsonobj.put("page", pageIndex);
						jsonobj.put("limit", Integer.parseInt(Params.pageSize));
					} catch (JSONException e) {
						Log.warn("׼����������ʱ����!");
						continue;
					}
					//���������ΨƷ��
					String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getReturnList", jsonobj.toString());
					if(responseText.equals("")) continue;
					//�ѷ��ص�����ת��json����
					JSONObject responseObj=new JSONObject(responseText);
					//��������
					if(!responseObj.getString("returnCode").equals("0")){
						String ErrStrCode = responseObj.getString("returnCode");
						String ErrMsg = responseObj.getString("returnMessage");
						Log.warn("ȡ�˻�����������,������: "+ErrStrCode+"������Ϣ: "+ErrMsg);
						sleep(10000L);
						continue;
					}
					//ҳ��
					int orderNum= responseObj.getJSONObject("result").getInt("total");
					int pageTotal=0;
					if(orderNum!=0){
						pageTotal = orderNum>=Integer.parseInt(Params.pageSize) ? (orderNum %Integer.parseInt(Params.pageSize)==0?orderNum /Integer.parseInt(Params.pageSize):(orderNum /Integer.parseInt(Params.pageSize)+1)) : 1;
					}
					Log.info("��ǰҳ:" + pageIndex + ",��ҳ���� " + pageTotal);
					//��ǰû����
					if (pageTotal==0)
					{				
						if (n==1)
						{
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).compareTo(
									this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimerefundvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimerefundvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimerefundvalue, value);
										Log.info("һ��֮�ڶ�ȡ�����˻�����,���ҵ�ǰ�����������! �ѽ�ȡ�˻���������ʱ�����Ϊ��ǰ������");
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
					//��ȡ��ǰҳ���˻������б�
					JSONArray ordersList = responseObj.getJSONObject("result").getJSONArray("dvd_return_list");
					for(int i = 0 ; i< ordersList.length() ; i++)
					{
						//��ȡ��ǰ�˻�����
						JSONObject orderJson = ordersList.getJSONObject(i);
						ReturnOrder o = new ReturnOrder();
						o.setObjValue(o,orderJson);
						
						//������� 
						String order_sn = orderJson.getString("order_id");
						//�������뵥��
						String back_sn = orderJson.getString("back_sn");
						//��b2c��ȡ���˶���״̬ʱ��
						Date createTime = Formatter.parseDate(orderJson.getString("create_time"),Formatter.DATE_TIME_FORMAT);
						//��ȡ��ǰ��������
						JSONArray itemArrayTemp = OrderUtils.getRefundOrderItem(back_sn);
						if(itemArrayTemp == null)	//��ȡ��������ʧ��
						{
							Log.warn("��ȡ�˻�������Ϣ����ʧ��,���Դ���");
							continue;
						}
						
						System.out.println(itemArrayTemp.toString());
						
						o.setFieldValue(o, "orderItemList", itemArrayTemp);
						
						if(o != null)
						{
							Log.info("���ڴ����˻�����:" + order_sn + "   ����״̬:" + OrderUtils.getOrderStateByCode(o.getReturn_status()) + "   �˻�ԭ��:" + o.getReturn_reason());
							//59���˻�
							//60�����
							//100�˻�ʧ��
							//54�˻������
							if ("54".equals(o.getReturn_status()))
							{
								if (!OrderManager.RefundIntfExists("���ΨƷ���˻�����", conn, o.getOrder_id(),o.getBack_sn()))
								{
									try
									{
										Log.info("���ɵ�ǰ�˻�����:" +o.getOrder_id()+ "���˻��ӿ�����...");
										OrderUtils.createRefundOrder(conn,o,Params.tradecontactid);
									} catch(SQLException sqle)
									{
										throw new JException("���ɽӿ��˻���������!" + sqle.getMessage());
									}
								}
							}
						}
						else
						{
							Log.warn("��ȡ�˻�������Ϣʧ�ܣ�");
							break;
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

				if (modified.compareTo(Formatter.parseDate(lastRefundTime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimerefundvalue, value);
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
		Log.info("����ȡΨƷ���˻��������������!");
	}
}