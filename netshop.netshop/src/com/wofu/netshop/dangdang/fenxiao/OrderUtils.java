package com.wofu.netshop.dangdang.fenxiao;

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

import com.wofu.netshop.dangdang.fenxiao.OperateInfo;
import com.wofu.netshop.dangdang.fenxiao.ReturnOrder;
import com.wofu.netshop.dangdang.fenxiao.ReturnOrderItem;
import com.wofu.netshop.dangdang.fenxiao.CommHelper;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class OrderUtils {
	private final static DecimalFormat decimalFormat = new DecimalFormat("########.00");
	private static String refundDesc[] = {"","�˻�","����",""} ;

	/**
	 * ��ȡ����������ϸ��Ϣ
	 * @param orderID	������
	 * @return
	 */
	public static Order getOrderByID(String url,String orderID,String session,String app_key,String app_Secret)
	{

		Order o = new Order() ;
		try 
		{
			Date temp = new Date();
			//������
			String methodName="dangdang.order.details.get";
			//������֤�� --md5;����
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
			
//			Log.info("��ϸ����:"+responseText);
			
			responseText = CommHelper.filterChar(responseText);
			Document doc = DOMHelper.newDocument(responseText, "GBK") ;
			Element result = doc.getDocumentElement() ;
			//�жϷ����Ƿ���ȷ
			if(DOMHelper.ElementIsExists(result, "Error"))
			{
				Element error = (Element) result.getElementsByTagName("Error").item(0);
				String operCode = DOMHelper.getSubElementVauleByName(error, "operCode") ;
				String operation = DOMHelper.getSubElementVauleByName(error, "operation") ;
				if(!"".equals(operCode))
				{
					Log.error("������ȡ����", "��ȡ����ʧ��,������:"+ orderID +",operCode:"+operCode+",operation:"+operation);
					o = null ;
					return o ;
				}
			}
			//����������Ϣ
			String orderId = DOMHelper.getSubElementVauleByName(result, "orderID") ;
			String orderState = DOMHelper.getSubElementVauleByName(result, "orderState") ;
			String message = DOMHelper.getSubElementVauleByName(result, "message") ;
			//ȥ������ɿո�
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
			//��ȡ�����б���Ϣ
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
			//�����Ϣ
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
			
			//������Ϣ
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
			
			//�����ַ��Ϣ ȥ�����й�������������
			consigneeAddr = consigneeAddr.replace("��", " ").replace("�й�", "").trim() ;
			
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
			//��Ʒ�嵥
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
				//���㱾������Ʒ�ܽ��
				totalFee += (it.getUnitPrice() * it.getOrderCount());
			}
			o.setOrderItemList(orderItemList) ;
			o.setTotalFee(totalFee) ;
			//��Ʊ��Ϣ	������ûҪ���ṩ��Ʊ�������Ϣ���᷵��
		
			Element receiptInfo = (Element) result.getElementsByTagName("receiptInfo").item(0);
			String receiptName = DOMHelper.getSubElementVauleByName(receiptInfo,"receiptName") ;
			String receiptDetails = DOMHelper.getSubElementVauleByName(receiptInfo,"receiptDetails") ;
			String receiptMoney = DOMHelper.getSubElementVauleByName(receiptInfo,"receiptMoney") ;
			o.setReceiptName(receiptName) ;
			o.setReceiptDetails(receiptDetails);
			o.setReceiptMoney(Float.parseFloat(receiptMoney)) ;

		} catch (Exception e) {
			Log.error("��ȡ��������", "��ȡ����ʧ�ܣ����ţ�"+orderID+"��������Ϣ��"+e.getMessage()) ;
			o = null ;
		}
		return o ;
	}

	/**
	 * �����ӿڶ���
	 * @param conn
	 * @param o
	 * @param tradeContactID
	 * @param username
	 * @throws SQLException 
	 */
	public static void createInterOrder(Connection conn,Order o,String tradeContactID,String username,int shopid) throws Exception
	{		
		try 
		{
			conn.setAutoCommit(false);		
			
			int sheetid;
			String sql="declare @Value int;exec TL_GetNewSerial_new 100001,@value output;select @value;";
			sheetid=SQLHelper.intSelect(conn, sql);
			if (sheetid==0)
				throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");
			//������Ϣ �µ�ʱ�� ����ʱ�� ����޸�ʱ��
			Date startTime= new Date();
			Date payTime= new Date();
			ArrayList<OperateInfo> operateInfoList = o.getOperateInfoList() ;	
			if(operateInfoList.size()>0){
				startTime = operateInfoList.get(0).getOperateTime();
				payTime = operateInfoList.get(0).getOperateTime();
				for(int i = 0 ; i < operateInfoList.size() ; i++)
				{
					if(operateInfoList.get(i).getOperateDetails().indexOf("����")>=0)
					{
						payTime = operateInfoList.get(i).getOperateTime() ;
						break ;
					}
				}
			}
			
			String buyerPayMode = "";//���ʽ �ӿ�ҵ��ģʽ��0������1����֧����2�������3�Զ�������4����
			if(o.getBuyerPayMode().indexOf("��������") >=0)
				buyerPayMode="2";
			else
				buyerPayMode="1";
			int needInvoice;//�Ƿ��跢Ʊ��1��Ҫ��0����Ҫ,needinvoice ����ֵ
			if("".equals(o.getReceiptName()) || o.getReceiptName() == null || o.getReceiptMoney() <= 0)
				needInvoice = 0;
			else
			{
				needInvoice = 1 ;
				if("����".equals(o.getReceiptName()))
					o.setReceiptName(o.getConsigneeName()) ;
			}
			//�Ƿ�ָ�����
			String express = "" ;
			String delivery = "" ;
			if(o.getPostage() > 0 && "1".equals(buyerPayMode))
			{
				express = o.getSendGoodsMode() +"���ͻ���֧���ʷ�:"+o.getPostage() ;
				if(o.getSendGoodsMode().indexOf("�ؿ�ר��") > -1)
					delivery="ems" ;
				else
					delivery="express" ;
			}
			
			//���������PayFee = totalfee - discountfee + postfee + PayFee - Prepay
			float discountfee = o.getTotalFee() - (o.getGoodsMoney()-o.getPostage()) - (o.getGiftCardMoney()+o.getAccountBalance()) ;//�Żݽ�� = ��Ʒ�ܽ�� - Ӧ֧�����������ʷѣ�- ��֧�����(��Ʒ��+���֧��) 
			//���Ƹ������㣬������ֳ�С��
			if(discountfee < 0.01)
				discountfee = 0 ;
			//���뵽���ݱ�

			sql =  "insert into itf_DecOrder(id,tid  , sellernick , shopid," 
            	+ " type , created , buyermessage , shippingtype , payment , "
				+ " discountfee , adjustfee , status , buyermemo , sellermemo , "
				+ " tradememo , paytime , endtime , modified ,buyerobtainpointfee , "
				+ " pointfee , realpointfee , totalfee , postfee , buyeralipayno , "
				+ " buyernick ,buyerUin, receivername , receiverstate , receivercity , receiverdistrict , "
				+ " receiveraddress , receiverzip , receivermobile , receiverphone , consigntime , "
				+ " buyeremail , commissionfee , availableconfirmfee , haspostFee , receivedpayment , "
				+ " codfee , codstatus , timeoutactiontime , delivery , deliverySheetID , "
				+ " alipayNo , buyerflag , sellerflag,price , num , title , snapshoturl , snapshot , "
				+ " sellerrate,buyerrate,dealRateState,numiid,promotion,tradefrom,alipayurl,PromotionDetails,paymode,InvoiceFlag,invoicetitle,Prepay) values(" 
                + "'" + sheetid + "','" + o.getOrderID() + "','" + username + "','" + shopid + "',"
                + "'','" + Formatter.format(startTime, Formatter.DATE_TIME_FORMAT) + "','" + o.getMessage() + "','"+ delivery +"','" + (o.getTotalFee()+o.getPostage()-discountfee) + "',"
                + "'" + discountfee + "','0.0','"+o.getOrderState()+"','" + o.getRemark() + "','"+express+"',"
                + "'','" + Formatter.format(payTime, Formatter.DATE_TIME_FORMAT) + "','','"+Formatter.format(o.getLastModifyTime(), Formatter.DATE_TIME_FORMAT)+"','',"
                + "'','','" + o.getTotalFee() + "','" + o.getPostage() + "','',"
                + "'" + StringUtil.replace(o.getConsigneeName(),"'","") + "','','" + StringUtil.replace(o.getConsigneeName(),"'","") + "','" + o.getConsigneeAddrProvince() + "','" + o.getConsigneeAddrCity() + "','" + o.getConsigneeAddrArea() + "',"
                + "'" + StringUtil.replace(o.getConsigneeAddr(),"'","") +"','"+ o.getConsigneePostcode() +"','" + o.getConsigneeMobileTel() + "','" + o.getConsigneeTel() + "','',"
                + "'','','','','',"
            	+ "'','','','','',"
            	+ "'','','"+ o.getLabel() +"','" + (o.getTotalFee()+o.getPostage()-o.getGiftCertMoney()) + "','','','','',"
            	+ "'','','','','','dangdang','','','"  + buyerPayMode + "','" + needInvoice + "','"+StringUtil.replace(o.getReceiptName(),"'","")+"','"+ (o.getGiftCardMoney()+o.getAccountBalance()) +"')";

			SQLHelper.executeSQL(conn, sql);
			
			//������ϸ
			float totalPrice = o.getTotalFee() ;//�ܽ��
			float sellerDiscount = discountfee ;//�̼��Żݽ��
			float giftCardMoney = o.getGiftCardMoney() ;//��Ʒ��������Ҫ���ߵ���Ʊ���档��Ʊ���=������Ʒ�ܽ��-���Żݽ��+��Ʒ�����+��ȯ��
			float paymentPercent = 1-(sellerDiscount/totalPrice) ;
			float countDiscountFee = 0f ;
			float countPayment = 0f ;
			//��Ʊ���
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
				sql="declare @Value integer;exec TL_GetNewSerial_new '100002',@value output;select @value;";
				int subid = SQLHelper.intSelect(conn, sql);
				sql = "insert into itf_DecOrderItem(ID , ParentID  ,skuid , itemmealname , " 
                    + " title , sellernick , buyernick , type , created , " 
                    + " refundstatus , outeriid , outerskuid , totalfee , payment , " 
                    + " discountfee , adjustfee , status , " 
                    + " iid , skuPropertiesName , num , price , picPath , " 
                    + " oid , snapShotUrl , snapShot ,modified) values( "
                    + "'" + subid + "','" + sheetid + "','" + item.getItemID() + "','" + item.getItemType() + "',"
                    + "'" + item.getItemName() + "','" + username + "','" + StringUtil.replace(o.getConsigneeName(),"'","") + "','','" + Formatter.format(startTime, Formatter.DATE_TIME_FORMAT) + "',"
                    + "'','" + item.getOuterItemID() + "','" + item.getOuterItemID() + "','" + itemTotalFee + "','" + itemPayment + "'," 
                    + "'" + countDiscountFee + "','','"+o.getOrderState()+"',"
                    + "'','"+ item.getSpecialAttribute() +"','" + item.getOrderCount() + "','" + item.getUnitPrice() + "','',"
                    + "'','','','')" ;

        		SQLHelper.executeSQL(conn, sql) ;
        		Log.info(sql);
			}
			//���뵽֪ͨ��
			
			sql = new StringBuilder().append("insert into inf_downnote(sheettype,notetime,opertype,operdata,flag,owner)")
			.append("values(1,getdate(),100,'")
			.append(sheetid).append("',0,'')").toString();
			SQLHelper.executeSQL(conn,sql);
			
            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
                + sheetid +"',1 , '"+tradeContactID+"' , 'yongjun' , getdate() , null) ";
			SQLHelper.executeSQL(conn, sql);
			conn.commit();
			conn.setAutoCommit(true);
			Log.info("���ɶ�����" + o.getOrderID() + "���ӿ����ݳɹ����ӿڵ��š�" + sheetid + "��");

			
		} catch (JSQLException e1) {
			if (!conn.getAutoCommit())
				try {
					System.out.println("�ع�");
					conn.rollback();
				} catch (Exception e2) {
				}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e3) {
			}
			throw new JException("���ɶ�����" + o.getOrderID() + "���ӿ�����ʧ��!"
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
	
	//���ɽӿ�����
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
						throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");
					if("1".equals(o.getReturnExchangeStatus()))
						refundFee = item.getUnitPrice() * item.getOrderCount() ;
					//����ϵͳ,�������㴦��
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
					
					//���뵽֪ͨ��
		            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
		                + sheetid +"',2 , '"+tradecontactid+"' , 'yongjun' , getdate() , null) ";				
					//Log.info(sql) ;
					SQLHelper.executeSQL(conn,sql);
					
					Log.info(jobname,"�ӿڵ���:"+sheetid+" �˻�������:"+o.getReturnExchangeCode()+"����������ʱ��:"+Formatter.format(o.getOrderTime(),Formatter.DATE_TIME_FORMAT));
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
					throw new JSQLException("�����˻���" + o.getOrderID() + "���ӿ�����ʧ��!"+e1.getMessage());
				}
			}
		}
		catch (Exception e) 
		{
			Log.error(jobname, "���ɽӿ��˻���ʧ��,������:"+o.getOrderID() + ",�˻�������:"+o.getReturnExchangeCode()+",�˻�������:"+o.getReturnExchangeStatus()+",������Ϣ:"+e.getMessage()) ;
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
		if("100".equals(orderStateCode))
			return "�ȴ�����" ;
		else if("101".equals(orderStateCode))
			return "�ȴ�����" ;
		else if("300".equals(orderStateCode))
			return "�ѷ���" ;
		else if("400".equals(orderStateCode))
			return "���ʹ�" ;
		else if("1000".equals(orderStateCode))
			return "���׳ɹ�" ;
		else if("-100".equals(orderStateCode))
			return "ȡ��" ;
		else if("1100".equals(orderStateCode))
			return "����ʧ��" ;
		else if("-200".equals(orderStateCode))
			return "�Ѳ�" ;
		else if("50".equals(orderStateCode))
			return "�ȴ����" ;
		else
			return "δ֪�Ķ���״̬" ;
	}

}
