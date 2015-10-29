package com.wofu.ecommerce.threeg;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.ecommerce.threeg.util.CommonHelper;
import com.wofu.ecommerce.threeg.util.Utility;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class OrderUtils {
	
	private static String orderquerymethod="OrderListQuery.ashx";
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private static long daymillis=2*24*60*60*1000L;

	public static void getOrderList(String modulename,Connection conn,
			Hashtable htwfinfo,String status,Date begintime,Date endtime)
			throws Exception
	{
		String cmdcode=htwfinfo.get("cmd").toString();
		String wsurl=htwfinfo.get("wsurl").toString();
		String CustomerPrivateKeyPath=htwfinfo.get("CustomerPrivateKeyPath").toString();
		String GGMallPublicKeyPath=htwfinfo.get("GGMallPublicKeyPath").toString();		
		String encoding=htwfinfo.get("encoding").toString();
		String sellername=htwfinfo.get("username").toString();
		String style=htwfinfo.get("style").toString();
		String agentid=htwfinfo.get("agentid").toString();		
		String tradecontactid=htwfinfo.get("tradecontactid").toString();
		String lasttimeconfvalue=htwfinfo.get("lasttimeconfvalue").toString();
		
		String body=getBody(status,begintime,endtime);
		String requestdata=CommonHelper.getXML(CustomerPrivateKeyPath, agentid, cmdcode, body);
		
	
		String s=CommonHelper.SendRequest(wsurl+orderquerymethod, requestdata);
	
		
		String bodystr=s.substring(s.indexOf("<body>"), s.indexOf("</body>")+7);
	
		Document doc = DOMHelper.newDocument(s, encoding);
		Element msgelement = doc.getDocumentElement();
		Element bodyElement=(Element) msgelement.getElementsByTagName("body").item(0);
		Element ctrlElement=(Element) msgelement.getElementsByTagName("ctrl").item(0);		
		String messagedigest=DOMHelper.getSubElementVauleByName(ctrlElement, "md");
	
		
		//if (!Utility.ValifyDigest(bodystr,messagedigest,GGMallPublicKeyPath))		
		//{
		//	throw new JException("签名验证失败!");
		//}
		//else
		//{
			
			NodeList orders=((Element) bodyElement.getElementsByTagName("OrderList").item(0)).getElementsByTagName("OrderInfo");
			if (orders.getLength()>0)
			{
				for (int i=0; i<orders.getLength();i++)
				{
					Element orderinfo=(Element) orders.item(i);
					Order o=getOrderDetail(orderinfo);
		
					if (style.equals("0"))
					{
						try
						{
							createInterOrder(conn,tradecontactid,sellername,o);					
										
						} catch(SQLException sqle)
						{
							throw new JException("生成接口订单出错!" + sqle.getMessage());
						}	
						
						//更新同步订单最新时间
		                if (Formatter.parseDate(o.getCreateTime(),Formatter.DATE_TIME_FORMAT).compareTo(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT))>0)
		                	PublicUtils.setConfig(conn, lasttimeconfvalue, Formatter.format(Formatter.parseDate(o.getCreateTime(),Formatter.DATE_TIME_FORMAT), Formatter.DATE_TIME_FORMAT));
					}
					else
					{
						
						if (!OrderManager.isCheck("检查3G未入订单", conn, String.valueOf(o.getOrderId())))
						{
							if (!OrderManager.TidLastModifyIntfExists("检查3G未入订单", conn, String.valueOf(o.getOrderId()),Formatter.parseDate(o.getModified(),Formatter.DATE_TIME_FORMAT))
									|| (OrderManager.CIDExists("检查3G未入订单", conn, String.valueOf(o.getOrderId())) && status.equals("7")))
							{								
								try
								{
									createInterOrder(conn,tradecontactid,sellername,o);				
								} catch(SQLException sqle)
								{
									throw new JException("生成接口订单出错!" + sqle.getMessage());
								}	
							}
						}
						
					}
					
					//减其他店库存
					for(int j=0;j<o.getOrderitems().size();j++)
					{
						OrderItem item=(OrderItem) o.getOrderitems().get(j);
						String sku=item.getExternalId();
						int qty=Integer.valueOf(item.getCount()).intValue();
						StockManager.addSynReduceStore(modulename, conn, tradecontactid, status,o.getOrderId(), sku, -qty,false);
					}
										 
				}
			}else
			{
				if (style.equals("0"))
				{
					try
					{
						//如该段时间之内都取不到订单，而且当前天大于配置天，则将取订单最新时间更新为当前天的零点
						if (dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
								compareTo(dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
						{
							                	
							PublicUtils.setConfig(conn,lasttimeconfvalue,Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn, lasttimeconfvalue, ""), Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00");
		                	
						}
					}catch(ParseException e)
					{
						throw new JException("不可用的日期格式!"+e.getMessage());
					}
				}
			}
		//}		
	}
	
	private static String getBody(String orderstatus,Date begintime,Date endtime)
	{
		StringBuffer bodybuffer=new StringBuffer();
		
		bodybuffer.append("<body>");
		bodybuffer.append("<status>").append(orderstatus).append("</status>");	
		
		bodybuffer.append("<StartTime>");
		bodybuffer.append(Formatter.format(begintime, Formatter.DATE_TIME_FORMAT));
		bodybuffer.append("</StartTime>");
		bodybuffer.append("<EndTime>");
		bodybuffer.append(Formatter.format(endtime, Formatter.DATE_TIME_FORMAT));
		bodybuffer.append("</EndTime>");
		bodybuffer.append("</body>");
		
		return bodybuffer.toString();
	}
	
	private static Order getOrderDetail(Element orderinfo) throws Exception
	{
		Order o=new Order();
		String OrderId=DOMHelper.getSubElementVauleByName(orderinfo, "OrderId");
		String OrderStatus=DOMHelper.getSubElementVauleByName(orderinfo, "OrderStatus");
		String orderMoneyStatus=DOMHelper.getSubElementVauleByName(orderinfo, "OrderMoneyStatus");
		String UserName=DOMHelper.getSubElementVauleByName(orderinfo, "UserName");
		String phone=DOMHelper.getSubElementVauleByName(orderinfo, "Phone");
		String address=DOMHelper.getSubElementVauleByName(orderinfo, "Address");
		String provinceId=DOMHelper.getSubElementVauleByName(orderinfo, "ProvinceId");
		String provinceName=DOMHelper.getSubElementVauleByName(orderinfo, "ProvinceName");
		String cityId=DOMHelper.getSubElementVauleByName(orderinfo, "CityId");
		String cityName=DOMHelper.getSubElementVauleByName(orderinfo, "CityName");
		String areaid=DOMHelper.getSubElementVauleByName(orderinfo, "AreaId");
		
		String areaName=DOMHelper.getSubElementVauleByName(orderinfo, "AreaName");
		String createTime=DOMHelper.getSubElementVauleByName(orderinfo, "CreateTime");
		String modified=createTime;
		
		String payMode=DOMHelper.getSubElementVauleByName(orderinfo, "PayMode");
		String totalMoney=DOMHelper.getSubElementVauleByName(orderinfo, "TotalMoney");
		String ScoreMoney=DOMHelper.getSubElementVauleByName(orderinfo, "ScoreMoney");
		String payFee=DOMHelper.getSubElementVauleByName(orderinfo, "PayFee");
	
		String ShippingFee=DOMHelper.getSubElementVauleByName(orderinfo, "ShippingFee");
		String LogisticOperator=DOMHelper.getSubElementVauleByName(orderinfo, "LogisticOperator");
		String LogisticId=DOMHelper.getSubElementVauleByName(orderinfo, "LogisticId");
		String Remark=DOMHelper.getSubElementVauleByName(orderinfo, "Remark");
		String OrderRemarks="";
		Element remarks=(Element) orderinfo.getElementsByTagName("OrderRemarks").item(0);	
		if (DOMHelper.ElementIsExists(remarks, "OrderRemark"))
		{
			NodeList remarksnodes=remarks.getElementsByTagName("OrderRemark");
			
			Element orderremark=(Element) remarksnodes.item(remarksnodes.getLength()-1);
			
			if (DOMHelper.ElementIsExists(orderremark, "Remark"))
			{
				OrderRemarks=DOMHelper.getSubElementVauleByName(orderremark,"Remark");
				String modifytime=DOMHelper.getSubElementVauleByName(orderremark,"Time");
				
				if (Formatter.parseDate(modifytime,Formatter.DATE_TIME_FORMAT).compareTo(Formatter.parseDate(modified,Formatter.DATE_TIME_FORMAT))>0)
					modified=modifytime;
			}
		}
		
		
		o.setOrderId(OrderId);
		o.setOrderStatus(OrderStatus);
		o.setOrderMoneyStatus(orderMoneyStatus);
		o.setUserName(UserName);
		o.setPhone(phone);
		o.setAddress(address);
		o.setProvinceId(provinceId);
		o.setProvinceName(provinceName);
		o.setCityId(cityId);
		o.setCityName(cityName);
		o.setAreaid(areaid);
		o.setAreaName(areaName);
		o.setCreateTime(createTime);
		o.setModified(modified);
		o.setPayMode(payMode);
		o.setTotalMoney(totalMoney);
		o.setScoreMoney(ScoreMoney);
		o.setPayFee(payFee);
		o.setShippingFee(ShippingFee);
		o.setLogisticId(LogisticId);
		o.setLogisticOperator(LogisticOperator);
		o.setRemark(Remark);
		o.setOrderRemarks(OrderRemarks);
		
		NodeList items=((Element) orderinfo.getElementsByTagName("ProductList").item(0)).getElementsByTagName("ProductModel");
		
		for(int i=0;i<items.getLength();i++)
		{
			Element product=(Element) items.item(i);
			OrderItem item=new OrderItem();
			String productName=DOMHelper.getSubElementVauleByName(product, "ProductName");
			String ExternalId=DOMHelper.getSubElementVauleByName(product, "ExternalId");
		
			String count=DOMHelper.getSubElementVauleByName(product, "Count");
			String productPrice=DOMHelper.getSubElementVauleByName(product, "ProductPrice");
	
			item.setCount(count);
			item.setExternalId(ExternalId);
			item.setProductName(productName);
			item.setProductPrice(productPrice);
			o.getOrderitems().add(item);
		}
			
		return o;
	}
	
	public static void createInterOrder(Connection conn, String tradecontactid,String sellername,Order o) 
		throws Exception
	{
		String sheetid="";
		int haspostFee;
		String sql="declare @Err int ; declare @NewSheetID char(16); "
				+"execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;"
				+"select @NewSheetID;";
		try
		{			
				conn.setAutoCommit(false);			
				sheetid=SQLHelper.strSelect(conn, sql);
				if (sheetid.trim().equals(""))
					throw new JSQLException(sql,"取接口单号出错!");
				
				 //加入到通知表
                sql = "insert into it_downnote(Owner,sheetid,sheettype,sender,receiver,notetime,handletime) "
                		+" values('yongjun','"+ sheetid +"',1,'"+tradecontactid+"','yongjun',getdate(),null) ";				
				SQLHelper.executeSQL(conn, sql);
				
				if (Float.valueOf(o.getShippingFee()).compareTo(Float.valueOf("0.0"))>0)
					haspostFee=1;
				else
					haspostFee=0;
				
				//Log.info(o.getRemark());
				//Log.info(o.getOrderRemarks());
				//Log.info(o.getPhone());
				
				//Log.info(o.getCreateTime());
				//Log.info(o.getTotalMoney());
				//Log.info(o.getOrderId());
				//Log.info(o.getPayFee());
				//Log.info(o.getShippingFee());
				
				//Log.info(o.getProvinceName());
				//Log.info(o.getCityName());
				//Log.info(o.getAreaName());
				//Log.info(o.getUserName());
				
				String scorenote="";
				if (Float.valueOf(o.getScoreMoney())>0.00)
					scorenote="积分抵用金额:"+o.getScoreMoney();
				
				 //加入到单据表
                sql = "insert into ns_customerorder(CustomerOrderId,SheetID,Owner,tid,OrderSheetID,sellernick," 
                    + " created ,payment,status,buyermemo,sellermemo,tradememo,paytime,modified,totalfee,postfee,payfee,"
                    + " buyernick,receivername,receiverstate,receivercity,receiverdistrict," 
                    + " receiveraddress,receiverzip,receivermobile,receiverphone,buyeremail,haspostFee," 
                    + " price,num,title,tradefrom,TradeContactID,paymode,discountfee) "
                    + " values(" 
                    + "'" + sheetid + "','" + sheetid + "','yongjun','" + o.getOrderId() + "','','" +sellername + "'," 
                    + "'" + o.getCreateTime() + "','"  +String.valueOf(Float.valueOf(o.getTotalMoney())+Float.valueOf(o.getScoreMoney())+Float.valueOf(o.getPayFee())+Float.valueOf(o.getShippingFee())) + "'," 
                    + "'" + o.getOrderStatus() + "','" + o.getRemark() + "','" + o.getOrderRemarks()+"','"+scorenote+ "','" + o.getCreateTime() + "','"+o.getModified()+"','"
                    + String.valueOf(Float.valueOf(o.getTotalMoney())+Float.valueOf(o.getScoreMoney())) + "','" + o.getShippingFee() + "','" + o.getPayFee()+"','"+o.getPhone()+ "'" 
                    + ",'" + o.getUserName() +  "','" + o.getProvinceName() + "', '" + o.getCityName() + "','"+o.getAreaName()+"','" + o.getAddress() + "', " 
                    + "'','" + o.getPhone() + "','"+o.getPhone()+"','','" + String.valueOf(haspostFee) + "',"                                        
                    + "'" + String.valueOf(Float.valueOf(o.getTotalMoney())+Float.valueOf(o.getScoreMoney())) + "','', '" + sellername + "','3G','"+tradecontactid+"',"+o.getPayMode()+",'"+o.getScoreMoney()+"')";
                SQLHelper.executeSQL(conn, sql);
           
                String refundstatus="NO_REFUND";
                String refundid="0";
                if (o.getOrderMoneyStatus().equals("3") || o.getOrderStatus().equals("7"))
                {
                	refundstatus="SUCCESS";
                	refundid="10000";
                }
        
                
                for(int i=0;i<o.getOrderitems().size();i++)
                {
                	OrderItem item=o.getOrderitems().get(i);
                	                	
                	
	                sql = "insert into ns_orderitem(CustomerOrderId,orderItemId,SheetID,"
	                + " sellernick,buyernick,created,outerskuid,title,totalfee,payment,"
	                + " status,refundstatus,refundid,owner,num,price,skuid) values( " 
	                + "'" + sheetid + "','" + sheetid + "_" + o.getOrderId() + "_"+item.getExternalId()+"','" + sheetid + "'," 
	                + "'" + sellername + "', '" + o.getPhone() + "' ,'" + o.getCreateTime() + "', " 
	                + "'" + item.getExternalId() + "','" +item.getProductName()+"','"+String.valueOf(Float.valueOf(item.getCount())*Float.valueOf(item.getProductPrice()))
	                +"','" +String.valueOf(Float.valueOf(item.getCount())*Float.valueOf(item.getProductPrice())+Float.valueOf(o.getShippingFee())+Float.valueOf(o.getPayFee())) + "' , " 
	                + "'" +  o.getOrderStatus() + "','"+refundstatus+"','"+refundid+"','yongjun'," 
	                + "" +  item.getCount() + ",'" + item.getProductPrice()+"','"+item.getExternalId()+"')";
	                SQLHelper.executeSQL(conn,sql);
                }
                
                                  
				conn.commit();
				conn.setAutoCommit(true);
				Log.info("生成订单【" + o.getOrderId() + "】接口数据成功，接口单号【" + sheetid + "】");				
		}
		catch (JSQLException e1)
		{			
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
			throw new JException("生成订单【" + o.getOrderId() + "】接口数据失败!"+e1.getMessage());
		}		
	}
}
