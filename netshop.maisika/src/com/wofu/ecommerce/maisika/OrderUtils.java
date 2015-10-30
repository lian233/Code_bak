package com.wofu.ecommerce.maisika;

import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.maisika.util.CommHelper;
import com.wofu.ecommerce.maisika.Order;
import com.wofu.ecommerce.maisika.OrderItem;
import com.wofu.ecommerce.maisika.Params;



public class OrderUtils 
{	

	/*
	 * ת��һ���������ӿڱ�
	 */
	public static String createInterOrder(Connection conn,
			Order o, String tradecontactid,String username,String address,String area,String phone,String mob_phone,String buyname,String order_message) throws Exception {
			try{

			String sheetid = "";
			
			int invoiceflag=0;		
			String invoicetitle="";
			
			
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

			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  ,OrderSheetID," 
					+ " sellernick , paymode,invoiceflag,invoicetitle, created ,"
					+ "  payment ,  status  , buyermemo , sellermemo  ,consigntime , paytime ,  modified , "
					+ " totalfee , postfee, buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress  , receivermobile , receiverphone , tradefrom,tradeContactid,payfee," 
					+ "distributorshopname,distributeTid,buyermessage) "
					+ " values('"+ sheetid+ "','"+ sheetid+ "','"+username+"','"+ o.getOrder_sn()+"','"+ o.getOrder_id()+"'," 
					+ "'��˹��', "+o.getPayment_code()+","+invoiceflag+",'"+invoicetitle+"','"+Formatter.format(new Date(o.getAdd_time()*1000L),Formatter.DATE_TIME_FORMAT)+"',"+ o.getOrder_amount()+ ", '"
					+ o.getOrder_state()+ "' , '"+deliveryremark + "' , '"+ merchantremark+ "','"+Formatter.format((new Date(o.getShipping_time()*1000L)), Formatter.DATE_TIME_FORMAT)+"','"+Formatter.format(new Date(o.getPayment_time()*1000L), Formatter.DATE_TIME_FORMAT)+"',"
					+ "'"+Formatter.format(new Date(o.getPayment_time()*1000L), /*getAdd_time��Ϊ*/Formatter.DATE_TIME_FORMAT)+ "' , "+ o.getOrder_amount()+ " , '"+0/*�˷�*/+ "'"
					+ ",'"	+ o.getBuyer_name()+ "' ,'"+ buyname+"','"
					+ area+ "', '"	+ area+ "' , '"+area+"', "
					+ "'"+ address+ "', '"
					+ mob_phone+ "' , '"+ phone+ "','maisika'," + tradecontactid + ",'"+""/*����������*/+
					"','"+  o.getOrder_id() +"','"+ o.getOrder_id()   + "','"+  order_message  + "'" 
					+")";
			//System.out.println(sql);     //////testsql
			//System.out.println("getSalePrice:"+o.getSalePrice());
			SQLHelper.executeSQL(conn, sql);
//			for (Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();) {
//				OrderItem item=(OrderItem) ito.next();
//			System.out.println("������Ʒ������"+o.getOrderItemList().getRelationData().size());
			for (int i=0;i<o.getOrderItemList().getRelationData().size();i++) {
				OrderItem item = (OrderItem) o.getOrderItemList().getRelationData().get(i);
				System.out.println("��Ʒ�������"+o.getOrder_sn());
				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  ,sheetid,  iid,skuid ,   "
					+ " title , sellernick ,buyernick, created , "
					+ "  outerskuid ,outeriid, totalfee , payment ,num , price   ,DistributePrice ) values( "
					+ "'"+ sheetid+ "','"+ o.getOrder_sn()+"/"+i+ "','"+sheetid+"','"+item.getMid()+"','"+item.getSku()
					+ "', '"+ item.getTitle()+ "' , '"+ username+ "','"+o.getReciver_name()+"', '"+Formatter.format(new Date(o.getAdd_time()*1000L),Formatter.DATE_TIME_FORMAT)
					+ "', '"+ item.getSku()+ "' ,'"+item.getSku()+"', '"+ String.valueOf(item.getPrice() * item.getNum())/*��ط�û����*/
					+ "' , '"+String.valueOf(item.getPrice() * item.getNum())+"',"				
					+ item.getNum()+ " , '"+ item.getPrice() + "'," + 0 
					+  ")";
				
				//System.out.println(sql);     //////testsql
				SQLHelper.executeSQL(conn, sql);		
			}
			conn.commit();
			conn.setAutoCommit(true);
			
			Log.info("���ɶ�����" + o.getOrder_sn() + "���ӿ����ݳɹ����ӿڵ��š�"+ sheetid + "��");
			return sheetid;

		} catch (Exception e1) {
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
			throw new JException("���ɶ�����" + o.getOrder_sn()+ "���ӿ�����ʧ��,������Ϣ��"+ e1.getMessage());
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
		LinkedHashMap<String,Object> map = new LinkedHashMap<String,Object>();
		map.put("&op","orders");
        map.put("service","order");
        map.put("vcode", Params.vcode);
        map.put("page_size", Params.pageSize);
        map.put("order_id",orderCode);
        //��������
		String responseText = CommHelper.doGet(map,Params.url);
//        String responseText = json;
		JSONObject responseorder = new JSONObject(responseText);
		JSONObject orderdetail=responseorder.getJSONArray("order_list").getJSONObject(0);//��ʱ��һ��jsonarray���治ֹһ��jsonobject�����������п���©��һЩ
		o.setObjValue(o, orderdetail);//���������
		JSONArray OrderDets=responseorder.getJSONArray("order_list").getJSONObject(0).getJSONArray("order_goods");   //һ��Ҫ��API���ص�json�������ϸ��Ϣ
		o.setFieldValue(o, "orderItemList", OrderDets);  //������ϸ��orderItemList��order��Ӧ
		return o;
		
	}



	public static void createRefund(Connection conn,ReturnOrder r,ReturnOrderItem item,
			int tradecontactid,String express_company) throws Exception 
	{
		String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="+ tradecontactid;
		String inshopid = SQLHelper.strSelect(conn, sql);
		conn.setAutoCommit(false);
			
			sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			String sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "ȡ�ӿڵ��ų���!");

			// ���뵽֪ͨ��
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
					+"values('yongjun','"+ sheetid+ "',2 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			sql = "insert into ns_Refund(SheetID ,RefundID  ,Oid, "
					+ "BuyerNick , Created  ,  Status , GoodStatus , "
					+ " HasGoodReturn ,RefundFee , Payment ,     Reason,Description , Title ,"
					+ "Price , Num , GoodReturnTime  , "
					+ " TotalFee ,  OuterIid ,  CompanyName ,sid, "
					+ " InShopID , Tid)"
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			


			String Status = state(r.getRefund_state(),r.getReturn_type(),r.getGoods_state(),r.getSeller_state());
			String GoodStatus = goodStatus(r.getGoods_state());
			String HasGoodReturn = hasGoodReturn(r.getReturn_type());
			Object[] sqlv = {
					sheetid,
					r.getRefund_sn(),
					item.getRec_id(),

					
					r.getBuyer_name(),
					Formatter.format(new Date(r.getAdd_time()*1000L),Formatter.DATE_TIME_FORMAT),
//					r.getMtime(),//�˻��޸�ʱ��
					Status,
					GoodStatus,
					
					HasGoodReturn,
					item.getGoods_num()*item.getGoods_price(),
					0,
					r.getBuyer_message(),
					r.getReason_info(),
					item.getGoods_name(),
					
					item.getRefund_amount(),
					item.getGoods_num(),
					Formatter.format(new Date(r.getShip_time()*1000L),Formatter.DATE_TIME_FORMAT),
					
					item.getGoods_num()*item.getGoods_price(),
					item.getGoods_serial(),
					express_company,
					r.getExpress_id(),
					
					tradecontactid,
					r.getOrder_sn(),
		
			};
			SQLHelper.executePreparedSQL(conn, sql, sqlv);


			Log.info( "�ӿڵ���:"+ sheetid	+ " ������:"	+ r.getOrder_id()+ " ״̬��"+ r.getRefund_state()+ "�˻�����ʱ��:"
					+ Formatter.format(new Date(r.getAdd_time()*1000L),Formatter.DATE_TIME_FORMAT));
		
		
		conn.commit();
		conn.setAutoCommit(true);

	}


	private static String hasGoodReturn(String hasGoodReturn) {
		if(hasGoodReturn.equals("1"))
			hasGoodReturn="0";//����Ҫ�˻�
		if(hasGoodReturn.equals("2"))
			hasGoodReturn="1";//��Ҫ�˻�
		return hasGoodReturn;
	}



	private static String goodStatus(String goods_state) {
		if(goods_state.equals("1")||goods_state.equals("2")||goods_state.equals("3"))
			goods_state="BUYER_NOT_RECEIVED";
		if(goods_state.equals("4"))
			goods_state="BUYER_RECEIVED";
		return goods_state;
	}



	private static String state(String refund_state,String return_type,String goods_state,String seller_state) {
		//����Ѿ������˿�ȴ�����ͬ��
		if(seller_state.equals("1"))
			refund_state="WAIT_SELLER_AGREE";
		//�����Ѿ�ͬ���˿�ȴ�����˻�
		if(seller_state.equals("2"))
			refund_state="WAIT_BUYER_RETURN_GOODS";
		//���Ҿܾ��˿�
		if(seller_state.equals("3"))
			refund_state="SELLER_REFUSE_BUYER";
		//����Ѿ��˻����ȴ�����ȷ���ջ�
		if(goods_state.equals("2"))
			refund_state="WAIT_SELLER_CONFIRM_GOODS";
		//�˿�ɹ�
		if(refund_state.equals("3"))
			refund_state="SUCCESS";
		
		return refund_state;
	}







	
}
