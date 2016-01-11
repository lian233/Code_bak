package com.wofu.netshop.jingdong.fenxiao;

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
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.Types;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.jingdong.fenxiao.Params;
public class OrderUtils 
{	
	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	private static long daymillis=24*60*60*1000L;
	
	//获取流水号
	public static String getTradeNo()
	{
		return sdf.format(new Date()) ;
	}
	

	/**
	 * 取消京东订单
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
			//返回:0成功处理，1撤单，2已经出库，不能撤单，3不存在或已取消
			int result = SQLHelper.intSelect(conn, sql) ;
			//直接取消成功或者到仓库截单
			if(result == 0 || result == 1)
			{
				Log.info("取消京东订单成功，单号：" + orderID) ;
				return true ;
			}
			//已经出库，不能撤单
			else if(result == 2)
			{
				Log.info("要取消京的东订单已经出库，单号：" + orderID) ;
				return false ;
			}
			//不存在或已取消
			else if(result == 3)
			{
				Log.info("要取消的京东订单不存在或已取消，单号：" + orderID) ;
				return true ;
			}
			else
			{
				Log.info("取消京东订单出错，单号：" + orderID) ;
				return false ;
			}
		} 
		catch (Exception e) 
		{
			//e.printStackTrace();
			Log.error("取消京东订单失败！", "单号：" + orderID + "。错误信息：" + e.getMessage()) ;
			return false ;
		}
	}
		
	/**
	 * 检查返货单是否存在
	 * @param conn
	 * @param orderID
	 * @return	存在：返回true	不存在：返回false
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
	//获取发票明细信息
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
			Log.error(jobname, "获取商品款号名称失败,错误信息:"+e.getMessage()) ;
			detail = "" ;
			e.printStackTrace() ;
		}
		
		return detail ;
	}
	//获取发票单位
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
			Log.error(jobname, "获取商品单位失败,错误信息:"+e.getMessage()+",sku:"+sku) ;
		}
		return unitName ;
	}
	
	public static OrderInfo getFullTrade(String orderID,String SERVER_URL,String token,String appKey,String appSecret) throws Exception
	{
		DefaultJdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
		OrderGetRequest request = new OrderGetRequest();
		request.setOrderId(orderID);
		//返回商家备注
		//request.setOptionalFields("order_id,modified,order_state,vender_remark");
		OrderGetResponse response=client.execute(request);
		OrderInfo order = response.getOrderDetailInfo().getOrderInfo() ;
		
		return order;
	}
	
	/** 
	 * 创建接口订单 V2
	 * @param conn
	 * @param o					需要创建接口订单的京东订单
	 * @param tradecontactid	接口店铺代码
	 * @param username			sellernick
	 * @return					创建成功返回true，否则返回false
	 * @throws SQLException 
	 * @throws JException 
	 */
	public static void createInterOrder(Connection conn,String SERVER_URL,String appKey,
			String appSecret,String token,OrderInfo o, int shopid,String username,  //OrderInfo o,
			boolean isLBP,boolean isNeedGetDeliverysheetid) throws Exception
	{		
		try 
		{
			/**
			 *  "pay_type": "1-货到付款", 
			 * 	"pay_type": "2-邮局汇款", 
			 *  "pay_type": "3-自提", 
			 *  "pay_type": "4-在线支付", 
			 *  "pay_type": "5-公司转帐",
			 *  "pay_type": "6-银行卡转帐", 
			 */
			String jdsql="SELECT code FROM jdcod where shopid= '"+shopid+"'";
			String JBDCustomerCode = SQLHelper.strSelect(conn, jdsql);
			Log.info("订单总金额: "+o.getOrderTotalPrice());
			Log.info("用户应付金额: "+o.getOrderPayment());
			Log.info("订单货款金额: "+o.getOrderSellerPrice());
			Log.info("邮费金额: "+o.getFreightPrice());
			String pay_type = o.getPayType()!=null?o.getPayType():"8";//pay_type String 否   接口业务模式，0其它，1在线支付，2货到付款，3自动发货，4分销
			String paytime=Formatter.format(o.getOrderStartTime()!=null?o.getOrderStartTime():new Date(), Formatter.DATE_TIME_FORMAT);
			if(pay_type.indexOf("1") == 0)
				pay_type = "2" ;//货到付款
			else if(pay_type.indexOf("3") == 0)
				pay_type = "0" ;//其它
			else {
				pay_type = "1" ;//在线支付
				paytime=Formatter.format(o.getModified()!=null?o.getModified():new Date(), Formatter.DATE_TIME_FORMAT);
			}
			String delivery="";
			String deliverysheetid="";
			if (pay_type.equals("2") && !JBDCustomerCode.equals("")){
				delivery="JDKD";
				if(isNeedGetDeliverysheetid){
					deliverysheetid=getJDPostNo(JBDCustomerCode,SERVER_URL,token,appKey,appSecret);
					if(deliverysheetid.equals("")){
						Log.error("生成货到付款订单失败","取运单号出错,订单号:　"+o.getOrderId());
						throw new Exception("生成货到付款订单失败,取运单号出错,退出此次创建订单!");
					}
					//向青龙系统提交资料 这一步在这里中完成
					if(!waybillSend(deliverysheetid,JBDCustomerCode,SERVER_URL,token,appKey,appSecret,o)){
						Log.error("生成货到付款订单失败","向青龙系统提交资料失败,订单号:　"+o.getOrderId()+",运单号： "+deliverysheetid);
						throw new Exception("向青龙系统提交资料失败,退出此次创建订单!");
					}
				}
				
			}
			String invoice_info = o.getInvoiceInfo()!=null?o.getInvoiceInfo():"不需要开具发票" ;//invoice_info String 否   发票信息 
			int needInvoice ;//是否需发票：1需要，0不需要,needinvoice 返回值
			if("不需要开具发票".equals(invoice_info))
				needInvoice = 0 ;
			else
			{
				needInvoice = 1 ;
				invoice_info = invoice_info.substring((invoice_info.indexOf("发票抬头:")+5),invoice_info.indexOf(";发票内容")) ;
				if("个人".equals(invoice_info))
					invoice_info = o.getConsigneeInfo().getFullname() ;
			}
			//收货人基本信息
			UserInfo user = o.getConsigneeInfo();
			if(user!=null){
				//处理区信息防止过长，去年()里面的内容
				if(user.getCounty().indexOf("(") > 0)
					user.setCounty(user.getCounty().substring(0, user.getCounty().indexOf("("))) ;
				if(user.getCounty().indexOf("（") > 0)
					user.setCounty(user.getCounty().substring(0, user.getCounty().indexOf("（"))) ;
				if(user.getCounty().indexOf(",") > 0)
					user.setCounty(user.getCounty().substring(0, user.getCounty().indexOf(","))) ;
				//处理地址信息，防止过长，去掉冗余
				String address = user.getFullAddress() ;
				String shortAddress  = address.substring(0,5) ;
				int sortAddressLastIndex = address.lastIndexOf(shortAddress) ;
				String subAddress = address.substring(sortAddressLastIndex, address.length()) ;
				user.setFullAddress(subAddress) ;
			}
			
			float totalPrice = Float.parseFloat(o.getOrderTotalPrice()!=null?o.getOrderTotalPrice():"0.0") ;//订单总金额
			float sellerDiscount = Float.parseFloat(o.getSellerDiscount()!=null?o.getSellerDiscount():"0.0") ;//商家优惠金额
			float paymentPercent = 1-(sellerDiscount/totalPrice) ;   //用户应付百分比
			float discountFee = 0f ;
			float countPayment = 0f ;
			BigDecimal b1,b2 ;
			conn.setAutoCommit(false);
			
			int sheetid;
			String sql="declare @Value int;exec TL_GetNewSerial_new 100001,@value output;select @value;";
			
			sheetid=SQLHelper.intSelect(conn, sql);
			if (sheetid==0)
				throw new JSQLException(sql,"取接口单号出错!");
			if( o.getFreightPrice() == null ||"".equals(o.getFreightPrice()))
				o.setFreightPrice("0") ;
			float postFee = Float.parseFloat(o.getFreightPrice()!=null?o.getFreightPrice():"0.00") ;
			//去掉备注中的"'"号
			String remark = o.getOrderRemark()!=null?o.getOrderRemark():"";
			remark=remark.indexOf("'")!=-1?remark.replaceAll("'", ","):remark;
			String vender_remark=getVenderRemark(SERVER_URL,token,appKey,appSecret,Long.parseLong(o.getOrderId()));
			vender_remark =vender_remark.indexOf("'")!=-1?vender_remark.replaceAll("'", ","):vender_remark;
			Log.info("商家备注为: "+vender_remark);
			float priceTemp=Float.parseFloat(o.getOrderSellerPrice()!=null?o.getOrderSellerPrice():"0.0")+Float.parseFloat(o.getFreightPrice()!=null?o.getFreightPrice():"0.0");
			//加入到单据表
			sql =new StringBuilder().append("insert into itf_DecOrder")
			.append("(ID , shopid , tid , sellernick  , type , ")
			.append(" CreateTime , created , buyermessage , shippingtype , payment , ")
			.append(" discountfee , adjustfee , status , buyermemo , sellermemo , ")
			.append(" tradememo , paytime , endtime , modified ,buyerobtainpointfee , ")
			.append(" pointfee , realpointfee , totalfee , postfee , buyeralipayno , ")
			.append(" buyernick , receivername , receiverstate , receivercity , receiverdistrict , ")
			.append(" receiveraddress , receiverzip , receivermobile , receiverphone , consigntime , ")
			.append(" buyeremail , haspostFee , receivedpayment , codstatus,delivery,")
			.append(" alipayNo , buyerflag , sellerflag,brandsaleflag,dealRateState,")
			.append("InvoiceFlag,invoicetitle,Prepay,")
			.append(" sellerrate , buyerrate , promotion , tradefrom , alipayurl , ")
			.append(" PromotionDetails,paymode)")
			.append(" values(")
			.append(sheetid).append(",").append(shopid).append(",'").append(o.getOrderId())
			.append("','").append(username).append("','")
			.append("','").append(Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)).append("','")
			.append(Formatter.format(o.getOrderStartTime()!=null?o.getOrderStartTime():new Date(), Formatter.DATE_TIME_FORMAT)).append("','")
			.append(remark).append("','")
			.append("','").append(String.valueOf(priceTemp)).append("','")
			.append(sellerDiscount).append("','0.0','").append(20)
			.append("','").append(remark).append("','").append(vender_remark).append("','")
			.append("','").append(paytime).append("','").append(o.getModified())
			.append("','','','','','")
			.append(o.getOrderTotalPrice()!=null?o.getOrderTotalPrice():"0.0").append("','")
			.append(o.getFreightPrice()).append("','")
			.append("','").append(user!=null?user.getFullname():"")
			.append("','").append(user!=null?user.getFullname():"")
			.append("','").append(user!=null?user.getProvince():"")
			.append("','").append(user!=null?user.getCity():"")
			.append("','").append(user!=null?user.getCounty():"")
			.append("','").append(user!=null?user.getFullAddress().replace("'", ""):"")
			.append("','")
			.append("','").append(user!=null?user.getMobile():"")
			.append("','").append(user!=null?user.getTelephone():"")
			.append("','").append(o.getModified())
			.append("','")
			.append("','")
			.append("','").append(String.valueOf(priceTemp))
			.append("','0','")
			.append(delivery)
			.append("','")//cod状态和快递现在留空
			.append("','")
			.append("','")
			.append("','','',")//dealRateState订单评价状态暂时为空
			.append("0,'','','")//InvoiceFlag=0   invoicetitle="";  Prepay=''
			.append("','")
			.append("','")
			.append("','").append("360buy")
			.append("','")
			.append("','")
			.append("','").append(pay_type).append("')").toString();
			SQLHelper.executeSQL(conn, sql) ;
        	//订单商品列表
			ArrayList<ItemInfo> itemList = (ArrayList<ItemInfo>) o.getItemInfoList() ;
			if(itemList!=null){
				for(int i = 0 ; i < itemList.size() ; i ++)
				{
					com.jd.open.api.sdk.domain.order.ItemInfo item = (com.jd.open.api.sdk.domain.order.ItemInfo) itemList.get(i) ;
					//这个商品中的总价
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
						payment = Float.parseFloat(decimalFormat.format(totalFee*paymentPercent)) ;//总价x用户应付百分比
						countPayment += payment ;
						discountFee = totalFee - payment ;
					}
					
					if(payment==0.00001f||payment==-0.00001 || Math.abs(payment)<0.001f){
						payment=0;
					}
					if(discountFee==0.00001f||discountFee==-0.00001 ||  Math.abs(discountFee)<0.001f){
						discountFee=0;
					}
					sql="declare @Value integer;exec TL_GetNewSerial_new '100002',@value output;select @value;";
					int subid = SQLHelper.intSelect(conn, sql);
					sql = new StringBuilder("insert into itf_DecOrderItem(ID , ParentID  , skuid , itemmealname , title , ")
					.append(" sellernick , buyernick , type , created , refundstatus , ")
					.append(" outeriid , outerskuid , totalfee , payment , discountfee , ")
					.append(" adjustfee , status , timeoutactiontime  ,")
					.append(" iid , skuPropertiesName , num , price , ")
					.append(" picPath , oid , snapShotUrl , ")
					.append(" snapShot ) values( ")
					.append(subid).append(",").append(sheetid).append(",'")
					.append(item.getSkuId()).append("','").append(item.getSkuName() )
					.append("','").append(item.getSkuName())
					.append("','").append(username)
					.append("','").append(user.getFullname())
					.append("','")
					.append("','").append(Formatter.format(o.getOrderStartTime(), Formatter.DATE_TIME_FORMAT))
					//0没有退款。10买家已经申请退款，等待卖家同意。20卖家已经同意退款，等待买家退货。30买家已经退货，等待卖家确认收货。40卖家拒绝退款。90退款关闭。100退款成功。
					.append("',0")
					.append(",'")
					.append("','").append(getOuterSkuId(item.getSkuId(),SERVER_URL,token,appKey,appSecret,conn))
					.append("','").append(totalFee)
					.append("','").append(payment)
					.append("','").append(discountFee)
					.append("','")
					.append("','").append(20)
					.append("','")//timeoutactiontime   订单超时到期时间暂时为空
					.append("','")//iid也为空
					.append("','")
					.append("',").append(item.getItemTotal())
					.append(",'").append(item.getJdPrice())
					.append("','")
					.append("','")
					.append("','")
//					.append("','")8
//					.append("','")7
//					.append("','")6
//					.append("','")//sellertype为空
//					.append("','")5
//					.append("','")4
//					.append("','")//modified为空3
//					.append("','")2
//					.append("','")1
					.append("',0.0)").toString();//DistributePrice暂时为空
	        		SQLHelper.executeSQL(conn, sql) ;
					
				}
			}
			
			
			if (isLBP)  //如果是lbp订单获取lbp信息
			{
				DefaultJdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
				OrderPrintDataGetRequest request = new OrderPrintDataGetRequest();
				request.setOrderId(o.getOrderId());
				OrderPrintDataGetResponse response=client.execute(request);
				
				int is_notice_before_delivery=0;
				String lbpdc=response.getApiOrderPrintData().getCky2Name();
				String lbppartner=response.getApiOrderPrintData().getPartner();
				if(response.getApiOrderPrintData().getBfDeliGoodGlag().equals("是"))
					is_notice_before_delivery=1;
				String out_bound_date =response.getApiOrderPrintData().getCodTimeName();
				
				if (lbpdc.equals("") || lbpdc ==null) throw new JException("LBP信息为空,订单号："+o.getOrderId());
				
				sql="update ns_customerorder set lbpdc='"+lbpdc+"',lbppartner='"+lbppartner+"',"
					+"is_notice_before_delivery="+is_notice_before_delivery+",out_bound_date='"+out_bound_date+"' "
					+"where sheetid='"+sheetid+"'";
				SQLHelper.executeSQL(conn,sql);
					
			
				sql="select count(*) from ns_customerorder where sheetid='"+sheetid+"' and lbpdc is null";
				if (SQLHelper.intSelect(conn, sql)>0)
					throw new JException("LBP信息为空,订单号："+o.getOrderId());
			}
			
