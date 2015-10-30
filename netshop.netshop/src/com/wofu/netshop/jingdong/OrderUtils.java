package com.wofu.netshop.jingdong;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.order.ItemInfo;
import com.jd.open.api.sdk.domain.order.OrderInfo;
import com.jd.open.api.sdk.domain.order.OrderSearchInfo;
import com.jd.open.api.sdk.domain.order.UserInfo;
import com.jd.open.api.sdk.request.delivery.EtmsWaybillSendRequest;
import com.jd.open.api.sdk.request.delivery.EtmsWaybillcodeGetRequest;
import com.jd.open.api.sdk.request.order.OrderGetRequest;
import com.jd.open.api.sdk.request.order.OrderPrintDataGetRequest;
import com.jd.open.api.sdk.request.order.OrderSopPrintDataGetRequest;
import com.jd.open.api.sdk.request.order.OrderVenderRemarkQueryByOrderIdRequest;
import com.jd.open.api.sdk.request.ware.WareSkuGetRequest;
import com.jd.open.api.sdk.response.delivery.EtmsWaybillSendResponse;
import com.jd.open.api.sdk.response.delivery.EtmsWaybillcodeGetResponse;
import com.jd.open.api.sdk.response.order.OrderGetResponse;
import com.jd.open.api.sdk.response.order.OrderPrintDataGetResponse;
import com.jd.open.api.sdk.response.order.OrderSopPrintDataGetResponse;
import com.jd.open.api.sdk.response.ware.WareSkuGetResponse;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
public class OrderUtils 
{
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	private static long daymillis=24*60*60*1000L;
	
	//��ȡ��ˮ��
	public static String getTradeNo()
	{
		return sdf.format(new Date()) ;
	}
	

	/**
	 * ȡ����������
	 * @param conn
	 * @param o
	 * @param tradecontactid
	 * @throws NumberFormatException
	 * @throws JException
	 */
	private static boolean cancelOrder(Connection conn,String orderID ,String tradecontactid) throws NumberFormatException, JException
	{
		if(refundExist(conn, orderID))
		{
			return true;
		}
		
		String sql="declare @ret int;  execute  @ret = IF_CancelCustomerOrder '" + orderID + "';select @ret ret;";
		try
		{
			//����:0�ɹ�����1������2�Ѿ����⣬���ܳ�����3�����ڻ���ȡ��
			int result = SQLHelper.intSelect(conn, sql) ;
			//ֱ��ȡ���ɹ����ߵ��ֿ�ص�
			if(result == 0 || result == 1)
			{
				Log.info("ȡ�����������ɹ������ţ�" + orderID) ;
				return true ;
			}
			//�Ѿ����⣬���ܳ���
			else if(result == 2)
			{
				Log.info("Ҫȡ�����Ķ������Ѿ����⣬���ţ�" + orderID) ;
				return false ;
			}
			//�����ڻ���ȡ��
			else if(result == 3)
			{
				Log.info("Ҫȡ���ľ������������ڻ���ȡ�������ţ�" + orderID) ;
				return true ;
			}
			else
			{
				Log.info("ȡ�����������������ţ�" + orderID) ;
				return false ;
			}
		} 
		catch (Exception e) 
		{
			//e.printStackTrace();
			Log.error("ȡ����������ʧ�ܣ�", "���ţ�" + orderID + "��������Ϣ��" + e.getMessage()) ;
			return false ;
		}
	}
		
