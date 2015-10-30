package com.wofu.ecommerce.s;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.s.utils.Utils;




public class OrderUtils {
	/*
	 * 转入一个订单到接口表
	 */
	public static String createInterOrder(Connection conn,
			Order o, String tradecontactid,String username) throws Exception {
		try {

			String sheetid = "";
			
			int paymode=1/*o.getPayServiceType()*/;
//			
//			if ( paymode ==2 ||  paymode ==5 ||  paymode ==12) paymode=2;
//			else if(paymode !=1) paymode=0;
			
			int invoiceflag=0;
//			
			String invoicetitle="";
			
//			if (o.getOrderNeedInvoice()!=0)
//			{
//				invoiceflag=1;
//				invoicetitle=o.getInvoiceTitle();
//			}
			
			

			conn.setAutoCommit(false);

			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid+ "',1 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			
			String deliveryremark="";/*o.getDeliveryRemark()==null || o.getDeliveryRemark().equals("null")?"":o.getDeliveryRemark().replaceAll("'", "")*/;
			String merchantremark="";/*o.getMerchantRemark()==null || o.getMerchantRemark().equals("null")?"":o.getMerchantRemark().replaceAll("'", "")*/;


			String moblie=o.getRcvTel()!=null?o.getRcvTel():"";
			String phone=o.getRcvTel()!=null?o.getRcvTel():"";
			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  , sellernick , paymode,invoiceflag,invoicetitle,"
					+ "  created ,  payment ,  status  , buyermemo , sellermemo  , paytime ,  modified , "
					+ " totalfee , postfee, buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone , tradefrom,tradeContactid,payfee) "
					
					+ " values('"+ sheetid+ "','"+ sheetid+ "','"+username+"','"+ o.getVendorOrderNo()
					+ "','"+ username+ "', "+paymode+","+invoiceflag+",'"+invoicetitle+"','"+Formatter.format(o.getUpdateDate(),Formatter.DATE_TIME_FORMAT)+"',"+ o.getGoodsPrice()+ ", '"
					+ o.getOrderStatus()+ "' , '"+deliveryremark + "' , '"+ merchantremark+ "','"+Formatter.format(o.getSubmitDate(), Formatter.DATE_TIME_FORMAT)+"',"
					+"'"+Formatter.format(o.getUpdateDate(), Formatter.DATE_TIME_FORMAT)+ "' , "+ o.getSalePrice()+ " , '"+0/*运费*/+ "'"
					+ ",'"	+ o.getSellerId()+ "' ,'"+ o.getRcvName()+ "' , '"
					+ o.getRcvAddrDetail()+ "', '"	+ ""+ "' , '"+""+"', "
					+ "'"+ o.getRcvAddrDetail()+ "','"+ o.getRcvAddrId()+ "' , '"
					+ moblie+ "' , '"+ phone+ "','yhd'," + tradecontactid + ",'"+""/*货到付款金额*/+"')";

			SQLHelper.executeSQL(conn, sql);
			


			for (int i=0;i<o.getOrderItemList().getRelationData().size();i++) {
				
				OrderItem item = (OrderItem) o.getOrderItemList().getRelationData().get(i);


				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  ,oid, SheetID  ,skuid, itemmealname , "
						+ " title , sellernick , created , "
						+ "  outerskuid , totalfee , payment ,num , price ) values( "
						+ "'"+ sheetid+ "','"+ sheetid+ item.getVendorOrderDetNo()+ "','"+item.getVendorOrderDetNo()+"','"+ sheetid+ "','"+item.getVendorSkuId()+"','"+ ""/*商品名*/
						+ "', '"+ ""/*商品名*/+ "' , '"+ username+ "', '"+Formatter.format(o.getUpdateDate(),Formatter.DATE_TIME_FORMAT)
						+ "', '"+ item.getVendorSkuId()+ "' , '"+ item.getSalePrice()
						+ "' , '"+item.getSalePrice()+"',"				
						+ ""/*第几项*/+ " , '"+ item.getSalePrice()+"')";
				SQLHelper.executeSQL(conn, sql);		
			}
		

			conn.commit();
			conn.setAutoCommit(true);
			
			Log.info("生成订单【" + o.getVendorOrderNo() + "】接口数据成功，接口单号【"+ sheetid + "】");

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
			throw new JException("生成订单【" + o.getVendorOrderNo()+ "】接口数据失败,错误信息："+ e1.getMessage());
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
						o.getVendorOrderNo(),
						String.valueOf(o.getVendorOrderNo())+String.valueOf(item.getVendorSkuId()),
						String.valueOf(item.getVendorSkuId()),
						o.getVendorId(),
						o.getVendorId(),
						o.getUpdateDate(),
						o.getUpdateDate(),
						o.getOrderStatus(),
						o.getOrderStatus(),
						o.getOrderStatus(),
						1,
						item.getSalePrice(),
						item.getSalePrice(),
						/*o.getDeliveryRemark()*/"",
						/*o.getDeliveryRemark()*/"",
						item.getVendorSkuId(),
						item.getSalePrice(),
						item.getVendorSkuId(),
						/*item.getProcessFinishDate()*/"",
						"",
						item.getSalePrice(),
						item.getVendorSkuId(),
						item.getVendorSkuId(),
						"",
						"",
						o.getRcvAddrDetail()/* + " " + o.getGoodReceiverCity() + " "
								+ o.getGoodReceiverCounty()+ " "
								+ o.getGoodReceiverAddress()*/, inshopid,
						o.getVendorOrderNo(), o.getRcvName(),
						o.getRcvTel(),
						o.getVendorId() };
	
	
				SQLHelper.executePreparedSQL(conn, sql, sqlv);
				
			}

	

