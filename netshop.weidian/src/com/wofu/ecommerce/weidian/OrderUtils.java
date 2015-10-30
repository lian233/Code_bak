package com.wofu.ecommerce.weidian;

import java.net.URLEncoder;
import java.sql.Connection;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weidian.utils.Utils;
import com.wofu.ecommerce.weidian.utils.getToken;




public class OrderUtils 
{
	/*
	 * 转入一个订单到接口表
	 */
	public static String createInterOrder(Connection conn,
			Order o, String tradecontactid,String username) throws Exception {
		try {

			String sheetid = "";
			
			int paymode=1/*o.getPayServiceType()*/;

			
			int invoiceflag=0;		
			String invoicetitle="";
			

			

			conn.setAutoCommit(false);

			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid+ "',1 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			
			String deliveryremark=o.getNote(); /*o.getDeliveryRemark()==null || o.getDeliveryRemark().equals("null")?"":o.getDeliveryRemark().replaceAll("'", "")*/;
			String merchantremark=o.getExpress_note();/*o.getMerchantRemark()==null || o.getMerchantRemark().equals("null")?"":o.getMerchantRemark().replaceAll("'", "")*/;


			String moblie=o.getPhone()!=null?o.getPhone():"";
			String phone=/*o.getPhone()!=null?o.getPhone():""*/moblie.equals("")?o.getPhone():"";
			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  , sellernick , paymode,invoiceflag,invoicetitle,"
					+ "  created ,  payment ,  status  , buyermemo , sellermemo  , paytime ,  modified , "
					+ " totalfee , postfee, buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone , tradefrom,tradeContactid,payfee," +
							"distributorshopname,distributeTid) "
					
					+ " values('"+ sheetid+ "','"+ sheetid+ "','"+username+"','"+ o.getOrder_id()
					+ "','"+ o.getSeller_name()+ "', "+paymode+","+invoiceflag+",'"+invoicetitle+"','"+Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)+"',"+ o.getTotal()+ ", '"
					+ o.getStatus()+ "' , '"+deliveryremark + "' , '"+ merchantremark+ "','"+Formatter.format(o.getPay_time(), Formatter.DATE_TIME_FORMAT)+"',"
					+"'"+Formatter.format(o.getAdd_time(), Formatter.DATE_TIME_FORMAT)+ "' , "+ String.valueOf(Double.parseDouble(o.getTotal().trim()))+ " , '"+Double.parseDouble(o.getExpress_fee())+ "'"
					+ ",'"	+ o.getSeller_id()+ "' ,'"+ o.getName()+ "' , '"
					+ /*o.getAddress()*/"" + "', '"	+ ""+ "' , '"+""+"', "
					+ "'"+ o.getAddress()+ "','"+ o.getPost()+ "' , '" 
					+ moblie+ "' , '"+ phone+ "','weidian'," + tradecontactid + ",'"+o.getTotal_fee()/*货到付款金额*/+
					"','"+  o.getSeller_id() +"','"+ o.getOrder_id()   + "'" 
					+")";
			System.out.println("sql1:"+sql);     //////testsql
			//System.out.println("getSalePrice:"+o.getSalePrice());
			SQLHelper.executeSQL(conn, sql);
			


			for (int i=0;i<o.getOrderItemList().getRelationData().size();i++) {
				
				OrderItem item = (OrderItem) o.getOrderItemList().getRelationData().get(i);
				//System.out.println("item.getUnitPrice():"+item.getUnitPrice());
				/*sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  ,oid, SheetID  ,skuid, itemmealname , "
					+ " title , sellernick , created , "
					+ "  outerskuid , totalfee , payment ,num , price  ,outeriid ,DistributePrice ,buyernick) values( "
					+ "'"+ sheetid+ "','"+ sheetid+ item.getItem_id() //用item.getSku_id()可能有问题+ "','"+item.getSku_id()+"','"+ sheetid+ "','"+item.getSku_id()+"','"+ item.getSku_id()
					+ "', '"+ item.getSku_title()+ "' , '"+ o.getSeller_id()+ "', '"+Formatter.format(o.getPay_time(),Formatter.DATE_TIME_FORMAT)
					+ "', '"+ item.getSku_id()+ "' , '"+ item.getTotal_price()
					+ "' , '"+item.getTotal_price()+"',"				
					+ item.getQuantity()+ " , '"+ item.getTotal_price() + "','" + item.getSku_id() + "'," + 0 
					+  ",'"+ o.getSeller_id() +
					"')";*/
				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  ,oid, SheetID  ,skuid, itemmealname , "
					+ " title , sellernick , created , "
					+ "  outerskuid , totalfee , payment ,num , price  ,outeriid ,DistributePrice ,buyernick) values( "
					+ "'"+ sheetid+ "','"+ sheetid+ item.getSku_id()+ "','"+item.getItem_id()+"','"+ sheetid+ "','"+item.getSku_id()+"','"+ item.getSku_id()
					+ "', '"+ item.getSku_title()+ "' , '"+ o.getSeller_id()+ "', '"+Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)
					+ "', '"+ item.getSku_merchant_code()+ "' , '"+ String.valueOf(Double.parseDouble(item.getPrice()) * Integer.parseInt(item.getQuantity()))
					+ "' , '"+String.valueOf(Double.parseDouble(item.getPrice()) * Integer.parseInt(item.getQuantity()))+"',"				
					+ item.getQuantity()+ " , '"+ String.valueOf(Double.parseDouble(item.getPrice()) * Integer.parseInt(item.getQuantity())) + "','" + item.getSku_merchant_code() + "'," + 0 
					+  ",'"+ o.getSeller_id() +
					"')";
				System.out.println("OrderItemSQL: "+sql);     //////testsql
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
			throw new JException("生成订单【" + o.getOrder_id()+ "】接口数据失败,错误信息："+ e1.getMessage());
		}
	}
	
	
	public static Order getOrderByID(String orderCode,String token) throws Exception
	{
		Order o=new Order();
		JSONObject param_Object = new JSONObject();
		JSONObject public_Object = new JSONObject();
		
		param_Object.put("order_id", orderCode);
		public_Object.put("method", "vdian.order.get");
		public_Object.put("access_token", token); //写个方法用于获取access_token
		public_Object.put("version", "1.0"); 
		public_Object.put("format", "json"); 
		
		String opt_to_sever = Params.url + "?param=" + URLEncoder.encode(param_Object.toString(),"UTF-8") + "&public=" + URLEncoder.encode(public_Object.toString(),"UTF-8");
		//System.out.println(Params.url + "?param=" + param_Object.toString() + "&public=" + public_Object.toString());
		String responseOrderListData = Utils.sendbyget(opt_to_sever);
		JSONObject responseorder = new JSONObject(responseOrderListData);
		JSONObject orderdetail=responseorder.getJSONObject("result")/*.getJSONObject("buyer_info")*/;
		System.out.println("返回的详细数据"+responseOrderListData);
		JSONObject buyer_info = responseorder.getJSONObject("result").getJSONObject("buyer_info");
		orderdetail.put("name", buyer_info.optString("name"));
		orderdetail.put("address", buyer_info.optString("address"));
		orderdetail.put("post", buyer_info.optString("post"));
		orderdetail.put("phone", buyer_info.optString("phone"));
		orderdetail.put("province", buyer_info.optString("province"));
		orderdetail.put("city", buyer_info.optString("city"));
		orderdetail.put("region", buyer_info.optString("region"));
		orderdetail.put("self_address", buyer_info.optString("self_address"));
		
		o.setObjValue(o, orderdetail);
		JSONArray OrderDets=responseorder.getJSONObject("result").getJSONArray("items");   //一定要用API返回的json对象的明细信息
		o.setFieldValue(o, "orderItemList"/*"items"*/, OrderDets);   //第三个参数一定要是jsonarray格式，第二个参数要和Order类里面的orderItemList定义的一样
		return o;
	}
	
}
