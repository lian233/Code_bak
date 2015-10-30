package com.wofu.ecommerce.lenovo;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.conv.MD5Util;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.lenovo.util.CommHelper;

public class OrderUtils {
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	private static String refundDesc[] = {"","�˻�","����",""} ;

	/**
	 * ��ȡ����������ϸ��Ϣ
	 * @param orderCode	������
	 * @return
	 */
	

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
			//��Ʊ��Ϣ
			String InvoiceFlag="".equals(o.getInv_payee())?"0":"1";
			String InvoiceTitle="";
			if("1".equals(InvoiceFlag))
				InvoiceTitle = o.getInv_payee();
			//������ϸ
			float totalPrice = Float.parseFloat(o.getMoney_paid()) ;//�ܽ��
			//�����Żݽ��
			float sellerDiscount = Float.parseFloat(o.getDiscount());
			float paymentPercent= 1-(sellerDiscount/totalPrice);   //�û�Ӧ���ٷֱ�
			//ʵ���ܽ��
			float totalItemPayment=0.0f;
			//Ӧ���ܽ��
			float totalfee=0.0f;
			BigDecimal b1,b2;
			//���ʷ�
			float totalPostfee=0.0f;
			float CountPayment =0f;
			float discountFee = 0f;
			int j=0;
			String statusTemp = o.getShipping_status();
			String status = "0".equals(statusTemp)?"δ����":"�ѷ���";
			int i=0;
			for(Iterator ito=o.getGoods_list().getRelationData().iterator();ito.hasNext();i++)
			{
				float payment =0f;
				OrderItem item=(OrderItem) ito.next();	
				float itemPayment = item.getGoods_price()*Integer.parseInt(item.getGoods_number());
				totalfee+=itemPayment;
				if(i==o.getGoods_list().size()-1){
					b1 = new BigDecimal(Float.toString(totalPrice));
					b2 = new BigDecimal(Float.toString(CountPayment));
					payment = b1.subtract(b2).floatValue();
				}else{
					payment = Float.parseFloat(decimalFormat.format(itemPayment*paymentPercent));
					CountPayment+=payment;
					discountFee =itemPayment - payment ;
					
				}
				sql = new StringBuilder().append("insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , ")
                    .append(" title , sellernick , buyernick , type , created , ") 
                    .append(" refundstatus , outeriid , outerskuid , totalfee , payment , ")  //15
                    .append(" discountfee , adjustfee , status , timeoutactiontime , owner , ")
                    .append(" skuPropertiesName , num , price , picPath , " )
                    .append("modified) values( '")
                    .append(sheetid).append("','").append(sheetid).append("-").append(o.getOrder_sn()).append(String.valueOf(++j))
                    .append("','").append(sheetid).append("','").append(item.getGoods_id()).append("','','")
                    .append(item.getGoods_name()).append("','").append(username).append("','")
                    .append(o.getUser_name()).append("','','")
                    .append(Formatter.format(new Date(o.getAdd_time()*1000L), Formatter.DATE_TIME_FORMAT)).append("','','").append(item.getGoods_sn()).append("','")
                    .append(item.getGoods_sn()).append("','").append(itemPayment).append("','").append(payment).append("','")
                    .append(discountFee).append("','','").append(status).append("','','yongjun',").append("'','")
                    .append(Integer.parseInt(item.getGoods_number())).append("','").append(item.getGoods_price()).append("','','")
                    .append(Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)).append("')").toString();
				//Log.info("ns_orderitem��SQL��䣺"+sql);    
        		SQLHelper.executeSQL(conn, sql) ;

			}
			//Log.info("ns_orderitemд����ϣ���ns_customerorder��");
			//���뵽���ݱ�
			sql =  new StringBuilder().append("insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , ")
            	.append(" type , created , buyermessage , shippingtype , payment , ")  //11
				.append(" discountfee , adjustfee , status ,paytime,totalfee , postfee , buyeralipayno , ")//18
				.append(" buyernick ,buyerUin, receivername , receiverstate , receivercity , receiverdistrict , ")
				.append(" receiveraddress , receivermobile , ")
				.append(" tradefrom,TradeContactID,modified,InvoiceFlag,invoicetitle,paymode) values('")
				.append(sheetid).append("','").append(sheetid).append("','yongjun','").append(o.getOrder_sn()).append("','','").append(username)  //6
				.append("','','").append(Formatter.format(new Date(o.getAdd_time()*1000L),Formatter.DATE_TIME_FORMAT)).append("','','','").append(totalPrice).append("','").append(sellerDiscount).append("','','")  //13
				.append(status).append("','").append(Formatter.format(new Date(o.getAdd_time()*1000L),Formatter.DATE_TIME_FORMAT)).append("','").append(totalfee).append("','").append(o.getShipping_fee()).append("','','").append(o.getUser_name()).append("','','")
				.append(new String(o.getUser_name().getBytes(),"gbk")).append("','").append(new String(o.getProvince_name().getBytes(),"gbk")).append("','").append(new String(o.getCity_name().getBytes(),"gbk")).append("','").append(new String(o.getDistrict_name().getBytes(),"gbk"))
				.append("','").append(new String(o.getAddress().getBytes(),"gbk").replaceAll("'", "")).append("','").append(o.getTel()).append("',")
                .append("'LENOVO','").append(tradeContactID).append("','").append(Formatter.format(new Date(o.getAdd_time()*1000L), Formatter.DATE_TIME_FORMAT)).append("',").append(InvoiceFlag).append(",'").append(InvoiceTitle).append("',").append(o.getPay_id()).append(")").toString();
			//Log.info("ns_customerorder��SQL��䣺"+sql);

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
			RefundOrder order)
	{
		String sql = "" ;
		float refundFee = 0f ;
		//�������
		String BuyerNick=order.getUser_name();
		//֧�������ҵĽ��
		float Payment=Float.parseFloat(order.getTotal_fee());
		try 
		{
			sql="select shopid from ContactShopContrast with(nolock) where tradecontactid="+tradecontactid;
            String inshopid = SQLHelper.strSelect(conn, sql);
			for(Iterator it = order.getGoods_list().getRelationData().iterator() ; it.hasNext() ; )
			{
				try 
				{
					RefundOrderItem item = (RefundOrderItem)it.next();
		            conn.setAutoCommit(false);
		            
		            sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";			
					//Log.info(sql) ;
		            String sheetid = SQLHelper.strSelect(conn, sql);
					if (sheetid.trim().equals(""))
						throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");
					
					refundFee = Integer.parseInt(item.getGoods_number()) * item.getGoods_price();
					
					sql=new StringBuilder().append("insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , BuyerNick , ")
						.append("Created , Modified , OrderStatus , Status , GoodStatus , ")  //10
	                    .append(" HasGoodReturn ,RefundFee , Payment , Reason,Description ,")
	                    .append(" Title , Price , Num , GoodReturnTime , Sid , ")
	                    .append(" TotalFee , Iid , OuterIid , OuterSkuId , CompanyName ,") 
	                    .append(" Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)")
	                    .append(" values('").append(sheetid ).append("','").append(sheetid).append("','','','").append(BuyerNick).append("','")  //5
	                    .append( Formatter.format(new Date(order.getRefund_time()*1000L), Formatter.DATE_TIME_FORMAT)).append("','").append(Formatter.format(new Date(order.getAdd_time()*1000L), Formatter.DATE_TIME_FORMAT)).append("','�˻�','','BUYER_NOT_RECEIVED',")//10
	                    .append("'1','").append(order.getRefund_paid()).append("','").append(Payment).append("','','','")
	                    .append(item.getGoods_name()).append("','").append(item.getGoods_price()).append("','").append(item.getGoods_number()).append("','").append(Formatter.format(new Date(order.getRefund_time()*1000L), Formatter.DATE_TIME_FORMAT)).append("','")
	                    .append("','").append(order.getMoney_paid()).append("','").append(item.getGoods_id()).append("','','").append(item.getGoods_sn()).append("',")
	                    .append("'','").append(new String(order.getAddress().getBytes(),"gbk").replaceAll("'","''")).append("','").append(new String(order.getAddress().getBytes(),"gbk").replaceAll("'","''")).append("','").append(inshopid).append("','").append(order.getOrder_sn()).append("','").append(new String(order.getUser_name().getBytes(),"gbk")).append("','").append(order.getTel()).append("','')").toString();

					Log.info("�˻���sql: "+sql) ;
					SQLHelper.executeSQL(conn,sql);
					
					//���뵽֪ͨ��     �˻���־Ϊ2
		            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
		                + sheetid +"',2 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";				
					//Log.info(sql) ;
					SQLHelper.executeSQL(conn,sql);
					
					Log.info(jobname,"�ӿڵ���:"+sheetid+" �˻�������:"+order.getOrder_sn()+"����������ʱ��:"+Formatter.format(new Date(order.getAdd_time()*1000L),Formatter.DATE_TIME_FORMAT));
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
					throw new JSQLException("�����˻���" + order.getOrder_sn() + "���ӿ�����ʧ��!"+e1.getMessage());
				}
			}
		}
		catch (Exception e) 
		{
			Log.error(jobname, "���ɽӿ��˻���ʧ��,������:"+order.getOrder_sn() + ",�˻�������:"+",������Ϣ:"+e.getMessage()) ;
			e.printStackTrace() ;
		}
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
	/**
	 * ���ݳ��д����ȡ����
	 * @return
	 */
	public static Hashtable getCityByCode(Connection conn,String provinceCode,String citycode,String districtcode){
		String sql = new StringBuilder().append("select provinceName,cityname,districtname from sn_citycode where provinceCode='").append(provinceCode)
			.append("' and citycode='").append(citycode).append("' and districtcode='").append(districtcode).append("'").toString();
		try {
			return SQLHelper.oneRowSelect(conn, sql);
		} catch (JSQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	
	/**
	 * ��ȡ������˾����
	 * @param code
	 * @return
	 */
	public static String getExpressInfo(Connection conn,String code){
		String sql = new StringBuilder().append("select name from expressInfo where code='")
		.append(code).append("'").toString();
		try {
			return SQLHelper.strSelect(conn, sql);
		} catch (JSQLException e) {
			Log.info("��ѯ������˾�����Ӧ��������˾����!");
			return "";
		}
	}
	
	//������ɫ�����ţ������ѯsku
	public static String getItemCodeByColSizeCustom(Connection conn,String goods_no,
			String color, String size) {
		String result="";
		String sql = new StringBuilder().append("select custombc from v_barcodeAll where colorname='")
			.append(color).append("' and customno='").append(goods_no)
			.append("' and sizename='").append(size).append("'").toString();
		try{
			Log.info("��sku:��"+sql);
			result = SQLHelper.strSelect(conn, sql);
		}catch(Exception ex){
			Log.error("��ѯ��Ʒsku����", ex.getMessage());
		}
		return result;
	}




	//����orderId��ȡһ����������
	public static Order getOrderById(String vcode, String orderCode,String url) throws Exception{
		Order o = new Order();
		//������
		String apimethod="get_order_info.php";
		HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("oid", orderCode);
        map.put("apimethod", apimethod);
        map.put("key", MD5Util.getMD5Code((Params.vcode+orderCode).getBytes()));

        //��������
		String responseText = CommHelper.doPost(map,url);
		Log.info("������: "+orderCode+", ��������Ϊ: "+responseText);
		JSONObject order= new JSONObject(responseText);
		if(5==order.getInt("status")) {
			Log.info("������: "+orderCode+", ��������Ϊ��");
			return o;
		}
		o.setObjValue(o, order.getJSONObject("list"));
		o.setFieldValue(o,"goods_list",order.getJSONObject("list").getJSONArray("goods_list"));
		return o;
	}




	/**
	 * ��ȡ�����˻�������Ϣ
	 * @param string
	 * @param conn
	 * @param tradecontactid
	 * @param o
	 * @param url
	 */
	public static RefundOrder getRefundOrderByCode(String vcode, String orderCode, String url) throws Exception{
		RefundOrder o = new RefundOrder();
		//������
		String apimethod="get_refund_info.php";
		HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("oid", orderCode);
        map.put("apimethod", apimethod);
        map.put("key", MD5Util.getMD5Code((Params.vcode+orderCode).getBytes()));

        //��������
		String responseText = CommHelper.doPost(map,url);
		Log.info("�˻���������Ϊ: "+responseText);
		JSONObject order= new JSONObject(responseText);
		if(order.getInt("status")!=1){
			throw new Exception("��ȡ�˻�������������,������:��"+orderCode);
		}
		o.setObjValue(o, order.getJSONObject("list"));
		o.setFieldValue(o,"goods_list",order.getJSONObject("list").getJSONArray("goods_list"));
		return o;
		
	}

}