			Log.info("生成退货单成功,订单号:"+ o.getVendorOrderNo()+ " 订单状态："+o.getOrderStatus()				
					+ " 订单创建时间:"+Formatter.format(o.getUpdateDate(),Formatter.DATE_TIME_FORMAT));

			conn.commit();
			conn.setAutoCommit(true);

	}
	
	public static void createRefund(Connection conn,RefundDetail r,
			int tradecontactid,String app_key,String token,String format,String ver) throws Exception 
	{
		String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="+ tradecontactid;
		String inshopid = SQLHelper.strSelect(conn, sql);
		UTF8_transformer utf8_transformer=new UTF8_transformer();
		String method="scn.vendor.order.full.get";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//		Map<String, String> orderparams = new HashMap<String, String>();
//        //系统级参数设置
//		orderparams.put("appKey", app_key);
//		orderparams.put("sessionKey", token);
//		orderparams.put("format", format);
//		orderparams.put("method", "yhd.order.detail.get");
//		orderparams.put("ver", ver);
//		orderparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
//        
//		orderparams.put("orderCode", r.getOrderCode());
     
        
		//String responseOrderData = Utils.sendByPost(orderparams,Params.app_Secret,Params.url);
		/***data部分***/
		JSONObject data=new JSONObject();
		data.put("Fields",Params.Fields);	
		data.put("StartUpdateDate", Params.StartUpdateDate); //订单更新开始时间
		data.put("EndUpdateDate", Params.EndUpdateDate);   //订单更新结束时间
		data.put("StartSubmitDate", Params.StartSubmitDate); //订单提交开始时间
		data.put("EndSubmitDate", Params.EndSubmitDate);   //订单提交结束时间
		data.put("SellerId", Params.SellerId);        //分销商ID(外部商家该值留空)
		data.put("SellerOrderNo", Params.SellerOrderNo);   //分销商订单号，由服务器返回的ID提供
		data.put("VendorOrderNo", Params.VendorOrderNo);   //供货商订单号
		data.put("OrderStatus", Params.OrderStatus);     //订单状态(1-未处理 2-已确认 3-已发货 4-已作废)
		data.put("PageNo", Params.PageNo);          //页码
		data.put("PageSize", Params.PageSize);        //每页条数。默认40，最大100
		/**签名部分***/
		String sign=Params.app_Secret
		+"app_key"+Params.app_key
		+"data"+data.toString()
		+"format"+Params.format
		+"method"+method
		+"timestamp"+df.format(new Date())
		+"v"+ver;
		sign=MD5Util.getMD5Code(sign.getBytes());
		/***合并为输出语句****/
		String output_to_server=
			"data="+utf8_transformer.getUTF8String(data.toString())+"&"+
			"method="+utf8_transformer.getUTF8String(method)+"&"+
			"v="+Params.ver+"&"+
			"app_key="+utf8_transformer.getUTF8String(Params.app_key)+"&"+
			"format=json"+"&"+
			"timestamp="+utf8_transformer.getUTF8String(df.format(new Date()))+"&"+
			"sign="+sign.toUpperCase();		
		String responseOrderData = Utils.sendByPost(Params.url,output_to_server);
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
				if (orderitem.getVendorSkuId()==String.valueOf(item.getOrderItemId()))
					outerskuid=orderitem.getVendorSkuId();
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
	public static Order getOrderByID(String params) throws Exception
	{
		Order o=new Order();  
		String responseOrderData = Utils.sendByPost(Params.url,params);
		JSONObject responseorder=new JSONObject(responseOrderData);
		if(!responseorder.get("ErrCode").equals(null) || !responseorder.get("ErrMsg").equals(null))
		{
			String errdesc="";
			errdesc=errdesc+" "+responseorder.get("ErrCode").toString()+" "+responseorder.get("ErrMsg").toString(); 
			throw new JException(errdesc);	
		}
//		JSONArray orderlist=responseorder.getJSONArray("Result");
//		JSONObject orderdetail=orderlist.getJSONObject(0);
		JSONObject orderdetail=responseorder.getJSONArray("Result").getJSONObject(0);
		o.setObjValue(o, orderdetail);
		JSONArray OrderDets=responseorder.getJSONArray("Result")/*.getJSONObject(0).getJSONArray("OrderDets")*/;
		o.setFieldValue(o, "Result", OrderDets);
		return o;
	}
	
}