			if (pay_type.equals("2") && !JBDCustomerCode.equals("")){	
				JdClient JDBclient = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
				OrderSopPrintDataGetRequest  JDBRequest = new OrderSopPrintDataGetRequest ();
				JDBRequest.setOrderId(o.getOrderId());
				OrderSopPrintDataGetResponse JDBResponse = JDBclient.execute(JDBRequest);
				
				
				int is_notice_before_delivery=0;
				String lbpdc=JDBResponse.getApiOrderPrintData().getCky2Name();
				String lbppartner=JDBResponse.getApiOrderPrintData().getPartner();
				
				if(JDBResponse.getApiOrderPrintData().getBfDeliGoodGlag().equals("是"))
					is_notice_before_delivery=1;
				String out_bound_date =JDBResponse.getApiOrderPrintData().getCodTimeName();
			
				
				sql="update ns_customerorder set lbpdc='"+lbpdc+"',lbppartner='"+lbppartner+"',"
					+"is_notice_before_delivery="+is_notice_before_delivery+",out_bound_date='"+out_bound_date+"' "
					+"where sheetid='"+sheetid+"'";
				SQLHelper.executeSQL(conn,sql);
			}
			
		
			// 加入到通知表  sheettype=1 opertype=100
			sql = new StringBuilder().append("insert into inf_downnote(sheettype,notetime,opertype,operdata,flag,owner)")
					.append("values(1,getdate(),100,'")
					.append(sheetid).append("',0,'')").toString();
			SQLHelper.executeSQL(conn,sql);

