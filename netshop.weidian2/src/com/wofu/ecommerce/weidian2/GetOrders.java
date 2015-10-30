package com.wofu.ecommerce.weidian2;

import java.net.URLEncoder;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

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
import com.wofu.ecommerce.weidian2.utils.Utils;

public class GetOrders extends Thread
{
	private static String jobName = "��ȡ΢�궩����ҵ";
	private static String lasttimeconfvalue = "΢��ȡ��������ʱ��"; // Parmas���Ǵ������ط����ƹ����ģ��Ѿ������޸�
	private static long daymillis = 24 * 60 * 60 * 1000L;
	private boolean is_importing = false;
	private String lasttime;
	private static String orderLineStatus = "1";
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void run()
	{

		System.out.println();
		Log.info(jobName, "����[" + jobName + "]ģ��");
		do
		{
			Connection connection = null;
			is_importing = true;
			try
			{
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.weidian2.Params.dbname); // ���ݿ�����ʱ����
				lasttime = PublicUtils
						.getConfig(connection, lasttimeconfvalue, Formatter
								.format(new Date(), Formatter.DATE_TIME_FORMAT));
				Log.info(lasttime);
				getOrderList(connection);
			} catch (Exception e)
			{
				try
				{
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1)
				{
					Log.error(jobName, "�ع�����ʧ��");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} catch (Throwable e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally
			{
				is_importing = false;
				try
				{
					if (connection != null)
						connection.close();
				} catch (Exception e)
				{
					Log.error(jobName, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long current = System.currentTimeMillis();
			while (System.currentTimeMillis() - current < Params.waittime * 1000)
			{
				try
				{
					Thread.sleep(100L);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} while (true);
		// super.run();
	}

	/***** �����̻�ȡ������ϸ��Ϣ *****/
	public void getOrderList(Connection conn) throws Throwable
	{
		Date modified = Formatter.parseDate(lasttime,
				Formatter.DATE_TIME_FORMAT);
		Date now = new Date();
		long pageno = 1L;
		JSONObject rsp_Object = new JSONObject();
		for (int k = 0; k < 10;)
		{
			try
			{
				while (true)
				{
					Date startdate = new Date(Formatter.parseDate(lasttime,
							Formatter.DATE_TIME_FORMAT).getTime() + 1000L);
					Date enddate = new Date(Formatter.parseDate(lasttime,
							Formatter.DATE_TIME_FORMAT).getTime()
							+ daymillis);
					StringBuffer buffer = new StringBuffer();
					buffer.append("service=order&");
					buffer.append("vcode=" + Params.vcode + "&");
					buffer.append("page=0&");
					buffer.append("page_size=100&");
					buffer.append("order_id=&");
					buffer.append("status=0&");
					buffer.append("mtime_start=" + URLEncoder.encode(Formatter.format(startdate, Formatter.DATE_TIME_FORMAT),"UTF-8") + "&"); // ʱ��Ҫ�޸�Ϊstartdate��enddate,UTF8���� 1970-01-01+00%3A00%3A00
					buffer.append("mtime_end="   + URLEncoder.encode(Formatter.format(enddate  , Formatter.DATE_TIME_FORMAT),"UTF-8"));
//					buffer.append("mtime_start=2015-06-29 15:00:00" + "&"); // ʱ��Ҫ�޸�Ϊstartdate��enddate,UTF8���� 1970-01-01+00%3A00%3A00
//					buffer.append("mtime_start=2015-06-29 17:00:00" ); // ʱ��Ҫ�޸�Ϊstartdate��enddate,UTF8���� 1970-01-01+00%3A00%3A00
					//Log.info(buffer.toString());
					String result = Utils.sendByPost(
							"http://www.xiulife.net/api/Order/PostOrder",
							buffer.toString());
					
//					 Log.info("�������ݣ� "+String.valueOf(result.replace("\\",
//					 "").toCharArray(), 1, result.replace("\\",
//					 "").toCharArray().length-2));
					
					char[] rsp_cleaned = result.replace("\\", "").toCharArray();
					rsp_Object = new JSONObject(String.valueOf(rsp_cleaned, 1,
							rsp_cleaned.length - 2));
					//System.out.println(rsp_Object.optInt("totalcount", 0));
					String errdesc = "";
					if (!rsp_Object.getString("msg").equals("success"))
					{
						errdesc = errdesc + " "
								+ rsp_Object.getString("msg").toString();
					}
					if (rsp_Object.optInt("totalcount",0) == 0)
					{
						if (pageno == 1L)
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
										Log.error(jobName, je.getMessage());
									}
								}
							} catch (ParseException e)
							{
								Log
										.error(jobName, "�����õ����ڸ�ʽ!"
												+ e.getMessage());
							}
						}
						Log.info(jobName, /* "ȡ�����б�ʧ��:" */
								"û�иö����������ѷ�������û�ж�����Ҫ����:" + errdesc);
					}
					k = 10;
					break;
				}

				if (!rsp_Object.getString("msg").equals("success"))
				{
					Log.error(jobName, rsp_Object.getString("msg"));
					k = 10;
					break;
				}

				int totalCount = rsp_Object.getInt("totalcount");

				if (rsp_Object.getInt("totalcount") == 0)
				{
					if (pageno == 1L)
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
									Log.error(jobName, je.getMessage());
								}
							}
						} catch (ParseException e)
						{
							Log.error(jobName, "�����õ����ڸ�ʽ!" + e.getMessage());
						}
					}
					k = 10;
					break;
				}
				String sku;
				JSONArray orderlist = rsp_Object.getJSONArray("Order_lists");
				for (int j = 0; j < orderlist.length(); j++)
				{
					JSONObject order = orderlist.getJSONObject(j);
					Order o = OrderUtils.getOrderByID(order					//���ݶ���ID��ת
							.getString("order_id"));
					/** �۲��Ƿ���Եõ�order���� **/
					Log.info(o.getOrder_id()
							+ " ���������"
							+ o.getStatus()
							+ " "
							+ Formatter.format(o.getCtime(),
									Formatter.DATE_TIME_FORMAT));
					if (o.getStatus() == 0) // ״̬ 0���� 1�ȴ����� 2�ȴ�����
											// 3�ȴ�ȷ���ջ���4���׳ɹ���5����ȡ��
					{
						if (!OrderManager.isCheck("���΢�궩��", conn, o
								.getOrder_id()))
						{
							SimpleDateFormat format = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date getUpdateDate_Date_type = null;
							try
							{
								getUpdateDate_Date_type = format.parse(o
										.getCtime());
							} catch (ParseException e)
							{
								e.printStackTrace();
							}
							if (!OrderManager.TidLastModifyIntfExists("���΢�궩��",
									conn, o.getOrder_id(),
									getUpdateDate_Date_type))
							{
								OrderUtils.createInterOrder(conn, o,
										Params.tradecontactid, Params.username);//��ת 
								for (Iterator ito = o.getOrderItemList()
										.getRelationData().iterator(); ito
										.hasNext();)
								{
									OrderItem item = (OrderItem) ito.next();
									sku = item.getSku();
									StockManager.deleteWaitPayStock(jobName,
											conn, Params.tradecontactid, o
													.getOrder_id(), sku);
									StockManager.addSynReduceStore(jobName,
											conn, Params.tradecontactid, String
													.valueOf(o.getStatus()), o
													.getOrder_id(), sku, 0,
											false);
								}
							}
						}
					}
					// ����ͬ����������ʱ��
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date getUpdateDate_date = null;
					try
					{
						getUpdateDate_date = format.parse(o.getCtime());
					} catch (ParseException e)
					{
						e.printStackTrace();
					}
					if (getUpdateDate_date.compareTo(modified) > 0)
					{
						modified = getUpdateDate_date;
					}
					// �ж��Ƿ�����һҳ
					// if
					// (pageno==(Double.valueOf(Math.ceil(totalCount/50.0))).intValue())
					// break;
					pageno++;
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
						Log.error(jobName, je.getMessage());
					}
				}
				break;

			} catch (Exception e)
			{
				if (++k >= 10)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "
						+ Log.getErrorMessage(e));
				e.printStackTrace();
				Thread.sleep(10000L);
			}
		}

	}

	@Override
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}

}
