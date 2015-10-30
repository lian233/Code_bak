package com.wofu.ecommerce.meilishuo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import com.wofu.base.job.Executer;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.meilishuo.util.CommHelper;
import com.wofu.ecommerce.meilishuo.util.Utils;
/**
 * 
 *���δ�붩��
 *���ȡ������
 *
 */
public class CheckOrderExecuter extends Executer
{

	private static String pageSize = "10" ;
	
	private static String jobName="��ʱ�������˵δ�붩��";
	private static long daymillis=24*60*60*1000L;
	private static String vcode = "" ;
	private static String url = "" ;
	private static String appKey = "" ;
	private static String appsecret = "" ;
	private static String token = "" ;
	private static String tradecontactid = "" ;
	private static String username = "" ;
	@Override
	public void run()
	{
		Properties prop = StringUtil.getStringProperties(this.getExecuteobj()
				.getParams());
		pageSize = prop.getProperty("pageSize");
		vcode = prop.getProperty("vcode");
		url = prop.getProperty("url");
		tradecontactid = prop.getProperty("tradecontactid");
		username = prop.getProperty("username");
		try
		{
			// ���δ�붩��
			updateJobFlag(1);
			token = PublicUtils.getToken(this.getDao().getConnection(), Integer.parseInt(tradecontactid));
			checkWaitStockOutOrders();
			// ���ȡ������
			// checkCancleOrders();

			UpdateTimerJob();

			Log.info(jobName, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes()
					+ "] ["
					+ this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
					+ this.datetimeformat.format(this.getExecuteobj()
							.getNextactive()));

		} catch (Exception e)
		{
			try
			{

				if (this.getExecuteobj().getSkip() == 1)
				{
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));

				updateJobFlag(0);

				if (this.getConnection() != null
						&& !this.getConnection().getAutoCommit())
					this.getConnection().rollback();

				if (this.getExtconnection() != null
						&& !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();

			} catch (Exception e1)
			{
				Log.error(jobName, "�ع�����ʧ��");
			}
			Log.error(jobName, "������Ϣ:" + Log.getErrorMessage(e));

			Log.error(jobName, "ִ����ҵʧ�� ["
					+ this.getExecuteobj().getActivetimes() + "] ["
					+ this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));

		} finally
		{
			try
			{
				updateJobFlag(0);
			} catch (Exception e)
			{
				Log.error(jobName, "���´����־ʧ��");
			}

			try
			{
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();

			} catch (Exception e)
			{
				Log.error(jobName, "�ر����ݿ�����ʧ��");
			}
		}

	}
	/**
	 * ���δ����������� orderStatus=10 �ȴ����� ������һ��ʱ���δ�붩��
	 **/
	public void checkWaitStockOutOrders() throws Exception
	{
		Log.info(jobName + "����ʼ!");
		Connection conn = this.getDao().getConnection();

		int pageIndex = 0; // ����˵�Ķ�����0ҳ����
		boolean hasNextPage = true;

		for (int k = 0; k < 10;)
		{
			try
			{
				int n = 1;
				while (hasNextPage)
				{

					Date startdate = new Date(new Date().getTime() - daymillis);
					Date enddate = new Date();
					// ������
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
					param.put("uptime_start", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
					param.put("uptime_end", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
					
					Log.info("��" + pageIndex + "ҳ");
					String responseText = Utils.sendbyget(Params.url,
							param,Params.appsecret);
					Log.info("��������Щ����: "
							+ new String(responseText.getBytes(), "GBK"));
					// �ѷ��ص�����ת��json����
					JSONObject responseObj = new JSONObject(responseText);
					try
					{
						String errormessage = responseObj.getJSONObject(
						"error_response").getString("message"); // ���û������try������ִ�гɹ����д�ͻ�ִ�г������
						Log.info(jobName, errormessage);
						break;
					}catch(Exception e)
					{
						
					}
					// ��ҳ��
					String pageTotal = String.valueOf(responseObj.getJSONObject("order_list_get_response").getInt("total_num"));
					Log.info("��ҳ���� " + pageTotal);
					if (pageTotal == null || pageTotal.equals("")
							|| pageTotal.equals("0"))
					{
						break;
					}
					// ����Ԫ��
					JSONArray ordersList = responseObj.getJSONObject("order_list_get_response").getJSONArray("info");
					for (int i = 0; i < ordersList.length(); i++)
					{ // ĳ������
						JSONObject orderInfo = ordersList.getJSONObject(i);
						JSONObject order = orderInfo.getJSONObject("order");
						JSONObject address = orderInfo.getJSONObject("address");
						// �������
						String orderCode = String.valueOf(orderInfo
								.getJSONObject("order").getLong("order_id"));
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
									+ o.getStatus_text() + "��");
							// ��������
							// ����ǵȴ����������������ӿڶ����ɹ�������������Ŀ��
							if ("\u7b49\u5f85\u53d1\u8d27".equals(o
									.getStatus_text()))
							{
								if (!OrderManager.isCheck(jobName, conn,
										orderCode))
								{
									if (!OrderManager.TidLastModifyIntfExists(
											jobName, conn, orderCode,
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
												// û�еȴ������״̬ ����Ҫɾ��δ���������Ŀ��/
												// StockManager.deleteWaitPayStock(jobName,
												// conn,Params.tradecontactid,
												// orderCode,sku);
												long qty = (long) item
														.getAmount();
												// ��ecs_rationconfig���д��ڻ������һ�����ͬ����¼(�������Լ���
												StockManager.addSynReduceStore(
														jobName, conn,
														Params.tradecontactid,
														o.getStatus_text(), o
																.getOrder_id(),
														sku, qty, false);
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
									StockManager.deleteWaitPayStock(jobName,
											conn, Params.tradecontactid,
											orderCode, sku);
									StockManager.addSynReduceStore(jobName,
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

									StockManager.deleteWaitPayStock(jobName,
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

									StockManager.addWaitPayStock(jobName, conn,
											Params.tradecontactid, orderCode, sku,
											qty);
									StockManager.addSynReduceStore(jobName,
											conn, Params.tradecontactid, o
													.getStatus_text(), orderCode.equals("")?o.getOrder_id():orderCode, sku,
											-qty, false);
								
								}

							}
						}

						// �����ǰ����ʱ����ڿ�ʼȡ����ʱ�䣬������´�ȡ����ʱ��(����ȡ�����б�����޸�ʱ��)
						// ����ͬ����������ʱ��

					}
					// �ж��Ƿ�����һҳ
					if ("".equals(pageTotal) || pageTotal == null)
						pageTotal = "0";
					if (pageIndex >= Integer.parseInt((pageTotal.equals("") || pageTotal==null)?"0":pageTotal))
					{
						Log.info("Integer.parseInt: "+Integer.parseInt((pageTotal.equals("") || pageTotal==null)?"0":pageTotal));
						hasNextPage = false;
					}
					else
						pageIndex++;

					n++;


				}	

				Log.info(jobName + "ִ�����!");
				break;

			}catch(Exception e)
			{
				e.printStackTrace();
				if (++k >= 5)
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
	}
}
