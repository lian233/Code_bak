package com.wofu.ecommerce.weidian;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weidian.utils.Utils;
import com.wofu.ecommerce.weidian.utils.getToken;

public class OrderDelivery extends Thread
{
	private static String jobname = "微店订单发货处理作业";

	private boolean is_exporting = false;

	@Override
	public void run()
	{
		Log.info(jobname, "启动[" + jobname + "]模块");
		do
		{
			Connection connection = null;
			is_exporting = true;
			try
			{
				connection = PoolHelper.getInstance().getConnection(
						Params.dbname);
				delivery(connection);

				// modifiRemark(connection);

			} catch (Exception e)
			{
				try
				{
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1)
				{
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally
			{
				is_exporting = false;
				try
				{
					if (connection != null)
						connection.close();
				} catch (Exception e)
				{
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (Params.waittime*60*1000))
				try
				{
					sleep(1000L);
				} catch (Exception e)
				{
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	private void delivery(Connection conn) throws Exception
	{

		String sql = "select  a.sheetid,b.tid, upper(ltrim(rtrim(b.companycode))) companycode,"
				+ "upper(ltrim(rtrim(b.outsid))) outsid from it_upnote a with(nolock), ns_delivery b with(nolock)"
				+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
				+ Params.tradecontactid + "' and b.iswait=0";
		Vector vdeliveryorder = SQLHelper.multiRowSelect(conn, sql);
		Log.info("本次要处理的订单发货条数为: " + vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++)
		{
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderid = hto.get("tid").toString(); // 订单号 网店制定的外部订单号
			String post_company = hto.get("companycode").toString().trim();
			String post_no = hto.get("outsid").toString();
			// 如果物流公司为空则忽略处理
			if (post_company.trim().equals(""))
			{
				Log.warn(jobname, "快递公司为空！订单号:" + orderid + "");
				continue;
			}
			String postcompanyid = Params.htComCode.get(post_company).toString();
			if (postcompanyid.equals(""))
			{
				// 如果物流公司为空则忽略处理
				if (post_company.trim().equals(""))
				{
					Log.warn(jobname, "快递公司未配置！快递公司：" + post_company + " 订单号:"
							+ orderid + "");
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
							+ " where SheetID = '"
							+ sheetid
							+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='" + sheetid
							+ "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
					continue;
				}
			}
			if ("".equals(post_no))
			{
				Log.warn(jobname, "快递单号未配置，快递公司：" + post_company + " 订单号:"
						+ orderid + "");
				conn.setAutoCommit(false);

				sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"
						+ sheetid
						+ "' and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);

				sql = "delete from IT_UpNote where SheetID='" + sheetid
						+ "' and sheettype=3";

				SQLHelper.executeSQL(conn, sql);
				conn.commit();
				conn.setAutoCommit(true);
				continue;
			}
			if ("".equals(postcompanyid))
			{
				Log.warn(jobname, "快递公司编号未配置，快递公司：" + post_company + " 订单号:"
						+ orderid + "");
				continue;
			}
			JSONObject param_Object = new JSONObject();
			JSONObject public_Object = new JSONObject();
			param_Object.put("order_id", orderid);
			param_Object.put("express_type", postcompanyid);
			param_Object.put("express_no", post_no);
			public_Object.put("method", "vdian.order.deliver");
			public_Object.put("access_token", getToken.getToken_zy(conn)); //写个方法用于获取access_token
			public_Object.put("version", "1.0"); 
			public_Object.put("format", "json"); 
			String opt_to_sever = Params.url + "?param=" + URLEncoder.encode(param_Object.toString(),"UTF-8") + "&public=" + URLEncoder.encode(public_Object.toString(),"UTF-8");
			String responseOrderListData = Utils.sendbyget(opt_to_sever);
			JSONObject responseproduct = new JSONObject(responseOrderListData);
			// /*这些判断语句一定要参考GetOrders进行修改，不能因为没有报错就随便他*/
			JSONObject result =responseproduct.getJSONObject("status");
			int errorCode = result.getInt("status_code");
			if (errorCode!=0)
			{
				String errdesc = "";
				errdesc = errdesc + " "
						+ result.getString("status_reason");
				Log.warn("订单发货失败,订单号:[" + orderid + "],快递公司:[" + post_company
						+ "],快递单号:[" + post_no + "] 错误信息:" + errdesc);
				if(20017==result.getInt("status_code"))
				{
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
							+ " where SheetID = '"
							+ sheetid
							+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='" + sheetid
							+ "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
				}
				continue;
			}

			try
			{
				conn.setAutoCommit(false);

				sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"
						+ sheetid
						+ "' and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);

				sql = "delete from IT_UpNote where SheetID='" + sheetid
						+ "' and sheettype=3";

				SQLHelper.executeSQL(conn, sql);
				conn.commit();
				conn.setAutoCommit(true);
			} catch (SQLException sqle)
			{
				if (!conn.getAutoCommit())
					try
					{
						conn.rollback();
					} catch (Exception e1)
					{
					}
				try
				{
					conn.setAutoCommit(true);
				} catch (Exception e1)
				{
				}
				throw new JSQLException(sql, sqle);
			}
			Log.info(jobname, "处理订单【" + orderid + "】发货成功,快递公司【" + post_company
					+ "】,快递单号【" + post_no + "】");

		}
	}

	@Override
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
