package com.wofu.ecommerce.taobao;

import java.sql.Connection;
import java.util.Date;
import java.util.Iterator;
import com.taobao.api.domain.RefundItem;
import com.taobao.api.domain.ReturnBill;
import com.taobao.api.domain.Tag;
import com.taobao.api.domain.Trade;
import com.taobao.api.domain.RefundBill;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;


public class RefundUtil {
	public static boolean RefundBillisCheck(Connection connection, String tid, String refundid,String modified)
    throws JSQLException
    {
		boolean flag = false;
		String sql = "select count(*) from ns_refundbill with(nolock) "
			+"where tid='"+tid+"' and refundid='"+refundid+"' "
			+"and modified='"+modified+"'";
		flag = SQLHelper.intSelect(connection, sql) != 0;
		return flag;
    }
	
	public static String createRefundBill(Connection conn,RefundBill refundbill,Trade td,String tradecontactid)throws Exception{
		try {
			//获取交易信息
			
			
			String taglist="";
			for (int i=0;i<refundbill.getTagList().size();i++)
			{
				Tag tag=(Tag) refundbill.getTagList().get(i);
				
				if (tag.getTagKey()!=null)
					taglist.concat(tag.getTagKey()+"|"+tag.getTagName()+"|"+tag.getTagType());
				
				if (i!=refundbill.getTagList().size()-1) taglist.concat(",");
			}
			
			String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="
				+ tradecontactid;
			String inshopid = SQLHelper.strSelect(conn, sql);
			
			String sheetid = "";
			conn.setAutoCommit(false);

			sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);

			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid
					+ "',11 , '"
					+ tradecontactid
					+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			sql = "insert into ns_refundbill"
					+ "(sheetid,refund_id,refund_version,refund_phase,refund_type,"
					+ "operation_constraint,trade_status,refund_fee,reason,actual_refund_fee,created,current_phase_timeout,"
					+ "alipay_no,buyer_nick,seller_nick,shopid,linkman,linktele,mobile,address,buyeralipayno,tid,oid,cs_status,"
					+ "attribute,status,tags,modified,description)"
					+ "values("+"'"
					+ sheetid
					+ "','"
					+ refundbill.getRefundId()
					+ "','"
					+ refundbill.getRefundVersion()
					+ "','"
					+ refundbill.getRefundPhase()
					+ "','"
					+ refundbill.getRefundType()
					+ "','"
					+ refundbill.getOperationConstraint()
					+ "','"
					+ refundbill.getTradeStatus()
					+ "',"
					+ refundbill.getRefundFee()
					+ ",'"
					+ refundbill.getReason()
					+ "',"
					+ refundbill.getActualRefundFee()
					+ ",'"
					+ Formatter.format(td.getCreated(), Formatter.DATE_TIME_FORMAT)
					+ "','"
					+ refundbill.getCurrentPhaseTimeout()  //运费
					+ "','"
					+ refundbill.getAlipayNo()
					+ "','"
					+ refundbill.getBuyerNick()
					+ "','"
					+ refundbill.getSellerNick()
					+ "','"
					+ inshopid
					+ "','"
					+ td.getReceiverName()
					+ "','"
					+ td.getReceiverPhone()
					+ "','"
					+ td.getReceiverMobile()
					+ "','"
					+ td.getReceiverAddress()
					+ "','"
					+ td.getBuyerAlipayNo()
					+ "','"
					+ refundbill.getTid()
					+ "','"
					+ refundbill.getOid()
					+ "','"
					+ refundbill.getCsStatus()
					+ "','"
					+ refundbill.getAttribute()
					+ "','"
					+ refundbill.getStatus()
					+ "','"
					+ taglist
					+ "','"
					+ Formatter.format(td.getModified(), Formatter.DATE_TIME_FORMAT)
					+ "','"
					+ refundbill.getDesc()
					+ "')";

			SQLHelper.executeSQL(conn, sql);
			//循环添加商品详情
			for(Iterator ito=refundbill.getItemList().iterator();ito.hasNext();){
				
				RefundItem refitem=(RefundItem) ito.next();
				
				sql = "insert into ns_refundbillitem(sheetid , num_iid  , price , num , outer_id , sku) values( "
					+ "'"
					+ sheetid
					+ "','"
					+ refitem.getNumIid()
					+ "',"
					+ refitem.getPrice()
					+ ","
					+ refitem.getNum()
					+ ",'"
					+ refitem.getOuterId()
					+ "','"
					+ refitem.getSku()
					+ "')";
				SQLHelper.executeSQL(conn, sql);
			}
			

			conn.commit();
			conn.setAutoCommit(true);

			Log.info("生成天猫退款成功,接口单号:"+ sheetid+ " 订单号:"+ refundbill.getTid()+ " 订单状态："
					+ td.getStatus()+ " 退款状态:"+ refundbill.getStatus()+ " 订单创建时间:"
					+ Formatter.format(td.getCreated(),
							Formatter.DATE_TIME_FORMAT));

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
			throw new JException("生成天猫退款失败,订单【" + refundbill.getTid() + "】接口数据失败,错误信息："
					+ e1.getMessage());
		}
	}
	
	
	
	public static boolean ReturnBillisCheck(Connection connection, String tid, String refundid,String modified)
    throws JSQLException
    {
		boolean flag = false;
		String sql = "select count(*) from ns_returnbill with(nolock) "
			+"where tid='"+tid+"' and refundid='"+refundid+"' "
			+"and modified='"+modified+"'";
		flag = SQLHelper.intSelect(connection, sql) != 0;
		return flag;
    }
	
	
	public static String createReturnBill(Connection conn,ReturnBill returnbill,Trade td,String tradecontactid)throws Exception{
		try {
		
			
			String taglist="";
			for (int i=0;i<returnbill.getTagList().size();i++)
			{
				Tag tag=(Tag) returnbill.getTagList().get(i);
				
				if (tag.getTagKey()!=null)
					taglist.concat(tag.getTagKey()+"|"+tag.getTagName()+"|"+tag.getTagType());
				
				if (i!=returnbill.getTagList().size()-1) taglist.concat(",");
			}
			
			String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="+ tradecontactid;
			String inshopid = SQLHelper.strSelect(conn, sql);
			
			String sheetid = "";
			conn.setAutoCommit(false);

			sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);

			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
					+"values('yongjun','"+ sheetid+ "',11 , '"	+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			sql = "insert into ns_returnbill"
					+ "(sheetid,refund_id,refund_version,status,refund_phase,"
					+ "reason,company_name,sid,created,tid,oid,shopid,"
					+ "linkman,linktele,mobile,address,buyeralipayno,operation_log," 
					+"tags,modified,description)"
					+ "values("+"'"
					+ sheetid
					+ "','"
					+ returnbill.getRefundId()
					+ "','"
					+ returnbill.getRefundVersion()
					+ "','"
					+ returnbill.getStatus()
					+ "','"
					+ returnbill.getRefundPhase()
					+ "','"
					+ returnbill.getReason()
					+ "','"
					+ returnbill.getCompanyName()
					+ "','"
					+ returnbill.getSid()
					+ "','"
					+ Formatter.format(td.getCreated(), Formatter.DATE_TIME_FORMAT)
					+ "','"
					+ td.getTid()
					+ "','"
					+ returnbill.getOid()
					+ "','"
					+ inshopid
					+ "','"
					+ td.getReceiverName()
					+ "','"
					+ td.getReceiverPhone()
					+ "','"
					+ td.getReceiverMobile()
					+ "','"
					+ td.getReceiverAddress()
					+ "','"
					+ td.getBuyerAlipayNo()
					+ "','"
					+ returnbill.getOperationLog()
					+ "','"
					+ taglist
					+ "','"
					+ Formatter.format(td.getModified(),Formatter.DATE_TIME_FORMAT)
					+ "','"
					+ returnbill.getDesc()
					+ "')";

			SQLHelper.executeSQL(conn, sql);
			
			//循环添加商品详情
			for(Iterator ito=returnbill.getItemList().iterator();ito.hasNext();){
				
				RefundItem refitem=(RefundItem) ito.next();
				
				sql = "insert into ns_returnbillitem(sheetid , num_iid  , price , num , outer_id , sku) values( "
					+ "'"+ sheetid	+ "','"	+ refitem.getNumIid()+ "',"
					+ refitem.getPrice()+ ","+ refitem.getNum()	+ ",'"+ refitem.getOuterId()
					+ "','"+ refitem.getSku()+ "')";
				SQLHelper.executeSQL(conn, sql);
			}
			

			conn.commit();
			conn.setAutoCommit(true);

			Log.info("生成天猫退货单成功,接口单号:"+ sheetid+ " 订单号:"+ returnbill.getTid()+ " 订单状态："
					+ td.getStatus()+ " 退货状态:"+ returnbill.getStatus()+ " 订单创建时间:"
					+ Formatter.format(td.getCreated(),
							Formatter.DATE_TIME_FORMAT));

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
			throw new JException("生成天猫退货单失败，订单【" + td.getTid() + "】,错误信息："
					+ e1.getMessage());
		}
	}
}
