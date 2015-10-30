package com.wofu.ecommerce.jiaju;
import java.sql.Connection;
import java.util.Date;
import java.util.Iterator;

import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class OrderUtils {
	/**
	 * 创建接口订单
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
			
			//商家总优惠金额
			float sellerDiscount = 0.0f ;
			
			int j=0;
			for(Iterator ito=o.getGoods().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();	
				String itemPayment = String.valueOf(item.getPrice_total());	//取实际总价
				sql = new StringBuilder()
					.append("insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , ")
                    .append(" title , sellernick , buyernick , type , created , ") 
                    .append(" refundstatus , outeriid , outerskuid , totalfee , payment , ")
                    .append(" discountfee , adjustfee , status , timeoutactiontime , owner , ")
                    .append(" skuPropertiesName , num , price , picPath , " )
                    .append("modified) values( '")
                    .append(sheetid).append("','")	//CustomerOrderId
                    .append(sheetid).append("-").append(o.getTrade_id()).append(String.valueOf(++j)).append("','")	//orderItemId
                    .append(sheetid).append("','")	//SheetID
                    .append(item.getOuter_id()).append("','','")	//skuid,itemmealname
                    .append(item.getGoods_name()).append("','")		//title
                    .append(username).append("','")					//sellernick
                    .append(o.getNickname()).append("','','")		//buyernick,type
                    .append(Formatter.format(o.getAdd_time(), Formatter.DATE_TIME_FORMAT)).append("','','")	//created,refundstatus
                    .append(item.getGoods_id()).append("','")		//outeriid
                    .append(item.getOuter_id()).append("','")		//outerskuid(接口没提供sku,只提供了Goods_id与Outer_id(),客服说Outer_id就是商家的唯一商品ID,sku他们没有)
                    .append(itemPayment).append("','")				//totalfee
                    .append(itemPayment).append("','','','")		//payment,discountfee,adjustfee
                    .append(o.getStatus()).append("','','接口','','")	//status,timeoutactiontime,owner,skuPropertiesName
                    .append((int)item.getAmount()).append("','")	//num
                    .append(item.getPrice()).append("','")			//price
                    .append(item.getGoods_logo()).append("','")		//picPath
                    .append(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)).append("')").toString();	//modified

				//Log.info("ns_orderitem的SQL语句："+sql);
        		SQLHelper.executeSQL(conn, sql) ;

			}
			//Log.info("ns_orderitem写入完毕，到ns_customerorder表");
			//加入到单据表
			sql =  new StringBuilder()
				.append("insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , ")
            	.append(" type , created , buyermessage , shippingtype , payment , ")
				.append(" discountfee , adjustfee , status ,paytime,totalfee , postfee , buyeralipayno , ")
				.append(" buyernick ,buyerUin, receivername , receiverstate , receivercity , receiverdistrict , ")
				.append(" receiveraddress , receivermobile , ")
				.append(" tradefrom,TradeContactID,modified,distributeTid) values('")
				.append(sheetid).append("','")			//CustomerOrderId
				.append(sheetid).append("','接口','")		//SheetID,Owner
				.append(o.getTrade_id()).append("','','")	//tid,OrderSheetID
				.append(username).append("','','")			//sellernick,type
				.append(Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)).append("','")	//created
				.append(o.getRemark()).append("','','")		//buyermessage,shippingtype
				.append(o.getFee_trade()).append("','")		//payment
				.append(sellerDiscount).append("','','")	//discountfee,adjustfee
				.append(new String(o.getStatus().getBytes(),"GBK")).append("','")	//status(从接口那里获取的都是等待发货)
				.append(Formatter.format(o.getPayment_time(),Formatter.DATE_TIME_FORMAT)).append("','")	//paytime
				.append(o.getFee_trade()).append("','")		//totalfee
				.append(o.getFee_fare()).append("','','")	//postfee,buyeralipayno
				.append(o.getNickname()).append("','','")	//buyernick,buyerUin
				.append(o.getConsignee()).append("','")		//receivername
				.append(o.getProv()).append("','")			//receiverstate
				.append(o.getCity()).append("','")			//receivercity
				.append(o.getCounty()).append("','")		//receiverdistrict
				.append(o.getDetail_addr().replaceAll("'", "")).append("','")	//receiveraddress
				.append(o.getMobile()).append("','家居就','")	//receivermobile,tradefrom
                .append(tradeContactID).append("','")		//TradeContactID
                .append(Formatter.format(o.getAdd_time(), Formatter.DATE_TIME_FORMAT)).append("','")	//modified
            	.append(o.getOrder_id()).append("')").toString();		//distributeTid(保存短订单号,家居就特殊情况)
			//Log.info("ns_customerorder的SQL语句："+sql);

			SQLHelper.executeSQL(conn, sql);
			//加入到通知表
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('接口','"
                + sheetid +"',1 , '"+tradeContactID+"' , '接口' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("生成订单【" + o.getTrade_id() + "】接口数据成功，接口单号【" + sheetid + "】");

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
			throw new JException("生成订单【" + o.getTrade_id() + "】接口数据失败!"
					+ e1.getMessage());
		}
	}
	
	//删除已经发货或者发货时截单的订单
	public static void DelDeliveryOrder(Connection conn,String sheetid) throws Exception
	{
		String[] sidarr = sheetid.split(",");
		
		conn.setAutoCommit(false);
		for(int i=0;i<sidarr.length;i++)
		{
			String sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
					+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
					+ " where SheetID = '" + sidarr[i] + "' and SheetType = 3";
			SQLHelper.executeSQL(conn, sql);
			
			sql = "delete from IT_UpNote where SheetID='" + sidarr[i] + "' and sheettype=3";
			SQLHelper.executeSQL(conn, sql);
			
			conn.commit();
		}
		conn.setAutoCommit(true);
	}
}
