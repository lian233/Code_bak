package com.wofu.ecommerce.meilishuo;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.meilishuo.util.CommHelper;
import com.wofu.ecommerce.meilishuo.util.Utils;
public class GetOrders extends Thread
{

	private static String jobname = "��ȡ����˵������ҵ";

	private static String lasttimeconfvalue = "����˵ȡ��������ʱ��";
	/** ����˵ȡ��������ʱ�� **/

	private static long daymillis = 24 * 60 * 60 * 1000L;

	private String lasttime;

	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	public void run()
	{

		Log.info(jobname, "����[" + jobname + "]ģ��");
		do
		{
			Connection connection = null;

			try
			{
				connection = PoolHelper.getInstance().getConnection(
						Params.dbname);

				lasttime = PublicUtils.getConfig(connection, lasttimeconfvalue,
						"");
				/**
				 * ����״̬ 10��������20�ѷ�����21���ַ�����30���׳ɹ� ��40���׹ر�
				 */
				// ��ȡ����˵�¶���
				//Params.refreshToken = PublicUtils.getRefreToken(connection, Integer.parseInt(Params.tradecontactid));
				//Params.token = CommHelper.refreshToken(Params.refreshToken,Params.appKey,Params.appsecret);
				Params.token = PublicUtils.getToken(connection, Integer.parseInt(Params.tradecontactid));
				getOrderList(connection);

			} catch (Exception e)
			{
				try
				{
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1)
				{
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally
			{
				try
				{
					if (connection != null)
						connection.close();
				} catch (Exception e)
				{
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))
				try
				{
					sleep(1000L);
				} catch (Exception e)
				{
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	// ��ȡ����˵�¶���
	public void getOrderList(Connection conn) throws Exception
	{
		int pageIndex = 0; // ����˵�Ķ�����0ҳ����
		boolean hasNextPage = true;

		Date modified = Formatter.parseDate(lasttime,
				Formatter.DATE_TIME_FORMAT);
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
					String apimethod = "meilishuo.order.list.get";
					HashMap<String, String> param = new HashMap<String,String>();
					param.put("method", apimethod);
					param.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
					param.put("format", "json");
					param.put("app_key", Params.appKey);
					param.put("v", "1.0");
					param.put("sign_method", "md5");
					param.put("session", Params.token);
					param.put("page", String.valueOf(pageIndex));
					param.put("page_size", Params.pageSize);
					param.put("ctime_start", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
					System.out.println("starttime: "+Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
					param.put("ctime_end", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
					
					Log.info("��" + pageIndex + "ҳ");
					String responseText = Utils.sendbyget(Params.url,
							param,Params.appsecret);
					Log.info("��������: "
							+ new String(responseText.getBytes(), "GBK"));
					JSONObject responseObj = new JSONObject(responseText);
					// �������Ļ���
					try
					{
						String errormessage = responseObj.getJSONObject(
								"error_response").getString("message"); // ���û������try������ִ�гɹ����д�ͻ�ִ�г������
						try
						{
							// ��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
							if (this.dateformat.parse(Formatter.format(new Date(),Formatter.DATE_FORMAT))
									.compareTo(this.dateformat.parse(Formatter.format(
							Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),
									Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT))) > 0)
							{
								try
								{
									String value = Formatter.format((new Date(
											Formatter.parseDate(
													PublicUtils.getConfig(conn,
															lasttimeconfvalue,
															""),
													Formatter.DATE_TIME_FORMAT)
													.getTime()
													+ daymillis)),
											Formatter.DATE_FORMAT)
											+ " 00:00:00";
									PublicUtils.setConfig(conn,
											lasttimeconfvalue, value);
								} catch (JException je)
								{
									Log.error(jobname, je.getMessage());
								}
							}
							return;
						} catch (ParseException e)
						{
							Log.error(jobname, "�����õ����ڸ�ʽ!" + e.getMessage());
						}

					} catch (Exception e)
					{

					}
					int orderNum;
					orderNum = responseObj.getJSONObject("order_list_get_response").getInt("total_num");
					if (orderNum == 0)//û�ж���
					{
						if (n == 1)
						{
							try
							{
								// ��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat
										.parse(
												Formatter.format(new Date(),
														Formatter.DATE_FORMAT))
										.compareTo(
												this.dateformat
														.parse(Formatter
																.format(
																		Formatter
																				.parseDate(
																						PublicUtils
																								.getConfig(
																										conn,
																										lasttimeconfvalue,
																										""),
																						Formatter.DATE_TIME_FORMAT),
																		Formatter.DATE_FORMAT))) > 0)
								{
									try
									{
										String value = Formatter
												.format(
														(new Date(
																Formatter
																		.parseDate(
																				PublicUtils
																						.getConfig(
																								conn,
																								lasttimeconfvalue,
																								""),
																				Formatter.DATE_TIME_FORMAT)
																		.getTime()
																		+ daymillis)),
														Formatter.DATE_FORMAT)
												+ " 00:00:00";
										PublicUtils.setConfig(conn,
												lasttimeconfvalue, value);
									} catch (JException je)
									{
										Log.error(jobname, je.getMessage());
									}
								}
							} catch (ParseException e)
							{
								Log
										.error(jobname, "�����õ����ڸ�ʽ!"
												+ e.getMessage());
							}
						}
						break;
					}
					int pageTotal = orderNum >= Integer
							.parseInt(Params.pageSize) ? (orderNum
							% Integer.parseInt(Params.pageSize) == 0 ? orderNum
							/ Integer.parseInt(Params.pageSize) : (orderNum
							/ Integer.parseInt(Params.pageSize) + 1)) : 1;
					Log.info("��ҳ���� " + pageTotal);
					
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
						Date createTime = o.getCtime();
						if (o != null)
						{
							Log.info("������־�� " + o.getDeliver_status() + "��");
							Log.info("�����š�" + o.getOrder_id() + "��,״̬��"
									+ o.getStatus_text() + "��"+"֧��ʱ��: "+Formatter.format(o.getPay_time(),Formatter.DATE_TIME_FORMAT));
							// ����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
							if ("\u7b49\u5f85\u53d1\u8d27".equals(o
									.getStatus_text()))
							{
								if (!OrderManager.isCheck(jobname, conn,
										orderCode))
								{
									if (!OrderManager.TidLastModifyIntfExists(
											jobname, conn, orderCode,
											createTime))
									{
										try
										{
											OrderUtils.createInterOrder(conn,
													o, Params.tradecontactid,
													Params.username);
											for (Iterator ito = o
													.getOrderItemList()
													.getRelationData()
													.iterator(); ito.hasNext();)
											{
												OrderItem item = (OrderItem) ito
														.next();
												String sku = item.getSku();
												long qty = (long) item
														.getAmount();
											}

										} catch (SQLException sqle)
										{
											throw new JException("���ɽӿڶ�������!"
													+ sqle.getMessage());
										}
									}
								} // ����״̬ 10��������20�ѷ�����21���ַ�����30���׳ɹ� ��40���׹ر�
							}

							// 40���׹ر�
							else if ("����ȡ��".equals(o.getStatus_text()))
							{
								Log.info("������: " + o.getOrder_id() + ", ����ȡ����");
								for (Iterator ito = o.getOrderItemList()
										.getRelationData().iterator(); ito
										.hasNext();)
								{
									OrderItem item = (OrderItem) ito.next();
									String sku = item.getSku();
									long qty = (long) (item.getAmount());
									// ɾ����������棬����������Ŀ��
									StockManager.deleteWaitPayStock(jobname,
											conn, Params.tradecontactid,
											orderCode, sku);
									StockManager.addSynReduceStore(jobname,
											conn, Params.tradecontactid, o
													.getStatus_text(), o
													.getOrder_id(), sku, qty,
											false);
								}

								// ȡ������
								String sql = "declare @ret int;  execute  @ret = IF_CancelCustomerOrder '"
										+ orderCode + "';select @ret ret;";
								int resultCode = SQLHelper.intSelect(conn, sql);
								// ȡ������ʧ��
								if (resultCode == 2)
									Log
											.info("����˵����ȡ������ʧ��,����:" + orderCode
													+ "");
								else
									Log
											.info("����˵����ȡ�������ɹ�,����:" + orderCode
													+ "");

							} else if ("���׳ɹ�".equals(o.getStatus_text())) // ���׳ɹ�
							{
								for (Iterator ito = o.getOrderItemList()
										.getRelationData().iterator(); ito
										.hasNext();)
								{
									OrderItem item = (OrderItem) ito.next();
									String sku = item.getSku();

									StockManager.deleteWaitPayStock(jobname,
											conn, Params.tradecontactid,
											orderCode, sku);
								}

							} else if ("�ȴ�����".equals(o.getStatus_text())) // ���׳ɹ�
							{

								for (Iterator ito = o.getOrderItemList()
										.getRelationData().iterator(); ito
										.hasNext();)
								{
									OrderItem item = (OrderItem) ito.next();
									String sku = item.getSku();
									long qty = (long) (item.getAmount());

									StockManager.addWaitPayStock(jobname, conn,
											Params.tradecontactid, String
													.valueOf(orderCode), sku,
											qty);
									StockManager.addSynReduceStore(jobname,
											conn, Params.tradecontactid, o
													.getStatus_text(), String
													.valueOf(orderCode), sku,
											-qty, false);
								}

							}

						}
						// �����ǰ����ʱ����ڿ�ʼȡ����ʱ�䣬������´�ȡ����ʱ��(����ȡ�����б�����޸�ʱ��)
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
				if (modified.compareTo(Formatter.parseDate(lasttime,
						Formatter.DATE_TIME_FORMAT)) > 0)
				{
					try
					{
						String value = Formatter.format(modified,
								Formatter.DATE_TIME_FORMAT);
						PublicUtils.setConfig(conn, lasttimeconfvalue, value);
					} catch (JException je)
					{
						Log.error(jobname, je.getMessage());
					}
				}

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
				Log.warn(jobname + " ,Զ������ʧ��[" + k + "], 10����Զ�����. "
						+ Log.getErrorMessage(e));
				Thread.sleep(10000L);

			}
		}
		Log.info("����ȡ����˵�������������!");
	}

}