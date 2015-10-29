package com.wofu.ecommerce.alibaba;
import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.alibaba.api.ApiCallService;
import com.wofu.ecommerce.alibaba.util.CommonUtil;
public class OrderUtils {

	private static String TradeFields = "";

	private static String RefundFields = "";

	public static void setOrderItemSKU(Connection conn,Order o,String orgId) throws Exception
	{		
		for(int j=0;j<o.getOrderEntries().getRelationData().size();j++)
		{
			OrderItem item=(OrderItem) o.getOrderEntries().getRelationData().get(j);
			String sku="";
			String sqls="select count(*) from ecs_StockConfigsku where itemid='"+item.getSourceId()+"' and skuid='"+item.getSpecId()+"'";
			if (SQLHelper.intSelect(conn, sqls)==0)
			{
				sku="";
				
			}
			
			sqls="select sku from ecs_StockConfigsku where itemid='"+item.getSourceId()+"' and skuid='"+item.getSpecId()+"'";
			sku=SQLHelper.strSelect(conn, sqls);
			item.setSku(sku);
			o.getOrderEntries().getRelationData().set(j, item);
		}
	}
	
	public static void setOrderItemCode(Connection conn,Order o,String orgId) throws Exception
	{		
		for(int j=0;j<o.getOrderEntries().getRelationData().size();j++)
		{
			OrderItem item=(OrderItem) o.getOrderEntries().getRelationData().get(j);
			String sku="";
			String sqls="select itemcode from ecs_StockConfig where itemid='"+item.getSourceId()+"' and orgid='"+orgId+"'";
			if ("".equals(SQLHelper.strSelect(conn, sqls)))
			{
				sqls ="select sku from ecs_stockconfigsku where itemid='"+item.getSourceId()+"' and orgid="+orgId;
				sku=SQLHelper.strSelect(conn, sqls);
				item.setSku(sku);
				o.getOrderEntries().getRelationData().set(j, item);
				
				
			}else{
				sqls="select itemcode from ecs_StockConfig where itemid='"+item.getSourceId()+"' and orgid='"+orgId+"'";
				sku=SQLHelper.strSelect(conn, sqls);
				item.setSku(sku);
				o.getOrderEntries().getRelationData().set(j, item);
			}
			
			
		}
	}
	/**
	 * 阿里巴巴 转入一个订单到接口表
	 */
	public static String createInterOrder(Connection conn,
			Order o, String tradecontactid,String username,String token,String appkey,String appSec)
			throws Exception {
		try {

			String sheetid = "";

			conn.setAutoCommit(false);

			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);

			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");
			String mobile=o.getToMobile()!=null?o.getToMobile():"";
			String phone=o.getToPhone()!=null?o.getToPhone():"";
			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid
					+ "',1 , '"
					+ tradecontactid
					+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  ,OrderSheetID, sellernick , type, "
					+ "  created , buyermessage, shippingtype, payment, status,paytime ,  modified ,totalfee , postfee  , "
					+ " buyernick , receivername ,  "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone , tradeContactid) "

					+ " values(" + "'"
					+ sheetid
					+ "','"
					+ sheetid
					+ "','"
					+ username
					+ "','"
					+ o.getId()
					+ "','','"
					+ username
					+ "', '','"
					+ Formatter.format(o.getGmtCreate()!=null?o.getGmtCreate():"1970-01-01 12:00:00",Formatter.DATE_TIME_FORMAT)
					+ "','','',"
					+ o.getSumPayment()/100//实付款   payment
					+ ","
					+ "'"
					+ o.getStatus()
					+ "','"
					+ Formatter.format(o.getGmtPayment()!=null?o.getGmtPayment():"1970-01-01 12:00:00",Formatter.DATE_TIME_FORMAT)//付款时间
					+ "',"
					+ "'"
					+ Formatter.format(o.getGmtModified()!=null?o.getGmtModified():"1970-01-01 12:00:00",Formatter.DATE_TIME_FORMAT)//最后修改时间
					+ "',"
					+ (o.getSumPayment()-o.getCarriage())/100 //总付款  totalfee
					+ ","
					+ o.getCarriage()/100  //运费
					+ ",'"
					+ getBuyerId(o.getId(),token,appkey,appSec)
					+ "' ,'"
					+ o.getToFullName()
					+ "', "
					+ "'"
					+ o.getToArea().replaceAll("'", "")
					+ "','"
					+ o.getToPost()//邮编
					+ "','"
					+ mobile
					+ "','"
					+ phone
					+ "',"
					+ tradecontactid
					+ ")";
			SQLHelper.executeSQL(conn, sql);
			//循环添加商品详情
			for(int i=0; i< o.getOrderEntries().getRelationData().size();i++){
				OrderItem item = (OrderItem)o.getOrderEntries().getRelationData().get(i);
				JSONArray pic=null;
				if(item.getProductPic()!=null)
				pic=new JSONArray(item.getProductPic());
				String skuinfo="";
				if(item.getSpecInfo()!=null){
					JSONArray sku=new JSONArray(item.getSpecInfo());
					
					for(int j=0;j<sku.length();j++){
						skuinfo=skuinfo+sku.getJSONObject(j).getString("specName")+":"+sku.getJSONObject(j).getString("specValue")+";";
					}
				}
				sql =new StringBuilder("insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , ")
					.append(" title , sellernick ,  created , ")
					.append("  outeriid , outerskuid , totalfee , payment , ")
					.append(" skuPropertiesName,  status  ,")
					.append(" num , price , picPath , ")
					.append("  buyerRate ,sellerRate , numiid ) values('")
					.append(sheetid)
					.append("','")
					.append(sheetid+item.getId())
					.append("','")
					.append(sheetid)
					.append("','")
					.append(item.getSpecId())
					.append("', '")
					.append(item.getProductName())
					.append("' , '")
					.append(item.getProductName())
					.append("' ,'")
					.append(username)
					.append("', '")
					.append(Formatter.format(o.getGmtCreate(),
							Formatter.DATE_TIME_FORMAT))
					.append("', '")
					.append(item.getSourceId())
					.append("' , '")
					.append(item.getSku())
					.append("' ,'")
					.append((item.getPrice()*item.getQuantity()+item.getEntryDiscount())/100)
					.append("' , '")
					.append(((item.getPrice()*item.getQuantity()+item.getEntryDiscount()+o.getCarriage())/100))
					.append("','")
					.append(skuinfo)
					.append("','")
					.append(o.getStatus())
					.append("',")
					.append(item.getQuantity())
					.append(",")
					.append(item.getPrice()/100)
					.append(",'")
					.append(pic!=null?pic.getString(0):"")
					.append("',")
					.append(o.getBuyerRateStatus())//是否买家评论
					.append(",")
					.append(o.getSellerRateStatus())//是否卖家评论)
					.append(",'")
					.append(item.getSourceId() + "')").toString();//商品ID
					Log.info(sql);
				SQLHelper.executeSQL(conn, sql);
			}
			conn.commit();
			conn.setAutoCommit(true);