			conn.commit();
			conn.setAutoCommit(true);
			Log.info("生成订单【" + o.getOrderId() + "】接口数据成功，接口单号【" + sheetid + "】");

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
			throw new JException("生成订单【" + o.getOrderId() + "】接口数据失败!"+e.getMessage());
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

			//状态码
			result = response.getResultInfo().getDeliveryIdList().get(0);
		}
		
		
		return result;
	}
	
	private static void sendJDPostNo(String JBDCustomerCode,String postno,OrderInfo o,
			String SERVER_URL,String token,String appKey,String appSecret) throws Exception
	{	Params param = new Params();
		JdClient client = new DefaultJdClient(SERVER_URL,token,appKey,appSecret);
		EtmsWaybillSendRequest  request = new EtmsWaybillSendRequest  ();
		request.setCustomerCode(JBDCustomerCode);
		request.setDeliveryId(postno);
		request.setSalePlat("jingdong");
		request.setOrderId(o.getOrderId());
		request.setSelfPrintWayBill(1);
		request.setSenderName(param.linkman);
		request.setSenderAddress(param.address);
		request.setSenderTel(param.phone);
		request.setReceiveName(o.getConsigneeInfo().getFullname());
		request.setReceiveAddress(o.getConsigneeInfo().getFullAddress());
		request.setReceiveMobile(o.getConsigneeInfo().getMobile());
		request.setPackageCount(1);
		request.setWeight(2.5);
		request.setVloumn(1.0);
		
		EtmsWaybillSendResponse response = client.execute(request);

		//状态码
		String code = response.getCode();
		
		
		if("0".equals(code))
		{
			Log.info("提交京东物流信息成功，京东单号【" + o.getOrderId() + "】，快递单号【" + postno + "】") ;
		}
		else
		{
			
			throw new JException("提交京东物流信息失败，京东单号【" + o.getOrderId() + "】，快递单号【" + postno + "】。错误信息：" + code + "," + response.getZhDesc()) ;
		}
	
	}
	
	
	//向京东物流系统提交运单信息  京东货到付款的订单发货的第二步处理流程
	/**
	 * 运单号      deliveryId 
	 * 销售平台编码 salePlat =0010001
	 * 商家店铺编码 customerCode
	 * 商家订单号  orderId 
      寄件人姓名  senderName 
      寄件人地址 senderAddress 
      收件人姓名 receiveName
      手机人地址 receiveAddress
      包裹数量 PackageCount 1
      重量    Weight 1
     包裹体积  vloumn  1 
     代收金额：collectionValue 1
     代收货款金额  collectionMoney
	 */
	public static Boolean waybillSend(String deliveryId ,String customerCode,
			String SERVER_URL,String accessToken,String appKey,String appSecret,OrderInfo o){
		Boolean isSuccess=false;
		double collectionMoney=0.00f;
		Params param = new Params();
		try{
			collectionMoney=getCollectionMoney(o.getOrderId(),SERVER_URL,accessToken,appKey,appSecret);
			//Log.info("应付金额为:　"+collectionMoney);
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
			request.setSenderName(param.username);
			request.setSenderAddress(param.address);
			request.setSenderMobile(param.phone);
			request.setReceiveName(o.getConsigneeInfo().getFullname());
			request.setReceiveAddress(o.getConsigneeInfo().getFullAddress());
			request.setReceiveMobile(o.getConsigneeInfo().getMobile());
			request.setPackageCount (1);
			request.setWeight(1.0);
			request.setVloumn(1000.0);
			EtmsWaybillSendResponse response=client.execute(request);
			isSuccess =  response.getResultInfo().getCode().equalsIgnoreCase("100");
			Log.info("订单号: "+o.getOrderId()+",快递单号: "+deliveryId+",返回信息: "+response.getMsg());
			Log.info(response.getResultInfo().getCode());
		}catch(Exception ex){
			Log.error("向京东物流系统提交运单信息出错,订单号:　"+o.getOrderId()+",错误信息: ", ex.getMessage());
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
			Log.info("订单号: "+orderId+" ,获取货到付款应付金额返回信息: "+response.getMsg());
		}catch(Exception ex){
			Log.error("取货到付款应收金额出错!", ex.getMessage());
		}
		return collectionMoney;
		
	}
	
	//根据京东skui获取商家skuid
	private static String getOuterSkuId(String skuId,String SERVER_URL,String token,String appkey,String appSecret,Connection conn) throws Exception{
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
				/**
				if(result.equals("")){  //从京东接口没有数据返回时直接取ecs_stockconfigsku表的数据
					String sql = new StringBuilder().append("select orgid from ecs_tradecontactorgcontrast where tradecontactid=")
						.append(tradecontactid).toString();
					int orgid= SQLHelper.intSelect(conn, sql);
					sql = new StringBuilder().append("select sku from ecs_stockconfigsku where orgid=")
						.append(orgid).append(" and skuid='").append(skuId).append("'").toString();
					result = SQLHelper.strSelect(conn, sql);
				}
				**/
				return result!=null?result:"";
	}
	
	//查询商家备注
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
			Log.error("获取商家备注失败", ex.getMessage());
			ex.printStackTrace();
			//throw new Exception("获取商家备注失败");
			
		}
		
		return VenderRemark;
	}
	
	
	
}
