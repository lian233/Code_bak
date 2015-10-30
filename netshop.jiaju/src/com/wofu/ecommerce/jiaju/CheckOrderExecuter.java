package com.wofu.ecommerce.jiaju;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.jiaju.utils.CommHelper;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.business.order.OrderManager;

public class CheckOrderExecuter extends Executer {
	public static String username = "";	//�̳�����(��:�ҾӾ͡������������̳�...)
	public static String dbname = "";	//���ݿ����ӳ�
	public static int waittime = 10;
	public static final String url = "http://www.jiaju.com/openapi/";	//�ӿڵ�ַ
	public static String partner_id = "";	//�ʺ�
	public static String Partner_pwd = "";	//����
	public static String tradecontactid = "7";		//���ݿ���:select * from TradeContacts,��ʽ��ʱ����Ҫ���һ����¼��ȥʹ��
	public static String company = "";		//��ݹ�˾��Ӧ��
	private static String jobName="���ҾӾͶ�����ҵ";

	public void run()  {
		try {		
			
			updateJobFlag(1);
	
			getOrderList();
			
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

	
	/*
	 * ��ȡһ��֮�ڵ����ж���
	 */
	private void getOrderList() throws Exception
	{
		try
		{
			Connection conn = this.getDao().getConnection();
			Properties prop = StringUtil.getStringProperties(this.getExecuteobj().getParams());
			username = prop.getProperty("username","�ҾӾ�");
			dbname =  prop.getProperty("dbname", "jiaju");
			waittime= Integer.parseInt(prop.getProperty("waittime","10"));
			partner_id = prop.getProperty("partner_id", "");
			Partner_pwd = prop.getProperty("Partner_pwd", "");
			//tradecontactid = prop.getProperty("tradecontactid","7");
			company = prop.getProperty("company","");
			
			//׼��Ҫ�������������
			HashMap<String, String> Data = new HashMap<String, String>();
			Data.put("service", "get_orders_to_send");	//������
			Data.put("type", "MD5");	//����ǩ������ʽ(�̶�)
			Data.put("partner_id", partner_id);	//������ID
			Data.put("doc", "json");	//�������ݸ�ʽ(�̶�)
	
			//��Key����
			String sortStr = CommHelper.sortKey(Data);
			//��������ǩ��
			String signed = CommHelper.makeSign(sortStr, Partner_pwd);
			//���������������
			//System.out.println("��������:" + signed);
			Log.info("���ͻ�ȡ�����б�����");
			//��������
			String responseText = CommHelper.sendByPost(url, signed);
			//������صĽ��
			//System.out.println(responseText);
			
			//�������ص�Json
			try
			{
				JSONObject responseObj = new JSONObject(responseText);
				String resultbool = responseObj.get("status").toString();
				int itemCount = responseObj.getInt("count");
				
				Log.info("���ص�status:" + resultbool);
				if(resultbool.equals("true"))
				{
					Log.info("��ǰ��Ҫ���Ķ�����Ϊ:" + itemCount);
					if(itemCount > 0)
					{//�ж���
						//������Щ����
						JSONArray ordersList = responseObj.getJSONArray("orders");
						for(int i = 0 ; i< ordersList.length() ; i++)
						{
							JSONObject orderInfo = ordersList.getJSONObject(i);
							
							JSONObject shipInfo = orderInfo.getJSONObject("ship_info");
							JSONArray goods = orderInfo.getJSONArray("goods");
							
							Order o = new Order();
							o.setObjValue(o, orderInfo);
							o.setObjValue(o, shipInfo);
							o.setFieldValue(o, "goods", goods);
							
							String orderid = o.getOrder_id();	//�������
							Date addtime = o.getAdd_time();

							//Log.info("���ڼ�鶩���Ƿ����:" + orderid);
							//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
							if (!OrderManager.isCheck(jobName, conn, orderid))
							{//��ѯ��customerorder(����˵Ķ���)��CustomerOrderRefList(�ϲ������б�)�Ƿ��Ѿ����ڸö�������
								if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderid, addtime))
								{//�������°汾�Ķ����Ƿ���ڸö�����(ns_customerorder)
									try
									{
										Log.info("��������:" + orderid);
										OrderUtils.createInterOrder(conn, o, tradecontactid, username);	//�����ӿڶ���
										//��ȡ��Ʒ�б�
										for(Iterator ito=o.getGoods().getRelationData().iterator();ito.hasNext();)
										{
											OrderItem item=(OrderItem) ito.next();
											//��ȡ��ǰ������Ʒ��sku
											String sku = item.getOuter_id() ;
												//û�еȴ������״̬ ����Ҫɾ��δ���������Ŀ��/
												//StockManager.deleteWaitPayStock(jobName, conn,tradecontactid, orderCode,sku);
											//��ȡ��ǰSKU�Ķ�����������
											long qty= (long)item.getAmount();
											//��ecs_rationconfig���д��ڻ������һ�����ͬ����¼(�������Լ���
											StockManager.addSynReduceStore(jobName, conn, tradecontactid, o.getStatus(),orderid, sku, qty, false);
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
				else
				{//���ص�statusΪfalse���׳��쳣
					throw new Exception();
				}
			}
			catch(Exception err)
			{Log.warn("���ؽ��������,���ҾӾͶ���ʧ��!");}
		}
		catch(Exception err)
		{Log.error(jobName, "���ҾӾͶ���ʧ��:\n" + err.getMessage());}
	}
}
