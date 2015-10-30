package com.wofu.ecommerce.icbc;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import com.wofu.base.systemmanager.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.icbc.util.CommHelper;
/**
 * 
 * 获取联想加盟店退换货单作业
 *
 */
public class GetRefundOrders extends Thread {

	private static String jobName = "获取联想加盟店退换货单作业";
	private static long daymillis=24*60*60*1000L;
	private static String lasttime="";
	private static final String lastReturnValue= Params.username+"取退货订单最新时间";
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	public void run() {
		
		Log.info(jobName, "启动[" + jobName + "]模块");
		do {
			Connection connection = null;
			
			try {
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.icbc.Params.dbname);
				lasttime=PublicUtils.getConfig(connection, lastReturnValue,"");// Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT));
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
		int pageIndex=1;
		String resultText = "" ;
		boolean hasNextPage=true;
		long modified = Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT).getTime()/1000L;
		for(int k=0;k<5;)
		{
			try 
			{	
				int n=1;
				while(hasNextPage){
					long startdate=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()/1000+1L;
					long enddate=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()/1000+daymillis;
					//方法名
					String apimethod="get_refund_list.php";
					HashMap<String,Object> map = new HashMap<String,Object>();
					map.put("start_time",startdate);
			        map.put("end_time",enddate);
			        map.put("page", String.valueOf(pageIndex));
			        map.put("limit", Params.pageSize);
			        map.put("apimethod", apimethod);
			        //map.put("key", MD5Util.getMD5Code((Params.vcode+startdate).getBytes()));
			        //发送请求
					String responseText = CommHelper.doPost(map,Params.url);
					Log.info("返回数据为: "+responseText);
						//把返回的数据转成json对象
						JSONObject responseObj= new JSONObject(responseText);
						if(responseObj.getInt("status")==5){   //code为1代表没有数据返回
							Log.info("本次取不到退货订单");
							if (n==1)		
							{
								try
								{
									//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
									if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
											compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lastReturnValue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
									{
										try
					                	{
											String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lastReturnValue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
											PublicUtils.setConfig(conn, lastReturnValue, value);			    
					                	}catch(JException je)
					                	{
					                		Log.error(jobName, je.getMessage());
					                	}
									}
								}catch(ParseException e)
								{
									Log.error(jobName, "不可用的日期格式!"+e.getMessage());
								}
							}
							break;
						}
						//错误对象 
						if(responseObj.getInt("status")!=1){   //发生错误
							int operCode= responseObj.getInt("status");
								Log.error("联想加盟店获取退货订单", "获取退货订单失败,operCode:"+operCode);
							return;
							
						}
						
						//总页数
						int  pageTotal = responseObj.getInt("total_page");
						Log.info("总页数： "+pageTotal);
						if (pageTotal==0)
						{				
							if (n==1)		
							{
								try
								{
									//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
									if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
											compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lastReturnValue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
									{
										try
					                	{
											String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lastReturnValue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
											PublicUtils.setConfig(conn, lastReturnValue, value);			    
					                	}catch(JException je)
					                	{
					                		Log.error(jobName, je.getMessage());
					                	}
									}
								}catch(ParseException e)
								{
									Log.error(jobName, "不可用的日期格式!"+e.getMessage());
								}
							}
							break;
						}
						
						JSONArray ReturnCodeList = responseObj.getJSONArray("list");
						for(int i = 0 ; i < ReturnCodeList.length() ; i++)
						{	RefundOrder o=null;
							try{
								String orderCode=ReturnCodeList.getJSONObject(i).getString("order_sn");
								o = OrderUtils.getRefundOrderByCode( orderCode, Params.url);
								OrderUtils.createRefundOrder( jobName,conn, Params.tradecontactid, o) ;
							}catch(Exception ex){
								if(conn!=null && !conn.getAutoCommit()){
									conn.rollback();
								}
								Log.error(jobName, ex.getMessage());
								continue;
								
							}
							if(o.getRefund_time()>modified) modified=o.getRefund_time();
							
						}
						if(pageIndex >= pageTotal-1)
							hasNextPage = false ;
						else
							pageIndex ++ ;
						
						n++;
						
				}
				if(modified>Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()/1000L){
					try{
						String value = Formatter.format(new Date(modified*1000L), Formatter.DATE_TIME_FORMAT);
						PublicUtils.setConfig(conn, lastReturnValue, value);
					}catch(Exception e){
						Log.error(jobName, e.getMessage());
					}
					
				}//获取到退货订单号
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
		Log.info("本次取退货订单完毕");
	}
}