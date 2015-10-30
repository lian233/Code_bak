package com.wofu.ecommerce.dangdang;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.ecommerce.dangdang.util.CommHelper;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class OrderUtils {
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	private static String refundDesc[] = {"","退货","换货",""} ;

	/**
	 * 获取当当订单详细信息
	 * @param orderID	订单号
	 * @return
	 */
	public static Order getOrderByID(String url,String orderID,String session,String app_key,String app_Secret)
	{

		Order o = new Order() ;
		try 
		{
			Date temp = new Date();
			//方法名
			String methodName="dangdang.order.details.get";
			//生成验证码 --md5;加密
			String sign = CommHelper.getSign(app_Secret, app_key, methodName, session,temp) ;
			
			Hashtable<String, String> params = new Hashtable<String, String>() ;
			params.put("sign", sign) ;
			params.put("timestamp",URLEncoder.encode(Formatter.format(temp,Formatter.DATE_TIME_FORMAT),"GBK"));
			params.put("app_key",app_key);
			params.put("method",methodName);
			params.put("format","xml");
			params.put("session",session);
			params.put("sign_method","md5");
			params.put("o", orderID) ;
			
			String responseText = CommHelper.sendRequest(url, "GET",params,"") ;
			responseText = CommHelper.filterChar(responseText);
			Document doc = DOMHelper.newDocument(responseText, "GBK") ;
			Element result = doc.getDocumentElement() ;
			//判断返回是否正确
			if(DOMHelper.ElementIsExists(result, "Error"))
			{
				Element error = (Element) result.getElementsByTagName("Error").item(0);
				String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
				if(!"".equals(operCode))
				{
					Log.error("当当获取订单", "获取订单失败,订单号:"+ orderID +",operCode:"+operCode+",operation:"+operation);
					o = null ;
					return o ;
				}
			}
			//订单基本信息
			String orderId = DOMHelper.getSubElementVauleByName(result, "orderID") ;
			String orderState = DOMHelper.getSubElementVauleByName(result, "orderState") ;
			String message = DOMHelper.getSubElementVauleByName(result, "message") ;
			//去掉‘变成空格
			message = message.replace("'", " ") ;
			String remark = DOMHelper.getSubElementVauleByName(result, "remark") ;
			String label = DOMHelper.getSubElementVauleByName(result, "label") ;
			String lastModifyTime = DOMHelper.getSubElementVauleByName(result, "lastModifyTime") ;
			o.setOrderID(orderId) ;
			o.setOrderState(orderState) ;
			o.setMessage(message) ;
			o.setRemark(remark) ;
			o.setLabel(label) ;
			o.setLastModifyTime(Formatter.parseDate(lastModifyTime, Formatter.DATE_TIME_FORMAT)) ;
			//获取操作列表信息
			Element orderOperateList = (Element) result.getElementsByTagName("OrderOperateList").item(0) ;
			NodeList operateInfoList = orderOperateList.getElementsByTagName("OperateInfo") ;
			ArrayList<OperateInfo> operateList = new ArrayList<OperateInfo>() ;
			for(int i = 0 ; i < operateInfoList.getLength() ; i++)
			{
				Element operateInfo = (Element) operateInfoList.item(i) ;
				String operateRole = DOMHelper.getSubElementVauleByName(operateInfo, "operateRole") ;
				String operateTime = DOMHelper.getSubElementVauleByName(operateInfo, "operateTime") ;
				String operateDetails = DOMHelper.getSubElementVauleByName(operateInfo, "operateDetails") ;		
				String returnOrderID = DOMHelper.getSubElementVauleByName(operateInfo, "returnOrderID") ;		
				
				OperateInfo info = new OperateInfo() ;
				info.setOperateRole(operateRole) ;
				info.setOperateTime(Formatter.parseDate(operateTime, Formatter.DATE_TIME_FORMAT)) ;
				info.setOperateDetails(operateDetails) ;
				info.setReturnOrderID(returnOrderID) ;
				
				operateList.add(info) ;
			}
			o.setOperateInfoList(operateList) ;
			//买家信息
			Element buyerInfo = (Element) result.getElementsByTagName("buyerInfo").item(0) ;
			String buyerPayMode = DOMHelper.getSubElementVauleByName(buyerInfo, "buyerPayMode") ;
			String goodsMoney = DOMHelper.getSubElementVauleByName(buyerInfo, "goodsMoney") ;
			String deductAmount = DOMHelper.getSubElementVauleByName(buyerInfo, "deductAmount") ;
			String totalBarginPrice = DOMHelper.getSubElementVauleByName(buyerInfo, "totalBarginPrice") ;
			String postage = DOMHelper.getSubElementVauleByName(buyerInfo, "postage") ;
			String giftCertMoney = DOMHelper.getSubElementVauleByName(buyerInfo, "giftCertMoney") ;
			String giftCardMoney = DOMHelper.getSubElementVauleByName(buyerInfo, "giftCardMoney") ;
			String accountBalance = DOMHelper.getSubElementVauleByName(buyerInfo, "accountBalance") ;
			o.setBuyerPayMode(buyerPayMode) ;
			o.setGoodsMoney(Float.parseFloat(goodsMoney)) ;
			if("".equals(deductAmount) || deductAmount == null)
				deductAmount = "0" ;
			if("".equals(totalBarginPrice) || totalBarginPrice == null)
				totalBarginPrice = "0" ;
			if("".equals(postage) || postage == null)
				postage = "0" ;
			if("".equals(giftCertMoney) || giftCertMoney == null)
				giftCertMoney = "0" ;
			if("".equals(giftCardMoney) || giftCardMoney == null)
				giftCardMoney = "0" ;
			if("".equals(accountBalance) || accountBalance == null)
				accountBalance = "0" ;
			o.setDeductAmount(Float.parseFloat(deductAmount)) ;
			o.setTotalBarginPrice(Float.parseFloat(totalBarginPrice)) ;
			o.setPostage(Float.parseFloat(postage)) ;
			o.setGiftCertMoney(Float.parseFloat(giftCertMoney));
			o.setGiftCardMoney(Float.parseFloat(giftCardMoney));
			o.setAccountBalance(Float.parseFloat(accountBalance));
			
			//发货信息
			Element sendGoodsInfo = (Element) result.getElementsByTagName("sendGoodsInfo").item(0) ;
			String consigneeName = DOMHelper.getSubElementVauleByName(sendGoodsInfo, "consigneeName") ;
			String consigneeAddr = DOMHelper.getSubElementVauleByName(sendGoodsInfo, "consigneeAddr") ;
			String consigneeAddr_State = DOMHelper.getSubElementVauleByName(sendGoodsInfo, "consigneeAddr_State") ;
			String consigneeAddr_Province = DOMHelper.getSubElementVauleByName(sendGoodsInfo, "consigneeAddr_Province") ;
			String consigneeAddr_City = DOMHelper.getSubElementVauleByName(sendGoodsInfo, "consigneeAddr_City") ;
			String consigneeAddr_Area = DOMHelper.getSubElementVauleByName(sendGoodsInfo, "consigneeAddr_Area") ;
			String consigneePostcode = DOMHelper.getSubElementVauleByName(sendGoodsInfo, "consigneePostcode") ;
			String consigneeTel = DOMHelper.getSubElementVauleByName(sendGoodsInfo, "consigneeTel") ;
			String consigneeMobileTel = DOMHelper.getSubElementVauleByName(sendGoodsInfo, "consigneeMobileTel") ;
			String sendGoodsMode = DOMHelper.getSubElementVauleByName(sendGoodsInfo, "sendGoodsMode") ;
			String sendCompany = DOMHelper.getSubElementVauleByName(sendGoodsInfo, "sendCompany") ;
			String sendOrderID = DOMHelper.getSubElementVauleByName(sendGoodsInfo, "sendOrderID") ;
			String isDangdangReceipt = DOMHelper.getSubElementVauleByName(sendGoodsInfo, "Is_DangdangReceipt") ;
			String dangdangWarehouseAddr = DOMHelper.getSubElementVauleByName(sendGoodsInfo, "DangdangWarehouseAddr") ;
			
			//处理地址信息 去掉“中国”，“，”号
			consigneeAddr = consigneeAddr.replace("，", " ").replace("中国", "").trim() ;
			
			o.setConsigneeName(consigneeName);
			o.setConsigneeAddr(consigneeAddr) ;
			o.setConsigneeAddrStte(consigneeAddr_State) ;
			o.setConsigneeAddrProvince(consigneeAddr_Province) ;
			o.setConsigneeAddrCity(consigneeAddr_City) ;
			o.setConsigneeAddrArea(consigneeAddr_Area) ;
			o.setConsigneePostcode(consigneePostcode) ;
			o.setConsigneeTel(consigneeTel) ;
			o.setConsigneeMobileTel(consigneeMobileTel) ;
			o.setSendGoodsMode(sendGoodsMode) ;
			o.setSendCompany(sendCompany) ;
			o.setSendOrderID(sendOrderID) ;
			o.setIsDangdangReceipt(isDangdangReceipt) ;
			o.setDangdangWarehouseAddr(dangdangWarehouseAddr) ;
			//商品清单
			Element itemsList = (Element) result.getElementsByTagName("ItemsList").item(0) ;
			NodeList itemInfoList = itemsList.getElementsByTagName("ItemInfo") ;
			ArrayList<OrderItem> orderItemList = new ArrayList<OrderItem>() ;
			float totalFee = 0f ;
			for(int j = 0 ; j < itemInfoList.getLength() ; j++)
			{
				Element item = (Element)itemInfoList.item(j);
				String itemID = DOMHelper.getSubElementVauleByName(item, "itemID") ;
				String outerItemID = DOMHelper.getSubElementVauleByName(item, "outerItemID") ;
				String itemName = DOMHelper.getSubElementVauleByName(item, "itemName") ;
				String itemType = DOMHelper.getSubElementVauleByName(item, "itemType") ;
				String specialAttribute = DOMHelper.getSubElementVauleByName(item, "specialAttribute") ;
				String marketPrice = DOMHelper.getSubElementVauleByName(item, "marketPrice") ;
				String unitPrice = DOMHelper.getSubElementVauleByName(item, "unitPrice") ;
				String orderCount = DOMHelper.getSubElementVauleByName(item, "orderCount") ;
				OrderItem it = new OrderItem() ;
				it.setItemID(itemID);
				it.setOuterItemID(outerItemID) ;
				it.setItemName(itemName);
				it.setItemType(itemType) ;
				it.setSpecialAttribute(specialAttribute) ;
				if("".equals(marketPrice) || marketPrice == null)
					marketPrice="0" ;
				if("".equals(unitPrice) || unitPrice == null || "----".equals(unitPrice) || "1".equals(itemType))
					unitPrice="0" ;
				if("".equals(orderCount) || orderCount == null)
					orderCount="0" ;
				it.setMarketPrice(Float.parseFloat(marketPrice));
				it.setUnitPrice(Float.parseFloat(unitPrice));
				it.setOrderCount(Integer.parseInt(orderCount));
				
				orderItemList.add(it) ;
				//计算本订单商品总金额
				totalFee += (it.getUnitPrice() * it.getOrderCount());
			}
			o.setOrderItemList(orderItemList) ;
			o.setTotalFee(totalFee) ;
			//发票信息	如果买家没要求提供发票，则此信息不会返回
		
			Element receiptInfo = (Element) result.getElementsByTagName("receiptInfo").item(0);
			String receiptName = DOMHelper.getSubElementVauleByName(receiptInfo,"receiptName") ;
			String receiptDetails = DOMHelper.getSubElementVauleByName(receiptInfo,"receiptDetails") ;
			String receiptMoney = DOMHelper.getSubElementVauleByName(receiptInfo,"receiptMoney") ;
			o.setReceiptName(receiptName) ;
			o.setReceiptDetails(receiptDetails);
			o.setReceiptMoney(Float.parseFloat(receiptMoney)) ;

		} catch (Exception e) {
			Log.error("获取当当订单", "获取订单失败，单号："+orderID+"，错误信息："+e.getMessage()) ;
			o = null ;
		}
		return o ;
	}

	/**
	 * 创建接口订单
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
				throw new JSQLException(sql,"取接口单号出错!");
			//操作信息 下单时间 付款时间 最后修改时间
			Date startTime= new Date();
			Date payTime= new Date();
			ArrayList<OperateInfo> operateInfoList = o.getOperateInfoList() ;	
			if(operateInfoList.size()>0){
				startTime = operateInfoList.get(0).getOperateTime();
				payTime = operateInfoList.get(0).getOperateTime();
				for(int i = 0 ; i < operateInfoList.size() ; i++)
				{
					if(operateInfoList.get(i).getOperateDetails().indexOf("付款")>=0)
					{
						payTime = operateInfoList.get(i).getOperateTime() ;
						break ;
					}
				}
			}
			
			String buyerPayMode = "";//付款方式 接口业务模式，0其它，1在线支付，2货到付款，3自动发货，4分销
			if(o.getBuyerPayMode().indexOf("货到付款") >=0)
				buyerPayMode="2";
			else
				buyerPayMode="1";
			int needInvoice;//是否需发票：1需要，0不需要,needinvoice 返回值
			if("".equals(o.getReceiptName()) || o.getReceiptName() == null || o.getReceiptMoney() <= 0)
				needInvoice = 0;
			else
			{
				needInvoice = 1 ;
				if("个人".equals(o.getReceiptName()))
					o.setReceiptName(o.getConsigneeName()) ;
			}
			//是否指定快递
			String express = "" ;
			String delivery = "" ;
			if(o.getPostage() > 0 && "1".equals(buyerPayMode))
			{
				express = o.getSendGoodsMode() +"。客户已支付邮费:"+o.getPostage() ;
				if(o.getSendGoodsMode().indexOf("特快专递") > -1)
					delivery="ems" ;
				else
					delivery="express" ;
			}
			
			//货到付款金额：PayFee = totalfee - discountfee + postfee + PayFee - Prepay
			float discountfee = o.getTotalFee() - (o.getGoodsMoney()-o.getPostage()) - (o.getGiftCardMoney()+o.getAccountBalance()) ;//优惠金额 = 商品总金额 - 应支付金额（不包含邮费）- 已支付金额(礼品卡+余额支付) 
			//控制浮点运算，避免出现长小数
			if(discountfee < 0.01)
				discountfee = 0 ;
			//加入到单据表
			sql =  "insert into ns_customerorder(CustomerOrderId , SheetID , Owner , tid , OrderSheetID , sellernick , " 
            	+ " type , created , buyermessage , shippingtype , payment , "
				+ " discountfee , adjustfee , status , buyermemo , sellermemo , "
				+ " tradememo , paytime , endtime , modified ,buyerobtainpointfee , "
				+ " pointfee , realpointfee , totalfee , postfee , buyeralipayno , "
				+ " buyernick ,buyerUin, receivername , receiverstate , receivercity , receiverdistrict , "
				+ " receiveraddress , receiverzip , receivermobile , receiverphone , consigntime , "
				+ " buyeremail , commissionfee , availableconfirmfee , haspostFee , receivedpayment , "
				+ " codfee , codstatus , timeoutactiontime , delivery , deliverySheetID , "
				+ " alipayNo , buyerflag , sellerflag,price , num , title , snapshoturl , snapshot , "
				+ " sellerrate,buyerrate,dealRateState,numiid,promotion,tradefrom,alipayurl,PromotionDetails,TradeContactID,paymode,InvoiceFlag,invoicetitle,Prepay) values(" 
                + "'" + sheetid + "','" + sheetid + "','yongjun','" + o.getOrderID() + "','','" + username + "',"
                + "'','" + Formatter.format(startTime, Formatter.DATE_TIME_FORMAT) + "','" + o.getMessage() + "','"+ delivery +"','" + (o.getTotalFee()+o.getPostage()-discountfee) + "',"
                + "'" + discountfee + "','0.0','"+o.getOrderState()+"','" + o.getRemark() + "','"+express+"',"
                + "'','" + Formatter.format(payTime, Formatter.DATE_TIME_FORMAT) + "','','"+Formatter.format(o.getLastModifyTime(), Formatter.DATE_TIME_FORMAT)+"','',"
                + "'','','" + o.getTotalFee() + "','" + o.getPostage() + "','',"
                + "'" + StringUtil.replace(o.getConsigneeName(),"'","") + "','','" + StringUtil.replace(o.getConsigneeName(),"'","") + "','" + o.getConsigneeAddrProvince() + "','" + o.getConsigneeAddrCity() + "','" + o.getConsigneeAddrArea() + "',"
                + "'" + StringUtil.replace(o.getConsigneeAddr(),"'","") +"','"+ o.getConsigneePostcode() +"','" + o.getConsigneeMobileTel() + "','" + o.getConsigneeTel() + "','',"
                + "'','','','','',"
            	+ "'','','','','',"
            	+ "'','','"+ o.getLabel() +"','" + (o.getTotalFee()+o.getPostage()-o.getGiftCertMoney()) + "','','','','',"
            	+ "'','','','','','dangdang','','','" + tradeContactID + "','"  + buyerPayMode + "','" + needInvoice + "','"+StringUtil.replace(o.getReceiptName(),"'","")+"','"+ (o.getGiftCardMoney()+o.getAccountBalance()) +"')";

			SQLHelper.executeSQL(conn, sql);
			
			//订单明细
			float totalPrice = o.getTotalFee() ;//总金额
			float sellerDiscount = discountfee ;//商家优惠金额
			float giftCardMoney = o.getGiftCardMoney() ;//礼品卡金额，不需要开具到发票里面。发票金额=发货商品总金额-（优惠金额+礼品卡金额+礼券金额）
			float paymentPercent = 1-(sellerDiscount/totalPrice) ;
			float countDiscountFee = 0f ;
			float countPayment = 0f ;
			//发票金额
			float invoicePercent = 1 - ((discountfee+giftCardMoney) / totalPrice) ;
			float countInvoicePayment = 0f ;

			ArrayList<OrderItem> list = o.getOrderItemList() ;
			for(int j = 0 ; j < list.size() ; j++)
			{
				OrderItem item = list.get(j);
				float itemTotalFee = item.getOrderCount() * item.getUnitPrice() ;
				
				float itemPayment = 0f ;
				float invoicePayment = 0f ;
				if(j==(list.size()-1))
				{
					itemPayment = Float.parseFloat(decimalFormat.format(totalPrice-sellerDiscount-countPayment)) ;
					countDiscountFee = itemTotalFee - itemPayment ;
					
					invoicePayment = Float.parseFloat(decimalFormat.format(totalPrice-sellerDiscount-giftCardMoney-countInvoicePayment)) ;
					countInvoicePayment += invoicePayment ;
				}
				else
				{
					itemPayment = Float.parseFloat(decimalFormat.format(itemTotalFee*paymentPercent)) ;
					countPayment += itemPayment ;
					countDiscountFee = itemTotalFee - itemPayment ;
					
					invoicePayment = Float.parseFloat(decimalFormat.format(itemTotalFee*invoicePercent)) ;
					countInvoicePayment += invoicePayment ;
				}
				sql = "insert into ns_orderitem(CustomerOrderId , orderItemId  , SheetID , skuid , itemmealname , " 
                    + " title , sellernick , buyernick , type , created , " 
                    + " refundstatus , outeriid , outerskuid , totalfee , payment , " 
                    + " discountfee , adjustfee , status , timeoutactiontime , owner , " 
                    + " iid , skuPropertiesName , num , price , picPath , " 
                    + " oid , snapShotUrl , snapShot ,modified) values( "
                    + "'" + sheetid + "','" + sheetid+"-"+o.getOrderID() + String.valueOf(j+1) + "','" + sheetid + "','" + item.getItemID() + "','" + item.getItemType() + "',"
                    + "'" + item.getItemName() + "','" + username + "','" + StringUtil.replace(o.getConsigneeName(),"'","") + "','','" + Formatter.format(startTime, Formatter.DATE_TIME_FORMAT) + "',"
                    + "'','','" + item.getOuterItemID() + "','" + itemTotalFee + "','" + itemPayment + "'," 
                    + "'" + countDiscountFee + "','','"+o.getOrderState()+"','','yongjun',"
                    + "'','"+ item.getSpecialAttribute() +"','" + item.getOrderCount() + "','" + item.getUnitPrice() + "','',"
                    + "'','','','')" ;

        		SQLHelper.executeSQL(conn, sql) ;
        	
			}
			//加入到通知表
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                + sheetid +"',1 , '"+tradeContactID+"' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("生成订单【" + o.getOrderID() + "】接口数据成功，接口单号【" + sheetid + "】");

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
			throw new JException("生成订单【" + o.getOrderID() + "】接口数据失败!"
					+ e1.getMessage());
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
	
	//生成接口数据
	public static void createRefundOrder(String jobname,Connection conn,String tradecontactid,ReturnOrder o,Order customOrder)
	{
		String sql = "" ;
		float refundFee = 0f ;
		try 
		{
			ArrayList<ReturnOrderItem> itemList = o.getItemList() ;
			sql = "select count(*) as num from ns_refund with(nolock) where refundid='"+o.getReturnExchangeCode()+"'" ;
			Log.info(sql) ;
			int count = SQLHelper.intSelect(conn, sql) ;
			if(count == itemList.size())
				return ;
			sql="select shopid from ContactShopContrast with(nolock) where tradecontactid="+tradecontactid;
            String inshopid = SQLHelper.strSelect(conn, sql);
			for(int i=0 ; i<itemList.size() ; i++)
			{
				try 
				{
					ReturnOrderItem item = itemList.get(i) ;
		            conn.setAutoCommit(false);
		            
		            sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";			
					//Log.info(sql) ;
		            String sheetid = SQLHelper.strSelect(conn, sql);
					if (sheetid.trim().equals(""))
						throw new JSQLException(sql,"取接口单号出错!");
					if("1".equals(o.getReturnExchangeStatus()))
						refundFee = item.getUnitPrice() * item.getOrderCount() ;
					//金额不入系统,暂做清零处理
					refundFee = 0 ;
					o.setOrderMoney(0) ;
					item.setUnitPrice(0) ;
					
					sql = "insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , BuyerNick , "
						+ "Created , Modified , OrderStatus , Status , GoodStatus , "
	                    + " HasGoodReturn ,RefundFee , Payment , Reason,Description ,"
	                    + " Title , Price , Num , GoodReturnTime , Sid , "
	                    + " TotalFee , Iid , OuterIid , OuterSkuId , CompanyName ," 
	                    + " Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)"
	                    + " values('" + sheetid + "' , '" + o.getReturnExchangeCode() + "' , '" + o.getReturnExchangeCode() + "' , '' , '"+ customOrder.getConsigneeName() +"' ,"
	                    + "'" + Formatter.format(o.getOrderTime(), Formatter.DATE_TIME_FORMAT) + "','" + Formatter.format(o.getOrderTime(), Formatter.DATE_TIME_FORMAT) + "','"+ o.getOrderStatus() +"','','"+ o.getReturnExchangeStatus() +"',"
	                    + "'1','"+refundFee+"','"+ o.getOrderMoney() +"','','"+getRefundDesc(o.getReturnExchangeStatus())+"',"
	                    + "'" + item.getItemName() + "','" + item.getUnitPrice() + "','"+ item.getOrderCount() +"','" + Formatter.format(o.getOrderTime(), Formatter.DATE_TIME_FORMAT) + "','',"
	                    + "'"+ o.getOrderMoney() +"','"+ item.getItemID() +"','','" + item.getOuterItemID() + "',''," 
	                    + "'','"+ customOrder.getConsigneeAddr() +"','" + inshopid + "','" + o.getOrderID() + "','"+ customOrder.getConsigneeName() +"','"+ (customOrder.getConsigneeMobileTel()+customOrder.getConsigneeTel()) +"','')" ;

					//Log.info(sql) ;
					SQLHelper.executeSQL(conn,sql);
					
					//加入到通知表
		            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
		                + sheetid +"',2 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";				
					//Log.info(sql) ;
					SQLHelper.executeSQL(conn,sql);
					
					Log.info(jobname,"接口单号:"+sheetid+" 退货订单号:"+o.getReturnExchangeCode()+"，订单更新时间:"+Formatter.format(o.getOrderTime(),Formatter.DATE_TIME_FORMAT));
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
					throw new JSQLException("生成退货【" + o.getOrderID() + "】接口数据失败!"+e1.getMessage());
				}
			}
		}
		catch (Exception e) 
		{
			Log.error(jobname, "生成接口退货单失败,订单号:"+o.getOrderID() + ",退换货单号:"+o.getReturnExchangeCode()+",退换货类型:"+o.getReturnExchangeStatus()+",错误信息:"+e.getMessage()) ;
			e.printStackTrace() ;
		}
	}
	//退换货单描述
	private static String getRefundDesc(String index)
	{
		try 
		{
			return refundDesc[Integer.parseInt(index)] ;
		} catch (Exception e) {
			return index ;
		}
	}

	
	//返回订单状态
	public static String getOrderStateByCode(String orderStateCode)
	{
		if("100".equals(orderStateCode))
			return "等待到款" ;
		else if("101".equals(orderStateCode))
			return "等待发货" ;
		else if("300".equals(orderStateCode))
			return "已发货" ;
		else if("400".equals(orderStateCode))
			return "已送达" ;
		else if("1000".equals(orderStateCode))
			return "交易成功" ;
		else if("-100".equals(orderStateCode))
			return "取消" ;
		else if("1100".equals(orderStateCode))
			return "交易失败" ;
		else
			return "未知的订单状态" ;
	}
}
