package com.wofu.ecommerce.ming_xie_ku;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ming_xie_ku.utils.Utils;




public class OrderUtils 
{
	/*
	 * 转入一个订单到接口表
	 */
	public static String createInterOrder(Connection conn,
			Order o, String tradecontactid,String username) throws Exception {
		try {

			String sheetid = "";
			
			int paymode=1;//支付模式
			String delivery="";
			String deliverySheetID="";
			int invoiceflag=0;		
			String invoicetitle="";
			
			if(o.getIsCod().equals("true")){
				paymode=2;//货到付款
				delivery=conversionName(o.getSuggestExpress());//快递名字
				deliverySheetID=o.getSuggestExpressNo();//快递单号
			}
			


			conn.setAutoCommit(false);

			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "取接口单号出错!");

			// 加入到通知表
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid+ "',1 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			
			String deliveryremark="";/*o.getDeliveryRemark()==null || o.getDeliveryRemark().equals("null")?"":o.getDeliveryRemark().replaceAll("'", "")*/;
			String merchantremark="";/*o.getMerchantRemark()==null || o.getMerchantRemark().equals("null")?"":o.getMerchantRemark().replaceAll("'", "")*/;
			System.out.println("是否货到付款"+o.getIsCod()+"快递公司"+o.getSuggestExpress()+"快递号"+o.getSuggestExpressNo());
			//2015年9月11日新增paymode,deliverySheetID,delivery
			String moblie=o.getRcvTel()!=null?o.getRcvTel():"";
			String phone=o.getRcvTel()!=null?o.getRcvTel():"";
			sql = "insert into ns_customerorder"
					+ "(delivery,deliverySheetID,CustomerOrderId , SheetID , Owner , tid  , sellernick , paymode,invoiceflag,invoicetitle,"
					+ "  created ,  payment ,  status  , buyermemo , sellermemo  , paytime ,  modified , "
					+ " totalfee , postfee, buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone , tradefrom,tradeContactid,payfee) "
					
					+ " values('"+ delivery+ "','"+ deliverySheetID+ "','"+ sheetid+ "','"+ sheetid+ "','"+username+"','"+ o.getVendorOrderNo()
					+ "','"+ o.getSellerId()+ "', "+paymode+","+invoiceflag+",'"+invoicetitle+"','"+Formatter.format(o.getUpdateDate(),Formatter.DATE_TIME_FORMAT)+"',"+ o.getGoodsPrice()+ ", '"
					+ o.getOrderStatus()+ "' , '"+deliveryremark + "' , '"+ merchantremark+ "','"+Formatter.format(o.getSubmitDate(), Formatter.DATE_TIME_FORMAT)+"',"
					+"'"+Formatter.format(o.getUpdateDate(), Formatter.DATE_TIME_FORMAT)+ "' , "+ o.getSalePrice()+ " , '"+0/*运费*/+ "'"
					+ ",'"	+ o.getSellerId()+ "' ,'"+ o.getRcvName()+ "' , '"
					+ o.getRcvAddrDetail()+ "', '"	+ ""+ "' , '"+""+"', "
					+ "'"+ o.getRcvAddrDetail()+ "','"+ o.getRcvAddrId()+ "' , '"
					+ moblie+ "' , '"+ phone+ "','mxk'," + tradecontactid + ",'"+""/*货到付款金额*/+"')";
			//System.out.println(sql);     //////testsql
			SQLHelper.executeSQL(conn, sql);
			


			for (int i=0;i<o.getOrderItemList().getRelationData().size();i++) {
				
				OrderItem item = (OrderItem) o.getOrderItemList().getRelationData().get(i);

				Log.info("sku: "+item.getVendorSkuId());
				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  ,oid, SheetID  ,skuid, itemmealname , "
					+ " title , sellernick , created , "
					+ "  outerskuid , totalfee , payment ,num , price ) values( "
					+ "'"+ sheetid+ "','"+ sheetid+ item.getVendorOrderDetNo()+ "','"+item.getVendorOrderDetNo()+"','"+ sheetid+ "','0','','','"
					+ username+ "', '"+Formatter.format(o.getSubmitDate(),Formatter.DATE_TIME_FORMAT)
					+ "', '"+ item.getVendorSkuId()+ "' , '"+ item.getUnitPrice()*item.getQty()
					+ "' , '"+item.getUnitPrice()*item.getQty()+"',"				
					+ item.getQty()+ " , '"+ item.getUnitPrice()+"')";
				//System.out.println(sql);     //////testsql
				SQLHelper.executeSQL(conn, sql);		
			}

			conn.commit();
			conn.setAutoCommit(true);
			
			Log.info("生成订单【" + o.getVendorOrderNo() + "】接口数据成功，接口单号【"+ sheetid + "】");

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
			throw new JException("生成订单【" + o.getVendorOrderNo()+ "】接口数据失败,错误信息："+ e1.getMessage());
		}
	}
	

	
