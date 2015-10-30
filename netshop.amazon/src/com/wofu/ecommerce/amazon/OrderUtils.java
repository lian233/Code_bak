package com.wofu.ecommerce.amazon;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrders;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersClient;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersConfig;
import com.amazonservices.mws.orders._2013_09_01.model.GetOrderRequest;
import com.amazonservices.mws.orders._2013_09_01.model.GetOrderResponse;
import com.amazonservices.mws.orders._2013_09_01.model.GetOrderResult;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsRequest;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsResult;
import com.amazonservices.mws.orders._2013_09_01.model.Order;
import com.amazonservices.mws.orders._2013_09_01.model.OrderItem;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.business.order.OrderManager;
import com.wofu.business.stock.StockManager;
import com.wofu.business.util.PublicUtils;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.Types;
import com.wofu.common.tools.util.log.Log;

public class OrderUtils {

	

	/*
	 * 转入一个订单到接口表
	 */
	public static String createInterOrder(String modulename, Connection conn,
			Order o, List<OrderItem> orderitems,String tradecontactid) throws Exception {
		try {

			String sheetid = "";

			conn.setAutoCommit(false);

			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
					+"values('yongjun','"+sheetid+ "',1 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			int haspostFee = 0;

			String promotionDetails = "";
			
			int paymode=1;
			if (o.getPaymentMethod().equalsIgnoreCase("COD"))
				paymode=2;
			
			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  , sellernick , "
					+ " type , created , shippingtype,payment,"
					+ "  status,paytime,modified ,totalfee   , "
					+ " buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone  , "
					+ " buyeremail , haspostFee, tradefrom ,PromotionDetails,tradeContactid,paymode) "				
					+ " values("+ "'"+ sheetid	+ "','"	+ sheetid+ "','"+Params.username+"','"
					+ o.getAmazonOrderId()+ "','"+ o.getBuyerName()+ "', '"+ o.getSalesChannel()+ "' ,'"
					+ Formatter.format(AmazonUtil.convertToDate(o.getPurchaseDate()),Formatter.DATE_TIME_FORMAT)	+ "'"
					+ ",'"+ o.getShipServiceLevel()+ "','"+o.getOrderTotal().getAmount()+ "', "
					+ "'"+ o.getOrderStatus()
					+ "' ,'"+ Formatter.format(AmazonUtil.convertToDate(o.getLastUpdateDate()),Formatter.DATE_TIME_FORMAT)
					+ "' , '"+ Formatter.format(AmazonUtil.convertToDate(o.getLastUpdateDate()),Formatter.DATE_TIME_FORMAT)
					+ "' , '"+ o.getOrderTotal().getAmount()+ "' ,'"+ o.getBuyerName().replaceAll("'", "")
					+ "' ,'"+ o.getShippingAddress().getName().replaceAll("'", "")+ "' , '"+ o.getShippingAddress().getStateOrRegion()
					+ "', '" +o.getShippingAddress().getCity()+ "' , '"+ o.getShippingAddress().getCounty()+ "', "
					+ "'"+ o.getShippingAddress().getAddressLine1().replaceAll("'", " ")+ "','"
					+ o.getShippingAddress().getPostalCode()+ "' , '"+ o.getShippingAddress().getPhone()
					+ "' , '"	+ o.getShippingAddress().getPhone()+ "', '"
					+ o.getBuyerEmail() + "' , " + String.valueOf(haspostFee)+ ",'" + o.getSalesChannel()
					+ "','" + promotionDetails+ "'," + tradecontactid + ","+paymode+")";

			SQLHelper.executeSQL(conn, sql);
						      
	        int itemnum=orderitems.size();
	        
            for (OrderItem orderitem : orderitems) {
            	if(orderitem.getItemPrice()==null) continue;
            	sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid  , "
            		+ " title , sellernick , buyernick ,  created , "
            		+ "  outeriid , outerskuid , totalfee , payment , "
            		+ " discountfee  , status , num , price ,numiid ) "
            		+" values( '"+ sheetid+ "','"
            		+ orderitem.getOrderItemId()+ "','"	+ sheetid	+ "','"	+ orderitem.getASIN()
            		+ "',"+ "'"	+ orderitem.getTitle()+ "' , '"	+ Params.username+ "', '"
            		+ o.getBuyerName()+ "' ,'"+ Formatter.format(AmazonUtil.convertToDate(o.getPurchaseDate()),Formatter.DATE_TIME_FORMAT)
            		+ "', '"+ orderitem.getSellerSKU()+ "' , '"	+ orderitem.getSellerSKU()+ "' , '"	
            		+ Double.valueOf(orderitem.getItemPrice().getAmount())*orderitem.getQuantityOrdered()
            		+ "' , '"+ Double.valueOf(orderitem.getItemPrice().getAmount())*orderitem.getQuantityOrdered()
            		+ "' , "	+ "'"+ orderitem.getShippingDiscount().getAmount()
            		+ "', '"+ o.getOrderStatus()+ "' , '"+ orderitem.getQuantityOrdered()+ "', '"+ orderitem.getItemPrice().getAmount()
            		 +"'  , '"+ orderitem.getOrderItemId()+"')";
            	SQLHelper.executeSQL(conn, sql);
            	if (!"MustNotSend".equals(orderitem.getInvoiceData().getInvoiceRequirement()))
            	{
            		Log.info(orderitem.getInvoiceData().getInvoiceTitle());
            		sql=new StringBuilder().append("update ns_customerorder set invoiceflag=1,invoicetitle='").append(orderitem.getInvoiceData().getInvoiceTitle()!=null?orderitem.getInvoiceData().getInvoiceTitle():"")
            		.append("' where sheetid='").append(sheetid).append("'").toString();
            		SQLHelper.executeSQL(conn, sql);
            	}
            }
			conn.commit();
			conn.setAutoCommit(true);

			Log.info(modulename, "生成订单【" + o.getAmazonOrderId() + "】接口数据成功，接口单号【"
					+ sheetid + "】");

			return sheetid;

		} catch (Exception e1) {
			if (!conn.getAutoCommit())
				try {
					conn.rollback();
				} catch (Exception e2) {
				}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e3) {
			}
			e1.printStackTrace();
			throw new JException("生成订单【" + o.getAmazonOrderId() + "】接口数据失败!"
					+ e1.getMessage());
		}
	}

	public static Order getOrderByID(String serviceurl,String accesskeyid,String secretaccesskey,
			String applicationname,String applicationversion,String AmazonOrderId) throws Exception
	{
		Order o =null;
		
		MarketplaceWebServiceOrdersConfig config = new MarketplaceWebServiceOrdersConfig();
		config.setServiceURL(serviceurl);
	
		
		MarketplaceWebServiceOrders service = new MarketplaceWebServiceOrdersClient(
				accesskeyid, 
				secretaccesskey, 
				applicationname, 
				applicationversion, 
				config);
		
		ArrayList<String> ids=new ArrayList<String>();
		ids.add(AmazonOrderId);
		
		//OrderIdList orderids=new OrderIdList();
		//orderids.setId(ids);
		GetOrderRequest request = new GetOrderRequest();
        request.setAmazonOrderId(ids);
        GetOrderResponse response = service.getOrder(request);
         
       
        if (response.isSetGetOrderResult())
        {
        	GetOrderResult  getOrderResult =response.getGetOrderResult();
			  if (getOrderResult.isSetOrders()) {
		          
				  java.util.List<Order> orderList = getOrderResult.getOrders();
		            
		           o = orderList.get(0);
		            
		       }
		
		}
        
        return o;
	}
	
	public static List<OrderItem> getOrderItemList(String serviceurl,String accesskeyid,String secretaccesskey,
			String applicationname,String applicationversion,String sellerid,String AmazonOrderId) throws Exception
	{
		
		java.util.List<OrderItem> orderItems =null;
		
		MarketplaceWebServiceOrdersConfig config = new MarketplaceWebServiceOrdersConfig();
		config.setServiceURL(serviceurl);
	
		MarketplaceWebServiceOrders service = new MarketplaceWebServiceOrdersClient(
				accesskeyid, 
				secretaccesskey, 
				applicationname, 
				applicationversion, 
				config);

		ListOrderItemsRequest request = new ListOrderItemsRequest();
        request.setSellerId(sellerid);
        request.setAmazonOrderId(AmazonOrderId);
        ListOrderItemsResponse response = service.listOrderItems(request);
         
       
        if (response.isSetListOrderItemsResult())
        {
        	 ListOrderItemsResult  listOrderItemResult =response.getListOrderItemsResult();
        	  if (listOrderItemResult.isSetOrderItems()) {
                  
                   // orderitemlist orderitemlist = listOrderItemResult.getOrderItems();
        		  orderItems = listOrderItemResult.getOrderItems();
                   //orderItems = orderitemlist.getOrderItem();
                    
               }
		
		}
        
        return orderItems;
	}


}
