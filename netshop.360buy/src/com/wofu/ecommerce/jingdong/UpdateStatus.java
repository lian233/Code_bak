package com.wofu.ecommerce.jingdong;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.request.after.AfterStateUpdateRequest;
import com.jd.open.api.sdk.response.after.AfterStateUpdateResponse;
import com.wofu.ecommerce.jingdong.Params;
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

public class UpdateStatus extends Thread {
	
	private static String jobName = "京东订单状态更新作业";
	
//	api V2
	private static String SERVER_URL = Params.SERVER_URL ;
	private static String token = Params.token ;
	private static String appKey = Params.appKey ;
	private static String appSecret = Params.appSecret ;
	public void run() {
		Log.info(jobName, "启动[" + jobName + "]模块");
		do {
			Connection connection = null;
	
			try {		
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.jingdong.Params.dbname);
				
				//确认审核订单
				doUpdateCheckStatus(connection,Params.tradecontactid);
				//确认退货订单
				doUpdateRefundOrders(connection,Params.tradecontactid);
				
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "回滚事务失败");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
			
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.jingdong.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	//订单审核后，更新京东订单状态为：订单sop出库
	private static void doUpdateCheckStatus(Connection conn ,String tradecontactid) throws Exception
	{
		Vector vts=IntfUtils.getUpNotes(conn, tradecontactid, "1");
		for (int i=0;i<vts.size();i++)
		{
			Hashtable hts=(Hashtable) vts.get(i);
			String sheetid=hts.get("sheetid").toString();
			String sql="select tid from ns_delivery with(nolock) where sheetid='"+sheetid+"'";
			String tid=SQLHelper.strSelect(conn, sql);
			
			try
			{
				IntfUtils.backupUpNote(conn, "yongjun",sheetid, "1");
				Log.info("更新审核状态成功,单号:" + tid);
				conn.close();
			}
			catch(Exception je)
			{
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				//throw new JException(je.getMessage()+" 单号:"+tid+" 更新状态:1");
				Log.error(jobName+" 单号:"+tid+" 更新状态:1", je.getMessage());
			}
		}
	}

	private static void doUpdateRefundOrders(Connection conn ,String tradecontactid) throws Exception
	{
		Vector vts=IntfUtils.getUpNotes(conn, tradecontactid, "2");
		for (int i=0;i<vts.size();i++)
		{
			Hashtable hts=(Hashtable) vts.get(i);
			String sheetid=hts.get("sheetid").toString();
			//取得京东取货单号
			String sql="select refundID from ns_refund with(nolock) where sheetid='"+sheetid+"'";
			String tid=SQLHelper.strSelect(conn, sql);
			try
			{
				JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
				AfterStateUpdateRequest request = new AfterStateUpdateRequest();
				request.setReturnId("299054");
				request.setTradeNo(Long.toString(System.currentTimeMillis()));
				AfterStateUpdateResponse response = client.execute(request);
				if("0".equals(response.getCode()))
				{
					IntfUtils.backupUpNote(conn, "yongjun",sheetid, "2");
					Log.info("退货收货确认更新状态成功,单号:" + tid);
					conn.close();
				}
				else
				{
					Log.error(jobName, "退货收货确认更新状态失败,单号:" + tid + "。错误代码：" + response.getZhDesc()) ;
				}

			}
			catch(Exception je)
			{
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				//throw new JException(je.getMessage()+" 单号:"+tid+" 更新状态:2");
				Log.error(jobName+" 单号:"+tid+" 更新状态:2", je.getMessage());
			}
		}
	}
}