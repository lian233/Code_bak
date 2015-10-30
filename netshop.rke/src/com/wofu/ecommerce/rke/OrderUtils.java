package com.wofu.ecommerce.rke;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Element;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.rke.utils.Utils;



public class OrderUtils {
	/*
	 * 转入一个订单到接口表
	 */
	public static String createInterOrder(Connection conn,
			Order o, String tradecontactid,String username) throws Exception {
		try {

			String sheetid = "";
			
			int paymode=1;
			
			conn.setAutoCommit(false);

			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid+ "',1 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			Log.info("size: "+o.getOrderItems().size());
			float paymanet = 0.0f;
			for (int i=0;i<o.getOrderItems().size();i++) {
				
				OrderItem item = (OrderItem) o.getOrderItems().get(i);

				String orderItemId ="".equals(item.getProduct_id())?sheetid+item.getSku():sheetid+item.getProduct_id();
				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  ,oid, SheetID  ,skuid , "
						+ " title , sellernick , created , "
						+ "  outerskuid , totalfee , payment ,num , price ) values( "
						+ "'"+ sheetid+ "','"+ orderItemId+ "','"+item.getProduct_id()+"','"+ sheetid+ "','"+item.getGoods_sn()+"','"
						+ item.getGoods_name()+ "' , '"+ username+ "', '"+Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)
						+ "', '"+ item.getSku()+ "' , '"+ item.getGoods_price()*item.getGoods_number()
						+ "' , '"+item.getGoods_price()*item.getGoods_number()+"',"				
						+ item.getGoods_number()+ " , '"+ item.getGoods_price()+"')";
				SQLHelper.executeSQL(conn, sql);
				paymanet+=item.getGoods_price()*item.getGoods_number();
			}
			
			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  , sellernick , paymode,"
					+ "  created ,  payment ,  status  , paytime ,  modified , "  //sellernick为u8编码
					+ " totalfee , postfee, buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone , tradefrom,tradeContactid) "
					+ " values('"+ sheetid+ "','"+ sheetid+ "','"+username+"','"+ o.getOrder_sn()
					+ "','"+ o.getUser_sn()+ "', "+paymode+",'"+Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)+"',"+ paymanet +", '"
					+ o.getOrder_status()+ "' ,'"+Formatter.format(o.getPay_time(), Formatter.DATE_TIME_FORMAT)+"',"
					+"'"+Formatter.format(o.getPay_time(), Formatter.DATE_TIME_FORMAT)+ "' , "+ o.getOrder_amount()+ " , '"+o.getFee()+ "'"
					+ ",'"	+ username+ "' ,'"+ o.getConsignee()+ "' , '"
					+ o.getProvince()+ "', '"	+ o.getCity()+ "' , '"+o.getDistrict()+"', "
					+ "'"+ o.getAddress()+ "','"+ o.getZipcode()+ "' , '"
					+ o.getMobile()+ "' , '"+ o.getTel()+ "','经销'," + tradecontactid + ")";

