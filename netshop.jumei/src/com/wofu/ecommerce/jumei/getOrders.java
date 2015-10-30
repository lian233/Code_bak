package com.wofu.ecommerce.jumei;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.business.order.OrderManager;
public class getOrders extends Thread {

	private static String jobname = "获取聚美优品订单作业";
	
	private static long daymillis=5*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;

	public getOrders() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.jumei.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
				getOrderList(connection);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.jumei.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	
	/*
	 * 获取一天之类的所有订单
	 */
	private void getOrderList(Connection conn) throws Exception
	{		
		int pageno=1;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		Date modifiedTemp = modified;
		for(int k=0;k<10;)
		{
			try
			{
				/**while(true)
				{**/
					
					Map<String, String> orderlistparams = new HashMap<String, String>();
					String method="Order/GetOrder";
					//start_date - 1, end_date + 1  聚美接口会做这样的算法，所以这里先做调整
					Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+2000L);
					Log.info(startdate.getTime()+"");
					Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis-1000L);
					Log.info(enddate.toLocaleString());
			       
					
					Map<String, String> paramMap = new HashMap<String, String>();
			        //系统级参数设置
			        paramMap.put("client_id", Params.clientid);
			        paramMap.put("client_key", Params.clientkey);
			        paramMap.put("start_date", Formatter.format(startdate, Formatter.DATE_TIME_FORMAT));
			        paramMap.put("end_date", Formatter.format(enddate, Formatter.DATE_TIME_FORMAT));
			        paramMap.put("status", "2,7");
			        //paramMap.put("page", String.valueOf(pageno));
			        //paramMap.put("page_size", "50");
			       
			        String sign=JuMeiUtils.getSign(paramMap, Params.signkey, Params.encoding);
			        
			        paramMap.put("sign", sign);
			        
			        String responseData=CommHelper.sendRequest(Params.url+method, paramMap, "", Params.encoding);
			        Log.info("返回订单数据:　"+responseData);
			        
					JSONObject responseresult=new JSONObject(responseData);
					
					int errorCount=responseresult.getInt("error");
					
					if (errorCount>0)
					{
						String errdesc=responseresult.getString("message");
						
						k=10;
						throw new JException(errdesc);
						
					}
					
										
					
					int i=1;
			
					JSONArray orderlist=responseresult.getJSONObject("result").getJSONArray("response");
								
					if (orderlist.length()==0)
					{	/**		
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
				                		Log.error(jobname, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobname, "不可用的日期格式!"+e.getMessage());
							}
						}
						k=10;
						break;
						**/
						if(i==1){
							try{
								//每10分钟取一次订单，如果没有取到订单的话，配置时间比当前时间的小时数还要小的话
								if(Formatter.parseDate((PublicUtils.getConfig(conn,lasttimeconfvalue,"").substring(0,16)+":00"),Formatter.DATE_TIME_FORMAT)
										.compareTo(Formatter.parseDate((Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT).substring(0,16)+":00"),Formatter.DATE_TIME_FORMAT))<0){
									String value= Formatter.format(new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis),Formatter.DATE_TIME_FORMAT).substring(0,16)+":00";
									if(Formatter.parseDate(value, Formatter.DATE_TIME_FORMAT).compareTo(new Date())<=0){
										System.out.println("dd: "+value);
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);
										System.out.println("finish");
									}
									
									else{
										System.out.println("dd1");
										value= Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT).substring(0,16)+":00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);
									}
										
								}else{
									System.out.println("dd2");
									String value= Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT).substring(0,16)+":00";
									PublicUtils.setConfig(conn, lasttimeconfvalue, value);
								}
							}catch(Exception e){
								Log.error(jobname, "不可用的日期格式!"+e.getMessage());
							}
						}
						k=10;
						break;
					}
									
					int invalidCount = 0;
					for(int j=0;j<orderlist.length();j++)
					{
						JSONObject order=orderlist.getJSONObject(j);
											
						Order o=new Order();
					
						o.setObjValue(o, order);
					
						JSONObject receiverinfojsobj=order.getJSONObject("receiver_infos");
					
						o.setObjValue(o.getReceiver_info(),receiverinfojsobj);
						if(new Date(o.getTimestamp()*1000).compareTo(modifiedTemp)>0) invalidCount++;
										
						Log.info(o.getOrder_id()+" "+o.getStatus()+" "+Formatter.format(new Date(o.getTimestamp()*1000),Formatter.DATE_TIME_FORMAT));
						/*
						 *1、如果状态为等待卖家发货则生成接口订单
						 *2、删除等待买家付款时的锁定库存 
						 */		
						String sku;
						String sql="";
						if (o.getStatus()== 2 ||o.getStatus()== 7)
						{	
							
							if (!OrderManager.isCheck("检查聚美优品订单", conn, o.getOrder_id()))
							{
								if (!OrderManager.TidLastModifyIntfExists("检查聚美优品订单", conn, o.getOrder_id(),new Date(o.getTimestamp()*1000)))
								{
									OrderUtils.createInterOrder(conn,o,Params.tradecontactid,Params.username);
									
									for(Iterator ito=o.getProduct_infos().getRelationData().iterator();ito.hasNext();)
									{
										OrderItem item=(OrderItem) ito.next();
										sku=item.getUpc_code();
										
										StockManager.deleteWaitPayStock(jobname, conn,Params.tradecontactid, o.getOrder_id(),sku);
										StockManager.addSynReduceStore(jobname, conn, Params.tradecontactid, String.valueOf(o.getStatus()),String.valueOf(o.getOrder_id()), sku, -item.getQuantity(),false);
									}
								}
							}
	
							//等待买家付款时记录锁定库存
						}
						
						
						//更新同步订单最新时间
		                if ((new Date(o.getTimestamp()*1000)).compareTo(modified)>0)
		                {
		                	modified=new Date(o.getTimestamp()*1000);
		                }
					}
						
					/**	
						
					//判断是否有下一页
					if (pageno>=pageCount) break;
					
					pageno++;
					
					i=i+1;
					
				}**/
				//没有有效订单
				if(invalidCount==0){
					try{
						//每10分钟取一次订单，如果没有取到订单的话，配置时间比当前时间的小时数还要小的话
						if(Formatter.parseDate((PublicUtils.getConfig(conn,lasttimeconfvalue,"").substring(0,16)+":00"),Formatter.DATE_TIME_FORMAT)
								.compareTo(Formatter.parseDate((Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT).substring(0,16)+":00"),Formatter.DATE_TIME_FORMAT))<0){
							String value= Formatter.format(new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis),Formatter.DATE_TIME_FORMAT).substring(0,16)+":00";
							if(Formatter.parseDate(value, Formatter.DATE_TIME_FORMAT).compareTo(new Date())<=0){
								System.out.println("dd: "+value);
								PublicUtils.setConfig(conn, lasttimeconfvalue, value);
								System.out.println("finish");
							}
							
							else{
								System.out.println("dd1");
								value= Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT).substring(0,16)+":00";
								PublicUtils.setConfig(conn, lasttimeconfvalue, value);
							}
								
						}else{
							System.out.println("dd2");
							String value= Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT).substring(0,16)+":00";
							PublicUtils.setConfig(conn, lasttimeconfvalue, value);
						}
					}catch(Exception e){
						Log.error(jobname, "不可用的日期格式!"+e.getMessage());
					}
					k=10;
					break;
				}
				
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobname,je.getMessage());
	            	}
				}
				//执行成功后不再循环
				break;
			} catch (Exception e) {
				e.printStackTrace();
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