private static String conversionName(String suggestExpress) {
	    String express = "";
		if(suggestExpress.equals("京东快递")){
			express="JDKD";
		}
		else{
			Log.info("你怎么不跟我商量就添加了一个新的货到快递?"+suggestExpress);
		}
		return express;
	}



//	public static Order getOrderByID(String params) throws Exception
//	{
//		Order o=new Order();  
//		String responseOrderData = Utils.sendByPost(Params.url,params);
//		JSONObject responseorder=new JSONObject(responseOrderData);
//		if(!responseorder.get("ErrCode").equals(null) || !responseorder.get("ErrMsg").equals(null))
//		{
//			String errdesc="";
//			errdesc=errdesc+" "+responseorder.get("ErrCode").toString()+" "+responseorder.get("ErrMsg").toString(); 
//			throw new JException(errdesc);	
//		}
////		JSONArray orderlist=responseorder.getJSONArray("Result");
////		JSONObject orderdetail=orderlist.getJSONObject(0);
//		JSONObject orderdetail=responseorder.getJSONArray("Result").getJSONObject(0);
//		o.setObjValue(o, orderdetail);
//		JSONArray OrderDets=responseorder.getJSONArray("Result")/*.getJSONObject(0).getJSONArray("OrderDets")*/;
//		o.setFieldValue(o, "Result", OrderDets);
//		return o;
//	}
	
	public static Order getOrderByID(String app_secret,String orderCode,String app_key,String ver,String format) throws Exception
	{
		Order o=new Order();
		Date now=new Date();
		/***data部分***/
		JSONObject data=new JSONObject();
		//需要返回的字段：
		data.put("Fields","seller_id,suggest_express_no, is_cod, vendor_id, seller_order_no, vendor_order_no,submit_date,seller_memo,vendor_memo,shipping_fee,goods_price,rcv_name,rcv_addr_id,rcv_addr_detail,rcv_tel,order_status,update_date,suggest_express,detail.seller_order_det_no,detail.vendor_order_det_no,detail.seller_sku_id,detail.vendor_sku_id,detail.unit_price,detail.sale_price,detail.qty,express.express_no,express.express_company_id,express.sku_qty_pair");	
		data.put("VendorOrderNo", orderCode);   //供货商订单号
//		data.put("OrderStatus", 1);     //订单状态(1-未处理 2-已确认 3-已发货 4-已作废)
		/**sign部分***/
		String sign=Utils.get_sign(app_secret,app_key,data, "scn.vendor.order.full.get", now,ver,format);
		/***合并为输出语句****/
		String output_to_server=Utils.post_data_process("scn.vendor.order.full.get", data, app_key,now, sign).toString();	     
		String responseOrderData = Utils.sendByPost(Params.url,output_to_server);
//		Log.info("明细: "+responseOrderData);
		JSONObject responseorder=new JSONObject(responseOrderData);
		if(!responseorder.get("ErrCode").equals(null) || !responseorder.get("ErrMsg").equals(null))
		{
			String errdesc="";
			errdesc=errdesc+" "+responseorder.get("ErrCode").toString()+" "+responseorder.get("ErrMsg").toString(); 
			throw new JException(errdesc);	
		}
//		JSONArray orderlist=responseorder.getJSONArray("Result");
//		JSONObject orderdetail=orderlist.getJSONObject(0);
		JSONObject orderdetail=responseorder.getJSONArray("Result").getJSONObject(0);
		o.setObjValue(o, orderdetail);
		JSONArray OrderDets=responseorder.getJSONArray("Result").getJSONObject(0).getJSONArray("OrderDets");   //一定要用API返回的json对象的明细信息
		//o.setFieldValue(o, "Result", OrderDets);
		o.setFieldValue(o, "orderItemList", OrderDets);   //第三个参数一定要是jsonarray格式，第二个参数要和Order类里面的orderItemList定义的一样
		return o;
	}
	
}
