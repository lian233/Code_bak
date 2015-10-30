package com.wofu.ecommerce.weipinhui;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import com.wofu.base.job.Executer;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weipinhui.util.CommHelper;

/**
 * 
 *���δ�붩��
 *���ȡ������
 *
 */
public class CheckOrderExecuter extends Executer {

	private static String pageSize = "10" ;
	
	private static String jobName="��ʱ���ΨƷ��δ�붩��";
	private static long daymillis=24*60*60*1000L;
	
	@Override
	public void run() {
		try 
		{	
			//��ȡ����
			Connection conn = this.getDao().getConnection();
			Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
			Params.tradecontactid = prop.getProperty("tradecontactid","10");
			Params.username = prop.getProperty("username","");
			Params.UpdateSettingFromDB(conn);

			
			//���δ�붩��
			updateJobFlag(1);
			
			checkWaitStockOutOrders(conn);

			UpdateTimerJob();
			
			conn.close();
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
	public  void checkWaitStockOutOrders(Connection conn) throws Exception
	{
		Log.info(jobName+"����ʼ!");
		int pageIndex = 1 ;  //ΨƷ��Ķ�����0ҳ����
		boolean hasNextPage = true ;	
		
		for (int k=0;k<10;)
		{
			try 
			{
				int n=1;
				
				while(hasNextPage)
				{
					//�µ�ʱ��(һ��ǰ�Ķ���)
					Date startdate=new Date(new Date().getTime()-daymillis);
					Date enddate=new Date();
					//��ȡ�����б�
					JSONObject jsonobj = new JSONObject();
					try {
						jsonobj.put("st_add_time", "2014-01-01");  //Formatter.format(startdate, Formatter.DATE_TIME_FORMAT)
						jsonobj.put("et_add_time", "2015-01-01");  //Formatter.format(enddate, Formatter.DATE_TIME_FORMAT)
//						jsonobj.put("st_add_time", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
//						jsonobj.put("et_add_time", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
						jsonobj.put("vendor_id", Params.vendor_id);
						jsonobj.put("order_status", 10);
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
						break;
					}
					//ҳ��
					int orderNum = responseObj.getJSONObject("result").getInt("total");
					int pageTotal=0;
					if(orderNum!=0){
						pageTotal = orderNum>=Integer.parseInt(Params.pageSize) ? (orderNum %Integer.parseInt(Params.pageSize)==0?orderNum /Integer.parseInt(Params.pageSize):(orderNum /Integer.parseInt(Params.pageSize)+1)) : 1;
					}
					Log.info("��ǰҳ:" + pageIndex + ",��ҳ���� " + pageTotal);
					//��ǰû����
					if(pageTotal==0){
						Log.info("���ζ�����Ϊ0");
						break;
					}
					//��ȡ��ǰҳ�Ķ����б�
					JSONArray ordersList = responseObj.getJSONObject("result").getJSONArray("dvd_order_list");
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

						Log.info("���ڴ�����:" + order_sn + "   ����״̬:" + OrderUtils.getOrderStateByCode(o.getOrder_status()));
						
						//��ȡ��ǰ��������
						JSONArray itemArrayTemp = OrderUtils.getOrderItem(order_sn);
						o.setFieldValue(o, "orderItemList", itemArrayTemp);
						
						if(o != null)
						{
							//�ȴ����������������ӿڶ����ɹ�������������Ŀ��
							if("10".equals(o.getOrder_status()))
							{
								String nschaverecode = SQLHelper.strSelect(conn, "select count(*) from ns_customerorder where tid = '" + order_sn + "' and sellernick = '" + Params.username + "'");
								System.out.println("ns_customerorder have recode:" + nschaverecode);
								if (nschaverecode.equals("0") && !OrderManager.isCheck(jobName, conn, order_sn) && !OrderManager.TidLastModifyIntfExists(jobName, conn, order_sn,addTime))
								{
									Log.info("�������ɽӿڶ���");
									try
									{
										OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);
										for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
										{
											OrderItem item=(OrderItem) ito.next();
											String sku = item.getBarcode();
											//û�еȴ������״̬ ����Ҫɾ��δ���������Ŀ��/
											StockManager.deleteWaitPayStock(jobName, conn,Params.tradecontactid, order_sn,sku);
											long qty= (long)item.getAmount();
											//��ecs_rationconfig���д��ڻ������һ�����ͬ����¼(�������Լ���
											StockManager.addSynReduceStore(jobName, conn, Params.tradecontactid, o.getOrder_status(),o.getOrder_id(), sku, qty,false);
										}
									} catch(SQLException sqle)
									{
										throw new JException("���ɽӿڶ�������!" + sqle.getMessage());
									}
								}
								else
									Log.info("����:" +order_sn+ "�Ѿ����������ݿ���");
							}
						}
					}
					if(pageIndex >= pageTotal)
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

}
