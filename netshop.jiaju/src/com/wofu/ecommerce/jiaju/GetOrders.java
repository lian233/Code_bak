package com.wofu.ecommerce.jiaju;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.jiaju.utils.CommHelper;
import com.wofu.ecommerce.jiaju.Order;
import com.wofu.ecommerce.jiaju.OrderUtils;

//�ҾӾͻ�ȡ������ҵ
public class GetOrders extends Thread {

	private static String jobname = "��ȡ�ҾӾͶ�����ҵ";		//��ҵ����
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Date nowtime = new Date();
			if(Params.startTime.getTime() <= nowtime.getTime())
			{//���ϻ򳬹�ָ��������ʱ��
				Connection connection = null;
				Log.info("��ʼ����ȡ�ҾӾͶ�����������!");
				try {
					//��ȡ���ݿ�conn
					connection = PoolHelper.getInstance().getConnection(Params.dbname);

					//��ȡ�ҾӾ��¶��� 
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
	
	
	//��ȡ�ҾӾ��¶���
	public void getOrderList(Connection conn) throws Exception
	{
		try
		{
			//׼��Ҫ�������������
			HashMap<String, String> Data = new HashMap<String, String>();
			Data.put("service", "get_orders_to_send");	//������
			Data.put("type", "MD5");	//����ǩ������ʽ(�̶�)
			Data.put("partner_id", Params.partner_id);	//������ID
			Data.put("doc", "json");	//�������ݸ�ʽ(�̶�)
	
			//��Key����
			String sortStr = CommHelper.sortKey(Data);
			//��������ǩ��
			String signed = CommHelper.makeSign(sortStr, Params.Partner_pwd);
			//���������������
			//System.out.println("��������:" + signed);
			Log.info("��������:" + signed);
			//��������
			String responseText = CommHelper.sendByPost(Params.url, signed);
			//������صĽ��
			Log.info(responseText);
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
					Log.info("��ǰ������Ҫ����Ķ�����Ϊ(���ܰ����Ѵ���Ķ���):" + itemCount);
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
							
							String orderid = o.getTrade_id();	//�������
							Date addtime = o.getAdd_time();
							
							//�����趨��"ֻץȡָ��ʱ��֮��Ķ���"ʱ,���µ�ʱ������ж�
							if(Params.orderAddTime != null)
							{
								//���µ�ʱ����趨ʱ����������
								if(Params.orderAddTime.getTime() > addtime.getTime())
								{
									Log.info("�����趨��ֻȡָ��ʱ��[" + Formatter.format(Params.orderAddTime, "yyyy-MM-dd HH:mm:ss") + "]֮��Ķ���,���Ըö�����������������! ������:" + orderid + " �µ�ʱ��:" 
											+ Formatter.format(addtime, "yyyy-MM-dd HH:mm:ss"));
									continue;
								}
							}
							//����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
							if (!OrderManager.isCheck(jobname, conn, orderid))
							{//��ѯ��customerorder(����˵Ķ���)��CustomerOrderRefList(�ϲ������б�)�Ƿ��Ѿ����ڸö�������
								if (!OrderManager.TidLastModifyIntfExists(jobname, conn, orderid, addtime))
								{//�������°汾�Ķ����Ƿ���ڸö�����(ns_customerorder)
									Log.info("���ڴ�����:" + orderid);
									try
									{
										OrderUtils.createInterOrder(conn, o, Params.tradecontactid, Params.username);	//�����ӿڶ���
										//��ȡ��Ʒ�б�
										for(Iterator ito=o.getGoods().getRelationData().iterator();ito.hasNext();)
										{
											OrderItem item=(OrderItem) ito.next();
											//��ȡ��ǰ������Ʒ��sku
											String sku = item.getOuter_id() ;
												//û�еȴ������״̬ ����Ҫɾ��δ���������Ŀ��/
												//StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, orderCode,sku);
											//��ȡ��ǰSKU�Ķ�����������
											long qty= (long)item.getAmount();
											//��ecs_rationconfig���д��ڻ������һ�����ͬ����¼(�������Լ���
											StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, o.getStatus(),orderid, sku, qty, false);
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
			catch(Exception jsonerr)
			{//���ؽ��������
				Log.warn("���ؽ��������,��ȡ����ʧ��!");
				//jsonerr.printStackTrace();
				//failed = true;
			}
		}
		catch(Exception err)
		{
			Log.error(jobname, "��ȡ����ʧ��:\n" + err.getMessage());
			//failed = true;
		}
		Log.info("����ȡ�ҾӾͶ������������!");
		Thread.sleep(Params.waittime * 1000 * 60);
	}
}