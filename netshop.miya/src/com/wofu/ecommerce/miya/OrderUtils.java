package com.wofu.ecommerce.miya;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.miya.Order;
import com.wofu.ecommerce.miya.OrderItem;
import com.wofu.ecommerce.miya.Params;
import com.wofu.ecommerce.miya.RefundDetail;
import com.wofu.ecommerce.miya.RefundItem;
import com.wofu.ecommerce.miya.utils.Utils;



public class OrderUtils {
	//转入订单到接口表
	
	public static void createInterOrder(Connection conn,
			Order o, String tradecontactid,String username, JSONObject data) throws Exception {
		try {
			if(o.getOrder_state().equals("2")){
				o.setOrder_state("等待发货");
			}
			
			String sheetid = "";
			int paymode=1;
			conn.setAutoCommit(false);

			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals("")){
				throw new JSQLException(sql, "取接口单号出错!");
				}

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid+ "',1 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			JSONObject address_info = new JSONObject(o.getAddress_info());
			System.out.println(o.getAddress_info());
			System.out.println(address_info.optString("dst_name"));
			//把订单数据写入ns_customerorder
			sql = "insert into ns_customerorder(" 
				+ " CustomerOrderId ,SheetID  ,Owner,tid , created  , "
				+ " payment , status ,buyermemo ,sellermemo , paytime , "
				+ " modified,totalfee , postfee , buyernick , receivername ,"
				+ " receiverstate  ,receivercity ,  receiverdistrict ,  receiveraddress ,"
				+ " receivermobile , receiverphone,tradeContactid,paymode)"
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			Object[] sqlv = {
				sheetid,
				sheetid,
				username,
				o.getOrder_id(),
				o.getOrder_time(),
				//5个
				o.getPay_price(),
				o.getOrder_state(),
				o.getOrder_remark(),
				"",
				o.getOrder_time(),
				//5个
				o.getModify_time(),
				o.getOrder_payment(),
				o.getShip_price(),
				address_info.optString("dst_name"),
				address_info.optString("dst_name"),
				//5个
				address_info.optString("dst_province"),
				address_info.optString("dst_city"),
				address_info.optString("dst_area"),
				address_info.optString("dst_street")+address_info.optString("dst_address"),
				//4个
				address_info.optString("dst_mobile"),
				address_info.optString("dst_tel"),
				tradecontactid,
				paymode,
				//4个
			};
			SQLHelper.executePreparedSQL(conn, sql, sqlv);
			
			OrderItem item=new OrderItem();
			JSONArray orderItemList =data.getJSONArray("item_info_list");
			System.out.println("订单商品数量"+orderItemList.length());
			for (int q=0;q<orderItemList.length();q++) {
				JSONObject orderItem =orderItemList.getJSONObject(q);
				item.setObjValue(item, orderItem);
				sql = "insert into ns_orderitem(" 
					+ "	CustomerOrderId , orderItemId  ,oid, SheetID  ,skuid , "
					+ " title , sellernick , created ,outerskuid , totalfee , "
					+ " payment ,num , price ,iid)"
					+ "	values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			Object[] sqlItem = {
				sheetid,
				o.getOrder_id()+"_"+item.getItem_id(),
				o.getOrder_id(),
				sheetid,
				item.getSku_id(),
				
				item.getItem_name(),
				username,
				o.getOrder_time(),
				item.getBarcode(),
				item.getSale_price(),
				
				item.getPay_price(),
				item.getItem_total(),
				item.getSale_price(),
				item.getItem_id(),
			};
			SQLHelper.executePreparedSQL(conn, sql, sqlItem);
			}
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("生成订单【" + o.getOrder_id() + "】接口数据成功，接口单号【"+ sheetid + "】");
			//订单打单确认
			confirOrder(o.getOrder_id());

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
			throw new JException("生成订单【" + o.getOrder_id() + "】接口数据失败,错误信息："+ e1.getMessage());
		}
	}
	/*
	 *
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
     
        
		String responseOrderData = Utils.sendByPost(orderparams,Params.app_secret,Params.url);
		
		

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
	 */

	static void confirOrder(String orderid) throws Exception {
		Map<String, String> confirmlistparams = new HashMap<String, String>();
        //系统级参数设置
		confirmlistparams.put("method", "mia.order.confirm");
		confirmlistparams.put("vendor_key", Params.vendor_key);
		confirmlistparams.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
		confirmlistparams.put("version", Params.ver);
		//应用级输入参数
		confirmlistparams.put("order_id", orderid);
		String responseConfirmListData = Utils.sendByPost(confirmlistparams, Params.secret_key, Params.url);
	    Log.info(Utils.Unicode2GBK(responseConfirmListData));
	    JSONObject confirmData = new JSONObject(responseConfirmListData);
		//判断是否已经打单或者打单成功
		int code = confirmData.optInt("code");
		String msg = confirmData.optString("msg");
		//预料到网络问题，有时会打单失败。所以打单两遍；
		if(code!=200&&code!=182){
			Log.error(orderid+"打单失败失败，暂停3秒再试两遍：", msg);
			for(int i=0;i<2;i++){
				Thread.sleep(3000);
				confirOrder(orderid);
			}
		}
		
	}
}
