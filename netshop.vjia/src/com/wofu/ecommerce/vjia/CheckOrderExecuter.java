package com.wofu.ecommerce.vjia;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.base.job.Executer;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.conv.DesUtil;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
public class CheckOrderExecuter extends Executer {

	private static String passWord = "" ;
	private static String username = "" ;
	private static String supplierid = "" ;
	
	private static String URI = "" ;
	private static String swsSupplierID = "" ;
	private static String wsurl = "" ;
	private static String strkey = "" ;
	private static String striv = "" ;
	private static String pageSize = "" ;
	private static String tradecontactid = "" ;
	private static String jobname="定时检查V+订单";
	private static long daymillis=24*60*60*1000L;

	@Override
	public void run() {

		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		passWord = prop.getProperty("passWord") ;
		username = prop.getProperty("username") ;
		supplierid = prop.getProperty("supplierid") ;
		URI = prop.getProperty("URI") ;
		swsSupplierID = prop.getProperty("swsSupplierID") ;

		wsurl = prop.getProperty("wsurl") ;
		strkey = prop.getProperty("strkey") ;
		striv = prop.getProperty("striv") ;
		pageSize = prop.getProperty("pageSize") ;
		tradecontactid = prop.getProperty("tradecontactid") ;


		try 
		{	
			updateJobFlag(1);
			//检查未入订单
			checkOrders();
			checkCanceledOrders();
			
			UpdateTimerJob();
			
			Log.info(jobname, "执行作业成功 ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] 下次处理时间: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
	
		} catch (Exception e) {
			try {
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));

				
			} catch (Exception e1) {
				Log.error(jobname,"回滚事务失败");
			}
			Log.error(jobname,"错误信息:"+Log.getErrorMessage(e));
			
			
			Log.error(jobname, "执行作业失败 [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
			
		} finally {
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobname,"更新处理标志失败");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobname,"关闭数据库连接失败");
			}
		}
		
	
	}
	
	private void checkOrders() throws Exception
	{
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		
		for (int k=0;k<10;)
		{
			try
			{

				SoapHeader soapheader=new SoapHeader();
				soapheader.setPassword(passWord);
				soapheader.setUname(supplierid);
				soapheader.setUri(URI);
			   
				SoapBody soapbody=new SoapBody();
				soapbody.setRequestname("GetOrderByTime");
				soapbody.setUri(URI);
			   
				Hashtable<String,String> bodyparams=new Hashtable<String,String>();
		
				   
				while(hasNextPage)
				{
					bodyparams.clear() ;
					bodyparams.put("swsSupplierID", swsSupplierID);
					bodyparams.put("pageSize", String.valueOf(pageSize)) ;
					Date startdate=new Date(System.currentTimeMillis()-2*this.daymillis);
					bodyparams.put("startTime",Formatter.format(startdate, Formatter.DATE_TIME_FORMAT) ) ;
					bodyparams.put("endTime", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)) ;
					bodyparams.put("status", "ALL") ;
					bodyparams.put("sort", "0");
					bodyparams.put("orderCode", "");
					bodyparams.put("addressee", "");
					bodyparams.put("phone", "");
				    bodyparams.put("page", String.valueOf(pageIndex)) ;
				   soapbody.setBodyParams(bodyparams);
				   
				   SoapServiceClient client=new SoapServiceClient();
				   client.setUrl(wsurl+"/GetOrderService.asmx");
				   client.setSoapbody(soapbody);
				   client.setSoapheader(soapheader);
				   
				   String result=client.request();
				  // Log.info("v+ss: "+result);
				   
				   Document resultdoc=DOMHelper.newDocument(result);
				   Element resultelement=resultdoc.getDocumentElement();
				   
				   String resultcode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
				   String resultmessage = DOMHelper.getSubElementVauleByName(resultelement, "resultmessage").trim() ;
					   
				   if("-1234699".indexOf(resultcode) >= 0 ||"5".equals(resultcode))
				   {
					   Log.error(jobname,"获取订单失败，错误代码："+ resultcode+"，错误信息："+resultmessage) ;
					   hasNextPage = false ;
					   break ;
				   }			
					   
				   Element resultdetail=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
				   NodeList orderList = resultdetail.getElementsByTagName("order") ;
				   if(orderList.getLength() <= 0)
				   {
					   hasNextPage = false ;
					   break ;
				   }

					   		   
				   for (int i=0;i<orderList.getLength();i++)
				   {
					   Order o  = new Order() ;
					   
					   Element order=(Element) orderList.item(i);
					   //取订单信息
					   String orderid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderid"), strkey, striv).trim() ;
				
					   String orderdistributetime = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderdistributetime"), strkey, striv) ;
					   String username = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "username"), strkey, striv) ;
					   String usertel = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "usertel"), strkey, striv) ;
					   String userphone = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "userphone"), strkey, striv) ;
					   String areaid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "areaid"), strkey, striv) ;
					   String postalcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "postalcode"), strkey, striv) ;
					   String address = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "address"), strkey, striv) ;
					   String needinvoice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "needinvoice"), strkey, striv).trim() ;
					   String receivetime = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "receivetime"), strkey, striv) ;
					   String totalprice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "totalprice"), strkey, striv) ;
					   String transferprice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "transferprice"), strkey, striv) ;
					   String paidprice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "paidprice"), strkey, striv) ;
					   String unpaidprice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "unpaidprice"), strkey, striv).trim() ;
					   String comment = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "comment"), strkey, striv) ;
					   String orderstatus = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderstatus"), strkey, striv) ;
					      
					   
					   //处理送货时间，去掉括号后面的内容
					   if(receivetime != null && receivetime.indexOf("（") > -1)
					   {
						   receivetime = receivetime.substring(0, receivetime.indexOf("（")) ;
					   }
					   
					   //付款模式 1是在线支付	2是货到付款
					   if("0.00".equals(unpaidprice) || Float.parseFloat(unpaidprice) == 0)
						   o.setPayMode("1") ;
					   else
						   o.setPayMode("2") ;
					   //是否需发票：1需要，0不需要,needinvoice 返回值
					   if("True".equalsIgnoreCase(needinvoice))
					   {
						   o.setNeedinvoice("1") ;
						   String invoiceTitle =OrderUtils.getInvoiceTitle(jobname, orderid,URI,wsurl,supplierid,passWord,swsSupplierID,strkey,striv);
						   //如果发票台头为“个人”，则用联系人作为发票台头
						   if("个人".equals(invoiceTitle))
							   o.setInvoiceTitle(username.replace("'", "''")) ;
						   else
							   o.setInvoiceTitle(invoiceTitle) ;
					   }
					   else
						   o.setNeedinvoice("0") ;
					   
					   //处理地址信息，（省）（市）（区）用空格替代
					   address = address.replace("（省）", " ").replace("（市）", " ").replace("（区）", " ") ;
		   
					   o.setOrderid(orderid) ;
					   if (!orderdistributetime.equals(""))
						   o.setOrderdistributetime(Formatter.parseDate(orderdistributetime,Formatter.DATE_TIME_FORMAT)) ;
					   else				   
						   o.setOrderdistributetime(new Date()) ;
					   o.setUsername(username) ;
					   o.setUsertel(usertel) ;
					   o.setUserphone(userphone) ;
					   o.setAreaid(areaid) ;
					   o.setPostalcode(postalcode) ;
					   o.setAddress(address) ;
					   o.setReceivetime(receivetime) ;
					   o.setTotalprice(Float.parseFloat(totalprice)) ;
					   o.setTransferprice(Float.parseFloat(transferprice)) ;
					   o.setPaidprice(Float.parseFloat(paidprice)) ;
					   o.setUnpaidprice(Float.parseFloat(unpaidprice)) ;
					   o.setComment(comment) ;	   
					   o.setOrderstatus(orderstatus);
						   
					   //取得订单明细
					   Element[] orderDetail = DOMHelper.getSubElementsByName(order, "orderdetail") ;

					   for(int j = 0 ; j < orderDetail.length ; j++)
					   {
						   Element detail=(Element) orderDetail[j] ; 
						   String barcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "barcode"), strkey, striv) ;
						   String sku = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "sku"), strkey, striv) ;
						   String productname = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "productname"), strkey, striv) ;
						   String size = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "size"), strkey, striv) ;
						   String qty = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "qty"), strkey, striv) ;
						   String price = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "price"), strkey, striv) ;
						   String amount = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "amount"), strkey, striv) ;
						   
						   if("".equals(qty) || qty==null)
							   qty = "0" ;
						   if("".equals(price) || price==null)
							   qty = "0" ;
						   if("".equals(amount) || amount==null)
							   amount = "0" ;
						   
						   OrderItem oi = new OrderItem() ;
						   oi.setBarcode(barcode) ;
						   oi.setSku(sku) ;
						   oi.setProductname(productname) ;
						   oi.setSize(size) ;
						   oi.setQty(qty) ;
						   oi.setPrice(price) ;
						   oi.setAmount(amount) ;
						   
						   o.addOrderitems(oi) ;
					   }
						 
						if (!OrderManager.isCheck("检查V+订单", this.getDao().getConnection(), o.getOrderid()))
						{
							if (!OrderManager.TidLastModifyIntfExists("检查V+订单", this.getDao().getConnection(), o.getOrderid(),o.getOrderdistributetime()))
							{
								try
								{
															
									OrderUtils.createInterOrder(this.getDao().getConnection(),o,tradecontactid,username);
									
									for(int j=0;j<o.getOrderitems().size();j++)
									{
										String sku = o.getOrderitems().get(j).getBarcode() ;
										long qty=Integer.valueOf(o.getOrderitems().get(j).getQty());
					
										StockManager.deleteWaitPayStock("检查V+订单", this.getDao().getConnection(),tradecontactid, o.getOrderid(), sku);										
																
									}
									
								} catch(SQLException sqle)
								{
									throw new JException("生成接口订单出错!" + sqle.getMessage());
								}
							}
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
				k=10 ;
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
	
	private void checkCanceledOrders() throws Exception
	{
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		
		for (int k=0;k<10;)
		{
			try
			{

				SoapHeader soapheader=new SoapHeader();
				soapheader.setPassword(passWord);
				soapheader.setUname(supplierid);
				soapheader.setUri(URI);
			   
				SoapBody soapbody=new SoapBody();
				soapbody.setRequestname("GetOrderByTime");
				soapbody.setUri(URI);
			   
				Hashtable<String,String> bodyparams=new Hashtable<String,String>();
		
				Date begintime=new Date(System.currentTimeMillis()-daymillis);
				Date endtime=new Date();
				String lastModifyTimeStart = Formatter.format(begintime, Formatter.DATE_TIME_FORMAT) ;
				String lastModifyTimeEnd = Formatter.format(endtime, Formatter.DATE_TIME_FORMAT) ;
				
				   
				while(hasNextPage)
				{
					bodyparams.clear() ;
					bodyparams.put("swsSupplierID", swsSupplierID);
					bodyparams.put("pageSize", String.valueOf(pageSize)) ;
					bodyparams.put("startTime", lastModifyTimeStart) ;
					bodyparams.put("endTime", lastModifyTimeEnd) ;
					bodyparams.put("status", "CANCELED") ;
					bodyparams.put("sort", "0");
					bodyparams.put("orderCode", "");
					bodyparams.put("addressee", "");
					bodyparams.put("phone", "");
				    bodyparams.put("page", String.valueOf(pageIndex)) ;
				   soapbody.setBodyParams(bodyparams);
				   
				   SoapServiceClient client=new SoapServiceClient();
				   client.setUrl(wsurl+"/GetOrderService.asmx");
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
					   hasNextPage = false ;
					   break ;
				   }			
					   
				   Element resultdetail=(Element) resultelement.getElementsByTagName("resultdetail").item(0);
				   NodeList orderList = resultdetail.getElementsByTagName("order") ;
				   if(orderList.getLength() <= 0)
				   {
					   hasNextPage = false ;
					   break ;
				   }

					   		   
				   for (int i=0;i<orderList.getLength();i++)
				   {
					   Order o  = new Order() ;
					   
					   Element order=(Element) orderList.item(i);
					   //取订单信息
					   String orderid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderid"), strkey, striv).trim() ;
				
					   String orderdistributetime = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderdistributetime"), strkey, striv) ;
					   String username = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "username"), strkey, striv) ;
					   String usertel = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "usertel"), strkey, striv) ;
					   String userphone = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "userphone"), strkey, striv) ;
					   String areaid = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "areaid"), strkey, striv) ;
					   String postalcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "postalcode"), strkey, striv) ;
					   String address = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "address"), strkey, striv) ;
					   String needinvoice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "needinvoice"), strkey, striv).trim() ;
					   String receivetime = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "receivetime"), strkey, striv) ;
					   String totalprice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "totalprice"), strkey, striv) ;
					   String transferprice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "transferprice"), strkey, striv) ;
					   String paidprice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "paidprice"), strkey, striv) ;
					   String unpaidprice = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "unpaidprice"), strkey, striv).trim() ;
					   String comment = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "comment"), strkey, striv) ;
					   String orderstatus = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(order, "orderstatus"), strkey, striv) ;
					      
					   
					   //处理送货时间，去掉括号后面的内容
					   if(receivetime != null && receivetime.indexOf("（") > -1)
					   {
						   receivetime = receivetime.substring(0, receivetime.indexOf("（")) ;
					   }
					   
					   //付款模式 1是在线支付	2是货到付款
					   if("0.00".equals(unpaidprice) || Float.parseFloat(unpaidprice) == 0)
						   o.setPayMode("1") ;
					   else
						   o.setPayMode("2") ;
					   //是否需发票：1需要，0不需要,needinvoice 返回值
					   if("True".equalsIgnoreCase(needinvoice))
					   {
						   o.setNeedinvoice("1") ;
						   String invoiceTitle =OrderUtils.getInvoiceTitle(jobname, orderid,URI,wsurl,supplierid,passWord,swsSupplierID,strkey,striv);
						   //如果发票台头为“个人”，则用联系人作为发票台头
						   if("个人".equals(invoiceTitle))
							   o.setInvoiceTitle(username.replace("'", "''")) ;
						   else
							   o.setInvoiceTitle(invoiceTitle) ;
					   }
					   else
						   o.setNeedinvoice("0") ;
					   
					   //处理地址信息，（省）（市）（区）用空格替代
					   address = address.replace("（省）", " ").replace("（市）", " ").replace("（区）", " ") ;
		   
					   o.setOrderid(orderid) ;
					   if (!orderdistributetime.equals(""))
						   o.setOrderdistributetime(Formatter.parseDate(orderdistributetime,Formatter.DATE_TIME_FORMAT)) ;
					   else				   
						   o.setOrderdistributetime(new Date()) ;
					   o.setUsername(username) ;
					   o.setUsertel(usertel) ;
					   o.setUserphone(userphone) ;
					   o.setAreaid(areaid) ;
					   o.setPostalcode(postalcode) ;
					   o.setAddress(address) ;
					   o.setReceivetime(receivetime) ;
					   o.setTotalprice(Float.parseFloat(totalprice)) ;
					   o.setTransferprice(Float.parseFloat(transferprice)) ;
					   o.setPaidprice(Float.parseFloat(paidprice)) ;
					   o.setUnpaidprice(Float.parseFloat(unpaidprice)) ;
					   o.setComment(comment) ;	   
					   o.setOrderstatus(orderstatus);
						   
					   //取得订单明细
					   Element[] orderDetail = DOMHelper.getSubElementsByName(order, "orderdetail") ;

					   for(int j = 0 ; j < orderDetail.length ; j++)
					   {
						   Element detail=(Element) orderDetail[j] ; 
						   String barcode = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "barcode"), strkey, striv) ;
						   String sku = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "sku"), strkey, striv) ;
						   String productname = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "productname"), strkey, striv) ;
						   String size = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "size"), strkey, striv) ;
						   String qty = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "qty"), strkey, striv) ;
						   String price = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "price"), strkey, striv) ;
						   String amount = DesUtil.DesDecode(DOMHelper.getSubElementVauleByName(detail, "amount"), strkey, striv) ;
						   
						   if("".equals(qty) || qty==null)
							   qty = "0" ;
						   if("".equals(price) || price==null)
							   qty = "0" ;
						   if("".equals(amount) || amount==null)
							   amount = "0" ;
						   
						   OrderItem oi = new OrderItem() ;
						   oi.setBarcode(barcode) ;
						   oi.setSku(sku) ;
						   oi.setProductname(productname) ;
						   oi.setSize(size) ;
						   oi.setQty(qty) ;
						   oi.setPrice(price) ;
						   oi.setAmount(amount) ;
						   
						   o.addOrderitems(oi) ;
					   }
						 
				
						for(int j=0;j<o.getOrderitems().size();j++)
						{
							String sku = o.getOrderitems().get(j).getBarcode() ;
							long qty=Integer.valueOf(o.getOrderitems().get(j).getQty());
		
							StockManager.deleteWaitPayStock("检查V+订单", this.getDao().getConnection(),tradecontactid, o.getOrderid(), sku);										
													
						}
						
						  //取消订单
						String sql="declare @ret int;  execute  @ret = IF_CancelCustomerOrder '" + o.getOrderid() + "';select @ret ret;";
	
						int resultCode = this.getDao().intSelect(sql) ;
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
				k=10 ;
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
