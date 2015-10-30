package com.wofu.ecommerce.coo8;

import java.sql.Connection;
import java.util.Iterator;

import com.coo8.open.order.Order;
import com.coo8.open.order.OrderDetail;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;


public class OrderUtils {

	/**
	 * 库巴 转入一个订单到接口表
	 */
	public static String createInterOrder(Connection conn,
			Order o, String tradecontactid,String username)
			throws Exception {
		try {
			//商品金额
			int totol=0;
			for(Iterator ito=o.getOrderDetails().iterator();ito.hasNext();)
			{
				OrderDetail detail=(OrderDetail) ito.next();
				totol+=detail.getCount()*detail.getPrice();
			}
			int invoice=0;
			if(o.getConsignee().getInvoice().endsWith("0")){
				invoice=0;
			}else{
				invoice=1;
			}
			String sheetid = "";
			conn.setAutoCommit(false);
			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");
			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid
					+ "',1 , '"
					+ tradecontactid
					+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId,SheetID,Owner,tid,sellernick,"
					+ "created,payment,status,paytime,modified,totalfee,postfee,"
					+ "buyernick,receivername,receiverstate,receivercity,receiverdistrict,"
					+ "receiveraddress,receiverzip,receivermobile,receiverphone,invoicetitle,InvoiceFlag,tradeContactid)"
					+ "values("+"'"
					+ sheetid
					+ "','"
					+ sheetid
					+ "','"
					+ username
					+ "','"
					+ o.getOrderId()
					+ "','"
					+ username
					+ "','"
					+ Formatter.format(o.getOrderTime(),Formatter.DATE_TIME_FORMAT)
					+ "',"
					+ o.getPayment()//实付款
					+ ",'"
					+ o.getStatus()
					+ "','"
					+ Formatter.format(o.getOrderChangeTime(),Formatter.DATE_TIME_FORMAT)//付款时间
					+ "','"
					+ Formatter.format(o.getOrderChangeTime(),Formatter.DATE_TIME_FORMAT)//最后修改时间
					+ "',"
					+ totol//总付款
					+ ","
					+ o.getFreightPrice()  //运费
					+ ",'"
					+ o.getUserId()
					+ "','"
					+ o.getConsignee().getName()
					+ "','"
					+ o.getConsignee().getProvince()
					+ "','"
					+ o.getConsignee().getCity()
					+ "','"
					+ o.getConsignee().getCounty()
					+ "','"
					+ o.getConsignee().getAddress()
					+ "','"
					+ o.getConsignee().getPost()//邮编
					+ "','"
					+ o.getConsignee().getMobilephone()
					+ "','"
					+ o.getConsignee().getTelephone()
					+ "','"
					+ o.getConsignee().getInvoiceTitle()
					+ "',"
					+ invoice
					+ ","
					+ tradecontactid
					+ ")";

			SQLHelper.executeSQL(conn, sql);
			//循环添加商品详情
			for(Iterator ito=o.getOrderDetails().iterator();ito.hasNext();){
				OrderDetail detail=(OrderDetail) ito.next();
				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , "
					+ " title , sellernick ,buyernick,  created , "
					+ "  outeriid , outerskuid , totalfee , payment , "
					+ " skuPropertiesName,  status  ,"
					+ " num , price , "
					+ " numiid ) values( " + "'"
					+ sheetid
					+ "','"
					+ sheetid+detail.getItemId()
					+ "','"
					+ sheetid
					+ "','"
					+ detail.getMainId()
					+ "', '"
					+ detail.getItemName()
					+ "' , '"
					+ detail.getItemName()
					+ "' ,'"
					+ username
					+"' ,'"
					+ o.getConsignee().getName()
					+ "', '"
					+ Formatter.format(o.getOrderTime(),
							Formatter.DATE_TIME_FORMAT)
					+ "', '"
					+ detail.getOutId()
					+ "' , '"
					+ detail.getOutId()
					+ "' ,'"
					+ (detail.getPrice()*detail.getCount()-(detail.getPartDiscountPrice()!=null?detail.getPartDiscountPrice():0)-detail.getCouponValue())
					+ "' , '"
					+ (detail.getPrice()*detail.getCount()-(detail.getPartDiscountPrice()!=null?detail.getPartDiscountPrice():0)-detail.getCouponValue()+o.getFreightPrice())
					+ "','"
					+ "color:"+detail.getColor()+",size:"+detail.getSize()
					+ "','"
					+ o.getStatus()
					+ "',"
					+ detail.getCount()
					+ ","
					+ detail.getPrice()
					+ ",'"
					+ detail.getItemId() + "')";//商品ID
				SQLHelper.executeSQL(conn, sql);
			}
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("生成订单【" + o.getOrderId() + "】接口数据成功，接口单号【" + sheetid + "】");
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
			throw new JException("生成订单【" + o.getOrderId() + "】接口数据失败,错误信息："
					+ e1.getMessage());
		}
	}

	//退货
	public static void getRefund(String modulename, Connection conn,
			String tradecontactid, Order o) throws Exception {
		OrderDetail detail=o.getOrderDetails().get(0);
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
				+ "values('yongjun','"
				+ sheetid
				+ "',2 , '"
				+ tradecontactid
				+ "' , 'yongjun' , getdate() , null) ";
		SQLHelper.executeSQL(conn, sql);

		sql = "insert into ns_Refund(SheetID , RefundID , Oid , "
				+ "BuyerNick , Created , Modified , OrderStatus , Status , GoodStatus , "
				+ " HasGoodReturn ,RefundFee , Payment ,  Title ,"
				+ "Price , Num ,"
				+ " TotalFee ,  OuterIid , OuterSkuId  , "
				+ " ReturnAddress , InShopID , Tid , LinkMan , LinkTele)"
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		Object[] sqlv = {//23
				sheetid,
				o.getOrderId(),
				o.getOrderId(),
				o.getUserId(),
				Formatter.format(o.getOrderTime(), Formatter.DATE_TIME_FORMAT),
				Formatter.format(o.getOrderChangeTime(), Formatter.DATE_TIME_FORMAT),
				o.getStatus(),
				o.getStatus(),
				o.getStatus(),
				1,
				o.getPayment(),
				o.getPayment(),
				detail.getItemName(),
				detail.getPrice(),
				detail.getCount(),
				detail.getCount()*detail.getPrice(),
				detail.getOutId(),
				detail.getItemId(),
				o.getConsignee().getAddress(),
				inshopid,
				o.getOrderId(),
				o.getConsignee().getName(),
				o.getConsignee().getMobilephone() + " " + o.getConsignee().getTelephone()};

		SQLHelper.executePreparedSQL(conn, sql, sqlv);

		Log.info(modulename, "接口单号:"
				+ sheetid
				+ " 订单号:"
				+ o.getOrderId()
				+ " 订单状态："
				+ o.getStatus()
				+ " 订单创建时间:"
				+ Formatter.format(o.getOrderTime(),
						Formatter.DATE_TIME_FORMAT));

		conn.commit();
		conn.setAutoCommit(true);

	}

}
