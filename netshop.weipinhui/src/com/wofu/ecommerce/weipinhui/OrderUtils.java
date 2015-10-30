package com.wofu.ecommerce.weipinhui;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONException;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weipinhui.util.CommHelper;

public class OrderUtils {
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	//״̬���ձ�
	private static String[][] OrderStatusList = new String[][]{
		{"0","δ֧������"},
		{"1","����˶�������֧��/δ����"},
		{"10","��������ˣ��Ѵ���"},
		{"11","δ����"},
		{"12","��Ʒ������"},
		{"13","ȱ��"},
		{"14","��������ʧ��"},
		{"20","�����"},
		{"21","�Ѵ��"},
		{"22","�ѷ���"},
		{"23","�ۺ���"},
		{"24","δ����"},
		{"25","��ǩ��"},
		{"28","�����ط�"},
		{"30","δ����"},
		{"31","δ����"},
		{"40","��Ʒ�ؼ���"},
		{"41","�˻�����������"},
		{"42","��Ч����"},
		{"44","�ѷ���"},
		{"45","�˿����"},
		{"46","�˻���δ����"},
		{"47","�޸��˿�����"},
		{"48","��Ч�˻�"},
		{"49","���˿�"},
		{"51","�˻��쳣������"},
		{"52","�˿��쳣������"},
		{"53","�˻�δ���"},
		{"54","�˻������"},
		{"55","���ջط�"},
		{"56","�ۺ��쳣"},
		{"57","����ȡ��"},
		{"58","�˻��ѷ���"},
		{"59","���˻�"},
		{"60","�����"},
		{"61","�ѻ���"},
		{"70","�û��Ѿ���"},
		{"71","����������"},
		{"72","���շ�����"},
		{"96","�������޸�"},
		{"97","������ȡ��"},
		{"98","�Ѻϲ�"},
		{"99","��ɾ��"},
		{"100","�˻�ʧ��"}
	};
	
	
	/**
	 * �����ӿڶ���
	 * @param conn
	 * @param o
	 * @param tradeContactID
	 * @param username
	 * @return
	 * @throws SQLException 
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
				throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");
			
			//������ϸ
			//float totalPrice = 0.00f ;//�ܽ��
			//float sellerDiscount = 0.0f ;//�̼����Żݽ��
			//ʵ���ܽ��
			//float totalItemPayment=0.0f;
			//���ʷ�
			//float totalPostfee=0.0f;
			
			//Ӧ���ܽ��      ���ų��ⵥ��Ʒ����ܺ�(���㷢Ʊ��� == ���ų��ⵥ��Ʒ����ܺ� + ��ݷ��� - �Żݽ�� - �����Żݽ��)
			String totalfee=String.valueOf(o.getProduct_money());
			int j=0;
			for(Iterator ito=o.getOrderItemList().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();	
				String itemPayment = String.valueOf(item.getSell_price()*item.getAmount());
				sql = new StringBuilder()
					.append("insert into ns_orderitem(")
					.append("CustomerOrderId, orderItemId, SheetID, skuid, itemmealname, ")
                    .append("title, sellernick, buyernick, type, created, ")
                    .append("refundstatus, outeriid, outerskuid, totalfee, payment, ")
                    .append("discountfee, adjustfee, status, owner, ")
                    .append("skuPropertiesName, num, price, picPath, modified) values('" )
                    .append(sheetid).append("','")	//CustomerOrderId
                    .append(sheetid).append("-").append(o.getOrder_id()).append(String.valueOf(++j)).append("','")	//orderItemId
                    .append(sheetid).append("','")	//SheetID
                    .append(item.getBarcode()).append("','','")	//skuid,itemmealname
                    .append(item.getProduct_name()).append("','")	//title
                    .append(username).append("','")	//sellernick
                    .append(o.getBuyer()).append("','','")	//buyernick,type
                    .append(Formatter.format(o.getAdd_time(), Formatter.DATE_TIME_FORMAT)).append("','','")	//created,refundstatus
                    .append(item.getArt_no()).append("','")	//outeriid
                    .append(item.getBarcode()).append("','")	//outerskuid
                    .append(itemPayment).append("','")	//totalfee
                    .append(itemPayment).append("','")	//payment
                    .append("','','")	//discountfee,adjustfee
                    .append(getOrderStateByCode(o.getOrder_status())).append("','yongjun',")	//status,owner
                    .append("'','")	//skuPropertiesName
                    .append((int)item.getAmount()).append("','")	//num
                    .append(item.getSell_price()).append("','','")	//price,picPath
                    .append(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)).append("')").toString();	//modified
				//Log.info("ns_orderitem��SQL��䣺"+sql);    
        		SQLHelper.executeSQL(conn, sql) ;

			}
			Log.info("ns_orderitemд����ϣ���ns_customerorder��");
			//���뵽���ݱ�
			if("".equals(o.getInvoice()))  
			sql =  new StringBuilder()
					.append("insert into ns_customerorder(")
					.append("CustomerOrderId, SheetID, Owner, tid, OrderSheetID, sellernick, ")
	            	.append("type, created, buyermessage, shippingtype, payment, discountfee, ")
					.append("adjustfee, status, paytime, totalfee, postfee, buyeralipayno, ")
					.append("buyernick, buyerUin, receivername, receiverstate, receivercity, receiverdistrict, ")
					.append("receiveraddress, receivermobile, tradefrom, TradeContactID, modified, receiverphone) values('")
					.append(sheetid).append("','")	//CustomerOrderId
					.append(sheetid).append("','yongjun','")	//SheetID,Owner
					.append(o.getOrder_id()).append("','','")	//tid,OrderSheetID
					.append(username).append("','','")	//sellernick,type
					.append(Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)).append("','")	//created
					.append(o.getRemark()).append("','','")	//buyermessage,shippingtype
					.append(totalfee).append("','")	//payment
					.append(o.getDiscount_amount()).append("','")	//discountfee
					.append(o.getPromo_discount_amount()).append("','")	//adjustfee
					.append(getOrderStateByCode(o.getOrder_status())).append("','")	//status
					.append(Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)).append("','")	//paytime
					.append(o.getProduct_money() - o.getCarriage() + o.getPromo_discount_amount() + o.getDiscount_amount()).append("','")	//totalfee
					.append(o.getCarriage()).append("','','")	//postfee,buyeralipayno
					.append(o.getBuyer().replaceAll("'","''")).append("','','")	//buyernick,buyerUin
					.append(o.getBuyer().replace("'", "''")).append("','")	//receivername,
					.append(o.getProvince()).append("','")	//receiverstate
					.append(o.getCity()).append("','")	//receivercity
					.append(o.getCountry()).append("','")	//receiverdistrict
					.append(o.getAddress().replaceAll("'", "")).append("','")	//receiveraddress
					.append(o.getMobile()).append("','WEIPINHUI','")	//receivermobile,tradefrom
					.append(tradeContactID).append("','")	//TradeContactID
					.append(Formatter.format(o.getAdd_time(), Formatter.DATE_TIME_FORMAT)).append("','")	//modified
					.append(o.getTel()).append("')").toString();	//receiverphone
			else{  
				//��Ҫ��Ʊ
				sql =  new StringBuilder().append("insert into ns_customerorder(")
					.append("CustomerOrderId, SheetID, Owner, tid, OrderSheetID, sellernick, ")
					.append("InvoiceFlag, invoicetitle, type, created, buyermessage, shippingtype, ")
					.append("payment, discountfee, adjustfee, status, paytime, totalfee, ")  //11
					.append("postfee, buyeralipayno, buyernick, buyerUin, receivername, receiverstate, ")//18
					.append("receivercity, receiverdistrict, receiveraddress, receivermobile, tradefrom, TradeContactID, ")//24
					.append("modified, receiverphone) values('")
					.append(sheetid).append("','")	//CustomerOrderId
					.append(sheetid).append("','yongjun','")	//SheetID,Owner
					.append(o.getOrder_id()).append("','','")	//tid,OrderSheetID
					.append(username).append("','1','")	//sellernick,InvoiceFlag
					.append(o.getInvoice()!=null?o.getInvoice().replaceAll("'","''"):"").append("','','")	//invoicetitle,type
					.append(Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)).append("','")	//created
					.append(o.getRemark()).append("','','")	//buyermessage,shippingtype
					.append(totalfee).append("','")	//payment
					.append(o.getDiscount_amount()).append("','")	//discountfee
					.append(o.getPromo_discount_amount()).append("','")  //adjustfee
					.append(getOrderStateByCode(o.getOrder_status())).append("','")	//status
					.append(Formatter.format(o.getAdd_time(),Formatter.DATE_TIME_FORMAT)).append("','")	//paytime
					.append(o.getProduct_money() - o.getCarriage() + o.getPromo_discount_amount() + o.getDiscount_amount()).append("','")	//totalfee
					.append(o.getCarriage()).append("','','")	//postfee,buyeralipayno
					.append(o.getBuyer().replace("'", "''")).append("','','")	//buyernick,buyerUin
					.append(o.getBuyer().replace("'", "''")).append("','")	//receivername
					.append(o.getProvince()).append("','")	//receiverstate
					.append(o.getCity()).append("','")	//receivercity
					.append(o.getCountry()).append("','")	//receiverdistrict
					.append(o.getAddress().replaceAll("'", "")).append("','")	//receiveraddress
					.append(o.getMobile()).append("','WEIPINHUI','")	//receivermobile,tradefrom
	                .append(tradeContactID).append("','")	//TradeContactID
	                .append(Formatter.format(o.getAdd_time(), Formatter.DATE_TIME_FORMAT)).append("','")	//modified
	                .append(o.getTel()).append("')").toString();	//receiverphone
			}
			//Log.info("ns_customerorder��SQL���:��"+sql);
			SQLHelper.executeSQL(conn, sql);
			//���뵽֪ͨ��
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                + sheetid +"',1 , '"+tradeContactID+"' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("���ɶ�����" + o.getOrder_id() + "���ӿ����ݳɹ����ӿڵ��š�" + sheetid + "��");

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
			throw new JException("���ɶ�����" + o.getOrder_id() + "���ӿ�����ʧ��!"
					+ e1.getMessage());
		}
	}
	
	/**
	 * ���ض���״̬
	 * @param orderStateCode
	 * @return
	 */
	public static String getOrderStateByCode(String orderStateCode)
	{
		String result = "";
		for(int i=0;i<OrderStatusList.length;i++)
		{
			if(orderStateCode.equals(OrderStatusList[i][0]))
			{
				result = OrderStatusList[i][1];
				break;
			}
		}
		return result;
	}
	
