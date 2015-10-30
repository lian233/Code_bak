package com.wofu.ecommerce.uwuku;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.Types;
import com.wofu.common.tools.util.log.Log;

public class OrderUtils {
	/*
	 * 转入一个订单到接口表
	 */
	public static String createInterOrder(Connection conn,
			Order o, String tradecontactid,String username) throws Exception {
		try {

			String sheetid = "";

			conn.setAutoCommit(false);

			String sql = "declare @Err int ; declare @NewSheetID char(16); " +
					"execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
					+"values('yongjun','"+ sheetid+ "',1 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  , sellernick , "
					+ "  created , payment, status,paytime ,  modified ,totalfee , postfee  , "
					+ " buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone , tradeContactid) "
					
					+ " values("+ "'"+ sheetid+ "','"+ sheetid+ "','"+username+"','"+ o.getTid()
					+ "','"+ username+ "', '"+o.getCreat_time()+"',"+ o.getPayment()+ "', "+ "'"
					+ o.getStatus()+ "' ,'"+Formatter.format(o.getPay_time(),Formatter.DATE_TIME_FORMAT)+"',"
					+"'"+Formatter.format(o.getModified(),Formatter.DATE_TIME_FORMAT)+ " , "+ o.getTotal_fee()+ "' , '"+o.getPost_fee()+ "','"
					+ "','"	+ o.getBuyer_nick()+ "' ,'"+ o.getReceiver_name()+ "' , '"
					+ o.getReceiver_state()+ "', '"	+ o.getReceiver_city()+ "' , '"+o.getReceiver_district()+"', "
					+ "'"+ o.getReceiver_address()+ "','"+ o.getReceiver_zip()+ "' , '"
					+ o.getReceiver_mobile()+ "' , '"+ o.getReceiver_phone()+ "'," + tradecontactid + ")";

			SQLHelper.executeSQL(conn, sql);



			sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , "
				+ " title , sellernick , buyernick ,  created , "
				+ "  outeriid , outerskuid , totalfee , payment , "
				+ " discountfee , adjustfee , status  ,"
				+ " skuPropertiesName , num , price , picPath , "
				+ "  buyerRate ,sellerRate , numiid ) values( "
					+ "'"+ sheetid+ "','"+ sheetid+ "','"+ sheetid+ "','"+ o.getSku_id()
					+ "', '"+o.getTitle()+ "' , '"+o.getTitle()+ "' ,'"+ username+ "', '"+Formatter.format(o.getCreat_time(),Formatter.DATE_TIME_FORMAT)
					+ "', '"+ o.getOuter_id()+ "' , '"+ o.getSku_outer_id()+ "' ,'"+ o.getPayment()
					+ "' , '"+o.getDiscount_fee()+"','"+o.getAdjust_fee()+"','"			
					+ o.getStatus()+ "' , '"+ o.getProperties_name()+"',"+o.getNum()+","+o.getPrice()+",'"+o.getPic_path()+"',"
					+o.getBuyer_rate()+","+o.getSeller_rate()+",'"+o.getNum_iid()+"')";
			SQLHelper.executeSQL(conn, sql);

		
			

			conn.commit();
			conn.setAutoCommit(true);

			Log.info("生成订单【" + o.getTid() + "】接口数据成功，接口单号【"+ sheetid + "】");

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
			throw new JException("生成订单【" + o.getTid() + "】接口数据失败,错误信息："+ e1.getMessage());
		}
	}
	
	public static void getRefund(String modulename, Connection conn,String tradecontactid,Order o)	throws Exception {

			
		
			String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="
					+ tradecontactid;
			String inshopid = SQLHelper.strSelect(conn, sql);

			conn.setAutoCommit(false);

			sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			String sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
					+"values('yongjun','"+ sheetid+ "',2 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			sql = "insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , "
					+ "BuyerNick , Created , Modified , OrderStatus , Status , GoodStatus , "
					+ " HasGoodReturn ,RefundFee , Payment ,  Title ,"
					+ "Price , Num ,"
					+ " TotalFee ,  OuterIid , OuterSkuId  , "
					+ " ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			
			Object[] sqlv = {
					sheetid,
					o.getTid(),
					o.getTid(),
					o.getAli_trade_no(),
					o.getBuyer_nick(),
					Formatter.format(o.getCreat_time(), Formatter.DATE_TIME_FORMAT),
					Formatter.format(o.getModified(), Formatter.DATE_TIME_FORMAT),
					o.getStatus(),
					o.getStatus(),
					o.getStatus(),
					1,
					o.getPayment(),
					o.getPayment(),
					o.getTitle(),
					Double.valueOf(o.getPrice()),
					o.getNum(),
					o.getTotal_fee(),
					o.getOuter_id(),
					o.getSku_outer_id(),
					o.getReceiver_state() + " " + o.getReceiver_city() + " "
							+ o.getReceiver_district() + " "
							+ o.getReceiver_address(), inshopid,
					o.getTid(), o.getReceiver_name(),
					o.getReceiver_mobile() + " " + o.getReceiver_phone(),
					o.getAli_trade_no() };


			SQLHelper.executePreparedSQL(conn, sql, sqlv);

	

			Log.info(modulename, "接口单号:"	+ sheetid+ " 订单号:"+ o.getTid()+ " 订单状态："+ o.getStatus()				
					+ " 订单创建时间:"+Formatter.format(o.getCreat_time(),Formatter.DATE_TIME_FORMAT));

			conn.commit();
			conn.setAutoCommit(true);

		 
	}
}
