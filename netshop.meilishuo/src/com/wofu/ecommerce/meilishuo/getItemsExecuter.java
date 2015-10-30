package com.wofu.ecommerce.meilishuo;

import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.Executer;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.meilishuo.util.CommHelper;
import com.wofu.ecommerce.meilishuo.util.Utils;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;

/**
 * 
 * ȡ����˵��Ʒִ����
 * 
 * @author Administrator
 * 
 */
public class getItemsExecuter extends Executer
{
	private static String jobName = "��ȡ����˵��Ʒ��ҵ";
	private String tradecontactid = "23";
	private static String pageSize = "";
	private static String url = "";
	private static String vcode = "";
	private static String appKey = "";
	private static String appsecret = "";
	private static String token = "";

	public void run()
	{
		Properties prop = StringUtil.getStringProperties(this.getExecuteobj()
				.getParams());
		pageSize = prop.getProperty("pageSize","10");
		vcode = prop.getProperty("vcode");
		url = prop.getProperty("url");
		tradecontactid = prop.getProperty("tradecontactid","23");
		appKey = prop.getProperty("app_key","23");
		appsecret = prop.getProperty("app_sercert","23");

		Connection conn = null;

		try
		{
			conn = this.getDao().getConnection();
			token = PublicUtils.getToken(this.getDao().getConnection(), Integer.parseInt(tradecontactid));
			updateJobFlag(1);
			getAllItems(conn);
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

				if (this.getConnection() != null
						&& !this.getConnection().getAutoCommit())
					this.getConnection().rollback();

				if (this.getExtconnection() != null
						&& !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();

				if (this.getExecuteobj().getSkip() == 1)
				{
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));

			} catch (Exception e1)
			{
				Log.error(jobName, "�ع�����ʧ��");
			}
			Log.error(jobName, "������Ϣ:" + Log.getErrorMessage(e));

			Log.error(jobName, "ִ����ҵʧ�� ["
					+ this.getExecuteobj().getActivetimes() + "] ["
					+ this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			e.printStackTrace();

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

	/*
	 * status=����״̬��1�����ڴ���2������ɹ���3������ʧ�ܡ�
	 */
	private void getAllItems(Connection conn) throws Exception
	{
		int m = 0, n = 0;
		// Date
		// modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		ECSDao dao = new ECSDao(conn);
		String sql = "select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="
				+ tradecontactid;
		int orgid = dao.intSelect(sql);
		Log.info("��ʼȡ����˵��Ʒ��ҵ��ʼ");
		Log.info("orgid:  " + orgid);
		for (int k = 0; k < 10;)
		{
			try
			{
				int pageIndex = 0;
				boolean hasNextpage = true;
				while (hasNextpage)
				{
					// ������
					String apimethod = "meilishuo.items.list.get";
					HashMap<String, String> param = new HashMap<String,String>();
					param.put("method", apimethod);
					param.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
					param.put("format", "json");
					param.put("app_key", appKey);
					param.put("v", "1.0");
					param.put("sign_method", "md5");
					param.put("session",token);
					param.put("page", String.valueOf(pageIndex));
					param.put("page_size", pageSize);
					
					Log.info("��" + pageIndex + "ҳ");
					String responseText = Utils.sendbyget(Params.url,
							param,appsecret);
					Log.info("��������: "+responseText);
					JSONObject responseObj= new JSONObject(responseText);
					//���û�����ݷ����򷵻�һ����
					try
					{
						if(responseObj.getJSONObject("items_list_get_response").getInt("total_num")==0)
						{
							return;
						}	
					}catch(Exception e)
					{
						
					}
					//�������Ļ���
					try
					{
						Log.error(responseObj.optJSONObject("error_response").getInt("code")+"",responseObj.optJSONObject("error_response").getString("message"));
						return;
						//˼·�������ȡ�������ɹ��Ļ����Ͳ���ִ�еڶ����return
					}
					catch (Exception e) 
					{
						
					}
					//ͳ����Ϣ��
					//System.out.println("totalnum:"+Integer.parseInt(responseObj.getJSONObject("items_list_get_response").getString("total_num").trim()));
					int goodsNum;
					try
					{
						goodsNum = Integer.parseInt(responseObj.getJSONObject("items_list_get_response").getString("total_num").trim());
					}
					catch(Exception e)
					{
						goodsNum = responseObj.getJSONObject("items_list_get_response").getInt("total_num");
					}
					int pageTotal= goodsNum>=Integer.parseInt(pageSize)?(goodsNum %Integer.parseInt(pageSize)==0?goodsNum /Integer.parseInt(pageSize):(goodsNum /Integer.parseInt(pageSize)+1)):1;
					Log.info("��ҳ����"+pageTotal);
					if (pageTotal ==0)
					{				
						k=5;  //20150513 16:51
						//k=10; //20150513 17:10
						break;
					}
					//��Ʒ����
					JSONArray items = responseObj.getJSONObject("items_list_get_response").getJSONArray("info");
					for(int i = 0 ; i < items.length() ; i++)
					{
						JSONObject itemInfo = items.getJSONObject(i) ;
						//����˵��Ʒ���
						String itemID = itemInfo.getString("twitter_id");
						//��Ʒ���� 
						String itemName = new String(itemInfo.getString("goods_title").getBytes(),"gbk");
						//����
						String goods_no =itemInfo.getString("goods_no");
						//��Ʒ���
						String stockCount="";
						//������Ʒ�����дstockconfigsku��
						JSONArray chileItem = itemInfo.getJSONArray("stocks");
						int totalCount = 0;
						for (int j = 0; j < chileItem.length(); j++)
						{
							JSONObject item = chileItem.getJSONObject(j);
							// sku
							String sku = item.optString("sku_id");
							// �ⲿsku
							String goods_code = item.optString("goods_code");  //����ʱ�����ǿյ�
							Log.info("��Ʒ���: " + sku);
							// ���
							stockCount = item.getString("repertory");
							totalCount += Integer.parseInt(stockCount);
							Log.info("��ȡ���µ�SKU: " + sku);
							StockManager.addStockConfigSku(dao, orgid, itemID, sku, goods_code.equals("")?sku:goods_code, Integer.valueOf(stockCount).intValue());
							n++;
						}
						Log.info("tradecontactid:"+Integer
								.parseInt(tradecontactid.trim()));
						StockManager.stockConfig(dao, orgid, Integer
								.parseInt(tradecontactid.trim()), itemID, goods_no.equals("")?"0":goods_no,
								itemName, totalCount);
						m++;
					}
					// �Ƿ�����һҳ
					if (pageIndex < pageTotal-1)
					{
						hasNextpage = true;
						pageIndex++;
						Log.info("ҳ��:" + pageIndex + 1);
					} else
					{
						hasNextpage = false;
					}
					
				}
				Log.info("ȡ������˵����Ʒ��:" + String.valueOf(m) + " ��SKU��:"
						+ String.valueOf(n));
				// ִ�гɹ�����ѭ��
				k=10;
				break;
	
			}catch(Exception e)
			{
				if (++k >= 10)
					throw e;
				if (this.getDao().getConnection() != null
						&& !this.getDao().getConnection().getAutoCommit())
					this.getDao().getConnection().rollback();
				Log.warn(jobName + " ,Զ������ʧ��[" + k + "], 10����Զ�����. "
						+ Log.getErrorMessage(e));
				Thread.sleep(10000L);				
			}
		}
	}

}