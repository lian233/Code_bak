package com.wofu.ecommerce.rke2;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Element;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.rke2.utils.Utils;



public class OrderUtils {
	/*
	 * ת��һ���������ӿڱ�
	 */
	/**
	public static String createInterOrder(Connection conn,
			Order o, String tradecontactid,String username) throws Exception {
		try {

			String sheetid = "";
			
			int paymode=1;
			
			conn.setAutoCommit(false);

			String sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "ȡ�ӿڵ��ų���!");

			// ���뵽֪ͨ��
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
					+ sheetid+ "',1 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			sql = "insert into ns_customerorder"
					+ "(CustomerOrderId , SheetID , Owner , tid  , sellernick , paymode,"
					+ "  created ,  payment ,  status  , paytime ,  modified , "  //sellernickΪu8����
					+ " totalfee , postfee, buyernick , receivername , receiverstate , receivercity , receiverdistrict , "
					+ " receiveraddress , receiverzip , receivermobile , receiverphone , tradefrom,tradeContactid) "
					+ " values('"+ sheetid+ "','"+ sheetid+ "','"+username+"','"+ o.getOrder_sn()
					+ "','"+ o.getOrder_sn()+ "', "+paymode+",'"+Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)+"',"+ o.getOrder_amount() +", '"
					+ o.getOrder_state()+ "' ,'"+Formatter.format(o.getPayment_time(), Formatter.DATE_TIME_FORMAT)+"',"
					+"'"+Formatter.format(o.getPayment_time(), Formatter.DATE_TIME_FORMAT)+ "' , "+ o.getOrder_amount()+ " , '"+o.getShipping_fee()+ "'"
					+ ",'"	+ username+ "' ,'"+ o.getReciver_name()+ "' , '"
					+ o.getReciver_province_id()+ "', '"	+ o.getReciver_city_id()+ "' , '"+o.getDistri_level_id()+"', "
					+ "'"+ o.getAddress()+ "','"+ ""/*o.getZipcode()+ "' , '"
					
					+ o.getMob_phone()+ "' , '"+ o.getTel_phone()+ "','����'," + tradecontactid + ")";

			SQLHelper.executeSQL(conn, sql);
			

			Log.info("size: "+o.getOrderItems().size());
			for (int i=0;i<o.getOrderItems().size();i++) {
				
				OrderItem item = (OrderItem) o.getOrderItems().get(i);

				String orderItemId ="".equals(item.getGoods_id())?sheetid+item.getSku():sheetid+item.getGoods_id();
				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  ,oid, SheetID  ,skuid , "
						+ " title , sellernick , created , "
						+ "  outerskuid , totalfee , payment ,num , price ) values( "
						+ "'"+ sheetid+ "','"+ orderItemId+ "','"+item.getGoods_id()+"','"+ sheetid+ "','"+item.getGoods_sn()+"','"
						+ item.getGoods_name()+ "' , '"+ username+ "', '"+Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)
						+ "', '"+ item.getSku()+ "' , '"+ item.getGoods_price()*item.getGoods_number()
						+ "' , '"+item.getGoods_price()*item.getGoods_number()+"',"				
						+ item.getGoods_number()+ " , '"+ item.getGoods_price()+"')";
				SQLHelper.executeSQL(conn, sql);		
			}
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("���ɶ�����" + o.getOrder_sn() + "���ӿ����ݳɹ����ӿڵ��š�"+ sheetid + "��");
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
			throw new JException("���ɶ�����" + o.getOrder_sn() + "���ӿ�����ʧ��,������Ϣ��"+ e1.getMessage());
		}
	}**/
	
