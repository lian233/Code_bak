package com.wofu.ecommerce.weipinhui;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weipinhui.util.CommHelper;

public class OrderUtils {
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	//状态对照表
	private static String[][] OrderStatusList = new String[][]{
		{"0","未支付订单"},
		{"1","待审核订单（已支付/未处理）"},
		{"10","订单已审核（已处理）"},
		{"11","未处理"},
		{"12","商品调拨中"},
		{"13","缺货"},
		{"14","订单发货失败"},
		{"20","拣货中"},
		{"21","已打包"},
		{"22","已发货"},
		{"23","售后处理"},
		{"24","未处理"},
		{"25","已签收"},
		{"28","订单重发"},
		{"30","未处理"},
		{"31","未处理"},
		{"40","货品回寄中"},
		{"41","退换货服务不受理"},
		{"42","无效换货"},
		{"44","已发货"},
		{"45","退款处理中"},
		{"46","退换货未处理"},
		{"47","修改退款资料"},
		{"48","无效退货"},
		{"49","已退款"},
		{"51","退货异常处理中"},
		{"52","退款异常处理中"},
		{"53","退货未审核"},
		{"54","退货已审核"},
		{"55","拒收回访"},
		{"56","售后异常"},
		{"57","上门取件"},
		{"58","退货已返仓"},
		{"59","已退货"},
		{"60","已完成"},
		{"61","已换货"},
		{"70","用户已拒收"},
		{"71","超区返仓中"},
		{"72","拒收返仓中"},
		{"96","订单已修改"},
		{"97","订单已取消"},
		{"98","已合并"},
		{"99","已删除"},
		{"100","退货失败"}
	};
	
	
	/**
	 * 创建接口订单
	 * @param conn
	 * @param o
	 * @param tradeContactID
	 * @param username
	 * @return
	 * @throws SQLException 
	 */
	public static String createInterOrder(Connection conn,Order o,String tradeContactID,String username) throws Exception
	{		
		try 
		{
			conn.setAutoCommit(false);		
			
			String sheetid="";
			String sql="declare @Err int ; declare @NewSheetID char(16); "+
				"execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
				
			sheetid=SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql,"取接口单号出错!");
			
			//订单明细
			//float totalPrice = 0.00f ;//总金额
			//float sellerDiscount = 0.0f ;//商家总优惠金额
			//实付总金额
			//float totalItemPayment=0.0f;
			//总邮费
			//float totalPostfee=0.0f;
			
			//应付总金额      整张出库单商品金额总和(计算发票金额 == 整张出库单商品金额总和 + 快递费用 - 优惠金额 - 促销优惠金额)
			String totalfee=String.valueOf(o.getProduct_money());
			int j=0;
			for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();	
				String itemPayment = String.valueOf(item.getSell_price()*item.getAmount());
				sql = new StringBuilder()
					.append("insert into ns_orderitem(")
					.append("CustomerOrderId, orderItemId, SheetID, skuid, itemmealname, ")
                    .append("title, sellernick, buyernick, type, created, ")
                    .append("refundstatus, outeriid, outerskuid, totalfee, payment, ")
                    .append("discountfee, adjustfee, status, owner, ")
                    .append("skuPropertiesName, num, price, picPath, modified) values('" )
                    .append(sheetid).append("','")	//CustomerOrderId
                    .append(sheetid).append("-").append(o.getOrder_id()).append(String.valueOf(++j)).append("','")	//orderItemId
                    .append(sheetid).append("','")	//SheetID
                    .append(item.getBarcode()).append("','','")	//skuid,itemmealname
                    .append(item.getProduct_name()).append("','")	//title
                    .append(username).append("','")	//sellernick
                    .append(o.getBuyer()).append("','','")	//buyernick,type
                    .append(Formatter.format(o.getAdd_time(), Formatter.DATE_TIME_FORMAT)).append("','','")	//created,refundstatus
                    .append(item.getArt_no()).append("','")	//outeriid
                    .append(item.getBarcode()).append("','")	//outerskuid
                    .append(itemPayment).append("','")	//totalfee
                    .append(itemPayment).append("','")	//payment
                    .append("','','")	//discountfee,adjustfee
                    .append(getOrderStateByCode(o.getOrder_status())).append("','yongjun',")	//status,owner
                    .append("'','")	//skuPropertiesName
                    .append((int)item.getAmount()).append("','")	//num
                    .append(item.getSell_price()).append("','','")	//price,picPath
                    .append(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)).append("')").toString();	//modified
				//Log.info("ns_orderitem的SQL语句："+sql);    
        		SQLHelper.executeSQL(conn, sql) ;

			}
			Log.info("ns_orderitem写入完毕，到ns_customerorder表");
			//加入到单据表
			if("".equals(o.getInvoice()))  
			sql =  new StringBuilder()
					.append("insert into ns_customerorder(")
					.append("CustomerOrderId, SheetID, Owner, tid, OrderSheetID, sellernick, ")
	            	.append("type, created, buyermessage, shippingtype, payment, discountfee, ")
					.append("adjustfee, status, paytime, totalfee, postfee, buyeralipayno, ")
					.append("buyernick, buyerUin, receivername, receiverstate, receivercity, receiverdistrict, ")
					.append("receiveraddress, receivermobile, tradefrom, TradeContactID, modified, receiverphone) values('")
					.append(sheetid).append("','")	//CustomerOrderId
					.append(sheetid).append("','yongjun','")	//SheetID,Owner
					.append(o.getOrder_id()).append("','','")	//tid,OrderSheetID
					.append(username).append("','','")	//sellernick,type
					.append(Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)).append("','")	//created
					.append(o.getRemark()).append("','','")	//buyermessage,shippingtype
					.append(totalfee).append("','")	//payment
					.append(o.getDiscount_amount()).append("','")	//discountfee
					.append(o.getPromo_discount_amount()).append("','")	//adjustfee
					.append(getOrderStateByCode(o.getOrder_status())).append("','")	//status
					.append(Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)).append("','")	//paytime
					.append(o.getProduct_money() - o.getCarriage() + o.getPromo_discount_amount() + o.getDiscount_amount()).append("','")	//totalfee
					.append(o.getCarriage()).append("','','")	//postfee,buyeralipayno
					.append(o.getBuyer().replaceAll("'","''")).append("','','")	//buyernick,buyerUin
					.append(o.getBuyer().replace("'", "''")).append("','")	//receivername,
					.append(o.getProvince()).append("','")	//receiverstate
					.append(o.getCity()).append("','")	//receivercity
					.append(o.getCountry()).append("','")	//receiverdistrict
					.append(o.getAddress().replaceAll("'", "")).append("','")	//receiveraddress
					.append(o.getMobile()).append("','WEIPINHUI','")	//receivermobile,tradefrom
					.append(tradeContactID).append("','")	//TradeContactID
					.append(Formatter.format(o.getAdd_time(), Formatter.DATE_TIME_FORMAT)).append("','")	//modified
					.append(o.getTel()).append("')").toString();	//receiverphone
			else{  
				//需要发票
				sql =  new StringBuilder().append("insert into ns_customerorder(")
					.append("CustomerOrderId, SheetID, Owner, tid, OrderSheetID, sellernick, ")
					.append("InvoiceFlag, invoicetitle, type, created, buyermessage, shippingtype, ")
					.append("payment, discountfee, adjustfee, status, paytime, totalfee, ")  //11
					.append("postfee, buyeralipayno, buyernick, buyerUin, receivername, receiverstate, ")//18
					.append("receivercity, receiverdistrict, receiveraddress, receivermobile, tradefrom, TradeContactID, ")//24
					.append("modified, receiverphone) values('")
					.append(sheetid).append("','")	//CustomerOrderId
					.append(sheetid).append("','yongjun','")	//SheetID,Owner
					.append(o.getOrder_id()).append("','','")	//tid,OrderSheetID
					.append(username).append("','1','")	//sellernick,InvoiceFlag
					.append(o.getInvoice()!=null?o.getInvoice().replaceAll("'","''"):"").append("','','")	//invoicetitle,type
					.append(Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)).append("','")	//created
					.append(o.getRemark()).append("','','")	//buyermessage,shippingtype
					.append(totalfee).append("','")	//payment
					.append(o.getDiscount_amount()).append("','")	//discountfee
					.append(o.getPromo_discount_amount()).append("','")  //adjustfee
					.append(getOrderStateByCode(o.getOrder_status())).append("','")	//status
					.append(Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)).append("','")	//paytime
					.append(o.getProduct_money() - o.getCarriage() + o.getPromo_discount_amount() + o.getDiscount_amount()).append("','")	//totalfee
					.append(o.getCarriage()).append("','','")	//postfee,buyeralipayno
					.append(o.getBuyer().replace("'", "''")).append("','','")	//buyernick,buyerUin
					.append(o.getBuyer().replace("'", "''")).append("','")	//receivername
					.append(o.getProvince()).append("','")	//receiverstate
					.append(o.getCity()).append("','")	//receivercity
					.append(o.getCountry()).append("','")	//receiverdistrict
					.append(o.getAddress().replaceAll("'", "")).append("','")	//receiveraddress
					.append(o.getMobile()).append("','WEIPINHUI','")	//receivermobile,tradefrom
	                .append(tradeContactID).append("','")	//TradeContactID
	                .append(Formatter.format(o.getAdd_time(), Formatter.DATE_TIME_FORMAT)).append("','")	//modified
	                .append(o.getTel()).append("')").toString();	//receiverphone
			}
			//Log.info("ns_customerorder的SQL语句:　"+sql);
			SQLHelper.executeSQL(conn, sql);
			//加入到通知表
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                + sheetid +"',1 , '"+tradeContactID+"' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("生成订单【" + o.getOrder_id() + "】接口数据成功，接口单号【" + sheetid + "】");

			return sheetid;
			
		} catch (JSQLException e1) {
			e1.printStackTrace();
			if (!conn.getAutoCommit())
				try {
					conn.rollback();
				} catch (Exception e2) {
				}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e3) {
			}
			throw new JException("生成订单【" + o.getOrder_id() + "】接口数据失败!"
					+ e1.getMessage());
		}
	}
	
	/**
	 * 返回订单状态
	 * @param orderStateCode
	 * @return
	 */
	public static String getOrderStateByCode(String orderStateCode)
	{
		String result = "";
		for(int i=0;i<OrderStatusList.length;i++)
		{
			if(orderStateCode.equals(OrderStatusList[i][0]))
			{
				result = OrderStatusList[i][1];
				break;
			}
		}
		return result;
	}
	
	/**
	 * 获取订单详情
	 * @param order_sn 订单号
	 * @return
	 * @throws Exception
	 */
	public static JSONArray getOrderItem(String order_sn) throws Exception{
		Log.info("获取订单详情:" + order_sn);
		JSONArray result = null;
		int pageIndex = 1;
		boolean hasNextPage = true;
		//获取订单明细
		while(hasNextPage)
		{
			JSONObject jsonobj = new JSONObject();
			result = new JSONArray();
			try {
				jsonobj.put("order_id", order_sn);
				jsonobj.put("vendor_id", Params.vendor_id);
				jsonobj.put("page", 1);
				jsonobj.put("limit", 100);

				String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getOrderDetail", jsonobj.toString());
				
				String returnCode = new JSONObject(responseText).getString("returnCode");
				if(!returnCode.equals("0"))
					break;
				JSONArray orderDetails = new JSONObject(responseText).getJSONObject("result").getJSONArray("orderDetails");
				
				int orderNum= new JSONObject(responseText).getJSONObject("result").getInt("total");
				int pageTotal=0;
				if(orderNum!=0){
					pageTotal = orderNum>=Integer.parseInt(Params.pageSize) ? (orderNum %Integer.parseInt(Params.pageSize)==0?orderNum /Integer.parseInt(Params.pageSize):(orderNum /Integer.parseInt(Params.pageSize)+1)) : 1;
				}
				
				for(int i=0;i<orderDetails.length();i++)
				{
					result.put(orderDetails.getJSONObject(i));
				}
				
				//判断是否有下一页
				if(pageIndex >= pageTotal)
					hasNextPage = false ;
				else
					pageIndex ++ ;
				
			} catch (JSONException e) {
				//e.printStackTrace();
				break;
			}
		}
		return result;
	}
	
	/**
	 * 获取退货订单详情
	 * @param order_sn 客退申请单号
	 * @return
	 * @throws Exception
	 */
	public static JSONArray getRefundOrderItem(String back_sn) throws Exception{
		Log.info("获取退货订单详情,客退申请单号:" + back_sn);
		JSONArray result = null;
		int pageIndex = 1;
		boolean hasNextPage = true;
		//获取订单明细
		while(hasNextPage)
		{
			JSONObject jsonobj = new JSONObject();
			result = new JSONArray();
			try {
				jsonobj.put("back_sn", back_sn);
				jsonobj.put("vendor_id", Params.vendor_id);
				jsonobj.put("page", 1);
				jsonobj.put("limit", 100);
				String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getReturnProduct", jsonobj.toString());
				
				String returnCode = new JSONObject(responseText).getString("returnCode");
				if(!returnCode.equals("0"))
					break;
				JSONArray productDetails = new JSONObject(responseText).getJSONObject("result").getJSONArray("dvd_return_product_list");

				int orderNum= new JSONObject(responseText).getJSONObject("result").getInt("total");
				int pageTotal=0;
				if(orderNum!=0){
					pageTotal = orderNum>=Integer.parseInt(Params.pageSize) ? (orderNum %Integer.parseInt(Params.pageSize)==0?orderNum /Integer.parseInt(Params.pageSize):(orderNum /Integer.parseInt(Params.pageSize)+1)) : 1;
				}
				
				for(int i=0;i<productDetails.length();i++)
				{
					result.put(productDetails.getJSONObject(i));
				}
				
				//判断是否有下一页
				if(pageIndex >= pageTotal)
					hasNextPage = false ;
				else
					pageIndex ++ ;
			} catch (JSONException e) {
				//e.printStackTrace();
				break;
			}
		}
		return result;
	}
	
	//生成退货接口数据
	public static void createRefundOrder(Connection conn,ReturnOrder r,String tradecontactid) throws Exception
	{
		if(r == null) return;
		//获取inshopid
		String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="+ tradecontactid;
		String inshopid = SQLHelper.strSelect(conn, sql);
		conn.setAutoCommit(false);
			for(Iterator ito=r.getOrderItemList().getRelationData().iterator();ito.hasNext();)
			{
				ReturnOrderItem ritem=(ReturnOrderItem) ito.next();
				sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
				String sheetid = SQLHelper.strSelect(conn, sql);
				if (sheetid.trim().equals(""))
					throw new JSQLException(sql, "取接口单号出错!");

				// 加入到通知表
				sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
						+"values('yongjun','"+ sheetid+ "',2 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
				SQLHelper.executeSQL(conn, sql);
				
				//写入退货单信息到ns_Refund表
				sql =	"insert into ns_Refund(" +
						"SheetID, RefundID, Oid, Tid, Created, "+ 
						"GoodStatus, Status, HasGoodReturn, Payment, Reason, Title, "+ 
						"Num, OuterIid, InShopID)"+ 
						"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
				//获取订单状态
				String Status = "";
				
				//唯品会提供的字段:
				//退货单:供应商ID,订单编号,退货申请单状态,退货原因,从b2c拉取客退订单状态时间,客退申请单号
				//退货单明细:商品名称,订单编号,PO单号,条形码,退货商品数量
				Object[] sqlv = {
					sheetid,	//接口单号
					r.getBack_sn(),	//客退申请单号
					ritem.getOrder_id(),	//商品-订单编号
					r.getOrder_id(),	//订单号
					Formatter.format(r.getCreate_time(),Formatter.DATE_TIME_FORMAT),	//创建时间 - 从b2c拉取客退订单状态时间
				goodStatus(r.getReturn_status()),		//商品状态
					state(r.getReturn_status()),		//订单状态			退货申请单状态
					1,	//是否需要退货(0:不需要,1需要)
					0,	//应付款项(0)
					r.getReturn_reason(),	//退款原因
					ritem.getProduct_name(),	//商品标题
					ritem.getAmount(),	//数量				退货商品数量
					ritem.getBarcode(),	//商品编码 sku		条形码
					inshopid	//tradecontactid
				};
				SQLHelper.executePreparedSQL(conn, sql, sqlv);
				
				Log.info("接口单号:"+ sheetid + " 订单号:" + r.getOrder_id()+ " 状态："+ Status+ "退货申请时间:"
						+ Formatter.format(r.getCreate_time(),Formatter.DATE_TIME_FORMAT));
			}		
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	private static String goodStatus(String goods_state) {
		if(goods_state.equals("59"))
			//客户已退回
			goods_state="BUYER_RECEIVED";
		else
			//客户未退回
			goods_state="BUYER_NOT_RECEIVED";
		
		return goods_state;
	}

	private static String state(String order_state) {
		//买家已经申请退款，等待卖家同意
		if(order_state.equals("46")||order_state.equals("53"))
			order_state="WAIT_SELLER_AGREE";
		//卖家已经同意退款，等待买家退货
		if(order_state.equals("54"))
			order_state="WAIT_BUYER_RETURN_GOODS";
		//卖家拒绝退款
		if(order_state.equals("41")||order_state.equals("48")||order_state.equals("100"))
			order_state="SELLER_REFUSE_BUYER";
		//买家已经退货，等待卖家确认收货
		if(order_state.equals("45"))
			order_state="WAIT_SELLER_CONFIRM_GOODS";
		//退款成功
		if(order_state.equals("60"))
			order_state="SUCCESS";
		
		return order_state;
	}
	
//	状态列表:
//		买家已经申请退款，等待卖家同意
//			WAIT_SELLER_AGREE				46退换货未处理/53退货未审核
//		卖家已经同意退款，等待买家退货
//			WAIT_BUYER_RETURN_GOODS			54退货已审核
//		卖家拒绝退款
//			SELLER_REFUSE_BUYER				41退换货服务不受理/48无效退货/100退货失败
//		买家已经退货，等待卖家确认收货
//			WAIT_SELLER_CONFIRM_GOODS		45退款处理中
//		退货成功
//			SUCCESS			60已完成
//				
//	商品状态
//		客户未退回
//			BUYER_NOT_RECEIVED   非59的都是
//		客户已退回
//			BUYER_RECEIVED		59已退货
}
