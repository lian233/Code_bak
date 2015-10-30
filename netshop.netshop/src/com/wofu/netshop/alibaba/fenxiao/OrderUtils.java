package com.wofu.netshop.alibaba.fenxiao;
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
import com.wofu.netshop.alibaba.fenxiao.api.ApiCallService;
import com.wofu.netshop.alibaba.fenxiao.util.CommonUtil;
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
	 * public static String getBuyerId(Long orderId,String token,String appkey,String appSec
			,String namespace,int version,String requestmodel,String url ){
	 */
	public static int createInterOrder(Connection conn,
			Order o, String tradecontactid,String username,String token,String appkey,String appSec
			,String namespace,int version,String requestmodel,String url,int shopid,int orderStstus)
			throws Exception {
		try {
			conn.setAutoCommit(false);
			int sheetid;
			String sql="declare @Value int;exec TL_GetNewSerial_new 100001,@value output;select @value;";
			sheetid=SQLHelper.intSelect(conn, sql);
			if (sheetid==0)
				throw new JSQLException(sql,"取接口单号出错!");

			// 加入到通知表
			sql = new StringBuilder().append("insert into inf_downnote(sheettype,notetime,opertype,operdata,flag,owner)")
			.append("values(1,getdate(),100,'")
			.append(sheetid).append("',0,'')").toString();
			SQLHelper.executeSQL(conn,sql);

			String addresses[] = o.getToArea().replaceAll("'", "").split(" ");
			sql =new StringBuilder().append("insert into itf_DecOrder")
			.append("(ID , shopid , tid , sellernick  , type , ")
			.append(" CreateTime , created , buyermessage , shippingtype , payment , ")
			.append(" discountfee , adjustfee , status , buyermemo , sellermemo , ")
			.append(" tradememo , paytime , endtime , modified ,buyerobtainpointfee , ")
			.append(" pointfee , realpointfee , totalfee , postfee , buyeralipayno , ")
			.append(" buyernick , receivername , receiverstate , receivercity , receiverdistrict , ")
			.append(" receiveraddress , receiverzip , receivermobile , receiverphone , consigntime , ")
			.append(" buyeremail , haspostFee , receivedpayment , codstatus,delivery,")
			.append(" alipayNo , buyerflag , sellerflag,brandsaleflag,dealRateState,")
			.append("InvoiceFlag,invoicetitle,Prepay,")
			.append(" sellerrate , buyerrate , promotion , tradefrom , alipayurl , ")
			.append(" PromotionDetails,paymode)")//56
			.append(" values(")
			.append(sheetid).append(",").append(shopid).append(",'").append(o.getId())
			.append("','").append(username).append("','")
			.append("','").append(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)).append("','")
			.append(Formatter.format(o.getGmtCreate()!=null?o.getGmtCreate():new Date(), Formatter.DATE_TIME_FORMAT)).append("','")
			.append("','")
			.append("','").append(o.getSumPayment()/100).append("','")
			.append("").append("','0.0','").append(orderStstus)//20待发货
			.append("','").append("").append("','").append("").append("','")
			.append("','").append(Formatter.format(o.getGmtCreate(), Formatter.DATE_TIME_FORMAT)).append("','")
			.append("','").append(Formatter.format(o.getGmtModified(), Formatter.DATE_TIME_FORMAT))
			.append("','','','','")
			.append((o.getSumPayment()-o.getCarriage())/100 ).append("','")
			.append(o.getCarriage()/100).append("','")
			.append("','").append(getBuyerId(o.getId(),token,appkey,appSec,namespace,version,requestmodel,url))
			.append("','").append(o.getToFullName())
			.append("','").append(addresses[0])
			.append("','").append(addresses[1])
			.append("','").append(addresses[2])
			.append("','").append(addresses[3])
			.append("','")
			.append("','").append(o.getToMobile()!=null?o.getToMobile():"")
			.append("','").append(o.getToPhone()!=null?o.getToPhone():"")
			.append("','").append(Formatter.format(o.getGmtModified(), Formatter.DATE_TIME_FORMAT))
			.append("','")
			.append("','")
			.append("','").append(o.getCarriage()/100)
			.append("','0','")
			//.append(delivery)  快递留空
			.append("','")//cod状态和快递现在留空
			.append("','")
			.append("','")
			.append("','','',")//dealRateState订单评价状态暂时为空
			.append("0,'','','")//InvoiceFlag=0   invoicetitle="";  Prepay=''
			.append("','")
			.append("','")
			.append("','").append("ALIBABA")
			.append("','")
			.append("','")
			.append("','").append(1).append("')").toString();
			SQLHelper.executeSQL(conn, sql) ;
			
			//循环添加商品详情
			for(int i=0; i< o.getOrderEntries().getRelationData().size();i++){
				OrderItem item = (OrderItem)o.getOrderEntries().getRelationData().get(i);
				sql="declare @Value integer;exec TL_GetNewSerial_new '100002',@value output;select @value;";
				int subid = SQLHelper.intSelect(conn, sql);
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
				
				sql = new StringBuilder("insert into itf_DecOrderItem(ID , ParentID  , skuid , itemmealname , title , ")
				.append(" sellernick , buyernick , type , created , refundstatus , ")
				.append(" outeriid , outerskuid , totalfee , payment , discountfee , ")
				.append(" adjustfee , status , timeoutactiontime  ,")
				.append(" iid , skuPropertiesName , num , price , ")
				.append(" picPath , oid , snapShotUrl , snapShot ,buyerRate ,sellerRate,")
				.append("  sellertype , refundId , isoversold,modified,numiid,cid,DistributePrice) values( ")
				.append(subid).append(",").append(sheetid).append(",'")
				.append(item.getSpecId()).append("','").append(item.getProductName())
				.append("','").append(item.getProductName())
				.append("','").append(username)
				.append("','").append(username)
				.append("','")
				.append("','").append(Formatter.format(o.getGmtModified(),Formatter.DATE_TIME_FORMAT))
				//0没有退款。10买家已经申请退款，等待卖家同意。20卖家已经同意退款，等待买家退货。30买家已经退货，等待卖家确认收货。40卖家拒绝退款。90退款关闭。100退款成功。
				.append("',0")
				.append(",'")
				.append("','").append(item.getSpecId())
				.append("','").append((item.getPrice()*item.getQuantity()+item.getEntryDiscount())/100)
				.append("','").append(((item.getPrice()*item.getQuantity()+item.getEntryDiscount()+o.getCarriage())/100))
				.append("','")
				.append("','")
				.append("','")
				.append("','")//timeoutactiontime   订单超时到期时间暂时为空
				.append("','")//iid也为空
				.append("','").append(skuinfo)
				.append("',").append(item.getQuantity())
				.append(",'").append(item.getPrice()/100)
				.append("','").append(item.getProductPic().substring(2,item.getProductPic().length()-2))//产品图片
				.append("','").append(item.getSpecId())//明细的唯一标识  在平台上面可以换颜色，尺码之类的，明细信息会变，但这个oid不会变
				.append("','")
				.append("','")
				.append("',0")
				.append(",0")
				.append(",'")//sellertype为空
				.append("',0")//refundid=0
				.append(",'")
				.append("','")//modified为空
				.append("','")
				.append("','")
				.append("',0.0)").toString();//DistributePrice暂时为空
        		SQLHelper.executeSQL(conn, sql) ; 
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
	public static String getBuyerId(Long orderId,String token,String appkey,String appSec
			,String namespace,int version,String requestmodel,String url ){
		String result="";
		try{
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("id", orderId+"") ;
			String urlPath=CommonUtil.buildInvokeUrlPath(namespace,"trade.order.detail.get",version,requestmodel,appkey);
			params.put("access_token", token);
			String responseText = ApiCallService.callApiTest(url, urlPath, appSec, params);
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
