package com.wofu.ecommerce.meilishuo;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;

import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.meilishuo.util.CommHelper;
import com.wofu.ecommerce.meilishuo.util.Utils;

public class GetRefundOrders extends Thread 
{
	private static String jobname = "获取美丽说退货单作业";
	private static String lastRefundTime = Params.username+"获取退货订单最新时间";
	private static long datetime = 24*60*60*1000L;
	public void run()
	{
		Log.info(jobname, "启动[" + jobname + "]模块");
		do
		{
			Connection connection = null;
			try
			{
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.meilishuo.Params.dbname);
				lastRefundTime = PublicUtils.getConfig(connection, lastRefundTime, Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT));
				Params.token = PublicUtils.getToken(connection, Integer.parseInt(Params.tradecontactid));
				getRefund(connection);
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
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000 * Params.timeInterval))
				try
				{
					sleep(1000L);
				} catch (Exception e)
				{
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
	public void getRefund(Connection conn) throws Exception
	{
		String sql = "" ;
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		String status = "1" ;//处理状态 1:待处理 2:已处理 已在当当后台审核同意或不同意
		Log.info("获取美丽说退货开始!");
		Date startTime = new Date(Formatter.parseDate(lastRefundTime, Formatter.DATE_TIME_FORMAT).getTime()+1000L);
		Date endTime = new Date(startTime.getTime()+datetime);
		Date modified = Formatter.parseDate(lastRefundTime, Formatter.DATE_TIME_FORMAT);
		int j=0;
		for(int k=0;k<10;)
		{
			try 
			{	
				while(hasNextPage)
				{
					String apimethod = "meilishuo.aftersales.list.get";
					HashMap<String, String> param = new HashMap<String,String>();
					param.put("method", apimethod);
					param.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
					param.put("format", "json");
					param.put("app_key", Params.appKey);
					param.put("v", "1.0");
					param.put("sign_method", "MD5");
					param.put("session", Params.token);
					param.put("page", String.valueOf(pageIndex));
					param.put("apply_stime",Formatter.format(startTime, Formatter.DATE_TIME_FORMAT));
					param.put("apply_etime", Formatter.format(endTime, Formatter.DATE_TIME_FORMAT));
					param.put("page_size", Params.pageSize);
					Log.info("第" + pageIndex + "页");
					String responseText = Utils.sendbyget(Params.url,
							param,Params.appsecret);
					Log.info("result: "+responseText);
					JSONObject responseObj = new JSONObject(responseText);
					int totalNum = responseObj.getJSONObject("aftersales_list_get_response").getInt("total_num");
					if(totalNum==0){
						if(j==0){
							// 如一天之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
							if (Formatter.parseDate(Formatter.format(new Date(),Formatter.DATE_FORMAT),Formatter.DATE_FORMAT)
									.compareTo(Formatter.parseDate(Formatter.format(
							Formatter.parseDate(PublicUtils.getConfig(conn,lastRefundTime,""),
									Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT),Formatter.DATE_FORMAT)) > 0)
							{
								try
								{
									String value = Formatter.format((new Date(
											Formatter.parseDate(
													PublicUtils.getConfig(conn,
															lastRefundTime,
															""),
													Formatter.DATE_TIME_FORMAT)
													.getTime()
													+ datetime)),
											Formatter.DATE_FORMAT)
											+ " 00:00:00";
									PublicUtils.setConfig(conn,
											lastRefundTime, value);
								} catch (JException je)
								{
									Log.error(jobname, je.getMessage());
								}
							}
							
						}
						k=10;
						break;
						
					}
					if(!responseObj.isNull("error_response")){
						String errormessage = responseObj.getJSONObject("error_response").getString("message"); // 如果没错整个try都不会执行成功，有错就
						Log.error(jobname, "获取美丽说退换货订单失败,错误信息:"+errormessage) ;
						k=10;
						break;
					}
					JSONArray info = responseObj.getJSONObject("aftersales_list_get_response").getJSONArray("info");
					//Log.info("info: "+info.toString());
		            for(int i=0;i<info.length();i++)
		            {
		            	Date temp = Formatter.parseDate(info.getJSONObject(i).getString("ctime"), Formatter.DATE_TIME_FORMAT);
						sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";			
			            String sheetid = SQLHelper.strSelect(conn, sql);
			            sql = "insert into ns_Refund(" +
	            		"SheetID , RefundID , Oid , AlipayNo , BuyerNick , Created , Modified , " +
	            		"OrderStatus , Status , GoodStatus ,  HasGoodReturn ,RefundFee, Payment , " +
	            		"Reason,Description , Title , Price , Num , GoodReturnTime , Sid ,  " +
	            		"TotalFee , Iid , OuterIid , OuterSkuId , CompanyName , Address , " +
	            		"ReturnAddress ,InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo) " +
	            		"values('"+sheetid+"','"+sheetid+"','','','"+info.getJSONObject(i).getString("buyer_nick")+"','"+Formatter.format(info.getJSONObject(i).getString("ctime"), Formatter.DATE_TIME_FORMAT)+"','"+Formatter.format(info.getJSONObject(i).getString("ctime"), Formatter.DATE_TIME_FORMAT)+"'," +
	            			   "'"+info.getJSONObject(i).getString("status")+"','"+info.getJSONObject(i).getString("status")+"','"+info.getJSONObject(i).getString("status")+"',"+info.getJSONObject(i).getInt("has_good_return")+","+info.getJSONObject(i).getString("total_fee")+","+info.getJSONObject(i).getString("total_fee")+"," +
	            			   "'"+info.getJSONObject(i).getString("reason")+"','"+info.getJSONObject(i).getString("reason")+"','"+info.getJSONObject(i).getString("goods_title")+"',"+info.getJSONObject(i).getString("total_fee")+","+info.getJSONObject(i).getString("goods_num")+",'"+Formatter.format(info.getJSONObject(i).getString("ctime"), Formatter.DATE_TIME_FORMAT)+"','"+info.getJSONObject(i).getString("sid")+"'," +
	            			   info.getJSONObject(i).getString("total_fee")+",'"+info.getJSONObject(i).getString("refund_id")+"','"+info.getJSONObject(i).getString("oid")+"','"+info.getJSONObject(i).getString("sid")+"','"+info.getJSONObject(i).getString("company_name")+"','"+info.getJSONObject(i).getString("address")+"'," +
	            			   "'"+info.getJSONObject(i).getString("address")+"','','"+info.getJSONObject(i).getString("tid")+"','"+info.getJSONObject(i).getString("buyer_nick")+"','','')";
			            
						Log.info("退货单sql: "+sql) ;
						SQLHelper.executeSQL(conn,sql);
						
						//加入到通知表     退货标志为2
			            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
			                + sheetid +"',2 , '"+Params.tradecontactid+"' , 'yongjun' , getdate() , null) ";				
						//Log.info(sql) ;
						SQLHelper.executeSQL(conn,sql);
						
						Log.info(jobname,"接口单号:"+sheetid+" 退货订单号:"+info.getJSONObject(i).getString("tid")+"，订单更新时间:"+Formatter.format(info.getJSONObject(i).getString("ctime"),Formatter.DATE_TIME_FORMAT));
						conn.commit();
						conn.setAutoCommit(true);
						if(temp.compareTo(modified)>0)
							modified = temp;
		            }
		            int totalPage = Double.valueOf(Math.ceil(Float.valueOf(totalNum)/Integer.parseInt(Params.pageSize))).intValue();
		            Log.info("总页数: "+totalPage);
		            if (totalPage==pageIndex){
		            	hasNextPage = false;
			            k=10;
			            break;
		            }
		            pageIndex++;
		            j++;
				}
				if(modified.compareTo(Formatter.parseDate(lastRefundTime, Formatter.DATE_TIME_FORMAT))>0){
					try{
						String value = Formatter.format(modified, Formatter.DATE_TIME_FORMAT);
						PublicUtils.setConfig(conn, lastRefundTime, value);
					}catch(Exception e){
						Log.error(jobname, "修改最新取退货时间出错");
					}
					
				}
			Log.info(jobname+"任务完成");
			}catch(Exception e)
			{
				e.printStackTrace();
				if (!conn.getAutoCommit())
					try
					{
						conn.rollback();
					}
					catch (Exception e2) { }
				try
				{
					conn.setAutoCommit(true);
				}
				catch (Exception e3) { }
				throw new JSQLException("生成退货 接口数据失败!"+e.getMessage());
			}
		}
	}
}
