package com.wofu.ecommerce.ecshop;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import sun.misc.GC.LatencyRequest;

import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.Executer;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ecshop.util.CommHelper;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
/**
 * 取ecshop商品执行器
 * @author Administrator
 *
 */
public class GetItems extends Thread {
	private static String jobName = "获取ecshop商品作业";
	private String tradecontactid="";
	private static String lasttimeconfvalue = Params.username+"获取ecshop最新修改商品";
	private static String lasttime = "";
	private static long daymillis = 24*60*60*60*1000L;
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	public void run() {

		Log.info(jobName, "启动[" + jobName + "]模块");
		do {
			Connection connection = null;
			try {
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	

				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
				/**
				 * 订单状态 10待发货，20已发货，21部分发货，30交易成功 ，40交易关闭
				 */
				//获取ecshop新订单 
				getAllItems(connection) ;
				
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
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobName, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	/*
	 * status=处理状态。1：正在处理；2：处理成功；3：处理失败。
	 */
	private void getAllItems(Connection conn) throws Exception
	{
		int m=0,n=0;
		//Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		ECSDao dao=new ECSDao(conn);
		
		Log.info("开始取ecshop商品作业开始");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+Params.tradecontactid;
		int orgid=SQLHelper.intSelect(conn,sql);
		//当前最新商品修改时间
		long modifiedDate = Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT).getTime()/1000L;
		for(int k=0;k<10;)
		{
			
			try 
			{

				int pageIndex = 1 ;
				boolean hasNextpage = true ;
				
				while(hasNextpage)
				{
					//方法名
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
					//方法名
					String apimethod="search_goods_list";
					HashMap<String,Object> reqMap = new HashMap<String,Object>();
			        reqMap.put("last_modify_st_time", startdate.getTime()/1000L);
			        reqMap.put("last_modify_en_time",enddate.getTime()/1000L );
			        reqMap.put("pages", String.valueOf(pageIndex));
			        reqMap.put("counts", Params.pageSize);
			        reqMap.put("return_data", "json");
			        reqMap.put("act", apimethod);
			        reqMap.put("api_version", "1.0");
			        //发送请求
			        
			        Log.info("第"+pageIndex+"页");
					String responseText = CommHelper.doRequest(reqMap,Params.url);
					Log.info("返回数据为:　"+responseText);
					//把返回的数据转成json对象
					JSONObject responseObj= new JSONObject(responseText);
					if(!"success".equals(responseObj.getString("result"))){   //发生错误
						String operCode = responseObj.getJSONObject("sn_error").getString("error_code");
						if("biz.handler.data-get:no-result".equals(operCode)){ //没有结果
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
								return;
							}catch(ParseException e)
							{
								Log.error(jobName, "不可用的日期格式!"+e.getMessage());
							}
							Log.info("没有可用的商品!");
						}else{
							Log.warn("取订单出错了,错误码: "+operCode);
						}
						
						break;
					}
					
					
					//总页数
					String pageTotal ="";
					JSONObject itemInfos =responseObj.getJSONObject("info");
					String itemTotal = itemInfos.getString("counts");
					Log.info("总商品数为： "+itemTotal);
					if (itemTotal==null || itemTotal.equals("") || itemTotal.equals("0"))
					{				
						if (n==1)		
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
						break;
					}
					int itemTotaltemp=Integer.parseInt(itemTotal);
					int pageSizeTemp = Integer.parseInt(Params.pageSize);
					pageTotal=String.valueOf(itemTotaltemp<pageSizeTemp?1:(itemTotaltemp/pageSizeTemp+itemTotaltemp%pageSizeTemp));
					//商品集合
					JSONArray items = itemInfos.getJSONArray("data_info");
					for(int i = 0 ; i < items.length() ; i++)
					{
						try{
							JSONObject itemInfo = items.getJSONObject(i) ;
							//ecshop商品编号
							String itemID = itemInfo.getString("goods_id");
							//商品标题 
							String itemName = new String(itemInfo.getString("goods_name").getBytes(),"gbk");
							//货号
							String outerItemID =itemInfo.getString("goods_sn");
							long modifiedTemp = Long.parseLong(itemInfo.getString("last_modify"));
							if(modifiedTemp>modifiedDate) modifiedDate=modifiedTemp;
							String stockCount="0";
							if(!"null".equals(itemInfo.get("product_number")))
								stockCount = itemInfo.getString("product_number");
							Log.info("stockCount: "+stockCount);
							if("".equals(itemID)){  //商品编码为空，跳过
								break;
							}
							//商品库存
											//sku
							String sku = itemInfo.getString("product_sn");
							String skuId = itemInfo.getString("product_id");
											//外部sku
								//库存   String produceCode,String app_key,String app_Secret,String format,String url
								Log.info("获取到新的SKU: "+sku);
								StockManager.addStockConfigSku(dao, orgid,itemID,skuId,sku,Integer.valueOf(stockCount).intValue()) ;
										
									StockManager.stockConfig(dao, orgid,Integer.valueOf(Params.tradecontactid),itemID,outerItemID,itemName,Integer.parseInt(stockCount)) ;
									m++;
									
									
						}catch(Exception ex){
							ex.printStackTrace();
							Log.warn("ecshop取商品出错,错误信息: "+ex.getMessage());
							if (conn != null && !conn.getAutoCommit())
								conn.rollback();
							continue;
						}
						
						}
			
					//是否还有下一页
					if(pageIndex < Integer.parseInt(pageTotal))
					{
						hasNextpage = true ;
						pageIndex ++ ;
						Log.info("页数:"+pageIndex);
					}
					else
					{
						hasNextpage = false ;
					}
			}
				//修改取商品最新时间
				if(modifiedDate > Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT).getTime()/1000L){
					try{
						String value=Formatter.format(new Date(modifiedDate*1000L), Formatter.DATE_TIME_FORMAT);
						PublicUtils.setConfig(conn, lasttimeconfvalue, value);
					}catch(JException ex){
						Log.error("修改取商品最新修改时间出错", ex.getMessage());
					}
				}
				
				Log.info("取到ecshop总商品数:"+String.valueOf(m)+" 总SKU数:"+String.valueOf(n));
				
				//执行成功后不再循环
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				if(conn!=null && !conn.getAutoCommit())
					conn.rollback();
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
}