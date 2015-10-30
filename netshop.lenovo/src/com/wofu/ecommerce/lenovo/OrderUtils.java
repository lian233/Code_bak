package com.wofu.ecommerce.lenovo;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.lenovo.util.CommHelper;

public class OrderUtils {
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	private static String refundDesc[] = {"","退货","换货",""} ;

	/**
	 * 获取苏宁订单详细信息
	 * @param orderCode	订单号
	 * @return
	 */
	

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
			//发票信息
			String InvoiceFlag="".equals(o.getInv_payee())?"0":"1";
			String InvoiceTitle="";
			if("1".equals(InvoiceFlag))
				InvoiceTitle = o.getInv_payee();
			//订单明细
			float totalPrice = Float.parseFloat(o.getMoney_paid()) ;//总金额
			//订单优惠金额
			float sellerDiscount = Float.parseFloat(o.getDiscount());
			float paymentPercent= 1-(sellerDiscount/totalPrice);   //用户应付百分比
			//实付总金额
			float totalItemPayment=0.0f;
			//应付总金额
			float totalfee=0.0f;
			BigDecimal b1,b2;
			//总邮费
			float totalPostfee=0.0f;
			float CountPayment =0f;
			float discountFee = 0f;
			int j=0;
			String statusTemp = o.getShipping_status();
			String status = "0".equals(statusTemp)?"未发货":"已发货";
			int i=0;
			for(Iterator ito=o.getGoods_list().getRelationData().iterator();ito.hasNext();i++)
			{
				float payment =0f;
				OrderItem item=(OrderItem) ito.next();	
				float itemPayment = item.getGoods_price()*Integer.parseInt(item.getGoods_number());
				totalfee+=itemPayment;
				if(i==o.getGoods_list().size()-1){
					b1 = new BigDecimal(Float.toString(totalPrice));
					b2 = new BigDecimal(Float.toString(CountPayment));
					payment = b1.subtract(b2).floatValue();
				}else{
					payment = Float.parseFloat(decimalFormat.format(itemPayment*paymentPercent));
					CountPayment+=payment;
					discountFee =itemPayment - payment ;
					
				}
				sql = new StringBuilder().append("insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , ")
                    .append(" title , sellernick , buyernick , type , created , ") 
                    .append(" refundstatus , outeriid , outerskuid , totalfee , payment , ")  //15
                    .append(" discountfee , adjustfee , status , timeoutactiontime , owner , ")
                    .append(" skuPropertiesName , num , price , picPath , " )
                    .append("modified) values( '")
                    .append(sheetid).append("','").append(sheetid).append("-").append(o.getOrder_sn()).append(String.valueOf(++j))
                    .append("','").append(sheetid).append("','").append(item.getGoods_id()).append("','','")
                    .append(item.getGoods_name()).append("','").append(username).append("','")
                    .append(o.getUser_name()).append("','','")
                    .append(Formatter.format(new Date(o.getAdd_time()*1000L), Formatter.DATE_TIME_FORMAT)).append("','','").append(item.getGoods_sn()).append("','")
                    .append(item.getGoods_sn()).append("','").append(itemPayment).append("','").append(payment).append("','")
                    .append(discountFee).append("','','").append(status).append("','','yongjun',").append("'','")
                    .append(Integer.parseInt(item.getGoods_number())).append("','").append(item.getGoods_price()).append("','','")
                    .append(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)).append("')").toString();
				//Log.info("ns_orderitem的SQL语句："+sql);    
        		SQLHelper.executeSQL(conn, sql) ;

			}
			//Log.info("ns_orderitem写入完毕，到ns_customerorder表");
			//加入到单据表
			sql =  new StringBuilder().append("insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , ")
            	.append(" type , created , buyermessage , shippingtype , payment , ")  //11
				.append(" discountfee , adjustfee , status ,paytime,totalfee , postfee , buyeralipayno , ")//18
				.append(" buyernick ,buyerUin, receivername , receiverstate , receivercity , receiverdistrict , ")
				.append(" receiveraddress , receivermobile , ")
				.append(" tradefrom,TradeContactID,modified,InvoiceFlag,invoicetitle,paymode) values('")
				.append(sheetid).append("','").append(sheetid).append("','yongjun','").append(o.getOrder_sn()).append("','','").append(username)  //6
				.append("','','").append(Formatter.format(new Date(o.getAdd_time()*1000L),Formatter.DATE_TIME_FORMAT)).append("','','','").append(totalPrice).append("','").append(sellerDiscount).append("','','")  //13
				.append(status).append("','").append(Formatter.format(new Date(o.getAdd_time()*1000L),Formatter.DATE_TIME_FORMAT)).append("','").append(totalfee).append("','").append(o.getShipping_fee()).append("','','").append(o.getUser_name()).append("','','")
				.append(new String(o.getUser_name().getBytes(),"gbk")).append("','").append(new String(o.getProvince_name().getBytes(),"gbk")).append("','").append(new String(o.getCity_name().getBytes(),"gbk")).append("','").append(new String(o.getDistrict_name().getBytes(),"gbk"))
				.append("','").append(new String(o.getAddress().getBytes(),"gbk").replaceAll("'", "")).append("','").append(o.getTel()).append("',")
                .append("'LENOVO','").append(tradeContactID).append("','").append(Formatter.format(new Date(o.getAdd_time()*1000L), Formatter.DATE_TIME_FORMAT)).append("',").append(InvoiceFlag).append(",'").append(InvoiceTitle).append("',").append(o.getPay_id()).append(")").toString();
			//Log.info("ns_customerorder的SQL语句："+sql);

			SQLHelper.executeSQL(conn, sql);
			//加入到通知表
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                + sheetid +"',1 , '"+tradeContactID+"' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("生成订单【" + o.getOrder_sn() + "】接口数据成功，接口单号【" + sheetid + "】");

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
			throw new JException("生成订单【" + o.getOrder_sn() + "】接口数据失败!"
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
			RefundOrder order)
	{
		String sql = "" ;
		float refundFee = 0f ;
		//买家姓名
		String BuyerNick=order.getUser_name();
		//支付给卖家的金额
		float Payment=Float.parseFloat(order.getTotal_fee());
		try 
		{
			sql="select shopid from ContactShopContrast with(nolock) where tradecontactid="+tradecontactid;
            String inshopid = SQLHelper.strSelect(conn, sql);
			for(Iterator it = order.getGoods_list().getRelationData().iterator() ; it.hasNext() ; )
			{
				try 
				{
					RefundOrderItem item = (RefundOrderItem)it.next();
		            conn.setAutoCommit(false);
		            
		            sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";			
					//Log.info(sql) ;
		            String sheetid = SQLHelper.strSelect(conn, sql);
					if (sheetid.trim().equals(""))
						throw new JSQLException(sql,"取接口单号出错!");
					
					refundFee = Integer.parseInt(item.getGoods_number()) * item.getGoods_price();
					
					sql=new StringBuilder().append("insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , BuyerNick , ")
						.append("Created , Modified , OrderStatus , Status , GoodStatus , ")  //10
	                    .append(" HasGoodReturn ,RefundFee , Payment , Reason,Description ,")
	                    .append(" Title , Price , Num , GoodReturnTime , Sid , ")
	                    .append(" TotalFee , Iid , OuterIid , OuterSkuId , CompanyName ,") 
	                    .append(" Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)")
	                    .append(" values('").append(sheetid ).append("','").append(sheetid).append("','','','").append(BuyerNick).append("','")  //5
	                    .append( Formatter.format(new Date(order.getRefund_time()*1000L), Formatter.DATE_TIME_FORMAT)).append("','").append(Formatter.format(new Date(order.getAdd_time()*1000L), Formatter.DATE_TIME_FORMAT)).append("','退货','','BUYER_NOT_RECEIVED',")//10
	                    .append("'1','").append(order.getRefund_paid()).append("','").append(Payment).append("','','','")
	                    .append(item.getGoods_name()).append("','").append(item.getGoods_price()).append("','").append(item.getGoods_number()).append("','").append(Formatter.format(new Date(order.getRefund_time()*1000L), Formatter.DATE_TIME_FORMAT)).append("','")
	                    .append("','").append(order.getMoney_paid()).append("','").append(item.getGoods_id()).append("','','").append(item.getGoods_sn()).append("',")
	                    .append("'','").append(new String(order.getAddress().getBytes(),"gbk").replaceAll("'","''")).append("','").append(new String(order.getAddress().getBytes(),"gbk").replaceAll("'","''")).append("','").append(inshopid).append("','").append(order.getOrder_sn()).append("','").append(new String(order.getUser_name().getBytes(),"gbk")).append("','").append(order.getTel()).append("','')").toString();

					Log.info("退货单sql: "+sql) ;
					SQLHelper.executeSQL(conn,sql);
					
					//加入到通知表     退货标志为2
		            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
		                + sheetid +"',2 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";				
					//Log.info(sql) ;
					SQLHelper.executeSQL(conn,sql);
					
					Log.info(jobname,"接口单号:"+sheetid+" 退货订单号:"+order.getOrder_sn()+"，订单更新时间:"+Formatter.format(new Date(order.getAdd_time()*1000L),Formatter.DATE_TIME_FORMAT));
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
					throw new JSQLException("生成退货【" + order.getOrder_sn() + "】接口数据失败!"+e1.getMessage());
				}
			}
		}
		catch (Exception e) 
		{
			Log.error(jobname, "生成接口退货单失败,订单号:"+order.getOrder_sn() + ",退换货类型:"+",错误信息:"+e.getMessage()) ;
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
	
	//根据颜色，货号，尺码查询sku
	public static String getItemCodeByColSizeCustom(Connection conn,String goods_no,
			String color, String size) {
		String result="";
		String sql = new StringBuilder().append("select custombc from v_barcodeAll where colorname='")
			.append(color).append("' and customno='").append(goods_no)
			.append("' and sizename='").append(size).append("'").toString();
		try{
			Log.info("查sku:　"+sql);
			result = SQLHelper.strSelect(conn, sql);
		}catch(Exception ex){
			Log.error("查询商品sku出错", ex.getMessage());
		}
		return result;
	}




	//根据orderId获取一个订单详情
	public static Order getOrderById(String vcode, String orderCode,String url) throws Exception{
		Order o = new Order();
		//方法名
		String apimethod="get_order_info.php";
		HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("oid", orderCode);
        map.put("apimethod", apimethod);
        map.put("key", MD5Util.getMD5Code((Params.vcode+orderCode).getBytes()));

        //发送请求
		String responseText = CommHelper.doPost(map,url);
		Log.info("订单号: "+orderCode+", 订单详情为: "+responseText);
		JSONObject order= new JSONObject(responseText);
		if(5==order.getInt("status")) {
			Log.info("订单号: "+orderCode+", 订单详情为空");
			return o;
		}
		o.setObjValue(o, order.getJSONObject("list"));
		o.setFieldValue(o,"goods_list",order.getJSONObject("list").getJSONArray("goods_list"));
		return o;
	}




	/**
	 * 获取单条退货订单信息
	 * @param string
	 * @param conn
	 * @param tradecontactid
	 * @param o
	 * @param url
	 */
	public static RefundOrder getRefundOrderByCode(String vcode, String orderCode, String url) throws Exception{
		RefundOrder o = new RefundOrder();
		//方法名
		String apimethod="get_refund_info.php";
		HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("oid", orderCode);
        map.put("apimethod", apimethod);
        map.put("key", MD5Util.getMD5Code((Params.vcode+orderCode).getBytes()));

        //发送请求
		String responseText = CommHelper.doPost(map,url);
		Log.info("退货订单详情为: "+responseText);
		JSONObject order= new JSONObject(responseText);
		if(order.getInt("status")!=1){
			throw new Exception("获取退货订单订单出错,订单号:　"+orderCode);
		}
		o.setObjValue(o, order.getJSONObject("list"));
		o.setFieldValue(o,"goods_list",order.getJSONObject("list").getJSONArray("goods_list"));
		return o;
		
	}

}