	/**
	 * ��ȡ��������
	 * @param order_sn ������
	 * @return
	 * @throws Exception
	 */
	public static JSONArray getOrderItem(String order_sn) throws Exception{
		Log.info("��ȡ��������:" + order_sn);
		JSONArray result = null;
		int pageIndex = 1;
		boolean hasNextPage = true;
		//��ȡ������ϸ
		while(hasNextPage)
		{
			JSONObject jsonobj = new JSONObject();
			result = new JSONArray();
			try {
				jsonobj.put("order_id", order_sn);
				jsonobj.put("vendor_id", Params.vendor_id);
				jsonobj.put("page", 1);
				jsonobj.put("limit", 100);

				String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getOrderDetail", jsonobj.toString());
				
				String returnCode = new JSONObject(responseText).getString("returnCode");
				if(!returnCode.equals("0"))
					break;
				JSONArray orderDetails = new JSONObject(responseText).getJSONObject("result").getJSONArray("orderDetails");
				
				int orderNum= new JSONObject(responseText).getJSONObject("result").getInt("total");
				int pageTotal=0;
				if(orderNum!=0){
					pageTotal = orderNum>=Integer.parseInt(Params.pageSize) ? (orderNum %Integer.parseInt(Params.pageSize)==0?orderNum /Integer.parseInt(Params.pageSize):(orderNum /Integer.parseInt(Params.pageSize)+1)) : 1;
				}
				
				for(int i=0;i<orderDetails.length();i++)
				{
					result.put(orderDetails.getJSONObject(i));
				}
				
				//�ж��Ƿ�����һҳ
				if(pageIndex >= pageTotal)
					hasNextPage = false ;
				else
					pageIndex ++ ;
				
			} catch (JSONException e) {
				//e.printStackTrace();
				break;
			}
		}
		return result;
	}
	
