package com.wofu.ecommerce.vjia;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.conv.DesUtil;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class GetOrders extends Thread {

	private static String jobname = "获取vjia订单作业";
	private static String tradecontactid = String.valueOf(Params.tradecontactid) ;
	private static String lasttimeconfvalue = Params.username + "取订单最新时间" ;
	private static long daymillis=24*60*60*1000L;
	private String lasttime;
	private Date mosttime;
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Connection connection = null;
		
			try 
			{					
				connection = PoolHelper.getInstance().getConnection(Params.dbname);	
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,"");
				
				getOrders(connection);
			} 
			catch (Exception e) 
			{
				try 
				{
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} 
				catch (Exception e1) 
				{
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} 
			finally 
			{
		
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try 
				{
					sleep(1000L);
				} 
				catch (Exception e) 
				{
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	private void getOrders(Connection conn) throws Exception
	{
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		
		for (int k=0;k<10;)
		{
			try
			{

				SoapHeader soapheader=new SoapHeader();
				soapheader.setPassword(Params.suppliersign);
				soapheader.setUname(Params.supplierid);
				soapheader.setUri(Params.uri);
			   
				SoapBody soapbody=new SoapBody();
				soapbody.setRequestname("GetOrderByTime");
				soapbody.setUri(Params.uri);
			   
				Hashtable<String,String> bodyparams=new Hashtable<String,String>();
		
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				
				String lastModifyTimeStart = Formatter.format(startdate, Formatter.DATE_TIME_FORMAT) ;
				String lastModifyTimeEnd = Formatter.format(enddate, Formatter.DATE_TIME_FORMAT) ;
				
				   
				while(hasNextPage)
				{
					bodyparams.clear() ;
					bodyparams.put("swsSupplierID", Params.swssupplierid);
					bodyparams.put("pageSize", String.valueOf(Params.pageSize)) ;
					bodyparams.put("startTime", lastModifyTimeStart) ;
					bodyparams.put("endTime", lastModifyTimeEnd) ;
					bodyparams.put("status", "ALL") ;
					bodyparams.put("sort", "0");
					bodyparams.put("orderCode", "");
					bodyparams.put("addressee", "");
					bodyparams.put("phone", "");
				    bodyparams.put("page", String.valueOf(pageIndex)) ;
				   soapbody.setBodyParams(bodyparams);
				   
				   SoapServiceClient client=new SoapServiceClient();
				   client.setUrl(Params.wsurl+"/GetOrderService.asmx");
				   client.setSoapbody(soapbody);
				   client.setSoapheader(soapheader);
				   
				   String result=client.request();
				   
		
				  
				   
				   Document resultdoc=DOMHelper.newDocument(result);
				   Element resultelement=resultdoc.getDocumentElement();
				   
				   String resultcode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
				   String resultmessage = DOMHelper.getSubElementVauleByName(resultelement, "resultmessage").trim() ;

				   if("-1234699".indexOf(resultcode) >= 0 ||"5".equals(resultcode))
				   {

					   Log.error(jobname,"获取订单失败，错误代码："+ resultcode+"，错误信息："+resultmessage+",result="+result) ;
					   
					   if (pageIndex==1 && resultmessage.indexOf("没有可处理的订单")>=0)		
						{
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
						   Log.info(jobname,"不存在需要处理的订单!");
						   hasNextPage=false;								
						   break ;
						}
					   
				   }			
			
				   Element resultdetail=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
				   NodeList orderList = resultdetail.getElementsByTagName("order") ;
				   if(orderList.getLength() <= 0)
				   {
									
						if (pageIndex==1)		
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
						hasNextPage = false ;
						break;
	 
				   }

					   		   
				   for (int i=0;i<orderList.getLength();i++)
				   {
					  
					   
					   Element order=(Element) orderList.item(i);
					   //取订单信息
					   String orderid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderid"), Params.strkey, Params.striv).trim() ;
					   
					   
					   Order o  = OrderUtils.getOrderByID(Params.wsurl,Params.uri,Params.swssupplierid,Params.strkey, Params.striv, Params.supplierid, Params.suppliersign, orderid);
					   
					   
					   Log.info("订单号:"+o.getOrderid()+" 订单状态:"+o.getOrderstatus()+",时间:"+Formatter.format(o.getOrderdistributetime(),Formatter.DATE_TIME_FORMAT));
					   
						 
					   if (o.getOrderstatus().equalsIgnoreCase("NEW") || o.getOrderstatus().equalsIgnoreCase("CONFIRMED")|| o.getOrderstatus().equalsIgnoreCase("12"))
					   {
							if (!OrderManager.isCheck("检查V+订单", conn, o.getOrderid()))
							{
								if (!OrderManager.TidLastModifyIntfExists("检查V+订单", conn, o.getOrderid(),o.getOrderdistributetime()))
								{
									try
									{
																
										OrderUtils.createInterOrder(conn,o,tradecontactid,Params.username);
										
										for(int j=0;j<o.getOrderitems().size();j++)
										{
											String sku = o.getOrderitems().get(j).getBarcode() ;
											long qty=Integer.valueOf(o.getOrderitems().get(j).getQty());
						
											StockManager.deleteWaitPayStock("检查V+订单", conn,tradecontactid, o.getOrderid(), sku);										
																	
										}
										
									} catch(SQLException sqle)
									{
										throw new JException("生成接口订单出错!" + sqle.getMessage());
									}
								}
							}
					   }else if (o.getOrderstatus().equalsIgnoreCase("FINISHED") || o.getOrderstatus().equalsIgnoreCase("25")
							   ||o.getOrderstatus().equalsIgnoreCase("SENDED") || o.getOrderstatus().equalsIgnoreCase("6"))
					   {
							for(int j=0;j<o.getOrderitems().size();j++)
							{
								String sku = o.getOrderitems().get(j).getBarcode() ;
								long qty=Integer.valueOf(o.getOrderitems().get(j).getQty());
			
								StockManager.deleteWaitPayStock("检查V+订单", conn,tradecontactid, o.getOrderid(), sku);										
														
							}
					   }else if ( o.getOrderstatus().equalsIgnoreCase("CANCELED") || o.getOrderstatus().equalsIgnoreCase("-1"))
					   {
					  
						   for(int j=0;j<o.getOrderitems().size();j++)
							{
								String sku = o.getOrderitems().get(j).getBarcode() ;
								long qty=Integer.valueOf(o.getOrderitems().get(j).getQty());
			
								StockManager.deleteWaitPayStock("检查V+订单", conn,tradecontactid, o.getOrderid(), sku);										
														
							}
					   
						   //取消订单
							String sql="declare @ret int;  execute  @ret = IF_CancelCustomerOrder '" + o.getOrderid() + "';select @ret ret;";
		
							int resultCode = SQLHelper.intSelect(conn, sql) ;
							//取消订单失败
							if(resultCode == 0)
							{
								Log.info("订单未审核-取消成功,单号:"+o.getOrderid()+"");
							}else if(resultCode == 1)
							{
								Log.info("订单已审核-截单,单号:"+o.getOrderid()+"");
							}else if(resultCode ==2)
							{
								Log.info("订单已经出库-取消失败,单号:"+o.getOrderid()+"");
							}else if(resultCode ==3)
							{
								Log.info("订单不存在或已取消-取消失败,单号:"+o.getOrderid()+"");
							}
							else
							{
								Log.info("取消失败,单号:"+o.getOrderid()+"");
							}
					   }
					  
					   
					   if(i==0){
						   mosttime=o.getOrderdistributetime();
					   }
					  
					   if(o.getOrderdistributetime().compareTo(modified)>0){
						   mosttime=o.getOrderdistributetime();
					   }
					 //更新同步订单最新时间
		                if (mosttime.compareTo(modified)>0)
		                {
		                	modified=mosttime;
		                }
		               
					}
				  
	
				   String allpagenum = DOMHelper.getSubElementVauleByName(resultdetail, "allpagenum").trim();
				   
				   if(pageIndex < Integer.parseInt(allpagenum))
				   {
					   //下一页
					   pageIndex ++ ;
				   }
				   else
				   {
					   hasNextPage = false ;
					   break;
				   }
				} 
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{	
						Log.info("同步订单最新时间"+modified);
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobname,je.getMessage());
	            	}
				}
				break ;
			}
			catch(Exception e)
			{
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
	}
	
	
}