			Log.info("生成订单【" + o.getId() + "】接口数据成功，接口单号【" + sheetid + "】");

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
			throw new JException("生成订单【" + o.getId() + "】接口数据失败,错误信息："
					+ e1.getMessage());
		}
	}

	//退货
	public static void getRefund(String modulename, Connection conn,
			String tradecontactid, Order o) throws Exception {

//		String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="
//				+ tradecontactid;
//		String inshopid = SQLHelper.strSelect(conn, sql);
//
//		conn.setAutoCommit(false);
//
//		sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
//		String sheetid = SQLHelper.strSelect(conn, sql);
//		if (sheetid.trim().equals(""))
//			throw new JSQLException(sql, "取接口单号出错!");
//
//		// 加入到通知表
//		sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
//				+ "values('yongjun','"
//				+ sheetid
//				+ "',2 , '"
//				+ tradecontactid
//				+ "' , 'yongjun' , getdate() , null) ";
//		SQLHelper.executeSQL(conn, sql);
//
//		sql = "insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , "
//				+ "BuyerNick , Created , Modified , OrderStatus , Status , GoodStatus , "
//				+ " HasGoodReturn ,RefundFee , Payment ,  Title ,"
//				+ "Price , Num ,"
//				+ " TotalFee ,  OuterIid , OuterSkuId  , "
//				+ " ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
//				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//
//		Object[] sqlv = {
//				sheetid,
//				o.getId(),
//				o.getId(),
//				o.getAli_trade_no(),
//				o.getBuyer_nick(),
//				Formatter.format(o.getCreat_time(), Formatter.DATE_TIME_FORMAT),
//				Formatter.format(o.getModified(), Formatter.DATE_TIME_FORMAT),
//				o.getStatus(),
//				o.getStatus(),
//				o.getStatus(),
//				1,
//				o.getPayment(),
//				o.getPayment(),
//				o.getTitle(),
//				Double.valueOf(o.getPrice()),
//				o.getNum(),
//				o.getTotal_fee(),
//				o.getOuter_id(),
//				o.getSku_outer_id(),
//				o.getReceiver_state() + " " + o.getReceiver_city() + " "
//						+ o.getReceiver_district() + " "
//						+ o.getReceiver_address(), inshopid, o.getTid(),
//				o.getReceiver_name(),
//				o.getReceiver_mobile() + " " + o.getReceiver_phone(),
//				o.getAli_trade_no() };
//
//		SQLHelper.executePreparedSQL(conn, sql, sqlv);
//
//		Log.info(modulename, "接口单号:"
//				+ sheetid
//				+ " 订单号:"
//				+ o.getTid()
//				+ " 订单状态："
//				+ o.getStatus()
//				+ " 订单创建时间:"
//				+ Formatter.format(o.getCreat_time(),
//						Formatter.DATE_TIME_FORMAT));
//
//		conn.commit();
//		conn.setAutoCommit(true);

	}
	
	/**
	 *根据订单id取买家旺旺号--填充ns_customerorder的buynick字段
	 * @param orderId
	 * @param token
	 * @param appkey
	 * @param appSec
	 * @return
	 */
	public static String getBuyerId(Long orderId,String token,String appkey,String appSec){
		String result="";
		try{
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("id", orderId+"") ;
			String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"trade.order.detail.get",Params.version,Params.requestmodel,appkey);
			params.put("access_token", token);
			String responseText = ApiCallService.callApiTest(Params.url, urlPath, appSec, params);
			Log.info("取订单详情返回数据为: "+responseText);
			JSONObject res=new JSONObject(responseText);
			result =  res.getJSONObject("orderModel").getString("buyerLoginId");
			Log.info("旺旺号:　"+result);
		}catch(Exception ex){
			Log.error("根据订单id取买家id出错", ex.getMessage());
		}
		return result;
		
		
	}

}
