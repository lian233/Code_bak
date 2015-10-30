package com.wofu.ecommerce.maisika;


import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;



import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;

import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;

import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.maisika.OrderUtils;
import com.wofu.ecommerce.maisika.ReturnOrder;
import com.wofu.ecommerce.maisika.util.CommHelper;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;

public class GetRefund extends Thread {

	private static String jobName = "获取麦斯卡退货作业";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"取退货单最新时间";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;


	public GetRefund() {
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
						com.wofu.ecommerce.maisika.Params.dbname);
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
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.maisika.Params.waittime * 1000*5))		
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
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					LinkedHashMap<String,Object> map = new LinkedHashMap<String,Object>();
					map.put("&op","refund");
			        map.put("service","refund");
			        map.put("vcode", Params.vcode);
			        map.put("mtime_start", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        map.put("mtime_end", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        map.put("page", String.valueOf(pageno));
			        map.put("page_size", Params.pageSize);
			        map.put("status", "3");
			        //发送请求
			        Log.info("第"+String.valueOf(pageno)+"页");

					String responseOrderListData = CommHelper.doGet(map,Params.url);
			        //Log.info("退货: "+responseOrderListData);
					JSONObject responseproduct=new JSONObject(responseOrderListData);
					
					int totalCount=responseproduct.getInt("counts");
//					
					int i=1;
//								
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
										System.out.println("无退货单");
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
					
					
					JSONArray refundlist=responseproduct.getJSONArray("refund_list");
					for(int j=0;j<refundlist.length();j++)
					{
						try{
							JSONObject refund=refundlist.getJSONObject(j);//外面杂乱的信息
							JSONArray orderlist = refund.getJSONArray("extend_order_goods");
							ReturnOrder r=new ReturnOrder();
							r.setObjValue(r, refund);
							r.setFieldValue(r, "returnItemList", orderlist);
							System.out.println("数组长度"+r.getReturnItemList().size());
							String express_company = "";
//							String express_company=responseproduct.getJSONArray("refund_list").getJSONObject(j).getJSONObject("express_company").optString("e_name");
							for(Iterator ito=r.getReturnItemList().getRelationData().iterator();ito.hasNext();)
							{
							ReturnOrderItem item=(ReturnOrderItem) ito.next();
							//添加时间
							Log.info("订单ID:"+r.getOrder_sn()+" 退货状态:"+r.getRefund_state()+" 退货时间申请:"+Formatter.format(new Date(r.getAdd_time()*1000L),Formatter.DATE_TIME_FORMAT));
					
							/*
							 *1、如果状态为等待卖家发货则生成接口订单
							 *2、删除等待买家付款时的锁定库存 
							*/		

							if (!OrderManager.RefundIntfExists("检查麦斯卡退货单", conn, r.getOrder_sn(),r.getRefund_sn()))
							{
								OrderUtils.createRefund(conn,r,item,
										Integer.valueOf(Params.tradecontactid).intValue(),express_company);
							
							}
									
							}
							//更新同步订单最新时间
			                if (new Date(r.getAdd_time()*1000L).compareTo(modified)>0)
			                {
			                	modified=new Date(r.getAdd_time()*1000L);
			                }
						}catch(Exception e){
							Log.error(jobName, e.getMessage());
							continue;
						}
						
					}
						
						
						
					//判断是否有下一页
					if (pageno==(Double.valueOf(Math.ceil(i/20.0))).intValue()) break;
					
					pageno++;
					
					i=i+1;
				}
				
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{	
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            		System.out.println("更新退货单获取时间"+value);
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
