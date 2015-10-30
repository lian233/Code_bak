package com.wofu.ecommerce.ecshop;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import com.sun.org.apache.xpath.internal.operations.Or;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.ecshop.util.CommHelper;

public class CopyOfOrderUtils {
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	private static String refundDesc[] = {"","�˻�","����",""} ;


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
			float totalPrice = o.getGoods_amount() ;//�ܽ��
			float sellerDiscount = o.getPreferential() ;//�̼����Żݽ��
			//ʵ���ܽ��
			float totalItemPayment=0.0f;
			//Ӧ���ܽ��
			float totalfee=0.0f;
			BigDecimal b1,b2;
			//���ʷ�
			float totalPostfee=o.getFee();
			float paymentPercent = 1-(sellerDiscount/totalPrice) ;
			float countDiscountFee = 0f ;
			float countPayment = 0f ;
			//��Ʊ���
			//float invoicePercent = 1 - ((discountfee+giftCardMoney) / totalPrice) ;
			//float countInvoicePayment = 0f ;
			int j=0;
			int InvoiceFlag="".equals(o.getInv_payee())?0:1;
			String status = "0".equals(o.getShipping_status())?"�ȴ�����":"�ѷ���";
			Date addTime = new Date(o.getAdd_time()*1000L);
			//��ݹ�˾
			String delivery = Params.htComCode1.get(o.getShipping_id());
			for(Iterator ito=o.getShop_info().getRelationData().iterator();ito.hasNext();)
			{
				OrderItem item=(OrderItem) ito.next();
				float payment = 0f;
				float discountFee = 0f;
				float itemTotalTemp = item.getGoods_price()* item.getGoods_number();
				if(j==o.getShop_info().size()-1){
					payment = Float.parseFloat(decimalFormat.format(totalPrice-countPayment));
					b1 = new BigDecimal(Float.toString(itemTotalTemp));
					b2 = new BigDecimal(Float.toString(payment));
					discountFee = b1.subtract(b2).floatValue();
					
				}else{
					payment = Float.parseFloat(decimalFormat.format(itemTotalTemp * paymentPercent));
					countPayment +=payment;
					discountFee = itemTotalTemp - payment;
				}
				
				//float invoicePayment = 0f ;
				//����iidΪ�����̼ұ���
				sql = new StringBuilder().append("insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , ")
                    .append(" title , sellernick , buyernick , type , created , ") 
                    .append(" refundstatus , outeriid , outerskuid , totalfee , payment , ")
                    .append(" discountfee , adjustfee , status , timeoutactiontime , owner , ")
                    .append(" iid , skuPropertiesName , num , price , " )
                    .append(" modified) values( '")
                    .append(sheetid).append("','").append(sheetid).append("-").append(o.getOrder_sn()).append(String.valueOf(++j))
                    .append("','").append(sheetid).append("','").append(item.getGoods_id()).append("','','")
                    .append(item.getGoods_name()).append("','").append(username).append("','")
                    .append(o.getUser_name()).append("','','")
                    .append(Formatter.format(addTime, Formatter.DATE_TIME_FORMAT)).append("','','").append(item.getGoods_id()).append("','")
                    .append(item.getProduct_sn()).append("','").append(itemTotalTemp).append("','").append(payment).append("','")
                    .append(discountFee).append("','','").append(status).append("','','yongjun','").append(item.getGoods_id()).append("','','")
                    .append((int)item.getGoods_number()).append("','").append(item.getGoods_price()).append("','")
                    .append(Formatter.format(addTime,Formatter.DATE_TIME_FORMAT)).append("')").toString();
				//Log.info("ns_orderitem��SQL��䣺"+sql);    
        		SQLHelper.executeSQL(conn, sql) ;
        		
			}
			//Log.info("ns_orderitemд����ϣ���ns_customerorder��");
			//���뵽���ݱ�
			if(!"".equals(o.getRealename()) && !"".equals(o.getSubs_identi_no())){
				sql =  new StringBuilder().append("insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , ")
            	.append(" type , created , buyermessage , shippingtype , payment , ")
				.append(" discountfee , adjustfee , status ,paytime,totalfee , postfee , buyeralipayno , ")
				.append(" buyernick ,buyerUin, receivername , receiverstate , receivercity , receiverdistrict ,")
				.append(" receiveraddress , receivermobile , receiverphone,")
				.append(" tradefrom,TradeContactID,modified,InvoiceFlag,delivery,CertType,CertNo,Tax,CertName) values('")
				.append(sheetid).append("','").append(sheetid).append("','yongjun','").append(o.getOrder_sn()).append("','','").append(username)
				.append("','','").append(Formatter.format(addTime,Formatter.DATE_TIME_FORMAT)).append("','','','").append(totalPrice+totalPostfee).append("','").append(sellerDiscount).append("','','")
				.append(status).append("','").append(Formatter.format(addTime,Formatter.DATE_TIME_FORMAT)).append("','").append(totalPrice).append("','").append(totalPostfee).append("','','").append(new String(o.getUser_name().getBytes())).append("','','")
				.append(new String(o.getConsignee().getBytes())).append("','").append(new String(o.getProvince().getBytes())).append("','").append(new String(o.getCity().getBytes())).append("','").append(new String(o.getDistrict().getBytes()))
				.append("','").append(new String(o.getAddress().getBytes()).replaceAll("'", "")).append("','").append(o.getMobile()).append("','").append(o.getTel()).append("',")
                .append("'ECSHOP','").append(tradeContactID).append("','").append(Formatter.format(addTime, Formatter.DATE_TIME_FORMAT)).append("',").append(InvoiceFlag).append(",'").append(delivery).append("','01','").append(o.getSubs_identi_no()).append("','").append(o.getHwgs_fee()).append("','").append(o.getRealename()).append("')").toString();
			}else{
				sql =  new StringBuilder().append("insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , ")
            	.append(" type , created , buyermessage , shippingtype , payment , ")
				.append(" discountfee , adjustfee , status ,paytime,totalfee , postfee , buyeralipayno , ")
				.append(" buyernick ,buyerUin, receivername , receiverstate , receivercity , receiverdistrict , ")
				.append(" receiveraddress , receivermobile ,receiverphone, ")
				.append(" tradefrom,TradeContactID,modified,InvoiceFlag,delivery) values('")
				.append(sheetid).append("','").append(sheetid).append("','yongjun','").append(o.getOrder_sn()).append("','','").append(username)
				.append("','','").append(Formatter.format(addTime,Formatter.DATE_TIME_FORMAT)).append("','','','").append(totalPrice+totalPostfee).append("','").append(sellerDiscount).append("','','")
				.append(status).append("','").append(Formatter.format(addTime,Formatter.DATE_TIME_FORMAT)).append("','").append(totalPrice).append("','").append(totalPostfee).append("','','").append(new String(o.getUser_name().getBytes())).append("','','")
				.append(new String(o.getConsignee().getBytes())).append("','").append(new String(o.getProvince().getBytes())).append("','").append(new String(o.getCity().getBytes())).append("','").append(new String(o.getDistrict().getBytes()))
				.append("','").append(new String(o.getAddress().getBytes()).replaceAll("'", "")).append("','").append(o.getMobile()).append("','").append(o.getTel()).append("',")
                .append("'ECSHOP','").append(tradeContactID).append("','").append(Formatter.format(addTime, Formatter.DATE_TIME_FORMAT)).append("',").append(InvoiceFlag).append(",'").append(delivery).append("')").toString();
			}
			

