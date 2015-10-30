package com.wofu.ecommerce.lefeng;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
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

			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid+ "',1 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  , sellernick , "
					+ "  created ,  payment ,  status  , "
					+ " tradememo , paytime ,  modified , "
					+ " totalfee , postfee  , "
					+ " buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone , tradefrom,tradeContactid) "
					
					+ " values('"+ sheetid+ "','"+ sheetid+ "','"+username+"','"+ o.getOrderCode()
					+ "','"+ username+ "', '"+o.getCreateTime()+"',"+ o.getTotalPay()/100+ ", '"
					+ o.getOrderStatus()+ "' , '"+ o.getOperatorMemo()+ "' ,'"+o.getPayTime()+"',"
					+"'"+o.getPayTime()+ "' , "+ o.getTotalPay()/100+ " , '"+o.getDeliverPay()/100+ "'"
					+ ",'"	+ o.getPayerUserId()+ "' ,'"+ o.getReceiverName()+ "' , '"
					+ o.getReceiverProvince()+ "', '"	+ o.getReceiverArea().split("-")[0]+ "' , '"+o.getReceiverArea().split("-")[1]+"', "
					+ "'"+ o.getReceiverAddress()+ "','"+ o.getReceiverPostcode()+ "' , '"
					+ o.getReceiverMobile()+ "' , '"+ o.getReceiverPhone()+ "','lefeng'," + tradecontactid + ")";

			SQLHelper.executeSQL(conn, sql);

			for (int i=0;i<o.getItemList().getRelationData().size();i++) {
				
				OrderItem item = (OrderItem) o.getItemList().getRelationData().get(i);


				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID  ,skuid, itemmealname , "
						+ " title , sellernick , created , "
						+ "  outerskuid , totalfee , payment ,num , price ) values( "
						+ "'"+ sheetid+ "','"+ sheetid+ item.getItemCode()+ "','"+ sheetid+ "','0','"+ item.getItemName()
						+ "', '"+ item.getItemName()+ "' , '"+ username+ "', '"+o.getCreateTime()
						+ "', '"+ item.getItemCode()+ "' , '"+ item.getItemQuantity()*item.getItemPirce()/100
						+ "' , '"+item.getItemQuantity()*item.getItemPirce()/100+"',"				
						+ item.getItemQuantity()+ " , '"+ item.getItemPirce()/100+"')";
				SQLHelper.executeSQL(conn, sql);

		
			}

			conn.commit();
			conn.setAutoCommit(true);

			Log.info("生成订单【" + o.getOrderCode() + "】接口数据成功，接口单号【"+ sheetid + "】");

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
			throw new JException("生成订单【" + o.getOrderCode() + "】接口数据失败,错误信息："+ e1.getMessage());
		}
	}
	
	public static Order getOrderByID(String url,String shopid,String secretKey,String encoding,String orderCode) throws Exception
	{
		Order o=new Order();
		String methodApi="sellerSearchDealList";
		
		Hashtable<String, String> params = new Hashtable<String, String>() ;
		params.put("shopId", shopid) ;
		params.put("shopOrderId", orderCode) ;
		
		String sign=LefengUtil.getSign(params, methodApi, secretKey, encoding);
		
		params.put("sign", sign);
		

		String responseText = LefengUtil.filterResponseText(CommHelper.sendRequest(url+methodApi+".htm",params,"",encoding));

		
		responseText=StringUtil.replace(responseText, "null", "\"\"");

		
		JSONObject jo = new JSONObject(responseText);
		
		int retcode=jo.optInt("result");
		
		if (retcode==0)
		{	
			JSONArray dealList=jo.optJSONArray("dealList");
			
			if (dealList.length()!=0)
			{
				JSONObject deal=dealList.getJSONObject(0);
				
				o.setObjValue(o, deal);
		
			}
		}
		return o;
	}
	
	public static void getRefund(String modulename, Connection conn,String tradecontactid,Order o)	throws Exception {

			
		/*
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
					+ " HasGoodReturn ,RefundFee , Payment , Reason,Description , Title ,"
					+ "Price , Num , GoodReturnTime , Sid , "
					+ " TotalFee ,  OuterIid , OuterSkuId , CompanyName , "
					+ "Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			
			Object[] sqlv = {
					sheetid,
					String.valueOf(r.getRefundId()),
					String.valueOf(r.getOid()),
					r.getAlipayNo(),
					r.getBuyerNick(),
					r.getCreated(),
					r.getModified(),
					r.getOrderStatus(),
					r.getStatus(),
					r.getGoodStatus(),
					hasGoodReturn,
					Double.valueOf(r.getRefundFee()),
					Double.valueOf(r.getPayment()),
					r.getReason(),
					r.getDesc(),
					r.getTitle(),
					Double.valueOf(r.getPrice()),
					r.getNum().intValue(),
					r.getGoodReturnTime(),
					r.getSid(),
					Double.valueOf(r.getTotalFee()),
					o.getOuterIid(),
					o.getOuterSkuId(),
					r.getCompanyName(),
					r.getAddress(),
					td.getReceiverState() + " " + td.getReceiverCity() + " "
							+ td.getReceiverDistrict() + " "
							+ td.getReceiverAddress(), inshopid,
					String.valueOf(r.getTid()), td.getReceiverName(),
					td.getReceiverPhone() + " " + td.getReceiverMobile(),
					td.getBuyerAlipayNo() };


			SQLHelper.executePreparedSQL(conn, sql, sqlv);

	

			Log.info(modulename, "接口单号:"	+ sheetid+ " 订单号:"+ o.getOrderCode()+ " 订单状态："+ LefengUtil.getStatusName(o.getOrderStatus())				
					+ " 订单创建时间:"+o.getCreateTime());

			conn.commit();
			conn.setAutoCommit(true);

		 */
	}
}
