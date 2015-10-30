package com.wofu.netshop.meilishuo.fenxiao;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import com.wofu.business.fenxiao.order.OrderManager;
import com.wofu.business.fenxiao.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.common.fenxiao.Utils;
import com.wofu.netshop.meilishuo.fenxiao.Params;
public class GetOrdersRunnable implements Runnable{
	private CountDownLatch watch=null;
	public String jobName="����˵���ض�����ҵ";
	private String lasttime;
	private String refundlasttime;
	private static long daymillis = 24 * 60 * 60 * 1000L;
	Params param;
	public GetOrdersRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
	}
	public void run() {
		Connection conn =null;
		jobName = param.username+jobName;
		try{
			conn= PoolHelper.getInstance().getConnection("shop");
			getOrderList(conn);
		}catch(Throwable e){
			Log.error(param.username, e.getMessage(),null);
		}finally{
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.error(param.username, e.getMessage(),null);
				}
			}
			watch.countDown();
		}
		
	}
	//���ض���
	private void getOrderList(Connection conn) throws Throwable{
		lasttime=PublicUtils.getConfig(conn,"LastOrderTime",param.shopid);
		System.out.println("lasttime: "+lasttime);
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		boolean hasNextPage=true;
		int pageIndex=0;
		for (int k = 0; k < 10;)
		{
			try
			{
				int n = 1;
				while (hasNextPage)
				{
					Date startdate = new Date(Formatter.parseDate(lasttime,
							Formatter.DATE_TIME_FORMAT).getTime() + 1000L);
					Date enddate = new Date(Formatter.parseDate(lasttime,
							Formatter.DATE_TIME_FORMAT).getTime()
							+ daymillis);
					Log.info(param.username,"��" + pageIndex + "ҳ",null);
					/**
					JSONObject object = new JSONObject(PublicUtils.getConfig(
							conn, "����˵Token��Ϣ2", "")); // ��ȡ���µ�Token
							**/
					HashMap<String,String> request = new HashMap<String,String>();
					request.put("app_key", param.appKey);
					request.put("method", "meilishuo.order.list.get");
					request.put("session", param.token);
					request.put("sign_method", "md5");
					request.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
					request.put("v", "1.0");
					request.put("page", String.valueOf(pageIndex));
					request.put("page_size", param.pageSize);
					request.put("uptime_start", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
					request.put("uptime_end", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
					String responseText = Utils.sendByGet(request,param.url,true,param.appsecret);
					Log.info(param.username,"��������: "
							+ new String(responseText.getBytes(), "GBK"),null);
					JSONObject responseObj = new JSONObject(responseText);
					if(!responseObj.isNull("error_response")){
						Log.error(param.username, responseObj.getJSONObject("error_response").getString("message"));
						k=10;
						break;
					}
					int orderNum;
					try
					{
						orderNum = Integer.parseInt(responseObj.getJSONObject(
						"order_list_get_response").getString("total_num"));
					}catch(Exception e)
					{
						orderNum = responseObj.getJSONObject("order_list_get_response").getInt("total_num");
					}
					int pageTotal = orderNum >= Integer
							.parseInt(param.pageSize) ? (orderNum
							% Integer.parseInt(param.pageSize) == 0 ? orderNum
							/ Integer.parseInt(param.pageSize) : (orderNum
							/ Integer.parseInt(param.pageSize) + 1)) : 1;
					Log.info(param.username,"��ҳ���� " + pageTotal,null);
					if (pageTotal == 0)
					{
						if (n == 1)
						{
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (Formatter.parseDate(Formatter.format(new Date(), Formatter.DATE_FORMAT),Formatter.DATE_FORMAT).
										compareTo(Formatter.parseDate(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,"LastOrderTime",param.shopid),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT),Formatter.DATE_FORMAT))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,"LastOrderTime",param.shopid),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, "LastOrderTime", param.shopid,value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(param.username,jobName, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(param.username,jobName, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						break;
					}
					// ����Ԫ��
					JSONArray ordersList = responseObj.getJSONObject(
							"order_list_get_response").getJSONArray("info");
					for (int i = 0; i < ordersList.length(); i++)
					{ // ĳ������
						JSONObject orderInfo = ordersList.getJSONObject(i);
						JSONObject order = orderInfo.getJSONObject("order");
						JSONObject address = orderInfo.getJSONObject("address");
						// �������
						String orderCode = String.valueOf(orderInfo
								.getJSONObject("order").get("order_id"));
						// ������Ʒ����
						JSONArray items = orderInfo.getJSONArray("goods");
						// �������񼯺�
						JSONArray servers = orderInfo.getJSONArray("service");
						// ����һ����������
						Order o = new Order();
						o.setObjValue(o, order);
						o.setObjValue(o, address);
						o.setFieldValue(o, "orderItemList", items);
						o.setFieldValue(o, "serviceList", servers);
						// ������Ʒ��ĳЩ����
						for (Iterator ito = o.getOrderItemList()
								.getRelationData().iterator(); ito.hasNext();)
						{
							OrderItem item = (OrderItem) ito.next();
							String color = "";
							String size = "";
							for (Iterator it = item.getProp().getRelationData()
									.iterator(); it.hasNext();)
							{
								Prop prop = (Prop) it.next();
								if (prop.getName().equals("\u989c\u8272"))
								{
									color = prop.getValue();
									color = new String(prop.getValue()
											.getBytes(), "GBK");
									Log.info(color);
								}
								if (prop.getName().equals("\u5c3a\u7801"))
								{
									size = prop.getValue();
									size = new String(prop.getValue()
											.getBytes(), "GBK");
									Log.info(size);
								}
							}
							// ��Ʒsku
							String sku = OrderUtils.getItemCodeByColSizeCustom(
									conn, item.getGoods_no(), color, size);
							item.setSku(sku);
						}
						Date createTime = o.getPay_time();
						if (o != null)
						{
							Log.info(param.username,"������־�� " + o.getDeliver_status() + "��",null);
							Log.info(param.username,"�����š�" + o.getOrder_id() + "��,״̬��"
									+ o.getStatus_text() + "��",null);
							// ��������
							// ����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
							if ("\u7b49\u5f85\u53d1\u8d27".equals(o
									.getStatus_text()))
							{
								//if (!OrderManager.isCheck(jobName, conn,
									//	orderCode))
								//{
									if (!OrderManager.TidLastModifyIntfExists(
											jobName, conn, orderCode,
											createTime))
									{
										try
										{
											OrderUtils.createInterOrder(conn,
													o, param.shopid,
													param.username,20);
										} catch (SQLException sqle)
										{
											throw new JException("���ɽӿڶ�������!"
													+ sqle.getMessage());
										}
									}
								//} // ����״̬ 10��������20�ѷ�����21���ַ�����30���׳ɹ� ��40���׹ر�
							}//�ȴ�ȷ���ջ�Ҳ��ϵͳ����ֹ�ظ�����
							else if("\u7b49\u5f85\u786e\u8ba4\u6536\u8d27".equals(o.getStatus_text())){
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderCode,createTime))
									{
										try
										{
											OrderUtils.createInterOrder(conn,o, param.shopid,
													param.username,30);
										} catch (SQLException sqle)
										{
											throw new JException("���ɽӿڶ�������!"
													+ sqle.getMessage());
										}
									}
							}
							else if("���׳ɹ�".equals(o.getStatus_text())){
								if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderCode,createTime))
								{
									try
									{
										OrderUtils.createInterOrder(conn,o, param.shopid,
												param.username,100);
									} catch (SQLException sqle)
									{
										throw new JException("���ɽӿڶ�������!"
												+ sqle.getMessage());
									}
								}
						}

							// 40���׹ر�
							else if ("���׹ر�".equals(o.getStatus_text()))
							{
								//if (!OrderManager.isCheck(jobName, conn,orderCode))
								//{
									if (!OrderManager.TidLastModifyIntfExists(jobName, conn, orderCode,createTime))
									{
										try
										{
											OrderUtils.createInterOrder(conn,o, param.shopid,
													param.username,110);
										} catch (SQLException sqle)
										{
											throw new JException("���ɽӿڶ�������!"
													+ sqle.getMessage());
										}
									}
								//}
								

							}

						}

						// �����ǰ����ʱ����ڿ�ʼȡ����ʱ�䣬������´�ȡ����ʱ��(����ȡ�����б�����޸�ʱ��)
						// ����ͬ����������ʱ��

						if (createTime.compareTo(modified) > 0)
						{
							modified = createTime;
						}

					}
					// �ж��Ƿ�����һҳ
					if (pageIndex >= pageTotal - 1)
						hasNextPage = false;
					else
						pageIndex++;

					n++;

				}
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, "LastOrderTime", param.shopid,value);	
	            	}catch(JException je)
	            	{
	            		Log.error(param.username,param.username,je.getMessage(),0);
	            	}
				}
				k=10;
				break;

			} catch (Exception e)
			{
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				if (conn != null && !conn.getAutoCommit())
				{
					conn.rollback();
				}
				Log.warn(jobName + " ,Զ������ʧ��[" + k + "], 10����Զ�����. "
						+ Log.getErrorMessage(e));
				Thread.sleep(10000L);

			}
		}
		Log.info("����ȡ����˵�������������!");
	}

}