	/**
	 * ��ȡ�˻���������
	 * @param order_sn �������뵥��
	 * @return
	 * @throws Exception
	 */
	public static JSONArray getRefundOrderItem(String back_sn) throws Exception{
		Log.info("��ȡ�˻���������,�������뵥��:" + back_sn);
		JSONArray result = null;
		int pageIndex = 1;
		boolean hasNextPage = true;
		//��ȡ������ϸ
		while(hasNextPage)
		{
			JSONObject jsonobj = new JSONObject();
			result = new JSONArray();
			try {
				jsonobj.put("back_sn", back_sn);
				jsonobj.put("vendor_id", Params.vendor_id);
				jsonobj.put("page", 1);
				jsonobj.put("limit", 100);
				String responseText = CommHelper.doRequest("vipapis.delivery.DvdDeliveryService", "getReturnProduct", jsonobj.toString());
				
				String returnCode = new JSONObject(responseText).getString("returnCode");
				if(!returnCode.equals("0"))
					break;
				JSONArray productDetails = new JSONObject(responseText).getJSONObject("result").getJSONArray("dvd_return_product_list");

				int orderNum= new JSONObject(responseText).getJSONObject("result").getInt("total");
				int pageTotal=0;
				if(orderNum!=0){
					pageTotal = orderNum>=Integer.parseInt(Params.pageSize) ? (orderNum %Integer.parseInt(Params.pageSize)==0?orderNum /Integer.parseInt(Params.pageSize):(orderNum /Integer.parseInt(Params.pageSize)+1)) : 1;
				}
				
				for(int i=0;i<productDetails.length();i++)
				{
					result.put(productDetails.getJSONObject(i));
				}
				
				//�ж��Ƿ�����һҳ
				if(pageIndex >= pageTotal)
					hasNextPage = false ;
				else
					pageIndex ++ ;
			} catch (JSONException e) {
				//e.printStackTrace();
				break;
			}
		}
		return result;
	}
	
