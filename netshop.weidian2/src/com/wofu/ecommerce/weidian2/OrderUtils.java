package com.wofu.ecommerce.weidian2;

import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Date;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weidian2.utils.Utils;



public class OrderUtils 
{
	/*
	 * 转入一个订单到接口表
	 */
	public static String createInterOrder(Connection conn,
			Order o, String tradecontactid,String username) throws Exception {
			try{
				

				
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
			
			String deliveryremark="";/*o.getDeliveryRemark()==null || o.getDeliveryRemark().equals("null")?"":o.getDeliveryRemark().replaceAll("'", "")*/;
			String merchantremark="";/*o.getMerchantRemark()==null || o.getMerchantRemark().equals("null")?"":o.getMerchantRemark().replaceAll("'", "")*/;
			String moblie=o.getMobile()!=null?o.getMobile():"";
			String phone=o.getPhone()!=null?o.getPhone():"";
			String sTime = !o.getSend_time().equals("0001/1/1 0:00:00")?o.getSend_time():"";
			String pTime = !o.getPay_time().equals("0001/1/1 0:00:00")?o.getPay_time():"";

			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  , sellernick , paymode,invoiceflag,invoicetitle,"
					+ "  created ,  payment ,  status  , buyermemo , sellermemo  ,consigntime , paytime ,  modified , "
					+ " totalfee , postfee, buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone , tradefrom,tradeContactid,payfee," +
							"distributorshopname,distributeTid) "
					
					+ " values('"+ sheetid+ "','"+ sheetid+ "','"+username+"','"+ o.getOrder_id()+"',' "
					+ "', "+paymode+","+invoiceflag+",'"+invoicetitle+"','"+Formatter.format(o.getCtime(),Formatter.DATE_TIME_FORMAT)+"',"+ o.getTotal_price()+ ", '"
					+ o.getStatus()+ "' , '"+deliveryremark + "' , '"+ merchantremark+ "','"+Formatter.format(sTime, Formatter.DATE_TIME_FORMAT)+"','"+Formatter.format(pTime, Formatter.DATE_TIME_FORMAT)+"',"
					+"'"+Formatter.format(o.getMtime(), Formatter.DATE_TIME_FORMAT)+ "' , "+ String.valueOf(Double.parseDouble(o.getTotal_price().trim()))+ " , '"+0/*运费*/+ "'"
					+ ",'"	+ o.getOrder_id()+ "' ,'"+ o.getName()+"','"
					+ o.getProvince()+ "', '"	+ o.getCity()+ "' , '"+o.getDistrict()+"', "
					+ "'"+ o.getAddress()+ "','"+ o.getPostcode()+ "' , '"
					+ moblie+ "' , '"+ phone+ "','weidian'," + tradecontactid + ",'"+""/*货到付款金额*/+
					"','"+  o.getOrder_id() +"','"+ o.getOrder_id()   + "'" 
					+")";
			//System.out.println(sql);     //////testsql
			//System.out.println("getSalePrice:"+o.getSalePrice());
			SQLHelper.executeSQL(conn, sql);
			//System.out.println("测试"+o.getOrderItemList().getRelationData().size());
			for (int i=0;i<o.getOrderItemList().getRelationData().size();i++) {
				
				OrderItem item = (OrderItem) o.getOrderItemList().getRelationData().get(i);

				
				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  ,sheetid,  iid,skuid ,   "
					+ " title , sellernick ,buyernick, created , "
					+ "  outerskuid ,outeriid, totalfee , payment ,num , price   ,DistributePrice ) values( "
					+ "'"+ sheetid+ "','"+ o.getOrder_id()+ "','"+sheetid+"','"+item.getMid()+"','"+item.getSku()
					+ "', '"+ item.getTitle()+ "' , '"+ username+ "','"+o.getName()+"', '"+Formatter.format(o.getCtime(),Formatter.DATE_TIME_FORMAT)
					+ "', '"+ item.getSku()+ "' ,'"+item.getMid()+"', '"+ String.valueOf(Double.parseDouble(item.getPrice()) * item.getNum())
					+ "' , '"+String.valueOf(Double.parseDouble(item.getPrice()) * item.getNum())+"',"				
					+ item.getNum()+ " , '"+ item.getPrice() + "'," + 0 
					+  ")";
				
//				System.out.println(sql);     //////testsql
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
	
	public static Order getOrderByID(String orderCode) throws Exception
	{
		Order o=new Order();
		StringBuffer  buffer = new StringBuffer(); 
		buffer.append("service=order&");
		buffer.append("vcode="+Params.vcode+"&");
		buffer.append("page=0&");
		buffer.append("page_size=100&");
		buffer.append("order_id="+orderCode+"&");
		//System.out.println("orderCode:"+orderCode);
		buffer.append("status=0&");
		buffer.append("mtime_start=1970-01-01+00%3A00%3A00"+"&"); //时间要修改为startdate和enddate,UTF8处理
		buffer.append("mtime_end="+URLEncoder.encode(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT), "UTF-8"));
		String result = Utils.sendByPost("http://www.xiulife.net/api/Order/PostOrder", buffer.toString());
		char[] rsp_cleaned = result.replace("\\", "").toCharArray();
		JSONObject responseorder = new JSONObject(String.valueOf(rsp_cleaned, 1, rsp_cleaned.length-2));
		JSONObject orderdetail=responseorder.getJSONArray("Order_lists").getJSONObject(0);//有时候一个jsonarray里面不止一个jsonobject，所以这里有可能漏掉一些
		o.setObjValue(o, orderdetail);//设置外面的
		JSONArray OrderDets=responseorder.getJSONArray("Order_lists").getJSONObject(0).getJSONArray("detail");   //一定要用API返回的json对象的明细信息
		o.setFieldValue(o, "orderItemList", OrderDets);  //设置明细，orderItemList与order对应
		return o;
		
	}
	
}
