package com.wofu.ecommerce.yhd;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.yhd.utils.Utils;



public class OrderUtils {
	/*
	 * 转入一个订单到接口表
	 */
	public static String createInterOrder(Connection conn,
			Order o, String tradecontactid,String username) throws Exception {
		try {

			String sheetid = "";
			
			int paymode=o.getPayServiceType();
			
			if ( paymode ==2 ||  paymode ==5 ||  paymode ==12) paymode=2;
			else if(paymode !=1) paymode=0;
			
			int invoiceflag=0;
			
			String invoicetitle="";
			
			if (o.getOrderNeedInvoice()!=0)
			{
				invoiceflag=1;
				invoicetitle=o.getInvoiceTitle();
			}
			
			

			conn.setAutoCommit(false);

			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid+ "',1 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			
			String deliveryremark=o.getDeliveryRemark()==null || o.getDeliveryRemark().equals("null")?"":o.getDeliveryRemark().replaceAll("'", "");
			String merchantremark=o.getMerchantRemark()==null || o.getMerchantRemark().equals("null")?"":o.getMerchantRemark().replaceAll("'", "");


			String moblie=o.getGoodReceiverMoblie()!=null?o.getGoodReceiverMoblie():"";
			String phone=o.getGoodReceiverPhone()!=null?o.getGoodReceiverPhone():"";
			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  , sellernick , paymode,invoiceflag,invoicetitle,"
					+ "  created ,  payment ,  status  , buyermemo , sellermemo  , paytime ,  modified , "
					+ " totalfee , postfee, buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone , tradefrom,tradeContactid,payfee) "
					
					+ " values('"+ sheetid+ "','"+ sheetid+ "','"+username+"','"+ o.getOrderCode()
					+ "','"+ username+ "', "+paymode+","+invoiceflag+",'"+invoicetitle+"','"+Formatter.format(o.getOrderCreateTime(),Formatter.DATE_TIME_FORMAT)+"',"+ o.getOrderAmount()+ ", '"
					+ o.getOrderStatus()+ "' , '"+deliveryremark + "' , '"+ merchantremark+ "','"+Formatter.format(o.getOrderPaymentConfirmDate(), Formatter.DATE_TIME_FORMAT)+"',"
					+"'"+Formatter.format(o.getUpdateTime(), Formatter.DATE_TIME_FORMAT)+ "' , "+ o.getProductAmount()+ " , '"+o.getOrderDeliveryFee()+ "'"
					+ ",'"	+ o.getEndUserId()+ "' ,'"+ o.getGoodReceiverName()+ "' , '"
					+ o.getGoodReceiverProvince()+ "', '"	+ o.getGoodReceiverCity()+ "' , '"+o.getGoodReceiverCounty()+"', "
					+ "'"+ o.getGoodReceiverAddress()+ "','"+ o.getGoodReceiverPostCode()+ "' , '"
					+ moblie+ "' , '"+ phone+ "','yhd'," + tradecontactid + ",'"+o.getCollectOnDeliveryAmount()+"')";

			SQLHelper.executeSQL(conn, sql);
			


			for (int i=0;i<o.getOrderItemList().getRelationData().size();i++) {
				
				OrderItem item = (OrderItem) o.getOrderItemList().getRelationData().get(i);


				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  ,oid, SheetID  ,skuid, itemmealname , "
						+ " title , sellernick , created , "
						+ "  outerskuid , totalfee , payment ,num , price ) values( "
						+ "'"+ sheetid+ "','"+ sheetid+ item.getId()+ "','"+item.getId()+"','"+ sheetid+ "','0','"+ item.getProductCName()
						+ "', '"+ item.getProductCName()+ "' , '"+ username+ "', '"+Formatter.format(o.getOrderCreateTime(),Formatter.DATE_TIME_FORMAT)
						+ "', '"+ item.getOuterId()+ "' , '"+ item.getOrderItemAmount()
						+ "' , '"+item.getOrderItemAmount()+"',"				
						+ item.getOrderItemNum()+ " , '"+ item.getOrderItemPrice()+"')";
				SQLHelper.executeSQL(conn, sql);		
			}
		

			conn.commit();
			conn.setAutoCommit(true);

			Log.info("生成订单【" + o.getOrderCode() + "】接口数据成功，接口单号【"+ sheetid + "】");

			return sheetid;

		} catch (JSQLException e1) {
			if (!conn.getAutoCommit())
				try {
					conn.rollback();
				} catch (Exception e2) {
				}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e3) {
			}
			throw new JException("生成订单【" + o.getOrderCode() + "】接口数据失败,错误信息："+ e1.getMessage());
		}
	}
	
	public static void getRefund(Connection conn,String tradecontactid,Order o)	throws Exception {

			
		
			String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="
					+ tradecontactid;
			String inshopid = SQLHelper.strSelect(conn, sql);

			conn.setAutoCommit(false);
			
			for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();

				sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
				String sheetid = SQLHelper.strSelect(conn, sql);
				if (sheetid.trim().equals(""))
					throw new JSQLException(sql, "取接口单号出错!");
	
				// 加入到通知表
				sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
						+"values('yongjun','"+ sheetid+ "',2 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
				SQLHelper.executeSQL(conn, sql);
	
				sql = "insert into ns_Refund(SheetID , tid,RefundID , Oid , AlipayNo , "
						+ "BuyerNick , Created , Modified , OrderStatus , Status , GoodStatus , "
						+ " HasGoodReturn ,RefundFee , Payment , Reason,Description , Title ,"
						+ "Price , Num , GoodReturnTime , Sid , "
						+ " TotalFee ,  OuterIid , OuterSkuId , CompanyName , "
						+ "Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
						+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
				
				Object[] sqlv = {
						sheetid,
						o.getOrderCode(),
						String.valueOf(o.getOrderId())+String.valueOf(item.getId()),
						String.valueOf(item.getId()),
						o.getEndUserId(),
						o.getEndUserId(),
						o.getOrderCreateTime(),
						o.getUpdateTime(),
						o.getOrderStatus(),
						o.getOrderStatus(),
						o.getOrderStatus(),
						1,
						item.getOrderItemAmount(),
						item.getOrderItemAmount(),
						o.getDeliveryRemark(),
						o.getDeliveryRemark(),
						item.getProductCName(),
						item.getOrderItemPrice(),
						item.getOrderItemNum(),
						item.getProcessFinishDate(),
						"",
						item.getOrderItemAmount(),
						item.getOuterId(),
						item.getOuterId(),
						"",
						"",
						o.getGoodReceiverProvince() + " " + o.getGoodReceiverCity() + " "
								+ o.getGoodReceiverCounty()+ " "
								+ o.getGoodReceiverAddress(), inshopid,
						o.getOrderCode(), o.getGoodReceiverName(),
						o.getGoodReceiverPhone() + " " + o.getGoodReceiverMoblie(),
						o.getEndUserId() };
	
	
				SQLHelper.executePreparedSQL(conn, sql, sqlv);
				
			}

	

			Log.info("生成退货单成功,订单号:"+ o.getOrderCode()+ " 订单状态："+o.getOrderStatus()				
					+ " 订单创建时间:"+Formatter.format(o.getOrderCreateTime(),Formatter.DATE_TIME_FORMAT));

			conn.commit();
			conn.setAutoCommit(true);

	}
	
	public static void createRefund(Connection conn,RefundDetail r,
			int tradecontactid,String app_key,String token,String format,String ver) throws Exception 
	{
		String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="+ tradecontactid;
		String inshopid = SQLHelper.strSelect(conn, sql);

		
		
		Map<String, String> orderparams = new HashMap<String, String>();
        //系统级参数设置
		orderparams.put("appKey", app_key);
		orderparams.put("sessionKey", token);
		orderparams.put("format", format);
		orderparams.put("method", "yhd.order.detail.get");
		orderparams.put("ver", ver);
		orderparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
        
		orderparams.put("orderCode", r.getOrderCode());
     
        
		String responseOrderData = Utils.sendByPost(orderparams,Params.app_secret,Params.url);
        
		//Log.info("退货详情: "+responseOrderData);
		JSONObject responseorder=new JSONObject(responseOrderData);
		
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
			
			throw new JException(errdesc);						
		}
		
		
		JSONObject orderdetail=responseorder.getJSONObject("response").getJSONObject("orderInfo").getJSONObject("orderDetail");
		
		
		Order o=new Order();
		o.setObjValue(o, orderdetail);
						
		
		JSONArray orderItemList=responseorder.getJSONObject("response").getJSONObject("orderInfo").getJSONObject("orderItemList").getJSONArray("orderItem");
		
		o.setFieldValue(o, "orderItemList", orderItemList);
		
		conn.setAutoCommit(false);
		
		for (Iterator it=r.getRefundItemList().getRelationData().iterator();it.hasNext();)
		{

			RefundItem item=(RefundItem) it.next();
			
			sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			String sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
					+"values('yongjun','"+ sheetid+ "',2 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			sql = "insert into ns_Refund(SheetID ,RefundID , Oid  , "
					+ "BuyerNick , Created , Modified ,  Status , GoodStatus , "
					+ " HasGoodReturn ,RefundFee , Payment , Reason,Description , Title ,"
					+ "Price , Num , GoodReturnTime  , "
					+ " TotalFee ,  OuterIid , OuterSkuId , CompanyName ,sid, "
					+ "Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele)"
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			String outerskuid="";
			for (Iterator itorder=o.getOrderItemList().getRelationData().iterator();itorder.hasNext();)
			{
				OrderItem orderitem=(OrderItem) itorder.next();
				if (orderitem.getId()==item.getOrderItemId())
					outerskuid=orderitem.getOuterId();
			}
						

			Object[] sqlv = {
					sheetid,
					String.valueOf(r.getRefundCode()),
					String.valueOf(item.getOrderItemId()),
					r.getReceiverName(),
					r.getApplyDate(),
					r.getApplyDate(),
					String.valueOf(r.getRefundStatus()),
					String.valueOf(r.getRefundStatus()),
					1,
					Double.valueOf(item.getOrderItemPrice()*item.getProductRefundNum()),
					Double.valueOf(item.getOrderItemPrice()*item.getProductRefundNum()),
					r.getReasonMsg(),
					r.getRefundProblem(),
					item.getProductCname(),
					item.getOrderItemPrice(),
					item.getProductRefundNum(),
					r.getSendBackDate(),
					Double.valueOf(item.getOrderItemPrice()*item.getProductRefundNum()),
					outerskuid,
					outerskuid,
					r.getExpressName(),
					r.getExpressNbr(),
					r.getSendBackAddress(),
					r.getReceiverAddress(), inshopid,
					r.getOrderCode(), r.getReceiverName(),
					r.getReceiverPhone()};
		

			SQLHelper.executePreparedSQL(conn, sql, sqlv);


			Log.info( "接口单号:"+ sheetid	+ " 订单号:"	+ r.getOrderCode()+ " 状态："+ r.getRefundStatus()+ "退货申请时间:"
					+ Formatter.format(r.getApplyDate(),
							Formatter.DATE_TIME_FORMAT));
		}
		
		conn.commit();
		conn.setAutoCommit(true);

	}
	public static Order getOrderByID(String orderCode,String appKey,String token,String format,String ver) throws Exception
	{
		Order o=new Order();
		
		Map<String, String> orderparams = new HashMap<String, String>();
        //系统级参数设置
		//系统级参数设置
		orderparams.put("appKey", appKey);
		orderparams.put("sessionKey", token);
		orderparams.put("format", format);
		orderparams.put("method", "yhd.order.detail.get");
		orderparams.put("ver", ver);
		orderparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
        
		orderparams.put("orderCode", orderCode);
     
        System.out.println("获取信息中");
        Thread.sleep(1000*Params.getDetailedTime);
		String responseOrderData = Utils.sendByPost(orderparams,Params.app_secret,Params.url);
		System.out.println("获取详细信息"+responseOrderData);
		JSONObject responseorder=new JSONObject(responseOrderData);
		
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

			throw new JException(errdesc);						
		}
		
		
		JSONObject orderdetail=responseorder.getJSONObject("response").getJSONObject("orderInfo").getJSONObject("orderDetail");
	
		
		
		o.setObjValue(o, orderdetail);
						

		
		JSONArray orderItemList=responseorder.getJSONObject("response").getJSONObject("orderInfo").getJSONObject("orderItemList").getJSONArray("orderItem");
		
		o.setFieldValue(o, "orderItemList", orderItemList);
		
		return o;
	}
	
}
