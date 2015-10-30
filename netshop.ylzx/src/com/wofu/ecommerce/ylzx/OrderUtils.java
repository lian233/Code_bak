package com.wofu.ecommerce.ylzx;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ylzx.utils.AuthTokenManager;
import com.wofu.ecommerce.ylzx.utils.Content;
import com.wofu.ecommerce.ylzx.utils.Utils;
public class OrderUtils {
	/*
	 * 转入一个订单到接口表
	 */
	
	public static String createInterOrder(Connection conn,
			Order o, String tradecontactid,String username) throws Exception {
		try {
			String sheetid = "";
			int invoiceflag=0;
			String invoicetitle="";
			
			if (!"0".equals(o.getInvoice_kind()))
			{
				invoiceflag=1;
				invoicetitle=o.getInvoice_title();
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
			float totalFee = o.getOrder_amount()+o.getDiscount();
			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  , sellernick , paymode,invoiceflag,invoicetitle,"
					+ "  created ,  payment , discountfee, status  , buyermemo , sellermemo  , paytime ,  modified , "
					+ " totalfee , postfee, buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone , tradefrom,tradeContactid) "
					
					+ " values('"+ sheetid+ "','"+ sheetid+ "','"+username+"','"+ o.getOrder_sn()
					+ "','"+ username+ "',1,"+invoiceflag+",'"+invoicetitle+"','"+Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)+"',"+ o.getSeller_income()+ ", '"
					+ o.getDiscount()+"','"+o.getStatus()+ "' , '"+o.getPostscript() + "' , '"+ o.getDispose_remark()+ "','"+Formatter.format(o.getPay_time(), Formatter.DATE_TIME_FORMAT)+"',"
					+"'"+Formatter.format(o.getPay_time(), Formatter.DATE_TIME_FORMAT)+ "' , "+ totalFee+ " , '"+o.getShipping_fee()+ "'"
					+ ",'"	+ o.getBuyer_name()+ "' ,'"+ o.getConsignee()+ "' , '"
					+ o.getRegion_name().substring(0,o.getRegion_name().indexOf("	"))+ "', '"	+ o.getRegion_name().substring(o.getRegion_name().indexOf("	")+1)+ "' , '"+o.getAddress().substring(0,3)+"', "
					+ "'"+ o.getAddress()+ "','"+ o.getZipcode()+ "' , '"
					+ o.getPhone_mob()+ "' , '"+ o.getPhone_tel()+ "','YLZX'," + tradecontactid + ")";//
			SQLHelper.executeSQL(conn, sql);
			
			for (int i=0;i<o.getOrderItems().size();i++) {
				OrderItem item = (OrderItem) o.getOrderItems().get(i);
				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  ,oid, SheetID  ,skuid, itemmealname , "
						+ " title , sellernick , created , "
						+ "  outerskuid , totalfee , payment ,num , price ) values( "
						+ "'"+ sheetid+ "','"+ sheetid+ item.getGoods_id()+ "','"+item.getGoods_id()+"','"+ sheetid+ "','0','"+ item.getGoods_name()
						+ "', '"+ item.getGoods_name()+ "' , '"+ username+ "', '"+Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)
						+ "', '"+ item.getSku()+ "' , '"+ item.getGoods_price()*item.getQuantity()
						+ "' , '"+item.getGoods_price()*item.getQuantity()+"',"				
						+ item.getQuantity()+ " , '"+ item.getGoods_price()+"')";
				SQLHelper.executeSQL(conn, sql);		
			}
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("生成订单【" + o.getOrder_sn() + "】接口数据成功，接口单号【"+ sheetid + "】");
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
			throw new JException("生成订单【" + o.getOrder_sn() + "】接口数据失败,错误信息："+ e1.getMessage());
		}
	}
	//构造一个order对象
	public static Order getOrder(Element e) {
		Order o = new Order();
		o.setAdd_time(new Date(Long.parseLong(DOMHelper.getSubElementVauleByName(e, "add_time"))*1000L));
		o.setAddress(DOMHelper.getSubElementVauleByName(e, "address"));
		o.setBuyer_id(DOMHelper.getSubElementVauleByName(e, "buyer_id"));
		o.setBuyer_name(DOMHelper.getSubElementVauleByName(e, "buyer_name"));
		o.setConsignee(DOMHelper.getSubElementVauleByName(e, "consignee"));
		o.setDiscount((Float.parseFloat(DOMHelper.getSubElementVauleByName(e, "discount"))));
		o.setDispose_remark(DOMHelper.getSubElementVauleByName(e, "dispose_remark"));
		o.setEvaluation_status((Integer.parseInt(DOMHelper.getSubElementVauleByName(e, "evaluation_status"))));//
		o.setEvaluation_time(new Date(Long.parseLong(DOMHelper.getSubElementVauleByName(e, "evaluation_time"))*1000L));//
		o.setFinished_time(new Date(Long.parseLong(DOMHelper.getSubElementVauleByName(e, "finished_time"))*1000L));//
		o.setGoods_amount((Float.parseFloat(DOMHelper.getSubElementVauleByName(e, "goods_amount"))));//
		o.setInvoice_content(DOMHelper.getSubElementVauleByName(e, "invoice_content"));//
		o.setInvoice_kind(DOMHelper.getSubElementVauleByName(e, "invoice_kind"));//
		o.setInvoice_title(DOMHelper.getSubElementVauleByName(e, "invoice_title"));//
		o.setMall_income((Float.parseFloat(DOMHelper.getSubElementVauleByName(e, "mall_income"))));//
		o.setMall_rate((Float.parseFloat(DOMHelper.getSubElementVauleByName(e, "mall_rate"))));//
		o.setOrder_amount((Float.parseFloat(DOMHelper.getSubElementVauleByName(e, "order_amount"))));//
		o.setOrder_id(DOMHelper.getSubElementVauleByName(e, "order_id"));//
		o.setOrder_sn(DOMHelper.getSubElementVauleByName(e, "order_sn"));//
		o.setPay_message(DOMHelper.getSubElementVauleByName(e, "pay_message"));//
		o.setPay_time(new Date(Long.parseLong(DOMHelper.getSubElementVauleByName(e, "pay_time"))*1000L));//
		o.setPayment_code(DOMHelper.getSubElementVauleByName(e, "payment_code"));//
		o.setPayment_name(DOMHelper.getSubElementVauleByName(e, "payment_name"));//
		o.setPhone_mob(DOMHelper.getSubElementVauleByName(e, "phone_mob"));//
		o.setPhone_tel(DOMHelper.getSubElementVauleByName(e, "phone_tel"));//
		o.setPostscript(DOMHelper.getSubElementVauleByName(e, "postscript"));//
		o.setRecommend_income((Float.parseFloat(DOMHelper.getSubElementVauleByName(e, "recommend_income"))));//
		o.setRecommend_rate((Float.parseFloat(DOMHelper.getSubElementVauleByName(e, "recommend_rate"))));//
		o.setRegion_name(DOMHelper.getSubElementVauleByName(e, "region_name"));//
		o.setSeller_income((Float.parseFloat(DOMHelper.getSubElementVauleByName(e, "seller_income"))));//
		o.setSeller_name(DOMHelper.getSubElementVauleByName(e, "seller_name"));//
		o.setShipping_fee((Float.parseFloat(DOMHelper.getSubElementVauleByName(e, "shipping_fee"))));//
		o.setShipping_name(DOMHelper.getSubElementVauleByName(e, "shipping_name"));//
		o.setStatus((DOMHelper.getSubElementVauleByName(e, "status")));//
		o.setType((DOMHelper.getSubElementVauleByName(e, "type")));//
		o.setZipcode((DOMHelper.getSubElementVauleByName(e, "zipcode")));//
		return o;
	}

	//获取订单的商品明细
	public static void setOrderItem(Order o, Element e,AuthTokenManager authTokenManager) throws Exception {
		Map<String, String> orderItemlistparams = new HashMap<String, String>();
        //系统级参数设置
		orderItemlistparams.put("oauth_consumer_key", authTokenManager.getOauth_consumer_key());
		orderItemlistparams.put("oauth_signature_method", Content.HMAC_SHA1);
		orderItemlistparams.put("oauth_timestamp", String.valueOf(new Date().getTime()/1000L));
		orderItemlistparams.put("oauth_nonce", String.valueOf(System.currentTimeMillis()));
		orderItemlistparams.put("oauth_version", Params.ver);
		orderItemlistparams.put("order_sn", o.getOrder_sn());
		orderItemlistparams.put("oauth_token", authTokenManager.getToken());
		String responseOrderListData = Utils.sendByPost(Content.getOrderItem_url,
				orderItemlistparams,"POST",authTokenManager.getOauth_consumer_secert(),authTokenManager.getOauth_token_secret());
		Log.info(responseOrderListData);
		Document doc = DOMHelper.newDocument(responseOrderListData);
		Element elementOrderItem = doc.getDocumentElement();
		if(!"200".equals(DOMHelper.getSubElementVauleByName(elementOrderItem,"status").trim())) throw new Exception("取订单明细出错!");
		Element body = DOMHelper.getSubElementsByName(elementOrderItem, "body")[0];
		Element item = DOMHelper.getSubElementsByName(body, "order_goods")[0];
		Element[] items = DOMHelper.getSubElementsByName(item, "goods");
		for(Element ele:items){
			OrderItem orderitem = new OrderItem();
			orderitem.setGoods_id(DOMHelper.getSubElementVauleByName(ele, "goods_id"));
			orderitem.setGoods_name(DOMHelper.getSubElementVauleByName(ele, "goods_name"));
			orderitem.setSku(DOMHelper.getSubElementVauleByName(ele, "sku"));
			orderitem.setGoods_price(Float.parseFloat(DOMHelper.getSubElementVauleByName(ele, "goods_price")));
			orderitem.setQuantity(Integer.parseInt(DOMHelper.getSubElementVauleByName(ele, "quantity")));
			o.getOrderItems().add(orderitem);
		}
	}
	
}