	/**
	public static void getRefund(Connection conn,String tradecontactid,Order o)	throws Exception {

			
		
			String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="
					+ tradecontactid;
			String inshopid = SQLHelper.strSelect(conn, sql);

			conn.setAutoCommit(false);
			
			for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();

				sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
				String sheetid = SQLHelper.strSelect(conn, sql);
				if (sheetid.trim().equals(""))
					throw new JSQLException(sql, "ȡ�ӿڵ��ų���!");
	
				// ���뵽֪ͨ��
				sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
						+"values('yongjun','"+ sheetid+ "',2 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
				SQLHelper.executeSQL(conn, sql);
	
				sql = "insert into ns_Refund(SheetID , tid,RefundID , Oid , AlipayNo , "
						+ "BuyerNick , Created , Modified , OrderStatus , Status , GoodStatus , "
						+ " HasGoodReturn ,RefundFee , Payment , Reason,Description , Title ,"
						+ "Price , Num , GoodReturnTime , Sid , "
						+ " TotalFee ,  OuterIid , OuterSkuId , CompanyName , "
						+ "Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
						+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
				
				Object[] sqlv = {
						sheetid,
						o.getOrderCode(),
						String.valueOf(o.getOrderId())+String.valueOf(item.getId()),
						String.valueOf(item.getId()),
						o.getEndUserId(),
						o.getEndUserId(),
						o.getOrderCreateTime(),
						o.getUpdateTime(),
						o.getOrderStatus(),
						o.getOrderStatus(),
						o.getOrderStatus(),
						1,
						item.getOrderItemAmount(),
						item.getOrderItemAmount(),
						o.getDeliveryRemark(),
						o.getDeliveryRemark(),
						item.getProductCName(),
						item.getOrderItemPrice(),
						item.getOrderItemNum(),
						item.getProcessFinishDate(),
						"",
						item.getOrderItemAmount(),
						item.getOuterId(),
						item.getOuterId(),
						"",
						"",
						o.getGoodReceiverProvince() + " " + o.getGoodReceiverCity() + " "
								+ o.getGoodReceiverCounty()+ " "
								+ o.getGoodReceiverAddress(), inshopid,
						o.getOrderCode(), o.getGoodReceiverName(),
						o.getGoodReceiverPhone() + " " + o.getGoodReceiverMoblie(),
						o.getEndUserId() };
	
	
				SQLHelper.executePreparedSQL(conn, sql, sqlv);
				
			}

	

			Log.info("�����˻����ɹ�,������:"+ o.getOrderCode()+ " ����״̬��"+o.getOrderStatus()				
					+ " ��������ʱ��:"+Formatter.format(o.getOrderCreateTime(),Formatter.DATE_TIME_FORMAT));

			conn.commit();
			conn.setAutoCommit(true);

	}**/
	/**
	public static void createRefund(Connection conn,RefundDetail r,
			int tradecontactid,String app_key,String token,String format,String ver) throws Exception 
	{
		String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="+ tradecontactid;
		String inshopid = SQLHelper.strSelect(conn, sql);

		
		
		Map<String, String> orderparams = new HashMap<String, String>();
        //ϵͳ����������
		orderparams.put("appKey", app_key);
		orderparams.put("sessionKey", token);
		orderparams.put("format", format);
		orderparams.put("method", "yhd.order.detail.get");
		orderparams.put("ver", ver);
		orderparams.put("timestamp", Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
        
		orderparams.put("orderCode", r.getOrderCode());
     
        
		String responseOrderData = Utils.sendByPost(orderparams,Params.app_secret,Params.url);
        
		//Log.info("�˻�����: "+responseOrderData);
		JSONObject responseorder=new JSONObject(responseOrderData);
		
		int errorOrderCount=responseorder.getJSONObject("response").getInt("errorCount");
		
		if (errorOrderCount>0)
		{
			String errdesc="";
			JSONArray errlist=responseorder.getJSONObject("response").getJSONObject("errInfoList").getJSONArray("errDetailInfo");
			for(int n=0;n<errlist.length();n++)
			{
				JSONObject errinfo=errlist.getJSONObject(n);
				
				errdesc=errdesc+" "+errinfo.getString("errorDes"); 
									
			}
			
			throw new JException(errdesc);						
		}
		
		
		JSONObject orderdetail=responseorder.getJSONObject("response").getJSONObject("orderInfo").getJSONObject("orderDetail");
		
		
		Order o=new Order();
		o.setObjValue(o, orderdetail);
						
		
		JSONArray orderItemList=responseorder.getJSONObject("response").getJSONObject("orderInfo").getJSONObject("orderItemList").getJSONArray("orderItem");
		
		o.setFieldValue(o, "orderItemList", orderItemList);
		
		conn.setAutoCommit(false);
		
		for (Iterator it=r.getRefundItemList().getRelationData().iterator();it.hasNext();)
		{

			RefundItem item=(RefundItem) it.next();
			
			sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			String sheetid = SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql, "ȡ�ӿڵ��ų���!");

			// ���뵽֪ͨ��
			sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
					+"values('yongjun','"+ sheetid+ "',2 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			sql = "insert into ns_Refund(SheetID ,RefundID , Oid  , "
					+ "BuyerNick , Created , Modified ,  Status , GoodStatus , "
					+ " HasGoodReturn ,RefundFee , Payment , Reason,Description , Title ,"
					+ "Price , Num , GoodReturnTime  , "
					+ " TotalFee ,  OuterIid , OuterSkuId , CompanyName ,sid, "
					+ "Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele)"
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			String outerskuid="";
			for (Iterator itorder=o.getOrderItemList().getRelationData().iterator();itorder.hasNext();)
			{
				OrderItem orderitem=(OrderItem) itorder.next();
				if (orderitem.getId()==item.getOrderItemId())
					outerskuid=orderitem.getOuterId();
			}
						

			Object[] sqlv = {
					sheetid,
					String.valueOf(r.getRefundCode()),
					String.valueOf(item.getOrderItemId()),
					r.getReceiverName(),
					r.getApplyDate(),
					r.getApplyDate(),
					String.valueOf(r.getRefundStatus()),
					String.valueOf(r.getRefundStatus()),
					1,
					Double.valueOf(item.getOrderItemPrice()*item.getProductRefundNum()),
					Double.valueOf(item.getOrderItemPrice()*item.getProductRefundNum()),
					r.getReasonMsg(),
					r.getRefundProblem(),
					item.getProductCname(),
					item.getOrderItemPrice(),
					item.getProductRefundNum(),
					r.getSendBackDate(),
					Double.valueOf(item.getOrderItemPrice()*item.getProductRefundNum()),
					outerskuid,
					outerskuid,
					r.getExpressName(),
					r.getExpressNbr(),
					r.getSendBackAddress(),
					r.getReceiverAddress(), inshopid,
					r.getOrderCode(), r.getReceiverName(),
					r.getReceiverPhone()};
		

			SQLHelper.executePreparedSQL(conn, sql, sqlv);


			Log.info( "�ӿڵ���:"+ sheetid	+ " ������:"	+ r.getOrderCode()+ " ״̬��"+ r.getRefundStatus()+ "�˻�����ʱ��:"
					+ Formatter.format(r.getApplyDate(),
							Formatter.DATE_TIME_FORMAT));
		}
		
		conn.commit();
		conn.setAutoCommit(true);

	}
	**/

	
	
}