			SQLHelper.executeSQL(conn, sql);
			

			
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("生成订单【" + o.getOrder_sn() + "】接口数据成功，接口单号【"+ sheetid + "】");
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
			throw new JException("生成订单【" + o.getOrder_sn() + "】接口数据失败,错误信息："+ e1.getMessage());
		}
	}
	/**
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

	}**/
	/**
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
	**/
	//根据Element创建订单对象
	public static Order getOrderByElement(Element order) throws Exception{
		Order o = new Order();
		Date add_time = new Date(Integer.parseInt(DOMHelper.getSubElementVauleByName(order, "add_time"))*1000L);
		o.setAdd_time(add_time);
		o.setOrder_sn(DOMHelper.getSubElementVauleByName(order, "order_sn"));
		o.setUser_sn(DOMHelper.getSubElementVauleByName(order, "user_sn"));//u8编码
		o.setUser_name(DOMHelper.getSubElementVauleByName(order, "user_name"));
		o.setEmail(DOMHelper.getSubElementVauleByName(order, "email"));
		o.setZipcode(DOMHelper.getSubElementVauleByName(order, "zipcode"));
		o.setPostscript(DOMHelper.getSubElementVauleByName(order, "postscript"));
		o.setOrder_amount(Float.parseFloat(DOMHelper.getSubElementVauleByName(order, "order_amount")));
		o.setPreferential(Float.parseFloat(DOMHelper.getSubElementVauleByName(order, "preferential")));
		o.setProvince(DOMHelper.getSubElementVauleByName(order, "province"));
		o.setCity(DOMHelper.getSubElementVauleByName(order, "city"));
		o.setDistrict(DOMHelper.getSubElementVauleByName(order, "district"));
		o.setTo_buyer(DOMHelper.getSubElementVauleByName(order, "to_buyer"));
		o.setGoods_amount(Float.parseFloat(DOMHelper.getSubElementVauleByName(order, "goods_amount")));
		o.setShipping_name(DOMHelper.getSubElementVauleByName(order, "shipping_name"));
		o.setAddress(DOMHelper.getSubElementVauleByName(order, "address"));
		o.setMobile(DOMHelper.getSubElementVauleByName(order, "mobile"));
		o.setConsignee(DOMHelper.getSubElementVauleByName(order, "consignee"));
		o.setTel(DOMHelper.getSubElementVauleByName(order, "tel"));
		o.setOrder_status(DOMHelper.getSubElementVauleByName(order, "order_status"));
		o.setShipping_status(DOMHelper.getSubElementVauleByName(order, "shipping_status"));
		o.setPay_status(DOMHelper.getSubElementVauleByName(order, "pay_status"));
		o.setShipping_print(DOMHelper.getSubElementVauleByName(order, "shipping_print"));
		String pay_timeTemp = DOMHelper.getSubElementVauleByName(order, "pay_time");
		if(!"".equals(pay_timeTemp)){
			Date pay_time = new Date(Integer.parseInt(DOMHelper.getSubElementVauleByName(order, "pay_time"))*1000L);
			o.setPay_time(pay_time);
		}
		
		o.setFee(Float.parseFloat(DOMHelper.getSubElementVauleByName(order, "fee")));
		String confirm_timeTemp = DOMHelper.getSubElementVauleByName(order, "confirm_time");
		if(!"".equals(confirm_timeTemp)){
			Date confirm_time =  new Date(Integer.parseInt(DOMHelper.getSubElementVauleByName(order, "confirm_time"))*1000L);
			o.setConfirm_time(confirm_time);
		}
		String shipping_timeTemp = DOMHelper.getSubElementVauleByName(order, "shipping_time");
		if(!"".equals(shipping_timeTemp)){
			Date shipping_time = new Date(Integer.parseInt(DOMHelper.getSubElementVauleByName(order, "shipping_time"))*1000L);
			o.setShipping_time(shipping_time);
		}
		o.setInv_payee(DOMHelper.getSubElementVauleByName(order, "inv_payee"));
		o.setInv_content(DOMHelper.getSubElementVauleByName(order, "inv_content"));
		Element[] items = DOMHelper.getSubElementsByName(order, "item");
		for(int i=0;i<items.length;i++){
			Element item= items[i];
			OrderItem oItem = new OrderItem();
			oItem.setGoods_id(DOMHelper.getSubElementVauleByName(item, "goods_id"));
			oItem.setGoods_name(DOMHelper.getSubElementVauleByName(item, "goods_name"));
			//oItem.setGoods_sn("sku: "+DOMHelper.getSubElementVauleByName(item, "goods_sn"));
			//Log.info(DOMHelper.getSubElementVauleByName(item, "sku"));
			oItem.setSku(DOMHelper.getSubElementVauleByName(item, "sku"));
			oItem.setGoods_attr(DOMHelper.getSubElementVauleByName(item, "goods_attr"));
			oItem.setGoods_price(Float.parseFloat(DOMHelper.getSubElementVauleByName(item, "goods_price")));
			oItem.setMarket_price(Float.parseFloat(DOMHelper.getSubElementVauleByName(item, "market_price")));
			oItem.setGoods_number(Integer.parseInt(DOMHelper.getSubElementVauleByName(item, "goods_number")));
			oItem.setGoods_attr_id(DOMHelper.getSubElementVauleByName(item, "goods_attr_id"));
			oItem.setProduct_id(DOMHelper.getSubElementVauleByName(item, "product_id"));
			oItem.setProduct_sn(DOMHelper.getSubElementVauleByName(item, "product_sn"));
			oItem.setOrder_sn(DOMHelper.getSubElementVauleByName(item, "order_sn"));
			oItem.setOrder_status(DOMHelper.getSubElementVauleByName(item, "order_status"));
			o.getOrderItems().add(oItem);
		}
		return o;
	}
	
	
}