			SQLHelper.executeSQL(conn, sql);
			//���뵽֪ͨ��
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                + sheetid +"',1 , '"+tradeContactID+"' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("���ɶ�����" + o.getOrder_sn() + "���ӿ����ݳɹ����ӿڵ��š�" + sheetid + "��");

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
			throw new JException("���ɶ�����" + o.getOrder_sn() + "���ӿ�����ʧ��!"
					+ e1.getMessage());
		}
	}

	


	
	//��ȡ��Ʊ��ϸ��Ϣ
	public static String getInvoiceDetail(String jobname,Connection conn,String sku)
	{
		String detail = "" ;
		try 
		{
			String sql = "select a.customBC+c.name from barcode as a with(nolock),goods as b with(nolock),dept as c with(nolock) " +
			"where a.goodsid=b.goodsid and b.deptid=c.id and a.customBC='"+ sku +"'" ;
			detail = SQLHelper.strSelect(conn, sql) ;
		}
		catch (Exception e) 
		{
			Log.error(jobname, "��ȡ��Ʒ�������ʧ��,������Ϣ:"+e.getMessage()) ;
			detail = "" ;
			e.printStackTrace() ;
		}
		
		return detail ;
	}

	//��ȡ��Ʊ��λ
	public static String getGoodsUnitName(String jobname,Connection conn,String sku)
	{
		String unitName = "" ;
		if("".equals(sku) || sku == null)
			return unitName ;
		try 
		{
			String sql ="select unitname from goods as a with(nolock),barcode as b with(nolock) where a.goodsid=b.goodsid and b.customBC='"+ sku +"'" ;
			unitName = SQLHelper.strSelect(conn, sql) ;
		} catch (Exception e) {
			Log.error(jobname, "��ȡ��Ʒ��λʧ��,������Ϣ:"+e.getMessage()+",sku:"+sku) ;
		}
		return unitName ;
	}
	
	//�����˻��ӿ�����
	public static void createRefundOrder(String jobname,Connection conn,String tradecontactid,
			Order order)
	{
		/**
		ReturnOrder o = new ReturnOrder();
		String sql = "" ;
		float refundFee = 0f ;
		//�������
		String BuyerNick=order.getUser_name();
		//֧�������ҵĽ��
		float Payment=o.getGoods_amount();
		try 
		{
			
			sql="select shopid from ContactShopContrast with(nolock) where tradecontactid="+tradecontactid;
            String inshopid = SQLHelper.strSelect(conn, sql);
			for(Iterator it = order.getShop_info().getRelationData().iterator() ; it.hasNext() ; )
			{
				try 
				{
					ReturnOrderItem item = (ReturnOrderItem)it.next() ;
		            conn.setAutoCommit(false);
		            
		            sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";			
					//Log.info(sql) ;
		            String sheetid = SQLHelper.strSelect(conn, sql);
					if (sheetid.trim().equals(""))
						throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");
					
					refundFee = item.getOrderQty()* item.get ;
					
					sql=new StringBuilder().append("insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , BuyerNick , ")
						.append("Created , Modified , OrderStatus , Status , GoodStatus , ")
	                    .append(" HasGoodReturn ,RefundFee , Payment , Reason,Description ,")
	                    .append(" Title , Price , Num , GoodReturnTime , Sid , ")
	                    .append(" TotalFee , Iid , OuterIid , OuterSkuId , CompanyName ,") 
	                    .append(" Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)")
	                    .append(" values('").append(sheetid ).append("','").append(sheetid).append("','','','").append(BuyerNick).append("','")
	                    .append( Formatter.format(new Date(o.getAdd_time()*1000L), Formatter.DATE_TIME_FORMAT)).append("','").append(Formatter.format(new Date(o.getAdd_time()*1000L), Formatter.DATE_TIME_FORMAT)).append("','','',''")
	                    .append("'1','").append(refundFee).append("','").append(Payment).append("','','','")
	                    .append(item.getGoods_name()).append("','").append(item.getGoods_price()).append("','").append(item.getGoods_number()).append("','").append(Formatter.format(new Date(o.getAdd_time()*1000L), Formatter.DATE_TIME_FORMAT)).append("','")
	                    .append("','").append(refundFee).append("','").append(item.getGoods_sn()).append("','','").append(item.getGoods_id()).append("','")
	                    .append("'','").append(order.getAddress().replaceAll("'",",'','")).append(inshopid).append("','").append(order.getOrder_sn()).append("'").append(order.getUser_name()).append("','").append(order.getTel()).append("','')").toString();

					Log.info("�˻���sql: "+sql) ;
					SQLHelper.executeSQL(conn,sql);
					
					//���뵽֪ͨ��     �˻���־Ϊ2
		            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
		                + sheetid +"',2 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";				
					//Log.info(sql) ;
					SQLHelper.executeSQL(conn,sql);
					
					Log.info(jobname,"�ӿڵ���:"+sheetid+" �˻�������:"+o.getOrder_sn()+"����������ʱ��:"+Formatter.format(new Date(o.getAdd_time()*1000L),Formatter.DATE_TIME_FORMAT));
					conn.commit();
					conn.setAutoCommit(true);
				}
				catch (SQLException e1)
				{			
					if (!conn.getAutoCommit())
						try
						{
							conn.rollback();
						}
						catch (Exception e2) { }
					try
					{
						conn.setAutoCommit(true);
					}
					catch (Exception e3) { }
					throw new JSQLException("�����˻���" + o.getOrder_sn() + "���ӿ�����ʧ��!"+e1.getMessage());
				}
			}
		}
		catch (Exception e) 
		{
			Log.error(jobname, "���ɽӿ��˻���ʧ��,������:"+o.getOrder_sn() + ",�˻�������:"+",������Ϣ:"+e.getMessage()) ;
			e.printStackTrace() ;
		}
		**/
	}
	//�˻���������
	private static String getRefundDesc(String index)
	{
		try 
		{
			return refundDesc[Integer.parseInt(index)] ;
		} catch (Exception e) {
			return index ;
		}
	}

	
	//���ض���״̬
	public static String getOrderStateByCode(String orderStateCode)
	{
		if("10".equals(orderStateCode))
			return "�ȴ�����" ;
		else if("20".equals(orderStateCode))
			return "�ѷ���" ;
		else if("21".equals(orderStateCode))
			return "���ַ���" ;
		else if("30".equals(orderStateCode))
			return "���׳ɹ�" ;
		else if("40".equals(orderStateCode))
			return "���׹ر�" ;
		else
			return "δ֪�Ķ���״̬" ;
	}
	

	
}
