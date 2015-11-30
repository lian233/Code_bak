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
	 * ת��һ���������ӿڱ�
	 */
	public static String createInterOrder(Connection conn,
			Order o, String tradecontactid,String username) throws Exception {
		try {

			String sheetid = "";
			
			int paymode=1;//֧��ģʽ
			String delivery="";
			String deliverySheetID="";
			int invoiceflag=0;		
			String invoicetitle="";
			String codAmount = o.getCodAmount();
			if(o.getIsCod().equals("true")){
				paymode=2;//��������
				delivery=conversionName(o.getSuggestExpress());//�������
				deliverySheetID=o.getSuggestExpressNo();//��ݵ���
			}
			if(codAmount==null&&!o.getIsCod().equals("true")){
				codAmount="0";
			}
			


			conn.setAutoCommit(false);

			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "ȡ�ӿڵ��ų���!");

			// ���뵽֪ͨ��
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid+ "',1 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			
			String deliveryremark="";/*o.getDeliveryRemark()==null || o.getDeliveryRemark().equals("null")?"":o.getDeliveryRemark().replaceAll("'", "")*/;
			String merchantremark="";/*o.getMerchantRemark()==null || o.getMerchantRemark().equals("null")?"":o.getMerchantRemark().replaceAll("'", "")*/;
			System.out.println("�Ƿ��������"+o.getIsCod()+"��ݹ�˾"+o.getSuggestExpress()+"��ݺ�"+o.getSuggestExpressNo());
			//2015��9��11������paymode,deliverySheetID,delivery
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
					+"'"+Formatter.format(o.getUpdateDate(), Formatter.DATE_TIME_FORMAT)+ "' , "+ o.getSalePrice()+ " , '"+0/*�˷�*/+ "'"
					+ ",'"	+ o.getSellerId()+ "' ,'"+ o.getRcvName()+ "' , '"
					+ o.getRcvAddrDetail()+ "', '"	+ ""+ "' , '"+""+"', "
					+ "'"+ o.getRcvAddrDetail()+ "','"+ o.getRcvAddrId()+ "' , '"
					+ moblie+ "' , '"+ phone+ "','mxk'," + tradecontactid + ",'"+codAmount/*����������*/+"')";
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
			
			Log.info("���ɶ�����" + o.getVendorOrderNo() + "���ӿ����ݳɹ����ӿڵ��š�"+ sheetid + "��");

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
			throw new JException("���ɶ�����" + o.getVendorOrderNo()+ "���ӿ�����ʧ��,������Ϣ��"+ e1.getMessage());
		}
	}
	

	
private static String conversionName(String suggestExpress) {
	    String express = "";
		if(suggestExpress.equals("�������")){
			express="JDKD";
		}
		else{
			Log.info("����ô�����������������һ���µĻ������?"+suggestExpress);
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
		/***data����***/
		JSONObject data=new JSONObject();
		//��Ҫ���ص��ֶΣ�
		data.put("Fields","cod_amount,seller_id,suggest_express_no, is_cod, vendor_id, seller_order_no, vendor_order_no,submit_date,seller_memo,vendor_memo,shipping_fee,goods_price,rcv_name,rcv_addr_id,rcv_addr_detail,rcv_tel,order_status,update_date,suggest_express,detail.seller_order_det_no,detail.vendor_order_det_no,detail.seller_sku_id,detail.vendor_sku_id,detail.unit_price,detail.sale_price,detail.qty,express.express_no,express.express_company_id,express.sku_qty_pair");	
		data.put("VendorOrderNo", orderCode);   //�����̶�����
//		data.put("OrderStatus", 1);     //����״̬(1-δ���� 2-��ȷ�� 3-�ѷ��� 4-������)
		/**sign����***/
		String sign=Utils.get_sign(app_secret,app_key,data, "scn.vendor.order.full.get", now,ver,format);
		/***�ϲ�Ϊ������****/
		String output_to_server=Utils.post_data_process("scn.vendor.order.full.get", data, app_key,now, sign).toString();	     
		String responseOrderData = Utils.sendByPost(Params.url,output_to_server);
//		Log.info("��ϸ: "+responseOrderData);
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
		JSONArray OrderDets=responseorder.getJSONArray("Result").getJSONObject(0).getJSONArray("OrderDets");   //һ��Ҫ��API���ص�json�������ϸ��Ϣ
		//o.setFieldValue(o, "Result", OrderDets);
		o.setFieldValue(o, "orderItemList", OrderDets);   //����������һ��Ҫ��jsonarray��ʽ���ڶ�������Ҫ��Order�������orderItemList�����һ��
		return o;
	}
	
}
