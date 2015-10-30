package com.wofu.ecommerce.jumei;

import java.sql.Connection;
import java.util.Date;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class OrderUtils {
	/*
	 * 转入一个订单到接口表
	 */
	public static String createInterOrder(Connection conn,
			Order o, String tradecontactid,String username) throws Exception {
		try {

			String sheetid = "";
			
			int paymode=1;
			
			int invoiceflag=0;
			
			String invoicetitle="";
			
			String address=o.getReceiver_info().getAddress();
			
			String state="";
			String city="";
			String district="";
			
		
			
			if (address.indexOf("-")>=0)
			{
				state=address.substring(0,address.indexOf("-"));
				address=address.substring(address.indexOf("-")+1);	
			}
			
			if (address.indexOf("-")>=0)
			{
				city=address.substring(0,address.indexOf("-"));
				address=address.substring(address.indexOf("-")+1);	
			}
			
			if (address.indexOf(" ")>=0)
			{
				district=address.substring(0,address.indexOf(" "));
				address=address.substring(address.indexOf(" ")+1);	
			}
			address = address.replaceAll("'", " ");
			
		
			
			conn.setAutoCommit(false);

			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid+ "',1 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  , sellernick , paymode,invoiceflag,invoicetitle,"
					+ "  created ,  payment ,  status  , paytime ,  modified , "
					+ " totalfee , postfee, buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone , tradefrom,tradeContactid) "
					
					+ " values('"+ sheetid+ "','"+ sheetid+ "','"+username+"','"+ o.getOrder_id()
					+ "','"+ username+ "', "+paymode+","+invoiceflag+",'"+invoicetitle+"','"+Formatter.format(new Date(o.getCreation_time()*1000),Formatter.DATE_TIME_FORMAT)+"',"+ (o.getTotal_products_price()+o.getDelivery_fee())+ ", '"
					+ o.getStatus()+ "' ,'"+Formatter.format(new Date(o.getTimestamp()*1000), Formatter.DATE_TIME_FORMAT)+"',"
					+"'"+Formatter.format(new Date(o.getTimestamp()*1000), Formatter.DATE_TIME_FORMAT)+ "' , "+ o.getTotal_products_price()+ " , "+o.getDelivery_fee()+ ",'"	+ o.getReceiver_info().getReceiver_name()+ "' ,'"+o.getReceiver_info().getReceiver_name()+ "' , '"
					+ state+ "', '"	+ city+ "' , '"+district+"', "
					+ "'"+ address+ "','"+ o.getReceiver_info().getPostalcode()+ "' , '"
					+o.getReceiver_info().getHp()+ "' , '"+ o.getReceiver_info().getPhone()+ "','jumei'," + tradecontactid + ")";

			SQLHelper.executeSQL(conn, sql);

			for (int i=0;i<o.getProduct_infos().getRelationData().size();i++) {
				
				OrderItem item = (OrderItem) o.getProduct_infos().getRelationData().get(i);


				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID  ,skuid, itemmealname , "
						+ " title , sellernick , created ,skuPropertiesName, "
						+ "  outerskuid , totalfee , payment ,num , price ) values( "
						+ "'"+ sheetid+ "','"+ sheetid+ item.getSku_no()+ "','"+ sheetid+ "','0','"+ item.getDeal_short_name()
						+ "', '"+ item.getDeal_short_name()+ "' , '"+ username+ "', '"+Formatter.format(new Date(o.getCreation_time()*1000),Formatter.DATE_TIME_FORMAT)
						+"','"+item.getAttribute()
						+ "', '"+ item.getUpc_code()+ "' , '"+ item.getSettlement_price()*item.getQuantity()
						+ "' , '"+item.getSettlement_price()*item.getQuantity()+"',"				
						+ item.getQuantity()+ " , '"+ item.getSettlement_price()+"')";
				SQLHelper.executeSQL(conn, sql);

		
			}

			conn.commit();
			conn.setAutoCommit(true);

			Log.info("生成订单【" + o.getOrder_id() + "】接口数据成功，接口单号【"+ sheetid + "】");

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
			throw new JException("生成订单【" + o.getOrder_id() + "】接口数据失败,错误信息："+ e1.getMessage());
		}
	}
	
}
