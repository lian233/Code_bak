package com.wofu.ecommerce.meilishuo2;
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
			
			//订单明细
			float totalPrice = 0.00f ;//总金额
			float sellerDiscount = 0.0f ;//商家总优惠金额
			//实付总金额
			float totalItemPayment=0.0f;
			//应付总金额
			float totalfee=0.0f;
			//总邮费
			float totalPostfee=0.0f;
			int j=0;
			for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();	
				String itemPayment = String.valueOf(item.getPrice()*item.getAmount());
				sql = new StringBuilder().append("insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , ")
                    .append(" title , sellernick , buyernick , type , created , ") 
                    .append(" refundstatus , outeriid , outerskuid , totalfee , payment , ")
                    .append(" discountfee , adjustfee , status , timeoutactiontime , owner , ")
                    .append(" skuPropertiesName , num , price , picPath , " )
                    .append("modified) values( '")
                    .append(sheetid).append("','").append(sheetid).append("-").append(o.getOrder_id()).append(String.valueOf(++j))
                    .append("','").append(sheetid).append("','").append(item.getSku()).append("','','")
                    .append(item.getGoods_title()).append("','").append(username).append("','")
                    .append(o.getNickname()).append("','','")
                    .append(Formatter.format(o.getCtime(), Formatter.DATE_TIME_FORMAT)).append("','','").append(item.getGoods_no()).append("','")
                    .append(item.getSku()).append("','").append(itemPayment).append("','").append(itemPayment).append("','")
                    .append("','','").append(o.getStatus_text()).append("','','yongjun',").append("'','")
                    .append((int)item.getAmount()).append("','").append(item.getPrice()).append("','").append(item.getGoods_img())
                    .append("','").append(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)).append("')").toString();
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
				.append(" tradefrom,TradeContactID,modified) values('")
				.append(sheetid).append("','").append(sheetid).append("','yongjun','").append(o.getOrder_id()).append("','','").append(username)  //6
				.append("','','").append(Formatter.format(o.getCtime(),Formatter.DATE_TIME_FORMAT)).append("','','','").append(o.getTotal_price()).append("','").append(sellerDiscount).append("','','")  //13
				.append(new String(o.getStatus_text().getBytes(),"GBK")).append("','").append(Formatter.format(o.getCtime(),Formatter.DATE_TIME_FORMAT)).append("','").append(o.getTotal_price()).append("','").append(o.getExpress_price()).append("','','").append(o.getBuyer_nickname()).append("','','")
				.append(o.getNickname()).append("','").append(o.getProvince()).append("','").append(o.getCity()).append("','").append(o.getDistrict())
				.append("','").append(o.getStreet().replaceAll("'", "")).append("','").append(o.getPhone()).append("',")
                .append("'MEILISUO','").append(tradeContactID).append("','").append(Formatter.format(o.getCtime(), Formatter.DATE_TIME_FORMAT)).append("')").toString();
			//Log.info("ns_customerorder的SQL语句："+sql);

			SQLHelper.executeSQL(conn, sql);
			//加入到通知表
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                + sheetid +"',1 , '"+tradeContactID+"' , 'yongjun' , getdate() , null) ";
            //Log.info("神经病人欢乐多："+sql);
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
			Order order,String url,String appKey,String appsecret)
	{
		
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
	 * 根据苏宁产品编码获取商家sku,商品图片链接
	 * @return
	 */
	public static String[] getItemCodeByProduceCode(String productCode,String appKey,String appSec,String format) throws Exception{
			return null;
		
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
}
