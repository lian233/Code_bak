package com.wofu.ecommerce.ylw;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ylw.util.CommHelper;

public class OrderUtils {
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	private static String refundDesc[] = {"","退货","换货",""} ;

	
		

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
			Hashtable cityInfo = getCityByCode(conn,o.getProvinceCode(),o.getCityCode(),o.getDistrictCode());
			
			//订单明细
			float totalPrice = 0.00f ;//总金额
			float sellerDiscount = 0.0f ;//商家总优惠金额
			//实付总金额
			float totalItemPayment=0.0f;
			//应付总金额
			float totalfee=0.0f;
			//总邮费
			float totalPostfee=0.0f;
			//float paymentPercent = 1-(sellerDiscount/totalPrice) ;
			//float countDiscountFee = 0f ;
			//float countPayment = 0f ;
			//发票金额
			//float invoicePercent = 1 - ((discountfee+giftCardMoney) / totalPrice) ;
			//float countInvoicePayment = 0f ;
			int j=0;
			for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();	
				
				//商品的优惠金额
				float discountfee=item.getCoupontotalMoney()+item.getVouchertotalMoney();
				//应付金额
				float itemTotalFee = item.getSaleNum() * item.getUnitPrice()-discountfee ;
				//实付金额
				float itemPayment = item.getPayAmount() ;
				//float invoicePayment = 0f ;
				//这里iid为苏宁商家编码
				sql = new StringBuilder().append("insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , ")
                    .append(" title , sellernick , buyernick , type , created , ") 
                    .append(" refundstatus , outeriid , outerskuid , totalfee , payment , ")
                    .append(" discountfee , adjustfee , status , timeoutactiontime , owner , ")
                    .append(" iid , skuPropertiesName , num , price , picPath , " )
                    .append(" buyerRate,modified) values( '")
                    .append(sheetid).append("','").append(sheetid).append("-").append(o.getOrderCode()).append(String.valueOf(++j))
                    .append("','").append(sheetid).append("','").append(item.getItemCode()).append("','','")
                    .append(item.getProductName()).append("','").append(username).append("','")
                    .append(o.getUserName()).append("','','")
                    .append(Formatter.format(o.getOrderSaleTime(), Formatter.DATE_TIME_FORMAT)).append("','','").append(item.getItemCode()).append("','")
                    .append(item.getItemCode()).append("','").append(itemTotalFee).append("','").append(itemPayment).append("','")
                    .append(discountfee).append("','','").append(getOrderStateByCode(o.getOrderLineStatus())).append("','','yongjun','").append(item.getProductCode()).append("','','")
                    .append((int)item.getSaleNum()).append("','").append(item.getUnitPrice()).append("','").append(item.getPicPath())
                    .append("',").append(Integer.parseInt(o.getEvaluationMark())).append(",'").append(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)).append("')").toString();
				//Log.info("ns_orderitem的SQL语句："+sql);    
        		SQLHelper.executeSQL(conn, sql) ;
        		sellerDiscount+=discountfee;
        		totalItemPayment+=itemPayment;
        		totalfee+=item.getUnitPrice()*item.getSaleNum();
        		totalPostfee+=item.getTransportFee();
			}
			//Log.info("ns_orderitem写入完毕，到ns_customerorder表");
			//加入到单据表
			sql =  new StringBuilder().append("insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , ")
            	.append(" type , created , buyermessage , shippingtype , payment , ")
				.append(" discountfee , adjustfee , status ,paytime,totalfee , postfee , buyeralipayno , ")
				.append(" buyernick ,buyerUin, receivername , receiverstate , receivercity , receiverdistrict , ")
				.append(" receiveraddress , receivermobile , dealRateState, ")
				.append(" tradefrom,TradeContactID,modified) values('")
				.append(sheetid).append("','").append(sheetid).append("','yongjun','").append(o.getOrderCode()).append("','','").append(username)
				.append("','','").append(Formatter.format(o.getOrderSaleTime(),Formatter.DATE_TIME_FORMAT)).append("','','','").append(totalItemPayment).append("','").append(sellerDiscount).append("','','")
				.append(getOrderStateByCode(o.getOrderLineStatus())).append("','").append(Formatter.format(o.getOrderSaleTime(),Formatter.DATE_TIME_FORMAT)).append("','").append(totalfee).append("','").append(totalPostfee).append("','','").append(o.getUserName()).append("','','")
				.append(o.getCustomerName()).append("','").append(cityInfo.get("provinceName")).append("','").append(cityInfo.get("cityname")).append("','").append(cityInfo.get("districtname"))
				.append("','").append(o.getCustomerAddress().replaceAll("'", "")).append("','").append(o.getMobNum()).append("','").append(o.getEvaluationMark()).append("',")
                .append("'SUNING','").append(tradeContactID).append("','").append(Formatter.format(o.getOrderSaleTime(), Formatter.DATE_TIME_FORMAT)).append("')").toString();
			//Log.info("ns_customerorder的SQL语句："+sql);

			SQLHelper.executeSQL(conn, sql);
			//加入到通知表
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                + sheetid +"',1 , '"+tradeContactID+"' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("生成订单【" + o.getOrderCode() + "】接口数据成功，接口单号【" + sheetid + "】");

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
			throw new JException("生成订单【" + o.getOrderCode() + "】接口数据失败!"
					+ e1.getMessage());
		}
	}

	


	
	//获取发票明细信息
	public static String getInvoiceDetail(String jobname,Connection conn,String sku)
	{
		String detail = "" ;
		try 
		{
			String sql = "select a.customBC+c.name from barcode as a with(nolock),goods as b with(nolock),dept as c with(nolock) " +
			"where a.goodsid=b.goodsid and b.deptid=c.id and a.customBC='"+ sku +"'" ;
			detail = SQLHelper.strSelect(conn, sql) ;
		}
		catch (Exception e) 
		{
			Log.error(jobname, "获取商品款号名称失败,错误信息:"+e.getMessage()) ;
			detail = "" ;
			e.printStackTrace() ;
		}
		
		return detail ;
	}

	//获取发票单位
	public static String getGoodsUnitName(String jobname,Connection conn,String sku)
	{
		String unitName = "" ;
		if("".equals(sku) || sku == null)
			return unitName ;
		try 
		{
			String sql ="select unitname from goods as a with(nolock),barcode as b with(nolock) where a.goodsid=b.goodsid and b.customBC='"+ sku +"'" ;
			unitName = SQLHelper.strSelect(conn, sql) ;
		} catch (Exception e) {
			Log.error(jobname, "获取商品单位失败,错误信息:"+e.getMessage()+",sku:"+sku) ;
		}
		return unitName ;
	}
	
	//生成退货接口数据
	public static void createRefundOrder(String jobname,Connection conn,String tradecontactid,
			Order order,String url,String appKey,String appsecret,String format)
	{
		ReturnOrder o = getReturnOrder(order,url,appKey,appsecret,format);
		String sql = "" ;
		float refundFee = 0f ;
		//买家姓名
		String BuyerNick=order.getCustomerName();
		//支付给卖家的金额
		float Payment=o.getDealMoney()-o.getReturnMoney();
		try 
		{
			ArrayList<ReturnOrderItem> itemList = o.getItemList() ;
			
			sql="select shopid from ContactShopContrast with(nolock) where tradecontactid="+tradecontactid;
            String inshopid = SQLHelper.strSelect(conn, sql);
			for(int i=0 ; i<itemList.size() ; i++)
			{
				try 
				{
					ReturnOrderItem item = itemList.get(i) ;
		            conn.setAutoCommit(false);
		            
		            sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";			
					//Log.info(sql) ;
		            String sheetid = SQLHelper.strSelect(conn, sql);
					if (sheetid.trim().equals(""))
						throw new JSQLException(sql,"取接口单号出错!");
					
					refundFee = item.getUnitPrice() * item.getOrderCount() ;
					
					sql=new StringBuilder().append("insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , BuyerNick , ")
						.append("Created , Modified , OrderStatus , Status , GoodStatus , ")
	                    .append(" HasGoodReturn ,RefundFee , Payment , Reason,Description ,")
	                    .append(" Title , Price , Num , GoodReturnTime , Sid , ")
	                    .append(" TotalFee , Iid , OuterIid , OuterSkuId , CompanyName ,") 
	                    .append(" Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)")
	                    .append(" values('").append(sheetid ).append("','").append(sheetid).append("','','','").append(BuyerNick).append("','")
	                    .append( Formatter.format(o.getApplyTime(), Formatter.DATE_TIME_FORMAT)).append("','").append(Formatter.format(o.getApplyTime(), Formatter.DATE_TIME_FORMAT)).append("','").append(o.getStatusDesc()).append("','',''")
	                    .append("'1','").append(o.getReturnMoney()).append("','").append(Payment).append("','").append(o.getReason()).append("','','")
	                    .append(item.getItemName()).append("','").append(item.getUnitPrice()).append("','").append(item.getOrderCount()).append("','").append(Formatter.format(o.getApplyTime(), Formatter.DATE_TIME_FORMAT)).append("','").append(o.getMailNo())
	                    .append("','").append(o.getDealMoney()).append("','").append(item.getItemID()).append("','','").append(item.getOuterItemID()).append("','")
	                    .append("'','").append(order.getCustomerAddress().replaceAll("'",",'','")).append(inshopid).append("','").append(order.getOrderCode()).append("'").append(order.getCustomerName()).append("','").append(order.getMobNum()).append("','')").toString();

					Log.info("退货单sql: "+sql) ;
					System.out.println("--");
					SQLHelper.executeSQL(conn,sql);
					
					//加入到通知表     退货标志为2
		            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
		                + sheetid +"',2 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";				
					//Log.info(sql) ;
					SQLHelper.executeSQL(conn,sql);
					
					Log.info(jobname,"接口单号:"+sheetid+" 退货订单号:"+o.getOrderCode()+"，订单更新时间:"+Formatter.format(o.getApplyTime(),Formatter.DATE_TIME_FORMAT));
					conn.commit();
					conn.setAutoCommit(true);
				}
				catch (SQLException e1)
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
					throw new JSQLException("生成退货【" + o.getOrderCode() + "】接口数据失败!"+e1.getMessage());
				}
			}
		}
		catch (Exception e) 
		{
			Log.error(jobname, "生成接口退货单失败,订单号:"+o.getOrderCode() + ",退换货类型:"+",错误信息:"+e.getMessage()) ;
			e.printStackTrace() ;
		}
	}
	//退换货单描述
	private static String getRefundDesc(String index)
	{
		try 
		{
			return refundDesc[Integer.parseInt(index)] ;
		} catch (Exception e) {
			return index ;
		}
	}

	
	//返回订单状态
	public static String getOrderStateByCode(String orderStateCode)
	{
		if("10".equals(orderStateCode))
			return "等待发货" ;
		else if("20".equals(orderStateCode))
			return "已发货" ;
		else if("21".equals(orderStateCode))
			return "部分发货" ;
		else if("30".equals(orderStateCode))
			return "交易成功" ;
		else if("40".equals(orderStateCode))
			return "交易关闭" ;
		else
			return "未知的订单状态" ;
	}
	/**
	 * 根据城市代码获取城市
	 * @return
	 */
	public static Hashtable getCityByCode(Connection conn,String provinceCode,String citycode,String districtcode){
		String sql = new StringBuilder().append("select provinceName,cityname,districtname from sn_citycode where provinceCode='").append(provinceCode)
			.append("' and citycode='").append(citycode).append("' and districtcode='").append(districtcode).append("'").toString();
		try {
			return SQLHelper.oneRowSelect(conn, sql);
		} catch (JSQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	

	
	/**
	 * 根据订单号取得退货信息
	 * orderCode  原订单号
	 * ReturnOrder 退货订单对象
	 */
	public static ReturnOrder getReturnOrder(Order o,String url,String appKey,String appsecret,String format){
		ReturnOrder ro=null;
		try{
			for(int k=0;k<5;k++){
				//方法名
				String apimethod="suning.custom.singlerejected.get";
				HashMap<String,String> reqMap = new HashMap<String,String>();
		        reqMap.put("orderCode", o.getOrderCode());
		        HashMap<String,String> map = new HashMap<String,String>();
		        map.put("appSecret", appsecret);
		        map.put("appMethod", apimethod);
		        map.put("format", format);
		        map.put("versionNo", "v1.2");
		        map.put("appKey", appKey);
		        //发送请求
				String responseText = CommHelper.doRequest(map,url);
				Log.info("退货单信息: "+responseText);
				JSONObject responseObj = new JSONObject(responseText);
				JSONArray returnItems = responseObj.getJSONObject("sn_responseContent").getJSONObject("sn_body").getJSONArray("singleGetRejected");
				JSONObject returnObj = returnItems.getJSONObject(0);
				ro = new ReturnOrder();
				ro.setObjValue(ro, returnObj);
				ArrayList<ReturnOrderItem> returnOrderItems = new ArrayList<ReturnOrderItem>();
				for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
				{
					OrderItem oi=(OrderItem) ito.next();
					ReturnOrderItem rotm = new ReturnOrderItem();
					rotm.setItemID(oi.getProductCode());
					rotm.setItemName(oi.getProductName());
					rotm.setItemSubhead(oi.getProductName());
					rotm.setOrderCount((int)oi.getSaleNum());
					rotm.setOuterItemID(oi.getItemCode());
					rotm.setUnitPrice(oi.getUnitPrice());
					returnOrderItems.add(rotm);
				}
				ro.setItemList(returnOrderItems);
				
			}
			return ro;
		}catch(Exception ex){
			Log.error("获取退货单信息失败", ex.getMessage());
			return null;
		}
		
	}
	
	/**
	 * 获取物流公司名称
	 * @param code
	 * @return
	 */
	public static String getExpressInfo(Connection conn,String code){
		String sql = new StringBuilder().append("select name from expressInfo where code='")
		.append(code).append("'").toString();
		try {
			return SQLHelper.strSelect(conn, sql);
		} catch (JSQLException e) {
			Log.info("查询物流公司代码对应的物流公司出错!");
			return "";
		}
	}
}
