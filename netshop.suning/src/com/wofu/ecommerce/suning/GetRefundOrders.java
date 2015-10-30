package com.wofu.ecommerce.suning;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.suning.util.CommHelper;
/**
 * 
 * 获取苏宁退换货单作业
 *
 */
public class GetRefundOrders extends Thread {

	private static String jobName = "获取苏宁退换货单作业";
	private static long daymillis=24*60*60*1000L;

	public GetRefundOrders() {
		setDaemon(true);
		setName(jobName);
	}

	public void run() {
		
		Log.info(jobName, "启动[" + jobName + "]模块");
		do {
			Connection connection = null;

			try {
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.suning.Params.dbname);
				SuNing.setCurrentDate_getRefundOrder(new Date());
				getRefund(connection) ;
				
			} catch (Exception e) {
				try {
					e.printStackTrace() ;
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
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000 * Params.timeInterval))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	

	public void getRefund(Connection conn) throws Exception
	{
		String resultText = "" ;
		for(int k=0;k<5;)
		{
			try 
			{	
					//获取到退货订单号
					String apiMethod="suning.custom.batchrejectedOrd.query";
					 HashMap<String,String> reqMap = new HashMap<String,String>();
					 reqMap.put("startTime", Formatter.format(new Date(System.currentTimeMillis()-daymillis), Formatter.DATE_TIME_FORMAT));
				     reqMap.put("endTime",Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT) );
				     String ReqParams = CommHelper.getJsonStr(reqMap, "batchQueryRejectedOrd");
				     HashMap<String,Object> map = new HashMap<String,Object>();
				     map.put("appSecret", Params.appsecret);
				     map.put("appMethod", apiMethod);
				     map.put("format", Params.format);
				     map.put("versionNo", "v1.2");
				     map.put("appRequestTime", CommHelper.getNowTime());
				     map.put("appKey", Params.appKey);
				     map.put("resparams", ReqParams);
				     //发送请求
					 String responseText = CommHelper.doRequest(map,Params.url);
					Log.info("退换货数据: "+responseText);
					//把返回的数据转成json对象
					JSONObject responseObj= new JSONObject(responseText).getJSONObject("sn_responseContent");
					//错误对象 
					if(responseText.indexOf("sn_error")!=-1){   //发生错误
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if(!"".equals(operCode))
						{
							Log.error("苏宁获取退货订单", "获取退货订单失败,operCode:"+operCode);
						}
						return;
						
					}
					
					JSONArray ReturnCodeList = responseObj.getJSONObject("sn_body").getJSONArray("batchQueryRejectedOrd");
					for(int i = 0 ; i < ReturnCodeList.length() ; i++)
					{	
						try{
							String orderCode=ReturnCodeList.getJSONObject(i).getString("orderCode");
							Order o = OrderUtils.getOrderByCode(Params.url,orderCode,Params.session,Params.appKey,Params.appsecret);
							OrderUtils.createRefundOrder("生成苏宁退换货接口订单", conn, Params.tradecontactid, o,Params.url,Params.appKey,Params.appsecret,Params.format) ;
						}catch(Exception ex){
							if(conn!=null && !conn.getAutoCommit()){
								conn.rollback();
							}
							Log.error(jobName, ex.getMessage());
							continue;
							
						}
						
					}
					
				break;
				
			}catch (Exception e) 
			{
				if (++k >= 5)
					throw e;
				if(conn!=null && !conn.getAutoCommit()){
					conn.rollback();
				}
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
	}
}