	//�����˻��ӿ�����
	public static void createRefundOrder(Connection conn,ReturnOrder r,String tradecontactid) throws Exception
	{
		if(r == null) return;
		//��ȡinshopid
		String sql = "select shopid from ContactShopContrast with(nolock) where tradecontactid="+ tradecontactid;
		String inshopid = SQLHelper.strSelect(conn, sql);
		conn.setAutoCommit(false);
			for(Iterator ito=r.getOrderItemList().getRelationData().iterator();ito.hasNext();)
			{
				ReturnOrderItem ritem=(ReturnOrderItem) ito.next();
				sql = "declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
				String sheetid = SQLHelper.strSelect(conn, sql);
				if (sheetid.trim().equals(""))
					throw new JSQLException(sql, "ȡ�ӿڵ��ų���!");

				// ���뵽֪ͨ��
				sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) "
						+"values('yongjun','"+ sheetid+ "',2 , '"+ tradecontactid+ "' , 'yongjun' , getdate() , null) ";
				SQLHelper.executeSQL(conn, sql);
				
				//д���˻�����Ϣ��ns_Refund��
				sql =	"insert into ns_Refund(" +
						"SheetID, RefundID, Oid, Tid, Created, "+ 
						"GoodStatus, Status, HasGoodReturn, Payment, Reason, Title, "+ 
						"Num, OuterIid, InShopID)"+ 
						"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
				//��ȡ����״̬
				String Status = "";
				
				//ΨƷ���ṩ���ֶ�:
				//�˻���:��Ӧ��ID,�������,�˻����뵥״̬,�˻�ԭ��,��b2c��ȡ���˶���״̬ʱ��,�������뵥��
				//�˻�����ϸ:��Ʒ����,�������,PO����,������,�˻���Ʒ����
				Object[] sqlv = {
					sheetid,	//�ӿڵ���
					r.getBack_sn(),	//�������뵥��
					ritem.getOrder_id(),	//��Ʒ-�������
					r.getOrder_id(),	//������
					Formatter.format(r.getCreate_time(),Formatter.DATE_TIME_FORMAT),	//����ʱ�� - ��b2c��ȡ���˶���״̬ʱ��
				goodStatus(r.getReturn_status()),		//��Ʒ״̬
					state(r.getReturn_status()),		//����״̬			�˻����뵥״̬
					1,	//�Ƿ���Ҫ�˻�(0:����Ҫ,1��Ҫ)
					0,	//Ӧ������(0)
					r.getReturn_reason(),	//�˿�ԭ��
					ritem.getProduct_name(),	//��Ʒ����
					ritem.getAmount(),	//����				�˻���Ʒ����
					ritem.getBarcode(),	//��Ʒ���� sku		������
					inshopid	//tradecontactid
				};
				SQLHelper.executePreparedSQL(conn, sql, sqlv);
				
				Log.info("�ӿڵ���:"+ sheetid + " ������:" + r.getOrder_id()+ " ״̬��"+ Status+ "�˻�����ʱ��:"
						+ Formatter.format(r.getCreate_time(),Formatter.DATE_TIME_FORMAT));
			}		
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	private static String goodStatus(String goods_state) {
		if(goods_state.equals("59"))
			//�ͻ����˻�
			goods_state="BUYER_RECEIVED";
		else
			//�ͻ�δ�˻�
			goods_state="BUYER_NOT_RECEIVED";
		
		return goods_state;
	}