	/**
	 * ��鷵�����Ƿ����
	 * @param conn
	 * @param orderID
	 * @return	���ڣ�����true	�����ڣ�����false
	 */
	private static boolean refundExist(Connection conn, String orderID)
	{
		try 
		{
			String sql = "select count(*) from refund where tid='" + orderID + "'" ;
			return SQLHelper.intSelect(conn, sql) != 0 ;
		} catch (Exception e) {
			// TODO: handle exception
			return false ;
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
	
	public static OrderInfo getFullTrade(String orderID,String SERVER_URL,String token,String appKey,String appSecret) throws Exception
	{
		DefaultJdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
		OrderGetRequest request = new OrderGetRequest();
		request.setOrderId(orderID);
		//�����̼ұ�ע
		//request.setOptionalFields("order_id,modified,order_state,vender_remark");
		OrderGetResponse response=client.execute(request);
		OrderInfo order = response.getOrderDetailInfo().getOrderInfo() ;
		
		return order;
	}
	
	/**
	 * �����ӿڶ��� V2
	 * @param conn
	 * @param o					��Ҫ�����ӿڶ����ľ�������
	 * @param tradecontactid	�ӿڵ��̴���
	 * @param username			sellernick
	 * @return					�����ɹ�����true�����򷵻�false
	 * @throws SQLException 
	 * @throws JException 
	 */
	public static String createInterOrder(Connection conn,String SERVER_URL,String appKey,
			String appSecret,String token,OrderInfo o, String tradecontactid,String username,  //OrderInfo o,
			String JBDCustomerCode,boolean isLBP,boolean isNeedGetDeliverysheetid) throws Exception
	{		
		try 
		{
			/**
			 *  "pay_type": "1-��������", 
			 * 	"pay_type": "2-�ʾֻ��", 
			 *  "pay_type": "3-����", 
			 *  "pay_type": "4-����֧��", 
			 *  "pay_type": "5-��˾ת��",
			 *  "pay_type": "6-���п�ת��", 
			 */
			Log.info("�����ܽ��: "+o.getOrderTotalPrice());
			Log.info("�û�Ӧ�����: "+o.getOrderPayment());
			Log.info("����������: "+o.getOrderSellerPrice());
			Log.info("�ʷѽ��: "+o.getFreightPrice());
			String pay_type = o.getPayType()!=null?o.getPayType():"8";//pay_type String ��   �ӿ�ҵ��ģʽ��0������1����֧����2�������3�Զ�������4����
			String paytime=Formatter.format(o.getOrderStartTime()!=null?o.getOrderStartTime():new Date(), Formatter.DATE_TIME_FORMAT);
			if(pay_type.indexOf("1") == 0)
				pay_type = "2" ;//��������
			else if(pay_type.indexOf("3") == 0)
				pay_type = "0" ;//����
			else {
				pay_type = "1" ;//����֧��
				paytime=Formatter.format(o.getModified()!=null?o.getModified():new Date(), Formatter.DATE_TIME_FORMAT);
			}
			String delivery="";
			String deliverysheetid="";
			if (pay_type.equals("2") && !JBDCustomerCode.equals("")){
				delivery="JDKD";
				if(isNeedGetDeliverysheetid){
					deliverysheetid=getJDPostNo(JBDCustomerCode,SERVER_URL,token,appKey,appSecret);
					if(deliverysheetid.equals("")){
						Log.error("���ɻ��������ʧ��","ȡ�˵��ų���,������:��"+o.getOrderId());
						throw new Exception("���ɻ��������ʧ��,ȡ�˵��ų���,�˳��˴δ�������!");
					}
					//������ϵͳ�ύ���� ��һ�������������
					if(!waybillSend(deliverysheetid,JBDCustomerCode,SERVER_URL,token,appKey,appSecret,o)){
						Log.error("���ɻ��������ʧ��","������ϵͳ�ύ����ʧ��,������:��"+o.getOrderId()+",�˵��ţ� "+deliverysheetid);
						throw new Exception("������ϵͳ�ύ����ʧ��,�˳��˴δ�������!");
					}
				}
				
			}
			String invoice_info = o.getInvoiceInfo()!=null?o.getInvoiceInfo():"����Ҫ���߷�Ʊ" ;//invoice_info String ��   ��Ʊ��Ϣ 
			int needInvoice ;//�Ƿ��跢Ʊ��1��Ҫ��0����Ҫ,needinvoice ����ֵ
			if("����Ҫ���߷�Ʊ".equals(invoice_info))
				needInvoice = 0 ;
			else
			{
				needInvoice = 1 ;
				invoice_info = invoice_info.substring((invoice_info.indexOf("��Ʊ̧ͷ:")+5),invoice_info.indexOf(";��Ʊ����")) ;
				if("����".equals(invoice_info))
					invoice_info = o.getConsigneeInfo().getFullname() ;
			}
			//�ջ��˻�����Ϣ
			UserInfo user = o.getConsigneeInfo();
			if(user!=null){
				//��������Ϣ��ֹ������ȥ��()���������
				if(user.getCounty().indexOf("(") > 0)
					user.setCounty(user.getCounty().substring(0, user.getCounty().indexOf("("))) ;
				if(user.getCounty().indexOf("��") > 0)
					user.setCounty(user.getCounty().substring(0, user.getCounty().indexOf("��"))) ;
				if(user.getCounty().indexOf(",") > 0)
					user.setCounty(user.getCounty().substring(0, user.getCounty().indexOf(","))) ;
				//�����ַ��Ϣ����ֹ������ȥ������
				String address = user.getFullAddress() ;
				String shortAddress  = address.substring(0,5) ;
				int sortAddressLastIndex = address.lastIndexOf(shortAddress) ;
				String subAddress = address.substring(sortAddressLastIndex, address.length()) ;
				user.setFullAddress(subAddress) ;
			}
			
			float totalPrice = Float.parseFloat(o.getOrderTotalPrice()!=null?o.getOrderTotalPrice():"0.0") ;//�����ܽ��
			float sellerDiscount = Float.parseFloat(o.getSellerDiscount()!=null?o.getSellerDiscount():"0.0") ;//�̼��Żݽ��
			float paymentPercent = 1-(sellerDiscount/totalPrice) ;   //�û�Ӧ���ٷֱ�
			float discountFee = 0f ;
			float countPayment = 0f ;
			BigDecimal b1,b2 ;
			conn.setAutoCommit(false);
			
			String sheetid="";
			String sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";
			
			sheetid=SQLHelper.strSelect(conn, sql);
			if (sheetid.trim().equals(""))
				throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");
			if( o.getFreightPrice() == null ||"".equals(o.getFreightPrice()))
				o.setFreightPrice("0") ;
			float postFee = Float.parseFloat(o.getFreightPrice()!=null?o.getFreightPrice():"0.00") ;
			//ȥ����ע�е�"'"��
			String remark = o.getOrderRemark()!=null?o.getOrderRemark():"";
			remark=remark.indexOf("'")!=-1?remark.replaceAll("'", ","):remark;
			String vender_remark=getVenderRemark(SERVER_URL,token,appKey,appSecret,Long.parseLong(o.getOrderId()));
			vender_remark =vender_remark.indexOf("'")!=-1?vender_remark.replaceAll("'", ","):vender_remark;
			Log.info("�̼ұ�עΪ: "+vender_remark);
			float priceTemp=Float.parseFloat(o.getOrderSellerPrice()!=null?o.getOrderSellerPrice():"0.0")+Float.parseFloat(o.getFreightPrice()!=null?o.getFreightPrice():"0.0");
			//���뵽���ݱ�
			sql =  new StringBuilder().append("insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , ")
            	.append( " type , created , buyermessage , shippingtype , payment , ")  //11
				.append( " discountfee , adjustfee , status , buyermemo , sellermemo , ")  //16
				.append( " tradememo , paytime , endtime , modified ,buyerobtainpointfee , ")  //20
				.append(" pointfee , realpointfee , totalfee , postfee , buyeralipayno , ")
				.append(" buyernick ,buyerUin, receivername , receiverstate , receivercity , receiverdistrict , ")
				.append(" receiveraddress , receiverzip , receivermobile , receiverphone ,  ")
				.append( " delivery , deliverySheetID , price , tradefrom,alipayurl,")
				.append(" PromotionDetails,TradeContactID,paymode,InvoiceFlag,invoicetitle,payfee) values('") 
                .append( sheetid).append("','").append(sheetid).append("','yongjun','").append(o.getOrderId()).append("','','").append( username).append("','','")
                .append(Formatter.format(o.getOrderStartTime()!=null?o.getOrderStartTime():new Date(), Formatter.DATE_TIME_FORMAT)).append("','").append(remark).append("','','")//10
                .append(String.valueOf(priceTemp)).append( "','")//11  ԭ��o.getOrderPayment() ����ֻȡ�����ܽ��-�̼��Żݽ��
                .append(sellerDiscount).append("','0.0','").append(o.getOrderState()).append("','").append(remark).append("','").append(vender_remark).append("','','") //16        20
                .append(paytime).append("','','").append(o.getModified()).append("','','','','")
                .append(o.getOrderTotalPrice()!=null?o.getOrderTotalPrice():"0.0").append("','").append(o.getFreightPrice()).append("','','")
                .append(user!=null?user.getFullname():"").append("','','").append(user!=null?user.getFullname():"").append("','").append(user!=null?user.getProvince():"").append("','").append(user!=null?user.getCity():"").append("','").append(user!=null?user.getCounty():"").append("','")
                .append(user!=null?user.getFullAddress().replace("'", ""):"").append("','','").append(user!=null?user.getMobile():"").append("','").append(user!=null?user.getTelephone():"").append("','")
            	.append(delivery).append("','").append(deliverysheetid).append("','")
            	.append(o.getOrderTotalPrice()!=null?o.getOrderTotalPrice():"0.0").append("','360buy','','','").append(tradecontactid).append("','").append(pay_type).append("','").append(needInvoice).append("','").append(invoice_info!=null?invoice_info:"").append("','").append(o.getOrderPayment()!=null?o.getOrderPayment():"").append("')").toString();
			SQLHelper.executeSQL(conn, sql);
        	//������Ʒ�б�
			ArrayList<ItemInfo> itemList = (ArrayList<ItemInfo>) o.getItemInfoList() ;
			if(itemList!=null){
				for(int i = 0 ; i < itemList.size() ; i ++)
				{
					com.jd.open.api.sdk.domain.order.ItemInfo item = (com.jd.open.api.sdk.domain.order.ItemInfo) itemList.get(i) ;
					//�����Ʒ�е��ܼ�
					float totalFee = Float.parseFloat(item.getJdPrice()) * Integer.parseInt(item.getItemTotal()) ;
					float payment = 0f ;
					if(i==(itemList.size()-1))
					{
						payment = Float.parseFloat(decimalFormat.format(totalFee-countPayment)) ;
						b1 = new BigDecimal(Float.toString(totalPrice-sellerDiscount)) ;
						b2 = new BigDecimal(Float.toString(countPayment)) ;
						payment = b1.subtract(b2).floatValue() ;

						b1 = new BigDecimal(Float.toString(totalFee)) ;
						b2 = new BigDecimal(Float.toString(payment)) ;
						discountFee = b1.subtract(b2).floatValue() ;
					}
					else
					{
						payment = Float.parseFloat(decimalFormat.format(totalFee*paymentPercent)) ;//�ܼ�x�û�Ӧ���ٷֱ�
						countPayment += payment ;
						discountFee = totalFee - payment ;
					}
					
					if(payment==0.00001f||payment==-0.00001 || Math.abs(payment)<0.001f){
						payment=0;
					}
					if(discountFee==0.00001f||discountFee==-0.00001 ||  Math.abs(discountFee)<0.001f){
						discountFee=0;
					}
					sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , "   //5
	                    + " title , sellernick , buyernick , type , created , "   //10
	                    + " refundstatus , outeriid , outerskuid , totalfee , payment , "   //15
	                    + " discountfee , adjustfee , status , timeoutactiontime , owner , " 
	                    + " iid , skuPropertiesName , num , price , picPath , " 
	                    + " oid , snapShotUrl , snapShot ,modified) values( "
	                    + "'" + sheetid + "','" + sheetid+"-"+o.getOrderId() + String.valueOf(i+1) + "','" + sheetid + "','" + item.getSkuId() + "','" + item.getSkuName() + "',"  //5
	                    + "'" + item.getSkuName() + "','" + username + "','" + user.getFullname() + "','','" + Formatter.format(o.getOrderStartTime(), Formatter.DATE_TIME_FORMAT) + "',"  //10
	                    + "'','','" + getOuterSkuId(item.getSkuId(),SERVER_URL,token,appKey,appSecret,tradecontactid,conn) + "','" + totalFee + "','" + payment + "'," 
	                    + "'" + discountFee + "','','"+o.getOrderState()+"','','yongjun',"
	                    + "'','','" + item.getItemTotal() + "','" + item.getJdPrice() + "','',"
	                    + "'','','','')" ;
	        		SQLHelper.executeSQL(conn, sql) ;
					
				}
			}
			
			
			if (isLBP)  //�����lbp������ȡlbp��Ϣ
			{
				DefaultJdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
				OrderPrintDataGetRequest request = new OrderPrintDataGetRequest();
				request.setOrderId(o.getOrderId());
				OrderPrintDataGetResponse response=client.execute(request);
				
				int is_notice_before_delivery=0;
				String lbpdc=response.getApiOrderPrintData().getCky2Name();
				String lbppartner=response.getApiOrderPrintData().getPartner();
				if(response.getApiOrderPrintData().getBfDeliGoodGlag().equals("��"))
					is_notice_before_delivery=1;
				String out_bound_date =response.getApiOrderPrintData().getCodTimeName();
				
				if (lbpdc.equals("") || lbpdc ==null) throw new JException("LBP��ϢΪ��,�����ţ�"+o.getOrderId());
				
				sql="update ns_customerorder set lbpdc='"+lbpdc+"',lbppartner='"+lbppartner+"',"
					+"is_notice_before_delivery="+is_notice_before_delivery+",out_bound_date='"+out_bound_date+"' "
					+"where sheetid='"+sheetid+"'";
				SQLHelper.executeSQL(conn,sql);
					
			
				sql="select count(*) from ns_customerorder where sheetid='"+sheetid+"' and lbpdc is null";
				if (SQLHelper.intSelect(conn, sql)>0)
					throw new JException("LBP��ϢΪ��,�����ţ�"+o.getOrderId());
			}
			
			if (pay_type.equals("2") && !JBDCustomerCode.equals("")){	
				JdClient JDBclient = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
				OrderSopPrintDataGetRequest  JDBRequest = new OrderSopPrintDataGetRequest ();
				JDBRequest.setOrderId(o.getOrderId());
				OrderSopPrintDataGetResponse JDBResponse = JDBclient.execute(JDBRequest);
				
				
				int is_notice_before_delivery=0;
				String lbpdc=JDBResponse.getApiOrderPrintData().getCky2Name();
				String lbppartner=JDBResponse.getApiOrderPrintData().getPartner();
				
				if(JDBResponse.getApiOrderPrintData().getBfDeliGoodGlag().equals("��"))
					is_notice_before_delivery=1;
				String out_bound_date =JDBResponse.getApiOrderPrintData().getCodTimeName();
			
				
				sql="update ns_customerorder set lbpdc='"+lbpdc+"',lbppartner='"+lbppartner+"',"
					+"is_notice_before_delivery="+is_notice_before_delivery+",out_bound_date='"+out_bound_date+"' "
					+"where sheetid='"+sheetid+"'";
				SQLHelper.executeSQL(conn,sql);
			}
			
		
			 //���뵽֪ͨ��
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                + sheetid +"',1 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);

			conn.commit();
			conn.setAutoCommit(true);
			Log.info("���ɶ�����" + o.getOrderId() + "���ӿ����ݳɹ����ӿڵ��š�" + sheetid + "��");
			
			return sheetid;

		} 
		catch (Exception e) 
		{
			e.printStackTrace() ;
			if (!conn.getAutoCommit())
				try
				{
					conn.rollback();
				}
				catch (Exception e1) { }
			try
			{
				conn.setAutoCommit(true);

			}
			catch (Exception e2) { }
			throw new JException("���ɶ�����" + o.getOrderId() + "���ӿ�����ʧ��!"+e.getMessage());
		}
	}
	
	private static String getJDPostNo(String JBDCustomerCode,String SERVER_URL,String token,String appKey,String appSecret) throws Exception
	{
		String result="";
		while(result.equals("") || result==null){
			JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
			EtmsWaybillcodeGetRequest  request = new EtmsWaybillcodeGetRequest ();
			request.setPreNum("1");
			request.setCustomerCode(JBDCustomerCode);
			EtmsWaybillcodeGetResponse response = client.execute(request);

			//״̬��
			result = response.getResultInfo().getDeliveryIdList().get(0);
		}
		
		
		return result;
	}
	
	private static void sendJDPostNo(String JBDCustomerCode,String postno,OrderInfo o,
			String SERVER_URL,String token,String appKey,String appSecret) throws Exception
	{
		JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
		EtmsWaybillSendRequest  request = new EtmsWaybillSendRequest  ();
		request.setCustomerCode(JBDCustomerCode);
		request.setDeliveryId(postno);
		request.setSalePlat("jingdong");
		request.setOrderId(o.getOrderId());
		request.setSelfPrintWayBill(1);
		request.setSenderName(Params.linkman);
		request.setSenderAddress(Params.address);
		request.setSenderTel(Params.phone);
		request.setReceiveName(o.getConsigneeInfo().getFullname());
		request.setReceiveAddress(o.getConsigneeInfo().getFullAddress());
		request.setReceiveMobile(o.getConsigneeInfo().getMobile());
		request.setPackageCount(1);
		request.setWeight(2.5);
		request.setVloumn(1.0);
		
		EtmsWaybillSendResponse response = client.execute(request);

		//״̬��
		String code = response.getCode();
		
		
		if("0".equals(code))
		{
			Log.info("�ύ����������Ϣ�ɹ����������š�" + o.getOrderId() + "������ݵ��š�" + postno + "��") ;
		}
		else
		{
			
			throw new JException("�ύ����������Ϣʧ�ܣ��������š�" + o.getOrderId() + "������ݵ��š�" + postno + "����������Ϣ��" + code + "," + response.getZhDesc()) ;
		}
	
	}
	
	
	//�򾩶�����ϵͳ�ύ�˵���Ϣ  ������������Ķ��������ĵڶ�����������
	/**
	 * �˵���      deliveryId 
	 * ����ƽ̨���� salePlat =0010001
	 * �̼ҵ��̱��� customerCode
	 * �̼Ҷ�����  orderId 
      �ļ�������  senderName 
      �ļ��˵�ַ senderAddress 
      �ռ������� receiveName
      �ֻ��˵�ַ receiveAddress
      �������� PackageCount 1
      ����    Weight 1
     �������  vloumn  1 
     ���ս�collectionValue 1
     ���ջ�����  collectionMoney
	 */
	public static Boolean waybillSend(String deliveryId ,String customerCode,
			String SERVER_URL,String accessToken,String appKey,String appSecret,OrderInfo o){
		Boolean isSuccess=false;
		double collectionMoney=0.00f;
		try{
			collectionMoney=getCollectionMoney(o.getOrderId(),SERVER_URL,accessToken,appKey,appSecret);
			//Log.info("Ӧ�����Ϊ:��"+collectionMoney);
			//Hashtable receiverInfo = getReceiverInfo(thrOrderId,conn);
			JdClient client=new DefaultJdClient(SERVER_URL,accessToken,appKey,appSecret);
			EtmsWaybillSendRequest request=new EtmsWaybillSendRequest();
			request.setDeliveryId(deliveryId);
			request.setSalePlat("0010001");
			request.setCustomerCode(customerCode);
			request.setCollectionValue(1);
			request.setCollectionMoney(collectionMoney);
			request.setOrderId(o.getOrderId());
			request.setThrOrderId(o.getOrderId());
			request.setSenderName(Params.username);
			request.setSenderAddress(Params.address);
			request.setSenderMobile(Params.phone);
			request.setReceiveName(o.getConsigneeInfo().getFullname());
			request.setReceiveAddress(o.getConsigneeInfo().getFullAddress());
			request.setReceiveMobile(o.getConsigneeInfo().getMobile());
			request.setPackageCount (1);
			request.setWeight(1.0);
			request.setVloumn(1000.0);
			EtmsWaybillSendResponse response=client.execute(request);
			isSuccess =  response.getResultInfo().getCode().equalsIgnoreCase("100");
			Log.info("������: "+o.getOrderId()+",��ݵ���: "+deliveryId+",������Ϣ: "+response.getMsg());
			Log.info(response.getResultInfo().getCode());
		}catch(Exception ex){
			Log.error("�򾩶�����ϵͳ�ύ�˵���Ϣ����,������:��"+o.getOrderId()+",������Ϣ: ", ex.getMessage());
		}
		return isSuccess;
	}
	
	public static double getCollectionMoney(String orderId,String SERVER_URL,String token,String appKey,String appSecret) throws Exception{
		double collectionMoney=0.00f;
		try{
			JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
			OrderPrintDataGetRequest request = new OrderPrintDataGetRequest();
			request.setOrderId(orderId);
			OrderPrintDataGetResponse response = client.execute(request);
			if(response.getCode().equals("0")){
				collectionMoney=Double.valueOf(response.getApiOrderPrintData().getShouldPay());
			}
			Log.info("������: "+orderId+" ,��ȡ��������Ӧ��������Ϣ: "+response.getMsg());
		}catch(Exception ex){
			Log.error("ȡ��������Ӧ�ս�����!", ex.getMessage());
		}
		return collectionMoney;
		
	}
	
	//���ݾ���skui��ȡ�̼�skuid
	private static String getOuterSkuId(String skuId,String SERVER_URL,String token,String appkey,String appSecret,String tradecontactid,Connection conn) throws Exception{
				String result="";
				JdClient client = new DefaultJdClient(SERVER_URL,token,appkey,appSecret);
				WareSkuGetRequest wareSkuGetRequest = new WareSkuGetRequest();

				wareSkuGetRequest.setSkuId(skuId);

				wareSkuGetRequest.setFields("outer_id");

				WareSkuGetResponse res = client.execute(wareSkuGetRequest);
				if(res.getCode().equals("0")){
					if(res.getSku()!=null)
					result = res.getSku().getOuterId();
				}
				if(result.equals("")){  //�Ӿ����ӿ�û�����ݷ���ʱֱ��ȡecs_stockconfigsku�������
					String sql = new StringBuilder().append("select orgid from ecs_tradecontactorgcontrast where tradecontactid=")
						.append(tradecontactid).toString();
					int orgid= SQLHelper.intSelect(conn, sql);
					sql = new StringBuilder().append("select sku from ecs_stockconfigsku where orgid=")
						.append(orgid).append(" and skuid='").append(skuId).append("'").toString();
					result = SQLHelper.strSelect(conn, sql);
				}
				return result!=null?result:"";
	}
	
	//��ѯ�̼ұ�ע
	private static String getVenderRemark(String SERVER_URL,String token,String appkey,String appSecret,Long orderId) throws Exception{
		String VenderRemark = "";
		try{
			JdClient client=new DefaultJdClient(SERVER_URL,token,appkey,appSecret); 
			OrderVenderRemarkQueryByOrderIdRequest request=new OrderVenderRemarkQueryByOrderIdRequest();
			request.setOrderId(orderId);
			com.jd.open.api.sdk.response.order.OrderVenderRemarkQueryByOrderIdResponse response=client.execute(request);
			if(response.getVenderRemarkQueryResult().getApiJosResult().getSuccess())
			VenderRemark = response.getVenderRemarkQueryResult().getVenderRemark().getRemark();
		}catch(Exception ex){
			Log.error("��ȡ�̼ұ�עʧ��", ex.getMessage());
			ex.printStackTrace();
			//throw new Exception("��ȡ�̼ұ�עʧ��");
			
		}
		
		return VenderRemark;
	}
	
	
	
}
