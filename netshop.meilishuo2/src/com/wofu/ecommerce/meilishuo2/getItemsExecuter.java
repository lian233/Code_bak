package com.wofu.ecommerce.meilishuo2;

import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Date;
import java.util.Properties;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.Executer;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.meilishuo2.util.Utils;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;

/**
 * 
 * 取美丽说商品执行器
 * 
 * @author Administrator
 * 
 */
public class getItemsExecuter extends Executer
{
	private static String jobName = "获取美丽说商品作业";
	private String tradecontactid = "23";
	private static String pageSize = "";
	private static String url = "";
	private static String vcode = "";
	private static String appKey = "";
	private static String appsecret = "";

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
			updateJobFlag(1);
			getAllItems(conn);
			UpdateTimerJob();
			Log.info(jobName, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes()
					+ "] ["
					+ this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
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
				Log.error(jobName, "回滚事务失败");
			}
			Log.error(jobName, "错误信息:" + Log.getErrorMessage(e));

			Log.error(jobName, "执行作业失败 ["
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
				Log.error(jobName, "更新处理标志失败");
			}

			try
			{
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();

			} catch (Exception e)
			{
				Log.error(jobName, "关闭数据库连接失败");
			}
		}

	}

	/*
	 * status=处理状态。1：正在处理；2：处理成功；3：处理失败。
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
		Log.info("开始取美丽说商品作业开始");
		Log.info("orgid:  " + orgid);
		for (int k = 0; k < 10;)
		{
			try
			{
				int pageIndex = 0;
				boolean hasNextpage = true;
				while (hasNextpage)
				{
					// 方法名
					String apimethod = "meilishuo.items.list.get";
					JSONObject object=new JSONObject(PublicUtils.getConfig(conn, "美丽说Token信息2", "")); //获取最新的Token
					Log.info("token: "+object.toString());
					String responseText = Utils.sendbyget(url, appKey, appsecret, apimethod, object.optString("access_token"), new Date(), "");
					Log.info("返回数据: "+responseText);
					JSONObject responseObj= new JSONObject(responseText);
					//如果没有数据返回则返回一个空
					try
					{
						if(responseObj.getJSONObject("items_list_get_response").getInt("total_num")==0)
						{
							return;
						}	
					}catch(Exception e)
					{
						
					}
					//如果错误的话：
					try
					{
						Log.error(responseObj.optJSONObject("error_response").getInt("code")+"",responseObj.optJSONObject("error_response").getString("message"));
						return;
						//思路，如果获取错误代码成功的话，就不会执行第二句的return
					}
					catch (Exception e) 
					{
						
					}
					//统计信息：
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
					if (pageTotal ==0)
					{				
						k=5;  //20150513 16:51
						//k=10; //20150513 17:10
						break;
					}
					//商品集合
					JSONArray items = responseObj.getJSONObject("items_list_get_response").getJSONArray("info");
					for(int i = 0 ; i < items.length() ; i++)
					{
						JSONObject itemInfo = items.getJSONObject(i) ;
						//美丽说商品编号
						String itemID = itemInfo.getString("twitter_id");
						//商品标题 
						String itemName = new String(itemInfo.getString("goods_title").getBytes(),"gbk");
						//货号
						String goods_no =itemInfo.getString("goods_no");
						//商品库存
						String stockCount="";
						//有子商品情况下写stockconfigsku表
						JSONArray chileItem = itemInfo.getJSONArray("stocks");
						int totalCount = 0;
						for (int j = 0; j < chileItem.length(); j++)
						{
							JSONObject item = chileItem.getJSONObject(j);
							// sku
							String sku = item.optString("sku_id");
							// 外部sku
							String goods_code = item.optString("goods_code");  //更多时候它是空的
							Log.info("产品编号: " + sku);
							// 库存
							stockCount = item.getString("repertory");
							totalCount += Integer.parseInt(stockCount);
							Log.info("获取到新的SKU: " + sku);
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
					// 是否还有下一页
					if (pageIndex < pageTotal - 1)
					{
						hasNextpage = true;
						pageIndex++;
						Log.info("页数:" + pageIndex + 1);
					} else
					{
						hasNextpage = false;
					}
					System.out.println("mVal:"+m+"  "+"json_length:"+items.length());
					if(m>=items.length())
					{
						k=11;
						hasNextpage = false;
						break;
					}
					Log.info("取到美丽说总商品数:" + String.valueOf(m) + " 总SKU数:"
							+ String.valueOf(n));
					// 执行成功后不再循环
					break;
				}
	
			}catch(Exception e)
			{
				if (++k >= 10)
					throw e;
				if (this.getDao().getConnection() != null
						&& !this.getDao().getConnection().getAutoCommit())
					this.getDao().getConnection().rollback();
				Log.warn(jobName + " ,远程连接失败[" + k + "], 10秒后自动重试. "
						+ Log.getErrorMessage(e));
				Thread.sleep(10000L);				
			}
		}
	}

}