	private static String state(String order_state) {
		//����Ѿ������˿�ȴ�����ͬ��
		if(order_state.equals("46")||order_state.equals("53"))
			order_state="WAIT_SELLER_AGREE";
		//�����Ѿ�ͬ���˿�ȴ�����˻�
		if(order_state.equals("54"))
			order_state="WAIT_BUYER_RETURN_GOODS";
		//���Ҿܾ��˿�
		if(order_state.equals("41")||order_state.equals("48")||order_state.equals("100"))
			order_state="SELLER_REFUSE_BUYER";
		//����Ѿ��˻����ȴ�����ȷ���ջ�
		if(order_state.equals("45"))
			order_state="WAIT_SELLER_CONFIRM_GOODS";
		//�˿�ɹ�
		if(order_state.equals("60"))
			order_state="SUCCESS";
		
		return order_state;
	}
	
//	״̬�б�:
//		����Ѿ������˿�ȴ�����ͬ��
//			WAIT_SELLER_AGREE				46�˻���δ����/53�˻�δ���
//		�����Ѿ�ͬ���˿�ȴ�����˻�
//			WAIT_BUYER_RETURN_GOODS			54�˻������
//		���Ҿܾ��˿�
//			SELLER_REFUSE_BUYER				41�˻�����������/48��Ч�˻�/100�˻�ʧ��
//		����Ѿ��˻����ȴ�����ȷ���ջ�
//			WAIT_SELLER_CONFIRM_GOODS		45�˿����
//		�˻��ɹ�
//			SUCCESS			60�����
//				
//	��Ʒ״̬
//		�ͻ�δ�˻�
//			BUYER_NOT_RECEIVED   ��59�Ķ���
//		�ͻ����˻�
//			BUYER_RECEIVED		59���˻�
}
