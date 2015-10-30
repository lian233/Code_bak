package com.wofu.ecommerce.rke;


import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;

import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;

import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.rke.utils.Utils;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;

public class getRefund extends Thread {

	private static String jobName = "获取麦斯卡经销退货作业";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"取退货单最新时间";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;


	public getRefund() {
		setDaemon(true);
		setName(jobName);
	}

	public void run() {
		Log.info(jobName, "启动[" + jobName + "]模块");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.rke.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				getRefundList(connection);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobName, "回滚事务失败");
				}
				Log.error("105", jobName, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobName, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.rke.Params.waittime * 1000*5))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	
	/*
	 * 获取一天之类的所有退货单
	 */
	private void getRefundList(Connection conn) throws Exception
	{		
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				while(true)
				{
					
					Map<String, String> refundlistparams = new HashMap<String, String>();
			        //系统级参数设置
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					
					refundlistparams.put("appKey", Params.app_key);
					refundlistparams.put("sessionKey", "");
					refundlistparams.put("format", Params.format);
					refundlistparams.put("method", "yhd.refund.get");
					refundlistparams.put("ver", Params.ver);
					refundlistparams.put("dateType", "5");
					refundlistparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
					refundlistparams.put("curPage", String.valueOf(pageno));
					refundlistparams.put("pageRows", "50");
			        refundlistparams.put("startTime", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        refundlistparams.put("endTime", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        refundlistparams.put("dateType",String.valueOf(1));
			        
			        String responseOrderListData = Utils.sendByPost(refundlistparams, Params.url);
			        //Log.info("退货: "+responseOrderListData);
					JSONObject responseproduct=new JSONObject(responseOrderListData);
					
					if (responseOrderListData.indexOf("errInfoList")>=0)
					{
						JSONArray errinfolist=responseproduct.getJSONObject("response").optJSONObject("errInfoList").optJSONArray("errDetailInfo");
						String errdesc="";
						
						for(int j=0;j<errinfolist.length();j++)
						{
							JSONObject errinfo=errinfolist.getJSONObject(j);
							
							errdesc=errdesc+" "+errinfo.getString("errorDes"); 
												
						}
						
						Log.error(jobName, "取退货单列表失败:"+errdesc);
						
						if (errdesc.indexOf("根据指定的参数查不到相应的退货信息")>=0)
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
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
						if(errdesc.indexOf("该接口调用成功率过低，被禁用")>=0){
							Log.info("接口调用过于频繁！");
							break;
						}
						k=10;
						break;
					}
					
					int totalCount=responseproduct.getJSONObject("response").getInt("totalCount");
					int errorCount=responseproduct.getJSONObject("response").getInt("errorCount");
					
					if (errorCount>0)
					{
						String errdesc="";
						JSONArray errlist=responseproduct.getJSONObject("response").getJSONObject("errInfoList").getJSONArray("errDetailInfo");
						for(int j=0;j<errlist.length();j++)
						{
							JSONObject errinfo=errlist.getJSONObject(j);
							
							errdesc=errdesc+" "+errinfo.getString("errorDes"); 
												
						}
						
						if (errdesc.indexOf("退货列表信息不存在")<0)
						{
							k=10;
							throw new JException(errdesc);
						}
					}
					
										
					
					int i=1;
			
			
								
					if (totalCount==0)
					{				
						if (i==1)		
						{
							try
							{
								//如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
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
						k=10;
						break;
					}
					
					
					JSONArray refundlist=responseproduct.getJSONObject("response").getJSONObject("refundList").getJSONArray("refund");
					
					
					for(int j=0;j<refundlist.length();j++)
					{
						try{
							JSONObject refund=refundlist.getJSONObject(j);
							
							Map<String, String> orderparams = new HashMap<String, String>();
					        //系统级参数设置
							orderparams.put("appKey", Params.app_key);
							orderparams.put("sessionKey", "");
							orderparams.put("format", Params.format);
							orderparams.put("method", "yhd.refund.detail.get");
							orderparams.put("ver", Params.ver);
							orderparams.put("dateType", "5");
							orderparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
					        
					    
							orderparams.put("refundCode", refund.getString("refundCode"));
					     
					        
					        
							String responseData =Utils.sendByPost(orderparams, Params.url);
							//Log.info("responseData: "+responseData);

							JSONObject responseorder=new JSONObject(responseData);
							
							int errorOrderCount=responseorder.getJSONObject("response").getInt("errorCount");
							
							if (errorOrderCount>0)
							{
								String errdesc="";
								JSONArray errlist=responseorder.getJSONObject("response").getJSONObject("errInfoList").getJSONArray("errDetailInfo");
								for(int n=0;n<errlist.length();n++)
								{
									JSONObject errinfo=errlist.getJSONObject(n);
									
									errdesc=errdesc+" "+errinfo.getString("errorDes"); 
														
								}
								
								k=10;
								throw new JException(errdesc);						
							}
							
							
							JSONObject refunddetail=responseorder.getJSONObject("response").getJSONObject("refundInfoMsg").getJSONObject("refundDetail");
							
							
							RefundDetail r=new RefundDetail();
							r.setObjValue(r, refunddetail);
											
							
							JSONArray refundItemList=responseorder.getJSONObject("response").getJSONObject("refundInfoMsg").getJSONObject("refundItemList").getJSONArray("refundItem");
							
							r.setFieldValue(r, "refundItemList", refundItemList);
							
							//退货状态(0:待审核;3:客服仲裁;4:已拒绝;11:退货中-待顾客寄回;12:退货中-待确认退款;13:换货中;27:退款完成;33:换货完成;34:已撤销;40:已关闭)
					
							Log.info(r.getOrderCode()+" "+r.getRefundStatus()+" "+Formatter.format(r.getApplyDate(),Formatter.DATE_TIME_FORMAT));
							/*
							 *1、如果状态为等待卖家发货则生成接口订单
							 *2、删除等待买家付款时的锁定库存 
							 */		
							String sku;
							String sql="";
					
							if (!OrderManager.RefundIntfExists("检查麦斯卡经销退货单", conn, r.getOrderCode(),r.getRefundCode()))
							{
								//OrderUtils.createRefund(conn,r,
										//Integer.valueOf(Params.tradecontactid).intValue(),Params.app_key,"",Params.format,Params.ver);
							
							}
									
							
							//更新同步订单最新时间
			                if (r.getApplyDate().compareTo(modified)>0)
			                {
			                	modified=r.getApplyDate();
			                }
						}catch(Exception e){
							Log.error(jobName, e.getMessage());
							continue;
						}
						
					}
						
						
						
					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(totalCount/50.0))).intValue()) break;
					
					pageno++;
					
					i=i+1;
				}
				
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobName,je.getMessage());
	            	}
				}
				//执行成功后不再循环
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn(jobName+", 远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
	public String toString()
	{
		return jobName + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
