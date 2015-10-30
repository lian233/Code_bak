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
 *检查未入订单
 *检查取消订单
 *
 */
public class RefreTokenExecuter extends Executer
{
	private static String jobName="定时刷新token";
	private String appkey="";
	private String appsecret="";
	private String url="";
	private int tradecontactid;
	
	@Override
	public void run()
	{
		Properties prop = StringUtil.getStringProperties(this.getExecuteobj()
				.getParams());
		appkey = prop.getProperty("appkey");
		appsecret = prop.getProperty("appsecret");
		tradecontactid = Integer.parseInt(prop.getProperty("tradecontactid"));
		url =prop.getProperty("url");
		try
		{
			// 检查未入订单
			updateJobFlag(1);
			String refretoken = PublicUtils.getRefreToken(this.getDao().getConnection(), tradecontactid);
			refreToken(refretoken);
			// 检查取消订单
			// checkCancleOrders();

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
				Log.error(jobName, "回滚事务失败");
			}
			Log.error(jobName, "错误信息:" + Log.getErrorMessage(e));

			Log.error(jobName, "执行作业失败 ["
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
	//刷新token
	private void refreToken(String refretoken) throws Exception{
		JSONObject result = CommHelper.refreshToken(refretoken, appkey, appsecret,url);
		System.out.println("result: "+result);
		String token = result.optString("access_token");
		String refreshToken = result.optString("refresh_token");
		String sql ="update ecs_org_params set token='"+token+"',refreshtoken='"+refreshToken+
		"' from ecs_tradecontactorgcontrast a where a.orgid=ecs_org_params.orgid and a.tradecontactid="+tradecontactid;
		this.getDao().execute(sql);
		Log.info("刷新美丽说token成功,token:　"+token+",refreshtoken: "+refreshToken);
		
		
	}
	
}
